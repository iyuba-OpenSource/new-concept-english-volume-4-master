package com.iyuba.conceptEnglish.lil.concept_other.talkshow_fix.myTalk;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.conceptEnglish.databinding.FragmentMyRankListFixBinding;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.List;

/**
 * @title: 我的配音-收藏和下载界面
 * @date: 2023/8/2 11:28
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Fix_MyTalkListFragment extends BaseViewBindingFragment<FragmentMyRankListFixBinding> {

    public final static String PUBLISH = "publish_type";
    public final static String COLLECT = "my_talk_type";
    public final static String DOWNLOAD = "download_type";
    private final static String TYPE = "my_talk_type";

    private String mType;//Fragment 显示列表的类型
    private List<TalkLesson> lessonList;
    private String mUid;
    private boolean isDeleteAll;

    private Fix_MyTalkAdapter mAdapter;

    public static Fix_MyTalkListFragment getInstance(String type) {
        Fix_MyTalkListFragment fragment = new Fix_MyTalkListFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mType = getArguments().getString(TYPE);
        }
        mUid = String.valueOf(UserInfoManager.getInstance().getUserId());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new Fix_MyTalkAdapter();
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setAdapter(mAdapter);

        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initData(){
        DubDBManager dbManager = DubDBManager.getInstance();
        if (UserInfoManager.getInstance().isLogin()) {
            switch (mType) {
                case COLLECT:
                    lessonList = dbManager.getCollectList(mUid);
                    break;
                case DOWNLOAD:
                    lessonList = dbManager.getDownList();
                    break;
            }
        }
        if (lessonList!=null) {
            mAdapter.setData(lessonList);
        }else {
            ToastUtil.showToast(getContext(),"数据为空");
        }
    }

    public void setDeleteShow(){
        if (lessonList!=null){
            mAdapter.isShowDelete=true;
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setDeleteSure(){
        if (lessonList!=null){
            for (TalkLesson lesson: lessonList){
                if (lesson.isDelete){
                    switch (mType){
                        case COLLECT:
                            DubDBManager.getInstance().deleteCollect(lesson.Id,mUid);
                            break;
                        case DOWNLOAD:
                            DubDBManager.getInstance().deleteDown(lesson.Id);
                            StorageUtil.deleteAudioFile(getContext(), Integer.parseInt(lesson.Id));
                            StorageUtil.deleteVideoFile(getContext(), Integer.parseInt(lesson.Id));
                            break;
                    }
                }
            }
            initData();
        }
    }

    public void setDeleteCancel(){
        if (lessonList!=null){
            mAdapter.isShowDelete=false;
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setDeleteAll(){
        if (lessonList!=null){
            isDeleteAll = !isDeleteAll;
            for (TalkLesson lesson:lessonList){
                lesson.isDelete = isDeleteAll;
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
