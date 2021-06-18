package com.task.agilecoach.views.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.task.agilecoach.BuildConfig;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.FireBaseDatabaseConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.main.MainActivity;
import com.task.agilecoach.views.signup.SignupActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int SIGNUP_REQUEST_CODE = 101;
    private ProgressDialog progressDialog;

    private EditText editMobileNumber;
    private TextInputEditText editMPin;
    private TextView textSignup, textVersion;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(LoginActivity.this);

        setContentView(R.layout.activity_login);

        setUpView();
    }

    private void setUpView() {
        editMobileNumber = findViewById(R.id.edit_mobile_number);
        editMPin = findViewById(R.id.edit_mPin);

        textSignup = findViewById(R.id.text_signup);
        textVersion = findViewById(R.id.text_version);

        btnLogin = findViewById(R.id.btn_login);

        editMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        editMPin.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        String version = "v." + BuildConfig.VERSION_NAME;
        textVersion.setText(version);

        SpannableString signUpStyledString = new SpannableString(getApplicationContext().getResources().getString(R.string.account_signup));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(intent, SIGNUP_REQUEST_CODE);
            }
        };
        signUpStyledString.setSpan(clickableSpan, 23, 30, 0);
        signUpStyledString.setSpan(new RelativeSizeSpan(1.2f), 23, 30, 0);
        signUpStyledString.setSpan(new ForegroundColorSpan(Color.BLACK), 23, 30, 0);
        textSignup.setText(signUpStyledString);
        textSignup.setMovementMethod(LinkMovementMethod.getInstance());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    Log.d(TAG, "onClick mobileNumber: " + editMobileNumber.getText().toString().trim());
                    Log.d(TAG, "onClick mPin: " + editMPin.getText().toString().trim());

                    String userMobileNumber = editMobileNumber.getText().toString().trim();
                    String userMPin = editMPin.getText().toString().trim();

                   /* LoginResponse loginResponse = Utils.checkLogin(LoginActivity.this,editMobileNumber.getText().toString().trim(),editMPin.getText().toString().trim());
                    Log.d(TAG, "onClick: loginResponse: "+loginResponse);
                    if(loginResponse != null){
                        if(loginResponse.getResponseCode().equals("200")){
                            MyTasksToast.showSuccessToastWithBottom(LoginActivity.this, loginResponse.getResponseMessage(),MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                            navigateToMainActivity();
                        }else{
                           MyTasksToast.showErrorToastWithBottom(LoginActivity.this,loginResponse.getResponseMessage(),MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    }*/

                    showProgressDialog("Verifying please wait.");

                    verifyUserLogin(LoginActivity.this, userMobileNumber, userMPin);


//                    navigateToMainActivity();

                }
            }
        });
    }

    private boolean validateFields() {
        if (editMobileNumber.getText().toString().trim().isEmpty()) {
            MyTasksToast.showErrorToastWithBottom(LoginActivity.this, "Please enter mobile number", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (editMPin.getText().toString().trim().isEmpty()) {
            MyTasksToast.showErrorToastWithBottom(LoginActivity.this, "Please enter mPin", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (editMPin.getText().toString().trim().length() > 4 || editMPin.getText().toString().trim().length() < 4) {
            MyTasksToast.showErrorToastWithBottom(LoginActivity.this, "mPin must be four digits", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        }
        return true;
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void verifyUserLogin(Context context, String userMobileNumber, String mPin) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);

            databaseReference.child(userMobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String mobileNumber = snapshot.child(FireBaseDatabaseConstants.MOBILE_NUMBER).getValue(String.class);
                        String mobilePin = snapshot.child(FireBaseDatabaseConstants.M_PIN).getValue(String.class);
                        Log.d(TAG, "onDataChange: mobileNumber: " + mobileNumber);
                        Log.d(TAG, "onDataChange: mobileNumber: " + mobilePin);
                        if (mobileNumber != null && mobilePin != null) {
                            if (userMobileNumber.equals(mobileNumber) && mobilePin.equals(mPin)) {
                                MyTasksToast.showSuccessToastWithBottom(context, "Login success", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);

                                String mPin = snapshot.child(FireBaseDatabaseConstants.M_PIN).getValue(String.class);
                                String firstName = snapshot.child(FireBaseDatabaseConstants.FIRST_NAME).getValue(String.class);
                                String lastName = snapshot.child(FireBaseDatabaseConstants.LAST_NAME).getValue(String.class);
                                String email_id = snapshot.child(FireBaseDatabaseConstants.EMAIL_ID).getValue(String.class);
                                String date_of_birth = snapshot.child(FireBaseDatabaseConstants.DATE_OF_BIRTH).getValue(String.class);
                                String gender = snapshot.child(FireBaseDatabaseConstants.GENDER).getValue(String.class);
                                String role = snapshot.child(FireBaseDatabaseConstants.ROLE).getValue(String.class);

                                boolean isActive = true;
                                if(Objects.equals(snapshot.child(FireBaseDatabaseConstants.IS_ACTIVE).getValue(String.class), "false")){
                                    isActive = false;
                                }

                                User user = new User();
                                user.setMobileNumber(mobileNumber);
                                user.setmPin(mPin);
                                user.setFirstName(firstName);
                                user.setLastName(lastName);
                                user.setEmailId(email_id);
                                user.setDateOfBirth(date_of_birth);
                                user.setGender(gender);
                                user.setRole(role);
                                user.setActive(isActive);

                                Utils.saveSharedPrefsString(context, AppConstants.LOGIN_TOKEN, user.getMobileNumber());
                                Utils.saveLoginUserDetails(context, user);

                                hideProgressDialog();

                                navigateToMainActivity();

                            } else {
                                hideProgressDialog();
                                MyTasksToast.showErrorToastWithBottom(context, "Failed to login, try again", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                            }
                        } else {
                            hideProgressDialog();
                            MyTasksToast.showErrorToastWithBottom(context, "Mobile number and mPin null", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    } else {
                        hideProgressDialog();
                        MyTasksToast.showErrorToastWithBottom(context, "Failed to login, try again", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    hideProgressDialog();
                    MyTasksToast.showErrorToastWithBottom(context, error.getMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            hideProgressDialog();
            e.printStackTrace();
            MyTasksToast.showErrorToastWithBottom(context, e.getMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
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