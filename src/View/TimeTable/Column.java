package View.TimeTable;

import Data.TimeSlot;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

class Column extends Pane {

    private ArrayList<TimeSlot> timeSlots;

    public Column(ArrayList<TimeSlot> timeSlots, ColumnsOfDay columnsOfDay){
        setMinWidth(TimeTablePane.CELL_WIDTH);
        this.timeSlots = timeSlots;
        drawAllTimeSlots(columnsOfDay);
    }

    private void drawAllTimeSlots(ColumnsOfDay columnsOfDay){
        for(TimeSlot timeSlot : timeSlots){
            TimeSlotView view = new TimeSlotView(timeSlot, columnsOfDay);
            getChildren().add(view);
        }
    }
}
