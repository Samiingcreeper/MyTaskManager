package View.TimeTable;

import Data.DataController;
import Data.Miscellaneous.DateValues;
import Data.Miscellaneous.TaskInfoException;
import Data.Miscellaneous.TimeValues;
import View.Config;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.awt.*;
import java.time.*;

public class TimeTablePane extends Pane {

    //<editor-fold desc="Constants">
    static final double CELL_HEIGHT = 140;
    static final double CELL_WIDTH = 200;

    static final double HORIZONTAL_LINE_EXTENSION = 10;
    static final double VERTICAL_LINE_EXTENSION = 30;

    static final double BACKGROUND_GRID_X_OFFSET = 30;
    static final double BACKGROUND_GRID_Y_OFFSET = 25;

    private static final String GRID_LINE_STYLE = "-fx-stroke: lightgrey; -fx-stroke-width: 1px;";
    private static final String MAIN_GRID_LINE_STYLE = "-fx-stroke: lightgrey; -fx-stroke-width: 2px;";

    private static final double TIME_LABEL_FONT_SIZE = 12;

    private static final double SNAP_TIME = 15;
    //</editor-fold>


    /*
        General
     */
    private LocalDate showcaseDate = LocalDate.now();


    /*
        Dragging
     */
    private TimeSlotView draggingView;
    private GuideLine guideLine = new GuideLine();
    {

    }


    /*
        Columns
     */

    private HBox timeTableOfDaysView;

    private LocalDate startDate;
    private Pane dateLabels;

    private ColumnsOfDay[] timeTableOfDays;
    {
        timeTableOfDays = new ColumnsOfDay[7];
        int currDayOfWeek = showcaseDate.getDayOfWeek().getValue();
        startDate = showcaseDate.minusDays(currDayOfWeek - 1);
        for(int i = 0; i < 7; i++){
            LocalDate date = startDate.plusDays(i);
            timeTableOfDays[i] = new ColumnsOfDay(this, date);
        }
    }

    /*
        Backgrounds
     */
    private Pane backgroundGrid = new Pane();
    private Pane timeLabels;


    public TimeTablePane(){
        initializePane();
        timeTableOfDaysView = createTimeTableOfDaysView();

        updateBackgroundGrid();
        timeLabels = createTimeLabels();

        dateLabels = new Pane();
        updateDateLabels();

        guideLine.setVisible(false);

        getChildren().addAll(guideLine, backgroundGrid, timeLabels, timeTableOfDaysView, dateLabels);

    }

    //<editor-fold desc="General Initialization">
    private void initializePane(){
        setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
    }
    //</editor-fold>


    //<editor-fold desc="Drawing Time Table Background">
    private Pane createTimeLabels(){
        Pane timeLabels = new Pane();
        Label timeLabel = null;
        for(int i = 0; i < 24; i++){
            String timeString = String.format("%02d:00", i);
            timeLabel = new Label(timeString);
            timeLabel.setFont(Font.font(Config.DEFAULT_FONT_NAME, TIME_LABEL_FONT_SIZE));

            timeLabel.setLayoutY(i * CELL_HEIGHT);
            timeLabels.getChildren().add(timeLabel);
        }

        timeLabels.setLayoutY(VERTICAL_LINE_EXTENSION + BACKGROUND_GRID_Y_OFFSET - TIME_LABEL_FONT_SIZE / 2d);
        return timeLabels;
    }

    void updateBackgroundGrid(){

        backgroundGrid.getChildren().clear();

        double verticalLineLength = VERTICAL_LINE_EXTENSION + CELL_HEIGHT * 24;
        double horizontalLineLength = HORIZONTAL_LINE_EXTENSION + CELL_WIDTH * getColumnCount();

        // draw vertical lines
        int i = 0;
        for(ColumnsOfDay currDayTimeTable : timeTableOfDays){

            // draw main line
            double xPos = HORIZONTAL_LINE_EXTENSION + CELL_WIDTH * i;
            drawVerticalLine(backgroundGrid, verticalLineLength, xPos, true);
            i++;

            for(int columnIndex = 0; columnIndex < currDayTimeTable.getColumnCount() - 1; columnIndex++){
                xPos = HORIZONTAL_LINE_EXTENSION + CELL_WIDTH * i;
                drawVerticalLine(backgroundGrid, verticalLineLength, xPos, false);
                i++;
            }
        }

        // draw horizontal lines
        for(int j = 0; j < 24; j++){
            double yPos = VERTICAL_LINE_EXTENSION + CELL_HEIGHT * j;
            Line currHorizontalLine = new Line(0, yPos, horizontalLineLength, yPos);
            currHorizontalLine.setStyle(GRID_LINE_STYLE);

            backgroundGrid.getChildren().add(currHorizontalLine);
        }


        // position the grid
        backgroundGrid.setLayoutX(BACKGROUND_GRID_X_OFFSET);
        backgroundGrid.setLayoutY(BACKGROUND_GRID_Y_OFFSET);

    }

