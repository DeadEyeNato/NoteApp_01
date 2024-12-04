package com.example.NoteApp_01;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(
        entities = {Note.class, Category.class},
        version = 2, // Updated version number
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    public abstract CategoryDao categoryDao();
}
