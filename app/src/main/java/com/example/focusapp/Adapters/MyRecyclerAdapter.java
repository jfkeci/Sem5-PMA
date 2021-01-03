package com.example.focusapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focusapp.Models.Events;
import com.example.focusapp.R;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Events> eventsList = new ArrayList<>();
    int listType;

    public MyRecyclerAdapter() {}

    public MyRecyclerAdapter(Context context, ArrayList<Events> events, int listType) {
        this.context = context;
        this.eventsList = events;
        this.listType = listType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_layout,parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textContent, textDate, textTime;
        private CheckBox eventCheckbox;
        private ImageView typeIcon;

        public ListViewHolder(View itemView){

            super (itemView);
            textContent = (TextView)itemView.findViewById(R.id.textContentSingleEvent);
            textDate = (TextView)itemView.findViewById(R.id.textDateSingleEvent);
            eventCheckbox = (CheckBox) itemView.findViewById(R.id.checkBoxSingleEvent);
            typeIcon = (ImageView) itemView.findViewById(R.id.imageViewSingleEvent);
            itemView.setOnClickListener(this);

        }

        public void bindView(int position){
            textContent.setText(eventsList.get(position).getEVENT_CONTENT());
            textDate.setText(eventsList.get(position).getEVENT_DATE_TIME());

            if((eventsList.get(position).getEVENT_TYPE()).equals("Event")){
                typeIcon.setImageResource(R.drawable.ic_baseline_calendar_today_24);
            }
            if((eventsList.get(position).getEVENT_TYPE()).equals("Reminder")){
                typeIcon.setImageResource(R.drawable.ic_baseline_notifications_24);
            }
            if((eventsList.get(position).getEVENT_TYPE()).equals("ToDo")){
                typeIcon.setVisibility(View.INVISIBLE);
                eventCheckbox.setVisibility(View.VISIBLE);
            }

        }
        public void onClick(View view){

        }

    }

}
