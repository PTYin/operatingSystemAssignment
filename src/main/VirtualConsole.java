package main;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.awt.*;
import java.util.HashMap;

public class VirtualConsole extends GridPane
{
    public int rowSize = 10, columnSize = 80;
    public int end = rowSize*columnSize;
    private Font font = Font.font("Monospaced Regular", 15);
    private final String originStyle = "-fx-text-fill: white; -fx-font-family: monospace; -fx-font-size: 15;";
    private String currentStyle = originStyle;
    private String withCursorStyle = currentStyle + "-fx-background-color: indianred;";
    private FadeTransition cursorBlink;
    public boolean repeat = false;
    public int count = 0;

    private final HashMap<Integer, Label> reference = new HashMap<>();

    private final int NPAR = 16;
    private int npar;
    private int[] par = new int[NPAR];
    private boolean ques = false;

    public int state = 0;  // [0, 1, 2, 3, 4]
    public int x = 0, y = 0;
    public int top = 0, bottom = rowSize;
    private char[] buffer;
    private int i;
    private int nr;
    private int saved_x, saved_y;

    public VirtualConsole()
    {
        super();
        this.setStyle("-fx-background-color: black");

        for (int j = 0; j < columnSize; j++)
        {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(1. / columnSize * 100);
            this.getColumnConstraints().add(constraints);
        }

        for (int i = 0; i < rowSize; i++)
            for (int j = 0; j < columnSize; j++)
            {
                Label text = new Label(" ");
                text.setStyle(originStyle);
                text.prefWidthProperty().bind(this.widthProperty().divide(columnSize));
                reference.put(i * columnSize + j, text);
                this.add(text, j, i);
            }
    }

    private void modifyStyle(String style)
    {
//        0: setStyle(originStyle);
//        1: setStyle(originStyle + "-fx-font: bold;")
//        4: setStyle(originStyle + "-fx-font-style: italic;");
//        7: setStyle(originStyle + "-fx-text-fill: black; -fx-background-color: white;");
//        27: setStyle(originStyle);
        if(! style.endsWith(";"))
            style += ";";
        currentStyle = style;
        withCursorStyle = currentStyle + "-fx-background-color: indianred;";
    }

    public void display(char c, int columnIndex, int rowIndex)
    {
//        reference.get(rowIndex * columnSize + columnIndex).setFont(font);

        reference.get(rowIndex * columnSize + columnIndex).setStyle(currentStyle);
        reference.get(rowIndex * columnSize + columnIndex).setText(String.valueOf(c));
    }

    public void process(String str)
    {
        this.buffer = str.toCharArray();
        this.setOnMouseClicked(this::next);
        i = 0;
        nr = str.length();
    }

    private void next(MouseEvent e)
    {
        if (i < nr)
        {
            process(buffer[i]);
            i++;
        }
    }

