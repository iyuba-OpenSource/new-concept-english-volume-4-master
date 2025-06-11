package com.iyuba.core.talkshow.myTalk;

import static com.iyuba.core.talkshow.myTalk.MyTalkListFragment.COLLECT;
import static com.iyuba.core.talkshow.myTalk.MyTalkListFragment.DOWNLOAD;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.iyuba.lib.R;

import java.util.ArrayList;
import java.util.List;

import personal.iyuba.personalhomelibrary.ui.my.CommonPagerAdapter;

public class MyTalkActivity extends AppCompatActivity {

    TextView tvEdit;
    TextView tvSelectAll;
    TextView tvCancel;
    TabLayout mTabs;
    ViewPager mViewPager;
    Toolbar toolbar;
    TextView tvDelete;

    FragmentPagerAdapter mPagerAdapter;
    private PublishFragment publishFragment;
    private MyTalkListFragment collectFragment;
    private MyTalkListFragment DownLoadFragment;

    private int mSelectItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_talk);
        initView();

        setSupportActionBar(toolbar);
        initViewPage();
        initListener();
    }

    private void initView(){
        tvEdit = findViewById(R.id.tv_edit);
        tvSelectAll = findViewById(R.id.tv_select_all);
        tvCancel = findViewById(R.id.tv_cancel);
        mTabs = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar2);
        tvDelete = findViewById(R.id.tv_delete_sure);
    }

    private void initListener() {
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCancel.setVisibility(View.VISIBLE);
                tvSelectAll.setVisibility(View.VISIBLE);
                tvEdit.setVisibility(View.GONE);

                publishFragment.setDeleteShow();
                collectFragment.setDeleteShow();
                DownLoadFragment.setDeleteShow();
                tvDelete.setVisibility(View.VISIBLE);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCancel.setVisibility(View.GONE);
                tvSelectAll.setVisibility(View.GONE);
                tvEdit.setVisibility(View.VISIBLE);

                publishFragment.setDeleteCancel();
                collectFragment.setDeleteCancel();
                DownLoadFragment.setDeleteCancel();
                tvDelete.setVisibility(View.GONE);
            }
        });

        tvSelectAll.setOnClickListener(new View.OnClickListener() {
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

        tvDelete.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void initViewPage() {
        String[] titles = {"已发布","已收藏" ,"已下载"};
        List<Fragment> fragments = new ArrayList<>();
        publishFragment = PublishFragment.newInstance();
        collectFragment = MyTalkListFragment.newInstance(COLLECT);
        DownLoadFragment = MyTalkListFragment.newInstance(DOWNLOAD);
        fragments.add(publishFragment);
        fragments.add(collectFragment);
        fragments.add(DownLoadFragment);
        mPagerAdapter = new CommonPagerAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mPagerAdapter);
        mTabs.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
