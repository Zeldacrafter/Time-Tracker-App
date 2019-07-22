package com.uni.time_tracking.database.tables;

import android.graphics.Color;
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
                    + FeedEntry.COLUMN_COLOR
                    + " TEXT NOT NULL DEFAULT '#FFFFFF', "
                    + FeedEntry.COLUMN_ACTIVE
                    + " INTEGER NOT NULL DEFAULT 1)";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS "
                    + FeedEntry.TABLE_NAME;

    private int id;
    private String name;
    private boolean active;
    private int color;

    public ActivityDB(int id, String name, boolean active, int color) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.color = color;
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

    public int getColor() {
        return color;
    }

    public static final class FeedEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "Activity";

        //Table columns
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_ACTIVE = "Enabled";
        public static final String COLUMN_COLOR = "Color"; //AS HEX TEXT
    }

}
