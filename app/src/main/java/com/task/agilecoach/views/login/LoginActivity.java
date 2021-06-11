package com.task.agilecoach.views.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.task.agilecoach.BuildConfig;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.LoginResponse;
import com.task.agilecoach.views.main.MainActivity;
import com.task.agilecoach.views.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int SIGNUP_REQUEST_CODE = 101;

    private EditText editEmailId, editPassword;
    private TextView textSignup, textVersion;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        setUpView();
    }

    private void setUpView() {
        editEmailId = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);

        textSignup = findViewById(R.id.text_signup);
        textVersion = findViewById(R.id.text_version);

        btnLogin = findViewById(R.id.btn_login);

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
                if(validateFields()){
                    Log.d(TAG, "onClick email: " + editEmailId.getText().toString().trim());
                    Log.d(TAG, "onClick password: " + editPassword.getText().toString().trim());

                    LoginResponse loginResponse = Utils.checkLogin(LoginActivity.this,editEmailId.getText().toString().trim(),editPassword.getText().toString().trim());
                    Log.d(TAG, "onClick: loginResponse: "+loginResponse);
                    if(loginResponse != null){
                        if(loginResponse.getResponseCode().equals("200")){
                            MyTasksToast.showSuccessToastWithBottom(LoginActivity.this, loginResponse.getResponseMessage(),MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                            navigateToMainActivity();
                        }else{
                           MyTasksToast.showErrorToastWithBottom(LoginActivity.this,loginResponse.getResponseMessage(),MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                        }
                    }
                }
            }
        });
    }

    private boolean validateFields() {
        if (editEmailId.getText().toString().trim().isEmpty()) {
            MyTasksToast.showErrorToastWithBottom(LoginActivity.this, getString(R.string.enter_email_id_alert), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        } else if (editPassword.getText().toString().trim().isEmpty()) {
            MyTasksToast.showErrorToastWithBottom(LoginActivity.this, getString(R.string.enter_password_alert), MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
            return false;
        }
        return true;
    }

    private void navigateToMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}