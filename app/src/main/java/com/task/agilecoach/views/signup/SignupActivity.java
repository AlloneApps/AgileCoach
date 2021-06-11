package com.task.agilecoach.views.signup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.jakewharton.rxbinding.view.RxView;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.dataUtils.DataUtils;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.ServerResponse;
import com.task.agilecoach.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    private EditText editEmailId, editPassword, editRetypePassword, editFirstName, editLastName, editMobileNumber, editDob;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setUpViews();
    }

    private void setUpViews() {

        editEmailId = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editRetypePassword = findViewById(R.id.edit_retype_password);

        editFirstName = findViewById(R.id.edit_first_name);
        editLastName = findViewById(R.id.edit_last_name);
        editMobileNumber = findViewById(R.id.edit_mobile_phone);
        editDob = findViewById(R.id.edit_dob);
        textGender = findViewById(R.id.text_gender);

        btnSignup = findViewById(R.id.btn_signup);

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
                    builderSingle.setTitle("Select Task Type");

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
                    user.setEmailId(Utils.getFieldValue(editEmailId));
                    user.setPassword(Utils.getFieldValue(editPassword));
                    user.setFirstName(Utils.getFieldValue(editFirstName));
                    user.setLastName(Utils.getFieldValue(editLastName));
                    user.setMobileNumber(Utils.getFieldValue(editMobileNumber));
                    user.setDateOfBirth(Utils.getFieldValue(editDob));
                    user.setGender(textGender.getText().toString().trim());
                    user.setRole(AppConstants.User_ROLE);

                    ServerResponse serverResponse = Utils.saveUser(SignupActivity.this, user);

                    if (serverResponse.getResponseCode().equals("200")) {
                        MyTasksToast.showSuccessToast(SignupActivity.this, serverResponse.getResponseMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        navigateToLogin();
                    } else {
                        MyTasksToast.showErrorToastWithBottom(SignupActivity.this, serverResponse.getResponseMessage(), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                    }
                }
            }
        });
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
        if (Utils.isEmptyField(editEmailId.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter email id.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editPassword.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter password.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editRetypePassword.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter retype password.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (!(editPassword.getText().toString().trim().equals(editRetypePassword.getText().toString().trim()))) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Password and Retype password missmatch.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editFirstName.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter first name.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editLastName.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter last name.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editMobileNumber.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter mobile number.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(editDob.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter date of birth.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (Utils.isEmptyField(textGender.getText().toString().trim())) {
            MyTasksToast.showErrorToastWithBottom(SignupActivity.this, "Please enter gender.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        }
        return true;
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

    /**
     * Updates editDate EditText text with user selected date from date picker dialog or updates
     * with current date.
     */
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
}