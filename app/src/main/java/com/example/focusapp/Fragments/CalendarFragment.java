package com.example.focusapp.Fragments;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.focusapp.Adapters.MyRecyclerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Events;
import com.example.focusapp.Models.User;
import com.example.focusapp.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment  implements AdapterView.OnItemSelectedListener{

    public String eventTypeSelected, dateSelected;
    int nHour, nMinute;

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
    SimpleDateFormat currentMonthFormat = new SimpleDateFormat("MMMM yyyy");


    private Events event = new Events();

    private ImageButton buttonSetTime, buttonNext, buttonPrev, buttonSaveEvent;
    private TextView textViewTime, textViewDate;
    private CompactCalendarView calendarView;
    private EditText editTextEvent;

    private RecyclerView recyclerView;

    MyDbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_calendar, container, false);

        textViewDate=v.findViewById(R.id.textViewDate);
        textViewTime = v.findViewById(R.id.textViewTime);

        buttonSetTime = v.findViewById(R.id.buttonSetTime);
        buttonSaveEvent = v.findViewById(R.id.saveEventButton);
        buttonNext = v.findViewById(R.id.buttonNext);
        buttonPrev = v.findViewById(R.id.buttonPrev);

        editTextEvent = v.findViewById(R.id.editTextEvent);

        ArrayList<Events> eventsList = new ArrayList<>();

        recyclerView = v.findViewById(R.id.recyclerViewCalendar);

        calendarView = v.findViewById(R.id.calendarView);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        dbHelper = new MyDbHelper(getActivity().getBaseContext());

        Event ev1 = new Event(Color.GREEN, 1608117037000L, "Some extra data that I want to store.");
        calendarView.addEvent(ev1);

        String currentDateandTime = currentMonthFormat.format(new Date());
        textViewDate.setText(currentDateandTime);

        InitEventTypeSpinner(v);
        InitCalendarButtons();
        InitCalendar();
        InitButtonSetTime();
        InitButtonSaveEvent();

        eventsList = allEventsList();
        InitRecycleView(eventsList, 1);

        return v;
    }

    //spinner
    private void InitEventTypeSpinner(View v) {
        Spinner eventTypeSpinner = v.findViewById(R.id.eventTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.eventTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(adapter);
        eventTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        eventTypeSelected = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void InitCalendar() {
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {

                dateSelected = epochToDate(dateToEpoch(dateClicked));

                String selectedDateAndTime = sdf.format(dateClicked);

                InitRecycleView(allEventsByDateList(dateSelected),1);
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                String currentMonth = currentMonthFormat.format(firstDayOfNewMonth);
                textViewDate.setText(currentMonth);
            }
        });
    }

    private void InitCalendarButtons() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollRight();
            }
        });
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollLeft();
            }
        });
    }
    private void InitButtonSetTime() {
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                nHour = hourOfDay;
                                nMinute = minute;
                                String time = nHour+":"+nMinute;
                                SimpleDateFormat f24hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24hours.parse(time);
                                    textViewTime.setText(f24hours.format(date));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        },24,0,true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(nHour,nMinute);
                timePickerDialog.show();
            }
        });
    }
    private void InitButtonSaveEvent() {
        buttonSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventContent = editTextEvent.getText().toString();
                String datetime = makeDateAndTime(dateSelected, textViewTime.getText().toString());

                if(dateSelected.equals(null) || dateSelected==null){
                    makeMyToast("Not ready yet", "Set a date for your "+eventTypeSelected, 0);
                }
                if(eventContent.length()<1){
                    makeMyToast("Not ready yet", "Please write a description for your "+eventTypeSelected, 0);
                }
                if(!dateSelected.equals("") && dateSelected != null && eventContent.length()>1){

                    User currentUser = dbHelper.getUser();

                    boolean isInserted = dbHelper.addNewEvent(currentUser.getUser_id(), eventTypeSelected, eventContent, datetime);

                    if(isInserted){
                        Toast.makeText(getActivity(), "Event added!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(), "data NOT inserted! problem", Toast.LENGTH_LONG).show();
                    }

                    textViewTime.setText("00:00");
                    editTextEvent.setText("Add new event");

                    InitRecycleView(allEventsByDateList(dateSelected),1);
                }
            }
        });
    }
    public void InitRecycleView(ArrayList<Events> userEventsList, int adapter_case){
        MyRecyclerAdapter todoAdapter = new MyRecyclerAdapter(getActivity(), userEventsList, 0);
        recyclerView.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    public ArrayList<Events> allEventsList(){
        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = dbHelper.getAllEventsByCheck(0);

        if(res.getCount() == 0){
            makeMyToast("Error", "No events found", 0);
        }
        StringBuffer buffer = new StringBuffer();
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

    public ArrayList<Events> allEventsByDateList(String dateSelected){
        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = dbHelper.getAllEventsByCheck(0);

        if(res.getCount() == 0){
            makeMyToast("Error", "No events found", 0);
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){

            boolean checked = false;

            if(Integer.parseInt(res.getString(5)) == 0){
                checked = false;
            }if(Integer.parseInt(res.getString(5)) == 1){
                checked=true;
            }

            int id = Integer.parseInt(res.getString(0));

            if(res.getString(4).contains(dateSelected)){
                Events event = new Events(id, res.getString(1),
                        res.getString(2), res.getString(3),
                        res.getString(4), checked );

                myEvents.add(event);
            }
        }

        return myEvents;
    }

    private void makeMyToast(String thing, String message, int yn){
        if(yn == 1){
            Toast.makeText(getActivity().getBaseContext(), thing + " - Has been done, Message: "+message, Toast.LENGTH_SHORT).show();
        }if(yn == 0){
            Toast.makeText(getActivity().getBaseContext(), thing + " - Has failed, Message: "+message, Toast.LENGTH_SHORT).show();
        }
    }
    public void makeMyMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void makeMyLog(String message, String thing){
        Log.d("Logged item", ""+message+" :"+thing);
    }

    public String makeDateAndTime(String date, String time){
        String dateAndTime = date + " at " + time;
        return dateAndTime;
    }

    //Filters and converters
    public String epochToDate(Long epoch){
        String sDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        sDate = dateFormat.format(new Date(epoch*1000));

        return sDate;
    }
    public long dateToEpoch(Date date){
        long epoch;

        epoch = date.getTime();
        epoch = epoch/1000;

        return epoch;
    }
    /*public String epochToDateAndTime(Long epoch){

    }*/


}