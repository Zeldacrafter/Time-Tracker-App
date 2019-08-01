package com.uni.time_tracking.activities.time;

import android.os.Bundle;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    dbHelper.addEntryTime(start, end, activity.getId());
                    dbHelper.close();

                    showToast("Saved new Time-Entry!", getApplicationContext());
                    finish();
                }
                break;

            default:
                Log.e(TAG, "Did not find menu item");
        }
        return super.onOptionsItemSelected(item);
    }

}
