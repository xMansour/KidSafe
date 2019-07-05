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
import android.widget.Spinner;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnLanguageSelectionListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.SharedPrefsUtils;

public class LanguageSelectionDialogFragment extends DialogFragment {
	private Spinner spinnerLanguageEntries;
	private Button btnOkLanguageSelection;
	private Button btnCancelLanguageSelection;
	private OnLanguageSelectionListener onLanguageSelectionListener;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dialog_language_selection, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		onLanguageSelectionListener = (OnLanguageSelectionListener) getActivity();
		
		String appLanguage = SharedPrefsUtils.getStringPreference(getContext(), Constant.APP_LANGUAGE, "en");
		spinnerLanguageEntries = view.findViewById(R.id.spinnerLanguageEntries);
		if (appLanguage.equals("en"))
			spinnerLanguageEntries.setSelection(0);
		else if (appLanguage.equals("ar"))
			spinnerLanguageEntries.setSelection(1);
		
		
		btnOkLanguageSelection = view.findViewById(R.id.btnOkLanguageSelection);
		btnOkLanguageSelection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onLanguageSelectionListener.onLanguageSelection(spinnerLanguageEntries.getSelectedItem().toString());
				dismiss();
			}
		});
		
		btnCancelLanguageSelection = view.findViewById(R.id.btnCancelLanguageSelection);
		btnCancelLanguageSelection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		
		
	}
}
