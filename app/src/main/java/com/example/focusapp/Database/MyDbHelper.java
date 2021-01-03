package com.example.focusapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.focusapp.Models.Events;
import com.example.focusapp.Models.User;

public class MyDbHelper extends SQLiteOpenHelper {

    //database
    public static final String DATABASE_NAME = "focus.db";
    //table events
    public static final String TABLE_EVENTS = "EVENTS_TABLE";
    public static final String COL11 = "EVENT_ID";
    public static final String COL12 = "USER_ID";
    public static final String COL13 = "EVENT_TYPE";
    public static final String COL14 = "EVENT_CONTENT";
    public static final String COL15 = "EVENT_DATE_TIME";
    public static final String COL16 = "CHECKED";

    //table user
    public static final String TABLE_USER = "USER_TABLE";
    public static final String COL21 = "USER_ID";
    public static final String COL22 = "USERNAME";
    public static final String COL23 = "EMAIL";

    //table notes
    public static final String TABLE_NOTES = "NOTES_TABLE";
    public static final String COL31 = "NOTE_ID";
    public static final String COL32 = "USER_ID";
    public static final String COL33 = "NOTE_TITLE";
    public static final String COL34 = "NOTE_CONTENT";
    public static final String COL35 = "NOTE_DATE_TIME";

    //table session
    public static final String TABLE_SESSIONS = "SESSIONS_TABLE";
    public static final String COL41 = "SESSION_ID";
    public static final String COL42 = "USER_ID";
    public static final String COL43 = "SESSION_LENGTH";
    public static final String COL44 = "SESSION_DATE_TIME";
    public static final String COL45 = "SESSION_POINTS";
    public static final String COL46 = "SESSION_FINISHED";

    public MyDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_EVENTS +
                "(EVENT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER_ID TEXT, EVENT_TYPE TEXT, EVENT_CONTENT TEXT, " +
                "EVENT_DATE_TIME TEXT, CHECKED INTEGER DEFAULT 0)");

        db.execSQL("create table " + TABLE_USER +
                "(USER_ID TEXT PRIMARY KEY, " +
                "USERNAME TEXT, EMAIL TEXT)");

        db.execSQL("create table " + TABLE_NOTES +
                "(NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER_ID TEXT, NOTE_TITLE TEXT, NOTE_CONTENT TEXT, " +
                "NOTE_DATE_TIME TEXT)");

        db.execSQL("create table " + TABLE_SESSIONS +
                "(SESSION_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER_ID TEXT, SESSION_LENGTH TEXT, SESSION_DATE_TIME TEXT, " +
                "SESSION_POINTS TEXT, SESSION_FINISHED INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        onCreate(db);
    }

    public boolean addNewEvent(String user_id, String event_type, String event_content, String date_time){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL12, user_id);
        contentValues.put(COL13, event_type);
        contentValues.put(COL14, event_content);
        contentValues.put(COL15, date_time);
        contentValues.put(COL16, 0);

        long result = db.insert(TABLE_EVENTS, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllEventsByCheck(int check){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_EVENTS+" WHERE CHECKED='" + check + "'", null);
        return res;
    }
    public boolean updateEventStateToChecked(String EVENT_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL11, EVENT_ID);
        contentValues.put(COL16, 1);
        db.update(TABLE_EVENTS, contentValues, "EVENT_ID = ?", new String[]{EVENT_ID});

        return true;
    }
    public boolean updateEventContent(String EVENT_ID, String EVENT_CONTENT){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL11, EVENT_ID);
        contentValues.put(COL14, EVENT_CONTENT);
        db.update(TABLE_EVENTS, contentValues, "EVENT_ID = ?", new String[]{EVENT_ID});

        return true;
    }

    public Integer deleteEvent(String EVENT_ID){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_EVENTS, "EVENT_ID = ?", new String[]{EVENT_ID});
    }

    public boolean setCurrentUser(String id, String username, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_USER);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL21, id);
        contentValues.put(COL22, username);
        contentValues.put(COL23, email);

        long isSet = db.insert(TABLE_USER, null, contentValues);

        if(isSet == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean userIsSet(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        int c = 0;
        while(res.moveToNext()){
            c=1;
        }
        if(c==1){
            return true;
        }else{
            return false;
        }
    }

    public User getUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        User user = new User();
        while(res.moveToNext()){
            user.setUser_id(res.getString(0));
            user.setUsername(res.getString(1));
            user.setEmail(res.getString(2));
        }
        return user;
    }
}
