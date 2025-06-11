package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.wordTrain;

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
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.core.lil.base.StackUtil;

/**
 * @title: 单词训练界面
 * @date: 2023/8/15 16:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordTrainActivity extends BaseViewBindingActivity<LayoutContainerTabTitleBinding> {

    private String showType;
    private String types;
    private String bookId;
    private String id;

    private Fragment showFragment = null;

    public static void start(Context context,String showType,String types,String bookId,String id){
        Intent intent = new Intent();
        intent.setClass(context,WordTrainActivity.class);
        intent.putExtra(StrLibrary.showType,showType);
        intent.putExtra(StrLibrary.types,types);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.id,id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showType = getIntent().getStringExtra(StrLibrary.showType);
        types = getIntent().getStringExtra(StrLibrary.types);
        bookId = getIntent().getStringExtra(StrLibrary.bookId);
        id = getIntent().getStringExtra(StrLibrary.id);

        //区分类型进行数据处理
        if (types.equals(TypeLibrary.BookType.conceptJunior)){
            //青少版
            id = String.valueOf(ConceptDataManager.searchConceptJuniorUnitIdByVoaId(String.valueOf(id)));
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
    private void initToolbar(){
        String showTitle = "英汉训练";
        switch (showType){
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
        binding.toolbar.btnBack.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });

        binding.tabLayout.setVisibility(View.GONE);
    }

    private void initFragment(){
        switch (showType){
            case TypeLibrary.WordTrainType.Train_enToCn:
                //英汉训练
                showFragment = WordTrain_enCnFragment.getInstance(TypeLibrary.WordTrainType.Train_enToCn,types,bookId,id);
                break;
            case TypeLibrary.WordTrainType.Train_cnToEn:
                //汉英训练
                showFragment = WordTrain_enCnFragment.getInstance(TypeLibrary.WordTrainType.Train_cnToEn,types,bookId,id);
                break;
            case TypeLibrary.WordTrainType.Word_spell:
                //单词拼写
                showFragment = WordTrain_spellFragment.getInstance(types,bookId,id);
                break;
            case TypeLibrary.WordTrainType.Train_listen:
                //听力训练
                showFragment = WordTrain_listenFragment.getInstance(types,bookId,id);
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,showFragment);
        transaction.show(showFragment);
        transaction.commitNowAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (showFragment instanceof WordTrain_enCnFragment){
            WordTrain_enCnFragment enCnFragment = (WordTrain_enCnFragment) showFragment;
            if (!enCnFragment.showExistDialog()){
                super.onBackPressed();
            }
        }else if (showFragment instanceof WordTrain_listenFragment){
            WordTrain_listenFragment listenFragment = (WordTrain_listenFragment) showFragment;
            if (!listenFragment.showExistDialog()){
                super.onBackPressed();
            }
        }else if (showFragment instanceof WordTrain_spellFragment){
            WordTrain_spellFragment spellFragment = (WordTrain_spellFragment) showFragment;
            if (!spellFragment.showExistDialog()){
                super.onBackPressed();
            }
        }
    }
}