    private void drawVerticalLine(Pane backgroundGrid, double verticalLineLength, double xPos, boolean isMainLine) {
        Line currVerticalLine = new Line(xPos, isMainLine ? 0 : VERTICAL_LINE_EXTENSION, xPos, verticalLineLength);
        currVerticalLine.setStyle(isMainLine ? MAIN_GRID_LINE_STYLE : GRID_LINE_STYLE);

        backgroundGrid.getChildren().add(currVerticalLine);
    }
    //</editor-fold>


    //<editor-fold desc="Columns Initialization">
    private HBox createTimeTableOfDaysView(){
        HBox hBox = new HBox();
        for(ColumnsOfDay columnsOfDay : timeTableOfDays){
            hBox.getChildren().add(columnsOfDay);
        }

        hBox.setLayoutX(BACKGROUND_GRID_X_OFFSET + HORIZONTAL_LINE_EXTENSION);
        hBox.setLayoutY(BACKGROUND_GRID_Y_OFFSET + VERTICAL_LINE_EXTENSION);

        return hBox;
    }
    //</editor-fold>


    //<editor-fold desc="Date Label">
    void updateDateLabels(){

        dateLabels.getChildren().clear();

        double currXPos = HORIZONTAL_LINE_EXTENSION + BACKGROUND_GRID_X_OFFSET + CELL_WIDTH / 2d - 20;
        LocalDate currDate = startDate;
        String[] dayOfWeekString = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for(int i = 0; i < timeTableOfDays.length; i++){

            Label dayLabel = new Label(dayOfWeekString[currDate.getDayOfWeek().getValue() - 1]);
//            dayLabel.setFont(Font.font(Config.DEFAULT_FONT_NAME, 17));
            dayLabel.setFont(Font.font(Config.DEFAULT_FONT_NAME, 15));
            Label dateLabel = new Label(String.format("%d/%d", currDate.getMonthValue(), currDate.getDayOfMonth()));
//            dateLabel.setFont(Font.font(Config.DEFAULT_FONT_NAME, 15));
            dateLabel.setFont(Font.font(Config.DEFAULT_FONT_NAME, 13));
            BorderPane labelContainer = new BorderPane();
            labelContainer.setTop(dayLabel);
            labelContainer.setCenter(dateLabel);

            if(currDate.isEqual(LocalDate.now())){
                dateLabel.setTextFill(Color.RED);
                dayLabel.setTextFill(Color.RED);
            }

            dateLabels.getChildren().add(labelContainer);
//            labelContainer.setLayoutY(BACKGROUND_GRID_Y_OFFSET - 15);
            labelContainer.setLayoutY(BACKGROUND_GRID_Y_OFFSET - 10);
            labelContainer.setLayoutX(currXPos);

            currXPos += Math.max(timeTableOfDays[i].getColumnCount(), 1) * CELL_WIDTH;
            currDate = currDate.plusDays(1);
        }

    }
    //</editor-fold>




    public int getColumnCount(){
        int count = 0;
        for(ColumnsOfDay timeTableOfDay : timeTableOfDays){
            count += timeTableOfDay.getColumnCount();
        }
        return count;
    }

    //<editor-fold desc="Miscellaneous Methods">
    static int getSecondsOfDate(LocalTime localDateTime){
        return localDateTime.getHour() * 3600 + localDateTime.getMinute() * 60 + localDateTime.getSecond();
    }

    static int getSecondPassed(LocalTime start, LocalTime end){
        return getSecondsOfDate(end) - getSecondsOfDate(start);
    }
    //</editor-fold>


    public void updateShowcaseDate(LocalDate showcaseDate){
        this.showcaseDate = showcaseDate;
        int dayOfWeek = showcaseDate.getDayOfWeek().getValue();
        startDate = showcaseDate.minusDays(dayOfWeek - 1);

        LocalDate currDate = startDate;
        for(int i = 0; i < timeTableOfDays.length; i++){
            timeTableOfDays[i].updateShowcaseDate(currDate.plusDays(i));
        }
        updateDateLabels();
    }


    public void setDraggingView(TimeSlotView draggingView){
        this.draggingView = draggingView;
        draggingView.setLayoutX(-9999);
        switchGuideLine(true);
        getChildren().add(draggingView);


    }

