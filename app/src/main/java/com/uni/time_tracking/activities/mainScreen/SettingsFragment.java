package com.uni.time_tracking.activities.mainScreen;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.uni.time_tracking.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}