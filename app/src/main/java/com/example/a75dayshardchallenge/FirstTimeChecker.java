package com.example.a75dayshardchallenge;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeChecker {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String FIRST_TIME_KEY = "isFirstTime";

    public static boolean isFirstTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(FIRST_TIME_KEY, true);
    }

    public static void setFirstTime(Context context, boolean isFirstTime) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(FIRST_TIME_KEY, isFirstTime).apply();
    }
}
