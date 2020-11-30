package shape;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Queue extends VBox
{
    public static final double SIZE = 50;
    private final IntegerProperty head, tail;
    Arrow headArrow, tailArrow;
    Pane upper, lower;
    HBox queue;
    public Queue(int length)
    {
        super();

        head = new SimpleIntegerProperty(0);
        tail = new SimpleIntegerProperty(0);

        upper = new Pane();
        lower = new Pane();
        upper.setPrefWidth(length*SIZE);
        lower.setPrefWidth(length*SIZE);

        headArrow = new Arrow();
        headArrow.startXProperty().bind((head.add(0.5)).multiply(SIZE));
        headArrow.endXProperty().bind(headArrow.startXProperty());
        headArrow.setStartY(0);
        headArrow.setEndY(50);
        Label headLabel = new Label("head");
        headLabel.setAlignment(Pos.CENTER);
        headLabel.setStyle("-fx-text-alignment: center; -fx-text-fill: black;");
        headLabel.layoutXProperty().bind((headArrow.startXProperty().add(headArrow.endXProperty()).subtract(headLabel.widthProperty())).divide(2));
        headLabel.layoutYProperty().bind((headArrow.startYProperty().add(headArrow.endYProperty()).subtract(headLabel.heightProperty())).divide(2));
        headArrow.addToContainer(upper.getChildren());
        upper.getChildren().add(headLabel);

        tailArrow = new Arrow();
        tailArrow.startXProperty().bind((tail.add(0.5)).multiply(SIZE));
        tailArrow.endXProperty().bind(tailArrow.startXProperty());
        tailArrow.setStartY(50);
        tailArrow.setEndY(0);
        Label tailLabel = new Label("tail");
        tailLabel.setAlignment(Pos.CENTER);
        tailLabel.setStyle("-fx-text-alignment: center; -fx-text-fill: black;");
        tailLabel.layoutXProperty().bind((tailArrow.startXProperty().add(tailArrow.endXProperty()).subtract(tailLabel.widthProperty())).divide(2));
        tailLabel.layoutYProperty().bind((tailArrow.startYProperty().add(tailArrow.endYProperty()).subtract(tailLabel.heightProperty())).divide(2));
        tailArrow.addToContainer(lower.getChildren());
        lower.getChildren().add(tailLabel);

        queue = new HBox();

        for(int i=0;i<length+1;i++)
        {
            Label temp = new Label("...");
            temp.setStyle("-fx-pref-width: 50; -fx-pref-height:50;" +
                    "-fx-border-color: black");
            temp.setAlignment(Pos.CENTER);
            queue.getChildren().add(temp);
        }

        this.getChildren().addAll(upper, queue, lower);
    }
    public void push(String c)
    {
        ((Label)queue.getChildren().get(head.get())).setText(c);
        head.set(head.get()+1);
    }

    public void pop()
    {
        ((Label)queue.getChildren().get(tail.get())).setText("...");
        tail.set(tail.get()+1);
    }

    public HBox getQueue()
    {
        return queue;
    }
}
