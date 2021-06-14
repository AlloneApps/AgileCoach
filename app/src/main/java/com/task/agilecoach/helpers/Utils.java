package com.task.agilecoach.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.task.agilecoach.model.LoginResponse;
import com.task.agilecoach.model.ServerResponse;
import com.task.agilecoach.model.TaskStatus;
import com.task.agilecoach.model.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Utils {
    private static final String TAG = "Utils";

    public static boolean isEmptyField(String value) {
        return value.trim().isEmpty();
    }

    public static String getFieldValue(EditText editTextField) {
        return editTextField.getText().toString().trim();
    }

    public static void saveLoginUserDetails(Context context,User user){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String loginUserDetails = gson.toJson(user);
        sharedPreferences.edit().putString(AppConstants.LOGIN_USER_DETAILS, loginUserDetails).apply();
    }

    public static User getLoginUserDetails(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String userDetailsString = sharedPreferences.getString(AppConstants.LOGIN_USER_DETAILS,"");
        return gson.fromJson(userDetailsString,User.class);
    }

    public static void removeLoginUserDetails(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
        sharedPreferences.edit().remove(AppConstants.LOGIN_USER_DETAILS).apply();
    }

    public static String getSharedPrefsString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void saveSharedPrefsString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static void removeSharedPrefsString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
        sharedPreferences.edit().remove(key).apply();
    }

    public static ServerResponse saveUser(Context context, User user) {
        ServerResponse serverResponse = new ServerResponse();
        try {
            ArrayList<User> userList;
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.USER_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<User>>() {
                }.getType();
                userList = gson.fromJson(readString, type);
                int count = 0;
                assert userList != null;
                for (User userSub : userList) {
                    if (userSub.getMobileNumber().equals(user.getMobileNumber())) {
                        count = count + 1;
                    }
                }
                if (count == 0) {
                    userList.add(user);
                    serverResponse.setResponseCode("200");
                    serverResponse.setResponseMessage("Signup successful.");
                } else {
                    serverResponse.setResponseCode("400");
                    String alreadyRegistered = user.getMobileNumber() + " mobile number already registered to " + user.getFirstName() + " " + user.getLastName();
                    serverResponse.setResponseMessage(alreadyRegistered);
                }
            } else {
                userList = new ArrayList<>();
                userList.add(user);
                serverResponse.setResponseCode("200");
                serverResponse.setResponseMessage("Signup successful.");
            }
            String writeString = gson.toJson(userList);
            prefs.edit().putString(AppConstants.USER_LIST, writeString).apply();
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            serverResponse.setResponseCode("500");
            serverResponse.setResponseMessage("Signup Failed, please try again.");
            return serverResponse;
        }
    }

    public static User getUserDetails(Context context, String userEmail) {
        Log.d(TAG, "getUserDetails: userEmail");
        User userMaster = null;
        try {
            ArrayList<User> userList;
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.USER_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<User>>() {
                }.getType();
                userList = gson.fromJson(readString, type);
                int count = 0;
                assert userList != null;
                for (User userSub : userList) {
                    if (userSub.getEmailId().equals(userEmail)) {
                        Log.d(TAG, "getUserDetails: userDetails: " + userSub);
                        return userSub;
                    }
                }
            } else {
                return userMaster;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return userMaster;
        }
        return userMaster;
    }

    public static LoginResponse checkLogin(Context context, String userEmail, String password) {
        LoginResponse loginResponse = new LoginResponse();
        if (userEmail.equals(ServerConstants.ADMIN_EMAIL) && password.equals(ServerConstants.ADMIN_PASSWORD)) {
            User user = new User();
            user.setMobileNumber("1234567890");
            user.setEmailId(ServerConstants.ADMIN_EMAIL);
            user.setRole(AppConstants.ADMIN_ROLE);
            user.setFirstName(AppConstants.ADMIN_ROLE);
            user.setGender(AppConstants.MALE_GENDER);

            loginResponse.setResponseCode("200");
            loginResponse.setResponseMessage("Admin login");
            loginResponse.setUserDetails(user);
            Utils.saveSharedPrefsString(context, AppConstants.LOGIN_TOKEN, user.getMobileNumber());
            Utils.saveLoginUserDetails(context, user);
            return loginResponse;
        } else if(userEmail.equals(ServerConstants.ADMIN_EMAIL) || password.equals(ServerConstants.ADMIN_PASSWORD)){
            loginResponse.setResponseCode("300");
            loginResponse.setResponseMessage("Admin credential mismatch.");
            Utils.removeSharedPrefsString(context, AppConstants.LOGIN_TOKEN);
            Utils.removeLoginUserDetails(context);
            return loginResponse;
        } else {
            User user = getUserDetails(context, userEmail);
            if (user != null) {
                if (user.getmPin().equals(password)) {
                    loginResponse.setResponseCode("200");
                    loginResponse.setResponseMessage("Login successfully");
                    loginResponse.setUserDetails(user);
                    Utils.saveSharedPrefsString(context, AppConstants.LOGIN_TOKEN, user.getMobileNumber());
                    Utils.saveLoginUserDetails(context, user);
                } else {
                    loginResponse.setResponseCode("300");
                    loginResponse.setResponseMessage("Credential mismatch.");
                    loginResponse.setUserDetails(user);
                    Utils.removeSharedPrefsString(context, AppConstants.LOGIN_TOKEN);
                    Utils.removeLoginUserDetails(context);
                }
            } else {
                loginResponse.setResponseCode("400");
                loginResponse.setResponseMessage("Email not yet registered.");
                loginResponse.setUserDetails(user);
                Utils.removeSharedPrefsString(context, AppConstants.LOGIN_TOKEN);
                Utils.removeLoginUserDetails(context);
            }
            return loginResponse;
        }
    }

    public static List<User> getAllUserList(Context context) {
        List<User> userList = new ArrayList<>();
        try {
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.USER_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<User>>() {
                }.getType();
                userList = gson.fromJson(readString, type);
                return userList;
            } else {
                return userList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return userList;
        }
    }

    public static void removeAllDataWhenLogout(Context context){
        removeLoginUserDetails(context);
        removeSharedPrefsString(context,AppConstants.LOGIN_TOKEN);
    }
}
