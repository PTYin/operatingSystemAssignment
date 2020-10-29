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

    public void process(char c)
    {
        delCursor();
        switch (state)
        {
            case 0:
                if (c > 31 && c < 127)
                {
                    if (x >= columnSize)
                    {
                        x -= columnSize;
                        y++;
                    }
                    display(c, x, y);
                    x++;
                } else if (c == 27)  // ESC
                {
                    state = 1;
                } else if (c == 10 || c == 11 || c == 12)
                {
                    y++;
                } else if (c == 13)
                {
                    x = 0;
                } else if (c == 127)
                {
                    if (x != 0)
                        x--;
                } else if (c == 8)
                {
                    if (x != 0)
                        x--;
                } else if (c == 9)  // HT
                {
                    x += 8 - (x & 7);
                    if (x > columnSize)
                    {
                        x -= columnSize;
                        y++;
                    }
                } else if (c == 7)  // BEL
                {
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    toolkit.beep();
                }
                break;
            case 1:
                state = 0;
                if (c == '[')  // ESC [ - CSI序列
                {
                    state = 2;
                } else if (c == 'E')  // ESC E - 光标下移1行回0列
                {
                    gotoXY(0, y + 1);
                } else if (c == 'M')
                {
                    if (y > 0)
                    {
                        y--;
                    }
                } else if (c == 'D')
                {
                    y++;
                } else if (c == 'Z')
                {
                    // respond(tty)
                } else if (c == '7')
                {
                    save_cur();
                } else if (c == '8')
                {
                    restore_cur();
                }
                break;
            case 2:
                for (npar = 0; npar < NPAR; npar++)
                {
                    par[npar] = 0;
                }
                npar = 0;
                state = 3;
                if (ques = (c == '?'))
                    break;
            case 3:
                if (c == ';' && npar < NPAR - 1)
                    npar++;
                else if (c >= '0' && c <= '9')
                {
                    par[npar] = 10 * par[npar] + c - '0';
                    break;
                } else
                    state = 4;
            case 4:
                state = 0;
                switch (c)
                {
                    case 'G': case '`':
                        if(par[0] != 0)
                            par[0]--;
                        gotoXY(par[0], y);
                        break;
                    case 'A':
                        if(par[0] == 0)
                            par[0]++;
                        gotoXY(x, y-par[0]);
                        break;
                    case 'B': case 'e':
                        if(par[0] == 0)
                            par[0]++;
                        gotoXY(x, y+par[0]);
                        break;
                    case 'C': case 'a':
                        if (par[0] == 0)
                            par[0]++;
                        gotoXY(x+par[0], y);
                        break;
                    case 'D':
                        if (par[0] == 0)
                            par[0]++;
                        gotoXY(x-par[0], y);
                        break;
                    case 'E':
                        if (par[0] == 0)
                            par[0]++;
                        gotoXY(0, y+par[0]);
                        break;
                    case 'F':
                        if(par[0] == 0)
                            par[0]++;
                        gotoXY(0, y-par[0]);
                        break;
                    case 'd':
                        if (par[0] != 0)
                            par[0]--;
                        gotoXY(x, par[0]);
                        break;
                    case 'H': case 'f':
                        if (par[0] != 0)
                            par[0]--;
                        if (par[1] != 0)
                            par[1]--;
                        gotoXY(par[1], par[0]);
                        break;
                    case 'J':
                        csi_J(par[0]);
                        break;
                    case 'K':
                        csi_K(par[0]);
                        break;
                    case 'L':
                        csi_L(par[0]);
                        break;
                    case 'M':
                        csi_M(par[0]);
                        break;
                    case 'P':
                        csi_P(par[0]);
                        break;
                    case '@':
                        csi_at(par[0]);
                        break;
                    case 'm':
                        csi_m();
                        break;
                    case 'r':
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
                        save_cur();
                        break;
                    case 'u':
                        restore_cur();
                        break;
                }
                break;
        }
        setCursor();
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
