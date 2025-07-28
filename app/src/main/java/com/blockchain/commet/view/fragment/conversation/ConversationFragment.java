package com.blockchain.commet.view.fragment.conversation;

import static android.app.Activity.RESULT_OK;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blockchain.commet.data.database.DBHelper;
import com.blockchain.commet.data.database.Logs;
import com.blockchain.commet.R;
import com.blockchain.commet.data.sharepref.SharedPrefsHelper;
import com.blockchain.commet.view.fragment.users.AdapterUser;
import com.blockchain.commet.base.BaseFragment;
import com.blockchain.commet.databinding.ConversationFragmentBinding;
import com.blockchain.commet.util.NotificationUtilsKt;
import com.example.mysolana.contact.BalanceComponent;
import com.example.mysolana.contact.BalanceComponentInterface;
import com.example.mysolana.conversation.AddMemberInterface;
import com.example.mysolana.conversation.ConversationComponent;
import com.example.mysolana.conversation.ConversationInterface;
import com.example.mysolana.conversation.CreateConversationInterface;
import com.example.mysolana.conversation.SendMessageInterface;
import com.example.mysolana.conversation.StateAddMember;
import com.example.mysolana.conversation.StateConversation;
import com.example.mysolana.conversation.StateCreateConversation;
import com.example.mysolana.conversation.StateSendMessage;
import com.example.mysolana.conversations.ConversationsComponent;
import com.example.mysolana.conversations.ConversationsInterface;
import com.example.mysolana.conversations.StateConversations;
import com.example.mysolana.customipfs.CustomIpfsComponent;
import com.example.mysolana.customipfs.CustomIpfsComponentInterface;
import com.example.mysolana.encryptdecrypt.EncryptdecryptJavaHelper;
import com.example.mysolana.encryptdecrypt.RSAUtil;
import com.google.gson.Gson;
import com.solana.SolanaHelper;
import com.solana.core.PublicKey;
import com.solana.models.buffer.ConversationItemModel;
import com.solana.models.buffer.ConversationModel;
import com.solana.models.buffer.MessageModel;
import com.solana.models.buffer.MessageState;
import com.solana.models.buffer.MessageStatus;
import com.solana.models.buffer.ProfileModel;
import com.solana.models.buffer.SendMessageType;
import com.solana.models.buffer.UserModel;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.sol4k.Base58;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ConversationFragment extends BaseFragment implements ConversationInterface, ConversationsInterface, CreateConversationInterface, SendMessageInterface, AddMemberInterface, BalanceComponentInterface, CustomIpfsComponentInterface {
    ConversationFragmentBinding binding;
    AdapterConversation adapter;
    AdapterUser adapterUser;
    public ObservableInt vis;
    List<UserModel> currentMembers = new ArrayList<>();
    UserModel currentAdmin = new UserModel();
    String currentConversation_name = "";
    String currenttCreated_time = "";
    String id, idConversation, userID, userName, cahtName, avatar, usernameShared, message;
    boolean startChat, isChat, isfirst;
    Gson gson;
    Handler handler;
    Runnable task;
    ConversationModel conversationModel;
    boolean back = false;
    boolean lock = false;
    int counter = 0;
    ConversationComponent conversationComponent;
    BalanceComponent balanceComponent;
    List<ConversationItemModel> conversationItemModel;
    DBHelper db;
    ConversationsComponent conversationsComponent;
    int retray;
    private static final int FILE_SELECT_CODE = 0;
    CustomIpfsComponent customIpfsComponent;
    String main32SecretKey = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBHelper(getActivity());
        vis = new ObservableInt(0);
        gson = new Gson();
        startChat = false;
        idConversation = requireArguments().getString("id");
        if (idConversation == null) {
            idConversation = "y";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.conversation_fragment, container, false);
        binding.setModel(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeUI(false);
        conversationComponent = new ConversationComponent(this, this, this);
        conversationsComponent = new ConversationsComponent(this);
        balanceComponent = new BalanceComponent(ConversationFragment.this);
        if (this.isAdded()){
            balanceComponent.getUserBalance(SharedPrefsHelper.getSharedPrefsHelper().get("base_pubkey"));
        }
        conversationItemModel = db.GetConversations();

        id = requireArguments().getString("id");
        userID = requireArguments().getString("userID");
        userName = requireArguments().getString("userName");
        startChat = requireArguments().getBoolean("startChat", false);
        isChat = requireArguments().getBoolean("isChat");
        isfirst = requireArguments().getBoolean("first_chat", false);
        cahtName = requireArguments().getString("name");
        avatar = requireArguments().getString("avatar","1");

        switch (Objects.requireNonNull(avatar)) {
            case "1":
                binding.conversationFragmentAvatar.setImageResource(R.drawable.user_1);
                break;

            case "2":
                binding.conversationFragmentAvatar.setImageResource(R.drawable.user_2);
                break;

            case "3":
                binding.conversationFragmentAvatar.setImageResource(R.drawable.user_3);
                break;

            case "4":
                binding.conversationFragmentAvatar.setImageResource(R.drawable.user_4);
                break;

            case "5":
                binding.conversationFragmentAvatar.setImageResource(R.drawable.user_5);
                break;

            case "6":
                binding.conversationFragmentAvatar.setImageResource(R.drawable.user_6);
                break;
        }
        if (cahtName.contains("&_#")) {
            String[] names = cahtName.split("&_#");
            if (names[0].equals(SharedPrefsHelper.getSharedPrefsHelper().get("username")))
                binding.textViewName.setText(names[1]);
            else
                binding.textViewName.setText(names[0]);
        } else {
            binding.textViewName.setText(cahtName);
        }
        ConversationComponent.CallBackSendMessage callBackSendMessage = create -> Log.e("erfan", "salamm33");
        if (startChat) {
            main32SecretKey = getMain32SecretKey();
        }
        back = false;
        customIpfsComponent = new CustomIpfsComponent(this);
        adapter = new AdapterConversation(getActivity(), isChat, customIpfsComponent);
        adapterUser = new AdapterUser(model -> {
            if (model.getUser_address().equals(SharedPrefsHelper.getSharedPrefsHelper().get("id")))
                return;
            Bundle bundle = new Bundle();
            bundle.putString("userID", model.getUser_address());
            bundle.putString("userName", model.getUser_name());
            bundle.putBoolean("startChat", true);
            bundle.putBoolean("isChat", true);
//            Navigation.findNavController(view).navigate(R.id.action_conversationFragment_to_profileFragment, bundle);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(false);
        binding.rec.setLayoutManager(linearLayoutManager);
        binding.rec.setAdapter(adapter);
        binding.users.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.users.setAdapter(adapterUser);

        if (startChat) {
            try {
                id = PublicKey.Companion.createWithSeed(new PublicKey(SharedPrefsHelper.getSharedPrefsHelper().get("base_pubkey")), SharedPrefsHelper.getSharedPrefsHelper().get("username") + "&_#" + userName, new PublicKey("2qT2bqsFTdD1uDQ1mqLJsnbTumeJAeymUeDC43BSmP8H")).toBase58();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        binding.appCompatButton.setOnClickListener(view12 -> {
            vis.set(0);
            binding.users.setVisibility(View.INVISIBLE);
            adapter.clear();
            getConver(true, true);
        });

        callBackSendMessage.run(true);
        binding.message.setOnClickListener(v -> binding.rec.scrollToPosition(adapter.getList().size() - 1));
        binding.send.setOnClickListener(view1 -> {
            message = Objects.requireNonNull(binding.message.getText()).toString().trim();
            if (message.isEmpty())
                return;
            binding.message.setEnabled(false);
            binding.progress.setVisibility(View.VISIBLE);
            binding.send.setVisibility(View.INVISIBLE);
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    () -> {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                        String nowAsISO = df.format(new Date());
                        MessageModel model = new MessageModel(UUID.randomUUID().toString(), message, nowAsISO, new ArrayList<>(), new PublicKey(SharedPrefsHelper.getSharedPrefsHelper().get("id")).toBase58(), MessageStatus.PENDING.name(),
                                MessageState.SEND.name(), String.valueOf(SendMessageType.text), null, null, "", "");
                        model.setOfflineAdded(true);
                        String user_secret_key = SharedPrefsHelper.getSharedPrefsHelper().get("private_key");
                        conversationComponent.sendMessage(main32SecretKey, user_secret_key, false, id, model, create -> {
                            if (create) {
                                try {
                                    conversationComponent.createConversation(getMain32SecretKey(), userID, userName, id, startChat, true,
                                            () -> getConver(false, false),
                                            SharedPrefsHelper.getSharedPrefsHelper().get("id"),
                                            Base58.decode(SharedPrefsHelper.getSharedPrefsHelper().get("private_key")),
                                            SharedPrefsHelper.getSharedPrefsHelper().get("username"), SharedPrefsHelper.getSharedPrefsHelper().get("index_profile"), callBackDataGetConversation,
                                            data -> {});
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    },
                    0);
        });
        binding.rec.scrollToPosition(adapter.getList().size() - 1);

        checkConversation();

        if (startChat) {
            usernameShared = SharedPrefsHelper.getSharedPrefsHelper().get("username");
            try {
                conversationComponent.createConversation(getMain32SecretKey(), userID, userName, id, startChat, true,
                        () -> getConver(false, false),
                        SharedPrefsHelper.getSharedPrefsHelper().get("id"),
                        Base58.decode(SharedPrefsHelper.getSharedPrefsHelper().get("private_key")), SharedPrefsHelper.getSharedPrefsHelper().get("username"), SharedPrefsHelper.getSharedPrefsHelper().get("index_profile"),
                        callBackDataGetConversation,
                        data -> {
                            Handler mainHandler = new Handler(requireContext().getMainLooper());
                            Runnable myRunnable = () -> {
                                if (isfirst)
                                    callModal();
                            };
                            mainHandler.post(myRunnable);

                            changeUI(true);
                        });
            } catch (JSONException e) {}
        } else {
            changeUI(true);
        }

        String walletbalance = SharedPrefsHelper.getSharedPrefsHelper().get("walletbalance");
        if (walletbalance == null) {
        } else {
            binding.textView3.setText("" + walletbalance + " SOL");
        }

        binding.emojiBtn.setOnClickListener(view15 -> {
            if (binding.emojiPicker.getVisibility() == View.VISIBLE) {
                binding.emojiPicker.setVisibility(View.GONE);
            } else {
                binding.emojiPicker.setVisibility(View.VISIBLE);
            }
        });
        binding.emojiPicker.setOnEmojiPickedListener(emojiViewItem -> binding.message.append(emojiViewItem.getEmoji()));

        binding.pickBtn.setOnClickListener(view14 -> updateSpaceModal());

        binding.toolbar.setOnClickListener(view13 -> {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("id", adapterUser.getMembers());
            bundle.putString("userName", userName);
            bundle.putString("cahtName", cahtName);
            bundle.putString("avatar", avatar);
            Navigation.findNavController(requireView()).navigate(R.id.action_conversationsFragment_to_detail_conversation, bundle);
        });
    }

    @Override
    public void getConversations(String str, ProfileModel profileModel, StateConversations stateConversations) {
        switch (stateConversations) {
            case SUCCESS:
                try {
                    for (int i = 0; i < profileModel.getConversation_list().size(); i++) {
                        if (profileModel.getConversation_list().get(i).getConversation_id().equals(idConversation)) {
                            changeUI(true);
                            startChat = false;
                        }
                    }
                    break;
                } catch (Exception e) {
                    vis.set(3);
                }
            case FAILURE:
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                break;
        }
    }

    public void checkConversation(){
        for (int i = 0; i < conversationItemModel.size(); i++) {
            if (conversationItemModel.get(i).getConversation_name().contains(cahtName)) {
                startChat = false;
                id = conversationItemModel.get(i).getConversation_id();
                idConversation = conversationItemModel.get(i).getConversation_id();
                getConver(true, true);
            }
        }
    }

    private void startTimerConversation() {
        handler = new Handler();
        task = new Runnable() {
            @Override
            public void run() {
                getConversationOnline(true, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(task);
    }

    public String getMain32SecretKey() {
        if (main32SecretKey.isEmpty()) {
            main32SecretKey = EncryptdecryptJavaHelper.generate32ByteKey();
        }
        return main32SecretKey;
    }

    public void changeUI(boolean parentChat) {
        requireActivity().runOnUiThread(() -> {
            try {
                if (parentChat) {
                    binding.parentChat.setVisibility(View.VISIBLE);
                    binding.parentFirstConversation.setVisibility(View.GONE);
                } else {
                    binding.parentChat.setVisibility(View.GONE);
                    binding.parentFirstConversation.setVisibility(View.VISIBLE);
                }
                binding.message.setText("");
                binding.message.setEnabled(true);
                binding.progress.setVisibility(View.GONE);
                binding.send.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void updateSpaceModal() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog;
        alertDialog = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
        alertDialog.setMessage("Your conversation capacity has reached its limit! To increase the capacity, please approve a transaction of 0.3 SOL. Once the transaction is confirmed, additional space will be allocated for this conversation").setPositiveButton("OK", (dialogInterface, i) -> requireActivity().runOnUiThread(() -> {
            dialogInterface.dismiss();
            try {
                // get conversation with this conversationName start and get maximum id and should ++ and set net id and.....
                conversationComponent.createConversation(getMain32SecretKey(), userID, userName, db.getMaxParentConversation(userName), false, true,
                        () -> getConver(false, false),
                        SharedPrefsHelper.getSharedPrefsHelper().get("id"),
                        Base58.decode(SharedPrefsHelper.getSharedPrefsHelper().get("private_key")),
                        "PDA",
                        SharedPrefsHelper.getSharedPrefsHelper().get("index_profile"),
                        callBackDataGetConversation,
                        data -> {});
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        })).show();
    }

    public void callModal() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog;
        alertDialog = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
        alertDialog.setMessage("Are You Sure Pay 0.28 Sol for Update?").setPositiveButton("OK", (dialogInterface, i) -> requireActivity().runOnUiThread(() -> {
            dialogInterface.dismiss();
            requireActivity().onBackPressed();
        })).show();
    }

    private String[] queryName(Uri uri) {
        Cursor returnCursor =
                requireActivity().getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        String size = returnCursor.getString(sizeIndex);
        returnCursor.close();
        String[] tt = new String[3];
        tt[0] = name;
        tt[1] = Double.parseDouble(size) / 1024 + "";
        tt[2] = name.substring(name.lastIndexOf(".")).replaceAll("\\.", "");
        return tt;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String[] metaData = queryName(uri);
                double temp = Double.parseDouble(metaData[1]);
                if (temp > 200) {
                    Toast.makeText(getActivity(), "is the file size large than 200 KB!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (uri != null) {
                    try {
                        InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
                        if (inputStream != null) {
                            String base64String = convertInputStreamToBase64(inputStream);
                            String uploadID = UUID.randomUUID().toString();
                            addImageToRecyclerview("", uploadID, metaData[0], metaData[2], metaData[1], base64String);
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    () -> customIpfsComponent.sendIpfsRequest(base64String,
                                            metaData[0], metaData[2], metaData[1], uploadID, base64String,
                                            (bytesUploaded, totalBytes) -> {
                                                float progress = (float) bytesUploaded / totalBytes * 100;
                                                Log.i("erfanerfan", String.valueOf(progress));
                                            }),
                                    700);
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String convertInputStreamToBase64(InputStream inputStream) throws IOException {
        byte[] byteArray = new byte[inputStream.available()];
        inputStream.read(byteArray);
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void updateMessageBefore() {
        ConversationModel messageBefore = db.getMessages(idConversation);
        String userSecretKey = SharedPrefsHelper.getSharedPrefsHelper().get("private_key");
        for (MessageModel message : messageBefore.getMessages()) {
            if (MessageStatus.PENDING.name().equals(message.getStatus())) {
                sendMessageAndCreateConversation(message, userSecretKey);
            }
        }
    }

    private void sendMessageAndCreateConversation(MessageModel message, String userSecretKey) {
        conversationComponent.sendMessage(main32SecretKey, userSecretKey, false, id, message, create -> {
            if (create) {
                try {
                    conversationComponent.createConversation(
                            getMain32SecretKey(), userID, userName, id, startChat, true,
                            () -> getConver(false, false),
                            SharedPrefsHelper.getSharedPrefsHelper().get("id"),
                            Base58.decode(SharedPrefsHelper.getSharedPrefsHelper().get("private_key")),
                            SharedPrefsHelper.getSharedPrefsHelper().get("username"),
                            SharedPrefsHelper.getSharedPrefsHelper().get("index_profile"),
                            callBackDataGetConversation, data -> {}
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String decrypted(String encryptedData, String KEY) throws Exception {
        return EncryptdecryptJavaHelper.decrypt(EncryptdecryptJavaHelper.hexToBytes(encryptedData), KEY);
    }

    private void getConver(boolean change, boolean reverse) {
        ConversationModel conversationModel1 = db.getMessages(idConversation);
        currentMembers = conversationModel1.getMembers();
        currentAdmin = conversationModel1.getAdmin();
        currentConversation_name = conversationModel1.getConversation_name();
        currenttCreated_time = conversationModel1.getCreated_time();
        List<MessageModel> finalMessageModel = conversationModel1.getMessages();
        requireActivity().runOnUiThread(() -> {
            if (!conversationModel1.getMessages().isEmpty()) {
                for (int i = 0; i < conversationModel1.getMessages().size(); i++) {
                    try {
                        conversationModel1.getMessages().get(i).setText(decrypted(finalMessageModel.get(i).getText(), main32SecretKey));
                    } catch (Exception e) {
                        conversationModel1.getMessages().get(i).setText(finalMessageModel.get(i).getText());
                    }
                }
                binding.lblCreate.setText("Created at " + finalMessageModel.get(0).getTime3());
                if (conversationModel1.getMembers().size() > 2) {
                    requireActivity().runOnUiThread(() -> binding.textViewCounter.setText(adapterUser.getItemCount() + " members"));
                    requireActivity().runOnUiThread(() -> binding.users.setVisibility(View.VISIBLE));
                }
                adapterUser.addAll(conversationModel1.getMembers());
                adapter.setUsers(conversationModel1.getMembers());
                adapter.addAll(finalMessageModel);
                binding.rec.smoothScrollToPosition(adapter.getItemCount() - 1);
                vis.set(1);
            }
            getConversationOnline(change, reverse);
            startTimerConversation();
        });
    }

    ConversationComponent.CallBackDataGetConversation callBackDataGetConversation = new ConversationComponent.CallBackDataGetConversation() {
        @Override
        public void data(ConversationModel data, boolean back2) {
            if (back2) {
                back = true;
            } else {
                conversationModel = data;
            }
        }

        @Override
        public void onFailure(String id2) {
            id = id2;
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.leave) {
            leave();
        } else if (item.getItemId() == R.id.add) {
//            if (conversationModel != null) {
//                new BottomSheetAddMember((member, bottomSheetAddMember) -> {
//                    bottomSheetAddMember.dismiss();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setMessage(requireActivity().getString(R.string.add_member, member.getUser_name()));
//                    builder.setPositiveButton(R.string.add, (dialogInterface, i) -> {
//                        for (int j = 0; j < conversationModel.getMembers().size(); j++) {
//                            if (conversationModel.getMembers().get(j).getUser_address().trim().equals(member.getUser_address().trim())) {
//                                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), R.string.already_added, Toast.LENGTH_SHORT).show());
//                                return;
//                            }
//                        }
//                    });
//                    builder.setNegativeButton(R.string.cancel, null);
//                    builder.show();
//                }).show(getChildFragmentManager(), "BottomSheetAddMember");
//            }
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_conversation, menu);
    }

    private void leave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.leave);
        builder.setPositiveButton("Leave", (dialogInterface, i) -> SolanaHelper.INSTANCE.leftUser(new PublicKey(id), new PublicKey(SharedPrefsHelper.getSharedPrefsHelper().get("id")), new SolanaHelper.OnResponseE() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> Navigation.findNavController(requireView()).popBackStack());
            }

            @Override
            public void onFailure(@Nullable Exception e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show());
            }
        }));
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();

    }

    private void getConversationOnline(boolean change, boolean reverse){
        if (!lock){
            lock = true;
            conversationComponent.getConversation(change, reverse, id);
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void getConversation(ConversationModel conversationModelFromNetwork, StateConversation stateConversations, boolean change, boolean check) {
        lock = false;
        if (getActivity() != null) {
            switch (stateConversations) {
                case SUCCESS:
                    if (conversationModelFromNetwork != null) {
                        String privateKey = RSAUtil.privateKey;
                        try {
                            main32SecretKey = RSAUtil.decrypt(conversationModelFromNetwork.getMembers().get(1).getToken_cipher(), privateKey);
                        } catch (IllegalBlockSizeException | InvalidKeyException |
                                 BadPaddingException | NoSuchAlgorithmException |
                                 NoSuchPaddingException e) {}
                        updateMessageBefore();
                        ConversationComponent.CallBackDataGetConversation callBackData = callBackDataGetConversation;
                        try {
                            requireActivity().runOnUiThread(() -> binding.send.setEnabled(true));
                            callBackData.data(conversationModelFromNetwork, false);
                            List<MessageModel> messages = new ArrayList<>();
                            List<MessageModel> tmp_messages = conversationModelFromNetwork.getMessages();
                            ConversationModel temp = db.getMessages(idConversation);
                            for (int i = 0; i < tmp_messages.size(); i++) {
                                try {
                                    tmp_messages.get(i).setText(decrypted(tmp_messages.get(i).getText(), main32SecretKey));
                                    if (!tmp_messages.get(i).getMessage_type().equals("text") && tmp_messages.get(i).getText().isEmpty()){} else {
                                        messages.add(tmp_messages.get(i));
                                    }
                                } catch (Exception e) {}
                            }

                            if (!messages.isEmpty()) {
                                for (int i = 0; i < messages.size(); i++) {
                                    db.insertMessages(messages.get(i), idConversation, conversationModelFromNetwork.getMembers(),
                                            conversationModelFromNetwork.getAdmin(), conversationModelFromNetwork.getConversation_name(),
                                            conversationModelFromNetwork.getCreated_time(),
                                            messages.get(i).getMessage_type(), "", "", "");
                                }

                                //New
                                ConversationModel conversationModelFromDB = db.getMessages(idConversation);
                                //Old
//                                conversationModelFromDB = db.getMessages(idConversation);
                                currentMembers = conversationModelFromDB.getMembers();
                                currentAdmin = conversationModelFromDB.getAdmin();
                                currentConversation_name = conversationModelFromDB.getConversation_name();
                                currenttCreated_time = conversationModelFromDB.getCreated_time();
                                List<MessageModel> finalMessageModel = conversationModelFromDB.getMessages();
                                ConversationModel finalConversationModel = conversationModelFromDB;
                                getActivity().runOnUiThread(() -> {
                                    int size = adapter.getList().size();
                                    boolean scroll = !binding.rec.canScrollVertically(1) && binding.rec.getScrollState() == RecyclerView.SCROLL_STATE_IDLE;
                                    adapter.setUsers(finalConversationModel.getMembers());
                                    userID = finalConversationModel.getMembers().get(1).getUser_address();
                                    if (!finalMessageModel.isEmpty()){
                                        binding.lblCreate.setText("Created at " + finalMessageModel.get(0).getTime());
                                        for (MessageModel mess : finalMessageModel) {
                                            mess.setStatus(MessageStatus.SUCCESS_NETWORK.name());
                                        }
                                        adapter.addAll(finalMessageModel);
                                        if (temp.getMessages().size() < finalMessageModel.size()){
                                            binding.rec.smoothScrollToPosition(adapter.getItemCount() - 1);
                                        }
                                    }
                                    if (!finalConversationModel.getMembers().isEmpty()) {
                                        adapterUser.addAll(finalConversationModel.getMembers());
                                    } else {
                                        adapterUser.addAll(conversationModelFromNetwork.getMembers());
                                    }
                                    if (scroll && size < adapter.getList().size())
                                        vis.set(1);
                                });

                                vis.set(1);
                                getActivity().runOnUiThread(() -> binding.textViewCounter.setText(adapterUser.getItemCount() + " " + " members"));
                                getActivity().runOnUiThread(() -> binding.users.setVisibility(isChat ? View.INVISIBLE : View.VISIBLE));
                            } else {
                                ConversationModel conversationModel3 = db.getMessages(idConversation);
                                if (conversationModel3.getMessages().isEmpty()) {
                                    if (!conversationModelFromNetwork.getMembers().isEmpty()) {
                                        MessageModel messageModel = new MessageModel();
                                        UserModel admin = new UserModel();
                                        List<UserModel> members = new ArrayList<>();
                                        for (int i = 0; i < conversationModelFromNetwork.getMembers().size(); i++) {
                                            members.add(conversationModelFromNetwork.getMembers().get(i));
                                        }
                                        admin.setUser_address(conversationModelFromNetwork.getAdmin().getUser_address());
                                        admin.setUser_name(conversationModelFromNetwork.getAdmin().getUser_name());
                                        String customcreated_time = conversationModelFromNetwork.getCreated_time();
                                        messageModel.setTime(customcreated_time);
                                        db.insertMessages(messageModel, idConversation, members, admin, conversationModelFromNetwork.getConversation_name(),
                                                customcreated_time, messageModel.getMessage_type(),
                                                messageModel.getImage(), messageModel.getName(), messageModel.getSize()
                                        );
                                    }
                                    requireActivity().runOnUiThread(() -> vis.set(2));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (this.isAdded()){
                                DBHelper dbHelperMessageList3 = new DBHelper(requireActivity());
                                ConversationModel conversationModel4 = dbHelperMessageList3.getMessages(idConversation);
                                if (conversationModel4.getMessages().isEmpty()) {
                                    getActivity().runOnUiThread(() -> {
                                        binding.send.setEnabled(true);
                                        vis.set(2);
                                    });
                                }
                            }
                        }
                    }
                    break;
                case FAILURE:
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connection Error!!", Toast.LENGTH_SHORT).show());
                    break;
            }
        }
    }

    @Override
    public void create1(String str, StateCreateConversation stateConversations, Runnable getconver, ConversationComponent.CallBackDataGetConversation callBackData, ConversationComponent.CallBackData<String> call) {
        switch (stateConversations) {
            case SUCCESS:
                idConversation = str;
                ConversationItemModel conversationItemModel = new ConversationItemModel();
                conversationItemModel.setConversation_id(str);
                conversationItemModel.setNew_conversation(true);
                conversationItemModel.setAvatar(avatar);
                if (db.conversationExists(userName)) {
                    conversationItemModel.setConversation_name(userName + "@" + db.getMaxParentConversation(userName));
                    conversationItemModel.setParen_id(db.getMaxParentConversation(conversationItemModel.getConversation_name()));
                } else {
                    conversationItemModel.setConversation_name(usernameShared + "&_#" + userName);
                }
                db.insertConversations(conversationItemModel);
                call.data(str);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                String nowAsISO = df.format(new Date());
                List<UserModel> users = new ArrayList<>();
                users.add(new UserModel(new PublicKey(SharedPrefsHelper.getSharedPrefsHelper().get("id")).toBase58(), usernameShared, ""));
                users.add(new UserModel(new PublicKey(userID).toBase58(), userName, ""));
                currentMembers = users;
                currentAdmin = users.get(0);
                currentConversation_name = usernameShared + "&_#" + userName;
                currenttCreated_time = nowAsISO;
                MessageModel messageModel = new MessageModel();
                List<UserModel> members = new ArrayList<>();
                members.add(new UserModel(new PublicKey(SharedPrefsHelper.getSharedPrefsHelper().get("id")).toBase58(), usernameShared, ""));
                members.add(new UserModel(new PublicKey(userID).toBase58(), userName, ""));
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                String customcreated_time = df1.format(new Date());
                messageModel.setTime(customcreated_time);
                break;
            case FAILURE:
                try {
                    requireActivity().runOnUiThread(() -> {
                        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                        df2.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                        String nowAsISO1 = df2.format(new Date());
                        String CustomText = "FAILURE-" + "CreateConversation-" + str;
                        Log.e("erfan", CustomText);
                        db.insertLogs(new Logs(CustomText, "", nowAsISO1, ""));
                        try {
                            String tempSwitchButton = SharedPrefsHelper.getSharedPrefsHelper().get("switchButton");
                            if (isfirst)
                                requireActivity().onBackPressed();
                            if (tempSwitchButton.equals("true") && counter == 0) {
                                Toast.makeText(getActivity(), "Test Again", Toast.LENGTH_LONG).show();
                                counter++;
                                conversationComponent.createConversation(getMain32SecretKey(), userID, userName, id, startChat, true, () -> getConver(false, false), SharedPrefsHelper.getSharedPrefsHelper().get("id"),
                                        Base58.decode(SharedPrefsHelper.getSharedPrefsHelper().get("private_key")), SharedPrefsHelper.getSharedPrefsHelper().get("username"),
                                        SharedPrefsHelper.getSharedPrefsHelper().get("index_profile"), callBackDataGetConversation, data -> {
                                            Log.i("tag", "created conversation38");
                                        });
                            } else {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireActivity(), "Connection Error", Toast.LENGTH_LONG).show();
                                    binding.message.setEnabled(true);
                                    binding.message.setText("");
                                    binding.progress.setVisibility(View.GONE);
                                    binding.send.setVisibility(View.VISIBLE);
                                    changeUI(true);
                                });
                            }
                        } catch (Exception e1) {
                            Toast.makeText(requireActivity(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                            requireActivity().runOnUiThread(() -> {
                                binding.message.setEnabled(true);
                                binding.message.setText("");
                                binding.progress.setVisibility(View.GONE);
                                binding.send.setVisibility(View.VISIBLE);
                                changeUI(true);
                            });
                        }
                    });
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void send(String str, MessageModel model, boolean loop, StateSendMessage stateSendMessage, ConversationComponent.CallBackSendMessage callBackSendMessage) {
        switch (stateSendMessage) {
            case SUCCESS:
                try {
                    requireActivity().runOnUiThread(() -> {
                        binding.message.setEnabled(true);
                        binding.progress.setVisibility(View.GONE);
                        binding.send.setVisibility(View.VISIBLE);
                        binding.message.setText("");
                        binding.lblCreate.setText("Created at " + model.getTime());
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                        df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                        String nowAsISO = df.format(new Date());
                        String CustomText = "SUCCESS-" + "SendMessage-" + str + "sender_address: " + model.getMessage_id() +
                                " text: " + model.getText() + " message_id: " + model.getMessage_id();
                        db.insertMessages(model, idConversation, currentMembers,
                                currentAdmin, currentConversation_name, currenttCreated_time, "text", "", "", "");
                        model.setStatus(MessageStatus.SUCCESS.name());
                        db.updateStatus(model);
                        if (model.getImage() == null){
                            adapter.add(model);
                        } else {
                            adapter.imageUpdate();
                        }
                        binding.rec.scrollToPosition(adapter.getList().size() - 1);
                        vis.set(1);
                        Log.e("erfan", CustomText);
                        db.insertLogs(new Logs(CustomText, "", nowAsISO, ""));
                    });
                } catch (Exception ee) {
                    ee.printStackTrace();
                    if (this.isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            binding.message.setEnabled(true);
                            binding.progress.setVisibility(View.GONE);
                            binding.send.setVisibility(View.VISIBLE);
                            binding.message.setText("");
                            Toast.makeText(requireActivity(), ee.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                }
                callBackSendMessage.run(false);
                break;
            case FAILURE:
                try {
                    if (retray <= 1) {
                        updateMessageBefore();
                    }
                    retray++;
                    requireActivity().runOnUiThread(() -> {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                        df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                        String nowAsISO = df.format(new Date());
                        DBHelper dbHelper = new DBHelper(getActivity());
                        String CustomText = "FAILURE-" + "SendMessage-" + str + "sender_address: " + model.getMessage_id() +
                                " text: " + model.getText() + " message_id: " + model.getMessage_id();
                        Log.e("erfan", CustomText);
                        dbHelper.insertLogs(new Logs(CustomText, "", nowAsISO, ""));
                        binding.message.setEnabled(true);
                        binding.message.setText("");
                        binding.progress.setVisibility(View.GONE);
                        binding.send.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
                    });
                } catch (Exception ee) {
                    if (this.isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            binding.message.setEnabled(true);
                            binding.progress.setVisibility(View.GONE);
                            binding.send.setVisibility(View.VISIBLE);
                            binding.message.setText("");
                            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
                        });
                    }
                }
                break;
        }
    }

    @Override
    public void add(StateAddMember stateAddMember) {
        switch (stateAddMember) {
            case SUCCESS:
                requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), R.string.successfully_added, Toast.LENGTH_SHORT).show());
                break;
            case FAILURE:
                requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show());
                break;
        }
    }

    @Override
    public void create(String error, String userpublickey) {
        switch (error) {
            case ("false"): {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        String result = String.valueOf(new Float(userpublickey) / 1000000000);
                        String.valueOf(SharedPrefsHelper.getSharedPrefsHelper().put("walletbalance", result));
                        binding.textView3.setText("" + result + " SOL");
                    });
                }
                break;
            }
            case ("true"): {
                String walletbalance = SharedPrefsHelper.getSharedPrefsHelper().get("walletbalance");
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> binding.textView3.setText("" + walletbalance + " SOL"));
                }
            }
        }
    }

    @Override
    public void uploadResponse(String error, String ipfsHash, String uploadID, String name, String type, String size, String base64String) {
        if (error.equals("false")) {
            requireActivity().runOnUiThread(() -> new Handler(Looper.getMainLooper()).postDelayed(
                    () -> {
                        Toast.makeText(getActivity(), "file uploaded", Toast.LENGTH_LONG).show();
                        db.updateIpfs(ipfsHash, uploadID, base64String);
                        MessageModel model = new MessageModel();
                        ConversationModel conversationModel = db.getMessages(idConversation);
                        List<MessageModel> messageModels;
                        messageModels = conversationModel.getMessages();
                        for (int i = 0; i < messageModels.size(); i++) {
                            if (messageModels.get(i).getMessage_id().equals(uploadID)) {
                                model = messageModels.get(i);
                                model.setOfflineAdded(true);
                            }
                        }
                        String user_secret_key = SharedPrefsHelper.getSharedPrefsHelper().get("private_key");
                        conversationComponent.sendMessage(main32SecretKey, user_secret_key, false, id, model, create -> {});
                    },
                    100));
        } else {
            requireActivity().runOnUiThread(() -> {
                db.deleteMessageUploadFile(uploadID);
                adapter.remove(uploadID);
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "file not upload", Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public void downloadResponse(String error, String content, String name, String type, String size, int position, String message_id) {
        if (error.equals("false")) {
            requireActivity().runOnUiThread(() -> {
                LayoutInflater factory = LayoutInflater.from(getActivity());
                if (type.equals("jpg") || type.equals("png") || type.equals("jpeg")) {
                    db.updateImage(content, name, size, message_id);
                } else {
                    String file = saveBase64ToFile(content, name);
                    db.updateImage(file, name, size, message_id);
                    Toast.makeText(getActivity(), "File downloaded", Toast.LENGTH_SHORT).show();
                    NotificationUtilsKt.showSimpleNotification(requireContext(),1100,"File Downloaded on download folder",name);
                }
                adapter.update(content, position);
                binding.progress.setVisibility(View.GONE);
            });
        } else {
            requireActivity().runOnUiThread(() -> {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(getActivity(), content, Toast.LENGTH_LONG).show();
            });
        }
    }

    public static String saveBase64ToFile(String base64String, String filePath) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), filePath);
            File parentDir = file.getParentFile();
            assert parentDir != null;
            if (!parentDir.exists()) {

                boolean pp = parentDir.mkdir();
                Log.e("TAG", "saveBase64ToFile: ");
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(decodedBytes);
            fos.close();
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void addImageToRecyclerview(String ipfsHash, String uploadID, String name, String type, String size, String base64String) {
        requireActivity().runOnUiThread(() -> {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
            String nowAsISO = df.format(new Date());
            MessageModel model =
                    new MessageModel(uploadID, ipfsHash, nowAsISO, new ArrayList<>(),
                    new PublicKey(SharedPrefsHelper.getSharedPrefsHelper().get("id")).toBase58(),
                    MessageStatus.PENDING.name(), MessageState.SEND.name(), type.trim(), base64String, "0", name.trim(), size.trim());
            model.setOfflineAdded(true);
            db.insertMessages(model, idConversation, currentMembers,
                    currentAdmin, currentConversation_name, currenttCreated_time, type.trim(), name.trim(), size.trim(), "");
            binding.message.setEnabled(true);
            binding.progress.setVisibility(View.GONE);
            binding.send.setVisibility(View.VISIBLE);
            binding.message.setText("");
            binding.lblCreate.setText("Created at " + model.getTime());
            adapter.add(model);
            binding.rec.scrollToPosition(adapter.getList().size() - 1);
        });
    }
}


