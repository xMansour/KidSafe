package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 59;
    private Uri imageUri;
    private FirebaseAuth auth;
    private TextInputLayout txtSignUpEmailLayout;
    private TextInputLayout txtSignUpPasswordLayout;
    private TextInputLayout txtSignUpNameLayout;
    private TextInputEditText txtSignUpEmail;
    private TextInputEditText txtSignUpPassword;
    private TextInputEditText txtSignUpName;
    private Button btnSignUp;
    private CircleImageView imgProfile;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        txtSignUpEmailLayout = (TextInputLayout) findViewById(R.id.txtSignUpEmailLayout);
        txtSignUpEmail = (TextInputEditText) findViewById(R.id.txtSignUpEmail);

        txtSignUpPasswordLayout = (TextInputLayout) findViewById(R.id.txtSignUpPasswordLayout);
        txtSignUpPassword = (TextInputEditText) findViewById(R.id.txtSignUpPassword);

        txtSignUpNameLayout = (TextInputLayout) findViewById(R.id.txtSignUpNameLayout);
        txtSignUpName = (TextInputEditText) findViewById(R.id.txtSignUpName);
        //TODO:: Name Should be uploaded to the database

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.GONE);

        imgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtSignUpEmail.getText().toString();
                String password = txtSignUpPassword.getText().toString();
                signUp(email, password);
            }
        });

    }

    private boolean validateForm() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (txtSignUpEmail.getText().toString().equals("")) {
            txtSignUpEmailLayout.setErrorEnabled(true);
            txtSignUpEmailLayout.setError("Enter your email");
            return false;
        } else {
            txtSignUpEmailLayout.setErrorEnabled(false);

        }
        if (!txtSignUpEmail.getText().toString().trim().matches(emailPattern)) {
            txtSignUpEmailLayout.setErrorEnabled(true);
            txtSignUpEmailLayout.setError("Enter a valid email");
            return false;
        } else {
            txtSignUpEmailLayout.setErrorEnabled(false);
        }

        if (txtSignUpPassword.getText().toString().length() <= 6) {
            txtSignUpPasswordLayout.setErrorEnabled(true);
            txtSignUpPasswordLayout.setError("Enter a valid password");
            return false;
        } else {
            txtSignUpPasswordLayout.setErrorEnabled(false);
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
            //TODO:: imgageUri Should be uploaded to the database as the profile image's uri
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
