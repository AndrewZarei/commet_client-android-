package com.example.mysolana.customipfs;

public class IpfsResponseDownload {
    private String content;
    private String name;
    private String size;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public IpfsResponseDownload(String content,String name, String type, String size) {
        this.content = content;
        this.name = name;
        this.size = size;
        this.type = type;
    }
}
