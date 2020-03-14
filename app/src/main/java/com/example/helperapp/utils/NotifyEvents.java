package com.example.helperapp.utils;

public class NotifyEvents {
    private String eventName = "";
    private String extraData = "";

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public NotifyEvents(String str) {
        eventName = str;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
