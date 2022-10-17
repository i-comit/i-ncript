/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES_T.listPaths;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Statics.GB;
import static com.i_comit.windows.Statics.directory;
import static com.i_comit.windows.Statics.path;
import static com.i_comit.windows.Statics.root;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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

    public static Thread t;
    public static Thread t1;

    public static void getGB() {
        File diskPartition = new File(root);
        GB = diskPartition.getUsableSpace() / (1024 * 1024 * 1024);
    }

    public static void labelCutterThread(JLabel jLabel, String labelMsg, int initSleep, int sleep, int pause) {
        t = new Thread(() -> labelCutter_T.labelCutter_T(jLabel, labelMsg, initSleep, sleep, pause));
        t.start();
    }

    public static void progressBarThread() {
        progressBar_T pgThread = new progressBar_T();
        t1 = new Thread(pgThread);
        t1.start();

    }

    public static int countFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).count());
//            int result2 = 0;
//            switch (Statics.AESMode) {
//                case 0 -> {
//                    result2 = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc")).count());
//                    result = result2;
//                }
//                case 1 -> {
//                    result2 = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".enc")).count());
//                    result = result2;
//                }
//            }
        }
        return result;
    }

    public static int countFiles2(Path path) throws IOException {
        int result = 0;
        try ( Stream<Path> walk = Files.walk(path)) {
            int result2 = 0;
            switch (Statics.AESMode) {
                case 0 -> {
                    result2 = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc")).count());
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
};

class progressBar_T implements Runnable {

    public int threadIterator;

    public void run() {
        try {
            progressBar();
        } catch (InterruptedException ex) {
            //ex.printStackTrace();
            System.exit(0);
        } catch (IOException | UncheckedIOException ex) {
            System.out.println("USB disconnected");
        }
    }

    public static void progressBar() throws InterruptedException, IOException {

        Statics.fileIter = 0;
        //System.out.println("File Count from GUI progress Bar: " + Statics.fileCount);
        jProgressBar1.setStringPainted(true);
        while (jProgressBar1.isStringPainted()) {
            try {
                jProgressBar1.setMaximum(GUI.countFiles(Statics.path));
                List<Path> paths = listPaths(path);
                File[] contents = directory.listFiles();

                if (contents != null) {
                    if (contents.length != 0) {
                        if (!paths.isEmpty()) {
                            if (jProgressBar1.getValue() == Statics.fileCount) {
                                Thread.sleep(100);

                                switch (Statics.AESMode) {
                                    case 0 -> {
                                        AES.t.interrupt();

                                        jProgressBar1.setMaximum(100);
                                        jProgressBar1.setValue(100);
                                        GUI.labelCutterThread(jAlertLabel, "encryption of " + Statics.fileCount + " files complete", 20, 20, 600);
                                        Thread.sleep(600);

                                        for (int x = 100; x >= 0; x--) {
                                            Thread.sleep(10);
                                            jProgressBar1.setValue(x);
                                        }
                                        jProgressBar1.setStringPainted(false);
                                        Statics.fileIter = 0;
                                        jProgressBar1.setValue(0);
                                        Thread.currentThread().interrupt();

                                    }
                                    case 1 -> {
                                        AES.t.interrupt();

                                        jProgressBar1.setMaximum(100);
                                        jProgressBar1.setValue(100);
                                        GUI.labelCutterThread(jAlertLabel, "decryption of " + Statics.fileCount + " files complete", 20, 20, 600);
                                        Thread.sleep(600);
                                        for (int x = 100; x >= 0; x--) {
                                            Thread.sleep(10);
                                            jProgressBar1.setValue(x);
                                        }
//                        
                                        jProgressBar1.setStringPainted(false);
                                        Statics.fileIter = 0;
                                        jProgressBar1.setValue(0);
                                        Thread.currentThread().interrupt();

                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException | UncheckedIOException ex) {
                System.out.println("USB disconnected");
                System.exit(0);
            }
            if (!jProgressBar1.isStringPainted()) {
                break;
            }
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
            //ex.printStackTrace();

        }
    }

//    }
}
