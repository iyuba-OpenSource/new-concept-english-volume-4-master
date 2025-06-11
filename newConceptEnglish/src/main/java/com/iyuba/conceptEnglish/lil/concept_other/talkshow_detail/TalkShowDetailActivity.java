package com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.BaseActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

/**
 * @title: 配音详情页
 * @date: 2023/6/5 10:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TalkShowDetailActivity extends BaseActivity {

    private static final String key_voaId = "voaId";

    public static void start(Context context,String voaId){
        Intent intent = new Intent();
        intent.setClass(context,TalkShowDetailActivity.class);
        intent.putExtra(key_voaId,voaId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_container;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        String voaId = getIntent().getStringExtra(key_voaId);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        TalkShowDetailFragment detailFragment = TalkShowDetailFragment.getInstance(voaId);
        transaction.add(R.id.frameLayout,detailFragment).show(detailFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void loadData() {

    }
}
