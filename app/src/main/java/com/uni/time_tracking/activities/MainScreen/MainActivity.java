package com.uni.time_tracking.activities.MainScreen;

import android.content.Intent;
import android.os.Bundle;

import com.uni.time_tracking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uni.time_tracking.activities.AddCategory;
import com.uni.time_tracking.activities.DevAndroidDatabaseManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.danlew.android.joda.JodaTimeAndroid;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    BottomNavigationView navView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        loadFragment(new CategoryListFragment());
                        return true;
                    case R.id.navigation_calendar:
                        loadFragment(new CalendarFragment());
                        return true;
                    case R.id.navigation_settings:
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        JodaTimeAndroid.init(this);

        loadFragment(new CategoryListFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu_home, menu);
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
                break;
            default:
                Log.e(TAG, "Did not find menu item");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(navView.getSelectedItemId() == R.id.navigation_calendar) {
            //TODO: If we could just reload the calendar entries when we come back from adding
            //      a time entry we would not need to do this.
            loadFragment(new CalendarFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.main_fragment_frame, fragment);
        fragmentTransaction.commit(); // save the changes
    }

}
