package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnModeSelectionListener;

public class ModeSelectionFragment extends DialogFragment {
    private View view;
    private Button btnModeSelection;
    private Button btnCancelModeSelection;
    private EditText txtParentEmail;
    private Switch switchMode;
    private boolean child = false;
    private boolean valid = false;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private OnModeSelectionListener onModeSelectionListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialog_mode_selection, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        onModeSelectionListener = (OnModeSelectionListener) getActivity();

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
                        if (!dataSnapshot.exists()) {
                            txtParentEmail.setError(getString(R.string.this_email_isnt_registered_as_parent));
                            valid = false;
                        } else
                            valid = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        switchMode = view.findViewById(R.id.switchMode);
        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchMode.setText(getString(R.string.child));
                    txtParentEmail.setEnabled(true);
                    txtParentEmail.requestFocus();
                    child = true;
                } else {
                    switchMode.setText(getString(R.string.parent));
                    txtParentEmail.setEnabled(false);
                    child = false;
                }
            }
        });

        btnModeSelection = view.findViewById(R.id.btnModeSelection);
        btnModeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (child && isValid() && valid) {
                    onModeSelectionListener.onModeSelected(txtParentEmail.getText().toString().toLowerCase(), true);
                    dismiss();
                }
                if (!child) {
                    onModeSelectionListener.onModeSelected(null, false);
                    dismiss();
                }
            }

        });

        btnCancelModeSelection = (Button) view.findViewById(R.id.btnCancelModeSelection);
        btnCancelModeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onModeSelectionListener.onDismiss();
                dismiss();
            }
        });
    }

    private boolean isValid() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String parentEmail = txtParentEmail.getText().toString();

        if (parentEmail.equals("")) {
            txtParentEmail.setError(getString(R.string.enter_valid_email));
            return false;
        } else if (!parentEmail.trim().matches(emailPattern)) {
            txtParentEmail.setError(getString(R.string.enter_valid_email));
            return false;
        }

        return true;
    }

}
