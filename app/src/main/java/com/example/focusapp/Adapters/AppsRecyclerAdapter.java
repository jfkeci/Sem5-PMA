package com.example.focusapp.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.AppModel;
import com.example.focusapp.R;

import java.util.ArrayList;
import java.util.List;

public class AppsRecyclerAdapter extends RecyclerView.Adapter<AppsRecyclerAdapter.AppExampleViewHolder> {

    List<AppModel> appModels = new ArrayList<>();
    Context context;


    private OnAppClickedListener mListener;

    public interface OnAppClickedListener{
        void onAppClick(int position);
    }

    public void setOnItemClickListener(OnAppClickedListener listener){
        mListener = listener;
    }

    public AppsRecyclerAdapter(List<AppModel> appModels, Context context) {
        this.appModels = appModels;
        this.context = context;
    }

    @NonNull
    @Override
    public AppExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.app_row_layout, parent, false);
        AppExampleViewHolder design = new AppExampleViewHolder(v, mListener);

        return design;
    }

    @Override
    public void onBindViewHolder(@NonNull AppExampleViewHolder holder, int position) {
        AppModel app = appModels.get(position);

        holder.appName.setText(app.getAppname());
        holder.appIcon.setImageDrawable(app.getAppicon());

        if(app.getStatus() == 0){
            holder.lockIcon.setImageResource(R.drawable.ic_baseline_lock_open_24);
        }else{
            holder.lockIcon.setImageResource(R.drawable.ic_baseline_lock_24);
        }


    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }

    public class AppExampleViewHolder extends RecyclerView.ViewHolder{

        TextView appName;
        ImageView appIcon, lockIcon;

        public AppExampleViewHolder(@NonNull View itemView, final OnAppClickedListener listener) {
            super(itemView);

            appName = itemView.findViewById(R.id.appName);
            appIcon = itemView.findViewById(R.id.appIcon);
            lockIcon = itemView.findViewById(R.id.lockIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAppClick(position);
                        }
                    }
                }
            });
        }
    }
    public void filterList(List<AppModel> filteredList){
        appModels = filteredList;
        notifyDataSetChanged();
    }
}

