package chans.usersideProcrastinationBot;

import chans.usersideProcrastinationBot.UI.HomePage;
import chans.usersideProcrastinationBot.UI.RootUI;
import chans.usersideProcrastinationBot.processMonitoring.LocalAnalytics;
import chans.usersideProcrastinationBot.processMonitoring.ProcessMonitorThread;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserSideProcrastinationBot extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    public static ProcessMonitorThread monitorThread = new ProcessMonitorThread();

    @Override
    public void start(Stage primaryStage) {
        try {
            monitorThread.setUpLocalWrite();
        }
        catch(Exception e){
            System.out.println("Cannot read or create the stored logs.");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        RootUI.INSTANCE.getChildren().add(HomePage.INSTANCE);

        Scene scene = new Scene(RootUI.INSTANCE, 800, 600);

        primaryStage.setTitle("Usage Monitor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
