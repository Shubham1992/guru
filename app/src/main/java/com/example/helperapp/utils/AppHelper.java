package com.example.helperapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.helperapp.models.AppModel;

import java.util.ArrayList;
import java.util.HashMap;

public class AppHelper {
    public static ArrayList<Object> featuredList = new ArrayList<>();
    public static ArrayList<Object> userAppList = new ArrayList<>();
    public static ArrayList<AppModel> selectedappModels = new ArrayList<AppModel>();
    public static ArrayList<Object> AllappModels = new ArrayList<Object>();
    public static HashMap<String, Object> stringList = new HashMap<String, Object>();
    public static String[] supportedAppList = {"in.swiggy.android", "com.ubercab", "com.olacabs.customer",
            "com.application.zomato", "com.google.android.gm", "net.one97.paytm"};


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isSupportedPackage(String packageName) {
        for (int i = 0; i < supportedAppList.length; i++) {
            if (packageName.equalsIgnoreCase(supportedAppList[i])) {
                return true;
            }
        }
        return false;
    }
}
