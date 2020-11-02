package main;

import Shape.Queue;
import com.sun.istack.internal.Nullable;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Main extends Application
{
    private Panel panel;
    private Timeline mainThread;
    private boolean playing;
    private Component component;
    private VirtualConsole virtualConsole;
    private String str, cooked;
    private ArrayList<String> dict;

    private double time = 0;  // millis
    private final double epsilon = 1;

    @Override
    public void start(Stage primaryStage)
    {
//        String str = Prompt.prompt();
        str = "\033[7mwor\033[2Kld";
        dict = new ArrayList<>();
        cooked = cook(str, dict);
        component = new Component(str, cooked, dict);
        panel = new Panel();
        virtualConsole = new VirtualConsole();

//        console.prefWidthProperty().bind(panel.center.widthProperty());
//        console.prefHeightProperty().bind(panel.center.heightProperty().divide(2));
//        panel.center.getChildren().add(console);
//        // test
//        String str = "hello, \033[7mwor\033[2Kld";
//        console.process(str);
        panel.addObject(component);
        panel.center.getChildren().add(virtualConsole);
        virtualConsole.setOpacity(0);
        virtualConsole.prefWidthProperty().bind(panel.center.widthProperty());
        virtualConsole.prefHeightProperty().bind(panel.center.heightProperty().divide(2));
        panel.setXY(virtualConsole, virtualConsole.widthProperty(), virtualConsole.heightProperty(), 0.5, 0.5);

        constructTimeline();
        mainThread.play();
        playing = true;
        Scene scene = new Scene(panel, 1600, 900);
        scene.setOnKeyPressed(event ->
        {
            if(event.getCode().equals(KeyCode.SPACE))
            {
                if(playing)
                    mainThread.pause();
                else
                    mainThread.play();
                playing = !playing;
            }
        });
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
        panel.title.setText("./example.c");
        panel.descriptionText.setText("  这里是要执行输出字符串的源代码，经编译链接生成可执行文件./example\n");
        panel.setStep(1);

        component.sourceCode.layoutXProperty().bind((panel.center.widthProperty().subtract(component.sourceCode.widthProperty())).divide(2));
        component.sourceCode.layoutYProperty().bind((panel.center.heightProperty().subtract(component.sourceCode.heightProperty())).divide(2));
        // Fade In
        fade("in", 1000, component.sourceCode);
        // Last 3 Seconds
        time += 1000;
        // Fade Out
        fade("out", 1000, component.sourceCode);

        // ----------------------Step 2----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("fork & execve");
            panel.pushVariable("pid", "4");
            panel.pushVariable("filename", "./example");
            panel.descriptionText.setText("  fork系统调用创建进程，以供可执行文件执行，execve系统调用加载二进制文件\n");
            panel.setStep(2);
        }));
        fade("in", 1000, component.operatingSystem);
        fade("in", 1000, component.arrowDict.get("fork").getKey(), component.arrowDict.get("fork").getValue());
        fade("in", 1000, component.process);
        fade("out", 1000, component.arrowDict.get("fork").getKey(), component.arrowDict.get("fork").getValue());
        fade("in", 1000, component.arrowDict.get("execve").getKey(), component.arrowDict.get("execve").getValue());
        fade("in", 1000, component.file);
        translate(1000, 0.9, 0.25, component.process);

        fade("out", 1000, component.operatingSystem, component.file, component.arrowDict.get("execve").getKey(), component.arrowDict.get("execve").getValue());

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
        translate(1000, 0.5, 0.25, component.process);
        fade("in", 1000, component.arrowDict.get("movl $4, %eax\n" + "int 0x80").getKey(),
                component.arrowDict.get("movl $4, %eax\n" + "int 0x80").getValue());
        fade("in", 1000, component.systemCall);
        fade("in", 1000, component.arrowDict.get("movl $0x17, %edx\n" + "mov %dx, %fs").getKey(),
                component.arrowDict.get("movl $0x17, %edx\n" + "mov %dx, %fs").getValue());
        fade("in", 1000, component.terminal);

        time += 1000;
        fade("out", 1000, component.arrowDict.get("movl $4, %eax\n" + "int 0x80").getKey(),
                component.arrowDict.get("movl $4, %eax\n" + "int 0x80").getValue(),
                component.systemCall,
                component.arrowDict.get("movl $0x17, %edx\n" + "mov %dx, %fs").getKey(),
                component.arrowDict.get("movl $0x17, %edx\n" + "mov %dx, %fs").getValue());
        fade("in", 1000, component.arrowDict.get("write").getKey(), component.arrowDict.get("write").getValue());
        time += 1000;
        fade("out", 1000, component.process, component.arrowDict.get("write").getKey(), component.arrowDict.get("write").getValue());

        // ----------------------Step 4----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("sys_write(unsigned int fd, char * buf, int count)");
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("buf", cooked);
            panel.pushVariable("count", String.valueOf(str.length()));
            panel.pushVariable("current->filp[fd]->f_inode->i_num", "66");
            panel.descriptionText.setText("  调用函数sys_write，判断是管道文件、字符设备文件、块设备文件还是常规文件\n");
            panel.setStep(4);
        }));
        translate(1000, panel.center.widthProperty().multiply(1./3.).subtract(((ImageView)component.terminal.getChildren().get(0)).fitWidthProperty().divide(2)),
                panel.center.heightProperty().multiply(0.5).subtract(
                        (((ImageView)component.terminal.getChildren().get(0)).fitHeightProperty().add(
                                ((Label)component.terminal.getChildren().get(1)).heightProperty())).divide(2)),
                component.terminal);
        fade("in", 1000, component.pipeFile, component.charFile, component.blockFile, component.regularFile);
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            component.charFile.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                    "-fx-background-radius: 10; -fx-background-color: lightgreen;" +
                    " -fx-text-fill: white;");
        }));
        fade("in", 1000, component.arrowDict.get("is").getKey(), component.arrowDict.get("is").getValue());
        fade("out", 1000, component.terminal, component.pipeFile, component.blockFile, component.regularFile,
                component.arrowDict.get("is").getKey(), component.arrowDict.get("is").getValue());

        // ----------------------Step 5----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("int rw_char(int rw, int dev, char *buf, int count, off_t *pos)");
            panel.popVariable();
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("rw", "WRITE");
            panel.pushVariable("dev", "1024");
            panel.pushVariable("buf", cooked);
            panel.pushVariable("count", String.valueOf(str.length()));
            panel.descriptionText.setText("  是字符文件，根据该文件的inode的i_zone字段的i_zone[0]存放的所指设备的设备号作为参数传入rw_char，同时也将rw_char读写标记rw设为WRITE，作为参数传入，同时传入的还有用户缓冲区指针，读写字节数，文件当前读写指针（这里没有用）");
            panel.setStep(5);
        }));
        translate(500, 0.4, 0.37, component.charFile);

        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.setXY(component.process, component.process.widthProperty(), component.process.heightProperty(), 0.6, 0.25);
        }));
        fade("in", 1000, component.process);
        fade("in", 1000, component.arrowDict.get("sys_write").getKey(), component.arrowDict.get("sys_write").getValue());
        fade("in", 1000, component.arrowDict.get("select").getValue());
        fade("in", 1000, component.arrowDict.get("rw_char").getKey(), component.arrowDict.get("rw_char").getValue());

        fade("in", 100, component.posParameter);
        translate(1000, 0.6, 0.63, component.posParameter);
        fade("out", 100, component.posParameter);

        fade("in", 100, component.countParameter);
        translate(1000, 0.6, 0.63, component.countParameter);
        fade("out", 100, component.countParameter);

        fade("in", 100, component.bufParameter);
        translate(1000, 0.6, 0.63, component.bufParameter);
        fade("out", 100, component.bufParameter);

        fade("in", 100, component.devParameter);
        translate(1000, 0.6, 0.63, component.devParameter);
        fade("out", 100, component.devParameter);

        fade("in", 100, component.rwParameter);
        translate(1000, 0.6, 0.63, component.rwParameter);
        fade("out", 100, component.rwParameter);

        fade("out", 1000, component.process, component.charFile,
                component.arrowDict.get("sys_write").getKey(), component.arrowDict.get("sys_write").getValue(),
                component.arrowDict.get("select").getValue(),
                component.arrowDict.get("rw_char").getKey(), component.arrowDict.get("rw_char").getValue());

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
            panel.setStep(6);
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
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("int rw_ttyx(int rw, unsigned minor, char *buf, int count, off_t *pos)");
            panel.popVariable();
            panel.pushVariable("rw", "WRITE");
            panel.pushVariable("minor", "0");
            panel.pushVariable("buf", cooked);
            panel.pushVariable("count", String.valueOf(str.length()));
            panel.descriptionText.setText("  次设备号为0，将其作为参数传入rw_ttyx，同时也将用户缓冲区和读写字节数传入");
            panel.setStep(7);
        }));

        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.setXY(component.rwParameter, new SimpleDoubleProperty(0), component.rwParameter.heightProperty(), 0., 0.8);
            panel.setXY(component.minorParameter, new SimpleDoubleProperty(0), component.minorParameter.heightProperty(), 0., 0.8);
            panel.setXY(component.bufParameter, new SimpleDoubleProperty(0), component.bufParameter.heightProperty(), 0., 0.8);
            panel.setXY(component.countParameter, new SimpleDoubleProperty(0), component.countParameter.heightProperty(), 0., 0.8);
            panel.setXY(component.posParameter, new SimpleDoubleProperty(0), component.posParameter.heightProperty(), 0., 0.8);
            component.devHigh.setStyle("-fx-pref-width: 100; -fx-pref-height: 100;" +
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: black;" +
                    "-fx-text-fill: black");

        }));

        fade("in", 1000, component.arrowDict.get("rw_ttyx").getKey(), component.arrowDict.get("rw_ttyx").getValue());

        fade("in", 100, component.posParameter);
        translate(1000, 0.7, 0.8, component.posParameter);
