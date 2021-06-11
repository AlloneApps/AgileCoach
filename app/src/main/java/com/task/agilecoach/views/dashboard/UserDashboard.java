package com.task.agilecoach.views.dashboard;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.task.agilecoach.R;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.dataUtils.DataUtils;
import com.task.agilecoach.model.TaskMaster;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.main.MyVectorClock;

import java.util.Calendar;
import java.util.List;

public class UserDashboard extends Fragment {

    private static final String TAG = "DashboardFragment";
    private View rootView;

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

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 0);

        MyVectorClock vectorAnalogClock = rootView.findViewById(R.id.clock);

        //customization
        vectorAnalogClock.setCalendar(calendar)
                .setDiameterInDp(400.0f)
                .setOpacity(1.0f)
                .setShowSeconds(true)
                .setColor(Color.BLACK);
    }
}