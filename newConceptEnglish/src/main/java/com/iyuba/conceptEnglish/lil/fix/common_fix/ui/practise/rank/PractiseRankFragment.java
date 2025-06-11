package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.rank;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.FragmentPractiseRankBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.jn.yyz.practise.model.bean.TestRankingBean;
import com.jn.yyz.practise.vm.PractiseViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版练习题的排行界面
 */
public class PractiseRankFragment extends BaseViewBindingFragment<FragmentPractiseRankBinding> {

    //数据
    private PractiseViewModel practiseViewModel;
    //适配器
    private PractiseRankAdapter rankAdapter;

    //展示类型
    private String showFlag = "D";
    //页码
    private int pageIndex = 1;
    //每页数量
    private static final int SHOW_COUNT = 15;

    public static PractiseRankFragment getInstance(){
        PractiseRankFragment fragment = new PractiseRankFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        practiseViewModel = new ViewModelProvider(requireActivity()).get(PractiseViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initList();
        setCallback();
        loadData(showFlag);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initToolbar(){
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button_normal);
        binding.toolbar.btnBack.setOnClickListener(v->{
            requireActivity().finish();
        });
        binding.toolbar.title.setText("排行榜");
    }

    private void initList(){
        //头部
        List<Pair<String,String>> tabList = new ArrayList<>();
        tabList.add(new Pair<>("D","日榜"));
        tabList.add(new Pair<>("W","周榜"));
        tabList.add(new Pair<>("M","月榜"));

        PractiseRankTypeAdapter typeAdapter = new PractiseRankTypeAdapter(requireActivity(),tabList);
        GridLayoutManager typeManager = new GridLayoutManager(requireActivity(),tabList.size());
        binding.tabView.setLayoutManager(typeManager);
        binding.tabView.setAdapter(typeAdapter);
        typeAdapter.setOnSimpleClickListener(new OnSimpleClickListener<Pair<String, String>>() {
            @Override
            public void onClick(Pair<String, String> pairData) {
                pageIndex = 1;
                showFlag = pairData.first;
                loadData(showFlag);
            }
        });

        //列表
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(requireActivity()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(requireActivity()));
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(true);
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadData(showFlag);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                binding.refreshLayout.setEnableLoadMore(true);
                //刷新数据
                pageIndex = 1;
                loadData(showFlag);
            }
        });

        rankAdapter = new PractiseRankAdapter(requireActivity(),new ArrayList<>());
        LinearLayoutManager rankManager = new LinearLayoutManager(requireActivity());
        binding.recyclerView.setLayoutManager(rankManager);
        binding.recyclerView.setAdapter(rankAdapter);
    }

    private void loadData(String showFlag){
        practiseViewModel.requestExpRankData(showFlag,pageIndex,SHOW_COUNT);
    }

    private void setCallback(){
        practiseViewModel.getTestRankingBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<TestRankingBean>() {
                    @Override
                    public void onChanged(TestRankingBean rankBean) {
                        if (rankBean.getResult()==200){
                            //关闭刷新
                            binding.refreshLayout.finishRefresh(true);
                            binding.refreshLayout.finishLoadMore(true);
                            //更新用户的排行信息
                            updateUserRank(rankBean);

                            //判断数据
                            if (rankBean.getData().size()<=0){
                                ToastUtil.showToast(requireActivity(),"暂无更多数据");
                                return;
                            }

                            //数据显示
                            if (pageIndex>1){
                                //增加数据
                                rankAdapter.addData(rankBean.getData());
                            }else {
                                //刷新数据
                                rankAdapter.refreshData(rankBean.getData());
                            }
                            //页码+1
                            pageIndex+=1;
                        }else {
                            //关闭刷新
                            binding.refreshLayout.finishRefresh(false);
                            binding.refreshLayout.finishLoadMore(false);
                            //显示信息
                            ToastUtil.showToast(requireActivity(),"暂无更多数据");
                        }
                    }
                });
    }

    private void updateUserRank(TestRankingBean rankData){
        binding.bottomLayout.indexPic.setVisibility(View.GONE);
        if (rankData.getMyranking()<=0){
            binding.bottomLayout.indexText.setText("未上榜");
        }else {
            binding.bottomLayout.indexText.setText(String.valueOf(rankData.getMyranking()));
        }
        LibGlide3Util.loadCircleImg(requireActivity(),"http://static1.iyuba.cn/uc_server/"+rankData.getMyImgSrc(),0,binding.bottomLayout.pic);
        binding.bottomLayout.name.setText(rankData.getMyusername());
        binding.bottomLayout.experience.setText(rankData.getMyExp()+"经验");
        //获取经验层级信息
        binding.bottomLayout.grade.setVisibility(View.VISIBLE);
        Pair<String,Integer> showGrade = showCurGrade(rankData.getMyExp());
        binding.bottomLayout.grade.setText("等级："+showGrade.first+"(升级还需"+showGrade.second+"经验)");
    }

    //计算当前的阶段
    private Pair<String,Integer> showCurGrade(int exp){
        if (exp <= 50){
            return new Pair<>("书童",50-exp);
        }else if (exp <= 200){
            return new Pair<>("童生",200-exp);
        }else if (exp <= 500){
            return new Pair<>("秀才",500-exp);
        }else if (exp <= 1000){
            return new Pair<>("举人",1000-exp);
        }else if (exp <= 2000){
            return new Pair<>("解元",2000-exp);
        }else if (exp <= 4000){
            return new Pair<>("贡士",4000-exp);
        }else if (exp <= 7000){
            return new Pair<>("进士",7000-exp);
        }else if (exp <= 12000){
            return new Pair<>("榜眼",12000-exp);
        }else if (exp <= 20000){
            return new Pair<>("探花",2000-exp);
        }else {
            return new Pair<>("状元",0);
        }
    }
}
