package com.mansourappdevelopment.androidapp.kidsafe.dialogfragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnGeoFenceSettingListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Validators;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_NAME_EXTRA;

public class GeoFenceSettingDialogFragment extends DialogFragment {
	private Spinner spinnerGeoFenceEntries;
	private EditText txtGeoFenceDiameter;
	//private TextView txtGeoFenceHeader;
	private TextView txtGeoFenceBody;
	private Button btnSetGeoFence;
	private Button btnCancelSetGeoFence;
	private OnGeoFenceSettingListener onGeoFenceSettingListener;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dialog_geo_fence, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		onGeoFenceSettingListener = (OnGeoFenceSettingListener) getTargetFragment();
		
		spinnerGeoFenceEntries = view.findViewById(R.id.spinnerGeoFenceEntries);
		txtGeoFenceDiameter = view.findViewById(R.id.txtGeoFenceDiameter);

        /*txtGeoFenceHeader = (TextView) view.findViewById(R.id.txtGeoFenceHeader);
        String header = getResources().getString(R.string.setup_a_geofence) + " " + getString(R.string.on) + " " + getChildName();
        txtGeoFenceHeader.setText(header);*/
		
		txtGeoFenceBody = view.findViewById(R.id.txtGeoFenceBody);
		String body = getResources().getString(R.string.setup_geo_fence_on) + " " + getChildName() + getResources().getString(R.string.if_he_exceeded_it_you_will_be_alerted);
		txtGeoFenceBody.setText(body);
		
		btnSetGeoFence = view.findViewById(R.id.btnSetGeoFence);
		btnSetGeoFence.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String selectedEntry = spinnerGeoFenceEntries.getSelectedItem().toString();
				String geoFenceDiameter = txtGeoFenceDiameter.getText().toString();
				
				if (!Validators.isValidGeoFenceDiameter(geoFenceDiameter)) {
					txtGeoFenceDiameter.setError(getResources().getString(R.string.please_enter_a_valid_diameter));
					txtGeoFenceDiameter.requestFocus();
				} else {
					onGeoFenceSettingListener.onGeoFenceSet(selectedEntry, Double.parseDouble(geoFenceDiameter));
					dismiss();
				}
				
			}
		});
		
		btnCancelSetGeoFence = view.findViewById(R.id.btnCancelSetGeoFence);
		btnCancelSetGeoFence.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		
		
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
