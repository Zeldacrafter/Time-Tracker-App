package com.uni.time_tracking;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
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

    public static DateTime fromLong(long dateLong) {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        return f.parseDateTime(dateLong+"");
    }

    public static long toLong(@NonNull DateTime dateTime) {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        return Long.parseLong(dateTime.toString(f));
    }

    public static String differenceToNowString(DateTime dateTime) {
        return toString(difference(getCurrentTime(), dateTime));
    }

    private static Period difference(DateTime d1, DateTime d2) {
        return new Period(d2, d1);
    }

    private static String toString(Period p) {
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

    public static DateTimeZone getTimezone() {
        return DateTimeZone.forTimeZone(TimeZone.getDefault());
    }

}
