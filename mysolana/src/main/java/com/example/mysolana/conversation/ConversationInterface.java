package com.example.mysolana.conversation;

import com.solana.models.buffer.ConversationModel;
import com.solana.models.buffer.ProfileModel;

public interface ConversationInterface {
    void getConversation(ConversationModel conversationModel , StateConversation stateConversations,boolean change,boolean check);
}
