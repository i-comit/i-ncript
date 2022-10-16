/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Statics.GB;
import static com.i_comit.windows.Statics.root;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JLabel;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class GUI {

    public static void GUIThread() {
        GUI_T guiThread = new GUI_T();
        Thread t1 = new Thread(guiThread);
        t1.start();
    }

    public static void getGB() {
        File diskPartition = new File(root);
        GB = diskPartition.getUsableSpace() / (1024 * 1024 * 1024);
    }

    public static void labelCutterThread(JLabel jLabel, String labelMsg, int sleep, int pause) {
        Thread t = new Thread(() -> labelCutter_T.labelCutter_T(jLabel, labelMsg, sleep, pause));
        t.start();

    }

    public static void progressBar() throws InterruptedException {
        Statics.fileIter = 0;
        try {
            Statics.fileCount = countFiles(Statics.path);
            jProgressBar1.setMaximum(Statics.fileCount);
            //System.out.println("File Count: " + countFiles(Statics.path));
            for (int i = 0; i < Statics.fileCount; i++) {
                Thread.sleep(50);
                Statics.fileIter++;
                jProgressBar1.setValue(Statics.fileIter);
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
}

class labelCutter_T implements Runnable {

    public void run() {

    }

    //Thread-4
    public static void labelCutter_T(JLabel jLabel, String labelMsg, int sleep, int pause) {
        jLabel.setText("");
        int msgL = labelMsg.length();
        try {
            Thread.sleep(150);
            for (int i = 0; i <= msgL; i++) {
                //labelMsg = "";
                CharSequence cutLabel = labelMsg.subSequence(0, i);
                //System.out.println(cutLabel.toString());
                jLabel.setText(cutLabel.toString());

                Thread.sleep(sleep);
            }
            Thread.sleep(pause);
            for (int i = msgL; i >= 0; i--) {
                CharSequence cutLabel = labelMsg.subSequence(0, i);
                //System.out.println(cutLabel.toString());
                jLabel.setText(cutLabel.toString());
                Thread.sleep(sleep);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
