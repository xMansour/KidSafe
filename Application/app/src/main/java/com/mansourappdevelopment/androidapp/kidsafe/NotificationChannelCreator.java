package com.mansourappdevelopment.androidapp.kidsafe;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannelCreator extends Application {
	public static final String CHANNEL_ID = "com.mansourappdevelopment.androidapp.kidsafe.utils.CHANNEL_ID";
	
	@Override
	public void onCreate() {
		super.onCreate();
		createNotificationChannel();
	}
	
	
	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Service Channel", NotificationManager.IMPORTANCE_LOW);
			
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(serviceChannel);
		}
	}
}
