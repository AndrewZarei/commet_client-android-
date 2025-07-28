package com.example.mysolana.contact;

import com.example.mysolana.encryptdecrypt.EncryptdecryptJavaHelper;
import com.solana.SolanaHelper;
import com.solana.core.Account;
import com.solana.core.PublicKey;
import com.solana.models.buffer.ContactListModel;
import com.solana.models.buffer.ContactModel;
import com.solana.models.buffer.UserModel;
import java.util.ArrayList;
import java.util.List;

public class ContactComponent {

    ContactInterface contactInterface;
    CreateConversationInterface createConversationInterface;

    public ContactComponent(ContactInterface contactInterface, CreateConversationInterface conversationInterface) {
        this.contactInterface = contactInterface;
        this.createConversationInterface = conversationInterface;
    }

    public ContactComponent(CreateConversationInterface conversationInterface) {
        this.createConversationInterface = conversationInterface;
    }

    public ContactComponent(ContactInterface contactInterface) {
        this.contactInterface = contactInterface;
    }

    public void getContacts(String[] publicKey) {

        List<ContactModel> finalList = new ArrayList<>();
        int i = 1;
        for (String key : publicKey)
        {
            int a = i++;
            SolanaHelper.INSTANCE.getContacts(new PublicKey(key), new SolanaHelper.OnResponseData<ContactListModel>() {
                @Override
                public void onSuccess(ContactListModel contactListModel) {
                    finalList.addAll(contactListModel.getIndex());
                    if (a == publicKey.length){
                        get_Contact(finalList);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    contactInterface.getContact(e.getMessage(),null, StateContact.SUCCESS);
                }
            });
        }
    }

    private void get_Contact(List<ContactModel> finalList){
        contactInterface.getContact("SUCCESS",finalList, StateContact.SUCCESS);
    }

    public void createConversation(String name, List<ContactModel> contactModels, String id, String username,boolean is_private, byte[] secret,String walletUser1,String walletUser2, String indexAvatar) {
        try{
            EncryptdecryptJavaHelper encryptdecryptJavaHelper = new EncryptdecryptJavaHelper();
            String text32bytes = encryptdecryptJavaHelper.generate32ByteKey();
            String encryptedWalletPubkey1 = encryptdecryptJavaHelper.encrypt(text32bytes,walletUser1);

            List<UserModel> users = new ArrayList<>();
//            users.add(new UserModel(new PublicKey(id).toBase58(), username,encryptedWalletPubkey1));
            users.add(new UserModel(new PublicKey(id).toBase58(), username,""));
            for (int i = 0; i < contactModels.size(); i++) {
                String encryptedWalletPubkey2 = encryptdecryptJavaHelper.encrypt(text32bytes,contactModels.get(i).getBase_pubkey());
//                users.add(new UserModel(new PublicKey(contactModels.get(i).getPublic_key()).toBase58(), contactModels.get(i).getUser_name(),encryptedWalletPubkey2));
                users.add(new UserModel(new PublicKey(contactModels.get(i).getPublic_key()).toBase58(), contactModels.get(i).getUser_name(),""));
            }

            SolanaHelper.INSTANCE.createConversation(new PublicKey(id), new Account(secret), name, users,is_private, indexAvatar,  new SolanaHelper.OnResponseStr() {
                @Override
                public void onSuccess(String str) {
                    createConversationInterface.create(StateCreateConversation.SUCCESS,str);

                }

                @Override
                public void onFailure(Exception e) {
                    createConversationInterface.create(StateCreateConversation.FAILURE,e.getMessage());
                }
            });
        } catch (Exception e) {

        }
    }
}
