package com.example.focusapp.Fragments;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

    public String eventTypeSelected;
    public String dateSelected="";
    int nHour, nMinute;

    int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    int countFirst = 0;

    private Events checkedEvent = new Events();
    public Events deletedEvent = new Events();

    public ArrayList<Events> arrayListEvents = new ArrayList<>();

    public SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
    public SimpleDateFormat currentMonthFormat = new SimpleDateFormat("MMMM yyyy");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    public SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    public SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    public SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
    public SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");

    private ImageButton buttonSetTime, buttonNext, buttonPrev, buttonSaveEvent;
    private TextView textViewTime, textViewDate;
    private CompactCalendarView calendarView;
    private EditText editTextEvent;

    private RecyclerView recyclerView;

    public MyDbHelper dbHelper;

    public MyRecyclerAdapter todoAdapter;

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


    public void RefreshItHere(){
        Date currentDate = Calendar.getInstance().getTime();
        dateSelected = dateFormat.format(currentDate);
        InitRecycleViewFunct(dateSelected);
        InitCalendar();
    }

    public void InitRecycleViewFunct(String dateSelected){

        arrayListEvents.clear();

        arrayListEvents = allEventsByDateList(dateSelected, false);

        todoAdapter = new MyRecyclerAdapter(getActivity(), arrayListEvents, 0);
        recyclerView.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        todoAdapter.notifyDataSetChanged();

        if(arrayListEvents.isEmpty()){
            makeMyToast("No events for today");
        }

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
                        todoAdapter.notifyItemRemoved(position);
                        InitCalendar();
                    }

                    Snackbar.make(recyclerView, deletedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            boolean undone = dbHelper.addNewEventWithId(deletedEvent);

                            if(undone){
                                arrayListEvents.add(position, deletedEvent);
                                todoAdapter.notifyItemInserted(position);
                                InitCalendar();
                            }else{
                                makeMyToast("Something went wrong!");
                            }
                        }
                    }).show();

                }
                if(direction == ItemTouchHelper.RIGHT){
                    checkedEvent = arrayListEvents.get(position);

                    arrayListEvents.remove(checkedEvent);
                    todoAdapter.notifyItemRemoved(position);

                    boolean checked = dbHelper.eventSetChecked(String.valueOf(checkedEvent.getEVENT_ID()));

                    if(checked){
                        makeMyToast("Awesome!");
                        InitCalendar();
                    }else{
                        makeMyToast("Failed");
                    }
                    Snackbar.make(recyclerView, checkedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean checked = dbHelper.eventUncheck(String.valueOf(checkedEvent.getEVENT_ID()));
                            if(checked){
                                makeMyToast("Unchecked");
                                arrayListEvents.add(position, checkedEvent);
                                todoAdapter.notifyItemInserted(position);
                                InitCalendar();
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
                    makeMyToast("Not ready yet, Please write a description for your "+eventTypeSelected);
                }
                if(eventContent.length()>1){

                    User currentUser = dbHelper.getUser();

                    boolean isInserted = dbHelper.addNewEvent(currentUser.getUser_id(), eventTypeSelected, eventContent, datetime);

                    if(isInserted){
                        makeMyToast("Event added");

                        InitRecycleViewFunct(dateSelected);

                        InitCalendar();

                        textViewTime.setText("00:00");
                        editTextEvent.setText("Add new event");
                    }else{
                        Toast.makeText(getActivity(), "Event NOT inserted, problem", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public ArrayList<Events> allEventsByDateList(String dateSelected, boolean check){
        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = dbHelper.getAllEventsByCheck(0);

        if(res.getCount() == 0){
            makeMyToast("Error, No events found");
        }
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

        if(res.getCount() == 0){
            makeMyToast("Error, No events found");
        }
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
                Events event = new Events(id, res.getString(1),
                        res.getString(2), res.getString(3),
                        res.getString(4), checked );
                myEvents.add(event);
            }

        }

        return myEvents;
    }

    private void SetEventNotification(Events event){

        Intent intent = new Intent(getActivity().getBaseContext(), AlarmReceiver.class);
        intent.putExtra("event_id", event.getEVENT_ID());
        intent.putExtra("event_type", event.getEVENT_TYPE());
        intent.putExtra("event_content", event.getEVENT_CONTENT());
        intent.putExtra("event_date_time", event.getEVENT_DATE_TIME());

    }

    private void makeMyToast(String message){
            Toast.makeText(getActivity().getBaseContext(), "Message:  "+message, Toast.LENGTH_LONG).show();
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