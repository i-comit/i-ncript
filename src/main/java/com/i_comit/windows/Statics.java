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
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Statics {

    public static String folderName = "i-ncript";
    public static String rootFolder = Main.root + folderName;
    public static String keyName = "\\i-ncript.key";
    
    public static int AESMode;
    
    public static String GB;

    public static boolean emptyDirectory;
    public static boolean fileHiderBool = false;

    public static File f = new File(root);
    public static File directory = new File(root + folderName);
    public static File keyFile = Paths.get(root.substring(0,3)+Main.masterFolder + "app\\"+ keyName).toFile();
    
    public static File[] contents = directory.listFiles();

    public static Path path = Paths.get(root + folderName);
    
    public static String username="";
    public static String password = "";

    public static int fileCount;
    public static int fileIter;
    
    public static int fileHideIter;
}
