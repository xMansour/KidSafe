package com.mansourappdevelopment.androidapp.kidsafe.dialogfragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnConfirmationListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class ConfirmationDialogFragment extends DialogFragment {
	private Button btnConfirm;
	private Button btnCancelConfirm;
	private TextView txtConfirmationBody;
	private OnConfirmationListener onConfirmationListener;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dialog_confirmation, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		onConfirmationListener = (OnConfirmationListener) getActivity();
		
		Bundle bundle = getArguments();
		String confirmationMessage = bundle.getString(Constant.CONFIRMATION_MESSAGE);
		
		txtConfirmationBody = view.findViewById(R.id.txtConfirmationBody);
		txtConfirmationBody.setText(confirmationMessage);
		
		btnConfirm = view.findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onConfirmationListener.onConfirm();
				dismiss();
			}
			
		});
		
		btnCancelConfirm = view.findViewById(R.id.btnCancelConfirm);
		btnCancelConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onConfirmationListener.onConfirmationCancel();
				dismiss();
			}
		});
		
	}
}
