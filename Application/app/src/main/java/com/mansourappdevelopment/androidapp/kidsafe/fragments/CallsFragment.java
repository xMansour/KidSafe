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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.CallAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnCallDeleteClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Call;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_CALLS_EXTRA;
import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_EMAIL_EXTRA;


public class CallsFragment extends Fragment implements OnCallDeleteClickListener {
    private static final String TAG = "CallsFragmentTAG";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private HashMap<String, Call> calls;
    private String childEmail;
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
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        recyclerViewCalls = (RecyclerView) view.findViewById(R.id.recyclerViewCalls);
        recyclerViewCalls.setHasFixedSize(true);
        recyclerViewCalls.setLayoutManager(new LinearLayoutManager(getContext()));

        initializeAdapter(getData(), this);
    }

    private ArrayList<Call> getData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            calls = (HashMap<String, Call>) bundle.getSerializable(CHILD_CALLS_EXTRA);
            childEmail = bundle.getString(CHILD_EMAIL_EXTRA);

            return new ArrayList<>(calls.values());
        }
        return null;
    }

    private void initializeAdapter(ArrayList<Call> calls, OnCallDeleteClickListener onCallDeleteClickListener) {
        CallAdapter callAdapter = new CallAdapter(getContext(), calls);
        callAdapter.setOnCallDeleteClickListener(onCallDeleteClickListener);
        recyclerViewCalls.setAdapter(callAdapter);
    }

    @Override
    public void onCallDeleteClick(final Call call) {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                final String key = nodeShot.getKey();
                Log.i(TAG, "onDataChange: key: " + key);
                Query query = databaseReference.child("childs").child(key).child("calls").orderByChild("callTime").equalTo(call.getCallTime());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            snapshot.getRef().removeValue();
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