    public String process(char c)
    {
        String description = "";
        delCursor();
        switch (state)
        {
            case 0:
                if (c > 31 && c < 127)
                {
                    description = "  " + Main.char2HexStr(c) + "是普通显示字符，判断当前光标处在行末端或末端以外，则将光标移到下行头列，并调整光标位置对应的内存指针pos。接着将字符c写到显示内存pos处，并将光标右移1列，同时也将pos对应移动2个字节。";
                    if (x >= columnSize)
                    {
                        x -= columnSize;
                        y++;
                    }
                    display(c, x, y);
                    x++;
                } else if (c == 27)  // ESC
                {
                    description = "  " + Main.char2HexStr(c) + "是ESC，则说明转义序列开始，将state设为1。";
                    state = 1;
                } else if (c == 10 || c == 11 || c == 12)
                {
                    description = "  " + Main.char2HexStr(c) + "是LF、VT、FF，则将光标移动到下1行，调用lf()，如果光标没有处再最后一行，直接修改行变量y++，并使pos+=video_size_row。";
                    y++;
                } else if (c == 13)
                {
                    description = "  " + Main.char2HexStr(c) + "是CR，则将光标移动到头列，调用cr()，使pos-=x*2，x复位为0。";
                    x = 0;
                } else if (c == 127)
                {
                    description = "  " + Main.char2HexStr(c) + "是DEL，则将光标左边字符用空格字符替代，并将光标移到被擦除位置，调用del()，pos-=2，x--，*pos=video_erase_char。";
                    if (x != 0)
                        x--;
                } else if (c == 8)
                {
                    description = "  " + Main.char2HexStr(c) + "BS，则将光标左移1格，pos减2，x--，pos-=2。";
                    if (x != 0)
                        x--;
                } else if (c == 9)  // HT
                {
                    description = "  " + Main.char2HexStr(c) + "是HT，则将光标移到8的倍数列上，若光标列数超出则移动到下一行。x+=8-(x&7)，pos+=(8-(x&7))<<1。如果x>video_num_columns则进行换行。";
                    x += 8 - (x & 7);
                    if (x > columnSize)
                    {
                        x -= columnSize;
                        y++;
                    }
                } else if (c == 7)  // BEL
                {
                    description = "  " + Main.char2HexStr(c) + "是BEL，则调用sysbeep()。";
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    toolkit.beep();
                }
                break;
            case 1:
                state = 0;
                if (c == '[')  // ESC [ - CSI序列
                {
                    description = "  " + Main.char2HexStr(c) + "是'['，ESC [表示是一个CSI序列，说明这是一个CSI控制序列，将state转到2，state=2。";
                    state = 2;
                } else if (c == 'E')  // ESC E - 光标下移1行回0列
                {
                    description = "  " + Main.char2HexStr(c) + "是'E'，ESC E表示光标下移1行回0列，将光标移到下一行第0列，调用gotoxy(0,y+1)。";
                    gotoXY(0, y + 1);
                } else if (c == 'M')
                {
                    description = "  " + Main.char2HexStr(c) + "是'M'，ESC M表示光标上移1行，则将将光标上移一行，调用ri()，如果光标不在屏幕第一行上，则y--，调整pos，否则则将窗口内容下移一行。";
                    if (y > 0)
                    {
                        y--;
                    }
                } else if (c == 'D')
                {
                    description = "  " + Main.char2HexStr(c) + "是'D'，ESC D表示光标下移1行，则将光标下移一行，调用lf()。";
                    y++;
                } else if (c == 'Z')
                {
                    description = "  " + Main.char2HexStr(c) + "是'Z'，则将发送终端应答字符序列，调用respond(tty)，即将\"ESC [?1;2c\"放入tty读队列中并使用copy_to_cooked()函数处理后放入辅助队列中。";
                    // respond(tty)
                } else if (c == '7')
                {
                    description = "  " + Main.char2HexStr(c) + "是'7'，那么保存当前光标位置。";
                    save_cur();
                } else if (c == '8')
                {
                    description = "  " + Main.char2HexStr(c) + "是'8'，恢复之前保存的光标位置。";
                    restore_cur();
                }
                break;
            case 2:
                description = "  对于state为2则说明是字符处在一个CSI控制序列中，首先对保存参数的数组par[]进行清零，将索引变量npar重置为0，并设置state为3，若此时字符不是?那么将直接进入状态3处理，否则说明是终端设备私有序列，之后有一个功能字符，所以break，读取下一个字符后再到状态3进行处理。";
                // ------------------------------------utilize to construct timeline------------------------------------
                if(!repeat)
                {
                    repeat = true;
                    break;
                }
                repeat = false;
                // -----------------------------------------------------------------------------------------------------
                for (npar = 0; npar < NPAR; npar++)
                {
                    par[npar] = 0;
                }
                npar = 0;
                state = 3;
                if (ques = (c == '?'))
                {
                    description = "  " + Main.char2HexStr(c) + "是'?'，说明是一个私有终端设备私有序列，之后有一个功能字符，所以break，读取下一个字符后再到状态3进行处理。";
                    break;
                }
            case 3:
                description = "  对于state为3，该状态用于把转义字符序列中的数字字符转换成数值保存在par[]数组中，根据字符c进行分类。";
                // ------------------------------------utilize to construct timeline------------------------------------
                if(!repeat&&count==0)
                {
                    repeat = true;
                    break;
                }
                repeat = false;
                count++;
                // -----------------------------------------------------------------------------------------------------
                if (c == ';' && npar < NPAR - 1)
                {
                    description = "  " + Main.char2HexStr(c) + "是';'，且数组par未满，则npar++，处理下一个字符";
                    npar++;
                    break;
                }
                else if (c >= '0' && c <= '9')
                {
                    description = "  " + Main.char2HexStr(c) + "是数字字符，则将该字符转换成数值并作为个位数与par[npar]*10相加作为新的par[npar]";
                    par[npar] = 10 * par[npar] + c - '0';
                    break;
                }
                else
                {
                    description = "  参数处理完毕，将state设为4。";
                    state = 4;
                }
            case 4:
                description += "\r\n  对于state为4，之前状态的处理使得已经获得了控制序列的前几部分，那么现在根据参数字符串最后一个字符（命令）来执行相关的操作。首先将state复位到0，其次根据c的类型进行处理。";
                // ------------------------------------utilize to construct timeline------------------------------------
                count = 0;
                if(!repeat)
                {
                    repeat = true;
                    break;
                }
                repeat = false;
                // -----------------------------------------------------------------------------------------------------
                state = 0;
                switch (c)
                {
                    case 'G': case '`':
                        description = "  " + Main.char2HexStr(c) + "是'G'或'`'，CSI Pn G或CSI Pn `表示光标水平移动，则第1个参数代表列号，如果参数列号不为0则将参数列号减一，调用gotoxy(par[0], y)。";
                        if(par[0] != 0)
                            par[0]--;
                        gotoXY(par[0], y);
                        break;
                    case 'A':
                        description = "  " + Main.char2HexStr(c) + "是'A'，CSI Pn A表示光标上移，那么第一个参数代表光标上移的行数。参数为0也上移一行，调用gotoxy(x, y-par[0]) 如果是'A'，那么第一个参数代表光标上移的行数。参数为0也上移一行，调用gotoxy(x, y-par[0])。";
                        if(par[0] == 0)
                            par[0]++;
                        gotoXY(x, y-par[0]);
                        break;
                    case 'B': case 'e':
                        description = "  " + Main.char2HexStr(c) + "是'B'或'e'，CSI Pn B或CSI Pn e表示光标上移，那么第一个参数代表光标下移的行数。参数为0也下移一行，调用gotoxy(x, y+par[0])。";
                        if(par[0] == 0)
                            par[0]++;
                        gotoXY(x, y+par[0]);
                        break;
                    case 'C': case 'a':
                        description = "  " + Main.char2HexStr(c) + "是'C'或'a'，CSI Pn C或CSI Pn a表示光标右移，那么第一个参数代表光标右移的格数。参数为0也右移一格，调用gotoxy(x+par[0], y)。";
                        if (par[0] == 0)
                            par[0]++;
                        gotoXY(x+par[0], y);
                        break;
                    case 'D':
                        description = "  " + Main.char2HexStr(c) + "是'D'，CSI Pn D表示光标左移，那么第一个参数代表光标左移的格数，参数为0也左移一格，调用gotoxy(x-par[0], y)。";
                        if (par[0] == 0)
                            par[0]++;
                        gotoXY(x-par[0], y);
                        break;
                    case 'E':
                        description = "  " + Main.char2HexStr(c) + "是'E'，那么第一个参数代表光标向下移动的行数，并回到0列，参数为0也下移一行，调用gotoxy(0, y+par[0])。";
                        if (par[0] == 0)
                            par[0]++;
                        gotoXY(0, y+par[0]);
                        break;
                    case 'F':
                        description = "  " + Main.char2HexStr(c) + "是'F'，那么第一个参数代表光标向上移动的行数，并回到0列，参数为0也上移一行，调用gotoxy(0, y-par[0])。";
                        if(par[0] == 0)
                            par[0]++;
                        gotoXY(0, y-par[0]);
                        break;
                    case 'd':
                        description = "  " + Main.char2HexStr(c) + "是'd'，那么第一个参数代表行号，如果参数行号不为0则将参数行号减一，调用gotoxy(x, par[0])。";
                        if (par[0] != 0)
                            par[0]--;
                        gotoXY(x, par[0]);
                        break;
                    case 'H': case 'f':
                        description = "  " + Main.char2HexStr(c) + "是'H'或'f'，则第一个参数代表光标移到的行号，第二个参数代表光标移到的列号，如果不为0则将参数减一，调用gotoxy(par[1], par[0])。";
                        if (par[0] != 0)
                            par[0]--;
                        if (par[1] != 0)
                            par[1]--;
                        gotoXY(par[1], par[0]);
                        break;
                    case 'J':
                        description = "  " + Main.char2HexStr(c) + "是'J'，则第一个参数代表以光标所处位置清屏的方式，调用csi_J(par[0])，根据par[0]进行分类。\n" +
                                "  当par[0]为0时，擦除光标到屏幕底端所有字符。\n" +
                                "  当par[0]为1时，删除从屏幕开始到光标处的字符。\n" +
                                "  当par[1]为2时，删除整个屏幕上的所有字符。";
                        csi_J(par[0]);
                        break;
                    case 'K':
                        description = "  " + Main.char2HexStr(c) + "是'K'，第一个参数代表以光标所在位置对行中字符进行删除处理的方式，调用csi_K(par[0])，根据par[0]进行分类。\n" +
                                "  当par[0]为0时，删除光标到行尾底端所有字符。\n" +
                                "  当par[0]为1时，删除从行开始到光标处的字符。\n" +
                                "  当par[1]为2时，删除整行所有字符。";
                        csi_K(par[0]);
                        break;
                    case 'L':
                        description = "  " + Main.char2HexStr(c) + "是'L'，第一个参数表示在光标位置处插入几行，调用csi_L(par[0])，如果插入的行数大于屏幕最多行数，则截为屏幕显示行数，若插入行数nr为0，则插入1行。";
                        csi_L(par[0]);
                        break;
                    case 'M':
                        description = "  " + Main.char2HexStr(c) + "是'M'，第一个参数表示在光标位置删除n行，调用csi_M(par[0])，如果删除的行数大于屏幕最多行数，则截为屏幕显示行数；若欲删除的行数nr为0，则删除1行。";
                        csi_M(par[0]);
                        break;
                    case 'P':
                        description = "  " + Main.char2HexStr(c) + "是'P'，第一个参数表示在光标位置处删除n个字符，调用csi_P(par[0])，如果删除的字符数大于一行字符数，则截为一行字符数，若删除字符数为0，则删除1个字符。";
                        csi_P(par[0]);
                        break;
                    case '@':
                        description = "  " + Main.char2HexStr(c) + "是'@'，第一个参数表示在光标位置处插入n个字符，调用csi_at(par[0])，如果插入的字符数大于一行字符数，则截为一行字符数，若插入字符数为0，则插入1个字符。";
                        csi_at(par[0]);
                        break;
                    case 'm':
                        description = "  " + Main.char2HexStr(c) + "是'm'，则第一个参数表示改变光标处字符的显示属性（0：正常显示、1：加粗、4：加下划线、7：反显、27：正常显示），调用csi_m()。";
                        csi_m();
                        break;
                    case 'r':
                        description = "  " + Main.char2HexStr(c) + "是'r'，则第一个参数表示滚屏的起始行号，第二个参数表示终止行号。";
                        if(par[0] != 0)
                            par[0]--;
                        if(par[1] == 0)
                            par[1] = rowSize;
                        if (par[0] < par[1] && par[1] <= rowSize)
                        {
                            top = par[0];
                            bottom = par[1];
                        }
                        break;
                    case 's':
                        description = "  " + Main.char2HexStr(c) + "是's'，保存当前光标位置。";
                        save_cur();
                        break;
                    case 'u':
                        description = "  " + Main.char2HexStr(c) + "是'u'，恢复光标到原保存位置处。";
                        restore_cur();
                        break;
                }
                break;
        }
        setCursor();
        return description;
    }

