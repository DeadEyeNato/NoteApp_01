package com.example.NoteApp_01;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CategoryWithNotes {
    @Embedded
    public Category category;

    @Relation(
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    public List<Note> notes;
}
