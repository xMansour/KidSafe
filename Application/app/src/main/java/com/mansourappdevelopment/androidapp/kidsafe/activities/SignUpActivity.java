package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.ConfirmationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.GoogleChildSignUpDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.InformationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.LoadingDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnConfirmationListener;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnGoogleChildSignUp;
import com.mansourappdevelopment.androidapp.kidsafe.models.Child;
import com.mansourappdevelopment.androidapp.kidsafe.models.Parent;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Validators;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignUpActivity extends AppCompatActivity implements OnConfirmationListener, OnGoogleChildSignUp {
	private static final String TAG = "SingUpActivityTAG";
	private FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReference;
	private FirebaseStorage firebaseStorage;
	private StorageReference storageReference;
	private Uri imageUri;
	private FirebaseAuth auth;
	private EditText txtSignUpEmail;
	private EditText txtParentEmail;
	private EditText txtSignUpPassword;
	private EditText txtSignUpName;
	private Button btnSignUp;
	private Button btnSignUpWithGoogle;
	private CircleImageView imgProfile;
	private FragmentManager fragmentManager;
	private String uid;
	private boolean googleAuth = false;
	private boolean parent = true;
	private boolean validParent = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		fragmentManager = getSupportFragmentManager();
		
		Intent intent = getIntent();
		parent = intent.getBooleanExtra(Constant.PARENT_SIGN_UP, true);
		
		auth = FirebaseAuth.getInstance();
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("users");
		firebaseStorage = FirebaseStorage.getInstance();
		storageReference = firebaseStorage.getReference("profileImages");
		
		txtSignUpEmail = findViewById(R.id.txtSignUpEmail);
		txtParentEmail = findViewById(R.id.txtParentEmail);
		txtParentEmail.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				Query query = databaseReference.child("parents").orderByChild("email").equalTo(txtParentEmail.getText().toString().toLowerCase());
				query.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						validParent = dataSnapshot.exists();
						Log.i(TAG, "onDataChange: " + validParent);
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
				});
			}
		});
		if (!parent) {
			txtParentEmail.setVisibility(View.VISIBLE);
		}
		
		txtSignUpPassword = findViewById(R.id.txtSignUpPassword);
		txtSignUpName = findViewById(R.id.txtSignUpName);
		
		imgProfile = findViewById(R.id.imgProfile);
		imgProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openFileChooser();
			}
		});
		btnSignUp = findViewById(R.id.btnSignUp);
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = txtSignUpEmail.getText().toString().toLowerCase();
				String password = txtSignUpPassword.getText().toString();
				signUp(email, password);
			}
		});
		
		btnSignUpWithGoogle = findViewById(R.id.btnSignUpWithGoogle);
		btnSignUpWithGoogle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signInWithGoogle();
			}
		});
		
	}
	
	
	private void signUp(String email, String password) {
		if (isValid()) {
			final LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
			startLoadingFragment(loadingDialogFragment);
			auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					stopLoadingFragment(loadingDialogFragment);
					if (task.isSuccessful()) {
						signUpRoutine(txtParentEmail.getText().toString().toLowerCase());
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
	
	private void signUpRoutine(String parentEmail) {
		uid = auth.getCurrentUser().getUid();
		Log.i(TAG, "signUpRoutine: UID: " + uid);
		addUserToDB(parentEmail, parent);
		uploadProfileImage(parent);
		startAccountVerificationActivity();
	}
	
	private void startAccountVerificationActivity() {
		Intent intent = new Intent(this, AccountVerificationActivity.class);
		startActivity(intent);
	}
	
	private void uploadProfileImage(final boolean parent) {
		if (googleAuth && imageUri == null) {
			imageUri = auth.getCurrentUser().getPhotoUrl();
			if (parent)
				databaseReference.child("parents").child(uid).child("profileImage").setValue(imageUri.toString());
			else
				databaseReference.child("childs").child(uid).child("profileImage").setValue(imageUri.toString());
			
		} else if (!googleAuth) {
			final StorageReference profileImageStorageReference = storageReference.child(uid + "_profileImage");
			profileImageStorageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					profileImageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
						@Override
						public void onSuccess(Uri uri) {
							if (uri != null) {
								if (parent)
									databaseReference.child("parents").child(uid).child("profileImage").setValue(uri.toString());
								else
									databaseReference.child("childs").child(uid).child("profileImage").setValue(uri.toString());
							}
							Toast.makeText(SignUpActivity.this, getString(R.string.image_uploaded_successfully), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					Toast.makeText(SignUpActivity.this, getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	private void addUserToDB(String parentEmail, boolean parent) {
		String email;
		String name;
		if (googleAuth) {
			email = auth.getCurrentUser().getEmail();
			name = auth.getCurrentUser().getDisplayName();
		} else {
			email = txtSignUpEmail.getText().toString().toLowerCase();
			name = txtSignUpName.getText().toString().replaceAll("\\s+$", "");
		}
		Log.i(TAG, "signUpRoutine: UID: " + uid);
		
		if (parent) {
			Parent p = new Parent(name, email);
			databaseReference.child("parents").child(uid).setValue(p);
		} else {
			Child c = new Child(name, email, parentEmail);
			databaseReference.child("childs").child(uid).setValue(c);
		}
	}
	
	private void startLoadingFragment(LoadingDialogFragment loadingDialogFragment) {
		loadingDialogFragment.setCancelable(false);
		loadingDialogFragment.show(fragmentManager, Constant.LOADING_FRAGMENT);
	}
	
	private void stopLoadingFragment(LoadingDialogFragment loadingDialogFragment) {
		loadingDialogFragment.dismiss();
	}
	
	private boolean isValid() {
		if (!Validators.isValidName(txtSignUpName.getText().toString())) {
			txtSignUpName.setError(getString(R.string.name_validation));
			txtSignUpName.requestFocus();
			return false;
		}
		
		if (!Validators.isValidEmail(txtSignUpEmail.getText().toString())) {
			txtSignUpEmail.setError(getString(R.string.enter_valid_email));
			txtSignUpEmail.requestFocus();
			return false;
		}
		
		if (!parent) {
			if (!Validators.isValidEmail(txtParentEmail.getText().toString().toLowerCase()) || !validParent) {
				txtParentEmail.setError(getString(R.string.this_email_isnt_registered_as_parent));
				txtParentEmail.requestFocus();
				return false;
			}
		}
		
		if (!Validators.isValidPassword(txtSignUpPassword.getText().toString())) {
			txtSignUpPassword.setError(getString(R.string.enter_valid_password));
			txtSignUpPassword.requestFocus();
			return false;
		}
		
		
		if (!Validators.isValidImageURI(imageUri)) {
			ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.CONFIRMATION_MESSAGE, getString(R.string.would_you_love_to_add_a_profile_image));
			confirmationDialogFragment.setArguments(bundle);
			confirmationDialogFragment.setCancelable(false);
			confirmationDialogFragment.show(fragmentManager, Constant.CONFIRMATION_FRAGMENT_TAG);
			return false;
		}
		
		if (!Validators.isInternetAvailable(this)) {
			startInformationDialogFragment();
			return false;
		}
		
		return true;
	}
	
	private void startInformationDialogFragment() {
		InformationDialogFragment informationDialogFragment = new InformationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.INFORMATION_MESSAGE, getResources().getString(R.string.you_re_offline_ncheck_your_connection_and_try_again));
		informationDialogFragment.setArguments(bundle);
		informationDialogFragment.setCancelable(false);
		informationDialogFragment.show(getSupportFragmentManager(), Constant.INFORMATION_DIALOG_FRAGMENT_TAG);
	}
	
	private void openFileChooser() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, Constant.PICK_IMAGE_REQUEST);
	}
	
	private void signInWithGoogle() {
		if (Validators.isInternetAvailable(this)) {
			GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.id)).requestEmail().build();
			GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
			Intent signInIntent = googleSignInClient.getSignInIntent();
			startActivityForResult(signInIntent, Constant.RC_SIGN_IN);
		} else startInformationDialogFragment();
		
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
		auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					Log.i(TAG, "onComplete: Authentication Succeeded");
					//Toast.makeText(SignUpActivity.this, getString(R.string.authentication_succeeded), Toast.LENGTH_SHORT).show();
					FirebaseUser user = auth.getCurrentUser();
					googleAuth = true;
					getParentEmail();
					
				}
				
			}
		});
	}
	
	private void getParentEmail() {
		GoogleChildSignUpDialogFragment googleChildSignUpDialogFragment = new GoogleChildSignUpDialogFragment();
		googleChildSignUpDialogFragment.setCancelable(false);
		googleChildSignUpDialogFragment.show(fragmentManager, Constant.GOOGLE_CHILD_SIGN_UP);
	}
	
	@Override
	public void onConfirm() {
		imgProfile.requestFocus();
		Toast.makeText(this, getString(R.string.please_add_a_profile_image), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onConfirmationCancel() {
		imageUri = Uri.parse("android.resource://com.mansourappdevelopment.androidapp.kidsafe/drawable/ic_default_avatar");
		signUp(txtSignUpEmail.getText().toString().toLowerCase(), txtSignUpPassword.toString());
		//TODO:: default image here
		Log.i(TAG, "onConfirmationCancel: DONE");
	}
	
	@Override
	public void onModeSelected(String parentEmail) {
		signUpRoutine(parentEmail);
	}
	
}
