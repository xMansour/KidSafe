package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.InformationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.LoadingDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.RecoverPasswordDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPasswordResetListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.LocaleUtils;
import com.mansourappdevelopment.androidapp.kidsafe.utils.SharedPrefsUtils;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Validators;

public class LoginActivity extends AppCompatActivity implements OnPasswordResetListener {
	private static final String TAG = "LoginActivityTAG";
	private EditText txtLogInEmail;
	private EditText txtLogInPassword;
	private Button btnLogin;
	private TextView txtSignUp;
	private Button btnGoogleSignUp;
	private TextView txtForgotPassword;
	private CheckBox checkBoxRememberMe;
	private ProgressBar progressBar;
	private FirebaseAuth auth;
	private FragmentManager fragmentManager;
	private String uid;
	private FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReference;
	private String emailPrefs;
	private String passwordPrefs;
	private boolean autoLoginPrefs;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		fragmentManager = getSupportFragmentManager();
		LocaleUtils.setAppLanguage(this);
		
		
		//FirebaseApp.initializeApp(this);
		auth = FirebaseAuth.getInstance();
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("users");
		
		
		txtLogInEmail = findViewById(R.id.txtLogInEmail);
		txtLogInPassword = findViewById(R.id.txtLogInPassword);
		txtForgotPassword = findViewById(R.id.txtForgotPassword);
		progressBar = findViewById(R.id.progressBar);
		
		checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
		//progressBar.setVisibility(View.GONE);
		
