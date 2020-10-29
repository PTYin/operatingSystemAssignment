package Shape;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Arrow extends Line
{
    public Circle circle;
    public Arrow()
    {
        super();
        circle = new Circle();
        circle.radiusProperty().bind(this.strokeWidthProperty().multiply(2.));
        circle.centerXProperty().bind(endXProperty());
        circle.centerYProperty().bind(endYProperty());
        circle.opacityProperty().bind(this.opacityProperty());
    }
    public void addToContainer(ObservableList<Node> childrenList)
    {
        childrenList.addAll(this, circle);
    }

}
