package com.uni.time_tracking.activities.mainScreen.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.uni.time_tracking.R;
import com.uni.time_tracking.activities.ActivityWithBackButton;
import com.uni.time_tracking.activities.mainScreen.CategoryListFragment;
import com.uni.time_tracking.database.DBHelper;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uni.time_tracking.Utils._assert;


public class ThirdPartyList extends ActivityWithBackButton {

    public static final String TAG = "ThirdPartyList";

    @BindView(R.id.third_party_list_text) protected TextView thirdPartyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party_list);
        ButterKnife.bind(this);

        // Make links in text clickable
        //thirdPartyList.setMovementMethod(LinkMovementMethod.getInstance());



        final SpannableString spannable = SpannableString.valueOf(thirdPartyList.getText());

        applySpan(spannable, getResources().getString(R.string.third_party_text_week_view), new ClickableSpan() {
            @Override
            public void onClick(@NotNull View widget) {
                createDialog(getResources().getString(R.string.third_party_link_week_view),
                        getResources().getString(R.string.third_party_license_week_view));
            }
        });
        applySpan(spannable, getResources().getString(R.string.third_party_text_joda_time), new ClickableSpan() {
            @Override
            public void onClick(@NotNull View widget) {
                createDialog(getResources().getString(R.string.third_party_link_joda_time),
                        getResources().getString(R.string.third_party_license_joda_time));
            }
        });
        applySpan(spannable, getResources().getString(R.string.third_party_text_butter_knife), new ClickableSpan() {
            @Override
            public void onClick(@NotNull View widget) {
                createDialog(getResources().getString(R.string.third_party_link_butter_knife),
                             getResources().getString(R.string.third_party_license_butter_knife));
            }
        });
        applySpan(spannable, getResources().getString(R.string.third_party_text_material_icons), new ClickableSpan() {
            @Override
            public void onClick(@NotNull View widget) {
                createDialog(getResources().getString(R.string.third_party_link_material_icons),
                        getResources().getString(R.string.third_party_license_material_icons));
            }
        });
        applySpan(spannable, getResources().getString(R.string.third_party_text_color_picker), new ClickableSpan() {
            @Override
            public void onClick(@NotNull View widget) {
                createDialog(getResources().getString(R.string.third_party_link_color_picker),
                        getResources().getString(R.string.third_party_license_color_picker));
            }
        });
        applySpan(spannable, getResources().getString(R.string.third_party_text_calendar_view), new ClickableSpan() {
            @Override
            public void onClick(@NotNull View widget) {
                createDialog(getResources().getString(R.string.third_party_link_calendar_view),
                        getResources().getString(R.string.third_party_license_calendar_view));
            }
        });
        applySpan(spannable, getResources().getString(R.string.third_party_text_database_manager), new ClickableSpan() {
            @Override
            public void onClick(@NotNull View widget) {
                createDialog(getResources().getString(R.string.third_party_link_database_manager),
                        getResources().getString(R.string.third_party_license_database_manager));
            }
        });

        thirdPartyList.setText(spannable);
        thirdPartyList.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static void applySpan(SpannableString spannable, String target, ClickableSpan span) {
        _assert(spannable.toString().contains(target));

        final String spannableString = spannable.toString();
        final int start = spannableString.indexOf(target);
        final int end = start + target.length();
        spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void createDialog(String source_link, String license) {
        //TODO: Make pretty
        //FIXME: Link in title not clickable yet.

        // Linkify the message
        final SpannableString source_link_string = new SpannableString(source_link);
        final SpannableString license_link = new SpannableString(license);

        Linkify.addLinks(source_link_string, Linkify.ALL);
        Linkify.addLinks(license_link, Linkify.ALL);

        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(source_link)
                .setMessage(license_link)
                .create();

        d.show();

        // Make the textview clickable. Must be called after show()
        ((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}
