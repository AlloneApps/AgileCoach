package com.task.agilecoach.views.signup;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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
import com.task.agilecoach.model.User;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText editEmailId, editFirstName, editLastName, editMobileNumber, editDob;
    private TextInputEditText mPin;
    private TextView textGender;
    private Button btnSignup;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_signup);

            progressDialog = new ProgressDialog(SignupActivity.this);

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);

            setUpViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        try {
            editMobileNumber = findViewById(R.id.edit_mobile_phone);
            mPin = findViewById(R.id.edit_mPin);

            editFirstName = findViewById(R.id.edit_first_name);
            editLastName = findViewById(R.id.edit_last_name);

            editEmailId = findViewById(R.id.edit_email);
            editDob = findViewById(R.id.edit_dob);
            textGender = findViewById(R.id.text_gender);

            btnSignup = findViewById(R.id.btn_signup);

            editMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            mPin.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

            editDob.setKeyListener(null);

            RxView.touches(editDob).subscribe(motionEvent -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    showDatePicker().show();
            });

            List<String> genderTypeList = DataUtils.getGenderType();

            RxView.touches(textGender).subscribe(motionEvent -> {
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(SignupActivity.this);
                        builderSingle.setTitle("Select Gender");

                        final ArrayAdapter<String> genderTypeSelectionAdapter = new ArrayAdapter<String>(SignupActivity.this,
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

            btnSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateFields()) {
                        User user = new User();
                        user.setMobileNumber(Utils.getFieldValue(editMobileNumber));
                        user.setmPin(Utils.getFieldValue(mPin));
                        user.setFirstName(Utils.getFieldValue(editFirstName));
                        user.setLastName(Utils.getFieldValue(editLastName));
                        user.setEmailId(Utils.getFieldValue(editEmailId));
                        user.setDateOfBirth(Utils.getFieldValue(editDob));
                        user.setGender(textGender.getText().toString().trim());
                        user.setRole(AppConstants.User_ROLE);
                        user.setIsActive("false");

                        boolean isNotExistUser = verifyUserRegistration(SignupActivity.this, user);
                        Log.d(TAG, "onClick: isNotExistUser:" + isNotExistUser);
                        if (isNotExistUser) {
                            showProgressDialog("Processing please wait.");
                            signUpNewUser(SignupActivity.this, user);
                        } else {
                            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Mobile number already exists", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToLogin() {
        try {
            Intent intent = new Intent();
            this.setResult(RESULT_OK, intent);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        try {
            if (Utils.isEmptyField(editMobileNumber.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter mobile number.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(mPin.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter your mPin.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (mPin.getText().toString().trim().length() > 4 || mPin.getText().toString().trim().length() < 4) {
                MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "mPin must be four digits", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(editFirstName.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter first name.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(editLastName.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter last name.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(editEmailId.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter email id.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(editDob.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter date of birth.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                return false;
            } else if (Utils.isEmptyField(textGender.getText().toString().trim())) {
                MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter gender.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this, date, calendar
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

    public void signUpNewUser(Context context, User user) {
        try {
            mUserReference.child(user.getMobileNumber()).setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            // ...
                            hideProgressDialog();
                            MyTasksToast.showSuccessToastWithBottom(SignupActivity.this, "User created successfully, Contact admin to activate.", MyTasksToast.MYTASKS_TOAST_LENGTH_LONG);
                            navigateToLogin();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            // ...
                            hideProgressDialog();
                            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Failed to register", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, e.getMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    public boolean verifyUserRegistration(Context context, User user) {
        final boolean[] returnValue = {true};
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);

            databaseReference.child(user.getMobileNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String mobileNumber = snapshot.child(FireBaseDatabaseConstants.MOBILE_NUMBER).getValue(String.class);
                        Log.d(TAG, "onDataChange: userMobileNumber: " + user.getMobileNumber());
                        Log.d(TAG, "onDataChange: mobileNumber: " + mobileNumber);
                        if (mobileNumber != null) {
                            if (mobileNumber.equals(user.getMobileNumber())) {
                                returnValue[0] = false;
                            } else {
                                returnValue[0] = true;
                            }
                        } else {
                            returnValue[0] = true;
                        }
                    } else {
                        returnValue[0] = true;
                        Log.d(TAG, "onDataChange: snapchat not exists");
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    returnValue[0] = true;
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                }
            });
            return returnValue[0];
        } catch (Exception e) {
            e.printStackTrace();
            return returnValue[0];
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