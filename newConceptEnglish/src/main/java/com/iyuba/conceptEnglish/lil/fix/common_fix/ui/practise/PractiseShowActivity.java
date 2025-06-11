package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line.PractiseLineFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.rank.PractiseRankFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

/**
 * 新版练习题的分类界面
 */
public class PractiseShowActivity extends BaseViewBindingActivity<LayoutContainerBinding> {
    public static final String showType_line = "line";//顺序
    public static final String showType_list = "list";//列表
    public static final String showType_note = "note";//错题
    public static final String showType_rank = "rank";//排行

    private String showType;

    public static void start(Context context,String showType){
        Intent intent = new Intent();
        intent.setClass(context, PractiseShowActivity.class);
        intent.putExtra(StrLibrary.type,showType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showType = getIntent().getStringExtra(StrLibrary.type);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        switch (showType){
            case showType_line:
                fragment = PractiseLineFragment.getInstance("concept",1);
                break;
            case showType_list:
                break;
            case showType_rank:
                fragment = PractiseRankFragment.getInstance();
                break;
            case showType_note:
                break;
        }

        transaction.add(R.id.container,fragment).show(fragment).commitNowAllowingStateLoss();
    }
}
