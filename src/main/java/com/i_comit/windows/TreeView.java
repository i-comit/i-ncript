/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jTree1;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class TreeView {

    public static DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("ᴠᴀᴜʟᴛ"); // root node
    public static DefaultTreeModel model = new DefaultTreeModel(treeRoot);

    public static DefaultTreeModel populateStoreTree(Path path) {
        treeRoot.removeAllChildren();
        try {
            List<Path> directories = GUI.listDirs(path);
            listRoot(directories);
        } catch (IOException ex) {
        }

        model.reload(treeRoot);
        jTree1.revalidate();
        return model;
    }

    private static Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    private static List<DefaultMutableTreeNode> dirNodeList = new ArrayList<>();
    private static List<DefaultMutableTreeNode> receiveNodeList = new ArrayList<>();
    private static List<DefaultMutableTreeNode> sendNodeList = new ArrayList<>();

    private static List<TreePath> treePaths = new ArrayList<>();
    private static List<TreePath> receiveTreePaths = new ArrayList<>();
    private static List<TreePath> sendTreePaths = new ArrayList<>();

    public static void expandedNodesSwitch(int toolMode, DefaultMutableTreeNode treeNode) {
        switch (toolMode) {
            case 0:
                dirNodeList.add(treeNode);
                break;
            case 1:
                receiveNodeList.add(treeNode);
                break;
            case 2:
                sendNodeList.add(treeNode);
                break;
            case 3:
                dirNodeList.add(treeNode);
                break;
        }
    }

    public static void storeExpandedNodes(int toolMode) {
        switch (toolMode) {
            case 0:
                treePaths.clear();
                for (DefaultMutableTreeNode singleNode : dirNodeList) {
                    TreePath path = new TreePath(singleNode.getPath());
                    if (jTree1.isExpanded(path)) {
                        if (!treePaths.contains(path)) {
                            treePaths.add(path);
                        }
                    }
                    if (jTree1.isCollapsed(path)) {
                        if (treePaths.contains(path)) {
                            treePaths.remove(path);
                        }
                    }
                }
                break;
            case 1:
                receiveTreePaths.clear();
                for (DefaultMutableTreeNode singleNode : receiveNodeList) {
                    TreePath path = new TreePath(singleNode.getPath());
                    if (jTree1.isExpanded(path)) {
                        if (!receiveTreePaths.contains(path)) {
                            receiveTreePaths.add(path);
                        }
                    }
                    if (jTree1.isCollapsed(path)) {
                        if (receiveTreePaths.contains(path)) {
                            receiveTreePaths.remove(path);
                        }
                    }
                }
                break;
            case 2:
                sendTreePaths.clear();
                for (DefaultMutableTreeNode singleNode : sendNodeList) {
                    TreePath path = new TreePath(singleNode.getPath());
                    if (jTree1.isExpanded(path)) {
                        if (!sendTreePaths.contains(path)) {
                            sendTreePaths.add(path);
                        }
                    }
                    if (jTree1.isCollapsed(path)) {
                        if (sendTreePaths.contains(path)) {
                            sendTreePaths.remove(path);
                        }
                    }
                }
                break;
            case 3:
                treePaths.clear();
                for (DefaultMutableTreeNode singleNode : dirNodeList) {
                    TreePath path = new TreePath(singleNode.getPath());
                    if (jTree1.isExpanded(path)) {
                        if (!treePaths.contains(path)) {
                            treePaths.add(path);
                        }
                    }
                    if (jTree1.isCollapsed(path)) {
                        if (treePaths.contains(path)) {
                            treePaths.remove(path);
                        }
                    }
                }
                break;
        }
    }

    public static int nodeCaretPos;
    public static int receiveCaretPos;
    public static int sendCaretPos;

    public static void storeNodeCaretPos(int toolMode) {
        switch (toolMode) {
            case 0:
                nodeCaretPos = Main.jScrollPane5.getVerticalScrollBar().getValue();
                break;
            case 1:
                receiveCaretPos = Main.jScrollPane5.getVerticalScrollBar().getValue();
                break;
            case 2:
                sendCaretPos = Main.jScrollPane5.getVerticalScrollBar().getValue();
                break;
            case 3:
                nodeCaretPos = Main.jScrollPane5.getVerticalScrollBar().getValue();
                break;
        }
    }

    public static void renderTreeCells() {
        jTree1.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (leaf) {
                    if (value.toString().endsWith(".enc")) {
                        c.setForeground(new Color(110, 110, 110));
                        c.setIcon(new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/lock.png")), 11, 13)));
                    } else if (value.toString().endsWith(".pdf")) {
                        c.setIcon(new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pdf.png")), 14, 13)));
                    } else if (value.toString().endsWith(".mp4") || value.toString().endsWith(".avi") || value.toString().endsWith(".webm")) {
                        c.setIcon(new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/video.png")), 14, 13)));
                    } else if (value.toString().endsWith(".stl") || value.toString().endsWith(".step") || value.toString().endsWith(".gcode") || value.toString().endsWith(".fbx") || value.toString().endsWith(".FCStd")) {
                        c.setIcon(new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/model.png")), 14, 13)));
                    } else if (value.toString().endsWith(".png") || value.toString().endsWith(".jpg") || value.toString().endsWith(".gif") || value.toString().endsWith(".svg")) {
                        c.setIcon(new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img.png")), 14, 13)));
                    } else if (value.toString().endsWith(".xlsx") || value.toString().endsWith(".xls") || value.toString().endsWith(".ods")) {
                        c.setIcon(new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/xls.png")), 14, 13)));
                    } else if (value.toString().endsWith(".docx") || value.toString().endsWith(".odt") || value.toString().endsWith(".txt") || value.toString().endsWith(".md")) {
                        c.setIcon(new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/doc.png")), 14, 13)));
                    } else if (value.toString().endsWith(".c") || value.toString().endsWith(".cpp") || value.toString().endsWith(".java") || value.toString().endsWith(".py") || value.toString().endsWith(".php")) {
                        c.setIcon(new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/code.png")), 14, 13)));
                    } else {
                        c.setForeground(Color.WHITE);
                    }
                }
                return c;
            }
        });
        jTree1.revalidate();
    }

    public static void expandTreeNode(Path path) {
        String fileName = path.toFile().getName();
        switch (fileName) {
            case "ᴠᴀᴜʟᴛ":
                for (DefaultMutableTreeNode pathx : dirNodeList) {
                    TreePath path2 = new TreePath(pathx.getPath());
                    for (TreePath treePath : treePaths) {
                        if (treePath.toString().equals(path2.toString())) {
                            jTree1.expandPath(path2);
                        }
                    }
                }
                setCaretPos(nodeCaretPos);
                break;
            case "ɴ-ʙᴏx":
                for (DefaultMutableTreeNode pathx : receiveNodeList) {
                    TreePath path2 = new TreePath(pathx.getPath());
                    for (TreePath treePath : receiveTreePaths) {
                        if (treePath.toString().equals(path2.toString())) {
                            jTree1.expandPath(path2);
                        }
                    }
                }
                setCaretPos(receiveCaretPos);
                break;
            case "ᴏ-ʙᴏx":
                for (DefaultMutableTreeNode pathx : sendNodeList) {
                    TreePath path2 = new TreePath(pathx.getPath());
                    for (TreePath treePath : sendTreePaths) {
                        if (treePath.toString().equals(path2.toString())) {
                            jTree1.expandPath(path2);
                        }
                    }
                }
                setCaretPos(sendCaretPos);
                break;
        }
    }

    private static void setCaretPos(int caretPos) {
        try {
            Rectangle rect = new Rectangle(0, caretPos, 1, jTree1.getRowCount());
            Main.jScrollPane5.getViewport().scrollRectToVisible(rect);
            Main.jScrollPane5.getVerticalScrollBar().setValue(caretPos);
            Main.jScrollPane5.updateUI();
        } catch (NullPointerException ex) {
        }
    }

    public static void setRootName(String rootName) {
        treeRoot.setUserObject(rootName);
    }

    private static void listRoot(List<Path> directories) {
        dirNodeList.clear();
        receiveNodeList.clear();
        sendNodeList.clear();

        File[] filesArr = directories.get(0).toFile().listFiles();
        for (File filesArr1 : filesArr) {
            DefaultMutableTreeNode dirNodes = new DefaultMutableTreeNode(filesArr1.getName());
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr1.getName());
                    treeRoot.add(fileNodes);
                }
            } else {
                treeRoot.add(dirNodes);
                listFiles(filesArr1, dirNodes);
                expandedNodesSwitch(Statics.toolMode, dirNodes);
            }
        }
    }

    private static void listFiles(File file, DefaultMutableTreeNode dirNodes) {
        File[] filesArr = file.listFiles();
        for (File filesArr1 : filesArr) {
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr1.getName());
                    dirNodes.add(fileNodes);
                }
            } else {
                listFilesRecursively(filesArr1, dirNodes);
            }
        }
    }

    private static void listFilesRecursively(File file, DefaultMutableTreeNode dirNodes) {
        DefaultMutableTreeNode subDirNodes = new DefaultMutableTreeNode(file.getName());
        dirNodes.add(subDirNodes);
        File[] filesArr = file.listFiles();
        for (File filesArr1 : filesArr) {
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr1.getName());
                    subDirNodes.add(fileNodes);
                }
            } else {
                DefaultMutableTreeNode subDirNodes0 = new DefaultMutableTreeNode(filesArr1.getName());
                subDirNodes.add(subDirNodes0);
//                dirNodeList.add(subDirNodes0);
                expandedNodesSwitch(Statics.toolMode, subDirNodes0);
                listFiles(filesArr1, subDirNodes0);
            }
        }
