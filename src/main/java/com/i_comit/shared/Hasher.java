package com.i_comit.shared;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.io.UnsupportedEncodingException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Hasher {

    public static String hashedPassword;
    public static String hashedUsername;

    public static String getHash(String hash, boolean hashBool) {
        String hash2 = finalizeHash(hash, hashBool);
        int firstHashIndex = (hash.length() + 5);

        int secondHashIndex = (15 - hash.length()) + (hash.length() * 16) + 9;
        String[] hashIndices = {
            hash2.substring(0, 4).trim(),
            hash2.substring(firstHashIndex, firstHashIndex + 4).trim(),
            hash2.substring(secondHashIndex, secondHashIndex + 4).trim(),
            hash2.substring(hash2.length() - 4, hash2.length())};

        String finalHash = hashIndices[0] + hashIndices[1] + hashIndices[2] + hashIndices[3];
        return finalHash;
    }

    public static String readKey(String hash, String loginString) {
        int firstHashIndex = (loginString.length() + 5);
        int secondHashIndex = (15 - loginString.length()) + (loginString.length() * 16) + 9;

        String[] hashIndices = {
            hash.substring(0, 4).trim(),
            hash.substring(firstHashIndex, firstHashIndex + 4).trim(),
            hash.substring(secondHashIndex, secondHashIndex + 4).trim(),
            hash.substring(hash.length() - 4, hash.length())};

        String finalHash = hashIndices[0] + hashIndices[1] + hashIndices[2] + hashIndices[3];
        return finalHash;
    }

    public static String finalizeHash(String hash, boolean hashBool) {
        StringBuilder sb = new StringBuilder();
        try {
            String alphabet = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toUpperCase();
            String s1 = getHash64(joinHash(hash, hashBool));
            final int mid = s1.length() / 4; //get the middle of the String
            String[] parts = {s1.substring(0, mid), s1.substring(mid, mid * 2), s1.substring(mid * 2, mid * 3), s1.substring(mid * 3, mid * 4)};

            sb.append(parts[0]);
            for (int i = 0; i < hash.length() * 16; i++) {
                Random rand = new Random();
                char alphabetChar = alphabet.charAt(rand.nextInt(alphabet.length()));
                sb.append(alphabetChar);
                if (i == hash.length()) {
                    sb.append(parts[1]);
                }
            }
            int remainder = 15 - hash.length();
            for (int i = 0; i < remainder * 16; i++) {
                Random rand1 = new Random();
                char alphabetChar = alphabet.charAt(rand1.nextInt(alphabet.length()));
                sb.append(alphabetChar);
                if (i == remainder) {
                    sb.append(parts[2]);
                }
            }
            sb.append(parts[3]);
        } catch (UnsupportedEncodingException ex) {
        }
        return sb.toString().trim();
    }

    public static String joinHash(String hash, boolean hashBool) throws UnsupportedEncodingException {
        if (hashBool) {
            String finalStr = hashUsername(caesarCipher(hash, hash.length()).toString()).trim() + hashUsername(hash);
            return finalStr;
        } else {
            String finalStr = hashPassword(hash) + hashPassword(caesarCipher(hash, hash.length() * 2).toString()).trim();
            return finalStr;
        }
    }

    public static String hashUsername(String username) throws UnsupportedEncodingException {
        int pw = noNegatives(username.hashCode());
        int length = (int) (Math.log10(pw) + 1);
        String hash64Str = getHash64(username);
        final int mid = hash64Str.length() / 4;
        String[] parts = {hash64Str.substring(0, mid), hash64Str.substring(mid, mid * 2), hash64Str.substring(mid * 2, mid * 3), hash64Str.substring(mid * 3, mid * 4)};
        switch (length) {
            case 7:
                username = parts[0] + parts[1] + parts[2] + parts[3];
                break;
            case 8:
                username = parts[1] + parts[0] + parts[3] + parts[2];
                break;
            case 9:
                username = parts[2] + parts[3] + parts[1] + parts[0];
                break;
            case 10:
                username = parts[3] + parts[0] + parts[1] + parts[2];
                break;
            default:
                break;
        }
        return username;
    }

    public static String hashPassword(String password) throws UnsupportedEncodingException {
        int pw = noNegatives(password.hashCode());
        int length = (int) (Math.log10(pw) + 1);
        String hash64Str = getHash64(password);

        final int mid = hash64Str.length() / 4; //get the middle of the String
        String[] parts = {hash64Str.substring(0, mid), hash64Str.substring(mid, mid * 2), hash64Str.substring(mid * 2, mid * 3), hash64Str.substring(mid * 3, mid * 4)};
        switch (length) {
            case 7:
                password = parts[3] + parts[0] + parts[1] + parts[2];
                break;
            case 8:
                password = parts[2] + parts[3] + parts[1] + parts[0];
                break;
            case 9:
                password = parts[1] + parts[0] + parts[3] + parts[2];
                break;
            case 10:
                password = parts[0] + parts[1] + parts[2] + parts[3];
                break;
            default:
                break;
        }
        return password;
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

    public static String stringReverser(String str) {
        String nstr = "";
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i); //extracts each character
            nstr = ch + nstr; //adds each character in front of the existing string
        }
        return nstr;
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

    public static String SQLHasher(String username) throws UnsupportedEncodingException {
        int mod = username.length() % 2;
        String finalStr = "";
        StringBuffer SQLHasher = caesarCipher(getHash32(username), mod);
        String reverser = stringReverser(SQLHasher.toString());

        if (mod == 1) {
            finalStr = splitString(reverser, true);
        }
        if (mod == 0) {
            finalStr = splitString(reverser, false);
        }
        return finalStr;
    }

    private static final MessageDigest MESSAGE_DIGEST;
    public static final String[] EMPTY_ARRAY = new String[0];

    static {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException err) {
            throw new IllegalStateException();
        }
        MESSAGE_DIGEST = md;
    }
    private static final String HEX_CHARS = "0123456789ABCDEF";

    public static String getMD5(String string) {
        byte[] bytes;
        try {
            bytes = string.getBytes("UTF-8");
        } catch (java.io.UnsupportedEncodingException ue) {
            throw new IllegalStateException(ue);
        }
        byte[] result;
        synchronized (MESSAGE_DIGEST) {
            MESSAGE_DIGEST.update(bytes);
            result = MESSAGE_DIGEST.digest();
        }
        char[] resChars = new char[32];
        int len = result.length;
        for (int i = 0; i < len; i++) {
            byte b = result[i];
            int lo4 = b & 0x0F;
            int hi4 = (b & 0xF0) >> 4;
            resChars[i * 2] = HEX_CHARS.charAt(hi4);
            resChars[i * 2 + 1] = HEX_CHARS.charAt(lo4);
        }
        return new String(resChars);
    }

    public static String getHash32(String string) throws UnsupportedEncodingException {
        String md5 = getMD5(string);
        return md5.substring(0, 8);
    }

    public static String getHash64(String string) throws UnsupportedEncodingException {
        String md5 = getMD5(string);
        return md5.substring(0, 16);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        System.out.println(getHash64("khiemluong1"));
//        System.out.println(getHash64("khiemluong2"));

    }
}
