package main;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class Main extends Application
{
    private Panel panel;
    private Timeline mainThread;
    private Component component;
    private String str;

    private double time = 0;  // millis
    private double epsilon = 1;
    @Override
    public void start(Stage primaryStage)
    {
//        String str = Prompt.prompt();
        str = "hello, world";
        component = new Component(str);
        panel = new Panel();

//        VirtualConsole console = new VirtualConsole();
//        console.prefWidthProperty().bind(panel.center.widthProperty());
//        console.prefHeightProperty().bind(panel.center.heightProperty().divide(2));
//        panel.center.getChildren().add(console);
//        // test
//        String str = "hello, \033[7mwor\033[2Kld";
//        console.process(str);
        panel.addObject(component);
        constructTimeline();
        mainThread.play();
        Scene scene = new Scene(panel, 1600, 900);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMinWidth(1300);
        stage.setTitle("The Process of Outputting String");
        stage.show();
    }

    private void fade(String flag, double duration, Node... nodes)
    {
        if (flag.equals("in"))
        {
            for (Node node: nodes)
            {
                mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), new KeyValue(node.opacityProperty(), 0)));
                mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time+duration), new KeyValue(node.opacityProperty(), 1)));
            }
            time += duration;

        }
        else if (flag.equals("out"))
        {
            for (Node node: nodes)
            {
                mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), new KeyValue(node.opacityProperty(), 1)));
                mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time+duration), new KeyValue(node.opacityProperty(), 0)));
            }
            time += duration;
        }
    }
    private void translate(double duration, DoubleExpression toXProperty, DoubleExpression toYProperty, Node... nodes)
    {
        double checkpoint = time;
        for(Node node: nodes)
        {

            // add key frame in real time
            mainThread.getKeyFrames().addAll(new KeyFrame(Duration.millis(checkpoint-epsilon), e ->
            {
                node.layoutXProperty().unbind();
                node.layoutYProperty().unbind();
                KeyFrame start = new KeyFrame(Duration.millis(checkpoint),
                        new KeyValue(node.layoutXProperty(), node.layoutXProperty().getValue()),
                        new KeyValue(node.layoutYProperty(), node.layoutYProperty().getValue()));
                KeyFrame end = new KeyFrame(Duration.millis(checkpoint+duration),
                        new KeyValue(node.layoutXProperty(), toXProperty.getValue()),
                        new KeyValue(node.layoutYProperty(), toYProperty.getValue()));
                // bind property right after frame finished
                KeyFrame binding = new KeyFrame(Duration.millis(checkpoint+duration), event ->
                {
                    mainThread.stop();
                    node.layoutXProperty().bind(toXProperty);
                    node.layoutYProperty().bind(toYProperty);
                    mainThread.getKeyFrames().removeAll(start, end);
                    mainThread.playFrom(Duration.millis(checkpoint+duration+epsilon));
                });
                mainThread.stop();
                mainThread.getKeyFrames().addAll(start, end, binding);
                mainThread.playFrom(Duration.millis(checkpoint));
            }));
        }
        time += duration;
    }

    private void translate(double duration, double xRatio, double yRatio, Label node)
    {
        translate(duration, panel.center.widthProperty().multiply(xRatio).subtract(node.widthProperty().divide(2)),
                panel.center.heightProperty().multiply(yRatio).subtract(node.heightProperty().divide(2)),
                node);
    }

    private void constructTimeline()
    {
        mainThread = new Timeline();
        time += 10;
        // ----------------------Step 1----------------------
//        panel.title.setText("./example.c");
//        panel.descriptionText.setText("  这里是要执行输出字符串的源代码，经编译链接生成可执行文件./example\n");
//        panel.setStep(1);
//
//        component.sourceCode.layoutXProperty().bind((panel.center.widthProperty().subtract(component.sourceCode.widthProperty())).divide(2));
//        component.sourceCode.layoutYProperty().bind((panel.center.heightProperty().subtract(component.sourceCode.heightProperty())).divide(2));
//        // Fade In
//        fade("in", 1000, component.sourceCode);
//        // Last 3 Seconds
//        time += 1000;
//        // Fade Out
//        fade("out", 1000, component.sourceCode);

        // ----------------------Step 2----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("fork & execve");
            panel.pushVariable("pid", "4");
            panel.pushVariable("filename", "./example");
            panel.descriptionText.setText("  fork系统调用创建进程，以供可执行文件执行，execve系统调用加载二进制文件\n");
            panel.setStep(2);
        }));
