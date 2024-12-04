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

    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private AppDatabase db;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Removed getSupportActionBar().hide();

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "notes_db").allowMainThreadQueries().build();

        // Initialize RecyclerView
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load notes from database
        noteList = db.noteDao().getAllNotes();
        notesAdapter = new NotesAdapter(noteList);
        notesRecyclerView.setAdapter(notesAdapter);
    }

    public void goToAddNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteList.clear();
        noteList.addAll(db.noteDao().getAllNotes());
        notesAdapter.notifyDataSetChanged();
    }
}
