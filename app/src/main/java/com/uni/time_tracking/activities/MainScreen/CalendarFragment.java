package com.uni.time_tracking.activities.MainScreen;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.activities.Time.AddTime;
import com.uni.time_tracking.activities.Time.EditTime;
import com.uni.time_tracking.activities.Time.TimeModifier;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends BaseCalendarFragment {

    private static final String TAG = "CalendarFragment";

    private long uniqueId = 0;

    private WeekView weekView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weekView = view.findViewById(R.id.fragment_week_view);
        weekView.setTypeface(ResourcesCompat.getFont(getActivity().getApplicationContext(), R.font.lato));
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<>();

        //TODO: Big speed problem:
        //  Don't get all Categories, only the one relevant
        //  Don't search through all entries in dbHelper.getAllEventsInMonth.
        //      Filter them out in the SQLite query directly?
        //          -> Save Date as YYYYMMDD integer?

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        TimeDB[] dbEvents = dbHelper.getAllEventsInMonth(newYear, newMonth);
        ActivityDB[] activites = dbHelper.getActiveActivities();
        dbHelper.close();

        for(TimeDB event : dbEvents) {

            ActivityDB activity = findActivityWithId(activites, event.getActivityID());
            if(activity != null) {
                Calendar startTime = event.getStart().toCalendar(Locale.getDefault());
                Calendar endTime = event.getEnd().toCalendar(Locale.getDefault());

                //NOTE: We set the id to the end-id so that we can later retrieve it
                //      when we want to edit a event in onEventClick
                WeekViewEvent weekViewEvent = new WeekViewEvent(event.getId() + "", activity.getName(), startTime, endTime);
                weekViewEvent.setColor(activity.getColor());

                events.add(weekViewEvent);
            }
        }

        return events;

    }

    private ActivityDB findActivityWithId(ActivityDB[] activities, int id) {
        for(ActivityDB a : activities) {
            if(a.getId() == id) return a;
        }
        return null;
    }

    @Override
    public void onEventClick(@NotNull WeekViewEvent event, @NotNull RectF rectF) {
        Intent editTimeEntry = new Intent(getContext(), EditTime.class);

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        TimeDB timeEntry = dbHelper.getTimeEntry(Integer.parseInt(event.getId()));
        dbHelper.close();

        editTimeEntry.putExtra(TimeModifier.BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        editTimeEntry.putExtra(TimeModifier.BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        editTimeEntry.putExtra(EditTime.BUNDLE_ACTIVITY_ID, timeEntry.getActivityID());
        editTimeEntry.putExtra(EditTime.BUNDLE_TIME_ID, timeEntry.getId());


        startActivity(editTimeEntry);
    }

    @Override
    public void onAddEventClicked(@NotNull Calendar calendar, @NotNull Calendar calendar1) {
        Intent addTimeEntry = new Intent(getContext(), AddTime.class);

        addTimeEntry.putExtra(TimeModifier.BUNDLE_START_TIME, Time.toLong(Time.fromCalendar(calendar)));
        addTimeEntry.putExtra(TimeModifier.BUNDLE_END_TIME, Time.toLong(Time.fromCalendar(calendar1)));

        startActivity(addTimeEntry);
    }
}
