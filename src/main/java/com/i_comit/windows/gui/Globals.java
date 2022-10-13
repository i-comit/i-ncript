/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.gui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Globals {

    public static String root = "";
    public static String folderName = "i-ncript";
    public static String rootFolder = root+folderName;
    public static int queryMode;
    
    public static long GB;

    public static boolean emptyDirectory;

    public static File f = new File(root);

    public static File directory = new File(root + folderName);
    public static Path path = Paths.get(root + folderName);
    public static Path zipPath = Paths.get(root + folderName + ".zip");
    public static Path rootPath = Paths.get(root);
    public static String pw = "";
    public static String serialNumber = "";

    public static String encKeyString = "";
}
