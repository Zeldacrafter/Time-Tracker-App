package com.uni.time_tracking.database.tables;

import android.provider.BaseColumns;


public class ActivityDB {

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "
                    + FeedEntry.TABLE_NAME
                    + "( "
                    + FeedEntry._ID
                    + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + FeedEntry.COLUMN_NAME
                    + " TEXT, "
                    + FeedEntry.COLUMN_ACTIVE
                    + " INTEGER NOT NULL DEFAULT 1)";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS "
                    + FeedEntry.TABLE_NAME;

    private int id;
    private String name;
    private boolean active;

    public ActivityDB(int id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }


    public static final class FeedEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "Activity";

        //Table columns
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_ACTIVE = "Active";
    }

}
