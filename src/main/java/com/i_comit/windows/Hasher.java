package com.i_comit.windows;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Hasher {

    public static String modHash(String password) {
        int pl = password.length();
        int pw = noNegatives(password.hashCode());
        int length = (int) (Math.log10(pw) + 1);

        //System.out.println("Hash # " + pw);

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

    public static int numReverser(int hashTrimmer) {
        int reversed = 0;
        while (hashTrimmer != 0) {

            // get last digit from num
            int digit = hashTrimmer % 10;
            reversed = reversed * 10 + digit;

            // remove the last digit from num
            hashTrimmer /= 10;
        }
        return reversed;
    }

    public static String numPadder(String hashPassword) {
        int pw = hashPassword.length();
        StringBuilder sb = new StringBuilder();
        int lengthDifference = 16-pw;
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

}
