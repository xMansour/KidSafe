package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.activities.LoginActivity;
import com.mansourappdevelopment.androidapp.kidsafe.models.User;
import com.mansourappdevelopment.androidapp.kidsafe.services.GeoFencingForegroundService;
import com.mansourappdevelopment.androidapp.kidsafe.services.MainForegroundService;

public class AccountUtils {
	private static final String TAG = "AccountUtilsTAG";
	
	public static void changePassword(final Context context, String newPassword) {
		FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				Toast.makeText(context, R.string.password_updated, Toast.LENGTH_SHORT).show();
				context.startActivity(new Intent(context, LoginActivity.class));
				closeServices(context);
				
			}
		});
	}
	
	private static void closeServices(Context context) {
		try {
			context.stopService(new Intent(context, MainForegroundService.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			context.stopService(new Intent(context, GeoFencingForegroundService.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void logout(Context context) {
		FirebaseAuth.getInstance().signOut();
		context.startActivity(new Intent(context, LoginActivity.class));
		
		closeServices(context);
	}
	
	public static void deleteAccount(Context context, String password) {
		final String providerId = FirebaseAuth.getInstance().getCurrentUser().getProviderId();
		final String providerId2 = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getProviderId();
		Log.i(TAG, "deleteAccount: providerId: " + providerId);
		if (providerId.equals("google.com")) deleteAccountData(providerId, null, context);
		else deleteAccountData(providerId, password, context);
		closeServices(context);
		
	}
	
	private static void deleteAccountData(final String providerId, final String password, final Context context) {
		final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
		final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		
		Query parentQuery = databaseReference.child("parents").orderByChild("email").equalTo(user.getEmail());
		parentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					DataSnapshot parentNodeShot = dataSnapshot.getChildren().iterator().next();
					User parent = parentNodeShot.getValue(User.class);
					String imgUrl = parent.getProfileImage();
					removeImage(imgUrl, providerId, password, context);
					databaseReference.child("parents").child(user.getUid()).removeValue();
				} else {
					Query childQuery = databaseReference.child("childs").orderByChild("email").equalTo(user.getEmail());
					childQuery.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							if (dataSnapshot.exists()) {
								DataSnapshot childNodeShot = dataSnapshot.getChildren().iterator().next();
								User child = childNodeShot.getValue(User.class);
								String imgUrl = child.getProfileImage();
								removeImage(imgUrl, providerId, password, context);
								databaseReference.child("childs").child(user.getUid()).removeValue();
								
							}
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
		
		
	}
	
	private static void removeImage(String imgUrl, final String providerId, final String password, final Context context) {
		if (imgUrl.contains("https://firebasestorage.googleapis.com")) {
			StorageReference profileImageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
			profileImageStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
				@Override
				public void onSuccess(Void aVoid) {
					deleteUser(providerId, password, context);
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
				}
			});
		} else {
			deleteUser(providerId, password, context);
		}
	}
	
	private static void deleteUser(String providerId, final String password, final Context context) {
		final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		AuthCredential credential;
		if (providerId.equals("google.com"))
			credential = GoogleAuthProvider.getCredential(user.getEmail(), null);
		
		else credential = EmailAuthProvider.getCredential(user.getEmail(), password);
		
		user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						Toast.makeText(context, R.string.account_deleted, Toast.LENGTH_SHORT).show();
						context.startActivity(new Intent(context, LoginActivity.class));
					}
				});
			}
		});
	}
}
