package com.example.focusapp.Fragments;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focusapp.Adapters.MyRecyclerAdapter;
import com.example.focusapp.Adapters.SessionsRecyclerAdapter;
import com.example.focusapp.AppLockListActivity;
import com.example.focusapp.CountDownActivity;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Session;
import com.example.focusapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Timer;


public class TimerFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    //-----------------------------------------------------------------------------------------------------
    //VARIABLES
    //-----------------------------------------------------------------------------------------------------

    private static final long START_TIME_IN_MILLIS = 60000;
    private long TimeLeftInMillis = START_TIME_IN_MILLIS;

    private Button ButtonStartPause, ButtonDisableApps;
    private ImageView ivClock, ivArrow;
    private Spinner sessionLengthSpinner;
    private TextView tvCredits;

    ArrayList<Session> sessionList = new ArrayList<>();

    private CountDownTimer countDownTimer;
    private boolean TimerRunning;
    private String sessionLengthSelected;

    private ObjectAnimator anim;

    private SessionsRecyclerAdapter sessionsAdapter;
    RecyclerView recycleViewTimer;

    private MyDbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        Typeface MMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");

        ButtonStartPause = v.findViewById(R.id.buttonStartPause);
        ButtonDisableApps = v.findViewById(R.id.buttonDisableApps);
        ivClock = v.findViewById(R.id.ivTimerCircle);
        ivArrow = v.findViewById(R.id.ivTimerArrow);
        recycleViewTimer = v.findViewById(R.id.recycleViewTimer);
        sessionLengthSpinner = v.findViewById(R.id.sessionLengthSpinner);
        tvCredits = v.findViewById(R.id.textViewCredits);

        ButtonStartPause.setTypeface(MMedium);
        ButtonDisableApps.setTypeface(MMedium);

        dbHelper = new MyDbHelper(getActivity());
        String myCredits = CountCredits();
        tvCredits.setText(myCredits);

        InitRecycleViewSessions();
        InitEventTypeSpinner();

        ButtonDisableApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AppLockListActivity.class);
                startActivity(intent);
            }
        });
        ButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CountDownActivity.class);
                intent.putExtra("session_length", sessionLengthSelected);
                startActivity(intent);
            }
        });

        return v;
    }


    private void InitEventTypeSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.sessionLength, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sessionLengthSpinner.setAdapter(adapter);
        sessionLengthSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sessionLengthSelected = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String CountCredits(){
        int credits = 0;
        String sCredits="";
        ArrayList<Session> mySessions = allSessionsList();

        for (Session session : mySessions){
            float points = Float.parseFloat(session.getSESSION_POINTS());
            credits+=points;
        }
        if(credits > 0){
            sCredits = "You have: " +String.valueOf(credits)+ "\n working points";
        }else{
            sCredits = "Try, you can do it";
        }
        return sCredits;
    }


    public void InitRecycleViewSessions(){

        sessionList.clear();

        sessionList = allSessionsList();

        sessionsAdapter = new SessionsRecyclerAdapter(getActivity(), sessionList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        recycleViewTimer.setLayoutManager(gridLayoutManager);
        recycleViewTimer.setAdapter(sessionsAdapter);

        sessionsAdapter.notifyDataSetChanged();
    }

    public ArrayList<Session> allSessionsList(){
        ArrayList<Session> mySessions = new ArrayList<>();

        mySessions.clear();

        Cursor res = dbHelper.getAllSessions();

        while(res.moveToNext()){

            int id = Integer.parseInt(res.getString(0));

            boolean finished = true;

            if(Integer.parseInt(res.getString(5)) == 1){
                finished = false;
            }if(Integer.parseInt(res.getString(5)) == 0){
                finished = false;
            }

            Session session = new Session(id, res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4), res.getString(5), finished );

            mySessions.add(session);
        }

        return mySessions;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            makeMyToast("is not on screen");
        }
        else
        {
            makeMyToast("on screen");
        }
    }

    private void makeMyToast(String message){
        Toast.makeText(getActivity().getBaseContext(), ""+message, Toast.LENGTH_LONG).show();
    }
}