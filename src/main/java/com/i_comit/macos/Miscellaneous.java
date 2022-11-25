/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.i_comit.macos;

import static com.i_comit.windows.Main.jAlertLabel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Miscellaneous {

    public static boolean holidayCheck() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd"));
        System.out.println(today);
        switch (today) {
            case "01/01":
                GUI.labelCutterThread(jAlertLabel, "happy new year " + Main.year + "!", 80, 80, 100, true);
                return true;
            case "11/24":
                GUI.labelCutterThread(jAlertLabel, "happy thanksgiving " + Main.year + "!", 80, 80, 100, true);
                return true;
            case "12/25":
                GUI.labelCutterThread(jAlertLabel, "happy holidays " + Main.year + "!", 80, 80, 100, true);
                return true;
            case "12/28":
                GUI.labelCutterThread(jAlertLabel, "happy birthday linus torvalds!", 80, 80, 100, true);
                return true;
        }
        return false;
    }
}
