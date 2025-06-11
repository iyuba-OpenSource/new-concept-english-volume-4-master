package com.iyuba.conceptEnglish.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.iyuba.conceptEnglish.R;

public class TestFragmentAdapter extends FragmentPagerAdapter {
	protected static final int[] CONTENT = new int[] {
			R.raw.help_1,
			R.raw.help_2,
			R.raw.help_3,
			R.raw.help_4,
			R.raw.help_5,
			R.raw.help_6};
	public TestFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return TestFragment.newInstance(CONTENT[position]);
	}

	@Override
	public int getCount() {
		return CONTENT.length;
	}
}

