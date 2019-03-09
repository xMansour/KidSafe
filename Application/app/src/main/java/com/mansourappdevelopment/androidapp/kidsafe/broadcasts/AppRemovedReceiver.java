package com.mansourappdevelopment.androidapp.kidsafe.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

public class AppRemovedReceiver extends BroadcastReceiver {
    public static final String TAG = "AppRemovedReceiver";
    private FirebaseUser user;

    public AppRemovedReceiver(FirebaseUser user) {
        this.user = user;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        String componentPackage = intent.getData().getSchemeSpecificPart();

        String packageName = null;
        String appName = null;

        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(componentPackage, 0);
            packageName = applicationInfo.packageName;
            appName = (String) applicationInfo.loadLabel(packageManager);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "onReceive: action: " + action);
        Log.i(TAG, "onReceive: packageName: " + packageName);
        Log.i(TAG, "onReceive: appName: " + appName);
        Log.i(TAG, "onReceive: componentPackage: " + componentPackage);
        Log.i(TAG, "onReceive: " + intent.getData().getScheme());
        Log.i(TAG, "onReceive: " + intent.getData());

    }
}
