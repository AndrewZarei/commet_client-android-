package com.example.mysolana.customipfs;

public interface CustomIpfsComponentInterface {
    void uploadResponse(String error,String ipfsHash,String uploadID,String name, String type, String size,String base64String);
    void downloadResponse(String error,String content,String name, String type, String size, int position,String message_id);
}
