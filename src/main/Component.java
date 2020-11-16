package main;

import Shape.Arrow;
import Shape.Queue;
import Shape.State;
import com.sun.istack.internal.Nullable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.TreeMap;


public class Component
{
    private String str, cooked;
    private ArrayList<String> dict;
    public Label sourceCode;
    public ImageView operatingSystem;
    public Group file, terminal;
    public Label process, systemCall;
    public Label pipeFile, charFile, blockFile, regularFile;
    public Label rwParameter, devParameter, bufParameter, countParameter, posParameter;
    public Label devHigh, devLow;
    public ImageView crw_table;
    public Label minorParameter;
    public Label rwValue;
    public ImageView tty_table;
    public Label channel0, channel1, channel2;

    public HBox bufQueue;
    public Queue ttyQueue;
    public Label currentValue;

    public State states;

    public TreeMap<String, Pair<Label, Arrow>> arrowDict;



    public Component(String str, String cooked, ArrayList<String> dict)
    {
        this.str = str;
        this.cooked = cooked;
        this.dict = dict;

        arrowDict = new TreeMap<>();

        sourceCode = new Label(
                "#include<stdio.h>\n" +
                "int main()\n" +
                "{\n" +
                "   printf(\""+cooked+"\")\n" +
                "   return 0;\n" +
                "}\n");
        sourceCode.setStyle("-fx-font-size: 30; -fx-border-width: 2; -fx-border-color: black; -fx-padding: 50");

        operatingSystem = new ImageView("images/OS.png");
//        operatingSystem.setPreserveRatio(true);
        operatingSystem.setFitWidth(100);
        operatingSystem.setFitHeight(100);

        process = new Label("pid: 4");
        process.setAlignment(Pos.CENTER);
        process.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-background-radius: 30; -fx-background-color: black;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");

        ImageView fileImage = new ImageView("images/file.png");
//        file.setPreserveRatio(true);
        fileImage.setFitWidth(100);
        fileImage.setFitHeight(100);
        Label fileLabel = new Label("./example");
        fileLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        fileLabel.layoutXProperty().bind(fileImage.layoutXProperty().add((fileImage.fitWidthProperty().subtract(fileLabel.widthProperty())).divide(2)));
        fileLabel.layoutYProperty().bind(fileImage.fitHeightProperty());
        fileLabel.setAlignment(Pos.CENTER);
        file = new Group(fileImage, fileLabel);

        // ----------------------Step 3----------------------
        systemCall = new Label("system_call");
        systemCall.setAlignment(Pos.CENTER);
        systemCall.setStyle("-fx-pref-width: 200; -fx-pref-height:40;" +
                "-fx-background-radius: 30; -fx-background-color: black;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");

        ImageView terminalImage = new ImageView("images/terminal.png");
//        terminal.setPreserveRatio(true);
        terminalImage.setFitWidth(100);
        terminalImage.setFitHeight(100);
        Label terminalLabel = new Label("/dev/tty0");
        terminalLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        terminalLabel.layoutXProperty().bind(terminalImage.layoutXProperty().add((terminalImage.fitWidthProperty().subtract(terminalLabel.widthProperty())).divide(2)));
        terminalLabel.layoutYProperty().bind(terminalImage.fitHeightProperty());
        terminalLabel.setAlignment(Pos.CENTER);
        terminal = new Group(terminalImage, terminalLabel);

        // ----------------------Step 4----------------------
        pipeFile = new Label("Pipe");
        pipeFile.setAlignment(Pos.CENTER);
        pipeFile.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-background-radius: 10; -fx-background-color: black;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        charFile = new Label("Char");
        charFile.setAlignment(Pos.CENTER);
        charFile.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-background-radius: 10; -fx-background-color: black;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        blockFile = new Label("Block");
        blockFile.setAlignment(Pos.CENTER);
        blockFile.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-background-radius: 10; -fx-background-color: black;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        regularFile = new Label("Regular");
        regularFile.setAlignment(Pos.CENTER);
        regularFile.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-background-radius: 10; -fx-background-color: black;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");

        // ----------------------Step 5----------------------
        rwParameter = new Label("rw");
        rwParameter.setAlignment(Pos.CENTER);
        rwParameter.setStyle("-fx-pref-width: 100; -fx-pref-height:50;" +
                "-fx-background-radius: 10; -fx-background-color: lightgray;" +
                " -fx-text-fill: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        devParameter = new Label("dev");
        devParameter.setAlignment(Pos.CENTER);
        devParameter.setStyle("-fx-pref-width: 100; -fx-pref-height:50;" +
                "-fx-background-radius: 10; -fx-background-color: lightgray;" +
                " -fx-text-fill: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        bufParameter = new Label("buf");
        bufParameter.setAlignment(Pos.CENTER);
        bufParameter.setStyle("-fx-pref-width: 100; -fx-pref-height:50;" +
                "-fx-background-radius: 10; -fx-background-color: lightgray;" +
                " -fx-text-fill: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        countParameter = new Label("count");
        countParameter.setAlignment(Pos.CENTER);
        countParameter.setStyle("-fx-pref-width: 100; -fx-pref-height:50;" +
                "-fx-background-radius: 10; -fx-background-color: lightgray;" +
                " -fx-text-fill: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        posParameter = new Label("pos");
        posParameter.setAlignment(Pos.CENTER);
        posParameter.setStyle("-fx-pref-width: 100; -fx-pref-height:50;" +
                "-fx-background-radius: 10; -fx-background-color: lightgray;" +
                " -fx-text-fill: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");

        // ----------------------Step 6----------------------
        crw_table = new ImageView("images/crw_table.png");
        crw_table.setFitWidth(660);
        crw_table.setFitHeight(220);

        devHigh = new Label("0x04");
        devHigh.setAlignment(Pos.CENTER);
        devHigh.setStyle("-fx-pref-width: 100; -fx-pref-height: 100;" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: black;" +
                "-fx-text-fill: black");
        devLow = new Label("0x00");
        devLow.setAlignment(Pos.CENTER);
        devLow.setStyle("-fx-pref-width: 100; -fx-pref-height: 100;" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: black;" +
                "-fx-text-fill: black");

        // ----------------------Step 7----------------------
        minorParameter = new Label("minor");
        minorParameter.setAlignment(Pos.CENTER);
        minorParameter.setStyle("-fx-pref-width: 100; -fx-pref-height:50;" +
                "-fx-background-radius: 10; -fx-background-color: lightgray;" +
                " -fx-text-fill: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");

        // ----------------------Step 8----------------------
        rwValue = new Label("WRITE");
        rwValue.setAlignment(Pos.CENTER);
        rwValue.setStyle("-fx-pref-width: 100; -fx-pref-height: 100;" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: black;" +
                "-fx-text-fill: black");

        // ----------------------Step 9----------------------
        tty_table = new ImageView("images/tty_table.png");
        tty_table.setFitWidth(300);
        tty_table.setFitHeight(200);

        channel0 = new Label("0");
        channel0.setAlignment(Pos.CENTER);
        channel0.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;" );
        channel1 = new Label("1");
        channel1.setAlignment(Pos.CENTER);
        channel1.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
        channel2 = new Label("2");
        channel2.setAlignment(Pos.CENTER);
        channel2.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: black;" +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");

        // ----------------------Step 10----------------------
        bufQueue = new HBox();
        for(int i=0;i<str.length();i++)
        {
            Label temp = new Label(dict.get(i));
            temp.setStyle("-fx-pref-width: 50; -fx-pref-height:50;" +
                    "-fx-border-color: black;" +
                    "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20;");
            temp.setAlignment(Pos.CENTER);
            bufQueue.getChildren().add(temp);
        }

        ttyQueue = new Queue(str.length());

        currentValue = new Label("'"+ dict.get(0) +"'");
        currentValue.setAlignment(Pos.CENTER);
        currentValue.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: black");

        // ----------------------Step 11----------------------
        states = new State();
    }
    public Pair<Label, Arrow> makeArrow(ObservableValue<Number> startXProperty, ObservableValue<Number> startYProperty,
                                       ObservableValue<Number> endXProperty, ObservableValue<Number> endYProperty, @Nullable String name, @Nullable Double strokeWidth)
    {
        Arrow arrow = new Arrow();
        arrow.startXProperty().bind(startXProperty);
        arrow.startYProperty().bind(startYProperty);
        arrow.endXProperty().bind(endXProperty);
        arrow.endYProperty().bind(endYProperty);
        if (strokeWidth != null)
            arrow.setStrokeWidth(strokeWidth);
        if(name != null)
        {
            Label label = new Label(name);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-alignment: center; -fx-text-fill: black; -fx-font-size: 20;" +
                    "-fx-font-family: 'Comic Sans MS'");
            label.layoutXProperty().bind((arrow.startXProperty().add(arrow.endXProperty()).subtract(label.widthProperty())).divide(2));
            label.layoutYProperty().bind((arrow.startYProperty().add(arrow.endYProperty()).subtract(label.heightProperty())).divide(2));
            arrowDict.put(name, new Pair<>(label, arrow));
            return arrowDict.get(name);
        }
        return new Pair<>(null, arrow);
    }
}
