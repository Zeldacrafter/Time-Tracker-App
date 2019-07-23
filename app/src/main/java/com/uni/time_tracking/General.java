package com.uni.time_tracking;

import android.content.Context;
import android.widget.Toast;

public class General {

    private General(){}

    public static void showToast(String msg, Context context){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String msg, Context context, int duration){
        Toast.makeText(context, msg, duration).show();
    }

    public static String colorIntToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
