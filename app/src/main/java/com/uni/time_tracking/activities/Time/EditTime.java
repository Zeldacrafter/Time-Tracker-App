package com.uni.time_tracking.activities.Time;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.TimeDB;

import static com.uni.time_tracking.General.showToast;

public class EditTime extends TimeModifier {

    public static final String TAG = "EditTime";

    public static final String BUNDLE_TIME_ID = "Time_ID";
    public static final String BUNDLE_ACTIVITY_ID = "Activity_Name";

    private int idToEdit;
    private int activityID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        idToEdit = extras.getInt(BUNDLE_TIME_ID, TimeDB.NO_ID_VALUE);
        activityID = extras.getInt(BUNDLE_ACTIVITY_ID);

        //FIXME: check -1
        categorySpinner.setSelection(getIndexOfSpinnerItemWithName(activityID));
    }

    private int getIndexOfSpinnerItemWithName(int id) {
        for(int i = 0; i < spinnerItems.length; i++) {
            if(spinnerItems[i].getId() == id) return i;
        }
        return -1;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.menu_add_category_or_time_done):
                //TODO: Highlight missing box (Wiggle?)
                //TODO: Error message not as toast

                if (Time.toLong(start) >= Time.toLong(end)) {
                    showToast("The start must come before the end.", getApplicationContext());
                } else {
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.editEntryTime(idToEdit, start, end, activity.getId());
                    dbHelper.close();

                    showToast("Edited Time-Entry!", getApplicationContext());
                    finish();
                }
                break;

            default:
                Log.e(TAG, "Did not find menu item");
        }
        return super.onOptionsItemSelected(item);
    }
}
