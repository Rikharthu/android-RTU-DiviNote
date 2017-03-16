package com.rtu.uberv.divinote.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rtu.uberv.divinote.models.Note;

import java.util.ArrayList;
import java.util.List;

public class DiviNoteDatabaseDAO {
    public static final String LOG_TAG = DiviNoteDatabaseDAO.class.getSimpleName();

    private static final Object lock = new Object();

    // Singleton pattern
    private static DiviNoteDatabaseDAO sInstance = null;

    private DiviNoteDatabaseDAO() {
    }

    public static synchronized DiviNoteDatabaseDAO getInstance() {
        if (sInstance == null) {
            synchronized (lock) {
                if (sInstance == null) {
                    sInstance = new DiviNoteDatabaseDAO();
                }
            }
        }
        return sInstance;
    }


    // methods should be synchronized ?

    /**
     * Insert a new Note in the database
     *
     * @param note Note to insert in the database
     * @param ctx  Context
     * @return Row ID of the newly inserted note, or -1 if operation failed.
     */
    public synchronized long addNote(Note note, Context ctx) {
        try {
            // Create and/or open the database for writing
            SQLiteDatabase db = new DiviNoteDatabaseHelper(ctx).getWritableDatabase();
            // create a transaction
            db.beginTransaction();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_CREATED_AT, note.getCreatedAt());
            values.put(DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_UPDATED_AT, note.getUpdatedAt());
            values.put(DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_REMIND_AT, note.getRemindAt());
            values.put(DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_COMPLETED, note.isCompleted() ? 1 : 0);
            values.put(DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_TITLE, note.getTitle());
            values.put(DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_CONTENT, note.getContent());
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(DiviNoteDatabaseContract.NoteTable.TABLE_NAME, null, values);

            // approve and end transaction
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return newRowId;
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            return -1;
        }
    }

    public synchronized List<Note> getAllNotes(Context ctx) {
        // query the database
        SQLiteDatabase db = new DiviNoteDatabaseHelper(ctx).getReadableDatabase();
        Cursor cursor = db.query(
                DiviNoteDatabaseContract.NoteTable.TABLE_NAME,
                null, // columns (null=all)
                null, // selection (null=all)
                null, // selection arguments (replace ?s)
                null, // groupBy
                null, // having
                null);// orderby

        // parse database response
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            notes.add(extractNoteFromCursor(cursor));
        }

        // close cursor and db
        if (cursor != null)
            cursor.close();
        db.close();
        return notes;
    }

    private Note extractNoteFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            // null or 0 rows
            return null;
        } else {
            try {
                // get values from the cursor at specified column names
                Note note = new Note();
                note.setTitle(cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_TITLE)))
                        .setContent(cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_CONTENT)))
                        .setCreatedAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_CREATED_AT)))
                        .setUpdatedAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_UPDATED_AT)))
                        .setRemindAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_REMIND_AT)))
                        .setCompleted(cursor.getInt
                                (cursor.getColumnIndexOrThrow(
                                        DiviNoteDatabaseContract.NoteTable.COLUMN_NAME_COMPLETED)) == DiviNoteDatabaseContract.NoteTable.TRUE)
                        .setId(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteDatabaseContract.NoteTable._ID)));
                return note;
            } catch (Exception e) {
                return null;
            }
        }
    }

    public synchronized Note getNote(long id, Context ctx) {
        // prepare query
        String where = DiviNoteDatabaseContract.NoteTable._ID + "= ?";
        String[] whereArgs = {Long.toString(id)};

        SQLiteDatabase db = new DiviNoteDatabaseHelper(ctx).getReadableDatabase();
        Cursor cursor = db.query(DiviNoteDatabaseContract.NoteTable.TABLE_NAME,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Note task = extractNoteFromCursor(cursor);

        if (cursor != null)
            cursor.close();
        db.close();
        return task;
    }

    /**
     * Example of using a raw query. Not SQL-Injection safe!
     */
    public synchronized Note getNoteRaw(long id, Context ctx) {
        String query = "SELECT * FROM " + DiviNoteDatabaseContract.NoteTable.TABLE_NAME +
                " WHERE " + DiviNoteDatabaseContract.NoteTable._ID + " = '?'";
        String[] whereArgs = {Long.toString(id)};
        Cursor cursor = new DiviNoteDatabaseHelper(ctx).getReadableDatabase().rawQuery(query, whereArgs);
        cursor.moveToFirst();
        return extractNoteFromCursor(cursor);
    }

    public synchronized void clearNotesDB(Context ctx){
        SQLiteDatabase db = new DiviNoteDatabaseHelper(ctx).getWritableDatabase();
        db.delete(DiviNoteDatabaseContract.NoteTable.TABLE_NAME,null,null);
    }
}
