package main;

import javafx.scene.control.TextInputDialog;
import org.unbescape.java.JavaEscape;

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

    public static String testMode()
    {
        TextInputDialog dialog = new TextInputDialog("\\033[7mwor\\033[2Kld");
        dialog.setTitle("Test Model");
        dialog.setHeaderText("直接切入模拟终端");
        dialog.setContentText("输入字符串：");
        dialog.showAndWait();
        String raw = dialog.getResult();
        return JavaEscape.unescapeJava(raw);
    }

    public static String prompt()
    {
        TextInputDialog dialog = new TextInputDialog("Hello, World");
        dialog.setTitle("Prompt");
        dialog.setHeaderText("动画将依据输入字符串而变化");
        dialog.setContentText("输入字符串：");
        dialog.showAndWait();
        String raw = dialog.getResult();
        return JavaEscape.unescapeJava(raw);
    }
}
