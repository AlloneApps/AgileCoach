package com.task.agilecoach.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TaskMaster implements Parcelable {
    private int taskMasterId;
    private String taskHeader;
    private String taskDescription;
    private String taskCreatedBy;
    private String taskCreatedOn;
    private String taskType;
    private double taskEstimation;
    private List<TasksSubDetails> tasksSubDetailsList = new ArrayList<>();

    public TaskMaster() {
    }

    protected TaskMaster(Parcel in) {
        taskMasterId = in.readInt();
        taskHeader = in.readString();
        taskDescription = in.readString();
        taskCreatedBy = in.readString();
        taskCreatedOn = in.readString();
        taskType = in.readString();
        taskEstimation = in.readDouble();
        tasksSubDetailsList = in.createTypedArrayList(TasksSubDetails.CREATOR);
    }

    public static final Creator<TaskMaster> CREATOR = new Creator<TaskMaster>() {
        @Override
        public TaskMaster createFromParcel(Parcel in) {
            return new TaskMaster(in);
        }

        @Override
        public TaskMaster[] newArray(int size) {
            return new TaskMaster[size];
        }
    };

    public int getTaskMasterId() {
        return taskMasterId;
    }

    public void setTaskMasterId(int taskMasterId) {
        this.taskMasterId = taskMasterId;
    }

    public String getTaskHeader() {
        return taskHeader;
    }

    public void setTaskHeader(String taskHeader) {
        this.taskHeader = taskHeader;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskCreatedBy() {
        return taskCreatedBy;
    }

    public void setTaskCreatedBy(String taskCreatedBy) {
        this.taskCreatedBy = taskCreatedBy;
    }

    public String getTaskCreatedOn() {
        return taskCreatedOn;
    }

    public void setTaskCreatedOn(String taskCreatedOn) {
        this.taskCreatedOn = taskCreatedOn;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public double getTaskEstimation() {
        return taskEstimation;
    }

    public void setTaskEstimation(double taskEstimation) {
        this.taskEstimation = taskEstimation;
    }

    public List<TasksSubDetails> getTasksSubDetailsList() {
        return tasksSubDetailsList;
    }

    public void setTasksSubDetailsList(List<TasksSubDetails> tasksSubDetailsList) {
        this.tasksSubDetailsList = tasksSubDetailsList;
    }

    @Override
    public String toString() {
        return "TaskMaster{" +
                "taskMasterId=" + taskMasterId +
                ", taskHeader='" + taskHeader + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskCreatedBy='" + taskCreatedBy + '\'' +
                ", taskCreatedOn='" + taskCreatedOn + '\'' +
                ", taskType='" + taskType + '\'' +
                ", taskEstimation=" + taskEstimation +
                ", tasksSubDetailsList=" + tasksSubDetailsList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(taskMasterId);
        parcel.writeString(taskHeader);
        parcel.writeString(taskDescription);
        parcel.writeString(taskCreatedBy);
        parcel.writeString(taskCreatedOn);
        parcel.writeString(taskType);
        parcel.writeDouble(taskEstimation);
        parcel.writeTypedList(tasksSubDetailsList);
    }
}
