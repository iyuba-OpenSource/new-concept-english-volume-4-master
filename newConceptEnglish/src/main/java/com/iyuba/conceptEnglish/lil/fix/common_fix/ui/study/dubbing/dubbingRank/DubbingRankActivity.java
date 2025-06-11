package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

/**
 * @title: 中小学配音的排行榜
 * @date: 2023/6/13 15:15
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingRankActivity extends BaseViewBindingActivity<LayoutContainerBinding>{

    public static void start(Context context,String bookType,String voaId){
        Intent intent = new Intent();
        intent.setClass(context,DubbingRankActivity.class);
        intent.putExtra(StrLibrary.types,bookType);
        intent.putExtra(StrLibrary.voaId,voaId);
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
        String types = getIntent().getStringExtra(StrLibrary.types);
        String voaId = getIntent().getStringExtra(StrLibrary.voaId);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DubbingRankFragment rankFragment = DubbingRankFragment.getInstance(types,voaId);
        transaction.add(R.id.container,rankFragment).show(rankFragment);
        transaction.commitAllowingStateLoss();
    }
}
