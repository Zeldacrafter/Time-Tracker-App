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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.uni_app.General.showToast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Menu menu;
    private MenuItem devDbViewer;
    private MenuItem addCategory;

    private ListView categoryList;

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
        categoryList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));

        TextView tv = new TextView(getApplicationContext());
        tv.setText("ABC");
        tv.setOnClickListener(view -> showToast("Hi", getApplicationContext()));
        //categoryList.addHeaderView(tv);
        categoryList.addFooterView(tv, null, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu bar at top
        getMenuInflater().inflate(R.menu.top_menu, menu);
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
                showToast("Item Added!", getApplicationContext());
            default:
                Log.e(TAG, "Did not find menu item");
        }

        return super.onOptionsItemSelected(item);
    }
}
