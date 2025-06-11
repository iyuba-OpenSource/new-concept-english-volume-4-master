package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutListTitleBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Dubbing_rank;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank.dubbingRankDetail.DubbingRankDetailActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.core.lil.base.StackUtil;
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
 * @title:
 * @date: 2023/6/13 15:14
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingRankFragment extends BaseViewBindingFragment<LayoutListTitleBinding>  implements DubbingRankView{

    private String bookType;
    private String voaId;

    private DubbingRankPresenter presenter;
    //适配器
    private DubbingRankAdapter rankAdapter;
    //每页的数量
    private int pageCount = 20;
    //当前页码
    private int curIndex = 1;
    //是否处于刷新状态
    private boolean isRefresh = false;

    public static DubbingRankFragment getInstance(String types,String voaId){
        DubbingRankFragment rankFragment = new DubbingRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types,types);
        bundle.putString(StrLibrary.voaId,voaId);
        rankFragment.setArguments(bundle);
        return rankFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        bookType = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaId);

        presenter = new DubbingRankPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initList();

        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        presenter.detachView();
    }

    /********************初始化数据********************/
    private void initToolbar(){
        binding.toolbar.title.setText("配音排行");
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button);
        binding.toolbar.btnBack.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
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
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                isRefresh = false;
                presenter.searchDubbingRank(bookType,voaId,curIndex,pageCount);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                isRefresh = true;
                curIndex = 1;
                presenter.searchDubbingRank(bookType,voaId,curIndex,pageCount);
            }
        });

        rankAdapter = new DubbingRankAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(rankAdapter);
        rankAdapter.setListener(new OnSimpleClickListener<Dubbing_rank>() {
            @Override
            public void onClick(Dubbing_rank rank) {
                DubbingRankDetailActivity.start(getActivity(),bookType,voaId,rank);
            }
        });
    }

    /********************回调数据**********************/
    @Override
    public void showRank(List<Dubbing_rank> list) {
        if (list!=null&&list.size()>0){
            //停止加载
            if (isRefresh){
                binding.refreshLayout.finishRefresh(true);
            }else {
                binding.refreshLayout.finishLoadMore(true);
            }
            //判断当前是否需要+1
            if (list.size()==0){
                ToastUtil.showToast(getActivity(),"暂无更多数据~");
                return;
            }

            if (list.size()>=pageCount){
                curIndex++;
            }

            if (isRefresh){
                //刷新数据
                rankAdapter.refreshData(list);
            }else {
                //增加数据
                rankAdapter.addData(list);
            }
        }else {
            if (isRefresh){
                binding.refreshLayout.finishRefresh(false);
            }else {
                binding.refreshLayout.finishLoadMore(false);
            }
            ToastUtil.showToast(getActivity(),"获取配音排行数据失败～");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.dubbing_rank)){
            binding.refreshLayout.autoRefresh();
        }
    }
}
