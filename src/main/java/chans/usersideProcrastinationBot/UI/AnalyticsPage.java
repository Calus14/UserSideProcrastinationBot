package chans.usersideProcrastinationBot.UI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class AnalyticsPage  extends GridPane {

    public static AnalyticsPage INSTANCE = new AnalyticsPage();

    private Text startDateText = new Text("Start");
    private Text endDateText = new Text("End");

    private TextField startDateField;
    private TextField startTimeField;
    private ComboBox<String> startAmPm;

    private TextField endDateField;
    private TextField endTimeField;
    private ComboBox<String> endAmPm;

    private Button viewButton = new Button("View Usage");
    private Button backButton = new Button("Back");

    private AnalyticsPage(){
        this.setUpTextFields();
        this.setButtonActionHandlers();

        this.setAlignment(Pos.TOP_CENTER);
        this.setVgap(10);
        this.setHgap(5);
        this.add(startDateText, 0, 0);
        this.add(startDateField, 1, 0);
        this.add(startTimeField, 2, 0);
        this.add(startAmPm, 3, 0);

        this.add(endDateText, 0, 1);
        this.add(endDateField, 1, 1);
        this.add(endTimeField, 2, 1);
        this.add(endAmPm, 3, 1);

        this.add(viewButton, 0, 2, 2, 1);
        this.setHalignment(viewButton, HPos.CENTER);

        this.add(backButton, 2, 2, 2, 1);
        this.setHalignment(backButton, HPos.CENTER);
    }

    private void setUpTextFields() {
        this.startDateField = new TextField();
        this.startTimeField = new TextField();
        this.startAmPm = new ComboBox<>();
        this.startAmPm.getItems().addAll("AM", "PM");
        this.startAmPm.getSelectionModel().selectFirst();

        this.endDateField = new TextField();
        this.endTimeField = new TextField();
        this.endAmPm = new ComboBox<>();
        this.endAmPm.getItems().addAll("AM", "PM");
        this.endAmPm.getSelectionModel().selectFirst();
    }

    private void setButtonActionHandlers(){

        this.viewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO show a pie chart
            }
        });

        this.backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RootUI.INSTANCE.getChildren().remove(AnalyticsPage.INSTANCE);
                RootUI.INSTANCE.getChildren().add(HomePage.INSTANCE);
            }
        });

    }
}
