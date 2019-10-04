package com.chans.userside.procrastination.processMonitoring;

import java.util.concurrent.TimeUnit;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import org.springframework.web.client.RestTemplate;

/**
 * Class that constantly pulls from the windows Kernel and keeps a track of all processes and their ID's
 * then tries to observe what the current user of the OS has a focus on - and logs it to be stored later
 * or pushed to a server to be future processesd (twitter bot/analytics)
 */
public class ProcessMonitorThread extends Thread {

    private static final RestTemplate restTemplate = new RestTemplate();
    public static ProcessMonitorThread Instance = new ProcessMonitorThread();
    static boolean paused = false;

    // TODO make this a DTO
    static String lastProcessName;

    // Not using an executor because we want this to only really be run once
    @Override
    public void run() {
        while (!paused) {
            char[] buffer = new char[1024 * 2];
            PointerByReference pidPointer = new PointerByReference();

            WinDef.HWND currentWindowId = User32Wrapper.Instance.GetForegroundWindow();
            User32Wrapper.Instance.GetWindowThreadProcessId(currentWindowId, pidPointer);
            User32Wrapper.Instance.GetWindowTextW(currentWindowId, buffer, 1024);

            System.out.println("Window title is " + new String(buffer));
            handleUserActionLogged();

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

    protected void handleProcessNotFound() {

    }

    protected void handleUserActionLogged() {
        //restTemplate.
    }
}
