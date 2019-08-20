package com.uni.time_tracking;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Preferences {

    public static final String KEY_SIMULTANEOUS_ACTIVITIES = "simultaneous_activities";
    public static final String KEY_REMOVE_SHORT_ENTRIES = "remove_short_entries";
    public static final String KEY_DARK_MODE = "dark_mode";
    public static final String KEY_THIRD_PARTY = "third_party";
    public static final String KEY_DELETE_DATA = "delete_data";

    public static boolean isSimultaneousActivitiesAllowed(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_SIMULTANEOUS_ACTIVITIES, true);
    }

    public static boolean removeShortEntries(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_REMOVE_SHORT_ENTRIES, false);
    }

    public static boolean isDarkMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }
}
