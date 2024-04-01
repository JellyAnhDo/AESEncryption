/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package aesencryption;

/**
 *
 * @author Anhho
 */
public class AESEncryption {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // Input strings
        String plainText1 = "Mr Diep!";
        String plainText2 = "This is another message.";

        // Key (must be 16 bytes long)
        String key = "abcdefghijklmnop";

        // Encrypt the first string
        byte[] cipherText1 = AES.Encrypt(plainText1, key);
        System.out.println("Cipher Text 1: " + byteArrayToHexString(cipherText1));

        // Decrypt the first string
        String decryptedText1 = AES.Decrypt(cipherText1, key);
        System.out.println("Decrypted Text 1: " + decryptedText1);

        // Encrypt the second string
        byte[] cipherText2 = AES.Encrypt(plainText2, key);
        System.out.println("Cipher Text 2: " + byteArrayToHexString(cipherText2));

        // Decrypt the second string
        String decryptedText2 = AES.Decrypt(cipherText2, key);
        System.out.println("Decrypted Text 2: " + decryptedText2);
    }

    // Utility method to convert byte array to hex string
    private static String byteArrayToHexString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}

