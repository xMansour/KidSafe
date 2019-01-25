package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.ModeSelectionFragment;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.ModeSelectionCloseListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements ModeSelectionCloseListener {
    private final int PICK_IMAGE_REQUEST = 59;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Uri imageUri;
    private FirebaseAuth auth;
    private EditText txtSignUpEmail;
    private EditText txtSignUpPassword;
    private EditText txtSignUpName;
    private Button btnSignUp;
    private CircleImageView imgProfile;
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;
    private boolean isChild = false;
    private String parentEmail;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        txtSignUpEmail = (EditText) findViewById(R.id.txtSignUpEmail);
        txtSignUpPassword = (EditText) findViewById(R.id.txtSignUpPassword);
        txtSignUpName = (EditText) findViewById(R.id.txtSignUpName);
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

/*    private boolean validateForm() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (txtSignUpEmail.getText().toString().equals("")) {
            txtSignUpEmail.setError("Enter your email");
            return false;
        }
        if (!txtSignUpEmail.getText().toString().trim().matches(emailPattern)) {
            txtSignUpEmail.setError("Enter a valid email");
            return false;
        }

        if (txtSignUpPassword.getText().toString().length() <= 6) {
            txtSignUpPassword.setError("Enter a valid password");
            return false;
        }
        return true;
    }*/

    private void signUp(String email, String password) {
        //if (validateForm()) {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            //update ui -> go to LoginActivity
                            checkMode();

                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_INVALID_EMAIL":
                                    txtSignUpEmail.setError("Enter a valid email");
                                    break;
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    txtSignUpEmail.setError("Email is already in use");
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    txtSignUpPassword.setError("Weak password");
                                    break;
                                default:
                                    Toast.makeText(SignUpActivity.this, "SignUp Failed", Toast.LENGTH_SHORT).show();

                            }

                        }
                    }
                });
    }
    // }

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
            //TODO:: imageUri Should be uploaded to the storage as the profile image
        }
    }

    private void checkMode() {
        fragmentManager = getSupportFragmentManager();
        ModeSelectionFragment modeSelectionFragment = new ModeSelectionFragment();
        modeSelectionFragment.show(fragmentManager, "ModeSelectionFragment");

    }

    private void verifyAccount() {
        FirebaseUser user = auth.getCurrentUser();
        uid = user.getUid();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignUpActivity.this, "Verification email sent, it may be within your drafts", Toast.LENGTH_LONG).show();

                                }
                            }, 3000);
                    }
                });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onModeSelectionClose(DialogInterface dialogInterface) {
        Toast.makeText(SignUpActivity.this, "Sign up Succeeded", Toast.LENGTH_SHORT).show();
        verifyAccount();
        startLoginActivity();
    }

    @Override
    public void sendUserData(String parentEmail, boolean isChild) {
        this.isChild = isChild;
        this.parentEmail = parentEmail;
        addUserToDB();
    }

    private void addUserToDB() {

        String email = txtSignUpEmail.getText().toString();
        String password = txtSignUpPassword.getText().toString();
        String name = txtSignUpName.getText().toString();
        String parentEmail = this.parentEmail;
        boolean isChild = this.isChild;


        User user = new User(name, email, password, parentEmail, isChild);
        if (isChild)
            databaseReference.child("kids").child(uid).setValue(user);
        else
            databaseReference.child("parents").child(uid).setValue(user);

        //TODO:: add  an image
    }
}
