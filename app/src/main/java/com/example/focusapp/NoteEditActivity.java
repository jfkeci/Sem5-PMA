package com.example.focusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NoteEditActivity extends AppCompatActivity {

    Button buttonSaveEdit;
    EditText etTitle, etContent;

    MyDbHelper dbHelper;

    Intent intent;

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        buttonSaveEdit = findViewById(R.id.buttonEditSave);
        etContent = findViewById(R.id.etNoteContent);
        etTitle = findViewById(R.id.etNoteTitle);

        dbHelper = new MyDbHelper(this);

        intent = getIntent();
        String string = intent.getStringExtra("note_id");
        int noteID = Integer.parseInt(string);

        ArrayList<Notes> dbNotes = new ArrayList<>();

        dbNotes.clear();

        Cursor res = dbHelper.getNoteById(noteID);

        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));

            Notes note = new Notes(id, res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4));

            dbNotes.add(0, note);
        }



        etTitle.setText(dbNotes.get(0).getNOTE_TITLE());
        etContent.setText(dbNotes.get(0).getNOTE_CONTENT());


        buttonSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = Calendar.getInstance().getTime();
                String noteDateTime = sdf.format(date);
                String noteContent = etContent.getText().toString();
                String noteTitle = etTitle.getText().toString();
                dbHelper.updateNote(noteID, noteTitle, noteContent, noteDateTime);
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}