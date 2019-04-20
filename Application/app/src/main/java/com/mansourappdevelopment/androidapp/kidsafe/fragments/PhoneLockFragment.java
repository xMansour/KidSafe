package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnChildClickListener;

public class PhoneLockFragment extends DialogFragment {
    private Button btnLock;
    private Button btnCancelLock;
    private Spinner spinnerLockEntries;
    private LinearLayout layoutLockTime;
    private EditText txtLockHours;
    private EditText txtLockMinutes;
    private OnChildClickListener onChildClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        onChildClickListener = (OnChildClickListener) getActivity();
        btnLock = (Button) view.findViewById(R.id.btnLock);
        btnCancelLock = (Button) view.findViewById(R.id.btnCancelLock);
        layoutLockTime = (LinearLayout) view.findViewById(R.id.layoutLockTime);
        txtLockHours = (EditText) view.findViewById(R.id.txtLockHours);
        txtLockMinutes = (EditText) view.findViewById(R.id.txtLockMinutes);
        spinnerLockEntries = (Spinner) view.findViewById(R.id.spinnerLockEntries);
        spinnerLockEntries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    layoutLockTime.setVisibility(View.GONE);
                } else if (position == 1) {
                    layoutLockTime.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                layoutLockTime.setVisibility(View.GONE);

            }
        });


        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerLockEntries.getSelectedItemPosition() == 0) {
                    onChildClickListener.onLockPhoneSet(0, 0);
                    dismiss();

                } else if (spinnerLockEntries.getSelectedItemPosition() == 1) {
                    if (isValid()) {
                        int hours = Integer.parseInt(txtLockHours.getText().toString());
                        int minutes = Integer.parseInt(txtLockMinutes.getText().toString());
                        onChildClickListener.onLockPhoneSet(hours, minutes);
                        dismiss();
                    }

                }
            }
        });

        btnCancelLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChildClickListener.onLockCanceled();
                dismiss();
            }
        });

    }

    private boolean isValid() {
        if (txtLockHours.getText().toString().equals("")) {
            txtLockHours.setError(getString(R.string.enter_a_valid_number));
            return false;
        }
        if (txtLockMinutes.getText().toString().equals("")) {
            txtLockMinutes.setError(getString(R.string.enter_a_valid_number));
            return false;
        }
        if (Integer.parseInt(txtLockHours.getText().toString()) > 23) {
            txtLockHours.setError(getString(R.string.maximum_is_23_hours));
            return false;
        }
        if (Integer.parseInt(txtLockMinutes.getText().toString()) > 59) {
            txtLockMinutes.setError(getString(R.string.maximum_is_59_minutes));
            return false;
        }

        return true;
    }
}
