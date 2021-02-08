package com.example.focusapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Notes;
import com.example.focusapp.NoteEditActivity;
import com.example.focusapp.R;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder> {
    Context context;
    ArrayList<Notes> notesList = new ArrayList<>();

    private OnLongNoteClickedListener mListener;

    MyDbHelper dbHelper;

    public interface OnLongNoteClickedListener{
        void onNoteClicked(int position);
    }

    public void setOnLongItemClickListener(OnLongNoteClickedListener listener){
        mListener = listener;
    }

    public NotesRecyclerAdapter() {}

    public NotesRecyclerAdapter(Context context, ArrayList<Notes> notes) {
        this.context = context;
        this.notesList = notes;
        dbHelper = new MyDbHelper(context);
    }

    public void setData(ArrayList<Notes> notesList)
    {
        this.notesList = notesList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.note_card_layout, parent, false);

        return new NotesViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        if(notesList.size()>=1){

            String content = notesList.get(position).getNOTE_CONTENT();
            String title = notesList.get(position).getNOTE_TITLE();
            String mainContent = "";
            String mainTitle = "";

            if(content.length() > 45){
                for(int i=0;i<45;i++){
                    mainContent = mainContent+content.charAt(i);
                }
                mainContent = mainContent+"...";
            }if(content.length()<45){
                mainContent = content;
            }

            if(title.length() > 14){
                for(int i=0;i<14;i++){
                    mainTitle = mainTitle+title.charAt(i);
                }
                mainTitle = mainTitle+"...";
            }if(title.length()<14){
                mainTitle = title;
            }

            holder.twTitle.setText(mainTitle);
            holder.twContent.setText(mainContent);
            holder.twDate.setText(notesList.get(position).getNOTE_DATE_TIME());
        }

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder{

        private TextView twTitle, twContent, twDate;

        public NotesViewHolder(@NonNull View itemView, final OnLongNoteClickedListener listener) {
            super(itemView);

            twTitle = itemView.findViewById(R.id.twNoteTitle);
            twContent = itemView.findViewById(R.id.twNoteContent);
            twDate = itemView.findViewById(R.id.twNoteDateTime);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onNoteClicked(position);
                            openNoteEditor(position, v);
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void openNoteEditor(int position, View view){
        Intent intent = new Intent(context, NoteEditActivity.class);
        intent.putExtra("note_id", String.valueOf(notesList.get(position).getNOTE_ID()));
        view.getContext().startActivity(intent);
    }
    public void filterList(ArrayList<Notes> filteredList){
        notesList = filteredList;
        notifyDataSetChanged();
    }
}
