package com.mansourappdevelopment.androidapp.kidsafe.dialogfragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnGoogleChildSignUp;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Validators;

public class GoogleChildSignUpDialogFragment extends DialogFragment {
	private Button btnChildSignUp;
	private Button btnCancelChildSignUp;
	private EditText txtParentEmail;
	private FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReference;
	private OnGoogleChildSignUp onGoogleChildSignUp;
	private boolean validParent = false;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dialog_google_child_sign_up, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("users");
		onGoogleChildSignUp = (OnGoogleChildSignUp) getActivity();
		
		txtParentEmail = view.findViewById(R.id.txtParentEmail);
		txtParentEmail.addTextChangedListener(new TextWatcher() {//TODO:: need a better way
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				Query query = databaseReference.child("parents").orderByChild("email").equalTo(txtParentEmail.getText().toString());
				query.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						validParent = dataSnapshot.exists();
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
				});
				
			}
		});
		
		btnChildSignUp = view.findViewById(R.id.btnChildSignUp);
		btnChildSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isValid()) {
					onGoogleChildSignUp.onModeSelected(txtParentEmail.getText().toString().toLowerCase());
					dismiss();
				}
			}
			
		});
		
		btnCancelChildSignUp = view.findViewById(R.id.btnCancelChildSignUp);
		btnCancelChildSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	private boolean isValid() {
		if (!Validators.isValidEmail(txtParentEmail.getText().toString()) || !validParent) {
			txtParentEmail.setError(getString(R.string.this_email_isnt_registered_as_parent));
			txtParentEmail.requestFocus();
			return false;
		}
		
		return true;
	}
	
}
