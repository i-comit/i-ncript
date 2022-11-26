/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.macos;

import static com.i_comit.macos.Main.jProgressBar2;
import static com.i_comit.macos.Main.masterFolder;
import static com.i_comit.macos.Main.root;
import static com.i_comit.macos.Memory.byteFormatter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;
import java.util.List;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Memory {

    static long totalMemory = Runtime.getRuntime().totalMemory();
    static int selectedHeap;
    static int currentHeap;

    public static String byteFormatter(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    public static String getDataSizePercentage() {
        File diskPartition = new File(root).toPath().toFile();
        long totalSpace = diskPartition.getTotalSpace();
        long remainingSpace = diskPartition.getUsableSpace();

        float percentage = ((float) remainingSpace / totalSpace * 100);
        DecimalFormat format = new DecimalFormat("0.#");
        String percentageStr = format.format(percentage) + "% memory left out of " + byteFormatter(totalSpace);
        return percentageStr;
    }

    public static boolean checkBash() {
        DriveCheck driveCheck = new DriveCheck();
        boolean b = false;
        String logicaldisk = "df -Hl";
        String s;
        try {
            Process process = Runtime.getRuntime().exec(logicaldisk);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.readLine();
                while ((s = reader.readLine()) != null) {
                    if (s.trim().length() != 0) {
                        String driveDir = s.substring(68).trim();
                        if (root.startsWith(driveDir)) {
                            System.out.println("driveDir " + driveDir);
                            String devPath = s.substring(0, 10).trim();
                            System.out.println("DEVPATH " + devPath);
                            //Check if device is an external USB
                            if (checkBash2(devPath)) {
                                //This means that the app is running inside a USB
                                String replacer = Main.root.replaceAll(driveDir, "");
                                String replacer2 = replacer.substring(0, 10);
                                Main.appBundle = replacer.substring(10);
                                System.out.println("REPLACER " + replacer);
                                System.out.println("REPLACER2 " + replacer2);
                                System.out.println("APP BUNDLE " + Main.appBundle);
//                                System.out.println("TEMP " + temp.substring(16, 26));
                                if (Main.root.contains(masterFolder)) {
                                    //The app has the masterFolder somewhere in it, next we will check if the masterFolder is in the right path
                                    if (replacer2.equals(masterFolder)) {
                                        root = driveDir;
                                        Statics.directory = new File(root + masterFolder + Statics.folderName);
                                        Statics.keyFile = Paths.get(root + masterFolder + Statics.keyName).toFile();
                                        Statics.path = Paths.get(root + masterFolder + Statics.folderName);
                                        Statics.sendFolder = Paths.get(root + Main.masterFolder + "o-box");
                                        Statics.receiveFolder = Paths.get(root + Main.masterFolder + "n-box");
                                        System.out.println("MAIN ROOT " + root);
                                        System.out.println("CWD " + Paths.get("").toAbsolutePath().toString());
                                        driveCheck.dispose();
                                        b = true;

                                    } else {
                                        System.out.println("-------- folder needs to be at USB root");
                                        driveCheck.setVisible(true);
                                        driveCheck.setDriveCheckText(4);
                                    }

                                } else {
                                    System.out.println("App needs to be in a folder called -------- ");
                                    driveCheck.setVisible(true);
                                    driveCheck.setDriveCheckText(3);
                                }
                                break;

                            } else {
                                driveCheck.setVisible(true);
                                driveCheck.setDriveCheckText(1);
                            }
                        } else {
                            driveCheck.setVisible(true);
                            driveCheck.setDriveCheckText(1);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static void getHeapSize() {
        File cfgFile = new File(root + File.separator + masterFolder + File.separator + Main.appBundle + File.separator + "Info.plist");
        if (cfgFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(cfgFile))) {
                String line = "";
                for (int i = 0; i <= 56; i++) {
                    line = br.readLine();
                    System.out.println(line);
                }
                System.out.println("LAST LINE " + line);
                String gb = line.substring(line.length() - 11, line.length() - 10);
                System.out.println("GB S " + gb);
                currentHeap = Integer.parseInt(gb);
                System.out.println("current heap is " + currentHeap);
                Main.jSlider1.setValue(currentHeap);
                Main.jHeapLabel.setText(currentHeap + "GB heap");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            Main.jHeapLabel.setVisible(false);
            Main.jSlider1.setVisible(false);
        }
    }

    public static void changeHeapSize() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(root + File.separator + masterFolder + File.separator + Main.appBundle + File.separator + "Info.plist"), StandardCharsets.UTF_8);
            for (String line : lines) {
                System.out.println(line);
            }
            System.out.println("LINE AT " + lines.get(56));
            lines.remove(56);

            lines.add(56, "\t\t<string>-Xms" + selectedHeap + "g</string>");
//            switch (selectedHeap) {
//                case 1:
//                    lines.remove(7);
//                    lines.add(7, "java-options=-Xmx" + 2 + "g");
//                    break;
//                case 2:
//                    lines.remove(7);
//                    lines.add(7, "java-options=-Xmx" + 4 + "g");
//                    break;
//                case 3:
//                    lines.remove(7);
//                    lines.add(7, "java-options=-Xmx" + 6 + "g");
//                    break;
//                case 4:
//                    lines.remove(7);
//                    lines.add(7, "java-options=-Xmx" + 8 + "g");
//                    break;
//                case 5:
//                    lines.remove(7);
//                    lines.add(7, "java-options=-Xmx" + 10 + "g");
//                    break;
//                case 6:
//                    lines.remove(7);
//                    lines.add(7, "java-options=-Xmx" + 12 + "g");
//                    break;
//                case 7:
//                    lines.remove(7);
//                    lines.add(7, "java-options=-Xmx" + 14 + "g");
//                    break;
//                case 8:
//                    lines.remove(7);
//                    lines.add(7, "java-options=-Xmx" + 16 + "g");
//                    break;
//
//            }
//            Files.write(Paths.get(root + File.separator + masterFolder + File.separator + Main.appBundle + File.separator + "Info.plist"), lines, StandardCharsets.UTF_8);
            try {
                ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-l", "cd /Volumes", "ls");
                pb.redirectError();
                String s = "";
                Process sh = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                    while ((s = reader.readLine()) != null) {
                        System.out.println(s);
                    }
                }
                System.out.println("???");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
//            System.exit(0);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static boolean checkBash2(String devPath) {
        String diskUtil = String.format("diskutil list | grep '%s'", devPath);
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", diskUtil);

        String s;
        try {
            Process sh = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                while ((s = reader.readLine()) != null) {
                    if (s.trim().length() != 0) {
                        if (s.trim().contains("external, physical")) {
                            System.out.println("drive is a USB");
                            return true;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void byteMonitor(InputStream inputStream, File inputFile) throws IOException {
        long maxFileSize = inputFile.length();
        long iterator = maxFileSize - inputStream.available();
        float percentage = ((float) iterator / maxFileSize * 100);
        DecimalFormat format = new DecimalFormat("0.#");
        String percentageStr = format.format(percentage);
        jProgressBar2.setMaximum((int) maxFileSize);
        jProgressBar2.setValue((int) iterator);
        jProgressBar2.setStringPainted(true);
        jProgressBar2.setString(percentageStr + "% | " + byteFormatter(iterator));

        if (inputStream.available() == 0) {
            try {
                Thread.sleep(50);
                jProgressBar2.setMaximum(50);
                jProgressBar2.setValue(Main.jProgressBar2.getMaximum());
                for (int x = jProgressBar2.getMaximum(); x >= 0; x--) {
                    Thread.sleep(5);
                    jProgressBar2.setValue(x);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (jProgressBar2.getValue() == 0) {
                maxFileSize = 0;
                iterator = 0;
                jProgressBar2.setStringPainted(false);
                Main.jAlertLabel.setText("");
                jProgressBar2.setVisible(false);
            }
        }
    }
}
