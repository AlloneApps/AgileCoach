package com.task.agilecoach.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String mobileNumber;
    private String mPin;
    private String firstName;
    private String lastName;
    private String emailId;
    private String dateOfBirth;
    private String gender;
    private String role;
    private boolean isActive;

    public User() {
    }

    protected User(Parcel in) {
        mobileNumber = in.readString();
        mPin = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        emailId = in.readString();
        dateOfBirth = in.readString();
        gender = in.readString();
        role = in.readString();
        isActive = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getmPin() {
        return mPin;
    }

    public void setmPin(String mPin) {
        this.mPin = mPin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "mobileNumber='" + mobileNumber + '\'' +
                ", mPin='" + mPin + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mobileNumber);
        parcel.writeString(mPin);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(emailId);
        parcel.writeString(dateOfBirth);
        parcel.writeString(gender);
        parcel.writeString(role);
        parcel.writeByte((byte) (isActive ? 1 : 0));
    }
}
