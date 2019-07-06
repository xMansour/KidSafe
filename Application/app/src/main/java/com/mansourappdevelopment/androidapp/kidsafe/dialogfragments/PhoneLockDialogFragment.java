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
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnChildClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Validators;

public class PhoneLockDialogFragment extends DialogFragment {
	private Button btnLock;
	private Button btnCancelLock;
	private Spinner spinnerLockEntries;
	private LinearLayout layoutLockTime;
	private EditText txtLockHours;
	private EditText txtLockMinutes;
	//private TextView txtLockHeader;
	private TextView txtLockBody;
	private OnChildClickListener onChildClickListener;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dialog_lock, container, false);
	}

    /*@Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onChildClickListener.onLockDismiss();
    }*/
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		onChildClickListener = (OnChildClickListener) getActivity();
		
		Bundle bundle = getArguments();
		String childName = bundle.getString(Constant.CHILD_NAME_EXTRA);
		
		
		layoutLockTime = view.findViewById(R.id.layoutLockTime);
		txtLockHours = view.findViewById(R.id.txtLockHours);
		txtLockMinutes = view.findViewById(R.id.txtLockMinutes);

        /*txtLockHeader = (TextView) view.findViewById(R.id.txtLockHeader);
        String header = getString(R.string.lock) + " " + childName + getString(R.string.upper_dot_s) + " " + getString(R.string.phone);
        txtLockHeader.setText(header);*/
		
		txtLockBody = view.findViewById(R.id.txtLockBody);
		String body = getString(R.string.lock) + " " + childName + getString(R.string.upper_dot_s) + " " + getString(R.string.phone) + " " + getString(R.string.now_or_after_a_period);
		txtLockBody.setText(body);
		
		spinnerLockEntries = view.findViewById(R.id.spinnerLockEntries);
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
		
		btnLock = view.findViewById(R.id.btnLock);
		btnLock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int hours = 0;
				int minutes = 0;
				//onChildClickListener.onLockDismiss();
				if (spinnerLockEntries.getSelectedItemPosition() == 0) {
					onChildClickListener.onLockPhoneSet(hours, minutes);
					dismiss();
				}
				if (spinnerLockEntries.getSelectedItemPosition() == 1) {
					if (Validators.isValidHours(txtLockHours.getText().toString())) {
						hours = Integer.parseInt(txtLockHours.getText().toString());
					} else {
						txtLockHours.setError(getResources().getString(R.string.maximum_is_23_hours));
						txtLockHours.requestFocus();
					}
					
					if (Validators.isValidMinutes(txtLockMinutes.getText().toString())) {
						minutes = Integer.parseInt(txtLockMinutes.getText().toString());
					} else {
						txtLockMinutes.setError(getResources().getString(R.string.maximum_is_59_minutes));
						txtLockMinutes.requestFocus();
					}
					
					if (Validators.isValidHours(txtLockHours.getText().toString()) && Validators.isValidMinutes(txtLockMinutes.getText().toString())) {
						onChildClickListener.onLockPhoneSet(hours, minutes);
						dismiss();
					}
				}
				
			}
		});
		
		btnCancelLock = view.findViewById(R.id.btnCancelLock);
		btnCancelLock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onChildClickListener.onLockCanceled();
				dismiss();
			}
		});
	}
}
