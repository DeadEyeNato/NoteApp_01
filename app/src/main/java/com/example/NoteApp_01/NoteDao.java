package com.example.NoteApp_01;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY lastModified DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE id = :noteId")
    Note getNoteById(int noteId);

    @Query("SELECT * FROM notes WHERE categoryId = :categoryId ORDER BY lastModified DESC")
    List<Note> getNotesByCategory(int categoryId);

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);
}
