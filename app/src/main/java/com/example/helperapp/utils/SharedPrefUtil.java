package com.example.helperapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefUtil {

    public static void savePref(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("GuruPrefs", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static String getPref(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("GuruPrefs", MODE_PRIVATE);

        return prefs.getString(key, "");
    }

    public static SharedPreferences getInstance(Context context) {

        SharedPreferences prefs = context.getSharedPreferences("GuruPrefs", MODE_PRIVATE);
        return prefs;
    }
}
