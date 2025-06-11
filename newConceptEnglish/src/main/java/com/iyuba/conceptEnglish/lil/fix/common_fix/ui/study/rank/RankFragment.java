package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.rank;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.iyuba.conceptEnglish.databinding.FragmentFixRankBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_rank;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.rank.rank_detail.RankDetailActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ImageUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.study.voaStructure.NoScrollLinearLayoutManager;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 排行界面
 * @date: 2023/5/25 10:09
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RankFragment extends BaseViewBindingFragment<FragmentFixRankBinding> implements RankView{

    //类型
    private String types;
    //voaId
    private String voaId;
    //开始页码
    private int startIndex = 0;
    //每页数量
    private int pageCount = 20;
    //类型
    private String rankType = "D";

    private RankPresenter presenter;
    private RankAdapter rankAdapter;

    //是否加载完成
    private boolean isLoadFinish = false;

    public static RankFragment getInstance(String types, String voaId){
        RankFragment fragment = new RankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaid, voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        types = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaid);

        presenter = new RankPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isLoadFinish){
            isLoadFinish = true;
            binding.refreshLayout.autoRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        presenter.detachView();
    }

    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(true);
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    binding.refreshLayout.finishLoadMore(false);
                    refreshFailView("请链接网络后重试~");
                    return;
                }

                presenter.getPublishRankData(types,voaId,startIndex,pageCount,rankType,false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    binding.refreshLayout.finishRefresh(false);
                    ToastUtil.showToast(getActivity(),"请链接网络后重试~");
                    return;
                }

                startIndex = 0;
                presenter.getPublishRankData(types,voaId,startIndex,pageCount,rankType,true);
            }
        });

        rankAdapter = new RankAdapter(getActivity(),new ArrayList<>());
        NoScrollLinearLayoutManager manager = new NoScrollLinearLayoutManager(getActivity(),false);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(rankAdapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        rankAdapter.setListener(new OnSimpleClickListener<Eval_rank.DataBean>() {
            @Override
            public void onClick(Eval_rank.DataBean dataBean) {
                RankDetailActivity.start(getActivity(),types,voaId,String.valueOf(dataBean.getUid()),dataBean.getName(),dataBean.getImgSrc());
            }
        });

        binding.championLayout.setOnClickListener(v->{
            //获取第一个数据进行展示
            Eval_rank.DataBean dataBean = rankAdapter.getFirstData();
            if (dataBean!=null){
                RankDetailActivity.start(getActivity(),types,voaId,String.valueOf(dataBean.getUid()),dataBean.getName(),dataBean.getImgSrc());
            }else {
                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(getActivity());
                    return;
                }

                RankDetailActivity.start(getActivity(),types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()), UserInfoManager.getInstance().getUserName(), FixUtil.getUserHeadPic(String.valueOf(UserInfoManager.getInstance().getUserId())));
            }
        });

        binding.myLayout.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(getActivity());
                return;
            }

            RankDetailActivity.start(getActivity(),types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()), UserInfoManager.getInstance().getUserName(), FixUtil.getUserHeadPic(String.valueOf(UserInfoManager.getInstance().getUserId())));
        });
    }

    /*******************刷新数据***************/
    private void refreshUser(boolean isRefresh,Eval_rank bean){
        //刷新页码
        int newStartIndex = startIndex;
//        if (bean!=null&&bean.getData()!=null&&bean.getData().size()>0){
//            if (bean.getData().size()>=pageCount){
//                newStartIndex++;
//            }
//        }

        //刷新前边的数据
        if (bean==null||bean.getMyid()==0){
            binding.myName.setText("未登录");
            binding.myinfo.setText("未登录");
        }else {
            ImageUtil.loadCircleImg(bean.getMyimgSrc(),0,binding.myPic);
            binding.myName.setText(bean.getMyname());
            String info = "句子:"+bean.getMycount()+",得分:"+bean.getMyscores()+",排名:"+bean.getMyranking();
            binding.myinfo.setText(info);
        }

        //刷新冠军数据
        if (bean!=null&&bean.getData()!=null&&bean.getData().size()>0){
            Eval_rank.DataBean dataBean = bean.getData().get(0);
            if (TextUtils.isEmpty(dataBean.getImgSrc())){
                binding.championText.setVisibility(View.VISIBLE);
                binding.championPic.setVisibility(View.GONE);
                binding.championText.setText(FixUtil.getFirstChar(dataBean.getName()));
            }else {
                binding.championText.setVisibility(View.GONE);
                binding.championPic.setVisibility(View.VISIBLE);
                ImageUtil.loadCircleImg(dataBean.getImgSrc(), 0,binding.championPic);
            }
            binding.championName.setText(dataBean.getName());
            binding.championWords.setText(dataBean.getScores()+"分");

            //刷新列表数据
            refreshData(isRefresh,newStartIndex,bean.getData());
            //设置显示位置数据
            startIndex+=bean.getData().size();
        }else {
            if (TextUtils.isEmpty(bean.getMyimgSrc())){
                binding.championText.setVisibility(View.VISIBLE);
                binding.championPic.setVisibility(View.GONE);
                binding.championText.setText(FixUtil.getFirstChar(bean.getMyname()));
            }else {
                binding.championText.setVisibility(View.GONE);
                binding.championPic.setVisibility(View.VISIBLE);
                ImageUtil.loadCircleImg(bean.getMyimgSrc(), 0,binding.championPic);
            }
            binding.championName.setText(bean.getMyname());
            binding.championWords.setText(bean.getMyscores()+"分");
        }
    }

    private void refreshData(boolean isRefresh,int newStartIndex,List<Eval_rank.DataBean> list){
        if (list!=null&&list.size()>0){
            if (isRefresh){
                rankAdapter.refreshData(list);
            }else {
//                if (newStartIndex>startIndex){
//                    startIndex = newStartIndex;
//                    rankAdapter.addData(list);
//                }
                rankAdapter.addData(list);
            }
        }else {
            rankAdapter.refreshData(new ArrayList<>());
        }
    }

    //回调
    @Override
    public void showData(boolean isRefresh, Eval_rank bean) {
        binding.refreshLayout.finishRefresh(true);
        binding.refreshLayout.finishLoadMore(true);

        if (bean==null){
            if (isRefresh){
                refreshFailView("加载数据错误，请重试～");
            }else {
                ToastUtil.showToast(getActivity(),"加载数据错误，请重试～");
            }
            return;
        }

        refreshUser(isRefresh,bean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.eval_rank)){
            binding.refreshLayout.autoRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VipChangeEvent event){
        binding.refreshLayout.autoRefresh();
    }

    /****************辅助功能*****************/
    //显示错误数据
    private void refreshFailView(String msg){
        binding.failLayout.setVisibility(View.VISIBLE);
        binding.failText.setText(msg);
        binding.failBtn.setOnClickListener(v->{
            binding.failLayout.setVisibility(View.GONE);
            binding.refreshLayout.autoRefresh();
        });
    }

    public void refreshDate(){
        binding.refreshLayout.autoRefresh();
    }
}
