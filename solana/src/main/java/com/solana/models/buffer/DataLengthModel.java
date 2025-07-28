package com.solana.models.buffer;


import com.syntifi.near.borshj.Borsh;
import com.syntifi.near.borshj.annotation.BorshField;


public class DataLengthModel implements Borsh {
    @BorshField(order = 1)
    private int length;
    public DataLengthModel() {

    }

    public DataLengthModel(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
