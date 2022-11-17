package View.Task;

import Data.*;

import Data.Miscellaneous.DateValues;
import Data.Miscellaneous.TaskInfoException;
import Data.Miscellaneous.TimeValues;
import View.Config;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;

public class TaskCreatorPane extends BorderPane {

    //<editor-fold desc = "Constants">
    private static final BackgroundFill BACKGROUND_FILL = new BackgroundFill(Color.rgb(229, 231, 233 ), CornerRadii.EMPTY, new Insets(0));
    private static final double FORM_V_GAP = 6;
    private static final double FORM_H_GAP = 16;
    private static final double FORM_PADDING = 16;
    //</editor-fold>

    private Label lbHeader;

    private TextField tfTitle;
    private TextField tfDescription;
    private TextField tfMonth;
    private TextField tfDayOfMonth;

    private TextField tfStartHour;
    private TextField tfStartMinute;
    private TextField tfEndHour;
    private TextField tfEndMinute;

    private Alert alert;
    private Button btnSubmit;

    private SubmissionOperation submissionOperation;


    public TaskCreatorPane(){
        this("Create New Task", 20);
    }

    public TaskCreatorPane(String header, double headerSize){
        setPrefWidth(280);

        lbHeader = getTextLabel(header, headerSize);

        registerSubmissionOperation(() ->{
            Task task = DataController.instance.createTask(tfTitle.getText(), packDateInput());
            if(hasDescriptionInput()){
                DataController.instance.updateDescription(task, tfDescription.getText());
            }
            if(hasTimeInput()){
                DataController.instance.updateTime(task, packStartTimeInput(), packEndTimeInput());
            }
            clearAllInputs();
            alert.setAlert(false);
        });

        setBackground(new Background(BACKGROUND_FILL));
        setPadding(new Insets(FORM_PADDING));

        initializeNodes();

        GridPane form = createForm();
        setCenter(form);
        setBottom(alert);
    }



    //<editor-fold desc="Task Creator Pane Initialization">
    private void initializeNodes() {
        tfTitle = getTextField();
        tfDescription = getTextField();
        tfMonth = getTextField("MM", 3);
        tfDayOfMonth = getTextField("DD", 3);
        btnSubmit = getSubmitButton();

        tfStartHour = getTextField("HH", 3);
        tfStartMinute = getTextField("MM", 3);
        tfEndHour = getTextField("HH", 3);
        tfEndMinute = getTextField("MM", 3);

        alert = new Alert();
    }

    private Button getSubmitButton(){
        Button btn = Config.getDefaultButton("Submit");
        btn.setOnAction( actionEvent ->{
            handleSubmit();
        });
        btn.setAlignment(Pos.BOTTOM_RIGHT);
        return btn;
    }

    private GridPane createForm(){
        GridPane gridPane = createForm(new Node[][]{
//                {lbHeader},
                {getTextLabel("標題"), tfTitle},
                {getTextLabel("描述"), tfDescription},
                {getTextLabel("日期"), tfMonth, tfDayOfMonth},
                {getTextLabel("開始時間"), tfStartHour, tfStartMinute},
                {getTextLabel("結束時間"), tfEndHour, tfEndMinute},
                {btnSubmit}
        });
        GridPane.setHalignment(btnSubmit, HPos.RIGHT);
        gridPane.setHgap(FORM_H_GAP);
        gridPane.setVgap(FORM_V_GAP);

        return gridPane;
    }

    private GridPane createForm(Node[][] rows){
        GridPane gridPane = new GridPane();

        int maxNodesInRow = 0;
        for(Node[] currRow : rows){
            maxNodesInRow = currRow.length > maxNodesInRow ? currRow.length : maxNodesInRow;
        }

        for(int currRow = 0; currRow < rows.length; currRow++){
            for(int currColumn = 0; currColumn < rows[currRow].length; currColumn++){
                Node currNode = rows[currRow][currColumn];
                gridPane.add(currNode, currColumn, currRow);

                int nodesInCurrRow = rows[currRow].length;
                boolean isSpanNode = nodesInCurrRow <= 1 || (currColumn != 0 && nodesInCurrRow < maxNodesInRow);
                if(isSpanNode){
                    GridPane.setColumnSpan(currNode, maxNodesInRow - nodesInCurrRow + 1);
                }
            }
        }

        return gridPane;
    }
    //</editor-fold>


    //<editor-fold desc="Submission Handling">
    private void handleSubmit(){
        try{
            checkAll();
            submissionOperation.doWhenSuccess();

        }catch (TaskInfoException exc){
            alert.setAlert(exc.isAlertTitle(), exc.isAlertDate(), exc.isAlertTime());
            if(exc.isAlertDate()){
                clearDateInputs();
            }
            if(exc.isAlertTime()){
                clearTimeInputs();
            }
        }
    }

    private void checkAll() throws TaskInfoException{
        TaskInfoException exc = TaskInfoException.combine(
                TaskInfoException.checkDataForCreate(tfTitle.getText(), packDateInput()),
                hasTimeInput() ? TaskInfoException.checkTime(packStartTimeInput(), packEndTimeInput()) : null
        );
        if(exc != null) throw exc;
    }
    //</editor-fold>


    //<editor-fold desc="User Input Packer">
    protected String packTitleInput(){
        return tfTitle.getText();
    }

    protected String packDescriptionInput(){
        return tfDescription.getText();
    }

