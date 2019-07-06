package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.utils.LocaleUtils;

public class AccountVerificationActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_verification);
		sendVerificationMessage();
		
		if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
			startActivity(new Intent(this, LoginActivity.class));
		
		final Button btnVerify = findViewById(R.id.btnVerify);
		startCountDownTimer(btnVerify);
		
		btnVerify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendVerificationMessage();
				startCountDownTimer(btnVerify);
				
			}
		});
		
	}
	
	private void startCountDownTimer(final Button btnVerify) {
		btnVerify.setEnabled(false);
		btnVerify.setClickable(false);
		new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long l) {
				btnVerify.setText(String.valueOf(l / 1000));
			}
			
			@Override
			public void onFinish() {
				btnVerify.setEnabled(true);
				btnVerify.setClickable(true);
				btnVerify.setText(R.string.resend_verification_email);
				
			}
		}.start();
	}
	
	private void sendVerificationMessage() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		FirebaseAuth.getInstance().setLanguageCode(LocaleUtils.getAppLanguage());
		user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful())
					Toast.makeText(AccountVerificationActivity.this, getString(R.string.verification_email_sent_it_may_be_within_your_drafts), Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
}
