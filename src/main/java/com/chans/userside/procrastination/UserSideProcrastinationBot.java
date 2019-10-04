package com.chans.userside.procrastination;

import com.chans.userside.procrastination.processMonitoring.ProcessMonitorThread;

import java.util.concurrent.TimeUnit;

public class UserSideProcrastinationBot {

    public static void main(String[] args) {
        ProcessMonitorThread.Instance.run();
        try {
            TimeUnit.MINUTES.sleep(5);
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }

        ProcessMonitorThread.Instance.pause();
    }
}
