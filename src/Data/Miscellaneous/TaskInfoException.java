package Data.Miscellaneous;

import Data.DataController;

import java.time.DateTimeException;

public class TaskInfoException extends Exception{

    private boolean isAlertTitle;
    private boolean isAlertDate;
    private boolean isAlertTime;

    private TaskInfoException(boolean isAlertTitle, boolean isAlertDate){
        this(isAlertTitle, isAlertDate, false);
    }

    private TaskInfoException(boolean isAlertTitle, boolean isAlertDate, boolean isAlertTime){
        this.isAlertDate = isAlertDate;
        this.isAlertTitle = isAlertTitle;
        this.isAlertTime = isAlertTime;
    }


    public boolean isAlertTitle(){
        return isAlertTitle;
    }

    public boolean isAlertDate(){
        return isAlertDate;
    }

    public boolean isAlertTime(){
        return isAlertTime;
    }


    public static TaskInfoException combine(TaskInfoException... exceptions){
        TaskInfoException combined = new TaskInfoException(false, false, false);
        for(TaskInfoException exception : exceptions){
            if(exception == null){
                continue;
            }
            combined.isAlertTime = combined.isAlertTime || exception.isAlertTime;
            combined.isAlertDate = combined.isAlertDate || exception.isAlertDate;
            combined.isAlertTitle = combined.isAlertTitle || exception.isAlertTitle;
        }

        boolean hasAlert = combined.isAlertTime || combined.isAlertTitle || combined.isAlertDate;
        if(hasAlert){
            return combined;
        }else {
            return null;
        }
    }

    //<editor-fold desc="Task Info Exception Generators">
    public static TaskInfoException checkDataForCreate(String title, DateValues dateValues){
        boolean isAlertTitle = isAlertTitle(title);
        boolean isAlertDate = isAlertDate(dateValues);

        if(isAlertTitle || isAlertDate){
            return new TaskInfoException(isAlertTitle, isAlertDate);
        }else {
            return null;
        }
    }

    public static TaskInfoException checkTitle(String title){
        if(isAlertTitle(title)){
            return new TaskInfoException(true, false);
        }else {
            return null;
        }
    }

    public static TaskInfoException checkDate(DateValues dateValues){
        if(isAlertDate(dateValues)){
            return new TaskInfoException(false, true);
        }else {
            return null;
        }
    }

    public static TaskInfoException checkTime(TimeValues start, TimeValues end){
        if(isAlertTime(start, end)){
            return new TaskInfoException(false, false, true);
        }else {
            return null;
        }
    }

    private static boolean isAlertTitle(String title){
        return title.isBlank();
    }

    private static boolean isAlertDescription(String description){
        return false;
    }

    private static boolean isAlertDate(DateValues dateValues){
        try{
            dateValues.getLocalDate();
            return false;
        }catch (NumberFormatException | DateTimeException exc){
            return true;
        }
    }

    private static boolean isAlertTime(TimeValues start, TimeValues end){
        if(!isTimeValid(start) || !isTimeValid(end)){
            return true;
        }

        boolean isEndAfterStart = end.getLocalTime().isAfter(start.getLocalTime());
        return !isEndAfterStart;
    }

    private static boolean isTimeValid(TimeValues timeValues){
        try{
            timeValues.getLocalTime();
        }catch (NumberFormatException | DateTimeException exc){
            return false;
        }
        return true;
    }

    //</editor-fold>


}
