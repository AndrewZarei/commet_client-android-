package com.example.mysolana.customipfs;

import android.util.Log;

import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.UUID;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomIpfsComponent {
    CustomIpfsComponentInterface customIpfsComponentInterface;
    IpfsResponseUpload ipfsResponseUpload;
    IpfsResponseDownload ipfsResponseDownload;
    public CustomIpfsComponent(CustomIpfsComponentInterface customIpfsComponentInterface) {
        this.customIpfsComponentInterface = customIpfsComponentInterface;
    }

    public void sendIpfsRequest(String img,String name, String type, String size,String uploadID,String base64String,ProgressListener progressListener) {
        new Thread(() -> {
            try {
//                    String img = "iVBORw0KGgoAAAANSUhEUgAAAkUAAAEWCAYAAABojOMFAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAR5SURBVHhe7dYxAcAwDMCwbBjCn+rmoywqPabgZ3e/AQC43HsKAHA1UwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAQUwQAEFMEABBTBAAwMz+o1QNnrdFzjgAAAABJRU5ErkJggg==";
                Log.d("Lasemi", "sendIpfsRequest: " + img);
                UUID uuid = UUID.randomUUID(); // Generates a Version 4 UUID
                String uuidString = uuid.toString();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                JSONObject pinataContent = new JSONObject();
                pinataContent.put("content", img);
                pinataContent.put("name", name);
                pinataContent.put("size", size);
                pinataContent.put("type", type);
                JSONObject requestBodyJson = new JSONObject();
                requestBodyJson.put("pinataOptions", new JSONObject().put("cidVersion", 0));
                requestBodyJson.put("pinataContent", pinataContent);
                requestBodyJson.put("pinataMetadata", new JSONObject().put("name", uuidString));

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType,requestBodyJson.toString());
                RequestBody requestBody = new ProgressRequestBody(
                        RequestBody.create(mediaType, requestBodyJson.toString()),
                        progressListener);
                Request request = new Request.Builder()
                        .url("https://api.pinata.cloud/pinning/pinJSONToIPFS")
                        .method("POST", requestBody)
                        .addHeader("pinata_api_key", "8b4d1b2f17e8aabafb83")
                        .addHeader("pinata_secret_api_key", "b1030db36f592a52fb045799e864471b39829133df9906ded6b72bf8e75f880b")
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = null;
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    ipfsResponseUpload = gson.fromJson(responseBody, IpfsResponseUpload.class);
                    String ipfsHash = ipfsResponseUpload.getIpfsHash();
                    customIpfsComponentInterface.uploadResponse("false", ipfsHash,uploadID,name, type,  size,base64String);
                } else {
                    customIpfsComponentInterface.uploadResponse("true", "error",uploadID,"","","","");
                }
            } catch (Exception e) {
                customIpfsComponentInterface.uploadResponse("true", e.getMessage(),uploadID,"","","","");
            }
        }).start();
    }

    public void getIpfsRequest(String img, int position, String message_id) {
        String finalImg = img;
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://gateway.pinata.cloud/ipfs/").newBuilder();
                urlBuilder.addPathSegment(finalImg);
                String url = urlBuilder.build().toString();
                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .build();
                Response response = null;
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    ipfsResponseDownload = gson.fromJson(responseBody, IpfsResponseDownload.class);
                    String ipfsContent = ipfsResponseDownload.getContent();
                    String name = ipfsResponseDownload.getName();
                    String size = ipfsResponseDownload.getSize();
                    String type = ipfsResponseDownload.getType();
                    customIpfsComponentInterface.downloadResponse("false", ipfsContent,name,type,size,position,message_id);
                } else {
                    customIpfsComponentInterface.downloadResponse("true", "error","","","",position,message_id);
                }
            } catch (Exception e) {
                customIpfsComponentInterface.downloadResponse("true", e.getMessage(),"","","",position,message_id);
            }
        }).start();
    }

}
