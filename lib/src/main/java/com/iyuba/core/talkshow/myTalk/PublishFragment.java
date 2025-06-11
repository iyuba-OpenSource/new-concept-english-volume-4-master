package com.iyuba.core.talkshow.myTalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.talkshow.lesson.watch.WatchDubbingActivity;
import com.iyuba.lib.R;

import java.util.List;

/**
 * 我的发布Fragment
 */
public class PublishFragment extends Fragment implements PublishMvpView{

    public static PublishFragment newInstance() {
        return new PublishFragment();
    }

    RecyclerView mRecyclerView;
    View mLoadingLayout;
    View mEmptyView;
    TextView mEmptyTextTv;


    PublishPresenter mPresenter;
    PublishAdapter mAdapter;

    private int mUid;
    List<Ranking> mData;
    private boolean isDeleteAll;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PublishPresenter(DataManager.getInstance());
        mPresenter.attachView(this);
        mUid = UserInfoManager.getInstance().getUserId();
        mContext= getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_publish, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        mRecyclerView = view.findViewById(R.id.released_recycler_view);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mEmptyView = view.findViewById(R.id.empty_view);
        mEmptyTextTv = view.findViewById(R.id.empty_text);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new PublishAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.getLessonList(mUid);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void showLoadingLayout() {
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingLayout() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void setEmptyData() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setReleasedData(List<Ranking> data) {
        mData= data;
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void startWatchDubbingActivity(TalkLesson voa, Ranking ranking) {
        Intent intent = WatchDubbingActivity.buildIntent(getContext(), ranking, voa, mUid);
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
        //setDeleteCancel();
    }

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
