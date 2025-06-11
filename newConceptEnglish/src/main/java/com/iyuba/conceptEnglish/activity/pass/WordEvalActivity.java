package com.iyuba.conceptEnglish.activity.pass;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimerNew;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
import com.iyuba.conceptEnglish.manager.sharedpreferences.InfoHelper;
import com.iyuba.conceptEnglish.protocol.DictRequest;
import com.iyuba.conceptEnglish.protocol.DictResponse;
import com.iyuba.conceptEnglish.protocol.WordUpdateRequest;
import com.iyuba.conceptEnglish.sqlite.mode.EvaluateBean;
import com.iyuba.conceptEnglish.sqlite.mode.NewWord;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.util.EvaluateRequset;
import com.iyuba.conceptEnglish.util.GsonUtils;
import com.iyuba.conceptEnglish.util.MediaRecordHelper;
import com.iyuba.conceptEnglish.util.OnPlayStateChangedListener;
import com.iyuba.conceptEnglish.util.Player;
import com.iyuba.conceptEnglish.util.ResultParse;
import com.iyuba.conceptEnglish.util.download.StorageUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.sqlite.op.WordOp;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.view.PermissionMsgDialog;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * 单词学习评测页面
 */
public class WordEvalActivity extends BaseStackActivity {
    /* 调试用标记的代码并非正式功能，尽快删除 */

    private static final String TIP1 = "多加练习，口语大咖在向你招手！";
    private static final String TIP2 = "勤能补拙，每天都来练一练！"; //60以下
    private static final String TIP3 = "再接再厉，一定会有提升！";
    private static final String TIP4 = "多读多练，你还可以读的更好！";//60-75
    private static final String TIP5 = "表现不错，继续努力冲高分！";
    private static final String TIP6 = "标准流利，为你点赞！";//75-90
    private static final String TIP7 = "十分优秀，口语大咖！"; //90以上


    @BindView(R.id.btn_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView titlebarTitle;


    @BindView(R.id.btn_auto)
    ImageView btnAuto;


    @BindView(R.id.tv_former)
    TextView tvFormer;//上一个

    @BindView(R.id.tv_next)
    TextView tvNext;

    @BindView(R.id.tv_show_index)
    TextView tvShowIndex;

    @BindView(R.id.cb_collect)
    CheckBox cbCollect;


    @BindView(R.id.tv_word)
    TextView tvWord;
    @BindView(R.id.img_speaker)
    ImageView imgSpeaker;
    @BindView(R.id.tv_pron)
    TextView tvPron;
    @BindView(R.id.tv_explain)
    TextView tvDef;

    @BindView(R.id.img_mode)
    ImageView imgMode;


    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_content_cn)
    TextView tvContentCn;
    @BindView(R.id.img_play)
    ImageView imgPlay;
    @BindView(R.id.img_eval)
    ImageView imgEval;
    @BindView(R.id.img_user)
    ImageView imgUser;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.ll_middle)
    LinearLayout llMiddle;
    @BindView(R.id.tv_play)
    TextView tvPlay;
    @BindView(R.id.tv_user)
    TextView tvUser;

    @BindView(R.id.tv_eval)
    TextView tvEval;

    @BindView(R.id.re_score)
    RelativeLayout reScore;

    @BindView(R.id.img_score)
    ImageView imgScore;


    @BindView(R.id.progressBar_eval)
    ProgressBar progressBarEval;


    @BindView(R.id.rl_top)
    RelativeLayout rlTop;

    @BindView(R.id.img_trans)
    ImageView imgTrans;

    private ImageView mWordImage;
    private ImageView mVideoPlay;
    private VideoView mVideoView;

    private Context mContext;

    private String contentUrl;
    private AnimationDrawable animPlay, animEval, animAudio, animWord;
    private boolean isRecord = false;


    private MediaRecordHelper mediaRecordHelper;
    String fileName = null;
    private Player wordPlayer;//单词播放器
