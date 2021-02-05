package com.example.focusapp.Fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.example.focusapp.MainActivity;
import com.example.focusapp.Models.Events;
import com.example.focusapp.Models.Notes;
import com.example.focusapp.Models.User;
import com.example.focusapp.R;
import com.example.focusapp.Recievers.AlarmReceiver;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.snackbar.Snackbar;


import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CalendarFragment extends Fragment  implements AdapterView.OnItemSelectedListener{

    Context calContext;

    public String eventTypeSelected;
    public String dateSelected="";
    int nHour, nMinute;

    public long atTime;

    int countFirst = 0;

    public Events checkedEvent = new Events();
    public Events deletedEvent = new Events();

    public ArrayList<Events> arrayListEvents = new ArrayList<>();

    public SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
    public SimpleDateFormat currentMonthFormat = new SimpleDateFormat("MMMM yyyy");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private ImageButton buttonSetTime, buttonNext, buttonPrev, buttonSaveEvent;
    private TextView textViewTime, textViewDate;
    private CompactCalendarView calendarView;
    private EditText editTextEvent;

    public RecyclerView recyclerView;

    public MyDbHelper dbHelper;

    public MyRecyclerAdapter calendarAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_calendar, container, false);

        calContext = getActivity();


        textViewDate=v.findViewById(R.id.textViewDate);
        textViewTime = v.findViewById(R.id.textViewTime);

        buttonSetTime = v.findViewById(R.id.buttonSetTime);
        buttonSaveEvent = v.findViewById(R.id.saveEventButton);
        buttonNext = v.findViewById(R.id.buttonNext);
        buttonPrev = v.findViewById(R.id.buttonPrev);

        editTextEvent = v.findViewById(R.id.editTextEvent);

        recyclerView = v.findViewById(R.id.recyclerViewCalendar);

        calendarView = v.findViewById(R.id.calendarView);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        dbHelper = new MyDbHelper(getActivity().getBaseContext());

        String currentDateandTime = currentMonthFormat.format(new Date());
        textViewDate.setText(currentDateandTime);

        InitEventTypeSpinner(v);
        InitCalendar();
        InitButtonSetTime();
        InitButtonSaveEvent();

        InitRecycleViewFunct(dateSelected);

        return v;
    }

    public void InitRecycleViewFunct(String dateSelected){

        arrayListEvents.clear();

        arrayListEvents = allEventsByDateList(dateSelected, false);

        calendarAdapter = new MyRecyclerAdapter(getActivity(), arrayListEvents, 0);
        recyclerView.setAdapter(calendarAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        calendarAdapter.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if(direction == ItemTouchHelper.LEFT){
                    deletedEvent = arrayListEvents.get(position);

                    String event_id = String.valueOf(deletedEvent.getEVENT_ID());

                    int deleted = dbHelper.deleteEvent(event_id);

                    if(deleted == 1){
                        arrayListEvents.remove(deletedEvent);
                        calendarAdapter.notifyItemRemoved(position);
                        InitCalendar();
                        RemoveEventNotification(deletedEvent.getEVENT_ID());
                        Log.d("Removing ", "onSwiped: delete notification for eventid :  " + deletedEvent.getEVENT_ID());
                    }

                    Snackbar.make(recyclerView, deletedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean undone = dbHelper.addNewEventWithId(deletedEvent);

                            if(undone){
                                arrayListEvents.add(position, deletedEvent);
                                calendarAdapter.notifyItemInserted(position);
                                InitCalendar();
                                SetEventNotification(deletedEvent);

                            }else{
                                makeMyToast("Something went wrong!");
                            }
                        }
                    }).show();

                }
                if(direction == ItemTouchHelper.RIGHT){
                    checkedEvent = arrayListEvents.get(position);

                    boolean checked = dbHelper.eventSetChecked(String.valueOf(checkedEvent.getEVENT_ID()));

                    if(checked){
                        arrayListEvents.remove(checkedEvent);
                        calendarAdapter.notifyItemRemoved(position);
                        InitCalendar();
                        RemoveEventNotification(checkedEvent.getEVENT_ID());
                    }else{
                        makeMyToast("Failed");
                    }
                    Snackbar.make(recyclerView, checkedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean checked = dbHelper.eventUncheck(String.valueOf(checkedEvent.getEVENT_ID()));
                            if(checked){
                                arrayListEvents.add(position, checkedEvent);
                                calendarAdapter.notifyItemInserted(position);
                                InitCalendar();
                                SetEventNotification(checkedEvent);
                            }else{
                                makeMyToast("Failed");
                            }
                        }
                    }).show();
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.MyPinkColor))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_sweep_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.MyGoodGreenColor))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_check_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void InitEventTypeSpinner(View v) {
        Spinner eventTypeSpinner = v.findViewById(R.id.eventTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.eventTypes, R.layout.spinner_item);
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

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        arrayListEvents = allEventsByDateList(dateSelected, false);
        calendarAdapter.setData(arrayListEvents);
    }

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void InitCalendar() {
        ArrayList<Events> arrayListAllEvents = new ArrayList<>();
        arrayListAllEvents.clear();
        arrayListAllEvents = allEventsList(false);

        calendarView.removeAllEvents();

        if(countFirst < 1){
            Date currentDate = Calendar.getInstance().getTime();
            dateSelected = dateFormat.format(currentDate);
            countFirst++;
        }

        for (Events event : arrayListAllEvents) {
            if(!event.isCHECKED()){
                String eventDateTimeString = event.getEVENT_DATE_TIME();
                if (!event.isCHECKED()) {
                    try {
                        Date eventDate = sdf.parse(eventDateTimeString);
                        long eventEpoch = dateToEpoch(eventDate);

                        Event ev1 = new Event(Color.RED, eventEpoch * 1000, event.getEVENT_CONTENT());
                        calendarView.addEvent(ev1);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {

                dateSelected = epochToDate(dateToEpoch(dateClicked));

                InitRecycleViewFunct(dateSelected);
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                String currentMonth = currentMonthFormat.format(firstDayOfNewMonth);
                textViewDate.setText(currentMonth);
            }
        });

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

                if(eventContent.length()<1){
                    makeMyToast("Please write a description for your "+eventTypeSelected);
                }
                if(eventContent.length()>1){

                    User currentUser = dbHelper.getUser();
                    String userID = currentUser.getUser_id();

                    boolean isInserted = dbHelper.addNewEvent(userID, eventTypeSelected, eventContent, datetime);

                    if(isInserted){

                        InitRecycleViewFunct(dateSelected);

                        InitCalendar();

                        int id = getEventId(eventTypeSelected, eventContent, datetime);

                        Log.d("", "notification event id " + id);
                        Events newEvent = new Events(id, userID, eventTypeSelected, eventContent, datetime, false);

                        SetEventNotification(newEvent);

                        textViewTime.setText("00:00");
                        editTextEvent.setText("Add new event");

                        closeKeyboard();

                    }else{
                        makeMyToast("Failed to add new " + eventTypeSelected);
                    }
                }
            }
        });
    }

    public ArrayList<Events> allEventsByDateList(String dateSelected, boolean check){
        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = dbHelper.getAllEventsByCheck(0);

        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){

            boolean checked = false;

            if(Integer.parseInt(res.getString(5)) == 0){
                checked = false;
            }if(Integer.parseInt(res.getString(5)) == 1){
                checked = true;
            }

            if(!checked && !check){
                int id = Integer.parseInt(res.getString(0));

                if(res.getString(4).contains(dateSelected)){
                    Events event = new Events(id, res.getString(1),
                            res.getString(2), res.getString(3),
                            res.getString(4), checked );

                    myEvents.add(event);
                }
            }

        }

        return myEvents;
    }

    public ArrayList<Events> allEventsList(boolean check){
        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = dbHelper.getAllEventsByCheck(0);

        while(res.moveToNext()){

            boolean checked = false;

            if(Integer.parseInt(res.getString(5)) == 0){
                checked = false;
            }if(Integer.parseInt(res.getString(5)) == 1){
                checked = true;
            }

            if(!checked && !check){
                int id = Integer.parseInt(res.getString(0));
                Events event = new Events(id, res.getString(1),
                        res.getString(2), res.getString(3),
                        res.getString(4), checked );
                myEvents.add(event);
            }

        }

        return myEvents;
    }

    private void SetEventNotification(Events event){

        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");

        int nyear, nmonth, nday, nhour, nminute;

        Date myDate;

        try{

            myDate = sdf.parse(event.getEVENT_DATE_TIME());

            nyear = Integer.parseInt(sdfYear.format(myDate));
            nmonth = Integer.parseInt(sdfMonth.format(myDate));
            nday = Integer.parseInt(sdfDay.format(myDate));
            nhour = Integer.parseInt(sdfHour.format(myDate));
            nminute = Integer.parseInt(sdfMinute.format(myDate));

        }catch(ParseException e){
            e.printStackTrace();
        }

        Intent intent = new Intent(getActivity().getBaseContext(), AlarmReceiver.class);

        int ev_id = event.getEVENT_ID();

        intent.putExtra("event_id", String.valueOf(ev_id));
        intent.putExtra("event_date_time", event.getEVENT_DATE_TIME());
        intent.putExtra("event_content", event.getEVENT_CONTENT());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), ev_id, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        try{
            Date date = sdf.parse(event.getEVENT_DATE_TIME());

            atTime = date.getTime();
        }catch(ParseException e){
            e.printStackTrace();
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, atTime, pendingIntent);

    }

    private void RemoveEventNotification(int ev_id){

        Intent intent = new Intent(getActivity().getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), ev_id, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    private int getEventId(String type, String content, String datetime){
        int id = 0;
        Cursor res = dbHelper.readEventId(type, content, datetime);

        while(res.moveToNext()){
            id = Integer.parseInt(res.getString(0));
        }

        res.close();

        return id;
    }

    private void makeMyToast(String message){
            Toast.makeText(getActivity().getBaseContext(), ""+message, Toast.LENGTH_LONG).show();
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

        sDate = dateFormat.format(new Date(epoch*1000));

        return sDate;
    }
    public long dateToEpoch(Date date){
        long epoch;

        epoch = date.getTime() / 1000;

        return epoch;
    }


}