package com.example.mysolana.auth;

import android.util.Log;
import com.solana.SolanaHelper;
import com.solana.core.PublicKey;
import com.solana.models.buffer.AccountInfo;
import com.solana.models.buffer.ContactListModel;
import com.solana.models.buffer.ContactModel;
import org.sol4k.Keypair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import cash.z.ecc.android.bip39.Mnemonics;
import io.ipfs.multibase.Base58;

public class AuthComponent {
    AuthInterface authInterface;
    MyUser myUser;

    public AuthComponent(AuthInterface authInterface) {
        myUser = new MyUser();
        this.authInterface = authInterface;
    }

    public void Login(String[] public_key_instance, String username, Boolean CreateOrNoteCreate) throws IOException {
        PublicKey publicKey = SolanaHelper.INSTANCE.createWithSeed(username);
        List<ContactModel> finalList = new ArrayList<>();
        int i = 1;
        for (String key : public_key_instance)
        {
            int a = i++;
            SolanaHelper.INSTANCE.getContacts(new PublicKey(key), new SolanaHelper.OnResponseData<ContactListModel>() {
                @Override
                public void onSuccess(ContactListModel accountInfo) {
                    finalList.addAll(accountInfo.getIndex());
                    if (a == public_key_instance.length){
                        getContact(finalList,username,publicKey.toString(),CreateOrNoteCreate);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("TAG", "onFailure: " + e.getMessage() + " PDA: " + key);
                }
            });
        }
    }

    private void getContact(List<ContactModel> finalList, String username, String publicKey, Boolean CreateOrNoteCreate){
        try {
            boolean checkEquals = false;
            for (int i = 0; i < finalList.size(); i++) {
                String temp_username = String.valueOf(finalList.get(i).getUser_name());
                if (temp_username.equals(username)) {
                    checkEquals = true;
                    myUser.setUsername(username);
                    myUser.setBase_pubkey(finalList.get(i).getBase_pubkey());
                    myUser.setId(finalList.get(i).getPublic_key());
                    myUser.setIndexProfile("1");
                    myUser.setStateAuthLogin(StateAuthLogin.GET_INDEX);
                }
            }
            if (!checkEquals) {
                myUser.setUsername(username);
                myUser.setId(publicKey);
                if (!CreateOrNoteCreate) {
                    myUser.setModalcreate("modal");
                    myUser.setStateAuthLogin(StateAuthLogin.SHOW_MODAL);
                    authInterface.login(myUser, "success");
                    myUser.setLogin(true);
                }
            } else {
                myUser.setStateAuthLogin(StateAuthLogin.REPETITIOUS);
                myUser.setIndexProfile("1");
                authInterface.login(myUser, "success");
            }
        } catch (Exception ee) {
            myUser.setStateAuthLogin(StateAuthLogin.EXCEPTION);
            authInterface.login(myUser, "success");
        }
    }

    public void createAccounts(String username, String avatar, String indexProfile) throws IOException {

        PublicKey publicKey = SolanaHelper.INSTANCE.createWithSeed(username);
        Keypair userKeyPair;
        try {
            Mnemonics.MnemonicCode mnemonicCode = MnemonicsClass.INSTANCE.getMnemonics();
            userKeyPair = MnemonicsClass.INSTANCE.getKeypair(mnemonicCode);
            PublicKey publicKey1 = new PublicKey(userKeyPair.getPublicKey().toBase58());
            SolanaHelper.INSTANCE.getOrCreateAccount(publicKey1, publicKey, username, avatar, indexProfile, new SolanaHelper.OnResponse() {
                @Override
                public void onSuccess(AccountInfo accountInfo) {
                    try {
                        String encoded1 = Base58.encode(userKeyPair.getSecret());
                        myUser.setBase_pubkey(userKeyPair.getPublicKey().toString());
                        myUser.setPrivateKey(encoded1);
                        myUser.setMnemonicCode(mnemonicCode);
                        myUser.setStateAuthLogin(StateAuthLogin.NOT_REPETITIOUS);
                        myUser.setIndexProfile(indexProfile);
                        authInterface.login(myUser, "success");

                    } catch (Exception ee) {
                        Log.e("TAG", "onSuccess Exception create account: " + ee.getMessage());
                        authInterface.login(myUser, ee.getMessage());
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    myUser.setStateAuthLogin(StateAuthLogin.NOT_REPETITIOUS_FAILURE);
                    myUser.setLogin(false);
                    myUser.setIndexProfile("1");
                    authInterface.login(myUser, e.getMessage());
                }
            });

        } catch (Exception ignored) {
            Log.e("aasdf", ignored.toString());
        }
    }
}
