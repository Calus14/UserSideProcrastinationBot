package chans.usersideProcrastinationBot.UI.AnalyticsDisplay;

import chans.usersideProcrastinationBot.processMonitoring.LocalAnalytics;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PieChartDisplay implements AnalyticDisplay {

    // Maps the name of the executable to the full window text name
    protected Map<String, List<String>> executableToWindowsTitleMap;

    // Knowing how many logged readings there our lets us calculate the percentage
    protected double totalLogs;
    protected final int numberOfProgramsToShow =10;

    protected final PieChart chart = new PieChart();

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

    public void updatePieChart(){
        ArrayList<PieChart.Data> simplifiedData = new ArrayList<PieChart.Data>();

        // List from highest to lowest used executables in our logs
        List<Map.Entry> sortedExecutables = this.executableToWindowsTitleMap.entrySet().stream()
                .sorted( (entry1, entry2) -> {return Integer.compare(entry2.getValue().size(), entry1.getValue().size());})
                .collect(Collectors.toList());

        double totalPercent = 0.f;

        for(Map.Entry<String, List<String>> entry : sortedExecutables){

            // the last entry in the chart will be "other"
            if(simplifiedData.size() == numberOfProgramsToShow-1){
                simplifiedData.add(new PieChart.Data("Other", 100.f-totalPercent));
                break;
            }

            totalPercent = (100.f * (float)entry.getValue().size()) / totalLogs;
            simplifiedData.add(new PieChart.Data(entry.getKey(), totalPercent));
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(simplifiedData);
        chart.setPrefSize(450, 300);
        chart.setData(pieChartData);
        chart.setLegendVisible(true);
        chart.setLabelsVisible(true);
        chart.setTitle("Program Usage");

        final Label caption = new Label("");
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 24 arial;");
    }

    @Override
    public Node getDisplayNode() {
        updatePieChart();
        return chart;
    }
}
