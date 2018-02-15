package com.superchatroom.superchatroom.database;

import android.provider.BaseColumns;

/**
 * Contract class that specifies the layout of schema.
 */
public class ChatStorageContract {
    private ChatStorageContract() {}

    public static class ChatEntry implements BaseColumns {
        public static final String TABLE_NAME = "chat";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_TOPIC = "topic";
        public static final String COLUMN_NAME_USER = "user";
    }
}