    protected DateValues packDateInput(){
        return new DateValues(Integer.toString(LocalDate.now().getYear()), tfMonth.getText().trim(), tfDayOfMonth.getText().trim());
    }

    protected TimeValues packStartTimeInput(){
        return new TimeValues(tfStartHour.getText().trim(), tfStartMinute.getText().trim());
    }

    protected TimeValues packEndTimeInput(){
        return new TimeValues(tfEndHour.getText().trim(), tfEndMinute.getText().trim());
    }
    //</editor-fold>


    //<editor-fold desc="Clearing Inputs">
    protected void clearTimeInputs() {
        tfStartHour.clear();
        tfStartMinute.clear();
        tfEndHour.clear();
        tfEndMinute.clear();
    }

    protected void clearDateInputs() {
        tfMonth.clear();
        tfDayOfMonth.clear();
    }

    protected void clearAllInputs() {
        tfTitle.clear();
        tfDescription.clear();
        clearDateInputs();
        clearTimeInputs();
    }
    //</editor-fold>


    //<editor-fold desc="Input Detection">
    protected boolean hasTimeInput() {
        return !tfStartHour.getText().isBlank() || !tfStartMinute.getText().isBlank() || !tfEndHour.getText().isBlank() || !tfEndMinute.getText().isBlank();
    }

    protected boolean hasDateInput(){
        return !tfMonth.getText().isBlank() && !tfDayOfMonth.getText().isBlank();
    }

    protected boolean hasDescriptionInput(){
        return !tfDescription.getText().isBlank();
    }

    protected boolean hasTitleInput(){
        return !tfTitle.getText().isBlank();
    }
    //</editor-fold>


    //<editor-fold desc="General UI Elements Creator">

    static Label getTextLabel(String text){
        return getTextLabel(text, Config.DEFAULT_FONT_SIZE);
    }

    static Label getTextLabel(String text, double size){
        Label lb = new Label(text);
        lb.setFont(Font.font(Config.DEFAULT_FONT_NAME, size));
        lb.setTextFill(Config.DEFAULT_TEXT_FILL);
        return lb;
    }

    private TextField getTextField(){
        return getTextField("");
    }

    private TextField getTextField(int prefColumnCount){
        return getTextField("", prefColumnCount);
    }

    private TextField getTextField(String promptText){
        return getTextField(promptText, -1);
    }

    private TextField getTextField(String promptText, int prefColumnCount){
        TextField tf = new TextField();
        tf.setPromptText(promptText);
        if(prefColumnCount > 0) tf.setPrefColumnCount(prefColumnCount);
        tf.setFont(Font.font(Config.DEFAULT_FONT_NAME, Config.DEFAULT_FONT_SIZE));
        tf.setStyle("-fx-background-color: transparent;");
        return tf;
    }
    //</editor-fold>

    protected void registerSubmissionOperation(SubmissionOperation submissionOperation){
        this.submissionOperation = submissionOperation;
    }

    protected void fillInfoToForm(Task target) {
        tfTitle.setText(target.getTitle());
        tfDescription.setText(target.getDescription());
        tfMonth.setText(Integer.toString(target.getDate().getMonth().getValue()));
        tfDayOfMonth.setText(Integer.toString(target.getDate().getDayOfMonth()));

        if(target.getTimeSlot() != null){
            tfStartHour.setText(Integer.toString(target.getTimeSlot().getStartTime().getHour()));
            tfStartMinute.setText(Integer.toString(target.getTimeSlot().getStartTime().getMinute()));
            tfEndHour.setText(Integer.toString(target.getTimeSlot().getEndTime().getHour()));
            tfEndMinute.setText(Integer.toString(target.getTimeSlot().getEndTime().getMinute()));
        }

    }

    protected Alert getAlert(){
        return alert;
    }
}

class Alert extends VBox{

    private static final double ALERT_FONT_SIZE = Config.DEFAULT_FONT_SIZE;
    private static final Color ALERT_FONT_COLOR = Color.rgb(231, 76, 60);

    private Label lbTitleAlert;
    private Label lbDateAlert;
    private Label lbTimeAlert;

    public Alert(){
        setSpacing(Config.DEFAULT_SPACING);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(Config.DEFAULT_PADDING));
        initializeAlertLabels();
    }


    private void initializeAlertLabels(){
        lbTitleAlert = getAlertLabel("標題不能為空");
        lbDateAlert = getAlertLabel("日期輸入錯誤");
        lbTimeAlert = getAlertLabel("開始/結束時間輸入錯誤");
        setAlert(false);
    }

    private Label getAlertLabel(String text){
        return getAlertLabel(text, ALERT_FONT_SIZE);
    }

    private Label getAlertLabel(String text, double size){
        Label lb = TaskCreatorPane.getTextLabel(text, size);
        lb.setTextFill(ALERT_FONT_COLOR);
        return lb;
    }


    //<editor-fold desc="Alert On/Off">
    void setAlert(boolean isAlert){
        setAlert(isAlert, isAlert, isAlert);
    }

    void setAlert(boolean isAlertTitle, boolean isAlertDate, boolean isAlertTime){
        getChildren().clear();
        if(isAlertTitle) getChildren().add(lbTitleAlert);
        if(isAlertDate) getChildren().add(lbDateAlert);
        if(isAlertTime) getChildren().add(lbTimeAlert);
    }
    //</editor-fold>
}

@FunctionalInterface
interface SubmissionOperation{

    void doWhenSuccess() throws TaskInfoException;
}