package com.mansourappdevelopment.androidapp.kidsafe.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {
    private String senderPhoneNumber;
    private String messageBody;
    private String timeReceived;

    public Message() {

    }

    public Message(String senderPhoneNumber, String messageBody, String timeReceived) {
        this.senderPhoneNumber = senderPhoneNumber;
        this.messageBody = messageBody;
        this.timeReceived = timeReceived;
    }

    protected Message(Parcel in) {
        senderPhoneNumber = in.readString();
        messageBody = in.readString();
        timeReceived = in.readString();
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(senderPhoneNumber);
        dest.writeString(messageBody);
        dest.writeString(timeReceived);
    }
}
