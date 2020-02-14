package com.example.helperapp.utils;

public class NotifyEvents {
    private String eventName = "";

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public NotifyEvents(String str) {
        eventName = str;
    }
}
