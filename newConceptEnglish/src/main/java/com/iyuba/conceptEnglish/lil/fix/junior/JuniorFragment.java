package com.iyuba.conceptEnglish.lil.fix.junior;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerTabTitleBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.JuniorBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.choose.ChooseActivity;
import com.iyuba.conceptEnglish.lil.fix.junior.word.JuniorWordFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.junior.main.JuniorListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 中小学主界面
 * @date: 2023/6/8 15:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorFragment extends BaseViewBindingFragment<LayoutContainerTabTitleBinding> {

    public static JuniorFragment getInstance(){
        JuniorFragment fragment = new JuniorFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /******************************初始化数据***************************/
    private void initToolbar(){
        binding.toolbar.title.setText(JuniorBookChooseManager.getInstance().getBookName());
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setOnClickListener(v->{
            ChooseActivity.start(getActivity(), JuniorBookChooseManager.getInstance().getBookType());
        });
        binding.toolbar.leftLine.setVisibility(View.VISIBLE);
        binding.toolbar.rightLine.setVisibility(View.VISIBLE);
    }

    private void initFragment(){
        //这里将两个数据都添加进来，但是只显示一个
        List<Pair<String, Fragment>> pairList = new ArrayList<>();
        JuniorListFragment listFragment = JuniorListFragment.getInstance();
        JuniorWordFragment wordFragment = JuniorWordFragment.getInstance();
        pairList.add(new Pair<>("课程",listFragment));
        pairList.add(new Pair<>("单词",wordFragment));

        showFragment(false,pairList,0);

        for (int i = 0; i < pairList.size(); i++) {
            String showText = pairList.get(i).first;

            TabLayout.Tab tab = binding.tabLayout.newTab();
            tab.setText(showText);
            binding.tabLayout.addTab(tab);
        }
        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayout.setTabIndicatorFullWidth(true);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showFragment(true,pairList,tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //如果只有一个，则不用显示
        if (pairList.size()<=1){
            binding.tabLayout.setVisibility(View.GONE);
        }else {
            binding.tabLayout.setVisibility(View.VISIBLE);
        }
    }

    /*************************************刷新界面****************************/
    //显示界面
    private void showFragment(boolean isAdd,List<Pair<String,Fragment>> list,int position){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            Fragment fragment = list.get(i).second;
            if (!isAdd){
                transaction.add(R.id.container,fragment);
            }
            transaction.hide(fragment);
        }
        Fragment fragment = list.get(position).second;
        transaction.show(fragment).commitAllowingStateLoss();
    }

    /************************************回调**********************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.junior)){
            binding.toolbar.title.setText(JuniorBookChooseManager.getInstance().getBookName());
        }
    }
}
