package View.Task;

import Data.Task;
import Data.DataController;
import Filter.*;
import Filter.FilterMode.BooleanFilterMode;
import Filter.FilterMode.LocalDateFilterMode;
import Filter.Logical.And;
import Filter.Logical.Or;
import View.Config;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;

import java.time.LocalDate;

public class TaskDisplayPane extends VBox {

    public static final TaskFilter FILTER_ONLY_UNCOMPLETED = new ArithmeticTaskFilter<Boolean>(false, EntityDataGetter.TASK_IS_COMPLETED, BooleanFilterMode.EQUALS);
    public static final TaskFilter ACCEPT_ALL = new Or(new ArithmeticTaskFilter<Boolean>(false, EntityDataGetter.TASK_IS_COMPLETED, BooleanFilterMode.EQUALS),
            new ArithmeticTaskFilter<Boolean>(true, EntityDataGetter.TASK_IS_COMPLETED, BooleanFilterMode.EQUALS));

    private TaskFilter taskFilter = FILTER_ONLY_UNCOMPLETED;


    public TaskDisplayPane(){
        this(null);
    }

    public TaskDisplayPane(TaskFilter taskFilter){
        if(taskFilter != null){
            updateTaskFilter(taskFilter);
        }
        initializePaneStyle();
        DataController.instance.registerTaskCreateUpdateListener(this::handleNewOrUpdatedTask);
        DataController.instance.registerTaskRemoveListener(this::removeTaskCard);
    }

    private void initializePaneStyle(){
        setSpacing(Config.DEFAULT_SPACING);
    }

    //<editor-fold desc="Task Card Manipulations">
    private void handleNewOrUpdatedTask(Task task){
        if(taskFilter.isMatch(task)){
            addTaskCard(task);
        }else {
            removeTaskCard(task);
        }
    }

    private void addTaskCard(Task task){
        if(isTaskShownHere(task)){
            return;
        }
        TaskCard taskCard = new TaskCard(task);
        getChildren().add(taskCard);
    }

    private void removeTaskCard(Task targetTask){
        TaskCard targetTaskCard = getTaskCard(targetTask);
        if(!isTaskShownHere(targetTask)){
            return;
        }
        getChildren().remove(targetTaskCard);
    }
    //</editor-fold>


    //<editor-fold desc="Helper Methods">
    private boolean isTaskShownHere(Task task){
        return getTaskCard(task) != null;
    }

    private TaskCard getTaskCard(Task task){
        for(Node node : getChildren()){
            if(!(node instanceof TaskCard)) continue;

            TaskCard currTaskCard = (TaskCard) node;
            if(currTaskCard.getTask().equals(task)){
                return currTaskCard;
            }
        }
        return null;
    }

    //</editor-fold>


    void updateTaskFilter(TaskFilter taskFilter){
        this.taskFilter = taskFilter;
        getChildren().clear();
        for(Task task : DataController.instance.getTasks()){
            handleNewOrUpdatedTask(task);
        }
    }

}
