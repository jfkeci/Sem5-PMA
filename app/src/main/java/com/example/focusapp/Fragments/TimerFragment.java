package com.example.focusapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.focusapp.R;

import java.util.Locale;


public class TimerFragment extends Fragment {

    //-----------------------------------------------------------------------------------------------------
    //VARIABLES
    //-----------------------------------------------------------------------------------------------------

    private static final long START_TIME_IN_MILLIS = 600000;
    private long TimeLeftInMillis = START_TIME_IN_MILLIS;

    private TextView TextViewCountDown;
    private Button ButtonStartPause, ButtonReset;

    private CountDownTimer countDownTimer;
    private boolean TimerRunning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        TextViewCountDown = v.findViewById(R.id.textViewCountdown);
        ButtonStartPause = v.findViewById(R.id.buttonStartPause);
        ButtonReset = v.findViewById(R.id.buttonResetTimer);

        ButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TimerRunning){
                    pauseTimer();
                }else{
                    startTimer();
                }
            }
        });
        ButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        updateCountDownText();

        return v;
    }


//-----------------------------------------------------------------------------------------------------
//METHODS
//-----------------------------------------------------------------------------------------------------

    private void startTimer(){
        countDownTimer = new CountDownTimer(TimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                TimerRunning=false;
                ButtonStartPause.setText("start");
                ButtonStartPause.setVisibility(View.INVISIBLE);
                ButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();
        TimerRunning = true;
        ButtonStartPause.setText("pause");
        ButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer(){
        countDownTimer.cancel();
        TimerRunning = false;
        ButtonStartPause.setText("start");
        ButtonReset.setVisibility(View.VISIBLE);
    }
    private void resetTimer(){
        TimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        ButtonReset.setVisibility(View.INVISIBLE);
        ButtonStartPause.setVisibility(View.VISIBLE);

    }
    private void updateCountDownText(){
        int minutes = (int) (TimeLeftInMillis / 1000) / 60;
        int seconds = (int) (TimeLeftInMillis / 1000) % 60;

        String TimeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        TextViewCountDown.setText(TimeLeftFormatted);
    }
}