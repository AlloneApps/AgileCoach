package com.task.agilecoach.views.dashboard;

import android.app.ProgressDialog;
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
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.model.TaskMaster;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.login.LoginActivity;
import com.task.agilecoach.views.main.MainActivity;
import com.task.agilecoach.views.main.MyVectorClock;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserDashboard extends Fragment {

    private static final String TAG = "DashboardFragment";
    private View rootView;
    private TextView notTaskOrBugs;
    MyVectorClock vectorAnalogClock;

    List<TaskMaster> myTasksList = new ArrayList<>();

    private PieChart pieChartTasksOrBugs, pieChartTasksStatus;

    private ProgressDialog progressDialog;

    public UserDashboard() {
        // Required empty public constructor
    }

    public static UserDashboard createInstance(Bundle bundle) {
        UserDashboard fragment = new UserDashboard();
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
        rootView = inflater.inflate(R.layout.fragment_user_dashboard, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            ((MainActivity) requireActivity()).setTitle("Dashboard");

            progressDialog = new ProgressDialog(requireContext());

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 0);

            vectorAnalogClock = rootView.findViewById(R.id.clock);

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
            notTaskOrBugs = rootView.findViewById(R.id.no_task_details);

            pieChartTasksOrBugs = rootView.findViewById(R.id.pie_chart_tasks_or_bugs);
            pieChartTasksStatus = rootView.findViewById(R.id.pie_chart_tasks_bug_status);

            setPieChartOfTasksOrBugs();

            setPieChartOfTasksStatus();

            User loginUser = Utils.getLoginUserDetails(requireContext());

            loadAllTaskOrBugList(loginUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllTaskOrBugList(User loginUser) {
        try {

            showProgressDialog("Dashboard details loading..");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.TASK_LIST_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    hideProgressDialog();
                    myTasksList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        TaskMaster taskMaster = postSnapshot.getValue(TaskMaster.class);
                        if (taskMaster != null) {
                            int lastPosition = taskMaster.getTasksSubDetailsList().size() - 1;
                            if (taskMaster.getTasksSubDetailsList().get(lastPosition).getTaskUserId().equals(loginUser.getMobileNumber())) {
                                myTasksList.add(taskMaster);
                            }
                        }
                    }

                    if(myTasksList.size() > 0){
                        notTaskOrBugs.setVisibility(View.GONE);
                        vectorAnalogClock.setVisibility(View.GONE);
                        pieChartTasksOrBugs.setVisibility(View.VISIBLE);
                        pieChartTasksStatus.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onDataChange: taskMasterList:" + myTasksList);
                        generatePieChartForTasksOrBugs(myTasksList);
                        generatePieChartForTasksStatus(myTasksList);
                    }else{
                        pieChartTasksOrBugs.setVisibility(View.GONE);
                        pieChartTasksStatus.setVisibility(View.GONE);
                        notTaskOrBugs.setVisibility(View.VISIBLE);
                        vectorAnalogClock.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();
                    if(myTasksList.size() > 0){
                        notTaskOrBugs.setVisibility(View.GONE);
                        vectorAnalogClock.setVisibility(View.GONE);
                        pieChartTasksOrBugs.setVisibility(View.VISIBLE);
                        pieChartTasksStatus.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onDataChange: taskMasterList:" + myTasksList);
                        generatePieChartForTasksOrBugs(myTasksList);
                        generatePieChartForTasksStatus(myTasksList);
                    }else{
                        pieChartTasksOrBugs.setVisibility(View.GONE);
                        pieChartTasksStatus.setVisibility(View.GONE);
                        notTaskOrBugs.setVisibility(View.VISIBLE);
                        vectorAnalogClock.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "onCancelled: failed to load user details");
                }
            });
        } catch (Exception e) {
            hideProgressDialog();
            if(myTasksList.size() > 0){
                notTaskOrBugs.setVisibility(View.GONE);
                vectorAnalogClock.setVisibility(View.GONE);
                pieChartTasksOrBugs.setVisibility(View.VISIBLE);
                pieChartTasksStatus.setVisibility(View.VISIBLE);
                Log.d(TAG, "onDataChange: taskMasterList:" + myTasksList);
                generatePieChartForTasksOrBugs(myTasksList);
                generatePieChartForTasksStatus(myTasksList);
            }else{
                pieChartTasksOrBugs.setVisibility(View.GONE);
                pieChartTasksStatus.setVisibility(View.GONE);
                notTaskOrBugs.setVisibility(View.VISIBLE);
                vectorAnalogClock.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePieChartForTasksOrBugs(List<TaskMaster> myTasksList) {
        try {
            int totalTasks = 0;
            int tasks = 0;
            int bugs = 0;

            for (TaskMaster taskMaster : myTasksList) {
                if (taskMaster.getTaskType().equalsIgnoreCase(AppConstants.BUG_TYPE)) {
                    bugs = bugs + 1;
                    totalTasks = totalTasks + 1;
                } else {
                    tasks = tasks + 1;
                    totalTasks = totalTasks + 1;
                }
            }

            ArrayList<PieEntry> entries = new ArrayList<>();


            entries.add(new PieEntry(tasks, "Tasks  "));
            entries.add(new PieEntry(bugs, "Bugs  "));

            String totalUsersTitle = "Total Tasks or Bugs : " + totalTasks;
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
            pieChartTasksOrBugs.setData(data);

            // undo all highlights
            pieChartTasksOrBugs.highlightValues(null);

            pieChartTasksOrBugs.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generatePieChartForTasksStatus(List<TaskMaster> myTasksList) {
        try {
            int totalTasksOrBugs = 0;
            int todoTasks = 0;
            int assignedTasks = 0;
            int inProgressTasks = 0;
            int readyForTestTasks = 0;
            int doneTasks = 0;

            for (TaskMaster taskMaster : myTasksList) {

                int lastPosition = taskMaster.getTasksSubDetailsList().size() - 1;
                String taskOrBugStatus = taskMaster.getTasksSubDetailsList().get(lastPosition).getTaskStatus();

                switch (taskOrBugStatus) {
                    case AppConstants.TODO_STATUS:
                        todoTasks = todoTasks + 1;
                        totalTasksOrBugs = totalTasksOrBugs + 1;
                        break;
                    case AppConstants.ASSIGNED_STATUS:
                        assignedTasks = assignedTasks + 1;
                        totalTasksOrBugs = totalTasksOrBugs + 1;
                        break;
                    case AppConstants.IN_PROGRESS_STATUS:
                        inProgressTasks = inProgressTasks + 1;
                        totalTasksOrBugs = totalTasksOrBugs + 1;
                        break;
                    case AppConstants.READY_FOR_TEST_STATUS:
                        readyForTestTasks = readyForTestTasks + 1;
                        totalTasksOrBugs = totalTasksOrBugs + 1;
                        break;
                    case AppConstants.DONE_STATUS:
                        doneTasks = doneTasks + 1;
                        totalTasksOrBugs = totalTasksOrBugs + 1;
                        break;
                    default:
                        doneTasks = doneTasks + 1;
                        totalTasksOrBugs = totalTasksOrBugs + 1;
                        break;
                }
            }

            Log.d(TAG, "generatePieChartForTasks: totalTasksOrBugs:" + totalTasksOrBugs);
            Log.d(TAG, "generatePieChartForTasks: todoTasks:" + todoTasks);
            Log.d(TAG, "generatePieChartForTasks: assignedTasks:" + assignedTasks);
            Log.d(TAG, "generatePieChartForTasks: inProgressTasks:" + inProgressTasks);
            Log.d(TAG, "generatePieChartForTasks: readyForTestTasks:" + readyForTestTasks);
            Log.d(TAG, "generatePieChartForTasks: doneTasks:" + doneTasks);

            ArrayList<PieEntry> entries = new ArrayList<>();

            entries.add(new PieEntry(todoTasks, AppConstants.TODO_STATUS));
            entries.add(new PieEntry(assignedTasks, AppConstants.ASSIGNED_STATUS));
            entries.add(new PieEntry(inProgressTasks, AppConstants.IN_PROGRESS_STATUS));
            entries.add(new PieEntry(readyForTestTasks, AppConstants.READY_FOR_TEST_STATUS));
            entries.add(new PieEntry(doneTasks, AppConstants.DONE_STATUS));

            String totalTaskOrBugsString = "Total Tasks or Bugs : " + totalTasksOrBugs;

            PieDataSet dataSet = new PieDataSet(entries, totalTaskOrBugsString);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colors = new ArrayList<>();

            colors.add(Color.rgb(255, 0, 0));
            colors.add(Color.rgb(255, 215, 0));
            colors.add(Color.rgb(238, 130, 238));
            colors.add(Color.rgb(153, 50, 204));
            colors.add(Color.rgb(124, 252, 0));

            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            pieChartTasksStatus.setData(data);

            // undo all highlights
            pieChartTasksStatus.highlightValues(null);

            pieChartTasksStatus.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPieChartOfTasksOrBugs() {
        try {
            pieChartTasksOrBugs.setUsePercentValues(false);
            pieChartTasksOrBugs.setDrawEntryLabels(false);
            pieChartTasksOrBugs.getDescription().setEnabled(false);
            pieChartTasksOrBugs.setExtraOffsets(0, 0, 0, 10);

            pieChartTasksOrBugs.setDragDecelerationFrictionCoef(0.95f);

            pieChartTasksOrBugs.setDrawHoleEnabled(false);

            pieChartTasksOrBugs.setTransparentCircleColor(Color.WHITE);
            pieChartTasksOrBugs.setTransparentCircleAlpha(110);

            pieChartTasksOrBugs.setHoleRadius(58f);
            pieChartTasksOrBugs.setTransparentCircleRadius(61f);

            pieChartTasksOrBugs.setDrawCenterText(false);

            pieChartTasksOrBugs.setRotationAngle(0);
            // enable rotation of the chart by touch
            pieChartTasksOrBugs.setRotationEnabled(true);
            pieChartTasksOrBugs.setHighlightPerTapEnabled(true);

            pieChartTasksOrBugs.animateY(500, Easing.EaseInOutQuad);
            // pieChartTasksOrBugs.spin(2000, 0, 360);

            Legend l = pieChartTasksOrBugs.getLegend();
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
            pieChartTasksOrBugs.setEntryLabelColor(Color.WHITE);
            pieChartTasksOrBugs.setEntryLabelTextSize(12f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPieChartOfTasksStatus() {
        try {
            pieChartTasksStatus.setUsePercentValues(false);
            pieChartTasksStatus.setDrawEntryLabels(false);
            pieChartTasksStatus.getDescription().setEnabled(false);
            pieChartTasksStatus.setExtraOffsets(0, 0, 0, 10);

            pieChartTasksStatus.setDragDecelerationFrictionCoef(0.95f);

            pieChartTasksStatus.setDrawHoleEnabled(false);

            pieChartTasksStatus.setTransparentCircleColor(Color.WHITE);
            pieChartTasksStatus.setTransparentCircleAlpha(110);

            pieChartTasksStatus.setHoleRadius(58f);
            pieChartTasksStatus.setTransparentCircleRadius(61f);

            pieChartTasksStatus.setDrawCenterText(false);

            pieChartTasksStatus.setRotationAngle(0);
            // enable rotation of the chart by touch
            pieChartTasksStatus.setRotationEnabled(true);
            pieChartTasksStatus.setHighlightPerTapEnabled(true);

            pieChartTasksStatus.animateY(500, Easing.EaseInOutQuad);
            // pieChartTasksStatus.spin(2000, 0, 360);

            Legend l = pieChartTasksStatus.getLegend();
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
            pieChartTasksStatus.setEntryLabelColor(Color.WHITE);
            pieChartTasksStatus.setEntryLabelTextSize(12f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog(String message) {
        try {
            if (progressDialog != null) {
                progressDialog.setMessage(message);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}