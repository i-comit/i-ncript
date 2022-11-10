package com.i_comit.windows;

import java.util.Random;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Hasher {

    public static String getHash(String hash, boolean hashBool) {
        String hash2 = finalizeHash(hash, hashBool).substring(hash.length() * 32, (hash.length() + 1) * 32).trim();
        return hash2;
    }

    public static String finalizeHash(String hash, boolean hashBool) {
        String s1 = joinHash(hash, hashBool);
        final int mid = s1.length() / 4; //get the middle of the String
        String[] parts = {s1.substring(0, mid), s1.substring(mid, mid * 2), s1.substring(mid * 2, mid * 3), s1.substring(mid * 3, mid * 4)};
        if (hash.length() < 10) {
            StringBuilder sb = new StringBuilder();
            //Convert to 32bit
            for (int i = 0; i < hash.length() * 4; i++) {
                Random rand = new Random();
                // Generate random 8bit int
                int rand_int1 = rand.nextInt(10000000, 99999999);
                sb.append(String.valueOf(rand_int1));
            }

            sb.append(joinHash(hash, hashBool));

            int remainder = 15 - hash.length();
            Random rand = new Random();
            if (remainder != 0) {
                for (int i = 0; i < remainder * rand.nextInt(3, 99); i++) {
                    Random rand2 = new Random();
                    // Generate random 8bit int
                    int rand_int1 = rand2.nextInt(1000000, 9999999);
                    sb.append(String.valueOf(rand_int1));
                }
                Random rand3 = new Random();
                sb.append(String.valueOf(rand3.nextInt(99, 199)));
            }
            return sb.toString().trim();
        } else {
            StringBuilder sb = new StringBuilder();
            //Convert to 32bit
            for (int i = 0; i < 8 * 4; i++) {
                Random rand = new Random();
                // Generate random 8bit int
                int rand_int1 = rand.nextInt(10000000, 99999999);
                sb.append(String.valueOf(rand_int1));
            }
            sb.append(joinHash(hash, hashBool));
            Random rand = new Random();
            for (int i = 0; i < 7 * rand.nextInt(3, 99); i++) {
                Random rand2 = new Random();
                // Generate random 8bit int
                int rand_int1 = rand2.nextInt(1000000, 9999999);
                sb.append(String.valueOf(rand_int1));
            }
            Random rand3 = new Random();
            sb.append(String.valueOf(rand3.nextInt(99, 199)));
            return sb.toString().trim();
        }
    }

    public static String joinHash(String hash, boolean hashBool) {
        if (hashBool) {
            String joiner = hashUsername(hash) + hashUsername(caesarCipher(hash, hash.length()).toString());
            String finalStr = splitString(joiner, hashBool);
            return finalStr;

        } else {
            String joiner = hashPassword(caesarCipher(hash, hash.length()).toString()) + hashPassword(hash);
            String finalStr = splitString(joiner, hashBool);
            return finalStr;
        }
    }

    public static String hashUsername(String username) {
        int pw = noNegatives(username.hashCode());
        int length = (int) (Math.log10(pw) + 1);

        int hashTrimmer = 0;
        int sbInt = 0;
        StringBuilder sb = new StringBuilder();

        switch (length) {
            case 7 -> {
                hashTrimmer = pw / 1000;
                //Trims to 4 digits
                sb.append(numReverser(hashTrimmer));
                sb.append(hashTrimmer);
                sbInt = (int) Math.sqrt(Integer.parseInt(sb.toString()));
                sb.append(numReverser(sbInt));
                sb.append(sbInt);
                username = sb.toString();
            }
            case 8 -> {
                hashTrimmer = pw / 1;
                sbInt = (int) Math.sqrt(hashTrimmer);
                sb.append(sbInt);
                sb.append(numReverser(sbInt));
                sb.append(numReverser(hashTrimmer));
                username = sb.toString();
            }
            case 9 -> {
                hashTrimmer = pw / 10;
                sbInt = (int) Math.sqrt(hashTrimmer);
                sb.append(hashTrimmer);
                sb.append(sbInt);
                sb.append(numReverser(sbInt));
                username = sb.toString();
            }
            case 10 -> {
                hashTrimmer = pw / 100;
                sb.append(hashTrimmer);
                sb.append(numReverser(hashTrimmer));
                username = sb.toString();
            }
            default -> {
            }
        }
        return numPadder(username);
    }

    public static String hashPassword(String password) {
        int pw = noNegatives(password.hashCode());
        int length = (int) (Math.log10(pw) + 1);

        int hashTrimmer = 0;
        int sbInt = 0;
        StringBuilder sb = new StringBuilder();

        switch (length) {
            case 7 -> {
                hashTrimmer = pw / 1000;
                //Trims to 4 digits
                sb.append(hashTrimmer);
                sb.append(numReverser(hashTrimmer));
                sbInt = (int) Math.sqrt(Integer.parseInt(sb.toString()));
                sb.append(sbInt);
                sb.append(numReverser(sbInt));
                password = sb.toString();
            }
            case 8 -> {
                hashTrimmer = pw / 1;
                sbInt = (int) Math.sqrt(hashTrimmer);
                sb.append(sbInt);
                sb.append(numReverser(hashTrimmer));
                sb.append(numReverser(sbInt));
                password = sb.toString();
            }
            case 9 -> {
                hashTrimmer = pw / 10;
                sbInt = (int) Math.sqrt(hashTrimmer);
                sb.append(numReverser(sbInt));
                sb.append(hashTrimmer);
                sb.append(sbInt);
                password = sb.toString();
            }
            case 10 -> {
                hashTrimmer = pw / 100;
                sb.append(numReverser(hashTrimmer));
                sb.append(hashTrimmer);
                password = sb.toString();
            }
            default -> {
            }
        }
        return numPadder(password);
    }

    public static String splitString(String s1, boolean hashBool) {
        final int mid = s1.length() / 4; //get the middle of the String
        String[] parts = {s1.substring(0, mid), s1.substring(mid, mid * 2), s1.substring(mid * 2, mid * 3), s1.substring(mid * 3, mid * 4)};

        if (hashBool) {
            String splitStr = parts[1] + parts[0] + parts[3] + parts[2];
            return splitStr;
        } else {
            String splitStr = parts[3] + parts[1] + parts[2] + parts[0];
            return splitStr;
        }
    }

    public static int numReverser(int hashTrimmer) {
        int reversed = 0;
        while (hashTrimmer != 0) {
            int digit = hashTrimmer % 10;
            reversed = reversed * 10 + digit;

            hashTrimmer /= 10;
        }
        return reversed;
    }

    public static String numPadder(String hashPassword) {
        int pw = hashPassword.length();
        StringBuilder sb = new StringBuilder();
        int lengthDifference = 16 - pw;
        sb.append(hashPassword);
        for (int i = 0; i < lengthDifference; i++) {
            sb.append(0);
        }
        return sb.toString();
    }

    public static Integer noNegatives(Integer negativeInt) {
        if (negativeInt < 0) {
            negativeInt *= -1;
        }
        return negativeInt;
    }

    public static StringBuffer caesarCipher(String text, int s) {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < text.length(); i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                char ch = (char) (((int) text.charAt(i)
                        + s - 65) % 26 + 65);
                result.append(ch);
            } else {
                char ch = (char) (((int) text.charAt(i)
                        + s - 97) % 26 + 97);
                result.append(ch);
            }
        }
        return result;
    }
}