//    private Player textPlayer;//句子播放器
    private ExoPlayer exoPlayer;

    private boolean isPlayClick = false, isAudioClick = false, isWordClick = false;

    private List<VoaWord2> voaWordList = new ArrayList<>();

    private VoaWord2 voaWord;

    private VoaDetailOp voaDetailOp;

    private boolean isWordMode = false;
    private VoaDetail voaDetail;

    private int wordIndex = 0;

    private boolean hasSentence = true;

    private int currSenEndTime;
    private int currStartTime;

    private boolean isAutoPlay;

    private boolean isChild;//是否是青少版儿童英语

    private String imageHeaderUrl = "http://static2." + Constant.IYUBA_CN + "images/words/";

    private String mDir;

    private SpannableStringBuilder mSsb;
    private final static Long delayMillis = 50L;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isDestroyed()) {
                return;
            }
            switch (msg.what) {
                case 0:
                    if (exoPlayer.isPlaying()) {
                        int progress = (int) ((exoPlayer.getCurrentPosition() - currStartTime) / (currSenEndTime - currStartTime));
                        if (progress >= 1) {
                            animPlayStop();
                            handler.removeMessages(0);
                        } else {
                            handler.sendEmptyMessageDelayed(0, delayMillis);
                        }
                    } else {
                        animPlayStop();
                        handler.removeMessages(0);
                    }
                    break;
                case 13:
                    progressBarEval.setVisibility(View.GONE);
                    imgEval.setVisibility(View.VISIBLE);
                    tvEval.setText("轻点开始录音");
                    tvPlay.setVisibility(View.VISIBLE);
                    imgPlay.setVisibility(View.VISIBLE);
                    break;
                case 14: //评测请求失败
                    progressBarEval.setVisibility(View.GONE);
                    imgEval.setVisibility(View.VISIBLE);
                    tvEval.setText("轻点开始录音");
                    tvPlay.setVisibility(View.VISIBLE);
                    imgPlay.setVisibility(View.VISIBLE);
                    ToastUtil.showToast(mContext, "评测失败，请稍后再试");
                    break;
                case 15: //评测请求成功
                    String data = (String) msg.obj;
                    EvaluateBean bean = GsonUtils.toObject(data, EvaluateBean.class);
                    double score_double = Double.parseDouble(bean.getTotal_score());
                    int score = (int) (score_double * 20);
                    tvScore.setText(String.valueOf(score));
                    tvContent.setText(ResultParse.getSenResultEvaluate(bean.getWords(), bean.getSentence()));
                    tvPlay.setVisibility(View.VISIBLE);
                    imgPlay.setVisibility(View.VISIBLE);
                    tvUser.setVisibility(View.VISIBLE);
                    imgUser.setVisibility(View.VISIBLE);
                    initTip(score);
                    llMiddleAnimToVisible();
                    progressBarEval.setVisibility(View.GONE);
                    imgEval.setVisibility(View.VISIBLE);
                    tvEval.setText("轻点开始录音");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileName = Constant.getsimRecordAddr(this) + "wordEval" + ".mp4";

        isChild = ConfigManager.Instance().getCurrBookforPass() > 4;
        if (isChild) {
            setContentView(R.layout.activity_word_eval_child);
        } else {
            setContentView(R.layout.activity_word_eval);
        }
        ButterKnife.bind(this);
        mContext = this;
        voaDetailOp = new VoaDetailOp(mContext);
        contentUrl = getIntent().getStringExtra("contentUrl");
        voaWordList = (List<VoaWord2>) getIntent().getSerializableExtra("list");
        wordIndex = getIntent().getIntExtra("wordIndex", 0);
        mediaRecordHelper = new MediaRecordHelper();
        isAutoPlay = InfoHelper.getInstance().getWordIsAuto();

        initBottomBtn();
        initPlayer();
        initWord();
        searchSentence();
        initMiddle();
        initTopBar();

        if (isChild) {
            initVideo();
            resetVideo();
        } else {
            contentUrl = getLocalSoundPath();

//            textPlayer.playUrl(contentUrl);
            playUrl(contentUrl);
        }

        //下载路径
        mDir = StorageUtil
                .getMediaDir(this, voaWord.bookId, voaWord.unitId)
                .getAbsolutePath();


    }

    private void searchSentence() {
        if (!isChild && null == voaDetailOp.findDataByKeyAndId(voaWord.examples, voaWord.voaId)) {
            tvContent.setText("暂无匹配例句");
            tvContentCn.setText("");
            voaDetail = null;
        } else {
            if (isChild) {
                voaDetail = new VoaDetail(voaWord.Sentence, voaWord.SentenceCn, voaWord.picUrl);
            } else {
                voaDetail = voaDetailOp.findDataByKeyAndId(voaWord.examples, voaWord.voaId).get(0);
            }
            if (!TextUtils.isEmpty(voaDetail.sentence)) {
                mSsb = new SpannableStringBuilder(voaDetail.sentence);
                final String HALF_SPACE = " ";
                /* 按道理来说，在Android Studio里，全角空格应该明显宽于半角，但是原本代码如此。不建议轻易改动 */
                final String FULL_SPACE = " ";
                try {
                    boolean isShow = false;
                    int startIndex = 0;
                    if (voaDetail.sentence.toLowerCase().contains(HALF_SPACE + voaWord.word.toLowerCase())) {
                        startIndex = voaDetail.sentence.toLowerCase().indexOf(HALF_SPACE + voaWord.word.toLowerCase()) + 1;
                        isShow = true;
                    } else if (voaDetail.sentence.toLowerCase().contains(FULL_SPACE + voaWord.word.toLowerCase())) {
                        startIndex = voaDetail.sentence.toLowerCase().indexOf(FULL_SPACE + voaWord.word.toLowerCase()) + 1;
                        isShow = true;
                    } else if (voaDetail.sentence.toLowerCase().indexOf(voaWord.word.toLowerCase()) == 0) {
                        startIndex = voaDetail.sentence.toLowerCase().indexOf(voaWord.word.toLowerCase());
                        isShow = true;
                    } else if (voaDetail.sentence.toLowerCase().contains(" " + voaWord.word.toLowerCase()
                            .substring(0, voaWord.word.toLowerCase().length() - 2))) {
                        String newWord = voaWord.word.toLowerCase().substring(0, voaWord.word.length() - 2);
                        startIndex = voaDetail.sentence.toLowerCase().indexOf(" " + newWord) + 1;
                        isShow = true;
                    } else if (voaDetail.sentence.toLowerCase().contains(voaWord.word.toLowerCase())) {
                        startIndex = voaDetail.sentence.toLowerCase().indexOf(voaWord.word.toLowerCase());
                        isShow = true;
                    }
                    if (isShow) {
                        String other = voaDetail.sentence.toLowerCase().substring(startIndex + voaWord.word.length());
                        Timber.d("其他的部分" + other);
                        String sentenceWord = voaDetail.sentence.toLowerCase().replace(other, "").substring(startIndex);
                        boolean isShort = false;
                        if (!sentenceWord.isEmpty()) {
                            isShort = sentenceWord.substring(sentenceWord.length() - 1).equals(" ");//句子中单词长度小于 原单词长度 sped sped
                        }
                        int endIndex;
                        if (other.equals("") || other.substring(0, 1).equals(" ") || other.substring(0, 1).equals(" ")) {
                            endIndex = startIndex + voaWord.word.length();
                        } else if (isShort) {
                            endIndex = startIndex + sentenceWord.length();
                        } else if (other.substring(0, 1).equals("，") || other.substring(0, 1).equals(",")) {
                            endIndex = startIndex + voaWord.word.length();
                        } else {
                            boolean isEndSpace = !other.contains(" ");
                            int realLength = isEndSpace ? other.length() : other.indexOf(" ");
                            endIndex = realLength + startIndex + voaWord.word.length();
                            if (endIndex < voaWord.word.length()) {
                                endIndex = startIndex + voaWord.word.length();
                            }
                        }
                        //int endIndex = startIndex + voaWord.word.length();

                        mSsb.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvContent.setText(mSsb);
                tvContentCn.setText(voaDetail.sentenceCn);
                tvContentCn.setVisibility(View.GONE);
                Timber.d("WordEval: getSentence:%s", voaDetail.sentence);
                Timber.d("WordEval: getSpan:%s", mSsb.toString());
                tvContent.post(() -> {
                    /* 出于单词过长时，将中文置于其他地方的目的 */
                    if (tvContent.getLineCount() > 4) {
                        tvContentCn.setVisibility(View.GONE);
                        imgTrans.setVisibility(View.VISIBLE);
                        tvContent.setVisibility(View.VISIBLE);
                    } else {
                        tvContentCn.setVisibility(View.VISIBLE);
                        imgTrans.setVisibility(View.GONE);
                        tvContent.setVisibility(View.VISIBLE);
                    }
                });
            }

            imgTrans.setOnClickListener(v -> {
                if (tvContentCn.getVisibility() == View.VISIBLE) {
                    tvContentCn.setVisibility(View.GONE);
                    tvContent.setVisibility(View.VISIBLE);
                } else {
                    tvContentCn.setVisibility(View.VISIBLE);
                    tvContent.setVisibility(View.GONE);
                }
            });
        }

    }

    private void initMiddle() {
        imgMode.setOnClickListener(v -> {
            isWordMode = !isWordMode;
            //隐藏分数显示
            imgScore.setVisibility(View.GONE);
            reScore.setVisibility(View.INVISIBLE);
            //隐藏提示消息
            tvTip.setVisibility(View.INVISIBLE);
            if (isWordMode) {
                //单词模式
                tvContent.setText(voaWord.word);
                tvContentCn.setText(voaWord.def);
            } else {
                //句子模式
                if (voaDetail == null) {
                    tvContent.setText("暂无匹配例句");
                    tvContentCn.setText("");
                } else {
                    tvContentCn.setText(voaDetail.sentenceCn);
                    tvContent.setText(mSsb != null ? mSsb : voaDetail.sentence);
                }
            }
            imgMode.setImageResource(R.drawable.ic_swift_normal);

            tvContent.post(() -> {
                if (tvContent.getLineCount() > 4) {
                    tvContentCn.setVisibility(View.GONE);
                    imgTrans.setVisibility(View.VISIBLE);
                    tvContent.setVisibility(View.VISIBLE);
                } else {
                    tvContent.setVisibility(View.VISIBLE);
                    tvContentCn.setVisibility(View.VISIBLE);
                    imgTrans.setVisibility(View.GONE);
                }
            });

            isPlayClick = false;
            isAudioClick = false;

            llMiddle.setVisibility(View.INVISIBLE);

            wordPlayer.stopPlay();
            animWordStop();
            animPlayStop();
            animAudioStop();

            if (isRecord) {
                isRecord = false;
                mediaRecordHelper.stop_record();
            }
            tvPlay.setVisibility(View.VISIBLE);
            imgPlay.setVisibility(View.VISIBLE);
            animEval.selectDrawable(0);
            animEval.stop();
            imgUser.setVisibility(View.INVISIBLE);
            tvUser.setVisibility(View.GONE);
            progressBarEval.setVisibility(View.GONE);
            imgEval.setVisibility(View.VISIBLE);
            tvEval.setText("轻点开始录音");
        });
        tvContent.setOnClickListener(v -> {
            if (!isWordMode)
                playSentence();
        });
    }


    private void initWord() {
        voaWord = voaWordList.get(wordIndex);
        tvWord.setText(voaWord.word);
        tvDef.setText(voaWord.def);

        if (isChild && TextUtils.isEmpty(voaWord.Sentence)) {
            imgEval.setVisibility(View.GONE);
            tvEval.setVisibility(View.GONE);
            imgMode.setVisibility(View.GONE);
        } else {
            imgEval.setVisibility(View.VISIBLE);
            tvEval.setVisibility(View.VISIBLE);
            imgMode.setVisibility(View.VISIBLE);
        }


        if (voaWord.examples == 0) {
            hasSentence = false;
        } else {
            hasSentence = true;
        }
        Typeface mFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/segoeui.ttf");
        tvPron.setTypeface(mFace);
        if (!TextUtils.isEmpty(voaWord.pron)) {
            tvPron.setVisibility(View.VISIBLE);
            tvPron.setText(String.format("[%s]", voaWord.pron));
        } else {
            tvPron.setVisibility(View.GONE);
        }

        if (isCollect()) {
            cbCollect.setChecked(true);
        } else {
            cbCollect.setChecked(false);
        }

        cbCollect.setOnClickListener(v -> {
            if (isCollect()) {
                ClientSession.Instace().asynGetResponse(
                        new com.iyuba.core.common.protocol.news.WordUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
                                com.iyuba.core.common.protocol.news.WordUpdateRequest.MODE_DELETE,
                                voaWord.word), (response, request, rspCookie) -> {
                            runOnUiThread(() -> ToastUtil.showToast(mContext, "取消收藏成功"));
                            new WordOp(mContext).deleteItemWord(String.valueOf(UserInfoManager.getInstance().getUserId()), voaWord.word);
                        }
                );
            } else {
                Word newWord = new Word();
                newWord.audioUrl = voaWord.audio;
                newWord.def = voaWord.def;
                newWord.userid = String.valueOf(UserInfoManager.getInstance().getUserId());
                newWord.pron = voaWord.pron;
                newWord.key = voaWord.word;
                saveNewWords(newWord);
            }
        });

        animWord = (AnimationDrawable) imgSpeaker.getDrawable();
        rlTop.setOnClickListener(v -> playWord());

        if (isAutoPlay)
            playWord();
    }

    private void initVideo() {
        mVideoPlay = findViewById(R.id.video_play);
        mWordImage = findViewById(R.id.word_img);
        mVideoView = findViewById(R.id.video_view);
        mVideoView.setVisibility(View.GONE);
        mVideoPlay.setOnClickListener(v -> playVideo());
        mWordImage.setOnClickListener(v -> playVideo());
    }

    private void resetVideo() {
        String mImageDirPath = StorageUtil.getImageUnzipDir(mContext, voaWord.bookId).getAbsolutePath();
        if (StorageUtil.isImageExists(mImageDirPath, voaWord.picUrl)) {
            File file = new File(mImageDirPath, voaWord.picUrl);
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
            mWordImage.setImageBitmap(bm);
            Timber.d("本地图片存在" + mImageDirPath);
        } else if (!TextUtils.isEmpty(voaWord.picUrl)) {
            Timber.d("本地图片不存在" + mImageDirPath);
            Glide.with(mContext)
                    .load(imageHeaderUrl + voaWord.picUrl)
                    .placeholder(R.drawable.failed_image)
                    .error(R.drawable.failed_image)
                    .into(mWordImage);
        }
        if (isChild && TextUtils.isEmpty(voaWord.videoUrl)) {
            mVideoPlay.setVisibility(View.INVISIBLE);
        } else {
            mVideoPlay.setVisibility(View.VISIBLE);
        }
    }

    private void playVideo() {
        if (TextUtils.isEmpty(voaWord.videoUrl)) {
            ToastUtil.showToast(mContext, "视频为空");
            return;
        }
        mWordImage.setVisibility(View.GONE);
        mVideoPlay.setVisibility(View.INVISIBLE);
        mVideoView.setVisibility(View.VISIBLE);
        mVideoView.setBackgroundColor(Color.TRANSPARENT);

        String mDir = StorageUtil
                .getMediaDir(this, voaWord.videoUrl)
                .getAbsolutePath();
        String videoUri;
        if (StorageUtil.isVideoClipExist(mDir, voaWord.videoUrl)) {
            Timber.d("视频文件存在");
            videoUri = new File(mDir, StorageUtil.getVideoClipExist(voaWord.videoUrl)).getAbsolutePath();
        } else {
            File file = new File(mDir, StorageUtil.getVideoClipExist(voaWord.videoUrl));
            Timber.d("视频文件不存在" + file.getPath());
            videoUri = voaWord.videoUrl;
        }

        try {
            mVideoView.setVideoURI(Uri.parse(videoUri));
            mVideoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                if (isChild && TextUtils.isEmpty(voaWord.videoUrl)) {
                    mVideoPlay.setVisibility(View.INVISIBLE);
                } else {
                    mVideoPlay.setVisibility(View.VISIBLE);
                }
                mWordImage.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        wordPlayer.pause();
        mediaRecordHelper.stop_record();
        animAudioStop();
        animPlayStop();
        animEval.selectDrawable(0);
        animEval.stop();
        tvPlay.setVisibility(View.VISIBLE);
        imgPlay.setVisibility(View.VISIBLE);
        isRecord = false;

        isAudioClick = false;
        isPlayClick = false;
        isWordMode = false;
        isWordClick = false;
        imgMode.setImageResource(R.drawable.ic_swift_normal);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wordPlayer.stopAndRelease();

        if (msgDialog!=null){
            msgDialog.dismiss();
        }
    }

    private void initPlayer() {

        wordPlayer = new Player(mContext, new OnPlayStateChangedListener() {
            @Override
            public void playCompletion() {
                animAudioStop();
                animPlayStop();
                animWordStop();
            }

            @Override
            public void playFaild() {
            }

            @Override
            public void playSuccess() {
                if (isAudioClick) {
                    animAudio.start();
                } else if (isPlayClick) {
                    if (voaDetail == null) {
                        return;
                    }
                    wordPlayer.seekTo((int) voaDetail.startTime);
                    animPlay.start();
                } else {
                    animWord.start();
                }
            }
        });


//        textPlayer = new Player(mContext, new OnPlayStateChangedListener() {
//            @Override
//            public void playCompletion() {
//                animPlayStop();
//            }
//
//            @Override
//            public void playFaild() {
//            }
//
//            @Override
//            public void playSuccess() {
//                if (!isChild) {
//                    textPlayer.pause();
//                }
//            }
//        });

        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new com.google.android.exoplayer2.Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case ExoPlayer.STATE_READY:
                        if (isChild){
                            exoPlayer.play();
                            animPlay.start();
                            currSenEndTime = (int) exoPlayer.getDuration();
                            startTime();
                        }
                        break;
                    case ExoPlayer.STATE_ENDED:
                        animPlayStop();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(WordEvalActivity.this,"加载音频出错，请重试");
            }
        });
    }


    private void initBottomBtn() {
        animPlay = (AnimationDrawable) imgPlay.getDrawable();
        animEval = (AnimationDrawable) imgEval.getDrawable();
        animAudio = (AnimationDrawable) imgUser.getDrawable();


        imgUser.setVisibility(View.GONE);
        tvUser.setVisibility(View.GONE);
        llMiddle.setVisibility(View.INVISIBLE);
        imgPlay.setOnClickListener(v -> playSentence());

        imgEval.setOnClickListener(v -> {
            pauseIPlayer();

            // TODO: 2023/12/1 之前这里只能进行句子评测，现在放开单词也能评测 
            if (!isWordMode&&!hasSentence) {
                ToastUtil.showToast(this,"暂无匹配例句");
                return;
            }

            if (isRecord) {
                isRecord = false;
                imgEval.setVisibility(View.INVISIBLE);
                progressBarEval.setVisibility(View.VISIBLE);
                tvEval.setText("评分中...");
                mediaRecordHelper.stop_record();
                animEval.stop();
                setRequest();
            } else {
                showPermissionDialog();
            }
        });

        imgUser.setOnClickListener(v -> {
            pauseIPlayer();

            isPlayClick = false;
            isWordClick = false;

            animPlayStop();
            animWordStop();

            if (!isAudioClick) {
                isAudioClick = true;
                wordPlayer.playUrl(fileName);
            } else {
                if (wordPlayer.isPlaying()) {
                    wordPlayer.pause();
                    animAudioStop();
                } else {
                    wordPlayer.seekTo(0);
                    wordPlayer.start();
                    animAudio.start();
                }
            }
        });

        tvFormer.setOnClickListener(v -> {

            if (wordIndex > 0) {
                wordIndex--;
                onPause();
                initWord();
                if (isChild) {
                    resetVideo();
                }
                searchSentence();
                tvShowIndex.setText(String.format("%d/%d", wordIndex + 1, voaWordList.size()));
                imgUser.setVisibility(View.GONE);
                tvUser.setVisibility(View.GONE);
            }

        });

        tvNext.setOnClickListener(v -> {
            if (wordIndex < voaWordList.size() - 1) {
                wordIndex++;
                onPause();
                initWord();
                if (isChild) {
                    resetVideo();
                }
                searchSentence();
                imgUser.setVisibility(View.GONE);
                tvUser.setVisibility(View.GONE);
                llMiddle.setVisibility(View.INVISIBLE);
                tvShowIndex.setText(String.format("%d/%d", wordIndex + 1, voaWordList.size()));
            } else {
                ToastUtil.showToast(mContext, "已经是最后一个单词了");
            }
        });

        tvShowIndex.setText(MessageFormat.format("{0}/{1}", wordIndex + 1, voaWordList.size()));
    }

    private void initTopBar() {
        titlebarTitle.setText("闯关单词");

        if (isAutoPlay) {
            ToastUtil.showToast(mContext, "开启单词自动发音");
            btnAuto.setImageResource(R.drawable.ic_auto);
        } else {
            ToastUtil.showToast(mContext, "关闭单词自动发音");
            btnAuto.setImageResource(R.drawable.ic_auto_false);
        }

        btnAuto.setOnClickListener(v -> {
            isAutoPlay = !isAutoPlay;
            InfoHelper.getInstance().putWordIsAuto(isAutoPlay);
            if (isAutoPlay) {
                ToastUtil.showToast(mContext, "开启单词自动发音");
                btnAuto.setImageResource(R.drawable.ic_auto);
            } else {
                ToastUtil.showToast(mContext, "关闭单词自动发音");
                btnAuto.setImageResource(R.drawable.ic_auto_false);
            }

        });
        ivTitleBack.setOnClickListener(v -> finish());

    }


    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //评测请求
    private void setRequest() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Map<String, String> textParams = new HashMap<String, String>();

                File file = new File(fileName);
                textParams.put("type", "concept");
                textParams.put("userId", String.valueOf(UserInfoManager.getInstance().getUserId()));
                textParams.put("newsId", "1000");
                textParams.put("paraId", "1");
                textParams.put("IdIndex", "1");

                String urlSentence = TextAttr.encode(tvContent.getText().toString());
