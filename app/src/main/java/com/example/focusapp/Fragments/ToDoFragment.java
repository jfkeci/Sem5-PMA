package com.example.focusapp.Fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.focusapp.Adapters.MyRecyclerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Models.Events;
import com.example.focusapp.R;

import java.util.ArrayList;

public class ToDoFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView recyclerViewChecked;

    MyDbHelper dbHelper;

    ArrayList<Events> eventsList = new ArrayList<>();
    ArrayList<Events> eventsCheckedList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_to_do, container, false);

        recyclerView = v.findViewById(R.id.recyclerViewToDo);

        recyclerViewChecked = v.findViewById(R.id.recyclerViewToDoDone);

        dbHelper = new MyDbHelper(getActivity().getBaseContext());

        eventsList = allEventsList(0);
        eventsCheckedList = allEventsList(1);
        InitRecycleView(eventsList, 1);
        InitRecycleViewChecked(eventsCheckedList, 1);


        return v;
    }

    public void InitRecycleView(ArrayList<Events> userEventsList, int adapter_case){
        MyRecyclerAdapter todoAdapter = new MyRecyclerAdapter(getActivity(), userEventsList, 0);
        recyclerView.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }
    public void InitRecycleViewChecked(ArrayList<Events> userEventsList, int adapter_case){
        MyRecyclerAdapter todoAdapter = new MyRecyclerAdapter(getActivity(), userEventsList, 0);
        recyclerViewChecked.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewChecked.setLayoutManager(layoutManager);
    }

    public ArrayList<Events> allEventsList(int check){
        ArrayList<Events> myEvents = new ArrayList<>();

        myEvents.clear();

        Cursor res = dbHelper.getAllEventsByCheck(check);

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