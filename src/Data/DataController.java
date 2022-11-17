package Data;

import Data.Miscellaneous.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class DataController {

    public static final DataController instance = new DataController();

    private LinkedList<Task> tasks;
    private LinkedList<TimeSlot> timeSlots;


    private EventManager<Task> taskCreationUpdateEventManager;
    private EventManager<Task> taskRemovalEventManager;

    private EventManager<TimeSlot> timeSlotCreationUpdateEventManager;
    private EventManager<TimeSlot> timeSlotRemovalEventManager;

    private DataController(){
        tasks = new LinkedList<>();
        timeSlots = new LinkedList<>();

        taskCreationUpdateEventManager = new EventManager<>();
        taskRemovalEventManager = new EventManager<>();
        timeSlotCreationUpdateEventManager = new EventManager<>();
        timeSlotRemovalEventManager = new EventManager<>();
    }


    public void initializeData(){
        LinkedList<Task> tasks = DataIO.loadTasks();
        if(tasks == null){
            return;
        }

        for(Task task : tasks){
            task.initializeWhenLoaded();
            this.tasks.add(task);
            if(task.getTimeSlot() != null){
                timeSlots.add(task.getTimeSlot());
            }

            taskCreationUpdateEventManager.notifyListener(task);
            timeSlotCreationUpdateEventManager.notifyListener(task.getTimeSlot());

        }
    }


    public Task createTask(String title, DateValues dateValues) throws TaskInfoException {
        TaskInfoException taskInfoException = TaskInfoException.checkDataForCreate(title, dateValues);
        if(taskInfoException != null){
            throw taskInfoException;
        }

        LocalDate date = LocalDate.of(dateValues.getYear(), dateValues.getMonth(), dateValues.getDayOfMonth());
        Task task = new Task(title, date);

        tasks.add(task);
        taskCreationUpdateEventManager.notifyListener(task);

        DataIO.saveTasks(tasks);
        return task;
    }

    public void updateTitle(Task task, String title) throws TaskInfoException{
        TaskInfoException taskInfoException = TaskInfoException.checkTitle(title);
        if(taskInfoException != null){
            throw taskInfoException;
        }
        task.updateTitle(title);
        taskCreationUpdateEventManager.notifyListener(task);

        DataIO.saveTasks(tasks);

    }

    public void updateDescription(Task task, String description) throws TaskInfoException{
        task.updateDescription(description);
        taskCreationUpdateEventManager.notifyListener(task);

        DataIO.saveTasks(tasks);

    }

    public void updateDate(Task task, DateValues dateValues) throws TaskInfoException {
        TaskInfoException taskInfoException = TaskInfoException.checkDate(dateValues);
        if(taskInfoException != null){
            throw taskInfoException;
        }
        task.updateDate(dateValues.getLocalDate());
        taskCreationUpdateEventManager.notifyListener(task);
        timeSlotCreationUpdateEventManager.notifyListener(task.getTimeSlot());

        DataIO.saveTasks(tasks);

    }

    public void updateTime(Task task, TimeValues start, TimeValues end) throws TaskInfoException{
        TaskInfoException taskInfoException = TaskInfoException.checkTime(start, end);
        if(taskInfoException != null){
            throw taskInfoException;
        }
        if(task.getTimeSlot() == null){
            TimeSlot timeSlot = addTimeSlot(task, start, end);
            task.updateTimeSlot(timeSlot);
        }else {
            task.getTimeSlot().updateTime(start.getLocalTime(), end.getLocalTime());
        }
        taskCreationUpdateEventManager.notifyListener(task);
        timeSlotCreationUpdateEventManager.notifyListener(task.getTimeSlot());

        DataIO.saveTasks(tasks);

    }

    private TimeSlot addTimeSlot(Task task, TimeValues start, TimeValues end){
        TimeSlot timeSlot = new TimeSlot(task, start.getLocalTime(), end.getLocalTime());
        timeSlots.add(timeSlot);
        timeSlotCreationUpdateEventManager.notifyListener(timeSlot);

        DataIO.saveTasks(tasks);

        return timeSlot;
    }

    public void updateTaskCompletion(Task task, boolean isCompleted){
        task.setCompletion(isCompleted);
        taskCreationUpdateEventManager.notifyListener(task);

        DataIO.saveTasks(tasks);

    }

    public void removeTask(Task task){
        tasks.remove(task);
        timeSlots.remove(task.getTimeSlot());
        taskRemovalEventManager.notifyListener(task);
        timeSlotRemovalEventManager.notifyListener(task.getTimeSlot());

        DataIO.saveTasks(tasks);
    }

    //<editor-fold desc= "Subscribe and Unsubscribe Methods">

    public void registerTaskCreateUpdateListener(EventListener<Task> listener){
        taskCreationUpdateEventManager.registerListener(listener);
    }

    public void unregisterTaskCreateUpdateListener(EventListener<Task> listener){
        taskCreationUpdateEventManager.unregisterListener(listener);
    }

    public void registerTaskRemoveListener(EventListener<Task> listener){
        taskRemovalEventManager.registerListener(listener);
    }

    public void unregisterTaskRemoveListener(EventListener<Task> listener){
        taskRemovalEventManager.unregisterListener(listener);
    }

    public void registerTimeSlotCreateUpdateListener(EventListener<TimeSlot> listener){
        timeSlotCreationUpdateEventManager.registerListener(listener);
    }

    public void unregisterTimeSlotCreateUpdateListener(EventListener<TimeSlot> listener){
        timeSlotCreationUpdateEventManager.unregisterListener(listener);
    }

    public void registerTimeSlotRemoveListener(EventListener<TimeSlot> listener){
        timeSlotRemovalEventManager.registerListener(listener);
    }

    public void unregisterTimeSlotRemoveListener(EventListener<TimeSlot> listener) {
        timeSlotRemovalEventManager.unregisterListener(listener);
    }
    //</editor-fold>


    public LinkedList<Task> getTasks(){
        return tasks;
    }

    public LinkedList<TimeSlot> getTimeSlots(){
        return timeSlots;
    }


}
