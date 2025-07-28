package com.solana.models.buffer;

import com.syntifi.near.borshj.Borsh;
import com.syntifi.near.borshj.annotation.BorshField;

import java.util.List;

public class ContactListModel implements Borsh {
    @BorshField(order = 1)
    private List<ContactModel> index;

    public ContactListModel() {
    }

    public ContactListModel(List<ContactModel> index) {
        this.index = index;
    }

    public List<ContactModel> getIndex() {
        return index;
    }

    public void setIndex(List<ContactModel> index) {
        this.index = index;
    }
}
