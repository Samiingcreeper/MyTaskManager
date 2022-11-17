package Data;

import Data.Miscellaneous.EventListener;
import Data.Miscellaneous.EventManager;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task implements Serializable {

    private static int NEXT_ID = 0;

    private int taskId;

    private String title = "";                   // MAX 150 chars
    private String description = "";             // optional, MAX

    private LocalDate date;
    private LocalDateTime dueDateTime;      // optional
    private TimeSlot timeSlot;              // optional

    private boolean isPinned;
    private boolean isCompleted;


    transient private EventManager<Task> updateEventManager;

    //<editor-fold desc="Constructors">
    Task(String title, LocalDate date){
        this.taskId = NEXT_ID;
        NEXT_ID++;

        this.title = title;
        this.date = date;

        updateEventManager = new EventManager<>();
    }
    //</editor-fold>


    void initializeWhenLoaded(){
        updateEventManager = new EventManager<>();
    }

    //<editor-fold desc = "Getters and Setters">
    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public LocalDate getDate(){
        return date;
    }

    public LocalDateTime getDueDateTime(){
        return dueDateTime;
    }

    public TimeSlot getTimeSlot(){
        return timeSlot;
    }

    public boolean isPinned(){
        return isPinned;
    }

    public boolean isCompleted(){
        return isCompleted;
    }


    void updateTitle(String title){
        this.title = title;
    }

    void updateDescription(String description){
        this.description = description;
        updateEventManager.notifyListener(this);
    }

    void updateDate(LocalDate date){
        this.date = date;
        updateEventManager.notifyListener(this);
    }
    void updateDate(int year, int month, int dayOfMonth){
        updateDate(LocalDate.of(year, month, dayOfMonth));
    }

    void updateDueDateTime(LocalDateTime dueDateTime){
        this.dueDateTime = dueDateTime;
        updateEventManager.notifyListener(this);
    }

    void updateTimeSlot(TimeSlot timeSlot){
        this.timeSlot = timeSlot;
        updateEventManager.notifyListener(this);
    }

    void setCompletion(boolean isCompleted){
        this.isCompleted = isCompleted;
        updateEventManager.notifyListener(this);
    }

    //</editor-fold>


    void fireUpdateEvent(){
        updateEventManager.notifyListener(this);
    }


    //<editor-fold desc= "Register and Unregister Listening Methods">

    public void registerUpdateListener(EventListener<Task> listener){
        updateEventManager.registerListener(listener);
    }

    public void unregisterUpdateListener(EventListener<Task> listener){
        updateEventManager.unregisterListener(listener);
    }

    //</editor-fold>

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Task)) return false;
        Task other = (Task) obj;
        return other.taskId == this.taskId;
    }


}
