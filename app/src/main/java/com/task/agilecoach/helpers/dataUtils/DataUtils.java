package com.task.agilecoach.helpers.dataUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.model.ServerResponse;
import com.task.agilecoach.model.TaskMaster;
import com.task.agilecoach.model.TaskStatus;
import com.task.agilecoach.model.TasksSubDetails;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DataUtils {

    private static final String TAG = "DataUtils";

    public static void loadDefaultTaskStatus(Context context) {
        List<TaskStatus> taskStatusList = new ArrayList<>();
        TaskStatus taskStatusNew = new TaskStatus();
        taskStatusNew.setTaskId(1);
        taskStatusNew.setTaskName(AppConstants.TODO_STATUS);
        taskStatusNew.setLocked(true);
        taskStatusList.add(taskStatusNew);

        TaskStatus taskStatusAssigned = new TaskStatus();
        taskStatusAssigned.setTaskId(2);
        taskStatusAssigned.setTaskName(AppConstants.ASSIGNED_STATUS);
        taskStatusAssigned.setLocked(true);
        taskStatusList.add(taskStatusAssigned);

        TaskStatus taskStatusInProgress = new TaskStatus();
        taskStatusInProgress.setTaskId(3);
        taskStatusInProgress.setTaskName(AppConstants.IN_PROGRESS_STATUS);
        taskStatusInProgress.setLocked(true);
        taskStatusList.add(taskStatusInProgress);

        TaskStatus taskStatusReadyForTest = new TaskStatus();
        taskStatusReadyForTest.setTaskId(4);
        taskStatusReadyForTest.setTaskName(AppConstants.READY_FOR_TEST_STATUS);
        taskStatusReadyForTest.setLocked(true);
        taskStatusList.add(taskStatusReadyForTest);

        TaskStatus taskStatusDone = new TaskStatus();
        taskStatusDone.setTaskId(5);
        taskStatusDone.setTaskName(AppConstants.DONE_STATUS);
        taskStatusDone.setLocked(true);
        taskStatusList.add(taskStatusDone);

        Gson gson = new Gson();
        String taskStatusValue = gson.toJson(taskStatusList);

        Utils.saveSharedPrefsString(context, AppConstants.TASKS_STATUS_LIST, taskStatusValue);
    }

    public static List<String> getTaskTypeList() {
        List<String> taskTypeList = new ArrayList<>();
        taskTypeList.add(AppConstants.TASK_TYPE);
        taskTypeList.add(AppConstants.BUG_TYPE);
        return taskTypeList;
    }

    public static List<String> getGenderType() {
        List<String> genderType = new ArrayList<>();
        genderType.add(AppConstants.MALE_GENDER);
        genderType.add(AppConstants.FEMALE_GENDER);
        genderType.add(AppConstants.OTHER_GENDER);
        return genderType;
    }


    public static List<TaskStatus> getTaskStatusList(Context context) {
        List<TaskStatus> tasksStatusList = new ArrayList<>();
        try {
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.TASKS_STATUS_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<TaskStatus>>() {
                }.getType();
                tasksStatusList = gson.fromJson(readString, type);
                return tasksStatusList;
            } else {
                return tasksStatusList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return tasksStatusList;
        }
    }

    public static List<TaskMaster> getAssignedTasks(Context context, String taskUserId, boolean isReturnAll) {
        Log.d(TAG, "getAssignedTasks: taskUserId: "+taskUserId);
        List<TaskMaster> taskMasterFilteredList = new ArrayList<>();
        List<TaskMaster> taskMasterList = new ArrayList<>();
        try {
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.TASKS_MAIN_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<TaskMaster>>() {
                }.getType();
                taskMasterList = gson.fromJson(readString, type);
                assert taskMasterList != null;
                if (isReturnAll) {
                    taskMasterFilteredList = taskMasterList;
                    return taskMasterFilteredList;
                } else {
                    for (TaskMaster taskMasterMain : taskMasterList) {
                        int lastPosition = taskMasterMain.getTasksSubDetailsList().size() - 1;
                        Log.d(TAG, "getAssignedTasks lastPosition: "+lastPosition);
                        if (taskMasterMain.getTasksSubDetailsList().get(lastPosition).getTaskUserId().equals(taskUserId)) {
                            taskMasterFilteredList.add(taskMasterMain);
                            Log.d(TAG, "getAssignedTasks: filteredTask: "+taskMasterMain);
                        }
                    }
                    return taskMasterFilteredList;
                }
            } else {
               return taskMasterFilteredList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return taskMasterFilteredList;
        }
    }

    public static ServerResponse createTask(Context context, TaskMaster taskMaster) {
        ServerResponse serverResponse = new ServerResponse();
        try {
            List<TaskMaster> taskMasterList;
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.TASKS_MAIN_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<TaskMaster>>() {
                }.getType();
                taskMasterList = gson.fromJson(readString, type);
                int count = 0;
                assert taskMasterList != null;
                for (TaskMaster taskMasterMain : taskMasterList) {
                    if (taskMasterMain.getTaskHeader().equals(taskMaster.getTaskHeader())) {
                        count = count + 1;
                    }
                }
                if (count == 0) {
                    int currentId = (taskMasterList.size() + 1);
                    taskMaster.setTaskMasterId(currentId+"");
                    taskMasterList.add(taskMaster);
                    serverResponse.setResponseCode("200");
                    serverResponse.setResponseMessage("Task created successfully.");
                } else {
                    serverResponse.setResponseCode("400");
                    serverResponse.setResponseMessage("Task with same name already exists.");
                }
            } else {
                taskMasterList = new ArrayList<>();
                taskMaster.setTaskMasterId(1+"");
                taskMasterList.add(taskMaster);
                serverResponse.setResponseCode("200");
                serverResponse.setResponseMessage("Task created successfully.");
            }
            String writeString = gson.toJson(taskMasterList);
            prefs.edit().putString(AppConstants.TASKS_MAIN_LIST, writeString).apply();
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            serverResponse.setResponseCode("500");
            serverResponse.setResponseMessage("Failed to create Task, please try again.");
            return serverResponse;
        }
    }

    public static ServerResponse updateTask(Context context, TasksSubDetails tasksSubDetails) {
        ServerResponse serverResponse = new ServerResponse();
        try {
            List<TaskMaster> taskMasterList = new ArrayList<>();
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.TASKS_MAIN_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<TaskMaster>>() {
                }.getType();
                taskMasterList = gson.fromJson(readString, type);
                int count = 0;
                assert taskMasterList != null;

                for (int pos = 0; pos < taskMasterList.size(); pos++) {
                    if (taskMasterList.get(pos).getTaskMasterId() == tasksSubDetails.getTaskId()) {
                        taskMasterList.get(pos).getTasksSubDetailsList().add(tasksSubDetails);
                        count = count + 1;
                        break;
                    }
                }

                if (count > 0) {
                    serverResponse.setResponseCode("200");
                    serverResponse.setResponseMessage("Task updated successfully.");
                } else {
                    serverResponse.setResponseCode("400");
                    serverResponse.setResponseMessage("Task with this id not exists");
                }
            } else {
                serverResponse.setResponseCode("200");
                serverResponse.setResponseMessage("Task not available.");
            }
            String writeString = gson.toJson(taskMasterList);
            prefs.edit().putString(AppConstants.TASKS_MAIN_LIST, writeString).apply();
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            serverResponse.setResponseCode("500");
            serverResponse.setResponseMessage("Failed to create Task, please try again.");
            return serverResponse;
        }
    }


}
