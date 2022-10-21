/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.linux;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Statics {

    public static String root = "";
    public static String folderName = "/i-ncript";
    public static String rootFolder = root + folderName;
    public static String keyName = "/i-ncript.key";

    public static Runtime env = Runtime.getRuntime();

    public static int AESMode;

    public static long GB;

    public static boolean emptyDirectory;
    public static boolean fileHiderBool = false;

    public static File f = new File(root);
    public static File directory = new File(root + folderName);
    public static File keyFile = Paths.get(root + keyName).toFile();

    public static File[] contents = directory.listFiles();

    public static Path path = Paths.get(root + folderName);
    public static Path zipPath = Paths.get(root + folderName + ".zip");
    public static Path rootPath = Paths.get(root);

    public static String username = "";
    public static String password = "";

    public static int fileCount;
    public static int fileIter;

    public static int fileHideIter;
}