    private void csi_J(int par)
    {
        int count, start;
        switch(par)
        {
            case 0:  // 擦除光标到屏幕底端所有字符
                count = end - y*columnSize+x;
                start = y*columnSize+x;
                break;
            case 1:  // 删除从屏幕开始到光标处的字符
                count = y*columnSize+x;
                start = 0;
                break;
            case 2:  // 删除整个屏幕上的字符
                count = end;
                start = 0;
                break;
            default:
                return;
        }
        for(int i = 0; i < count; i++)
        {
            reference.get(start+i).setText(" ");
        }
    }

    private void csi_K(int par)
    {
        int count, start;
        switch(par)
        {
            case 0:  // 擦除光标到行末所有字符
                if (x>=columnSize)
                    return;
                count = columnSize - x;
                start = y*columnSize+x;
                break;
            case 1:  // 删除从行开始到光标处的字符
                count = Math.min(x, columnSize);
                start = y*columnSize;
                break;
            case 2:  // 删除整行的字符
                count = columnSize;
                start = y*columnSize;
                break;
            default:
                return;
        }
        for(int i = 0; i < count; i++)
        {
            reference.get(start+i).setText(" ");
        }
    }

    private void csi_L(int nr)
    {
        if (nr > columnSize)
            nr = rowSize;
        else if (nr == 0)
            nr = 1;
        while(nr-- != 0)
            insert_line();
    }

