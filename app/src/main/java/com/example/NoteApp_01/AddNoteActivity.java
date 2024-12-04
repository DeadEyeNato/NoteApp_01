package com.example.NoteApp_01;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

public class AddNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private RichEditor contentRichEditor;
    private Spinner categorySpinner;
    private AppDatabase db;
    private List<Category> categoryList;
    private int preselectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleEditText = findViewById(R.id.titleEditText);
        contentRichEditor = findViewById(R.id.contentRichEditor);
        categorySpinner = findViewById(R.id.categorySpinner);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "notes_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        // Load categories into spinner
        categoryList = db.categoryDao().getAllCategories();
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Get preselected categoryId if provided
        preselectedCategoryId = getIntent().getIntExtra("categoryId", -1);
        if (preselectedCategoryId != -1) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).id == preselectedCategoryId) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }

        contentRichEditor.setEditorHeight(200);
        contentRichEditor.setEditorFontSize(16);
        contentRichEditor.setPadding(10, 10, 10, 10);
        contentRichEditor.setPlaceholder("Write your note here...");

        // Set up formatting buttons (same as before)
        ImageButton boldButton = findViewById(R.id.action_bold);
        boldButton.setOnClickListener(v -> contentRichEditor.setBold());

        ImageButton italicButton = findViewById(R.id.action_italic);
        italicButton.setOnClickListener(v -> contentRichEditor.setItalic());

        ImageButton underlineButton = findViewById(R.id.action_underline);
        underlineButton.setOnClickListener(v -> contentRichEditor.setUnderline());

        ImageButton bulletListButton = findViewById(R.id.action_bulleted_list);
        bulletListButton.setOnClickListener(v -> contentRichEditor.setBullets());
    }

    public void saveNote(View view) {
        String title = titleEditText.getText().toString().trim();
        String content = contentRichEditor.getHtml();
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();

        if (selectedCategory != null && (!title.isEmpty() || content != null)) {
            Note note = new Note(title, content, System.currentTimeMillis(), selectedCategory.id);
            db.noteDao().insert(note);
            finish();
        } else {
            // Handle case where no category is selected or note is empty
        }
    }

    public void goBack(View view) {
        finish();
    }
}
