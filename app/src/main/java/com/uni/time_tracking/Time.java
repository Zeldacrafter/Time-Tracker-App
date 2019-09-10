package com.uni.time_tracking;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Calendar;
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

    public static DateTime fromCalendar(Calendar c) {
        return new DateTime(c);
    }

    public static long toLong(DateTime dateTime) {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        return Long.parseLong(dateTime.toString(f));
    }

    public static String differenceToNowString(DateTime dateTime) {
        return toString(differenceToNow(dateTime));
    }

    public static Period differenceToNow(DateTime dateTime) {
        return difference(getCurrentTime(), dateTime);
    }

    public static long getDifferenceLong(Period p) {
        String str = toString(p).replace(":", ""); // HH:mm:ss
        return Long.parseLong(str);
    }

    /**
     * Compares if the length of the first period is longer than the second.
     * @param p1 The first period.
     * @param p2 The second period.
     * @return returns {@code true} if the first period is longer or just as long as the first one
     *         and {@code false} otherwise.
     */
    public static boolean isFirstPeriodLonger(Period p1, Period p2) {
        if (p1.getYears() > p2.getYears()) return true;
        if (p1.getYears() < p2.getYears()) return false;

        if (p1.getMonths() > p2.getMonths()) return true;
        if (p1.getMonths() < p2.getMonths()) return false;

        if (p1.getDays() > p2.getDays()) return true;
        if (p1.getDays() < p2.getDays()) return false;

        if (p1.getHours() > p2.getHours()) return true;
        if (p1.getHours() < p2.getHours()) return false;

        if (p1.getYears() > p2.getYears()) return true;
        if (p1.getYears() < p2.getYears()) return false;
        
        if (p1.getMinutes() > p2.getMinutes()) return true;
        if (p1.getMinutes() < p2.getMinutes()) return false;


        if (p1.getSeconds() > p2.getSeconds()) return true;
        if (p1.getSeconds() < p2.getSeconds()) return false;

        if (p1.getMillis() > p2.getMillis()) return true;
        return p1.getMillis() == p2.getMillis();
    }

    private static Period difference(DateTime d1, DateTime d2) {
        return new Period(d2, d1);
    }

    public static String toString(DateTime dateTime) {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.toString(f);
    }

    public static String toDateString(DateTime dateTime) {
        //TODO: Date format in settings. DD-MM-YYYY or MM-DD-YYYY ?
        DateTimeFormatter f = DateTimeFormat.forPattern("dd.MM.yyyy");
        return dateTime.toString(f);
    }

    public static String toTimeString(DateTime dateTime) {
        //Date format?
        DateTimeFormatter f = DateTimeFormat.forPattern("HH:mm");
        return dateTime.toString(f);
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

    private static DateTimeZone getTimezone() {
        return DateTimeZone.forTimeZone(TimeZone.getDefault());
    }
}
