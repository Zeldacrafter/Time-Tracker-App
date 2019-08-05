package com.uni.time_tracking.activities.mainScreen.settings;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.uni.time_tracking.R;
import com.uni.time_tracking.activities.ActivityWithBackButton;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ThirdPartyList extends ActivityWithBackButton {

    @BindView(R.id.third_party_list_text) protected TextView thirdPartyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party_list);
        ButterKnife.bind(this);

        // Make links in text clickable
        thirdPartyList.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
