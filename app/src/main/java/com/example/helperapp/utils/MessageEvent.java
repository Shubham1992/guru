package com.example.helperapp.utils;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MessageEvent {

    private AccessibilityEvent accessibilityEvent;
    private AccessibilityNodeInfo accessibilityNodeInfo;

    public AccessibilityEvent getEvent() {
        return accessibilityEvent;
    }

    public void setEvent(AccessibilityEvent name) {
        this.accessibilityEvent = name;
    }

    public AccessibilityNodeInfo getAccessibilityNodeInfo() {
        return accessibilityNodeInfo;
    }

    public void setAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        this.accessibilityNodeInfo = accessibilityNodeInfo;
    }
}