//
//        fade("in", 1000, component.operatingSystem);
//        fade("in", 1000, component.arrowDict.get("fork").getKey(), component.arrowDict.get("fork").getValue());
//        fade("in", 1000, component.process);
//        fade("out", 1000, component.arrowDict.get("fork").getKey(), component.arrowDict.get("fork").getValue());
//        fade("in", 1000, component.arrowDict.get("execve").getKey(), component.arrowDict.get("execve").getValue());
//        fade("in", 1000, component.file);
//        translate(1000, 0.9, 0.25, component.process);
//
//        fade("out", 1000, component.operatingSystem, component.file, component.arrowDict.get("execve").getKey(), component.arrowDict.get("execve").getValue());
        // ----------------------Step 3----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("write");
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("eax", "4");
            panel.pushVariable("fs", "23");
            panel.descriptionText.setText("  进程执行到printf语句，调用系统调用write，将用户缓冲区写入到字符设备文件/dev/tty0中\n");
            panel.setStep(3);
        }));
//        translate(1000, 0.5, 0.25, component.process);
//        fade("in", 1000, component.arrowDict.get("movl $4, %eax\n" + "int 0x80").getKey(),
//                component.arrowDict.get("movl $4, %eax\n" + "int 0x80").getValue());
//        fade("in", 1000, component.systemCall);
//        fade("in", 1000, component.arrowDict.get("movl $0x17, %edx\n" + "mov %dx, %fs").getKey(),
//                component.arrowDict.get("movl $0x17, %edx\n" + "mov %dx, %fs").getValue());
//        fade("in", 1000, component.terminal);
//
//        time += 1000;
//        fade("out", 1000, component.arrowDict.get("movl $4, %eax\n" + "int 0x80").getKey(),
//                component.arrowDict.get("movl $4, %eax\n" + "int 0x80").getValue(),
//                component.systemCall,
//                component.arrowDict.get("movl $0x17, %edx\n" + "mov %dx, %fs").getKey(),
//                component.arrowDict.get("movl $0x17, %edx\n" + "mov %dx, %fs").getValue());
//        fade("in", 1000, component.arrowDict.get("write").getKey(), component.arrowDict.get("write").getValue());
//        time += 1000;
//        fade("out", 1000, component.process, component.arrowDict.get("write").getKey(), component.arrowDict.get("write").getValue());

        // ----------------------Step 4----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("sys_write(unsigned int fd, char * buf, int count)");
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("buf", str);
            panel.pushVariable("count", String.valueOf(str.length()));
            panel.pushVariable("current->filp[fd]->f_inode->i_num", "66");
            panel.descriptionText.setText("  调用函数sys_write，判断是管道文件、字符设备文件、块设备文件还是常规文件\n");
            panel.setStep(4);
        }));
