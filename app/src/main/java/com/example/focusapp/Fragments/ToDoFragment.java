package com.example.focusapp.Fragments;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.example.focusapp.Adapters.MyRecyclerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Events;
import com.example.focusapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ToDoFragment extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerView recyclerViewChecked;

    public MyDbHelper dbHelper;
    public SwipeRefreshLayout swipeRefreshLayout;
    public MyRecyclerAdapter todoAdapter;
    public MyRecyclerAdapter checkedAdapter;

    public ArrayList<Events> eventsList = new ArrayList<>();
    public ArrayList<Events> eventsCheckedList = new ArrayList<>();


    public Events deletedEvent = new Events();
    public Events checkedEvent = new Events();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_to_do, container, false);

        createNotificationChannel();

        recyclerView = v.findViewById(R.id.recyclerViewToDo);

        recyclerViewChecked = v.findViewById(R.id.recyclerViewToDoDone);

        dbHelper = new MyDbHelper(getActivity().getBaseContext());

        RefreshToDoHere();

        return v;
    }


    public void RefreshToDoHere(){
        InitRecycleViewFunct();
        InitRecycleViewChecked();
    }

    public void InitRecycleViewChecked(){

        eventsCheckedList.clear();

        eventsCheckedList = allEventsList(1);

        checkedAdapter = new MyRecyclerAdapter(getActivity(), eventsCheckedList, 1);
        recyclerViewChecked.setAdapter(checkedAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewChecked.setLayoutManager(layoutManager);

        checkedAdapter.notifyDataSetChanged();
    }

    public void InitRecycleViewFunct(){

        eventsList.clear();

        eventsList = allEventsList(0);

        todoAdapter = new MyRecyclerAdapter(getActivity(), eventsList, 0);
        recyclerView.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        todoAdapter.notifyDataSetChanged();

        if(eventsList.isEmpty()){
            makeMyToast("Nothing to do");
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
                    deletedEvent = eventsList.get(position);

                    String event_id = String.valueOf(deletedEvent.getEVENT_ID());

                    int deleted = dbHelper.deleteEvent(event_id);

                    if(deleted == 1){
                        eventsList.remove(deletedEvent);
                        todoAdapter.notifyItemRemoved(position);
                    }

                    Snackbar.make(recyclerView, deletedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            boolean undone = dbHelper.addNewEventWithId(deletedEvent);

                            if(undone){
                                eventsList.add(position, deletedEvent);
                                todoAdapter.notifyItemInserted(position);
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
                        InitRecycleViewChecked();
                    }else{
                        makeMyToast("Failed");
                    }
                    Snackbar.make(recyclerView, checkedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean checked = dbHelper.eventUncheck(String.valueOf(checkedEvent.getEVENT_ID()));
                            if(checked){
                                makeMyToast("Unchecked");
                                eventsList.add(position, checkedEvent);
                                todoAdapter.notifyItemInserted(position);
                                InitRecycleViewChecked();
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

    public ArrayList<Events> allEventsList(int checkNum){
        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = dbHelper.getAllEventsByCheck(checkNum);

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
}