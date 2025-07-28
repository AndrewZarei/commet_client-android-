package com.example.mysolana.contact;

import com.solana.SolanaHelper;
import com.solana.core.PublicKey;
import com.solana.models.buffer.GetSignaturesForAddressModel;
import com.solana.models.buffer.GetTransactionModel;

import org.jetbrains.annotations.Nullable;

public class GetTransactionComponent {
    GetTransactionInterface transactionInterface;

    public GetTransactionComponent(GetTransactionInterface transactionInterface) {
        this.transactionInterface = transactionInterface;
    }

    public void getTransaction(String transaction) {

        SolanaHelper.INSTANCE.getTransaction(transaction, new SolanaHelper.OnResponseData<GetTransactionModel>() {
            @Override
            public void onSuccess(@Nullable GetTransactionModel data) {
                transactionInterface.getTransactionInterfaceResult(false, data);
            }

            @Override
            public void onFailure(@Nullable Exception e) {
                transactionInterface.getTransactionInterfaceResult(true, null);
            }
        });
    }
}
