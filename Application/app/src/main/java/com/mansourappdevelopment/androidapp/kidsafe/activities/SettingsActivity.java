package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mansourappdevelopment.androidapp.kidsafe.AppCompatPreferenceActivity;
import com.mansourappdevelopment.androidapp.kidsafe.models.User;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.LocaleUtils;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.utils.SharedPrefsUtils;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = "SettingsActivityTAG";
    private static Context context;
    private static FirebaseAuth auth;
    private static FirebaseUser user;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    private static FirebaseStorage firebaseStorage;
    private static StorageReference storageReference;
    private static String password;

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
            user = auth.getCurrentUser();


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
                .setTitle(R.string.change_password)
                .setMessage(R.string.do_you_want_to_change_password)
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
                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }

    private static void logout() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.logout)
                .setMessage(R.string.do_you_want_to_logout)
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
                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }

    private static void deleteAccount() {
        final String providerId = user.getProviders().get(0);
        new AlertDialog.Builder(context)
                .setTitle(R.string.delete_account)
                .setMessage(R.string.do_you_want_to_delete_account)
                //.setIcon(R.drawable.ic_home_)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (providerId.equals("google.com")) {
                            deleteAccountData(providerId);
                        } else {
                            LinearLayout parentLayout = new LinearLayout(context);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            layoutParams.setMargins(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                            final EditText txtPassword = new EditText(context);
                            txtPassword.setHint(R.string.enter_your_password);
                            txtPassword.setLayoutParams(layoutParams);
                            txtPassword.setSingleLine();
                            parentLayout.addView(txtPassword);
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.delete_account)
                                    //.setMessage(R.string.enter_your_password)
                                    .setView(parentLayout)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            password = txtPassword.getText().toString();
                                            if (isValid(password)) {
                                                deleteAccountData(providerId);

                                            } else {
                                                Toast.makeText(context, R.string.wrong_password, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                                        }
                                    }).create().show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
    }

    private static boolean isValid(String password) {
        String savedPassword = SharedPrefsUtils.getStringPreference(context, Constant.PASSWORD);
        Log.i(TAG, "isValid: savedPassword: " + savedPassword);
        Log.i(TAG, "isValid: password: " + password);
        return password.equals(savedPassword);

    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private static void deleteAccountData(final String providerId) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        Query query = databaseReference.child("parents").orderByChild("email").equalTo(user.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    databaseReference.child("parents").child(user.getUid()).removeValue();
                } else {
                    databaseReference.child("childs").child(user.getUid()).removeValue();
                }

                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                User dbUser = nodeShot.getValue(User.class);
                String imgUrl = dbUser.getProfileImage();
                if (imgUrl.contains("https://firebasestorage.googleapis.com")) {
                    Log.i(TAG, "onDataChange: imgUrl: " + imgUrl);
                    firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference profileImageStorageReference = firebaseStorage.getReferenceFromUrl(imgUrl);
                    profileImageStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deleteUser(providerId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                } else {
                    deleteUser(providerId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private static void deleteUser(String providerId) {
        AuthCredential credential;
        if (providerId.equals("google.com"))
            credential = GoogleAuthProvider.getCredential(user.getEmail(), null);

        else
            credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
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
