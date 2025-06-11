package com.iyuba.conceptEnglish.lil.concept_other.me_localNews;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ActivityLocalNewsBinding;
import com.iyuba.conceptEnglish.lil.concept_other.me_localNews.junior.JuniorLocalNewsFragment;
import com.iyuba.conceptEnglish.lil.concept_other.me_localNews.novel.NovelLocalNewsFragment;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AbilityControlManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.configation.Constant;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 我的-篇目操作
 * @date: 2023/6/20 13:57
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LocalNewsActivity extends BaseViewBindingActivity<ActivityLocalNewsBinding> {

    private ConceptLocalNewsFragment conceptFragment;
    private JuniorLocalNewsFragment juniorFragment;
    private NovelLocalNewsFragment novelFragment;

    //三个类型的名称
    private static final String title_concept = "新概念";
    private static final String title_junior = "中小学";
    private static final String title_novel = "小说";
    private static final String title_novel_read = "阅读";

    public static void start(Context context, int localType) {
        Intent intent = new Intent();
        intent.setClass(context, LocalNewsActivity.class);
        intent.putExtra("localType", localType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置播放类型
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initFragment();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //关闭播放器
        EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_hide));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (conceptFragment != null) {
            if (conceptFragment.isDelStart) {
                conceptFragment.onBackShowDialog();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    /**************************显示样式******************/
    //隐藏界面
    private void hideFragment(FragmentTransaction transaction,List<Pair<String, Fragment>> list) {
        for (int i = 0; i < list.size(); i++) {
            Pair<String, Fragment> pair = list.get(i);
            transaction.hide(pair.second);
        }
    }

    //显示样式
    private void showFragment(List<Pair<String, Fragment>> list, int selectIndex) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction,list);
        transaction.show(list.get(selectIndex).second).commitNowAllowingStateLoss();
    }

    /**************************初始化*******************/
    //初始化
    private void initToolbar() {
        int localType = getIntent().getIntExtra("localType", 0);
        binding.title.setText(setTopTitle(localType));

        binding.back.setOnClickListener(v -> {
            finish();
        });
        binding.edit.setOnClickListener(v -> {
            int selectIndex = binding.tabLayout.getSelectedTabPosition();
            String showText = binding.tabLayout.getTabAt(selectIndex).getText().toString();

            if (showText.equals(title_concept)) {
                //操作新概念界面
                if (conceptFragment != null) {
                    conceptFragment.setEdit(true);
                }
            } else if (showText.equals(title_junior)){
                //中小学界面
                if (juniorFragment != null) {
                    juniorFragment.setEdit(true);
                }
            }else if (showText.equals(title_novel)
                    ||showText.equals(title_novel_read)){
                //书虫内容界面
                if (novelFragment!=null){
                    novelFragment.setEdit(true);
                }
            }
        });
        binding.sync.setOnClickListener(v -> {
            int selectIndex = binding.tabLayout.getSelectedTabPosition();
            String showText = binding.tabLayout.getTabAt(selectIndex).getText().toString();

            if (showText.equals(title_concept)) {
                //操作新概念界面
                if (conceptFragment != null) {
                    conceptFragment.setSync();
                }
            } else if (showText.equals(title_junior)){
                //新概念英语微课-操作中小学界面
                if (juniorFragment != null) {
                    juniorFragment.setSync();
                }
            }else if (showText.equals(title_novel)
                    ||showText.equals(title_novel_read)){
                //操作小说界面-新概念人工智能学外语/新概念英语全四册
                if (novelFragment!=null){
                    novelFragment.setSync();
                }
            }
        });

        if (localType==1){
            binding.edit.setVisibility(View.GONE);
            binding.sync.setVisibility(View.VISIBLE);
        }else {
            binding.edit.setVisibility(View.VISIBLE);
            binding.sync.setVisibility(View.GONE);
        }
    }

    private void initFragment() {
        //类型
        int localType = getIntent().getIntExtra("localType", 0);
        //渠道
        String channel = ChannelReaderUtil.getChannel(this);
        //包名
        String packageName = getPackageName();

        List<Pair<String, Fragment>> pairList = new ArrayList<>();
        conceptFragment = ConceptLocalNewsFragment.getInstance(localType);
        juniorFragment = JuniorLocalNewsFragment.getInstance();
        novelFragment = NovelLocalNewsFragment.getInstance();

        pairList.add(new Pair<>(title_concept, conceptFragment));
        if (localType == 1) {
            if (packageName.equals(Constant.package_learnNewEnglish)){
                //中小学
                if (!AbilityControlManager.getInstance().isLimitJunior()){
                    pairList.add(new Pair<>(title_junior, juniorFragment));
                }

                //小说
                if (!AbilityControlManager.getInstance().isLimitNovel()){
                    pairList.add(new Pair<>(title_novel_read,novelFragment));
                }
            } else if ((packageName.equals(Constant.package_concept2)
                    ||packageName.equals(Constant.package_englishfm))
                    &&!AbilityControlManager.getInstance().isLimitNovel()){
                String showType = title_novel;
                if (packageName.equals(Constant.package_concept2)){
                    showType = title_novel_read;
                }
                //小说
                pairList.add(new Pair<>(showType,novelFragment));
            }else if (packageName.equals(Constant.package_conceptStory)&&channel.equals("tiktok")){
                //中小学
                if (!AbilityControlManager.getInstance().isLimitJunior()){
                    pairList.add(new Pair<>(title_junior, juniorFragment));
                }
            }
        }

        //界面显示
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < pairList.size(); i++) {
            Pair<String, Fragment> pair = pairList.get(i);
            transaction.add(R.id.container, pair.second);
        }
        hideFragment(transaction,pairList);
        transaction.show(pairList.get(0).second).commitNow();

        //tab显示
        for (int i = 0; i < pairList.size(); i++) {
            String showText = pairList.get(i).first;
            TabLayout.Tab tab = binding.tabLayout.newTab();
            tab.setText(showText);
            binding.tabLayout.addTab(tab);
        }
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showFragment(pairList, tab.getPosition());
                setEditBtn(false);

                /*if (tab.getPosition() == 0) {
                    //不进行任何操作
                } else {
                    if (conceptFragment != null) {
                        conceptFragment.setEdit(false);
                    }
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (pairList.size() > 1) {
            binding.tabLayout.setVisibility(View.VISIBLE);
        } else {
            binding.tabLayout.setVisibility(View.GONE);
        }
    }

    /***********************辅助数据*******************/
    public String setTopTitle(int type) {
        switch (type) {
            case 0:
                return getResources().getString(R.string.local_title);
            case 1:
                return getResources().getString(R.string.favor_title);
            case 2:
                return getResources().getString(R.string.read_title);
        }
        return "";
    }

    //操作编辑按钮状态
    public void setEditBtn(boolean open) {
        if (open) {
            binding.edit.setBackgroundResource(R.drawable.button_edit_finished);
        } else {
            binding.edit.setBackgroundResource(R.drawable.button_edit);
        }
    }
}
