package com.iyuba.core.talkshow.dub.preview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.OnSeekCompletionListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.common.data.model.WavListItem;
import com.iyuba.core.common.data.remote.IntegralService;
import com.iyuba.core.common.util.ScreenUtils;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.LoadingAdDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.talkshow.TalkShowFragment;
import com.iyuba.core.talkshow.lesson.NormalVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.BaseVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.MyOnTouchListener;
import com.iyuba.lib.R;
import com.iyuba.play.ExtendedPlayer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import personal.iyuba.personalhomelibrary.utils.StatusBarUtil;

public class PreviewActivity extends AppCompatActivity implements PreviewMvpView{

    private static final String IS_FROM_UNRELEASED = "isFromReleased";
    private static final String TIMESTAMP = "timestamp";
    private static final String VOA = "voa";
    private static final String MAP = "map";
    private static final String PREVIEW_INFO = "previewInfo";
    //private static final String RECORD = "RECORD";
    private static final String VOA_TEXT = "VOA_TEXT";


    public static Intent buildIntent(List<VoaText> voaTextList , Context context, TalkLesson voa, Map<Integer, WavListItem> map,
                                     PreviewInfoBean previewInfoBean, /*Record draftRecord,*/ long timeStamp, boolean isFromReleased) {
        Intent intent = new Intent();
        intent.putExtra(VOA, voa);
        intent.putExtra(VOA_TEXT, (Serializable) voaTextList);
        intent.putExtra(MAP, (Serializable) map);
        //intent.putExtra(RECORD, draftRecord);
        intent.putExtra(PREVIEW_INFO, previewInfoBean);
        intent.putExtra(TIMESTAMP, timeStamp);
        intent.putExtra(IS_FROM_UNRELEASED, isFromReleased);
        intent.setClass(context, PreviewActivity.class);
        return intent;
    }

    VideoView mVideoView;
    TextView mPublishShareTv;
    TextView mShareFriendsTv;
    //@BindView(R2.id.tv_save_draft)
    //TextView mSaveDraftTv;
    Button mBackBtn;//返回并修改

    ProgressBar mProgressAccuracy;//准确度
    TextView mTextAccuracy;

    ProgressBar mProgressCompleteness;//完整度
    TextView mTextCompleteness;
    ProgressBar mProgressFluence;//流畅度
    TextView mTextFluence;

    PreViewPresenter mPresenter;

    private TalkLesson mVoa;
    private long mTimestamp;
    private HashMap<Integer , WavListItem> map = new HashMap<>();
    private List<VoaText> mVoaTexts;
    private boolean isFromReleased;
    private PreviewInfoBean previewInfoBean;

    private LoadingAdDialog mLoadingDialog;
    private NormalVideoControl mVideoControl;


    private MediaPlayer mAacMediaPlayer;
    private MediaPlayer mMp3MediaPlayer;
    private ExtendedPlayer dubbingPlayer;

    private Disposable dis;
    private int dubbingPosition = -1;

    private Context mContext;

    private String mUid;
    private String mUserName;

