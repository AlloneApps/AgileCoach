package com.task.agilecoach.views.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.task.agilecoach.R;
import com.task.agilecoach.views.main.MainActivity;
import com.task.agilecoach.views.main.MyVectorClock;

import java.util.ArrayList;
import java.util.Calendar;

public class AdminDashboard extends Fragment {

    private static final String TAG = "DashboardFragment";

    private View rootView;

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

        /*User loginUser = Utils.getLoginUserDetails(requireContext());

        if(loginUser != null){
            Log.d(TAG, "onCreate: userName:  "+loginUser.getFirstName());
            Log.d(TAG, "onCreate: userId:  "+loginUser.getMobileNumber());
            List<TaskMaster> tasksList = DataUtils.getAssignedTasks(requireContext(),loginUser.getMobileNumber(),false);
            Log.d(TAG, "onCreate: tasksList: "+tasksList);
        }*/


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
    }

    private void setUpView(){
        pieChartUsers = rootView.findViewById(R.id.pie_chart_users);
        pieChartTasks = rootView.findViewById(R.id.pie_chart_tasks);


        setPieChart1();

        setPieChart2();

    }

    private void setPieChart1() {
        pieChartUsers.setUsePercentValues(true);
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

        generatePieData1(" Length Segregation");

        pieChartUsers.animateY(500, Easing.EaseInOutQuad);
        // pieChartUsers.spin(2000, 0, 360);

        Legend l = pieChartUsers.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setXOffset(0f);
        l.setTextSize(8f);

        // entry label styling
        pieChartUsers.setEntryLabelColor(Color.WHITE);
        pieChartUsers.setEntryLabelTextSize(12f);
    }

    private void setPieChart2() {
        pieChartTasks.setUsePercentValues(true);
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

        generatePieData2(" Quality Segregation");

        pieChartTasks.animateY(500, Easing.EaseInOutQuad);
        // pieChartTasks.spin(2000, 0, 360);

        Legend l = pieChartTasks.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setXOffset(0f);
        l.setTextSize(8f);

        // entry label styling
        pieChartTasks.setEntryLabelColor(Color.WHITE);
        pieChartTasks.setEntryLabelTextSize(12f);
    }

    private void generatePieData1(String text) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(27, "40cm "));
        entries.add(new PieEntry(25, "50cm "));
        entries.add(new PieEntry(35, "60cm "));
        entries.add(new PieEntry(13, "70cm "));

        PieDataSet dataSet = new PieDataSet(entries, text);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.rgb(224, 64, 10));
        colors.add(Color.rgb(5, 100, 146));
        colors.add(Color.rgb(65, 140, 240));
        colors.add(Color.rgb(252, 180, 65));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChartUsers.setData(data);

        // undo all highlights
        pieChartUsers.highlightValues(null);

        pieChartUsers.invalidate();
    }

    private void generatePieData2(String text) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(77, "Quality A "));
        entries.add(new PieEntry(23, "Quality B "));

        PieDataSet dataSet = new PieDataSet(entries, text);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.rgb(65, 140, 240));
        colors.add(Color.rgb(252, 180, 65));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChartTasks.setData(data);

        // undo all highlights
        pieChartTasks.highlightValues(null);

        pieChartTasks.invalidate();
    }
}