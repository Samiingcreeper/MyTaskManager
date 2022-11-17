package View;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Config {

    //<editor-fold desc="Font Related">
    public static final Color DEFAULT_TEXT_FILL = Color.rgb(66, 73, 73);
    public static final String DEFAULT_FONT_NAME = "Yu Gothic";
    public static final double DEFAULT_FONT_SIZE = 14;
    public static final double SMALL_FONT_SIZE = 12;
    public static final Font DEFAULT_FONT = Font.font(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE);
    public static final Font SMALL_FONT = Font.font(DEFAULT_FONT_NAME, SMALL_FONT_SIZE);
    //</editor-fold>

    //<editor-fold desc="General Spacing Related">
    public static final double DEFAULT_SPACING = 8;
    public static final double DEFAULT_PADDING = 12;
    //</editor-fold>

    private static final double STRIPE_HEIGHT = 40;
    private static final double BIG_STRIPE_HEIGHT = 60;

    public static final double DEFAULT_CORNER_RADIUS = 8;
    public static final CornerRadii DEFAULT_CORNER_RADII = new CornerRadii(DEFAULT_CORNER_RADIUS);

    public static final Background WHITE_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0)));


    //<editor-fold desc = "Button Configuration and Creators">
    public static final Color DEFAULT_BUTTON_FILL = Color.rgb(242, 243, 244);

    public static Button getDefaultButton(String text){
        Button btn = new Button(text);
        btn.setFont(DEFAULT_FONT);
        return btn;
    }

    public static Button getCrossButton(){
        Button btn = new Button("", new Cross(7));
        btn.setShape(new Circle());
        return btn;
    }
    //</editor-fold>

    public static Stripe getStripe(String desc, Color color){
        return getStripe(desc, color, 20);
    }

    public static Stripe getStripe(String desc, Color color, double fontSize){
        return new Stripe(desc, color, fontSize);
    }

    public static Stripe getBigStripe(String desc, Color color){
        return getStripe(desc, color, 30);
    }

    public static VBox alignedVBox(Node... nodes){
        VBox vBox = new VBox(nodes);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        return vBox;
    }

    public static class Stripe extends HBox{

        Label label;

        private Stripe(String desc, Color color, double fontSize){
            label = new Label(desc);
            color = Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.25);
            label.setFont(Font.font(DEFAULT_FONT_NAME, FontWeight.SEMI_BOLD, fontSize));
            label.setPadding(new Insets(5, 5, 5, 20));

            getChildren().add(label);
            setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            setMinHeight(STRIPE_HEIGHT);
        }
    }
}



class Cross extends Group {

    public Cross(){
        this(1);
    }

    public Cross(double size){
        this(size, "");
    }

    public Cross(double size, String style){
        Line line1 = new Line(0, 0, size, size);
        Line line2 = new Line(0, size, size, 0);
        line1.setStyle(style);
        line2.setStyle(style);
        getChildren().addAll(line1, line2);
    }
}