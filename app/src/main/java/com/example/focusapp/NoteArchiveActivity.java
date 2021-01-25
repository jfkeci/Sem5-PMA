package com.example.focusapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.focusapp.Adapters.ArchiveNotesRecyclerAdapter;
import com.example.focusapp.Adapters.MyRecyclerAdapter;
import com.example.focusapp.Adapters.NotesRecyclerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Notes;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NoteArchiveActivity extends AppCompatActivity {

    MyDbHelper dbHelper;

    ImageButton buttonBack;
    RecyclerView recyclerViewArchive;

    ArchiveNotesRecyclerAdapter notesArchiveAdapter;

    ArrayList<Notes> archivedNotesList;

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