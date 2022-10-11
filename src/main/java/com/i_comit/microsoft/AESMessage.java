/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.microsoft;

import static com.i_comit.microsoft.Globals.encKeyString;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class AESMessage {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static String encryptMessage(byte[] message, byte[] keyBytes) throws InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedMessage = cipher.doFinal(message);
        return new String(encryptedMessage);
    }

    public static String decryptMessage(byte[] encryptedMessage, byte[] keyBytes) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] clearMessage = cipher.doFinal(encryptedMessage);
        return new String(clearMessage);
    }

    public static void AES(String bitString) throws Exception {
        try {

            System.out.println(encKeyString);
            String message = bitString;

            String encryptedstr = encryptMessage(message.getBytes(), encKeyString.getBytes());

            //Files.write(path, bytes);
            String decryptedStr = decryptMessage(encryptedstr.getBytes(), encKeyString.getBytes());
            System.out.println("Original String -> " + message);
            System.out.println("Encrypted String -> " + encryptedstr.getBytes());
            System.out.println("Decrypted String -> " + decryptedStr.getBytes());

        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }
    }
}
