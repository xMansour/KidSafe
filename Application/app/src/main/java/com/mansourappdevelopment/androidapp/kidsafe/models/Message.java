package com.mansourappdevelopment.androidapp.kidsafe.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mansourappdevelopment.androidapp.kidsafe.utils.DateUtils;

public class Message implements Parcelable, Comparable<Message> {
	public static final Creator<Message> CREATOR = new Creator<Message>() {
		@Override
		public Message createFromParcel(Parcel in) {
			return new Message(in);
		}
		
		@Override
		public Message[] newArray(int size) {
			return new Message[size];
		}
	};
	private String senderPhoneNumber;
	private String messageBody;
	private String timeReceived;
	private String contactName;
	
	public Message() {
	
	}
	
	public Message(String senderPhoneNumber, String messageBody, String timeReceived, String contactName) {
		this.senderPhoneNumber = senderPhoneNumber;
		this.messageBody = messageBody;
		this.timeReceived = timeReceived;
		this.contactName = contactName;
	}
	
	protected Message(Parcel in) {
		senderPhoneNumber = in.readString();
		messageBody = in.readString();
		timeReceived = in.readString();
		contactName = in.readString();
	}
	
	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public String getSenderPhoneNumber() {
		return senderPhoneNumber;
	}
	
	public void setSenderPhoneNumber(String senderPhoneNumber) {
		this.senderPhoneNumber = senderPhoneNumber;
	}
	
	public String getMessageBody() {
		return messageBody;
	}
	
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(senderPhoneNumber);
		parcel.writeString(messageBody);
		parcel.writeString(timeReceived);
		parcel.writeString(contactName);
	}
	
	@Override
	public int compareTo(Message message) {
		return DateUtils.stringToDate(getTimeReceived(), DateUtils.FORMAT).compareTo(DateUtils.stringToDate(message.getTimeReceived(), DateUtils.FORMAT));
	}
	
	public String getTimeReceived() {
		return timeReceived;
	}
	
	public void setTimeReceived(String timeReceived) {
		this.timeReceived = timeReceived;
	}
}
