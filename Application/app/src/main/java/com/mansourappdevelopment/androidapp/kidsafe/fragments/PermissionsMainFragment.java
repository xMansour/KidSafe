package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnFragmentChangeListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class PermissionsMainFragment extends Fragment {
	private OnFragmentChangeListener onFragmentChangeListener;
	private Button btnPermissionsMainNext;
	
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_permissions_main, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onFragmentChangeListener = (OnFragmentChangeListener) getActivity();
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			ImageView imgSecondDot = view.findViewById(R.id.imgSecondDot);
			imgSecondDot.setVisibility(View.GONE);
			ImageView imgThirdDot = view.findViewById(R.id.imgThirdDot);
			imgThirdDot.setVisibility(View.GONE);
			ImageView imgFourthDot = view.findViewById(R.id.imgFourthDot);
			imgFourthDot.setVisibility(View.GONE);
		}
		
		btnPermissionsMainNext = view.findViewById(R.id.btnPermissionsMainNext);
		btnPermissionsMainNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
					onFragmentChangeListener.onFragmentChange(Constant.PERMISSIONS_SETTINGS_FRAGMENT);
				else onFragmentChangeListener.onFragmentChange(Constant.PERMISSIONS_SMS_FRAGMENT);
				
			}
		});
		
		
	}
}
