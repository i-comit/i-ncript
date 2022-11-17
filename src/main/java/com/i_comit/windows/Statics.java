/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.GUI.listAESPaths;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Main.root;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Statics {

    public static int toolMode = 0;

    public static String folderName = "i-ncript";
    public static String rootFolder = root.substring(0, 3) + Main.masterFolder + folderName;
    public static String keyName = "\\i-ncript.key";

    public static int maxFileBytes = 1048576 * 4;

    public static int AESMode;
    public static String GB;

    public static boolean fileHiderBool = false;
    public static boolean treeViewBool = false;

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
    public static int zipFileCount;
    public static int zipIter;

    public static int fileCount;
    public static int fileIter;

    public static int fileHideIter;

    public static void encryptFunction() {
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        AESMode = 0;
        jProgressBar1.setValue(0);
        jProgressBar1.setMaximum(0);
        try {
            fileCount = GUI.countFiles(path);
            jProgressBar1.setMaximum(fileCount);
            AES.AESThread(listAESPaths(path), directory, true, 0);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
