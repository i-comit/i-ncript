/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.i_comit.shared;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Enums {

    public static enum OS {
        windows,
        macos,
        linux,
        solaris,
        unsupported;
    }

    public static enum Requests {
        USER_GET,
        FILE_GET,
        FILE_POST,
        TBL_GET,
        SESS_GET,
        SESS_POST,
        SESS_DEL,

    }

    public static byte[] requestCode(Requests requests) {
        String requestStr = requests.toString();
        System.out.println(requestStr);
        byte[] requestCode_B = requestStr.getBytes();
        System.out.println(requestCode_B);
        return requestCode_B;
    }

    public static String getOS() {
        String OSStr = System.getProperty("os.name").toLowerCase();
        OS os = null;
        if (OSStr.contains("win")) {
            os = OS.windows;
        } else if (OSStr.contains("mac")) {
            os = OS.macos;
        } else if (OSStr.contains("nix") || OSStr.contains("nux") || OSStr.contains("aix")) {
            os = OS.linux;
        } else if (OSStr.contains("sunos")) {
            os = OS.solaris;
        } else {
            os = OS.unsupported;
        }
        String osStr = os.toString().toUpperCase();
        return osStr;
    }
}
