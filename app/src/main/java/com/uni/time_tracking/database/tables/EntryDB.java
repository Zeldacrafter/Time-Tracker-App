package com.uni.time_tracking.database.tables;

import android.provider.BaseColumns;

import org.joda.time.LocalDateTime;

public class EntryDB {

    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int activityID;

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " ( " +
                    FeedEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                    FeedEntry.COLUMN_START + " TEXT, " +
                    FeedEntry.COLUMN_END + " TEXT, " +
                    FeedEntry.COLUMN_ACTIVITY_ID + " INTEGER, " +
                    "FOREIGN KEY(" + FeedEntry.COLUMN_ACTIVITY_ID + ") REFERENCES " + ActivityDB.FeedEntry.TABLE_NAME + "(" + ActivityDB.FeedEntry._ID + ")" +
                    ")";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS "
                    + FeedEntry.TABLE_NAME;

    //TODO: Pass LocalDateTime aswell.
    public EntryDB(int id, LocalDateTime start, LocalDateTime end, int activityID) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.activityID = activityID;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public int getActivityID() {
        return activityID;
    }


    public static final class FeedEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "Entry";

        //Table columns
        public static final String COLUMN_START = "Start";
        public static final String COLUMN_END = "End";
        public static final String COLUMN_ACTIVITY_ID = "Activity_ID";
    }
}
