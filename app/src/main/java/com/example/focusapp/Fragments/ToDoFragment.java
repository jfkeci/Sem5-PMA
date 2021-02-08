package com.example.focusapp.Fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focusapp.Adapters.MyRecyclerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Events;
import com.example.focusapp.R;
import com.example.focusapp.Recievers.AlarmReceiver;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ToDoFragment extends Fragment {


    public RecyclerView recyclerViewToDo;
    public RecyclerView recyclerViewChecked;

    public ConstraintLayout constraintLayoutToDo, constraintLayoutChecked;
    public TextView tw1, tw2;

    public long atTime;

    public SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");

    public MyDbHelper dbHelper;
    public MyRecyclerAdapter todoAdapter;
    public MyRecyclerAdapter checkedAdapter;

    public ArrayList<Events> eventsList = new ArrayList<>();
    public ArrayList<Events> eventsCheckedList = new ArrayList<>();

    public Events deletedEvent = new Events();
    public Events checkedEvent = new Events();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View todoView = inflater.inflate(R.layout.fragment_to_do, container, false);

        createNotificationChannel();

        recyclerViewToDo = todoView.findViewById(R.id.recyclerViewToDo);
        recyclerViewChecked = todoView.findViewById(R.id.recyclerViewToDoDone);

        constraintLayoutToDo = todoView.findViewById(R.id.constraintLayoutToDo);
        constraintLayoutChecked = todoView.findViewById(R.id.constraintLayoutChecked);

        ConstraintLayout.LayoutParams lpTodo = (ConstraintLayout.LayoutParams) constraintLayoutToDo.getLayoutParams();
        ConstraintLayout.LayoutParams lpChecked = (ConstraintLayout.LayoutParams) constraintLayoutChecked.getLayoutParams();


        tw1 = todoView.findViewById(R.id.tw1);
        tw2 = todoView.findViewById(R.id.tw2);

        tw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lpChecked.height = 550;
                lpTodo.height = 1100;
                constraintLayoutToDo.setLayoutParams(lpTodo);
                constraintLayoutChecked.setLayoutParams(lpChecked);
            }
        });
        tw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lpChecked.height = 1100;
                lpTodo.height = 550;
                constraintLayoutToDo.setLayoutParams(lpTodo);
                constraintLayoutChecked.setLayoutParams(lpChecked);
            }
        });

        eventsCheckedList = allEventsList(1);
        checkedAdapter = new MyRecyclerAdapter(getActivity(), eventsCheckedList, 1);
        recyclerViewChecked.setAdapter(checkedAdapter);
        RecyclerView.LayoutManager checkedLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewChecked.setLayoutManager(checkedLayoutManager);

        dbHelper = new MyDbHelper(getActivity());

        eventsList = allEventsList(0);
        todoAdapter = new MyRecyclerAdapter(getActivity(), eventsList, 0);
        recyclerViewToDo.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewToDo.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if(direction == ItemTouchHelper.LEFT){
                    deletedEvent = eventsList.get(position);

                    String event_id = String.valueOf(deletedEvent.getEVENT_ID());

                    int deleted = dbHelper.deleteEvent(event_id);

                    if(deleted == 1){
                        eventsList.remove(deletedEvent);
                        todoAdapter.notifyItemRemoved(position);
                        RemoveEventNotification(deletedEvent.getEVENT_ID());
                    }

                    Snackbar.make(recyclerViewToDo, deletedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            boolean undone = dbHelper.addNewEventWithId(deletedEvent);

                            if(undone){
                                eventsList.add(position, deletedEvent);
                                todoAdapter.notifyItemInserted(position);
                                SetEventNotification(deletedEvent);
                            }else{
                                makeMyToast("Something went wrong!");
                            }
                        }
                    }).show();

                }
                if(direction == ItemTouchHelper.RIGHT){
                    checkedEvent = eventsList.get(position);

                    eventsList.remove(checkedEvent);
                    todoAdapter.notifyItemRemoved(position);

                    boolean checked = dbHelper.eventSetChecked(String.valueOf(checkedEvent.getEVENT_ID()));

                    if(checked){
                        makeMyToast("Awesome!");
                        eventsCheckedList.add(checkedEvent);
                        checkedAdapter.notifyDataSetChanged();
                        RemoveEventNotification(checkedEvent.getEVENT_ID());
                    }else{
                        makeMyToast("Something went wrong!");
                    }
                    Snackbar.make(recyclerViewToDo, checkedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean checked = dbHelper.eventUncheck(String.valueOf(checkedEvent.getEVENT_ID()));
                            if(checked){
                                eventsList.add(position, checkedEvent);
                                todoAdapter.notifyItemInserted(position);
                                eventsCheckedList.remove(checkedEvent);
                                checkedAdapter.notifyDataSetChanged();
                                SetEventNotification(checkedEvent);
                            }else{
                                makeMyToast("Something went wrong!");
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
        itemTouchHelper.attachToRecyclerView(recyclerViewToDo);

        return todoView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        eventsList = allEventsList(0);
        eventsCheckedList = allEventsList(1);
        todoAdapter.setData(eventsList);
        checkedAdapter.setData(eventsCheckedList);
    }

    public ArrayList<Events> allEventsList(int checkNum){
        MyDbHelper helper = new MyDbHelper(getActivity());

        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = helper.getAllEventsByCheck(checkNum);

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

            if(checkNum == 1){
                if(event.isCHECKED()){
                    myEvents.add(event);
                }
            }if(checkNum == 0){
                if(!event.isCHECKED()){
                    myEvents.add(event);
                }
            }
        }
        return myEvents;
    }

    private void SetEventNotification(Events event){

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

    public void createNotificationChannel(){
        String channelId = "todo_fragment_notification_channel";
        CharSequence channelName = "todo_fragment_channel";
        String description = "todo_fragment_notifications";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void makeMyToast(String message){
        Toast.makeText(getActivity(), "Message:  "+message, Toast.LENGTH_LONG).show();
    }
}