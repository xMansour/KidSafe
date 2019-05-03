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
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnChildClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class PhoneLockDialogFragment extends DialogFragment {
    private Button btnLock;
    private Button btnCancelLock;
    private Spinner spinnerLockEntries;
    private LinearLayout layoutLockTime;
    private EditText txtLockHours;
    private EditText txtLockMinutes;
    private TextView txtLockHeader;
    private TextView txtLockBody;
    private OnChildClickListener onChildClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_lock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        onChildClickListener = (OnChildClickListener) getActivity();
        Bundle bundle = getArguments();
        String childName = bundle.getString(Constant.CHILD_NAME_EXTRA);

        btnLock = (Button) view.findViewById(R.id.btnLock);
        btnCancelLock = (Button) view.findViewById(R.id.btnCancelLock);
        layoutLockTime = (LinearLayout) view.findViewById(R.id.layoutLockTime);
        txtLockHours = (EditText) view.findViewById(R.id.txtLockHours);
        txtLockMinutes = (EditText) view.findViewById(R.id.txtLockMinutes);
        txtLockHeader = (TextView) view.findViewById(R.id.txtLockHeader);
        txtLockBody = (TextView) view.findViewById(R.id.txtLockBody);


        spinnerLockEntries = (Spinner) view.findViewById(R.id.spinnerLockEntries);
        spinnerLockEntries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    layoutLockTime.setVisibility(View.GONE);
                } else if (position == 1) {
                    layoutLockTime.setVisibility(View.VISIBLE);
                    txtLockHours.requestFocus();
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

        String header = getString(R.string.lock) + " " + childName + getString(R.string.upper_dot_s) + " " + getString(R.string.phone);
        txtLockHeader.setText(header);

        String body = getString(R.string.lock) + " " + childName + getString(R.string.upper_dot_s) + " " + getString(R.string.phone) + " " + getString(R.string.now_or_after_a_period);
        txtLockBody.setText(body);

    }

    private boolean isValid() {
        if (txtLockHours.getText().toString().equals("")) {
            txtLockHours.setError(getString(R.string.enter_a_valid_number));
            txtLockHours.requestFocus();
            return false;
        }
        if (txtLockMinutes.getText().toString().equals("")) {
            txtLockMinutes.setError(getString(R.string.enter_a_valid_number));
            txtLockMinutes.requestFocus();
            return false;
        }
        if (Integer.parseInt(txtLockHours.getText().toString()) > 23) {
            txtLockHours.setError(getString(R.string.maximum_is_23_hours));
            txtLockHours.requestFocus();
            return false;
        }
        if (Integer.parseInt(txtLockMinutes.getText().toString()) > 59) {
            txtLockMinutes.setError(getString(R.string.maximum_is_59_minutes));
            txtLockMinutes.requestFocus();
            return false;
        }

        return true;
    }
}
