/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES.*;
import static com.i_comit.windows.HotFiler.listNewPaths;
import static com.i_comit.windows.Main.jAlertLabel;
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

        if (Statics.hotFilerState) {
            t.start();
        } else {
            t.interrupt();
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

    public static int countNewFiles(Path path) throws IOException {
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
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    List<Path> paths = listNewPaths(Statics.path);
//                    paths.forEach(y -> System.out.println(y));
//                    System.out.println(
//                            "Event kind:" + event.kind()
//                            + ". File affected: " + event.context() + ".");
                    Main.jProgressBar1.setMaximum(countNewFiles(Statics.path));
                    getLastModified();
                }

                key.reset();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void getLastModified() throws IOException {
        List<Path> paths = listNewPaths(Statics.path);
        File[] contents = Statics.directory.listFiles();

//        AES.AESThread();
        if (Statics.hotFilerState) {
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
            return;

        }

    }
}

class HotFiler_T implements Runnable {

    public int threadIterator;

    public void run() {

        try {
            HotFiler.folderWatcher();
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
    }

}
