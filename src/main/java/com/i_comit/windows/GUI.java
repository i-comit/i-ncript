/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.root;
import static com.i_comit.windows.Statics.AESMode;
import static com.i_comit.windows.Statics.GB;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class GUI {

    public static Thread t;
    public static Thread tb;
    public static Thread t1;
    public static Thread t2;

    public static void getGB() {
        File diskPartition = new File(root).toPath().getRoot().toFile();
        GB = Memory.byteFormatter(diskPartition.getUsableSpace());
        Main.jLabel3.setText(root.substring(0, 2) + " " + GB);
    }

    public static void labelCutterThread(JLabel jLabel, String labelMsg, int initSleep, int sleep, int pause, boolean stayAlive) {
        t = new Thread(() -> labelCutter_T.labelCutter_T(jLabel, labelMsg, initSleep, sleep, pause, stayAlive));
        t.start();
    }

    public static void labelCutterTreeThread(JLabel jLabel, String labelMsg, int initSleep, int sleep, int pause, boolean stayAlive) {
        tb = new Thread(() -> labelCutterTree_T.labelCutterTree_T(jLabel, labelMsg, initSleep, sleep, pause, stayAlive));
        tb.start();
    }

    public static void loggerThread(File outputFile, int toolMode) {
        t1 = new Thread(() -> {
            logger_T.logger_T(outputFile, toolMode);
        });
        t1.start();
    }

    public static int countAllFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".i-cc"))
                    .filter(p -> !p.getFileName().toString().startsWith("Thumbs.db"))
                    .count());
        }
        return result;
    }

    public static int countFiles(Path path) throws IOException {
        int result = 0;
        try ( Stream<Path> walk = Files.walk(path)) {
            int result2 = 0;
            switch (Statics.AESMode) {
                case 0:
                    result2 = Math.toIntExact(walk.filter(Files::isRegularFile)
                            .filter(p -> !p.getFileName().toString().endsWith(".enc"))
                            .filter(p -> !p.getFileName().toString().endsWith(".i-cc"))
                            .filter(p -> !p.getFileName().toString().startsWith("Thumbs.db"))
                            .count());
                    result = result2;
                    break;
                case 1:
                    result2 = Math.toIntExact(walk.filter(Files::isRegularFile)
                            .filter(p -> p.getFileName().toString().endsWith(".enc"))
                            .filter(p -> !p.getFileName().toString().endsWith(".i-cc"))
                            .filter(p -> !p.getFileName().toString().startsWith("Thumbs.db"))
                            .count());
                    result = result2;
                    break;
            }
        }
        return result;
    }

    public static List<Path> listDirs(Path path) throws IOException {
        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isDirectory)
                    .collect(Collectors.toList());
            return result;
        }
    }

    public static List<Path> listPaths(Path path) throws IOException {
        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().startsWith("Thumbs.db"))
                    .filter(p -> !p.getFileName().toString().endsWith(".i-cc"))
                    .collect(Collectors.toList());
            return result;
        }
    }

    public static List<Path> listAESPaths(Path path) throws IOException {
        List<Path> result = null;
        try ( Stream<Path> walk = Files.walk(path)) {
            switch (AESMode) {
                case 0:
                    result = walk.filter(Files::isRegularFile)
                            .filter(p -> !p.getFileName().toString().endsWith(".enc"))
                            .filter(p -> !p.getFileName().toString().startsWith("Thumbs.db"))
                            .filter(p -> !p.getFileName().toString().endsWith(".i-cc"))
                            .collect(Collectors.toList());
                    break;
                case 1:
                    result = walk.filter(Files::isRegularFile)
                            .filter(p -> p.getFileName().toString().endsWith(".enc"))
                            .filter(p -> !p.getFileName().toString().startsWith("Thumbs.db"))
                            .filter(p -> !p.getFileName().toString().endsWith(".i-cc"))
                            .collect(Collectors.toList());
                    break;
            }
        }
        return result;
    }

    public static void resetIncorrectKeyProgressBar(JProgressBar progressBar) {
        progressBar.setMaximum(2);
        progressBar.setValue(progressBar.getMaximum());
        for (int x = progressBar.getMaximum(); x >= 0; x--) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            progressBar.setValue(x);
            if (x <= 1) {
                Main.progressbarBool = true;
            }
        }
    }

    public static void resetProgressBar(JProgressBar progressBar) {
        progressBar.setValue(progressBar.getMaximum());
        Main.jButton2.setVisible(false);
        Main.jSwitchMode.setVisible(true);
        if (progressBar == Main.jProgressBar1) {
            progressBar.setString("100% | " + AES_T.paths.size() + "/" + AES_T.paths.size());
            try {
                AudioPlayer.audioPlayerThread("ding-sfx.wav");
                switch (Statics.AESMode) {
                    case 0:
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "encrypted " + Statics.fileIter + " files", 10, 20, 400, false);
                        Main.jTextArea1.append("encrypted " + Statics.fileIter + " files at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n");
                        break;
                    case 1:
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "decrypted " + Statics.fileIter + " files", 10, 20, 400, false);
                        Main.jTextArea1.append("decrypted " + Statics.fileIter + " files at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n");
                        break;
                }
                Thread.sleep(200);
                if (Statics.fileIter <= 100) {
                    for (int x = progressBar.getMaximum(); x >= 0; x--) {
                        Thread.sleep(4);
                        progressBar.setValue(x);
                        if (x <= 1) {
                            Main.progressbarBool = true;
                        }
                    }
                } else {
                    for (int x = progressBar.getMaximum(); x >= 0; x--) {
                        Thread.sleep(2);
                        progressBar.setValue(x);
                        if (x <= 1) {
                            Main.progressbarBool = true;
                        }
                    }
                }

                if (progressBar.getValue() == 0) {
                    progressBar.setStringPainted(false);
                    switch (Statics.toolMode) {
                        case 0:
                            FileHider.FileHiderThread(Main.jToggleButton2.isSelected(), Statics.path);
                            break;
                        case 1:
                            FileHider.FileHiderThread(Main.jToggleButton2.isSelected(), Statics.receiveFolder);
                            break;
                        case 2:
                            FileHider.FileHiderThread(Main.jToggleButton2.isSelected(), Statics.sendFolder);
                            break;
                        case 3:
                            FileHider.FileHiderThread(Main.jToggleButton2.isSelected(), Statics.path);
                            break;
                    }
                }

            } catch (InterruptedException | IOException ex) {
                ex.printStackTrace();
            }
        }
        if (progressBar == Main.jProgressBar2) {
            try {
                String fileName = new File(Folder.sendFolderStr).getName();
                if (Statics.toolMode == 2) {
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "packaged " + fileName + ".i-cc", 10, 25, 500, false);
                    Main.jTextArea1.append("packaged " + fileName + ".i-cc at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n");
                }
                if (Statics.toolMode == 1) {
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "unpacked " + fileName + ".i-cc", 10, 25, 500, false);
                    Main.jTextArea1.append("unpacked " + fileName + ".i-cc at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n");
                }
                Thread.sleep(800);
                for (int x = progressBar.getMaximum(); x >= 0; x--) {
                    Thread.sleep(4);
                    progressBar.setValue(x);
                    if (x <= 1) {
                        Main.progressbarBool = true;
                    }
                }
                if (progressBar.getValue() >= 0) {
                    progressBar.setStringPainted(false);
                    Main.jTabbedPane1.setSelectedIndex(0);
                    Main.jProgressBar2.setVisible(true);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String formatDateTime(FileTime fileTime) {
        LocalDateTime localDateTime = fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yy HH:mm"));
    }
}

