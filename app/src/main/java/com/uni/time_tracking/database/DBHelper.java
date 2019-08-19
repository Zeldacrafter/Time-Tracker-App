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

import com.uni.time_tracking.Preferences;
import com.uni.time_tracking.Utils;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.uni.time_tracking.Utils._assert;

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
    private DBHelper(Context context) {
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

    private static void createTables(@NotNull SQLiteDatabase db){
        db.execSQL(ActivityDB.SQL_CREATE_TABLE);
        db.execSQL(TimeDB.SQL_CREATE_TABLE);
    }

    private static void deleteEntries(@NotNull SQLiteDatabase db){
        db.execSQL(ActivityDB.SQL_DELETE_TABLE);
        db.execSQL(TimeDB.SQL_DELETE_TABLE);
    }

    public void addEntryActivity(String name, int color){
        ContentValues values = new ContentValues();
        values.put(ActivityDB.FeedEntry.COLUMN_NAME, name);
        values.put(ActivityDB.FeedEntry.COLUMN_COLOR, Utils.colorIntToHex(color));
        values.put(ActivityDB.FeedEntry.COLUMN_LIST_POSITION, Long.MAX_VALUE);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(ActivityDB.FeedEntry.TABLE_NAME, null, values);

        db.close();

        squashListPositions();
    }

    /**
     * Changes the {@link ActivityDB.FeedEntry#COLUMN_LIST_POSITION} values so that they dont have
     * gaps. Values of '1' '7' '3' would be changed to '1' '3' '2'
     */
    private void squashListPositions() {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(ActivityDB.FeedEntry.TABLE_NAME,
                new String[]{ActivityDB.FeedEntry._ID},
                null, null, null, null,
                ActivityDB.FeedEntry.COLUMN_LIST_POSITION);

        for (int pos = 0; cursor.moveToNext(); pos++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ActivityDB.FeedEntry.COLUMN_LIST_POSITION, pos);

            db.update(ActivityDB.FeedEntry.TABLE_NAME,
                    contentValues,
                    ActivityDB.FeedEntry._ID + " = ?",
                    new String[] {cursor.getInt(0)+""});
        }

        cursor.close();
        db.close();
    }

    public void addEntryTime(DateTime start, DateTime end, int activityID){
        ContentValues values = new ContentValues();
        values.put(TimeDB.FeedEntry.COLUMN_START, Time.toLong(start));
        values.put(TimeDB.FeedEntry.COLUMN_END, Time.toLong(end));
        values.put(TimeDB.FeedEntry.COLUMN_ACTIVITY_ID, activityID);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TimeDB.FeedEntry.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Deleting and re-creating all tables in the database.
     */
    public void resetDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        deleteEntries(db);
        createTables(db);
        db.close();
    }

    /**
     * Returns array of all {@link ActivityDB} entries in the database.
     * @return Array with all {@link ActivityDB} entries in the database.
     */
    public ActivityDB[] getAcitivities() {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(
                ActivityDB.FeedEntry.TABLE_NAME,
                new String[]{
                        ActivityDB.FeedEntry._ID,
                        ActivityDB.FeedEntry.COLUMN_NAME,
                        ActivityDB.FeedEntry.COLUMN_COLOR,
                        ActivityDB.FeedEntry.COLUMN_ACTIVE,
                        ActivityDB.FeedEntry.COLUMN_LIST_POSITION},
                null, null, null, null,
                ActivityDB.FeedEntry.COLUMN_LIST_POSITION);

        ActivityDB[] values = new ActivityDB[cursor.getCount()];
        for (int count = 0; cursor.moveToNext(); count++){

            int id = Integer.parseInt(cursor.getString(0));
            String name = cursor.getString(1);
            int color = Color.parseColor(cursor.getString(2));
            boolean active = cursor.getInt(3) == 1;
            int listPos = cursor.getInt(4);

            values[count] = new ActivityDB(id, name, active, color, listPos);
        }

        cursor.close();
        db.close();

        return values;
    }

    /**
     * Swaps the values of the {@link ActivityDB.FeedEntry#COLUMN_LIST_POSITION} columns of two rows.
     * @param id1 The id of the first row.
     * @param id2 The if of the second row.
     */
    public void swapActivityListOrder(int id1, int id2) {

        SQLiteDatabase db = getWritableDatabase();

        //Get the COLUMN_LIST_POSITION value of the first row
        Cursor pos1Cursor = db.query(ActivityDB.FeedEntry.TABLE_NAME,
                new String[]{ActivityDB.FeedEntry.COLUMN_LIST_POSITION},
                ActivityDB.FeedEntry._ID + " = ?",
                new String[] {id1+""},
                null, null, null);
        pos1Cursor.moveToFirst();
        int pos1 = pos1Cursor.getInt(0);
        pos1Cursor.close();

        //Get the COLUMN_LIST_POSITION value of the second row
        Cursor pos2Cursor = db.query(ActivityDB.FeedEntry.TABLE_NAME,
                new String[]{ActivityDB.FeedEntry.COLUMN_LIST_POSITION},
                ActivityDB.FeedEntry._ID + " = ?",
                new String[] {id2+""},
                null, null, null);
        pos2Cursor.moveToFirst();
        int pos2 = pos2Cursor.getInt(0);
        pos2Cursor.close();

        //Set the COLUMN_LIST_POSITION value of the first row
        ContentValues pos1Values = new ContentValues();
        pos1Values.put(ActivityDB.FeedEntry.COLUMN_LIST_POSITION, pos2);
        db.update(ActivityDB.FeedEntry.TABLE_NAME,
                pos1Values,
                ActivityDB.FeedEntry._ID + " = ?",
                new String[]{id1+""});

        //Set the COLUMN_LIST_POSITION value of the second row
        ContentValues pos2Values = new ContentValues();
        pos2Values.put(ActivityDB.FeedEntry.COLUMN_LIST_POSITION, pos1);
        db.update(ActivityDB.FeedEntry.TABLE_NAME,
                pos2Values,
                ActivityDB.FeedEntry._ID + " = ?",
                new String[]{id2+""});

        db.close();
    }

    /**
     * Sets the entry with the specified id to all values (except id) contained in the
     * specified {@link ActivityDB} instance.
     * @param id The id of the entry to alter.
     * @param activity {@link ActivityDB} instance that holds all values to set.
     */
    public void editActivity(int id, ActivityDB activity) {
        _assert(id > 0, id+"");

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ActivityDB.FeedEntry.COLUMN_NAME, activity.getName());
        contentValues.put(ActivityDB.FeedEntry.COLUMN_COLOR, Utils.colorIntToHex(activity.getColor()));
        contentValues.put(ActivityDB.FeedEntry.COLUMN_ACTIVE, activity.isActive() ? 1 : 0);
        db.update(
                ActivityDB.FeedEntry.TABLE_NAME,
                contentValues,
                ActivityDB.FeedEntry._ID + " = ?",
                new String[] {""+id});

        db.close();
    }

    /**
     * Returns an array of all {@link ActivityDB} entries
     * where {@link ActivityDB.FeedEntry#COLUMN_ACTIVE} is set to false.
     * @return Array with {@link ActivityDB} objects.
     */
    public ActivityDB[] getActiveActivities() {

        SQLiteDatabase db = getWritableDatabase();

        //TODO: No raw query

        String query =
                "SELECT "
                        + ActivityDB.FeedEntry._ID + ", "
                        + ActivityDB.FeedEntry.COLUMN_NAME + ", "
                        + ActivityDB.FeedEntry.COLUMN_COLOR + ", "
                        + ActivityDB.FeedEntry.COLUMN_LIST_POSITION +
                        " FROM "
                        + ActivityDB.FeedEntry.TABLE_NAME +
                        " WHERE "
                        + ActivityDB.FeedEntry.COLUMN_ACTIVE + " = 1";

        //Getting the result-cursor
        Cursor cursor = db.rawQuery(query, null);

        //Putting the results in a 2-dimensional array
        //where the first dimension is the entryNr
        //and the second dimension a list of the wanted columns
        ActivityDB[] values = new ActivityDB[cursor.getCount()];
        int count = 0;
        while (cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(0));
            String name = cursor.getString(1);
            int color = Color.parseColor(cursor.getString(2));
            int listPos = cursor.getInt(3);
            values[count] = new ActivityDB(id, name, true, color, listPos);
            count++;
        }

        cursor.close();
        db.close();

        return values;
    }

    /**
     * Get {@link ActivityDB} database entry with the specified ID.
     * @param activityID The ID of the wanted activity entry.
     * @return Wanted {@link ActivityDB} instance.
     */
    public ActivityDB getActivity(int activityID) {
        _assert(activityID > 0, activityID+"");

        SQLiteDatabase db = getReadableDatabase();

        //TODO: Dont use rawQuery
        String query = "SELECT " +
                ActivityDB.FeedEntry.COLUMN_NAME + ", " +
                ActivityDB.FeedEntry.COLUMN_ACTIVE + ", " +
                ActivityDB.FeedEntry.COLUMN_COLOR  + ", " +
                ActivityDB.FeedEntry.COLUMN_LIST_POSITION  +
                " FROM " + ActivityDB.FeedEntry.TABLE_NAME +
                " WHERE " + ActivityDB.FeedEntry._ID + " = " + activityID;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        ActivityDB result = new ActivityDB(activityID, c.getString(0), c.getInt(1) == 1, Color.parseColor(c.getString(2)), c.getInt(3));

        c.close();
        db.close();

        return result;
    }

    /**
     * Returns whether there is a active {@link TimeDB} database entry
     * ({@link TimeDB.FeedEntry#COLUMN_END} = null) for a specified activity.
     * @param activityID The ID of the activity.
     * @return {@code true} if such an entry exists, {@code false} otherwise.
     */
    public boolean isActivityActive(int activityID) {
        _assert(activityID > 0, activityID+"");
        return getActiveTime(activityID) != null;
    }

    /**
     * Returns {@link TimeDB} database entry that is currently active
     * ({@link TimeDB.FeedEntry#COLUMN_END} = null) for a specified activity.
     * @param activityID The ID of the activity.
     * @return {@link TimeDB} object corresponding to the entry. {@code null} if no such entry exists.
     */
    public TimeDB getActiveTime(int activityID) {
        _assert(activityID > 0, activityID+"");

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TimeDB.FeedEntry.TABLE_NAME,
                    new String[]{
                        TimeDB.FeedEntry._ID,
                        TimeDB.FeedEntry.COLUMN_START,
                        TimeDB.FeedEntry.COLUMN_END},
                    TimeDB.FeedEntry.COLUMN_ACTIVITY_ID + " = ? AND " + TimeDB.FeedEntry.COLUMN_END + " IS NULL",
                    new String[]{""+activityID}, null, null, null);

        _assert(c.getCount() < 2, "More than one instance of the activity active.");

        if(c.getCount() == 0) {
            c.close();
            db.close();
            return null;
        }else {
            c.moveToFirst();
            int id = c.getInt(0);
            DateTime start = Time.fromLong(c.getLong(1));
            DateTime end = null;
            c.close();
            db.close();
            return new TimeDB(id, start, end, activityID);
        }
    }

    /**
     * Adds a new active {@link TimeDB} entry for an activity starting at the current system-time.
     * @param activityID The ID of the activity.
     */
    public void activateTimeActivity(int activityID, Context context) {
        _assert(activityID > 0, activityID+"");
        _assert(!isActivityActive(activityID), "This activity is already active.");

        SQLiteDatabase db = getWritableDatabase();

        if (!Preferences.isSimultaneousActivitiesAllowed(context)) {
            // If another activity has an active entry entry we want to stop that activity from
            // running because the user selected this in the settings.
            ContentValues contentValues = new ContentValues();
            contentValues.put(TimeDB.FeedEntry.COLUMN_END, Time.toLong(Time.getCurrentTime()));


            Cursor c = db.query(TimeDB.FeedEntry.TABLE_NAME,
                    new String[]{TimeDB.FeedEntry._ID},
                    TimeDB.FeedEntry.COLUMN_ACTIVITY_ID + " IS NOT ? " +
                            "AND " + TimeDB.FeedEntry.COLUMN_END + " IS NULL",
                    new String[]{""+activityID},
                    null, null, null);

            _assert(c.getCount() < 2);

            if(c.getCount() == 1) {
                c.moveToFirst();
                db.close();
                deactivateTimeEntry(c.getInt(0), context);
                db = getWritableDatabase();
                //FIXME: Ugly workaround. DB gets closed in deactivateTimeEntry() so we need to reopen it.
            }

            c.close();
        }

        ContentValues values = new ContentValues();
        values.put(TimeDB.FeedEntry.COLUMN_START, Time.toLong(Time.getCurrentTime()));
        values.put(TimeDB.FeedEntry.COLUMN_ACTIVITY_ID, activityID);

        db.insert(TimeDB.FeedEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void editEntryTime(int id, DateTime start, DateTime end, int activityID) {
        _assert(id > 0, id+"");
        _assert(activityID > 0, activityID+"");

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TimeDB.FeedEntry.COLUMN_START, Time.toLong(start));
        contentValues.put(TimeDB.FeedEntry.COLUMN_END, Time.toLong(end));
        contentValues.put(TimeDB.FeedEntry.COLUMN_ACTIVITY_ID, activityID);
        db.update(TimeDB.FeedEntry.TABLE_NAME,
                contentValues,
                TimeDB.FeedEntry._ID + " = ?",
                new String[] {""+id});

        db.close();
    }

    /**
     * Get {@link TimeDB} database entry with specified id.
     * @param id The id of the wanted entry.
     * @return Wanted database entry as {@link TimeDB} instance.
     */
    public TimeDB getTimeEntry(int id) {
        _assert(id > 0, id+"");

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TimeDB.FeedEntry.TABLE_NAME,
                new String[]{
                        TimeDB.FeedEntry.COLUMN_START,
                        TimeDB.FeedEntry.COLUMN_END,
                        TimeDB.FeedEntry.COLUMN_ACTIVITY_ID},
                TimeDB.FeedEntry._ID + " = ?", new String[] {""+id},
                null, null, null);
        c.moveToFirst();
        TimeDB result = new TimeDB(id,
                Time.fromLong(c.getLong(0)),
                Time.fromLong(c.getLong(1)),
                c.getInt(2));

        c.close();
        db.close();

        return result;
    }

    /**
     * Deactivating a {@link TimeDB} entry by setting {@link TimeDB.FeedEntry#COLUMN_END}
     * to the current system-time.
     * @param timeID The ID of the {@link TimeDB} entry.
     */
    public void deactivateTimeEntry(int timeID, Context context) {

        _assert(timeID > 0, "ID = " + timeID);

        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.query(TimeDB.FeedEntry.TABLE_NAME,
                new String[] {TimeDB.FeedEntry.COLUMN_START},
                TimeDB.FeedEntry._ID + " = ?",
                new String[] {"" + timeID},
                null, null, null);
        c.moveToFirst();
        Period elapsedTime = Time.differenceToNow(Time.fromLong(c.getLong(0)));
        Period oneMinute = new Period(0, 1, 0, 0);
        c.close();

        if (!Preferences.removeShortEntries(context) || Time.isFirstPeriodLonger(elapsedTime, oneMinute)) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(TimeDB.FeedEntry.COLUMN_END, Time.toLong(Time.getCurrentTime()));
            int nrChanged = db.update(
                    TimeDB.FeedEntry.TABLE_NAME,
                    contentValues,
                    TimeDB.FeedEntry._ID + " = ?",
                    new String[] {""+timeID});
            _assert(nrChanged == 1);
        } else {
            // We want to delete this entry because it is shorter than 1 minute and
            // the corresponding option was selected by the user.
            int nrDeleted = db.delete(TimeDB.FeedEntry.TABLE_NAME,
                    TimeDB.FeedEntry._ID + " = ?",
                    new String[] {"" + timeID});
            _assert(nrDeleted == 1);
        }
        db.close();
    }

    /**
     * Returns Array of all EntryDBs that have part of their time interval start-end in
     * a specified month where the associated ActivityDB entry is active.
     * If the Entry does not occupy that month it is ignored.
     * If the Entry partially occupies that month it is cut in such a way that the start/end
     * now match the border of the month.
     * <br/><br/>
     * Example: Month 6, Year 2019: <br/>
     * Start: 2019-05-04 7:20:40, End: 2019-06-13 12:30:20 <br/>
     * will be returned as <br/>
     * Start: 2019-06-00 0:00:00, End 2019-06-13 12:30:20
     * @param year The year that the wanted month is in.
     * @param month The month.
     * @return Array of all wanted elements.
     */
    public TimeDB[] getAllActiveEventsInMonth(int year, int month) {

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " +
                TimeDB.FeedEntry.TABLE_NAME + "." + TimeDB.FeedEntry._ID + ", " +
                TimeDB.FeedEntry.COLUMN_START + ", " +
                TimeDB.FeedEntry.COLUMN_END + ", " +
                TimeDB.FeedEntry.COLUMN_ACTIVITY_ID  +
                " FROM " + TimeDB.FeedEntry.TABLE_NAME +
                " INNER JOIN " + ActivityDB.FeedEntry.TABLE_NAME +
                    " ON " + TimeDB.FeedEntry.COLUMN_ACTIVITY_ID + " = " + ActivityDB.FeedEntry.TABLE_NAME + "." + ActivityDB.FeedEntry._ID +
                " WHERE " + TimeDB.FeedEntry.TABLE_NAME + "." + TimeDB.FeedEntry.COLUMN_START +
                        " <= " + year + (month < 10 ? "0" : "") + month + "99999999" +
                    " AND " + TimeDB.FeedEntry.TABLE_NAME + "." + TimeDB.FeedEntry.COLUMN_END +
                        " >= " + year + (month < 10 ? "0" : "") + month + "00000000" +
                    " AND " + ActivityDB.FeedEntry.COLUMN_ACTIVE + " = 1";

        Cursor c = db.rawQuery(query, null);

        ArrayList<TimeDB> result = new ArrayList<>();

        while(c.moveToNext()) {

            int id = c.getInt(0);
            long start = c.getLong(1);
            long end = c.isNull(2) ? Time.toLong(Time.getCurrentTime()) : c.getLong(2);
            int activity_id = c.getInt(3);

            long startYearMonth = start / 100000000;
            long endYearMonth = end / 100000000;

            long wantedYearMonth = year*100 + month;

            if(startYearMonth < wantedYearMonth) {
                // Start time starts too early.
                // Set to wanted month, first day, 0 hours/mins/secs.
                start = wantedYearMonth*100000000 + 1000000; //01 00:00:00
            }

            if(endYearMonth > wantedYearMonth) {
                // End time ends too late.
                // Set to wanted month, last day, 23 hours, 59 mins/secs.
                long maxDaysInMonth = (new GregorianCalendar(year, month, 1)).getActualMaximum(Calendar.DAY_OF_MONTH);
                end = wantedYearMonth*100000000 + maxDaysInMonth*1000000 + 235959; //eg 31 23:59:59
            }
            result.add(new TimeDB(id, Time.fromLong(start), Time.fromLong(end), activity_id));
        }

        db.close();
        c.close();
        return result.toArray(new TimeDB[0]);
    }

    /**
     * Removed the activity with the given id and all associated {@link TimeDB} entries.
     * @param activityID The ID of the activity we want to delete.
     */
    public void deleteActivity(int activityID) {
        _assert(activityID > 0, "ID = " + activityID);


        SQLiteDatabase db = getWritableDatabase();
        db.delete(TimeDB.FeedEntry.TABLE_NAME,
                TimeDB.FeedEntry.COLUMN_ACTIVITY_ID + " = ?",
                new String[]{activityID+""});
        db.delete(ActivityDB.FeedEntry.TABLE_NAME,
                ActivityDB.FeedEntry._ID + " = ?",
                new String[]{activityID+""});
        db.close();

        squashListPositions();
    }

    /**
     * Toggle whether {@link ActivityDB.FeedEntry#COLUMN_ACTIVE} is 0 or 1.
     * @param activityID The ID of the activity.
     */
    public void toggleActivityActive(int activityID) {
        _assert(activityID > 0, "ID = " + activityID);

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

    public void deleteTimeEntry(int id) {
        _assert(id > 0, "ID = " + id);

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TimeDB.FeedEntry.TABLE_NAME, TimeDB.FeedEntry._ID + " = ?", new String[]{id+""});
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