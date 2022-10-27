/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Heap {

    static long heapSize = Runtime.getRuntime().totalMemory();
// Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.

    public static String humanReadableByteCountBin(long bytes) {
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
//                            while ((s1 = reader1.readLine()) != null && !b1) {
//                                if (s1.trim().length() != 0) {
//                                    if (s1.trim().equals("Removable Media")) {
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
//                                        b1 = true;
//                                    } else {
//                                        System.out.println("Incompatible USB Device");
//                                        DriveCheck.driveState = 2;
//                                        new DriveCheck().setVisible(true);
//                                        b = false;
//                                    }
//                                }
//                            }
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
}
