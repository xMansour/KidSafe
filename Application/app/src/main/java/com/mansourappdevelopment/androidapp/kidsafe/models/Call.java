package com.mansourappdevelopment.androidapp.kidsafe.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Call implements Parcelable {
    private String callType;
    private String phoneNumber;
    private String contactName;
    private String callTime;
    private String callDurationInSeconds;

    public Call() {

    }

    public Call(String callType, String phoneNumber, String contactName, String callTime, String callDurationInSeconds) {
        this.callType = callType;
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.callTime = callTime;
        this.callDurationInSeconds = callDurationInSeconds;
    }

    protected Call(Parcel in) {
        callType = in.readString();
        phoneNumber = in.readString();
        contactName = in.readString();
        callTime = in.readString();
        callDurationInSeconds = in.readString();
    }

    public static final Creator<Call> CREATOR = new Creator<Call>() {
        @Override
        public Call createFromParcel(Parcel in) {
            return new Call(in);
        }

        @Override
        public Call[] newArray(int size) {
            return new Call[size];
        }
    };

    public String getCallType() {
        return callType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallDurationInSeconds() {
        return callDurationInSeconds;
    }

    public void setCallDurationInSeconds(String callDurationInSeconds) {
        this.callDurationInSeconds = callDurationInSeconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(callType);
        dest.writeString(phoneNumber);
        dest.writeString(contactName);
        dest.writeString(callTime);
        dest.writeString(callDurationInSeconds);
    }
}
