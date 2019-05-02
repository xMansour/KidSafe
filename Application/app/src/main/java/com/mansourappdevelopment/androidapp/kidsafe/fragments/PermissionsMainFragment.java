package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnFragmentChangeListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class PermissionsMainFragment extends Fragment {
    private OnFragmentChangeListener onFragmentChangeListener;
    private Button btnPermissionsMainNext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permissions_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onFragmentChangeListener = (OnFragmentChangeListener) getActivity();
        btnPermissionsMainNext = (Button) view.findViewById(R.id.btnPermissionsMainNext);
        btnPermissionsMainNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentChangeListener.onFragmentChange(Constant.PERMISSIONS_SMS_FRAGMENT);
            }
        });


    }
}
