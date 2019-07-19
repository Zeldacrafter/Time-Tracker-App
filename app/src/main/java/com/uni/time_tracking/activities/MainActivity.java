package com.uni.time_tracking.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.uni.time_tracking.General;
import com.uni.time_tracking.Pair;
import com.uni.time_tracking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Menu menu;
    private MenuItem devDbViewer;
    private MenuItem addCategory;

    private LinearLayout categoryList;

    private ArrayList<Pair<TextView, Integer>> toRefreshRunningActivities;
    final Handler runningTimerUpdateHandler = new Handler();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    categoryList.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_calendar:
                    categoryList.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_settings:
                    categoryList.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        categoryList = findViewById(R.id.home_category_list);

        toRefreshRunningActivities = new ArrayList<>();

        runningTimerUpdateHandler.post(new Runnable() {
            @Override
            public void run() {
                for (Pair<TextView, Integer> p : toRefreshRunningActivities)
                    updateRunningText(p.item1, p.item2);
                runningTimerUpdateHandler.postDelayed(this, 100);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu bar at top
        getMenuInflater().inflate(R.menu.top_menu_home, menu);
        this.menu = menu;
        devDbViewer = this.menu.findItem(R.id.menu_dev_db_viewer);
        addCategory = this.menu.findItem(R.id.menu_add_category);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_dev_db_viewer):
                Intent dbManager = new Intent(this, DevAndroidDatabaseManager.class);
                startActivity(dbManager);
                break;
            case (R.id.menu_add_category):
                Intent addCategory = new Intent(this, AddCategory.class);
                startActivity(addCategory);
            default:
                Log.e(TAG, "Did not find menu item");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addCategoriesToList();
    }

    private void addCategoriesToList() {

        //TODO: Only add new items. Don't re-add every item

        categoryList.removeAllViews();

        //Add all categories to the list.

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View divider = inflater.inflate(R.layout.divider_horizontal, null);
        categoryList.addView(divider);

        //TODO: Maybe this should be activityClicked table-layout.
        //TODO: Acually make this look kind of good..
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        ActivityDB[] activities = dbHelper.getActiveActivities();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 20);

        for(ActivityDB activity : activities) {

            LinearLayout innerLayout = new LinearLayout(getApplicationContext());
            innerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setId(activity.getId()); //TODO: is this problematic? And is there is activityClicked better way?
            innerLayout.setOnClickListener(view -> MainActivity.activityClicked((LinearLayout)view, view.getId()));

            TextView idText = new TextView(getApplicationContext());
            TextView nameText = new TextView(getApplicationContext());
            TextView runningText = new TextView(getApplicationContext());
            idText.setText("ID:" + activity.getId());
            nameText.setText("Name: " + activity.getName());
            updateRunningText(runningText, activity.getId());
            idText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            nameText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            runningText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            idText.setLayoutParams(layoutParams);
            nameText.setLayoutParams(layoutParams);
            runningText.setLayoutParams(layoutParams);

            toRefreshRunningActivities.add(new Pair<>(runningText, activity.getId()));

            View space1 = new View(getApplicationContext());
            View space2 = new View(getApplicationContext());
            space1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));
            space2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));
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

    private static void activityClicked(LinearLayout layout, int activityID){

        DBHelper dbHelper = DBHelper.getInstance(layout.getContext());
        TimeDB time = dbHelper.getActiveTime(activityID);
        if(time == null) {
            // No active time. activate now.
            dbHelper.activateActivity(activityID);
        }else {
            // The activity is active. Deactivate it.
            dbHelper.deactivateTime(time.getId());
        }
    }

    private void updateRunningText(TextView runningText, int activityID) {
        TimeDB time = DBHelper.getInstance(runningText.getContext()).getActiveTime(activityID);
        if(time != null) {
            Log.d(TAG,System.currentTimeMillis()/1000 + "-" + time.getStart()/1000+" = " + (System.currentTimeMillis()-time.getStart()) / 1000);
            String timeString = General.millisToTimeString(System.currentTimeMillis() - time.getStart());
            runningText.setText(timeString);
        }else {
            runningText.setText("Inactive!");
        }
    }
}
