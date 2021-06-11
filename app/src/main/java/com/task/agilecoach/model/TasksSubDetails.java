package com.task.agilecoach.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TasksSubDetails implements Parcelable {
    private int taskId;
    private String taskStatus;
    private String taskUserAssigned;
    private String taskUserId;
    private String modifiedOn;
    private String modifiedBy;

    public TasksSubDetails() {
    }

    protected TasksSubDetails(Parcel in) {
        taskId = in.readInt();
        taskStatus = in.readString();
        taskUserAssigned = in.readString();
        taskUserId = in.readString();
        modifiedOn = in.readString();
        modifiedBy = in.readString();
    }

    public static final Creator<TasksSubDetails> CREATOR = new Creator<TasksSubDetails>() {
        @Override
        public TasksSubDetails createFromParcel(Parcel in) {
            return new TasksSubDetails(in);
        }

        @Override
        public TasksSubDetails[] newArray(int size) {
            return new TasksSubDetails[size];
        }
    };

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskUserAssigned() {
        return taskUserAssigned;
    }

    public void setTaskUserAssigned(String taskUserAssigned) {
        this.taskUserAssigned = taskUserAssigned;
    }

    public String getTaskUserId() {
        return taskUserId;
    }

    public void setTaskUserId(String taskUserId) {
        this.taskUserId = taskUserId;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "TasksSubDetails{" +
                "taskId=" + taskId +
                ", taskStatus='" + taskStatus + '\'' +
                ", taskUserAssigned='" + taskUserAssigned + '\'' +
                ", taskUserId='" + taskUserId + '\'' +
                ", modifiedOn='" + modifiedOn + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(taskId);
        parcel.writeString(taskStatus);
        parcel.writeString(taskUserAssigned);
        parcel.writeString(taskUserId);
        parcel.writeString(modifiedOn);
        parcel.writeString(modifiedBy);
    }
}
