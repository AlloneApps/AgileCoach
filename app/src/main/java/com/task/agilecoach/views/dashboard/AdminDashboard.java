package com.task.agilecoach.views.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.task.agilecoach.R;
import com.task.agilecoach.views.main.MainActivity;
import com.task.agilecoach.views.main.MyVectorClock;

import java.util.Calendar;

public class AdminDashboard extends Fragment {

    private static final String TAG = "DashboardFragment";

    private View rootView;

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
    }
}