//        translate(1000, panel.center.widthProperty().multiply(1./3.).subtract(((ImageView)component.terminal.getChildren().get(0)).fitWidthProperty().divide(2)),
//                panel.center.heightProperty().multiply(0.5).subtract(
//                        (((ImageView)component.terminal.getChildren().get(0)).fitHeightProperty().add(
//                                ((Label)component.terminal.getChildren().get(1)).heightProperty())).divide(2)),
//                component.terminal);
//        fade("in", 1000, component.pipeFile, component.charFile, component.blockFile, component.regularFile);
//        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
//        {
//            component.charFile.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
//                    "-fx-background-radius: 10; -fx-background-color: lightgreen;" +
//                    " -fx-text-fill: white;");
//        }));
//        fade("in", 1000, component.arrowDict.get("is").getKey(), component.arrowDict.get("is").getValue());
//        fade("out", 1000, component.terminal, component.pipeFile, component.blockFile, component.regularFile,
//                component.arrowDict.get("is").getKey(), component.arrowDict.get("is").getValue());

        // ----------------------Step 5----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("int rw_char(int rw, int dev, char *buf, int count, off_t *pos)");
            panel.popVariable();
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("rw", "WRITE");
            panel.pushVariable("dev", "1024");
            panel.pushVariable("buf", str);
            panel.pushVariable("count", String.valueOf(str.length()));
            panel.descriptionText.setText("  是字符文件，根据该文件的inode的i_zone字段的i_zone[0]存放的所指设备的设备号作为参数传入rw_char，同时也将rw_char读写标记rw设为WRITE，作为参数传入，同时传入的还有用户缓冲区指针，读写字节数，文件当前读写指针（这里没有用）");
            panel.setStep(5);
        }));
//        translate(500, 0.4, 0.37, component.charFile);
//
//        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
//        {
//            panel.setXY(component.process, component.process.widthProperty(), component.process.heightProperty(), 0.6, 0.25);
//        }));
//        fade("in", 1000, component.process);
//        fade("in", 1000, component.arrowDict.get("sys_write").getKey(), component.arrowDict.get("sys_write").getValue());
//        fade("in", 1000, component.arrowDict.get("select").getValue());
//        fade("in", 1000, component.arrowDict.get("rw_char").getKey(), component.arrowDict.get("rw_char").getValue());
//
//        fade("in", 100, component.posParameter);
//        translate(1000, 0.6, 0.63, component.posParameter);
//        fade("out", 100, component.posParameter);
//
//        fade("in", 100, component.countParameter);
//        translate(1000, 0.6, 0.63, component.countParameter);
//        fade("out", 100, component.countParameter);
//
//        fade("in", 100, component.bufParameter);
//        translate(1000, 0.6, 0.63, component.bufParameter);
//        fade("out", 100, component.bufParameter);
//
//        fade("in", 100, component.devParameter);
//        translate(1000, 0.6, 0.63, component.devParameter);
//        fade("out", 100, component.devParameter);
//
//        fade("in", 100, component.rwParameter);
//        translate(1000, 0.6, 0.63, component.rwParameter);
//        fade("out", 100, component.rwParameter);
//
//        fade("out", 1000, component.process, component.charFile,
//                component.arrowDict.get("sys_write").getKey(), component.arrowDict.get("sys_write").getValue(),
//                component.arrowDict.get("select").getValue(),
//                component.arrowDict.get("rw_char").getKey(), component.arrowDict.get("rw_char").getValue());

        // ----------------------Step 6----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("int rw_char(int rw, int dev, char *buf, int count, off_t *pos)");
            panel.popVariable();
            panel.popVariable();
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("dev", "1024");
            panel.descriptionText.setText("  根据传入的设备号的高八位（即主设备号）和crw_table规定的主设备号对应的设备读写操作函数进行调用，主设备号为4，调用rw_ttyx");
            panel.setStep(5);
        }));
        fade("in", 1000, component.crw_table);
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.setXY(component.devParameter, component.devParameter.widthProperty(), component.devParameter.heightProperty(),
                    0.2, 0.5);
        }));
        fade("in", 1000, component.devParameter);
        fade("in", 1000, component.arrowDict.get("value").getValue());
        fade("in", 1000, component.devHigh, component.devLow);
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            component.devHigh.setStyle("-fx-pref-width: 100; -fx-pref-height: 100;" +
                    "-fx-background-color: indianred;" +
                    "-fx-border-color: black;" +
                    "-fx-text-fill: black");
        }));
        fade("in", 1000, component.arrowDict.get("according").getValue());
        fade("out", 1000, component.arrowDict.get("according").getValue(), component.crw_table);

        // ----------------------Step 7----------------------

    }
}
