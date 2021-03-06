package com.example.focusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focusapp.Database.MyDbHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private MyDbHelper dbHelper;

    private String dbUrl = "https://caltodo-27ad3-default-rtdb.firebaseio.com/";
    private DatabaseReference events, notes, sessions;

    private FirebaseDatabase database;

    private ProgressBar progressBar;

    Button button;
    TextView twBanner, twSlogan;
    ImageView ivSplash;

    Animation a1, a2, a3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=findViewById(R.id.startButton);
        twBanner = findViewById(R.id.twSplashBanner);
        twSlogan = findViewById(R.id.twSubBanner);
        ivSplash = findViewById(R.id.splashScreenImage);
        progressBar = findViewById(R.id.progressBarBackup);

        database = FirebaseDatabase.getInstance();
        events = database.getReference("events");
        notes = database.getReference("notes");
        sessions = database.getReference("sessions");

        a1 = AnimationUtils.loadAnimation(this, R.anim.appear);
        a2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_one);
        a3 = AnimationUtils.loadAnimation(this, R.anim.slide_in_two);

        ivSplash.startAnimation(a1);
        twBanner.startAnimation(a2);
        twSlogan.startAnimation(a2);
        button.startAnimation(a3);

        dbHelper = new MyDbHelper(this);

        Typeface MLight = Typeface.createFromAsset(getAssets(), "fonts/MLight.ttf");
        Typeface MMedium = Typeface.createFromAsset(getAssets(), "fonts/MLight.ttf");
        Typeface MRegular = Typeface.createFromAsset(getAssets(), "fonts/MLight.ttf");

        button.setTypeface(MRegular);
        twBanner.setTypeface(MMedium);
        twSlogan.setTypeface(MLight);
        boolean isUserSet = dbHelper.userIsSet();

        if(isUserSet){
            startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUserSet){
                    startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));
                }else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

    }


}