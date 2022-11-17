package View.TimeTable;

import Data.Miscellaneous.DateValues;
import Data.Miscellaneous.EventListener;
import Data.*;
import Data.Miscellaneous.TaskInfoException;
import Data.Miscellaneous.TimeValues;
import View.ApplicationManager;
import View.Config;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;

public class TimeSlotView extends BorderPane {

    private static final BackgroundFill BACKGROUND_FILL = new BackgroundFill(Color.rgb(173, 216, 230, 0.9), CornerRadii.EMPTY, new Insets(0));
    private static final BackgroundFill BACKGROUND_FILL_COMPLETED = new BackgroundFill(Color.rgb(211, 211, 211, 0.9), CornerRadii.EMPTY, new Insets(0));
    private static final double VIEW_PADDING = 6;
    private static final double STRETCH_ZONE_HEIGHT = 10;

    private TimeSlot timeSlot;
    private ColumnsOfDay columnsOfDay;

    private Label lbTitle;
    private Label lbTime;
    private Label lbDescription;
    private VBox container;

    private boolean isFolded = false;
    private boolean isDragging = false;
    private boolean isStretching = false;

    private EventListener<Task> updateListener;

    public TimeSlotView(TimeSlot timeSlot, ColumnsOfDay columnsOfDay){
        this.timeSlot = timeSlot;
        this.columnsOfDay = columnsOfDay;
        renderView();
        updateListener = task -> updateView(task);
        timeSlot.getArrangedTask().registerUpdateListener(updateListener);

        setOnMouseClicked( mouseClick ->{
            if(mouseClick.isControlDown()){
                ApplicationManager.getUpdatePane().enter(timeSlot.getArrangedTask());
            }else if(mouseClick.isAltDown()) {
                System.out.println("btn down");
                duplicateTask();
            }
        });

        setOnMouseEntered( mouseEnter ->{
            setCursor(Cursor.CLOSED_HAND);
        });

        setOnMousePressed( mousePress -> {

            isStretching = false;
            isDragging = false;

            if(mousePress.isControlDown() || mousePress.isAltDown()){

            }
            else if(mousePress.isShiftDown()){
                isStretching = true;
            }else {
                handleDragStart(columnsOfDay, mousePress);
            }
        });

        setOnMouseDragged( mouseDrag ->{
            if(mouseDrag.isShiftDown() && !isDragging){
                handleStretching(mouseDrag);
            }else if(!isStretching) {
                handleDragging(mouseDrag);
            }else if(isStretching){
                handleStretchEnd(mouseDrag);
            }

        });

        setOnMouseReleased( mouseRelease ->{
            System.out.println("release");
            if(isStretching){
                isStretching = false;
                handleStretchEnd(mouseRelease);
            }else{
                handleDragEnd(mouseRelease);
            }

        });


    }

    private void handleDragStart(ColumnsOfDay columnsOfDay, MouseEvent mousePress) {
        ApplicationManager.timeTablePane.setDraggingView(this);
        draggingOffset = new Point2D(mousePress.getX(), mousePress.getY());
        isDragging = true;
    }

    private void renderView(){
        setPadding(new Insets(VIEW_PADDING));

        initializeLabels();
        updateView(timeSlot.getArrangedTask());
        BorderPane.setAlignment(container, Pos.TOP_LEFT);
        setState();

        positionView();
    }

    private void setState(){
        setBackground(new Background(timeSlot.getArrangedTask().isCompleted() ? BACKGROUND_FILL_COMPLETED : BACKGROUND_FILL));
        setOpacity(timeSlot.getArrangedTask().isCompleted() ? 0.5 : 1);
    }

    private void initializeLabels() {
        lbTitle = new Label();
        lbTitle.setFont(Config.DEFAULT_FONT);
        lbTitle.setWrapText(true);

        lbTime = new Label();
        lbTime.setFont(Config.SMALL_FONT);

        lbDescription = new Label();
        lbDescription.setFont(Config.SMALL_FONT);
        lbDescription.setWrapText(true);

        container = new VBox(new VBox(lbTitle, lbTime), lbDescription);
        container.setSpacing(10);
    }

    private void positionView() {
        Point2D size = getViewSize();
        setPrefSize(size.getX(), size.getY());
        setMaxHeight(size.getY());
        setLayoutY(getViewYPosInColumn());
    }


    private void updateView(Task task){
        lbTitle.setText(timeSlot.getArrangedTask().getTitle());
        lbTime.setText(timeSlot.toString());
        lbDescription.setText(timeSlot.getArrangedTask().getDescription());

        boolean isFold = getViewSize().getY() < 60;
        if(isFold){
            Label lbTooBig = new Label("●  ●  ●");
            lbTooBig.setFont(Font.font(Config.DEFAULT_FONT_NAME, 4));
            setCenter(lbTooBig);
            appendTooltip();

        }else if(!getChildren().contains(container)){
            setCenter(container);
        }

        setState();
    }

