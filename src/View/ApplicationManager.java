package View;

import Data.DataController;
import View.Calendar.Calendar;
import View.Task.TaskCreatorPane;
import View.Task.TaskTablePane;
import View.Task.TaskUpdaterPane;
import View.TimeTable.TimeTablePane;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ApplicationManager extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    private static final double WINDOW_WIDTH = 600;
    private static final double WINDOW_HEIGHT = 900;

    private static StackPane mainScenePane;
    private static Scene mainScene;

    private static TaskUpdaterPane updatePane;


    public static TaskTablePane taskTablePlane;
    public static TimeTablePane timeTablePane;

    @Override
    public void start(Stage primaryStage){

        mainScenePane = new StackPane();
        mainScene = new Scene(mainScenePane);
        updatePane = new TaskUpdaterPane();

        taskTablePlane = new TaskTablePane();
        timeTablePane = new TimeTablePane();


        mainScenePane.getChildren().addAll(new MainPane(), updatePane);
        primaryStage.setScene(mainScene);

        DataController.instance.initializeData();

//        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setTitle("時間行程管理程式");
        primaryStage.show();
    }

    public static Scene getMainScene(){
        return mainScene;
    }

    public static TaskUpdaterPane getUpdatePane(){
        return updatePane;
    }



    private class MainPane extends BorderPane{

        public MainPane(){
            setBackground(Config.WHITE_BACKGROUND);

            VBox left = new VBox(Config.getStripe("\uD83D\uDCC5 日厝", Color.RED) ,new Calendar(),
                    Config.getStripe("✒ 新建事項", Color.RED), new TaskCreatorPane());
            TaskTablePane taskTable = taskTablePlane;
            TimeTablePane timeTable = timeTablePane;

//            HBox taskTableRow = new HBox(taskTable, new Config.ShowCompleteStripe("顯示已完成任務" , Color.YELLOW,16));
//            taskTableRow.setSpacing(10);

            left.setSpacing(10);
            left.setPadding(new Insets(10));

            VBox center = new VBox(Config.getBigStripe("\uD83D\uDCDC 事項清單", Color.ORANGE), taskTable,
                    Config.getBigStripe("\uD83D\uDCC8 時間表", Color.GREEN), timeTable);

            center.setSpacing(10);
            center.setPadding(new Insets(10));
            center.setBackground(Config.WHITE_BACKGROUND);

            ScrollPane centerWrap = new ScrollPane(center);
            centerWrap.setPrefWidth(1480);
            centerWrap.setStyle("-fx-background-color:transparent;");

            setLeft(left);
            setCenter(centerWrap);
        }
    }
}
