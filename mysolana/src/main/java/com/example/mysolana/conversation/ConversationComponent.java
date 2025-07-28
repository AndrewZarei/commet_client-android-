package com.example.mysolana.conversation;

import com.example.mysolana.encryptdecrypt.EncryptdecryptJavaHelper;
import com.example.mysolana.encryptdecrypt.RSAUtil;
import com.solana.SolanaHelper;
import com.solana.core.Account;
import com.solana.core.PublicKey;
import com.solana.models.buffer.ConversationModel;
import com.solana.models.buffer.MessageModel;
import com.solana.models.buffer.UserModel;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConversationComponent {
    ConversationInterface conversationsInterface;
    CreateConversationInterface createConversationInterface;
    SendMessageInterface sendMessageInterface;
    AddMemberInterface addMemberInterface;
    private static final BlockingQueue<SendTask> queue = new LinkedBlockingQueue<>();
    private static volatile boolean isRunning = false;

    static {
        startWorker();
    }

    public ConversationComponent(ConversationInterface conversationsInterface, CreateConversationInterface createConversationInterface, SendMessageInterface sendMessageInterface) {
        this.conversationsInterface = conversationsInterface;
        this.createConversationInterface = createConversationInterface;
        this.sendMessageInterface = sendMessageInterface;
    }

    public void getConversation(boolean change, boolean check, String id) {

        SolanaHelper.INSTANCE.getConversation(new PublicKey(id), new SolanaHelper.OnResponseData<ConversationModel>() {
            @Override
            public void onSuccess(ConversationModel data) {
                conversationsInterface.getConversation(data, StateConversation.SUCCESS, change, check);
            }

            @Override
            public void onFailure(Exception e) {
                conversationsInterface.getConversation(null, StateConversation.FAILURE, change, check);
            }
        });
    }

    public void createConversation(
            String main32ByteKey,
            String userID,
            String userName,
            String id,
            boolean startChat,
            boolean is_private,
            Runnable getconver,
            String idShared,
            byte[] secret,
            String usernameShared,
            String indexAvatar,
            ConversationComponent.CallBackDataGetConversation callBackData,
            CallBackData<String> call) throws JSONException {
                assert main32ByteKey != null;
                String token_cipher_userOne = "";
                String token_cipher_userTwo = "";
                try {
                    RSAUtil.encrypt("sajjad you can do it");
                    token_cipher_userOne = EncryptdecryptJavaHelper.encrypt(idShared, main32ByteKey);
                    token_cipher_userOne = RSAUtil.encrypt(main32ByteKey);
                    token_cipher_userTwo = RSAUtil.encrypt(main32ByteKey);
                } catch (Exception e) {throw new RuntimeException(e);}
            List<UserModel> users = new ArrayList<>();
            if (usernameShared.equals("PDA")){
                String[] a = userName.split("&_#");
                users.add(new UserModel(new PublicKey(idShared).toBase58(), a[0], token_cipher_userOne));
                users.add(new UserModel(new PublicKey(userID).toBase58(), a[1], token_cipher_userTwo));
                SolanaHelper.INSTANCE.createConversation(new PublicKey(idShared), new Account(secret), userName + "@" + id, users, is_private, indexAvatar, new SolanaHelper.OnResponseStr() {
                    @Override
                    public void onSuccess(String str) {
                        createConversationInterface.create1(str, StateCreateConversation.SUCCESS, getconver, callBackData, call);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        createConversationInterface.create1(e.getMessage(), StateCreateConversation.FAILURE, getconver, callBackData, call);
                    }
                });
            } else {
                users.add(new UserModel(new PublicKey(idShared).toBase58(), usernameShared, token_cipher_userOne));
                users.add(new UserModel(new PublicKey(userID).toBase58(), userName, token_cipher_userTwo));
                SolanaHelper.INSTANCE.createConversation(new PublicKey(idShared), new Account(secret), usernameShared + "&_#" + userName, users, is_private, indexAvatar, new SolanaHelper.OnResponseStr() {
                    @Override
                    public void onSuccess(String str) {
                        createConversationInterface.create1(str, StateCreateConversation.SUCCESS, getconver, callBackData, call);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        createConversationInterface.create1(e.getMessage(), StateCreateConversation.FAILURE, getconver, callBackData, call);
                    }
                });
            }
    }

    private static class SendTask {
        final String main32SecretKey;
        final String userPrivateKey;
        final boolean loop;
        final String id;
        final MessageModel model;
        final CallBackSendMessage callBackSendMessage;
        final SendMessageInterface sendMessageInterface;

        public SendTask(String main32SecretKey, String userPrivateKey, boolean loop, String id, MessageModel model, CallBackSendMessage callBackSendMessage, SendMessageInterface sendMessageInterface) {
            this.main32SecretKey = main32SecretKey;
            this.userPrivateKey = userPrivateKey;
            this.loop = loop;
            this.id = id;
            this.model = model;
            this.callBackSendMessage = callBackSendMessage;
            this.sendMessageInterface = sendMessageInterface;
        }

        public void send() {
            MessageModel m = new MessageModel(model);
            try {
                String encryptedMessage = EncryptdecryptJavaHelper.encrypt(m.getText(), main32SecretKey);
                m.setText(encryptedMessage);
            } catch (Exception e) {
                sendMessageInterface.send(e.getMessage(), model, loop, StateSendMessage.FAILURE, callBackSendMessage);
                return;
            }
            SolanaHelper.INSTANCE.sendMessage(userPrivateKey, new PublicKey(id), m, new SolanaHelper.OnResponseE() {
                @Override
                public void onSuccess() {
                    sendMessageInterface.send("success", model, loop, StateSendMessage.SUCCESS, callBackSendMessage);
                }

                @Override
                public void onFailure(Exception e) {
                    sendMessageInterface.send(e.getMessage(), model, loop, StateSendMessage.FAILURE, callBackSendMessage);
                }
            });
        }
    }

    public void sendMessage(String main32SecretKey, String userPrivateKey, boolean loop, String id, MessageModel model, CallBackSendMessage callBackSendMessage) {
        SendTask task = new SendTask(main32SecretKey, userPrivateKey, loop, id, model, callBackSendMessage, sendMessageInterface);
        queue.offer(task);
    }

    public static void stopWorker() {
        isRunning = false;
    }

    private static synchronized void startWorker() {
        if (!isRunning) {
            isRunning = true;
            Thread worker = new Thread(new WorkerRunnable(), "MessageQueueWorker");
            worker.start();
        }
    }

    private static class WorkerRunnable implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    SendTask task = queue.take();
                    task.send();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface CallBackSendMessage {
        void run(boolean create);
    }

    public interface CallBackDataGetConversation {
        void data(ConversationModel data, boolean back);

        void onFailure(String id);
    }

    public interface CallBackData<T> {
        void data(T data);
    }

}