    private boolean looperFlag=true;
    private MediaPlayer backPlayer;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            handler.sendEmptyMessageDelayed(0,20);
            long position = mVideoView.getCurrentPosition() / 1000;
            for (int i = 0; i < mVoaTexts.size(); i++) {
                VoaText item = mVoaTexts.get(i);
                boolean timeEqual = item.timing <= position && item.endTiming >= position;
//                File video = StorageUtil.getParaRecordMP4File(mContext,item.getVoaId(),item.paraId,mTimestamp);
                File video = StorageUtil.getParaRecordMp3File(mContext,item.getVoaId(),item.paraId,mTimestamp);
                if (timeEqual && video.exists()&&!voicePlayer.isPlaying()) {
                    voicePlayer.reset();
                    try {
                        voicePlayer.setDataSource(video.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    voicePlayer.prepareAsync();
                    voicePlayer.setOnPreparedListener(player -> voicePlayer.start());
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        initView();
        initClick();
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.black, getTheme()));
        mContext = this;
        TalkShowFragment.addActivity(this);
        mPresenter =new PreViewPresenter();
        mPresenter.attachView(this);
        mTimestamp = getIntent().getLongExtra(TIMESTAMP, 0);
        mVoa = getIntent().getParcelableExtra(VOA);
        backPlayer=new MediaPlayer();
        try {
            backPlayer.setDataSource(StorageUtil.getAudioFile(this, mVoa.voaId()).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        backPlayer.prepareAsync();
        mVoaTexts =  getIntent().getParcelableArrayListExtra(VOA_TEXT);
        map = (HashMap<Integer, WavListItem>) getIntent().getSerializableExtra(MAP);
        isFromReleased = getIntent().getBooleanExtra(IS_FROM_UNRELEASED, false);
//        startListenVideoThread();
        //draftRecord = getIntent().getParcelableExtra(RECORD);
        previewInfoBean = (PreviewInfoBean) getIntent().getSerializableExtra(PREVIEW_INFO);


        mLoadingDialog = new LoadingAdDialog(this);
        mLoadingDialog.setTitleText(mPresenter.formatTitle("正在发布配音，请耐心等待\n成功后积分 +5"));
        mLoadingDialog.setMessageText(
                mPresenter.formatMessage(
                        previewInfoBean.getWordCount(),
                        previewInfoBean.getAverageScore(),
                        previewInfoBean.getRecordTime()
                )
        );
        mLoadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mPresenter.cancelUpload();
            }
        });
        mLoadingDialog.setRetryOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadingDialog.retry();
                //onClickReleaseAndShare();
            }
        });

        mUid = String.valueOf(UserInfoManager.getInstance().getUserId());
        mUserName = UserInfoManager.getInstance().getUserName();

        setProgressBar();
        try {
            startDubbingOb();
            mAacMediaPlayer = new MediaPlayer();
            dubbingPlayer = new ExtendedPlayer(mContext);
            dubbingPlayer.setOnCompletionListener(new net.protyposis.android.mediaplayer.MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(net.protyposis.android.mediaplayer.MediaPlayer mediaPlayer) {
                    if (dubbingPosition == mVoaTexts.size() -1 ) return ;
                    startDubbingOb();
                }
            });
            mAacMediaPlayer.setDataSource(mPresenter.getMp3RecordPath(mVoa.voaId(), mTimestamp));
            mAacMediaPlayer.prepareAsync();
            mAacMediaPlayer.setVolume(1f,1f);

            mAacMediaPlayer.setOnPreparedListener(mAacPreparedListener);
            mMp3MediaPlayer = new MediaPlayer();
            mMp3MediaPlayer.setDataSource(mPresenter.getMp3Path(mVoa.voaId()));

            mMp3MediaPlayer.prepareAsync();
            mMp3MediaPlayer.setVolume(1f,1f);

            mMp3MediaPlayer.setOnPreparedListener(mMp3PreparedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isFromReleased) {
            //mSaveDraftTv.setVisibility(View.GONE);
            mBackBtn.setVisibility(View.GONE);
        } else {
            //mSaveDraftTv.setVisibility(View.VISIBLE);
            mBackBtn.setVisibility(View.VISIBLE);
        }

        initMedia();
        voicePlayer=new MediaPlayer();
        handler.sendEmptyMessageDelayed(0,20);
        //String textImage = "\uD83D\uDCD5\uD83D\uDCDA\uD83C\uDF32\uD83D\uDC16";



        //针对临时的包名处理
        if ("com.iyuba.peiyin".equals(getPackageName())){
            findViewById(R.id.tv_share_friends).setVisibility(View.GONE);
            TextView publishText = findViewById(R.id.tv_publish_share);
            publishText.setText("发布该配音");
        }
    }

    private void initView(){
        mVideoView = findViewById(R.id.video_view);
        mPublishShareTv = findViewById(R.id.tv_publish_share);
        mShareFriendsTv = findViewById(R.id.tv_share_friends);
        mBackBtn = findViewById(R.id.back_btn);
        mProgressAccuracy = findViewById(R.id.progress_accuracy);
        mTextAccuracy = findViewById(R.id.tv_accuracy);
        mProgressCompleteness = findViewById(R.id.progress_completeness);
        mTextCompleteness = findViewById(R.id.tv_completeness);
        mProgressFluence = findViewById(R.id.progress_fluence);
        mTextFluence = findViewById(R.id.tv_fluence);
    }

    private void initClick(){
        findViewById(R.id.tv_back_home).setOnClickListener(v->{
            startMainActivity();
        });
        findViewById(R.id.tv_publish_share).setOnClickListener(v->{
            pause();
            try {
                File file1= getAacRecordFile(mVoa.voaId(), mTimestamp);
                File file = new File(file1.getAbsolutePath().replace("aac","mp3"));
                mPresenter.releaseDubbing(map , mVoa.voaId(), mVoa.Sound,
                        previewInfoBean.getAverageScore(), mVoa.category(),mUid,mUserName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.tv_share_friends).setOnClickListener(v->{
            pause();
            showShareView("shareString");
        });
        findViewById(R.id.back_btn).setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        looperFlag=true;
    }

    private MediaPlayer voicePlayer;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mBackBtn.setVisibility(View.GONE);
        } else {
            mBackBtn.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setVideoViewParams();
    }

    private File getAacRecordFile(int voaId, long timeStamp) {
        return StorageUtil.getAacMergeFile(mContext, voaId, timeStamp);
    }

    private void setVideoViewParams() {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        int[] screenSize = ScreenUtils.getScreenSize(this);
        lp.width = screenSize[0];
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = screenSize[1]; // 16 : 9
        } else {
            lp.height = (int) (lp.width * 0.5625);
        }

        mVideoView.setLayoutParams(lp);
    }


    MediaPlayer.OnPreparedListener mAacPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //isAccPrepared = true;
            start();
        }
    };

    MediaPlayer.OnPreparedListener mMp3PreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //isMp3Prepared = true;
            start();
        }
    };


    private void setProgressBar() {
        mProgressAccuracy.setProgress(previewInfoBean.getAverageScore());
        mTextAccuracy.setText(previewInfoBean.getAverageScore() + "");
        mProgressCompleteness.setProgress(previewInfoBean.getCompleteness());
        mTextCompleteness.setText(previewInfoBean.getCompleteness() + "");
        mProgressFluence.setProgress(previewInfoBean.getFluence());
        mTextFluence.setText(previewInfoBean.getFluence()+"");
    }

    public void initMedia() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);

        mVideoControl = new NormalVideoControl(this);
