package com.uni.time_tracking.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import static com.uni.time_tracking.General.showToast;

public class AddTime extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "AddTime";
    public static final String BUNDLE_START_TIME = "Start_Time";
    public static final String BUNDLE_END_TIME = "End_Time";
    private static final String BUNDLE_MDOE = "Bundle_Mode";

    private enum Mode {
        START,
        END
    }

    DateTime start;
    DateTime end;
    ActivityDB activity;

    LinearLayout layout;
    Spinner categorySpinner;
    Button startTime;
    Button startDate;
    Button endTime;
    Button endDate;

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
        start = Time.fromLong(extras.getLong(BUNDLE_START_TIME));
        end = Time.fromLong(extras.getLong(BUNDLE_END_TIME));

        startTime.setText(Time.toTimeString(start));
        startDate.setText(Time.toDateString(start));
        endTime.setText(Time.toTimeString(end));
        endDate.setText(Time.toDateString(end));

        DBHelper dbHelper = DBHelper.getInstance(this);
        ActivityDB[] activities = dbHelper.getActiveActivities();
        dbHelper.close();

        ArrayAdapter<ActivityDB> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activities);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.menu_add_category_or_time_done):
                //TODO: Highlight missing box (Wiggle?)
                //TODO: Error message not as toast

                if (Time.toLong(start) >= Time.toLong(end)) {
                    showToast("The start must come before the end.", getApplicationContext());
                } else {

                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.addEntryTime(start, end, activity.getId());
                    dbHelper.close();

                    showToast("Saved new Category!", getApplicationContext());
                    finish();
                }
                break;

            default:
                Log.e(TAG, "Did not find menu item");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        activity = (ActivityDB)parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { /* Do nothing. */ }

    public void selectStartTimeClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(start));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(end));
        bundle.putString(BUNDLE_MDOE, Mode.START.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectTimeDialogFragment dialog = new SelectTimeDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, TAG);
    }

    public void selectStartDateClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(start));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(end));
        bundle.putString(BUNDLE_MDOE, Mode.START.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, TAG);
    }

    public void selectEndTimeClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(start));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(end));
        bundle.putString(BUNDLE_MDOE, Mode.END.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectTimeDialogFragment dialog = new SelectTimeDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, TAG);
    }

    public void selectEndDateClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(start));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(end));
        bundle.putString(BUNDLE_MDOE, Mode.END.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, TAG);
    }

    public void newStartTime(int hour, int minute) {
        start = new DateTime(start.getYear(), start.getMonthOfYear(), start.getDayOfMonth(), hour, minute);
        startTime.setText(Time.toTimeString(start));
    }

    public void newStartDate(int year, int month, int day) {
        start = new DateTime(year, month, day, start.getHourOfDay(), start.getMinuteOfHour(), start.getSecondOfMinute());
        startDate.setText(Time.toDateString(start));
    }

    public void newEndTime(int hour, int minute) {
        end = new DateTime(end.getYear(), end.getMonthOfYear(), end.getDayOfMonth(), hour, minute);
        endTime.setText(Time.toTimeString(end));
    }

    public void newEndDate(int year, int month, int day) {
        end = new DateTime(year, month, day, start.getHourOfDay(), start.getMinuteOfHour(), start.getSecondOfMinute());
        endDate.setText(Time.toDateString(end));
    }


    private static class SelectDateDialogFragment extends DialogFragment {

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AddTime caller = (AddTime)getActivity();

            Mode mode = Mode.valueOf(getArguments().getString(BUNDLE_MDOE));
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

            AddTime caller =  (AddTime)getActivity();

            Mode mode = Mode.valueOf(getArguments().getString(BUNDLE_MDOE));
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
