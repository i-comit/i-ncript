/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.GUI.listAESPaths;
import static com.i_comit.windows.Main.jAlertLabel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class HotFiler {

    public static Thread t;

    public static void HotFilerThread(Main main) {
        t = new Thread(() -> HotFiler_T.startFolderWatcher(main));
        t.start();
    }
}

class HotFiler_T implements Runnable {

    public int threadIterator;

//    @Override
    public void run() {

    }

    public static void startFolderWatcher(Main main) {
        try {
            if (Main.jToggleButton1.isSelected()) {
                List<Path> path = listAESPaths(Statics.path);
                if (path.isEmpty()) {
                    main.setSize(780, 241);
                    folderWatcher();
                } else {
                    AES.AESThread(path, Statics.directory, true, 0);
                }

            } else {
                HotFiler.t.stop();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int countRegFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc")).count());
        }
        return result;
    }

    public static WatchService watchService;
    public static FileTime originalFT = null;

    public static void folderWatcher() throws IOException {
        System.out.println("Folder Watcher Enabled");
        try {
            watchService = FileSystems.getDefault().newWatchService();
            List<Path> directories = GUI.listDirs(Paths.get(Statics.rootFolder));
            for (int i = 0; i < directories.size(); i++) {
                directories.get(i).toFile().toPath().register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE);
            }
            System.out.println("folder watcher attached at " + directories.size() + " folder(s)");
            WatchKey key;
            while ((key = watchService.take()) != null && Main.jToggleButton1.isSelected()) {
                GUI.labelCutterThread(jAlertLabel, "hot filer detected new files", 15, 25, 550, false);
                originalFT = getFileTime();
                Statics.fileCount = 0;

                for (WatchEvent<?> event : key.pollEvents()) {
                    Main.jToggleButton1.setEnabled(false);
                    boolean b = true;
                    while (b) {
                        int paths0 = countRegFiles(Statics.path);
                        Thread.sleep(2500);
                        Statics.fileCount = countRegFiles(Statics.path);
                        if (Statics.fileCount == paths0) {
                            if (paths0 != 0 && Statics.fileCount != 0) {
                                if (compareFileTimes(getLargestFile())) {
                                    GUI.getGB();
                                    key.cancel();
                                    watchService.close();
                                    Statics.fileIter = 0;
                                    Statics.fileCount = GUI.countFiles(Statics.path);
                                    Main.jProgressBar1.setMaximum(Statics.fileCount);
                                    AES.AESThread(listAESPaths(Statics.path), Statics.directory, true, 0);
                                    b = false;
                                } else {
                                    System.out.println("file times not synchronized.");
                                }
                            }
                        }
                    }
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ClosedWatchServiceException ex) {
            System.out.println("Watch Service Closed");
        }
    }

    private static FileTime getFileTime() throws IOException {
        Long aLargestFileSize = 0L;
        List<Path> paths = GUI.listAESPaths(Statics.path);
        Path largestFilePath = null;
        for (Path path : paths) {
            if (aLargestFileSize < path.toFile().length()) {
                aLargestFileSize = path.toFile().length();
                largestFilePath = path;
            }
        }
        BasicFileAttributes attr = Files.readAttributes(largestFilePath, BasicFileAttributes.class);
        FileTime lastModifiedFT = attr.lastModifiedTime();

        return lastModifiedFT;
    }

    private static List<FileTime> getLargestFile() throws IOException {
        List<Path> paths = GUI.listAESPaths(Statics.path);
        List<FileTime> fileTimes = new ArrayList<>();

        for (Path path : paths) {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime lastModifiedFT = attr.lastModifiedTime();
            fileTimes.add(lastModifiedFT);
        }
        return fileTimes;
    }

    private static boolean compareFileTimes(List<FileTime> fileTimes) {
        boolean b = false;
        for (FileTime fileTime : fileTimes) {
            if (fileTime.compareTo(originalFT) < 0) {
                System.out.println("all file times synchronized.");
                b = true;
            } else {
                b = false;
            }
        }
        return b;
    }
}
