package com.example.mysolana.encryptdecrypt

import org.bouncycastle.util.encoders.Base64
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

class EncryptdecryptHelper {
    fun callEncrypAndDecrypt() {
        val walletPubkey = "621Y5HvVG8syzLZqRspJMbDrkZKxX3MdFs1pZMDRwojx"
        val walletPrivateKey =
            "2D7yVTFHwvsjLKuDyKMrtxr9Jv7z1JTjxnGyo2Uhr36UjG6JKWymZQSXXoXXBUvpZzyPjMqfTYtzZ9fZz9uiixc2"
        val text32bytes = generate32ByteKey()

        // Encrypt with symmetric key
        // Encrypt the data with the public key and 32-byte key
//        val encryptedData = text32bytes?.let { encryptWithPublicKey(walletPubkey, it.toByteArray()) }
//        var toStringEncryptedData = encryptedData.toString();
        print("");


    }
//
//    fun encryptWithPublicKey(publicKey: String, dataToEncrypt: ByteArray): ByteArray {
//        val publicKeyBytes = Base64.getDecoder().decode(publicKey)
//        val keySpec = X509EncodedKeySpec(publicKeyBytes)
//        val keyFactory = KeyFactory.getInstance("RSA")
//        val publicKey = keyFactory.generatePublic(keySpec)
//
//        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
//
//        return cipher.doFinal(dataToEncrypt)
//    }


    fun generate32ByteKey(): String? {
        try {
            // Initialize a KeyGenerator with the desired algorithm and key size
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256) // 256 bits = 32 bytes

            // Generate a secret key
            val secretKey: SecretKey = keyGenerator.generateKey()

            // Convert the SecretKey to a byte array
            val keyBytes = secretKey.encoded
            val text32bytes = bytesToHex(keyBytes);
            return text32bytes;
            // Check if the keyBytes array is 32 bytes
            if (keyBytes.size == 32) {
                println("Generated 32-byte key: ${bytesToHex(keyBytes)}")
            } else {
                println("Key generation failed.")
            }
        } catch (e: NoSuchAlgorithmException) {
            return e.message
        }
    }

    // Helper method to convert a byte array to a hex string
    fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

}