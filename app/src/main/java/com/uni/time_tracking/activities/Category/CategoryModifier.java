package com.uni.time_tracking.activities.category;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.uni.time_tracking.R;

abstract public class CategoryModifier extends AppCompatActivity {

    //TODO: Finish Color
    //TODO: Simpler Color selector with sample colors before complex one
    //TODO: Save color so that it is the same next time"

    //TODO: Icon

    protected EditText nameText;
    protected ColorPickerDialog colorPickerDialog;
    protected Button colorPicker;

    protected int currentColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_category);

        nameText = findViewById(R.id.add_category_name_text);

        colorPicker = findViewById(R.id.color_picker_button);
        colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this, ColorPickerDialog.DARK_THEME);
        colorPickerDialog.hideOpacityBar();
        colorPickerDialog.setOnColorPickedListener((color, hexVal) -> {
            currentColor = color;
            colorPicker.setBackgroundColor(color);
        });
        colorPickerDialog.setHexaDecimalTextColor(Color.parseColor("#FFFFFF"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu bar at top
        getMenuInflater().inflate(R.menu.top_menu_add_category_or_time, menu);
        return true;
    }

    public void colorPickerClicked(View v) {
        colorPickerDialog.show();
    }
}
