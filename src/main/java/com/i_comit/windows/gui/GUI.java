/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.gui;

import static com.i_comit.windows.gui.Main.jProgressBar1;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class GUI implements ActionListener {

    public static void GUIThread() {
        GUI_T guiThread = new GUI_T();
        //guiThread.threadIterator = 0;
        Thread t1 = new Thread(guiThread);
        t1.start();
    }

    public void actionPerformed(ActionEvent e) {
        String str = e.getActionCommand();
        switch (str) {
            case "encrypted":
                System.out.println(0);
                break;
            case "decrypted":
                System.out.println(1);

                break;
            case "enter":
                System.out.println(2);
                break;

            default:
            // code block
        }
        System.out.println("Clicked = " + str);
    }

    public static void progressBar() throws InterruptedException {
        Globals.fileIter = 0;
        try {
            Globals.fileCount = countFiles(Globals.path);
            jProgressBar1.setMaximum(Globals.fileCount);
            System.out.println("File Count: " + countFiles(Globals.path));
            for (int i = 0; i < Globals.fileCount; i++) {
                Thread.sleep(50);
                Globals.fileIter++;
                System.out.println(Globals.fileIter);
                jProgressBar1.setValue(Globals.fileIter);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int countFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).count());
        }
        return result;
    }

};

class GUI_T implements Runnable {

    public int threadIterator;

    public void run() {
        try {
            GUI.progressBar();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Path> listFiles(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static List<Path> listNewFiles(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".enc"))
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static void getLastModified() throws IOException {
        List<Path> paths = listNewFiles(Globals.path);
        paths.forEach(x -> System.out.println(x));
    }
}
