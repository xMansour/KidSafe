package com.mansourappdevelopment.androidapp.kidsafe.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.models.App;
import com.mansourappdevelopment.androidapp.kidsafe.models.Child;

import java.util.ArrayList;


public class AppInstalledReceiver extends BroadcastReceiver {
	public static final String TAG = "AppInstalledReceiver";
	private FirebaseUser user;
	private FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReference;
	private String uid;
	private String childEmail;
	
	public AppInstalledReceiver(FirebaseUser user) {
		this.user = user;
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("users");
		uid = user.getUid();
		childEmail = user.getEmail();
		
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
		
		App newApp = new App(appName, packageName, false);
		getApps(newApp);
		
	}
	
	public void getApps(final App newApp) {
		Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					ArrayList<App> apps;
					DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
					Child child = nodeShot.getValue(Child.class);
					apps = child.getApps();
					addNewApp(apps, newApp);
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
	}
	
	private void addNewApp(ArrayList<App> apps, App newApp) {
		apps.add(newApp);
		databaseReference.child("childs").child(uid).child("apps").setValue(apps);
	}
}
