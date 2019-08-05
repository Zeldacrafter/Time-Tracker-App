package com.uni.time_tracking.activities.mainScreen.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.uni.time_tracking.R;
import com.uni.time_tracking.activities.ActivityWithBackButton;


public class ThirdPartyList extends ActivityWithBackButton {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party_list);
    }
}
