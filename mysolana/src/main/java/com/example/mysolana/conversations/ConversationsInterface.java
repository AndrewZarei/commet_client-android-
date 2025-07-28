package com.example.mysolana.conversations;

import com.solana.models.buffer.ProfileModel;

public interface ConversationsInterface {
    void getConversations(String error, ProfileModel profileModel, StateConversations stateConversations);
}
