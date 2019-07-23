package com.uni.time_tracking.activities.MainScreen;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.uni.time_tracking.Pair;
import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.EntryDB;

import java.util.ArrayList;

public class CategoryListFragment extends Fragment {

    private static final String TAG = "CategoryListFragment";

    /** Hold one element for each activity-entry. Also see {@link #addCategoriesToList()}. */
    private LinearLayout categoryList;

    /** Holds all TextViews that show how long any activity has been active (if enabled at all).
     * Every element also holds an integer, indicating what activityID this view corresponds to */
    private ArrayList<Pair<TextView, Integer>> toRefreshRunningActivities = new ArrayList<>();
    /** Handle refreshing the text in {@link #toRefreshRunningActivities}.*/
    private final Handler viewRefreshHandler = new Handler();
    /** See {@link #viewRefreshHandler}. */
    private final Runnable viewRefreshRunnable = new Runnable() {
        //TODO: Can this be done with a lambda somehow?
        @Override
        public void run() {
            for (Pair<TextView, Integer> p : toRefreshRunningActivities)
                updateTime(p.item1, p.item2);
            viewRefreshHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoryList = view.findViewById(R.id.home_category_list);

        viewRefreshHandler.post(viewRefreshRunnable);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_category_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        addCategoriesToList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewRefreshHandler.removeCallbacks(viewRefreshRunnable);
    }

    /**
     * Adds one row to {@link #categoryList} for every active entry in
     * {@link com.uni.time_tracking.database.tables.ActivityDB}: <br>
     * - Showing if/how long its been active <br>
     * - Making it possible to toggle if a category is currently used
     *   (add {@link EntryDB} entries).
     */
    private void addCategoriesToList() {

        //TODO: Only add new items. Don't re-add every item
        categoryList.removeAllViews();

        //Add all categories to the list.

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View divider = inflater.inflate(R.layout.divider_horizontal, null);
        categoryList.addView(divider);

        //TODO: Maybe this should be a table-layout.
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        ActivityDB[] activities = dbHelper.getActiveActivities();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 20);

        for(ActivityDB activity : activities) {

            LinearLayout innerLayout = new LinearLayout(getContext());
            innerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setId(activity.getId()); //TODO: is this problematic with potential collisions?
            innerLayout.setOnClickListener(view -> CategoryListFragment.activityClicked(view.getContext(), view.getId()));

            TextView idText = new TextView(getContext());
            TextView nameText = new TextView(getContext());
            TextView runningText = new TextView(getContext());
            idText.setText("ID:" + activity.getId());
            nameText.setText("Name: " + activity.getName());
            updateTime(runningText, activity.getId());
            idText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            nameText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            runningText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            idText.setLayoutParams(layoutParams);
            nameText.setLayoutParams(layoutParams);
            runningText.setLayoutParams(layoutParams);

            toRefreshRunningActivities.add(new Pair<>(runningText, activity.getId()));

            View space1 = new View(getContext());
            View space2 = new View(getContext());
            space1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));
            space2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));

            space1.setBackgroundColor(activity.getColor());

            innerLayout.addView(idText);
            innerLayout.addView(space1);
            innerLayout.addView(nameText);
            innerLayout.addView(space2);
            innerLayout.addView(runningText);

            categoryList.addView(innerLayout);

            divider = inflater.inflate(R.layout.divider_horizontal, null); //FIXME: Dont pass null?
            categoryList.addView(divider);
        }
    }

    /**
     * Toggle whether the activity has an active {@link EntryDB} entry.
     * @param context Application context.
     * @param activityID ID of the activity that we want to toggle.
     */
    private static void activityClicked(Context context, int activityID){
        DBHelper dbHelper = DBHelper.getInstance(context);
        EntryDB time = dbHelper.getActiveTime(activityID);
        if(time == null) {
            // No active Time-Entry. activate now.
            dbHelper.activateActivity(activityID);
        }else {
            dbHelper.deactivityEntry(time.getId());
        }
    }

    /**
     * Updates a TextView to show how long a activity has been active.
     * @param runningText The TextView to update the text of.
     * @param activityID The activity that is active/inactive.
     */
    private void updateTime(TextView runningText, int activityID) {
        EntryDB time = DBHelper.getInstance(runningText.getContext()).getActiveTime(activityID);
        if(time != null) {

            String timeString = Time.differenceToNowString(time.getStart());
            runningText.setText(timeString);
        }else {
            runningText.setText("Inactive!");
        }
    }
}
