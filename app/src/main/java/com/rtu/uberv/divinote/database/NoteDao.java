package com.rtu.uberv.divinote.database;

import com.rtu.uberv.divinote.models.Note;

import java.util.List;

public interface NoteDao {
    Note fetchNoteById(long noteId);
    List<Note> fetchAllNotes();
    long addNote(Note note);
    boolean addNotes(List<Note> notes);
    boolean deleteNoteById(long noteId);
    int deleteAllNotes();
    boolean updateNoteById(long noteId, Note note);
}
