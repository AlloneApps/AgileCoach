package com.task.agilecoach.views.myTasks;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.dataUtils.DataUtils;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.TaskMaster;
import com.task.agilecoach.model.User;

import java.util.ArrayList;
import java.util.List;

public class MyTasks extends Fragment implements MyTasksAdapter.MyTasksItemClickListener {

    private static final String TAG = "CreateTasks";
    private View rootView;
    private TextView textTitle;
    private RecyclerView taskRecyclerView;
    private RelativeLayout recyclerLayout;

    List<TaskMaster> myTasksList = new ArrayList<>();

    private TextView textNoTasks;
    private MyTasksAdapter myTasksAdapter;

    public MyTasks() {
        // Required empty public constructor
    }

    public static MyTasks createInstance(Bundle bundle) {
        MyTasks fragment = new MyTasks();
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
        rootView = inflater.inflate(R.layout.fragment_my_tasks, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            textTitle = requireActivity().findViewById(R.id.title_header);
            if (textTitle != null) {
                textTitle.setVisibility(View.VISIBLE);
                textTitle.setText("My Tasks");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadMyTasksList();

        setUpViews();
    }

    private void setUpViews() {
        recyclerLayout = rootView.findViewById(R.id.recycler_layout);
        textNoTasks = rootView.findViewById(R.id.text_no_tasks);
        taskRecyclerView = rootView.findViewById(R.id.recycler_my_tasks);
        if (myTasksList.size() > 0) {
            textNoTasks.setVisibility(View.GONE);
            recyclerLayout.setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            taskRecyclerView.setLayoutManager(linearLayoutManager);
            myTasksAdapter = new MyTasksAdapter(requireContext(), myTasksList, this);
            taskRecyclerView.setAdapter(myTasksAdapter);
            myTasksAdapter.notifyDataSetChanged();
        } else {
            recyclerLayout.setVisibility(View.GONE);
            textNoTasks.setVisibility(View.VISIBLE);
        }

    }

    private void loadMyTasksList() {
        User loginUser = Utils.getLoginUserDetails(requireContext());
        if (loginUser != null) {
            Log.d(TAG, "onCreate: userName:  " + loginUser.getFirstName());
            Log.d(TAG, "onCreate: userId:  " + loginUser.getMobileNumber());
            myTasksList = DataUtils.getAssignedTasks(requireContext(), loginUser.getMobileNumber(), false);
            Log.d(TAG, "onCreate: tasksList: " + myTasksList);
        }
    }


    private void navigateToDashboard() {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_main, new MyTasks()).commit();
    }

    @Override
    public void assignTask(int position, TaskMaster taskMaster) {
        MyTasksToast.showInfoToast(requireContext(), "Implementation Pending.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
    }

    @Override
    public void changeStatus(int position, TaskMaster taskMaster) {
        MyTasksToast.showInfoToast(requireContext(), "Implementation Pending.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (textTitle != null) {
                textTitle.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlertForUpdateBankDetails(Context context, TaskMaster taskMaster) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_task_admin_update, null);
            builder.setView(dialogView);
            builder.setCancelable(false);

            // TextView and EditText Initialization
            TextView textMainHeader = dialogView.findViewById(R.id.text_task_admin_update_main_header);
            TextView textTaskStatusHeader = dialogView.findViewById(R.id.text_task_status_header);
            TextView textTaskStatusValue = dialogView.findViewById(R.id.text_task_status_value);
            TextView textAssignToHeader = dialogView.findViewById(R.id.text_Assign_to_header);
            TextView textAssignToValue = dialogView.findViewById(R.id.text_assign_to_value);
            //Button Initialization
            Button btnUpdate = dialogView.findViewById(R.id.btn_update);
            Button btnClose = dialogView.findViewById(R.id.btn_close);


            String headerMessage = taskMaster.getTaskType() + " :"+ taskMaster.getTaskMasterId();
            textMainHeader.setText(headerMessage);
            textTaskStatusHeader.setText("Task Status");
            textAssignToHeader.setText("Assigned To");

            int lastPosition = (taskMaster.getTasksSubDetailsList().size() - 1);

            Log.d(TAG, "showAlertForUpdateBankDetails: lastPosition: " + lastPosition);

            if (lastPosition >= 0) {
                String lastStatus = taskMaster.getTasksSubDetailsList().get(lastPosition).getTaskStatus();
                Log.d(TAG, "showAlertForUpdateBankDetails: lastStatus: " + lastStatus);
                textTaskStatusValue.setText(lastStatus);
                String assignedTo = taskMaster.getTasksSubDetailsList().get(lastPosition).getTaskUserAssigned();
                textAssignToValue.setText(assignedTo);

            }

            AlertDialog alert = builder.create();
            alert.show();

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MyTasksToast.showInfoToast(requireContext(), "Implementation Pending.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        alert.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        alert.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}