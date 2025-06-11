package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.conceptWordTrain;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerTabTitleBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.wordTrain.WordTrain_enCnFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.wordTrain.WordTrain_listenFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.wordTrain.WordTrain_spellFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.core.lil.base.StackUtil;

/**
 * @title: 新概念内容专用的单词练习界面
 * @date: 2023/10/25 09:10
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptWordTrainActivity extends BaseViewBindingActivity<LayoutContainerTabTitleBinding> {

    private String showType;
    private String bookType;
    private String bookId;
    private String id;

    private Fragment showFragment = null;

    public static void start(Context context, String showType, String bookType, String bookId, String id) {
        Intent intent = new Intent();
        intent.setClass(context, ConceptWordTrainActivity.class);
        intent.putExtra(StrLibrary.showType, showType);
        intent.putExtra(StrLibrary.types, bookType);
        intent.putExtra(StrLibrary.bookId, bookId);
        intent.putExtra(StrLibrary.id, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showType = getIntent().getStringExtra(StrLibrary.showType);
        bookId = getIntent().getStringExtra(StrLibrary.bookId);
        id = getIntent().getStringExtra(StrLibrary.id);
        bookType = getIntent().getStringExtra(StrLibrary.types);
        if (bookType.equals(TypeLibrary.BookType.conceptFourUS)
                ||bookType.equals(TypeLibrary.BookType.conceptFourUK)){
            bookType = TypeLibrary.BookType.conceptFour;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /***********************初始化*************************/
    private void initToolbar() {
        String showTitle = "英汉训练";
        switch (showType) {
            case TypeLibrary.WordTrainType.Train_enToCn:
                showTitle = "英汉训练";
                break;
            case TypeLibrary.WordTrainType.Train_cnToEn:
                showTitle = "汉英训练";
                break;
            case TypeLibrary.WordTrainType.Word_spell:
                showTitle = "单词拼写";
                break;
            case TypeLibrary.WordTrainType.Train_listen:
                showTitle = "听力训练";
                break;
        }

        binding.toolbar.title.setText(showTitle);
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button);
        binding.toolbar.btnBack.setOnClickListener(v -> {
            StackUtil.getInstance().finishCur();
        });

        binding.tabLayout.setVisibility(View.GONE);
    }

    private void initFragment() {
        switch (showType) {
            case TypeLibrary.WordTrainType.Train_enToCn:
                //英汉训练
                showFragment = WordTrain_enCnFragment.getInstance(TypeLibrary.WordTrainType.Train_enToCn, bookType, bookId, id);
                break;
            case TypeLibrary.WordTrainType.Train_cnToEn:
                //汉英训练
                showFragment = WordTrain_enCnFragment.getInstance(TypeLibrary.WordTrainType.Train_cnToEn, bookType, bookId, id);
                break;
            case TypeLibrary.WordTrainType.Word_spell:
                //单词拼写
                showFragment = WordTrain_spellFragment.getInstance(bookType, bookId, id);
                break;
            case TypeLibrary.WordTrainType.Train_listen:
                //听力训练
                showFragment = WordTrain_listenFragment.getInstance(bookType, bookId, id);
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, showFragment);
        transaction.show(showFragment);
        transaction.commitNowAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (showFragment instanceof WordTrain_enCnFragment) {
            WordTrain_enCnFragment enCnFragment = (WordTrain_enCnFragment) showFragment;
            if (!enCnFragment.showExistDialog()) {
                super.onBackPressed();
            }
        } else if (showFragment instanceof WordTrain_listenFragment) {
            WordTrain_listenFragment listenFragment = (WordTrain_listenFragment) showFragment;
            if (!listenFragment.showExistDialog()) {
                super.onBackPressed();
            }
        } else if (showFragment instanceof WordTrain_spellFragment) {
            WordTrain_spellFragment spellFragment = (WordTrain_spellFragment) showFragment;
            if (!spellFragment.showExistDialog()) {
                super.onBackPressed();
            }
        }
    }
}
