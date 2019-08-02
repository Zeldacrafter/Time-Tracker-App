package com.uni.time_tracking;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    /**
     * Private constructor to make sure this class cannot be instantiated.
     */
    private Utils(){}

    /**
     * Replaces java-asserts because they don't work on android.
     * Throws AssertionError in case the condition does not hold true.
     * @param condition Condition that must hold true.
     * @param message Message in case the above condition does not hold true.
     */
    public static void _assert(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Replaces java-asserts because they don't work on android.
     * Throws AssertionError in case the condition does not hold true.
     * @param condition Condition that must hold true.
     */
    public static void _assert(boolean condition) {
        _assert(condition, "");
    }

    /**
     * Show a toast with duration {@link Toast#LENGTH_SHORT}.
     * @param message Toast message.
     * @param context Context.
     */
    public static void showToast(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a toast.
     * @param message Toast message.
     * @param context Context.
     * @param duration duration to show. Must be {@link Toast#LENGTH_SHORT} pr {@link Toast#LENGTH_LONG}.
     */
    public static void showToast(String message, Context context, int duration){
        Toast.makeText(context, message, duration).show();
    }

    /**
     * Converts a color to its hexadecimal representation.
     * @param color The color.
     * @return The hexadecimal representation as String of format '#RRGGBB'
     */
    public static String colorIntToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
