package com.example.focusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.focusapp.Database.MyDbHelper;

public class MainActivity extends AppCompatActivity {

    MyDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDbHelper(this);

        if(dbHelper.userIsSet()){
            startActivity(new Intent(this, FragmentHolderActivity.class));
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }





    }


}