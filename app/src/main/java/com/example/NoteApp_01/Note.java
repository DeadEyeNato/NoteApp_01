package com.example.NoteApp_01;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String content;
    public long lastModified;

    public Note(String title, String content, long lastModified) {
        this.title = title;
        this.content = content;
        this.lastModified = lastModified;
    }
}
