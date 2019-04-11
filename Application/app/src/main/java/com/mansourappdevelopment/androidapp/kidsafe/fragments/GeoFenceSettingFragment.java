package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnGeoFenceSettingListener;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_EMAIL_EXTRA;
import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_NAME_EXTRA;

public class GeoFenceSettingFragment extends DialogFragment {
    private Spinner spinnerGeoFenceEntries;
    private EditText txtGeoFenceDiameter;
    private TextView txtGeoFenceHeader;
    private Button btnSetGeoFence;
    private OnGeoFenceSettingListener onGeoFenceSettingListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo_fence, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        spinnerGeoFenceEntries = (Spinner) view.findViewById(R.id.spinnerGeoFenceEntries);
        txtGeoFenceDiameter = (EditText) view.findViewById(R.id.txtGeoFenceDiameter);
        txtGeoFenceHeader = (TextView) view.findViewById(R.id.txtGeoFenceHeader);
        btnSetGeoFence = (Button) view.findViewById(R.id.btnSetGeoFence);

        btnSetGeoFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedEntry = spinnerGeoFenceEntries.getSelectedItem().toString();
                String geoFenceDiameter = txtGeoFenceDiameter.getText().toString();

                if (geoFenceDiameter.equals("")) {
                    txtGeoFenceDiameter.setError("Please enter a value");
                } else {
                    onGeoFenceSettingListener = (OnGeoFenceSettingListener) getTargetFragment();
                    onGeoFenceSettingListener.onGeoFenceSet(selectedEntry, Double.parseDouble(geoFenceDiameter));
                    dismiss();
                }

            }
        });

        String header = getResources().getString(R.string.setup_a_geofence) + " " + getChildName();
        txtGeoFenceHeader.setText(header);
    }

    private String getChildName() {
        Bundle bundle = getActivity().getIntent().getExtras();
        String childName = null;
        if (bundle != null) {
            childName = bundle.getString(CHILD_NAME_EXTRA);
        }
        return childName;
    }
}
