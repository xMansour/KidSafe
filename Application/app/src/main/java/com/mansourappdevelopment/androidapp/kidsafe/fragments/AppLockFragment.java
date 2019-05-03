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
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnAppClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class AppLockFragment extends DialogFragment {
    private Button btnLockApp;
    private Button btnCancelLockApp;
    private Spinner spinnerLockAppEntries;
    private LinearLayout layoutLockAppTime;
    private EditText txtLockAppHours;
    private EditText txtLockAppMinutes;
    private TextView txtLockAppHeader;
    private OnAppClickListener onAppClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_lock_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        onAppClickListener = (OnAppClickListener) getTargetFragment();
        Bundle bundle = getArguments();
        String appName = bundle.getString(Constant.CHILD_APP_NAME_EXTRA);
        String headerText = "Block " + appName;

        txtLockAppHeader = (TextView) view.findViewById(R.id.txtLockAppHeader);
        txtLockAppHeader.setText(headerText);

        btnLockApp = (Button) view.findViewById(R.id.btnLockApp);
        btnCancelLockApp = (Button) view.findViewById(R.id.btnCancelLockApp);
        layoutLockAppTime = (LinearLayout) view.findViewById(R.id.layoutLockAppTime);
        txtLockAppHours = (EditText) view.findViewById(R.id.txtLockAppHours);
        txtLockAppMinutes = (EditText) view.findViewById(R.id.txtLockAppMinutes);
        spinnerLockAppEntries = (Spinner) view.findViewById(R.id.spinnerLockAppEntries);
        spinnerLockAppEntries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    layoutLockAppTime.setVisibility(View.GONE);
                } else if (position == 1) {
                    layoutLockAppTime.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                layoutLockAppTime.setVisibility(View.GONE);

            }
        });

        btnLockApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerLockAppEntries.getSelectedItemPosition() == 0) {
                    //onAppClickListener.onLockAppSet(0, 0);
                    dismiss();

                } else if (spinnerLockAppEntries.getSelectedItemPosition() == 1) {
                    if (isValid()) {
                        int hours = Integer.parseInt(txtLockAppHours.getText().toString());
                        int minutes = Integer.parseInt(txtLockAppMinutes.getText().toString());
                        //onAppClickListener.onLockAppSet(hours, minutes);
                        dismiss();
                    }

                }
            }
        });

        btnCancelLockApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onAppClickListener.onLockCanceled();
                dismiss();
            }
        });
    }

    private boolean isValid() {
        if (txtLockAppHours.getText().toString().equals("")) {
            txtLockAppHours.setError(getString(R.string.enter_a_valid_number));
            return false;
        }
        if (txtLockAppMinutes.getText().toString().equals("")) {
            txtLockAppMinutes.setError(getString(R.string.enter_a_valid_number));
            return false;
        }
        if (Integer.parseInt(txtLockAppHours.getText().toString()) > 23) {
            txtLockAppHours.setError(getString(R.string.maximum_is_23_hours));
            return false;
        }
        if (Integer.parseInt(txtLockAppMinutes.getText().toString()) > 59) {
            txtLockAppMinutes.setError(getString(R.string.maximum_is_59_minutes));
            return false;
        }

        return true;
    }
}
