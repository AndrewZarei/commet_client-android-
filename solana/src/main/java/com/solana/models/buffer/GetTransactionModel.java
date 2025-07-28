package com.solana.models.buffer;


import java.util.List;

public class GetTransactionModel {
    private getTransactionData result;
    private Double blockTime;

    public getTransactionData getResult() {
        return result;
    }

    public void setResult(getTransactionData result) {
        this.result = result;
    }

    public Double getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Double blockTime) {
        this.blockTime = blockTime;
    }


    public static class getTransactionData {
        private getTransactionMeta meta;
        private getTransactionInfo transaction;

        public getTransactionMeta getMeta() {
            return meta;
        }

        public void setMeta(getTransactionMeta meta) {
            this.meta = meta;
        }

        public getTransactionInfo getTransaction() {
            return transaction;
        }

        public void setTransaction(getTransactionInfo transaction) {
            this.transaction = transaction;
        }
    }

    public static class getTransactionMeta {
        private Double fee;
        private List<String> postBalances;
        private List<String> preBalances;

        public Double getFee() {
            return fee;
        }

        public void setFee(Double fee) {
            this.fee = fee;
        }

        public List<String> getPostBalances() {
            return postBalances;
        }

        public void setPostBalances(List<String> postBalances) {
            this.postBalances = postBalances;
        }

        public List<String> getPreBalances() {
            return preBalances;
        }

        public void setPreBalances(List<String> preBalances) {
            this.preBalances = preBalances;
        }
    }

    public static class getTransactionInfo {
        private getTransactionMessage message;
        private List<String> signatures;


        public List<String> getSignatures() {
            return signatures;
        }

        public void setSignatures(List<String> signatures) {
            this.signatures = signatures;
        }

        public getTransactionMessage getMessage() {
            return message;
        }

        public void setMessage(getTransactionMessage message) {
            this.message = message;
        }
    }

    public static class getTransactionMessage {
        private List<String> accountKeys;

        public List<String> getAccountKeys() {
            return accountKeys;
        }

        public void setAccountKeys(List<String> accountKeys) {
            this.accountKeys = accountKeys;
        }

    }

}
