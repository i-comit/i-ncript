/*
 * Copyright (C) 2022 Khiem Luong <khiemluong@i-comit.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.i_comit.server;

import com.i_comit.windows.GUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Main extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    private URL fontFile = getClass().getResource("/polentical-neon.ttf");

    private static int second = 0;
    private static String timeString = "";
    private final String appVer = "1.1.0";
    public static Executor executor = Executors.newSingleThreadScheduledExecutor();
    Server.Sessions sessions = new Server.Sessions();
    Server.Admin admin = new Server.Admin();
    Popup popup;

    public Main() {
        try {
            if (Server.serverSocket == null) {
                initServer();
                Server.socketStart(this);
            } else {
                Server.portKill();
                Server.appKill(".server.exe", false);
                Server.appKill("i-ncript.exe", true);
                Server.serverSocket.close();
                initServer();
                Server.socketStart(this);
            }

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
            Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
            int y = (int) rect.getMaxY() / 2 - this.getHeight() + this.getHeight() / 3;
            this.setLocation((int) rect.getMaxX() - this.getWidth() - this.getWidth() / 4, y);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Font customFont;

    public static Font registerCustomFont(float fontSize, URL fontFile) {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile.openStream()).deriveFont(fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return customFont;
    }

    private void initServer() throws UnsupportedEncodingException {
        initComponents();
        uptimeTimer();
        this.setBackground(new Color(0, 0, 0, 0));
        jTextArea1.setBackground(new Color(0, 0, 0, (float) 0.5));
        jPanel1.setBackground(new Color(0, 0, 0, (float) 0.5));
        jPanel3.setBackground(new Color(0, 0, 0, 0));
        jTabbedPane1.setBackground(new Color(0, 0, 0, (float) 0.5));
        jButton5.setVisible(false);
        Server.initDatabase();
        sessions.clearSessions();
        admin.checkAvailableSpace(0);
        if (admin.userCount == 1) {
            Main.jLabel7.setText(admin.countTables() + " user in network");
        } else if (admin.userCount == 0) {
            Main.jLabel7.setText("no user in network");
        }
        if (admin.userCount > 1) {
            Main.jLabel7.setText(admin.countTables() + " users in network");
        }
        GUI.labelCutterThread(jAlertLabel, "", 10, 10, 10, false);
    }

    private void uptimeTimer() {
        executor.execute(() -> {
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    second++;
                    int hours = second / 3600;
                    int minutes = (second % 3600) / 60;
                    int seconds = second % 60;
                    if (second < 60) {
                        timeString = String.format("%2d seconds", seconds);
                    } else if (second >= 3600) {
                        timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    } else {
                        timeString = String.format("%2d mins %2d secs".trim(), minutes, seconds);
                    }
                    jLabel4.setText(timeString);
                }
            };
            timer.schedule(timerTask,
                    1, 1000);
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jButton5 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jAppVerLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jAlertLabel = new javax.swing.JLabel();

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 106, 146), 2));
        jPanel4.setFocusable(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(280, 260));

        jLabel8.setFont(registerCustomFont(13, fontFile));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("i-ncript server user manual");

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("Last Updated: 01/10/2023\n\n[OVERVIEW OF TABS]:\nThe i-ncript server panel has 3 tabs: LOG, SYS, and UTIL, representing as the activity log output, system properties, and server utility tools respectively.\n\nLOG:\nThe LOG panel tracks any activity detected by the server, such as when a user has started or ended a session, or what files have been uploaded to or retrieved from the server.\n\nSYS:\nThe SYS, or System panel, provides data about the server state itself. Such as how much space is used and available in the server (the available server space is based on the total usable space of the USB that the server is in divided by 3), how many users are in the network and how much memory is allocated per user (which is the available server space divided by how many users are in the network).\n\nUTIL:\nThe UTIL, or Utility panel, has 4 buttons listed below:\n- HELP\nThis is the button which shows the user manual, the one being read right now.\n- RELOCATE\nThis button allows the title bar of the server panel to be visible, allowing you to relocate the panel to a different location. Once you are satisfied with your new location, you can press the SET LOCATION button that appears during this state, which will convert the server panel back to an undecorated state at the new location.\n- CLR LOG\nClears the output log in the LOG panel\n- CLR SERVER\nClears all data from the server and repacks the database, optimizing space. Since this is action should be done with caution you must double click this button to run the command.\n");
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setCaretPosition(0);
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setFocusable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/i-comiti.png")));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(280, 156));
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        jButton5.setBackground(new java.awt.Color(51, 51, 51));
        jButton5.setText("SET LOCATION");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 106, 146), 2));
        jPanel3.setOpaque(false);

        jLabel3.setFont(registerCustomFont(12, fontFile));
        jLabel3.setForeground(new java.awt.Color(128, 128, 128));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("up time: ");

        jLabel4.setFont(registerCustomFont(12, fontFile));
        jLabel4.setForeground(new java.awt.Color(128, 128, 128));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(timeString);

        jLabel1.setFont(registerCustomFont(12, fontFile));
        jLabel1.setForeground(new java.awt.Color(128, 128, 128));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(Server.getIP());

        jLabel2.setFont(registerCustomFont(12, fontFile));
        jLabel2.setForeground(new java.awt.Color(128, 128, 128));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("i-ncript™ server");

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setFocusable(false);
        jTabbedPane1.setFont(registerCustomFont(12, fontFile));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(256, 100));

        jScrollPane1.setBorder(null);
        jScrollPane1.setOpaque(false);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(51, 51, 51));
        jTextArea1.setColumns(18);
        jTextArea1.setRows(5);
        jTextArea1.setFocusable(false);
        jTextArea1.setMargin(new java.awt.Insets(2, 3, 2, 3));
        jScrollPane1.setViewportView(jTextArea1);

        jTabbedPane1.addTab("LOG", jScrollPane1);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel5.setFont(registerCustomFont(11, fontFile));
        jLabel5.setForeground(new java.awt.Color(68, 110, 158));
        jLabel5.setText("bytes availalbe");

        jLabel6.setFont(registerCustomFont(11, fontFile));
        jLabel6.setText("bytes per user");

        jLabel7.setFont(registerCustomFont(11, fontFile));
        jLabel7.setText("x user(s) in network");

        jAppVerLabel.setFont(registerCustomFont(11, fontFile));
        jAppVerLabel.setForeground(new java.awt.Color(68, 110, 158));
        jAppVerLabel.setText("ver: "+appVer + " - © i-comit LLC");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jAppVerLabel))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAppVerLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("SYS", jPanel1);

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));

        jButton1.setBackground(new java.awt.Color(51, 51, 51));
        jButton1.setText("CLR LOG");
        jButton1.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton1.setMaximumSize(new java.awt.Dimension(77, 24));
        jButton1.setMinimumSize(new java.awt.Dimension(77, 24));
        jButton1.setPreferredSize(new java.awt.Dimension(77, 24));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(51, 51, 51));
        jButton2.setText("CLR SERVER");
        jButton2.setToolTipText("clear all files in the database & compress it");
        jButton2.setMargin(new java.awt.Insets(2, 14, 2, 14));
        jButton2.setMaximumSize(new java.awt.Dimension(95, 24));
        jButton2.setMinimumSize(new java.awt.Dimension(95, 24));
        jButton2.setName(""); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(95, 24));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(51, 51, 51));
        jButton3.setText("RELOCATE");
        jButton3.setToolTipText("allows the server panel to be  movable");
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setMargin(new java.awt.Insets(2, 6, 2, 6));
        jButton3.setMaximumSize(new java.awt.Dimension(63, 24));
        jButton3.setMinimumSize(new java.awt.Dimension(63, 24));
        jButton3.setPreferredSize(new java.awt.Dimension(63, 24));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(51, 51, 51));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/help-icon_1.png"))); // NOI18N
        jButton4.setText("HELP");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jAlertLabel.setFont(registerCustomFont(12, fontFile));
        jAlertLabel.setForeground(new java.awt.Color(68, 110, 158));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAlertLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jAlertLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("UTIL", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(122, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap(151, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(105, Short.MAX_VALUE)))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel3)
                    .addContainerGap(213, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(130, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(30, Short.MAX_VALUE)))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addContainerGap(131, Short.MAX_VALUE)))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2)
                    .addContainerGap(131, Short.MAX_VALUE)))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap(131, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(80, 80, 80)
                    .addComponent(jButton5)
                    .addContainerGap(89, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jButton5)
                    .addContainerGap(127, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    //CLR LOG
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTextArea1.setText("");
        GUI.t.interrupt();
        GUI.labelCutterThread(jAlertLabel, "output log cleared.", 0, 20, 1200, false);
    }//GEN-LAST:event_jButton1ActionPerformed

    //CLR SERVER
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (jAlertLabel.getText().equals("")) {
            GUI.t.interrupt();
            GUI.labelCutterThread(jAlertLabel, "click once more to confirm.", 0, 20, 800, false);
        } else {
            admin.clearServer();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    //RELOCATE
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.dispose();
        this.setBackground(Color.GRAY);
        this.setUndecorated(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(0);
        jPanel3.setVisible(false);
        jButton5.setVisible(true);
        this.setSize(282, 95);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.dispose();
        this.setUndecorated(true);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setVisible(true);
        jPanel3.setVisible(true);
        jButton5.setVisible(false);
        this.setSize(282, 155);
        GUI.t.interrupt();
        GUI.labelCutterThread(jAlertLabel, "server panel moved.", 0, 20, 1200, false);
    }//GEN-LAST:event_jButton5ActionPerformed

    private boolean popupBool = false;
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (!popupBool) {
            PopupFactory pf = new PopupFactory();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
            Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
            int x = (int) rect.getMaxX() - this.getWidth() - this.getWidth() / 4;
            int y = (int) rect.getMaxY() / 2 - this.getHeight() + this.getHeight() * 2;
            popup = pf.getPopup(this, jPanel4, x, y);
            popup.show();
            System.out.println("true");
            popupBool = true;
        } else {
            popup.hide();
            System.out.println("false");
            popupBool = false;
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        try {
            //</editor-fold>
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Server.portKill();
            new Main().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected static javax.swing.JLabel jAlertLabel;
    protected static javax.swing.JLabel jAppVerLabel;
    protected static javax.swing.JButton jButton1;
    protected static javax.swing.JButton jButton2;
    protected static javax.swing.JButton jButton3;
    protected static javax.swing.JButton jButton4;
    protected static javax.swing.JButton jButton5;
    protected static javax.swing.JLabel jLabel1;
    protected static javax.swing.JLabel jLabel2;
    protected static javax.swing.JLabel jLabel3;
    protected static javax.swing.JLabel jLabel4;
    protected static javax.swing.JLabel jLabel5;
    protected static javax.swing.JLabel jLabel6;
    protected static javax.swing.JLabel jLabel7;
    protected static javax.swing.JLabel jLabel8;
    protected static javax.swing.JPanel jPanel1;
    protected static javax.swing.JPanel jPanel2;
    protected static javax.swing.JPanel jPanel3;
    protected static javax.swing.JPanel jPanel4;
    protected static javax.swing.JScrollPane jScrollPane1;
    protected static javax.swing.JScrollPane jScrollPane2;
    protected static javax.swing.JSeparator jSeparator1;
    protected static javax.swing.JTabbedPane jTabbedPane1;
    protected static javax.swing.JTextArea jTextArea1;
    protected static javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}
