package com.iyuba.conceptEnglish.lil.concept_other.me_wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ActivityWalletListBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Reward_history;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 钱包列表界面
 * @date: 2023/8/22 18:45
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WalletListActivity extends BaseViewBindingActivity<ActivityWalletListBinding> implements WalletView{

    private WalletListAdapter listAdapter;
    private WalletPresenter presenter;

    //起始数据
    private int pages = 1;
    //每页的数量
    private int pageCount = 20;
    //是否刷新状态
    private boolean isRefresh = true;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,WalletListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new WalletPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();
        initUserInfo();

        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    /*********************初始化*******************/
    private void initToolbar(){
        binding.toolbar.title.setText("奖励历史记录");
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button);
        binding.toolbar.btnBack.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
        /*binding.toolbar.btnRight.setVisibility(View.INVISIBLE);
        binding.toolbar.btnRight.setBackgroundResource(0);
        binding.toolbar.btnRight.setImageResource(R.drawable.ic_tips);
        binding.toolbar.btnRight.setOnClickListener(v->{
            String showMsg = "当前钱包金额:" + UserInfoManager.getInstance().getMoney() + "元,满10元可在[爱语吧]微信公众号提现(关注绑定爱语吧账号)";
            new AlertDialog.Builder(this)
                    .setTitle("奖励说明")
                    .setMessage(showMsg)
                    .show();
        });*/

        binding.showTag.type.setText("类型");
        binding.showTag.type.setTextColor(ResUtil.getInstance().getColor(R.color.colorPrimary));
        binding.showTag.reward.setText("金额(元)");
        binding.showTag.reward.setTextColor(ResUtil.getInstance().getColor(R.color.colorPrimary));
        binding.showTag.time.setText("时间");
        binding.showTag.time.setTextColor(ResUtil.getInstance().getColor(R.color.colorPrimary));
    }

    private void initUserInfo(){
        String imageUrl = "http://api."+ Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + UserInfoManager.getInstance().getUserId() + "&size=big";
        LibGlide3Util.loadCircleImg(this, imageUrl, R.drawable.ic_w_head, binding.userIcon);
        binding.userName.setText(UserInfoManager.getInstance().getUserName());
        String showTips = "(满1元可以抵扣会员购买，付费全站会员可以提现)";
        binding.userMoney.setText("金额："+UserInfoManager.getInstance().getMoney()+"元\n"+showTips);
    }

    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(true);

        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(WalletListActivity.this)){
                    stopRefreshAndMore(false);
                    ToastUtil.showToast(WalletListActivity.this,"请链接网络后重试~");
                    return;
                }

                isRefresh = false;
                presenter.getRewardData(UserInfoManager.getInstance().getUserId(),pages,pageCount);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(WalletListActivity.this)){
                    stopRefreshAndMore(false);
                    ToastUtil.showToast(WalletListActivity.this,"请链接网络后重试~");
                    return;
                }

                pages = 1;
                isRefresh = true;
                presenter.getRewardData(UserInfoManager.getInstance().getUserId(),pages,pageCount);
            }
        });

        listAdapter = new WalletListAdapter(this,new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(listAdapter);
    }

    /**********************刷新数据*****************/
    private void refreshData(){
        binding.refreshLayout.autoRefresh();
    }

    private void stopRefreshAndMore(boolean isFinish){
        binding.refreshLayout.finishRefresh(isFinish);
        binding.refreshLayout.finishLoadMore(isFinish);
    }

    /********************回调数据********************/
    @Override
    public void showRewardHistory(List<Reward_history> list) {
        if (list==null){
            stopRefreshAndMore(false);
            ToastUtil.showToast(this,"查询奖励的历史记录失败~");
            return;
        }

        stopRefreshAndMore(true);
        if (list.size()==0){
            if (isRefresh){
                ToastUtil.showToast(this,"当前账号暂无奖励记录~");
            }else {
                ToastUtil.showToast(this,"当前账号暂无更多奖励记录~");
            }
            return;
        }

        if (list.size()>0){
            pages++;
        }

        if (isRefresh){
            listAdapter.refreshData(list,false);
        }else {
            listAdapter.refreshData(list,true);
        }
    }
}
