package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mansourappdevelopment.androidapp.kidsafe.R;

public class RecoverPasswordFragment extends DialogFragment {
    private View view;
    private EditText txtRecoveryEmail;
    private Button btnRecoverPassword;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recover_password, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();

        txtRecoveryEmail = view.findViewById(R.id.txtRecoveryEmail);
        btnRecoverPassword = view.findViewById(R.id.btnRecoverPassword);
        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtRecoveryEmail.getText().toString();
                recoverPassword(email);
            }
        });
    }

    private boolean validateForm() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (txtRecoveryEmail.getText().toString().equals("")) {
            txtRecoveryEmail.setError("Enter your email");
            return false;
        } else if (!txtRecoveryEmail.getText().toString().trim().matches(emailPattern)) {
            txtRecoveryEmail.setError("Enter a valid email");
            return false;
        }
        return true;
    }

    private void recoverPassword(String email) {
        if (validateForm()) {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Email sent, it may be within your drafts", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }
                    });
        }
    }

}
