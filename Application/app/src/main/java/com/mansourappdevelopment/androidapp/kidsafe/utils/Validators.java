package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class Validators {
	
	public static boolean isValidEmail(String email) {
		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
		
		if (email.equals("")) {
			return false;
		}
		if (!email.trim().matches(emailPattern)) {
			return false;
		}
		return true;
	}
	
	public static boolean isValidPassword(String password) {
		if (password.equals("")) {
			return false;
		}
		
		if (password.length() < 6) {
			return false;
		}
		
		return true;
		
	}
	
	public static boolean isValidHours(String hours) {
		if (hours.equals("")) {
			return false;
		}
		
		if (hours.length() > 23) {
			return false;
		}
		
		return true;
		
	}
	
	public static boolean isValidMinutes(String minutes) {
		if (minutes.equals("")) {
			return false;
		}
		
		if (minutes.length() > 59) {
			return false;
		}
		
		return true;
		
	}
	
	public static boolean isValidGeoFenceDiameter(String diameter) {
		if (diameter.equals("")) {
			return false;
		}
		return true;
		
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
}
