package com.uni.time_tracking.activities.category;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.uni.time_tracking.R;

import org.jetbrains.annotations.NotNull;

import static com.uni.time_tracking.Utils._assert;

abstract public class CategoryModifier extends AppCompatActivity {

    public static final String TAG = "CategoryModifier";

    //TODO: Simpler Color selector with sample colors before complex one
    //TODO: Save previously selected colors?

    //TODO: Icon

    protected EditText nameText;
    protected ColorPickerDialog colorPickerDialog;
    protected Button colorPicker;

    protected int currentColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        _assert(getSupportActionBar() != null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        getMenuInflater().inflate(R.menu.top_menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called from clicking the button for changing the color.
     * @param v The view that called the method.
     */
    public void colorPickerClicked(View v) {
        colorPickerDialog.show();
    }
}