class labelCutter_T implements Runnable {

    public void run() {

    }

    public static void labelCutter_T(JLabel jLabel, String labelMsg, int initSleep, int sleep, int pause, boolean stayAlive) {
        jLabel.setText("");
        int msgL = labelMsg.length();
        try {
            Thread.sleep(initSleep);
            for (int i = 0; i <= msgL; i++) {
                CharSequence cutLabel = labelMsg.subSequence(0, i);
                jLabel.setText(cutLabel.toString());
                Thread.sleep(sleep);
            }
            if (!stayAlive) {
                Thread.sleep(pause);
                for (int i = msgL; i >= 0; i--) {
                    CharSequence cutLabel = labelMsg.subSequence(0, i);
                    jLabel.setText(cutLabel.toString());
                    Thread.sleep(sleep);
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("label thread interrupted.");
        }

    }
}

class labelCutterTree_T implements Runnable {

    public void run() {

    }

    public static void labelCutterTree_T(JLabel jLabel, String labelMsg, int initSleep, int sleep, int pause, boolean stayAlive) {
        jLabel.setText("");
        int msgL = labelMsg.length();
        try {
            Thread.sleep(initSleep);
            for (int i = 0; i <= msgL; i++) {
                CharSequence cutLabel = labelMsg.subSequence(0, i);
                jLabel.setText(cutLabel.toString());
                Thread.sleep(sleep);
            }
            if (!stayAlive) {
                Thread.sleep(pause);
                for (int i = msgL; i >= 0; i--) {
                    CharSequence cutLabel = labelMsg.subSequence(0, i);
                    jLabel.setText(cutLabel.toString());
                    Thread.sleep(sleep);
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("label thread interrupted.");
        }

    }
}

class logger_T implements Runnable {

    public void run() {

    }

    public static void logger_T(File outputFile, int toolMode) {
        try {
            Thread.sleep(40);
            if (!Statics.dragDropBool) {
                switch (toolMode) {
                    case 0:
                        Main.jTextArea1.append(outputFile.getAbsolutePath().substring(21, outputFile.getPath().length()) + "\n");
                        break;
                    case 1:
                        Main.jTextArea1.append(outputFile.getAbsolutePath().substring(18, outputFile.getPath().length()) + "\n");
                        break;
                    case 2:
                        Main.jTextArea1.append(outputFile.getAbsolutePath().substring(18, outputFile.getPath().length()) + "\n");
                        break;
                }
            } else {
                Main.jTextArea1.append(outputFile.getAbsolutePath() + "\n");
            }
            Thread.sleep(40);
            Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());
        } catch (InterruptedException ex) {
//            ex.printStackTrace();
            System.out.println("logger thread interrupted.");
        }
    }
}
