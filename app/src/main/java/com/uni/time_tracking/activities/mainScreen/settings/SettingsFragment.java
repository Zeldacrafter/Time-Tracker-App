package com.uni.time_tracking.activities.mainScreen.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.uni.time_tracking.Preferences;
import com.uni.time_tracking.R;

import static com.uni.time_tracking.Utils._assert;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference thirdParty;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        thirdParty = findPreference(Preferences.KEY_THIRD_PARTY);
        _assert(thirdParty != null);
        thirdParty.setOnPreferenceClickListener(preference -> {
            Log.d("ABC", "ABC");
            Intent intent = new Intent(getContext(), ThirdPartyList.class);
            startActivity(intent);
            return true;
        });
    }
}