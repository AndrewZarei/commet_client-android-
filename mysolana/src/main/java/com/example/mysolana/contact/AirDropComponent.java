package com.example.mysolana.contact;

import com.solana.SolanaHelper;
import com.solana.core.PublicKey;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AirDropComponent {
    AirDropComponentInterface airDropComponentInterface;

    public AirDropComponent(AirDropComponentInterface airDropComponentInterface) {
        this.airDropComponentInterface = airDropComponentInterface;
    }

    public void SetAirDrop(String userPublickey) {
        SolanaHelper.INSTANCE.setAirdrop(new PublicKey(userPublickey), new SolanaHelper.OnResponseStr() {
            @Override
            public void onSuccess(@Nullable String accountInfo) {
                airDropComponentInterface.result("false",accountInfo);
            }

            @Override
            public void onFailure(@Nullable Exception e) {
                airDropComponentInterface.result("true", Objects.requireNonNull(e).getMessage());
            }
        });
    }
}
