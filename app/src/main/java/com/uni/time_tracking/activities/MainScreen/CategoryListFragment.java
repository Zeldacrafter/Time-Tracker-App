package com.uni.time_tracking.activities.mainScreen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.uni.time_tracking.Pair;
import com.uni.time_tracking.R;
import com.uni.time_tracking.Time;
import com.uni.time_tracking.activities.category.EditCategory;
import com.uni.time_tracking.database.DBHelper;
import com.uni.time_tracking.database.tables.ActivityDB;
import com.uni.time_tracking.database.tables.TimeDB;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.uni.time_tracking.Utils._assert;

public class CategoryListFragment extends Fragment {

    private static final String TAG = "CategoryListFragment";

    private Unbinder unbinder;

    /**
     * Hold one element for each activity-entry. Also see {@link #addCategoriesToList()}.
     */
    @BindView(R.id.home_category_list) protected DragLinearLayout categoryList;

    /**
     * Holds all TextViews that show how long any activity has been active (if enabled at all).
     * Every element also holds an integer, indicating what activityID this view corresponds to
     */
    private final ArrayList<Pair<TextView, Integer>> toRefreshRunningActivities = new ArrayList<>();
    /**
     * Handle refreshing the text in {@link #toRefreshRunningActivities}.
     */
    private final Handler viewRefreshHandler = new Handler();
    /**
     * See {@link #viewRefreshHandler}.
     */
    private final Runnable viewRefreshRunnable = new Runnable() {
        //TODO: Can this be done with a lambda somehow?
        @Override
        public void run() {
            for (Pair<TextView, Integer> p : toRefreshRunningActivities)
                updateTime(p.item1, p.item2);
            viewRefreshHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewRefreshHandler.post(viewRefreshRunnable);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_category_list, container, false);
        unbinder = ButterKnife.bind(this, v);

        categoryList.setOnViewSwapListener(
                (firstView, firstPosition, secondView, secondPosition) ->
                DBHelper.getInstance(getContext()).swapActivityListOrder(firstView.getId(), secondView.getId())
        );

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        addCategoriesToList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        viewRefreshHandler.removeCallbacks(viewRefreshRunnable);
    }

    /**
     * Adds one row to {@link #categoryList} for every active entry in
     * {@link com.uni.time_tracking.database.tables.ActivityDB}: <br>
     * - Showing if/how long its been active <br>
     * - Making it possible to toggle if a category is currently used
     * (add {@link TimeDB} entries).
     */
    public void addCategoriesToList() {

        //TODO: Only add new items. Don't re-add every item
        categoryList.removeAllViews();

        //Add all categories to the list.

        _assert(getActivity() != null);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _assert(inflater != null);

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        ActivityDB[] activities = dbHelper.getAcitivities();
        dbHelper.close();

        categoryList.addView(inflater.inflate(R.layout.divider_horizontal, null));
        for (ActivityDB activity : activities) {

            RelativeLayout listItem = ((LinearLayout)inflater.inflate(R.layout.list_item, categoryList, true)).findViewById(R.id.list_item_rel_layout);
            listItem.setId(activity.getId());
            listItem.setOnClickListener(view -> CategoryListFragment.activityClicked(view.getContext(), view.getId()));

            View colorView = listItem.findViewById(R.id.list_item_color);
            colorView.setBackgroundColor(activity.getColor());

            TextView activityName = listItem.findViewById(R.id.list_item_name_text);
            activityName.setText(activity.getName());

            TextView activeText = listItem.findViewById(R.id.list_item_active_text);
            updateTime(activeText, activity.getId());
            toRefreshRunningActivities.add(new Pair<>(activeText, activity.getId()));

            ImageView moreMenu = listItem.findViewById(R.id.list_item_more_icon);
            moreMenu.setOnClickListener(view -> {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), moreMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.category_popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_edit_item:
                            Intent intent = new Intent(getContext(), EditCategory.class);
                            intent.putExtra(EditCategory.BUNDLE_ACTIVITY_ID, activity.getId());
                            startActivity(intent);
                            break;

                        case R.id.menu_delete_item:

                            Bundle bundle = new Bundle();
                            bundle.putString(DeleteActivityDialogFragment.BUNDLE_ACTIVITY_NAME, activity.getName());
                            bundle.putInt(DeleteActivityDialogFragment.BUNDLE_ACTIVITY_ID, activity.getId());

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            DeleteActivityDialogFragment dialog = new DeleteActivityDialogFragment();
                            dialog.setArguments(bundle);
                            dialog.setTargetFragment(this, 0);
                            dialog.show(fragmentManager, TAG);

                            break;

                        case R.id.menu_toggle_item_activation:
                            DBHelper dbHelper1 = DBHelper.getInstance(getContext());
                            dbHelper1.toggleActivityActive(activity.getId());
                            dbHelper1.close();
                            addCategoriesToList();
                            break;

                        default:
                            Log.e(TAG, "Did not find Popup Menu Item with id " + item.getItemId() + ", name " + item.getTitle());
                    }
                    return true;
                });

