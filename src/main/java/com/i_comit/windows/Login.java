/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jPasswordField1;
import static com.i_comit.windows.Main.jTextField1;
import static com.i_comit.windows.Main.root;
import static com.i_comit.windows.Statics.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Login {

    public static void loginCheck() {
        char[] password = jPasswordField1.getPassword();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        Statics.username = jTextField1.getText();
        Statics.password = new String(password);
        if (!"".equals(Statics.username)) {
            if (!"".equals(Statics.password)) {
                if (Statics.username.length() >= 4) {
                    if (Statics.password.length() >= 4) {
                        Login.Authenticator();
                    } else {
                        GUI.labelCutterThread(jAlertLabel, "please make a longer password", 20, 20, 1200);
                    }
                } else {
                    GUI.labelCutterThread(jAlertLabel, "please make a longer username", 20, 20, 1200);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "please make a password", 20, 20, 1200);
            }
        } else {
            GUI.labelCutterThread(jAlertLabel, "please make a username", 20, 20, 1200);
        }
        jTextField1.setText("");
        jPasswordField1.setText("");
    }

    public static void Authenticator() {
        if (keyFile.exists()) {
            verifyPassword();
        } else {
            Main.jProgressBar1.setVisible(true);
            GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript", 30, 30, 600);
            makeKey();
        }
    }

    public static void makeKey() {
        Path path = Paths.get(root + Main.masterFolder + keyName); //creates Path instance  
        try {
            List<String> lines = Arrays.asList(Hasher.modHash(username), Hasher.modHash(password));
            Path p = Files.createFile(path);//creates file at specified location  
            //System.out.println("Key generated at: " + p);
            //Files.writeString(path, st.toString());
            Files.write(path, lines);
            Files.setAttribute(p, "dos:hidden", true);
            Main.jLoginPanel.setVisible(false);
            Main.jToolPanel.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void verifyPassword() {
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(keyFile));
            String usernameRead = brTest.readLine();
            String passwordRead = brTest.readLine();

            if (usernameRead.equals(Hasher.modHash(username))) {
                if (passwordRead.equals(Hasher.modHash(password))) {
                    Main.jLoginPanel.setVisible(false);
                    Main.jToolPanel.setVisible(true);
                    Main.jProgressBar1.setVisible(true);
                    GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript", 45, 30, 900);
                    Main.dragDropper();

                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "incorrect login info", 45, 30, 900);

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
