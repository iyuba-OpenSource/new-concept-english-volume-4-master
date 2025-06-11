package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.rank.rank_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.junior.choose.JuniorChooseFragment;

/**
 * @title: 排行详情界面
 * @date: 2023/5/25 14:33
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RankDetailActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    public static void start(Context context, String types, String voaId, String userId, String userName,String userPic) {
        Intent intent = new Intent();
        intent.setClass(context, RankDetailActivity.class);
        intent.putExtra(StrLibrary.types, types);
        intent.putExtra(StrLibrary.voaid, voaId);
        intent.putExtra(StrLibrary.userId, userId);
        intent.putExtra(StrLibrary.username, userName);
        intent.putExtra(StrLibrary.userPic,userPic);
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

    private void initFragment() {
        String types = getIntent().getStringExtra(StrLibrary.types);
        String voaId = getIntent().getStringExtra(StrLibrary.voaid);
        String userId = getIntent().getStringExtra(StrLibrary.userId);
        String userName = getIntent().getStringExtra(StrLibrary.username);
        String userPic = getIntent().getStringExtra(StrLibrary.userPic);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        RankDetailFragment fragment = RankDetailFragment.getInstance(types,voaId,userId,userName,userPic);
        transaction.add(R.id.container, fragment).show(fragment);
        transaction.commitAllowingStateLoss();
    }
}
