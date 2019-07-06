package com.mansourappdevelopment.androidapp.kidsafe.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

import static com.mansourappdevelopment.androidapp.kidsafe.NotificationChannelCreator.CHANNEL_ID;

public class GeoFencingForegroundService extends Service {
	private static final String TAG = "GeoFencingTAG";
	private FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReference;
	private String lastChildEmail = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("users");
		
		final String childEmail = intent.getStringExtra(Constant.CHILD_EMAIL_EXTRA);
		final String childName = intent.getStringExtra(Constant.CHILD_NAME_EXTRA);
		String notificationContent = "GeoFencing " + childName;
		if (childEmail != null) lastChildEmail = childEmail;
		
		if (intent.getAction() != null) {
			if (intent.getAction().equals(Constant.ACTION_STOP_GEO_FENCING_SERVICE)) {
				closeFencingService();
				Log.i(TAG, "onStartCommand: lastEmail: " + lastChildEmail);
			}
		}
		
		Intent notificationIntent = new Intent(this, ParentSignedInActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		Intent stopIntent = new Intent(this, GeoFencingForegroundService.class);
		stopIntent.setAction(Constant.ACTION_STOP_GEO_FENCING_SERVICE);
		PendingIntent stopPendingIntent = PendingIntent.getService(this, Constant.GEO_FENCING_SERVICE_REQUEST_CODE, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(notificationContent).setSmallIcon(R.drawable.ic_kidsafe).addAction(R.drawable.ic_cancel, getString(R.string.stop), stopPendingIntent).setContentIntent(pendingIntent).build();
		
		startForeground(Constant.GEO_FENCING_NOTIFICATION_ID, notification);
		
		
		Log.i(TAG, "onStartCommand: service started");
		
		Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					final DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
					String childUID = nodeShot.getKey();
					Query geoFencingQuery = databaseReference.child("childs").child(childUID).child("location").child("outOfFence");
					geoFencingQuery.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							Log.i(TAG, "onDataChange: value: " + dataSnapshot.getValue());
							if (dataSnapshot.exists()) showNotification(dataSnapshot, childName);
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError databaseError) {
						
						}
					});
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
		
		return START_REDELIVER_INTENT;  //so the intent wouldn't be a null
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	private void showNotification(DataSnapshot dataSnapshot, String childName) {
		Log.i(TAG, "showNotification: key: " + dataSnapshot.getKey());
		Log.i(TAG, "showNotification: value: " + dataSnapshot.getValue());
		//Log.i(TAG, "showNotification: children: " + dataSnapshot.getChildren());
		//Log.i(TAG, "showNotification: childrenCount" + dataSnapshot.getChildrenCount());
		
		//TODO:: show a notification instead
		
		if ((boolean) dataSnapshot.getValue()) {
			Toast.makeText(this, childName + getString(R.string.is_out_of_the_fence), Toast.LENGTH_SHORT).show();
			stopSelf();
		}
	}
	
	
	private void closeFencingService() {
		if (lastChildEmail != null) {
			Query query = databaseReference.child("childs").orderByChild("email").equalTo(lastChildEmail);
			query.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					if (dataSnapshot.exists()) {
						final DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
						String childUID = nodeShot.getKey();
						Log.i(TAG, "onDataChange: Done");
						databaseReference.child("childs").child(childUID).child("location").child("outOfFence").setValue(false);
						databaseReference.child("childs").child(childUID).child("location").child("geoFence").setValue(false);
						stopSelf();
					}
				}
				
				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
				
				}
			});
			
		} else {
			stopSelf();
		}
	}
	
}
