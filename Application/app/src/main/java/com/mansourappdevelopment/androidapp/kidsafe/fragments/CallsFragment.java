package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Call;

import java.util.HashMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_CALLS_EXTRA;


public class CallsFragment extends Fragment {
    private static final String TAG = "CallsFragmentTAG";
    private HashMap<String, Call> calls;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getData();
    }

    private void getData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            calls = (HashMap<String, Call>) bundle.getSerializable(CHILD_CALLS_EXTRA);
        }

        for (String key : calls.keySet()) {
            Log.i(TAG, "getData: callType: " + calls.get(key).getCallType());
            Log.i(TAG, "getData: callerPhoneNumber: " + calls.get(key).getPhoneNumber());
            Log.i(TAG, "getData: contactName: " + calls.get(key).getContactName());
            Log.i(TAG, "getData: callTime: " + calls.get(key).getCallTime());
            Log.i(TAG, "getData: callDuration: " + calls.get(key).getCallDurationInSeconds());
        }
    }
}
