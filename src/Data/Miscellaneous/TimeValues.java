package Data.Miscellaneous;

import java.time.LocalTime;

public class TimeValues {
    private String hourString;
    private String minuteString;
    private String secondString;

    public TimeValues(LocalTime localTime){
        this(localTime.getHour(), localTime.getMinute(), localTime.getSecond());
    }

    public TimeValues(int hour, int minute){
        this(hour, minute, 0);
    }

    public TimeValues(int hour, int minute, int second){
        this(Integer.toString(hour), Integer.toString(minute), Integer.toString(second));
    }

    public TimeValues(String hourString, String minuteString) {
        this(hourString, minuteString, "0");
    }

    public TimeValues(String hourString, String minuteString, String secondString) {
        this.hourString = hourString.trim();
        this.minuteString = minuteString.trim();
        this.secondString = secondString.trim();
    }

    public int getHour() {
        return Integer.parseInt(hourString);
    }

    public int getMinute() {
        return Integer.parseInt(minuteString);
    }

    public int getSecond() {
        return Integer.parseInt(secondString);
    }

    public LocalTime getLocalTime() {
        return LocalTime.of(getHour(), getMinute(), getSecond());
    }

    @Override
    public String toString(){
        return String.format("%s:%s:%s", hourString, minuteString, secondString);
    }
}