package com.solana.models.buffer;

import com.syntifi.near.borshj.Borsh;
import com.syntifi.near.borshj.annotation.BorshField;

public class UserModel implements Borsh {
    @BorshField(order = 1)
    private String user_address;
    @BorshField(order = 2)
    private String user_name;
    @BorshField(order = 3)
    private String token_cipher;

    public UserModel() {

    }
    public UserModel(String user_address, String user_name,String token_cipher) {
        this.user_address = user_address;
        this.user_name = user_name;
        this.token_cipher = token_cipher;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public void setToken_cipher(String token_cipher) {this.token_cipher = token_cipher;}

    public String getToken_cipher() {return token_cipher;}
}