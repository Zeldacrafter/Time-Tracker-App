package com.uni.time_tracking.activities.Category;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.uni.time_tracking.R;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;

import static com.uni.time_tracking.General.showToast;

public class EditCategory extends AppCompatActivity {

    //TODO: Icon
    
    public static final String TAG = "EditCategory";

    public static final String BUNDLE_ACTIVITY_ID = "Activity_ID";
    private ActivityDB activityToEdit;

    private EditText nameText;
    private ColorPickerDialog colorPickerDialog;
    private Button colorPicker;

    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        int activityID = getIntent().getExtras().getInt(BUNDLE_ACTIVITY_ID);

        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        activityToEdit = dbHelper.getActivity(activityID);
        dbHelper.close();

        nameText = findViewById(R.id.add_category_name_text);
        nameText.setText(activityToEdit.getName());

        currentColor = activityToEdit.getColor();

        colorPicker = findViewById(R.id.color_picker_button);
        colorPicker.setBackgroundColor(currentColor);


        //Light theme variant:
        //  ColorPickerDialog colorPickerDialog= ColorPickerDialog.createColorPickerDialog(this);
        colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this, ColorPickerDialog.DARK_THEME);
        colorPickerDialog.hideOpacityBar();
        colorPickerDialog.setOnColorPickedListener((color, hexVal) -> {
            currentColor = color;
            colorPicker.setBackgroundColor(color);
        });
        colorPickerDialog.setHexaDecimalTextColor(Color.parseColor("#FFFFFF"));
        colorPickerDialog.setInitialColor(currentColor);
        colorPickerDialog.setLastColor(currentColor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu bar at top
        getMenuInflater().inflate(R.menu.top_menu_add_category_or_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_add_category_or_time_done):
                if ("".equals(nameText.getText().toString())) {
                    //TODO: Highlight missing box (Wiggle?)
                    //TODO: Error message not as toast
                    showToast("Please enter a category name!", getApplicationContext());
                } else {

                    activityToEdit.setName(nameText.getText().toString());
                    activityToEdit.setColor(colorPickerDialog.getCurrentColor());
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.editActivity(activityToEdit.getId(), activityToEdit);
                    dbHelper.close();
                    showToast("Edited Category successfully!", getApplicationContext());
                    finish();
                }

                break;

            default:
                Log.e(TAG, "Did not find menu item");
        }

        return super.onOptionsItemSelected(item);
    }

    public void colorPickerClicked(View v) {
        colorPickerDialog.show();
    }
}
