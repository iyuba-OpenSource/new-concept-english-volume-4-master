package com.iyuba.conceptEnglish.activity.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.viewpager2.widget.ViewPager2;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.BaseActivity;
import com.iyuba.conceptEnglish.activity.MainFragmentActivity;
import com.iyuba.conceptEnglish.lil.concept_other.book_choose.ConceptBookChooseActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.lil.base.StackUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 引导页
 * @date: 2023/5/16 09:10
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class GuideActivity extends BaseActivity {

    private ViewPager2 viewPager2;
    private Button button;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        viewPager2 = findViewById(R.id.viewPager2);
        button = findViewById(R.id.enter);
        button.setOnClickListener(v->{
            //保存在缓存中
            ConfigManager.Instance().putInt("guideVersion", ConstantNew.guide_version);
            //进入下一级
            if (getPackageName().equals(Constant.package_learnNewEnglish)){
                Intent intent3 = new Intent(this, MainFragmentActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
            }else {
                if (ConceptBookChooseManager.getInstance().getBookId() == 0) {
                    ConceptBookChooseActivity.start(this,0);
                }else {
                    Intent intent3 = new Intent(this, MainFragmentActivity.class);
                    intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);
                }
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            StackUtil.getInstance().finishCur();
        });
    }

    @Override
    protected void loadData() {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.guide_1);
        list.add(R.drawable.guide_2);
        list.add(R.drawable.guide_3);
        list.add(R.drawable.guide_4);
        list.add(R.drawable.guide_5);

        GuideAdapter guideAdapter = new GuideAdapter(this,list);
        viewPager2.setAdapter(guideAdapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == list.size()-1){
                    button.setVisibility(View.VISIBLE);
                }else {
                    button.setVisibility(View.GONE);
                }
            }
        });
    }
}
