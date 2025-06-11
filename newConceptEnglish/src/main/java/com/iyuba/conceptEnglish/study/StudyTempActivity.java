package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerTabTitleBinding;
import com.iyuba.conceptEnglish.lil.concept_other.study_section.eval.StudyEvalFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.concept.ConceptFragment;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.lil.fix.concept.study.ContentFragment;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;

import java.util.Locale;

/**
 * 临时的学习界面-用于新版本的练习题跳转（单一功能）
 */
public class StudyTempActivity extends BaseViewBindingActivity<LayoutContainerTabTitleBinding> {

    //类型
    private String showType = TypeLibrary.StudyPageType.read;//原文
    //课程id
    private int voaId = 0;

    public static void start(Context context,String showType,int voaId){
        Intent intent = new Intent();
        intent.setClass(context,StudyTempActivity.class);
        intent.putExtra(StrLibrary.showType,showType);
        intent.putExtra(StrLibrary.voaId,voaId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showType = getIntent().getStringExtra(StrLibrary.showType);
        voaId = getIntent().getIntExtra(StrLibrary.voaId,0);
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

    private void initToolbar(){
        binding.tabLayout.setVisibility(View.GONE);

        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button_normal);
        binding.toolbar.btnBack.setOnClickListener(v->{
            finish();
        });

        if (TextUtils.isEmpty(showType)){
            binding.toolbar.title.setText("未知类型");
        }else {
            String showTitle = getShowTitle();
            binding.toolbar.title.setText(showTitle);
        }
    }

    private void initFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        switch (showType){
            case TypeLibrary.StudyPageType.read:
                //听力
                fragment = ContentFragment.getInstance(0,true);
                break;
            case TypeLibrary.StudyPageType.eval:
                //评测
                fragment = StudyEvalFragment.getInstance();
                break;
            default:
                break;
        }

        transaction.add(R.id.container,fragment).show(fragment).commitNowAllowingStateLoss();
    }

    //获取显示的课程名称
    private String getShowTitle(){
        if (voaId<=0){
            switch (showType){
                case TypeLibrary.StudyPageType.read:
                    return "听力练习";
                case TypeLibrary.StudyPageType.eval:
                    return "口语练习";
                default:
                    return "未知类型("+showType+")";
            }
        }

        VoaOp voaOp = new VoaOp(this);
        Voa tempVoa = voaOp.findDataById(voaId);
        //判断名称显示
        if (tempVoa.voaId>10000){
            return tempVoa.title;
        }else {
            int lesson = tempVoa.voaId%1000;
            return String.format(Locale.CHINA, "Lesson %d %s", lesson, tempVoa.title);
        }
    }
}
