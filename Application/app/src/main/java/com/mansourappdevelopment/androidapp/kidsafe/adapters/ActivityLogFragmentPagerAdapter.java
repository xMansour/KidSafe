package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragmentList = new ArrayList<>();
	private List<String> fragmentTitleList = new ArrayList<>();
	
	public ActivityLogFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public void addFragment(Fragment fragment, String title) {
		fragmentList.add(fragment);
		fragmentTitleList.add(title);
	}
	
	@Override
	public Fragment getItem(int i) {
		return fragmentList.get(i);
	}
	
	@Override
	public int getCount() {
		return fragmentList.size();
	}
	
	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return fragmentTitleList.get(position);
	}
}
