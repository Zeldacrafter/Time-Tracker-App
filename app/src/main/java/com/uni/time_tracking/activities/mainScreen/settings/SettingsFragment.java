package com.uni.time_tracking.activities.mainScreen.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.uni.time_tracking.Preferences;
import com.uni.time_tracking.R;
import com.uni.time_tracking.Utils;
import com.uni.time_tracking.database.DBHelper;

import org.jetbrains.annotations.NotNull;

import static com.uni.time_tracking.Utils._assert;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference thirdParty = findPreference(Preferences.KEY_THIRD_PARTY);
        Preference deleteData = findPreference(Preferences.KEY_DELETE_DATA);
        _assert(thirdParty != null);
        _assert(deleteData != null);

        thirdParty.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), ThirdPartyList.class);
            startActivity(intent);
            return true;
        });

        deleteData.setOnPreferenceClickListener(preference -> {
            _assert(getActivity() != null);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ResetDatabaseDialog dialog = new ResetDatabaseDialog();
            dialog.show(fragmentManager, null);
            return true;
        });
    }

    public static class ResetDatabaseDialog extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Delete all app data?")
                    .setPositiveButton("Delete", (dialog, id) -> {
                        DBHelper.getInstance(getContext()).resetDatabase();
                        Utils.showToast("All Data has been reset!", getContext());
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {});
            return builder.create();
        }
    }

}