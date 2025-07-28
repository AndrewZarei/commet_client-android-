package com.solana.models.buffer;

import com.syntifi.near.borshj.Borsh;
import com.syntifi.near.borshj.annotation.BorshField;

import java.util.List;

public class ProfileModel implements Borsh {
    @BorshField(order = 1)
    private String userName;
    @BorshField(order = 2)
    private String createdTime;
    @BorshField(order = 3)
    private String index;
    @BorshField(order = 4)
    private List<ConversationItemModel> conversation_list;



    public ProfileModel() {
    }
    public ProfileModel(String userName, String createdTime, List<ConversationItemModel> conversation_list,String index) {
        this.userName = userName;
        this.createdTime = createdTime;
        this.conversation_list = conversation_list;
        this.index = index;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public List<ConversationItemModel> getConversation_list() {
        return conversation_list;
    }

    public void setConversation_list(List<ConversationItemModel> conversation_list) {
        this.conversation_list = conversation_list;
    }
}
