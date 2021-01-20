package com.example.focusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
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
import com.example.focusapp.Models.Events;
import com.example.focusapp.Models.Notes;
import com.example.focusapp.Models.Session;
import com.example.focusapp.Recievers.AlarmReceiver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

        startActivity(new Intent(MainActivity.this, AppLockListActivity.class));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHelper.userIsSet()){
                    backupData();
                    //startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));

                }else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

    }

    public void backupData(){
        ArrayList<Events> eventsList = allEventsList();
        ArrayList<Notes> notesList = allNotesList();
        ArrayList<Session> sessionsList = allSessionList();

        if(!eventsList.isEmpty()){
            //removing old data from firebase
            RemoveData("events");

            for (Events event : eventsList) {
                events.push().setValue(event);
                progressBar.setVisibility(View.VISIBLE);
                makeMyToast("Events backed up!");
            }
        }
        if(!notesList.isEmpty()){
            //removing old data from firebase
            RemoveData("notes");

            for (Notes note : notesList) {
                notes.push().setValue(note);
                progressBar.setVisibility(View.VISIBLE);
                makeMyToast("Notes backed up!");
            }
        }
        if(!sessionsList.isEmpty()){
            //removing old data from firebase
            RemoveData("sessions");

            for (Session session : sessionsList) {
                sessions.push().setValue(session);
                progressBar.setVisibility(View.VISIBLE);
                makeMyToast("Notes backed up!");
            }

        }
        progressBar.setVisibility(View.GONE);
    }

    public ArrayList<Events> allEventsList(){
        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = dbHelper.getAllEvents();

        while(res.moveToNext()){
            boolean checked = false;

            if(Integer.parseInt(res.getString(5)) == 0){
                checked = false;
            }if(Integer.parseInt(res.getString(5)) == 1){
                checked=true;
            }
            int id = Integer.parseInt(res.getString(0));

            Events event = new Events(id, res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4), checked );

            myEvents.add(event);
        }

        return myEvents;
    }
    public ArrayList<Notes> allNotesList(){
        ArrayList<Notes> myNotes = new ArrayList<>();

        myNotes.clear();

        Cursor res = dbHelper.getAllNotes();

        while(res.moveToNext()){

            int id = Integer.parseInt(res.getString(0));

            Notes note = new Notes(id, res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4) );

            myNotes.add(note);
        }

        return myNotes;
    }
    public ArrayList<Session> allSessionList(){
        ArrayList<Session> mySessions = new ArrayList<>();

        mySessions.clear();

        Cursor res = dbHelper.getAllSessions();

        while(res.moveToNext()){

            int id = Integer.parseInt(res.getString(0));

            boolean finished = true;

            if(Integer.parseInt(res.getString(5)) == 1){
                finished = true;
            }if(Integer.parseInt(res.getString(5)) == 0){
                finished = false;
            }

            Session session = new Session(id, res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4), finished );

            mySessions.add(session);
        }

        return mySessions;
    }

    public void RemoveData(String node){
        database.getReference(node).removeValue();
    }

    private void makeMyToast(String message){
        Toast.makeText(this, " "+message, Toast.LENGTH_LONG).show();
    }

}