package com.rtu.uberv.divinote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rtu.uberv.divinote.models.Note;

import java.util.ArrayList;
import java.util.List;


public class NotesDatabaseHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = NotesDatabaseHelper.class.getSimpleName();

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    // Singleton pattern
    private static NotesDatabaseHelper sInstance;

    public static synchronized NotesDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new NotesDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private NotesDatabaseHelper(Context context) {
        super(context, NotesDatabaseContract.DATABASE_NAME, null, NotesDatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(NotesDatabaseContract.NoteTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO REIMPLEMENT FOR RELEASE VERSION!!!
        // recreate the table
        sqLiteDatabase.execSQL(NotesDatabaseContract.NoteTable.DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void addNote(Note note) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        // create a transaction
        db.beginTransaction();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(NotesDatabaseContract.NoteTable.COLUMN_NAME_CREATED_AT, note.getCreatedAt());
        values.put(NotesDatabaseContract.NoteTable.COLUMN_NAME_UPDATED_AT, note.getUpdatedAt());
        values.put(NotesDatabaseContract.NoteTable.COLUMN_NAME_REMIND_AT, note.getRemindAt());
        values.put(NotesDatabaseContract.NoteTable.COLUMN_NAME_COMPLETED, note.isCompleted() ? 1 : 0);
        values.put(NotesDatabaseContract.NoteTable.COLUMN_NAME_TITLE, note.getTitle());
        values.put(NotesDatabaseContract.NoteTable.COLUMN_NAME_CONTENT, note.getContent());
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(NotesDatabaseContract.NoteTable.TABLE_NAME, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public List<Note> getAllNotes() {
        // query the database
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                NotesDatabaseContract.NoteTable.TABLE_NAME,
                null, // columns (null=all)
                null, // selection (null=all)
                null, // selection arguments (replace ?s)
                null, // groupBy
                null, // having
                null);// orderby

        // parse database response
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            notes.add(getNoteFromCursor(cursor));
        }

        // close cursor and db
        if (cursor != null)
            cursor.close();
        db.close();
        return notes;
    }

    private Note getNoteFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            // null or 0 rows
            return null;
        } else {
            try {
                // get values from the cursor at specified column names
                Note note = new Note();
                note.setTitle(cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                NotesDatabaseContract.NoteTable.COLUMN_NAME_TITLE)))
                        .setContent(cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        NotesDatabaseContract.NoteTable.COLUMN_NAME_CONTENT)))
                        .setCreatedAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        NotesDatabaseContract.NoteTable.COLUMN_NAME_CREATED_AT)))
                        .setUpdatedAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        NotesDatabaseContract.NoteTable.COLUMN_NAME_UPDATED_AT)))
                        .setRemindAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        NotesDatabaseContract.NoteTable.COLUMN_NAME_REMIND_AT)))
                        .setCompleted(cursor.getInt
                                (cursor.getColumnIndexOrThrow(
                                        NotesDatabaseContract.NoteTable.COLUMN_NAME_COMPLETED)) == TRUE)
                        .setId(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        NotesDatabaseContract.NoteTable._ID)));
                return note;
            } catch (Exception e) {
                return null;
            }
        }
    }

    public Note getNote(long id) {
        // prepare query
        String where = NotesDatabaseContract.NoteTable._ID + "= ?";
        String[] whereArgs = {Long.toString(id)};

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(NotesDatabaseContract.NoteTable.TABLE_NAME,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Note task = getNoteFromCursor(cursor);

        if (cursor != null)
            cursor.close();
        db.close();
        return task;
    }
}
