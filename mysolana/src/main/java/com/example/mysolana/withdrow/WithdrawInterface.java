package com.example.mysolana.withdrow;

import com.example.mysolana.contact.StateContact;
import com.solana.models.buffer.ContactListModel;

public interface WithdrawInterface {
    void sendWithdrawRequest(String error, StateWithdraw stateWithdraw);
}
