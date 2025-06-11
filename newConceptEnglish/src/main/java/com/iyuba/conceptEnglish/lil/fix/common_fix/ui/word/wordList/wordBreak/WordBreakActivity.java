package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordBreak;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordTrain.train_cnToEn.CnToEnFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

/**
 * @title: 单词闯关-容器界面
 * @date: 2023/5/26 00:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordBreakActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    private WordBreakFragment wordBreakFragment;

    public static void start(Context context, String bookType,String bookId,String id){
        Intent intent = new Intent();
        intent.setClass(context, WordBreakActivity.class);
        intent.putExtra(StrLibrary.types,bookType);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.id,id);
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

    @Override
    public void onBackPressed() {
        if (wordBreakFragment!=null&&!wordBreakFragment.showBackDialog()){
            super.onBackPressed();
        }
    }

    private void initFragment(){
        String bookType = getIntent().getStringExtra(StrLibrary.types);
        String bookId = getIntent().getStringExtra(StrLibrary.bookId);
        String id = getIntent().getStringExtra(StrLibrary.id);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        wordBreakFragment = WordBreakFragment.getInstance(bookType,bookId,id);
        transaction.add(R.id.container, wordBreakFragment);
        transaction.show(wordBreakFragment);
        transaction.commitNow();
    }
}
