package com.uni.time_tracking.database.tables;

import android.provider.BaseColumns;

public class TimeDB {

    private int id;
    private long start;
    private long end;
    private int activityID;

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " ( " +
                    FeedEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                    FeedEntry.COLUMN_START + " INTEGER, " +
                    FeedEntry.COLUMN_END + " INTEGER, " +
                    FeedEntry.COLUMN_ACTIVITY_ID + " INTEGER, " +
                    "FOREIGN KEY(" + FeedEntry.COLUMN_ACTIVITY_ID + ") REFERENCES " + ActivityDB.FeedEntry.TABLE_NAME + "(" + ActivityDB.FeedEntry._ID + ")" +
                    ")";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS "
                    + FeedEntry.TABLE_NAME;

    public TimeDB(int id, long start, long end, int activityID) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.activityID = activityID;
    }

    public int getId() {
        return id;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public int getActivityID() {
        return activityID;
    }


    public static final class FeedEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "Time";

        //Table columns
        public static final String COLUMN_START = "Start";
        public static final String COLUMN_END = "End";
        public static final String COLUMN_ACTIVITY_ID = "Activity_ID";
    }
}
