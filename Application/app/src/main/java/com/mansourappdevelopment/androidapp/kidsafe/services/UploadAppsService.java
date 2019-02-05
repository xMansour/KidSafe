package com.mansourappdevelopment.androidapp.kidsafe.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity.CHILD_EMAIL;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UploadAppsService extends JobService {
    public static final String TAG = "TAG";
    private boolean jobCancelled;
    private ArrayList<App> appsList;
    private List<ApplicationInfo> applicationInfoList;
    private PackageManager packageManager;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private boolean blocked = false;
    private String email;


    @Override
    public boolean onStartJob(JobParameters params) {

        email = params.getExtras().getString(CHILD_EMAIL);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        uploadApps(params);
        //to keep device awake
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        //true for rescheduling
        return true;
    }

    private void uploadApps(JobParameters params) {
        if (jobCancelled)
            return;


        new Thread(new Runnable() {
            @Override
            public void run() {
                //upload apps to the database
                prepareData();
                writeDataToDB();
            }
        }).start();

        //should be true for rescheduling if it failed
        jobFinished(params, false);
    }

    private void prepareData() {
        appsList = new ArrayList<>();
        getInstalledApplication();
        for (ApplicationInfo applicationInfo : applicationInfoList) {
            if (applicationInfo.packageName != null) {
                getAppState((String) applicationInfo.loadLabel(packageManager));
                Log.i(TAG, "prepareData: executed");
                appsList.add(new App((String) applicationInfo.loadLabel(packageManager), applicationInfo.loadIcon(packageManager), blocked));
            }
        }
    }

    private List<ApplicationInfo> getInstalledApplication() {
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
        return applicationInfoList;
    }

    private void writeDataToDB() {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //update the child's database and add the appsList
                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                String key = nodeShot.getKey();
                //appList contains drawables, that's why it can't be added to the database.
                //for now i will upload the names only
                //TODO:: upload app icons
                databaseReference.child("childs").child(key).child("apps").setValue(appsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void updateAppState() {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo("child@child.com");
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
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(email);
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

}
