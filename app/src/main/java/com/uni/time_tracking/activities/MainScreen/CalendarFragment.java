package com.uni.time_tracking.activities.MainScreen;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.uni.time_tracking.R;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.EntryDB;

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

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        EntryDB[] dbEvents = dbHelper.getAllEventsInMonth(newYear, newMonth);

        for(EntryDB event : dbEvents) {
            Calendar startTime = event.getStart().toDateTime().toCalendar(Locale.getDefault());
            Calendar endTime = event.getEnd().toDateTime().toCalendar(Locale.getDefault());

            WeekViewEvent weekViewEvent = new WeekViewEvent(event.getId() + "", event.getId()+"", startTime, endTime);
            weekViewEvent.setColor(Color.BLUE);
            events.add(weekViewEvent);
        }

        return events;

    }

    public long getUniqueId() {
        return uniqueId;
    }
}
