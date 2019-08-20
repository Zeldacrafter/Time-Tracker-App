package com.uni.time_tracking.activities.mainScreen;

import android.content.Intent;
import android.os.Bundle;

import com.uni.time_tracking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uni.time_tracking.activities.category.AddCategory;
import com.uni.time_tracking.activities.DevAndroidDatabaseManager;
import com.uni.time_tracking.activities.mainScreen.settings.SettingsFragment;
import com.uni.time_tracking.database.DBHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;

import net.danlew.android.joda.JodaTimeAndroid;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.nav_view) BottomNavigationView navView;
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
                    loadFragment(new SettingsFragment());
                    return true;
            }
            return false;
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
            case (R.id.top_menu_dev_db_viewer):
                Intent dbManager = new Intent(this, DevAndroidDatabaseManager.class);
                startActivity(dbManager);
                return true;
            case (R.id.top_menu_add_category):
                Intent addCategory = new Intent(this, AddCategory.class);
                startActivity(addCategory);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
