package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.HashMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_EMAIL_EXTRA;

public class AppsFragment extends Fragment implements OnAppClickListener {
    public static final String TAG = "AppsFragment";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<App> apps;
    private AppAdapter appAdapter;
    private RecyclerView recyclerViewApps;
    private Context context;
    private String childEmail;
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
        }
    }

    public void initializeAdapter(OnAppClickListener onAppClickListener) {
        appAdapter = new AppAdapter(context, apps);
        appAdapter.setOnAppClickListener(onAppClickListener);
        recyclerViewApps.setAdapter(appAdapter);
    }

    @Override
    public void onItemClick(String appName, boolean blocked) {
        if (blocked) {
            Toast.makeText(context, appName + " blocked", Toast.LENGTH_SHORT).show();
            updateAppState(appName, blocked);

        } else {
            Toast.makeText(context, appName + " enabled", Toast.LENGTH_SHORT).show();
            updateAppState(appName, blocked);

        }
    }

    private void updateAppState(final String appName, final boolean blocked) {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                final String key = nodeShot.getKey();
                Log.i(TAG, "onDataChange: key: " + key);
                Query query = databaseReference.child("childs").child(key).child("apps").orderByChild("appName").equalTo(appName);
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
