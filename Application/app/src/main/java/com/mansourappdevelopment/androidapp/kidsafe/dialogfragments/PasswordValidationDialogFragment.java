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
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPasswordValidationListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.SharedPrefsUtils;

public class PasswordValidationDialogFragment extends DialogFragment {
	private EditText txtValidationPassword;
	private Button btnValidation;
	private Button btnCancelValidation;
	private OnPasswordValidationListener onPasswordValidationListener;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dialog_password_validation, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onPasswordValidationListener = (OnPasswordValidationListener) getActivity();
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		txtValidationPassword = view.findViewById(R.id.txtValidationPassword);
		btnValidation = view.findViewById(R.id.btnValidation);
		btnCancelValidation = view.findViewById(R.id.btnCancelValidation);
		final String passwordPrefs = SharedPrefsUtils.getStringPreference(getContext(), Constant.PASSWORD, "");
		
		btnValidation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (txtValidationPassword.getText().toString().equals(passwordPrefs)) {
					onPasswordValidationListener.onValidationOk();
					dismiss();
				} else {
					txtValidationPassword.requestFocus();
					txtValidationPassword.setError(getString(R.string.wrong_password));
				}
				
			}
		});
		
		
		btnCancelValidation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		
		
	}
}