                popup.show(); //showing popup menu
            });

            if(!activity.isActive()) {
                // Make view semi transperent.
                listItem.setAlpha(0.5f);
            }

            categoryList.setViewDraggable(listItem, listItem);

            categoryList.addView(inflater.inflate(R.layout.divider_horizontal, null));
        }
    }

    /**
     * Toggle whether the activity has an active {@link TimeDB} entry.
     *
     * @param context    Application context.
     * @param activityID ID of the activity that we want to toggle.
     */
    private static void activityClicked(Context context, int activityID) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        TimeDB time = dbHelper.getActiveTime(activityID);
        if (time == null) {
            // No active Time-Entry. activate now.
            dbHelper.activateTimeActivity(activityID, context);
        } else {
            dbHelper.deactivateTimeEntry(time.getId(), context);
        }
        dbHelper.close();
    }

    /**
     * Updates a TextView to show how long a activity has been active.
     *
     * @param runningText The TextView to update the text of.
     * @param activityID  The activity that is active/inactive.
     */
    private void updateTime(TextView runningText, int activityID) {
        DBHelper dbHelper = DBHelper.getInstance(runningText.getContext());
        TimeDB time = dbHelper.getActiveTime(activityID);
        dbHelper.close();

        if (time != null) {
            String timeString = Time.differenceToNowString(time.getStart());
            runningText.setText(timeString);
        } else {
            runningText.setText(getResources().getString(R.string.activity_list_activity_inactive));
        }
    }

    public static class DeleteActivityDialogFragment extends DialogFragment {

        private static final String BUNDLE_ACTIVITY_NAME = "Activity_Name";
        private static final String BUNDLE_ACTIVITY_ID = "Activity_ID";

        private String activityName;
        private int activityID;

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            _assert(getArguments() != null, "No arguments passed!");
            activityName = getArguments().getString(BUNDLE_ACTIVITY_NAME);
            activityID = getArguments().getInt(BUNDLE_ACTIVITY_ID);
            _assert(activityName != null, "No value (or null) was passed for BUNDLE_ACTIVITY_NAME!");
            _assert(activityID > 0, "Passed value for BUNDLE_ACTIVITY_ID is " + activityID + ". Was one passed at all?");

            //TODO: Strings as resource.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Delete Category \"" + activityName + "\"?\n" +
                    "This will also delete all entries of that Category.")
                    .setPositiveButton("Delete", (dialog, id) -> {
                        DBHelper dbHelper = DBHelper.getInstance(getContext());
                        dbHelper.deleteActivity(activityID);
                        dbHelper.close();

                        _assert(getTargetFragment() instanceof CategoryListFragment);
                        ((CategoryListFragment)getTargetFragment()).addCategoriesToList();
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {});
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
