package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.AppAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnAppClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;

import java.util.ArrayList;

public class AppsFragment extends Fragment implements OnAppClickListener {
    ArrayList<App> apps;
    AppAdapter appAdapter;
    RecyclerView recyclerViewApps;
    Context context;
    Bundle bundle;


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
        }
    }

    public void initializeAdapter(OnAppClickListener onAppClickListener) {
        appAdapter = new AppAdapter(context, apps);
        appAdapter.setOnAppClickListener(onAppClickListener);
        recyclerViewApps.setAdapter(appAdapter);
    }

    @Override
    public void onItemClick(String appName, boolean blocked) {
        if (blocked)
            Toast.makeText(context, appName + " blocked", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, appName + " enabled", Toast.LENGTH_SHORT).show();
    }
}
