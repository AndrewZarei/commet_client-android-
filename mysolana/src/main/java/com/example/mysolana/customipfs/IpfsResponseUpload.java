package com.example.mysolana.customipfs;

public class IpfsResponseUpload {
    private String IpfsHash;
    private int PinSize;
    private String Timestamp;
    private boolean isDuplicate;

    public void setIpfsHash(String ipfsHash) {
        IpfsHash = ipfsHash;
    }

    public void setPinSize(int pinSize) {
        PinSize = pinSize;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public String getIpfsHash() {
        return IpfsHash;
    }

    public int getPinSize() {
        return PinSize;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public IpfsResponseUpload(String ipfsHash, int pinSize, String timestamp, boolean isDuplicate) {
        IpfsHash = ipfsHash;
        PinSize = pinSize;
        Timestamp = timestamp;
        this.isDuplicate = isDuplicate;
    }
}
