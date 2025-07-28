package com.example.mysolana.conversations;

import android.util.Log;

import com.solana.SolanaHelper;
import com.solana.core.PublicKey;
import com.solana.models.buffer.ProfileModel;

public class ConversationsComponent {
    ConversationsInterface conversationsInterface;

    public ConversationsComponent(ConversationsInterface conversationsInterface) {
        this.conversationsInterface = conversationsInterface;
    }

    public void getConversations(String id) {
        if (id.isEmpty())
            return;
        SolanaHelper.INSTANCE.getConversations(new PublicKey(id), new SolanaHelper.OnResponseData() {
            @Override
            public void onSuccess(Object profileModel) {
                conversationsInterface.getConversations("SUCCESS", (ProfileModel)profileModel, StateConversations.SUCCESS);
            }

            @Override
            public void onFailure(Exception e) {
                conversationsInterface.getConversations(e.getMessage(), null, StateConversations.FAILURE);

            }
        });
    }

}
