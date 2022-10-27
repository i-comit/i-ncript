/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Main.root;
import static com.i_comit.windows.Statics.GB;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JLabel;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class GUI {

    public static Thread t;
    public static Thread t1;
    public static Thread t2;

    public static void getGB() {
        File diskPartition = new File(root).toPath().getRoot().toFile();
        GB = Heap.humanReadableByteCountBin(diskPartition.getUsableSpace());
        Main.jLabel3.setText(root.substring(0, 2) + " | " + GB);

    }

    public static void labelCutterThread(JLabel jLabel, String labelMsg, int initSleep, int sleep, int pause) {
        t = new Thread(() -> labelCutter_T.labelCutter_T(jLabel, labelMsg, initSleep, sleep, pause));
        t.start();
    }

    public static void loggerThread(File outputFile) {
        t1 = new Thread(() -> {
            logger_T.logger_T(outputFile);
        });
        t1.start();
    }

//    public static void progressBarThread() {
//        t2 = new Thread(() -> progressBar_T.resetProgressBar());
//        t2.start();
//
//    }
    public static int countAllFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).count());
        }
        return result;
    }

    public static int countFiles(Path path) throws IOException {
        int result = 0;
        try ( Stream<Path> walk = Files.walk(path)) {
            int result2 = 0;
            switch (Statics.AESMode) {
                case 0 -> {
                    result2 = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc")).filter(p -> !p.getFileName().toString().startsWith("Thumbs.db")).count());
                    result = result2;
                }
                case 1 -> {
                    result2 = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".enc")).count());
                    result = result2;
                }
            }
        }
        return result;
    }

    public static void resetProgressBar() {
        Main.jProgressBar1.setString("100% | " + AES_T.paths.size() + "/" + AES_T.paths.size());
        jProgressBar1.setValue(jProgressBar1.getMaximum());
        Main.jButton2.setVisible(false);
        try {
            switch (Statics.AESMode) {
                case 0 -> {
                    GUI.labelCutterThread(jAlertLabel, "encrypted " + Statics.fileIter + " files", 10, 25, 400);
                    Thread.sleep(100);
                    Main.jTextArea1.append("encrypted " + Statics.fileIter + " files at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")));
                }
                case 1 -> {
                    GUI.labelCutterThread(jAlertLabel, "decrypted " + Statics.fileIter + " files", 10, 25, 400);
                    Thread.sleep(100);
                    Main.jTextArea1.append("decrypted " + Statics.fileIter + " files at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")));
                }
            }
            Thread.sleep(150);

            for (int x = jProgressBar1.getMaximum(); x >= 0; x--) {
                Thread.sleep(5);
                jProgressBar1.setValue(x);
            }
            if (jProgressBar1.getValue() == 0) {
                Statics.fileIter = 0;
                Statics.fileCount = 0;
                jProgressBar1.setValue(Statics.fileIter);
                jProgressBar1.setStringPainted(false);
                FileHider.FileHiderThread(Main.jToggleButton2.isSelected());
            }
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }

};

class progressBar_T implements Runnable {

    public int threadIterator;

    public void run() {
//        try {
//            progressBar();
//        } catch (InterruptedException ex) {
//            //ex.printStackTrace();
//            System.exit(0);
//        } catch (IOException | UncheckedIOException ex) {
//            System.out.println("USB disconnected");
//        }
    }

}

class labelCutter_T implements Runnable {

    public void run() {

    }

    //Thread-4
    public static void labelCutter_T(JLabel jLabel, String labelMsg, int initSleep, int sleep, int pause) {
        jLabel.setText("");
        int msgL = labelMsg.length();
        try {
            Thread.sleep(initSleep);
            for (int i = 0; i <= msgL; i++) {
                //labelMsg = "";
                CharSequence cutLabel = labelMsg.subSequence(0, i);
                jLabel.setText(cutLabel.toString());
                Thread.sleep(sleep);
            }
            Thread.sleep(pause);
            for (int i = msgL; i >= 0; i--) {
                CharSequence cutLabel = labelMsg.subSequence(0, i);
                jLabel.setText(cutLabel.toString());
                Thread.sleep(sleep);
            }
        } catch (InterruptedException ex) {
//            ex.printStackTrace();
            System.out.println("label thread interrupted.");
        }
    }
}

class logger_T implements Runnable {

    public void run() {

    }

    public static void logger_T(File outputFile) {
        try {
            Thread.sleep(50);
            Main.jTextArea1.append(outputFile.getAbsolutePath().substring(20, outputFile.getPath().length()) + "\n");
            Thread.sleep(50);
            Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());
        } catch (InterruptedException ex) {
//            ex.printStackTrace();
            System.out.println("logger thread interrupted.");
        }
    }
}
