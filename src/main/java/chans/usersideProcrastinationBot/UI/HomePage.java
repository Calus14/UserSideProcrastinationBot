package chans.usersideProcrastinationBot.UI;

import chans.usersideProcrastinationBot.UserSideProcrastinationBot;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Home page for when the application is run with a UI
 */
public class HomePage extends VBox {
    public static HomePage INSTANCE = new HomePage();

    private Button runBtn;
    private Button viewButton;

    private HomePage(){
        runBtn = new Button();
        runBtn.setText("Start Monitoring Computer Usage");
        viewButton = new Button();
        viewButton.setText("View Your Computer Usage Statistics");

        setButtonActionHandlers();

        this.setPadding(new Insets(20));
        this.setSpacing((10));
        this.getChildren().add(runBtn);
        this.getChildren().add(viewButton);
        AnalyticsPage.INSTANCE.getAccessibleText();
    }

    private void setButtonActionHandlers(){
        runBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!UserSideProcrastinationBot.monitorThread.isRunning()) {
                    if(!UserSideProcrastinationBot.monitorThread.wasStarted())
                        UserSideProcrastinationBot.monitorThread.start();
                    else
                        UserSideProcrastinationBot.monitorThread.unpause();
                    runBtn.setText("Pause Monitoring Computer Usage");
                }
                else{
                    UserSideProcrastinationBot.monitorThread.pause();
                    runBtn.setText("Start Monitoring Computer Usage");
                }
            }
        });

        viewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RootUI.INSTANCE.getChildren().remove(HomePage.INSTANCE);
                RootUI.INSTANCE.getChildren().add(AnalyticsPage.INSTANCE);
            }
        });
    }
}
