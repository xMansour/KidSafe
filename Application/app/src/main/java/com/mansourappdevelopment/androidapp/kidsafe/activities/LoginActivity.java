package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mansourappdevelopment.androidapp.kidsafe.R;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout txtLayoutEmail;
    private TextInputLayout txtLayoutPassword;
    private TextInputEditText txtEmail;
    private TextInputEditText txtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        txtLayoutEmail = (TextInputLayout) findViewById(R.id.txtEmailLayout);
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);

        txtLayoutPassword = (TextInputLayout) findViewById(R.id.txtPasswordLayout);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);

        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        btnLogin = (Button) findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                logIn(email, password);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //update ui -> go to the SignedIn Activity
        if (currentUser != null)
            startSignedInActivity();
    }

    private boolean validateForm() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (txtEmail.getText().toString().equals("")) {
            txtLayoutEmail.setErrorEnabled(true);
            txtLayoutEmail.setError("Enter your email");
            return false;
        } else {
            txtLayoutEmail.setErrorEnabled(false);

        }
        if (!txtEmail.getText().toString().trim().matches(emailPattern)) {
            txtLayoutEmail.setErrorEnabled(true);
            txtLayoutEmail.setError("Enter a valid email");
            return false;
        } else {
            txtLayoutEmail.setErrorEnabled(false);
        }

        if (txtPassword.getText().toString().length() <= 6) {
            txtLayoutPassword.setErrorEnabled(true);
            txtLayoutPassword.setError("Enter a valid password");
            return false;
        } else {
            txtLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void logIn(String email, String password) {
        if (validateForm()) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Authentication Succeeded", Toast.LENGTH_SHORT).show();
                                //update ui -> go to signedIn activity
                                startSignedInActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void startSignedInActivity() {
        Intent intent = new Intent(this, SignedInActivity.class);
        startActivity(intent);
    }
}
