/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES.*;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.*;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class HotFiler {

    public static Thread t;

    public static void HotFilerThread() throws IOException {
        HotFiler_T hotFilerThread = new HotFiler_T();
        t = new Thread(hotFilerThread);
        t.start();
    }

}

class HotFiler_T implements Runnable {

    public int threadIterator;

    public void run() {
        try {
            List<Path> paths = listNewPaths(Statics.path);
            if (Main.jToggleButton1.isSelected()) {
                if (paths.isEmpty()) {
                    GUI.labelCutterThread(jAlertLabel, "hot filer enabled", 30, 30, 900);
                    folderWatcher();
                } else {
                    AES.AESThread();
                    Statics.fileCount = GUI.countFiles2(Statics.path);
                    jProgressBar1.setMaximum(Statics.fileCount);
                    Thread.sleep(100);
                    boolean b = true;
                    //GUI.t1.interrupt();
                    while (b = true) {
                        if (!GUI.t1.isAlive()) {
                            GUI.labelCutterThread(jAlertLabel, "hot filer enabled", 30, 30, 900);
                            if (GUI.t.isAlive()) {
                                GUI.t.interrupt();
                            }
                            folderWatcher();
                            b = false;
                        }
                        if (!b) {
                            break;
                        }
                    }
                }
            } else {
                Main.jProgressBar1.setStringPainted(false);
                GUI.labelCutterThread(jAlertLabel, "hot filer disabled", 30, 30, 900);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
//            ex.getMessage();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Path> listNewPaths(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".enc"))
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static int countEncFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".enc")).count());
        }
        return result;
    }

    public static int countRegFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc")).count());
        }
        return result;
    }

    public static void folderWatcher() throws IOException {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path rootPath = Paths.get(Statics.rootFolder);
            rootPath.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE);
//                        StandardWatchEventKinds.ENTRY_DELETE,
//                        StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey key;
            while ((key = watchService.take()) != null && Main.jToggleButton1.isSelected()) {
                GUI.labelCutterThread(jAlertLabel, "hot filer detected new files", 15, 30, 600);
                Statics.fileCount = 0;
                System.out.println("Statics.fileCount " + Statics.fileCount);
                boolean b = true;

                for (WatchEvent<?> event : key.pollEvents()) {
                    while (b) {
                        int paths0 = countRegFiles(Statics.path);
                        Thread.sleep(1500);
                        Statics.fileCount = countRegFiles(Statics.path);
//                        System.out.println("Is AESThread active? " +AES.t.isAlive());
                        if (Statics.fileCount == paths0) {
                            key.cancel();
                            Main.jProgressBar1.setMaximum(Statics.fileCount);
                            AES.AESThread();
                            System.out.println("Hot Filer Called AES");
                            folderWatcher();
                            b = false;
                        }
                    }
                    System.out.println(Statics.fileIter++);
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                }
                key.reset();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ClosedWatchServiceException ex) {
            System.out.println("Watch Service Closed");
        }
    }

    public static void getLastModified() throws IOException {
        List<Path> paths = listNewPaths(Statics.path);
        File[] contents = Statics.directory.listFiles();
        if (AES.t.isAlive()) {
            AES.t.interrupt();
        }

        if (Main.jToggleButton1.isSelected()) {
//            AES.AESThread();
            if (contents != null) {
                if (contents.length != 0) {
                    paths.forEach(x -> {
                        try {
                            encrypt(Hasher.modHash(Statics.password), x.toFile(), x.toFile());
                        } catch (AES.CryptoException ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files", 40, 40, 1000);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist", 40, 40, 100);
            }
        } else {
            AES.t.interrupt();

        }

    }

}
