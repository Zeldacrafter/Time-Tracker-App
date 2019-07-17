package com.example.uni_app.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.uni_app.database.tables.ActivityDB;

import java.util.ArrayList;
import java.util.Random;

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
    }

    private static void deleteEntries(SQLiteDatabase db){
        db.execSQL(ActivityDB.SQL_DELETE_TABLE);
    }

    private void addEntryActivity(String name){
        ContentValues values = new ContentValues();
        values.put(ActivityDB.FeedEntry.COLUMN_NAME, name);

        getWritableDatabase().insert(ActivityDB.FeedEntry.TABLE_NAME, null, values);
    }




}