package com.mansourappdevelopment.androidapp.kidsafe.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mansourappdevelopment.androidapp.kidsafe.utils.DateUtils;

public class Message implements Parcelable, Comparable<Message> {
    private String senderPhoneNumber;
    private String messageBody;
    private String timeReceived;
    private String contactName;

    public Message() {

    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getTimeReceived() {
        return timeReceived;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public void setTimeReceived(String timeReceived) {
        this.timeReceived = timeReceived;
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
}
