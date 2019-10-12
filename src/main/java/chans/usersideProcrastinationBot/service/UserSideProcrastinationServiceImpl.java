package chans.usersideProcrastinationBot.service;

import chans.usersideProcrastinationBot.processMonitoring.ProcessMonitorThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class UserSideProcrastinationServiceImpl implements UserSideProcrastinationService {

    @Autowired
    protected ProcessMonitorThread processMonitorThread;

    @PostConstruct
    public void postConstruct(){
        processMonitorThread.run();

        try {
            TimeUnit.MINUTES.sleep(5);
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }

        processMonitorThread.pause();
    }


}
