package com.example.focusapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.focusapp.Models.AppModel;
import com.example.focusapp.Models.Events;
import com.example.focusapp.Models.Notes;
import com.example.focusapp.Models.Session;
import com.example.focusapp.Models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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


    //table study session
    public static final String TABLE_SESSIONS = "SESSIONS_TABLE";
    public static final String COL41 = "SESSION_ID";
    public static final String COL42 = "USER_ID";
    public static final String COL43 = "SESSION_LENGTH";
    public static final String COL44 = "SESSION_DATE";
    public static final String COL45 = "SESSION_TIME";
    public static final String COL46 = "SESSION_POINTS";
    public static final String COL47 = "SESSION_FINISHED";

    //table apps to block
    public static final String TABLE_APPS = "APPS_TABLE";
    public static final String COL51 = "APP_NAME";
    public static final String COL52 = "APP_STATUS";
    public static final String COL53 = "PACKAGE_NAME";


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
                "USER_ID TEXT, SESSION_LENGTH TEXT, SESSION_DATE TEXT, SESSION_TIME TEXT," +
                "SESSION_POINTS TEXT, SESSION_FINISHED INTEGER DEFAULT 0)");

        db.execSQL("create table " + TABLE_APPS + "(APP_NAME TEXT, " + "APP_STATUS INTEGER, " + "PACKAGE_NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS);
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

    public boolean addNewEventWithId(Events event){
        int check = 1;

        SQLiteDatabase db = this.getWritableDatabase();

        if(event.isCHECKED()){
            check = 1;
        }else{
            check = 0;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL11, event.getEVENT_ID());
        contentValues.put(COL12, event.getUSER_ID());
        contentValues.put(COL13, event.getEVENT_TYPE());
        contentValues.put(COL14, event.getEVENT_CONTENT());
        contentValues.put(COL15, event.getEVENT_DATE_TIME());
        contentValues.put(COL16, check);

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
    public Cursor getAllEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
        return res;
    }

    public Cursor readEventId(String type, String content, String datetime){
        SQLiteDatabase db = this.getWritableDatabase();
        String [] Projections = {COL11};
        String Selection = COL13 + "=? and " + COL14 + "=? and " + COL15 + "=?";
        String [] SelectionArgs = {type, content, datetime};

        return db.query(TABLE_EVENTS, Projections, Selection, SelectionArgs, null, null, null);
    }

    public int getNewEventId(){
        String selectQuery= "SELECT * FROM " + TABLE_EVENTS +" ORDER BY "+ COL11 +" DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(selectQuery, null);
        int event_id = 0;
        if(res.moveToFirst())
            event_id  =  Integer.parseInt(res.getString( 0 ));
        res.close();
        return event_id;
    }
    public boolean eventSetChecked(String EVENT_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL11, EVENT_ID);
        contentValues.put(COL16, 1);
        db.update(TABLE_EVENTS, contentValues, "EVENT_ID = ?", new String[]{EVENT_ID});

        return true;
    }
    public boolean eventUncheck(String EVENT_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL11, EVENT_ID);
        contentValues.put(COL16, 0);
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

    public boolean addNewNote(String uid, String note_title, String note_content, String note_DateTime){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL32, uid);
        contentValues.put(COL33, note_title);
        contentValues.put(COL34, note_content);
        contentValues.put(COL35, note_DateTime);

        long result = db.insert(TABLE_NOTES, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }
    public boolean addNewNoteWithId(int nid, String uid, String note_title, String note_content, String note_DateTime){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL31, nid);
        contentValues.put(COL32, uid);
        contentValues.put(COL33, note_title);
        contentValues.put(COL34, note_content);
        contentValues.put(COL35, note_DateTime);

        long result = db.insert(TABLE_NOTES, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllNotes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);
        return res;
    }

    public boolean updateNote(Notes note){
        String nid = String.valueOf(note.getNOTE_ID());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL33, note.getNOTE_TITLE());
        contentValues.put(COL34, note.getNOTE_CONTENT());
        contentValues.put(COL35, note.getNOTE_DATE_TIME());
        db.update(TABLE_NOTES, contentValues, "NOTE_ID = ?", new String[]{nid});

        return true;
    }

    public Integer deleteNote(String NOTE_ID){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NOTES, "NOTE_ID = ?", new String[]{NOTE_ID});
    }

    public void addNotesList(ArrayList<Notes> notesList){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NOTES);

        for (Notes note: notesList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL31, note.getNOTE_ID());
            contentValues.put(COL32, note.getUSER_ID());
            contentValues.put(COL33, note.getNOTE_TITLE());
            contentValues.put(COL34, note.getNOTE_CONTENT());
            contentValues.put(COL35, note.getNOTE_DATE_TIME());

            long result = db.insert(TABLE_NOTES, null, contentValues);
        }
    }

    public Cursor getAllSessions(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_SESSIONS, null);
        return res;
    }

    public Cursor getAllApps(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_APPS, null);
        return res;
    }

    public boolean addNewApp(AppModel app){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL51, app.getAppname());
        contentValues.put(COL52, app.getStatus());
        contentValues.put(COL53, app.getPackagename());

        long result = db.insert(TABLE_APPS, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }
    public Integer deleteApp(String app_name){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_APPS, "APP_NAME = ?", new String[]{app_name});
    }

    public boolean addNewSession(Session session){

        SQLiteDatabase db = this.getWritableDatabase();

        int finished = 1;

        if(session.isSESSION_FINISHED()){
            finished = 1;
        }else{
            finished = 0;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL42, session.getUSER_ID());
        contentValues.put(COL43, session.getSESSION_LENGTH());
        contentValues.put(COL44, session.getSESSION_DATE());
        contentValues.put(COL45, session.getSESSION_TIME());
        contentValues.put(COL46, session.getSESSION_POINTS());
        contentValues.put(COL47, finished);

        long result = db.insert(TABLE_SESSIONS, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }
}
