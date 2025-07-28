package com.example.mysolana.encryptdecrypt;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;

import java.security.*;
//import java.security.spec.EdDSAParameterSpec;
import java.security.spec.X509EncodedKeySpec;

public class Ed25519Encrytion {

    private static final String KEY_ALIAS = "my_ed25519_key";

    public static void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC,
                    "AndroidKeyStore"
            );

            KeyGenParameterSpec.Builder builder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    builder = new KeyGenParameterSpec.Builder(
                            KEY_ALIAS,
                            KeyProperties.PURPOSE_SIGN
                    )
                            .setDigests(KeyProperties.DIGEST_SHA512)
                            .setUserAuthenticationRequired(false)
                            .setIsStrongBoxBacked(false);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyPairGenerator.initialize(builder.build());
            }
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
            byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
            System.out.println(bytesToHex(publicKeyBytes));
            System.out.println(bytesToHex(privateKeyBytes));
            KeyPair gg = keyPair;

            // You can now use keyPair.getPrivate() and keyPair.getPublic() for signing and verification.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
    // Rest of the code remains the same...
}
