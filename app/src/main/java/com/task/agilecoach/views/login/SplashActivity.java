package com.task.agilecoach.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.views.main.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);

            setUpView();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpView() {
        try {
            String loginToken = Utils.getSharedPrefsString(SplashActivity.this, AppConstants.LOGIN_TOKEN);
            Log.d(TAG, "setUpView: login token: "+loginToken);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (loginToken.isEmpty()) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }, SPLASH_TIME_OUT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}