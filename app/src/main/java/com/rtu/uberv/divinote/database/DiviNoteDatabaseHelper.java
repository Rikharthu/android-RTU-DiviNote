package com.rtu.uberv.divinote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DiviNoteDatabaseHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = DiviNoteDatabaseHelper.class.getSimpleName();

    public static final String CREATE_NOTES_TABLE = "CREATE TABLE " +
            DiviNoteContract.NoteTable.TABLE_NAME + " (" +
            DiviNoteContract.NoteTable._ID + " INTEGER PRIMARY KEY," +             // define a primary key
            DiviNoteContract.NoteTable.COLUMN_NAME_CREATED_AT + " INTEGER," +
            DiviNoteContract.NoteTable.COLUMN_NAME_UPDATED_AT + " INTEGER," +
            DiviNoteContract.NoteTable.COLUMN_NAME_REMIND_AT + " INTEGER," +
            DiviNoteContract.NoteTable.COLUMN_NAME_COMPLETED + " INTEGER," +
            DiviNoteContract.NoteTable.COLUMN_NAME_TITLE + " TEXT," +
            DiviNoteContract.NoteTable.COLUMN_NAME_CONTENT + " TEXT" + " )";
    public static final String DROP_NOTES_TABLE="DROP TABLE IF EXISTS "+ DiviNoteContract.NoteTable.TABLE_NAME;

    public DiviNoteDatabaseHelper(Context context) {
        super(context, DiviNoteContract.DATABASE_NAME, null, DiviNoteContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO REIMPLEMENT FOR RELEASE VERSION!!!
        // drop and recreate the table. not a good approach
        sqLiteDatabase.execSQL(DROP_NOTES_TABLE);
        onCreate(sqLiteDatabase);
    }


}