		btnLogin = findViewById(R.id.btnLogin);
		txtSignUp = findViewById(R.id.txtSignUp);
		btnGoogleSignUp = findViewById(R.id.btnSignUpGoogle);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				autoLogin();
				String email = txtLogInEmail.getText().toString().toLowerCase();
				String password = txtLogInPassword.getText().toString();
				login(email, password);
			}
		});
		
		txtSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startModeSelectionActivity();
			}
		});
		
		txtForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendPasswordRecoveryEmail();
			}
		});
		
		btnGoogleSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signInWithGoogle();
			}
		});
		
		autoLoginPrefs = SharedPrefsUtils.getBooleanPreference(this, Constant.AUTO_LOGIN, false);
		checkBoxRememberMe.setChecked(autoLoginPrefs);
		
		emailPrefs = SharedPrefsUtils.getStringPreference(this, Constant.EMAIL, "");
		passwordPrefs = SharedPrefsUtils.getStringPreference(this, Constant.PASSWORD, "");
		if (autoLoginPrefs) {
			txtLogInEmail.setText(emailPrefs);
			txtLogInPassword.setText(passwordPrefs);
		}
		
		if (!Validators.isGooglePlayServicesAvailable(this)) {
			startInformationDialogFragment(getString(R.string.please_download_google_play_services));
			//Toast.makeText(this, getString(R.string.please_download_google_play_services), Toast.LENGTH_SHORT).show();
			btnLogin.setEnabled(false);
			btnLogin.setClickable(false);
			btnGoogleSignUp.setClickable(false);
			btnGoogleSignUp.setClickable(false);
			txtSignUp.setEnabled(false);
			txtSignUp.setClickable(false);
			txtForgotPassword.setEnabled(false);
			txtForgotPassword.setClickable(false);
			checkBoxRememberMe.setEnabled(false);
			checkBoxRememberMe.setClickable(false);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (autoLoginPrefs) {
			if (Validators.isInternetAvailable(this)) {
				FirebaseUser user = auth.getCurrentUser();
				if (user != null) {
					String email = user.getEmail();
					checkMode(email);
				}
			} else
				startInformationDialogFragment(getResources().getString(R.string.you_re_offline_ncheck_your_connection_and_try_again));
		}
	}
	
	
	private void autoLogin() {
		SharedPrefsUtils.setBooleanPreference(this, Constant.AUTO_LOGIN, checkBoxRememberMe.isChecked());
		SharedPrefsUtils.setStringPreference(this, Constant.EMAIL, txtLogInEmail.getText().toString().toLowerCase());
		SharedPrefsUtils.setStringPreference(this, Constant.PASSWORD, txtLogInPassword.getText().toString());
		
	}
	
	private void login(String email, String password) {
		if (isValid()) {
			final LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
			startLoadingFragment(loadingDialogFragment);
			auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					stopLoadingFragment(loadingDialogFragment);
					if (task.isSuccessful()) {
						FirebaseUser user = auth.getCurrentUser();
						String email = user.getEmail();
						uid = user.getUid();
						Log.i(TAG, "onComplete: user: " + user.toString());
						Log.i(TAG, "onComplete: email: " + email);
						Log.i(TAG, "onComplete: uid: " + uid);
						//String email = txtLogInEmail.getText().toString();
						if (Validators.isVerified(user)) checkMode(email);
						else startAccountVerificationActivity();
					} else {
						String errorCode = null;
						try {
							errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
						} catch (ClassCastException e) {
							e.printStackTrace();
						}
						switch (errorCode) {
							case "ERROR_INVALID_EMAIL":
								txtLogInEmail.setError(getString(R.string.enter_valid_email));
								break;
							case "ERROR_USER_NOT_FOUND":
								txtLogInEmail.setError(getString(R.string.email_isnt_registered));
								break;
							case "ERROR_WRONG_PASSWORD":
								txtLogInPassword.setError(getString(R.string.wrong_password));
								break;
							default:
								Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		}
	}
	
	private boolean isValid() {
		if (!Validators.isValidEmail(txtLogInEmail.getText().toString())) {
			txtLogInEmail.setError(getString(R.string.enter_valid_email));
			txtLogInEmail.requestFocus();
			return false;
		}
		
		if (!Validators.isValidPassword(txtLogInPassword.getText().toString())) {
			txtLogInPassword.setError(getString(R.string.enter_valid_email));
			txtLogInPassword.requestFocus();
			return false;
		}
		
		if (!Validators.isInternetAvailable(this)) {
			startInformationDialogFragment(getResources().getString(R.string.you_re_offline_ncheck_your_connection_and_try_again));
			return false;
		}
		
		return true;
	}
	
	private void startInformationDialogFragment(String message) {
		InformationDialogFragment informationDialogFragment = new InformationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.INFORMATION_MESSAGE, message);
		informationDialogFragment.setArguments(bundle);
		informationDialogFragment.setCancelable(false);
		informationDialogFragment.show(getSupportFragmentManager(), Constant.INFORMATION_DIALOG_FRAGMENT_TAG);
	}
	
	private void startLoadingFragment(LoadingDialogFragment loadingDialogFragment) {
		loadingDialogFragment.setCancelable(false);
		loadingDialogFragment.show(fragmentManager, Constant.LOADING_FRAGMENT);
	}
	
	private void stopLoadingFragment(LoadingDialogFragment loadingDialogFragment) {
		loadingDialogFragment.dismiss();
	}
	
	private void checkMode(String email) {
		final LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
		startLoadingFragment(loadingDialogFragment);
		Query query = databaseReference.child("parents").orderByChild("email").equalTo(email);
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				loadingDialogFragment.dismiss();
				if (dataSnapshot.exists()) {
					startParentSignedInActivity();
				} else {
					startChildSignedInActivity();
				}
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.i(TAG, "onCancelled: canceled");
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
	
	private void startAccountVerificationActivity() {
		Intent intent = new Intent(this, AccountVerificationActivity.class);
		startActivity(intent);
	}
	
	private void startModeSelectionActivity() {
		Intent intent = new Intent(this, ModeSelectionActivity.class);
		startActivity(intent);
        /*Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);*/
	}
	
	private void sendPasswordRecoveryEmail() {
		RecoverPasswordDialogFragment recoverPasswordDialogFragment = new RecoverPasswordDialogFragment();
		recoverPasswordDialogFragment.setCancelable(false);
		recoverPasswordDialogFragment.show(fragmentManager, Constant.RECOVER_PASSWORD_FRAGMENT);
	}
	
	private void signInWithGoogle() {
		if (Validators.isInternetAvailable(this)) {
			GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.id)).requestEmail().build();
			GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
			Intent signInIntent = googleSignInClient.getSignInIntent();
			startActivityForResult(signInIntent, Constant.RC_SIGN_IN);
		} else
			startInformationDialogFragment(getResources().getString(R.string.you_re_offline_ncheck_your_connection_and_try_again));
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
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
					Toast.makeText(LoginActivity.this, getString(R.string.authentication_succeeded), Toast.LENGTH_SHORT).show();
					FirebaseUser user = auth.getCurrentUser();
					checkMode(user.getEmail());
					
				}
			}
		});
	}
	
	@Override
	public void onOkClicked(String email) {
		sendPasswordRecoveryEmail(email);
	}
	
	@Override
	public void onCancelClicked() {
		//Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show();
	}
	
	private void sendPasswordRecoveryEmail(String email) {
		auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					Toast.makeText(LoginActivity.this, getString(R.string.password_reset_email_sent), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}
}
