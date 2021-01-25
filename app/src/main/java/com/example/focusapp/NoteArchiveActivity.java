package com.example.focusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.focusapp.Database.MyDbHelper;

public class NoteArchiveActivity extends AppCompatActivity {

    MyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_archive);

        dbHelper = new MyDbHelper(this);
    }
}