    private void csi_M(int nr)
    {
        if (nr > columnSize)
            nr = rowSize;
        else if (nr == 0)
            nr = 1;
        while(nr-- != 0)
            delete_line();
    }

    private void csi_P(int nr)
    {
        if (nr > columnSize)
            nr = rowSize;
        else if (nr == 0)
            nr = 1;
        while(nr-- != 0)
            delete_char();
    }

    private void csi_at(int nr)
    {
        if (nr > columnSize)
            nr = rowSize;
        else if (nr == 0)
            nr = 1;
        while(nr-- != 0)
            insert_char();
    }

//        0: setStyle(originStyle);
//        1: setStyle(originStyle + "-fx-font: bold;")
//        4: setStyle(originStyle + "-fx-font-style: italic;");
//        7: setStyle(originStyle + "-fx-text-fill: black; -fx-background-color: white;");
//        27: setStyle(originStyle);
    private void csi_m()
    {
        for(int i=0;i<=npar;i++)
            switch (par[i])
            {
                case 0:
                    modifyStyle(originStyle);
                    break;
                case 1:
                    modifyStyle(currentStyle + "-fx-font: bold;");
                    break;
                case 4:
                    modifyStyle(currentStyle + "-fx-font-style: italic;");
                    break;
                case 7:
                    modifyStyle(currentStyle + "-fx-text-fill: black; -fx-background-color: white;");
                    break;
                case 27:
                    modifyStyle(currentStyle + "-fx-text-fill: white; -fx-background-color: black;");
                    break;
            }
    }

    private void insert_line(){}
    private void insert_char(){}
    private void delete_line(){}
    private void delete_char(){}

    private void gotoXY(int new_x, int new_y)
    {
        if (new_x > columnSize || new_y >= rowSize)
            return;
        x = new_x;
        y = new_y;
    }

    private void save_cur()
    {
        saved_x = x;
        saved_y = y;
    }

    private void restore_cur()
    {
        gotoXY(saved_x, saved_y);
    }

    private void delCursor()
    {
        if (cursorBlink != null)
            cursorBlink.stop();
        reference.get(y * columnSize + x).setOpacity(1);
        reference.get(y * columnSize + x).setStyle(originStyle);
    }

    private void setCursor()
    {
        reference.get(y * columnSize + x).setStyle(withCursorStyle);
        cursorBlink = new FadeTransition(Duration.millis(100), reference.get(y * columnSize + x));
        cursorBlink.setFromValue(1.0);
        cursorBlink.setToValue(0.0);
        cursorBlink.setCycleCount(-1);
        cursorBlink.setAutoReverse(true);
        cursorBlink.play();
    }

}
