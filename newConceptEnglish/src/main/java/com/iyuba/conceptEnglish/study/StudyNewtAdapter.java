package com.iyuba.conceptEnglish.study;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class StudyNewtAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;
    private Context mContext;

    public StudyNewtAdapter(FragmentManager fm, List<Fragment> list, Context mContext) {
        super(fm);
        this.list = list;
        this.mContext = mContext;
    }

    public void destroyItem(@NonNull ViewGroup paramViewGroup, int paramInt, @NonNull Object paramObject) {
    }

    public int getCount() {
        return this.list.size();
    }

    public Fragment getItem(int position) {
        return (Fragment) this.list.get(position);
    }

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup paramViewGroup, int paramInt) {
        return super.instantiateItem(paramViewGroup, paramInt);
    }
}