    public void unsetDraggingView(){
        getChildren().remove(draggingView);

        try{
            Point2D layout = new Point2D(draggingView.getLayoutX(), draggingView.getLayoutY());
            System.out.println("layout: " + layout);
            TimeValues startTimeValues = viewLayoutToStartTime(layout);
            LocalTime endTime = startTimeValues.getLocalTime().plus(draggingView.getTimeSlot().getDuration());
            if(endTime.equals(LocalTime.of(0, 0, 0))){
                endTime = LocalTime.of(23, 59, 59);
            }

            int index = viewLayoutToColumnIndex(layout);
            System.out.println("index:" + index);
            LocalDate date = timeTableOfDays[index].getShowcaseDate();
            DataController.instance.updateDate(draggingView.getTask(), new DateValues(date));


            TimeValues endTimeValues = new TimeValues(endTime.getHour(), endTime.getMinute());

            DataController.instance.updateTime(draggingView.getTask(), startTimeValues, endTimeValues);
        }
        catch (TaskInfoException exc){
            exc.printStackTrace();
        }

        this.draggingView = null;
        switchGuideLine(false);
    }

    public Point2D getSnappedMousePos(double x, double y){
        return getSnappedMousePos(x, y ,false);
    }

    public Point2D getSnappedMousePos(double x, double y, boolean isStretchMode){
        return getSnappedMousePos(new Point2D(x, y), isStretchMode);
    }

    public Point2D getSnappedMousePos(Point2D sceneMousePos, boolean isStretchMode){
        Point2D localMousePos = sceneToLocal(sceneMousePos);
        Point2D offset = new Point2D(HORIZONTAL_LINE_EXTENSION + BACKGROUND_GRID_X_OFFSET, VERTICAL_LINE_EXTENSION + BACKGROUND_GRID_Y_OFFSET);

        double snap = (SNAP_TIME / 60d) * CELL_HEIGHT;
        Point2D posWithoutOffset = localMousePos.subtract(offset);
        double snappedX = Math.max(0, CELL_WIDTH * (int)(posWithoutOffset.getX() / CELL_WIDTH));
        int snapAmount = (int)(posWithoutOffset.getY() / snap);
        if(isStretchMode){
            snapAmount += 1;
        }
        double snappedY = Math.max(0, snap * snapAmount);

        return new Point2D(snappedX, snappedY).add(offset);
    }

    public Point2D getUnOffsetRelativePosFromScene(double x, double y){
        return getRelativePosFromScene(x, y).subtract(new Point2D(getXOffset(), getYOffset()));
    }

    public Point2D getRelativePosFromScene(double x, double y){
        return sceneToLocal(x, y);
    }

    public static double getSnapping(){
        return (SNAP_TIME / 60d) * CELL_HEIGHT;
    }

    public static double getXOffset(){
        return TimeTablePane.BACKGROUND_GRID_X_OFFSET + TimeTablePane.HORIZONTAL_LINE_EXTENSION;
    }

    public static double getYOffset(){
        return TimeTablePane.BACKGROUND_GRID_Y_OFFSET + TimeTablePane.VERTICAL_LINE_EXTENSION;
    }

    private int viewLayoutToColumnIndex(Point2D layout){
        int column = (int)((layout.getX() - getXOffset()) / CELL_WIDTH) + 1;
        for(int i = 0; i < timeTableOfDays.length; i++){
            column -= timeTableOfDays[i].getColumnCount();
            if(column <= 0){
                return i;
            }
        }
        return -1;
    }

    private TimeValues viewLayoutToStartTime(Point2D layout){
        double startTime = ((layout.getY() - getYOffset()) / CELL_HEIGHT);
        int hour = (int)startTime;
        int minutes = (int)((startTime - hour) * 60);
        return new TimeValues(Integer.toString(hour), Integer.toString(minutes));
    }




    class GuideLine extends Line{

        public GuideLine(){
            setStartX(0);
            setStartY(0);
            setEndX(CELL_WIDTH);
            setEndY(0);
            setStroke(Color.SALMON);
            setStrokeWidth(1);
        }
    }

    void moveAndSnapGuideLine(double x, double y){
        moveAndSnapGuideLine(x, y , false);
    }

    void moveAndSnapGuideLine(double x, double y, boolean isStretchMode){
        Point2D snappedPos = getSnappedMousePos(x, y, isStretchMode);
        System.out.println(snappedPos);
        guideLine.setLayoutX(snappedPos.getX());
        guideLine.setLayoutY(snappedPos.getY());
    }

    void switchGuideLine(boolean isOn){
        guideLine.setVisible(isOn);
    }



}


