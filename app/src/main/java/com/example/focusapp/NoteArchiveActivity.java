package com.example.focusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.focusapp.Adapters.ArchiveNotesRecyclerAdapter;
import com.example.focusapp.Adapters.MyRecyclerAdapter;
import com.example.focusapp.Adapters.NotesRecyclerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Notes;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NoteArchiveActivity extends AppCompatActivity {

    MyDbHelper dbHelper;

    ImageButton buttonBack;
    RecyclerView recyclerViewArchive;

    ArchiveNotesRecyclerAdapter notesArchiveAdapter;

    ArrayList<Notes> archivedNotesList;

    Notes deletedNote = new Notes();
    Notes unarchivedNote = new Notes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_archive);

        dbHelper = new MyDbHelper(this);

        buttonBack = findViewById(R.id.archiveBack);
        recyclerViewArchive = findViewById(R.id.recycleViewArchive);

        archivedNotesList = allArchivedNotesList();

        notesArchiveAdapter = new ArchiveNotesRecyclerAdapter(this, archivedNotesList);
        recyclerViewArchive.setAdapter(notesArchiveAdapter);
        RecyclerView.LayoutManager checkedLayoutManager = new LinearLayoutManager(this);
        recyclerViewArchive.setLayoutManager(checkedLayoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if(direction == ItemTouchHelper.LEFT){
                    deletedNote = archivedNotesList.get(position);

                    String note_id = String.valueOf(deletedNote.getNOTE_ID());

                    int deleted = dbHelper.deleteNote(note_id);

                    if(deleted == 1){
                        archivedNotesList.remove(position);
                        notesArchiveAdapter.notifyItemRemoved(position);
                    }else{
                        makeMyToast("Something went wrong!");
                    }

                    Snackbar.make(recyclerViewArchive, "Deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean undone = dbHelper.addNewNoteWithId(deletedNote.getNOTE_ID(), deletedNote.getUSER_ID(), deletedNote.getNOTE_TITLE(), deletedNote.getNOTE_CONTENT(), deletedNote.getNOTE_DATE_TIME());
                            if(undone){
                                archivedNotesList.add(position, deletedNote);
                                notesArchiveAdapter.notifyItemInserted(position);
                            }else{
                                makeMyToast("Something went wrong!");
                            }
                        }
                    }).show();
                }
                if(direction == ItemTouchHelper.RIGHT){
                    unarchivedNote = archivedNotesList.get(position);

                    int noteId = unarchivedNote.getNOTE_ID();

                    boolean archived = dbHelper.setNoteArchived(noteId, 1);

                    if(archived){
                        archivedNotesList.remove(unarchivedNote);
                        notesArchiveAdapter.notifyItemRemoved(position);
                    }else{
                        makeMyToast("Something went wrong!");
                    }

                    Snackbar.make(recyclerViewArchive, "Removed from archive", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean unarchived = dbHelper.setNoteArchived(noteId, 0);
                            if(unarchived){
                                archivedNotesList.add(position, unarchivedNote);
                                notesArchiveAdapter.notifyItemInserted(position);
                            }else{
                                makeMyToast("Something went wrong!");
                            }

                        }
                    }).show();
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.MyPinkColor))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_sweep_black_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.MyGoodGreenColor))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_unarchive_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewArchive);

        notesArchiveAdapter.setOnLongItemClickListener(new ArchiveNotesRecyclerAdapter.OnLongNoteClickedListener() {
            @Override
            public void onNoteClicked(int position) {
                notesArchiveAdapter.notifyDataSetChanged();
            }
        });


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public ArrayList<Notes> allArchivedNotesList(){
        ArrayList<Notes> dbNotes = new ArrayList<>();

        dbNotes.clear();

        Cursor res = dbHelper.getAllArchivedNotes();

        if(res.getCount() == 0){
            makeMyToast("Error, No notes found");
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));

            Notes note = new Notes(id, res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4));

            dbNotes.add(0, note);
        }

        return dbNotes;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void makeMyToast(String message){
        Toast.makeText(this, "Message:  "+message, Toast.LENGTH_SHORT).show();
    }
}