package com.example.NoteApp_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView categoriesRecyclerView;
    private CategoriesAdapter categoriesAdapter;
    private AppDatabase db;
    private List<CategoryWithNotes> categoryWithNotesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database with fallback to destructive migration
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "notes_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        // Initialize RecyclerView
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load categories and notes from database
        categoryWithNotesList = db.categoryDao().getCategoriesWithNotes();
        categoriesAdapter = new CategoriesAdapter(categoryWithNotesList, db);
        categoriesRecyclerView.setAdapter(categoriesAdapter);
    }

    public void goToCreateCategory(View view) {
        Intent intent = new Intent(this, CreateCategoryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoryWithNotesList = db.categoryDao().getCategoriesWithNotes();
        categoriesAdapter.updateData(categoryWithNotesList);
    }
}
