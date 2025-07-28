package com.example.mysolana.check_private_key;

import com.example.mysolana.auth.MnemonicsClass;
import org.sol4k.Keypair;
import io.ipfs.multibase.Base58;

public class CheckPrivateKeyComponent {

    CheckPrivateKeyInterface checkPrivateKeyInterface;

    public CheckPrivateKeyComponent(CheckPrivateKeyInterface checkPrivateKeyInterface) {
        this.checkPrivateKeyInterface = checkPrivateKeyInterface;
    }

    public void CheckMatchPrivateKey(String phrase) {
        try {
            Keypair keypair  = MnemonicsClass.INSTANCE.convertToKeypair(phrase);
            checkPrivateKeyInterface.checkPrivateKey(keypair.getPublicKey().toBase58(), StateCheckPrivateKey.SUCCESS, phrase, Base58.encode(keypair.getSecret()));
        } catch(Exception e) {
            checkPrivateKeyInterface.checkPrivateKey("aaa", StateCheckPrivateKey.FAILURE,"aaa", "bbb");
        }
    }
}
