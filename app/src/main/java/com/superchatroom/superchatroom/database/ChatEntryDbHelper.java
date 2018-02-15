package com.superchatroom.superchatroom.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.superchatroom.superchatroom.item.MessageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that manages database creation and version management.
 */
public class ChatEntryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ChatEntry.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ChatStorageContract.ChatEntry.TABLE_NAME + " (" +
                    ChatStorageContract.ChatEntry._ID + " INTEGER PRIMARY KEY," +
                    ChatStorageContract.ChatEntry.COLUMN_NAME_TIME + " TEXT," +
                    ChatStorageContract.ChatEntry.COLUMN_NAME_MESSAGE + " TEXT," +
                    ChatStorageContract.ChatEntry.COLUMN_NAME_TOPIC + " TEXT," +
                    ChatStorageContract.ChatEntry.COLUMN_NAME_USER + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ChatStorageContract.ChatEntry.TABLE_NAME;

    public ChatEntryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addChatEntry(String time, String message, String topic, String user) {
        SQLiteDatabase chatEntryDb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChatStorageContract.ChatEntry.COLUMN_NAME_TIME, time);
        values.put(ChatStorageContract.ChatEntry.COLUMN_NAME_MESSAGE, message);
        values.put(ChatStorageContract.ChatEntry.COLUMN_NAME_TOPIC, topic);
        values.put(ChatStorageContract.ChatEntry.COLUMN_NAME_USER, user);
        chatEntryDb.insert(ChatStorageContract.ChatEntry.TABLE_NAME, null, values);
        chatEntryDb.close();
    }

    public List<MessageItem> getChatEntry(String topic) {
        List<MessageItem> messageItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + ChatStorageContract.ChatEntry.TABLE_NAME +
                " WHERE " + ChatStorageContract.ChatEntry.COLUMN_NAME_TOPIC + " = '" + topic + "'";
        SQLiteDatabase chatEntryDb = this.getReadableDatabase();
        Cursor cursor = chatEntryDb.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MessageItem messageItem = new MessageItem();
                messageItem.setReceivedTime(cursor.getString(cursor.getColumnIndex(
                        ChatStorageContract.ChatEntry.COLUMN_NAME_TIME)));
                messageItem.setMessage(cursor.getString(cursor.getColumnIndex(
                        ChatStorageContract.ChatEntry.COLUMN_NAME_MESSAGE)));
                messageItems.add(messageItem);
            } while (cursor.moveToNext());
        }
        return messageItems;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}