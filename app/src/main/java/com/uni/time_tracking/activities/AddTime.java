package com.uni.time_tracking.activities;

import android.os.Bundle;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.uni.time_tracking.R;

public class AddTime extends AppCompatActivity {

    public static final String TAG = "AddTimeActivity";

    Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        categorySpinner = findViewById(R.id.add_time_category_spinner);
    }
}
