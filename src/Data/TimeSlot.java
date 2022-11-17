package Data;

import Data.Miscellaneous.EventManager;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TimeSlot implements Serializable {

    private static int nextId = 0;

    private int taskId;

    private LocalTime startTime;
    private LocalTime endTime;
    private Task arrangedTask;

    TimeSlot(Task arrangedTask, LocalTime startTime, LocalTime endTime){
        taskId = nextId;
        nextId++;

        this.arrangedTask = arrangedTask;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    //<editor-fold desc="Getters and Setters">
    public LocalTime getStartTime(){
        return startTime;
    }

    public LocalTime getEndTime(){
        return endTime;
    }

    public Task getArrangedTask(){
        return arrangedTask;
    }

    void updateTime(LocalTime startTime, LocalTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        arrangedTask.fireUpdateEvent();
    }
    //</editor-fold>



    public boolean isCollideWith(TimeSlot other){
        TimeSlot prior = this.startTime.isBefore(other.startTime) ? this : other;
        TimeSlot later = this != prior ? this : other;
        return prior.isPassThrough(later.startTime);
    }
    /*
        start and end are exclusive!
     */
    private boolean isPassThrough(LocalTime time){
        return (time.isAfter(startTime) || time.equals(startTime)) && time.isBefore(endTime);
    }

    public Duration getDuration(){
        return Duration.between(startTime, endTime);
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof TimeSlot)){
            return false;
        }
        return this.taskId == ((TimeSlot) other).taskId;
    }

    @Override
    public String toString(){
        return String.format("%02d:%02d - %02d:%02d", startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());
    }
}
