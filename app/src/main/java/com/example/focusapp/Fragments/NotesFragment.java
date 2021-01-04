package com.example.focusapp.Fragments;

import android.content.ClipData;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focusapp.Adapters.NotesRecyclerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Notes;
import com.example.focusapp.Models.User;
import com.example.focusapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NotesFragment extends Fragment {

    private ImageButton buttonAddNote, buttonUpdateNote;
    private EditText etNoteTitle, etNoteContent;
    private RecyclerView recyclerView;

    public Notes updatingNote = new Notes();

    public int update_position=0;

    MyDbHelper dbHelper;

    NotesRecyclerAdapter notesAdapter;

    public NotesFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        dbHelper = new MyDbHelper(getActivity().getBaseContext());

        buttonAddNote = (ImageButton) v.findViewById(R.id.addNoteButton);
        buttonUpdateNote = (ImageButton) v.findViewById(R.id.updateNoteButton);
        etNoteTitle = v.findViewById(R.id.etNoteTitle);
        etNoteContent = v.findViewById(R.id.etNoteContent);



        recyclerView = v.findViewById(R.id.recyclerViewNotes);

        InitButtonAddNewNote();
        InitButtonUpdateNote();

        InitRecycleView();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);



        return v;
    }

    public void InitButtonAddNewNote(){
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
                Date date = Calendar.getInstance().getTime();
                String noteDateTime = sdf.format(date);

                User user = dbHelper.getUser();
                String uid = user.getUser_id();

                String title="";
                String content="";

                title = etNoteTitle.getText().toString();
                content = etNoteContent.getText().toString();

                boolean isInserted = dbHelper.addNewNote(uid, title, content, noteDateTime);
                if(isInserted){
                    Toast.makeText(getActivity(), "Note added!", Toast.LENGTH_LONG).show();
                    etNoteTitle.setText("");
                    etNoteContent.setText("");
                }else{
                    Toast.makeText(getActivity(), "data NOT inserted! problem", Toast.LENGTH_LONG).show();
                }
                InitRecycleView();
            }
        });
    }
    public void InitButtonUpdateNote(){
        buttonUpdateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
                Date date = Calendar.getInstance().getTime();
                String noteDateTime = sdf.format(date);

                updatingNote.setNOTE_TITLE(etNoteTitle.getText().toString());
                updatingNote.setNOTE_CONTENT(etNoteContent.getText().toString());

                dbHelper.updateNote(updatingNote);

                etNoteContent.setText("");
                etNoteTitle.setText("");

                InitRecycleView();

                buttonUpdateNote.setVisibility(View.INVISIBLE);
                buttonAddNote.setVisibility(View.VISIBLE);
            }
        });
    }

    public void InitRecycleView(){

        ArrayList<Notes> myNotes = allNotesList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity().getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        notesAdapter = new NotesRecyclerAdapter(getActivity().getBaseContext(), myNotes);
        recyclerView.setAdapter(notesAdapter);
    }


    public ArrayList<Notes> allNotesList(){
        ArrayList<Notes> myNotes = new ArrayList<>();

        myNotes.clear();

        Cursor res = dbHelper.getAllNotes();

        if(res.getCount() == 0){
            makeMyToast("Error", "No notes found", 0);
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));

            Notes note = new Notes(id, res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4));

            myNotes.add(note);
        }

        return myNotes;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getLayoutPosition();

            ArrayList<Notes> newNotesList = allNotesList();

            if (direction == ItemTouchHelper.DOWN) {
                newNotesList.remove(position);
                notesAdapter.notifyItemRemoved(position);
            }
            if (direction == ItemTouchHelper.UP) {
                buttonAddNote.setVisibility(View.INVISIBLE);
                buttonUpdateNote.setVisibility(View.VISIBLE);

                updatingNote = newNotesList.get(position);

                etNoteTitle.setText(newNotesList.get(position).getNOTE_TITLE());
                etNoteContent.setText(newNotesList.get(position).getNOTE_CONTENT());

                makeMyToast("updating the note", "updating the note", 1);
            }
        }
    };



    private void makeMyToast(String thing, String message, int yn){
        if(yn == 1){
            Toast.makeText(getActivity().getBaseContext(), thing + " - Has been done, Message: "+message, Toast.LENGTH_SHORT).show();
        }if(yn == 0){
            Toast.makeText(getActivity().getBaseContext(), thing + " - Has failed, Message: "+message, Toast.LENGTH_SHORT).show();
        }
    }


}