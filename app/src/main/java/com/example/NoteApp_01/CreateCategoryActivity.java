package com.example.NoteApp_01;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class CreateCategoryActivity extends AppCompatActivity {

    private EditText categoryNameEditText;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        categoryNameEditText = findViewById(R.id.categoryNameEditText);

        // Initialize database with fallback to destructive migration
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "notes_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    public void saveCategory(View view) {
        String categoryName = categoryNameEditText.getText().toString().trim();

        if (!categoryName.isEmpty()) {
            Category category = new Category(categoryName);
            db.categoryDao().insert(category);
            finish();
        } else {
            // Handle empty category name if needed
        }
    }

    public void goBack(View view) {
        finish();
    }
}
