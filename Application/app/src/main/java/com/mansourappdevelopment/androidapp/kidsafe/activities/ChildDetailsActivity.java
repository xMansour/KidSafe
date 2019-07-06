package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.ActivityLogFragment;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.AppsFragment;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.LocationFragment;
import com.mansourappdevelopment.androidapp.kidsafe.models.App;

import java.util.ArrayList;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.APPS_EXTRA;
import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_NAME_EXTRA;

public class ChildDetailsActivity extends AppCompatActivity {
	private static final String TAG = "ChildDetailsTAG";
	private ArrayList<App> apps;
	private ImageButton btnBack;
	private ImageButton btnSettings;
	private TextView txtTitle;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_details);
		
		btnBack = findViewById(R.id.btnBack);
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
				Intent intent = new Intent(ChildDetailsActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
		txtTitle = findViewById(R.id.txtTitle);
		
		Intent intent = getIntent();
		String childName = intent.getStringExtra(CHILD_NAME_EXTRA);
		//final String childEmail = intent.getStringExtra(CHILD_EMAIL_EXTRA);
		apps = intent.getParcelableArrayListExtra(APPS_EXTRA);
        /*for (App app : apps) {
            Log.i(TAG, "onItemClick: appName: " + app.getAppName() + " " + "packageName" + app.getPackageName());

        }*/
		
		//setTitle(childName + "'s device");
		String title = childName + getString(R.string.upper_dot_s) + " " + getString(R.string.device);
		txtTitle.setText(title);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AppsFragment()).commit();
		
		BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
		bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
				Fragment selectedFragment = null;
				
				Bundle bundle = new Bundle();
				
				switch (menuItem.getItemId()) {
					case R.id.navApps:
						selectedFragment = new AppsFragment();
						//bundle.putParcelableArrayList(APPS_EXTRA, apps);  //not needed since we're sending it from
						//selectedFragment.setArguments(bundle);            //the ParentSignedInActivity
						break;
					case R.id.navLocation:
						selectedFragment = new LocationFragment();
						//bundle.putString(CHILD_EMAIL_EXTRA, childEmail);
						//selectedFragment.setArguments(bundle);
						break;
					case R.id.navActivityLog:
						selectedFragment = new ActivityLogFragment();
						break;
					
				}
				
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
				return true;
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, ParentSignedInActivity.class));
	}
}
