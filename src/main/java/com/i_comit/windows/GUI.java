/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES_T.listPaths;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Statics.GB;
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
        GB = Heap.humanReadableByteCountBin(diskPartition.getUsableSpace());
    }

    public static void labelCutterThread(JLabel jLabel, String labelMsg, int initSleep, int sleep, int pause) {
        t = new Thread(() -> labelCutter_T.labelCutter_T(jLabel, labelMsg, initSleep, sleep, pause));
        t.start();
    }

    public static void loggerThread(File outputFile) {
        t = new Thread(() -> {
            try {
                logger_T.logger_T(outputFile);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
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
        jProgressBar1.setStringPainted(true);
        Main.jToggleButton1.setEnabled(false);
        Main.jToggleButton2.setEnabled(false);
        while (jProgressBar1.isStringPainted()) {
            try {
//                jProgressBar1.setMaximum(GUI.countFiles(Statics.path));
                List<Path> paths = listPaths(path);
                List<Path> paths2 = AES_T.listAESPaths(path);
                if (!paths2.isEmpty()) {
                    if (Statics.contents != null) {
                        if (Statics.contents.length != 0) {
                            if (!paths.isEmpty()) {
                                if (Statics.fileIter == Statics.fileCount - 1) {
                                    switch (Statics.AESMode) {
                                        case 0 -> {
                                            jProgressBar1.setMaximum(Statics.fileCount);
                                            GUI.labelCutterThread(jAlertLabel, "encryption of " + Statics.fileCount + " files complete", 10, 15, 360);
                                            Thread.sleep(280);
//                                            FileHider.FileHiderThread(true);
//                                            jProgressBar1.setValue(100);
                                            for (int x = Statics.fileCount; x >= 0; x--) {
                                                Thread.sleep(4);
                                                jProgressBar1.setValue(x);
                                            }
                                            jProgressBar1.setStringPainted(false);
                                            Statics.fileIter = 0;
                                            jProgressBar1.setValue(Statics.fileIter);
                                            if (!Main.jToggleButton1.isSelected()) {
                                                Main.jRadioButton0.setEnabled(true);
                                                Main.jRadioButton1.setEnabled(true);
                                                Main.jToggleButton1.setSelected(false);
                                            } else {
                                                Main.jToggleButton1.setSelected(true);
                                                Main.jRadioButton0.setEnabled(true);
                                                Main.jRadioButton1.setEnabled(true);
                                            }
                                            Main.jToggleButton2.setEnabled(true);
                                            Main.jTextArea1.setText("");
                                            FileHider.FileHiderThread(Main.jToggleButton2.isSelected());
                                            GUI.t1.interrupt();
                                        }
                                        case 1 -> {
                                            jProgressBar1.setMaximum(Statics.fileCount);
                                            GUI.labelCutterThread(jAlertLabel, "decryption of " + Statics.fileCount + " files complete", 10, 15, 300);
                                            Thread.sleep(280);

//                                            jProgressBar1.setValue(100);
                                            for (int x = Statics.fileCount; x >= 0; x--) {
                                                Thread.sleep(4);
                                                jProgressBar1.setValue(x);
                                            }
                                            jProgressBar1.setStringPainted(false);
                                            Statics.fileIter = 0;
                                            jProgressBar1.setValue(Statics.fileIter);
                                            Main.jRadioButton0.setEnabled(true);
                                            Main.jRadioButton1.setEnabled(true);
                                            Main.buttonGroup1.clearSelection();
                                            Main.jToggleButton2.setEnabled(true);
                                            Main.jTextArea1.setText("");
                                            FileHider.FileHiderThread(Main.jToggleButton2.isSelected());
                                            GUI.t1.interrupt();
                                        }
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
                Main.jToggleButton1.setEnabled(true);

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
}

class logger_T implements Runnable {

    public void run() {

    }

    public static void logger_T(File outputFile) throws InterruptedException {
        Thread.sleep(150);
        Main.jTextArea1.append(outputFile.getPath().substring(11, outputFile.getPath().toString().length()) + "\n");
//        for(int i =0; i<5;i++){
//            
//        }
        Thread.sleep(150);
        Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());
    }
}

//class KeyListener_C implements KeyListener {
//
//    public KeyListener_C() {
//
//    }
//
//    @Override
//    public void keyPressed(KeyEvent ke) {
////        lbl.setText("You have pressed " + ke.getKeyChar());
//        switch (ke.getKeyCode()) {
//            case KeyEvent.VK_ENTER:
//                System.out.println("Enter");
//                break;
//        }
//    }
//
//    @Override
//    public void keyTyped(KeyEvent ke) {
////        lbl.setText("You have typed " + ke.getKeyChar());
//    }
//
//    @Override
//    public void keyReleased(KeyEvent ke) {
////        lbl.setText("You have released " + ke.getKeyChar());
//        //System.out.println("amogus 3");
//    }
//
//    public static void main(String args[]) {
//        new KeyListener_C();
//    }
//}
