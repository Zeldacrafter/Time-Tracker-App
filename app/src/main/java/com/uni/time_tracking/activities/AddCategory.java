package com.uni.time_tracking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.uni.time_tracking.R;
import com.uni.time_tracking.database.DBHelper;

import static com.uni.time_tracking.General.showToast;

public class AddCategory extends AppCompatActivity {

    //TODO: Color
    //TODO: Icon

    public static final String TAG = "AddCategory";

    private Menu menu;
    private MenuItem done;

    private EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        nameText = findViewById(R.id.add_category_name_text);
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
                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                if(!"".equals(nameText.getText().toString())) {
                    dbHelper.addEntryActivity(nameText.getText().toString());
                    showToast("Saved new Category!", getApplicationContext());
                    finish();
                } else {
                    //TODO: Highlight missing box (Wiggle?)
                    //TODO: Error message not as toast
                    showToast("Please name the new category", getApplicationContext());
                }
                break;

            default:
                Log.e(TAG, "Did not find menu item");
        }

        return super.onOptionsItemSelected(item);
    }
}
