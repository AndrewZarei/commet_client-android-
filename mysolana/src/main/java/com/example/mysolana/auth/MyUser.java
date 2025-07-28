package com.example.mysolana.auth;

import cash.z.ecc.android.bip39.Mnemonics;

public class MyUser {
    private String username;

    public void setModalcreate(String modalcreate) {this.modalcreate = modalcreate;}

    public String getModalcreate() {return modalcreate;}

    private String modalcreate;
    private String id;
    private String indexProfile;


    private String base_pubkey;
    private boolean login;
    private StateAuthLogin stateAuthLogin;
    private String privateKey;

    private Mnemonics.MnemonicCode mnemonicCode;

    public String getBase_pubkey() {return base_pubkey;}
    public void setBase_pubkey(String base_pubkey) {this.base_pubkey = base_pubkey;}
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public StateAuthLogin getStateAuthLogin() {
        return stateAuthLogin;
    }

    public void setStateAuthLogin(StateAuthLogin stateAuthLogin) {
        this.stateAuthLogin = stateAuthLogin;
    }

    public String getIndexProfile() {
        return indexProfile;
    }

    public void setIndexProfile(String indexProfile) {
        this.indexProfile = indexProfile;
    }

    public Mnemonics.MnemonicCode getMnemonicCode() {
        return mnemonicCode;
    }

    public void setMnemonicCode(Mnemonics.MnemonicCode mnemonicCode) {
        this.mnemonicCode = mnemonicCode;
    }
}