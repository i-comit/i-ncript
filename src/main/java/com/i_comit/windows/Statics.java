/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.root;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Statics {

    public static int toolMode = 0;

    public static String folderName = "i-ncript";
    public static String rootFolder = root.substring(0, 3) + folderName;
    public static String keyName = "\\i-ncript.key";

    public static int maxFileBytes = 1048576 * 4;

    public static int AESMode;
    public static String GB;

    public static boolean emptyDirectory;
    public static boolean fileHiderBool = false;

    public static File directory = new File(root.substring(0, 3) + Main.masterFolder + folderName);
    public static File keyFile = Paths.get(root.substring(0, 3) + Main.masterFolder + keyName).toFile();

    public static File[] contents = null;

    public static Path path = Paths.get(root.substring(0, 3) + Main.masterFolder + folderName);
    public static Path sendFolder = Paths.get(Main.root.substring(0, 3) + Main.masterFolder + "o-box");
    public static Path receiveFolder = Paths.get(Main.root.substring(0, 3) + Main.masterFolder + "n-box");

    public static String username = "";
    public static String password = "";

    public static String recipientUsername = "";
    public static String recipientPassword = "";

    public static String zipFileName = "";
    public static int zipFileIndex;

    public static int fileCount;
    public static int fileIter;

    public static int fileHideIter;
}
