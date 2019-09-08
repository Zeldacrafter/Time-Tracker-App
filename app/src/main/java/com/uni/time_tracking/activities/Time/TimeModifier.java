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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.activities.ActivityWithBackButton;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.uni.time_tracking.Utils._assert;

public abstract class TimeModifier extends ActivityWithBackButton implements AdapterView.OnItemSelectedListener {

    private static final String BUNDLE_MODE = "Bundle_Mode";

    public static final String BUNDLE_ID = "Time_ID";
    public static final String BUNDLE_ACTIVITY_ID = "Activity_ID";
    public static final String BUNDLE_START_TIME = "Start_Time";
    public static final String BUNDLE_END_TIME = "End_Time";
    public static final String BUNDLE_STILL_ACTIVE = "Still_Active";

    /**
     * This can only be 'true' if we are in {@link EditTime}.
     * TODO: Move this there somehow?
     */
    protected boolean isStillActive;
    protected DateTime endTimeAtBeginning;

    protected enum Mode {
        START,
        END
    }

    //TODO: Save TimeDB instance instead of seperate values.

    /** Time entry to modify/add. All its values are initially passed from the calling activity. */
    protected TimeDB timeEntry;
    protected ActivityDB activity;
    protected ActivityDB[] spinnerItems;

    @BindView(R.id.add_time_layout) protected LinearLayout layout;
    @BindView(R.id.add_time_category_spinner) protected Spinner categorySpinner;
    @BindView(R.id.add_time_select_start_time) protected Button startTime;
    @BindView(R.id.add_time_select_start_date) protected Button startDate;
    @BindView(R.id.add_time_select_end_time) protected Button endTime;
    @BindView(R.id.add_time_select_end_date) protected Button endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);
        ButterKnife.bind(this);

        // Get information about entry to edit from caller.
        Bundle extras = getIntent().getExtras();
        _assert(extras != null);
        isStillActive = extras.getBoolean(BUNDLE_STILL_ACTIVE, false);
        int timeID = extras.getInt(BUNDLE_ID, TimeDB.NO_ID_VALUE);
        DateTime start = Time.fromLong(extras.getLong(BUNDLE_START_TIME));
        // If this has not been passed we are in the following scenario:
        // We want to edit an activity-entry that is still running -> no end set yet.
        DateTime end = this instanceof EditTime && isStillActive ?
                Time.getCurrentTime()
                    :
                Time.fromLong(extras.getLong(BUNDLE_END_TIME));
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

        //plus(0) to get a copy not a reference
        //TODO: This variable is only used in EditTime
        endTimeAtBeginning = timeEntry.getEnd().plus(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu_confirm, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        activity = (ActivityDB)parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { /* Do nothing. */ }

    @OnClick(R.id.add_time_select_start_time)
    public void selectStartTimeClicked() {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        bundle.putString(BUNDLE_MODE, Mode.START.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectTimeDialogFragment dialog = new SelectTimeDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, null);
    }

    @OnClick(R.id.add_time_select_start_date)
    public void selectStartDateClicked() {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        bundle.putString(BUNDLE_MODE, Mode.START.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, null);
    }

    @OnClick(R.id.add_time_select_end_time)
    public void selectEndTimeClicked() {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        bundle.putString(BUNDLE_MODE, Mode.END.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectTimeDialogFragment dialog = new SelectTimeDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, null);
    }

    @OnClick(R.id.add_time_select_end_date)
    public void selectEndDateClicked() {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        bundle.putString(BUNDLE_MODE, Mode.END.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, null);
    }

    @OnClick(R.id.add_time_minus_15_start_time)
    public void minus15StartClicked() {
        timeEntry.setStart(timeEntry.getStart().minusMinutes(15));
        startTime.setText(Time.toTimeString(timeEntry.getStart()));
    }

    @OnClick(R.id.add_time_minus_5_start_time)
    public void minus5StartClicked() {
        timeEntry.setStart(timeEntry.getStart().minusMinutes(5));
        startTime.setText(Time.toTimeString(timeEntry.getStart()));
    }

    @OnClick(R.id.add_time_plus_5_start_time)
    public void plus5StartClicked() {
        timeEntry.setStart(timeEntry.getStart().plusMinutes(5));
        startTime.setText(Time.toTimeString(timeEntry.getStart()));
    }

    @OnClick(R.id.add_time_plus_15_start_time)
    public void plus15StartClicked() {
        timeEntry.setStart(timeEntry.getStart().plusMinutes(15));
        startTime.setText(Time.toTimeString(timeEntry.getStart()));
    }

    @OnClick(R.id.add_time_minus_15_end_time)
    public void minus15EndClicked() {
        timeEntry.setEnd(timeEntry.getEnd().minusMinutes(15));
        endTime.setText(Time.toTimeString(timeEntry.getEnd()));
    }

    @OnClick(R.id.add_time_minus_5_end_time)
    public void minus5EndClicked() {
        timeEntry.setEnd(timeEntry.getEnd().minusMinutes(5));
        endTime.setText(Time.toTimeString(timeEntry.getEnd()));
    }

    @OnClick(R.id.add_time_plus_5_end_time)
    public void plus5EndClicked() {
        timeEntry.setEnd(timeEntry.getEnd().plusMinutes(5));
        endTime.setText(Time.toTimeString(timeEntry.getEnd()));
    }

    @OnClick(R.id.add_time_plus_15_end_time)
    public void plus15EndClicked() {
        timeEntry.setEnd(timeEntry.getEnd().plusMinutes(15));
        endTime.setText(Time.toTimeString(timeEntry.getEnd()));
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

    public static class SelectDateDialogFragment extends DialogFragment {

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

    public static class SelectTimeDialogFragment extends DialogFragment {

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