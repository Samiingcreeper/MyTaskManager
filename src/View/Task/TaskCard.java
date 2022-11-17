package View.Task;

import Data.Miscellaneous.EventListener;
import Data.Task;
import Data.DataController;
import View.ApplicationManager;
import View.Config;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalTime;

public class TaskCard extends BorderPane {

    //<editor-fold desc = "Constants">
    private static final BackgroundFill BACKGROUND_FILL = new BackgroundFill(Color.rgb(242, 242, 242 ), CornerRadii.EMPTY, new Insets(0));
    //</editor-fold>

    private Task task;

    private VBox infoRows;
    private Label lbTitle;
    private Label lbDescription;
    private Label lbDate;

    private VBox checkboxAndTime;
    private CheckBox cbCompleted;
    private Label lbStartTime;
    private Label lbEndTime;

    private Button btnRemove;


    private EventListener<Task> updateListener;


    public TaskCard(Task task){
        setTask(task);
        initializeInfoLabels(task);
        initializeCheckBoxAndTime();

        btnRemove = getRemoveButton();

        infoRows = createInfoRows();
        renderCard();
        updateCardContent();

        setOnMouseEntered( mouseEnter ->{
            setCursor(Cursor.HAND);
        });

        setOnMouseClicked( mouseClick ->{
            if(isClickOnEmptySpace(mouseClick)){
                ApplicationManager.getUpdatePane().enter(task);
            }
        });

    }



    //<editor-fold desc="Task Card Initialization">
    private void initializeInfoLabels(Task task) {
        lbTitle = getTextLabel(task.getTitle(), 16, true);
        lbDescription = getTextLabel(task.getDescription());
        lbDescription.setWrapText(true);
        lbDate = getTextLabel(task.getDate().toString(), Config.SMALL_FONT_SIZE);
    }

    private void initializeCheckBoxAndTime() {
        cbCompleted = getCompletedCheckBox();

        lbStartTime = getTextLabel("", 10);
        lbEndTime = getTextLabel("", 10);

        VBox timeLabels = new VBox();
        timeLabels.getChildren().addAll(lbStartTime/*, lbSlash */, lbEndTime);

        checkboxAndTime = new VBox(Config.DEFAULT_SPACING / 2d);
        checkboxAndTime.getChildren().addAll(cbCompleted, timeLabels);

        cbCompleted.setPadding(new Insets(2));

        timeLabels.setAlignment(Pos.CENTER);
//        lbSlash.setAlignment(Pos.CENTER);
        cbCompleted.setAlignment(Pos.CENTER);

        checkboxAndTime.setAlignment(Pos.TOP_CENTER);
        checkboxAndTime.setPadding(new Insets(Config.DEFAULT_SPACING / 2d, Config.DEFAULT_PADDING * 1.5, Config.DEFAULT_SPACING / 2d, Config.DEFAULT_SPACING / 2d));
    }

    private void renderCard(){
        setPadding(new Insets(Config.DEFAULT_PADDING));
        setBackground(new Background(BACKGROUND_FILL));

        setCenter(infoRows);
        setLeft(checkboxAndTime);
        setRight(btnRemove);
        BorderPane.setAlignment(checkboxAndTime, Pos.TOP_CENTER);

        btnRemove.setAlignment(Pos.TOP_RIGHT);

        setVisibility(task.isCompleted());
    }

    private VBox createInfoRows(){
        VBox vBox = new VBox();
        vBox.setSpacing(Config.DEFAULT_SPACING);

        vBox.getChildren().add(lbTitle);
        if(!lbDescription.getText().isBlank()) vBox.getChildren().add(lbDescription);
        vBox.getChildren().add(lbDate);
        return vBox;
    }

    private Button getRemoveButton(){
        Button btnRemove = Config.getCrossButton();
        btnRemove.setOnAction( actionEvent ->{
            DataController.instance.removeTask(task);
            task.unregisterUpdateListener(updateListener);
        });
        return btnRemove;
    }

    private CheckBox getCompletedCheckBox(){
        CheckBox cb = new CheckBox();
        cb.setSelected(task.isCompleted());
        cb.setOnAction( actionEvent ->{
            DataController.instance.updateTaskCompletion(task, cb.isSelected());
        });
        return cb;
    }
    //</editor-fold>


    private void updateCardContent(){
        lbTitle.setText(task.getTitle());
        lbDescription.setText(task.getDescription());
        lbDate.setText(task.getDate().toString());

        String startTimeString = task.getTimeSlot() != null ? getTimeRepresentation(task.getTimeSlot().getStartTime()) : "";
        String endTimeString = task.getTimeSlot() != null ? getTimeRepresentation(task.getTimeSlot().getEndTime()) : "";
        lbStartTime.setText(startTimeString);
        lbEndTime.setText(endTimeString);


        boolean isDescriptionBlank = lbDescription.getText().isBlank();
        boolean isDescriptionAlreadyShown = infoRows.getChildren().contains(lbDescription);
        if(isDescriptionBlank && isDescriptionAlreadyShown){
            infoRows.getChildren().remove(lbDescription);

        }else if(!isDescriptionBlank && !isDescriptionAlreadyShown){
            infoRows.getChildren().add(1, lbDescription);
        }

        setVisibility(task.isCompleted());
    }


    private void setVisibility(boolean isCompleted){
        setOpacity(isCompleted ? 0.5 : 1);
    }


    //<editor-fold desc="UI Elements Creator">
    private Label getTextLabel(String text){
        return getTextLabel(text, Config.SMALL_FONT_SIZE);
    }

    private Label getTextLabel(String text, double size){
        return getTextLabel(text, size, false);
    }

    private Label getTextLabel(String text, double size, boolean isBold){
        Label lb = new Label(text);
        if(isBold){
            lb.setFont(Font.font(Config.DEFAULT_FONT_NAME, FontWeight.SEMI_BOLD, size));
        }else {
            lb.setFont(Font.font(Config.DEFAULT_FONT_NAME, size));
        }
        lb.setTextFill(Config.DEFAULT_TEXT_FILL);
        return lb;
    }
    //</editor-fold>


    private boolean isClickOnEmptySpace(MouseEvent mouseClick){
        for (Node node : getClickMaskers()){
            if(isPositionWithinNode(mouseClick.getSceneX(), mouseClick.getSceneY(), node)){
                return false;
            }
        }
        return true;
    }

    private Node[] getClickMaskers(){
        return new Node[]{cbCompleted, btnRemove};
    }

    private void setTask(Task task){
        this.task = task;
        updateListener = updated ->{
            updateCardContent();};
        task.registerUpdateListener(updateListener);
    }

    //<editor-fold desc="Getters and Setters">


    public Task getTask(){
        return task;
    }
    //</editor-fold>


    //<editor-fold desc ="Miscellaneous Helper Methods">
    private boolean isPositionWithinNode(double sceneX, double sceneY, Node node){
        return isPositionWithinNode(new Point2D(sceneX, sceneY), node);
    }

    private boolean isPositionWithinNode(Point2D position, Node node){
        Point2D localPosition = node.sceneToLocal(position);
//        System.out.printf("node: %s local pos: %s\n", node, localPosition.toString());
        return node.contains(localPosition);
    }

    private String getTimeRepresentation(LocalTime time){
        if(time == null){
            return "";
        }
        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }
    //</editor-fold>

}
