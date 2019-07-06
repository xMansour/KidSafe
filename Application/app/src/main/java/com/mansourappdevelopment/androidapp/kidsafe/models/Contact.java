package com.mansourappdevelopment.androidapp.kidsafe.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
	public static final Creator<Contact> CREATOR = new Creator<Contact>() {
		@Override
		public Contact createFromParcel(Parcel in) {
			return new Contact(in);
		}
		
		@Override
		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};
	private String contactName;
	private String contactNumber;
	
	public Contact() {
	
	}
	
	public Contact(String contactName, String contactNumber) {
		this.contactName = contactName;
		this.contactNumber = contactNumber;
	}
	
	protected Contact(Parcel in) {
		contactName = in.readString();
		contactNumber = in.readString();
	}
	
	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public String getContactNumber() {
		return contactNumber;
	}
	
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(contactName);
		parcel.writeString(contactNumber);
	}
}
