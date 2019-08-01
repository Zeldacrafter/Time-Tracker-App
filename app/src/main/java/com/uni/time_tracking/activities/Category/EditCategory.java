package com.uni.time_tracking.activities.category;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.uni.time_tracking.R;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;

import static com.uni.time_tracking.Utils._assert;
import static com.uni.time_tracking.Utils.showToast;

public class EditCategory extends CategoryModifier {

    public static final String TAG = "EditCategory";

    public static final String BUNDLE_ACTIVITY_ID = "Activity_ID";
    private ActivityDB activityToEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _assert(getIntent().getExtras() != null, "");
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        int activityID = getIntent().getExtras().getInt(BUNDLE_ACTIVITY_ID);
        activityToEdit = dbHelper.getActivity(activityID);
        dbHelper.close();

        nameText.setText(activityToEdit.getName());

        currentColor = activityToEdit.getColor();
        colorPicker.setBackgroundColor(currentColor);
        colorPickerDialog.setInitialColor(currentColor);
        colorPickerDialog.setLastColor(currentColor);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_add_category_or_time_done):
                if ("".equals(nameText.getText().toString())) {
                    //TODO: Highlight missing box (Wiggle?)
                    //TODO: Error message not as toast
                    showToast("Please enter a category name!", getApplicationContext());
                } else {
                    activityToEdit.setName(nameText.getText().toString());
                    activityToEdit.setColor(colorPickerDialog.getCurrentColor());

                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.editActivity(activityToEdit.getId(), activityToEdit);
                    dbHelper.close();

                    showToast("Edited Category successfully!", getApplicationContext());
                    finish();
                }
                break;

            default:
                Log.e(TAG, "Did not find menu item");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
