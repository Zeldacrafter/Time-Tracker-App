package com.uni.time_tracking.activities.mainScreen;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
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

    @BindView(R.id.evaluation_bar_chart) protected BarChart barChart;
    @BindView(R.id.evaluation_pie_chart) protected PieChart pieChart;
    private Unbinder unbinder;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setEnabled(false);
        barChart.getLegend().setWordWrapEnabled(true);
        setDataBarChart();

        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawCenterText(false);
        pieChart.setDrawEntryLabels(false);
        setDataPieChart();
        pieChart.getData().setValueTextSize(15);
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

    private void setDataBarChart() {

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

        barChart.setData(data);
        barChart.invalidate();

        dbHelper.close();
    }

    private void setDataPieChart() {
        DBHelper dbHelper = DBHelper.getInstance(getContext());

        ArrayList<PieEntry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        ActivityDB[] activities = dbHelper.getActiveActivities();

        for (ActivityDB activity : activities){

            float totalTimeSpent = 0;
            TimeDB[] entries = dbHelper.getEntriesInTimespan(
                    activity.getId(),
                    Time.getCurrentTime().minusDays(7),
                    Time.getCurrentTime());

            for(TimeDB entry : entries) {
                totalTimeSpent += getDifferenceLong(new Period(entry.getStart(), entry.getEnd())) / 10000.0f;
            }

            values.add(new PieEntry(totalTimeSpent, activity.getName()));
            colors.add(activity.getColor());
        }
        PieDataSet set = new PieDataSet(values, "");
        set.setDrawIcons(false);
        set.setColors(colors);
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.invalidate();

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
