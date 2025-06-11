package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.fragment.CourseNewFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

/**
 * @title: 微课展示界面
 * @date: 2023/7/18 10:13
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class MocShowActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,MocShowActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        CourseNewFragment newFragment = CourseNewFragment.getInstance();
        transaction.add(R.id.container,newFragment).show(newFragment).commitNowAllowingStateLoss();
    }
}
