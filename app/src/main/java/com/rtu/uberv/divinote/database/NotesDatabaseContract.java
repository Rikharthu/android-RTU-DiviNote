package com.rtu.uberv.divinote.database;


import android.provider.BaseColumns;

public final class NotesDatabaseContract {

    // Database Info
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notes_database";


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private NotesDatabaseContract() {
    }

    /* Inner class that defines the table contents */
    public static class NoteTable implements BaseColumns {
        // Table Name
        public static final String TABLE_NAME = "notes";
        // Notes Table Columns
        // no need to declare ID, since it is implemented in BaseColumns interface
//        private static final String KEY_NOTE_ID = "id";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
        public static final String COLUMN_NAME_REMIND_AT = "remind_at";
        public static final String COLUMN_NAME_COMPLETED = "completed";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";

        // Database Queries:
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" +
                _ID + " INTEGER PRIMARY KEY," +             // define a primary key
                COLUMN_NAME_CREATED_AT + " INTEGER," +
                COLUMN_NAME_UPDATED_AT + " INTEGER," +
                COLUMN_NAME_REMIND_AT + " INTEGER," +
                COLUMN_NAME_COMPLETED + " INTEGER," +
                COLUMN_NAME_TITLE + " TEXT," +
                COLUMN_NAME_CONTENT + " TEXT" +
                " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
