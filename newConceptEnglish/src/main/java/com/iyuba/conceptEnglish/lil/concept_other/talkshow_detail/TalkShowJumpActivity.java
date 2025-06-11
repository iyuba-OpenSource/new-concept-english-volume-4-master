package com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.BaseActivity;
import com.iyuba.conceptEnglish.sqlite.db.TalkShowDBManager;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.talkshow.lesson.rank.RankFragment;
import com.iyuba.core.talkshow.lesson.recommend.RecommendFragment;

import java.util.List;

/**
 * @title: 口语秀-功能跳转界面
 * @date: 2023/5/17 16:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TalkShowJumpActivity extends BaseActivity {

    //排行
    public static final String JUMP_RANK = "rank";
    //专辑
    public static final String JUMP_ALBUM = "album";


    private static final String JUMP_TYPE = "type";
    private static final String LESSON_VOAID = "voaId";

    private TextView title;

    public static void start(Context context,String type,String voaId){
        Intent intent = new Intent();
        intent.setClass(context,TalkShowJumpActivity.class);
        intent.putExtra(JUMP_TYPE,type);
        intent.putExtra(LESSON_VOAID,voaId);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_talkshow_jump;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Button backView = findViewById(R.id.button_back);
        backView.setOnClickListener(v->{
            finish();
        });
        title = findViewById(R.id.study_text);
        Button shareView = findViewById(R.id.share);
        shareView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void loadData() {
        String type = getIntent().getStringExtra(JUMP_TYPE);
        initToolbar(type);
        initFragment(type);
    }

    //标题
    private void initToolbar(String type){
        if (type.equals(JUMP_RANK)){
            title.setText("配音排行");
        }else if (type.equals(JUMP_ALBUM)){
            title.setText("更多内容");
        }
    }

    //界面
    private void initFragment(String type){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (type.equals(JUMP_RANK)){
            RankFragment rankFragment = RankFragment.newInstance(transData());
            transaction.add(R.id.frameLayout,rankFragment).show(rankFragment);
        }else if (type.equals(JUMP_ALBUM)){
            String voaId = getIntent().getStringExtra(LESSON_VOAID);
            RecommendFragment recommendFragment = RecommendFragment.newInstance(transMultiData(), voaId);
            transaction.add(R.id.frameLayout,recommendFragment).show(recommendFragment);
        }
        transaction.commitAllowingStateLoss();
    }

    //获取单个数据
    private TalkLesson transData(){
        String voaId = getIntent().getStringExtra(LESSON_VOAID);
        return TalkShowDBManager.getInstance().findTalkByVoaId(voaId);
    }

    //获取当前数据数据
    private List<TalkLesson> transMultiData(){
        int bookId = ConfigManager.Instance().loadInt("curBook");
        return TalkShowDBManager.getInstance().findTalkByBookId(String.valueOf(bookId));
    }
}
