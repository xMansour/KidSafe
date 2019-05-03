package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

public class LocaleUtils {
    private static final String TAG = "LocaleUtilsTAG";

    public static void setLocale(Context context, String language) {
        updateResources(context, language);
        saveSelectedLanguage(context, language);
    }

    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        context.createConfigurationContext(configuration);
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return true;
    }

    private static void saveSelectedLanguage(Context context, String selectedLanguage) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString(Constant.APP_LANGUAGE, selectedLanguage);
        editor.putBoolean(Constant.LANGUAGE_SELECTED, true);
        editor.apply();
    }

    public static void setAppLanguage(Context context) {//TODO:: use shared prefs util
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String appLanguage = sharedPreferences.getString(Constant.APP_LANGUAGE, "en");
        boolean languageSelected = sharedPreferences.getBoolean(Constant.LANGUAGE_SELECTED, false);

        Log.i(TAG, "setAppLanguage: LANGUAGE_SELECTED: " + languageSelected);
        if (languageSelected) {
            editor.clear();
            editor.putString(Constant.APP_LANGUAGE, appLanguage);
            editor.putBoolean(Constant.LANGUAGE_SELECTED, true);
            editor.apply();
            LocaleUtils.setLocale(context, appLanguage);
        } else {
            LocaleUtils.setLocale(context, Locale.getDefault().getLanguage());
            editor.clear();
            editor.putString(Constant.APP_LANGUAGE, Locale.getDefault().getLanguage());
            editor.putBoolean(Constant.LANGUAGE_SELECTED, false);
            editor.apply();

        }

    }

    public static String getAppLanguage() {
        return Locale.getDefault().getLanguage();
    }

}