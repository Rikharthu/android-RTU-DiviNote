package com.rtu.uberv.divinote.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rtu.uberv.divinote.models.Note;

import java.util.ArrayList;
import java.util.List;

public class DiviNoteDAO implements NoteDao {
    public static final String LOG_TAG = DiviNoteDAO.class.getSimpleName();

    private static final Object lock = new Object();

    // Singleton pattern
    private static DiviNoteDAO sInstance = null;
    private static DiviNoteDatabaseHelper mDatabaseHelper = null;

    private DiviNoteDAO() {
    }

    public static DiviNoteDAO getInstance(Context ctx) {
        if (sInstance == null) {
            synchronized (lock) {
                if (sInstance == null) {
                    sInstance = new DiviNoteDAO();
                    mDatabaseHelper = new DiviNoteDatabaseHelper(ctx.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public static Note extractNoteFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            // null or 0 rows
            return null;
        } else {
            try {
                // get values from the cursor at specified column names
                Note note = new Note();
                note.setTitle(cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DiviNoteContract.NoteTable.COLUMN_NAME_TITLE)))
                        .setContent(cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteContract.NoteTable.COLUMN_NAME_CONTENT)))
                        .setCreatedAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteContract.NoteTable.COLUMN_NAME_CREATED_AT)))
                        .setUpdatedAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteContract.NoteTable.COLUMN_NAME_UPDATED_AT)))
                        .setRemindAt(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteContract.NoteTable.COLUMN_NAME_REMIND_AT)))
                        .setCompleted(cursor.getInt
                                (cursor.getColumnIndexOrThrow(
                                        DiviNoteContract.NoteTable.COLUMN_NAME_COMPLETED)) == DiviNoteContract.NoteTable.TRUE)
                        .setId(cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DiviNoteContract.NoteTable._ID)));
                return note;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Example of using a raw query. Not SQL-Injection safe!
     */
    public synchronized Note getNoteRaw(long id, Context ctx) {
        String query = "SELECT * FROM " + DiviNoteContract.NoteTable.TABLE_NAME +
                " WHERE " + DiviNoteContract.NoteTable._ID + " = '?'";
        String[] whereArgs = {Long.toString(id)};
        Cursor cursor = new DiviNoteDatabaseHelper(ctx).getReadableDatabase().rawQuery(query, whereArgs);
        cursor.moveToFirst();
        return extractNoteFromCursor(cursor);
    }

    private ContentValues noteToContentsValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(DiviNoteContract.NoteTable.COLUMN_NAME_CREATED_AT, note.getCreatedAt());
        values.put(DiviNoteContract.NoteTable.COLUMN_NAME_UPDATED_AT, note.getUpdatedAt());
        values.put(DiviNoteContract.NoteTable.COLUMN_NAME_REMIND_AT, note.getRemindAt());
        values.put(DiviNoteContract.NoteTable.COLUMN_NAME_COMPLETED, note.isCompleted() ? 1 : 0);
        values.put(DiviNoteContract.NoteTable.COLUMN_NAME_TITLE, note.getTitle());
        values.put(DiviNoteContract.NoteTable.COLUMN_NAME_CONTENT, note.getContent());
        return values;
    }

    @Override
    public Note fetchNoteById(long noteId) {
        // prepare query
        String where = DiviNoteContract.NoteTable._ID + "= ?";
        String[] whereArgs = {Long.toString(noteId)};

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DiviNoteContract.NoteTable.TABLE_NAME,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Note task = extractNoteFromCursor(cursor);

        if (cursor != null)
            cursor.close();
        db.close();
        return task;
    }

    @Override
    public List<Note> fetchAllNotes() {
        // query the database
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DiviNoteContract.NoteTable.TABLE_NAME,
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

    @Override
    public long addNote(Note note) {
        // Create and/or open the database for writing
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        // create a transaction
        db.beginTransaction();

        // Create a new map of values, where column names are the keys
        ContentValues values = noteToContentsValues(note);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DiviNoteContract.NoteTable.TABLE_NAME, null, values);

        // approve and end transaction
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return newRowId;
    }

    @Override
    public boolean addNotes(List<Note> notes) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        // create a transaction
        db.beginTransaction();
        for (Note note : notes) {
            ContentValues values = noteToContentsValues(note);
            long newRowId = db.insert(DiviNoteContract.NoteTable.TABLE_NAME, null, values);
            if (newRowId == -1) {
                db.endTransaction();
                db.close();
                return false;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }

    @Override
    public boolean deleteNoteById(long noteId) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        // create a transaction
        db.beginTransaction();
        int rowsAffected = db.delete(
                DiviNoteContract.NoteTable.TABLE_NAME,
                DiviNoteContract.NoteTable._ID + " = ?",
                new String[]{String.valueOf(noteId)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return rowsAffected > 0;
    }

    @Override
    public int deleteAllNotes() {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.delete(DiviNoteContract.NoteTable.TABLE_NAME, "1", null);
    }

    @Override
    public boolean updateNoteById(long noteId, Note note) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        ContentValues noteValues = noteToContentsValues(note);
        int rowsAffected = db.update(
                DiviNoteContract.NoteTable.TABLE_NAME,
                noteValues,
                DiviNoteContract.NoteTable._ID + " = ?",
                new String[]{String.valueOf(noteId)});
        return rowsAffected > 0;
    }
}
