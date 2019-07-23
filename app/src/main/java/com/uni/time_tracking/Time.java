package com.uni.time_tracking;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Time {

    private Time() {}

    public static LocalDateTime getCurrentTime() {
        return new LocalDateTime(System.currentTimeMillis());
    }

    public static String getCurrentTimeString() {
        return toString(getCurrentTime());
    }

    public static String toString(LocalDateTime localDateTime) {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.toString(f);
    }

    public static String toHourString(LocalDateTime localDateTime) {
        DateTimeFormatter f = DateTimeFormat.forPattern("HH:mm:ss");
        return localDateTime.toString(f);
    }

    public static LocalDateTime fromString(String dateTimeString) {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return f.parseLocalDateTime(dateTimeString);
    }

    public static LocalDateTime difference(LocalDateTime d1, LocalDateTime d2) {
        return new LocalDateTime(d1.toDateTime().getMillis() - d2.toDateTime().getMillis());
    }

    public static LocalDateTime differenceToNow(LocalDateTime dateTime) {
        return difference(getCurrentTime(), dateTime);
    }

    public static String differenceToNowString(LocalDateTime dateTime) {
        return toString(difference(getCurrentTime(), dateTime));
    }
}
