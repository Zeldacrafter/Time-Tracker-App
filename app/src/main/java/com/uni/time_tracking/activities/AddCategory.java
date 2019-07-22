package com.uni.time_tracking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.uni.time_tracking.R;
import com.uni.time_tracking.database.DBHelper;

import static com.uni.time_tracking.General.showToast;

public class AddCategory extends AppCompatActivity {

    //TODO: Finish Color
    //TODO: Simpler Color selector with sample colors before complex one
    //TODO: Save color so that it is the same next time"

    //TODO: Icon

    public static final String TAG = "AddCategory";

    private EditText nameText;
    private ColorPickerDialog colorPickerDialog;
    private Button colorPicker;

    private int currentColor = Color.RED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        nameText = findViewById(R.id.add_category_name_text);

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
        getMenuInflater().inflate(R.menu.top_menu_add_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_add_category_done):
                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                if ("".equals(nameText.getText().toString())) {
                    //TODO: Highlight missing box (Wiggle?)
                    //TODO: Error message not as toast
                    showToast("Please name the new category", getApplicationContext());
                } else {
                    dbHelper.addEntryActivity(nameText.getText().toString(), currentColor);
                    showToast("Saved new Category!", getApplicationContext());
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
