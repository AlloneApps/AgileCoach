package com.task.agilecoach.views.allUsers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class AllUsers extends Fragment implements AllUsersAdapter.AllUsersItemClickListener {

    private static final String TAG = "AllTasks";
    private View rootView;
    private ProgressDialog progressDialog;
    private RecyclerView userRecyclerView;
    private RelativeLayout recyclerLayout;

    List<User> allUsersList = new ArrayList<>();

    private TextView textNoUsers;
    private AllUsersAdapter allUsersAdapter;

    public AllUsers() {
        // Required empty public constructor
    }

    public static AllUsers createInstance(Bundle bundle) {
        AllUsers fragment = new AllUsers();
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
        rootView = inflater.inflate(R.layout.fragment_all_users, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            ((MainActivity) requireActivity()).setTitle("All Users");

            progressDialog = new ProgressDialog(requireContext());

            loadAllUsersList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        recyclerLayout = rootView.findViewById(R.id.recycler_layout);
        textNoUsers = rootView.findViewById(R.id.text_no_users);
        userRecyclerView = rootView.findViewById(R.id.recycler_users);
        if (allUsersList.size() > 0) {
            textNoUsers.setVisibility(View.GONE);
            recyclerLayout.setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            userRecyclerView.setLayoutManager(linearLayoutManager);
            allUsersAdapter = new AllUsersAdapter(requireContext(), allUsersList, this);
            userRecyclerView.setAdapter(allUsersAdapter);
            allUsersAdapter.notifyDataSetChanged();
        } else {
            recyclerLayout.setVisibility(View.GONE);
            textNoUsers.setVisibility(View.VISIBLE);
        }
    }

    public void loadAllUsersList() {
        try {
            showProgressDialog("Loading user details, please wait.");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    allUsersList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User userMain = postSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: userMain: " + userMain);
                        if (userMain != null) {
                            if (!(userMain.getRole().equalsIgnoreCase(AppConstants.ADMIN_ROLE))) {
                                allUsersList.add(userMain);
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: allUsersList:" + allUsersList);

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
    public void onDestroy() {
        super.onDestroy();
    }

   /* public void showDialogForTaskStatusUpdateAdmin(Context context, int position, User taskMaster) {
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


            String headerMessage = taskMaster.getTaskType() + " : " + taskMaster.getUserId();
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
    }*/

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

    @Override
    public void userDetails(int position, User userDetails) {

    }

    @Override
    public void makeInActiveUser(int position, User userDetails) {

    }
}