package com.iyuba.core.talkshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.event.TalkClassEvent;
import com.iyuba.core.talkshow.talkClass.TalkClassActivity;
import com.iyuba.lib.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 *   配音的Fragment
 */
public class TalkShowFragment extends Fragment implements TalkShowMvpView {

    RecyclerView mRecyclerView;
    TextView mTvText;
    ImageView mIvLesson;

    ImageView ivBack;

    private Context mContext;

    private String mDefClass;
    private String mDefClassName;
    private TalkShowAdapter mAdapter;
    private TalkShowPresenter mPresenter;

    public static TalkShowFragment newInstance() {
        return new TalkShowFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk_show, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        mRecyclerView = view.findViewById(R.id.recycler);
        mTvText = view.findViewById(R.id.tv_title);
        mIvLesson = view.findViewById(R.id.iv_lesson_class);
        ivBack = view.findViewById(R.id.iv_back);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext =getContext();
        ivBack.setImageResource(R.drawable.lib_back_button);
        ivBack.setOnClickListener(v -> {
            requireActivity().finish();
            Log.d("退出显示14", this.getClass().getName());
        });
        mDefClass = ConfigManager.Instance().loadString("talkClassId");
        mDefClassName = ConfigManager.Instance().loadString("talkClassName");
        if (TextUtils.isEmpty(mDefClass)){
            mDefClass = "278";
            mDefClassName = "青少版StarterA";
        }
        mTvText.setText(mDefClassName);

        mPresenter = new TalkShowPresenter();
        mPresenter.attachView(this);
        mPresenter.getLessonList(mDefClass);

        mAdapter =new TalkShowAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(null);

        mIvLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* bookSelect */
                startActivityForResult(new Intent(mContext, TalkClassActivity.class),99);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showMessage(String message) {
        ToastUtil.showToast(mContext,message);
    }

    @Override
    public void getTalkLesson(List<TalkLesson> list) {
        mAdapter.setData(list);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTalkClass(TalkClassEvent event){
        mTvText.setText(event.className);
        //ToastUtil.showToast(mContext,className+classId);
        mDefClass =event.classId;
        ConfigManager.Instance().putString("talkClassId", event.classId);
        ConfigManager.Instance().putString("talkClassName", event.className);
        mPresenter.getLessonList(event.classId);
    }

    private static final List<Activity> activityList=new ArrayList<>();

    public static void addActivity(Activity a){
        activityList.add(a);
    }

    public static void clearActivity(){
        for (int i = 0; i < activityList.size(); i++) {
            Activity item = activityList.get(i);
            if (!item.isFinishing()){
                item.finish();
                Log.d("退出显示15",item.getClass().getName());
            }
        }
    }
}