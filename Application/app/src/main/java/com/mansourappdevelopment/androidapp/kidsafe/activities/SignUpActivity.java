package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mansourappdevelopment.androidapp.kidsafe.R;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextInputLayout txtEmailSignUpLayout;
    private TextInputLayout txtPasswordSignUpLayout;
    private TextInputEditText txtEmailSignUp;
    private TextInputEditText txtPasswordSignUp;
    private Button btnSignUp;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        txtEmailSignUpLayout = (TextInputLayout) findViewById(R.id.txtEmailSignUpLayout);
        txtEmailSignUp = (TextInputEditText) findViewById(R.id.txtEmailSignUp);

        txtPasswordSignUpLayout = (TextInputLayout) findViewById(R.id.txtPasswordSignUpLayout);
        txtPasswordSignUp = (TextInputEditText) findViewById(R.id.txtPasswordSignUp);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.GONE);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmailSignUp.getText().toString();
                String password = txtPasswordSignUp.getText().toString();
                signUp(email, password);
            }
        });

    }

    private boolean validateForm() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (txtEmailSignUp.getText().toString().equals("")) {
            txtEmailSignUpLayout.setErrorEnabled(true);
            txtEmailSignUpLayout.setError("Enter your email");
            return false;
        } else {
            txtEmailSignUpLayout.setErrorEnabled(false);

        }
        if (!txtEmailSignUp.getText().toString().trim().matches(emailPattern)) {
            txtEmailSignUpLayout.setErrorEnabled(true);
            txtEmailSignUpLayout.setError("Enter a valid email");
            return false;
        } else {
            txtEmailSignUpLayout.setErrorEnabled(false);
        }

        if (txtPasswordSignUp.getText().toString().length() <= 6) {
            txtPasswordSignUpLayout.setErrorEnabled(true);
            txtPasswordSignUpLayout.setError("Enter a valid password");
            return false;
        } else {
            txtPasswordSignUpLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void signUp(String email, String password) {
        if (validateForm()) {
            progressBar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                //update ui -> go to LoginActivity
                                Toast.makeText(SignUpActivity.this, "SignUp Succeeded", Toast.LENGTH_SHORT).show();
                                startLoginActivity();
                            } else {
                                Toast.makeText(SignUpActivity.this, "SignUp Failed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }


    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
