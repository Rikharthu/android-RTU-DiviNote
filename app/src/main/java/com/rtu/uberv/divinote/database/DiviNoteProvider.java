package com.rtu.uberv.divinote.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class DiviNoteProvider extends ContentProvider {

    // integer identifiers for URIs
    private static final int NOTE = 100;
    private static final int NOTE_ID = 101;

    private static final UriMatcher sUriMatcher;

    static {
        String content = DiviNoteContract.CONTENT_AUTHORITY;
        // match URIs to return codes
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(content, DiviNoteContract.PATH_NOTE, NOTE);
        sUriMatcher.addURI(content, DiviNoteContract.PATH_NOTE + "/#", NOTE_ID);
    }

    private DiviNoteDatabaseHelper mDBHelper;


    @Override
    public boolean onCreate() {
        mDBHelper = new DiviNoteDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case NOTE:
                retCursor = db.query(
                        DiviNoteContract.NoteTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NOTE_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        DiviNoteContract.NoteTable.TABLE_NAME,
                        projection,
                        DiviNoteContract.NoteTable._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Uknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTE:
                return DiviNoteContract.NoteTable.CONTENT_TYPE;
            case NOTE_ID:
                return DiviNoteContract.NoteTable.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case NOTE:
                _id = db.insert(DiviNoteContract.NoteTable.TABLE_NAME, null, values);
                if (_id > 0) {
                    // insert successfull
                    returnUri = DiviNoteContract.NoteTable.buildNoteUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Uknown uri: " + uri);
        }
        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rows; // number of rows affected

        switch (sUriMatcher.match(uri)) {
            case NOTE:
                rows = db.delete(DiviNoteContract.NoteTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Uknown uri: " + uri);
        }

        // Because null could delete all rows:
        if (selection == null || rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rows; // number of rows affected

        switch (sUriMatcher.match(uri)) {
            case NOTE:
                rows = db.update(DiviNoteContract.NoteTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Uknown uri: " + uri);
        }

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }
}
