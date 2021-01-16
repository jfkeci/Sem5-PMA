package com.example.focusapp.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focusapp.Models.AppModel;
import com.example.focusapp.R;

import java.util.ArrayList;
import java.util.List;

public class AppsRecyclerAdapter extends RecyclerView.Adapter<AppsRecyclerAdapter.adapter_design_backend> {

    List<AppModel> appModels = new ArrayList<>();
    Context context;

    public AppsRecyclerAdapter(List<AppModel> appModels, Context context) {
        this.appModels = appModels;
        this.context = context;
    }

    @NonNull
    @Override
    public adapter_design_backend onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.app_row_layout, parent, false);
        adapter_design_backend design = new adapter_design_backend(v);

        return design;
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_design_backend holder, int position) {
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

    public class adapter_design_backend extends RecyclerView.ViewHolder{

        TextView appName;
        ImageView appIcon, lockIcon;
        public adapter_design_backend(@NonNull View itemView) {
            super(itemView);

            appName = itemView.findViewById(R.id.appName);
            appIcon = itemView.findViewById(R.id.appIcon);
            lockIcon = itemView.findViewById(R.id.lockIcon);
        }
    }
}

