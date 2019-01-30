package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.RecoverPasswordFragment;

public class LoginActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 29;
    public static final String TAG = "TAG";
    private EditText txtLogInEmail;
    private EditText txtLogInPassword;
    private Button btnLogin;
    private Button btnEmailSignUp;
    private Button btnGoogleSignUp;
    private Button btnFacebookSignUp;
    private TextView txtForgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FragmentManager fragmentManager;
    private String uid;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        txtLogInEmail = (EditText) findViewById(R.id.txtLogInEmail);
        txtLogInPassword = (EditText) findViewById(R.id.txtLogInPassword);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.GONE);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnEmailSignUp = (Button) findViewById(R.id.btnSignUpEmail);
        btnGoogleSignUp = (Button) findViewById(R.id.btnSignUpGoogle);
        btnFacebookSignUp = (Button) findViewById(R.id.btnSignUpFacebook);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtLogInEmail.getText().toString();
                String password = txtLogInPassword.getText().toString();
                logIn(email, password);
            }
        });

        btnEmailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUpActivity();
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
            }
        });

        btnGoogleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //update ui -> go to the SignedIn Activity
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            checkMode(email);
        }
    }
/*    private boolean validateForm() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (txtLogInEmail.getText().toString().equals("")) {
            txtLogInEmail.setError("Enter your email");
            return false;
        }
        if (!txtLogInEmail.getText().toString().trim().matches(emailPattern)) {
            txtLogInEmail.setError("Enter a valid email");
            return false;
        }

        if (txtLogInPassword.getText().toString().length() <= 6) {
            txtLogInPassword.setError("Enter a valid password");
            return false;
        }
        return true;
    }*/

    private void logIn(String email, String password) {
        //if (validateForm()) {
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String email = user.getEmail();
                            Toast.makeText(LoginActivity.this, "Authentication Succeeded", Toast.LENGTH_SHORT).show();
                            //update ui -> go to signedIn activity, but verify which mode first
                            checkMode(email);
                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_INVALID_EMAIL":
                                    txtLogInEmail.setError("Enter a valid email");
                                    break;
                                case "ERROR_USER_NOT_FOUND":
                                    txtLogInEmail.setError("Email isn't registered");
                                    break;
                                case "ERROR_WRONG_PASSWORD":
                                    txtLogInPassword.setError("Wrong password");
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    //   }

    private void recoverPassword() {
        fragmentManager = getSupportFragmentManager();
        RecoverPasswordFragment recoverPasswordFragment = new RecoverPasswordFragment();
        recoverPasswordFragment.show(fragmentManager, "recoverPasswordFragment");
    }

    private void startParentSignedInActivity() {
        Intent intent = new Intent(this, ParentSignedInActivity.class);
        startActivity(intent);
    }

    private void startChildSignedInActivity() {
        Intent intent = new Intent(this, ChildSignedInActivity.class);
        startActivity(intent);
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    //lag at the login screen until verifying
    private void checkMode(String email) {
        Query query = databaseReference.child("parents").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    startParentSignedInActivity();
                else
                    startChildSignedInActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signInWithGoogle() {
        //TODO:: finish after adding the dependency
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Authentication Succeeded");
                            Toast.makeText(LoginActivity.this, "Authentication Succeeded", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();

                        }
                    }
                });
    }
}
