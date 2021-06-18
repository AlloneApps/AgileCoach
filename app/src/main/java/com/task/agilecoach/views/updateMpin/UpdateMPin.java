package com.task.agilecoach.views.updateMpin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.FireBaseDatabaseConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.main.MainActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateMPin extends Fragment {
    private static final String TAG = "UpdateMPin";
    private View rootView;
    private CircleImageView imageUser;

    private TextInputEditText editOldMPin, editNewMPin;
    private TextView textPersonName, textMobileNumber;
    private Button btnUpdate;
    private ProgressDialog progressDialog;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReference;


    public UpdateMPin() {
        // Required empty public constructor
    }


    public static UpdateMPin createInstance(Bundle bundle) {
        UpdateMPin fragment = new UpdateMPin();
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
        rootView = inflater.inflate(R.layout.fragment_update_mpin, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) requireActivity()).setTitle("Update mPin");

        progressDialog = new ProgressDialog(requireContext());

        firebaseDatabase = FirebaseDatabase.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);

        setUpViews();
    }

    private void setUpViews() {
        imageUser = rootView.findViewById(R.id.image_user);

        editOldMPin = rootView.findViewById(R.id.edit_old_mPin);
        editNewMPin = rootView.findViewById(R.id.edit_new_mPin);

        textPersonName = rootView.findViewById(R.id.text_personal_name);
        textMobileNumber = rootView.findViewById(R.id.text_mobile_number_value);

        btnUpdate = rootView.findViewById(R.id.btn_update);

        editOldMPin.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        editNewMPin.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    User loginUser = Utils.getLoginUserDetails(requireContext());
                    Log.d(TAG, "onClick: loginUser:" + loginUser);
                    if (loginUser.getmPin().equals(editOldMPin.getText().toString().trim())) {
                        loginUser.setmPin(editNewMPin.getText().toString().trim());
                        showProgressDialog("Processing please wait.");
                        Log.d(TAG, "onClick: loginUser:" + loginUser);
                        updateUserDetails(loginUser);
                    } else {
                        MyTasksToast.showErrorToastWithBottom(requireContext(), "Please verify old mPin.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                    }
                }
            }
        });

        User loginUser = Utils.getLoginUserDetails(requireContext());
        Log.d(TAG, "setUpViews: loginUser: " + loginUser);
        updateUserDetailsToView(loginUser);

    }

    private void clearFields() {
        editOldMPin.setText("");
        editNewMPin.setText("");
    }

    private boolean validateFields() {
        if (Utils.isEmptyField(editOldMPin.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter old mPin.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (editOldMPin.getText().toString().trim().length() < 4) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "Old mPin must be 4 Digits.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editNewMPin.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter new mPin.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (editNewMPin.getText().toString().trim().length() < 4) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "New mPin must be 4 Digits.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (editOldMPin.getText().toString().trim().equals(editNewMPin.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(requireContext(), "New mPin not same as Old mPin", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        }
        return true;
    }

    public void updateUserDetails(User user) {
        try {
            mUserReference.child(user.getMobileNumber()).setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            Utils.saveLoginUserDetails(requireContext(), user);
                            Log.d(TAG, "onSuccess: " + Utils.getLoginUserDetails(requireContext()));
                            MyTasksToast.showSuccessToastWithBottom(requireContext(), "mPin updated successfully", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                            updateUserPicBasedOnGender(user);
                            clearFields();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            MyTasksToast.showErrorToastWithBottom(requireContext(), "Failed to update mPin", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            MyTasksToast.showErrorToastWithBottom(requireContext(), e.getMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    private void updateUserDetailsToView(User user) {
        try {
            if (user != null) {
                String userName = user.getFirstName() + " " + user.getLastName();
                textPersonName.setText(userName);
                textMobileNumber.setText(user.getMobileNumber());

                updateUserPicBasedOnGender(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserPicBasedOnGender(User user) {
        try {
            if (user != null) {
                if (user.getGender().equals(AppConstants.FEMALE_GENDER)) {
                    imageUser.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_female_avatar));
                } else {
                    imageUser.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_male_avatar));
                }
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