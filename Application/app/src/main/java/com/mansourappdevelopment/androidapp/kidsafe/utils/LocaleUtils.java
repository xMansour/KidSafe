package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleUtils {
	private static final String TAG = "LocaleUtilsTAG";
	
	public static void setLocale(Context context, String language) {
		updateResources(context, language);
		saveSelectedLanguage(context, language);
	}
	
	private static void updateResources(Context context, String language) {
		Locale locale = new Locale(language);
		Locale.setDefault(locale);
		Resources resources = context.getResources();
		Configuration configuration = resources.getConfiguration();
		context.createConfigurationContext(configuration);
		configuration.locale = locale;
		resources.updateConfiguration(configuration, resources.getDisplayMetrics());
	}
	
	private static void saveSelectedLanguage(Context context, String selectedLanguage) {
		SharedPrefsUtils.setStringPreference(context, Constant.APP_LANGUAGE, selectedLanguage);
		SharedPrefsUtils.setBooleanPreference(context, Constant.LANGUAGE_SELECTED, true);
	}
	
	public static void setAppLanguage(Context context) {
		String appLanguage = SharedPrefsUtils.getStringPreference(context, Constant.APP_LANGUAGE, "en");
		boolean languageSelected = SharedPrefsUtils.getBooleanPreference(context, Constant.LANGUAGE_SELECTED, false);
		if (languageSelected) {
			SharedPrefsUtils.setStringPreference(context, Constant.APP_LANGUAGE, appLanguage);
			SharedPrefsUtils.setBooleanPreference(context, Constant.LANGUAGE_SELECTED, true);
			LocaleUtils.setLocale(context, appLanguage);
		} else {
			LocaleUtils.setLocale(context, getAppLanguage());
			SharedPrefsUtils.setStringPreference(context, Constant.APP_LANGUAGE, appLanguage);
			SharedPrefsUtils.setBooleanPreference(context, Constant.LANGUAGE_SELECTED, false);
			
		}
		
	}
	
	public static String getAppLanguage() {
		return Locale.getDefault().getLanguage();
	}
	
}