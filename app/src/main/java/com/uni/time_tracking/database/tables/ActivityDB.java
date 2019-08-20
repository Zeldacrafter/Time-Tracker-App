package com.uni.time_tracking.database.tables;

import android.provider.BaseColumns;

import org.jetbrains.annotations.NotNull;


public class ActivityDB {

    public static final int NO_ID_VALUE = -1;

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
                    + " INTEGER NOT NULL DEFAULT 1, "
                    + FeedEntry.COLUMN_LIST_POSITION
                    + " INTEGER)";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS "
                    + FeedEntry.TABLE_NAME;

    private int id;
    private String name;
    private boolean active;
    private int color;
    private int listPos;

    public ActivityDB(int id, String name, boolean active, int color, int listPos) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.color = color;
        this.listPos = listPos;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * NOTE: This does not alter the database in any way.
     * @param name new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @NotNull
    @Override
    public String toString() {
        //NOTE: Used in TimeModifier.class in the spinner.
        //      The ArrayAdapter of the spinner takes the toString method of its elements
        //      to display the spinner-item names.
        return getName();
    }

    public static final class FeedEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "Activity";

        //Table columns
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_ACTIVE = "Enabled";
        public static final String COLUMN_COLOR = "Color"; // As HEX text
        /** Indicates in what position the element will be shown in
         * for example the list on the home screen. */
        public static final String COLUMN_LIST_POSITION = "ListPosition";
    }

}
