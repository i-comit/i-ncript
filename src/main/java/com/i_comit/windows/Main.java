/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.i_comit.windows;

import com.i_comit.server.Client;
import com.i_comit.shared.Miscs;
import com.i_comit.server.Server;
import static com.i_comit.windows.DriveCheck.goWebsite;
import static com.i_comit.windows.Statics.*;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import static javax.swing.SwingConstants.LEFT;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Main extends javax.swing.JFrame {

    public static String root = "D:\\";
    public static final String masterFolder = "'--------'" + File.separator;
    public static boolean adminBool = false;

    private final String appVer = "1.9.0";
    private final String latestDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    public static final int year = Year.now().getValue();

    private URL fontFile = getClass().getResource("/polentical-neon.ttf");
    private URL fontFile1 = getClass().getResource("/robot-font.otf");

    public Main() {
//        root = Paths.get("").toAbsolutePath().toString();
//        if (Memory.checkWMIC()) {
        root = root.substring(0, 3);
        initComponents();
        Memory.readIPAddress();
        TreeView.renderTreeCells();

        Path runtime = Paths.get(root + masterFolder + "runtime");
        Path app = Paths.get(root + masterFolder + "app");
        if (runtime.toFile().exists()) {
            try {
                Files.setAttribute(runtime, "dos:hidden", true);
            } catch (IOException ex) {
            }
        }
        if (app.toFile().exists()) {
            try {
                Files.setAttribute(app, "dos:hidden", true);
            } catch (IOException ex) {
            }
        }

        jStorePanel.setVisible(true);
        jSendPanel.setVisible(false);
        jReceivePanel.setVisible(false);
        jRadioButton2.setVisible(false);
        jRadioButton3.setVisible(false);
        jScrollPane5.setVisible(false);
        jMenuBar1.setVisible(false);

        if (!keyFile.exists()) {
            jToolPanel.setVisible(false);
            loginLabelVisibleBool(false);
            this.setSize(540, 240);
            this.setLocationRelativeTo(null);
        } else {
            loginLabelVisibleBool(true);
            jUsernameLabel.setText("enter username");
            jPasswordLabel.setText("enter password");
            generateApp();

            jToolPanel.setVisible(false);
            jButton2.setVisible(false);
        }
        jProgressBar2.setVisible(false);
        dragDrop.setVisible(false);
        jSendSQL.setVisible(false);
//        }
    }

    private void getKeyBinding(int keyCode, JPanel jPanel, AbstractAction action) {
        int modifier = 0;
        switch (keyCode) {
            case KeyEvent.VK_E ->
                modifier = InputEvent.SHIFT_DOWN_MASK;
            case KeyEvent.VK_D ->
                modifier = InputEvent.SHIFT_DOWN_MASK;
            case KeyEvent.VK_S ->
                modifier = InputEvent.SHIFT_DOWN_MASK;
            case KeyEvent.VK_X ->
                modifier = InputEvent.SHIFT_DOWN_MASK;
            case KeyEvent.VK_F ->
                modifier = InputEvent.SHIFT_DOWN_MASK;
            case KeyEvent.VK_SPACE ->
                modifier = InputEvent.SHIFT_DOWN_MASK;
            case KeyEvent.VK_V -> {
            }
            case KeyEvent.VK_ENTER -> {
            }
            case KeyEvent.VK_1 -> {
            }
            case KeyEvent.VK_2 -> {
            }
            case KeyEvent.VK_3 -> {
            }
            case KeyEvent.VK_4 -> {
            }
        }
        jPanel.getInputMap().put(KeyStroke.getKeyStroke(keyCode, modifier), keyCode);
        jPanel.getActionMap().put(keyCode, action);
    }

    private void setKeybinding() {
        getKeyBinding(KeyEvent.VK_E, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toolMode == 0 || toolMode == 3) {
                    jRadioButton0.setSelected(true);
                    if (jRadioButton0.isEnabled()) {
                        try {
                            AESMode = 0;
                            fileCount = GUI.countFiles(path);
                            if (fileCount != 0) {
                                Main.this.setSize(779, 266);
                                encryptFunction(Main.this);
                            } else {
                                GUI.t.interrupt();
                                GUI.labelCutterThread(jAlertLabel, "no files to encrypt", 10, 20, 400, false);
                                Main.this.setSize(779, 240);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        getKeyBinding(KeyEvent.VK_D, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toolMode == 0 || toolMode == 3) {
                    jRadioButton1.setSelected(true);
                    if (jRadioButton1.isEnabled()) {
                        try {
                            AESMode = 1;
                            fileCount = GUI.countFiles(path);
                            if (fileCount != 0) {
                                Main.this.setSize(779, 266);
                                decryptFunction(Main.this);
                            } else {
                                GUI.t.interrupt();
                                GUI.labelCutterThread(jAlertLabel, "no files to decrypt", 10, 20, 400, false);
                                Main.this.setSize(779, 240);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        //STOP KEYBIND
        getKeyBinding(KeyEvent.VK_S, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Statics.fileIter != 0) {
                    stopFunction(Main.this);
                }
            }
        });
        getKeyBinding(KeyEvent.VK_X, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.setText("");
            }
        });

        //HOT FILER KEYBIND
        getKeyBinding(KeyEvent.VK_F, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toolMode == 0 || toolMode == 3) {
                    hotFilerBool ^= true;
                    if (jToggleButton1.isEnabled()) {
                        jToggleButton1.setSelected(hotFilerBool);
                        jRadioButton0.setSelected(hotFilerBool);
                        hotFilerFunction(Main.this);
                    }
                }
            }
        });
        //FILE HIDER KEYBIND
        getKeyBinding(KeyEvent.VK_SPACE, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toolMode == 0 || toolMode == 3) {
                    fileHiderBool ^= true;
                    jToggleButton2.setSelected(fileHiderBool);
                    if (jToggleButton2.isEnabled()) {
                        fileHiderFunction();
                    }
                }
            }
        });
        //RELOAD TREEVIEW KEYBIND
        getKeyBinding(KeyEvent.VK_V, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (toolMode) {
                    case 0:
                        refreshTreeView(path, TreeView.nodeCaretPos);
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "reloaded " + path.toFile().getName() + " folder", 0, 25, 600, false);
                        break;
                    case 1:
                        refreshTreeView(receiveFolder, TreeView.receiveCaretPos);
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "reloaded " + receiveFolder.toFile().getName() + " folder", 0, 25, 600, false);
                        break;
                    case 2:
                        refreshTreeView(sendFolder, TreeView.sendCaretPos);
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "reloaded " + sendFolder.toFile().getName() + " folder", 0, 25, 600, false);
                        break;
                    case 3:
                        refreshTreeView(path, TreeView.nodeCaretPos);
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "reloaded " + path.toFile().getName() + " folder", 0, 25, 600, false);
                        break;

                }
            }
        });
        //CYCLE TOOL PANELS
        getKeyBinding(KeyEvent.VK_ENTER, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jSwitchMode.isVisible()) {
                    switchToolPanels();
                }
            }
        });
        getKeyBinding(KeyEvent.VK_1, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTabbedPane1.setSelectedIndex(0);
            }
        });
        getKeyBinding(KeyEvent.VK_2, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTabbedPane1.setSelectedIndex(1);
            }
        });
        getKeyBinding(KeyEvent.VK_3, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTabbedPane1.setSelectedIndex(2);
            }
        });
        getKeyBinding(KeyEvent.VK_4, jToolPanel, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTabbedPane1.setSelectedIndex(3);
            }
        });
    }

    public static void refreshTreeView(Path path, int caretPos) {
        if (!dragDropBool) {
            TreeView.populateStoreTree(path);
            caretPos = jScrollPane5.getVerticalScrollBar().getValue();
            TreeView.expandTreeNode(path);
            TreeView.renderTreeCells();
        }
    }

    private void loginLabelVisibleBool(boolean b) {
        jLoginPanel.setVisible(b);
        jLabel1.setVisible(b);
        jLabel3.setVisible(b);
        jAlertLabel.setVisible(b);
        jTabbedPane1.setVisible(b);
    }

    private void adminLabelToggle() {
        if (adminBool) {
            jShowServer.setVisible(true);
            jAdminLabel.setVisible(false);
            jServerButton.setVisible(true);
            jClientIPInput.setVisible(false);
            jClientIPInput.setVisible(false);
        } else {
            jShowServer.setVisible(false);
            jServerButton.setVisible(false);
            if (Client.getClientIP()) {
                jAdminLabel.setVisible(true);
                jClientIPInput.setVisible(false);
            } else {
                jAdminLabel.setVisible(false);
                jClientIPInput.setVisible(true);
            }
        }
        adminBool = false;
    }

    private void startServer() {
        File serverExeFile = new File(root + masterFolder + "server.exe");
        if (serverExeFile.exists()) {
            try {
                Files.setAttribute(serverExeFile.toPath(), "dos:hidden", true);
                if (!Folder.appLockFile.exists()) {
                    try {
                        jAdminLabel.setVisible(true);
                        jServerButton.setVisible(false);
                        adminBool = true;
                        String listUSB = String.format("start %s", serverExeFile);
                        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", listUSB);
                        pb.start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            GUI.t.interrupt();
            GUI.labelCutterThread(jAlertLabel, "unable to start local server.", 25, 50, 1500, false);
            adminBool = false;
        }
    }

    private void generateApp() {
        adminLabelToggle();
        Memory.checkUSBConnection();

        Memory.getHeapSize(this);
        Memory.getUSBName(this);
        setKeybinding();
        GUI.getGB();
        jAlertLabel.setHorizontalAlignment(LEFT);
        jEULAPanel1.setVisible(false);
        jEULAPanel.setVisible(false);
        this.setLocationRelativeTo(null);
        System.out.println("Your available Memory Heap is " + Memory.byteFormatter(Memory.totalMemory));
        File rootFolder = Paths.get(root + masterFolder + folderName).toFile();
        File sendFolderF = Statics.sendFolder.toFile();
        File receiveFolderF = Statics.receiveFolder.toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!sendFolderF.exists()) {
            sendFolderF.mkdir();
        }
        if (!receiveFolderF.exists()) {
            receiveFolderF.mkdir();
        }
        if (!keyFile.exists()) {
            GUI.labelCutterThread(jAlertLabel, "please create an account.", 60, 80, 100, true);
            jUsernameLabel.setText("create username");
            jPasswordLabel.setText("create password");
        } else {
            Random rand = new Random();
            int rand_int1 = rand.nextInt(5);
            if (!Miscs.holidayCheck()) {
                switch (rand_int1) {
                    case 0 ->
                        GUI.labelCutterThread(jAlertLabel, "a data encryption app.", 80, 80, 100, true);
                    case 1 ->
                        GUI.labelCutterThread(jAlertLabel, "developed by i-comit LLC.", 80, 80, 100, true);
                    case 2 ->
                        GUI.labelCutterThread(jAlertLabel, "USB drive, reimagined.", 60, 80, 100, true);
                    case 3 ->
                        GUI.labelCutterThread(jAlertLabel, "also available on mac os.", 60, 80, 100, true);
                    case 4 ->
                        GUI.labelCutterThread(jAlertLabel, "also available on linux.", 60, 80, 100, true);
                }
            }
            FileHider.cleanUp(path);
            FileHider.cleanUp(sendFolder);
            FileHider.cleanUp(receiveFolder);
        }
    }

    public static void dragDropper() {
        DragDrop myDragDropListener = new DragDrop();
        // Connect the label with a drag and drop listener
        dragDrop.setVisible(true);
        new DropTarget(dragDrop, myDragDropListener);
    }

    public static void toolBtnsBool(boolean bool) {
        jToggleButton1.setEnabled(bool);
        jToggleButton2.setEnabled(bool);
        jRadioButton0.setEnabled(bool);
        jRadioButton1.setEnabled(bool);
        jSwitchMode.setVisible(bool);
        jTree1.setEnabled(bool);

        jToggleDragDrop.setEnabled(bool);
        jShowServer.setEnabled(bool);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jAlertLabel = new javax.swing.JLabel();
        jToolPanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jSwitchMode = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jProgressBar2 = new javax.swing.JProgressBar();
        jStorePanel = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton0 = new javax.swing.JRadioButton();
        jSendPanel = new javax.swing.JPanel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSendSQL = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jReceivePanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPasswordField3 = new javax.swing.JPasswordField();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLoginPanel = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jUsernameLabel = new javax.swing.JLabel();
        jPasswordLabel = new javax.swing.JLabel();
        jServerButton = new javax.swing.JButton();
        jAdminLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();
        jClientIPInput = new javax.swing.JFormattedTextField();
        jHeapLabel = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jCreationDateLabel = new javax.swing.JLabel();
        jFileSizeLabel = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        dragDrop = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jEULAPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jEULAPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jShowServer = new javax.swing.JMenuItem();
        jToggleDragDrop = new javax.swing.JRadioButtonMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("i-ncript™");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/i-comiti.png")));
        setMinimumSize(new java.awt.Dimension(295, 205));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setResizable(false);
        setSize(new java.awt.Dimension(320, 0));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        getContentPane().setLayout(null);

        jAlertLabel.setFont(Statics.registerCustomFont(12, fontFile));
        jAlertLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jAlertLabel);
        jAlertLabel.setBounds(21, 174, 236, 27);

        jToolPanel.setOpaque(false);
        jToolPanel.setPreferredSize(new java.awt.Dimension(252, 150));
        jToolPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setFont(Statics.registerCustomFont(12, fontFile));
        jButton2.setText("STOP");
        jButton2.setToolTipText("stops current AES task");
        jButton2.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton2.setPreferredSize(new java.awt.Dimension(105, 22));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolPanel.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 80, -1, -1));

        jSwitchMode.setFont(Statics.registerCustomFont(12, fontFile));
        jSwitchMode.setText("STORE");
        jSwitchMode.setToolTipText("current panel can encrypt & decrypt personal files");
        jSwitchMode.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jSwitchMode.setPreferredSize(new java.awt.Dimension(72, 22));
        jSwitchMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSwitchModeActionEvt(evt);
            }
        });
        jToolPanel.add(jSwitchMode, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 80, 105, -1));

        jButton3.setFont(Statics.registerCustomFont(12, fontFile));
        jButton3.setText("CLR LOG"); // NOI18N
        jButton3.setToolTipText("clear output from LOG tab");
        jButton3.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton3.setPreferredSize(new java.awt.Dimension(105, 22));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolPanel.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, -1, -1));

        jProgressBar2.setFont(Statics.registerCustomFont(11, fontFile));
        jProgressBar2.setForeground(Color.white);
        jToolPanel.add(jProgressBar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 118, 240, 18));

        jStorePanel.setOpaque(false);
        jStorePanel.setPreferredSize(new java.awt.Dimension(250, 75));

        jToggleButton1.setFont(Statics.registerCustomFont(12, fontFile));
        jToggleButton1.setText("HOT FILER"); // NOI18N
        jToggleButton1.setToolTipText("enable to automatically encrypt any file put into the i-ncript folder");
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setMargin(new java.awt.Insets(2, 13, 2, 13));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jToggleButton2.setFont(Statics.registerCustomFont(12, fontFile));

        jToggleButton2.setText("HIDE FILE"); // NOI18N
        jToggleButton2.setToolTipText("click to hide or unhide all files, runs after every AES task");
        jToggleButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton2.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jToggleButton2.setMaximumSize(new java.awt.Dimension(105, 22));
        jToggleButton2.setMinimumSize(new java.awt.Dimension(105, 22));
        jToggleButton2.setPreferredSize(new java.awt.Dimension(105, 22));
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(Statics.registerCustomFont(12, fontFile));
        jRadioButton1.setText("DECRYPT");
        jRadioButton1.setFocusable(false);
        jRadioButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jRadioButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton0);
        jRadioButton0.setFont(Statics.registerCustomFont(12, fontFile));
        jRadioButton0.setText("ENCRYPT");
        jRadioButton0.setFocusable(false);
        jRadioButton0.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jRadioButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton0ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jStorePanelLayout = new javax.swing.GroupLayout(jStorePanel);
        jStorePanel.setLayout(jStorePanelLayout);
        jStorePanelLayout.setHorizontalGroup(
            jStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jStorePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jStorePanelLayout.createSequentialGroup()
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jStorePanelLayout.createSequentialGroup()
                        .addComponent(jRadioButton0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRadioButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jStorePanelLayout.setVerticalGroup(
            jStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jStorePanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton0)
                    .addComponent(jRadioButton1)))
        );

        jToolPanel.add(jStorePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jSendPanel.setOpaque(false);
        jSendPanel.setPreferredSize(new java.awt.Dimension(250, 75));
        jSendPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPasswordField2.setPreferredSize(new java.awt.Dimension(103, 22));
        jPasswordField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPasswordField2Evt(evt);
            }
        });
        jSendPanel.add(jPasswordField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 46, 100, -1));

        jTextField2.setFont(Statics.registerCustomFont(12, fontFile));
        jTextField2.setPreferredSize(new java.awt.Dimension(103, 22));
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });
        jSendPanel.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 15, 100, -1));

        jLabel5.setFont(Statics.registerCustomFont(12, fontFile));
        jLabel5.setText("set file username");
        jLabel5.setToolTipText("the name must match your recipient's username");
        jSendPanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 18, -1, -1));

        jLabel6.setFont(Statics.registerCustomFont(12, fontFile));
        jLabel6.setText("create password");
        jSendPanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 50, -1, -1));

        jSendSQL.setFont(Statics.registerCustomFont(12, fontFile));
        jSendSQL.setText("SEND TO :");
        jSendSQL.setFocusable(false);
        jSendSQL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSendSQLActionPerformed(evt);
            }
        });
        jSendPanel.add(jSendSQL, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 16, 110, -1));

        jRadioButton2.setFont(Statics.registerCustomFont(12, fontFile));
        jRadioButton2.setText("ENCRYPT");
        jRadioButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jRadioButton2.setPreferredSize(new java.awt.Dimension(92, 20));
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2Evt(evt);
            }
        });
        jSendPanel.add(jRadioButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 49, -1, -1));

        jToolPanel.add(jSendPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jReceivePanel.setOpaque(false);
        jReceivePanel.setPreferredSize(new java.awt.Dimension(250, 75));
        jReceivePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(Statics.registerCustomFont(12, fontFile));
        jLabel7.setText("enter password");
        jReceivePanel.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 50, -1, -1));

        jPasswordField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPasswordField3Evt(evt);
            }
        });
        jReceivePanel.add(jPasswordField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 46, 100, -1));

        jRadioButton3.setFont(Statics.registerCustomFont(12, fontFile));
        jRadioButton3.setText("DECRYPT");
        jRadioButton3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jRadioButton3.setPreferredSize(new java.awt.Dimension(92, 20));
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3Evt(evt);
            }
        });
        jReceivePanel.add(jRadioButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 49, -1, -1));

        jLabel8.setFont(Statics.registerCustomFont(12, fontFile));
        jLabel8.setText("select a .i-cc file");
        jLabel8.setToolTipText("choose a .i-cc file that was made from o-box");
        jReceivePanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 18, -1, -1));

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setToolTipText("select the .i-cc file here");
        jScrollPane7.setHorizontalScrollBar(null);

        jList1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setToolTipText("select one .i-cc file from n-box folder");
        jList1.setAutoscrolls(false);
        jList1.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        jList1.setSelectionBackground(new java.awt.Color(75, 110, 175));
        jScrollPane7.setViewportView(jList1);

        jReceivePanel.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 15, 100, 22));

        jToolPanel.add(jReceivePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(jToolPanel);
        jToolPanel.setBounds(258, 34, 252, 150);

        jLoginPanel.setToolTipText("");
        jLoginPanel.setOpaque(false);
        jLoginPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextField1.setFont(Statics.registerCustomFont(12, fontFile));
        jLoginPanel.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(137, 6, 103, -1));

        jPasswordField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordField1KeyPressed(evt);
            }
        });
        jLoginPanel.add(jPasswordField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(137, 32, 103, -1));

        jUsernameLabel.setFont(Statics.registerCustomFont(12, fontFile));
        jUsernameLabel.setText("enter username");
        jLoginPanel.add(jUsernameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 6, -1, 21));

        jPasswordLabel.setFont(Statics.registerCustomFont(12, fontFile));
        jPasswordLabel.setText("enter password");
        jLoginPanel.add(jPasswordLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 35, -1, -1));

        jServerButton.setBackground(javax.swing.UIManager.getDefaults().getColor("CheckBox.background"));
        jServerButton.setFont(Statics.registerCustomFont(12, fontFile1));
        jServerButton.setForeground(new java.awt.Color(153, 153, 153));
        jServerButton.setText("INIT SERVER");
        jServerButton.setToolTipText("start the local data server as admin");
        jServerButton.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 0, 1, javax.swing.UIManager.getDefaults().getColor("CheckBox.background")));
        jServerButton.setBorderPainted(false);
        jServerButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jServerButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jServerButton.setMargin(new java.awt.Insets(0, 2, 6, 2));
        jServerButton.setMaximumSize(new java.awt.Dimension(85, 13));
        jServerButton.setMinimumSize(new java.awt.Dimension(81, 13));
        jServerButton.setPreferredSize(new java.awt.Dimension(85, 13));
        jServerButton.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jServerButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jServerButtonActionPerformed(evt);
            }
        });
        jLoginPanel.add(jServerButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 66, 72, 19));

        jAdminLabel.setFont(Statics.registerCustomFont(11, fontFile));
        jAdminLabel.setForeground(new java.awt.Color(102, 102, 102));
        jAdminLabel.setText("ADMIN USB DRIVE");
        jLoginPanel.add(jAdminLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 69, -1, -1));

        jButton1.setFont(Statics.registerCustomFont(12, fontFile));
        jButton1.setText("ENTER");
        jButton1.setToolTipText("log into i-ncript");
        jButton1.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton1.setPreferredSize(new java.awt.Dimension(73, 22));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jLoginPanel.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(137, 63, 103, -1));

        jSlider1.setMaximum(8);
        jSlider1.setMinimum(1);
        jSlider1.setSnapToTicks(true);
        jSlider1.setToolTipText("set a new heap size if needed");
        jSlider1.setValue(1);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });
        jLoginPanel.add(jSlider1, new org.netbeans.lib.awtextra.AbsoluteConstraints(71, 100, 180, -1));

        jClientIPInput.setBackground(javax.swing.UIManager.getDefaults().getColor("CheckBox.background"));
        jClientIPInput.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 3, 1, 1, javax.swing.UIManager.getDefaults().getColor("CheckBox.background")));
        jClientIPInput.setForeground(new java.awt.Color(140, 140, 140));
        try {
            jClientIPInput.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.#.###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jClientIPInput.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jClientIPInput.setText("000.000.0.000");
        jClientIPInput.setToolTipText("enter the host's IP address here");
        jClientIPInput.setFont(Statics.registerCustomFont(12, fontFile1));
        jClientIPInput.setMinimumSize(new java.awt.Dimension(105, 20));
        jClientIPInput.setPreferredSize(new java.awt.Dimension(115, 18));
        jLoginPanel.add(jClientIPInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 69, 110, -1));

        jHeapLabel.setFont(Statics.registerCustomFont(12, fontFile));
        jHeapLabel.setText("set heap");
        jHeapLabel.setToolTipText("currently selected heap size");
        jLoginPanel.add(jHeapLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 101, -1, -1));

        getContentPane().add(jLoginPanel);
        jLoginPanel.setBounds(20, 44, 250, 120);

        jProgressBar1.setFont(Statics.registerCustomFont(12, fontFile));
        jProgressBar1.setForeground(Color.WHITE);
        jProgressBar1.setBorder(null);
        jProgressBar1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jProgressBar1StateChanged(evt);
            }
        });
        getContentPane().add(jProgressBar1);
        jProgressBar1.setBounds(22, 204, 721, 17);

        jLabel1.setFont(Statics.registerCustomFont(18, fontFile));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText(goWebsite(jLabel1, "", "https://i-comit.com", "i-comit",true)
        );
        jLabel1.setToolTipText("go to i-comit.com");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(22, 10, 90, 16);

        jLabel3.setFont(Statics.registerCustomFont(17, fontFile)
        );
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("jLabel3");
        jLabel3.setToolTipText(Memory.getDataSizePercentage());
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel3MouseExited(evt);
            }
        });
        getContentPane().add(jLabel3);
        jLabel3.setBounds(125, 4, 135, 30);

        jCreationDateLabel.setFont(Statics.registerCustomFont(13, fontFile));
        jCreationDateLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(jCreationDateLabel);
        jCreationDateLabel.setBounds(22, 172, 130, 27);

        jFileSizeLabel.setFont(Statics.registerCustomFont(13, fontFile));
        jFileSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jFileSizeLabel.setFocusable(false);
        getContentPane().add(jFileSizeLabel);
        jFileSizeLabel.setBounds(146, 172, 100, 27);

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setFocusable(false);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(224, 190));

        jPanel2.setBackground(new java.awt.Color(57, 57, 57));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(75, 110, 175)));

        dragDrop.setBackground(new java.awt.Color(57, 57, 57));
        dragDrop.setToolTipText("drop box will encrypt & decrypt any files dropped here");
        dragDrop.setFocusable(false);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drop.png"))); // NOI18N

        jLabel10.setFont(Statics.registerCustomFont(15, fontFile));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("STORE MODE");

        jLabel11.setFont(Statics.registerCustomFont(12, fontFile));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("ENCRYPT & DECRYPT");

        jLabel12.setFont(Statics.registerCustomFont(12, fontFile));
        jLabel12.setText("DRAG");

        jLabel13.setFont(Statics.registerCustomFont(12, fontFile));
        jLabel13.setText("AND");

        jLabel14.setFont(Statics.registerCustomFont(12, fontFile));
        jLabel14.setText("DROP");

        javax.swing.GroupLayout dragDropLayout = new javax.swing.GroupLayout(dragDrop);
        dragDrop.setLayout(dragDropLayout);
        dragDropLayout.setHorizontalGroup(
            dragDropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(dragDropLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dragDropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(dragDropLayout.createSequentialGroup()
                        .addGap(0, 59, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(dragDropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel14)
                            .addComponent(jLabel13))
                        .addGap(0, 59, Short.MAX_VALUE)))
                .addContainerGap())
        );
        dragDropLayout.setVerticalGroup(
            dragDropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dragDropLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(dragDropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(dragDropLayout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel13)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel14))
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(jLabel10)
                .addGap(4, 4, 4)
                .addComponent(jLabel11)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dragDrop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dragDrop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("DROP", jPanel2);

        jPanel1.setFocusable(false);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(57, 57, 57));
        jTextArea1.setColumns(16);
        jTextArea1.setForeground(Color.white);
        jTextArea1.setRows(5);
        jTextArea1.setAutoscrolls(false);
        jTextArea1.setFocusable(false);
        jTextArea1.setMargin(new java.awt.Insets(3, 6, 3, 6));
        jTextArea1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTextArea1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextArea1MouseExited(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("LOG", jPanel1);

        jPanel3.setFocusable(false);

        jTextArea2.setEditable(false);
        jTextArea2.setBackground(new java.awt.Color(57, 57, 57));
        jTextArea2.setColumns(20);
        jTextArea2.setForeground(Color.WHITE);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText( "i-ncript " + appVer + " - "+ latestDate+ "\n\nCopyright "+ year +" i-comit LLC. All rights reserved.\n\nUser has the right to freely distribute this software. User does not have the right to distribute a modified version of this software.\n\ni-comit LLC is not responsible for any data loss from using this software.\n\nCustom font used is called Polentical Neon, developed by Jayvee Enaguas © Grand Chaos Productions. Some Rights Reserved. Licensed Under Creative Commons (CC-BY-SA 3.0).");
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setAutoscrolls(false);
        jTextArea2.setCaretPosition(0);
        jTextArea2.setFocusable(false);
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("ABOUT", jPanel3);

        jPanel4.setFocusable(false);

        jTextArea6.setEditable(false);
        jTextArea6.setBackground(new java.awt.Color(57, 57, 57));
        jTextArea6.setColumns(20);
        jTextArea6.setForeground(Color.WHITE);
        jTextArea6.setLineWrap(true);
        jTextArea6.setRows(5);
        jTextArea6.setText("Standard Operating Procedure:\n\ni-ncript operates with 3 tool panels connected to their respective folders: STORE, N-BOX, and O-BOX, which you can cycle through via the button on the bottom left. Within these panels are primary tools which are accessible in all 3 panels, and some others which are exclusive to its respective panel.\ni-ncript also has a tabbed pane to the right of its interface, containing panels which displays encryption output, drag&drop, about, and SOP (this tab).\n\n[TOOL PANEL]\n-ENCRYPT & DECRYPT\nThese two radio buttons are the main tools of i-ncript, and pressing either buttons will run the respective encryption task, using the AES cipher. Encrypted files will have an .enc file extension.\n-STOP\nThis button only appears during encryption/decryption; click it to stop the current crypto task.\n-CLR LOG\nThis button will clear the outputs seen in the LOG tab during encryption and decryption.\n\n[FILE TREE]\n- A file tree viewer can be seen to the left of the window after login. This tree recursively lists all your files for its respective tool panel. You can encrypt/decrypt the files from this file tree by selecting the desired files then dragging it to the DROP tab across the GUI, you can also double click on a single file to open it. Single clicking on a file will display its creation date and filesize at the bottom of the file tree panel, and selecting multiple files at once will give the sum of all the selected file sizes.\n\n\n[TABBED PANEL]\n-DROP\nThe second tab is only enabled while the STORE panel is active. You can drag and drop any files from your computer into the panel of this tab and it will automatically encrypt or decrypt (dependent on the file type) in its current directory. This is useful if you want to encrypt or decrypt only a few files to work on.\nIf you are on the N-BOX panel, the DROP tab will instead only accept 1 .i-cc file at a time, and move that file to the n-box folder.\n-LOG\nThe first tab of the pane logs each name of the file being encrypted and decrypted, along with the time that the crypto task was complete.\n-ABOUT\nCopyright information, contact information and liability clauses can be found here.\n-HELP\nThis tab serves to provide more information on the tools and fields offered by i-ncript. This is the tab that you are currently on.\n\nThese components are active throughout all 3 panels, now we will go over some that are exlusive to its tool panel.\n\n[STORE] (vault folder)\nThis panel is connected to the vault folder and the first panel that you will see. It is your personal encryption folder that you can use to store data that only you can access.\n-HOT FILE\nHot Filer can be toggled for automatic encryption whenever any new files is dropped into the vault folder. If it detects any new files it will run the Encrypt function the same way as clicking on the Encrypt radio button.\n-HIDE FILE\nHide File can be toggled to hide or unhide every file in the vaultt folder. It runs after every crypto task.\n\n[N-BOX]\nThis is the second panel after pressing the STORE button on the bottom left of the UI. This panel is connected to your n-box (inbox) folder, and it has the ability to decrypt .i-cc (specialized encrypted files) files that someone else has sent to you, granted you have the correct credentials.\n1. Rather than buttons, you are presented with a list and a password field. The list lists out all the .i-cc files that it found in the n-box folder, and if there is one, you can select it by clicking on the .i-cc file name.\n2. You then input the password that the sender has provided to you in faith, and as long as its over 4 characters (any less and the DECRYPT button will not appear) and matches the hash inside the .i-cc file, then that file will be decrypted into a folder with all its contents in readable form.\n\n[O-BOX]\nThis is the last panel when you press the N-BOX folder, which was previously STORE, and pressing it again will cycle you back to the STORE panel. This panel is connected to your o-box (outbox) folder, and any files you put in this folder can be encrypted and packaged into a .i-cc file (which you can send to someone else just like outbox mail)\n1. You must first know the username of the recipient's i-ncript account. It must match exactly in order for them to decrypt it. If you do know, then put it in the first text field.\n2. You then create a second password that you will confidentially share with the recipient, and this will be hashed. If there are files in the o-box folder (again, make sure you intend for ALL the files and folders in o-box to go to this person because it will package everything inside this folder) then it will be neatly packaged into a .i-cc file for you to email.\n\n[HOTKEYS]\n- SHIFT+E\nThis will run the encryption task on the vault folder, similar to clicking the ENCRYPT radio button.\n- SHIFT+D\nThis will run the decryption task on the vault folder, similar to clicking the DECRYPT radio button.\n- SHIFT+S\nThis will stop the active encryption/decryption task, similar to clicking the STOP button.\n- SHIFT+X\nThis will clear the output log on the tabbed pane, similar to clicking CLR LOG.\n- SHIFT+F\nThis will run hot filer function, similar to clicking on the HOT FILER button.\n- SHIFT+SPACE\nThis will run the file hider function, similar to clicking on the HIDE FILE button.\n- V\nThis will update the active file tree.\n- ENTER\nThis will cycle through the 3 tool panels, STORE, N-BOX, and O-BOX.\n- 1,2,3, and 4\nThese number keys will switch to the corresponding tabbed panel such as DROP, LOG, etc.");
        jTextArea6.setWrapStyleWord(true);
        jTextArea6.setCaretPosition(0);
        jTextArea6.setFocusable(false);
        jScrollPane6.setViewportView(jTextArea6);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTabbedPane1.addTab("HELP", jPanel4);

        getContentPane().add(jTabbedPane1);
        jTabbedPane1.setBounds(520, 12, 224, 190);

        jEULAPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(Statics.registerCustomFont(16, fontFile));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("END USER LICENSE AGREEMENT");
        jEULAPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 498, -1));
        jEULAPanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 28, 498, -1));

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jTextArea3.setText("Last updated: October 28, 2022\nPlease read these End User License Agreements carefully before using Our Service.\n\nThis End-User License Agreement (this “EULA”) is a legal agreement between you (“Licensee\") and i-comit LLC (“Licensor”), the author of i-ncript, including all HTML files, XML files, Java files, graphics files, animation files, data files, technology, development tools, scripts and programs, both in object code and source code (the \"Software\"), the deliverables provided pursuant to this EULA, which may include associated media, printed materials, and “online” or electronic documentation. \nBy installing, copying, or otherwise using the Software, Licensee agrees to be bound by the terms and conditions set forth in this EULA. If Licensee does not agree to the terms and conditions set forth in this EULA, then Licensee may not download, install, or use the Software. \n\nGRANT OF LICENSE\nA) Scope of License. Subject to the terms of this EULA, Licensor hereby grants to Licensee a royalty-free, non-exclusive license to possess and to use a copy of the Software. \nB) Installation and Use. Licensee may install and use an unlimited number of copies of the Software and make multiple back-up copies of the Software, solely for Licensee's business and personal use. \n\nDESCRIPTION OF RIGHTS AND LIMITATIONS\nA) Limitations. Licensee and third parties may not reverse engineer, decompile, or disassemble the Software, except and only to the extent that such activity is expressly permitted by applicable law notwithstanding the limitation. \nB) Update and Maintenance. Licensor shall provide updates and maintenance on the Software on an as needed basis. \nC) Separation of Components. The Software is licensed as a single product. Its components may not be separated for use on more than one computer.\n\nTITLE TO SOFTWARE\nLicensor represents and warrants that it has the legal right to enter into and perform its obligations under this EULA, and that use by the Licensee of the Software, in accordance with the terms of this EULA, will not infringe upon the intellectual property rights of any third parties.\n\nINTELLECTUAL PROPERTY\nAll now known or hereafter known tangible and intangible rights, title, interest, copyrights and moral rights in and to the Software, including but not limited to all images, photographs, animations, video, audio, music, text, data, computer code, algorithms, and information, are owned by Licensor. The Software is protected by all applicable copyright laws and international treaties.\n\nUSAGE AND MODIFICATION\nLicensee does not have any right to modify the design, labels, fields, or source code of this Software in any way. Licensee can freely use and distribute this Software for personal or commercial use, but can not modify the Software in such a way as to financially profit from the Software or attempt to freely distribute the Software under a different name.\n\nSUPPORT\nLicensor has no obligation to provide support services for the Software.\n\nDURATION\nThis EULA is perpetual until the Software is terminated or suspended by Licensor, with or without cause. In the event this EULA is terminated, you must cease use of the Software and destroy all copies of the Software. \n\nTRANSFERABILITY\nThis EULA is not assignable or transferable by Licensee, and any attempt to do so would be void.\n\nSEVERABILITY\nNo failure to exercise, and no delay in exercising, on the part of either party, any privilege, any power or any rights hereunder will operate as a waiver thereof, nor will any single or partial exercise of any right or power hereunder preclude further exercise of any other right hereunder. If any provision of this EULA shall be adjudged by any court of competent jurisdiction to be unenforceable or invalid, that provision shall be limited or eliminated to the minimum extent necessary so that this EULA shall otherwise remain in full force and effect and enforceable.\n\nWARRANTY DISCLAIMER\nLicensor, and author of the software, hereby expressly disclaim any warranty for the software. The software and any related documentation is provided \"As is\" without warranty of any kind, either express or implied, including, without limitation, the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. Licensee accepts any and all risk arising out of use or performance of the software.\n\nLIMITATION OF LIABILITY\nLicensor shall not be liable to licensee, or any other person or entity claiming through licensee any loss of profits, income, savings, or any other consequential, incidental, special, punitive, direct or indirect damage, whether arising in contract, tort, warranty, or otherwise. These limitations shall apply regardless of the essential purpose of any limited remedy. Under no circumstances shall licensor's aggregate liability to licensee, or any other person or entity claiming through licensee, exceed the financial amount actually paid by licensee to licensor for the software.\n\nCONTACTS\nFor additional information regarding this EULA, please contact:\n    • By email: info@i-comit.com\n    • By visiting our site: https://i-comit.com/contact/\n    • By phone number: +1(858) 585-7550");
        jTextArea3.setWrapStyleWord(true);
        jTextArea3.setCaretPosition(0);
        jScrollPane3.setViewportView(jTextArea3);

        jEULAPanel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 38, 498, 132));

        jButton4.setText("I AGREE");
        jButton4.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jEULAPanel.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(433, 176, -1, -1));

        jButton5.setText("I DISAGREE");
        jButton5.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jEULAPanel.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 176, -1, -1));

        getContentPane().add(jEULAPanel);
        jEULAPanel.setBounds(6, 0, 520, 230);

        jEULAPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(Statics.registerCustomFont(16, fontFile));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("TERMS OF USE");
        jEULAPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 6, 498, -1));
        jEULAPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 28, 498, -1));

        jTextArea4.setEditable(false);
        jTextArea4.setColumns(20);
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(5);
        jTextArea4.setText("Last updated: October 28, 2022\n\nPlease read these Terms and Conditions carefully before using Our Service.\n\nINTERPRETATION\nThe words of which the initial letter is capitalized have meanings defined under the following conditions. The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.\nFor the purposes of these Terms and Conditions:\n    • Application means the software program provided by the Company downloaded by You on any electronic device, named i-ncript\n    • Application Store means the digital distribution service operated and developed by Apple Inc. (Apple App Store) or Google Inc. (Google Play Store) in which the Application has been downloaded.\n    • Affiliate means an entity that controls, is controlled by or is under common control with a party, where \"control\" means ownership of 50% or more of the shares, equity interest or other securities entitled to vote for election of directors or other managing authority.\n    • Country refers to: California, United States\n    • Company (referred to as either \"the Company\", \"We\", \"Us\" or \"Our\" in this Agreement) refers to i-comit LLC, 13339 Carriage Rd.\n    • Device means any device that can access the Service such as a computer, a cellphone or a digital tablet.\n    • Feedback means feedback, innovations or suggestions sent by You regarding the attributes, performance or features of our Service.\n    • Service refers to the Application.\n    • Terms and Conditions (also referred as \"Terms\") mean these Terms and Conditions that form the entire agreement between You and the Company regarding the use of the Service.\n    • Third-party Social Media Service means any services or content (including data, information, products or services) provided by a third-party that may be displayed, included or made available by the Service.\n    • You means the individual accessing or using the Service, or the company, or other legal entity on behalf of which such individual is accessing or using the Service, as applicable.\n\nACKNOWLEDGEMENT\nThese are the Terms and Conditions governing the use of this Service and the agreement that operates between You and the Company. These Terms and Conditions set out the rights and obligations of all users regarding the use of the Service.\nYour access to and use of the Service is conditioned on Your acceptance of and compliance with these Terms and Conditions. These Terms and Conditions apply to all visitors, users and others who access or use the Service.\nBy accessing or using the Service You agree to be bound by these Terms and Conditions. If You disagree with any part of these Terms and Conditions then You may not access the Service.\nYou represent that you are over the age of 18. The Company does not permit those under 18 to use the Service.\nYour access to and use of the Service is also conditioned on Your acceptance of and compliance with the Privacy Policy of the Company. Our Privacy Policy describes Our policies and procedures on the collection, use and disclosure of Your personal information when You use the Application or the Website and tells You about Your privacy rights and how the law protects You. Please read Our Privacy Policy carefully before using Our Service.\n\nINTELLECTUAL PROPERTY\nThe Service and its original content (excluding Content provided by You or other users), features and functionality are and will remain the exclusive property of the Company and its licensors.\nThe Service is protected by copyright, trademark, and other laws of both the Country and foreign countries.\nOur trademarks and trade dress may not be used in connection with any product or service without the prior written consent of the Company.\n\nYOUR FEEDBACK TO US\nYou assign all rights, title and interest in any Feedback You provide the Company. If for any reason such assignment is ineffective, You agree to grant the Company a non-exclusive, perpetual, irrevocable, royalty free, worldwide right and license to use, reproduce, disclose, sub-license, distribute, modify and exploit such Feedback without restriction.\nLinks to Other Websites\nOur Service contains links to Our own Company website.\nThe Company has no control over, and assumes no responsibility for, the content, privacy policies, or practices of any third party web sites or services. You further acknowledge and agree that the Company shall not be responsible or liable, directly or indirectly, for any damage or loss caused or alleged to be caused by or in connection with the use of or reliance on any such content, goods or services available on or through any such web sites or services.\nWe strongly advise You to read the terms and conditions and privacy policies of any third-party web sites or services that You visit.\n\nTERMINATION\nWe may terminate or suspend Your access immediately, without prior notice or liability, for any reason whatsoever, including without limitation if You breach these Terms and Conditions.\nUpon termination, Your right to use the Service will cease immediately.\n\nLIMITATION OF LIABILITY\nTo the maximum extent permitted by applicable law, in no event shall the Company or its suppliers be liable for any special, incidental, indirect, or consequential damages whatsoever (including, but not limited to, damages for loss of profits, loss of data or other information, for business interruption, for personal injury, loss of privacy arising out of or in any way related to the use of or inability to use the Service, third-party software and/or third-party hardware used with the Service, or otherwise in connection with any provision of this Terms), even if the Company or any supplier has been advised of the possibility of such damages and even if the remedy fails of its essential purpose.\nSome states do not allow the exclusion of implied warranties or limitation of liability for incidental or consequential damages, which means that some of the above limitations may not apply. In these states, each party's liability will be limited to the greatest extent permitted by law.\n\n\"AS IS\" & \"AS AVAILABLE\" DISCLAIMER\nThe Service is provided to You \"AS IS\" and \"AS AVAILABLE\" and with all faults and defects without warranty of any kind. To the maximum extent permitted under applicable law, the Company, on its own behalf and on behalf of its Affiliates and its and their respective licensors and service providers, expressly disclaims all warranties, whether express, implied, statutory or otherwise, with respect to the Service, including all implied warranties of merchantability, fitness for a particular purpose, title and non-infringement, and warranties that may arise out of course of dealing, course of performance, usage or trade practice. Without limitation to the foregoing, the Company provides no warranty or undertaking, and makes no representation of any kind that the Service will meet Your requirements, achieve any intended results, be compatible or work with any other software, applications, systems or services, operate without interruption, meet any performance or reliability standards or be error free or that any errors or defects can or will be corrected.\nWithout limiting the foregoing, neither the Company nor any of the company's provider makes any representation or warranty of any kind, express or implied: (i) as to the operation or availability of the Service, or the information, content, and materials or products included thereon; (ii) that the Service will be uninterrupted or error-free; (iii) as to the accuracy, reliability, or currency of any information or content provided through the Service; or (iv) that the Service, its servers, the content, or e-mails sent from or on behalf of the Company are free of viruses, scripts, trojan horses, worms, malware, timebombs or other harmful components.\nSome jurisdictions do not allow the exclusion of certain types of warranties or limitations on applicable statutory rights of a consumer, so some or all of the above exclusions and limitations may not apply to You. But in such a case the exclusions and limitations set forth in this section shall be applied to the greatest extent enforceable under applicable law.\n\nGOVERNING LAW\nThe laws of the Country, excluding its conflicts of law rules, shall govern this Terms and Your use of the Service. Your use of the Application may also be subject to other local, state, national, or international laws.\n\nDISPUTES RESOLUTION\nIf You have any concern or dispute about the Service, You agree to first try to resolve the dispute informally by contacting the Company.\n\nFOR EUROPEAN UNION (EU) USERS\nIf You are a European Union consumer, you will benefit from any mandatory provisions of the law of the country in which you are resident in.\nUnited States Legal Compliance\nYou represent and warrant that (i) You are not located in a country that is subject to the United States government embargo, or that has been designated by the United States government as a \"terrorist supporting\" country, and (ii) You are not listed on any United States government list of prohibited or restricted parties.\n\nSEVERABILITY\nIf any provision of these Terms is held to be unenforceable or invalid, such provision will be changed and interpreted to accomplish the objectives of such provision to the greatest extent possible under applicable law and the remaining provisions will continue in full force and effect.\n\nWAIVER\nExcept as provided herein, the failure to exercise a right or to require performance of an obligation under these Terms shall not effect a party's ability to exercise such right or require such performance at any time thereafter nor shall the waiver of a breach constitute a waiver of any subsequent breach.\n\nTRANSLATION INTERPRETATION\nThese Terms and Conditions may have been translated if We have made them available to You on our Service. You agree that the original English text shall prevail in the case of a dispute.\n\nCHANGES TO THESE TERMS AND CONDITIONS\nWe reserve the right, at Our sole discretion, to modify or replace these Terms at any time. If a revision is material We will make reasonable efforts to provide at least 30 days' notice prior to any new terms taking effect. What constitutes a material change will be determined at Our sole discretion.\nBy continuing to access or use Our Service after those revisions become effective, You agree to be bound by the revised terms. If You do not agree to the new terms, in whole or in part, please stop using the website and the Service.\n\nCONTACTS\nIf you have any questions about these Terms and Conditions, You can contact us:\n    • By email: info@i-comit.com\n    • By visiting our site: https://i-comit.com/contact/\n    • By phone number: +1(858) 585-7550");
        jTextArea4.setWrapStyleWord(true);
        jTextArea4.setCaretPosition(0);
        jScrollPane4.setViewportView(jTextArea4);

        jEULAPanel1.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 38, 498, 132));

        jButton6.setText("I AGREE");
        jButton6.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActionjButton6(evt);
            }
        });
        jEULAPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(433, 176, -1, -1));

        jButton7.setText("I DISAGREE");
        jButton7.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jEULAPanel1.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 176, -1, -1));

        getContentPane().add(jEULAPanel1);
        jEULAPanel1.setBounds(6, 0, 520, 230);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(75, 110, 175)));
        jScrollPane5.setPreferredSize(new java.awt.Dimension(225, 160));
        jScrollPane5.setRequestFocusEnabled(false);
        jScrollPane5.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jScrollPane5MouseWheelMoved(evt);
            }
        });

        jTree1.setBackground(new java.awt.Color(57, 57, 57));
        jTree1.setForeground(Color.WHITE);
        jTree1.setModel(TreeView.populateStoreTree(path));
        jTree1.setDragEnabled(true);
        jTree1.setFocusCycleRoot(true);
        jTree1.setLargeModel(true);
        jTree1.setRootVisible(false);
        jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTree1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTree1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTree1MouseExited(evt);
            }
        });
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jTree1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTree1PropertyChange(evt);
            }
        });
        jScrollPane5.setViewportView(jTree1);

        getContentPane().add(jScrollPane5);
        jScrollPane5.setBounds(22, 12, 225, 158);

        jMenuBar1.setFocusable(false);

        jMenu1.setText("i-ncript™");

        jShowServer.setText("toggle server panel");
        jShowServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jShowServerActionPerformed(evt);
            }
        });
        jMenu1.add(jShowServer);

        jToggleDragDrop.setText("move dropped files");
        jToggleDragDrop.setToolTipText("files dropped in store mode will stay at their current directory");
        jToggleDragDrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleDragDropActionPerformed(evt);
            }
        });
        jMenu1.add(jToggleDragDrop);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(775, 234));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    //DECRYPT
    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        try {
            AESMode = 1;
            fileCount = GUI.countFiles(path);
            if (fileCount != 0) {
                this.setSize(779, 266);
                decryptFunction(this);
            } else {
                resetStaticInts();
                GUI.t.interrupt();
                GUI.labelCutterThread(jAlertLabel, "no files to decrypt", 10, 20, 400, false);
                this.setSize(779, 240);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    //LOGIN
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jButton1.getText().equals("ENTER")) {
            try {
                if (Login.loginCheck(this)) {
                    if (!Folder.appLockFile.exists()) {
                        if (Login.verifyLogin()) {
                            initLogin(this);
                        } else {
                            GUI.t.interrupt();
                            GUI.labelCutterThread(jAlertLabel, "username or password is invalid.", 20, 40, 2000, false);
                        }
                    } else {
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "an instance of this app is active.", 20, 40, 2000, false);
                    }
                } else {
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "username or password is invalid.", 20, 40, 2000, false);
                }
                jTextField1.setText("");
                jPasswordField1.setText("");
                jTextField1.requestFocus();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        if (jButton1.getText().equals("RESTART")) {
            jButton1.setToolTipText("apply heap size by closing");
            Memory.changeHeapSize();
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    //FILE HIDER
    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        fileHiderFunction();
    }//GEN-LAST:event_jToggleButton2ActionPerformed
    //ENCRYPT
    private void jRadioButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton0ActionPerformed
        try {
            AESMode = 0;
            fileCount = GUI.countFiles(path);
            if (fileCount != 0) {
                this.setSize(779, 266);
                encryptFunction(this);
            } else {
                resetStaticInts();
                GUI.t.interrupt();
                GUI.labelCutterThread(jAlertLabel, "no files to encrypt", 10, 20, 400, false);
                this.setSize(779, 240);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jRadioButton0ActionPerformed
    //CLEAR LOG
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTextArea1.setText("");
        jButton3.setFocusPainted(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jPasswordField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                if (Login.loginCheck(this)) {
                    if (!Folder.appLockFile.exists()) {
                        if (Login.verifyLogin()) {
                            initLogin(this);
                        } else {
                            GUI.t.interrupt();
                            GUI.labelCutterThread(jAlertLabel, "username or password is invalid.", 20, 40, 2000, false);
                        }
                    } else {
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "an instance of this app is active.", 20, 40, 2000, false);
                    }
                } else {
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "username or password is invalid.", 20, 40, 2000, false);
                }
                jTextField1.setText("");
                jPasswordField1.setText("");
                jTextField1.requestFocus();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_jPasswordField1KeyPressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jEULAPanel.setVisible(false);
        jTextArea4.setCaretPosition(0);
        jEULAPanel1.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void ActionjButton6(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActionjButton6
        loginLabelVisibleBool(true);
        jUsernameLabel.setText("make username");
        jPasswordLabel.setText("make password");
        Memory.getHeapSize(this);
        generateApp();
        jToolPanel.setVisible(false);
        jButton2.setVisible(false);
    }//GEN-LAST:event_ActionjButton6

    //SWITCH STORE/SEND/RECEIVE MODE
    private void jSwitchModeActionEvt(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSwitchModeActionEvt
        switchToolPanels();
    }//GEN-LAST:event_jSwitchModeActionEvt
    //SEND PANEL PW
    private void jPasswordField2Evt(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField2Evt
        if (jPasswordField2.getPassword().length < 5) {
            jLabel6.setVisible(true);
            jLabel5.setVisible(true);
            jRadioButton2.setVisible(false);
        } else {
            jLabel6.setVisible(false);
            jLabel5.setVisible(false);
            jRadioButton2.setVisible(true);
        }
        sendSQLToggle();
    }//GEN-LAST:event_jPasswordField2Evt
    //RECEIVE PANEL PW
    private void jPasswordField3Evt(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField3Evt
        if (jPasswordField3.getPassword().length < 5) {
            jLabel7.setVisible(true);
            jLabel8.setVisible(true);
            jRadioButton3.setVisible(false);
        } else {
            jLabel7.setVisible(false);
            jLabel8.setVisible(false);
            jRadioButton3.setVisible(true);
        }
    }//GEN-LAST:event_jPasswordField3Evt

    //SEND RADIO BUTTON
    private void jRadioButton2Evt(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2Evt
        try {
            zipFileCount = 0;
            zipIter = 0;
            AESMode = 1;
            fileCount = GUI.countFiles(sendFolder);
            if (jTree1.isSelectionEmpty()) {
                if (fileCount == 0) {
                    AESMode = 0;
                    fileCount = GUI.countFiles(sendFolder);
                    zipFileCount = fileCount;
                    jProgressBar1.setMaximum(zipFileCount);
                    contents = sendFolder.toFile().listFiles();
                    List<Path> pathsList = GUI.listPaths(sendFolder);
                    if (contents != null) {
                        if (contents.length != 0 && !pathsList.isEmpty()) {
                            if (Login.sendKeyCheck(jTree1.isSelectionEmpty())) {
                                this.setSize(779, 266);
                            } else {
                                resetSendTools(2);
                                this.setSize(779, 240);
                            }
                        } else {
                            GUI.t.interrupt();
                            GUI.labelCutterThread(jAlertLabel, "o-box folder has no files", 20, 40, 1200, false);
                            resetSendTools(2);
                            this.setSize(779, 240);
                        }
                    } else {
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "o-box folder does not exist", 20, 40, 1200, false);
                        resetSendTools(2);
                        this.setSize(779, 240);
                    }
                } else {
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "o-box can't contain .enc files", 20, 20, 1200, false);
                    resetSendTools(2);
                    this.setSize(779, 240);

                }
            } else {
                AESMode = 0;
                fileCount = jTree1.getSelectionPaths().length;
                zipFileCount = fileCount;
                jProgressBar1.setMaximum(zipFileCount);
                contents = sendFolder.toFile().listFiles();
                if (Login.sendKeyCheck(jTree1.isSelectionEmpty())) {
                    this.setSize(779, 266);
                } else {
                    resetSendTools(2);
                    this.setSize(779, 240);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jRadioButton2Evt
    //RECEIVE RADIO BTN
    private void jRadioButton3Evt(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3Evt
        zipFileCount = 0;
        zipIter = 0;
        if (Login.verifySendKey(receiveFolder + File.separator + jList1.getSelectedValue())) {
            this.setSize(779, 266);
        } else {
            this.setSize(779, 240);
        }
    }//GEN-LAST:event_jRadioButton3Evt

    //JTREE
    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        try {
            TreeView.getFileCreationNSize();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jTree1ValueChanged

    private void jTree1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseClicked
        if (evt.getClickCount() == 2) {
            if (jTree1.getSelectionPaths() != null) {
                GUI.t.interrupt();
                TreeView.openFile(jTree1.getSelectionPath());
            }
        }
        sendSQLToggle();
    }//GEN-LAST:event_jTree1MouseClicked
    //HOT FILER
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        hotFilerFunction(this);
    }//GEN-LAST:event_jToggleButton1ActionPerformed
    //STOP
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        stopFunction(this);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTree1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseExited
        TreeView.storeExpandedNodes(toolMode);
        TreeView.storeNodeCaretPos(Statics.toolMode);
        jCreationDateLabel.setText("");
        jFileSizeLabel.setText("");
        if (toolMode != 2)
            if (jTree1.getSelectionPaths() != null) {
                jTree1.clearSelection();
            }
    }//GEN-LAST:event_jTree1MouseExited

    public static boolean progressbarBool = false;

    private void jProgressBar1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jProgressBar1StateChanged
        if (progressbarBool) {
            this.setSize(779, 240);
            switch (Statics.toolMode) {
                case 0 ->
                    refreshTreeView(path, TreeView.nodeCaretPos);
                case 1 ->
                    refreshTreeView(receiveFolder, TreeView.receiveCaretPos);
                case 2 ->
                    refreshTreeView(sendFolder, TreeView.sendCaretPos);
                case 3 ->
                    refreshTreeView(path, TreeView.nodeCaretPos);
            }
            progressbarBool = false;
        } else {
            this.setSize(779, 266);
        }
    }//GEN-LAST:event_jProgressBar1StateChanged

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        if (!jToolPanel.isFocusOwner() && jStorePanel.isVisible()) {
            jToolPanel.requestFocus();
        }
    }//GEN-LAST:event_formMouseMoved

    private void jLabel3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseEntered
        jLabel3.setText("VER " + appVer);
    }//GEN-LAST:event_jLabel3MouseEntered

    private void jLabel3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseExited
        jLabel3.setText(root.substring(0, 2) + " " + GB);
    }//GEN-LAST:event_jLabel3MouseExited

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        Memory.selectedHeap = jSlider1.getValue();
        jHeapLabel.setText(Memory.selectedHeap + "GB heap");
        if (Memory.selectedHeap == Memory.currentHeap) {
            jButton1.setToolTipText("log into i-ncript");
            jButton1.setText("ENTER");
            jTextField1.setEnabled(true);
            jPasswordField1.setEnabled(true);
        } else {
            jButton1.setText("RESTART");
            jButton1.setToolTipText("apply heap size by restarting");
            jTextField1.setEnabled(false);
            jPasswordField1.setEnabled(false);
        }
    }//GEN-LAST:event_jSlider1StateChanged

    private void jTree1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseEntered
        if (jTree1.isEnabled()) {
            switch (toolMode) {
                case 0 ->
                    refreshTreeView(path, TreeView.nodeCaretPos);
                case 1 -> {
                    refreshTreeView(receiveFolder, TreeView.receiveCaretPos);
                    Folder.listZipFiles();
                }
                case 2 ->
                    refreshTreeView(sendFolder, TreeView.sendCaretPos);
                case 3 ->
                    refreshTreeView(path, TreeView.nodeCaretPos);
            }
        }
    }//GEN-LAST:event_jTree1MouseEntered

    private void jTree1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTree1PropertyChange
        if (evt.getPropertyName().equals("enabled")) {
            if (jTree1.isEnabled()) {
                switch (toolMode) {
                    case 0 ->
                        refreshTreeView(path, TreeView.nodeCaretPos);
                    case 1 -> {
                        refreshTreeView(receiveFolder, TreeView.receiveCaretPos);
                        Folder.listZipFiles();
                    }
                    case 2 ->
                        refreshTreeView(sendFolder, TreeView.sendCaretPos);
                    case 3 ->
                        refreshTreeView(path, TreeView.nodeCaretPos);
                }
            }
        }
    }//GEN-LAST:event_jTree1PropertyChange

    private void jScrollPane5MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jScrollPane5MouseWheelMoved
        if (jScrollPane5.getVerticalScrollBar().getValue() == 1 && evt.getWheelRotation() == -1) {
            jScrollPane5.getVerticalScrollBar().setValue(0);
        }
    }//GEN-LAST:event_jScrollPane5MouseWheelMoved

    public static boolean mouseOverLog = false;

    private void jTextArea1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseEntered
        mouseOverLog = true;
    }//GEN-LAST:event_jTextArea1MouseEntered

    private void jTextArea1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseExited
        mouseOverLog = false;
    }//GEN-LAST:event_jTextArea1MouseExited

    private void jSendSQLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSendSQLActionPerformed
        new Thread(() -> {
            try {
                if (jTree1.getSelectionPaths() != null) {
                    if (TreeView.treePathContainsDirs(jTree1.getSelectionPaths())) {
                        if (Client.getTable(Main.jTextField2.getText().trim())) {
                            List<Path> paths = TreeView.convertTreePathToPath(jTree1.getSelectionPaths());
                            jAlertLabel.setText("");
                            Main.jTextField2.setEnabled(false);
                            Main.jPasswordField2.setEnabled(false);
                            for (Path path : paths) {
                                Client.postRecords(Main.jTextField2.getText().trim(), path.toFile());
                                path.toFile().delete();
                                refreshTreeView(sendFolder, TreeView.sendCaretPos);
                            }
                            GUI.t.interrupt();
                            GUI.labelCutterThread(jAlertLabel, paths.size() + " files sent to " + Main.jTextField2.getText().trim(), 20, 25, 1000, false);
                            Main.jTextArea1.append(paths.size() + " files sent to " + Main.jTextField2.getText().trim() + " at " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n\n");
                            Statics.resetSendTools(2);
                        } else {
                            Statics.resetSendTools(2);
                            GUI.t.interrupt();
                            GUI.labelCutterThread(jAlertLabel, "user is not in this network", 20, 25, 1000, false);
                        }
                    } else {
                        Statics.resetSendTools(2);
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "you can only send files", 20, 25, 1000, false);
                    }
                }
            } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                Statics.resetSendTools(2);
                GUI.t.interrupt();
                GUI.labelCutterThread(jAlertLabel, "host has disconnected.", 25, 50, 1250, false);
            }
        }
        ).start();
    }//GEN-LAST:event_jSendSQLActionPerformed

    public static void sendSQLToggle() {
        if (!adminBool) {
            if (!Main.jClientIPInput.getText().equals("000.000.0.000")) {
                if (jTextField2.getText().length() < 4) {
                    jSendSQL.setVisible(false);
                    jSendSQL.setSelected(false);
                    jLabel5.setVisible(true);
                } else {
                    jSendSQL.setVisible(true);
                    jSendSQL.setSelected(false);
                    jLabel5.setVisible(false);
                }
            } else {
                jSendSQL.setVisible(false);
            }
        } else {
            if (jTextField2.getText().length() < 4) {
                jSendSQL.setVisible(false);
                jSendSQL.setSelected(false);
                jLabel5.setVisible(true);
            } else {
                jSendSQL.setVisible(true);
                jSendSQL.setSelected(false);
                jLabel5.setVisible(false);
            }
        }
    }
    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
        sendSQLToggle();
    }//GEN-LAST:event_jTextField2KeyTyped
    public static boolean toggleDragDropBool = false;
    private void jToggleDragDropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleDragDropActionPerformed
        toggleDragDropBool ^= true;
        if (toggleDragDropBool) {
            jToggleDragDrop.setToolTipText("file(s) dropped in store mode will be kept in their current folder");
        } else {
            jToggleDragDrop.setToolTipText("files(s) dropped in store mode will be moved to the vault folder");
        }
    }//GEN-LAST:event_jToggleDragDropActionPerformed

    private void jShowServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jShowServerActionPerformed
        Client.adminRequest(0);
    }//GEN-LAST:event_jShowServerActionPerformed

    private void jServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jServerButtonActionPerformed
        startServer();
    }//GEN-LAST:event_jServerButtonActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        try {
            //</editor-fold>
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            UIManager.put("ProgressBar.selectionForeground", Color.black);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public synchronized void run() {
                    System.out.println("shutdown hook initiated.");
                    Folder.appLockBool = false;
                    Folder.appLockFile.delete();
                    if (!username.equals("") && jToolPanel.isVisible()) {
                        Client.endSession(username);
                    }
                    if (adminBool) {
                        Server.portKill();
                        Server.serverKill("server.exe", true);
                    }
                }
            });
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.ButtonGroup buttonGroup1;
    protected static javax.swing.JPanel dragDrop;
    public static javax.swing.JLabel jAdminLabel;
    public static javax.swing.JLabel jAlertLabel;
    public javax.swing.JButton jButton1;
    protected static javax.swing.JButton jButton2;
    private static javax.swing.JButton jButton3;
    protected static javax.swing.JButton jButton4;
    protected static javax.swing.JButton jButton5;
    protected static javax.swing.JButton jButton6;
    protected static javax.swing.JButton jButton7;
    public static javax.swing.JFormattedTextField jClientIPInput;
    public static javax.swing.JLabel jCreationDateLabel;
    protected static javax.swing.JPanel jEULAPanel;
    protected static javax.swing.JPanel jEULAPanel1;
    public static javax.swing.JLabel jFileSizeLabel;
    protected static javax.swing.JLabel jHeapLabel;
    protected static javax.swing.JLabel jLabel1;
    protected static javax.swing.JLabel jLabel10;
    protected static javax.swing.JLabel jLabel11;
    protected static javax.swing.JLabel jLabel12;
    protected static javax.swing.JLabel jLabel13;
    protected static javax.swing.JLabel jLabel14;
    protected static javax.swing.JLabel jLabel2;
    protected static javax.swing.JLabel jLabel3;
    protected static javax.swing.JLabel jLabel4;
    protected static javax.swing.JLabel jLabel5;
    protected static javax.swing.JLabel jLabel6;
    protected static javax.swing.JLabel jLabel7;
    protected static javax.swing.JLabel jLabel8;
    protected static javax.swing.JLabel jLabel9;
    protected static javax.swing.JList<String> jList1;
    public static javax.swing.JPanel jLoginPanel;
    protected static javax.swing.JMenu jMenu1;
    protected static javax.swing.JMenuBar jMenuBar1;
    protected static javax.swing.JPanel jPanel1;
    protected static javax.swing.JPanel jPanel2;
    protected static javax.swing.JPanel jPanel3;
    protected static javax.swing.JPanel jPanel4;
    public static javax.swing.JPasswordField jPasswordField1;
    protected static javax.swing.JPasswordField jPasswordField2;
    protected static javax.swing.JPasswordField jPasswordField3;
    protected javax.swing.JLabel jPasswordLabel;
    public static javax.swing.JProgressBar jProgressBar1;
    protected static javax.swing.JProgressBar jProgressBar2;
    protected static javax.swing.JRadioButton jRadioButton0;
    protected static javax.swing.JRadioButton jRadioButton1;
    protected static javax.swing.JRadioButton jRadioButton2;
    protected static javax.swing.JRadioButton jRadioButton3;
    protected static javax.swing.JPanel jReceivePanel;
    protected static javax.swing.JScrollPane jScrollPane1;
    protected static javax.swing.JScrollPane jScrollPane2;
    protected static javax.swing.JScrollPane jScrollPane3;
    protected static javax.swing.JScrollPane jScrollPane4;
    protected static javax.swing.JScrollPane jScrollPane5;
    protected static javax.swing.JScrollPane jScrollPane6;
    protected static javax.swing.JScrollPane jScrollPane7;
    protected static javax.swing.JPanel jSendPanel;
    protected static javax.swing.JRadioButton jSendSQL;
    protected static javax.swing.JSeparator jSeparator1;
    protected static javax.swing.JSeparator jSeparator2;
    protected static javax.swing.JButton jServerButton;
    protected static javax.swing.JMenuItem jShowServer;
    protected static javax.swing.JSlider jSlider1;
    protected static javax.swing.JPanel jStorePanel;
    protected static javax.swing.JButton jSwitchMode;
    protected static javax.swing.JTabbedPane jTabbedPane1;
    protected static javax.swing.JTextArea jTextArea1;
    protected static javax.swing.JTextArea jTextArea2;
    protected static javax.swing.JTextArea jTextArea3;
    protected static javax.swing.JTextArea jTextArea4;
    protected static javax.swing.JTextArea jTextArea6;
    public static javax.swing.JTextField jTextField1;
    public static javax.swing.JTextField jTextField2;
    public static javax.swing.JToggleButton jToggleButton1;
    protected static javax.swing.JToggleButton jToggleButton2;
    protected static javax.swing.JRadioButtonMenuItem jToggleDragDrop;
    public static javax.swing.JPanel jToolPanel;
    protected static javax.swing.JTree jTree1;
    protected static javax.swing.JLabel jUsernameLabel;
    // End of variables declaration//GEN-END:variables

}
