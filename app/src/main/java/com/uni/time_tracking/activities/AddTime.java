package com.uni.time_tracking.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.uni.time_tracking.R;

import org.jetbrains.annotations.NotNull;

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

    private static class SelectDateDialogFragment extends DialogFragment {

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Date");


            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.popout_select_date, null);
            builder.setView(view);

            builder.setPositiveButton("Select", (dialog, id) -> {});
            builder.setNegativeButton("Cancel", (dialog, id) -> {});

            return builder.create();
        }
    }
}
