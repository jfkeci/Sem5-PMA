package com.example.focusapp.Adapters;


import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
    List<AppModel> blockedAppsList = new ArrayList<>();

    Context context;

    private OnAppClickedListener mListener;

    MyDbHelper dbHelper;

    public interface OnAppClickedListener{
        void onAppClick(int position);
    }

    public void setOnItemClickListener(OnAppClickedListener listener){
        mListener = listener;
    }

    public AppsRecyclerAdapter(List<AppModel> appModels, Context context) {
        this.appModels = appModels;
        this.context = context;
        dbHelper = new MyDbHelper(context);
    }

    @NonNull
    @Override
    public AppExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.app_row_layout, parent, false);

        return new AppExampleViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppExampleViewHolder holder, int position) {
        AppModel app = appModels.get(position);

        holder.appName.setText(app.getAppname());
        holder.appIcon.setImageDrawable(app.getAppicon());

        app = checkTheApp(app);

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
                            setAppStatus(position);
                            Log.d("OnClick", "onClick: for the app"+ appModels.get(position).getAppname() +", clicked app status: " +appModels.get(position).getStatus());

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

    public void setAppStatus(int position){

        AppModel app = appModels.get(position);

        int status = app.getStatus();

        if(status == 0){
            appModels.get(position).setStatus(1);
            dbHelper.addNewApp(appModels.get(position));
        }if(status == 1){
            appModels.get(position).setStatus(0);
            dbHelper.deleteApp(appModels.get(position).getAppname());
        }
    }

    public AppModel checkTheApp(AppModel app){
        blockedAppsList.clear();
        blockedAppsList = allBlockedAppsList();

        for (AppModel blocked_app : blockedAppsList) {
            if(blocked_app.getAppname().equals(app.getAppname())){
                app.setStatus(blocked_app.getStatus());
            }
        }

        return app;
    }

    public List<AppModel> allBlockedAppsList(){
        List<AppModel> myApps = new ArrayList<>();

        myApps.clear();

        Cursor res = dbHelper.getAllApps();

        Drawable icon = null;

        while(res.moveToNext()){
            AppModel app = new AppModel(res.getString(0), icon,
                    Integer.parseInt(res.getString(1)), res.getString(2) );
            myApps.add(app);
        }

        return myApps;
    }
}

