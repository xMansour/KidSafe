package com.mansourappdevelopment.androidapp.kidsafe.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity.CHILD_EMAIL;
import static com.mansourappdevelopment.androidapp.kidsafe.utils.NotificationChannelCreator.CHANNEL_ID;

public class UpdateAppStatsForegroundService extends Service {
    public static final int NOTIFICATION_ID = 27;
    public static final String TAG = "UpdateAppStatsService";
    private DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String childEmail = intent.getStringExtra(CHILD_EMAIL);
        //String notificationContent = "Monitoring device";

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String childEmail = user.getEmail();
        final String uid = user.getUid();

        Intent notificationIntent = new Intent(this, ChildSignedInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setContentTitle(notificationContent)
                .setSmallIcon(R.drawable.ic_location)   //TODO:: add the app icon
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        Query query = databaseReference.child("childs").child(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    getApps(childEmail);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void updateAppStats(ArrayList<App> apps) {
        /*for (App app : apps) {
            Log.i(TAG, "onDataChange: app name: " + app.getAppName() + ", blocked: " + app.isBlocked() + "\n");
        }*/
        Log.i(TAG, "updateAppStats: executed");
        Toast.makeText(this, "Updated the app list", Toast.LENGTH_SHORT).show();
        //TODO:: block the apps which have a blocked attribute = true

        //Toast.makeText(this, "Current App: " + getForegroundApp(), Toast.LENGTH_SHORT).show();

    }

    public void getApps(final String childEmail) {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Log.i(TAG, "onDataChange: dataSnapshot value: "+dataSnapshot.getValue());
                    //Log.i(TAG, "onDataChange: dataSnapshot as a string: "+dataSnapshot.toString());
                    //Log.i(TAG, "onDataChange: dataSnapshot children: " + dataSnapshot.getChildren());
                    //Log.i(TAG, "onDataChange: dataSnapshot key: " + dataSnapshot.getKey());

                    ArrayList<App> apps;
                    DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                    User child = nodeShot.getValue(User.class);
                    apps = child.getApps();

                    Log.i(TAG, "onDataChange: child name: " + child.getName());

                    updateAppStats(apps);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /*private String getForegroundApp() {
        String currentApp = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000,
                    time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> sortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }

                if (sortedMap != null && !sortedMap.isEmpty()) {        //not needed
                    currentApp = sortedMap.get(sortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appList = activityManager.getRunningAppProcesses();
            currentApp = appList.get(0).processName;

        }

        return currentApp;
    }*/
}
