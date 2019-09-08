package com.uni.time_tracking.activities.time;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.database.DBHelper;

import static com.uni.time_tracking.Utils._assert;
import static com.uni.time_tracking.Utils.showToast;

public class EditTime extends TimeModifier {

    public static final String TAG = "EditTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _assert(getIndexOfSpinnerItemWithName(timeEntry.getActivityID()) != -1);
        categorySpinner.setSelection(getIndexOfSpinnerItemWithName(timeEntry.getActivityID()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.top_menu_confirm):
                //TODO: Highlight missing box (Wiggle?)
                //TODO: Error message not as toast

                if (Time.toLong(timeEntry.getStart()) >= Time.toLong(timeEntry.getEnd())) {
                    showToast("The start must come before the end.", getApplicationContext());
                } else {
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.editEntryTime(timeEntry.getId(), timeEntry.getStart(), timeEntry.getEnd(), activity.getId());
                    if (isStillActive && timeEntry.getEnd().equals(endTimeAtBeginning)) {
                        dbHelper.makeTimeEntryEndNull(timeEntry.getId());
                    } else { /* TODO: Popup with warning? */ }
                    dbHelper.close();

                    showToast("Edited Time-Entry!", getApplicationContext());
                    finish();
                }
                return true;

            case (R.id.top_menu_delete):
                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                dbHelper.deleteTimeEntry(timeEntry.getId());
                dbHelper.close();

                showToast("Deleted Time-Entry!", getApplicationContext());
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Returns the position of the item with the specified ID in the spinner.
     * @param id The id of the ActivityDB entry.
     * @return The position of the ActivityDB entry in the spinner. Returns -1 if not found.
     */
    private int getIndexOfSpinnerItemWithName(int id) {
        for(int i = 0; i < spinnerItems.length; i++) {
            if(spinnerItems[i].getId() == id) return i;
        }
        return -1;
    }
}
