package com.task.agilecoach.views.allTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding.view.RxView;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.FireBaseDatabaseConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.dataUtils.DataUtils;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.TaskMaster;
import com.task.agilecoach.model.TasksSubDetails;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.main.MainActivity;
import com.task.agilecoach.views.taskDetails.TaskDetails;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AllTasks extends Fragment implements AllTasksAdapter.AllTasksItemClickListener {

    private static final String TAG = "AllTasks";
    private View rootView;
    private ProgressDialog progressDialog;
    private RecyclerView taskRecyclerView;
    private RelativeLayout recyclerLayout;

    List<TaskMaster> allTasksList = new ArrayList<>();
    List<String> userNameList = new ArrayList<>();
    HashMap<String, User> userHashMap = new HashMap<>();

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
        try {
            ((MainActivity) requireActivity()).setTitle("All Tasks");

            progressDialog = new ProgressDialog(requireContext());

            loadAllTaskList();

            loadAllUsersList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllTaskList() {
        try {
            showProgressDialog("Loading task details, please wait.");

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
        try {
            Intent intent = new Intent(requireContext(), TaskDetails.class);
            intent.putExtra(TaskDetails.TASK_MASTER_DETAILS, taskMaster);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeStatus(int position, TaskMaster taskMaster) {
        try {
            showDialogForTaskStatusUpdateAdmin(requireContext(), position, taskMaster);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showDialogForTaskStatusUpdateAdmin(Context context, int position, TaskMaster taskMaster) {
        try {
            String lastStatus = "";
            String assignedTo = "";

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

            List<String> taskStatusList = DataUtils.getTaskStatusStringList(requireContext());

            RxView.touches(textTaskStatusValue).subscribe(motionEvent -> {
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(requireContext());
                        builderSingle.setTitle("Task Status");

                        final ArrayAdapter<String> taskStatusSelectionAdapter = new ArrayAdapter<String>(requireContext(),
                                android.R.layout.select_dialog_singlechoice, taskStatusList) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };

                        builderSingle.setNegativeButton("Cancel", (dialog, subPosition) -> dialog.dismiss());

                        builderSingle.setAdapter(taskStatusSelectionAdapter, (dialog, subPosition) -> {
                            textTaskStatusValue.setText(taskStatusSelectionAdapter.getItem(subPosition));
                        });
                        builderSingle.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            RxView.touches(textAssignToValue).subscribe(motionEvent -> {
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(requireContext());
                        builderSingle.setTitle("Assign To");

                        final ArrayAdapter<String> userSelectionAdapter = new ArrayAdapter<String>(requireContext(),
                                android.R.layout.select_dialog_singlechoice, userNameList) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };

                        builderSingle.setNegativeButton("Cancel", (dialog, subPosition) -> dialog.dismiss());

                        builderSingle.setAdapter(userSelectionAdapter, (dialog, subPosition) -> {
                            textAssignToValue.setText(userSelectionAdapter.getItem(subPosition));
                        });
                        builderSingle.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

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
                lastStatus = taskMaster.getTasksSubDetailsList().get(lastPosition).getTaskStatus();
                Log.d(TAG, "showAlertForUpdateBankDetails: lastStatus: " + lastStatus);
                textTaskStatusValue.setText(lastStatus);
                assignedTo = taskMaster.getTasksSubDetailsList().get(lastPosition).getTaskUserAssigned();
                textAssignToValue.setText(assignedTo);
            }

            AlertDialog alert = builder.create();
            alert.show();

            String finalLastStatus = lastStatus;
            String finalAssignedTo = assignedTo;
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!(finalLastStatus.equalsIgnoreCase(textTaskStatusValue.getText().toString().trim())) || !finalAssignedTo.equalsIgnoreCase(textAssignToValue.getText().toString().trim())) {

                            User loginUser = Utils.getLoginUserDetails(requireContext());

                            TasksSubDetails tasksSubDetails = new TasksSubDetails();
                            tasksSubDetails.setTaskId(taskMaster.getTaskMasterId());
                            tasksSubDetails.setModifiedBy(loginUser.getEmailId());
                            tasksSubDetails.setModifiedOn(Utils.getCurrentTimeStampWithSeconds());

                            tasksSubDetails.setTaskStatus(textTaskStatusValue.getText().toString().trim());
                            tasksSubDetails.setTaskUserAssigned(textAssignToValue.getText().toString().trim());

                            tasksSubDetails.setTaskUserAssigned(textAssignToValue.getText().toString().trim());

                            String taskUserId = Objects.requireNonNull(userHashMap.get(textAssignToValue.getText().toString().trim())).getMobileNumber();
                            String taskUserGender = Objects.requireNonNull(userHashMap.get(textAssignToValue.getText().toString().trim())).getGender();
                            tasksSubDetails.setTaskUserId(taskUserId);
                            tasksSubDetails.setTaskUserGender(taskUserGender);

                            taskMaster.getTasksSubDetailsList().add(tasksSubDetails);

                            updateTaskOrBug(position, taskMaster, alert);
                        } else {
                            MyTasksToast.showInfoToast(requireContext(), "Nothing to Update.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
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

    private void updateTaskOrBug(int position, TaskMaster taskMaster, AlertDialog alert) {
        try {
            showProgressDialog("Processing your request.");

            Log.d(TAG, "updateTaskOrBug: taskMaster: " + taskMaster);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.TASK_LIST_TABLE);

            databaseReference.child(taskMaster.getTaskMasterId()).setValue(taskMaster)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            MyTasksToast.showSuccessToastWithBottom(requireContext(), taskMaster.getTaskType() + " details updated successfully", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                            if (alert != null) {
                                alert.dismiss();
                            }
                            allTasksAdapter.notifyItemChanged(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            MyTasksToast.showErrorToastWithBottom(requireContext(), "Failed to update " + taskMaster.getTaskType() + " details, Try again.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            MyTasksToast.showErrorToastWithBottom(requireContext(), e.getMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
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

    public void loadAllUsersList() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    userNameList.clear();
                    userHashMap.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User userMain = postSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: userMain: " + userMain);
                        if (userMain != null) {
                            if (!(userMain.getRole().equalsIgnoreCase(AppConstants.ADMIN_ROLE))) {
                                String userFullName = userMain.getFirstName() + " " + userMain.getLastName();
                                userNameList.add(userFullName);
                                userHashMap.put(userFullName, userMain);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: failed to load user details");
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}