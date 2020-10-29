package main;

import javafx.scene.control.TextInputDialog;

public class Prompt
{
    public static String str2HexStr(String str)
    {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (byte b : bs)
        {
            sb.append("0x");
            bit = (b & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = b & 0x0f;
            sb.append(chars[bit]);
             sb.append(" ");
        }
        return sb.toString().trim();
    }

    public static String prompt()
    {
        TextInputDialog dialog = new TextInputDialog("Hello, World");
        dialog.setTitle("Prompt");
        dialog.setHeaderText("动画将依据输入字符串而变化");
        dialog.setContentText("输入字符串：");
        dialog.showAndWait();
        return dialog.getResult();
    }

//    private int step = 1;
//    private Pane pane;
//    private ImageView user;
//    private Label stringLabel;
//    private VBox vBox;
//    private HBox stack;
//
//    private void constructStack(String string)
//    {
//        vBox = new VBox();
//
//        stack = new HBox(10);
//        String[] strings = string.split(" ");
//        for(String item: strings)
//        {
//            Label label = new Label(item);
//            label.setFont(Font.font(50));
//            stack.getChildren().add(label);
//        }
//        stack.setMinWidth(0.0);
//        stack.setAlignment(Pos.CENTER);
//        stack.setStyle("-fx-border-color: gray;");
////        stack.layoutXProperty().bind((pane.widthProperty().subtract(stringLabel.widthProperty())).divide(2));
////        stack.layoutYProperty().bind((pane.heightProperty().subtract(stringLabel.heightProperty())).divide(2));
//        stack.setOpacity(0.0);
//        Label label = new Label("function putchar()");
//        label.setFont(Font.font(50));
//        label.setTextFill(Paint.valueOf("indianred"));
//        label.setOpacity(0.0);
//        vBox.getChildren().addAll(label, stack);
//        vBox.layoutXProperty().bind((pane.widthProperty().subtract(vBox.widthProperty())).divide(2));
//        vBox.layoutYProperty().bind((pane.heightProperty().subtract(vBox.heightProperty())).divide(2));
//        vBox.setAlignment(Pos.CENTER);
//        pane.getChildren().add(vBox);
//    }
//
//    public void constructPane(String string)
//    {
//        pane = new Pane();
//        pane.setPrefSize(1920, 1080);
//        pane.setStyle("-fx-background-color: #f5f5dc;" +
//                "-fx-background-image: url('images/background.png');" +
//                "-fx-background-repeat: repeat");
//        user = new ImageView("images/user.png");
//        user.setPreserveRatio(true);
//        user.setFitHeight(600);
//        user.setY(pane.getPrefHeight() - user.getFitHeight());
////        Text stringText = new Text(pane.getPrefWidth()*2/5, pane.getPrefHeight()*2/5, "hello, world");
////        stringText.setFont(Font.font(60));
//        stringLabel = new Label(string);
//        stringLabel.setWrapText(true);
//        stringLabel.setFont(Font.font(100));
//        stringLabel.setCenterShape(true);
//        stringLabel.layoutXProperty().bind((pane.widthProperty().subtract(stringLabel.widthProperty())).divide(2));
//        stringLabel.layoutYProperty().bind((pane.heightProperty().subtract(stringLabel.heightProperty())).divide(2));
//        pane.getChildren().addAll(user, stringLabel);
//        constructStack(str2HexStr(string));
//
////        pane.setOnKeyPressed(this::handler);
//        pane.setOnMouseClicked(this::handler);
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception
//    {
//
//        String string = prompt();
////        String string = "hello, world";
//        constructPane(string);
//
//        Scene scene = new Scene(pane);
//        Stage stage = new Stage();
//        stage.setScene(scene);
////        stage.setFullScreen(true);
//        stage.setResizable(false);
//        stage.setTitle("The Process of Outputting String");
//        stage.show();
//        System.out.println(stack);
//
//    }
//
//
//    private void handler(MouseEvent event)
//    {
//        switch (step)
//        {
//            case 1:
//                FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), stringLabel);
//                fadeTransition.setFromValue(1.0);
//                fadeTransition.setToValue(0.0);
//                fadeTransition.play();
//                fadeTransition.setOnFinished(event1 ->
//                {
////                    constructStack(str2HexStr(stringLabel.getText()));
//                    FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(2000), stack);
//                    fadeTransition1.setFromValue(0.0);
//                    fadeTransition1.setToValue(1.0);
//                    fadeTransition1.play();
//                });
//                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), user);
//                translateTransition.setFromX(user.getX());
//                translateTransition.setToX(-1000);
//                translateTransition.play();
//
//                step++;
//                break;
//            case 2:
//                vBox.getChildren().get(0).setOpacity(1.0);
//                Timeline timeline = new Timeline();
//                ((Label)stack.getChildren().get(0)).setBackground(
//                        new Background(new BackgroundFill(Paint.valueOf("indianred"), null, null)));
//                for(int i=1;i<stack.getChildren().size();i++)
//                {
//                    int finalI = i;
//                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000*(i+1)),
//                            event2 ->
//                            {
//                                stack.getChildren().get(finalI -1).setOpacity(0.0);
//                            },
//                            new KeyValue(((Label)stack.getChildren().get(i-1)).backgroundProperty(), new Background(new BackgroundFill(Paint.valueOf("transparent"), null, null)))));
//
//                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000*(i+1)),
//                            new KeyValue(((Label)stack.getChildren().get(i)).backgroundProperty(), new Background(new BackgroundFill(Paint.valueOf("indianred"), null, null)))));
//
//                }
//                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000*(stack.getChildren().size()+1)),
//                        event2 ->
//                        {
//                            stack.getChildren().get(stack.getChildren().size()-1).setOpacity(0.0);
//                        },
//                        new KeyValue(((Label)stack.getChildren().get(stack.getChildren().size()-1)).backgroundProperty(), new Background(new BackgroundFill(Paint.valueOf("transparent"), null, null)))));
//
//                timeline.play();
//                step++;
//                break;
//        }
//    }

}
