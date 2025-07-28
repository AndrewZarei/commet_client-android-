package com.solana.models.buffer;


import com.syntifi.near.borshj.Borsh;
import com.syntifi.near.borshj.annotation.BorshField;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageModel implements Borsh {
    @BorshField(order = 1)
    private String message_id;
    @BorshField(order = 2)
    private String text;
    @BorshField(order = 3)
    private String time;
    @BorshField(order = 4)
    private List<UserModel> seen_by;
    @BorshField(order = 5)
    private String sender_address;
    @BorshField(order = 6)
    private String message_type;
    private boolean offlineAdded;
    private String status;
    private String state;
    private String image;
    private String checkUploadIpfs;
    private String name;
    private String size;

    public MessageModel(MessageModel newObj) {
        this(newObj.message_id, newObj.text, newObj.time, newObj.seen_by, newObj.sender_address, newObj.status, newObj.state, newObj.message_type, newObj.image, newObj.checkUploadIpfs, newObj.name, newObj.size);
    }
    public MessageModel() {
    }


    public MessageModel(String message_id, String text,
                        String time, List<UserModel> seen_by,
                        String sender_address, String status,
                        String state, String message_type, String image, String checkUploadIpfs, String name, String size) {
        this.message_id = message_id;
        this.text = text;
        this.time = time;
        this.seen_by = seen_by;
        this.sender_address = sender_address;
        this.status = status;
        this.state = state;
        this.message_type = message_type;
        this.image = image;
        this.checkUploadIpfs = checkUploadIpfs;
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getCheckUploadIpfs() {
        return checkUploadIpfs;
    }

    public void setCheckUploadIpfs(String checkUploadIpfs) {
        this.checkUploadIpfs = checkUploadIpfs;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOfflineAdded() {
        return offlineAdded;
    }

    public MessageModel setOfflineAdded(boolean offlineAdded) {
        this.offlineAdded = offlineAdded;
        return this;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime2() {
        return time;
    }

    public String getTime3() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {}
        if (date != null) {
            Calendar mydate = new GregorianCalendar();
            mydate.setTime(date);
            return mydate.get(Calendar.YEAR) + "-" + mydate.get(Calendar.MONTH) + "-" + mydate.get(Calendar.DAY_OF_MONTH) + " " + mydate.get(Calendar.HOUR_OF_DAY) + ":" + mydate.get(Calendar.MINUTE) + ":" + mydate.get(Calendar.SECOND);
        } else
            return time;
    }

    public String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
//        بخformat.setTimeZone(TimeZone.getTimeZone("GMT+4:30"));
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
//            e.printStackTrace();
        }
        if (date != null) {
            Calendar mydate = new GregorianCalendar();
            mydate.setTime(date);
//            return mydate.get(Calendar.YEAR) + "-" + mydate.get(Calendar.MONTH) + "-" + mydate.get(Calendar.DAY_OF_MONTH) + " " + mydate.get(Calendar.HOUR_OF_DAY) + ":" + mydate.get(Calendar.MINUTE) + ":" + mydate.get(Calendar.SECOND);
            return mydate.get(Calendar.HOUR_OF_DAY) + ":" + mydate.get(Calendar.MINUTE);
        } else
            return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<UserModel> getSeen_by() {
        return seen_by;
    }

    public void setSeen_by(List<UserModel> seen_by) {
        this.seen_by = seen_by;
    }

    public String getSender_address() {
        return sender_address;
    }

    public void setSender_address(String sender_address) {
        this.sender_address = sender_address;
    }
}
