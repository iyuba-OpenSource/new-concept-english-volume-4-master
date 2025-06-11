package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.RankFragmentAdapter;
import com.iyuba.conceptEnglish.fragment.SpeakCircleFragment;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpeakCircleActivity extends FragmentActivity {

    @BindView(R.id.ibtn_back)
    ImageButton backBtn;


    @BindView(R.id.tv_hot)
    TextView tvHot;
    @BindView(R.id.tv_recently)
    TextView tvRecently;
    @BindView(R.id.tv_my)
    TextView tvMy;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private Context mContext;


    private SpeakCircleFragment fragment0, fragment1, fragment2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_speak_circle);

        ButterKnife.bind(this);
        initView();

    }


    private void initView() {
        List<Fragment> list = new ArrayList<>();
        fragment0 = SpeakCircleFragment.instence(0);
        fragment1 = SpeakCircleFragment.instence(1);
        fragment2 = SpeakCircleFragment.instence(2);

        list.add(fragment0);//最新
        list.add(fragment1);//我的
        list.add(fragment2);//好友
        RankFragmentAdapter adapter = new RankFragmentAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        selectBtn(tvHot);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        selectBtn(tvHot);
                        fragment1.pauseAdapter();
                        fragment2.pauseAdapter();
                        break;
                    case 1:
                        selectBtn(tvRecently);
                        fragment0.pauseAdapter();
                        fragment2.pauseAdapter();
                        break;
                    case 2:
                        selectBtn(tvMy);
                        fragment0.pauseAdapter();
                        fragment1.pauseAdapter();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @OnClick({R.id.tv_hot, R.id.tv_recently, R.id.tv_my})
    void click(View v) {

        switch (v.getId()) {
            case R.id.tv_hot:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_recently:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_my:
                viewPager.setCurrentItem(2);
                break;
        }
        selectBtn((TextView) v);
    }

    private void selectBtn(TextView tv) {
        tvHot.setSelected(false);
        tvRecently.setSelected(false);
        tvMy.setSelected(false);

        tvHot.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvRecently.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvMy.setTextColor(getResources().getColor(R.color.colorPrimary));

        tv.setSelected(true);
        tv.setTextColor(0xffffffff);
    }
}
