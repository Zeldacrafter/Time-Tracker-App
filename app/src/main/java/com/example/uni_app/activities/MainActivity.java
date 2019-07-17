package com.example.uni_app.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.uni_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Menu menu;
    private MenuItem devDbViewer,
            devVokCheat,
            devReloadDatabaseAssets,
            devReloadDatabaseIterative,
            devShowSharedPrefrences;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_calendar:
                    return true;
                case R.id.navigation_settings:
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu bar at top
        getMenuInflater().inflate(R.menu.top_menu, menu);

        this.menu = menu;
        devDbViewer = this.menu.findItem(R.id.menu_dev_db_viewer);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_dev_db_viewer):
                Intent dbmanager = new Intent(this, DevAndroidDatabaseManager.class);
                startActivity(dbmanager);
                break;
            default:
                Log.e(TAG, "Did not find menu item");
        }

        return super.onOptionsItemSelected(item);
    }
}
