package com.example.a75dayshardchallenge.Model;

import android.content.Context;
import android.content.SharedPreferences;

public class TimerStateManager {
    private static final String PREFS_NAME = "TimerPrefs";
    private static final String PREF_TIME_LEFT = "timeLeftInMillis";
    private static final String PREF_TIMER_RUNNING = "timerRunning";
    public static final long DEFAULT_TIME = 1500000; // Default time, 20 minutes

    public static void saveTimerState(Context context, long timeLeftInMillis, boolean timerRunning) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREF_TIME_LEFT, timeLeftInMillis);
        editor.putBoolean(PREF_TIMER_RUNNING, timerRunning);
        editor.apply();
    }

    public static long getSavedTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(PREF_TIME_LEFT, DEFAULT_TIME);
    }

    public static boolean isTimerRunning(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_TIMER_RUNNING, false);
    }
}