//        mVideoControl.setPlayPauseImages(R.drawable.play, R.drawable.pause);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
        mVideoControl.setButtonListener(mBtnListener);
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                finish();
            }
        });
        mVideoView.setControls(mVideoControl);
        mVideoControl.setOnTouchListener(listener);
        mVideoView.setVideoURI(mPresenter.getVideoUri(mVoa.voaId()));

        mVideoView.setVolume(0);
        mVideoView.setOnPreparedListener(mVideoPreparedListener);
        mVideoView.setOnCompletionListener(mVideoCompletionListener);
        mVideoView.setOnSeekCompletionListener(new OnSeekCompletionListener() {
            @Override
            public void onSeekComplete() {
                long curPosition = mVideoView.getCurrentPosition();
                if (mMp3MediaPlayer != null) {
                    mMp3MediaPlayer.seekTo((int) curPosition);
                }
                if (mAacMediaPlayer != null) {
                    mAacMediaPlayer.seekTo((int) curPosition);
                }
            }
        });
    }


    MyOnTouchListener.SingleTapListener mSingleTapListener = new MyOnTouchListener.SingleTapListener() {
        @Override
        public void onSingleTap() {
            if (mVideoControl != null) {
                if (mVideoControl.getControlVisibility() == View.GONE) {
                    mVideoControl.show();
                    if (mVideoView.isPlaying()) {
                        mVideoControl.hideDelayed(VideoControls.DEFAULT_CONTROL_HIDE_DELAY);
                    }
                } else {
                    mVideoControl.hideDelayed(0);
                }
            }
        }
    };

    VideoControlsButtonListener mBtnListener = new VideoControlsButtonListener() {
        @Override
        public boolean onPlayPauseClicked() {
            if (mVideoView == null || mAacMediaPlayer == null || mMp3MediaPlayer == null) {
                return false;
            }

            if (mVideoView.isPlaying() || mAacMediaPlayer.isPlaying() || mMp3MediaPlayer.isPlaying()) {
                pause();
            } else {
                start();
            }

            return true;
        }

        @Override
        public boolean onPreviousClicked() {
            return false;
        }

        @Override
        public boolean onNextClicked() {
            return false;
        }

        @Override
        public boolean onRewindClicked() {
            return false;
        }

        @Override
        public boolean onFastForwardClicked() {
            return false;
        }
    };

    public void start() {
        if (mVideoView != null && mAacMediaPlayer != null && mMp3MediaPlayer != null) {
            mVideoView.start();
            mAacMediaPlayer.start();
            mMp3MediaPlayer.start();
        }
        backPlayer.setOnPreparedListener(player -> backPlayer.start());
    }

    public void pause() {
        if (mVideoView != null) {
            mVideoView.pause();
        }
        if (mAacMediaPlayer != null) {
            mAacMediaPlayer.pause();
        }
        if (mMp3MediaPlayer != null) {
            mMp3MediaPlayer.pause();
        }
        backPlayer.pause();
        looperFlag=false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        looperFlag=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        looperFlag=false;
        handler.removeCallbacksAndMessages(null);
        try {
            voicePlayer.stop();
            voicePlayer.reset();
            voicePlayer.release();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    OnPreparedListener mVideoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            //isVideoPrepared = true;
            start();
        }
    };

    OnCompletionListener mVideoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            mVideoView.restart();

            mAacMediaPlayer.seekTo(0);
            mAacMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mAacMediaPlayer.pause();
                }
            });

            mMp3MediaPlayer.seekTo(0);
            mMp3MediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMp3MediaPlayer.pause();
                }
            });
        }
    };

    private void startDubbingOb() {
        dis =  Observable.interval(50, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        Log.d("PreviewSelfPlayer",
                                String.format("dubbingPosition %d , voaP %d",
                                        dubbingPosition, findVoaPosition(mVideoView.getCurrentPosition())));
                        if (dubbingPosition !=findVoaPosition(mVideoView.getCurrentPosition())) {
                            dubbingPosition = findVoaPosition(mVideoView.getCurrentPosition());
                        }else {
                            return ;
                        }

                        //更改路径
//                        File file = StorageUtil.getParaRecordMP4File(mContext, mVoa.voaId(),
//                                dubbingPosition + 1, mTimestamp);
                        File file = StorageUtil.getParaRecordMp3File(mContext, mVoa.voaId(),
                                dubbingPosition + 1, mTimestamp);
                        Log.d("PreviewSelfPlayer",
                                String.format("(Preview) file and exist: %s, %b", file.getAbsolutePath(), file.exists()));
                        if (file.exists()) {
                            dubbingPlayer.reset();
                            dubbingPlayer.initialize(file.getAbsolutePath());
                            dubbingPlayer.prepareAndPlay();
                            dubbingPlayer.start();
                            dis.dispose();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private int findVoaPosition(long currentPosition) {
        int res = -1 ;
        for (int i = 0 ; i<mVoaTexts.size() ; i++  ){
            if (currentPosition>mVoaTexts.get(i).timing*1000){
                res = i ;
            }
        }
        return  res ;
    }


    @Override
    public void startLoginActivity() {

    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(mContext,resId);
    }

    @Override
    public void showToast(String resId) {
        ToastUtil.showToast(mContext,resId);
    }

    @Override
    public void showShareView(String url) {
        IntegralService integralService = IntegralService.Creator.newIntegralService();
        //shareString = filePath ;

        Share.prepareDubbingMessage(this ,  mVoa, mPresenter.getShuoshuoId(),mUserName,integralService,
                UserInfoManager.getInstance().getUserId());
    }

    @Override
    public void showShareHideReleaseButton() {
        mPublishShareTv.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show(UserInfoManager.getInstance().isVip());
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showPublishSuccess(int resID) {
        mLoadingDialog.showSuccess(mPresenter.formatTitle(getString(resID)));
        dismissPublishDialog();
    }

    void dismissPublishDialog() {
        mLoadingDialog.dismiss();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mLoadingDialog.dismiss();
//            }
//        }, 5 * 1000);
    }

    @Override
    public void showPublishFailure(int resID) {
        mLoadingDialog.showFailure(getString(resID));
        mLoadingDialog.showRetryButton();
    }

    @Override
    public void startMainActivity() {
        TalkShowFragment.clearActivity();
    }
}