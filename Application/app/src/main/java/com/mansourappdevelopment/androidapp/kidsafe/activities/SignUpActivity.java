package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.LoadingFragment;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.ModeSelectionFragment;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnModeSelectionListener;
import com.mansourappdevelopment.androidapp.kidsafe.models.User;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.LocaleUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements OnModeSelectionListener {
    private static final String TAG = "SingUpActivityTAG";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri imageUri;
    private FirebaseAuth auth;
    private EditText txtSignUpEmail;
    private EditText txtSignUpPassword;
    private EditText txtSignUpName;
    private Button btnSignUp;
    private Button btnSignUpWithGoogle;
    private CircleImageView imgProfile;
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;
    private String uid;
    private boolean googleAuth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        fragmentManager = getSupportFragmentManager();

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        txtSignUpEmail = (EditText) findViewById(R.id.txtSignUpEmail);
        txtSignUpPassword = (EditText) findViewById(R.id.txtSignUpPassword);
        txtSignUpName = (EditText) findViewById(R.id.txtSignUpName);

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

        btnSignUpWithGoogle = (Button) findViewById(R.id.btnSignUpWithGoogle);
        btnSignUpWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

    }


    private void signUp(String email, String password) {
        if (isValid()) {
            //progressBar.setVisibility(View.VISIBLE);
            final LoadingFragment loadingFragment = new LoadingFragment();
            startLoadingFragment(loadingFragment);
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //progressBar.setVisibility(View.GONE);
                            stopLoadingFragment(loadingFragment);
                            if (task.isSuccessful()) {
                                //update ui -> go to LoginActivity
                                checkMode();

                            } else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        txtSignUpEmail.setError(getString(R.string.enter_valid_email));
                                        break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        txtSignUpEmail.setError(getString(R.string.email_is_already_in_use));
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        txtSignUpPassword.setError(getString(R.string.weak_password));
                                        break;
                                    default:
                                        Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_falied), Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                    });
        }
    }

    private void checkMode() {
        ModeSelectionFragment modeSelectionFragment = new ModeSelectionFragment();
        modeSelectionFragment.setCancelable(false);
        modeSelectionFragment.show(fragmentManager, Constant.MODE_SELECTION_FRAGMENT);

    }


    @Override
    public void onModeSelected(String parentEmail, boolean child) {
        Toast.makeText(this, getString(R.string.sign_up_succeeded), Toast.LENGTH_SHORT).show();
        verifyAccount();
        addUserToDB(parentEmail, child);
        uploadProfileImage(child);
        if (child)
            startChildSignedInActivity();
        else
            startParentSignedInActivity();
    }

    @Override
    public void onDismiss() {
        Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show();
        FirebaseUser user = auth.getCurrentUser();
        AuthCredential credential;
        if (googleAuth) {
            credential = GoogleAuthProvider.getCredential(user.getEmail(), null);
        } else {
            credential = EmailAuthProvider.getCredential(user.getEmail(), txtSignUpPassword.getText().toString());

        }
        user.delete();

    }

    //Delay to not to overwrite the success toast
    private void verifyAccount() {
        FirebaseUser user = auth.getCurrentUser();
        uid = user.getUid();
        //String languageCode = auth.getLanguageCode();
        auth.setLanguageCode(LocaleUtils.getAppLanguage());
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignUpActivity.this, getString(R.string.verification_email_sent_it_may_be_within_your_drafts), Toast.LENGTH_LONG).show();
                                }
                            }, 3000);
                    }
                });
        //auth.setLanguageCode(languageCode);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constant.PICK_IMAGE_REQUEST);
    }

    private void uploadProfileImage(final boolean child) {
        if (googleAuth && imageUri == null) {
            imageUri = auth.getCurrentUser().getPhotoUrl();
            if (child)
                databaseReference.child("childs").child(uid).child("profileImage").setValue(imageUri.toString());
            else
                databaseReference.child("parents").child(uid).child("profileImage").setValue(imageUri.toString());

            Log.i(TAG, "uploadProfileImage: imageUri: " + imageUri);

        } else if (!googleAuth) {
            final StorageReference profileImageStorageReference = storageReference.child(uid).child("profileImage");
            profileImageStorageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileImageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        if (child)
                                            databaseReference.child("childs").child(uid).child("profileImage").setValue(uri.toString());
                                        else
                                            databaseReference.child("parents").child(uid).child("profileImage").setValue(uri.toString());
                                    }
                                    Toast.makeText(SignUpActivity.this, getString(R.string.image_uploaded_successfully), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addUserToDB(String parentEmail, boolean child) {
        String email;
        String name;
        if (googleAuth) {
            email = auth.getCurrentUser().getEmail();
            name = auth.getCurrentUser().getDisplayName();
        } else {
            email = txtSignUpEmail.getText().toString();
            //String password = txtSignUpPassword.getText().toString();
            name = txtSignUpName.getText().toString();

        }
        User user = new User(name, email/*, password*/, parentEmail, child);
        if (child)
            databaseReference.child("childs").child(uid).setValue(user);
        else
            databaseReference.child("parents").child(uid).setValue(user);
    }


    /*private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }*/
    private void signInWithGoogle() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constant.RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }

        if (requestCode == Constant.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SignUpActivity.this, getString(R.string.authentication_succeeded), Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                            googleAuth = true;
                            checkMode();

                        }
                    }
                });
    }

    private void startParentSignedInActivity() {
        Intent intent = new Intent(this, ParentSignedInActivity.class);
        startActivity(intent);
    }

    private void startChildSignedInActivity() {
        Intent intent = new Intent(this, ChildSignedInActivity.class);
        startActivity(intent);
    }

    private void startLoadingFragment(LoadingFragment loadingFragment) {
        loadingFragment.setCancelable(false);
        loadingFragment.show(fragmentManager, Constant.LOADING_FRAGMENT);
    }

    private void stopLoadingFragment(LoadingFragment loadingFragment) {
        loadingFragment.dismiss();
    }

    private boolean isValid() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (txtSignUpName.getText().toString().equals("")) {
            txtSignUpName.setError(getString(R.string.enter_your_name));
            return false;
        }

        if (txtSignUpEmail.getText().toString().equals("")) {
            txtSignUpEmail.setError(getString(R.string.enter_your_email));
            return false;
        }
        if (txtSignUpPassword.getText().toString().equals("")) {
            txtSignUpPassword.setError(getString(R.string.enter_your_password));
            return false;

        }
        if (!txtSignUpEmail.getText().toString().trim().matches(emailPattern)) {
            txtSignUpEmail.setError(getString(R.string.enter_valid_email));
            return false;
        }

        if (txtSignUpPassword.getText().toString().length() < 6) {
            txtSignUpPassword.setError(getString(R.string.enter_valid_password));
            return false;
        }
        return true;
    }
}
