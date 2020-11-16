package main;

import Shape.Arrow;
import Shape.Queue;
import com.sun.istack.internal.Nullable;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.util.HashMap;

public class Panel extends BorderPane
{
    public Pane center, top, right, bottom, left;
    public ImageView leftDecoration, rightDecoration;
    public Label title, leftTitle, rightTitle;
    public Label descriptionText;
    public Label step;
    private VBox variableStack;
    public HashMap<String, Label> variables;
    public int size=0;

    public void pushVariable(String identifier, String value)
    {
        Label text = new Label(identifier + ": " + value);
        text.setStyle("-fx-font-family: 'Comic Sans MS';-fx-font-size: 20;");
        text.setWrapText(true);
        variables.put(identifier, text);
        variableStack.getChildren().add(text);
        size++;
    }

    public String popVariable()
    {
        Label label = (Label)(variableStack.getChildren().get(variableStack.getChildren().size()-1));
        variables.remove(label.getText().substring(0, label.getText().indexOf(':')));
        variableStack.getChildren().remove(label);
        size--;
        return label.getText();
    }

    public void setStep(int i)
    {
        step.setText("step: " + i);
    }

    public Panel()
    {
        super();
        constructBackbone();
        placeComponents();
    }

    private void constructBackbone()
    {
        center = new Pane();
        top = new Pane();
        right = new Pane();
        bottom = new Pane();
        left = new Pane();

        center.prefWidthProperty().bind(this.widthProperty().multiply(3).divide(5));
        center.prefHeightProperty().bind((this.heightProperty().subtract(bottom.heightProperty())).multiply(6).divide(7));
        center.setStyle("-fx-background-color: rgba(250,235,215,0.95);" +
                "-fx-background-radius: 100;" +
                "-fx-background-insets: 0 20 0 20");

        top.prefHeightProperty().bind((this.heightProperty().subtract(bottom.heightProperty())).multiply(1).divide(7));
//        top.setStyle("-fx-border-color: transparent transparent black transparent;" +
//                "-fx-background-image: url('images/background.png');" +
//                "-fx-background-repeat: repeat;");
        top.setStyle("-fx-background-color: linear-gradient(to bottom,rgba(192,252,179,0.5),#cfff9c);" +
                "-fx-background-radius: 1000 1000 1000 1000;" +
                "-fx-background-insets: 20 0 20 0");


        right.prefWidthProperty().bind(this.widthProperty().divide(5));
//        right.setStyle("-fx-border-color: transparent transparent transparent black;" +
//                "-fx-background-image: url('images/background.png');" +
//                "-fx-background-repeat: repeat;");

        right.setStyle("-fx-background-color: linear-gradient(to left,rgba(105,255,220,0.6),rgba(105,255,190,0.8));" +
                "-fx-background-radius: 1000 1000 1000 1000;" +
                "-fx-background-insets: 0 0 50 0");

        bottom.setPrefHeight(20);
        bottom.setStyle("-fx-background-color: antiquewhite;" +
                "-fx-background-radius: 0 0 1000 1000;" +
                "-fx-background-insets: 0 500 0 500");

        left.prefWidthProperty().bind(this.widthProperty().divide(5));
//        left.setStyle("-fx-border-color: transparent black transparent transparent;" +
//                "-fx-background-image: url('images/background.png');" +
//                "-fx-background-repeat: repeat");
        left.setStyle("-fx-background-color: linear-gradient(to right,rgba(248,248,74,0.8),rgba(255,255,139,0.8));" +
                "-fx-background-radius: 1000 1000 1000 1000;" +
                "-fx-background-insets: 100 0 100 0");

        this.setCenter(center);
        this.setTop(top);
        this.setRight(right);
        this.setBottom(bottom);
        this.setLeft(left);
        this.setStyle("-fx-background-color: transparent");
        this.setStyle("-fx-background-image: url('images/background.png');" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-position: center;" +
                "-fx-background-size: cover");
    }

