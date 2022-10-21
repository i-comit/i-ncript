/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Statics {

    public enum OS {
        WINDOWS,
        UNIX,
        POSIX_UNIX,
        MAC,
        OTHER;

        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public static String root = "";
    public static String folderName = "";
    public static String keyName = "";
    public static String rootFolder = "";
    public static File keyFile = null;
    public static Path path = null;

    public static String getOperatingSystem() {
        String os = System.getProperty("os.name");
        System.out.println("Using System Property: " + os);
        Path currentRelativePath = Paths.get("");
//        root = currentRelativePath.toAbsolutePath().toString();      
        root = "/media/gluong/5250-0D81";
        rootFolder = root+folderName;
        path = Paths.get(root+folderName);
        GUI.getGB();

        if (os.equals("Linux")) {
            keyName = "/i-ncript.key";
            folderName = "/i-ncript";
            keyFile = Paths.get(root + keyName).toFile();
            Main.jLabel3.setText(GB + "GB");
            if (!new File(root+folderName).exists()) {
                GUI.labelCutterThread(Main.jAlertLabel, "i-ncript folder created", 60, 50, 1800);
                new File(root+folderName).mkdir();
            } else {
                GUI.labelCutterThread(Main.jAlertLabel, "developed by i-comit", 60, 50, 1800);
            }
        }
        if (os.equals("Windows")) {
            keyName = "\\i-ncript.key";
            folderName = "i-ncript";
            keyFile = Paths.get(root + keyName).toFile();
            Main.jLabel3.setText(root.substring(0, 2).toUpperCase() + " | " + GB + "GB");
            if (!path.toFile().exists()) {
                GUI.labelCutterThread(Main.jAlertLabel, "i-ncript folder created", 60, 50, 1800);
                path.toFile().mkdir();
            } else {
                GUI.labelCutterThread(Main.jAlertLabel, "developed by i-comit", 60, 50, 1800);
            }
        }
        return os;

    }

    public static int AESMode;

    public static long GB;

    public static boolean emptyDirectory;
    public static boolean fileHiderBool = false;

    public static File f = new File(root);
    public static File directory = new File(root + folderName);

    public static File[] contents = directory.listFiles();

    public static Path zipPath = Paths.get(root + folderName + ".zip");
    public static Path rootPath = Paths.get(root);

    public static String username = "";
    public static String password = "";

    public static int fileCount;
    public static int fileIter;

    public static int fileHideIter;
}
