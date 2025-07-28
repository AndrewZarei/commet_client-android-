package com.example.mysolana.contact;

import com.solana.SolanaHelper;
import com.solana.core.PublicKey;

import org.jetbrains.annotations.Nullable;

public class BalanceComponent {
    BalanceComponentInterface balanceComponentInterface;

    public BalanceComponent(BalanceComponentInterface balanceComponentInterface) {
        this.balanceComponentInterface = balanceComponentInterface;
    }

    public void getUserBalance(String userPublickey) {
        SolanaHelper.INSTANCE.getBalance(new PublicKey(userPublickey), new SolanaHelper.OnResponseStr() {
            @Override
            public void onSuccess(@Nullable String accountInfo) {
                balanceComponentInterface.create("false",accountInfo);
            }

            @Override
            public void onFailure(@Nullable Exception e) {
                balanceComponentInterface.create("true",e.getMessage());
            }
        });

    }
}
