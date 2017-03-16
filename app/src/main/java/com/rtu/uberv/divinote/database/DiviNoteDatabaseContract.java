package com.rtu.uberv.divinote.database;


import android.provider.BaseColumns;

public final class DiviNoteDatabaseContract {

    // Database Info
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notes_database";


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DiviNoteDatabaseContract() {
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

        public static final int TRUE = 1;
        public static final int FALSE = 0;
    }
}
