package com.example.mysolana.encryptdecrypt;

import org.bouncycastle.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptdecryptJavaHelper {
    public static void callEncrypAndDecrypt() throws Exception {
        String walletPubkey = "621Y5HvVG8syzLZqRspJMbDrkZKxX3MdFs1pZMDRwojx";
        String walletPrivateKey = "2D7yVTFHwvsjLKuDyKMrtxr9Jv7z1JTjxnGyo2Uhr36UjG6JKWymZQSXXoXXBUvpZzyPjMqfTYtzZ9fZz9uiixc2";
        String text32bytes = generate32ByteKey();
        String encryptedWalletPubkey = encrypt(text32bytes,walletPubkey);
        String decryptedWalletPubkey = decrypt(hexToBytes(encryptedWalletPubkey), walletPubkey);
    }

    public static String encrypt(String data, String KEY) throws Exception {
        String newkey = getUniqueHash(KEY);
        SecretKeySpec secretKey = new SecretKeySpec(newkey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return bytesToHex(encryptedBytes);
    }

    public static String decrypt(byte[] encryptedData, String KEY) throws Exception {
        String newkey = getUniqueHash(KEY);
        SecretKeySpec secretKey = new SecretKeySpec(newkey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, "UTF-8");
    }

    public static String generate32ByteKey() {
        try {
            // Initialize a KeyGenerator with the desired algorithm and key size
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // 256 bits = 32 bytes

            // Generate a secret key
            SecretKey secretKey = keyGenerator.generateKey();

            // Convert the SecretKey to a byte array
            byte[] keyBytes = secretKey.getEncoded();

            // Check if the keyBytes array is 32 bytes
            if (keyBytes.length == 32) {
                System.out.println("Generated 32-byte key: " + bytesToHex(keyBytes));
                return bytesToHex(keyBytes);
            } else {
                System.out.println("Key generation failed.");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to convert a byte array to a hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static String getUniqueHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());

            // Take a portion of the hash and convert it to a hexadecimal string
            byte[] truncatedHash = Arrays.copyOf(hash, 8); // 8 bytes = 16 hexadecimal characters
            StringBuilder hexString = new StringBuilder(2 * truncatedHash.length);
            for (byte b : truncatedHash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}




