/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.gui;

import static com.i_comit.windows.gui.Globals.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Login {

    public static void makePassword() {
        File tmpDir = new File(root + "\\key.i-comit");
        if (tmpDir.exists()) {
            verifyDrive();
        } else {
            //Scanner myObj = new Scanner(System.in);
            System.out.println("1st Time Use, Create password: ");
            //String pw1 = myObj.nextLine();  // Read user input
            int pw2 = pw.hashCode();
            pw = String.valueOf(noNegatives(pw2));
            System.out.println("pw from Login: "+pw);
            //makeKey();
            //MakeFolder.CreateFolder();

        }
    }

    public static void makeKey() {
        List<String> serialNumbers = new ArrayList<>();
        String command = "wmic diskdrive where InterfaceType='USB' get SerialNumber";
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            reader.readLine();
            while ((s = reader.readLine()) != null) {
                //System.out.println(s);
                serialNumbers.add(s.trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serialNumbers.removeAll(Arrays.asList("", null));
        Path path = Paths.get(root + "\\key.i-comit"); //creates Path instance  

        try {
            Integer st = serialNumbers.get(0).hashCode();
            List<String> lines = Arrays.asList(noNegatives(st).toString(), pw);

            Path p = Files.createFile(path);//creates file at specified location  
            System.out.println("Key generated at: " + p);
            //Files.writeString(path, st.toString());
            Files.write(path, lines);
            Files.setAttribute(p, "dos:hidden", true);

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

    public static void verifyDrive() {
        List<String> serialNumbers = new ArrayList<>();
        String command = "wmic diskdrive where InterfaceType='USB' get SerialNumber";
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            reader.readLine();
            s = reader.readLine();
            while ((s = reader.readLine()) != null) {
                //System.out.println(s);
                Integer snInt = noNegatives(s.trim().hashCode());
                String snStr = snInt.toString();
                serialNumbers.add(snStr);

            }
            serialNumbers.removeAll(Arrays.asList("0", null));
            reader.close();

            BufferedReader brTest = new BufferedReader(new FileReader(root + "\\key.i-comit"));
            String text;
            text = brTest.readLine();
            if (serialNumbers.stream().anyMatch(text::contains)) {
                System.out.println("Drive Match");
                verifyPassword();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void verifyPassword() throws FileNotFoundException {
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(root + "\\key.i-comit"));
            String text;
            brTest.readLine();
            text = brTest.readLine();

            Scanner myObj = new Scanner(System.in);
            System.out.println("Enter password: ");
            String pw1 = myObj.nextLine();  // Read user input
            int pw2 = pw1.hashCode();
            pw = String.valueOf(noNegatives(pw2));
            if (pw.equals(text)) {
                System.out.println("Password Match");
                text = pw;
                boolean enc = Paths.get(Globals.root + Globals.folderName + ".enc").toFile().exists();
                if (!enc && emptyDirectory) {
                    System.out.println("Please fill encrypted-folder first");
                    System.exit(0);
                } else {
                    //ZipFolder.AESQuery();

                }
                //ZipFolder.AESQuery();

            } else {
                System.out.println("Password Mismatch");

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void getGB() {
        try {
            File diskPartition = new File(root);
            GB = diskPartition.getUsableSpace() / (1024 * 1024 * 1024);
            String str ="Drive " + root.substring(0, 2) + " | " + GB + "GB";
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
