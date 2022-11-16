/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jTree1;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class TreeView {

    public static DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("i-ncript"); // root node
    public static DefaultTreeModel model = new DefaultTreeModel(treeRoot);

    public static DefaultTreeModel populateStoreTree(Path path) {
        treeRoot.removeAllChildren();
        try {
            List<Path> directories = GUI.listDirs(path);
            listRoot(directories);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        model.nodeChanged(treeRoot);
        jTree1.updateUI();

        return model;
    }

    public static void setRootName(String rootName) {
        treeRoot.setUserObject(rootName);

    }

    public static void listRoot(List<Path> directories) {
        File[] filesArr = directories.get(0).toFile().listFiles();
//        System.out.println("CHILD 0 DIR " + directories.get(0).getFileName());
        for (int x = 0; x < filesArr.length; x++) {
            DefaultMutableTreeNode dirNodes = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
//            treeRoot.add(fileNodes);
            if (filesArr[x].isDirectory()) {
//                System.out.println("directories in root " + filesArr[x]);
                treeRoot.add(dirNodes);
                listFiles(filesArr[x], dirNodes);
            } else {
//                System.out.println("files in root " + filesArr[x]);
                DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
                treeRoot.add(fileNodes);
            }
        }
    }

    public static void listFiles(File file, DefaultMutableTreeNode dirNodes) {
        File[] filesArr = file.listFiles();
//        System.out.println("List all files " + Arrays.toString(filesArr));
//        System.out.println("CHILD 2 DIR " + file.getName());
        for (int x = 0; x < filesArr.length; x++) {
            if (!filesArr[x].isDirectory()) {
                DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
                dirNodes.add(fileNodes);
            } else {
                listFilesRecursively(filesArr[x], dirNodes);
            }
        }
    }

    public static void listFilesRecursively(File file, DefaultMutableTreeNode dirNodes) {
        DefaultMutableTreeNode subDirNodes = new DefaultMutableTreeNode(file.getName()); // level 2 (leaf) node
        dirNodes.add(subDirNodes);
        File[] filesArr = file.listFiles();
//        System.out.println("Files in listFilesRecursively " + Arrays.toString(filesArr));
        for (int x = 0; x < filesArr.length; x++) {
            if (!filesArr[x].isDirectory()) {
                DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
                subDirNodes.add(fileNodes);
                subDirNodes.add(fileNodes);
//                System.out.println("files in recursive root folder is " + filesArr[x].getName());
            } else {
                DefaultMutableTreeNode subDirNodes0 = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
                subDirNodes.add(subDirNodes0);
                listFiles(filesArr[x], subDirNodes0);
            }
        }
        dirNodes.add(subDirNodes);
    }

    public static void openFile(TreePath treePath) {
        File file = null;
        file = new File(root + masterFolder + treePath.toString().substring(1, treePath.toString().length() - 1).replaceAll(", ", "\\\\"));
        if (!file.isDirectory()) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                }
            }
        }
    }

    public static boolean checkFilesAreFromSameFolder(List<Path> treepaths) {
        boolean b = false;
        List<String> pathNames = new ArrayList<>();
        for (int i = 0; i < treepaths.size(); i++) {
            String fileName = treepaths.get(i).toFile().getName();
            String dirName = treepaths.get(i).toString();
            String pathName = dirName.replaceAll(fileName, "");
            System.out.println("paths from fileCheck " + pathName);
            pathNames.add(pathName);
        }
        for (String s : pathNames) {
            if (!s.equals(pathNames.get(0))) {
                System.out.println("Found mismatched folder");
                b = false;
            } else {
                b = true;
            }
        }
        return b;
    }
}
