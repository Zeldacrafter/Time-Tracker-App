package com.uni.time_tracking.activities.mainScreen;

import android.content.Intent;
import android.graphics.RectF;

import com.alamkanak.weekview.WeekViewEvent;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.activities.time.AddTime;
import com.uni.time_tracking.activities.time.EditTime;
import com.uni.time_tracking.activities.time.TimeModifier;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.uni.time_tracking.Utils._assert;

public class CalendarFragment extends BaseCalendarFragment {

    private static final String TAG = "CalendarFragment";

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        TimeDB[] dbEvents = dbHelper.getAllActiveEventsInMonth(newYear, newMonth);
        ActivityDB[] activities = dbHelper.getActiveActivities();
        dbHelper.close();

        for(TimeDB event : dbEvents) {

            ActivityDB activity = findActivityWithId(activities, event.getActivityID());
            _assert(activity != null,
                    "Activity with ID " + event.getActivityID() + "could not be found.");

            Calendar startTime = event.getStart().toCalendar(Locale.getDefault());
            Calendar endTime = event.getEnd().toCalendar(Locale.getDefault());

            //NOTE: We set the ID to be the event-ID so that we can later retrieve it
            //      when we want to edit an event in onEventClick!
            WeekViewEvent weekViewEvent = new WeekViewEvent(event.getId() + "",
                    activity.getName(), startTime, endTime);
            weekViewEvent.setColor(activity.getColor());

            events.add(weekViewEvent);
        }

        return events;
    }

    @Override
    public void onEventClick(@NotNull WeekViewEvent event, @NotNull RectF rectF) {
        Intent editTimeEntry = new Intent(getContext(), EditTime.class);

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        TimeDB timeEntry = dbHelper.getTimeEntry(Integer.parseInt(event.getId()));
        dbHelper.close();

        editTimeEntry.putExtra(TimeModifier.BUNDLE_ID, timeEntry.getId());
        editTimeEntry.putExtra(TimeModifier.BUNDLE_START_TIME, Time.toLong(timeEntry.getStart()));
        editTimeEntry.putExtra(TimeModifier.BUNDLE_END_TIME, Time.toLong(timeEntry.getEnd()));
        editTimeEntry.putExtra(TimeModifier.BUNDLE_ACTIVITY_ID, timeEntry.getActivityID());

        startActivity(editTimeEntry);
    }

    @Override
    public void onAddEventClicked(@NotNull Calendar calendar, @NotNull Calendar calendar1) {
        Intent addTimeEntry = new Intent(getContext(), AddTime.class);

        addTimeEntry.putExtra(TimeModifier.BUNDLE_ID, TimeDB.NO_ID_VALUE);
        addTimeEntry.putExtra(TimeModifier.BUNDLE_START_TIME, Time.toLong(Time.fromCalendar(calendar)));
        addTimeEntry.putExtra(TimeModifier.BUNDLE_END_TIME, Time.toLong(Time.fromCalendar(calendar1)));
        addTimeEntry.putExtra(TimeModifier.BUNDLE_ACTIVITY_ID, ActivityDB.NO_ID_VALUE);

        startActivity(addTimeEntry);
    }

    private ActivityDB findActivityWithId(ActivityDB[] activities, int id) {
        for(ActivityDB a : activities) {
            if(a.getId() == id) return a;
        }
        return null;
    }
}
