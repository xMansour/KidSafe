package com.mansourappdevelopment.androidapp.kidsafe.broadcasts;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;

public class AdminReceiver extends DeviceAdminReceiver {
	private boolean deleted = false;
	
	@Override
	public void onEnabled(Context context, Intent intent) {
		Toast.makeText(context, context.getString(R.string.device_admin_enabled), Toast.LENGTH_SHORT).show();
		deleted = false;
		writeToDB();
	}
	
	@Override
	public void onDisabled(Context context, Intent intent) {
		Toast.makeText(context, context.getString(R.string.device_admin_disabled), Toast.LENGTH_SHORT).show();
		deleted = true;
		writeToDB();
		
	}
	
	private void writeToDB() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		FirebaseUser user = auth.getCurrentUser();
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference databaseReference = firebaseDatabase.getReference("users");
		Query query = databaseReference.child("childs").orderByChild("email").equalTo(user.getEmail());
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
					nodeShot.getRef().child("appDeleted").setValue(deleted);
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		});
	}
	
	
}
