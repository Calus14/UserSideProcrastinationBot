package chans.usersideProcrastinationBot.UI;

import chans.usersideProcrastinationBot.UI.AnalyticsDisplay.PieChartDisplay;
import chans.usersideProcrastinationBot.processMonitoring.LocalAnalytics;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.UnaryOperator;

public class AnalyticsPage  extends GridPane {

    public static AnalyticsPage INSTANCE = new AnalyticsPage();

    private Text startDateText = new Text("Start");
    private Text endDateText = new Text("End");

    private LocalDateStringConverter localDateStringConverter = new LocalDateStringConverter();
    private LocalTimeStringConverter localTimeStringConverter = new LocalTimeStringConverter();

    private TextField startDateField = new TextField();
    @Getter
    private LocalDate startDate = LocalDate.now();
    private TextField startTimeField = new TextField();
    @Getter
    private LocalTime startTime = LocalTime.now();

    private TextField endDateField = new TextField();
    @Getter
    private LocalDate endDate = LocalDate.now();
    private TextField endTimeField = new TextField();
    @Getter
    private LocalTime endTime = LocalTime.now();

    private Button viewButton = new Button("View Usage");
    private Button backButton = new Button("Back");

    private final Border defaultBorder = new Border(new BorderStroke(null, null, null, null));
    private final Border invalidBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2)));

    private boolean allSearchFieldsGood = true;

    private UnaryOperator<TextFormatter.Change> startDateFilter = change ->{
        try {
            localDateStringConverter.fromString(change.getControlNewText());
            allSearchFieldsGood = true;
            startDateField.setBorder(defaultBorder);
        }
        catch(Exception e){
            allSearchFieldsGood = false;
            startDateField.setBorder(invalidBorder);
        }
        return change;
    };

    private UnaryOperator<TextFormatter.Change> endDateFilter = change ->{
        try {
            localDateStringConverter.fromString(change.getControlNewText());
            allSearchFieldsGood = true;
            endDateField.setBorder(defaultBorder);
        }
        catch(Exception e){
            allSearchFieldsGood = false;
            endDateField.setBorder(invalidBorder);
        }
        return change;
    };

    private UnaryOperator<TextFormatter.Change> startTimeFilter = change ->{
        try{
            localTimeStringConverter.fromString(change.getControlNewText());
            allSearchFieldsGood = true;
            startTimeField.setBorder(defaultBorder);
        }
        catch(Exception e){
            allSearchFieldsGood = false;
            startTimeField.setBorder(invalidBorder);
        }
        return change;
    };

    private UnaryOperator<TextFormatter.Change> endTimeFilter = change ->{
        try{
            localTimeStringConverter.fromString(change.getControlNewText());
            allSearchFieldsGood = true;
            endTimeField.setBorder(defaultBorder);
        }
        catch(Exception e){
            allSearchFieldsGood = false;
            endTimeField.setBorder(invalidBorder);
        }
        return change;
    };

    private BooleanBinding validInputBinding = new BooleanBinding(){
        {
            super.bind(startDateField.textProperty(), endDateField.textProperty(), startTimeField.textProperty(), endTimeField.textProperty());
        }

        @Override
        protected boolean computeValue() {
            return !allSearchFieldsGood;
        }
    };

    private AnalyticsPage(){
        this.setUpTextFields();
        this.setButtonActionHandlers();

        this.setAlignment(Pos.TOP_CENTER);
        this.setVgap(10);
        this.setHgap(5);
        this.add(startDateText, 0, 0);
        this.add(startDateField, 1, 0);
        this.add(startTimeField, 2, 0);

        this.add(endDateText, 0, 1);
        this.add(endDateField, 1, 1);
        this.add(endTimeField, 2, 1);

        viewButton.disableProperty().bind(validInputBinding);
        this.add(viewButton, 0, 2, 2, 1);
        this.setHalignment(viewButton, HPos.CENTER);

        this.add(backButton, 2, 2, 2, 1);
        this.setHalignment(backButton, HPos.CENTER);
    }

    private void setUpTextFields() {
        this.startDateField.setTextFormatter( new TextFormatter<>( localDateStringConverter, LocalDate.now(), startDateFilter) );
        this.startDateField.textProperty().addListener( (observable, oldValue, newValue) ->{
                try {
                    startDate = localDateStringConverter.fromString(newValue);
                }
                catch(Exception e) {
                    // Do nothing, dont update it
                }
            }
        );

        this.startTimeField.setTextFormatter( new TextFormatter<>(localTimeStringConverter, LocalTime.now(), startTimeFilter) );
        this.startTimeField.textProperty().addListener( (observable, oldValue, newValue) -> {
                try{
                    startTime = localTimeStringConverter.fromString(newValue);
                }
                catch(Exception e) {
                    // Do nothing, dont update it
                }
            }
        );

        this.endDateField.setTextFormatter( new TextFormatter<>( localDateStringConverter, LocalDate.now(), endDateFilter) );
        this.endDateField.textProperty().addListener( (observable, oldValue, newValue) ->{
                try{
                    endDate = localDateStringConverter.fromString(newValue);
                }
                catch(Exception e) {
                    // Do nothing, dont update it
                }
            }
        );

        this.endTimeField.setTextFormatter( new TextFormatter<>(localTimeStringConverter, LocalTime.now(), endTimeFilter) );
        this.endTimeField.textProperty().addListener( (observable, oldValue, newValue) -> {
                try {
                    endTime = localTimeStringConverter.fromString(newValue);
                }
                catch(Exception e) {
                    // Do nothing, dont update it
                }
            }
        );
    }

    private void setButtonActionHandlers(){

        this.viewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LocalAnalytics.loadPeriod( startDate, endDate, startTime, endTime);
                PieChartDisplay.Instance.consumeLocalAnalytics();
                AnalyticsPage.INSTANCE.add(PieChartDisplay.Instance.getDisplayNode(), 0, 3, 4,4);
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
