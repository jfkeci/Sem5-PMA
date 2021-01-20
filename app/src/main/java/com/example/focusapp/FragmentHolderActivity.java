package com.example.focusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import com.example.focusapp.Adapters.AppPagerAdapter;
import com.example.focusapp.Database.MyDbHelper;
import com.example.focusapp.Fragments.CalendarFragment;
import com.example.focusapp.Fragments.ToDoFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FragmentHolderActivity extends AppCompatActivity {

    CalendarFragment calendarFragment;
    ToDoFragment toDoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        createNotificationChannel();

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new AppPagerAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch(position){
                    case 0: {
                        tab.setText("ToDo");
                        tab.setIcon(R.drawable.ic_baseline_check_24);
                        break;
                    }
                    case 1: {
                        tab.setText("Calendar");
                        tab.setIcon(R.drawable.ic_baseline_calendar_today_24);
                        break;
                    }
                    case 2: {
                        tab.setText("Timer");
                        tab.setIcon(R.drawable.ic_baseline_access_time_24);
                        break;
                    }
                    case 3: {
                        tab.setText("Notes");
                        tab.setIcon(R.drawable.ic_baseline_notes_24);
                        break;
                    }
                }
            }
        }
        );
        tabLayoutMediator.attach();
    }

    public void createNotificationChannel(){
        String channelId = "fragment_holder_activity_notification_channel";
        CharSequence channelName = "fragment_holder_activity_channel";
        String description = "fragment_holder_activity_notifications";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}