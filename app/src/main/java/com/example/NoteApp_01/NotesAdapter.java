package com.example.NoteApp_01;

import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Date;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> noteList;

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView dateTextView;

        public NoteViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.noteTitle);
            dateTextView = view.findViewById(R.id.noteDate);
        }
    }

    public NotesAdapter(List<Note> notes) {
        noteList = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View noteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(noteView);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.titleTextView.setText(note.title);
        String date = DateFormat.format("MMM dd, yyyy hh:mm a", new Date(note.lastModified)).toString();
        holder.dateTextView.setText(date);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewNoteActivity.class);
            intent.putExtra("noteId", note.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