//        dirNodeList.add(subDirNodes);
        expandedNodesSwitch(Statics.toolMode, subDirNodes);

        dirNodes.add(subDirNodes);
    }

    public static void openFile(TreePath treePath) {
        File file = null;
        file = new File(root + masterFolder + treePath.toString().substring(1, treePath.toString().length() - 1).replaceAll(", ", "\\\\"));
        if (!file.isDirectory()) {
            if (!file.getName().endsWith(".enc")) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                    }
                }
            } else {
                GUI.labelCutterTreeThread(Main.jAlertLabel, "you can't open encrypted files", 0, 20, 600, false);
            }
        }
    }

    public static void getFileCreationNSize() throws IOException {
        if (Main.jTree1.getSelectionPaths() != null) {
            Main.jAlertLabel.setText("");
            if (Main.jTree1.getSelectionPaths().length <= 1) {
                File fileFormat = new File(root + masterFolder + Main.jTree1.getSelectionPaths()[0].toString().substring(1, Main.jTree1.getSelectionPaths()[0].toString().length() - 1).replaceAll(", ", "\\\\"));
                Path file = fileFormat.toPath();

                if (!fileFormat.isDirectory()) {
                    BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                    GUI.labelCutterTreeThread(Main.jCreationDateLabel, GUI.formatDateTime(attr.lastModifiedTime()), 0, 16, 64, true);
                    GUI.labelCutterTreeThread(Main.jFileSizeLabel, Memory.byteFormatter(fileFormat.length()), 0, 16, 64, true);
                } else {
                    List<Long> fileSizes = new ArrayList<>();
                    List<Path> paths = GUI.listPaths(fileFormat.toPath());
                    paths.forEach(x -> {
                        File xf = x.toFile();
                        long fileSize = xf.length();
                        fileSizes.add(fileSize);
                    });
                    GUI.labelCutterTreeThread(Main.jCreationDateLabel, fileSizes.size() + " files", 0, 16, 64, true);

                    long sum = fileSizes.stream().mapToLong(Long::longValue).sum();
                    GUI.labelCutterTreeThread(Main.jFileSizeLabel, Memory.byteFormatter(sum), 0, 16, 64, true);
                }
            } else {
                List<Long> fileSizes = new ArrayList<>();
                for (int i = 0; i < Main.jTree1.getSelectionPaths().length; i++) {
                    File fileFormat = new File(root + masterFolder + Main.jTree1.getSelectionPaths()[i].toString().substring(1, Main.jTree1.getSelectionPaths()[i].toString().length() - 1).replaceAll(", ", "\\\\"));

                    if (i == Main.jTree1.getSelectionPaths().length) {
                        Path file = fileFormat.toPath();
                        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                        Main.jCreationDateLabel.setText(GUI.formatDateTime(attr.lastModifiedTime()));
                    }

                    if (!fileFormat.isDirectory()) {
                        long fileSize = fileFormat.length();
                        fileSizes.add(fileSize);
                    }
                }
                long sum = fileSizes.stream().mapToLong(Long::longValue).sum();
                GUI.labelCutterTreeThread(Main.jFileSizeLabel, Memory.byteFormatter(sum), 0, 16, 64, true);
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

    public static File convertTreePathToFile(TreePath[] treePaths, int i) {
        String fileStr = root + masterFolder + Main.jTree1.getSelectionPaths()[i].toString().substring(1, Main.jTree1.getSelectionPaths()[i].toString().length() - 1).replaceAll(", ", File.separator + File.separator);
        File filePath = Paths.get(fileStr).toFile();
        return filePath;
    }

    public static boolean treePathContainsDirs(TreePath[] treePaths) {
        for (int i = 0; i < treePaths.length; i++) {
            Path filePath = convertTreePathToFile(Main.jTree1.getSelectionPaths(), i).toPath();
            if (filePath.toFile().isDirectory()) {
                System.out.println("treePaths has folders.");
                return false;

            }
        }
        System.out.println("treePaths has no folders.");
        return true;
    }

    public static List<Path> convertTreePathToPath(TreePath[] treePaths) throws IOException {
        List<Path> pathLists = new ArrayList<>();
        for (int i = 0; i < treePaths.length; i++) {
            Path filePath = convertTreePathToFile(Main.jTree1.getSelectionPaths(), i).toPath();
            if (filePath.toFile().isDirectory()) {
                List<Path> paths = GUI.listPaths(filePath);
                paths.forEach(x -> {
                    pathLists.add(x);
                });
            } else {
                pathLists.add(filePath);
            }
        }
        return pathLists;
    }
}
