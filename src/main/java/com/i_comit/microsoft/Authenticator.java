/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.microsoft;

import static com.i_comit.microsoft.Globals.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */

public class Authenticator {

    public static void main(String[] args) {
        File root[] = File.listRoots();
        System.out.println("Directory: "+directory);

        // check if the root is null or not
        if (root != null) {
            // display the roots of the path name
            for (int i = 0; i < root.length; i++) {
                //System.out.print(root[i].getPath() + " \n");
            }
        } else {
            System.out.println("There are no roots");
        }
        makePassword();
        getGB();
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
                System.out.println(s);
                serialNumbers.add(s.trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serialNumbers.removeAll(Arrays.asList("", null));
        System.out.println(serialNumbers);
        System.out.println("root: " + root);
        Path path = Paths.get(root + "\\.key.i-comit"); //creates Path instance  

        try {
            Integer st = serialNumbers.get(0).hashCode();
            List<String> lines = Arrays.asList(noNegatives(st).toString(), pw);
            System.out.println(lines);

            Path p = Files.createFile(path);//creates file at specified location  
            System.out.println("File Created at Path: " + p);
            //Files.writeString(path, st.toString());
            Files.write(path, lines);
            //Files.setAttribute(p, "dos:hidden", true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makePassword() {
        File tmpDir = new File(root + "\\.key.i-comit");
        if (tmpDir.exists()) {
            verifyDrive();
        } else {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Create password: ");
            String pw1 = myObj.nextLine();  // Read user input
            int pw2 = pw1.hashCode();
            pw = String.valueOf(noNegatives(pw2));
            //System.out.println("Password hashed");
            makeKey();
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

            BufferedReader brTest = new BufferedReader(new FileReader(root + "\\.key.i-comit"));
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
            BufferedReader brTest = new BufferedReader(new FileReader(root + "\\.key.i-comit"));
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
                MakeFolder makeFolder = new MakeFolder();
                makeFolder.CreateFolder();
            } else {
                System.out.println("Password Mismatch");

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void getGB() {
        try {
            File[] files = f.listFiles();
            long GB = Math.floorDiv(f.getTotalSpace(), 1000000000);
            System.out.println(GB + "GB USB");
            // Display the names of the files
            for (int i = 1; i < files.length; i++) {
                //System.out.println(files[i].getName());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
