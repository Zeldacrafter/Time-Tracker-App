package com.uni.time_tracking.activities.category;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.uni.time_tracking.R;
import com.uni.time_tracking.activities.ActivityWithBackButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

abstract public class CategoryModifier extends ActivityWithBackButton {

    public static final String TAG = "CategoryModifier";

    //TODO: Simpler Color selector with sample colors before complex one
    //TODO: Save previously selected colors?

    //TODO: Icon

    @BindView(R.id.add_category_name_text) protected EditText nameText;
    @BindView(R.id.color_picker_button) protected Button colorPicker;
    protected ColorPickerDialog colorPickerDialog;

    protected int currentColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ButterKnife.bind(this);

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

    /**
     * Called from clicking the button for changing the color.
     */
    @OnClick(R.id.color_picker_button)
    public void colorPickerClicked() {
        colorPickerDialog.show();
    }
}
