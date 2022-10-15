/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.gui;

import static com.i_comit.windows.gui.AES.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.*;
import static java.lang.Thread.currentThread;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */

public class HotFiler {
    public static void HotFilerThread() throws IOException {
        HotFiler_T hotFilerThread = new HotFiler_T();
        //runnableWindows.threadIterator = 0;
        Thread t1 = new Thread(hotFilerThread);
        if (Statics.state) {
            t1.start();
            System.out.println("Hot Filer thread " + currentThread().getName() + "is " + t1.isAlive());

        } else {
            System.out.println("Hot Filer thread " + currentThread().getName() + " stopped" + t1.isAlive());
            t1.interrupt();

        }
//            for(int i=0; i< usbparser.windows.USBParse0.GetDeviceCount(); i++){
//                runnableWindows.threadIterator++;
//                Thread t =new Thread(runnableWindows);    
//                t.start();
//            }
    }

    public static List<Path> listNewFiles(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".enc"))
                    .collect(Collectors.toList());
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
                    List<Path> paths = listNewFiles(Statics.path);
                    paths.forEach(y -> System.out.println(y));
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                    getLastModified();

                }
                key.reset();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void getLastModified() throws IOException {
        List<Path> paths = listNewFiles(Statics.path);
        paths.forEach(x -> System.out.println(x));
        if (Statics.state) {
            paths.forEach(x -> {
                try {
                    encrypt(Statics.password, x.toFile(), x.toFile());
                } catch (CryptoException ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            return;
        }

    }
}

class HotFiler_T implements Runnable{

    public int threadIterator;

    public void run() {

        try {
            HotFiler.folderWatcher();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
