package com.mansourappdevelopment.androidapp.kidsafe.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UploadAppsService extends JobService {
    public static final String TAG = "UploadAppsService";
    private boolean jobCancelled;


    private ArrayList<App> apps;            //read from the database
    private List<ApplicationInfo> applicationInfoList;
    private PackageManager packageManager;


    private DatabaseReference databaseReference;
    private String childEmail;


    @Override
    public boolean onStartJob(JobParameters params) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        childEmail = user.getEmail();

        //childEmail = params.getExtras().getString(CHILD_EMAIL);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        uploadApps(params);
        //to keep device awake
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        //jobCancelled = true;
        //true for rescheduling
        return true;
    }

    private void uploadApps(JobParameters params) {
        /*if (jobCancelled)
            return;
            */

        new Thread(new Runnable() {
            @Override
            public void run() {
                //upload apps to the database
                getRecentAppStats();
            }
        }).start();

        //should be true for rescheduling if it failed
        jobFinished(params, false);
    }

    private void prepareData(ArrayList<App> apps) {

        ArrayList<App> appsList = new ArrayList<>();
        getInstalledApplication();

        for (ApplicationInfo applicationInfo : applicationInfoList) {
            if (applicationInfo.packageName != null) {
                if (apps.isEmpty()) {       //online appList will be empty for the first time, used for initialization
                    Log.i(TAG, "prepareData: online appsList empty");
                    appsList.add(new App((String) applicationInfo.loadLabel(packageManager), (String) applicationInfo.packageName, applicationInfo.loadIcon(packageManager), false));

                } else {

                    for (App app : apps) {
                        if (app.getAppName().equals((String) applicationInfo.loadLabel(packageManager))) {
                            appsList.add(new App((String) applicationInfo.loadLabel(packageManager), (String) applicationInfo.packageName, applicationInfo.loadIcon(packageManager), app.isBlocked()));
                            Log.i(TAG, "prepareData: if executed");
                        }
                    }

                    //TODO:: add new apps which aren't listed in that list
                    /*for (App app : appsList) {
                        if (!apps.contains(app)) {
                            //appsList.add(new App((String) applicationInfo.loadLabel(packageManager), (String) applicationInfo.packageName, applicationInfo.loadIcon(packageManager), false));
                            Log.i(TAG, "prepareData: new app added: " + (String) applicationInfo.loadLabel(packageManager));
                        }
                    }*/
                }
            }
        }

        writeDataToDB(appsList);
    }

    private void getInstalledApplication() {
        packageManager = getPackageManager();
        applicationInfoList = packageManager.getInstalledApplications(0);
        Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(packageManager));
        Iterator<ApplicationInfo> iterator = applicationInfoList.iterator();
        while (iterator.hasNext()) {
            ApplicationInfo applicationInfo = iterator.next();
            if (applicationInfo.packageName.contains("com.google") || applicationInfo.packageName.matches("com.android.chrome"))
                continue;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                iterator.remove();
            }
        }
    }

    private void getRecentAppStats() {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    apps = new ArrayList<>();
                    DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                    User child = nodeShot.getValue(User.class);
                    apps = child.getApps();

                    prepareData(apps);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void writeDataToDB(ArrayList<App> appsList) {
        final ArrayList<App> simpleAppInfo = new ArrayList<>();
        for (App app : appsList) {
            simpleAppInfo.add(new App(app.getAppName(), app.getPackageName(), app.isBlocked()));
        }

        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "onDataChange: executed");
                    //update the child's database and add the appsList
                    DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeShot.getKey();
                    //appList contains drawables, that's why it can't be added to the database.
                    //for now i will upload the names only
                    //TODO:: upload app icons
                    databaseReference.child("childs").child(key).child("apps").setValue(simpleAppInfo);
                    //databaseReference.child("childs").child(key).child("apps").removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


/*
    private void updateAppState() {
        Query query = databaseReference.child("childs").orderByChild("childEmail").equalTo("child@child.com");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                final String key = nodeShot.getKey();
                //TODO:: upload app icons
                Query query = databaseReference.child("childs").child(key).child("apps").orderByChild("appName").equalTo("a Paper");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //TODO:: move this to the parent activity
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            HashMap<String, Object> update = new HashMap<>();
                            update.put("blocked", "true");
                            databaseReference.child("childs").child(key).child("apps").child(snapshot.getKey()).updateChildren(update);
                            GenericTypeIndicator<List<App>> indicator = new GenericTypeIndicator<List<App>>() {
                            };
                            List<App> app = dataSnapshot.getValue(indicator);
                            Log.i(TAG, "onDataChange: app is found");
                            Log.i(TAG, "onDataChange: " + dataSnapshot.getValue());
                            Log.i(TAG, "onDataChange: " + dataSnapshot.toString());
                            Log.i(TAG, "onDataChange: " + String.valueOf(app.get(0).isBlocked()));
                            Log.i(TAG, "onDataChange: " + String.valueOf(app.get(0).getAppName()));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAppState(String email, String appName, boolean state) {
        //TODO:: this method should update the app state, blocked or not
    }


    //TODO:: this method is asynchronous, needs a fix
    private boolean getAppState(final String appName) {
        Log.i(TAG, "getAppState: " + appName);
        Query query = databaseReference.child("childs").orderByChild("childEmail").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                final String key = nodeShot.getKey();
                Query query = databaseReference.child("childs").child(key).child("apps").orderByChild("appName").equalTo(appName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            GenericTypeIndicator<List<App>> indicator = new GenericTypeIndicator<List<App>>() {
                            };
                            List<App> apps = dataSnapshot.getValue(indicator);
                            blocked = apps.get(0).isBlocked();
                            Log.i(TAG, "onDataChange: app is found");
                            Log.i(TAG, "onDataChange: " + String.valueOf(apps.get(0).getAppName()));
                            Log.i(TAG, "onDataChange: " + String.valueOf(apps.get(0).isBlocked()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return blocked;
    }
*/

}
