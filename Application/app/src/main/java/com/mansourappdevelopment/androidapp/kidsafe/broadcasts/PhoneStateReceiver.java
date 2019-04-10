package com.mansourappdevelopment.androidapp.kidsafe.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Call;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PhoneStateReceiver extends BroadcastReceiver {
    public static final String TAG = "PhoneStateReceiver";
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    //private HashMap<String, Object> calls;
    private Context context;
    private double startCallTime;
    private double endCallTime;
    private Call call;

    public PhoneStateReceiver(FirebaseUser user) {
        this.user = user;
        //this.calls = new HashMap<>();
        //call = new Call(null, null, null, null, null);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            Log.i(TAG, "onReceive: phoneState: " + phoneState);
            String uid = user.getUid();

            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String contactName = getContactName(phoneNumber);

            String format = "yyyy.MM.dd 'at' HH:mm:ss";
            String callTime = getCallTime(format);

            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                startCallTime = System.currentTimeMillis();

                /*calls.clear();
                calls.put("call", "Incoming Call");
                calls.put("phoneNumber", phoneNumber);
                calls.put("contactName", contactName);
                calls.put("callTime", callTime);

                Log.i(TAG, "onReceive: incoming call from: " + phoneNumber + " and the state is: " + phoneState + " and the name is: " + contactName);*/

                /*call.setCallType("Incoming Call");
                call.setPhoneNumber(phoneNumber);
                call.setContactName(contactName);
                call.setCallTime(callTime);*/

                call = new Call("Incoming Call", phoneNumber, contactName, callTime, null);

            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                startCallTime = System.currentTimeMillis();

                /*calls.clear();
                calls.put("call", "Outgoing Call");
                calls.put("phoneNumber", phoneNumber);
                calls.put("contactName", contactName);
                calls.put("callTime", callTime);

                Log.i(TAG, "onReceive: outgoing call to: " + phoneNumber + " and the state is: " + phoneState + " and the name is: " + contactName);*/

                /*call.setCallType("Outgoing Call");
                call.setPhoneNumber(phoneNumber);
                call.setContactName(contactName);
                call.setCallTime(callTime);*/

                call = new Call("Outgoing Call", phoneNumber, contactName, callTime, null);


            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                endCallTime = System.currentTimeMillis();
                double callDuration = (endCallTime - startCallTime) / 1000;

                /*calls.put("callDurationInSeconds", String.valueOf(callDuration));
                databaseReference.child("childs").child(uid).child("calls").push().setValue(calls);*/

                call.setCallDurationInSeconds(String.valueOf(callDuration));
                databaseReference.child("childs").child(uid).child("calls").push().setValue(call);
                //TODO:: written 4 times

            }


        }

    }

    private String getContactName(String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName = "Unknown Number";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    private String getCallTime(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }
}
