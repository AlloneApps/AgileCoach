package com.task.agilecoach.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskStatus implements Parcelable {
    private int taskId;
    private String taskName;
    private boolean isLocked;

    public TaskStatus() {
    }

    protected TaskStatus(Parcel in) {
        taskId = in.readInt();
        taskName = in.readString();
        isLocked = in.readByte() != 0;
    }

    public static final Creator<TaskStatus> CREATOR = new Creator<TaskStatus>() {
        @Override
        public TaskStatus createFromParcel(Parcel in) {
            return new TaskStatus(in);
        }

        @Override
        public TaskStatus[] newArray(int size) {
            return new TaskStatus[size];
        }
    };

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    @Override
    public String toString() {
        return "TaskStatus{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", isLocked=" + isLocked +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(taskId);
        parcel.writeString(taskName);
        parcel.writeByte((byte) (isLocked ? 1 : 0));
    }
}
