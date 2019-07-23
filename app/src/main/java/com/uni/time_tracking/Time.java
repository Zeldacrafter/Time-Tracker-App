package com.uni.time_tracking;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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

    public static Period difference(DateTime d1, DateTime d2) {
        return new Period(d2, d1);
    }

    public static Period differenceToNow(DateTime dateTime) {
        return difference(getCurrentTime(), dateTime);
    }

    public static String toString(Period p) {
        PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter();
        return minutesAndSeconds.print(p);
    }

    public static String differenceToNowString(DateTime dateTime) {
        return toString(difference(getCurrentTime(), dateTime));
    }

    public static DateTimeZone getTimezone() {
        return DateTimeZone.forTimeZone(TimeZone.getDefault());
    }
}