    private void placeComponents()
    {

        title = new Label();
        top.getChildren().add(title);
        title.layoutXProperty().bind((top.widthProperty().subtract(title.widthProperty())).divide(2));
//        title.layoutXProperty().bind((top.widthProperty().multiply(0.1)));
        title.layoutYProperty().bind((top.heightProperty().subtract(title.heightProperty())).divide(2));
        title.setStyle("-fx-font-family: 'Segoe UI Black';-fx-font-size: 30;");

        leftTitle = new Label("Variables & Registers");
        leftTitle.setStyle("-fx-background-color: linear-gradient(to right,rgba(248,248,74,0.8),rgba(255,255,139,0.8));" +
                "-fx-background-radius: 1000 1000 1000 1000;" +
                "-fx-font-family: 'Kristen ITC';-fx-font-size: 25;");
        leftTitle.setAlignment(Pos.CENTER);
        leftTitle.layoutYProperty().bind(left.heightProperty().multiply(0.28));
        leftTitle.prefWidthProperty().bind(left.widthProperty());
        leftTitle.prefHeightProperty().bind(left.heightProperty().multiply(0.1));

        variables = new HashMap<>();
        variableStack = new VBox();
        variableStack.layoutYProperty().bind((left.heightProperty().subtract(variableStack.heightProperty())).divide(2));
        variableStack.prefWidthProperty().bind(left.widthProperty());
        variableStack.setAlignment(Pos.CENTER);

        leftDecoration = new ImageView("images/octopus.gif");
        leftDecoration.setFitWidth(300);
        leftDecoration.setFitHeight(220);
        leftDecoration.layoutXProperty().bind(left.widthProperty().subtract(leftDecoration.fitWidthProperty()).divide(2));
        leftDecoration.layoutYProperty().bind(left.heightProperty().subtract(leftDecoration.fitHeightProperty().multiply(0.9)));

        left.getChildren().addAll(leftTitle, variableStack, leftDecoration);


        rightTitle = new Label("Description");
        rightTitle.setStyle("-fx-background-color: linear-gradient(to left,rgba(105,255,220,0.6),rgba(105,255,190,0.8));" +
                "-fx-background-radius: 1000 1000 1000 1000;" +
                "-fx-font-family: 'Kristen ITC';-fx-font-size: 30;");
        rightTitle.setAlignment(Pos.CENTER);
        rightTitle.layoutYProperty().bind(left.heightProperty().multiply(0.16));
        rightTitle.prefWidthProperty().bind(right.widthProperty());
        rightTitle.prefHeightProperty().bind(left.heightProperty().multiply(0.1));

        descriptionText = new Label();
        descriptionText.layoutYProperty().bind((right.heightProperty().subtract(descriptionText.heightProperty())).divide(2));
        descriptionText.maxWidthProperty().bind(right.widthProperty());
        descriptionText.setWrapText(true);
        descriptionText.setFont(Font.font(24));

//        rightDecoration = new ImageView("images/pikachu.gif");
//        rightDecoration.setFitWidth(300);
//        rightDecoration.setFitHeight(200);
//        rightDecoration.layoutXProperty().bind(right.widthProperty().subtract(rightDecoration.fitWidthProperty().multiply(0.9)).divide(2));
//        rightDecoration.layoutYProperty().bind(right.heightProperty().subtract(rightDecoration.fitHeightProperty().multiply(0.9)));
//        right.getChildren().addAll(rightTitle, descriptionText, rightDecoration);
        right.getChildren().addAll(rightTitle, descriptionText);

        step = new Label("step: ");
        step.layoutXProperty().bind((bottom.widthProperty().subtract(step.widthProperty())).divide(2));
        bottom.getChildren().add(step);
    }

    public void setXY(Node node, DoubleExpression widthProperty, DoubleExpression heightProperty, double xRatio, double yRatio)
    {
        node.layoutXProperty().unbind();
        node.layoutYProperty().unbind();
        node.layoutXProperty().bind(center.widthProperty().multiply(xRatio).subtract(widthProperty.divide(2)));
        node.layoutYProperty().bind(center.heightProperty().multiply(yRatio).subtract(heightProperty.divide(2)));
    }

    private void addArrow(Component component, ObservableValue<Number> startXProperty, ObservableValue<Number> startYProperty,
                          ObservableValue<Number> endXProperty, ObservableValue<Number> endYProperty, @Nullable String name, @Nullable Double strokeWidth)
    {
        Pair<Label, Arrow> arrow = component.makeArrow(startXProperty, startYProperty, endXProperty, endYProperty, name, strokeWidth);
        arrow.getValue().addToContainer(center.getChildren());
        center.getChildren().add(arrow.getKey());
        arrow.getValue().setOpacity(0);
        arrow.getKey().setOpacity(0);
    }

