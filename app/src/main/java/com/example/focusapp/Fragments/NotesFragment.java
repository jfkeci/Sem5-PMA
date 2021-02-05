package com.example.focusapp.Fragments;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focusapp.Adapters.NotesRecyclerAdapter;
import com.example.focusapp.AppLockListActivity;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Notes;
import com.example.focusapp.Models.User;
import com.example.focusapp.NoteArchiveActivity;
import com.example.focusapp.NoteEditActivity;
import com.example.focusapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class NotesFragment extends Fragment {

    private Button buttonAddNote, openArchiveButton;
    private EditText etNoteTitle, etNoteContent;
    private  TextView twBanner, twSubbanner;
    private RecyclerView recyclerView;

    public Notes updatingNote = new Notes();
    public Notes deletedNote = new Notes();


    public  ArrayList<Notes> myNotes = new ArrayList<>();

    MyDbHelper dbHelper;

    public String uid;

    NotesRecyclerAdapter notesAdapter;

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        dbHelper = new MyDbHelper(getActivity().getBaseContext());

        buttonAddNote = (Button) v.findViewById(R.id.addNoteButton);
        openArchiveButton = (Button) v.findViewById(R.id.buttonOpenArchive);
        etNoteTitle = v.findViewById(R.id.etNoteTitle);
        etNoteContent = v.findViewById(R.id.etNoteContent);
        twBanner = v.findViewById(R.id.banner);
        twSubbanner = v.findViewById(R.id.bannerSlogan);

        User user = dbHelper.getUser();
        uid = user.getUser_id();

        recyclerView = v.findViewById(R.id.recyclerViewNotes);

        Typeface MLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");
        Typeface MMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");

        etNoteTitle.setTypeface(MMedium);
        etNoteContent.setTypeface(MLight);
        twBanner.setTypeface(MMedium);
        twSubbanner.setTypeface(MLight);

        InitButtonAddNewNote();

        myNotes=allNotesList();

        notesAdapter = new NotesRecyclerAdapter(getActivity(), myNotes);
        recyclerView.setAdapter(notesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        notesAdapter.notifyDataSetChanged();

        notesAdapter.setOnLongItemClickListener(new NotesRecyclerAdapter.OnLongNoteClickedListener() {
            @Override
            public void onNoteClicked(int position) {
                notesAdapter.notifyDataSetChanged();
            }
        });

        if(myNotes.isEmpty()){
            makeMyToast("No notes");
        }

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getLayoutPosition();

                if (direction == ItemTouchHelper.DOWN) {
                    deletedNote = myNotes.get(position);

                    String note_id = String.valueOf(deletedNote.getNOTE_ID());

                    int deleted = dbHelper.deleteNote(note_id);

                    if(deleted == 1){
                        myNotes.remove(position);
                        notesAdapter.notifyItemRemoved(position);
                    }
                    Snackbar.make(recyclerView, "Deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean undone = dbHelper.addNewNoteWithId(deletedNote.getNOTE_ID(), deletedNote.getUSER_ID(), deletedNote.getNOTE_TITLE(), deletedNote.getNOTE_CONTENT(), deletedNote.getNOTE_DATE_TIME());
                            if(undone){
                                myNotes.add(position, deletedNote);
                                notesAdapter.notifyItemInserted(position);
                            }else{
                                makeMyToast("Something went wrong!");
                            }
                        }
                    }).show();
                }
                if (direction == ItemTouchHelper.UP) {
                    updatingNote = myNotes.get(position);

                    myNotes.remove(position);
                    notesAdapter.notifyItemRemoved(position);

                    dbHelper.setNoteArchived(updatingNote.getNOTE_ID(), 1);

                    Snackbar.make(recyclerView, "Archived", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbHelper.setNoteArchived(updatingNote.getNOTE_ID(), 0);
                            myNotes.add(position, updatingNote);
                            notesAdapter.notifyItemInserted(position);
                        }
                    }).show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        openArchiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoteArchiveActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        myNotes = allNotesList();
        notesAdapter.setData(myNotes);
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
                        makeMyToast("Note added");
                        etNoteTitle.setText("");
                        etNoteContent.setText("");
                        getData();
                        closeKeyboard();
                    }else{
                        makeMyToast("Try again!");
                    }
                }
            }
        });
    }

    public ArrayList<Notes> allNotesList(){
        ArrayList<Notes> dbNotes = new ArrayList<>();

        dbNotes.clear();

        Cursor res = dbHelper.getAllUnarchivedNotes();

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

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void makeMyToast(String message){
        Toast.makeText(getActivity().getBaseContext(), "Message:  "+message, Toast.LENGTH_SHORT).show();
    }
    public void makeMyLog(String message, String thing){
        Log.d("Logged item", ""+message+" :"+thing);
    }
}