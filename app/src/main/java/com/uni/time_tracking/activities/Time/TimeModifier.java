package com.uni.time_tracking.activities.time;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

public abstract class TimeModifier extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String BUNDLE_MODE = "Bundle_Mode";

    public static final String BUNDLE_ID = "Time_ID";
    public static final String BUNDLE_ACTIVITY_ID = "Activity_ID";
    public static final String BUNDLE_START_TIME = "Start_Time";
    public static final String BUNDLE_END_TIME = "End_Time";

    protected enum Mode {
        START,
        END
    }

    //TODO: Save TimeDB instance instead of seperate values.

    /** Time entry to modify/add. All its values are initially passed from the calling activity. */
    protected TimeDB timeEntry;
    protected ActivityDB activity;
    protected ActivityDB[] spinnerItems;

    protected LinearLayout layout;
    protected Spinner categorySpinner;
    protected Button startTime;
    protected Button startDate;
    protected Button endTime;
    protected Button endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        layout = findViewById(R.id.add_time_layout);
        categorySpinner = findViewById(R.id.add_time_category_spinner);
        startTime = findViewById(R.id.add_time_select_start_time);
        startDate = findViewById(R.id.add_time_select_start_date);
        endTime = findViewById(R.id.add_time_select_end_time);
        endDate = findViewById(R.id.add_time_select_end_date);

        Bundle extras = getIntent().getExtras();
        int timeID = extras.getInt(BUNDLE_ID, TimeDB.NO_ID_VALUE);
        DateTime start = Time.fromLong(extras.getLong(BUNDLE_START_TIME));
        DateTime end = Time.fromLong(extras.getLong(BUNDLE_END_TIME));
        int timeActivityID = extras.getInt(BUNDLE_ACTIVITY_ID, TimeDB.NO_ID_VALUE);
        timeEntry = new TimeDB(timeID, start, end, timeActivityID);

        startTime.setText(Time.toTimeString(timeEntry.getStart()));
        startDate.setText(Time.toDateString(timeEntry.getStart()));
        endTime.setText(Time.toTimeString(timeEntry.getEnd()));
        endDate.setText(Time.toDateString(timeEntry.getEnd()));

        DBHelper dbHelper = DBHelper.getInstance(this);
        spinnerItems = dbHelper.getActiveActivities();
        dbHelper.close();

        ArrayAdapter<ActivityDB> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setOnItemSelectedListener(this);
        categorySpinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu bar at top
        getMenuInflater().inflate(R.menu.top_menu_add_category_or_time, menu);
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        activity = (ActivityDB)parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { /* Do nothing. */ }

    public void selectStartTimeClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        bundle.putString(BUNDLE_MODE, Mode.START.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectTimeDialogFragment dialog = new SelectTimeDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, null);
    }

    public void selectStartDateClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        bundle.putString(BUNDLE_MODE, Mode.START.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, null);
    }

    public void selectEndTimeClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        bundle.putString(BUNDLE_MODE, Mode.END.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectTimeDialogFragment dialog = new SelectTimeDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, null);
    }

    public void selectEndDateClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        bundle.putString(BUNDLE_MODE, Mode.END.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, null);
    }

    protected void newStartTime(int hour, int minute) {
        timeEntry.setStart(new DateTime(timeEntry.getStart().getYear(), timeEntry.getStart().getMonthOfYear(), timeEntry.getStart().getDayOfMonth(), hour, minute));
        startTime.setText(Time.toTimeString(timeEntry.getStart()));
    }

    protected void newStartDate(int year, int month, int day) {
        timeEntry.setStart(new DateTime(year, month, day, timeEntry.getStart().getHourOfDay(), timeEntry.getStart().getMinuteOfHour(), timeEntry.getStart().getSecondOfMinute()));
        startDate.setText(Time.toDateString(timeEntry.getStart()));
    }

    protected void newEndTime(int hour, int minute) {
        timeEntry.setEnd(new DateTime(timeEntry.getEnd().getYear(), timeEntry.getEnd().getMonthOfYear(), timeEntry.getEnd().getDayOfMonth(), hour, minute));
        endTime.setText(Time.toTimeString(timeEntry.getEnd()));
    }

    protected void newEndDate(int year, int month, int day) {
        timeEntry.setEnd(new DateTime(year, month, day, timeEntry.getEnd().getHourOfDay(), timeEntry.getEnd().getMinuteOfHour(), timeEntry.getEnd().getSecondOfMinute()));
        endDate.setText(Time.toDateString(timeEntry.getEnd()));
    }

    private static class SelectDateDialogFragment extends DialogFragment {

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            TimeModifier caller = (TimeModifier)getActivity();

            Mode mode = Mode.valueOf(getArguments().getString(BUNDLE_MODE));
            DateTime startTime = Time.fromLong(getArguments().getLong(BUNDLE_START_TIME));
            DateTime endTime = Time.fromLong(getArguments().getLong(BUNDLE_END_TIME));

            LayoutInflater inflater = getActivity().getLayoutInflater();
            MaterialCalendarView calendarView = (MaterialCalendarView)inflater.inflate(R.layout.popout_select_date, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            switch (mode) {
                case START:
                    CalendarDay startDate = CalendarDay.from(startTime.getYear(), startTime.getMonthOfYear(), startTime.getDayOfMonth());
                    calendarView.setSelectedDate(startDate);
                    calendarView.setCurrentDate(startDate);
                    builder.setTitle("Select Start Date");
                    builder.setPositiveButton("Select", (dialog, id) -> {
                        CalendarDay day = calendarView.getSelectedDate();
                        caller.newStartDate(day.getYear(), day.getMonth(), day.getDay());
                    });
                    break;
                case END:
                    CalendarDay endDate = CalendarDay.from(endTime.getYear(), endTime.getMonthOfYear(), endTime.getDayOfMonth());
                    calendarView.setSelectedDate(endDate);
                    calendarView.setCurrentDate(endDate);
                    builder.setTitle("Select End Date");
                    builder.setPositiveButton("Select", (dialog, id) -> {
                        CalendarDay day = calendarView.getSelectedDate();
                        caller.newEndDate(day.getYear(), day.getMonth(), day.getDay());
                    });
                    break;
            }
            builder.setView(calendarView);
            builder.setNegativeButton("Cancel", (dialog, id) -> {});

            return builder.create();
        }
    }

    private static class SelectTimeDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

            TimeModifier caller = (TimeModifier)getActivity();

            Mode mode = Mode.valueOf(getArguments().getString(BUNDLE_MODE));
            DateTime startTime = Time.fromLong(getArguments().getLong(BUNDLE_START_TIME));
            DateTime endTime = Time.fromLong(getArguments().getLong(BUNDLE_END_TIME));

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            TimePicker timePicker = new TimePicker(getContext());
            switch (mode) {
                case START:
                    timePicker.setHour(startTime.getHourOfDay());
                    timePicker.setMinute(startTime.getMinuteOfHour());
                    builder.setPositiveButton("Select", (dialog, id) -> caller.newStartTime(timePicker.getHour(), timePicker.getMinute()));
                    break;
                case END:
                    timePicker.setHour(endTime.getHourOfDay());
                    timePicker.setMinute(endTime.getMinuteOfHour());
                    builder.setPositiveButton("Select", (dialog, id) -> caller.newEndTime(timePicker.getHour(), timePicker.getMinute()));
                    break;
            }
            timePicker.setIs24HourView(true); //TODO: Toggleable from options..
            builder.setView(timePicker);
            builder.setNegativeButton("Cancel", (dialog, id) -> {});

            return builder.create();
        }
    }
}