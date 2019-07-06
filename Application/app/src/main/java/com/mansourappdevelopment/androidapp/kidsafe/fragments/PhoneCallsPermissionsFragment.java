package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.InformationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.PermissionExplanationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnFragmentChangeListener;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPermissionExplanationListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class PhoneCallsPermissionsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, OnPermissionExplanationListener {
	private Switch switchPhoneStatePermission;
	private Switch switchReadCallLogPermission;
	private Switch switchReadContactsPermission;
	private Context context;
	private Activity activity;
	private View layout;
	private FragmentManager fragmentManager;
	private OnFragmentChangeListener onFragmentChangeListener;
	private Button btnPermissionsPhoneCallsPrev;
	private Button btnPermissionsPhoneCallsNext;
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
				case R.id.switchPhoneStatePermission:
					requestPhoneStatePermission();
					break;
				
				case R.id.switchReadCallLogPermission:
					requestReadCallLogPermission();
					break;
				
				case R.id.switchReadContactsPermission:
					requestReadContactsPermission();
					break;
			}
		}
	}
	
	private void requestPhoneStatePermission() {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
				startPermissionExplanationFragment(Constant.READ_PHONE_STATE_PERMISSION_REQUEST_CODE, switchPhoneStatePermission.getId());
			} else {
				requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constant.READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
			}
			
		}
	}
	
	private void startPermissionExplanationFragment(int requestCode, int id) {
		PermissionExplanationDialogFragment explanationFragment = new PermissionExplanationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.PERMISSION_REQUEST_CODE, requestCode);
		bundle.putInt(Constant.SWITCH_ID, id);
		explanationFragment.setArguments(bundle);
		explanationFragment.setCancelable(false);
		explanationFragment.setTargetFragment(this, Constant.PERMISSION_EXPLANATION_FRAGMENT);
		explanationFragment.show(fragmentManager, Constant.PERMISSION_EXPLANATION_FRAGMENT_TAG);
	}
	
	private void requestReadCallLogPermission() {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALL_LOG)) {
				startPermissionExplanationFragment(Constant.READ_CALL_LOG_PERMISSION_REQUEST_CODE, switchReadCallLogPermission.getId());
			} else {
				requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, Constant.READ_CALL_LOG_PERMISSION_REQUEST_CODE);
			}
			
		}
	}
	
	private void requestReadContactsPermission() {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
				startPermissionExplanationFragment(Constant.READ_CONTACTS_PERMISSION_REQUEST_CODE, switchReadContactsPermission.getId());
			} else {
				requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, Constant.READ_CONTACTS_PERMISSION_REQUEST_CODE);
			}
			
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case Constant.READ_PHONE_STATE_PERMISSION_REQUEST_CODE:
				if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(context, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
					switchPhoneStatePermission.setChecked(true);
					//switchPhoneStatePermission.setEnabled(false);
					
				} else {
					Toast.makeText(context, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
					switchPhoneStatePermission.setChecked(false);
				}
				
				break;
			
			case Constant.READ_CALL_LOG_PERMISSION_REQUEST_CODE:
				if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(context, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
					switchReadCallLogPermission.setChecked(true);
					//switchReadCallLogPermission.setEnabled(false);
					
				} else {
					Toast.makeText(context, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
					switchReadCallLogPermission.setChecked(false);
				}
				
				break;
			
			case Constant.READ_CONTACTS_PERMISSION_REQUEST_CODE:
				if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(context, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
					switchReadContactsPermission.setChecked(true);
					//switchReadContactsPermission.setEnabled(false);
					
				} else {
					Toast.makeText(context, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
					switchReadContactsPermission.setChecked(false);
				}
				
				break;
		}
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_permissions_phone_calls, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		context = getContext();
		activity = getActivity();
		layout = view;
		fragmentManager = getFragmentManager();
		onFragmentChangeListener = (OnFragmentChangeListener) activity;
		
		btnPermissionsPhoneCallsNext = view.findViewById(R.id.btnPermissionsPhoneCallsNext);
		btnPermissionsPhoneCallsNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkAllPermissions())
					onFragmentChangeListener.onFragmentChange(Constant.PERMISSIONS_LOCATION_FRAGMENT);
				else startInformationDialogFragment(getString(R.string.please_allow_permissions));
				
			}
		});
		
		btnPermissionsPhoneCallsPrev = view.findViewById(R.id.btnPermissionsPhoneCallsPrev);
		btnPermissionsPhoneCallsPrev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFragmentChangeListener.onFragmentChange(Constant.PERMISSIONS_SMS_FRAGMENT);
			}
		});
		
		
		switchPhoneStatePermission = view.findViewById(R.id.switchPhoneStatePermission);
		switchPhoneStatePermission.setChecked(isPhoneStatePermissionGranted());
		switchPhoneStatePermission.setOnCheckedChangeListener(this);
		switchReadCallLogPermission = view.findViewById(R.id.switchReadCallLogPermission);
		switchReadCallLogPermission.setChecked(isReadCallLogPermissionGranted());
		switchReadCallLogPermission.setOnCheckedChangeListener(this);
		switchReadContactsPermission = view.findViewById(R.id.switchReadContactsPermission);
		switchReadContactsPermission.setChecked(isReadContactsPermissionGranted());
		switchReadContactsPermission.setOnCheckedChangeListener(this);
	}
	
	private boolean checkAllPermissions() {
		return isPhoneStatePermissionGranted() && isReadCallLogPermissionGranted() && isReadContactsPermissionGranted();
	}
	
	private boolean isPhoneStatePermissionGranted() {
		return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
	}
	
	private boolean isReadCallLogPermissionGranted() {
		return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
	}
	
	private boolean isReadContactsPermissionGranted() {
		return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
		
	}
	
	private void startInformationDialogFragment(String message) {
		InformationDialogFragment informationDialogFragment = new InformationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.INFORMATION_MESSAGE, message);
		informationDialogFragment.setArguments(bundle);
		informationDialogFragment.setCancelable(false);
		informationDialogFragment.show(getChildFragmentManager(), Constant.INFORMATION_DIALOG_FRAGMENT_TAG);
	}
	
	@Override
	public void onOk(int requestCode) {
		switch (requestCode) {
			case Constant.READ_PHONE_STATE_PERMISSION_REQUEST_CODE:
				requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constant.READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
				break;
			case Constant.READ_CALL_LOG_PERMISSION_REQUEST_CODE:
				requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, Constant.READ_CALL_LOG_PERMISSION_REQUEST_CODE);
				break;
			case Constant.READ_CONTACTS_PERMISSION_REQUEST_CODE:
				requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, Constant.READ_CONTACTS_PERMISSION_REQUEST_CODE);
				break;
		}
		
	}
	
	@Override
	public void onCancel(int switchId) {
		Switch pressedSwitch = layout.findViewById(switchId);
		pressedSwitch.setChecked(false);
	}
}
