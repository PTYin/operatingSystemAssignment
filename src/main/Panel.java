package main;

import Shape.Arrow;
import com.sun.istack.internal.Nullable;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Pair;

public class Panel extends BorderPane
{
    public Pane center, top, right, bottom, left;
    public Label title, leftTitle, rightTitle;
    public Label descriptionText;
    public Label step;
    private VBox variableStack;
    public int size=0;

    public void pushVariable(String identifier, String value)
    {
        Label text = new Label(identifier + ": " + value);
        text.setFont(Font.font(12));
        variableStack.getChildren().add(text);
        size++;
    }

    public String popVariable()
    {
        Label label = (Label)(variableStack.getChildren().get(variableStack.getChildren().size()-1));
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
        center.setStyle("-fx-background-color: white");

        top.prefHeightProperty().bind((this.heightProperty().subtract(bottom.heightProperty())).multiply(1).divide(7));
        top.setStyle("-fx-border-color: transparent transparent black transparent;" +
                "-fx-background-image: url('images/background.png');" +
                "-fx-background-repeat: repeat");

        right.prefWidthProperty().bind(this.widthProperty().divide(5));
        right.setStyle("-fx-border-color: transparent transparent transparent black;" +
                "-fx-background-image: url('images/background.png');" +
                "-fx-background-repeat: repeat");

        bottom.setPrefHeight(20);
        bottom.setStyle("-fx-border-color: black transparent transparent transparent");

        left.prefWidthProperty().bind(this.widthProperty().divide(5));
        left.setStyle("-fx-border-color: transparent black transparent transparent;" +
                "-fx-background-image: url('images/background.png');" +
                "-fx-background-repeat: repeat");

        this.setCenter(center);
        this.setTop(top);
        this.setRight(right);
        this.setBottom(bottom);
        this.setLeft(left);
    }

    private void placeComponents()
    {

        title = new Label();
        top.getChildren().add(title);
        title.layoutXProperty().bind((top.widthProperty().subtract(title.widthProperty())).divide(2));
        title.layoutYProperty().bind((top.heightProperty().subtract(title.heightProperty())).divide(2));
        title.setFont(Font.font(18));

        leftTitle = new Label("Variables & Registers");
        leftTitle.setFont(Font.font(15));
        leftTitle.setAlignment(Pos.CENTER);
        leftTitle.prefWidthProperty().bind(left.widthProperty());
        leftTitle.setFont(Font.font(18));

        variableStack = new VBox();
        variableStack.layoutYProperty().bind(leftTitle.heightProperty());
        variableStack.prefWidthProperty().bind(left.widthProperty());
//        variableStack.setAlignment(Pos.CENTER);

        left.getChildren().addAll(leftTitle, variableStack);


        rightTitle = new Label("Description");
        rightTitle.setFont(Font.font(15));
        rightTitle.setAlignment(Pos.CENTER);
        rightTitle.prefWidthProperty().bind(right.widthProperty());
        rightTitle.setFont(Font.font(18));

        descriptionText = new Label();
        descriptionText.layoutYProperty().bind((right.heightProperty().subtract(rightTitle.heightProperty()).subtract(descriptionText.heightProperty())).divide(2));
        descriptionText.maxWidthProperty().bind(right.widthProperty());
        descriptionText.setWrapText(true);
        descriptionText.setFont(Font.font(18));
        right.getChildren().addAll(rightTitle, descriptionText);

        step = new Label("step: ");
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
                center.heightProperty().multiply(0.5),
                "movl $4, %eax\n" +
                        "int 0x80", null);
        // system_call
        center.getChildren().add(component.systemCall);
        component.systemCall.setOpacity(0);
        setXY(component.systemCall, component.systemCall.widthProperty(), component.systemCall.heightProperty(), 0.5, 0.5);
        // movl $0x17, %edx
        //   mov %dx, %fs
        addArrow(component, center.widthProperty().divide(2),
                center.heightProperty().multiply(0.5),
                center.widthProperty().divide(2),
                center.heightProperty().multiply(0.75),
                "movl $0x17, %edx\n" +
                        "mov %dx, %fs", null);
        // /dev/tty0
        center.getChildren().add(component.terminal);
        component.terminal.setOpacity(0);
        setXY(component.terminal, ((ImageView)component.terminal.getChildren().get(0)).fitWidthProperty(),
                ((ImageView)component.terminal.getChildren().get(0)).fitHeightProperty(), 0.5, 0.75);
        // write
        addArrow(component, center.widthProperty().divide(2),
                center.heightProperty().multiply(0.25).add(component.process.heightProperty().divide(2)),
                center.widthProperty().divide(2),
                component.terminal.layoutYProperty().subtract(5),
                "write", 50.);
        component.arrowDict.get("write").getKey().setTextFill(Paint.valueOf("white"));

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

        // ----------------------Step 6----------------------
        center.getChildren().add(component.minorParameter);
        component.minorParameter.setOpacity(0);
        setXY(component.minorParameter, new SimpleDoubleProperty(0), component.bufParameter.heightProperty(), 0., 0.8);
    }
}
