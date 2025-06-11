package com.iyuba.conceptEnglish.lil.fix.junior.word;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.conceptEnglish.databinding.FragmentListRefreshBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.JuniorBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.WordAdapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.WordPresenter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.WordView;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.WordListActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.MultiButtonDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 中小学单词界面
 * @date: 2023/6/8 15:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorWordFragment extends BaseViewBindingFragment<FragmentListRefreshBinding> implements WordView {

    private WordPresenter presenter;
    private WordAdapter wordAdapter;

    public static JuniorWordFragment getInstance(){
        JuniorWordFragment fragment = new JuniorWordFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new WordPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        //延迟1.5s加载
        RxTimer.getInstance().timerInMain("juniorWordDelayTime", 2000L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                RxTimer.getInstance().cancelTimer("juniorWordDelayTime");
                refreshData();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        presenter.detachView();
    }

    /*********************初始化**************************/
    private void initList(){
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    binding.refreshLayout.finishRefresh(false);
                    ToastUtil.showToast(getActivity(),"请链接网络后下拉刷新重试~");
                    return;
                }

                presenter.getNetWordData(JuniorBookChooseManager.getInstance().getBookType(), JuniorBookChooseManager.getInstance().getBookId());
            }
        });

        wordAdapter = new WordAdapter(getActivity(),new ArrayList<>());
        GridLayoutManager manager = new GridLayoutManager(getActivity(),3);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(wordAdapter);
        wordAdapter.setListener(new OnSimpleClickListener<Pair<Integer, WordProgressBean>>() {
            @Override
            public void onClick(Pair<Integer, WordProgressBean> pair) {
                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(getActivity());
                    return;
                }


                int position = pair.first;
                WordProgressBean progressBean = pair.second;

                //非vip判定操作
                if (!progressBean.isPass() && !UserInfoManager.getInstance().isVip()){
                    ToastUtil.showToast(getActivity(),"当前关卡未解锁，完成前一关卡后解锁");
                    return;
                }

                if (position>0 && !UserInfoManager.getInstance().isVip()){
                    MultiButtonDialog dialog = new MultiButtonDialog(getActivity());
                    dialog.create();
                    dialog.setTitle("会员购买");
                    dialog.setMsg("非会员单词闯关体验1关，会员无限制。是否购买会员继续使用?");
                    dialog.setButton("暂不购买", "立即购买", new MultiButtonDialog.OnMultiClickListener() {
                        @Override
                        public void onAgree() {
                            NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_APP);
                        }

                        @Override
                        public void onDisagree() {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }


                String types = JuniorBookChooseManager.getInstance().getBookType();
                String bookId = JuniorBookChooseManager.getInstance().getBookId();
                String tag = JuniorBookChooseManager.getInstance().getBookType();

                //其他判断
                WordListActivity.start(getActivity(),types,bookId,tag,progressBean.getVoaId(),progressBean.getId(),progressBean.isPass());
            }
        });
    }

    /**********************刷新数据***********************/
    private void refreshData(){
        presenter.getLocalWordData(JuniorBookChooseManager.getInstance().getBookType(), JuniorBookChooseManager.getInstance().getBookId());
    }

    /**********************回调数据***********************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.junior)){
            //刷新单词数据
            refreshData();
        }
    }

    @Override
    public void showLoadWord(boolean isLoading) {
        if (isLoading){
            binding.refreshLayout.autoRefresh();
        }
    }

    @Override
    public void showGroupWord(List<WordProgressBean> list) {
        if (list!=null&&list.size()>0){
            binding.refreshLayout.finishRefresh(true);
            wordAdapter.refreshData(list);
        }else {
            binding.refreshLayout.finishRefresh(false);
            ToastUtil.showToast(getActivity(),"获取单词进度失败～");
        }
    }
}
