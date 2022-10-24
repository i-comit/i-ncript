/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    static long heapMaxSize = Runtime.getRuntime().maxMemory();
    // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
    static long heapFreeSize = Runtime.getRuntime().freeMemory();

    public static void main(String[] args) {

        System.out.println(heapSize);
        System.out.println(heapMaxSize);
        System.out.println(heapFreeSize);
        Runtime env = Runtime.getRuntime();

        System.out.println("Available Processors " + Runtime.getRuntime().availableProcessors());
//        while (true) {
//            System.out.println("Max Heap Size = maxMemory() = " + humanReadableByteCountBin(env.maxMemory())); //max heap size from -Xmx, i.e. is constant during runtime
////            System.out.println("Available in Current Heap = freeMemory() = " + env.freeMemory()); //current heap will extend if no more freeMemory to a maximum of maxMemory
////            System.out.println("Currently Used Heap = " + (env.totalMemory() - env.freeMemory()));
//            System.out.println("Current Heap Size = totalMemory() = " + humanReadableByteCountBin(env.totalMemory())); //currently assigned  heap
////            System.out.println("Unassigned Heap = " + (env.maxMemory() - env.totalMemory()));
////            System.out.println("Currently Totally Available Heap Space = " + ((env.maxMemory() - env.totalMemory()) + env.freeMemory())); //available=unassigned + free
//        }
        checkDriveType();
    }

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

    public static boolean checkDriveType() {
        boolean b = false;

        String rootPath = Paths.get("").toAbsolutePath().toString().trim();
//        String rootPath = "Y:\\";
        System.out.println("Root Path: " + rootPath);
        if (Main.root.equals(rootPath)) {
            b = true;
        } else {
            new DriveCheck().setVisible(true);
            b = false;

        }
        return b;
    }
}
