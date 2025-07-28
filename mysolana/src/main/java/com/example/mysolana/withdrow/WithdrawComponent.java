package com.example.mysolana.withdrow;

import com.solana.SolanaHelper;
import com.solana.core.PublicKey;

public class WithdrawComponent {

    WithdrawInterface withdrawInterface;

    public WithdrawComponent(WithdrawInterface withdrawInterface) {this.withdrawInterface = withdrawInterface;}

    public void sendWithdrawRequest(String privateKey, String to, Long amount) {
        SolanaHelper.INSTANCE.sendWithdraw(privateKey, new PublicKey(to), amount, new SolanaHelper.OnResponseE() {
            @Override
            public void onSuccess() {
                withdrawInterface.sendWithdrawRequest("SUCCESS", StateWithdraw.SUCCESS);
            }

            @Override
            public void onFailure(Exception e) {
                withdrawInterface.sendWithdrawRequest(e.getMessage(), StateWithdraw.FAILURE);
            }
        });
    }
}
