/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.i_comit.shared;

import com.i_comit.windows.GUI;
import com.i_comit.windows.Main;
import static com.i_comit.windows.Main.jAlertLabel;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JLabel;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Miscs {

    public static boolean holidayCheck(Main main) {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd"));
        switch (today) {
            case "01/01":
                GUI.labelCutterLoginThread(jAlertLabel, "happy new year " + Main.year + "!", 80, 80, 1600, main);
                return true;
            case "11/24":
                GUI.labelCutterLoginThread(jAlertLabel, "happy thanksgiving " + Main.year + "!", 80, 80, 1600, main);
                return true;
            case "12/25":
                GUI.labelCutterLoginThread(jAlertLabel, "happy holidays " + Main.year + "!", 80, 80, 1600, main);
                return true;
            case "12/28":
                GUI.labelCutterLoginThread(jAlertLabel, "happy birthday linus torvalds!", 80, 80, 1600, main);
                return true;
            case "11/08":
                GUI.labelCutterLoginThread(jAlertLabel, "i-comit's " + (Main.year - 2021) + " year anniversary!", 80, 80, 1600, main);
                return true;
        }
        return false;
    }

    public static final String goWebsite(JLabel website, final String leadingStr, final String url, String text, boolean changeColor) {
        if (changeColor) {
            website.setText("<html>"
                    + leadingStr + "<a style=\"text-decoration:none\" text=\"rgb(187,187,187)\" href=\"\">"
                    + text + "</a><span text=\"rgb(187,187,187)\" style=\"white-space: nowrap\"><font size=\"2\">" + "®" + "</font></span></html>");
        } else {
            website.setText("<html>" + leadingStr + "<a style=\"text-decoration:none\" href=\"\">" + text + "</a></html>");
        }
        if (!url.equals("")) {
            website.setCursor(new Cursor(Cursor.HAND_CURSOR));
            website.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (URISyntaxException | IOException ex) {
                        //It looks like there's a problem
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (changeColor) {
                        website.setText("<html>"
                                + leadingStr + "<a style=\"text-decoration:none\" text=\"rgb(107,107,107)\" href=\"\">"
                                + text + "</a><span text=\"rgb(107,107,107)\" style=\"white-space: nowrap\"><font size=\"2\">" + "®" + "</font></span></html>");
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (changeColor) {
                        website.setText("<html>"
                                + leadingStr + "<a style=\"text-decoration:none\" text=\"rgb(187,187,187)\" href=\"\">"
                                + text + "</a><span text=\"rgb(187,187,187)\" style=\"white-space: nowrap\"><font size=\"2\">" + "®" + "</font></span></html>");
                    }
                }
            });
        }
        return website.getText();
    }

    public static byte[][] splitArrToByteArr(List<byte[]> byteArrList) {
        String objStr = byteArrList.toString().substring(1, byteArrList.toString().length() - 1);
        System.out.println(objStr);
        String[] stringArr = objStr.split(", ", 2);
        byte[][] byteArr = {stringArr[0].getBytes(), stringArr[1].getBytes()};
        return byteArr;
    }

    public static FileTime byteArrToFileTime(byte[] fileTimeArr) {
        long milis;
        FileTime fileTime = null;
        String str = new String(fileTimeArr, StandardCharsets.UTF_8);
        try {
            if (str.length() == 2) {
            }
            switch (str.length()) {
                case 20:
                    milis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(str).getTime();
                    fileTime = FileTime.fromMillis(milis);
                    break;
                case 22:
                    milis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").parse(str).getTime();
                    fileTime = FileTime.fromMillis(milis);
                    break;
                case 23:
                    milis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(str).getTime();
                    fileTime = FileTime.fromMillis(milis);
                    break;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileTime;
    }

    public static String stringToHex(String str) {
        StringBuffer sb = new StringBuffer();
        //Converting string to character array
        char ch[] = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        String result = sb.toString();
        return result;
    }

    public static String hexToString(String str) {
        String result = new String();
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i = i + 2) {
            String st = "" + charArray[i] + "" + charArray[i + 1];
            char ch = (char) Integer.parseInt(st, 16);
            result = result + ch;
        }
        return result;
    }

    public static String getCurrentTime() {
        Instant instant = Instant.now();
        System.out.println(instant);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a")
                .withZone(ZoneId.systemDefault());

        String formattedInstant = formatter.format(instant);

        return formattedInstant;

    }
}
