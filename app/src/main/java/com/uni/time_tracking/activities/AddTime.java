package com.uni.time_tracking.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

public class AddTime extends AppCompatActivity {

    public static final String TAG = "AddTime";
    public static final String BUNDLE_START_TIME = "Start_Time";
    public static final String BUNDLE_END_TIME = "End_Time";

    DateTime start;
    DateTime end;

    LinearLayout layout;
    Spinner categorySpinner;
    Button startDate;
    Button endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        layout = findViewById(R.id.add_time_layout);
        categorySpinner = findViewById(R.id.add_time_category_spinner);
        startDate = findViewById(R.id.add_time_select_start_date);
        endDate = findViewById(R.id.add_time_select_end_date);

        Bundle extras = getIntent().getExtras();
        start = Time.fromLong(extras.getLong(BUNDLE_START_TIME));
        end = Time.fromLong(extras.getLong(BUNDLE_END_TIME));

        startDate.setText(Time.toDateString(start));
        endDate.setText(Time.toDateString(end));
    }

    public void selectStartDateClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(start));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(end));
        bundle.putString(SelectDateDialogFragment.BUNDLE_MDOE, SelectDateDialogFragment.Mode.START.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, TAG);
    }

    public void selectEndDateClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_START_TIME, Time.toLong(start));
        bundle.putLong(BUNDLE_END_TIME, Time.toLong(end));
        bundle.putString(SelectDateDialogFragment.BUNDLE_MDOE, SelectDateDialogFragment.Mode.END.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectDateDialogFragment dialog = new SelectDateDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, TAG);
    }

    public void newStartDate(DateTime newDate) {
        start = new DateTime(newDate.getYear(), newDate.getMonthOfYear(), newDate.getDayOfMonth(),
                start.getHourOfDay(), start.getMinuteOfHour(), start.getSecondOfMinute());

        startDate.setText(Time.toDateString(start));
    }

    public void newEndDate(DateTime newDate) {
        end = new DateTime(newDate.getYear(), newDate.getMonthOfYear(), newDate.getDayOfMonth(),
                start.getHourOfDay(), start.getMinuteOfHour(), start.getSecondOfMinute());

        endDate.setText(Time.toDateString(end));
    }



    private static class SelectDateDialogFragment extends DialogFragment {

        private static final String BUNDLE_MDOE = "Bundle_Mode";

        private enum Mode {
            START,
            END
        }

        private MaterialCalendarView calendarView;

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Mode mode = Mode.valueOf(getArguments().getString(BUNDLE_MDOE));

            // Use the Builder class for convenient dialog construction

            LayoutInflater inflater = getActivity().getLayoutInflater();
            calendarView = (MaterialCalendarView)inflater.inflate(R.layout.popout_select_date, null);

            switch (mode) {
                case START:
                    DateTime startTime = Time.fromLong(getArguments().getLong(BUNDLE_START_TIME));
                    CalendarDay startDate = CalendarDay.from(startTime.getYear(), startTime.getMonthOfYear(), startTime.getDayOfMonth());
                    calendarView.setSelectedDate(startDate);
                    calendarView.setCurrentDate(startDate);
                    break;
                case END:
                    DateTime endTime = Time.fromLong(getArguments().getLong(BUNDLE_END_TIME));
                    CalendarDay endDate = CalendarDay.from(endTime.getYear(), endTime.getMonthOfYear(), endTime.getDayOfMonth());
                    calendarView.setSelectedDate(endDate);
                    calendarView.setCurrentDate(endDate);
                    break;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Date");
            builder.setView(calendarView);
            builder.setPositiveButton("Select", (dialog, id) -> {

                CalendarDay day = calendarView.getSelectedDate();
                DateTime newDate = new DateTime(
                        day.getYear(), day.getMonth(), day.getDay(),
                        0, 0, Time.getTimezone());

                switch(mode) {
                    case START:
                        ((AddTime)getActivity()).newStartDate(newDate);
                        break;
                    case END:
                        ((AddTime)getActivity()).newEndDate(newDate);
                        break;
                }
            });
            builder.setNegativeButton("Cancel", (dialog, id) -> {});

            return builder.create();
        }
    }
}
