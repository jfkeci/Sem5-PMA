package com.example.focusapp.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.focusapp.AppLockListActivity;
import com.example.focusapp.R;

import java.util.Locale;
import java.util.MissingResourceException;


public class TimerFragment extends Fragment {

    //-----------------------------------------------------------------------------------------------------
    //VARIABLES
    //-----------------------------------------------------------------------------------------------------

    private static final long START_TIME_IN_MILLIS = 600000;
    private long TimeLeftInMillis = START_TIME_IN_MILLIS;

    private TextView TextViewCountDown;
    private Button ButtonStartPause, ButtonReset, ButtonDisableApps;
    ImageView ivClock, ivArrow;

    private CountDownTimer countDownTimer;
    private boolean TimerRunning;

    Animation a1, a2, a3, rounding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        Typeface MLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");
        Typeface MMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");
        Typeface MRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MLight.ttf");

        TextViewCountDown = v.findViewById(R.id.textViewCountdown);
        ButtonStartPause = v.findViewById(R.id.buttonStartPause);
        ButtonReset = v.findViewById(R.id.buttonResetTimer);
        ButtonDisableApps = (Button) v.findViewById(R.id.buttonDisableApps);
        ivClock = v.findViewById(R.id.ivTimerCircle);
        ivArrow = v.findViewById(R.id.ivTimerArrow);

        a1 = AnimationUtils.loadAnimation(getActivity().getBaseContext(), R.anim.appear);
        a2 = AnimationUtils.loadAnimation(getActivity().getBaseContext(), R.anim.slide_in_one);
        a3 = AnimationUtils.loadAnimation(getActivity().getBaseContext(), R.anim.slide_in_two);
        rounding = AnimationUtils.loadAnimation(getActivity().getBaseContext(), R.anim.rounding);

        ivClock.startAnimation(a1);
        ivArrow.startAnimation(a1);
        TextViewCountDown.startAnimation(a2);
        ButtonStartPause.startAnimation(a3);
        ButtonReset.startAnimation(a3);
        ButtonDisableApps.startAnimation(a3);

        ButtonStartPause.setTypeface(MMedium);
        TextViewCountDown.setTypeface(MRegular);

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
                if(TimerRunning){

                }else{
                    startTimer();
                    ivArrow.startAnimation(rounding);
                }
            }
        });
        ButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
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

            }
        }.start();
        TimerRunning = true;

    }

    private void pauseTimer(){
        countDownTimer.cancel();
        TimerRunning = false;

        resetTimer();
    }
    private void resetTimer(){
        TimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();

    }
    private void updateCountDownText(){
        int minutes = (int) (TimeLeftInMillis / 1000) / 60;
        int seconds = (int) (TimeLeftInMillis / 1000) % 60;

        String TimeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        TextViewCountDown.setText(TimeLeftFormatted);
    }
}