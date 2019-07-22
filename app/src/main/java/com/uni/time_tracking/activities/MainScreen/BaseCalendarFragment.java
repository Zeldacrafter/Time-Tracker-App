package com.uni.time_tracking.activities.MainScreen;

import android.content.ClipData;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewUtil;
import com.uni.time_tracking.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.text.DateFormat;

abstract class BaseCalendarFragment extends Fragment implements
            WeekView.EventClickListener, MonthLoader.MonthChangeListener,
            WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener,
            WeekView.EmptyViewClickListener, WeekView.AddEventClickListener, WeekView.DropListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;

    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private DateFormat shortDateFormat;
    private DateFormat timeFormat;

    WeekView weekView;
    TextView draggableView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weekView = view.findViewById(R.id.fragment_week_view);
        draggableView = getView().findViewById(R.id.fragment_draggable_view);


        shortDateFormat = WeekViewUtil.getWeekdayWithNumericDayAndMonthFormat(getContext(), true);
        timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        if (timeFormat == null) {
            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }

        draggableView.setOnLongClickListener(new DragTapListener());

        // Show a toast message about the touched event.
        weekView.setEventClickListener(this);
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        weekView.setMonthChangeListener(this);
        // Set long press listener for events.
        weekView.setEventLongPressListener(this);
        // Set long press listener for empty view
        weekView.setEmptyViewLongPressListener(this);
        // Set EmptyView Click Listener
        weekView.setEmptyViewClickListener(this);
        // Set AddEvent Click Listener
        weekView.setAddEventClickListener(this);
        // Set Drag and Drop Listener
        weekView.setDropListener(this);

        // Set minDate
        /*Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        minDate.add(Calendar.MONTH, 1);
        mWeekView.setMinDate(minDate);

        // Set maxDate
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 1);
        maxDate.set(Calendar.DAY_OF_MONTH, 10);
        mWeekView.setMaxDate(maxDate);

        Calendar calendar = (Calendar) maxDate.clone();
        calendar.add(Calendar.DATE, -2);
        mWeekView.goToDate(calendar);*/

        //mWeekView.setAutoLimitTime(true);
        //mWeekView.setLimitTime(4, 16);

        //mWeekView.setMinTime(10);
        //mWeekView.setMaxTime(20);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        weekView.setShowDistinctPastFutureColor(true);
        weekView.setShowDistinctWeekendColor(true);
        weekView.setFutureBackgroundColor(Color.rgb(24,85,96));
        weekView.setFutureWeekendBackgroundColor(Color.rgb(255,0,0));
        weekView.setPastBackgroundColor(Color.rgb(85,189,200));
        weekView.setPastWeekendBackgroundColor(Color.argb(50, 0,255,0));
        */
    }

    private class DragTapListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.calendar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_today:
                weekView.goToToday();
                return true;

            case R.id.action_day_view:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    setDayViewType(TYPE_DAY_VIEW);
                }
                return true;

            case R.id.action_three_day_view:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    setDayViewType(TYPE_THREE_DAY_VIEW);
                }
                return true;

            case R.id.action_week_view:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    setDayViewType(TYPE_WEEK_VIEW);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDayViewType(int dayViewType) {
        setupDateTimeInterpreter(dayViewType == TYPE_WEEK_VIEW);

        switch (dayViewType) {
            case TYPE_DAY_VIEW:
                mWeekViewType = TYPE_DAY_VIEW;
                weekView.setNumberOfVisibleDays(1);
                // Lets change some dimensions to best fit the view.
                weekView.setColumnGap((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics()));
                weekView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics()));
                weekView.setEventTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics()));
                break;

            case TYPE_THREE_DAY_VIEW:
                mWeekViewType = TYPE_THREE_DAY_VIEW;
                weekView.setNumberOfVisibleDays(3);
                // Lets change some dimensions to best fit the view.
                weekView.setColumnGap((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics()));
                weekView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics()));
                weekView.setEventTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics()));
                break;

            case TYPE_WEEK_VIEW:
                mWeekViewType = TYPE_WEEK_VIEW;
                weekView.setNumberOfVisibleDays(7);
                // Lets change some dimensions to best fit the view.
                weekView.setColumnGap((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics()));
                weekView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, getResources().getDisplayMetrics()));
                weekView.setEventTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, getResources().getDisplayMetrics()));
                break;
        }
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    protected void setupDateTimeInterpreter(boolean shortDate) {
        Calendar calendar = Calendar.getInstance();
        /*.apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        */
        DateFormat normalDateFormat = WeekViewUtil.getWeekdayWithNumericDayAndMonthFormat(getActivity().getApplicationContext(), false);
        weekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public @NotNull String getFormattedTimeOfDay(int hour, int minutes) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minutes);
                return timeFormat.format(calendar.getTime());
            }

            @Override
            public @NotNull String getFormattedWeekDayTitle(@NotNull Calendar date) {
                return shortDate ? shortDateFormat.format(date.getTime()) : normalDateFormat.format(date.getTime());
            }
        });
    }

    protected String getEventTitle(Calendar startCal, boolean allDay) {
        return getEventTitle(startCal, null, allDay);
    }

    protected String getEventTitle(Calendar startCal) {
        return getEventTitle(startCal, null, false);
    }

    protected String getEventTitle(Calendar startCal, Calendar endCal) {
        return getEventTitle(startCal, endCal, false);
    }

    protected String getEventTitle(Calendar startCal, Calendar endCal, boolean allDay){
        Date startDate = startCal.getTime();
        Date endDate = null;
        if(endCal != null) {
            endDate = endCal.getTime();
        }

        if(allDay) {
            if (endCal == null || WeekViewUtil.isSameDay(startCal, endCal)) {
                return shortDateFormat.format(startDate);
            } else {
                return shortDateFormat.format(startDate) + ".." + shortDateFormat.format(endDate);
            }
        } else if(endCal == null) {
            return shortDateFormat.format(startDate) + " " + timeFormat.format(startDate);
        }else if(WeekViewUtil.isSameDay(startCal, endCal)) {
            return shortDateFormat.format(startDate) + " " + timeFormat.format(startDate) +
                    ".." + timeFormat.format(endDate);
        }else {
            return shortDateFormat.format(startDate) + " " + timeFormat.format(startDate) +
                    ".." + shortDateFormat.format(endDate) + " " + timeFormat.format(endDate);
        }
    }

    @Override
    public void onEventClick(@NotNull WeekViewEvent event, @NotNull RectF rectF) {
        Toast.makeText(getContext(), "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(@NotNull WeekViewEvent event, @NotNull RectF rectF) {
        Toast.makeText(getContext(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(@NotNull Calendar time) {
        Toast.makeText(getContext(), "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewClicked(@NotNull Calendar date) {
        Toast.makeText(getContext(), "Empty view" + " clicked: " + getEventTitle(date), Toast.LENGTH_SHORT).show();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return null;
    }

    @Override
    public void onAddEventClicked(@NotNull Calendar calendar, @NotNull Calendar calendar1) {
        Toast.makeText(getContext(), "Add event clicked.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrop(@NotNull View view, @NotNull Calendar date) {
        Toast.makeText(getContext(), "View dropped to " + date.toString(), Toast.LENGTH_SHORT).show();
    }
}
