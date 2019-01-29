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
import com.mansourappdevelopment.androidapp.kidsafe.utils.AppTestClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ChildSignedInActivity.CHILD_EMAIL;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UploadAppsService extends JobService {
    public static final String TAG = "appFound";
    private boolean jobCancelled;
    private ArrayList<App> appsList;
    private List<ApplicationInfo> applicationInfoList;
    private PackageManager packageManager;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private boolean blocked;


    @Override
    public boolean onStartJob(JobParameters params) {
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

        final String email = params.getExtras().getString(CHILD_EMAIL);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //upload apps to the database
                prepareData();
                writeDataToDB(email);
                updateAppState();
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
                appsList.add(new App((String) applicationInfo.loadLabel(packageManager), applicationInfo.loadIcon(packageManager), false));
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

    private void writeDataToDB(String email) {
        final ArrayList<AppTestClass> appNames = new ArrayList<>();
        for (App app : appsList) {
            //appNames.add(app.getAppName());
            appNames.add(new AppTestClass(app.getAppName(), app.isBlocked()));
        }
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
                databaseReference.child("childs").child(key).child("apps").setValue(appNames);
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
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            HashMap<String, Object> update = new HashMap<>();
                            update.put("blocked", "true");
                            databaseReference.child("childs").child(key).child("apps").child(snapshot.getKey()).updateChildren(update);
                            GenericTypeIndicator<List<AppTestClass>> indicator = new GenericTypeIndicator<List<AppTestClass>>() {
                            };
                            List<AppTestClass> app = dataSnapshot.getValue(indicator);
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

    private boolean getAppState(String email, String appName) {
        //TODO:: this method should get the app state
        return blocked;
    }

}
