package com.example.focusapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focusapp.Models.Notes;
import com.example.focusapp.R;

import java.util.ArrayList;

public class ArchiveRecyclerAdapter {
    /*extends RecyclerView.Adapter{
    Context context;
    ArrayList<Notes> notesList = new ArrayList<>();

    public NotesRecyclerAdapter() {}

    public NotesRecyclerAdapter(Context context, ArrayList<Notes> notes) {
        this.context = context;
        this.notesList = notes;
    }

    public void setData(ArrayList<Notes> notesList)
    {
        this.notesList = notesList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_layout,parent, false);

        return new NotesRecyclerAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((NotesRecyclerAdapter.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView twTitle, twContent, twDate;

        public ListViewHolder(View itemView){

            super (itemView);
            twTitle = (TextView)itemView.findViewById(R.id.twNoteTitle);
            twContent = (TextView)itemView.findViewById(R.id.twNoteContent);
            twDate = (TextView)itemView.findViewById(R.id.twNoteDateTime);

            itemView.setOnClickListener(this);
        }

        public void bindView(int position){

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

                twTitle.setText(mainTitle);
                twContent.setText(mainContent);
                twDate.setText(notesList.get(position).getNOTE_DATE_TIME());
            }
        }
        public void onClick(View view){

        }

    }*/

}