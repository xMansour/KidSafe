package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.ModeSelectionCloseListener;

public class ModeSelectionFragment extends DialogFragment {
    private View view;
    private Button btnModeSelection;
    private EditText txtParentEmail;
    private Switch switchMode;
    private boolean child = false;
    private boolean valid = false;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mode_selection, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        txtParentEmail = view.findViewById(R.id.txtParentEmail);
        switchMode = view.findViewById(R.id.switchMode);
        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchMode.setText("Child");
                    txtParentEmail.setEnabled(true);
                    txtParentEmail.setBackground(getResources().getDrawable(R.drawable.edit_text_rounded));
                    child = true;
                } else {
                    switchMode.setText("Parent");
                    txtParentEmail.setEnabled(false);
                    txtParentEmail.setBackground(getResources().getDrawable(R.drawable.edit_text_rounded_disabled));
                    child = false;
                }
            }
        });
        btnModeSelection = view.findViewById(R.id.btnModeSelection);
        btnModeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (child && validateForm() && valid)
                    dismiss();

                if (!child && !validateForm() && !valid)
                    dismiss();
            }

        });
    }

    private boolean validateForm() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String parentEmail = txtParentEmail.getText().toString();
        //TODO:: a delay is needed here with a progressbar animation on the button
        Query query = databaseReference.child("parents").orderByChild("email").equalTo(parentEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    txtParentEmail.setError("This email isn't registered as a parent");
                    valid = false;
                } else
                    valid = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (parentEmail.equals("")) {
            txtParentEmail.setError("Enter your email");
            return false;
        } else if (!parentEmail.trim().matches(emailPattern)) {
            txtParentEmail.setError("Enter a valid email");
            return false;
        }
        return true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof ModeSelectionCloseListener) {
            ((ModeSelectionCloseListener) activity).onModeSelectionClose(dialog);
            if (child) {
                String parentEmail = txtParentEmail.getText().toString();
                ((ModeSelectionCloseListener) activity).sendUserData(parentEmail, child);
            } else
                ((ModeSelectionCloseListener) activity).sendUserData("null", child);

        }

    }
}
