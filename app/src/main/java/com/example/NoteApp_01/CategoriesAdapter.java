package com.example.NoteApp_01;

import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CATEGORY = 0;
    private static final int VIEW_TYPE_NOTE = 1;

    private List<Item> items;
    private AppDatabase db;
    private Map<Integer, Boolean> expansionStateMap; // Map to track expansion state

    public CategoriesAdapter(List<CategoryWithNotes> categoryWithNotesList, AppDatabase db) {
        this.db = db;
        this.items = new ArrayList<>();
        this.expansionStateMap = new HashMap<>();
        buildItemsList(categoryWithNotesList);
    }

    public void updateData(List<CategoryWithNotes> categoryWithNotesList) {
        this.items.clear();
        buildItemsList(categoryWithNotesList);
        notifyDataSetChanged();
    }

    private void buildItemsList(List<CategoryWithNotes> categoryWithNotesList) {
        for (CategoryWithNotes categoryWithNotes : categoryWithNotesList) {
            int categoryId = categoryWithNotes.category.id;
            items.add(new Item(categoryWithNotes.category));

            boolean isExpanded = expansionStateMap.getOrDefault(categoryId, false);
            if (isExpanded) {
                for (Note note : db.noteDao().getNotesByCategory(categoryId)) {
                    items.add(new Item(note, categoryId));
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).isCategory) {
            return VIEW_TYPE_CATEGORY;
        } else {
            return VIEW_TYPE_NOTE;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CATEGORY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item, parent, false);
            return new CategoryViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_item, parent, false);
            return new NoteViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);

        if (item.isCategory) {
            CategoryViewHolder categoryHolder = (CategoryViewHolder) holder;
            categoryHolder.bind(item.category);
        } else {
            NoteViewHolder noteHolder = (NoteViewHolder) holder;
            noteHolder.bind(item.note);
        }
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView categoryNameTextView;
        public ToggleButton expandToggleButton;
        public Button addNoteButton;

        private Category category;

        public CategoryViewHolder(View view) {
            super(view);
            categoryNameTextView = view.findViewById(R.id.categoryName);
            expandToggleButton = view.findViewById(R.id.expandToggleButton);
            addNoteButton = view.findViewById(R.id.addNoteButton);

            expandToggleButton.setOnClickListener(this);
            addNoteButton.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), AddNoteActivity.class);
                intent.putExtra("categoryId", category.id);
                v.getContext().startActivity(intent);
            });
        }

        public void bind(Category category) {
            this.category = category;
            categoryNameTextView.setText(category.name);

            boolean isExpanded = expansionStateMap.getOrDefault(category.id, false);
            expandToggleButton.setChecked(isExpanded);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            boolean isExpanded = expansionStateMap.getOrDefault(category.id, false);
            isExpanded = !isExpanded;
            expansionStateMap.put(category.id, isExpanded);
            expandToggleButton.setChecked(isExpanded);

            if (isExpanded) {
                // Expand: Add notes under this category
                List<Note> notes = db.noteDao().getNotesByCategory(category.id);
                int index = position + 1;
                for (Note note : notes) {
                    items.add(index, new Item(note, category.id));
                    index++;
                }
                notifyItemRangeInserted(position + 1, notes.size());
            } else {
                // Collapse: Remove notes under this category
                int index = position + 1;
                int count = 0;
                while (index < items.size() && items.get(index).categoryId == category.id) {
                    items.remove(index);
                    count++;
                }
                notifyItemRangeRemoved(position + 1, count);
            }
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleTextView;
        public TextView dateTextView;
        private Note note;

        public NoteViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.noteTitle);
            dateTextView = view.findViewById(R.id.noteDate);
            view.setOnClickListener(this);
        }

        public void bind(Note note) {
            this.note = note;
            titleTextView.setText(note.title);
            String date = DateFormat.format("MMM dd, yyyy hh:mm a", new Date(note.lastModified)).toString();
            dateTextView.setText(date);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ViewNoteActivity.class);
            intent.putExtra("noteId", note.id);
            v.getContext().startActivity(intent);
        }
    }

    private class Item {
        boolean isCategory;
        Category category;
        Note note;
        int categoryId;

        Item(Category category) {
            this.isCategory = true;
            this.category = category;
        }

        Item(Note note, int categoryId) {
            this.isCategory = false;
            this.note = note;
            this.categoryId = categoryId;
        }
    }
}
