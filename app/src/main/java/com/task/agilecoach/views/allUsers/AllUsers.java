package com.task.agilecoach.views.allUsers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.FireBaseDatabaseConstants;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.main.MainActivity;
import com.task.agilecoach.views.myTasks.MyTasks;

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
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            MyTasksToast.showInfoToast(requireContext(),"Implementation pending",MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void makeInActiveUser(int position, User userDetails) {
        try {
            showDialogActiveOrInActiveUserByAdmin(requireContext(), position, userDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialogActiveOrInActiveUserByAdmin(Context context, int position, User user) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_with_two_buttons, null);
            builder.setView(dialogView);
            builder.setCancelable(false);

            // TextView and EditText Initialization
            TextView textAlertHeader = dialogView.findViewById(R.id.dialog_message_header);
            TextView textAlertDesc = dialogView.findViewById(R.id.dialog_message_desc);

            TextView textBtnClose = dialogView.findViewById(R.id.text_button_left);
            TextView textBtnActiveOrInActive = dialogView.findViewById(R.id.text_button_right);

            textAlertHeader.setText("Message..!");
            String activeOrInActiveMessage = "Are you sure want to make ";
            String rightButtonText = "Active";

            boolean isActive = true;
            if (user.getIsActive().equals("false")) {
                isActive = false;
            }

            if (isActive) {
                activeOrInActiveMessage = activeOrInActiveMessage + " InActive \n" + user.getFirstName() + " " + user.getLastName() + "?";
                rightButtonText = "InActive";
                textBtnActiveOrInActive.setTextColor(context.getResources().getColor(R.color.error_color, null));
                textAlertHeader.setTextColor(context.getResources().getColor(R.color.error_color, null));
            } else {
                activeOrInActiveMessage = activeOrInActiveMessage + " Active \n" + user.getFirstName() + " " + user.getLastName() + "?";
                rightButtonText = "Active";
                textBtnActiveOrInActive.setTextColor(context.getResources().getColor(R.color.success_color, null));
                textAlertHeader.setTextColor(context.getResources().getColor(R.color.success_color, null));
            }

            textAlertDesc.setText(activeOrInActiveMessage);
            textBtnClose.setText("Close");
            textBtnActiveOrInActive.setText(rightButtonText);

            AlertDialog alert = builder.create();
            alert.show();

            textBtnActiveOrInActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (user.getIsActive().equals("false")) {
                            user.setIsActive("true");
                        } else {
                            user.setIsActive("false");
                        }
                        alert.dismiss();
                        updateUserActiveStatus(context, position, user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            textBtnClose.setOnClickListener(new View.OnClickListener() {
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

    public void updateUserActiveStatus(Context context, int position, User user) {
        try {
            showProgressDialog("Processing your request.");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);
            databaseReference.child(user.getMobileNumber()).setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            MyTasksToast.showSuccessToastWithBottom(requireContext(), "Updated successfully", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);

                            if (allUsersAdapter != null) {
                                allUsersAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            MyTasksToast.showErrorToastWithBottom(requireContext(), "Failed to update", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            MyTasksToast.showErrorToastWithBottom(requireContext(), e.getMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }
}