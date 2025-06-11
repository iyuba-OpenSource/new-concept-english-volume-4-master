package com.iyuba.conceptEnglish.activity.pass;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.concept_other.util.ConceptHomeRefreshUtil;
import com.iyuba.conceptEnglish.protocol.DataCollectRequest;
import com.iyuba.conceptEnglish.protocol.UploadTestRecordRequest;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.mode.WordPassUser;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassUserOp;
import com.iyuba.conceptEnglish.util.DeviceInfoUtil;
import com.iyuba.conceptEnglish.util.NetWorkState;
import com.iyuba.conceptEnglish.util.PassJson;
import com.iyuba.conceptEnglish.widget.OutsideClickDialog;
import com.iyuba.conceptEnglish.widget.cdialog.CustomDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * 单词闯关练习
 */
public class WordExerciseActivity extends AppCompatActivity {
    @BindView(R.id.btn_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView mTitle;

    @BindView(R.id.rb_1)
    TextView rb_1;
    @BindView(R.id.rb_2)
    TextView rb_2;
    @BindView(R.id.rb_3)
    TextView rb_3;
    @BindView(R.id.rb_4)
    TextView rb_4;

    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.txt_word)
    TextView txt_word;


    @BindView(R.id.ll_option)
    ConstraintLayout ll_option;
    @BindView(R.id.ll_bottom)
    LinearLayout ll_bottom;


    private VoaWordOp voaWordOp;
    private List<VoaWord2> mWordList;
    private List<VoaWord2> mChoiceList;//获取错误选项的列表
    private List<Integer> mRandomList = new ArrayList<>(); // 3个其他的错误选项
    private List<String> mRandomChoice = new ArrayList<>();
    private final Random random = new Random();
    private int mPosQuestion = 0; //当前的题号
    private int mRightPos = -1; //正确选项位置随机
    private int mRightCount = 0;
    private boolean mIsCanClick = true;
    private int mClickPos;
    private CustomDialog mAlertDialog;
    private View mSuccessView;
    private DeviceInfoUtil mDeviceInfo;
    private String mBeginTime;

    private WordPassOp wordPassOp;

    private WordPassUserOp wordPassUserOp;

    private boolean isChildWord;


    public static void start(Context context, Serializable wordList){
        Intent intent = new Intent();
        intent.setClass(context,WordExerciseActivity.class);
        intent.putExtra("list",wordList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excerise_word);
        ButterKnife.bind(this);

        voaWordOp = new VoaWordOp(this);
        wordPassOp = new WordPassOp(this);
        wordPassUserOp = new WordPassUserOp(this);

        mAlertDialog = new CustomDialog(this, R.style.dialog_style);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mWordList = (List<VoaWord2>) getIntent().getSerializableExtra("list");

        //这里将显示的单词随机处理下
        Collections.shuffle(mWordList);

        int currBookForPass = ConfigManager.Instance().getCurrBookforPass();
        isChildWord = currBookForPass > 4;
        if (isChildWord) {
            //这个不能设置的太小
            mChoiceList = WordChildDBManager.getInstance().findDataByBookId(ConfigManager.Instance().getCurrBookId());
            //mChoiceList = mWordList;
        } else {
//            mChoiceList = voaWordOp.findDataByBookId(currBookForPass * 1000, (currBookForPass + 1) * 1000); //随机三个选项
            mChoiceList = voaWordOp.findDataByBookIdRandom100();
        }

        mDeviceInfo = new DeviceInfoUtil(this);
        mBeginTime = mDeviceInfo.getCurrentTime();
        mTitle.setText(mPosQuestion + 1 + "/" + mWordList.size());
        initChoice();

        ivTitleBack.setOnClickListener(v -> onBackPressed());
    }

