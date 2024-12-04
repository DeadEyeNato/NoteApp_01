package com.example.NoteApp_01;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "notes",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("categoryId")} // Added index on categoryId
)
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String content;
    public long lastModified;
    public int categoryId; // Foreign key to Category

    public Note(String title, String content, long lastModified, int categoryId) {
        this.title = title;
        this.content = content;
        this.lastModified = lastModified;
        this.categoryId = categoryId;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
