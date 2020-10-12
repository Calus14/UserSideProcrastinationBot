package chans.usersideProcrastinationBot.UI.AnalyticsDisplay;

import chans.usersideProcrastinationBot.processMonitoring.LocalAnalytics;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class PieChartDisplay implements AnalyticDisplay {

    // Maps the name of the executable to the full window text name
    protected Map<String, List<String>> executableToWindowsTitleMap;

    // Knowing how many logged readings there our lets us calculate the percentage
    protected double totalLogs;

    public static PieChartDisplay Instance= new PieChartDisplay();

    private PieChartDisplay(){
        executableToWindowsTitleMap = new HashMap<String, List<String>>();
        totalLogs = 0;
    }

    @Override
    public void consumeLocalAnalytics() {
        List<LocalAnalytics.LoggedAnalytic> loadedLogsInOrder = LocalAnalytics.getLoggedAnalytics();
        loadedLogsInOrder.stream().forEach(log ->{
            if( !executableToWindowsTitleMap.containsKey(log.getExecutableName()) ){
                executableToWindowsTitleMap.put(log.getExecutableName(), new ArrayList<String>());
            }
            executableToWindowsTitleMap.get(log.getExecutableName()).add(log.getFullWindowText());
        });

        this.totalLogs = loadedLogsInOrder.size();
    }

    @Override
    public Node getDisplayNode() {
        ArrayList<PieChart.Data> simplifiedData = new ArrayList<PieChart.Data>();
        for(Map.Entry<String, List<String>> entry : this.executableToWindowsTitleMap.entrySet()){
            simplifiedData.add(new PieChart.Data(entry.getKey(),
                                                ( ((float)entry.getValue().size()) / totalLogs) ) );
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(simplifiedData);
        PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Program Usage");

        final Label caption = new Label("");
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 24 arial;");

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    e -> {
                        double total = 0;
                        for (PieChart.Data d : chart.getData()) {
                            total += d.getPieValue();
                        }
                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getSceneY());
                        String text = String.format("%.1f%%", 100*data.getPieValue()/total) ;
                        caption.setText(text);
                    }
            );
        }

        return chart;
    }
}
