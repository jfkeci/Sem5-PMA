package com.example.focusapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focusapp.Models.Session;
import com.example.focusapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessionsRecyclerAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Session> sessionsList = new ArrayList<>();

    public SessionsRecyclerAdapter(Context context, ArrayList<Session> sessions) {
        this.context = context;
        this.sessionsList = sessions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_row_layout,parent, false);

        return new SessionsRecyclerAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SessionsRecyclerAdapter.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return sessionsList.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView twContent1, twDate1, twTime1;
        private ImageView ivIcon1;
        private CardView cardView1;

        public ListViewHolder(View itemView){

            super (itemView);
            twContent1 = (TextView)itemView.findViewById(R.id.singleSessionlength);
            twDate1 = (TextView)itemView.findViewById(R.id.singleSessionDate);
            twTime1 = (TextView)itemView.findViewById(R.id.singleSessionTime);
            ivIcon1 = (ImageView) itemView.findViewById(R.id.sessionTimeIcon);
            cardView1 = (CardView)itemView.findViewById(R.id.cardViewSession);

            itemView.setOnClickListener(this);
        }

        public void bindView(int position){

            if(!sessionsList.get(position).isSESSION_FINISHED()){
                cardView1.setCardBackgroundColor(ContextCompat.getColor(context, R.color.MyMatePinkColor));
            }if(sessionsList.get(position).isSESSION_FINISHED()){
                cardView1.setCardBackgroundColor(ContextCompat.getColor(context, R.color.MyTealGreenColor));
            }
            if (sessionsList.get(position).getSESSION_LENGTH().equals("10")) {
                ivIcon1.setImageResource(R.drawable.ic_baseline_looks_one_24);
            }
            if (sessionsList.get(position).getSESSION_LENGTH().equals("20")) {
                ivIcon1.setImageResource(R.drawable.ic_baseline_looks_two_24);
            }
            if (sessionsList.get(position).getSESSION_LENGTH().equals("30")) {
                ivIcon1.setImageResource(R.drawable.ic_baseline_looks_3_24);
            }
            if (sessionsList.get(position).getSESSION_LENGTH().equals("40")) {
                ivIcon1.setImageResource(R.drawable.ic_baseline_looks_4_24);
            }
            String length = sessionsList.get(position).getSESSION_LENGTH() + "min";
            String sDate = sessionsList.get(position).getSESSION_DATE();
            String sTime = sessionsList.get(position).getSESSION_TIME();
            twContent1.setText(length);
            twDate1.setText(sDate);
            twTime1.setText(sTime);

        }
        public void onClick(View view){
        }
    }
}