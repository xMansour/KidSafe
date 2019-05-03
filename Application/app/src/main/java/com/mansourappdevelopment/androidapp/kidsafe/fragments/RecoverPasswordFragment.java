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

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPasswordResetListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Validators;

public class RecoverPasswordFragment extends DialogFragment {
    private EditText txtRecoveryEmail;
    private Button btnRecoverPassword;
    private Button btnCancelRecoverPassword;
    private OnPasswordResetListener onPasswordResetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_recover_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        onPasswordResetListener = (OnPasswordResetListener) getActivity();
        txtRecoveryEmail = view.findViewById(R.id.txtRecoveryEmail);
        btnRecoverPassword = view.findViewById(R.id.btnRecoverPassword);
        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtRecoveryEmail.getText().toString();
                if (Validators.isValidEmail(email)) {
                    onPasswordResetListener.onOkClicked(email);
                    dismiss();
                } else {
                    txtRecoveryEmail.setError(getString(R.string.enter_valid_email));
                }
            }
        });

        btnCancelRecoverPassword = (Button) view.findViewById(R.id.btnCancelRecoverPassword);
        btnCancelRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPasswordResetListener.onCancelClicked();
                dismiss();
            }
        });
    }


}
