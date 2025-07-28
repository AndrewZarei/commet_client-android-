package com.solana.models.buffer;

import java.util.List;

public class TransactionModel {
    private Double blockTime;
    private Double fee;
    private String signature;
    private List<String> postBalances;
    private List<String> preBalances;

    public TransactionModel(String signature, Double time, Double fee, List<String> postBalances, List<String> preBalances) {
        this.signature = signature;
        this.blockTime = time;
        this.fee = fee;
        this.postBalances = postBalances;
        this.preBalances = preBalances;
    }

    public Double getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Double blockTime) {
        this.blockTime = blockTime;
    }

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

    public String getSignature() {return signature;}

    public void setSignature(String signature) {this.signature = signature;}
}
