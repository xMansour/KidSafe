package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.AppAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnAppClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.models.App;
import com.mansourappdevelopment.androidapp.kidsafe.models.ScreenLock;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_EMAIL_EXTRA;

public class AppsFragment extends Fragment implements OnAppClickListener {
    public static final String TAG = "AppsFragmentTAG";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<App> apps;
    private AppAdapter appAdapter;
    private RecyclerView recyclerViewApps;
    private Context context;
    private String childEmail;
    private String appName;
    private String packageName;
    private Bundle bundle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");


        recyclerViewApps = (RecyclerView) view.findViewById(R.id.recyclerViewApps);
        recyclerViewApps.setHasFixedSize(true);
        recyclerViewApps.setLayoutManager(new LinearLayoutManager(getContext()));

        getData();
        initializeAdapter(this);

    }

    public void getData() {
        bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            apps = bundle.getParcelableArrayList(ParentSignedInActivity.APPS_EXTRA);
            childEmail = bundle.getString(CHILD_EMAIL_EXTRA);

            for (App app : apps) {
                Log.i(TAG, "onItemClick: appName: " + app.getAppName() + " " + "packageName" + app.getPackageName());

            }
        }
    }

    public void initializeAdapter(OnAppClickListener onAppClickListener) {
        appAdapter = new AppAdapter(context, apps);
        appAdapter.setOnAppClickListener(onAppClickListener);
        recyclerViewApps.setAdapter(appAdapter);
    }

    @Override
    public void onItemClick(final String packageName, String appName, boolean blocked) {
        if (blocked) {
            Toast.makeText(context, appName + " blocked", Toast.LENGTH_SHORT).show();
            updateAppState(packageName, blocked);

        } else {
            Toast.makeText(context, appName + " enabled", Toast.LENGTH_SHORT).show();
            updateAppState(packageName, blocked);

        }
        /*this.appName = appName;
        this.packageName = packageName;

        if (blocked) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.CHILD_APP_NAME_EXTRA, appName);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            AppLockDialogFragment appLockFragment = new AppLockDialogFragment();
            appLockFragment.setCancelable(false);//TODO:: add this to all the other dialog fragments
            appLockFragment.setArguments(bundle);
            appLockFragment.setTargetFragment(AppsFragment.this, Constant.APP_LOCK_FRAGMENT_REQUEST_CODE);
            appLockFragment.show(fragmentManager, "AppLockDialogFragment");
        } else {
            Toast.makeText(context, appName + " " + getString(R.string.enabled), Toast.LENGTH_SHORT).show();
            final ScreenLock screenLock = new ScreenLock(0, 0, false);
            updateAppLockState(screenLock);
        }*/
    }


/*    @Override
    public void onLockAppSet(int hours, int minutes) {
        if (hours == 0 && minutes == 0) {
            Toast.makeText(context, appName + " " + getString(R.string.blocked), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, appName + " " + getString(R.string.will_be_blocked_after) + " " + hours + " " + getString(R.string.hours) + " "
                    + getString(R.string.and) + " " + minutes + " " + getString(R.string.minutes), Toast.LENGTH_SHORT).show();

        }

        ScreenLock screenLock = new ScreenLock(hours, minutes, true);
        updateAppLockState(screenLock);


    }

    @Override
    public void onLockCanceled() {
        Toast.makeText(context, getString(R.string.canceled), Toast.LENGTH_SHORT).show();
        initializeAdapter(this);//TODO:: don't know if it is the best solution

    }


    private void updateAppLockState(final ScreenLock screenLock) {
        Query childQuery = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        childQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: DONE");
                if (dataSnapshot.exists()) {
                    DataSnapshot childNodeShot = dataSnapshot.getChildren().iterator().next();
                    final String childKey = childNodeShot.getKey();
                    Log.i(TAG, "onDataChange: childKey: " + childKey);
                    Log.i(TAG, "onDataChange: packageName: " + packageName);
                    Query appQuery = databaseReference.child("childs").child(childKey).child("apps").orderByChild("packageName").equalTo(packageName);  //changed from appName
                    appQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i(TAG, "onDataChange: DONE 2");
                            Log.i(TAG, "onDataChange: dataSnapShotValue: " + dataSnapshot.getValue());
                            Log.i(TAG, "onDataChange: dataSnapShotChildren: " + dataSnapshot.getChildren());
                            if (dataSnapshot.exists()) {
                                DataSnapshot appNodeShot = dataSnapshot.getChildren().iterator().next();
                                String appKey = appNodeShot.getKey();
                                Log.i(TAG, "onDataChange: appKey: " + appKey);
                                Log.i(TAG, "onDataChange: appNodeShotValue: " + appNodeShot.getValue());
                                Log.i(TAG, "onDataChange: appNodeShotChildren: " + appNodeShot.getChildren());

                                databaseReference.child("childs").child(childKey).child("apps").child(appKey).child("screenLock").setValue(screenLock);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/


    private void updateAppState(final String packageName, final boolean blocked) {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                final String key = nodeShot.getKey();
                Log.i(TAG, "onDataChange: key: " + key);
                Query query = databaseReference.child("childs").child(key).child("apps").orderByChild("packageName").equalTo(packageName);  //changed from appName
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            HashMap<String, Object> update = new HashMap<>();
                            update.put("blocked", blocked);
                            databaseReference.child("childs").child(key).child("apps").child(snapshot.getKey()).updateChildren(update);

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

}
