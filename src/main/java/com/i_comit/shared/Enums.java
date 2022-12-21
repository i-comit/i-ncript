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

    public static void main(String[] args) {

        System.out.println(Requests.SESS_GET);
        System.out.println(getOS());
//        if (OSStr.contains("win")) {
//            OS os = OS.windows;
//            System.out.println(OS.windows);
//        } else if (OSStr.contains("mac")) {
//            System.out.println("This is MacOS");
//        } else if (OSStr.contains("nix") || OSStr.contains("nux") || OSStr.contains("aix")) {
//            System.out.println("This is Unix or Linux");
//        } else if (OSStr.contains("sunos")) {
//            System.out.println("This is Solaris");
//        } else {
//            System.out.println("This OS is not supported");
//        }
//        if(os )
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
