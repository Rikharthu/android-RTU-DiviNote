package com.rtu.uberv.divinote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rtu.uberv.divinote.models.Note;

import java.util.ArrayList;
import java.util.List;


public class DiviNoteDatabaseHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = DiviNoteDatabaseHelper.class.getSimpleName();

    public static final String CREATE_NOTES_TABLE = "CREATE TABLE " +
            DiviNoteDatabaseContract.NoteTable.TABLE_NAME + " (" +
            DiviNoteDatabaseContract.NoteTable._ID + " INTEGER PRIMARY KEY," +             // define a primary key
            DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_CREATED_AT + " INTEGER," +
            DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_UPDATED_AT + " INTEGER," +
            DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_REMIND_AT + " INTEGER," +
            DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_COMPLETED + " INTEGER," +
            DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_TITLE + " TEXT," +
            DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_CONTENT + " TEXT" + " )";
    public static final String DROP_NOTES_TABLE="DROP TABLE IF EXISTS "+ DiviNoteDatabaseContract.NoteTable.TABLE_NAME;

    public DiviNoteDatabaseHelper(Context context) {
        super(context, DiviNoteDatabaseContract.DATABASE_NAME, null, DiviNoteDatabaseContract.DATABASE_VERSION);
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
