package com.mansourappdevelopment.androidapp.kidsafe.fragments;

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

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.CallAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Call;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_CALLS_EXTRA;


public class CallsFragment extends Fragment {
    private static final String TAG = "CallsFragmentTAG";
    private HashMap<String, Call> calls;
    private RecyclerView recyclerViewCalls;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewCalls = (RecyclerView) view.findViewById(R.id.recyclerViewCalls);
        recyclerViewCalls.setHasFixedSize(true);
        recyclerViewCalls.setLayoutManager(new LinearLayoutManager(getContext()));

        initializeAdapter(getData());
    }

    private ArrayList<Call> getData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            calls = (HashMap<String, Call>) bundle.getSerializable(CHILD_CALLS_EXTRA);
            return new ArrayList<>(calls.values());
        }
        return null;
    }

    private void initializeAdapter(ArrayList<Call> calls) {
        CallAdapter callAdapter = new CallAdapter(getContext(), calls);
        recyclerViewCalls.setAdapter(callAdapter);
    }
}
