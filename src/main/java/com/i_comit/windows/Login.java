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
            makeKey();
            //MakeFolder.CreateFolder();

        }
    }

    public static void makeKey() {
        Path path = Paths.get(root + "\\i-ncript.key"); //creates Path instance  
        try {
            List<String> lines = Arrays.asList(Hasher.modHash(username), Hasher.modHash(password));
            Path p = Files.createFile(path);//creates file at specified location  
            System.out.println("Key generated at: " + p);
            //Files.writeString(path, st.toString());
            Files.write(path, lines);
            Files.setAttribute(p, "dos:hidden", true);
            Main.jLoginPanel.setVisible(false);
            Main.jToolPanel.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Integer noNegatives(Integer negativeInt) {
        if (negativeInt < 0) {
            negativeInt *= -1;
        }
        return negativeInt;
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
                    GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript", 30, 900);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "incorrect login info", 30, 900);

            }

//            if (password.equals(text)) {
//                System.out.println("Password Match");
//                text = password;
//                boolean enc = Paths.get(Statics.root + Statics.folderName + ".enc").toFile().exists();
//                if (!enc && emptyDirectory) {
//                    System.out.println("Please fill encrypted-folder first");
//                    System.exit(0);
//                } else {
//                    //ZipFolder.AESQuery();
//
//                }
//                //ZipFolder.AESQuery();
//
//            } else {
//                System.out.println("Password Mismatch");
//
//            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
