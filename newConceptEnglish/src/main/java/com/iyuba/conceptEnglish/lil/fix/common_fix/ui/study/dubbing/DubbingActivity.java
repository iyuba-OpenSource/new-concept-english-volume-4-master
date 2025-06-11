package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingPreview.DubbingPreviewFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

/**
 * @title: 配音界面
 * @date: 2023/6/14 10:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    public static void start(Context context,String types,String voaId){
        Intent intent = new Intent();
        intent.setClass(context,DubbingActivity.class);
        intent.putExtra(StrLibrary.types,types);
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

        DubbingFragment fragment = DubbingFragment.getInstance(types,voaId);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container,fragment).show(fragment).commitAllowingStateLoss();
    }
}
