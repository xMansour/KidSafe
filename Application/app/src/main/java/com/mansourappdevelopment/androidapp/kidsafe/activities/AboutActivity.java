package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mansourappdevelopment.androidapp.kidsafe.R;

public class AboutActivity extends AppCompatActivity {
	private ImageButton btnBack;
	private ImageButton btnSettings;
	private TextView txtTitle;
	private FrameLayout toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
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
		btnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
		txtTitle = findViewById(R.id.txtTitle);
		txtTitle.setText(getString(R.string.about));
	}
}
