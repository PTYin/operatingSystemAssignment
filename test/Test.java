import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import main.Prompt;

public class Test extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        String images_root = "images/";
        Pane pane = new Pane();
        pane.setPrefSize(1920, 1080);
        pane.setStyle("-fx-background-color: #f5f5dc;" +
                "-fx-background-image: url('images/background.png');" +
                "-fx-background-repeat: repeat");
        ImageView user = new ImageView(images_root +"user.png");
        user.setPreserveRatio(true);
        user.setFitHeight(600);
        Label stringText = new Label("hello, world");
        pane.getChildren().addAll(user, stringText);
        System.out.println(pane.getPrefHeight()-user.getFitHeight());
        user.setY(pane.getPrefHeight()-user.getFitHeight());



        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.setTitle("The Process of Outputting String");
//        stage.show();
        System.out.println(Prompt.str2HexStr("For example,\nThis is a number 3692."));
    }
}
