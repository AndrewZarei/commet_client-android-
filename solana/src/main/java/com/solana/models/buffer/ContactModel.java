package com.solana.models.buffer;

import com.syntifi.near.borshj.Borsh;
import com.syntifi.near.borshj.annotation.BorshField;


public class ContactModel implements Borsh {
    @BorshField(order = 1)
    private String user_name;
    @BorshField(order = 2)
    private String last_name;
    @BorshField(order = 3)
    private String public_key;
    @BorshField(order = 4)
    private String base_pubkey;
    @BorshField(order = 5)
    private String avatar;

    private boolean is_select;

    public boolean isFilled;


    public ContactModel() {
    }

    public ContactModel(String user_name, String last_name, String public_key, String base_pubkey, String avatar) {
        this.user_name = user_name;
        this.last_name = last_name;
        this.public_key = public_key;
        this.base_pubkey = base_pubkey;
        this.avatar = avatar;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getBase_pubkey() {
        return base_pubkey;
    }

    public void setBase_pubkey(String base_pubkey) {
        this.base_pubkey = base_pubkey;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public boolean isIs_select() {
        return is_select;
    }

    public void setIs_select(boolean is_select) {
        this.is_select = is_select;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
