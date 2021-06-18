package com.task.agilecoach.views.profile;

import android.app.DatePickerDialog;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding.view.RxView;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.FireBaseDatabaseConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.dataUtils.DataUtils;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {
    private static final String TAG = "Profile";
    private View rootView;
    private CircleImageView imageUser;

    private EditText editEmailId, editFirstName, editLastName, editDob;
    private TextView textGender;
    private Button btnUpdate;
    private final Calendar calendar = Calendar.getInstance();
    final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateView();
    };

    private String dateOfBirth;
    private ProgressDialog progressDialog;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReference;


    public Profile() {
        // Required empty public constructor
    }


    public static Profile createInstance(Bundle bundle) {
        Profile fragment = new Profile();
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
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            ((MainActivity) requireActivity()).setTitle("Profile");

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);

            setUpViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        try {
            imageUser = rootView.findViewById(R.id.image_user);

            editFirstName = rootView.findViewById(R.id.edit_first_name);
            editLastName = rootView.findViewById(R.id.edit_last_name);

            editEmailId = rootView.findViewById(R.id.edit_email);
            editDob = rootView.findViewById(R.id.edit_dob);
            textGender = rootView.findViewById(R.id.text_gender);

            btnUpdate = rootView.findViewById(R.id.btn_update);

            editDob.setKeyListener(null);

            RxView.touches(editDob).subscribe(motionEvent -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    showDatePicker().show();
            });

            List<String> genderTypeList = DataUtils.getGenderType();

            RxView.touches(textGender).subscribe(motionEvent -> {
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(requireContext());
                        builderSingle.setTitle("Select Gender");

                        final ArrayAdapter<String> genderTypeSelectionAdapter = new ArrayAdapter<String>(requireContext(),
                                android.R.layout.select_dialog_singlechoice, genderTypeList) {
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

                        builderSingle.setAdapter(genderTypeSelectionAdapter, (dialog, position) -> {
                            textGender.setText(genderTypeSelectionAdapter.getItem(position));
                        });
                        builderSingle.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateFields()) {

                        User loginUser = Utils.getLoginUserDetails(requireContext());

                        Log.d(TAG, "onClick: loginUser: " + loginUser);
                        User user = new User();
                        user.setMobileNumber(loginUser.getMobileNumber());
                        user.setmPin(loginUser.getmPin());

                        user.setFirstName(Utils.getFieldValue(editFirstName));
                        user.setLastName(Utils.getFieldValue(editLastName));
                        user.setEmailId(Utils.getFieldValue(editEmailId));
                        user.setDateOfBirth(Utils.getFieldValue(editDob));
                        user.setGender(textGender.getText().toString().trim());
                        user.setRole(AppConstants.User_ROLE);
                        user.setIsActive("true");

                        if (isAnyUpdate(user)) {
                            showProgressDialog("Processing please wait.");
                            updateUserDetails(user);
                        } else {
                            MyTasksToast.showInfoToast(requireContext(), "Nothing to update.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    }
                }
            });

            User loginUser = Utils.getLoginUserDetails(requireContext());
            Log.d(TAG, "setUpViews: loginUser: " + loginUser);
            updateUserDetailsToView(loginUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAnyUpdate(User editedUserDetails) {
        try {
            User user = Utils.getLoginUserDetails(requireContext());

            if (user != null) {
                if (!(user.getFirstName().equals(editedUserDetails.getFirstName()))) {
                    return true;
                } else if (!(user.getLastName().equals(editedUserDetails.getLastName()))) {
                    return true;
                } else if (!(user.getEmailId().equals(editedUserDetails.getEmailId()))) {
                    return true;
                } else if (!(user.getDateOfBirth().equals(editedUserDetails.getDateOfBirth()))) {
                    return true;
                } else if (!(user.getGender().equals(editedUserDetails.getGender()))) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateFields() {
        try {
            if (Utils.isEmptyField(editFirstName.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter first name.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(editLastName.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter last name.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(editEmailId.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter email id.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(editDob.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter date of birth.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(textGender.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(requireContext(), "Please enter gender.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private DatePickerDialog showDatePicker() {
        try {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            return datePickerDialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateDateView() {
        try {
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            editDob.setText(sdf.format(calendar.getTime()));
            dateOfBirth = sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserDetails(User user) {
        try {
            mUserReference.child(user.getMobileNumber()).setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();

                            Utils.saveLoginUserDetails(requireContext(), user);

                            Log.d(TAG, "onSuccess: loginUserUpdate: " + Utils.getLoginUserDetails(requireContext()));

                            MyTasksToast.showSuccessToastWithBottom(requireContext(), "User details updated successfully", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);

                            updateUserDetailsToView(user);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            MyTasksToast.showErrorToastWithBottom(requireContext(), "Failed to update details", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
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
                editFirstName.setText(user.getFirstName());
                editLastName.setText(user.getLastName());
                editEmailId.setText(user.getEmailId());
                editDob.setText(user.getDateOfBirth());
                textGender.setText(user.getGender());

                Log.d(TAG, "updateUserDetailsToView: userGender: " + user.getGender());

                if (user.getGender().equals(AppConstants.FEMALE_GENDER)) {
                    imageUser.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_female_avatar));
                } else {
                    imageUser.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_male_avatar));
                }

                ((MainActivity) requireActivity()).setProfileAvatar(user);
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