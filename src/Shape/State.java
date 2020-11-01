package Shape;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class State extends HBox
{
    public ArrayList<Label> states;
    public State()
    {
        super(20);

        states = new ArrayList<>();
        for(int i=0;i<5;i++)
        {
            Label state = new Label(""+i);
            state.setAlignment(Pos.CENTER);
            state.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                    "-fx-border-radius: 50;" +
                    "-fx-border-color: black;" +
                    "-fx-font-size: 25");
            states.add(state);
            this.getChildren().add(state);
        }
    }
}
