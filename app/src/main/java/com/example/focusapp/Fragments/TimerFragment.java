package com.example.focusapp.Fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focusapp.Adapters.SessionsRecyclerAdapter;
import com.example.focusapp.CountDownActivity;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Session;
import com.example.focusapp.R;

import java.util.ArrayList;


public class TimerFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    //-----------------------------------------------------------------------------------------------------
    //VARIABLES
    //-----------------------------------------------------------------------------------------------------

    private static final long START_TIME_IN_MILLIS = 60000;
    private long TimeLeftInMillis = START_TIME_IN_MILLIS;

    private Button ButtonStartPause;
    private ImageView ivClock, ivArrow;
    private Spinner sessionLengthSpinner;

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
        ivClock = v.findViewById(R.id.ivTimerCircle);
        ivArrow = v.findViewById(R.id.ivTimerArrow);
        recycleViewTimer = v.findViewById(R.id.recycleViewTimer);
        sessionLengthSpinner = v.findViewById(R.id.sessionLengthSpinner);

        ButtonStartPause.setTypeface(MMedium);
        dbHelper = new MyDbHelper(getActivity());

        InitEventTypeSpinner();

        ButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CountDownActivity.class);
                intent.putExtra("session_length", sessionLengthSelected);
                startActivity(intent);
            }
        });

        sessionList = allSessionsList();

        sessionsAdapter = new SessionsRecyclerAdapter(getActivity(), sessionList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        recycleViewTimer.setLayoutManager(gridLayoutManager);
        recycleViewTimer.setAdapter(sessionsAdapter);

        sessionsAdapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        sessionList = allSessionsList();
        sessionsAdapter.setData(sessionList);
    }


    private void InitEventTypeSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.sessionLength, R.layout.spinner_item);
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

    public ArrayList<Session> allSessionsList(){
        ArrayList<Session> mySessions = new ArrayList<>();

        mySessions.clear();

        Cursor res = dbHelper.getAllSessions();

        while(res.moveToNext()){

            int id = Integer.parseInt(res.getString(0));

            boolean finished = true;

            if(Integer.parseInt(res.getString(6)) == 1){
                finished = true;
            }if(Integer.parseInt(res.getString(6)) == 0){
                finished = false;
            }

            Session session = new Session(id, res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4), res.getString(5), finished );

            mySessions.add(0, session);
        }

        return mySessions;
    }
}