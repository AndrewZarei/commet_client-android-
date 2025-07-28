package com.solana.models.buffer;


import com.syntifi.near.borshj.Borsh;
import com.syntifi.near.borshj.annotation.BorshField;


import java.util.List;

public class ConversationModel implements Borsh {
    @BorshField(order = 1)
    private String conversation_name;
    @BorshField(order = 2)
    private String created_time;
    @BorshField(order = 3)
    private List<MessageModel> messages;
    @BorshField(order = 4)
    private List<UserModel> members;
    @BorshField(order = 5)
    private UserModel admin;
    @BorshField(order = 6)
    private boolean is_private;
    public ConversationModel() {
    }

    public ConversationModel(String conversation_name, String created_time, List<MessageModel> messages, List<UserModel> members, UserModel admin,boolean is_private) {
//    public ConversationModel(String conversation_name, String created_time, List<MessageModel> messages, List<UserModel> members, UserModel admin) {
        this.conversation_name = conversation_name;
        this.created_time = created_time;
        this.messages = messages;
        this.members = members;
        this.admin = admin;
        this.is_private = is_private;
    }

//    public boolean isIs_private() {
//        return is_private;
//    }
//    public void setIs_private(boolean is_private) {
//        this.is_private = is_private;
//    }

    public String getConversation_name() {
        return conversation_name;
    }

    public void setConversation_name(String conversation_name) {
        this.conversation_name = conversation_name;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }

    public List<UserModel> getMembers() {
        return members;
    }

    public void setMembers(List<UserModel> members) {
        this.members = members;
    }

    public UserModel getAdmin() {
        return admin;
    }

    public void setAdmin(UserModel admin) {
        this.admin = admin;
    }
}
