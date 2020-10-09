package chans.usersideProcrastinationBot.processMonitoring;

import chans.usersideProcrastinationBot.domain.dto.UserInfoDto;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Class that constantly pulls from the windows Kernel and keeps a track of all processes and their ID's
 * then tries to observe what the current user of the OS has a focus on - and logs it to be stored later
 * or pushed to a server to be future processesd (twitter bot/analytics)
 */
@Component
public class ProcessMonitorThread extends Thread {

    protected boolean paused = true;
    protected boolean started = false;
    protected final RestTemplate restTemplate = new RestTemplate();
    protected String fullWindowName;
    protected String executableName;

    static String procrastinationBotServerUrl = "http://localhost:8081/procrastinationBotServer";
    static String endpointName = "/postUserInfo";

    // Change when building for backend web stuff
    boolean isLocal = true;
    public static String localDirectoryPath;
    BufferedWriter localFileWriter;
    String currentDateForFile;

    static final UUID testUserId = UUID.fromString("1d782e3f-ad5f-46ce-92c8-3994cc1fdf8b");

    public boolean isRunning(){
        return paused == false;
    }

    public boolean wasStarted(){
        return started == true;
    }

    // Not using an executor because we want this to only really be run once
    @Override
    public void run() {
        started = true;
        paused = false;
        if(isLocal){
            try {
                setUpLocalWrite();
            }
            catch(Exception e){
                //TODO do something better like notify the GUI
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        while (true) {
            if(!paused) {
                char[] buffer = new char[1024];

                WinDef.HWND currentWindowId = User32.INSTANCE.GetForegroundWindow();
                User32.INSTANCE.GetWindowText(currentWindowId, buffer, 1024);

                // Get the PID off the Window Handler
                IntByReference procId = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(currentWindowId, procId);

                // Open the process to get permissions to the image name
                WinNT.HANDLE procHandle = Kernel32.INSTANCE.OpenProcess(
                        Kernel32.PROCESS_QUERY_LIMITED_INFORMATION,
                        false,
                        procId.getValue()
                );

                // Get the image name
                char[] processBuffer = new char[512];
                IntByReference bufferSize = new IntByReference(buffer.length);

                boolean success = Kernel32.INSTANCE.QueryFullProcessImageName(procHandle, 0, processBuffer, bufferSize);
                Kernel32.INSTANCE.CloseHandle(procHandle);

                if (success) {
                    executableName = new String(processBuffer, 0, bufferSize.getValue());
                    // shave off everything before the final / and the .exe at the end
                    String[] tokens = executableName.split("\\\\");
                    executableName = tokens[tokens.length - 1].replace(".exe", "");
                } else {
                    System.out.println("Failed to get the image name");
                }

                fullWindowName = new String(buffer).replace("\0", "");
                try {
                    if (isLocal) {
                        handleUserActionLoggedLocally();
                    } else {
                        handleUserActionLoggedToServer();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println(e.getStackTrace());
                }
            }
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        this.paused = true;
    }
    public void unpause(){ this.paused = false;}

    protected void handleUserActionLoggedToServer() throws Exception{
        UserInfoDto userInfoDto = new UserInfoDto();

        userInfoDto.setMainProcessName(new String(fullWindowName));
        // TODO
        userInfoDto.setUserUniqueId(testUserId);

        HttpEntity<UserInfoDto> postRequest = new HttpEntity( userInfoDto );

        ResponseEntity response = restTemplate.postForEntity(procrastinationBotServerUrl+endpointName, postRequest, UserInfoDto.class);
        if(response.getStatusCode() != HttpStatus.ACCEPTED)
            throw new Exception("Error while trying to post to "+procrastinationBotServerUrl+endpointName);
        System.out.println(response.getBody());
    }

    protected void handleUserActionLoggedLocally() throws Exception{
        // Check to see if the date has changed if it has close the current writer and make a new file
        if(this.currentDateForFile.equals(LocalDate.now().toString()) == false){
            this.localFileWriter.close();

            // Make the next day's file so the gui can display cool stuff
            File logFile = new File(this.localDirectoryPath+ LocalDate.now().toString());
            // try to create it if it doesnt already exist
            logFile.createNewFile();
            this.localFileWriter = Files.newBufferedWriter(logFile.toPath(), StandardOpenOption.APPEND);
            this.currentDateForFile = LocalDate.now().toString();
        }

        String logTime = LocalTime.now().toString();
        // CSV format of Time, FullWindowName (contains more info on what the app was running), executable name
        this.localFileWriter.write(logTime + ", "+this.fullWindowName + ", " + this.executableName);
        this.localFileWriter.newLine();
        this.localFileWriter.flush();
    }

    public void setUpLocalWrite() throws Exception{
        this.localDirectoryPath = System.getProperty("user.home")+"\\"+".procrastinationBot";
        File localDirectory = new File(this.localDirectoryPath);
        if( !localDirectory.exists() ){
            localDirectory.mkdir();
        }
        // Create our file and buffered writer now. there will be one file per date.
        File logFile = new File(this.localDirectoryPath+"\\"+ LocalDate.now().toString());
        // try to create it if it doesnt already exist
        logFile.createNewFile();

        this.localFileWriter = Files.newBufferedWriter(logFile.toPath(), StandardOpenOption.APPEND);
        this.localFileWriter.write("Testing with the 1.8 way");
        this.localFileWriter.flush();
        this.currentDateForFile = LocalDate.now().toString();
    }
}
