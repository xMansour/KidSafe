package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.AccountDeleteDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.ConfirmationDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.LanguageSelectionDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.dialogfragments.PasswordChangingDialogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnConfirmationListener;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnDeleteAccountListener;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnLanguageSelectionListener;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPasswordChangeListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.AccountUtils;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.LocaleUtils;
import com.mansourappdevelopment.androidapp.kidsafe.utils.SharedPrefsUtils;

public class SettingsActivity extends AppCompatActivity implements OnLanguageSelectionListener, OnConfirmationListener, OnPasswordChangeListener, OnDeleteAccountListener {
	private Button btnLanguageSelection;
	private Button btnLogout;
	private Button btnChangePassword;
	private Button btnDeleteAccount;
	private Button btnAbout;
	//private Button btnRateUs;   //Won't be uploaded to the play store duo to violation of privacy
	private Button btnSendFeedBack;
	private Button btnVisitWebsite;
	private ImageButton btnBack;
	private ImageButton btnSettings;
	private TextView txtTitle;
	private FrameLayout toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		toolbar = findViewById(R.id.toolbar);
		btnBack = findViewById(R.id.btnBack);
		btnBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		btnSettings = findViewById(R.id.btnSettings);
		btnSettings.setVisibility(View.GONE);
		txtTitle = findViewById(R.id.txtTitle);
		txtTitle.setText(getString(R.string.settings));
		
		btnLanguageSelection = findViewById(R.id.btnLanguageSelection);
		btnLanguageSelection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectLanguage();
			}
		});
		
		btnLogout = findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				logout();
			}
		});
		
		
		btnChangePassword = findViewById(R.id.btnChangePassword);
		btnChangePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				changePassword();
			}
		});
		
		
		btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
		btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				deleteAccount();
			}
		});
		
		
		btnAbout = findViewById(R.id.btnAbout);
		btnAbout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showAbout();
			}
		});
		
		
		/*btnRateUs = findViewById(R.id.btnRateUs);
		btnRateUs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				rateTheApp();
			}
		});*/
		
		
		btnSendFeedBack = findViewById(R.id.btnSendFeedBack);
		btnSendFeedBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SendFeedBack();
			}
		});
		
		
		btnVisitWebsite = findViewById(R.id.btnVisitWebsite);
		btnVisitWebsite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				visitWebsite();
			}
		});
		
	}
	
	private void showAbout() {
		startActivity(new Intent(this, AboutActivity.class));
	}
	
	/*private void rateTheApp() {
		Toast.makeText(this, "rateTheApp", Toast.LENGTH_SHORT).show();
		
	}*/
	
	private void SendFeedBack() {
		String body = null;
		try {
			body = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " + Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND + "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"MansourAppDevelopment@gmail.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "KidSafe Feedback");
		intent.putExtra(Intent.EXTRA_TEXT, body);
		startActivity(Intent.createChooser(intent, getString(R.string.choose_email_client)));
	}
	
	private void visitWebsite() {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://xmansour.github.io/KidSafe"));
		startActivity(intent);
	}
	
	private void deleteAccount() {
		AccountDeleteDialogFragment accountDeleteDialogFragment = new AccountDeleteDialogFragment();
		accountDeleteDialogFragment.setCancelable(false);
		accountDeleteDialogFragment.show(getSupportFragmentManager(), Constant.ACCOUNT_DELETE_DIALOG_FRAGMENT_TAG);
	}
	
	private void changePassword() {
		PasswordChangingDialogFragment passwordChangingDialogFragment = new PasswordChangingDialogFragment();
		passwordChangingDialogFragment.setCancelable(false);
		passwordChangingDialogFragment.show(getSupportFragmentManager(), Constant.PASSWORD_CHANGING_DIALOG_FRAGMENT_TAG);
	}
	
	private void logout() {
		ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.CONFIRMATION_MESSAGE, getString(R.string.do_you_want_to_logout));
		confirmationDialogFragment.setArguments(bundle);
		confirmationDialogFragment.setCancelable(false);
		confirmationDialogFragment.show(getSupportFragmentManager(), Constant.CONFIRMATION_FRAGMENT_TAG);
	}
	
	private void selectLanguage() {
		LanguageSelectionDialogFragment languageSelectionDialogFragment = new LanguageSelectionDialogFragment();
		languageSelectionDialogFragment.setCancelable(false);
		languageSelectionDialogFragment.show(getSupportFragmentManager(), Constant.LANGUAGE_SELECTION_DIALOG_FRAGMENT_TAG);
	}
	
	@Override
	public void onLanguageSelection(String language) {
		String appLanguage = SharedPrefsUtils.getStringPreference(this, Constant.APP_LANGUAGE, "en");
		if (language.equals("English") && !appLanguage.equals("en")) {
			LocaleUtils.setLocale(this, "en");
		} else if (language.equals("Arabic") && !appLanguage.equals("ar")) {
			LocaleUtils.setLocale(this, "ar");
			
		}
		
		restartApp();
		
	}
	
	private void restartApp() {
		Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
	}
	
	@Override
	public void onConfirm() {
		AccountUtils.logout(this);
	}
	
	@Override
	public void onConfirmationCancel() {
		//DO NOTHING
	}
	
	@Override
	public void onPasswordChange(String newPassword) {
		AccountUtils.changePassword(this, newPassword);
		
	}
	
	@Override
	public void onDeleteAccount(String password) {
		AccountUtils.deleteAccount(this, password);
		
	}
}
