package com.iyuba.core.talkshow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.lib.R;

import java.util.List;

/**
 *  青少版配音主页
 */
public class TalkShowActivity extends BaseStackActivity implements TalkShowMvpView {

    ImageView ivBack;
    RecyclerView mRecyclerView;
    TextView mTvText;

    private Context mContext;

    private String mDefClass;
    private String mDefClassName;
    private TalkShowAdapter mAdapter;
    private TalkShowPresenter mPresenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talkshow);
        initView();
        //StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.black, getTheme()));

        mContext =this;
        initListener();

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
    }

    private void initView(){
        ivBack = findViewById(R.id.iv_back);
        mRecyclerView = findViewById(R.id.recycler);
        mTvText = findViewById(R.id.tv_title);
    }

    private void initListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Log.d("退出显示13", this.getClass().getName());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==99&&resultCode==RESULT_OK){
            if (data!=null) {
                String classId = data.getStringExtra("classId");
                String className = data.getStringExtra("className");
                mTvText.setText(className);
                //ToastUtil.showToast(mContext,className+classId);
                mDefClass =classId;
                ConfigManager.Instance().putString("talkClassId", classId);
                ConfigManager.Instance().putString("talkClassName", className);
                mPresenter.getLessonList(classId);
            }
        }
    }

    @Override
    public void showMessage(String message) {
       ToastUtil.showToast(mContext,message);
    }

    @Override
    public void getTalkLesson(List<TalkLesson> list) {
        mAdapter.setData(list);
    }
}
