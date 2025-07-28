package com.solana.models.buffer;

import java.util.List;

public class GetSignaturesForAddressModel {
    private List<getSignaturesForAddressModelData> result;

    public List<getSignaturesForAddressModelData> getResult() {
        return result;
    }

    public void setResult(List<getSignaturesForAddressModelData> result) {
        this.result = result;
    }


    public static class getSignaturesForAddressModelData {
        private Double blockTime;
        private String signature;

        public Double getBlockTime() {
            return blockTime;
        }

        public void setBlockTime(Double blockTime) {
            this.blockTime = blockTime;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

}
