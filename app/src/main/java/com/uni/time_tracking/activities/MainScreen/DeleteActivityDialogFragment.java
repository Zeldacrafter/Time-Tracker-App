package com.uni.time_tracking.activities.MainScreen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.uni.time_tracking.database.DBHelper;

import org.jetbrains.annotations.NotNull;

public class DeleteActivityDialogFragment extends DialogFragment {

    public static final String BUNDLE_ACTIVITY_NAME = "Activity_Name";
    public static final String BUNDLE_ACTIVITY_ID = "Activity_ID";

    private String activityName;
    private int activityID;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        activityName = getArguments().getString(BUNDLE_ACTIVITY_NAME);
        activityID = getArguments().getInt(BUNDLE_ACTIVITY_ID);

        assert(activityName != null && activityID != 0) : "No argument passed";

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete Category \"" + activityName + "\"?\n" +
                        "This will also delete all entries of that Category.")
                .setPositiveButton("Delete", (dialog, id) -> {
                    DBHelper dbHelper = new DBHelper(getContext());
                    dbHelper.deleteActivity(activityID);
                    dbHelper.close();
                    ((CategoryListFragment)getTargetFragment()).addCategoriesToList();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {});
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