//                if (TextUtils.isEmpty(urlSentence)) {
//                    return;
//                }
                urlSentence = urlSentence.replaceAll("\\+", "%20");
                textParams.put("sentence", urlSentence);
                if (file.exists()) {
                    try {
                        EvaluateRequset.post(Constant.EVALUATE_URL_NEW, textParams, fileName, handler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(13);
                }
            }
        };
        thread.start();
    }

    private void animAudioStop() {
        animAudio.selectDrawable(0);
        animAudio.stop();
    }

    private void animPlayStop() {
        animPlay.selectDrawable(0);
        animPlay.stop();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
        }
    }

    private void animWordStop() {
        animWord.selectDrawable(0);
        animWord.stop();
    }

    private void llMiddleAnimToVisible() {
        llMiddle.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(llMiddle, "translationY", llMiddle.getHeight(), 0);
        objectAnimator.setDuration(700);
        objectAnimator.start();

    }

    private void initTip(int score) {
        imgScore.setVisibility(View.GONE);
        reScore.setVisibility(View.VISIBLE);
        String[] score60 = new String[]{TIP1, TIP2};
        String[] score75 = new String[]{TIP3, TIP4};
        String[] score90 = new String[]{TIP5, TIP6};
        int index = ((int) (Math.random() * 10)) % 2;

        if (score < 60) {
            imgScore.setVisibility(View.VISIBLE);
            reScore.setVisibility(View.GONE);
            tvTip.setText(score60[index]);
        } else if (score < 75) {
            tvTip.setText(score75[index]);
        } else if (score < 90) {
            tvTip.setText(score90[index]);
        } else {
            tvTip.setText(TIP7);
        }
    }


    //开始录音
    public void record() {
        animPlayStop();
        animAudioStop();

        animEval.start();
        llMiddle.setVisibility(View.INVISIBLE);
        imgPlay.setVisibility(View.INVISIBLE);
        imgUser.setVisibility(View.INVISIBLE);
        tvPlay.setVisibility(View.INVISIBLE);
        tvUser.setVisibility(View.INVISIBLE);
        tvEval.setText("轻点结束录音");

        if (wordPlayer.isPlaying()) {
            wordPlayer.pause();
            animAudioStop();
            animPlayStop();
        }
        //MP4 文件录制
        makeRootDirectory(Constant.getsimRecordAddr(this));
        mediaRecordHelper.setFilePath(fileName);
        mediaRecordHelper.recorder_Media();
        isRecord = true;
    }


    /**
     * 获取单词音频路径
     */
    private void playWordAudio() {

        String dir = StorageUtil.getWordDir(this).getAbsolutePath();
        if (TextUtils.isEmpty(voaWord.audio)
                || voaWord.audio.length() < 5
                || !"mp3".equals(voaWord.audio.substring(voaWord.audio.length() - 3))) {
            playFromNet();
            return;
        }
        File file = new File(dir, StorageUtil.getWordName(voaWord.audio));
        Timber.d("单词音频" + file.getPath());
        if (file.exists()) {
            Timber.d("单词音频本地存在");
            try {
                wordPlayer.playUrl(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                wordPlayer.playUrl(voaWord.audio);
            }
        } else {
            Timber.d("单词音频本地不存在，网络播放");
            try {
                wordPlayer.playUrl(voaWord.audio);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void playFromNet() {
        ClientSession.Instace().asynGetResponse(
                new DictRequest(TextAttr.encode(tvWord.getText().toString())), (response, request, rspCookie) -> {
                    DictResponse dictResponse = (DictResponse) response;
                    NewWord newWord = dictResponse.newWord;
                    if (newWord != null && !TextUtils.isEmpty(newWord.audio)) {
                        isWordClick = true;
                        wordPlayer.playUrl(newWord.audio);
                    } else {
                        runOnUiThread(() -> {
                            ToastUtil.showToast(mContext, "此单词暂无音频");
                        });

                    }
                }, null, null);
    }

    private String getLocalSoundPath() {
        int voaId = Integer.parseInt(voaWord.voaId);
        //暂时不做英音美音 例句的区分

        // 英音原文音频的存放路径
//        String pathString = Constant.videoAddr + voaId + "_B" + Constant.append;
        String pathString = FilePathUtil.getHomeAudioPath(voaId, TypeLibrary.BookType.conceptFourUK);
        File fileTemp = new File(pathString);
        if (fileTemp.exists()) {
            return pathString;
        } else {
            return Constant.sound + "british/" + voaId / 1000 + "/" + voaId / 1000 + "_" + voaId % 1000 + Constant.append;
        }

    }


    private boolean isCollect() {
        com.iyuba.core.common.sqlite.mode.Word word = new WordOp(mContext).findDataByName(voaWord.word, String.valueOf(UserInfoManager.getInstance().getUserId()));
        if (word == null) {
            return false;
        } else {
            if (word.delete.equals("1")) {
                return false;
            } else {
                return true;
            }
        }
    }


    private void saveNewWords(Word wordTemp) {
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(mContext);
        } else {
            try {
                new WordOp(mContext).saveData(wordTemp);
                CustomToast.showToast(mContext, R.string.play_ins_new_word_success, 1000);
                addNetwordWord(wordTemp.key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addNetwordWord(String wordTemp) {
        ClientSession.Instace().asynGetResponse(
                new WordUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
                        WordUpdateRequest.MODE_INSERT, wordTemp),
                (response, request, rspCookie) -> {
                }, null, null);
    }


    //播放单词
    private void playWord() {
        pauseIPlayer();
        isAudioClick = false;
        isPlayClick = false;
        animAudioStop();
        animPlayStop();
        if (!isWordClick) {
            playWordAudio();
        } else {
            wordPlayer.start();
            animWord.start();
        }
    }

    private void playSentence() {
        pauseIPlayer();
        if (isWordMode) {
            //单词
            isAudioClick = false;
            isPlayClick = true;
            animAudioStop();
            if (!isWordClick) {
                playWordAudio();
            } else {
                wordPlayer.start();
                animPlay.start();
            }
        } else {
            //句子
            if (!hasSentence) {
                ToastUtil.showToast(this,"暂无匹配例句");
                return;
            }
            isAudioClick = false;
            isWordClick = false;
            animAudioStop();
            animWordStop();
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                animPlayStop();
//                handler.removeMessages(0);
                RxTimerNew.getInstance().cancelTimer("textPlayer");
            } else {
                if (isChild) {
                    //这里的播放要加个下载，恶心的要求是下载 音频，视频，图片
                    if (TextUtils.isEmpty(voaWord.SentenceAudio)) {
                        ToastUtil.showToast(mContext, "暂无音频");
                        return;
                    }
                    if (StorageUtil.isAudioExist(mDir, voaWord.position)) {
                        Timber.d("句子音频存在" + mDir + "/" + voaWord.position + ".mp3");
                        try {
                            playUrl(mDir + "/" + voaWord.position + ".mp3");
                        } catch (Exception e) {
                            e.printStackTrace();
                            playUrl(voaWord.SentenceAudio);
                        }
                    } else {
                        File file = new File(mDir, StorageUtil.getAudioFilename(voaWord.position + ""));
                        Timber.d("句子音频不存在，网络播放" + file.getPath());
                        playUrl(voaWord.SentenceAudio);
                        Timber.d("播放例句音频" + voaWord.SentenceAudio);
                    }

//                    animPlay.start();
//                    currSenEndTime = (int) exoPlayer.getDuration();
////                    handler.sendEmptyMessageDelayed(0, delayMillis);
//                    startTime();
                } else if (!exoPlayer.isPlaying()) {
                    if (voaDetail == null) {
                        ToastUtil.showToast(mContext, "暂无音频信息，请刷新重试...");
                        return;
                    }

                    currStartTime = (int) (voaDetail.startTime * 1000);
                    currSenEndTime = (int) (voaDetail.endTime * 1000);
                    exoPlayer.seekTo(currStartTime);
                    exoPlayer.play();
                    animPlay.start();

//                    handler.sendEmptyMessageDelayed(0, delayMillis);//this
                    startTime();
                } else {
                    ToastUtil.showToast(mContext, "音频正在加载中，请稍后...");
                }
            }

        }
    }


    private void pauseIPlayer() {
//        if (BackgroundManager.Instace().bindService != null) {
//            MediaPlayer player = BackgroundManager.Instace().bindService.getPlayer();
//            if (player != null) {
//                if (player.isPlaying()) {
//                    player.pause();
//                }
//            }
//        }
        if (ConceptBgPlayManager.getInstance().getPlayService()!=null){
            ExoPlayer exoPlayer = ConceptBgPlayManager.getInstance().getPlayService().getPlayer();
            if (exoPlayer!=null&&exoPlayer.isPlaying()){
                exoPlayer.pause();
            }
        }
    }

    private void playUrl(String url){
        try {
            MediaItem mediaItem = MediaItem.fromUri(url);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }catch (Exception e){
            ToastUtil.showToast(this,"播放音频失败");
        }
    }

    private void startTime(){
        RxTimerNew.getInstance().multiTimerInMain("textPlayer", delayMillis, delayMillis, new RxTimerNew.RxActionListener() {
            @Override
            public void onAction(long number) {
                long curShowTime = exoPlayer.getCurrentPosition();
                long showTime = currSenEndTime;

                Log.d("显示数据-0000", curShowTime+"----"+showTime);

                if (curShowTime>=showTime){
                    animPlayStop();
                    RxTimerNew.getInstance().cancelTimer("textPlayer");
                }
            }
        });
    }

    //评测弹窗显示
    private PermissionMsgDialog msgDialog = null;

    private void showPermissionDialog(){
        List<Pair<String,Pair<String,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
        // TODO: 2025/4/11 区分版本处理
        if (Build.VERSION.SDK_INT < 35){
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));
        }

        msgDialog = new PermissionMsgDialog(this);
        msgDialog.showDialog(null, pairList, true,new PermissionMsgDialog.OnPermissionApplyListener() {
            @Override
            public void onApplyResult(boolean isSuccess) {
                if (isSuccess){
                    record();
                }
            }
        });
    }
}
