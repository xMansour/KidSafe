package com.mansourappdevelopment.androidapp.kidsafe.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity.CHILD_EMAIL;
import static com.mansourappdevelopment.androidapp.kidsafe.utils.NotificationChannelCreator.CHANNEL_ID;

public class UpdateAppStatsForegroundService extends Service {
    public static final int NOTIFICATION_ID = 27;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String childEmail = intent.getStringExtra(CHILD_EMAIL);
        //String notificationContent = "Monitoring device";



        Intent notificationIntent = new Intent(this, ChildSignedInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setContentTitle(notificationContent)
                .setSmallIcon(R.drawable.ic_location)   //TODO:: add the app icon
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        updateAppStats(childEmail);
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


    private void updateAppStats(String childEmail){

    }
}
