   package com.example.focusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.focusapp.Adapters.AppsRecyclerAdapter;
import com.example.focusapp.Models.AppModel;

import java.util.ArrayList;
import java.util.List;

public class AppLockListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<AppModel> appModelList = new ArrayList<>();

    AppsRecyclerAdapter adapter;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_list);

        recyclerView = findViewById(R.id.recycleView);

        adapter = new AppsRecyclerAdapter(appModelList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getInstalledApps();
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

    public void getInstalledApps(){
        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);
        // add to list of dataset



        for (int i = 0; i<packageInfos.size(); i++){
            String name = packageInfos.get(i).applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(getPackageManager());
            String packname = packageInfos.get(i).packageName;

            AppModel app = new AppModel(name, icon, 0, packname);


            appModelList.add(app);

        }
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

}