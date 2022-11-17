package View.TimeTable;

import Data.DataController;
import Data.TimeSlot;
import Filter.ArithmeticTaskFilter;
import Filter.EntityDataGetter;
import Filter.FilterMode.BooleanFilterMode;
import Filter.FilterMode.LocalDateFilterMode;
import Filter.TaskFilter;
import View.Config;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class ColumnsOfDay extends HBox {

    private LocalDate date;
    private TaskFilter taskFilter;

    private TimeTablePane timeTablePane;
    private HashSet<TimeSlot> timeSlots = new HashSet<>();
    private ArrayList<ArrayList<TimeSlot>> sortedStacks;

    public ColumnsOfDay(TimeTablePane timeTablePane, LocalDate date){
        updateShowcaseDate(date);

        this.timeTablePane = timeTablePane;
        update();
        DataController.instance.registerTimeSlotCreateUpdateListener(timeSlot ->{
            handleNewOrUpdatedTimeSlot(timeSlot);
        });
        DataController.instance.registerTimeSlotRemoveListener(timeSlots ->{
            handleRemoveTimeSlot(timeSlots);
        });
        setMinWidth(TimeTablePane.CELL_WIDTH);


    }

    private void update(){
        sortedStacks = sortTimeSlotsIntoStacks(new ArrayList<>(timeSlots.stream().toList()));

        getChildren().clear();
        for(ArrayList<TimeSlot> currStack : sortedStacks){
            Column column = new Column(currStack, this);
            getChildren().add(column);
        }

        // if there's no time slots in this day, then create an empty column
        if(getChildren().size() == 0){
            getChildren().add(new Column(new ArrayList<>(), this));
        }
    }

    private void handleNewOrUpdatedTimeSlot(TimeSlot timeSlot){
        if(timeSlot == null){
            return;
        }
        if(taskFilter.isMatch(timeSlot.getArrangedTask())){
            timeSlots.add(timeSlot);
            update();
            timeTablePane.updateBackgroundGrid();
            timeTablePane.updateDateLabels();
        }else {
            handleRemoveTimeSlot(timeSlot);
        }
    }

     void handleRemoveTimeSlot(TimeSlot timeSlot){
        if(timeSlot == null){
            return;
        }
        if(timeSlots.contains(timeSlot)) timeSlots.remove(timeSlot);
        update();
        timeTablePane.updateBackgroundGrid();
        timeTablePane.updateDateLabels();
    }


    void setDate(LocalDate date){
        this.date = date;
    }


    //<editor-fold desc="Sort TimeSlots Into Stacks">

    /*
        Sort the time slots into stacks so that for each time slot in a stack, the time slot doesn't collide with other time slots within the same stack.
        The reason of doing so is so show multiple tasks happening in the same time correctly. The task will be shown in multiple columns.
     */
    private ArrayList<ArrayList<TimeSlot>> sortTimeSlotsIntoStacks(ArrayList<TimeSlot> timeSlots){
        ArrayList<ArrayList<TimeSlot>> stacks = new ArrayList<>();

        for(TimeSlot timeSlot : timeSlots){
            ArrayList<TimeSlot> stackToPut = getStackToPutTimeSlot(timeSlot, stacks);
            if(stackToPut != null){
                stackToPut.add(timeSlot);
            }else {
                ArrayList<TimeSlot> newStack = new ArrayList<>();
                newStack.add(timeSlot);
                stacks.add(newStack);
            }
        }

        return stacks;
    }
    private ArrayList<TimeSlot> getStackToPutTimeSlot(TimeSlot timeSlot, ArrayList<ArrayList<TimeSlot>> stacks){
        traversStacks:
        for(ArrayList<TimeSlot> currStack : stacks){

            for(TimeSlot timeSlotInStack : currStack){
                if(timeSlotInStack.isCollideWith(timeSlot)){
                    continue traversStacks;
                }
            }
            return currStack;
        }
        return null;
    }

    //</editor-fold>


    void updateShowcaseDate(LocalDate date){
        this.date = date;
        taskFilter = new ArithmeticTaskFilter<LocalDate>(date, EntityDataGetter.TASK_DATE, LocalDateFilterMode.AT_THE_SAME_DAY);

        timeSlots.clear();
        getChildren().clear();

        for(TimeSlot timeSlot : DataController.instance.getTimeSlots()){
            handleNewOrUpdatedTimeSlot(timeSlot);
        }
    }


    int getColumnCount(){
        return Math.max(getChildren().size(), 1);
    }

    LocalDate getShowcaseDate(){
        return date;
    }

}
