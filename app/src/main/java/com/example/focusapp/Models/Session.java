package com.example.focusapp.Models;

public class Session {
    int SESSION_ID;
    String USER_ID, SESSION_LENGTH, SESSION_DATE, SESSION_TIME, SESSION_POINTS;
    boolean SESSION_FINISHED;

    public Session() {}

    public Session(int SESSION_ID, String USER_ID, String SESSION_LENGTH, String SESSION_DATE, String SESSION_TIME, String SESSION_POINTS, boolean SESSION_FINISHED) {
        this.SESSION_ID = SESSION_ID;
        this.USER_ID = USER_ID;
        this.SESSION_LENGTH = SESSION_LENGTH;
        this.SESSION_DATE = SESSION_DATE;
        this.SESSION_TIME = SESSION_TIME;
        this.SESSION_POINTS = SESSION_POINTS;
        this.SESSION_FINISHED = SESSION_FINISHED;
    }

    public int getSESSION_ID() {
        return SESSION_ID;
    }

    public void setSESSION_ID(int SESSION_ID) {
        this.SESSION_ID = SESSION_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getSESSION_LENGTH() {
        return SESSION_LENGTH;
    }

    public void setSESSION_LENGTH(String SESSION_LENGTH) {
        this.SESSION_LENGTH = SESSION_LENGTH;
    }

    public String getSESSION_DATE() {
        return SESSION_DATE;
    }

    public void setSESSION_DATE(String SESSION_DATE) {
        this.SESSION_DATE = SESSION_DATE;
    }

    public String getSESSION_TIME() {
        return SESSION_TIME;
    }

    public void setSESSION_TIME(String SESSION_TIME) {
        this.SESSION_TIME = SESSION_TIME;
    }

    public String getSESSION_POINTS() {
        return SESSION_POINTS;
    }

    public void setSESSION_POINTS(String SESSION_POINTS) {
        this.SESSION_POINTS = SESSION_POINTS;
    }

    public boolean isSESSION_FINISHED() {
        return SESSION_FINISHED;
    }

    public void setSESSION_FINISHED(boolean SESSION_FINISHED) {
        this.SESSION_FINISHED = SESSION_FINISHED;
    }
}
