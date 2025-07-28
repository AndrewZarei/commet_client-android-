package com.example.mysolana.contact;

import com.solana.models.buffer.GetSignaturesForAddressModel;

public interface SignatureForAddressInterface {
        void SignatureForAddressResult(boolean hasError, GetSignaturesForAddressModel data);
}