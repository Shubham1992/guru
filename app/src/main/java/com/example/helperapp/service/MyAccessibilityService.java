package com.example.helperapp.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.helperapp.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class MyAccessibilityService extends AccessibilityService {
    private AccessibilityServiceInfo info;

    public MyAccessibilityService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            if (event.getSource() != null && event.getSource().getViewIdResourceName() != null
                    && event.getSource().getViewIdResourceName().equals("com.ubercab.driver:id/indicator_outline"))
                return;
        }
        //Log.e("event name", event.toString());


        final AccessibilityNodeInfo textNodeInfo = getRootInActiveWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (event.getSource() != null) {
                if (event.getSource().getViewIdResourceName() != null)
                    Log.e("view id", event.getSource().getViewIdResourceName());
                Rect rect2 = new Rect();
                event.getSource().getBoundsInScreen(rect2);
                Log.e("view bounds", "" + rect2.top + " " + rect2.left);
            }

        }

        if (textNodeInfo != null) {
            Rect rect = new Rect();

            textNodeInfo.getBoundsInScreen(rect);

            Log.e(event.getClassName() == null ? "" : event.getClassName().toString(), "The TextView Node: " + rect.left + " " + rect.top + "  " + event);
        }

        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setEvent(event);
        messageEvent.setAccessibilityNodeInfo(textNodeInfo);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void onInterrupt() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


}


