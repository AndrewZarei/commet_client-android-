package com.blockchain.commet.data.database;

public class Logs {
    String type;
    String name;
    String date;
    String logName;

    public Logs(String type, String name, String date, String logName) {
        this.type = type;
        this.name = name;
        this.date = date;
        this.logName = logName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getLogName() {
        return logName;
    }
}
