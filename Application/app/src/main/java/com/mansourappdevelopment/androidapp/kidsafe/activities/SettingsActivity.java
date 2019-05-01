package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mansourappdevelopment.androidapp.kidsafe.AppCompatPreferenceActivity;
import com.mansourappdevelopment.androidapp.kidsafe.utils.LocaleUtils;
import com.mansourappdevelopment.androidapp.kidsafe.R;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = "SettingsActivityTAG";
    private static Context context;
    private static FirebaseAuth auth;
    private static FirebaseUser user;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    private static FirebaseStorage firebaseStorage;
    private static StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setTitle(R.string.settings);
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            context = getActivity();
            auth = FirebaseAuth.getInstance();


            bindPreferenceSummaryToValue(findPreference(getString(R.string.language_shared_prefs)));

            Preference logoutPref = findPreference(getString(R.string.logout_shared_pref));
            logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    logout();
                    return true;
                }
            });

            Preference changePasswordPref = findPreference(getString(R.string.change_password_shared_prefs));
            changePasswordPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    changePassword();
                    return true;
                }
            });

            Preference deleteAccountPref = findPreference(getString(R.string.delete_account_shared_prefs));
            deleteAccountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    deleteAccount();
                    return true;
                }
            });

            Preference sendFeedbackPref = findPreference(getString(R.string.send_feedback_shared_prefs));
            sendFeedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                if (index == 0) {
                    LocaleUtils.setLocale(context, "en");
                    restartApp();   //TODO:: best solution ?

                } else if (index == 1) {
                    LocaleUtils.setLocale(context, "ar");
                    restartApp();

                }

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                preference.setSummary(stringValue);
            }

            return true;
        }
    };

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"MansourAppDevelopment@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "KidSafe Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    private static void restartApp() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private static void changePassword() {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.change_password))
                .setMessage(context.getString(R.string.do_you_want_to_change_password))
                //.setIcon(R.drawable.ic_home_)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "OK clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }

    private static void logout() {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.logout))
                .setMessage(context.getString(R.string.do_you_want_to_logout))
                //.setIcon(R.drawable.ic_home_)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }

    private static void deleteAccount() {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete_account))
                .setMessage(context.getString(R.string.do_you_want_to_delete_account))
                //.setIcon(R.drawable.ic_home_)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "OK clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
    }

}
