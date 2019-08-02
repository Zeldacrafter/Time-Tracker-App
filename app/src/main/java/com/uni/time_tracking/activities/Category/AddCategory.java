package com.uni.time_tracking.activities.category;

import androidx.annotation.NonNull;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.uni.time_tracking.R;
import com.uni.time_tracking.database.DBHelper;

import static com.uni.time_tracking.Utils.showToast;

public class AddCategory extends CategoryModifier {

    public static final String TAG = "AddCategory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentColor = Color.RED;
        colorPicker.setBackgroundColor(currentColor);
        colorPickerDialog.setInitialColor(currentColor);
        colorPickerDialog.setLastColor(currentColor);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.top_menu_confirm):
                if ("".equals(nameText.getText().toString())) {
                    //TODO: Highlight missing box (Wiggle?)
                    //TODO: Error message not as toast
                    showToast("Please name the new category", getApplicationContext());
                } else {
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.addEntryActivity(nameText.getText().toString(), currentColor);
                    dbHelper.close();

                    showToast("Saved new Category!", getApplicationContext());
                    finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
