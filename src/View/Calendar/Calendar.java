package View.Calendar;

import View.ApplicationManager;
import View.Config;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.LinkedList;

public class Calendar extends BorderPane {

    private CalendarGrid grid;
    private BorderPane bar;

    private Label lbMonth;
    private Label lbNext, lbPrev;


    public Calendar(){
        this(LocalDate.now());
    }

    public Calendar(LocalDate showcaseDate){
        setMaxWidth(280);
        setBackground(new Background(new BackgroundFill(Color.rgb(247, 247, 247), CornerRadii.EMPTY, Insets.EMPTY)));

        lbMonth = new Label();
        lbMonth.setFont(Config.DEFAULT_FONT);
        updateMonthLabel(showcaseDate);

        lbNext = new Label(">");
        lbNext.setFont(Config.DEFAULT_FONT);
        lbNext.setOnMouseClicked(mouseClick ->{
            if(mouseClick.getButton() == MouseButton.PRIMARY){
                LocalDate date = grid.nextMonth();
                updateMonthLabel(date);
            }
        });
        lbNext.setOnMouseEntered( mouseEnter ->{
            setCursor(Cursor.HAND);
        });
        lbPrev = new Label("<");
        lbPrev.setFont(Config.DEFAULT_FONT);
        lbPrev.setOnMouseClicked(mouseClick ->{
            if(mouseClick.getButton() == MouseButton.PRIMARY){
                LocalDate date = grid.previousMonth();
                updateMonthLabel(date);
            }
        });
        lbPrev.setOnMouseEntered( mouseEnter ->{
            setCursor(Cursor.HAND);
        });

        bar = new BorderPane();
        bar.setLeft(lbPrev);
        bar.setCenter(lbMonth);
        bar.setRight(lbNext);
        bar.setBackground(new Background(new BackgroundFill(Color.rgb(230, 230, 230), CornerRadii.EMPTY, Insets.EMPTY)));
        bar.setPadding(new Insets(5, 0, 5, 0));


        grid = new CalendarGrid(showcaseDate);
        setCenter(grid);
        setTop(bar);
    }

    private void updateMonthLabel(LocalDate date){
        lbMonth.setText(String.format("%d %s", date.getYear(), date.getMonth().toString()));
    }

}

class CalendarGrid extends GridPane{

    private static final double CELL_SIZE = 40;

    private Year currYear;
    private Month currMonth;

    private LocalDate selectedDate;

    private LinkedList<CellLabel> cellLabels = new LinkedList<>();

    CalendarGrid(LocalDate showcaseDate){
        currYear = Year.of(showcaseDate.getYear());
        currMonth = showcaseDate.getMonth();
        selectedDate = showcaseDate;

        renderGrid();
        setPadding(new Insets(5));
    }

    private void renderGrid(){
        getChildren().clear();
        cellLabels.clear();

        int offset = LocalDate.of(currYear.getValue(), currMonth.getValue(), 1).getDayOfWeek().getValue() - 1;
        int dayCount = currMonth.length(currYear.isLeap());

        String[] abbreviation = {"M", "T", "W", "T", "F", "S", "S"};

        for(int i = 0; i < abbreviation.length; i++){
            Label lb = getDayOfWeekLabel(abbreviation[i]);
            add(lb, i, 0);
            lb.setAlignment(Pos.CENTER);
        }

        for(int i = 0; i < dayCount; i++){
            int column = (i + offset) % 7;
            int row = (i + offset) / 7;
            CellLabel lbDay = getDayLabel(i + 1, currMonth.getValue(), currYear.getValue());
            add(lbDay, column, row + 1);
            cellLabels.add(lbDay);
        }
    }

    private Label getDayOfWeekLabel(String abbreviation){
        Label label = new Label(abbreviation);
        label.setTextFill(Color.RED);

        label.setFont(Config.SMALL_FONT);
        label.setPrefSize(CELL_SIZE, CELL_SIZE);
        label.setMinHeight(CELL_SIZE);

        return label;
    }

    private CellLabel getDayLabel(int day, int month, int year){
        return new CellLabel(day, month, year);
    }

    private class CellLabel extends Label{

        private int day;

        public CellLabel(int day, int month, int year){
            this.day = day;
            setText(Integer.toString(day));

            if(LocalDate.now().equals(LocalDate.of(year, month, day))){
                setTextFill(Color.RED);
            }

            setFont(Config.SMALL_FONT);
            setPrefSize(CELL_SIZE, CELL_SIZE);
            setMinHeight(CELL_SIZE);
            setAlignment(Pos.CENTER);

            if(selectedDate.equals(LocalDate.of(year, month, day))){
                renderSelected();
            }

            setOnMouseEntered( mouseEnter ->{
                setCursor(Cursor.HAND);
            });

            setOnMouseClicked( mouseClick ->{
                if(mouseClick.getButton() == MouseButton.PRIMARY){
                    LocalDate date = LocalDate.of(currYear.getValue(), currMonth.getValue(), day);
                    ApplicationManager.timeTablePane.updateShowcaseDate(date);
                    ApplicationManager.taskTablePlane.updateShowcaseDate(date);
                    select();
                }
            });
        }

        void select(){
            for(CellLabel cellLabel : cellLabels){
                cellLabel.unselect();
            }

            selectedDate = LocalDate.of(currYear.getValue(), currMonth.getValue(), day);
            renderSelected();
        }

        private void renderSelected() {
            setUnderline(true);
            setFont(Font.font(Config.DEFAULT_FONT_NAME, FontWeight.BOLD, 14));
        }

        private void unselect(){
            setUnderline(false);
            setFont(Config.SMALL_FONT);
        }
    }

    public LocalDate nextMonth(){
        LocalDate nextMonth = LocalDate.of(currYear.getValue(), currMonth.getValue(), 1).plusMonths(1);
        currYear = Year.of(nextMonth.getYear());
        currMonth = nextMonth.getMonth();

        renderGrid();
        return nextMonth;
    }

    public LocalDate previousMonth(){
        LocalDate previousMonth = LocalDate.of(currYear.getValue(), currMonth.getValue(), 1).minusMonths(1);
        currYear = Year.of(previousMonth.getYear());
        currMonth = previousMonth.getMonth();

        renderGrid();
        return previousMonth;
    }


}
