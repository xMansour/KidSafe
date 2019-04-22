package com.mansourappdevelopment.androidapp.kidsafe.broadcasts;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mansourappdevelopment.androidapp.kidsafe.models.ScreenLock;

import java.util.Calendar;

public class ScreenTimeReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenTimeReceiverTAG";
    private long startTime;
    private long endTime;
    private long period;
    private long screenTime;
    private int allowedTime;
    private int dayOfYear;
    private int lastDayOfYear;
    private Calendar calendar;
    private DevicePolicyManager devicePolicyManager;


    public ScreenTimeReceiver(ScreenLock screenLock) {
        allowedTime = screenLock.getTimeInSeconds();
        calendar = Calendar.getInstance();
        Log.i(TAG, "ScreenTimeReceiver: allowedTime: " + allowedTime);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

            startTime = System.currentTimeMillis();

            devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

            if (dayOfYear != lastDayOfYear) {
                screenTime = 0;
            }
            Log.i(TAG, "onReceive: startTime: " + startTime);
            if (screenTime > allowedTime) {
                devicePolicyManager.lockNow();
            }

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            endTime = System.currentTimeMillis();
            period = endTime - startTime;
            lastDayOfYear = dayOfYear;

            Log.i(TAG, "onReceive: endTime: " + endTime);
            Log.i(TAG, "onReceive: period: " + period);

            if (period != startTime && period != endTime && period != 0) {
                screenTime += period / 1000;    //seconds
                Log.i(TAG, "onReceive: screenTime: " + screenTime + "s");
            }
        }


    }
}
