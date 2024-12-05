package com.example.NoteApp_01;

import android.app.AlertDialog;
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

public class ViewNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private RichEditor contentRichEditor;
    private Spinner categorySpinner;
    private AppDatabase db;
    private int noteId;
    private Note note;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

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

        noteId = getIntent().getIntExtra("noteId", -1);

        if (noteId != -1) {
            note = db.noteDao().getNoteById(noteId);
            if (note != null) {
                titleEditText.setText(note.title);
                // Set the spinner to the note's category
                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i).id == note.categoryId) {
                        categorySpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        contentRichEditor.setEditorHeight(200);
        contentRichEditor.setEditorFontSize(16);
        contentRichEditor.setPadding(10, 10, 10, 10);
        contentRichEditor.setPlaceholder("Edit your note here...");

        // Set up formatting buttons
        ImageButton boldButton = findViewById(R.id.action_bold);
        boldButton.setOnClickListener(v -> contentRichEditor.setBold());

        ImageButton italicButton = findViewById(R.id.action_italic);
        italicButton.setOnClickListener(v -> contentRichEditor.setItalic());

        ImageButton underlineButton = findViewById(R.id.action_underline);
        underlineButton.setOnClickListener(v -> contentRichEditor.setUnderline());

        ImageButton bulletListButton = findViewById(R.id.action_bulleted_list);
        bulletListButton.setOnClickListener(v -> contentRichEditor.setBullets());

        // Checkbox List Button
        ImageButton checkboxListButton = findViewById(R.id.action_checkbox_list);
        checkboxListButton.setOnClickListener(v -> insertCheckbox());

        // Wait for the editor to initialize before setting HTML content
        contentRichEditor.setOnInitialLoadListener(isReady -> {
            if (isReady && note != null) {
                contentRichEditor.setHtml(note.content);
            }
        });
    }

    public void saveNote(View view) {
        String title = titleEditText.getText().toString().trim();
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();

        if (note != null && selectedCategory != null) {
            String content = contentRichEditor.getHtml();
            note.title = title;
            note.content = content;
            note.categoryId = selectedCategory.id;
            note.lastModified = System.currentTimeMillis();
            db.noteDao().update(note);
            finish();
        } else {
            // Handle error case
            finish();
        }
    }

    public void goBack(View view) {
        finish();
    }

    public void deleteNote(View view) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Delete the note
                    if (note != null) {
                        db.noteDao().delete(note);
                    }
                    finish();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void insertCheckbox() {
        // Insert a checkbox at the cursor position without replacing existing content
        contentRichEditor.focusEditor();
        contentRichEditor.insertTodo();
    }
}
