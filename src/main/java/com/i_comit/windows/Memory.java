/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jProgressBar2;
import static com.i_comit.windows.Main.root;
import static com.i_comit.windows.Memory.byteFormatter;
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
        File diskPartition = new File(root).toPath().getRoot().toFile();
        long totalSpace = diskPartition.getTotalSpace();
        long remainingSpace = diskPartition.getUsableSpace();

        float percentage = ((float) remainingSpace / totalSpace * 100);
        DecimalFormat format = new DecimalFormat("0.#");
        String percentageStr = format.format(percentage) + "% memory left out of " + byteFormatter(totalSpace);
        return percentageStr;
    }

    public static void getHeapSize(Main main) {
        File cfgFile = new File(root + Main.masterFolder + "\\app\\i-ncript.cfg");
        if (cfgFile.exists()) {
            try ( BufferedReader br = new BufferedReader(new FileReader(cfgFile))) {
                String line = "";
                for (int i = 0; i < 7; i++) {
                    line = br.readLine();
                }
                currentHeap = Integer.parseInt(line.substring(line.length() - 2, line.length() - 1));
                System.out.println("current heap is " + currentHeap);
                Main.jSlider1.setValue(currentHeap);
                Main.jHeapLabel.setText(currentHeap + "GB heap");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Main.jAlertLabel.setLocation(21, 154);
            main.setSize(120, 241);
        } else {
            Main.jHeapLabel.setVisible(false);
            Main.jSlider1.setVisible(false);
            Main.jAlertLabel.setLocation(21, 154);
            main.setSize(120, 211);

        }
    }

    public static void changeHeapSize() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(root + Main.masterFolder + "\\app\\i-ncript.cfg"), StandardCharsets.UTF_8);
            lines.remove(6);
            lines.add(6, "java-options=-Xms" + selectedHeap + "g");
            switch (selectedHeap) {
                case 1:
                    lines.remove(7);
                    lines.add(7, "java-options=-Xmx" + 2 + "g");
                    break;
                case 2:
                    lines.remove(7);
                    lines.add(7, "java-options=-Xmx" + 4 + "g");
                    break;
                case 3:
                    lines.remove(7);
                    lines.add(7, "java-options=-Xmx" + 6 + "g");
                    break;
                case 4:
                    lines.remove(7);
                    lines.add(7, "java-options=-Xmx" + 8 + "g");
                    break;
                case 5:
                    lines.remove(7);
                    lines.add(7, "java-options=-Xmx" + 10 + "g");
                    break;
                case 6:
                    lines.remove(7);
                    lines.add(7, "java-options=-Xmx" + 12 + "g");
                    break;
                case 7:
                    lines.remove(7);
                    lines.add(7, "java-options=-Xmx" + 14 + "g");
                    break;
                case 8:
                    lines.remove(7);
                    lines.add(7, "java-options=-Xmx" + 16 + "g");
                    break;

            }
            Files.write(Paths.get(root + Main.masterFolder + "\\app\\i-ncript.cfg"), lines, StandardCharsets.UTF_8);
            Runtime.getRuntime().exec("cmd /c " + root + Main.masterFolder + "i-ncript.exe");
            System.exit(0);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void getUSBName(Main main) {
        String usbName = "wmic logicaldisk where name=" + "\"" + Main.root.substring(0, 2) + "\"" + " get VolumeName";
        String s = "";
        try {
            Process process = Runtime.getRuntime().exec(usbName);
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.readLine();
                while ((s = reader.readLine()) != null) {
                    if (s.trim().length() != 0) {
                        System.out.println(s);
                        if (!s.equals("")) {
                            main.setTitle("i-ncript™ - " + s.trim().toLowerCase());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkWMIC() {
        boolean b = false;
        String logicaldisk = "wmic logicaldisk where name=" + "\"" + Main.root.substring(0, 2) + "\"" + " get description";
        String s;
        try {
            Process process = Runtime.getRuntime().exec(logicaldisk);
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.readLine();
                while ((s = reader.readLine()) != null) {
                    if (s.trim().length() != 0) {
                        if (s.trim().equals("Removable Disk")) {
                            System.out.println("USB MATCH");
                            if (root.length() > 2) {
                                if (root.contains("--------")) {
                                    if (root.length() == 11) {
                                        b = true;
                                    } else {
                                        DriveCheck.driveState = 4;
                                        new DriveCheck().setVisible(true);
                                        b = false;
                                    }
                                } else {
                                    DriveCheck.driveState = 3;
                                    new DriveCheck().setVisible(true);
                                    b = false;
                                }
                            } else {
                                DriveCheck.driveState = 3;
                                new DriveCheck().setVisible(true);
                                b = false;
                            }
                        } else {
                            System.out.println("Drive Must Be A USB");
                            DriveCheck.driveState = 1;
                            new DriveCheck().setVisible(true);
                            b = false;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
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