//        fade("out", 100, component.posParameter);

        fade("in", 100, component.countParameter);
        translate(1000, component.posParameter.layoutXProperty().subtract(component.countParameter.widthProperty()),
                panel.center.heightProperty().multiply(0.8).subtract(component.countParameter.heightProperty().divide(2)),
                component.countParameter);
//        fade("out", 100, component.countParameter);

        fade("in", 100, component.bufParameter);
        translate(1000, component.countParameter.layoutXProperty().subtract(component.bufParameter.widthProperty()),
                panel.center.heightProperty().multiply(0.8).subtract(component.bufParameter.heightProperty().divide(2)),
                component.bufParameter);
//        fade("out", 100, component.bufParameter);

        fade("in", 100, component.minorParameter);
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            component.devLow.setStyle("-fx-pref-width: 100; -fx-pref-height: 100;" +
                    "-fx-background-color: indianred;" +
                    "-fx-border-color: black;" +
                    "-fx-text-fill: black");
        }));
        fade("in", 1000, component.arrowDict.get("minor").getValue());
        translate(1000, component.bufParameter.layoutXProperty().subtract(component.minorParameter.widthProperty()),
                panel.center.heightProperty().multiply(0.8).subtract(component.minorParameter.heightProperty().divide(2)),
                component.minorParameter);
