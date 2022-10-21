/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Statics.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Login {

    public static void Authenticator() {
        if (keyFile.exists()) {
            verifyPassword();
        } else {
            Main.jProgressBar1.setVisible(true);
            GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript", 45, 30, 900);
            makeKey();
        }
    }

    public static void makeKey() {
        Path path = Paths.get(root + keyName); //creates Path instance  
        try {
            List<String> lines = Arrays.asList(Hasher.modHash(username), Hasher.modHash(password));
            Path p = Files.createFile(path);//creates file at specified location  
            //System.out.println("Key generated at: " + p);
            //Files.writeString(path, st.toString());
            Files.write(path, lines);
            if (System.getProperty("os.name").equals("windows")) {
                Files.setAttribute(p, "dos:hidden", true);
            }
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
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "incorrect login info", 45, 30, 900);

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
