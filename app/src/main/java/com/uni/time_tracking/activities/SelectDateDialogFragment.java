package com.uni.time_tracking.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.uni.time_tracking.R;

import org.jetbrains.annotations.NotNull;

public class SelectDateDialogFragment extends DialogFragment {

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
