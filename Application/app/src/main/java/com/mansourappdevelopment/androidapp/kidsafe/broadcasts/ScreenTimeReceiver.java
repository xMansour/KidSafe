package com.mansourappdevelopment.androidapp.kidsafe.broadcasts;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

public class ScreenTimeReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenTimeReceiverTAG";
    private long startTime;
    private long endTime;
    private long period;
    private long screenTime;
    private FirebaseUser user;
    private DevicePolicyManager devicePolicyManager;

    public ScreenTimeReceiver(FirebaseUser user) {
        this.user = user;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            startTime = System.currentTimeMillis();
            devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            
            Log.i(TAG, "onReceive: startTime: " + startTime);
            if (screenTime > 10) {
                devicePolicyManager.lockNow();
            }

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            endTime = System.currentTimeMillis();
            Log.i(TAG, "onReceive: endTime: " + endTime);
            period = endTime - startTime;
            Log.i(TAG, "onReceive: period: " + period);

            if (period != startTime && period != endTime && period != 0) {
                screenTime += period / 1000;    //seconds
                Log.i(TAG, "onReceive: screenTime: " + screenTime + "s");
            }
        }


    }
}