    private void appendTooltip(){
        Rectangle rect = new Rectangle(0, 0, 200, 100);
        String string = timeSlot.getArrangedTask().getTitle() + "\n"+
                        timeSlot.toString() + "\n" +
                        timeSlot.getArrangedTask().getDescription();
        Tooltip tooltip = new Tooltip(string);
        tooltip.setShowDelay(Duration.millis(200));
        Tooltip.install(this, tooltip);
    }

    //<editor-fold desc="Helper Methods">
    private Point2D getViewSize(){
        double durationInHour = Math.abs(TimeTablePane.getSecondPassed(timeSlot.getStartTime(), timeSlot.getEndTime())) / 3600d;
        return new Point2D(TimeTablePane.CELL_WIDTH,durationInHour * TimeTablePane.CELL_HEIGHT);
    }

    private double getViewYPosInColumn(){
        double startTimeInHour = TimeTablePane.getSecondsOfDate(timeSlot.getStartTime()) / 3600d;
        return startTimeInHour * TimeTablePane.CELL_HEIGHT;
    }
    //</editor-fold>

    private Point2D draggingOffset;

    private void handleDragging(MouseEvent mouseDrag){
        if(!isDragging){

        }

//        Point2D snappedPos = ApplicationManager.timeTablePane.getSnappedMousePos(mouseDrag.getSceneX(), mouseDrag.getSceneY());
//        System.out.println(snappedPos);
//        setLayoutX(Math.min(snappedPos.getX(), TimeTablePane.CELL_WIDTH * 6 + TimeTablePane.getXOffset()));
//        setLayoutY(Math.min(snappedPos.getY(), TimeTablePane.CELL_HEIGHT * 24 + TimeTablePane.getYOffset() - getViewSize().getY()));

        Point2D local = ApplicationManager.timeTablePane.getRelativePosFromScene(mouseDrag.getSceneX(), mouseDrag.getSceneY());
        setLayoutX(local.getX() - draggingOffset.getX());
        setLayoutY(local.getY());

        ApplicationManager.timeTablePane.moveAndSnapGuideLine(mouseDrag.getSceneX(), mouseDrag.getSceneY());
    }

    private void handleDragEnd(MouseEvent mouseRelease){
        if(!isDragging){
            return;
        }

        Point2D snappedPos = ApplicationManager.timeTablePane.getSnappedMousePos(mouseRelease.getSceneX(), mouseRelease.getSceneY());
//        System.out.println(snappedPos);
        setLayoutX(Math.min(snappedPos.getX(), TimeTablePane.CELL_WIDTH * (ApplicationManager.timeTablePane.getColumnCount()-1) + TimeTablePane.getXOffset()));
        setLayoutY(Math.min(snappedPos.getY(), TimeTablePane.CELL_HEIGHT * 24 + TimeTablePane.getYOffset() - getViewSize().getY()));


        ApplicationManager.timeTablePane.unsetDraggingView();
//        columnsOfDay.handleRemoveTimeSlot(timeSlot);
        isDragging = false;

    }


    private void handleStretching(MouseEvent mouseDrag){
        ApplicationManager.timeTablePane.switchGuideLine(true);

        Point2D local = ApplicationManager.timeTablePane.getUnOffsetRelativePosFromScene(mouseDrag.getSceneX(), mouseDrag.getSceneY());
        double height = local.getY() - getViewYPosInColumn();
        setMinHeight(height);
        setMaxHeight(height);
        ApplicationManager.timeTablePane.moveAndSnapGuideLine(mouseDrag.getSceneX(), mouseDrag.getSceneY(), true);
    }

    private void handleStretchEnd(MouseEvent mouseRelease){
        double height = ApplicationManager.timeTablePane.getSnappedMousePos(mouseRelease.getSceneX(), mouseRelease.getSceneY(), true).getY()
                - TimeTablePane.getYOffset() - getViewYPosInColumn();

        height = Math.min(Math.max(height, TimeTablePane.getSnapping()), TimeTablePane.CELL_HEIGHT * 24 - getViewYPosInColumn() - 1);

        setMinHeight(height);
        setMaxHeight(height);

        try{
            long newDuration = (long)((height / TimeTablePane.CELL_HEIGHT)*3600);
            LocalTime newEndTime = timeSlot.getStartTime().plus(newDuration, ChronoUnit.SECONDS);

            DataController.instance.updateTime(getTask(), new TimeValues(timeSlot.getStartTime()), new TimeValues(newEndTime));
        }catch (TaskInfoException exc){
            exc.printStackTrace();
        }

        ApplicationManager.timeTablePane.switchGuideLine(false);

    }


    private void duplicateTask(){
        try {
            Task task = DataController.instance.createTask(getTask().getTitle(), new DateValues(getTask().getDate()));
            DataController.instance.updateDescription(task, getTask().getDescription());
            DataController.instance.updateTime(task, new TimeValues(getTimeSlot().getStartTime()),
                    new TimeValues(getTimeSlot().getEndTime()));
            DataController.instance.updateTaskCompletion(task, getTask().isCompleted());

        }catch (TaskInfoException exc){
            exc.printStackTrace();
        }
    }



    public Task getTask(){
        return timeSlot.getArrangedTask();
    }

    public TimeSlot getTimeSlot(){
        return timeSlot;
    }

}
