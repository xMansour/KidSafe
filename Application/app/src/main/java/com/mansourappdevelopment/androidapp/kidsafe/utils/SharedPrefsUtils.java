package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SharedPrefsUtils {
	
	/**
	 * Helper method to retrieve an float value from {@link SharedPreferences}.
	 *
	 * @param context a {@link Context} object.
	 * @param key
	 * @return The value from shared preferences, or the provided default.
	 */
	public static String getStringPreference(Context context, String key, String defValue) {
		String value = null;
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			value = preferences.getString(key, defValue);
		}
		return value;
	}
	
	/**
	 * Helper method to write a String value to {@link SharedPreferences}.
	 *
	 * @param context a {@link Context} object.
	 * @param key
	 * @param value
	 * @return true if the new value was successfully written to persistent storage.
	 */
	public static boolean setStringPreference(Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null && !TextUtils.isEmpty(key)) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.apply();
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to retrieve an float value from {@link SharedPreferences}.
	 *
	 * @param context      a {@link Context} object.
	 * @param key
	 * @param defaultValue A default to return if the value could not be read.
	 * @return The value from shared preferences, or the provided default.
	 */
	public static float getFloatPreference(Context context, String key, float defaultValue) {
		float value = defaultValue;
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			value = preferences.getFloat(key, defaultValue);
		}
		return value;
	}
	
	/**
	 * Helper method to write a float value to {@link SharedPreferences}.
	 *
	 * @param context a {@link Context} object.
	 * @param key
	 * @param value
	 * @return true if the new value was successfully written to persistent storage.
	 */
	public static boolean setFloatPreference(Context context, String key, float value) {
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putFloat(key, value);
			editor.apply();
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to retrieve an long value from {@link SharedPreferences}.
	 *
	 * @param context      a {@link Context} object.
	 * @param key
	 * @param defaultValue A default to return if the value could not be read.
	 * @return The value from shared preferences, or the provided default.
	 */
	public static long getLongPreference(Context context, String key, long defaultValue) {
		long value = defaultValue;
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			value = preferences.getLong(key, defaultValue);
		}
		return value;
	}
	
	/**
	 * Helper method to write a long value to {@link SharedPreferences}.
	 *
	 * @param context a {@link Context} object.
	 * @param key
	 * @param value
	 * @return true if the new value was successfully written to persistent storage.
	 */
	public static boolean setLongPreference(Context context, String key, long value) {
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putLong(key, value);
			editor.apply();
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to retrieve an integer value from {@link SharedPreferences}.
	 *
	 * @param context      a {@link Context} object.
	 * @param key
	 * @param defaultValue A default to return if the value could not be read.
	 * @return The value from shared preferences, or the provided default.
	 */
	public static int getIntegerPreference(Context context, String key, int defaultValue) {
		int value = defaultValue;
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			value = preferences.getInt(key, defaultValue);
		}
		return value;
	}
	
	/**
	 * Helper method to write an integer value to {@link SharedPreferences}.
	 *
	 * @param context a {@link Context} object.
	 * @param key
	 * @param value
	 * @return true if the new value was successfully written to persistent storage.
	 */
	public static boolean setIntegerPreference(Context context, String key, int value) {
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt(key, value);
			editor.apply();
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to retrieve a boolean value from {@link SharedPreferences}.
	 *
	 * @param context      a {@link Context} object.
	 * @param key
	 * @param defaultValue A default to return if the value could not be read.
	 * @return The value from shared preferences, or the provided default.
	 */
	public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
		boolean value = defaultValue;
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			value = preferences.getBoolean(key, defaultValue);
		}
		return value;
	}
	
	/**
	 * Helper method to write a boolean value to {@link SharedPreferences}.
	 *
	 * @param context a {@link Context} object.
	 * @param key
	 * @param value
	 * @return true if the new value was successfully written to persistent storage.
	 */
	public static boolean setBooleanPreference(Context context, String key, boolean value) {
		SharedPreferences preferences = context.getSharedPreferences(Constant.KID_SAFE_PREFS, Context.MODE_PRIVATE);
		if (preferences != null) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(key, value);
			editor.apply();
			return true;
		}
		return false;
	}
}