package com.example.focusapp.Models;

public class Notes {
    int NOTE_ID;
    String USER_ID, NOTE_TITLE, NOTE_CONTENT, NOTE_DATE_TIME;

    public Notes() { }

    public Notes(int NOTE_ID, String USER_ID, String NOTE_TITLE, String NOTE_CONTENT, String NOTE_DATE_TIME) {
        this.NOTE_ID = NOTE_ID;
        this.USER_ID = USER_ID;
        this.NOTE_TITLE = NOTE_TITLE;
        this.NOTE_CONTENT = NOTE_CONTENT;
        this.NOTE_DATE_TIME = NOTE_DATE_TIME;
    }

    public int getNOTE_ID() {
        return NOTE_ID;
    }

    public void setNOTE_ID(int NOTE_ID) {
        this.NOTE_ID = NOTE_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getNOTE_TITLE() {
        return NOTE_TITLE;
    }

    public void setNOTE_TITLE(String NOTE_TITLE) {
        this.NOTE_TITLE = NOTE_TITLE;
    }

    public String getNOTE_CONTENT() {
        return NOTE_CONTENT;
    }

    public void setNOTE_CONTENT(String NOTE_CONTENT) {
        this.NOTE_CONTENT = NOTE_CONTENT;
    }

    public String getNOTE_DATE_TIME() {
        return NOTE_DATE_TIME;
    }

    public void setNOTE_DATE_TIME(String NOTE_DATE_TIME) {
        this.NOTE_DATE_TIME = NOTE_DATE_TIME;
    }
}
