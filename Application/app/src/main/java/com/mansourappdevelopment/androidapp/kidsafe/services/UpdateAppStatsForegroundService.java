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
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
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
import com.mansourappdevelopment.androidapp.kidsafe.activities.BlockedAppActivity;
import com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity;
import com.mansourappdevelopment.androidapp.kidsafe.activities.MainActivity;
import com.mansourappdevelopment.androidapp.kidsafe.broadcasts.PhoneStateReceiver;
import com.mansourappdevelopment.androidapp.kidsafe.broadcasts.SmsReceiver;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity.CHILD_EMAIL;
import static com.mansourappdevelopment.androidapp.kidsafe.utils.NotificationChannelCreator.CHANNEL_ID;

public class UpdateAppStatsForegroundService extends Service {
    public static final int NOTIFICATION_ID = 27;
    public static final String TAG = "UpdateAppStatsService";
    public static final String BLOCKED_APP_NAME_EXTRA = "com.mansourappdevelopment.androidapp.kidsafe.services.BLOCKED_APP_NAME_EXTRA";
    public static final int LOCATION_UPDATE_INTERVAL = 5000;    //every 5 seconds
    public static final int LOCATION_UPDATE_DISPLACEMENT = 10;  //every 10 meters
    private DatabaseReference databaseReference;
    private ExecutorService executorService;
    private ArrayList<App> apps;
    private PhoneStateReceiver phoneStateReceiver;
    private SmsReceiver smsReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadExecutor();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        LockerThread thread = new LockerThread();
        executorService.submit(thread);
        Log.i(TAG, "onCreate: executed");
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

        getUserLocation(uid);

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

        phoneStateReceiver = new PhoneStateReceiver(user);
        IntentFilter callIntentFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(phoneStateReceiver, callIntentFilter);

        smsReceiver = new SmsReceiver(user);
        IntentFilter smsIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, smsIntentFilter);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        if (phoneStateReceiver != null) {
            unregisterReceiver(phoneStateReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

                    DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                    User child = nodeShot.getValue(User.class);
                    apps = child.getApps();

                    Log.i(TAG, "onDataChange: child name: " + child.getName());

                    //updateAppStats(apps);

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

    class LockerThread implements Runnable {

        Intent intnet = null;

        public LockerThread() {
            intnet = new Intent(UpdateAppStatsForegroundService.this, BlockedAppActivity.class);
            intnet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        @Override
        public void run() {
            while (true) {
                Log.i(TAG, "run: thread running");

                if (apps != null) {

                    String foregroundAppPackageName = getTopAppPackageName();
                    Log.i(TAG, "run: foreground app: " + foregroundAppPackageName);

                    //TODO:: need to handle com.google.android.gsf &  com.sec.android.provider.badge
                    for (final App app : apps) {
                        //Log.i(TAG, "run: app name: " + app.getAppName() + " blocked: " + app.isBlocked() + "\n");
                        if (foregroundAppPackageName.equals(app.getPackageName()) && app.isBlocked()) {
                            //Log.i(TAG, "run: " + app.getPackageName() + " is running");
                            intnet.putExtra(BLOCKED_APP_NAME_EXTRA, app.getAppName());
                            startActivity(intnet);
                        }

                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void getUserLocation(final String uid) {
        Log.i(TAG, "getUserLocation: executed");
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    Log.i(TAG, "onLocationChanged: latitude: " + location.getLatitude());
                    Log.i(TAG, "onLocationChanged: longitude: " + location.getLongitude());
                    addUserLocationToDatabase(location, uid);
                } else {
                    Log.i(TAG, "onLocationChanged: location is null");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //these two statements will be only executed when the permission is granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISPLACEMENT, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISPLACEMENT, locationListener);
            return;
        }

    }

    private void addUserLocationToDatabase(Location location, String uid) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        HashMap<String, Object> update = new HashMap<>();
        update.put("latitude", latitude);
        update.put("longitude", longitude);
        databaseReference.child("childs").child(uid).child("location").updateChildren(update);
    }
}