package com.mansourappdevelopment.androidapp.kidsafe.dialogfragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnAppClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class AppLockDialogFragment extends DialogFragment {
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
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		onAppClickListener = (OnAppClickListener) getTargetFragment();
		Bundle bundle = getArguments();
		String appName = bundle.getString(Constant.CHILD_APP_NAME_EXTRA);
		String headerText = "Block " + appName;
		
		txtLockAppHeader = view.findViewById(R.id.txtLockAppHeader);
		txtLockAppHeader.setText(headerText);
		
		btnLockApp = view.findViewById(R.id.btnLockApp);
		btnCancelLockApp = view.findViewById(R.id.btnCancelLockApp);
		layoutLockAppTime = view.findViewById(R.id.layoutLockAppTime);
		txtLockAppHours = view.findViewById(R.id.txtLockAppHours);
		txtLockAppMinutes = view.findViewById(R.id.txtLockAppMinutes);
		spinnerLockAppEntries = view.findViewById(R.id.spinnerLockAppEntries);
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
