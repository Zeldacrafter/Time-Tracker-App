package com.uni.time_tracking.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;

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
        db.execSQL(TimeDB.SQL_CREATE_TABLE);
    }

    private static void deleteEntries(SQLiteDatabase db){
        db.execSQL(ActivityDB.SQL_DELETE_TABLE);
        db.execSQL(TimeDB.SQL_DELETE_TABLE);
    }

    public void addEntryActivity(String name){
        ContentValues values = new ContentValues();
        values.put(ActivityDB.FeedEntry.COLUMN_NAME, name);

        getWritableDatabase().insert(ActivityDB.FeedEntry.TABLE_NAME, null, values);
    }

    /**
     * Deleting and re-creating all tables in the database.
     */
    public void resetDatabase() {
        deleteEntries(getWritableDatabase());
        createTables(getWritableDatabase());
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
                        + ActivityDB.FeedEntry.COLUMN_NAME +
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
            values[count] = new ActivityDB(id, name, true);
            count++;
        }

        cursor.close();

        return values;
    }

    /**
     * Returns whether there is a active {@link TimeDB} database entry
     * ({@link TimeDB.FeedEntry#COLUMN_END} = null) for a specified activity.
     * @param activityID The ID of the activity.
     * @return {@code true} if such an entry exists, {@code false} otherwise.
     */
    public boolean isActivityActive(int activityID) {
        return getActiveTime(activityID) != null;
    }

    /**
     * Returns {@link TimeDB} database entry that is currently active
     * ({@link TimeDB.FeedEntry#COLUMN_END} = null) for a specified activity.
     * @param activityID The ID of the activity.
     * @return {@link TimeDB} object corresponding to the entry. {@code null} if no such entry exists.
     */
    public TimeDB getActiveTime(int activityID) {
        String query = "SELECT " + TimeDB.FeedEntry._ID + ", " +
                TimeDB.FeedEntry.COLUMN_START + ", " +
                TimeDB.FeedEntry.COLUMN_END +
                " FROM " + TimeDB.FeedEntry.TABLE_NAME +
                " WHERE " + TimeDB.FeedEntry.COLUMN_ACTIVITY_ID + " = ?" +
                " AND " + TimeDB.FeedEntry.COLUMN_END + " IS NULL";
        Cursor c = getWritableDatabase().rawQuery(query, new String[]{""+activityID});

        assert(c.getCount() < 2) : "More than one instance of the activity active."; //FIXME

        if(c.getCount() == 0) {
            c.close();
            return null;
        }else {
            c.moveToFirst();
            int id = c.getInt(0);
            long start = c.getLong(1);
            long end = -1; //No end yet. TODO: This is ugly
            c.close();
            return new TimeDB(id, start, end, activityID);
        }
    }

    /**
     * Adds a new active {@link TimeDB} entry for an activity starting at the current system-time.
     * @param activityID The ID of the activity.
     */
    public void activateActivity(int activityID) {
        assert(!isActivityActive(activityID));

        ContentValues values = new ContentValues();
        values.put(TimeDB.FeedEntry.COLUMN_START, System.currentTimeMillis());
        values.put(TimeDB.FeedEntry.COLUMN_ACTIVITY_ID, activityID);
        getWritableDatabase().insert(TimeDB.FeedEntry.TABLE_NAME, null, values);
    }

    /**
     * Deactivating a {@link TimeDB} entry by setting {@link TimeDB.FeedEntry#COLUMN_END}
     * to the current system-time.
     * @param timeID The ID of the {@link TimeDB} entry.
     */
    public void deactivateTime(int timeID) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TimeDB.FeedEntry.COLUMN_END, System.currentTimeMillis());
        getWritableDatabase().update(
                TimeDB.FeedEntry.TABLE_NAME,
                contentValues,
                TimeDB.FeedEntry._ID + " = ?",
                new String[] {""+timeID});
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
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }

}