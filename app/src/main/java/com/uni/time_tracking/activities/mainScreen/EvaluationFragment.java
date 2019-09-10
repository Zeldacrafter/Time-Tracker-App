package com.uni.time_tracking.activities.mainScreen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;

import org.joda.time.Period;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.uni.time_tracking.Time.getDifferenceLong;

public class EvaluationFragment extends Fragment implements OnChartValueSelectedListener {

    private static final String TAG = "EvaluationFragment";

    @BindView(R.id.evaluation_bar_chart) protected BarChart chart;
    private Unbinder unbinder;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getLegend().setWordWrapEnabled(true);

        setData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_evaluation, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setData() {

        DBHelper dbHelper = DBHelper.getInstance(getContext());

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();

        ActivityDB[] activities = dbHelper.getActiveActivities();

        int pos = 1;
        for (ActivityDB activity : activities){

            float totalTimeSpent = 0;
            TimeDB[] entries = dbHelper.getEntriesInTimespan(
                    activity.getId(),
                    Time.getCurrentTime().minusDays(7),
                    Time.getCurrentTime());

            for(TimeDB entry : entries) {
                totalTimeSpent += getDifferenceLong(new Period(entry.getStart(), entry.getEnd())) / 10000.0f;
            }

            ArrayList<BarEntry> values = new ArrayList<>();
            values.add(new BarEntry(pos, totalTimeSpent));

            BarDataSet set1 = new BarDataSet(values, activity.getName());
            set1.setDrawIcons(false);
            set1.setColor(activity.getColor());

            dataSets.add(set1);

            pos++;
        }

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        chart.setData(data);
        chart.invalidate();

        dbHelper.close();
    }

    public String randomString() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
