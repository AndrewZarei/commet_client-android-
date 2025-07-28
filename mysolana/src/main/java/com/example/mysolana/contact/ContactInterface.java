package com.example.mysolana.contact;

import com.solana.models.buffer.ContactModel;

import java.util.List;

public interface ContactInterface {
    void getContact(String error, List<ContactModel> contactListModel, StateContact stateContact);
}
