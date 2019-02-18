package com.mansourappdevelopment.androidapp.kidsafe.services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
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
import com.mansourappdevelopment.androidapp.kidsafe.activities.MainActivity;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import java.util.ArrayList;
import java.util.Date;
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


    private void updateAppStats(final ArrayList<App> apps) {
        /*for (App app : apps) {
            Log.i(TAG, "onDataChange: app name: " + app.getAppName() + ", blocked: " + app.isBlocked() + "\n");
        }*/
        Log.i(TAG, "updateAppStats: executed");
        Toast.makeText(this, "Updated the app list", Toast.LENGTH_SHORT).show();
        //TODO:: block the apps which have a blocked attribute = true

        //Toast.makeText(this, "Current App: " + getForegroundApp(), Toast.LENGTH_SHORT).show();

        new Thread((new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted())
                    //Log.i(TAG, "run: thread started");
                    try {
                        //Log.i(TAG, "run: thread interrupted");
                        Thread.sleep(5000);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //Log.i(TAG, "run: fired");
                                String foregroundAppPackageName = getTopAppPackageName();
                                for (App app : apps) {
                                    //if (getTopAppPackageName().equals(app.getPackageName())) {
                                    if (foregroundAppPackageName.equals(app.getPackageName())) {
                                        Log.i(TAG, "run: " + app.getPackageName() + " is running");
                                        if (app.isBlocked()) {
                                            Log.i(TAG, "run: alert dialog shown");
                                            //showBlockedAlertDialog();     //TODO:: fix the error here
                                            Toast.makeText(UpdateAppStatsForegroundService.this, "This app \"" + app.getAppName() + "\" is blocked", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                        });
                    } catch (
                            InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        })).

                start();

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

    public String getTopAppPackageName() {
        String appPackageName = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                appPackageName = getLollipopForegroundAppPackageName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appPackageName;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private String getLollipopForegroundAppPackageName() {
        //Log.i(TAG, "getLollipopForegroundAppPackageName: executed");
        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);
            long milliSecs = 60 * 1000;
            Date date = new Date();
            List<UsageStats> foregroundApps = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());
            if (foregroundApps.size() == 0) {
                Log.i(TAG, "getLollipopForegroundAppPackageName: queryUsageSize: empty");
            }


            long recentTime = 0;
            String recentPkg = "";
            for (UsageStats stats : foregroundApps) {
                /*if (i == 0 && !"com.mansourappdevelopment.androidapp.kidsafe".equals(stats.getPackageName())) {
                    Log.i(TAG, "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                }*/
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }

            }

            //Log.i(TAG, "getLollipopForegroundAppPackageName: appPackageName: " + recentPkg);
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";
    }


    private void showBlockedAlertDialog() {
        AlertDialog blockedAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Blocked")
                .setMessage("This app is blocked by your parents")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        blockedAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        blockedAlertDialog.show();
    }


}
