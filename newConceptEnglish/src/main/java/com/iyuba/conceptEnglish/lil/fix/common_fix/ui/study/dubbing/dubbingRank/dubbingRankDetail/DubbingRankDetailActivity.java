package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank.dubbingRankDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Dubbing_rank;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank.DubbingRankFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

/**
 * @title: 配音排行详情界面
 * @date: 2023/6/13 18:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingRankDetailActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    private String types;
    private String voaId;
    private Dubbing_rank rank;

    public static void start(Context context,String types,String voaId,Dubbing_rank rank){
        Intent intent = new Intent();
        intent.setClass(context,DubbingRankDetailActivity.class);
        intent.putExtra(StrLibrary.types,types);
        intent.putExtra(StrLibrary.voaId,voaId);
        intent.putExtra(StrLibrary.data,rank);
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
        Dubbing_rank rank = (Dubbing_rank) getIntent().getSerializableExtra(StrLibrary.data);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DubbingRankDetailFragment rankFragment = DubbingRankDetailFragment.getInstance(types,voaId,rank);
        transaction.add(R.id.container,rankFragment).show(rankFragment);
        transaction.commitAllowingStateLoss();
    }
}
