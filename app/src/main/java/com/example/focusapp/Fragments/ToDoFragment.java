package com.example.focusapp.Fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Canvas;
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
    public boolean deleteyn=true;
    public boolean updateyn=true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_to_do, container, false);

        recyclerView = v.findViewById(R.id.recyclerViewToDo);

        recyclerViewChecked = v.findViewById(R.id.recyclerViewToDoDone);

//        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_view);

        dbHelper = new MyDbHelper(getActivity().getBaseContext());

        eventsList = allEventsList(0);
        eventsCheckedList = allEventsList(1);

        if(eventsList.size()>=1){
            InitRecycleViewFunct(allEventsList(0));
        }
        if(eventsCheckedList.size()>0){
            InitRecycleViewChecked(eventsCheckedList);
        }



        return v;
    }

    public void InitRecycleView(ArrayList<Events> userEventsList){
        todoAdapter = new MyRecyclerAdapter(getActivity(), userEventsList, 0);
        recyclerView.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }
    public void InitRecycleViewChecked(ArrayList<Events> userEventsList){
        checkedAdapter = new MyRecyclerAdapter(getActivity(), userEventsList, 1);
        recyclerViewChecked.setAdapter(checkedAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewChecked.setLayoutManager(layoutManager);
    }

    public void InitRecycleViewFunct(ArrayList<Events> arrayListEvents){

        todoAdapter = new MyRecyclerAdapter(getActivity(), arrayListEvents, 0);
        recyclerView.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

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
                    arrayListEvents.remove(position);
                    todoAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, deletedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrayListEvents.add(position, deletedEvent);
                            todoAdapter.notifyItemInserted(position);
                            deleteyn=false;
                        }
                    }).show();

                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    if(deleteyn){
                                        dbHelper.deleteEvent(String.valueOf(deletedEvent.getEVENT_ID()));
                                        makeMyLog("event is deleted ", " -->this one: "+deletedEvent.getEVENT_ID());
                                        deletedEvent = null;
                                        deleteyn = true;
                                    }else{
                                        makeMyLog("event is NOT deleted ", " -->this one: "+deletedEvent.getEVENT_ID());
                                        deletedEvent=null;
                                        deleteyn = true;
                                    }
                                }
                            },
                            4000
                    );

                }
                if(direction == ItemTouchHelper.RIGHT){
                    deletedEvent = arrayListEvents.get(position);
                    arrayListEvents.remove(position);
                    todoAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, deletedEvent.getEVENT_CONTENT(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrayListEvents.add(position, deletedEvent);
                            todoAdapter.notifyItemInserted(position);
                            updateyn=false;
                        }
                    }).show();

                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    if(deleteyn){
                                        makeMyLog("event has been   ", "updated yesyesyes");
                                        updateyn = true;
                                    }else{
                                        updateyn = true;
                                    }
                                }
                            },
                            4000
                    );
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
}