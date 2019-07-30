package com.uni.time_tracking.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.uni.time_tracking.R;
import com.uni.time_tracking.activities.MainScreen.DeleteActivityDialogFragment;

public class AddTime extends AppCompatActivity {

    public static final String TAG = "AddTimeActivity";

    LinearLayout layout;
    Spinner categorySpinner;
    Button selectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        layout = findViewById(R.id.add_time_layout);
        categorySpinner = findViewById(R.id.add_time_category_spinner);
        selectDate = findViewById(R.id.add_time_select_date);
    }

    public void selectDateClicked(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.show(fragmentManager, TAG);
    }
}
