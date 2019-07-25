package com.uni.time_tracking.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;


import com.uni.time_tracking.General;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.EntryDB;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * DBHelper is used for managing the database and its tables.
 * All queries are done in this class and called where they are needed
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private static DBHelper sInstance;

    //Version of the database. Currently of no use.
    private static final int DATABASE_VERSION = 1;

    //private final Context context;

    //Name of the database file on the target device.
    private static final String DATABASE_NAME = "Database.db";

    /**
     * Used for the initialisation of the database: similar to a setup()/init() method.
     *
     * Adding initial entries to the database.
     *
     * @param context Application Context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creating all tables
     *
     * @param db target database
     */
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    /**
     * This is currently unused and not implemented properly.
     * Currently just deletes all tables and recreates them.
     *
     * @param db         Database that should be upgraded
     * @param oldVersion old versionNr of the database
     * @param newVersion new versionNr of the database
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
    }

    public static synchronized DBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private static void createTables(SQLiteDatabase db){
        db.execSQL(ActivityDB.SQL_CREATE_TABLE);
        db.execSQL(EntryDB.SQL_CREATE_TABLE);
    }

    private static void deleteEntries(SQLiteDatabase db){
        db.execSQL(ActivityDB.SQL_DELETE_TABLE);
        db.execSQL(EntryDB.SQL_DELETE_TABLE);
    }

    public void addEntryActivity(String name, int color){
        ContentValues values = new ContentValues();
        values.put(ActivityDB.FeedEntry.COLUMN_NAME, name);
        values.put(ActivityDB.FeedEntry.COLUMN_COLOR, General.colorIntToHex(color));

        getWritableDatabase().insert(ActivityDB.FeedEntry.TABLE_NAME, null, values);
    }

    /**
     * Deleting and re-creating all tables in the database.
     */
    public void resetDatabase() {
        deleteEntries(getWritableDatabase());
        createTables(getWritableDatabase());
    }

    public ActivityDB[] getAcitivities() {
        //Creating the 'query'-String
        String query =
                "SELECT "
                        + ActivityDB.FeedEntry._ID + ", "
                        + ActivityDB.FeedEntry.COLUMN_NAME + ", "
                        + ActivityDB.FeedEntry.COLUMN_COLOR + ", "
                        + ActivityDB.FeedEntry.COLUMN_ACTIVE +
                        " FROM "
                        + ActivityDB.FeedEntry.TABLE_NAME;

        //Getting the result-cursor
        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        //Putting the results in a 2-dimensional array
        //where the first dimension is the entryNr
        //and the second dimension a list of the wanted columns
        ActivityDB[] values = new ActivityDB[cursor.getCount()];
        int count = 0;
        while (cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(0));
            String name = cursor.getString(1);
            int color = Color.parseColor(cursor.getString(2));
            boolean active = cursor.getInt(3) == 1;
            values[count] = new ActivityDB(id, name, active, color);
            count++;
        }

        cursor.close();

        return values;
    }

    public void editActivity(int id, ActivityDB activity) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        String query = "UPDATE " + ActivityDB.FeedEntry.TABLE_NAME + " SET " +
                ActivityDB.FeedEntry.COLUMN_NAME + " = \"" + activity.getName() + "\", " +
                ActivityDB.FeedEntry.COLUMN_COLOR + " = \"" + General.colorIntToHex(activity.getColor()) + "\", " +
                ActivityDB.FeedEntry.COLUMN_ACTIVE + " = " + (activity.isActive()  ? 1 : 0) +
                " WHERE " + ActivityDB.FeedEntry._ID + " = ?";
        db.execSQL(query, new String[] {id+""});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Returns an array of all {@link ActivityDB} entries
     * where {@link ActivityDB.FeedEntry#COLUMN_ACTIVE} is set to false.
     * @return Array with {@link ActivityDB} objects.
     */
    public ActivityDB[] getActiveActivities() {

        //Creating the 'query'-String
        String query =
                "SELECT "
                        + ActivityDB.FeedEntry._ID + ", "
                        + ActivityDB.FeedEntry.COLUMN_NAME + ", "
                        + ActivityDB.FeedEntry.COLUMN_COLOR +
                        " FROM "
                        + ActivityDB.FeedEntry.TABLE_NAME +
                        " WHERE "
                        + ActivityDB.FeedEntry.COLUMN_ACTIVE + " = 1";

        //Getting the result-cursor
        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        //Putting the results in a 2-dimensional array
        //where the first dimension is the entryNr
        //and the second dimension a list of the wanted columns
        ActivityDB[] values = new ActivityDB[cursor.getCount()];
        int count = 0;
        while (cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(0));
            String name = cursor.getString(1);
            int color = Color.parseColor(cursor.getString(2));
            values[count] = new ActivityDB(id, name, true, color);
            count++;
        }

        cursor.close();

        return values;
    }

    public ActivityDB getActivity(int acitivityID) {

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " +
                ActivityDB.FeedEntry.COLUMN_NAME + ", " +
                ActivityDB.FeedEntry.COLUMN_ACTIVE + ", " +
                ActivityDB.FeedEntry.COLUMN_COLOR  +
                " FROM " + ActivityDB.FeedEntry.TABLE_NAME +
                " WHERE " + ActivityDB.FeedEntry._ID + " = " + acitivityID;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        ActivityDB result = new ActivityDB(acitivityID, c.getString(0), c.getInt(1) == 1, Color.parseColor(c.getString(2)));

        c.close();
        db.close();

        return result;
    }

    /**
     * Returns whether there is a active {@link EntryDB} database entry
     * ({@link EntryDB.FeedEntry#COLUMN_END} = null) for a specified activity.
     * @param activityID The ID of the activity.
     * @return {@code true} if such an entry exists, {@code false} otherwise.
     */
    public boolean isActivityActive(int activityID) {
        return getActiveTime(activityID) != null;
    }

    /**
     * Returns {@link EntryDB} database entry that is currently active
     * ({@link EntryDB.FeedEntry#COLUMN_END} = null) for a specified activity.
     * @param activityID The ID of the activity.
     * @return {@link EntryDB} object corresponding to the entry. {@code null} if no such entry exists.
     */
    public EntryDB getActiveTime(int activityID) {
        String query = "SELECT " + EntryDB.FeedEntry._ID + ", " +
                EntryDB.FeedEntry.COLUMN_START + ", " +
                EntryDB.FeedEntry.COLUMN_END +
                " FROM " + EntryDB.FeedEntry.TABLE_NAME +
                " WHERE " + EntryDB.FeedEntry.COLUMN_ACTIVITY_ID + " = ?" +
                " AND " + EntryDB.FeedEntry.COLUMN_END + " IS NULL";
        Cursor c = getReadableDatabase().rawQuery(query, new String[]{""+activityID});

        assert(c.getCount() < 2) : "More than one instance of the activity active."; //FIXME

        if(c.getCount() == 0) {
            c.close();
            return null;
        }else {
            c.moveToFirst();
            int id = c.getInt(0);
            DateTime start = Time.fromString(c.getString(1));
            DateTime end = null; //TODO: Careful!
            c.close();
            return new EntryDB(id, start, end, activityID);
        }
    }

    /**
     * Adds a new active {@link EntryDB} entry for an activity starting at the current system-time.
     * @param activityID The ID of the activity.
     */
    public void activateActivity(int activityID) {
        assert(!isActivityActive(activityID));

        ContentValues values = new ContentValues();
        values.put(EntryDB.FeedEntry.COLUMN_START, Time.getCurrentTimeString());
        values.put(EntryDB.FeedEntry.COLUMN_ACTIVITY_ID, activityID);
        getWritableDatabase().insert(EntryDB.FeedEntry.TABLE_NAME, null, values);
    }

    /**
     * Deactivating a {@link EntryDB} entry by setting {@link EntryDB.FeedEntry#COLUMN_END}
     * to the current system-time.
     * @param timeID The ID of the {@link EntryDB} entry.
     */
    public void deactivityEntry(int timeID) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(EntryDB.FeedEntry.COLUMN_END, Time.getCurrentTimeString());
        getWritableDatabase().update(
                EntryDB.FeedEntry.TABLE_NAME,
                contentValues,
                EntryDB.FeedEntry._ID + " = ?",
                new String[] {""+timeID});
    }

    /**
     * Returns Array of all EntryDBs that have part of their time interval start-end in
     * a specified month.
     * If the Entry does not occupy that month it is ignored.
     * If the Entry partially occupies that month it is cut in such a way that the start/end
     * now match the border of the month.
     *
     * Example: Month: 6, Year 2019:
     * Start: 2019-05-04 7:20:40, End: 2019-06-13 12:30:20
     * will be returned as
     * Start: 2019-06-00 0:00:00, End 2019-06-13 12:30:20
     * @param year The year that the wanted month is in.
     * @param month The month.
     * @return Array of all wanted elements.
     */
    public EntryDB[] getAllEventsInMonth(int year, int month) {

        String query = "SELECT " +
                EntryDB.FeedEntry._ID + ", " +
                EntryDB.FeedEntry.COLUMN_START + ", " +
                EntryDB.FeedEntry.COLUMN_END + ", " +
                EntryDB.FeedEntry.COLUMN_ACTIVITY_ID  +
                " FROM " + EntryDB.FeedEntry.TABLE_NAME;

        Cursor c = getReadableDatabase().rawQuery(query, null);

        ArrayList<EntryDB> result = new ArrayList<>();

        while(c.moveToNext()) {

            int id = c.getInt(0);
            DateTime start = Time.fromString(c.getString(1));
            DateTime end = c.isNull(2) ? Time.getCurrentTime() : Time.fromString(c.getString(2));
            int activity_id = c.getInt(3);

            if ((start.getYear() < year || (start.getYear() == year && start.getMonthOfYear() <= month)) &&
                    (end.getYear() > year || (end.getYear() == year && end.getMonthOfYear() >= month))) {
                //The current time fits the wanted time interval.

                if(start.getYear() < year || (start.getYear() == year && start.getMonthOfYear() < month)) {
                    //Start time starts too early. Cut it off.
                    start = new DateTime(year, month, 0, 0, 0, 0, Time.getTimezone());
                }
                if(end.getYear() > year || (end.getYear() == year && end.getMonthOfYear() < month)) {
                    //End time ends too late. Cut it off.
                    end = new DateTime(year, month, start.dayOfMonth().getMaximumValue(), 23, 59, 59, Time.getTimezone());
                }
                result.add(new EntryDB(id, start, end, activity_id));
            }
        }

        c.close();
        return result.toArray(new EntryDB[0]);
    }

    /**
     * Removed the activity with the given id and all associated {@link EntryDB} entries.
     * @param activityId The ID of the activity we want to delete
     */
    public void deleteActivity(int activityId) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(EntryDB.FeedEntry.TABLE_NAME, EntryDB.FeedEntry.COLUMN_ACTIVITY_ID + "=" + activityId, null);
        db.delete(ActivityDB.FeedEntry.TABLE_NAME, ActivityDB.FeedEntry._ID + "=" + activityId, null);

        db.close();
    }

    public void toggleActivityActive(int activityID) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        String query = "UPDATE " + ActivityDB.FeedEntry.TABLE_NAME +
                " SET " + ActivityDB.FeedEntry.COLUMN_ACTIVE + " = CASE " + ActivityDB.FeedEntry.COLUMN_ACTIVE +
                " WHEN 1 THEN 0" +
                " ELSE 1" +
                " END" +
                " WHERE " + ActivityDB.FeedEntry._ID + " = " + activityID;
        db.execSQL(query);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * This method is for the class 'DevAndroidDatabaseManager'
     * Remove this before release
     * @param Query
     * @return
     */
    @SuppressWarnings("all")
    @SuppressLint("all")
    public ArrayList<Cursor> getData (String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.i("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.i("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }

}