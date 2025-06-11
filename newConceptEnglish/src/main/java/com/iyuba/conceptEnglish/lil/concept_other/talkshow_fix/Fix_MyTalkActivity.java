package com.iyuba.conceptEnglish.lil.concept_other.talkshow_fix;

import static com.iyuba.core.talkshow.myTalk.MyTalkListFragment.COLLECT;
import static com.iyuba.core.talkshow.myTalk.MyTalkListFragment.DOWNLOAD;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.iyuba.conceptEnglish.databinding.ActivityMyTalkFixBinding;
import com.iyuba.conceptEnglish.lil.concept_other.talkshow_fix.myTalk.Fix_MyTalkListFragment;
import com.iyuba.conceptEnglish.lil.concept_other.talkshow_fix.publish.Fix_PublishFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

import java.util.ArrayList;
import java.util.List;

import personal.iyuba.personalhomelibrary.ui.my.CommonPagerAdapter;

/**
 * @title: 我的配音界面
 * @date: 2023/8/2 10:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Fix_MyTalkActivity extends BaseViewBindingActivity<ActivityMyTalkFixBinding> {

    FragmentPagerAdapter mPagerAdapter;
    private Fix_PublishFragment publishFragment;
    private Fix_MyTalkListFragment collectFragment;
    private Fix_MyTalkListFragment DownLoadFragment;

    private int mSelectItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar2);
        initViewPage();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void initListener() {
        binding.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvCancel.setVisibility(View.VISIBLE);
                binding.tvSelectAll.setVisibility(View.VISIBLE);
                binding.tvEdit.setVisibility(View.GONE);

                publishFragment.setDeleteShow();
                collectFragment.setDeleteShow();
                DownLoadFragment.setDeleteShow();
                binding.tvDeleteSure.setVisibility(View.VISIBLE);
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvCancel.setVisibility(View.GONE);
                binding.tvSelectAll.setVisibility(View.GONE);
                binding.tvEdit.setVisibility(View.VISIBLE);

                publishFragment.setDeleteCancel();
                collectFragment.setDeleteCancel();
                DownLoadFragment.setDeleteCancel();
                binding.tvDeleteSure.setVisibility(View.GONE);
            }
        });

        binding.tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mSelectItem){
                    case 0:
                        publishFragment.setDeleteAll();
                        break;
                    case 1:
                        collectFragment.setDeleteAll();
                        break;
                    case 2:
                        DownLoadFragment.setDeleteAll();
                        break;
                }
            }
        });

        binding.tvDeleteSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mSelectItem){
                    case 0:
                        publishFragment.setDeleteSure();
                        break;
                    case 1:
                        collectFragment.setDeleteSure();
                        break;
                    case 2:
                        DownLoadFragment.setDeleteSure();
                        break;
                }
            }
        });
    }

    private void initViewPage() {
//        String[] titles = {"已发布","已收藏" ,"已下载"};
        String[] titles = {"已发布"};
        List<Fragment> fragments = new ArrayList<>();
        publishFragment = Fix_PublishFragment.getInstance();
        collectFragment = Fix_MyTalkListFragment.getInstance(COLLECT);
        DownLoadFragment = Fix_MyTalkListFragment.getInstance(DOWNLOAD);
        fragments.add(publishFragment);
//        fragments.add(collectFragment);
//        fragments.add(DownLoadFragment);
        mPagerAdapter = new CommonPagerAdapter(getSupportFragmentManager(), fragments, titles);
        binding.viewPager.setAdapter(mPagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.viewPager.setCurrentItem(0);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mSelectItem = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}