//        fade("out", 100, component.minorParameter, component.arrowDict.get("minor").getValue());

        fade("in", 100, component.rwParameter);
        translate(1000, component.minorParameter.layoutXProperty().subtract(component.rwParameter.widthProperty()),
                panel.center.heightProperty().multiply(0.8).subtract(component.rwParameter.heightProperty().divide(2)),
                component.rwParameter);
//        fade("out", 100, component.rwParameter);
        fade("out", 1000, component.arrowDict.get("value").getValue(), component.arrowDict.get("minor").getValue(),
                component.devHigh, component.devLow, component.devParameter,
                component.arrowDict.get("rw_ttyx").getKey(), component.arrowDict.get("rw_ttyx").getValue(),
                component.posParameter);

        // ----------------------Step 8----------------------

        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("int tty_write(unsigned channel, char *buf, int nr)");
            panel.popVariable();
            panel.popVariable();
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("channel", "0");
            panel.pushVariable("buf", cooked);
            panel.pushVariable("nr", String.valueOf(str.length()));
            panel.descriptionText.setText("  rw_ttyx根据传入的参数rw判断是读还是写，rw为WRITE，调用tty_write，将次设备号，用户缓冲区指针，读写字节数传入");
            panel.setStep(8);
        }));

        time += 1000;
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            component.minorParameter.setText("channel");
            component.countParameter.setText("nr");
        }));
        translate(1000, 0.25, 0.25, component.rwParameter);

        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            component.arrowDict.get("rw_ttyx").getValue().startXProperty().bind(panel.center.widthProperty().multiply(0.75));
            component.arrowDict.get("rw_ttyx").getValue().startYProperty().bind(panel.center.heightProperty().multiply(0.1));
            component.arrowDict.get("rw_ttyx").getValue().endXProperty().bind(panel.center.widthProperty().multiply(0.75));
            component.arrowDict.get("rw_ttyx").getValue().endYProperty().bind(panel.center.heightProperty().multiply(0.5));
        }));
        fade("in", 1000, component.arrowDict.get("rw_ttyx").getKey(), component.arrowDict.get("rw_ttyx").getValue());
        fade("in", 1000, component.rwValue);
        fade("in", 1000, component.arrowDict.get("rw").getValue());
        fade("in", 1000, component.arrowDict.get("para").getValue());
        fade("in", 1000, component.arrowDict.get("tty_write").getKey(), component.arrowDict.get("tty_write").getValue());
        translate(500, 0.75, 0.7, component.countParameter);
        fade("out", 100, component.countParameter);
        translate(500, 0.75, 0.7, component.bufParameter);
        fade("out", 100, component.bufParameter);
        translate(500, 0.75, 0.7, component.minorParameter);
        fade("out", 100, component.minorParameter);
        fade("out", 1000, component.rwParameter, component.arrowDict.get("rw").getValue(), component.rwValue, component.arrowDict.get("para").getValue(),
                component.arrowDict.get("rw_ttyx").getKey(), component.arrowDict.get("rw_ttyx").getValue(),
                component.arrowDict.get("tty_write").getKey(), component.arrowDict.get("tty_write").getValue());

        // ----------------------Step 9----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("int tty_write(unsigned channel, char *buf, int nr)");
            panel.popVariable();
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("channel", "0");
            panel.descriptionText.setText("  tty_write首先判断传入的次设备号是否合法（即为0，1，2其中一个）,根据次设备号，选择对应预先定义的tty_table中的项来进行初始化数据，次设备号是0，那么将输入的CR（\\r）转换为NL（\\n），将输出的NL（\\n）转换为CRNL（\\r\\n）并设置一些其它标志，如控制模式标志，本地模式标志等");
            panel.setStep(9);
        }));
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.setXY(component.minorParameter, component.minorParameter.widthProperty(), component.minorParameter.heightProperty(), 0.1, 0.5);
        }));
        fade("in", 1000, component.minorParameter);
        fade("in", 1000, component.channel0, component.channel1, component.channel2);
        fade("in", 1000, component.arrowDict.get("channel").getKey(), component.arrowDict.get("channel").getValue());
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            component.channel0.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                    "-fx-background-radius: 10;  -fx-background-color: lightgreen;" +
                    "-fx-border-color: black; -fx-border-radius: 10;");
        }));
        fade("in", 1000, component.arrowDict.get("tty_table").getKey(), component.arrowDict.get("tty_table").getValue());
        fade("in", 1000, component.tty_table);

        fade("out", 1000, component.minorParameter,
                component.channel0, component.channel1, component.channel2,
                component.arrowDict.get("channel").getKey(), component.arrowDict.get("channel").getValue(),
                component.arrowDict.get("tty_table").getKey(), component.arrowDict.get("tty_table").getValue(),
                component.tty_table);

        // ----------------------Step 10----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("tty->write_q");
            panel.popVariable();
            panel.pushVariable("fs", "23");
            panel.pushVariable("buf", cooked);
            panel.descriptionText.setText("  进入循环\n" +
                    "  先判断tty->write_q即tty写队列是否已满，刚开始图中所示是空，即是否大于TTY_BUF_SIZE(1024)，若满则进入可中断的睡眠状态，如果当前进程有信号要处理则退出循环体\n" +
                    "  因为用户缓冲区存在于用户数据空间，故需要用get_fs_byte将用户数据空间之间的数据复制到内核数据空间，该函数将[fs:addr]的一个字符返回\n");
            panel.setStep(10);
        }));
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.setXY(component.bufParameter, component.bufParameter.widthProperty(), component.bufParameter.heightProperty(), 0.1, 0.1);
        }));
        fade("in", 1000, component.ttyQueue);

        fade("in", 1000, component.bufParameter);
        fade("in", 1000, component.bufQueue);
        fade("in", 1000, component.arrowDict.get("buf").getValue());
        fade("in", 1000, component.arrowDict.get("get_fs_byte").getKey(), component.arrowDict.get("get_fs_byte").getValue());
        fade("in", 1000, component.currentValue);
        fade("in", 1000, component.arrowDict.get("push").getValue());

        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            component.ttyQueue.push(char2HexStr(str.charAt(0)));
        }));
        time += 1000;

        for(int i=1;i<str.length();i++)
        {
            int finalI = i;
            mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
            {
                ReadOnlyDoubleProperty bufQueueSize = ((Label) component.bufQueue.getChildren().get(0)).widthProperty();
                component.arrowDict.get("get_fs_byte").getValue().startXProperty().bind(
                        component.bufQueue.layoutXProperty().add(bufQueueSize.multiply(finalI+0.5)));
            }));
            time += 500;
            mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
            {
                component.currentValue.setText("'"+dict.get(finalI)+"'");
            }));
            time += 500;
            mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
            {
                component.arrowDict.get("push").getValue().endXProperty().bind(component.ttyQueue.layoutXProperty().add((finalI+0.5)* Queue.SIZE));
            }));
            time += 500;
            mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
            {
                component.ttyQueue.push(char2HexStr(str.charAt(finalI)));
            }));
            time += 1000;
        }

        fade("out", 1000, component.bufParameter, component.bufQueue,
                component.arrowDict.get("buf").getValue(),
                component.arrowDict.get("get_fs_byte").getKey(), component.arrowDict.get("get_fs_byte").getValue(),
                component.currentValue,
                component.arrowDict.get("push").getValue());

        // ----------------------Step 11----------------------
        mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
        {
            panel.title.setText("void con_write(struct tty_struct *tty)");
            panel.popVariable();
            panel.popVariable();
            panel.pushVariable("x", "0");
            panel.pushVariable("y", "0");
            panel.descriptionText.setText("  con_write依次获取tty写队列中的字符，将tail指针+1，若tail指针在循环队列在内存的最后一个元素则回到循环队列第一个元素\n" +
                    "  判断state（0/1/2/3/4）,再给根据字符进行不同的处理");
            panel.setStep(11);
        }));

        fade("in", 1000, component.states);
        fade("in", 1000, virtualConsole);
        VirtualConsole test = new VirtualConsole();
        for(int i=0;i<str.length();i++)
        {
            char c = str.charAt(i);
            test.process(c);  // pre process to construct timeline
            mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
            {
                String description = virtualConsole.process(c);
                for(Label state: component.states.states)
                {
                    state.setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                            "-fx-border-radius: 50; -fx-border-color: black;" +
                            "-fx-font-size: 25");
                }
                component.states.states.get(virtualConsole.state).setStyle("-fx-pref-width: 100; -fx-pref-height:100;" +
                        "-fx-border-radius: 50; -fx-border-color: black;" +
                        "-fx-background-radius:50; -fx-background-color: lightgreen;" +
                        "-fx-font-size: 25");
                panel.descriptionText.setText(description);
                panel.variables.get("x").setText("x: "+virtualConsole.x);
                panel.variables.get("y").setText("y: "+virtualConsole.y);
            }));
            time += 2000;
            mainThread.getKeyFrames().add(new KeyFrame(Duration.millis(time), event ->
            {
                if(!virtualConsole.repeat)
                    component.ttyQueue.pop();
            }));
            time += 1000;
            if(test.repeat)
            {
                i--;
            }
        }
    }


    public static String char2HexStr(char c)
    {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        int bit;
        sb.append("0x");
        bit = (c & 0x0f0) >> 4;
        sb.append(chars[bit]);
        bit = c & 0x0f;
        sb.append(chars[bit]);
        sb.append(" ");
        return sb.toString().trim();
    }

    public static String cook(String str, @Nullable ArrayList<String> map)
    {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        int bit;
        for (byte b : bs)
        {
            if(0x20 <= b && b <= 0x7E)
            {
                sb.append((char) b);
                if(map != null)
                    map.add((char) b + "");
            }
            else
            {
                StringBuilder temp = new StringBuilder();
                temp.append("\\");
                bit = (b & 0700) >> 6;
                if(chars[bit]!='0')
                    temp.append("0");
                temp.append(chars[bit]);
                bit = (b & 0070) >> 3;
                temp.append(chars[bit]);
                bit = b & 0007;
                temp.append(chars[bit]);
                sb.append(temp);
                if(map != null)
                    map.add(temp.toString());
            }
        }
        return sb.toString().trim();
    }
}
