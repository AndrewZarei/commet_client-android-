package com.solana.models.buffer;

import com.syntifi.near.borshj.Borsh;
import com.syntifi.near.borshj.annotation.BorshField;

public class ConversationItemModel implements Borsh {
    @BorshField(order = 1)
    private String conversation_id;
    @BorshField(order = 2)
    private String conversation_name;
    @BorshField(order = 3)
    private String avatar;
    private Boolean new_conversation;
    private String paren_id;
    public ConversationItemModel() {
    }

    public ConversationItemModel(String conversation_id, String conversation_name, String avatar, Boolean new_conversation) {
        this.conversation_id = conversation_id;
        this.conversation_name = conversation_name;
        this.new_conversation = new_conversation;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getConversation_name() {
        return conversation_name;
    }

    public void setConversation_name(String conversation_name) {
        this.conversation_name = conversation_name;
    }

    public Boolean getNew_conversation() {
        return new_conversation;
    }

    public void setNew_conversation(Boolean new_conversation) {
        this.new_conversation = new_conversation;
    }

    public String getParen_id() {
        return paren_id;
    }

    public void setParen_id(String paren_id) {
        this.paren_id = paren_id;
    }
}
