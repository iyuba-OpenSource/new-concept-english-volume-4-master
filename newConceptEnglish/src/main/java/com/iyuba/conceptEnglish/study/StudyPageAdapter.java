package com.iyuba.conceptEnglish.study;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class StudyPageAdapter extends FragmentStateAdapter {

    private List<Fragment> fragmentList;

    public StudyPageAdapter(@NonNull FragmentActivity fragmentActivity,List<Fragment> list) {
        super(fragmentActivity);
        this.fragmentList = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getItemCount() {
        return fragmentList==null?0:fragmentList.size();
    }
}
