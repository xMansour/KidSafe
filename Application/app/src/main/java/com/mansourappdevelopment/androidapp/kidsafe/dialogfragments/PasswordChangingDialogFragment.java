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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPasswordChangeListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.SharedPrefsUtils;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Validators;

public class PasswordChangingDialogFragment extends DialogFragment {
	private EditText txtOldPassword;
	private EditText txtNewPassword;
	private EditText txtNewPasswordConfirmation;
	private Button btnChangePassword;
	private Button btnCancelChangePassword;
	private OnPasswordChangeListener onPasswordChangeListener;
	
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dialog_change_password, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		onPasswordChangeListener = (OnPasswordChangeListener) getActivity();
		
		txtOldPassword = view.findViewById(R.id.txtOldPassword);
		txtNewPassword = view.findViewById(R.id.txtNewPassword);
		txtNewPasswordConfirmation = view.findViewById(R.id.txtNewPasswordConfirmation);
		btnChangePassword = view.findViewById(R.id.btnChangePassword);
		btnChangePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isValid()) {
					onPasswordChangeListener.onPasswordChange(txtNewPassword.getText().toString());
					dismiss();
				}
				
			}
		});
		btnCancelChangePassword = view.findViewById(R.id.btnCancelChangePassword);
		btnCancelChangePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		
	}
	
	
	private boolean isValid() {
		if (!Validators.isValidPassword(txtOldPassword.getText().toString())) {
			txtOldPassword.setError(getString(R.string.wrong_password));
			txtOldPassword.requestFocus();
			return false;
		}
		
		if (!txtOldPassword.getText().toString().equals(SharedPrefsUtils.getStringPreference(getContext(), Constant.PASSWORD, ""))) {
			txtOldPassword.setError(getString(R.string.wrong_password));
			txtOldPassword.requestFocus();
			return false;
		}
		
		
		if (!Validators.isValidPassword(txtNewPassword.getText().toString())) {
			txtNewPassword.setError(getString(R.string.enter_valid_password));
			txtNewPassword.requestFocus();
			return false;
		}
		
		if (!txtNewPasswordConfirmation.getText().toString().equals(txtNewPassword.getText().toString())) {
			txtNewPasswordConfirmation.setError(getString(R.string.new_password_doesnt_match));
			txtNewPasswordConfirmation.requestFocus();
			return false;
		}
		
		return true;
	}
}

