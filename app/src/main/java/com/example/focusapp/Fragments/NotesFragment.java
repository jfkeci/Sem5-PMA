package com.example.focusapp.Fragments;

import android.content.ClipData;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class NotesFragment extends Fragment {

    private ImageButton buttonAddNote, buttonUpdateNote;
    private EditText etNoteTitle, etNoteContent;
    private  TextView twBanner, twSubbanner;
    private RecyclerView recyclerView;

    public Notes updatingNote = new Notes();
    public Notes deletedNote = new Notes();

    public boolean deleteyn=true;
    public boolean updateyn=true;

    public int update_position=0;

    MyDbHelper dbHelper;

    public String uid;

    NotesRecyclerAdapter notesAdapter;

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        dbHelper = new MyDbHelper(getActivity().getBaseContext());

        buttonAddNote = (ImageButton) v.findViewById(R.id.addNoteButton);
        buttonUpdateNote = (ImageButton) v.findViewById(R.id.updateNoteButton);
        etNoteTitle = v.findViewById(R.id.etNoteTitle);
        etNoteContent = v.findViewById(R.id.etNoteContent);
        twBanner = v.findViewById(R.id.banner);
        twSubbanner = v.findViewById(R.id.bannerSlogan);

        User user = dbHelper.getUser();
        uid = user.getUser_id();

        recyclerView = v.findViewById(R.id.recyclerViewNotes);

        Typeface MLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");
        Typeface MMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");
        Typeface MRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");

        etNoteTitle.setTypeface(MMedium);
        etNoteContent.setTypeface(MLight);
        twBanner.setTypeface(MMedium);
        twSubbanner.setTypeface(MLight);

        InitButtonAddNewNote();
        InitButtonUpdateNote();

        InitRecycleView();

        return v;
    }

    public void InitButtonAddNewNote(){
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = Calendar.getInstance().getTime();
                String noteDateTime = sdf.format(date);

                String title="";
                String content="";

                title = etNoteTitle.getText().toString();
                content = etNoteContent.getText().toString();

                if(title.isEmpty() && content.isEmpty()){

                }else{
                    boolean isInserted = dbHelper.addNewNote(uid, title, content, noteDateTime);
                    if(isInserted){
                        Toast.makeText(getActivity(), "Note added!", Toast.LENGTH_LONG).show();
                        etNoteTitle.setText("");
                        etNoteContent.setText("");
                        InitRecycleView();
                    }else{
                        Toast.makeText(getActivity(), "data NOT inserted! problem", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }
    public void InitButtonUpdateNote(){
        buttonUpdateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = Calendar.getInstance().getTime();
                String noteDateTime = sdf.format(date);

                updatingNote.setNOTE_TITLE(etNoteTitle.getText().toString());
                updatingNote.setNOTE_CONTENT(etNoteContent.getText().toString());
                updatingNote.setNOTE_DATE_TIME(noteDateTime);

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
        ArrayList<Notes> myNotes = new ArrayList<>();

        myNotes.clear();

        myNotes = allNotesList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        notesAdapter = new NotesRecyclerAdapter(getActivity().getBaseContext(), myNotes);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();

                ArrayList<Notes> recycleNotesList = allNotesList();

                if (direction == ItemTouchHelper.DOWN) {
                    deletedNote = recycleNotesList.get(position);
                    recycleNotesList.remove(position);
                    notesAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, deletedNote.getNOTE_TITLE(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recycleNotesList.add(position, deletedNote);
                            notesAdapter.notifyItemInserted(position);
                            deleteyn = false;
                        }
                    }).show();

                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    if(deleteyn){
                                        dbHelper.deleteNote(String.valueOf(deletedNote.getNOTE_ID()));
                                        makeMyLog("note is deleted ", " -->this one: "+deletedNote.getNOTE_ID());
                                        deletedNote = null;
                                        deleteyn = true;
                                    }else{
                                        makeMyLog("note is NOT deleted ", " -->this one: "+deletedNote.getNOTE_ID());
                                        deletedNote=null;
                                        deleteyn = true;
                                    }
                                }
                            }, 4000
                    );
                }
                if (direction == ItemTouchHelper.UP) {
                    recycleNotesList.remove(position);
                    notesAdapter.notifyItemRemoved(position);

                    buttonAddNote.setVisibility(View.INVISIBLE);
                    buttonUpdateNote.setVisibility(View.VISIBLE);

                    etNoteTitle.setText(recycleNotesList.get(position).getNOTE_TITLE());
                    etNoteContent.setText(recycleNotesList.get(position).getNOTE_CONTENT());
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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

            myNotes.add(0, note);
        }

        return myNotes;
    }

    private void makeMyToast(String thing, String message, int yn){
        if(yn == 1){
            Toast.makeText(getActivity().getBaseContext(), thing + " - Has been done, Message: "+message, Toast.LENGTH_SHORT).show();
        }if(yn == 0){
            Toast.makeText(getActivity().getBaseContext(), thing + " - Has failed, Message: "+message, Toast.LENGTH_SHORT).show();
        }
    }
    public void makeMyLog(String message, String thing){
        Log.d("Logged item", ""+message+" :"+thing);
    }



}