package com.uni.time_tracking;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;

public class Time {

    private Time() {}

    public static DateTime getCurrentTime() {
        return new DateTime(System.currentTimeMillis(), getTimezone());
    }

    public static String getCurrentTimeString() {
        return toString(getCurrentTime());
    }

    public static String toString(DateTime dateTime) {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.toString(f);
    }

    public static String toHourString(DateTime dateTime) {
        DateTimeFormatter f = DateTimeFormat.forPattern("HH:mm:ss");
        return dateTime.toString(f);
    }

    public static DateTime fromString(String dateTimeString) {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return f.parseDateTime(dateTimeString);
    }

    public static DateTime difference(DateTime d1, DateTime d2) {
        return new DateTime(d1.getMillis() - d2.getMillis(), getTimezone());
    }

    public static DateTime differenceToNow(DateTime dateTime) {
        return difference(getCurrentTime(), dateTime);
    }

    public static String differenceToNowString(DateTime dateTime) {
        return toString(difference(getCurrentTime(), dateTime));
    }

    public static DateTimeZone getTimezone() {
        return DateTimeZone.forTimeZone(TimeZone.getDefault());
    }
}
