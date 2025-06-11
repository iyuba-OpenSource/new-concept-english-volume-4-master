package com.iyuba.conceptEnglish.lil.fix.concept;

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
import com.iyuba.conceptEnglish.fragment.HomeFragment;
import com.iyuba.conceptEnglish.fragment.PassFragment;
import com.iyuba.conceptEnglish.lil.concept_other.book_choose.ConceptBookChooseActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 新概念主界面
 * @date: 2023/6/14 11:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptFragment extends BaseViewBindingFragment<LayoutContainerTabTitleBinding> {

    //界面数据
    private List<Pair<String, Fragment>> pairList = new ArrayList<>();

    public static ConceptFragment getInstance(){
        ConceptFragment fragment = new ConceptFragment();
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
        binding.toolbar.title.setText(ConceptBookChooseManager.getInstance().getBookName());
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setOnClickListener(v->{
            //原来的跳转操作
//            BookChooseActivity.start(getActivity(),1);
            ConceptBookChooseActivity.start(getActivity(),1);
        });
        binding.toolbar.btnRight.setVisibility(View.VISIBLE);
        binding.toolbar.btnRight.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                //关闭后台播放
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_hide));

//                SearchActivity.start(getActivity(),null);
                NewSearchActivity.start(getActivity(),null);
            } else {
                ToastUtil.showToast(getActivity(), "未登录，请先登录");
                LoginUtil.startToLogin(getActivity());
            }
        });
        binding.toolbar.leftLine.setVisibility(View.VISIBLE);
        binding.toolbar.rightLine.setVisibility(View.VISIBLE);
    }

    private void initFragment(){
        //这里将两个数据都添加进来，但是只显示一个
        pairList = new ArrayList<>();
        HomeFragment listFragment = HomeFragment.getInstance(true);
        PassFragment wordFragment = PassFragment.getInstance(true);
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

                //切换界面时关闭播放器
                if (tab.getPosition() != 0){
                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
        if (event.getType().equals(TypeLibrary.RefreshDataType.concept_word)){
            //显示toolbar上的名称
            binding.toolbar.title.setText(ConceptBookChooseManager.getInstance().getBookName());
        }

        if (event.getType().equals(TypeLibrary.RefreshDataType.word_pass)
                &&getActivity().getPackageName().equals(Constant.package_learnNewEnglish)){
            //跳转到单词界面(这里严谨一些，判断名称中存在单词再跳转)
            int index = 0;
            for (int i = 0; i < pairList.size(); i++) {
                if (pairList.get(i).first.equals("单词")){
                    index = i;
                }
            }

            binding.tabLayout.getTabAt(index).select();
        }
    }
}
