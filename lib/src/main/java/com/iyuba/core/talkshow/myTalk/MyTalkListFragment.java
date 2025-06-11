package com.iyuba.core.talkshow.myTalk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.lib.R;

import java.util.List;

/**
 * 我的配音中的fragment.
 */
public class MyTalkListFragment extends Fragment {
    public final static String PUBLISH = "publish_type";
    public final static String COLLECT = "my_talk_type";
    public final static String DOWNLOAD = "download_type";
    private final static String TYPE = "my_talk_type";

    RecyclerView mRecyclerView;

    public static MyTalkListFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(TYPE, type);
        MyTalkListFragment fragment = new MyTalkListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String mType;//Fragment 显示列表的类型
    private List<TalkLesson> lessonList;
    private String mUid;
    private boolean isDeleteAll;

    private MyTalkAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rank_list, container, false);
        mRecyclerView = view.findViewById(R.id.recycler);
        return view;
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
        mAdapter = new MyTalkAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        initData();


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
