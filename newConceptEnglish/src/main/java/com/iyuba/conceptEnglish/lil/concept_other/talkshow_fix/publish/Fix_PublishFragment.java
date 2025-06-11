package com.iyuba.conceptEnglish.lil.concept_other.talkshow_fix.publish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.conceptEnglish.databinding.FragmentPublishFixBinding;
import com.iyuba.conceptEnglish.lil.concept_other.talkshow_fix.publish.watch.Fix_WatchDubbingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.talkshow.myTalk.PublishMvpView;
import com.iyuba.core.talkshow.myTalk.PublishPresenter;

import java.util.List;

/**
 * @title: 我的配音-发布界面
 * @date: 2023/8/2 11:13
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Fix_PublishFragment extends BaseViewBindingFragment<FragmentPublishFixBinding> implements PublishMvpView {

    PublishPresenter mPresenter;
    Fix_PublishAdapter mAdapter;

    private int mUid;
    List<Ranking> mData;
    private boolean isDeleteAll;
    private Context mContext;

    public static Fix_PublishFragment getInstance(){
        Fix_PublishFragment fragment = new Fix_PublishFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new PublishPresenter(DataManager.getInstance());
        mPresenter.attachView(this);
        mUid = UserInfoManager.getInstance().getUserId();
        mContext= getContext();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new Fix_PublishAdapter();
        binding.releasedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.releasedRecyclerView.setAdapter(mAdapter);

        mPresenter.getLessonList(mUid);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    /************************接口数据**********************/
    @Override
    public void showLoadingLayout() {
        binding.loadingLayout.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingLayout() {
        binding.loadingLayout.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void setEmptyData() {
        binding.releasedRecyclerView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public void setReleasedData(List<Ranking> data) {
        mData= data;
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        binding.releasedRecyclerView.setVisibility(View.VISIBLE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void startWatchDubbingActivity(TalkLesson voa, Ranking ranking) {
        Intent intent = Fix_WatchDubbingActivity.buildIntent(getContext(), ranking, voa, mUid);
        startActivity(intent);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(getContext(),resId);
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showToast(getContext(),msg);
    }

    @Override
    public void deleteSuccess() {
        mPresenter.getLessonList(mUid);
    }

    /***********************辅助功能*******************/
    public void setDeleteShow(){
        if (mData!=null){
            mAdapter.isShowDelete=true;
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setDeleteSure(){
        if (mData!=null){
            final StringBuilder deleteList= new StringBuilder();
            for (Ranking lesson: mData){
                if (lesson.isDelete) {
                    if (!TextUtils.isEmpty(deleteList.toString())) {
                        deleteList.append(",");
                    }
                    deleteList.append(lesson.id);
                }
            }
            if (TextUtils.isEmpty(deleteList.toString())){
                showToast("您还没有选择任何内容！");
            }else {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("确定要删除选中的配音吗？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPresenter.deleteList(deleteList.toString(),mUid);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        }
    }

    public void setDeleteCancel(){
        if (mData!=null){
            mAdapter.isShowDelete=false;
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setDeleteAll(){
        if (mData!=null){
            isDeleteAll = !isDeleteAll;
            for (Ranking ranking:mData){
                ranking.isDelete = isDeleteAll;
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
