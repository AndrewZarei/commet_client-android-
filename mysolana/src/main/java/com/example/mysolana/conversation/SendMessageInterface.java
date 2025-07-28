package com.example.mysolana.conversation;

import com.solana.models.buffer.ConversationModel;
import com.solana.models.buffer.MessageModel;

public interface SendMessageInterface {
    void send(String str,MessageModel model , boolean loop, StateSendMessage stateSendMessage, ConversationComponent.CallBackSendMessage callBackSendMessage);
}
