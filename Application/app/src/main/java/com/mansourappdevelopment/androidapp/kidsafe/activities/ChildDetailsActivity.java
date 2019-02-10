package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.AppsFragment;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.LocationFragment;
import com.mansourappdevelopment.androidapp.kidsafe.fragments.StatsFragment;

public class ChildDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AppsFragment()).commit();
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.navApps:
                        selectedFragment = new AppsFragment();
                        break;
                    case R.id.navLocation:
                        selectedFragment = new LocationFragment();
                        break;
                    case R.id.navStats:
                        selectedFragment = new StatsFragment();
                        break;

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                return true;
            }
        });
    }
}
