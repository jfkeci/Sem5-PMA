package com.example.focusapp.Models;

public class Events {
    int EVENT_ID;
    String USER_ID, EVENT_TYPE, EVENT_CONTENT, EVENT_DATE_TIME;
    boolean CHECKED;

    public Events(int EVENT_ID, String USER_ID, String EVENT_TYPE, String EVENT_CONTENT, String EVENT_DATE_TIME, boolean CHECKED) {
        this.EVENT_ID = EVENT_ID;
        this.USER_ID = USER_ID;
        this.EVENT_TYPE = EVENT_TYPE;
        this.EVENT_CONTENT = EVENT_CONTENT;
        this.EVENT_DATE_TIME = EVENT_DATE_TIME;
        this.CHECKED = CHECKED;
    }

    public Events() {}

    public int getEVENT_ID() {
        return EVENT_ID;
    }

    public void setEVENT_ID(int EVENT_ID) {
        this.EVENT_ID = EVENT_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getEVENT_TYPE() {
        return EVENT_TYPE;
    }

    public void setEVENT_TYPE(String EVENT_TYPE) {
        this.EVENT_TYPE = EVENT_TYPE;
    }

    public String getEVENT_CONTENT() {
        return EVENT_CONTENT;
    }

    public void setEVENT_CONTENT(String EVENT_CONTENT) {
        this.EVENT_CONTENT = EVENT_CONTENT;
    }

    public String getEVENT_DATE_TIME() {
        return EVENT_DATE_TIME;
    }

    public void setEVENT_DATE_TIME(String EVENT_DATE_TIME) {
        this.EVENT_DATE_TIME = EVENT_DATE_TIME;
    }

    public boolean isCHECKED() {
        return CHECKED;
    }

    public void setCHECKED(boolean CHECKED) {
        this.CHECKED = CHECKED;
    }
}
