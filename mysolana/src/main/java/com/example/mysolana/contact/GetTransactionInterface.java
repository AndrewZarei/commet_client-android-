package com.example.mysolana.contact;

import com.solana.models.buffer.GetSignaturesForAddressModel;
import com.solana.models.buffer.GetTransactionModel;

public interface GetTransactionInterface {
        void getTransactionInterfaceResult(boolean hasError, GetTransactionModel data);
}