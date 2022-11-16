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
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
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
//            for (int i = 1; i < directories.size(); i++) {
//                DefaultMutableTreeNode dirNodes = new DefaultMutableTreeNode(directories.get(i).toFile().getName()); // level 1 node
////                recursiveDir(directories.get(i).toFile());
//                if (recursiveDir(directories.get(i).toFile()).length != 0) {
////                    System.out.println("PARENT NODE OF RECURSIVE IS " + directories.get(i).toFile().getName());
//                    String[] arr = recursiveDir(directories.get(i).toFile());
////                    System.out.println("SIZE OF RECURSIVE IS " + arr.length);
//                    for (int a = 0; a < arr.length; a++) {
//                        String subDirPath = Main.root + Main.masterFolder + Statics.folderName + File.separator + directories.get(i).toFile().getName() + File.separator + arr[a];
//                        String subDirPath2 = arr[a];
//                        DefaultMutableTreeNode subDirNodes = new DefaultMutableTreeNode(subDirPath2); // level 1 node
//
////                        System.out.println("SUBDIR NAME IS " + subDirPath2);
//                        File[] files = new File(subDirPath).listFiles();
////                        System.out.println(Arrays.toString(files));
//                        if (files != null) {
//                            dirNodes.add(subDirNodes);
//                            for (int b = 0; b < files.length; b++) {
//                                if (!files[b].isDirectory()) {
////                                    System.out.println("FILES " + files[b]);
//                                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(files[b].getName()); // level 2 (leaf) node
//                                    subDirNodes.add(fileNodes);
//                                } else {
//                                    DefaultMutableTreeNode subDirNodes1 = new DefaultMutableTreeNode(files[b].getName()); // level 1 node
//                                    subDirNodes.add(subDirNodes1);
//                                    File[] files2 = files[b].listFiles();
//                                    for (int c = 0; c < files2.length; c++) {
//                                        if (!files2[c].isDirectory()) {
////                                            System.out.println("FILES2 " + files2[c]);
//                                            DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(files2[c].getName()); // level 2 (leaf) node
//                                            subDirNodes1.add(fileNodes);
//                                        }
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                } else {
//
//                }
//                if (i == 1) {
//                    treeRoot.add(dirNodes);
//                    System.out.println("CHILD 1 DIR " + directories.get(i).getFileName());
//                    File[] files1 = directories.get(i).toFile().listFiles();
//                    for (int x = 0; x < files1.length; x++) {
//                        DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(files1[x].getName()); // level 2 (leaf) node
//                        dirNodes.add(fileNodes);
//                        if (files1[x].isDirectory()) {
////                            dirNodes.remove(fileNodes);
//                        }
//                    }
//                    File[] files0 = directories.get(0).toFile().listFiles();
//                    System.out.println("CHILD 0 DIR " + directories.get(i).getFileName());
//                    for (int x = 1; x < files0.length; x++) {
//                        DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(files0[x].getName()); // level 2 (leaf) node
//                        treeRoot.add(fileNodes);
//                        if (files0[x].isDirectory()) {
////                            dirNodes.remove(fileNodes);
//                        }
//                    }
//                    jTree1.expandRow(i);
//                }
//            }
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
//                            dirNodes.remove(fileNodes);
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
        System.out.println("List all files " + Arrays.toString(filesArr));
        System.out.println("CHILD 2 DIR " + file.getName());
        for (int x = 0; x < filesArr.length; x++) {
            if (!filesArr[x].isDirectory()) {
                DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
                dirNodes.add(fileNodes);
//                System.out.println("files in " + file.getName() + " is " + filesArr[x].getName());
            } else {
//                listFiles(files0[x], dirNodes);               
                System.out.println("this is a directory 0 " + filesArr[x].getName());
                DefaultMutableTreeNode subDirNodes = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
                DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode("amogus"); // level 2 (leaf) node

//                subDirNodes.add(fileNodes);
//                dirNodes.add(subDirNodes);
                listFilesRecursively(filesArr[x], dirNodes, subDirNodes);
//                listFilesRecursively2(filesArr[x], dirNodes);

//                listFiles(filesArr[x], dirNodes);
            }
        }
    }

    public static void listFilesRecursively(File file, DefaultMutableTreeNode dirNodes, DefaultMutableTreeNode subDirNodes) {
        File[] filesArr = file.listFiles();
        System.out.println("Files in listFilesRecursively " + Arrays.toString(filesArr));
        for (int x = 0; x < filesArr.length; x++) {
            if (!filesArr[x].isDirectory()) {
//                            dirNodes.remove(fileNodes);
                DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
                subDirNodes.add(fileNodes);
                System.out.println("files in recursive root folder is " + filesArr[x].getName());
            } else {
                System.out.println("this is a recursive directory XXXXXXXXXXX " + filesArr[x].getName());

                listFilesRecursively(filesArr[x], dirNodes, subDirNodes);
            }
        }
        dirNodes.add(subDirNodes);
    }

    public static void listFilesRecursively2(File file, DefaultMutableTreeNode dirNodes) {
        DefaultMutableTreeNode subDirNodes = new DefaultMutableTreeNode(file.getName()); // level 2 (leaf) node
//        DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode("amogus"); // level 2 (leaf) node
        File[] filesArr = file.listFiles();
        System.out.println("Files in listFilesRecursively " + Arrays.toString(filesArr));
        for (int x = 0; x < filesArr.length; x++) {
            if (!filesArr[x].isDirectory()) {
                DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr[x].getName()); // level 2 (leaf) node
                subDirNodes.add(fileNodes);
                subDirNodes.add(fileNodes);
//                dirNodes.add(fileNodes);
                System.out.println("files in recursive root folder is " + filesArr[x].getName());
            } else {
                System.out.println("this is a directory " + filesArr[x].getName());
//                listFilesRecursively(filesArr[x], dirNodes);
            }
        }
        dirNodes.add(subDirNodes);
    }

    public static String[] recursiveDir(File file) {
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return directories;
    }

    public static File treeFileFormatter(TreePath[] treePaths) {
        File fileFormat = null;
        String path = root + masterFolder + treePaths[0].toString().substring(1, treePaths[0].toString().length() - 1).replaceAll(", ", "\\\\");
        String fileName = new File(root + masterFolder + treePaths[0].toString().substring(1, treePaths[0].toString().length() - 1).replaceAll(", ", "\\\\")).getName();
//                .replaceAll(new File(treePaths[0].toString()).getName(), "");
        if (!new File(path).isDirectory()) {
            System.out.println("Final path without filename " + path.replaceAll(fileName, ""));
        }
        for (int i = 0; i < treePaths.length; i++) {
            fileFormat = new File(root + masterFolder + treePaths[i].toString().substring(1, treePaths[i].toString().length() - 1).replaceAll(", ", "\\\\"));
            if (!fileFormat.isDirectory()) {
                System.out.println("files are " + fileFormat);
            }

        }
        return fileFormat;
    }

    public static void openFile(TreePath treePath) {
        File file = null;
        file = new File(root + masterFolder + treePath.toString().substring(1, treePath.toString().length() - 1).replaceAll(", ", "\\\\"));
        if (!file.isDirectory()) {
            System.out.println("file is " + file);
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    // no application registered for PDFs
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
                System.out.println("All folders matched");
                b = true;
            }
        }
        return b;
    }
}
