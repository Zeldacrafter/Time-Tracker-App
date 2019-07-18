package com.uni.time_tracking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.uni.time_tracking.R;
import static com.uni.time_tracking.General.showToast;

public class AddCategory extends AppCompatActivity {

    //TODO: Color
    //TODO: Icon

    public static final String TAG = "AddCategory";

    private Menu menu;
    private MenuItem done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu bar at top
        getMenuInflater().inflate(R.menu.top_menu_add_category, menu);
        this.menu = menu;
        done = this.menu.findItem(R.id.menu_add_category_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_add_category_done):
                showToast("Saved new Category!", getApplicationContext());
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
