package chans.usersideProcrastinationBot.processMonitoring;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import chans.usersideProcrastinationBot.domain.dto.UserInfoDto;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import org.apache.tomcat.jni.Time;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
 * Class that constantly pulls from the windows Kernel and keeps a track of all processes and their ID's
 * then tries to observe what the current user of the OS has a focus on - and logs it to be stored later
 * or pushed to a server to be future processesd (twitter bot/analytics)
 */
@Component
public class ProcessMonitorThread extends Thread {

    protected boolean paused = false;
    protected final RestTemplate restTemplate = new RestTemplate();
    protected String mainProcessName;


    // TODO ribbon list or something for this
    static String procrastinationBotServerUrl = "http://localhost:8080/procrastinationBotServer";
    static String endpointName = "/postUserInfo";
    static final UUID testUserId = UUID.fromString("1d782e3f-ad5f-46ce-92c8-3994cc1fdf8b");


    // Not using an executor because we want this to only really be run once
    @Override
    public void run() {

        while (!paused) {
            char[] buffer = new char[1024 * 2];
            PointerByReference pidPointer = new PointerByReference();

            WinDef.HWND currentWindowId = User32Wrapper.Instance.GetForegroundWindow();
            User32Wrapper.Instance.GetWindowThreadProcessId(currentWindowId, pidPointer);
            User32Wrapper.Instance.GetWindowTextW(currentWindowId, buffer, 1024);

            mainProcessName = new String(buffer);
            System.out.println("Window title is " + mainProcessName);
            try{
                handleUserActionLogged();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
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

    protected ProcessMonitorThread() {
    }

    protected void handleProcessNotFound() throws Exception{

    }

    final void test(){

    }

    protected void handleUserActionLogged() throws Exception{
        UserInfoDto userInfoDto = new UserInfoDto();

        float n = 1_23.1;
        userInfoDto.setMainProcessName(new String(mainProcessName));

        //TODO
        userInfoDto.setUserName("Test1");
        userInfoDto.setUserUniqueId(testUserId);

        HttpEntity<UserInfoDto> postRequest = new HttpEntity( userInfoDto );
        ResponseEntity response = restTemplate.postForEntity(procrastinationBotServerUrl+endpointName, postRequest, UserInfoDto.class);
        if(response.getStatusCode() != HttpStatus.ACCEPTED)
            throw new Exception("Error while trying to post to "+procrastinationBotServerUrl+endpointName);
    }
}