    public void addObject(Component component)
    {
        // ----------------------Step 1----------------------
        center.getChildren().add(component.sourceCode);
        component.sourceCode.setOpacity(0);

        // ----------------------Step 2----------------------
        // add operating system object
        center.getChildren().add(component.operatingSystem);
        component.operatingSystem.setOpacity(0);
        setXY(component.operatingSystem, component.operatingSystem.fitWidthProperty(), component.operatingSystem.fitHeightProperty(), 0.1, 0.25);

        // add process object
        center.getChildren().addAll(component.process);
        component.process.setOpacity(0);
        setXY(component.process, component.process.widthProperty(), component.process.heightProperty(), 0.1, 0.75);


        // add fork arrow
        addArrow(component, component.operatingSystem.layoutXProperty().add(component.operatingSystem.fitWidthProperty().divide(2)),
                component.operatingSystem.layoutYProperty().add(component.operatingSystem.fitHeightProperty()),
                center.widthProperty().multiply(0.1),
                center.heightProperty().multiply(0.75).subtract(component.process.heightProperty().divide(2)),
                "fork", null);

        // add file object
        center.getChildren().add(component.file);
        component.file.setOpacity(0);
        setXY(component.file, ((ImageView)component.file.getChildren().get(0)).fitWidthProperty(),
                ((ImageView)component.file.getChildren().get(0)).fitHeightProperty(), 0.5, 0.15);

        // add execve arrow
        addArrow(component, component.operatingSystem.layoutXProperty().add(component.operatingSystem.fitWidthProperty()),
                component.operatingSystem.layoutYProperty().add(component.operatingSystem.fitHeightProperty().divide(2)),
                center.widthProperty().multiply(0.9).subtract(component.process.widthProperty().divide(2)),
                center.heightProperty().multiply(0.25), "execve", null);

        // ----------------------Step 3----------------------
        // movl $4, %eax
        //   int 0x80
        addArrow(component, center.widthProperty().divide(2),
                center.heightProperty().multiply(0.25),
                center.widthProperty().divide(2),
                center.heightProperty().multiply(0.5).subtract(component.systemCall.heightProperty().divide(2)),
                "movl $4, %eax\n" +
                        "int 0x80", null);
        // system_call
        center.getChildren().add(component.systemCall);
        component.systemCall.setOpacity(0);
        setXY(component.systemCall, component.systemCall.widthProperty(), component.systemCall.heightProperty(), 0.5, 0.5);
        // movl $0x17, %edx
        //   mov %dx, %fs
        addArrow(component, center.widthProperty().divide(2),
                center.heightProperty().multiply(0.5).add(component.systemCall.heightProperty().divide(2)),
                center.widthProperty().divide(2),
                component.terminal.layoutYProperty(),
                "movl $0x17, %edx\n" +
                        "mov %dx, %fs", null);
        // /dev/tty0
        center.getChildren().add(component.terminal);
        component.terminal.setOpacity(0);
        setXY(component.terminal, ((ImageView)component.terminal.getChildren().get(0)).fitWidthProperty(),
                ((ImageView)component.terminal.getChildren().get(0)).fitHeightProperty().add(
                        ((Label)component.terminal.getChildren().get(1)).heightProperty()), 0.5, 0.75);
        // write
        addArrow(component, center.widthProperty().divide(2),
                center.heightProperty().multiply(0.25).add(component.process.heightProperty().divide(2)),
                center.widthProperty().divide(2),
                component.terminal.layoutYProperty(),
                "write", 3.);
//        component.arrowDict.get("write").getKey().setTextFill(Paint.valueOf("white"));

        // ----------------------Step 4----------------------
        center.getChildren().add(component.pipeFile);
        component.pipeFile.setOpacity(0);
        setXY(component.pipeFile, component.pipeFile.widthProperty(), component.pipeFile.heightProperty(), 2./3., 1./8.);
        center.getChildren().add(component.charFile);
        component.charFile.setOpacity(0);
        setXY(component.charFile, component.charFile.widthProperty(), component.charFile.heightProperty(), 2./3., 3./8.);
        center.getChildren().add(component.blockFile);
        component.blockFile.setOpacity(0);
        setXY(component.blockFile, component.blockFile.widthProperty(), component.blockFile.heightProperty(), 2./3., 5./8.);
        center.getChildren().add(component.regularFile);
        component.regularFile.setOpacity(0);
        setXY(component.regularFile, component.regularFile.widthProperty(), component.regularFile.heightProperty(), 2./3., 7./8.);

        addArrow(component, component.terminal.layoutXProperty().add(((ImageView)component.terminal.getChildren().get(0)).fitWidthProperty().divide(2)),
                center.heightProperty().divide(2),
                component.charFile.layoutXProperty(),
                component.charFile.layoutYProperty().add(component.charFile.heightProperty().divide(2)), "is", null);

        // ----------------------Step 5----------------------
        addArrow(component, center.widthProperty().multiply(0.4).add(component.charFile.widthProperty().divide(2)),
                center.heightProperty().multiply(0.37),
                center.widthProperty().multiply(0.6),
                center.heightProperty().multiply(0.37),
                "select", 0.5);
        addArrow(component, center.widthProperty().multiply(0.6),
                center.heightProperty().multiply(0.25).add(component.process.heightProperty().divide(2)),
                center.widthProperty().multiply(0.6),
                center.heightProperty().multiply(0.49),
                "sys_write", 3.);
        addArrow(component, center.widthProperty().multiply(0.6),
                center.heightProperty().multiply(0.51),
                center.widthProperty().multiply(0.6),
                center.heightProperty().multiply(0.75),
                "rw_char", 3.);
        center.getChildren().add(component.rwParameter);
        component.rwParameter.setOpacity(0);
        setXY(component.rwParameter, new SimpleDoubleProperty(0), component.rwParameter.heightProperty(), 0., 0.63);
        center.getChildren().add(component.devParameter);
        component.devParameter.setOpacity(0);
        setXY(component.devParameter, new SimpleDoubleProperty(0), component.devParameter.heightProperty(), 0., 0.63);
        center.getChildren().add(component.bufParameter);
        component.bufParameter.setOpacity(0);
        setXY(component.bufParameter, new SimpleDoubleProperty(0), component.bufParameter.heightProperty(), 0., 0.63);
        center.getChildren().add(component.countParameter);
        component.countParameter.setOpacity(0);
        setXY(component.countParameter, new SimpleDoubleProperty(0), component.countParameter.heightProperty(), 0., 0.63);
        center.getChildren().add(component.posParameter);
        component.posParameter.setOpacity(0);
        setXY(component.posParameter, new SimpleDoubleProperty(0), component.posParameter.heightProperty(), 0., 0.63);

        // ----------------------Step 6----------------------
        center.getChildren().add(component.crw_table);
        component.crw_table.setOpacity(0);
        setXY(component.crw_table, component.crw_table.fitWidthProperty(), component.crw_table.fitHeightProperty(), 0.5, 0.25);


        addArrow(component, center.widthProperty().multiply(0.2).add(component.devParameter.widthProperty().divide(2)),
                center.heightProperty().multiply(0.5),
                center.widthProperty().multiply(0.7).subtract(component.devHigh.widthProperty().divide(2)),
                center.heightProperty().multiply(0.5),
                "value", 3.);

        center.getChildren().add(component.devHigh);
        component.devHigh.setOpacity(0);
        setXY(component.devHigh, component.devHigh.widthProperty(), component.devHigh.heightProperty(), 0.7, 0.5);
        center.getChildren().add(component.devLow);
        component.devLow.setOpacity(0);
        component.devLow.layoutXProperty().bind(component.devHigh.layoutXProperty().add(component.devHigh.widthProperty()));
        component.devLow.layoutYProperty().bind(component.devHigh.layoutYProperty());

        addArrow(component, center.widthProperty().multiply(0.7),
                center.heightProperty().multiply(0.5).subtract(component.devHigh.heightProperty().divide(2)),
                center.widthProperty().multiply(0.5).subtract(component.crw_table.fitWidthProperty().multiply(0.28)),
                center.heightProperty().multiply(0.25).add(component.crw_table.fitHeightProperty().multiply(0.09)),
                "according", 3.);

        // ----------------------Step 7----------------------
        center.getChildren().add(component.minorParameter);
        component.minorParameter.setOpacity(0);
        setXY(component.minorParameter, new SimpleDoubleProperty(0), component.bufParameter.heightProperty(), 0., 0.8);

        addArrow(component, center.widthProperty().multiply(0.8),
                center.heightProperty().multiply(0.7),
                center.widthProperty().multiply(0.8),
                center.heightProperty().multiply(0.9),
                "rw_ttyx", 3.);


        addArrow(component, component.devLow.layoutXProperty().add(component.devLow.widthProperty().divide(2)),
                component.devLow.layoutYProperty().add(component.devLow.heightProperty()),
                component.minorParameter.layoutXProperty().add(component.minorParameter.widthProperty().divide(2)),
                component.minorParameter.layoutYProperty(),
                "minor", 1.);

        // ----------------------Step 8----------------------

        addArrow(component, component.rwParameter.layoutXProperty().add(component.rwParameter.widthProperty()),
                component.rwParameter.layoutYProperty().add(component.rwParameter.heightProperty().divide(2)),
                component.rwValue.layoutXProperty(),
                component.rwValue.layoutYProperty().add(component.rwValue.heightProperty().divide(2)),
                "rw", 1.);
        center.getChildren().add(component.rwValue);
        component.rwValue.setOpacity(0);
        setXY(component.rwValue, component.rwValue.widthProperty(), component.rwValue.heightProperty(), 0.5, 0.25);

        addArrow(component, component.rwValue.layoutXProperty().add(component.rwValue.widthProperty()),
                component.rwValue.layoutYProperty().add(component.rwValue.heightProperty().divide(2)),
                center.widthProperty().multiply(0.75),
                center.heightProperty().multiply(0.25),
                "para", 1.);
        addArrow(component, center.widthProperty().multiply(0.75),
                center.heightProperty().multiply(0.5),
                center.widthProperty().multiply(0.75),
                center.heightProperty().multiply(0.9),
                "tty_write", 3.);

        // ----------------------Step 9----------------------
        center.getChildren().add(component.channel0);
        component.channel0.setOpacity(0);
        setXY(component.channel0, component.channel0.widthProperty(), component.channel0.heightProperty(), 0.4, 0.25);
        center.getChildren().add(component.channel1);
        component.channel1.setOpacity(0);
        setXY(component.channel1, component.channel1.widthProperty(), component.channel1.heightProperty(), 0.4, 0.5);
        center.getChildren().add(component.channel2);
        component.channel2.setOpacity(0);
        setXY(component.channel2, component.channel2.widthProperty(), component.channel2.heightProperty(), 0.4, 0.75);

        addArrow(component, component.minorParameter.layoutXProperty().add(component.minorParameter.widthProperty()),
                component.minorParameter.layoutYProperty().add(component.minorParameter.heightProperty().divide(2)),
                component.channel0.layoutXProperty(),
                component.channel0.layoutYProperty().add(component.channel0.heightProperty().divide(2)),
                "channel", 1.);

        addArrow(component, component.channel0.layoutXProperty().add(component.channel0.widthProperty()),
                component.channel0.layoutYProperty().add(component.channel0.heightProperty().divide(2)),
                component.tty_table.layoutXProperty(),
                component.tty_table.layoutYProperty().add(component.tty_table.fitHeightProperty().divide(2)),
                "tty_table", 1.);

        center.getChildren().add(component.tty_table);
        component.tty_table.setOpacity(0);
        setXY(component.tty_table, component.tty_table.fitWidthProperty(), component.tty_table.fitHeightProperty(), 0.8, 0.25);

        // ----------------------Step 10----------------------
        center.getChildren().add(component.ttyQueue);
        component.ttyQueue.setOpacity(0);
        setXY(component.ttyQueue, component.ttyQueue.getQueue().widthProperty(), component.ttyQueue.getQueue().heightProperty().add(100), 0.5, 0.9);

        center.getChildren().add(component.bufQueue);
        component.bufQueue.setOpacity(0);
        setXY(component.bufQueue, component.bufQueue.widthProperty(), component.bufQueue.heightProperty(), 0.5, 0.1);

        ReadOnlyDoubleProperty bufQueueSize = ((Label) component.bufQueue.getChildren().get(0)).widthProperty();

        addArrow(component, component.bufParameter.layoutXProperty().add(component.bufParameter.widthProperty()),
                component.bufParameter.layoutYProperty().add(component.bufParameter.heightProperty().divide(2)),
                component.bufQueue.layoutXProperty(),
                component.bufQueue.layoutYProperty().add(bufQueueSize.divide(2)),
                "buf", 1.);

        center.getChildren().addAll(component.currentValue);
        component.currentValue.setOpacity(0);
        setXY(component.currentValue, component.currentValue.widthProperty(), component.currentValue.heightProperty(), 0.5, 0.5);

        addArrow(component, component.bufQueue.layoutXProperty().add(bufQueueSize.multiply(0+0.5)),
                component.bufQueue.layoutYProperty().add(bufQueueSize),
                component.currentValue.layoutXProperty().add(component.currentValue.widthProperty().divide(2)),
                component.currentValue.layoutYProperty(),
                "get_fs_byte", 3.);

        addArrow(component, component.currentValue.layoutXProperty().add(component.currentValue.widthProperty().divide(2)),
                component.currentValue.layoutYProperty().add(component.currentValue.heightProperty()),
                component.ttyQueue.layoutXProperty().add((0+0.5)*Queue.SIZE),
                component.ttyQueue.layoutYProperty().add(50),
                "push", 1.);

        // ----------------------Step 11----------------------
        center.getChildren().add(component.states);
        component.states.setOpacity(0);
        setXY(component.states, component.states.widthProperty(), component.states.heightProperty(), 0.5, 0.1);

    }
}
