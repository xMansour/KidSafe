package com.mansourappdevelopment.androidapp.kidsafe.dialogfragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class InformationDialogFragment extends DialogFragment {
	private TextView txtInformationBody;
	private Button btnInformationOk;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dialog_information, container, false);
	}
	
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		Bundle bundle = getArguments();
		String message = bundle.getString(Constant.INFORMATION_MESSAGE);
		
		txtInformationBody = view.findViewById(R.id.txtInformationBody);
		txtInformationBody.setText(message);
		btnInformationOk = view.findViewById(R.id.btnInformationOk);
		btnInformationOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}
}
