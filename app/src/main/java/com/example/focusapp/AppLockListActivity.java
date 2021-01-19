   package com.example.focusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.focusapp.Adapters.AppsRecyclerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.AppModel;
import com.example.focusapp.Models.Session;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AppLockListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<AppModel> appModelList = new ArrayList<>();
    public List<AppModel> blockedAppsList = new ArrayList<>();

    EditText etSearchApps;
    Button buttonSave;

    AppsRecyclerAdapter adapter;

    ProgressDialog progressDialog;

    MyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_list);

        dbHelper = new MyDbHelper(this);

        recyclerView = findViewById(R.id.recycleViewApps);
        etSearchApps = findViewById(R.id.etAppSearch);
        buttonSave = findViewById(R.id.saveBlockedButton);

        adapter = new AppsRecyclerAdapter(appModelList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        blockedAppsList = allBlockedAppsList();

        adapter.setOnItemClickListener(new AppsRecyclerAdapter.OnAppClickedListener() {
            @Override
            public void onAppClick(int position) {
                changeAppStatus(position);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getInstalledApps();
            }
        });

        etSearchApps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppLockListActivity.this, FragmentHolderActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.setTitle("fetching apps");
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }

    public void changeAppStatus(int position){
        AppModel clickedApp = appModelList.get(position);
        int status = clickedApp.getStatus();

        if(status == 1){
            appModelList.get(position).setStatus(0);
        }if(status == 0){
            appModelList.get(position).setStatus(1);
        }
        if(clickedApp.getStatus() == 0){
            dbHelper.deleteApp(clickedApp.getAppname());
        }if(clickedApp.getStatus() == 1){
            dbHelper.addNewApp(clickedApp);
        }
        adapter.notifyItemChanged(position);
    }

    private void filter(String appString){
        List<AppModel> filteredList = new ArrayList<>();

        for(AppModel app : appModelList){
            if(app.getAppname().toLowerCase().contains(appString.toLowerCase())){
                filteredList.add(app);
            }
        }

        adapter.filterList(filteredList);
    }

    public void getInstalledApps(){
        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);


        for (int i = 0; i<packageInfos.size(); i++){
            String name = packageInfos.get(i).applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(getPackageManager());
            String packname = packageInfos.get(i).packageName;

            AppModel app = new AppModel(name, icon, 0, packname);

            app = checkTheApp(app);

            appModelList.add(app);

        }
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    public AppModel checkTheApp(AppModel app){
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