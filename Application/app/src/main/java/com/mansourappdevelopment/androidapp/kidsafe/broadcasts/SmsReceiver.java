package com.mansourappdevelopment.androidapp.kidsafe.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SmsReceiver extends BroadcastReceiver {
    public static final String TAG = "SmsReceiver";
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private SmsManager smsManager;
    //private HashMap<String, Object> messeges;


    public SmsReceiver(FirebaseUser user) {
        this.user = user;
        //this.messeges = new HashMap<>();
        this.smsManager = SmsManager.getDefault();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String senderPhoneNumber = null;
        StringBuilder message;

        if (bundle != null) {
            Object[] pdusObjs = (Object[]) bundle.get("pdus");
            message = new StringBuilder();

            for (Object pdusObj : pdusObjs) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj);
                senderPhoneNumber = currentMessage.getDisplayOriginatingAddress();
                String messageBody = currentMessage.getDisplayMessageBody().replace("\n", " ");
                message.append(messageBody);
            }
            String format = "yyyy.MM.dd 'at' HH:mm:ss";
            String timeReceived = getSmsReceivedTime(format);
            String uid = user.getUid();

            uploadMessage(senderPhoneNumber, message.toString(), timeReceived, uid);

        }

    }

    private String getSmsReceivedTime(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    private void uploadMessage(String senderPhoneNumber, String messageBody, String timeReceived, String uid) {
        Log.i(TAG, "uploadMessage: messageBody" + messageBody);
        Log.i(TAG, "uploadMessage: senderPhoneNumber" + senderPhoneNumber);
        Log.i(TAG, "uploadMessage: timeReceived" + timeReceived);

        /*messeges.clear();
        messeges.put("senderPhoneNumber", senderPhoneNumber);
        messeges.put("messageBody", messageBody);
        messeges.put("timeReceived", timeReceived);
        databaseReference.child("childs").child(uid).child("messages").push().setValue(messeges);*/

        Message message = new Message(senderPhoneNumber, messageBody, timeReceived);
        databaseReference.child("childs").child(uid).child("messages").push().setValue(message);


    }
}
