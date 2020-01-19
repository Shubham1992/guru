package com.example.helperapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.helperapp.models.AppModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AppHelper {
    public static ArrayList<Object> featuredList = new ArrayList<>();
    public static ArrayList<Object> userAppList = new ArrayList<>();
    public static ArrayList<AppModel> selectedappModels =  new ArrayList<AppModel>();
    public static HashMap<String, Object> stringList = new HashMap<String, Object>();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
