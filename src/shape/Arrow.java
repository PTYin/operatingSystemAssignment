package shape;

import javafx.collections.ObservableList;
import javafx.scene.Node;
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
        circle.opacityProperty().bindBidirectional(this.opacityProperty());
    }
    public void addToContainer(ObservableList<Node> childrenList)
    {
        childrenList.addAll(this, circle);
    }

}
