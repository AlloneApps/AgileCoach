package com.task.agilecoach.views.createTasks;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import com.task.agilecoach.views.dashboard.AdminDashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateTasks extends Fragment {

    private static final String TAG = "CreateTasks";
    private View rootView;
    private TextView textTitle;
    private ProgressDialog progressDialog;

    private EditText editTaskHeader, editTaskDescription, editTaskEstimation;
    private TextView textAssignedUser, textTaskType;
    private Button btnCreateTask;

    private List<String> taskTypeList = new ArrayList<>();

    private List<User> userList = new ArrayList<>();
    private List<String> userNameList = new ArrayList<>();
    private HashMap<String, User> userDetailsMap = new HashMap<>();

    public CreateTasks() {
        // Required empty public constructor
    }

    public static CreateTasks createInstance(Bundle bundle) {
        CreateTasks fragment = new CreateTasks();
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
        rootView = inflater.inflate(R.layout.fragment_create_tasks, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            progressDialog = new ProgressDialog(requireContext());
            textTitle = requireActivity().findViewById(R.id.title_header);
            if (textTitle != null) {
                textTitle.setVisibility(View.VISIBLE);
                textTitle.setText("Create Tasks");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadAllUsers();

//        loadUserDetails();

        loadTaskTypes();

//        setUpViews();
    }

    private void setUpViews() {
        editTaskHeader = rootView.findViewById(R.id.edit_task_header);
        editTaskDescription = rootView.findViewById(R.id.edit_task_description);
        editTaskEstimation = rootView.findViewById(R.id.edit_task_estimation);

        textAssignedUser = rootView.findViewById(R.id.text_assign_user);
        textTaskType = rootView.findViewById(R.id.text_task_type);

        btnCreateTask = rootView.findViewById(R.id.btn_create_task);

        // Create Task button click
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {

                    String taskMasterId = (System.currentTimeMillis() / 1000) + "";
                    TaskMaster taskMaster = new TaskMaster();

                    taskMaster.setTaskMasterId(taskMasterId);
                    taskMaster.setTaskType(textTaskType.getText().toString().trim());
                    taskMaster.setTaskHeader(editTaskHeader.getText().toString().trim());
                    taskMaster.setTaskDescription(editTaskDescription.getText().toString().trim());

                    if (editTaskEstimation.getText().toString().trim().isEmpty()) {
                        taskMaster.setTaskEstimation(0.0);
                    } else {
                        taskMaster.setTaskEstimation(Double.parseDouble(editTaskEstimation.getText().toString().trim()));
                    }

                    User user = Utils.getLoginUserDetails(requireContext());
                    if (user != null) {
                        taskMaster.setTaskCreatedBy(user.getEmailId());
                    }

                    taskMaster.setTaskCreatedOn(Utils.getCurrentTimeStampWithSeconds());

                    List<TasksSubDetails> tasksSubDetailsList = new ArrayList<>();

                    TasksSubDetails tasksSubDetails = new TasksSubDetails();

                    if (textTaskType.getText().toString().trim().equalsIgnoreCase(AppConstants.BUG_TYPE)) {
                        tasksSubDetails.setTaskStatus(AppConstants.TODO_STATUS);
                    } else {
                        tasksSubDetails.setTaskStatus(AppConstants.TODO_STATUS);
                    }

                    tasksSubDetails.setTaskUserAssigned(textAssignedUser.getText().toString().trim());

                    User userSelected = userDetailsMap.get(textAssignedUser.getText().toString().trim());

                    if (userSelected != null) {
                        String userId = userSelected.getMobileNumber();
                        tasksSubDetails.setTaskUserId(userId);
                    }

                    if (user != null) {
                        tasksSubDetails.setModifiedBy(user.getEmailId());
                    }

                    tasksSubDetails.setModifiedOn(Utils.getCurrentTimeStampWithSeconds());

                    tasksSubDetailsList.add(tasksSubDetails);
                    taskMaster.setTasksSubDetailsList(tasksSubDetailsList);


                    showProgressDialog("Processing your request.");

                    createNewTask(taskMaster);
                }
            }
        });

        // Task type Selection
        RxView.touches(textTaskType).subscribe(motionEvent -> {
            try {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(requireContext());
                    builderSingle.setTitle("Select Task Type");

                    final ArrayAdapter<String> taskTypeSelectionAdapter = new ArrayAdapter<String>(requireContext(),
                            android.R.layout.select_dialog_singlechoice, taskTypeList) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView text = view.findViewById(android.R.id.text1);
                            text.setTextColor(Color.BLACK);
                            return view;
                        }
                    };

                    builderSingle.setNegativeButton("Cancel", (dialog, position) -> dialog.dismiss());

                    builderSingle.setAdapter(taskTypeSelectionAdapter, (dialog, position) -> {
                        textTaskType.setText(taskTypeSelectionAdapter.getItem(position));
                    });
                    builderSingle.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // User Selection
        RxView.touches(textAssignedUser).subscribe(motionEvent -> {
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

                    builderSingle.setNegativeButton("Cancel", (dialog, position) -> dialog.dismiss());

                    builderSingle.setAdapter(userSelectionAdapter, (dialog, position) -> {
                        textAssignedUser.setText(userSelectionAdapter.getItem(position));
                    });
                    builderSingle.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean validateFields() {
        if (textTaskType.getText().toString().trim().isEmpty()) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "Please select task type.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editTaskHeader.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter task header.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editTaskDescription.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter task description.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (textAssignedUser.getText().toString().trim().isEmpty()) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "Please select user.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        }
        return true;
    }

    public void createNewTask(TaskMaster taskMaster) {
        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.TASK_LIST_TABLE);

            databaseReference.child(taskMaster.getTaskMasterId()).setValue(taskMaster)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            // ...
                            hideProgressDialog();
                            MyTasksToast.showSuccessToastWithBottom(requireContext(), taskMaster.getTaskType() + " created successfully", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                            navigateToDashboard();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            // ...
                            hideProgressDialog();
                            MyTasksToast.showErrorToastWithBottom(requireContext(), "Failed to create " + taskMaster.getTaskType() + ", Try again.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            MyTasksToast.showErrorToastWithBottom(requireContext(), e.getMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    public void loadAllUsers() {
        try {

            showProgressDialog("Loading users details, please wait.");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);

            List<User> userList = new ArrayList<>();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    userList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        if (user != null) {
                            if (!(user.getRole().equals(AppConstants.ADMIN_ROLE))) {
                                userList.add(user);
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: userList:" + userList);

                    if (userList.size() > 0) {
                        for (User user : userList) {
                            String nameKey = user.getFirstName() + " " + user.getLastName();
                            userNameList.add(nameKey);
                            userDetailsMap.put(nameKey, user);
                        }
                    }

                    loadSetUpViews(userList.size() > 0);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: failed to load user details");
                    loadSetUpViews(false);
                }
            });
        } catch (Exception e) {
            loadSetUpViews(false);
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSetUpViews(boolean isUsersAvailable) {
        hideProgressDialog();
        if (isUsersAvailable) {
            setUpViews();
        } else {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "Failed to load users list.", MyTasksToast.MYTASKS_TOAST_LENGTH_LONG);
            navigateToDashboard();
        }

    }

    private void loadTaskTypes() {
        taskTypeList = DataUtils.getTaskTypeList();
    }

    private void navigateToDashboard() {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_main, new AdminDashboard()).commit();
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