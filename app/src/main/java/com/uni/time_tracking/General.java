package com.uni.time_tracking;

import android.content.Context;
import android.widget.Toast;

public class General {

    public static void showToast(String msg, Context context){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String msg, Context context, int duration){
        Toast.makeText(context, msg, duration).show();
    }

    public static String millisToTimeString(long millis) {
        int seconds = (int) (millis / 1000) % 60 ;
        String secondsString = (seconds < 10 ? "0" : "") + seconds;
        int minutes = (int) ((millis / (1000*60)) % 60);
        String minutesString = (minutes < 10 ? "0" : "") + minutes;
        int hours   = (int) ((millis / (1000*60*60)) % 24);
        return hours + ":" + minutesString + "." + secondsString;
    }
}
