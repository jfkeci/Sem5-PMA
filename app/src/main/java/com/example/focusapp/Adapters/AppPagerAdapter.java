package com.example.focusapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.focusapp.Fragments.CalendarFragment;
import com.example.focusapp.Fragments.NotesFragment;
import com.example.focusapp.Fragments.TimerFragment;
import com.example.focusapp.Fragments.ToDoFragment;

public class AppPagerAdapter extends FragmentStateAdapter {

    public AppPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new ToDoFragment();
            case 1:
                return new CalendarFragment();
            case 2:
                return new TimerFragment();
            default:
                return new NotesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}