    private void setTranslation() {
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.item_animation_fall_down);
        ll_option.clearAnimation();
        ll_option.startAnimation(animation1);
    }

    private void initChoice() {
        mRandomList.clear();
        mRandomChoice.clear();
        reset();
        int randomNum;
        String item;
        while (mRandomList.size() < 3) {
            randomNum = random.nextInt(mChoiceList.size());//随机生成一个数（0到 size）
            item = mChoiceList.get(randomNum).def;
            //randomNum
            if (!mRandomList.contains(randomNum) && !mRandomChoice.contains(item) && !TextUtils.isEmpty(item)
                    && !item.equals(mWordList.get(mPosQuestion).def)) { //不包含重复选项
                mRandomList.add(randomNum);
                mRandomChoice.add(item);
            }
            Timber.d("获取错误选项循环" + randomNum + "总数量" + mChoiceList.size());//太容易死循环了
        }

        initChoiceData();
        initBottomData();
    }

    private void initChoiceData() {
        txt_word.setText(mWordList.get(mPosQuestion).word);

        //根据要求，播放选中数据的音频
        VoaWord2 word2 = mWordList.get(mPosQuestion);
        startPlay(word2.audio);

        if (mRightPos == 0 || mRightPos == 1) {
            rb_1.setText(mWordList.get(mPosQuestion).def);
            rb_2.setText(mChoiceList.get(mRandomList.get(0)).def);
            rb_3.setText(mChoiceList.get(mRandomList.get(1)).def);
            rb_4.setText(mChoiceList.get(mRandomList.get(2)).def);
        } else if (mRightPos == 2) {
            rb_2.setText(mWordList.get(mPosQuestion).def);
            rb_1.setText(mChoiceList.get(mRandomList.get(0)).def);
            rb_3.setText(mChoiceList.get(mRandomList.get(1)).def);
            rb_4.setText(mChoiceList.get(mRandomList.get(2)).def);
        } else if (mRightPos == 3) {
            rb_3.setText(mWordList.get(mPosQuestion).def);
            rb_2.setText(mChoiceList.get(mRandomList.get(0)).def);
            rb_1.setText(mChoiceList.get(mRandomList.get(1)).def);
            rb_4.setText(mChoiceList.get(mRandomList.get(2)).def);
        } else if (mRightPos == 4) {
            rb_4.setText(mWordList.get(mPosQuestion).def);
            rb_2.setText(mChoiceList.get(mRandomList.get(0)).def);
            rb_3.setText(mChoiceList.get(mRandomList.get(1)).def);
            rb_1.setText(mChoiceList.get(mRandomList.get(2)).def);
        }
    }

    private void initBottomData() {
        if (mPosQuestion == mWordList.size() - 1) {
            btn_next.setText("完成");
        }
    }

    /**
     * 重置状态
     */
    private void reset() {
        mIsCanClick = true;

        ll_bottom.setVisibility(View.GONE);
        mRightPos = -1;

        //R.drawable.bg_option_white
        rb_1.setBackgroundResource(R.drawable.word_exercise_white);
        rb_2.setBackgroundResource(R.drawable.word_exercise_white);
        rb_3.setBackgroundResource(R.drawable.word_exercise_white);
        rb_4.setBackgroundResource(R.drawable.word_exercise_white);
        mRightPos = random.nextInt(5);
    }

    /**
     * 设置正确背景色
     */
    private void setBackground(TextView view) {
        if (!mIsCanClick) {
            return;
        }
        mIsCanClick = false;
        ll_bottom.setVisibility(View.VISIBLE);
        int isTrue = 0;
        if (mClickPos == mRightPos || (mClickPos == 1 && mRightPos == 0)) {
            view.setBackgroundResource(R.drawable.word_exercise_right);
            mRightCount = mRightCount + 1;
            isTrue = 1;
        } else {
            isTrue = 0;
            view.setBackgroundResource(R.drawable.word_exercise_error);
            setRightBackground();
        }
        //voaWord 当前的单词信息
        VoaWord2 voaWord = mWordList.get(mPosQuestion);
        int voaid = Integer.parseInt(voaWord.voaId);

        //dbId ：获得数据库里最终存储的
        Timber.d("Update word voaId: %d", voaid);
        wordPassUserOp.updateWord(voaid, voaWord.word, voaWord.position, isTrue,voaWord.unitId,"1");
    }

    @OnClick(R.id.rb_1)
    void clickOption1() {
        mClickPos = 1;
        setBackground(rb_1);
    }

    @OnClick(R.id.rb_2)
    void clickOption2() {
        mClickPos = 2;
        setBackground(rb_2);
    }

    @OnClick(R.id.rb_3)
    void clickOption3() {
        mClickPos = 3;
        setBackground(rb_3);
    }

    @OnClick(R.id.rb_4)
    void clickOption4() {
        mClickPos = 4;
        setBackground(rb_4);
    }

    private void setRightBackground() {
        if (mRightPos == 1 || mRightPos == 0) {
            rb_1.setBackgroundResource(R.drawable.word_exercise_right);
        } else if (mRightPos == 2) {
            rb_2.setBackgroundResource(R.drawable.word_exercise_right);
        } else if (mRightPos == 3) {
            rb_3.setBackgroundResource(R.drawable.word_exercise_right);
        } else if (mRightPos == 4) {
            rb_4.setBackgroundResource(R.drawable.word_exercise_right);
        }
    }

    private void isContinue() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        if (mRightCount < mWordList.size()*0.8) {
            StringBuilder sb = new StringBuilder();
            sb.append("共做：").append(mPosQuestion + 1).append("题，做对：").append(mRightCount).append("题").
                    append("，正确比例：").append(decimalFormat.format(mRightCount * 1.0f / (mPosQuestion + 1) * 100)).append("%");
            showFailAlertDialog(sb.toString(), "闯关失败", true);
        } else {
            showCompleteDialog();
        }
    }

    @OnClick(R.id.btn_next)
    void nextWord() {
        try {
            if (mPosQuestion == mWordList.size() - 1) {
                isContinue();
                return;
            }

            setTranslation();//动画
            if (mPosQuestion < mWordList.size()) {
                ++mPosQuestion;
                mTitle.setText(mPosQuestion + 1 + "/" + mWordList.size());
                initChoice();//生成错误选项
            } else {
                ToastUtil.showToast(this, "已经是最后一个单词了");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        if (mPosQuestion < mWordList.size() - 1) {
            showFailAlertDialog("尚未闯关成功，即将退出？", "", false);
        } else {
            new Thread(new UpdateStudyRecordThread()).start();
            super.onBackPressed();
        }
    }

    /**
     * 闯关失败时 的提示框，上传记录
     * @param contentStr
     * @param titleStr
     * @param isShowCancel
     */
    private void showFailAlertDialog(String contentStr, String titleStr, boolean isShowCancel) {
        View merge_view = LayoutInflater.from(this).inflate(R.layout.dialog_custom_wrong, null);//要填充的layout
        mAlertDialog.setContentView(merge_view);
        TextView content = merge_view.findViewById(R.id.tv_dialog_content);//提示的内容
        TextView title = merge_view.findViewById(R.id.tv_dialog_title);//提示的内容
        View line = merge_view.findViewById(R.id.line);//提示的内容
        Button rechCancleBtn = merge_view.findViewById(R.id.btn_dialog_no);
        Button rechOkBtn = merge_view.findViewById(R.id.btn_dialog_yes);
        content.setText(contentStr);
        if (!TextUtils.isEmpty(titleStr)) {
            title.setText(titleStr);
        }
        if (isShowCancel) {
            rechCancleBtn.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            rechCancleBtn.setText("错误列表");
            rechCancleBtn.setVisibility(View.GONE);
        } else {
            rechCancleBtn.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        mAlertDialog.show();
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mAlertDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.8); //设置宽度
        mAlertDialog.getWindow().setAttributes(lp);

        //分享领红包 按钮
        rechOkBtn.setOnClickListener(v -> {
            new Thread(new UpdateStudyRecordThread()).start();
            finish();
            Log.d("当前退出的界面0017", getClass().getName());
        });
    }

    /**
     * 闯关成功时候的提示框，上传记录
     */
    private void showCompleteDialog() {
        //提交单词闯关数据
        new Thread(this::updatePassData).start();

        final Dialog dialog = new OutsideClickDialog(this, R.style.Dialog) {
            @Override
            protected void onTouchOutside() {
                ToastUtil.showToast(WordExerciseActivity.this, "恭喜您闯关成功，请开始下一关吧！");
                onBackPressed();
            }
        };
        LayoutInflater inflater = LayoutInflater.from(this);
        mSuccessView = inflater.inflate(R.layout.dialog_word_share, null);
        TextView txtStage = mSuccessView.findViewById(R.id.txt_stage_num);
        TextView txtWordNum = mSuccessView.findViewById(R.id.txt_word_num);
        ImageView ic_close = mSuccessView.findViewById(R.id.iv_close);
        Button button = mSuccessView.findViewById(R.id.btn_share);
        if (InfoHelper.getInstance().openShare()){
            button.setVisibility(View.VISIBLE);
        }else {
            button.setVisibility(View.GONE);
        }
        dialog.setContentView(mSuccessView);

        int courseId;

        if (isChildWord) {
            courseId = Integer.parseInt(mWordList.get(0).unitId);
        } else {
            courseId = Integer.parseInt(mWordList.get(0).voaId);
        }
        txtStage.setText("恭喜您成功闯过" + (isChildWord ? courseId : courseId % 1000) + "关");
        txtWordNum.setText("共学习单词" + mWordList.size() + "个");
        dialog.show();

        int allCourseNum = 1144;
        int currBookForPass = ConfigManager.Instance().getCurrBookforPass();//书的id
        switch (currBookForPass) {
            case 1:
                allCourseNum = 1144;
                break;
            case 2:
                allCourseNum = 2096;
                break;
            case 3:
                allCourseNum = 3060;
                break;
            case 4:
                allCourseNum = 4048;
                break;
        }

        if (isChildWord) {
            //存入 unitId 和 书的ID 原本是voaID 不知道voaID有什么用处
            //如果是最新的闯关课程才记录关数
            int laseLessonID = Integer.parseInt(mWordList.get(0).unitId) + 1;//这个是 关数 原来是voaID
            if (laseLessonID > wordPassOp.getCurrPassNum(currBookForPass)) {
                wordPassOp.updateVoaId(laseLessonID, currBookForPass);// 4,5,6,7,8,9-----16
            }
        } else if (courseId < allCourseNum) {//courseId == wordPassOp.getCurrPassNum(currBookForPass) &&
            Timber.tag("Comparation for").d(courseId + " " + wordPassOp.getCurrPassNum(currBookForPass));
            if ((courseId + 1) % 1000 > wordPassOp.getCurrPassNum(currBookForPass)) {
                wordPassOp.updateVoaId(courseId + 1, currBookForPass);
            }
        }

        //刷新数据显示
        ConceptHomeRefreshUtil.getInstance().setRefreshState(true);

        ic_close.setOnClickListener(v -> {
            dialog.dismiss();
            ToastUtil.showToast(WordExerciseActivity.this, "恭喜您闯关成功，请开始下一关吧！");
            onBackPressed();
            //startActivity(new Intent(ExerciseWordActivity.this, MainFragmentActivity.class));
        });

        button.setOnClickListener(v -> {
            Intent intent = new Intent(WordExerciseActivity.this, WordShareActivity.class);
            intent.putExtra("courseId", (isChildWord ? courseId : courseId % 1000));
            intent.putExtra("wordNum", mWordList.size());
            intent.putExtra("rate", mRightCount * 1.0f / (mPosQuestion + 1) * 100 + "%");
            startActivity(intent);
            onBackPressed();
        });
    }

    /**
     * 失败和成功的时候都会调用 提交学习记录
     */
    class UpdateStudyRecordThread implements Runnable {
        @Override
        public void run() {
            if (TextUtils.isEmpty(mBeginTime)) {
                return;
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
            String endTime = df.format(new Date());
            String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
            String endFlag = "1";
            String lesson = "concept"; // lesson应该上传用户使用的哪一款app的名称
            String lessonId = mWordList.get(0).voaId == null ? "1001" : mWordList.get(0).voaId;
            String testNumber = String.valueOf(mPosQuestion);

            final String testMode = "1";
            String userAnswer = "";
            String score = "0";
            SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
            String sign = uid + mBeginTime + dft.format(System.currentTimeMillis());

            if (NetWorkState.isConnectingToInternet()) {
                try {
                    ClientSession.Instace().asynGetResponse(new DataCollectRequest(uid, mBeginTime, endTime, lesson, lessonId, testNumber, mWordList.size() + "", testMode, userAnswer, score, endFlag, "", sign,false), new IResponseReceiver() {
                        @Override
                        public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                        }
                    }, null, null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 用于提交详细的答题情况，包含每个题的选择，只在成功的时候调用
     */
    private void updatePassData() {
        try {
            int voaId = Integer.parseInt(mWordList.get(0).voaId);

            //使用网络接口上传已经成功的数据[可能存在上传不上去的问题]
            Timber.d("Progress(voaId): %s", voaId);
            List<WordPassUser> allWordsData = wordPassUserOp.findData(voaId);
            Timber.d("更新网络记录errorList" + allWordsData.size());
            String url = Constant.URL_UPDATE_EXAM_RECORD;//updateExamRecord
            //仅青少版的单词闯关 用到bookid
            int bookid=voaWordOp.getBookidByVoaid(voaId+"");
            String body = PassJson.buildJsonForTestRecordDouble(allWordsData, voaId,bookid);
            UploadTestRecordRequest up = new UploadTestRecordRequest(body, url, new UploadTestRecordRequest.UpLoadRecordCall() {
                @Override
                public void onSuccess() {
                    //成功之后才能保存数据
                    new WordPassUserOp(WordExerciseActivity.this).updateWordOpLoad(String.valueOf(voaId));
                }

                @Override
                public void onError() {
                    //失败无法保存数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(WordExerciseActivity.this)
                                    .setTitle("闯关进度")
                                    .setMessage("上传闯关进度失败，闯关进度可能会丢失，是否重新上传？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new Thread(() -> updatePassData()).start();
                                        }
                                    }).setNegativeButton("取消",null)
                                    .setCancelable(false)
                                    .create().show();
                        }
                    });
                }
            });
            String result = up.getResultByName("result");
            String jifen = up.getResultByName("jiFen");
            LogUtils.e("积分:" + jifen + "结果   " + result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopPlay();
        EventBus.getDefault().post(new WordPassEvent());
    }

    /*****************音频*****************/
    private MediaPlayer mediaPlayer;
    //音频链接
    private String curPlayUrl = null;

    //播放音频
    private void startPlay(String audioUrl){
        stopPlay();

        try {
            if (mediaPlayer==null){
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        ToastUtil.showToast(WordExerciseActivity.this,"播放音频错误");
                        return true;
                    }
                });
            }


            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(audioUrl));

            if (curPlayUrl!=null&&curPlayUrl.equals(audioUrl)){
                mediaPlayer.start();
            }else {
                curPlayUrl = audioUrl;
                mediaPlayer.prepare();
            }
        }catch (Exception e){
            ToastUtil.showToast(this,"播放音频错误");
        }
    }

    //停止音频
    private void stopPlay(){
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }
}

