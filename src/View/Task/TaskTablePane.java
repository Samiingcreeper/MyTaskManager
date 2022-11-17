package View.Task;

import Filter.Logical.And;
import Filter.ArithmeticTaskFilter;
import Filter.EntityDataGetter;
import Filter.FilterMode.LocalDateFilterMode;
import Filter.TaskFilter;
import View.Config;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;

public class TaskTablePane extends BorderPane {

    private static final double CREATION_PANE_WIDTH = 250;
    private static final double CREATION_PANE_HEIGHT = 250;

    private static final double TASK_PANE_WIDTH = 350;

    private static final double TABLE_MIN_HEIGHT = 450;

    private HBox taskPaneRow;
    private TaskDisplayPane todayTaskPane;
    private TaskDisplayPane tomorrowTaskPane;
    private TaskDisplayPane allTaskPane;

    private Config.Stripe todayStripe;
    private Config.Stripe tomorrowStripe;
//    private TaskCreatorPane creatorPane;

    private LocalDate showcaseDate;

    private boolean isShowCompleted = false;

    public TaskTablePane(){
        taskPaneRow = new HBox();
        todayTaskPane = new TaskDisplayPane();
        tomorrowTaskPane = new TaskDisplayPane();

        updateShowcaseDate(LocalDate.now());

        allTaskPane = new TaskDisplayPane();
//        creatorPane = new TaskCreatorPane();

        initializeControlPane();
        initializeTaskPaneRow();
        initializeCreatorPane();

        setCenter(taskPaneRow);
//        setLeft(creatorPane);
//        setAlignment(creatorPane, Pos.TOP_LEFT);

        setMinHeight(TABLE_MIN_HEIGHT);
    }

    private void initializeControlPane(){
        setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
//        setMargin(creatorPane, new Insets(0, Config.DEFAULT_SPACING, 0, 0));
        setPadding(new Insets(Config.DEFAULT_SPACING));
    }

    private void initializeTaskPaneRow(){
        taskPaneRow.setSpacing(Config.DEFAULT_SPACING);

        todayStripe = Config.getStripe("\uD83D\uDCC3 當日事項", Color.ORANGE);
        tomorrowStripe = Config.getStripe("\uD83D\uDCC3 當日後天事項", Color.ORANGE);
        Config.Stripe allStripe = Config.getStripe("\uD83D\uDCC3 所有事項", Color.ORANGE);

        VBox v1 = Config.alignedVBox(todayStripe, todayTaskPane);
        VBox v2 = Config.alignedVBox(tomorrowStripe, tomorrowTaskPane);
        VBox v3 = Config.alignedVBox(allStripe, allTaskPane);

        taskPaneRow.getChildren().addAll(v1, v2, v3, Config.alignedVBox(getShowCompleteLabel()));
        todayTaskPane.setPrefWidth(TASK_PANE_WIDTH);
        tomorrowTaskPane.setPrefWidth(TASK_PANE_WIDTH);
        allTaskPane.setPrefWidth(TASK_PANE_WIDTH);
    }

    private void initializeCreatorPane(){
//        creatorPane.setPrefWidth(CREATION_PANE_WIDTH);
//        creatorPane.setMaxHeight(CREATION_PANE_HEIGHT);
    }



    public void updateShowcaseDate(LocalDate showcaseDate){
        this.showcaseDate = showcaseDate;

        TaskFilter todayFilter;
        TaskFilter tomorrowFilter;
        if(!isShowCompleted){
            todayFilter = new And(
                    TaskDisplayPane.FILTER_ONLY_UNCOMPLETED,
                    new ArithmeticTaskFilter<>(showcaseDate, EntityDataGetter.TASK_DATE, LocalDateFilterMode.AT_THE_SAME_DAY));
            tomorrowFilter = new And(
                    TaskDisplayPane.FILTER_ONLY_UNCOMPLETED,
                    new ArithmeticTaskFilter<>(showcaseDate, EntityDataGetter.TASK_DATE, LocalDateFilterMode.isAfterDays(1)));
        }else{
            todayFilter = new ArithmeticTaskFilter<>(showcaseDate, EntityDataGetter.TASK_DATE, LocalDateFilterMode.AT_THE_SAME_DAY);
            tomorrowFilter = new ArithmeticTaskFilter<>(showcaseDate, EntityDataGetter.TASK_DATE, LocalDateFilterMode.isAfterDays(1));
        }

        todayTaskPane.updateTaskFilter(todayFilter);
        tomorrowTaskPane.updateTaskFilter(tomorrowFilter);
    }

    public void isShowCompleted(boolean isShowCompleted){
        TaskFilter todayFilter;
        TaskFilter tomorrowFilter;
        this.isShowCompleted = isShowCompleted;
        if(!isShowCompleted){
            todayFilter = new And(
                    TaskDisplayPane.FILTER_ONLY_UNCOMPLETED,
                    new ArithmeticTaskFilter<>(showcaseDate, EntityDataGetter.TASK_DATE, LocalDateFilterMode.AT_THE_SAME_DAY));
            tomorrowFilter = new And(
                    TaskDisplayPane.FILTER_ONLY_UNCOMPLETED,
                    new ArithmeticTaskFilter<>(showcaseDate, EntityDataGetter.TASK_DATE, LocalDateFilterMode.isAfterDays(1)));
            allTaskPane.updateTaskFilter(TaskDisplayPane.FILTER_ONLY_UNCOMPLETED);
        }else{
            todayFilter = new ArithmeticTaskFilter<>(showcaseDate, EntityDataGetter.TASK_DATE, LocalDateFilterMode.AT_THE_SAME_DAY);
            tomorrowFilter = new ArithmeticTaskFilter<>(showcaseDate, EntityDataGetter.TASK_DATE, LocalDateFilterMode.isAfterDays(1));
            allTaskPane.updateTaskFilter(TaskDisplayPane.ACCEPT_ALL);
        }

        todayTaskPane.updateTaskFilter(todayFilter);
        tomorrowTaskPane.updateTaskFilter(tomorrowFilter);
    }

    private Label getShowCompleteLabel(){
        Label label = new Label(" 顯示已完成事項");
        label.setFont(Font.font(Config.DEFAULT_FONT_NAME, 16));

        CheckBox cb = new CheckBox();
        cb.setOnAction( action ->{
            isShowCompleted(cb.isSelected());
        });

        label.setGraphic(cb);
        label.setContentDisplay(ContentDisplay.LEFT);

        label.setPadding(new Insets(25, 5, 25, 30));

        Color color = Color.ORANGE;
        color = Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.25);
        label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefSize(290, 30);

        return label;
    }

}
