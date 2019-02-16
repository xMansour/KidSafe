package com.mansourappdevelopment.androidapp.kidsafe.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity.CHILD_EMAIL;
import static com.mansourappdevelopment.androidapp.kidsafe.utils.NotificationChannelCreator.CHANNEL_ID;

public class UpdateAppStatsForegroundService extends Service {
    public static final int NOTIFICATION_ID = 27;
    public static final String TAG = "UpdateAppStatsService";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<App> apps;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String childEmail = intent.getStringExtra(CHILD_EMAIL);
        //String notificationContent = "Monitoring device";

        auth = FirebaseAuth.getInstance();      //Now you get the user email even after reboot
        user = auth.getCurrentUser();
        String childEmail = user.getEmail();

        Intent notificationIntent = new Intent(this, ChildSignedInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setContentTitle(notificationContent)
                .setSmallIcon(R.drawable.ic_location)   //TODO:: add the app icon
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        getApps(childEmail);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void updateAppStats(String childEmail) {

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

                    apps = new ArrayList<>();
                    DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                    User child = nodeShot.getValue(User.class);
                    apps = child.getApps();

                    Log.i(TAG, "onDataChange: child name: "+child.getName());

                    for (App app : apps) {
                        Log.i(TAG, "onDataChange: app name: " + app.getAppName() + ", blocked: " + app.isBlocked() + "\n");
                    }

                    updateAppStats(childEmail);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
