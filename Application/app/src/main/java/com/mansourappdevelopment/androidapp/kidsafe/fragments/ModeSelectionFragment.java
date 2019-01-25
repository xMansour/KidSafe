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

import com.google.firebase.auth.FirebaseAuth;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.ModeSelectionCloseListener;

public class ModeSelectionFragment extends DialogFragment {
    private View view;
    private Button btnModeSelection;
    private EditText txtParentEmail;
    private Switch switchMode;
    private Boolean isChild = false;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mode_selection, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        txtParentEmail = view.findViewById(R.id.txtParentEmail);
        switchMode = view.findViewById(R.id.switchMode);
        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchMode.setText("Child");
                    txtParentEmail.setEnabled(true);
                    txtParentEmail.setBackground(getResources().getDrawable(R.drawable.edit_text_rounded));
                    isChild = true;
                } else {
                    switchMode.setText("Parent");
                    txtParentEmail.setEnabled(false);
                    txtParentEmail.setBackground(getResources().getDrawable(R.drawable.edit_text_rounded_disabled));
                    isChild = false;
                }
            }
        });
        btnModeSelection = view.findViewById(R.id.btnModeSelection);
        btnModeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChild)
                    connectParent();
                else
                    setAsParent();

                dismiss();
            }
        });
    }

    private boolean validateForm() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (txtParentEmail.getText().toString().equals("")) {
            txtParentEmail.setError("Enter your email");
            return false;
        } else if (!txtParentEmail.getText().toString().trim().matches(emailPattern)) {
            txtParentEmail.setError("Enter a valid email");
            return false;
        }
        return true;
    }

    private void connectParent() {
        if (validateForm()) {
            //TODO:: associate the parent email with that child in the database
        }

    }

    private void setAsParent() {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof ModeSelectionCloseListener) {
            ((ModeSelectionCloseListener) activity).onModeSelectionClose(dialog);
        }
    }
}
