package com.task.agilecoach.views.allTasks;

import android.app.ProgressDialog;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.FireBaseDatabaseConstants;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.TaskMaster;

import java.util.ArrayList;
import java.util.List;

public class AllTasks extends Fragment implements AllTasksAdapter.AllTasksItemClickListener {

    private static final String TAG = "AllTasks";
    private View rootView;
    private ProgressDialog progressDialog;
    private TextView textTitle;
    private RecyclerView taskRecyclerView;
    private RelativeLayout recyclerLayout;

    List<TaskMaster> allTasksList = new ArrayList<>();

    private TextView textNoTasks;
    private AllTasksAdapter allTasksAdapter;

    public AllTasks() {
        // Required empty public constructor
    }

    public static AllTasks createInstance(Bundle bundle) {
        AllTasks fragment = new AllTasks();
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

        progressDialog = new ProgressDialog(requireContext());

        try {
            textTitle = requireActivity().findViewById(R.id.title_header);
            if (textTitle != null) {
                textTitle.setVisibility(View.VISIBLE);
                textTitle.setText("All Tasks");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadAllTaskList();

//        setUpViews();
    }

    private void setUpViews() {
        recyclerLayout = rootView.findViewById(R.id.recycler_layout);
        textNoTasks = rootView.findViewById(R.id.text_no_tasks);
        taskRecyclerView = rootView.findViewById(R.id.recycler_my_tasks);
        if (allTasksList.size() > 0) {
            textNoTasks.setVisibility(View.GONE);
            recyclerLayout.setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            taskRecyclerView.setLayoutManager(linearLayoutManager);
            allTasksAdapter = new AllTasksAdapter(requireContext(), allTasksList, this);
            taskRecyclerView.setAdapter(allTasksAdapter);
            allTasksAdapter.notifyDataSetChanged();
        } else {
            recyclerLayout.setVisibility(View.GONE);
            textNoTasks.setVisibility(View.VISIBLE);
        }
    }

    public void loadAllTaskList() {
        try {
            showProgressDialog("Loading task details, please wait.");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.TASK_LIST_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    allTasksList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        TaskMaster taskMaster = postSnapshot.getValue(TaskMaster.class);
                        if (taskMaster != null) {
                            allTasksList.add(taskMaster);
                        }
                    }
                    Log.d(TAG, "onDataChange: taskMasterList:" + allTasksList);

                    hideProgressDialog();

                    setUpViews();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();
                    setUpViews();
                    Log.d(TAG, "onCancelled: failed to load user details");
                }
            });
        } catch (Exception e) {
            hideProgressDialog();
            setUpViews();
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void assignTask(int position, TaskMaster taskMaster) {
        MyTasksToast.showInfoToast(requireContext(), "Implementation Pending.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
    }

    @Override
    public void changeStatus(int position, TaskMaster taskMaster) {
        showDialogForTaskStatusUpdateAdmin(requireContext(), position, taskMaster);
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

    public void showDialogForTaskStatusUpdateAdmin(Context context, int position, TaskMaster taskMaster) {
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


            String headerMessage = taskMaster.getTaskType() + " : " + taskMaster.getTaskMasterId();
            textMainHeader.setText(headerMessage);

            if (taskMaster.getTaskType().equals(AppConstants.BUG_TYPE)) {
                textMainHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_task_or_bug_red, 0, 0, 0);
                textMainHeader.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.std_10_dp));
                textMainHeader.setTextColor(context.getResources().getColor(R.color.error_color, null));
            } else {
                textMainHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_task_or_bug_green, 0, 0, 0);
                textMainHeader.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.std_10_dp));
                textMainHeader.setTextColor(context.getResources().getColor(R.color.success_color, null));
            }

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