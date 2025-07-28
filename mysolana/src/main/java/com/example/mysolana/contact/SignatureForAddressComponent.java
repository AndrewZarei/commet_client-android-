package com.example.mysolana.contact;

import com.solana.SolanaHelper;
import com.solana.core.PublicKey;
import com.solana.models.buffer.ContactListModel;
import com.solana.models.buffer.GetSignaturesForAddressModel;

import org.jetbrains.annotations.Nullable;

public class SignatureForAddressComponent {
    SignatureForAddressInterface signatureForAddressInterface;

    public SignatureForAddressComponent(SignatureForAddressInterface signatureForAddressInterface) {
        this.signatureForAddressInterface = signatureForAddressInterface;
    }

    public void getSignaturesForAddress(String userPublickey) {

        SolanaHelper.INSTANCE.getSignaturesForAddress(new PublicKey(userPublickey), new SolanaHelper.OnResponseData<GetSignaturesForAddressModel>() {
            @Override
            public void onSuccess(@Nullable GetSignaturesForAddressModel data) {
                signatureForAddressInterface.SignatureForAddressResult(true, data);
            }

            @Override
            public void onFailure(@Nullable Exception e) {
                signatureForAddressInterface.SignatureForAddressResult(false, null);
            }
        });
    }
}
