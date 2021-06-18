package com.task.agilecoach.views.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.FireBaseDatabaseConstants;
import com.task.agilecoach.model.TaskMaster;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.main.MainActivity;
import com.task.agilecoach.views.main.MyVectorClock;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminDashboard extends Fragment {

    private static final String TAG = "DashboardFragment";

    private View rootView;
    List<TaskMaster> allTasksList = new ArrayList<>();
    List<User> userList = new ArrayList<>();
    private TextView textNoUsers, textNoTasksOrBugs;

    private PieChart pieChartUsers, pieChartTasks;

    public AdminDashboard() {
        // Required empty public constructor
    }

    public static AdminDashboard createInstance(Bundle bundle) {
        AdminDashboard fragment = new AdminDashboard();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            ((MainActivity) requireActivity()).setTitle("Dashboard");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 0);

            MyVectorClock vectorAnalogClock = rootView.findViewById(R.id.clock);

            //customization
            vectorAnalogClock.setCalendar(calendar)
                    .setDiameterInDp(400.0f)
                    .setOpacity(1.0f)
                    .setShowSeconds(true)
                    .setColor(Color.BLACK);

            setUpView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpView() {
        try {
            textNoUsers = rootView.findViewById(R.id.no_users_available);
            textNoTasksOrBugs = rootView.findViewById(R.id.no_tasks_or_bugs_available);

            pieChartUsers = rootView.findViewById(R.id.pie_chart_users);
            pieChartTasks = rootView.findViewById(R.id.pie_chart_tasks);

            setPieChartOfUser();

            setPieChartOfTasks();

            loadAllUsersList();

            loadAllTaskList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllTaskList() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.TASK_LIST_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    allTasksList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        TaskMaster taskMaster = postSnapshot.getValue(TaskMaster.class);
                        if (taskMaster != null) {
                            allTasksList.add(taskMaster);
                        }
                    }
                    Log.d(TAG, "onDataChange: taskMasterList:" + allTasksList);
                    generatePieChartForTasks(allTasksList);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    generatePieChartForTasks(allTasksList);
                    Log.d(TAG, "onCancelled: failed to load user details");
                }
            });
        } catch (Exception e) {
            generatePieChartForTasks(allTasksList);
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadAllUsersList() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    userList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User userMain = postSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: userMain: " + userMain);
                        if (userMain != null) {
                            if (!(userMain.getRole().equalsIgnoreCase(AppConstants.ADMIN_ROLE))) {
                                userList.add(userMain);
                            }
                        }
                    }
                    generatePieChartForUserDetails(userList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    generatePieChartForUserDetails(userList);
                    Log.d(TAG, "onCancelled: failed to load user details");
                }
            });
        } catch (Exception e) {
            generatePieChartForUserDetails(userList);
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePieChartForUserDetails(List<User> userList) {
        try {
            if (userList.size() > 0) {
                textNoUsers.setVisibility(View.GONE);
                pieChartUsers.setVisibility(View.VISIBLE);

                int activeUsers = 0;
                int inActiveUsers = 0;
                int totalUsers = 0;

                for (User user : userList) {
                    if (user.getIsActive().equals("true")) {
                        activeUsers = activeUsers + 1;
                        totalUsers = totalUsers + 1;
                    } else {
                        inActiveUsers = inActiveUsers + 1;
                        totalUsers = totalUsers + 1;
                    }
                }

                Log.d(TAG, "generatePieChartForUserDetails: activeUsers: " + activeUsers);
                Log.d(TAG, "generatePieChartForUserDetails: inActiveUsers: " + inActiveUsers);

                ArrayList<PieEntry> entries = new ArrayList<>();


                entries.add(new PieEntry(activeUsers, "Active  "));
                entries.add(new PieEntry(inActiveUsers, "InActive  "));


                String totalUsersTitle = "Total users : " + totalUsers;
                PieDataSet dataSet = new PieDataSet(entries, totalUsersTitle);
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);

                // add a lot of colors
                ArrayList<Integer> colors = new ArrayList<>();

                colors.add(Color.rgb(50, 205, 50));
                colors.add(Color.rgb(255, 0, 0));

                dataSet.setColors(colors);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new LargeValueFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.WHITE);
                pieChartUsers.setData(data);

                // undo all highlights
                pieChartUsers.highlightValues(null);

                pieChartUsers.invalidate();
            } else {
                pieChartUsers.setVisibility(View.GONE);
                textNoUsers.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generatePieChartForTasks(List<TaskMaster> allTasksList) {
        try {
            if (allTasksList.size() > 0) {
                textNoTasksOrBugs.setVisibility(View.GONE);
                pieChartTasks.setVisibility(View.VISIBLE);
                int totalTasksOrBugs = 0;
                int totalTasks = 0;
                int totalBugs = 0;

                for (TaskMaster taskMaster : allTasksList) {
                    if (taskMaster.getTaskType().equalsIgnoreCase(AppConstants.BUG_TYPE)) {
                        totalBugs = totalBugs + 1;
                        totalTasksOrBugs = totalTasksOrBugs + 1;
                    } else {
                        totalTasks = totalTasks + 1;
                        totalTasksOrBugs = totalTasksOrBugs + 1;
                    }
                }

                Log.d(TAG, "generatePieChartForTasks: totalTasksOrBugs:" + totalTasksOrBugs);
                Log.d(TAG, "generatePieChartForTasks: totalTasks:" + totalTasks);
                Log.d(TAG, "generatePieChartForTasks: totalBugs:" + totalBugs);


                ArrayList<PieEntry> entries = new ArrayList<>();

                String tasks = requireContext().getString(R.string.tasks_with_space);
                String bugs = requireContext().getString(R.string.bugs_with_space);

                entries.add(new PieEntry(totalTasks, tasks));
                entries.add(new PieEntry(totalBugs, bugs));

                String totalTaskOrBugsString = "Total Tasks or Bugs : " + totalTasksOrBugs;

                PieDataSet dataSet = new PieDataSet(entries, totalTaskOrBugsString);
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);

                // add a lot of colors
                ArrayList<Integer> colors = new ArrayList<>();

                colors.add(Color.rgb(50, 205, 50));
                colors.add(Color.rgb(255, 0, 0));

                dataSet.setColors(colors);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new LargeValueFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.WHITE);
                pieChartTasks.setData(data);

                // undo all highlights
                pieChartTasks.highlightValues(null);

                pieChartTasks.invalidate();

            } else {
                pieChartTasks.setVisibility(View.GONE);
                textNoTasksOrBugs.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPieChartOfUser() {
        try {
            pieChartUsers.setUsePercentValues(false);
            pieChartUsers.setDrawEntryLabels(false);
            pieChartUsers.getDescription().setEnabled(false);
            pieChartUsers.setExtraOffsets(0, 0, 0, 10);

            pieChartUsers.setDragDecelerationFrictionCoef(0.95f);

            pieChartUsers.setDrawHoleEnabled(false);

            pieChartUsers.setTransparentCircleColor(Color.WHITE);
            pieChartUsers.setTransparentCircleAlpha(110);

            pieChartUsers.setHoleRadius(58f);
            pieChartUsers.setTransparentCircleRadius(61f);

            pieChartUsers.setDrawCenterText(false);

            pieChartUsers.setRotationAngle(0);
            // enable rotation of the chart by touch
            pieChartUsers.setRotationEnabled(true);
            pieChartUsers.setHighlightPerTapEnabled(true);

            pieChartUsers.animateY(500, Easing.EaseInOutQuad);
            // pieChartUsers.spin(2000, 0, 360);

            Legend l = pieChartUsers.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(0f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            l.setXOffset(0f);
            l.setTextSize(8f);

            // entry label styling
            pieChartUsers.setEntryLabelColor(Color.WHITE);
            pieChartUsers.setEntryLabelTextSize(12f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPieChartOfTasks() {
        try {
            pieChartTasks.setUsePercentValues(false);
            pieChartTasks.setDrawEntryLabels(false);
            pieChartTasks.getDescription().setEnabled(false);
            pieChartTasks.setExtraOffsets(0, 0, 0, 10);

            pieChartTasks.setDragDecelerationFrictionCoef(0.95f);

            pieChartTasks.setDrawHoleEnabled(false);

            pieChartTasks.setTransparentCircleColor(Color.WHITE);
            pieChartTasks.setTransparentCircleAlpha(110);

            pieChartTasks.setHoleRadius(58f);
            pieChartTasks.setTransparentCircleRadius(61f);

            pieChartTasks.setDrawCenterText(false);

            pieChartTasks.setRotationAngle(0);
            // enable rotation of the chart by touch
            pieChartTasks.setRotationEnabled(true);
            pieChartTasks.setHighlightPerTapEnabled(true);

            pieChartTasks.animateY(500, Easing.EaseInOutQuad);
            // pieChartTasks.spin(2000, 0, 360);

            Legend l = pieChartTasks.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(0f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            l.setXOffset(0f);
            l.setTextSize(8f);

            // entry label styling
            pieChartTasks.setEntryLabelColor(Color.WHITE);
            pieChartTasks.setEntryLabelTextSize(12f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}