package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.InformationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.PasswordValidationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.PermissionExplanationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPasswordValidationListener;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPermissionExplanationListener;
import com.mansourappdevelopment.androidapp.kidsafe.services.MainForegroundService;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.SharedPrefsUtils;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Validators;

public class ChildSignedInActivity extends AppCompatActivity implements OnPermissionExplanationListener, OnPasswordValidationListener {
	public static final int JOB_ID = 38;
	public static final String CHILD_EMAIL = "childEmail";
	private static final String TAG = "ChildSignedInTAG";
	private FirebaseAuth auth;
	private FirebaseUser user;
	private ImageButton btnBack;
	private ImageButton btnSettings;
	private TextView txtTitle;
	private FrameLayout toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_signed_in);
		
		boolean childFirstLaunch = SharedPrefsUtils.getBooleanPreference(this, Constant.CHILD_FIRST_LAUNCH, true);
		if (childFirstLaunch) startActivity(new Intent(this, PermissionsActivity.class));
		else {
			
			auth = FirebaseAuth.getInstance();
			user = auth.getCurrentUser();
			
			String email = user.getEmail();
            /*PersistableBundle bundle = new PersistableBundle();
            bundle.putString(CHILD_EMAIL, email);*/
			
			toolbar = findViewById(R.id.toolbar);
			btnBack = findViewById(R.id.btnBack);
			btnBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_));
			btnSettings = findViewById(R.id.btnSettings);
			btnSettings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startPasswordValidationDialogFragment();
				}
			});
			txtTitle = findViewById(R.id.txtTitle);
			txtTitle.setText(getString(R.string.home));
			
			//schedualJob(bundle);
			startMainForegroundService(email);
			
			if (!Validators.isLocationOn(this)) startPermissionExplanationDialogFragment();
			
			if (!Validators.isInternetAvailable(this))
				startInformationDialogFragment(getResources().getString(R.string.you_re_offline_ncheck_your_connection_and_try_again));
			
		}
	}
	
	private void startMainForegroundService(String email) {
		Intent intent = new Intent(this, MainForegroundService.class);
		intent.putExtra(CHILD_EMAIL, email);
		ContextCompat.startForegroundService(this, intent);
		
	}
	
	private void startPermissionExplanationDialogFragment() {
		PermissionExplanationDialogFragment permissionExplanationDialogFragment = new PermissionExplanationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.PERMISSION_REQUEST_CODE, Constant.CHILD_LOCATION_PERMISSION_REQUEST_CODE);
		permissionExplanationDialogFragment.setArguments(bundle);
		permissionExplanationDialogFragment.setCancelable(false);
		permissionExplanationDialogFragment.show(getSupportFragmentManager(), Constant.PERMISSION_EXPLANATION_FRAGMENT_TAG);
	}
	
	private void startInformationDialogFragment(String message) {
		InformationDialogFragment informationDialogFragment = new InformationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.INFORMATION_MESSAGE, message);
		informationDialogFragment.setArguments(bundle);
		informationDialogFragment.setCancelable(false);
		informationDialogFragment.show(getSupportFragmentManager(), Constant.INFORMATION_DIALOG_FRAGMENT_TAG);
	}
	
	private void startPasswordValidationDialogFragment() {
		PasswordValidationDialogFragment passwordValidationDialogFragment = new PasswordValidationDialogFragment();
		passwordValidationDialogFragment.setCancelable(false);
		passwordValidationDialogFragment.show(getSupportFragmentManager(), Constant.PASSWORD_VALIDATION_DIALOG_FRAGMENT_TAG);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.DEVICE_ADMIN_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.i(TAG, "onActivityResult: DONE");
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	public void onOk(int requestCode) {
		startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}
	
	@Override
	public void onCancel(int switchId) {
		Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	public void onValidationOk() {
		Intent intent = new Intent(ChildSignedInActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void schedualJob(PersistableBundle bundle) {
        ComponentName componentName = new ComponentName(this, UploadAppsService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .setExtras(bundle)
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            //Success
        } else {
            //Failure
        }
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void cancelJob() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
        //Job cancelled
    }*/
	
	
}
