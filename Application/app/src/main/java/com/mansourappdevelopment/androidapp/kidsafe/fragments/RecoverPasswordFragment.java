package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mansourappdevelopment.androidapp.kidsafe.R;

public class RecoverPasswordFragment extends DialogFragment {
    private View view;
    private EditText txtRecoveryEmail;
    private Button btnRecoverPassword;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recover_password, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        txtRecoveryEmail = view.findViewById(R.id.txtRecoveryEmail);
        btnRecoverPassword = view.findViewById(R.id.btnRecoverPassword);
        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Sending reset email", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
