package com.uni.time_tracking.activities;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.os.Bundle;

import com.uni.time_tracking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;


import static com.uni.time_tracking.General.showToast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Menu menu;
    private MenuItem devDbViewer;
    private MenuItem addCategory;

    private LinearLayout categoryList;

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

        //TODO: Maybe this should be a table-layout.
        //TODO: Acually make this look kind of good..
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        ActivityDB[] activities = dbHelper.getActiveActivities();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 20);

        for(ActivityDB activity : activities) {

            LinearLayout innerLayout = new LinearLayout(getApplicationContext());
            innerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setId(activity.getId()); //TODO: is this problematic? And is there is a better way?
            innerLayout.setOnClickListener(view -> DBHelper.getInstance(getApplicationContext()).activityStartedOrStopped(view.getId()));

            TextView tv = new TextView(getApplicationContext());
            TextView tv2 = new TextView(getApplicationContext());
            tv.setText("ID:" + activity.getId());
            tv2.setText("Name: " + activity.getName());
            tv.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            tv2.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            tv.setLayoutParams(layoutParams);
            tv2.setLayoutParams(layoutParams);

            View v = new View(getApplicationContext());
            View v2 = new View(getApplicationContext());
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));
            v2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));

            innerLayout.addView(tv);
            innerLayout.addView(v);
            innerLayout.addView(tv2);
            innerLayout.addView(v2);

            categoryList.addView(innerLayout);

            divider = inflater.inflate(R.layout.divider_horizontal, null); //FIXME: Dont pass null?
            categoryList.addView(divider);
        }
    }
}
