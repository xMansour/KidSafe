package com.mansourappdevelopment.androidapp.kidsafe.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.mansourappdevelopment.androidapp.kidsafe.services.UpdateAppStatsForegroundService;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, UpdateAppStatsForegroundService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
