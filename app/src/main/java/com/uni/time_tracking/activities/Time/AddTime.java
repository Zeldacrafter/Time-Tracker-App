package com.uni.time_tracking.activities.time;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.database.DBHelper;

import static com.uni.time_tracking.Utils.showToast;

public class AddTime extends TimeModifier {

    public static final String TAG = "AddTime";

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
                    dbHelper.addEntryTime(timeEntry.getStart(), timeEntry.getEnd(), activity.getId());
                    dbHelper.close();

                    showToast("Saved new Time-Entry!", getApplicationContext());
                    finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
