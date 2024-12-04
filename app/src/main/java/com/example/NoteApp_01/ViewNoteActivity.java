package com.example.NoteApp_01;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import jp.wasabeef.richeditor.RichEditor;

public class ViewNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private RichEditor contentRichEditor;
    private AppDatabase db;
    private int noteId;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        contentRichEditor = findViewById(R.id.contentRichEditor);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "notes_db").allowMainThreadQueries().build();

        // Get the note ID from the intent
        noteId = getIntent().getIntExtra("noteId", -1);

        if (noteId != -1) {
            note = db.noteDao().getNoteById(noteId);
            if (note != null) {
                titleEditText.setText(note.title);
                contentRichEditor.setHtml(note.content);
            }
        }

        // Set up RichEditor
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
    }

    public void saveNote(View view) {
        String title = titleEditText.getText().toString().trim();
        String content = contentRichEditor.getHtml();

        if (note != null) {
            note.title = title;
            note.content = content;
            note.lastModified = System.currentTimeMillis();
            db.noteDao().update(note);
            finish();
        } else {
            // Handle error case
            finish();
        }
    }

    public void goBack(View view) {
        // Simply finish the activity without saving changes
        finish();
    }
}
