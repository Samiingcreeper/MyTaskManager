package View.Task;

import Data.DataController;
import Data.Task;
import View.ApplicationManager;
import View.Config;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TaskUpdaterPane extends StackPane {

    //<editor-fold desc = "Constants">
    private static final double CREATION_PANE_WIDTH = 300;
    private static final double CREATION_PANE_HEIGHT = 250;

    private static final double MASK_OPACITY = 0.5;
    private static final CornerRadii UPDATE_CORNER_RADII = new CornerRadii(8);
    private static final double UPDATE_FORM_PADDING = 24;
    private static final BackgroundFill BACKGROUND_FILL = new BackgroundFill(Color.rgb(251, 252, 252 ), CornerRadii.EMPTY, new Insets(0));
    //</editor-fold>

    private UpdaterBasePane basePane;

    private Rectangle backgroundMask;


    private class UpdaterBasePane extends TaskCreatorPane{

        private Task target;
        private Button btnEscape;

        public UpdaterBasePane(){
            super("Update Current Task", 20);
            registerSubmissionOperation( () ->{

                DataController.instance.updateTitle(target, packTitleInput());
                DataController.instance.updateDescription(target, packDescriptionInput());
                if(hasTimeInput()) {
                    DataController.instance.updateTime(target, packStartTimeInput(), packEndTimeInput());
                }
                DataController.instance.updateDate(target, packDateInput());

                clearAllInputs();
                getAlert().setAlert(false);
                escape();
            } );

            initializeEscapeButton();

            setRight(btnEscape);

            setBackground(new Background(BACKGROUND_FILL));
            setPadding(new Insets(UPDATE_FORM_PADDING));
        }

        private void initializeEscapeButton(){
            btnEscape = Config.getCrossButton();
            btnEscape.setOnAction( actionEvent ->{
                escape();
            });
            btnEscape.setAlignment(Pos.TOP_RIGHT);
        }


    }




    public TaskUpdaterPane(){

        basePane = new UpdaterBasePane();
        initializeAndRenderBackgroundMask();

        basePane.setMaxWidth(CREATION_PANE_WIDTH);
        basePane.setMaxHeight(CREATION_PANE_HEIGHT);

        getChildren().addAll(backgroundMask, basePane);
        escape();
    }

    //<editor-fold desc="Initialize Background Mask">

    private void initializeAndRenderBackgroundMask(){
        backgroundMask = new Rectangle();
        backgroundMask.setOpacity(MASK_OPACITY);
        backgroundMask.widthProperty().bind(ApplicationManager.getMainScene().widthProperty());
        backgroundMask.heightProperty().bind(ApplicationManager.getMainScene().heightProperty());

        backgroundMask.setOnMouseClicked( mouseClick ->{
            escape();
        });
    }


    //</editor-fold>


    //<editor-fold desc="Enter and Escape Methods">
    public void enter(Task target){
        basePane.target = target;
        basePane.getAlert().setAlert(false);
        basePane.fillInfoToForm(target);

        setVisible(true);
    }

    private void escape(){
        basePane.target = null;
        setVisible(false);
    }
    //</editor-fold desc>





}
