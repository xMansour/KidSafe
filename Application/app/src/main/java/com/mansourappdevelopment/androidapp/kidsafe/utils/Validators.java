package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseUser;

public class Validators {
	
	public static boolean isValidEmail(String email) {
		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
		
		if (email.equals("")) {
			return false;
		}
		return email.trim().matches(emailPattern);
	}
	
	public static boolean isValidPassword(String password) {
		if (password.equals("")) {
			return false;
		}
		
		return password.length() >= 6;
		
	}
	
	public static boolean isValidHours(String hours) {
		if (hours.equals("")) {
			return false;
		}
		
		return hours.length() <= 23;
		
	}
	
	public static boolean isValidMinutes(String minutes) {
		if (minutes.equals("")) {
			return false;
		}
		
		return minutes.length() <= 59;
		
	}
	
	public static boolean isValidGeoFenceDiameter(String diameter) {
		return !diameter.equals("");
		
	}
	
	
	public static boolean isValidName(String name) {
		return name.length() <= 15 && !name.equals("");
	}
	
	public static boolean isValidImageURI(Uri imageUri) {
		return imageUri != null;
		
	}
	
	public static boolean isVerified(FirebaseUser user) {
		return user.isEmailVerified();
	}
	
	public static boolean isInternetAvailable(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected()) haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected()) haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
		
	}
	
	public static boolean isGooglePlayServicesAvailable(Activity activity) {
		GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
		int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
		if (status != ConnectionResult.SUCCESS) {
			if (googleApiAvailability.isUserResolvableError(status))
				googleApiAvailability.getErrorDialog(activity, status, 2404).show();
			return false;
		}
		
		return true;
	}
	
	public static boolean isLocationOn(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
}
