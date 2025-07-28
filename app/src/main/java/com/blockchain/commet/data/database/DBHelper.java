package com.blockchain.commet.data.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.solana.models.buffer.ContactModel;
import com.solana.models.buffer.ConversationItemModel;
import com.solana.models.buffer.ConversationModel;
import com.solana.models.buffer.MessageModel;
import com.solana.models.buffer.UserModel;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "DBLogs", null, 1);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String TBMessages = "CREATE TABLE TBMessages ( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "message_id TEXT UNIQUE, " +
                "text TEXT, " +
                "time TEXT, " +
                "status TEXT, " +
                "state TEXT, " +
                "sender_address TEXT, " +
                "seen_by TEXT, " +
                "members TEXT, " +
                "admin TEXT, " +
                "conversation_name TEXT, " +
                "created_time TEXT, " +
                "type TEXT, " +
                "name TEXT, " +
                "size TEXT, " +
                "img TEXT, " +
                "conversation_id TEXT, " +
                "checkUploadIpfs TEXT )";

        String TBConversations = "CREATE TABLE TBConversations ( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "conversation_id TEXT UNIQUE, " +
                "parent_id TEXT," +
                "conversation_name TEXT, " +
                "avatar TEXT, " +
                "new_message TEXT," +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (parent_id) REFERENCES TBConversations(conversation_id) ON DELETE CASCADE )";

        String TBContacts = "CREATE TABLE TBContacts ( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_name TEXT, " +
                "last_name TEXT, " +
                "public_key DATE, " +
                "base_pubkey DATE, " +
                "avatar TEXT )";

        String TBContactsTemp = "CREATE TABLE TBContactsTemp ( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_name TEXT, " +
                "last_name TEXT, " +
                "public_key DATE, " +
                "base_pubkey DATE, " +
                "avatar TEXT )";

        String TBLogs = "CREATE TABLE TBLogs ( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT, " +
                "name TEXT, " +
                "date DATE, " +
                "logName TEXT )";

        sqLiteDatabase.execSQL(TBLogs);
        sqLiteDatabase.execSQL(TBContacts);
        sqLiteDatabase.execSQL(TBContactsTemp);
        sqLiteDatabase.execSQL(TBConversations);
        sqLiteDatabase.execSQL(TBMessages);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DropTable = "DROP TABLE IF EXISTS TBLogs";
        sqLiteDatabase.execSQL(DropTable);
        onCreate(sqLiteDatabase);
    }

    public void insertLogs(Logs logs) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("type", logs.getType());
            contentValues.put("name", logs.getName());
            contentValues.put("date", String.valueOf(logs.getDate()));
            contentValues.put("logName", logs.getName());
            db.insert("TBLogs", null, contentValues);
            db.close();
        } catch (Exception error) {
            Log.e("DBHelper", "Error inserting logs: " + error.getMessage());
        }
    }

    public void deleteLogs(String value) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM TBLogs WHERE date='" + value + "'");
            db.close();
        } catch (Exception e) {
            Log.e("DBHelper", "Error deleting logs: " + e.getMessage());
        }
    }

    public void deleteLogsAll() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM TBLogs");
            db.close();
        } catch (Exception e) {
            Log.e("DBHelper", "Error deleting all logs: " + e.getMessage());
        }
    }

    public ArrayList<Logs> GetLogs(String value) {
        ArrayList<Logs> logs = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String getLogs = "SELECT * FROM TBLogs ORDER BY ID " + value;
        Cursor cursor = db.rawQuery(getLogs, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            logs.add(new Logs(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4)));
            cursor.moveToNext();
        }
        db.close();
        return logs;
    }


    public void insertContacts(ContactModel contactModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_name", contactModel.getUser_name());
            contentValues.put("last_name", contactModel.getLast_name());
            contentValues.put("public_key", contactModel.getPublic_key());
            contentValues.put("base_pubkey", contactModel.getBase_pubkey());
            contentValues.put("avatar", contactModel.getAvatar());
            db.insert("TBContacts", null, contentValues);
            db.close();
        } catch (Exception error) {
            Log.e("DBHelper", "Error inserting contact: " + error.getMessage());
        }
    }

    public void insertContactsTemp(ContactModel contactModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_name", contactModel.getUser_name());
            contentValues.put("last_name", contactModel.getLast_name());
            contentValues.put("public_key", contactModel.getPublic_key());
            contentValues.put("base_pubkey", contactModel.getBase_pubkey());
            contentValues.put("avatar", contactModel.getAvatar());

            if (dubUpCheck(contactModel)) {
                Log.e("TAG", "Duplicate contact found, not inserting.");
            } else {
                Log.e("TAG", "Inserting new contact.");
                db.insertOrThrow("TBContactsTemp", null, contentValues);
            }

        } catch (Exception error) {
            Log.e("DBHelper", "Error inserting temp contact: " + error.getMessage());
        }
    }

    public boolean dubUpCheck(ContactModel contactModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("TBContactsTemp", null, "user_name = ?", new String[]{contactModel.getUser_name()}, null, null, null, null);
        return cursor != null && cursor.getCount() > 0;
    }

    public List<ContactModel> GetContacts() {
        List<ContactModel> contactModel = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String getContacts = "SELECT * FROM TBContacts";
        Cursor cursor = db.rawQuery(getContacts, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            contactModel.add(new ContactModel(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5)));
            cursor.moveToNext();
        }
        cursor.close();
        return contactModel;
    }

    public List<ContactModel> GetContactsTemp() {
        List<ContactModel> contactModel = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String getContacts = "SELECT * FROM TBContactsTemp";
        Cursor cursor = db.rawQuery(getContacts, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            contactModel.add(new ContactModel(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5)));
            cursor.moveToNext();
        }
        cursor.close();
        return contactModel;
    }

    public void deleteContacts() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM TBContacts");
            db.close();
        } catch (Exception e) {
            Log.e("DBHelper", "Error deleting contacts: " + e.getMessage());
        }
    }

    public boolean conversationExists(String conversationName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM TBConversations WHERE conversation_name LIKE ?", new String[]{conversationName + "%"});


        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();
        return exists;
    }

    public void insertConversations(ConversationItemModel conversationItemModel) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("conversation_id", conversationItemModel.getConversation_id());
            contentValues.put("conversation_name", conversationItemModel.getConversation_name());
            contentValues.put("parent_id", conversationItemModel.getParen_id());
            contentValues.put("new_message", conversationItemModel.getNew_conversation());
            contentValues.put("avatar", conversationItemModel.getAvatar());
            db.insert("TBConversations", null, contentValues);
        } catch (Exception error) {
            Log.e("insertConversations", "Error inserting conversation", error);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public String getMaxParentConversation(String conversationName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM TBConversations WHERE conversation_name = ? ORDER BY parent_id DESC LIMIT 1", new String[]{conversationName});

        String conversationId = null;
        if (cursor.moveToFirst()) {
            conversationId = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return conversationId;
    }

    public ArrayList<ConversationItemModel> GetConversations() {
        ArrayList<ConversationItemModel> conversationItemModel = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String getConversations = "SELECT * FROM TBConversations";
        Cursor cursor = db.rawQuery(getConversations, null);

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            conversationItemModel.add(new ConversationItemModel(cursor.getString(1), cursor.getString(3), cursor.getString(4),false));
            cursor.moveToNext();
        }
        return conversationItemModel;
    }

    public void deleteConversations() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.execSQL("DELETE FROM TBConversations");
        } catch (Exception e) {
            Log.e("deleteConversations", "Error deleting conversations", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public void updateStatus(MessageModel messageModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("Update " + "TBMessages" + " SET status='" + messageModel.getStatus() + "'  WHERE " + "message_id" + "='" + messageModel.getMessage_id() + "'");
            db.close();
        } catch (Exception e) {
            Log.i("erfan3", e.getMessage());
        }

    }

    public void updateImage(String img, String name, String size, String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("Update " + "TBMessages" + " SET img='" + img + "', name=' " + name + "', size=' " + size + "' WHERE " + "message_id" + "='" + id + "'");
            db.close();
        } catch (Exception e) {
            Log.i("erfan3", e.getMessage());
        }

    }

    public void deleteMessageUploadFile(String message_id) {
        try {
            String deleteQuery = "DELETE FROM " + "TBMessages" + " WHERE message_id='" + message_id + "'";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(deleteQuery);
        } catch (Exception e) {
            Log.i("erfan3", e.getMessage());
        }
    }

    public void updateIpfs(String ipfsHash, String id, String base64String) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "UPDATE TBMessages SET text='" + ipfsHash + "', checkUploadIpfs='1', img='" + base64String + "' WHERE message_id='" + id + "'";
            db.execSQL(sql);
        } catch (Exception e) {
            Log.i("erfan3", e.getMessage());
        }

    }

    public void insertMessages(MessageModel messageModel, String conversation_id,
                               List<UserModel> custommembers, UserModel customadmin,
                               String customconversation_name, String customcreated_time, String type,
                               String name, String size,
                               String img) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            String seen_by = new Gson().toJson(messageModel.getSeen_by());
            String members = new Gson().toJson(custommembers);
            String admin = new Gson().toJson(customadmin);

            contentValues.put("message_id", messageModel.getMessage_id());
            contentValues.put("checkUploadIpfs", messageModel.getCheckUploadIpfs() != null ? messageModel.getCheckUploadIpfs() : "1");
            contentValues.put("text", messageModel.getText() != null ? messageModel.getText() : "");
            contentValues.put("time", messageModel.getTime() != null ? messageModel.getTime() : "");
            contentValues.put("status", messageModel.getStatus());
            contentValues.put("state", messageModel.getState());
            contentValues.put("sender_address", messageModel.getSender_address() != null ? messageModel.getSender_address() : "");
            contentValues.put("seen_by", seen_by);
            contentValues.put("members", members);
            contentValues.put("admin", admin);
            contentValues.put("conversation_name", customconversation_name);
            contentValues.put("created_time", customcreated_time);
            contentValues.put("conversation_id", conversation_id);
            contentValues.put("type", type);
            contentValues.put("name", name);
            contentValues.put("size", size);
            contentValues.put("img", img);

            db.insert("TBMessages", null, contentValues);
        } catch (Exception error) {
            Log.e("insertMessages", "Error inserting message", error);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private String joinColumns(String[] columns) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(columns[i]);
        }
        return sb.toString();
    }

    @SuppressLint("Range")
    public void removeDuplicates() {
        String[] columnsToSearch = {"message_id"};
        String selectDuplicatesQuery = "SELECT MAX(id) AS id FROM " + "TBMessages" +
                " GROUP BY " + joinColumns(columnsToSearch);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectDuplicatesQuery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String deleteQuery = "DELETE FROM " + "TBMessages" + " WHERE id != " + id;
                db.execSQL(deleteQuery);
            }
            cursor.close();
        }
    }

    @SuppressLint("Range")
    public ConversationModel getMessages(String conversation_id1) {
        ArrayList<MessageModel> messageModel = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Gson gson = new Gson();
        String conversation_name = "";
        String created_time = "";
        UserModel admin = new UserModel();
        List<UserModel> members = new ArrayList<>();

        try {
            db = this.getWritableDatabase();
            String getMessages = "SELECT * FROM TBMessages WHERE conversation_id=?";
            cursor = db.rawQuery(getMessages, new String[]{conversation_id1});

            if (cursor.moveToFirst()) {
                Type type = new TypeToken<List<UserModel>>() {}.getType();
                Type type1 = new TypeToken<UserModel>() {}.getType();

                do {
                    String seenByJson = cursor.getString(cursor.getColumnIndex("seen_by"));
                    String members1 = cursor.getString(cursor.getColumnIndex("members"));
                    String admin1 = cursor.getString(cursor.getColumnIndex("admin"));

                    List<UserModel> seenByList = gson.fromJson(seenByJson, type);

                    // Extract conversation and other meta data only once (first row)
                    if (cursor.getPosition() == 0) {
                        members = gson.fromJson(members1, type);
                        admin = gson.fromJson(admin1, type1);
                        conversation_name = cursor.getString(cursor.getColumnIndex("conversation_name"));
                        created_time = cursor.getString(cursor.getColumnIndex("created_time"));
                    }

                    String message_id = cursor.getString(cursor.getColumnIndex("message_id"));
                    String checkUploadIpfs = cursor.getString(cursor.getColumnIndex("checkUploadIpfs"));
                    String text = cursor.getString(cursor.getColumnIndex("text"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    String sender_address = cursor.getString(cursor.getColumnIndex("sender_address"));
                    String status = cursor.getString(cursor.getColumnIndex("status"));
                    String state = cursor.getString(cursor.getColumnIndex("state"));
                    String type_message = cursor.getString(cursor.getColumnIndex("type"));
                    String name_message = cursor.getString(cursor.getColumnIndex("name"));
                    String size_message = cursor.getString(cursor.getColumnIndex("size"));
                    String img = cursor.getString(cursor.getColumnIndex("img"));

                    if (message_id != null) {
                        messageModel.add(new MessageModel(
                                message_id, text, time, seenByList, sender_address, status, state, type_message,
                                img, checkUploadIpfs, name_message, size_message
                        ));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("getMessages", "Error retrieving messages", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return new ConversationModel(conversation_name, created_time, messageModel, members, admin, true);
    }

    public void dropTables() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE " + "TBMessages");
            db.close();
        } catch (Exception e) {
            Log.i("erfan3", e.getMessage());
        }
    }

    public void deleteConversationsMessage() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + "TBMessages");
            db.close();
        } catch (Exception e) {
            Log.i("erfan3", e.getMessage());
        }

    }

    public void deleteOneConversations(String conversation_id1) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + "TBMessages" + " WHERE " + "conversation_id" + "='" + conversation_id1 + "'");
            db.close();
        } catch (Exception e) {
            Log.i("erfan3", e.getMessage());
        }
    }
}
