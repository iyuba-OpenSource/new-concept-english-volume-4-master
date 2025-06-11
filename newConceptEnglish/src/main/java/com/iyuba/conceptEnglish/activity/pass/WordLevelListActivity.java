package com.iyuba.conceptEnglish.activity.pass;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.PassWordListAdapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.protocol.UploadTestRecordRequest;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.mode.WordPassUser;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassUserOp;
import com.iyuba.conceptEnglish.util.NetWorkState;
import com.iyuba.conceptEnglish.util.PassJson;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.core.lil.view.PermissionMsgDialog;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WordLevelListActivity extends BaseStackActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.btn_back)
    ImageView btnBack;

    @BindView(R.id.btn_share)
    ImageView btnShare;

    @BindView(R.id.tv_title)
    TextView tvTitle;


    private PassWordListAdapter mAdapter;
    private Context mContext;
    private List<VoaWord2> mWordList = new ArrayList<>();
    private VoaWordOp voaWordOp;
    private int courseId;
    private WordPassUserOp op;

    //是否可以闯关
    private boolean canExercise = true;

    public static void start(Context context,int position,boolean isCanExercise){
        Intent intent = new Intent();
        intent.setClass(context,WordLevelListActivity.class);
        intent.putExtra(StrLibrary.exercise,isCanExercise);
        intent.putExtra("courseId",position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        ButterKnife.bind(this);

        //根据闯关解锁进度显示
        canExercise = getIntent().getBooleanExtra(StrLibrary.exercise,true);
        Button btnExercise = findViewById(R.id.btnExcerise);
        int bgResId = 0;
        if (canExercise){
            bgResId = R.drawable.bg_next_word_normal;
        }else {
            bgResId = R.drawable.bg_next_word_pressed;
        }
        btnExercise.setBackgroundResource(bgResId);

        mContext = this;
        op = new WordPassUserOp(mContext);

        courseId = getIntent().getIntExtra("courseId", 1);
        initToolbar();
        voaWordOp = new VoaWordOp(mContext);
        int curBookForPass = ConfigManager.Instance().getCurrBookforPass();
        String bookId = ConfigManager.Instance().getCurrBookId();
        if (curBookForPass > 4) {
            String currBook = ConfigManager.Instance().getCurrBookTitle();
            int extra = 0;
            if (currBook.contains("新概念青少版1B") || currBook.contains("新概念青少版2B") || currBook.contains("新概念青少版3B")) {
                extra = 15;
            } else if (currBook.contains("新概念青少版4B") || currBook.contains("新概念青少版5B")) {
                extra = 24;
            }
            if (extra > 0 && courseId == 1) {
                courseId += extra;
            }
            try {
                mWordList = WordChildDBManager.getInstance().findDataByVoaId(bookId, String.valueOf(courseId));
                mAdapter = new PassWordListAdapter(this, mWordList);
            } catch (Exception e) {
                e.printStackTrace();
                mAdapter = new PassWordListAdapter(this, mWordList);
            }
            mAdapter.setGreaterThanFour(true);
        } else {
            try {
                mWordList = voaWordOp.findDataByVoaId(curBookForPass * 1000 + courseId);
                mAdapter = new PassWordListAdapter(this, mWordList);
            } catch (Exception e) {
                e.printStackTrace();
                mAdapter = new PassWordListAdapter(this, mWordList);
            }
        }
        new Thread() {
            @Override
            public void run() {
                continueOpLoad();
            }
        }.start();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener((holder, position) -> {
            Intent intent = new Intent(this, WordEvalActivity.class);
            intent.putExtra("list", (Serializable) mWordList);
            intent.putExtra("wordIndex", position);
            startActivity(intent);
        });
    }

    private void continueOpLoad() {
        List<WordPassUser> errorList = op.getUploadError();
        if (errorList.isEmpty()) {
            return;
        }
        //根据voaId分割list为Map再转换为list，根据失败或成功来更新状态
        Map<Integer, List<WordPassUser>> map = new HashMap<>();
        errorList.forEach(item -> {
            List<WordPassUser> list = map.get(item.voa_id);
            Stream<WordPassUser> stream = errorList.stream().filter(wordPassUser -> item.voa_id == wordPassUser.voa_id);
            if (list == null) {
                map.put(item.voa_id, stream.collect(Collectors.toList()));
            }
        });
        map.forEach((integer, list) -> {
            try {
                upLoadError(integer, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void upLoadError(int voaId, List<WordPassUser> list) throws Exception {
        //此处也有点瑕疵，窃以为应该一个线程里进行for，但是因为是复用之前的封装，有点无奈吧
        int bookId = voaWordOp.getBookidByVoaid(String.valueOf(voaId));
        String body = PassJson.buildJsonForTestRecordDouble(list, voaId, bookId);
        new UploadTestRecordRequest(body, Constant.URL_UPDATE_EXAM_RECORD, new UploadTestRecordRequest.UpLoadRecordCall() {
            @Override
            public void onSuccess() {
                op.updateUpLoadSuccess(voaId);
            }

            @Override
            public void onError() {

            }
        });
    }

    @OnClick(R.id.btnExcerise)
    void jumpExcerise() {//闯关
        if (mWordList.isEmpty()) {
            ToastUtil.showToast(this, "未查询到单词数据");
            return;
        }

        if (!canExercise){
            ToastUtil.showToast(this,"通关前面的单元后解锁此单元的闯关内容");
            return;
        }

        WordExerciseActivity.start(this,(Serializable) mWordList);
        finish();
    }

    @OnClick(R.id.btnLearn)
    void jumpLearn() {//学习
        //不请求权限，在详情的界面在请求权限
        jumpToStudy();
    }

    //跳转到学习界面
    private void jumpToStudy(){
        if (mWordList.isEmpty()){
            ToastUtil.showToast(this,"未查询到当前单元的单词数据");
            return;
        }

        Intent intent = new Intent(this, WordEvalActivity.class);
        intent.putExtra("list", (Serializable) mWordList);
        startActivity(intent);
    }

    private void initToolbar() {
        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> {
        });
        if (ConfigManager.Instance().getCurrBookforPass() > 4) {
            tvTitle.setText(MessageFormat.format("Unit {0} 单词", courseId));
        } else {
            tvTitle.setText(MessageFormat.format("Lesson {0} 单词", courseId));
        }
    }
}
