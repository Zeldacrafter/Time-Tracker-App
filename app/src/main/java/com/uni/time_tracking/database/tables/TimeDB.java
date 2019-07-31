package com.uni.time_tracking.database.tables;

import android.provider.BaseColumns;

import org.joda.time.DateTime;

public class TimeDB {

    /** If any TimeDB instance holds this value it is not existent in the database. */
    public static final int NO_ID_VALUE = -1;

    private int id;
    private DateTime start;
    private DateTime end;
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

    //TODO: Pass LocalDateTime aswell.
    public TimeDB(int id, DateTime start, DateTime end, int activityID) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.activityID = activityID;
    }

    public int getId() {
        return id;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
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
