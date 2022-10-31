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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Memory {
    static long heapSize = Runtime.getRuntime().totalMemory();

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

    public static boolean checkWMIC() {
        boolean b = false;
//        boolean b1 = false;
        String logicaldisk = "wmic logicaldisk where name=" + "\"" + Main.root.substring(0, 2) + "\"" + " get description";
//        String diskdrive = "wmic diskdrive where model=" + "\"" + "SMI USB DISK USB Device" + "\"" + " get mediatype";
        String s;
//        String s1;
        try {
            Process process = Runtime.getRuntime().exec(logicaldisk);
//            Process process1 = Runtime.getRuntime().exec(diskdrive);
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                BufferedReader reader1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
                reader.readLine();
//                reader1.readLine();
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
