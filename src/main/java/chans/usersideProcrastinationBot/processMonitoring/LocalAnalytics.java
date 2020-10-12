package chans.usersideProcrastinationBot.processMonitoring;

import lombok.Data;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Helper class that takes input and returns a data set so that it can be graphically displayed
 */
public class LocalAnalytics {

    @Data
    public static class LoggedAnalytic{

        LocalTime loggedTime;
        String fullWindowText;
        String executableName;

        public LoggedAnalytic(String logLine){
            String[] splitLine = logLine.split(",");
            this.loggedTime = LocalTime.parse(splitLine[0]);
            this.fullWindowText = splitLine[1];
            this.executableName = splitLine[2];
        }
    }

    @Data
    public static class DailyLog{
        List<LoggedAnalytic> orderedAnalytics;
        LocalDate daysDate;

        public DailyLog(LocalDate daysDate){
            this.daysDate = daysDate;
            orderedAnalytics = new LinkedList<LoggedAnalytic>();
        }

        public void filterLogsBeforeTime(LocalTime startTime){
            List<LoggedAnalytic> filteredAnalytics = new LinkedList<LoggedAnalytic>();
            orderedAnalytics.stream()
                .filter( loggedAnalytic -> loggedAnalytic.getLoggedTime().isAfter(startTime) )
                .forEach( loggedAnalytic -> filteredAnalytics.add(loggedAnalytic));
            this.orderedAnalytics = filteredAnalytics;
        }

        public void filterLogsAfterTime(LocalTime endTime){
            List<LoggedAnalytic> filteredAnalytics = new LinkedList<LoggedAnalytic>();
            orderedAnalytics.stream()
                    .filter( loggedAnalytic -> loggedAnalytic.getLoggedTime().isBefore(endTime) )
                    .forEach( loggedAnalytic -> filteredAnalytics.add(loggedAnalytic));
            this.orderedAnalytics = filteredAnalytics;
        }
    }

    protected static List<DailyLog> loadedLogs = new ArrayList<DailyLog>();
    protected static LocalTime startTime;
    protected static LocalTime stopTime;

    protected static void loadUsageFile(LocalDate dateToLoad) throws Exception{
        String filePath = ProcessMonitorThread.localDirectoryPath+ "\\"+dateToLoad.toString();
        File fileToLoad = new File(filePath);
        if(!fileToLoad.exists()){
            throw new Exception("Failed to find the file named "+filePath);
        }

        DailyLog dailyLog = new DailyLog(dateToLoad);
        try(Stream<String> stream = Files.lines(Paths.get(filePath))){
            stream.forEach( line -> {
                try{
                    LoggedAnalytic lineAsAnalytic = new LoggedAnalytic(line);
                    dailyLog.getOrderedAnalytics().add(lineAsAnalytic);
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            });
        }
        catch(Exception e){
            throw(e);
        }
        loadedLogs.add(dailyLog);
    }

    public static void loadPeriod(LocalDate startDate, LocalDate endDate, LocalTime beginTime, LocalTime endTime){
        try{
            loadUsageFile(startDate);
            startDate = startDate.plusDays(1);
            if( endDate != null ){
                while(endDate.isAfter(startDate) || endDate.isEqual(startDate)){
                    loadUsageFile(startDate);
                    startDate = startDate.plusDays(1);
                }
            }
            if( loadedLogs.isEmpty()){
                return;
            }
        }
        catch(Exception e){
            //TODO pop up an error window if we are using the graphics
            System.out.println(e.getMessage());
            e.printStackTrace();
            return;
        }

        // Only applies to the startDate
        if(beginTime != null){
            startTime = beginTime;
            loadedLogs.get(0).filterLogsBeforeTime(beginTime);
        }
        if(endTime != null){
            stopTime = endTime;
            loadedLogs.get(loadedLogs.size()-1).filterLogsAfterTime(endTime);
        }
    }

    public static List<LoggedAnalytic> getLoggedAnalytics(){
        LinkedList<LoggedAnalytic> combinedAnalytics = new LinkedList<LoggedAnalytic>();
        for(DailyLog dailyLog : loadedLogs){
            combinedAnalytics.addAll(dailyLog.getOrderedAnalytics());
        }
        return combinedAnalytics;
    }

}
