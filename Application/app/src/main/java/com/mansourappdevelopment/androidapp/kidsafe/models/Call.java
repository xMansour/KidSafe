package com.mansourappdevelopment.androidapp.kidsafe.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mansourappdevelopment.androidapp.kidsafe.utils.DateUtils;

public class Call implements Parcelable, Comparable<Call> {
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
	
	public String getCallType() {
		return callType;
	}
	
	public void setCallType(String callType) {
		this.callType = callType;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
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
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(callType);
		parcel.writeString(phoneNumber);
		parcel.writeString(contactName);
		parcel.writeString(callTime);
		parcel.writeString(callDurationInSeconds);
	}
	
	@Override
	public int compareTo(Call call) {
		return DateUtils.stringToDate(getCallTime(), DateUtils.FORMAT).compareTo(DateUtils.stringToDate(call.getCallTime(), DateUtils.FORMAT));
		
	}
	
	public String getCallTime() {
		return callTime;
	}
	
	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}
}
