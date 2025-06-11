package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordStudy;

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
 * @title: 单词学习界面-容器
 * @date: 2023/5/12 14:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordStudyActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    public static void start(Context context,String types,String bookId,String id,int position){
        Intent intent = new Intent();
        intent.setClass(context,WordStudyActivity.class);
        intent.putExtra(StrLibrary.types,types);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.id,id);
        intent.putExtra(StrLibrary.position,position);
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
        String bookId = getIntent().getStringExtra(StrLibrary.bookId);
        String id = getIntent().getStringExtra(StrLibrary.id);
        int position = getIntent().getIntExtra(StrLibrary.position,0);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        WordStudyFragment listFragment = WordStudyFragment.getInstance(types,bookId,id,position);
        transaction.add(R.id.container,listFragment);
        transaction.show(listFragment);
        transaction.commitNow();
    }
}
