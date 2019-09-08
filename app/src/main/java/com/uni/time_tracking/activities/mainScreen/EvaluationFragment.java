package com.uni.time_tracking.activities.mainScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.uni.time_tracking.R;

public class EvaluationFragment extends Fragment {

    private static final String TAG = "EvaluationFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_evaluation, container, false);
    }

}
