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
        private TextView textContent, textDate, textPosition;
        private ImageView typeIcon, ivLeft, ivRight;

        public ListViewHolder(View itemView){

            super (itemView);
            textContent = (TextView)itemView.findViewById(R.id.textContentSingleEvent);
            textDate = (TextView)itemView.findViewById(R.id.textDateSingleEvent);
            textPosition = (TextView)itemView.findViewById(R.id.position);
            typeIcon = (ImageView) itemView.findViewById(R.id.imageViewSingleEvent);
            ivLeft = (ImageView) itemView.findViewById(R.id.swLeft);
            ivRight = (ImageView) itemView.findViewById(R.id.swRight);

            itemView.setOnClickListener(this);

        }

        public void bindView(int position){

            if(eventsList.size()>=1){
                String content = eventsList.get(position).getEVENT_CONTENT();
                String mainContent = "";

                if(content.length() > 35){
                    int n=35;
                    for(int i=0;i<content.length();i++){
                        mainContent = mainContent + content.charAt(i);
                        if(i==n){
                            mainContent = mainContent + " \n ";
                            n+=35;
                        }
                    }
                }if(content.length() < 35){
                    mainContent = content;
                }


                textContent.setText(mainContent);
                textDate.setText(eventsList.get(position).getEVENT_DATE_TIME());

                Events posEvent = eventsList.get(position);

                textPosition.setText("rec: " + position + ", list pos" + eventsList.indexOf(posEvent));

                if((eventsList.get(position).getEVENT_TYPE()).equals("Event")){
                    typeIcon.setImageResource(R.drawable.ic_baseline_calendar_today_24);
                }
                if((eventsList.get(position).getEVENT_TYPE()).equals("Reminder")){
                    typeIcon.setImageResource(R.drawable.ic_baseline_notifications_24);
                }
                if((eventsList.get(position).getEVENT_TYPE()).equals("ToDo")){
                    typeIcon.setImageResource(R.drawable.ic_baseline_check_24);
                }
            }

            if(listType == 1){
                ivLeft.setVisibility(View.INVISIBLE);
                ivRight.setVisibility(View.INVISIBLE);
            }
        }
        public void onClick(View view){

        }

    }

}
