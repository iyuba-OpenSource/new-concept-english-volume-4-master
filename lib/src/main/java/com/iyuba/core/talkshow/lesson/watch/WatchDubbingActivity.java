package com.iyuba.core.talkshow.lesson.watch;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.ScreenUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.circularimageview.CircularImageView;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.talkshow.lesson.NormalVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.BaseVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.MyOnTouchListener;
import com.iyuba.core.talkshow.lesson.watch.comment.CommentFragment;
import com.iyuba.lib.R;

import personal.iyuba.personalhomelibrary.utils.StatusBarUtil;

public class WatchDubbingActivity extends AppCompatActivity implements WatchDubbingMvpView{

    private static final String RANKING = "ranking";
    private static final String VOA = "voa";
    private static final String UID = "uid";

    public static Intent buildIntent(Context context, Ranking ranking, TalkLesson voa, Integer uid) {
        Intent intent = new Intent();
        intent.setClass(context, WatchDubbingActivity.class);
        intent.putExtra(RANKING, ranking);
        intent.putExtra(VOA, voa);
        intent.putExtra(UID, uid);
        return intent;
    }

    VideoView mVideoView;
    CircularImageView mPhotoIv;
    ImageView mThumbIv;
    TextView mThumbNumTv;
    TextView mUserNameTv;
    TextView mDateTv;
    View mLoadingView;
    TextView mLoadingTv;

    private boolean isInterrupted = false;
    private Ranking mRanking;
    private TalkLesson mVoa;


    //private DownloadDialog mDownloadDialog;

    //public WatchDownloadPresenter mDownloadPresenter;
    WatchDubbingPresenter mPresenter;
    //UploadStudyRecordUtil uploadStudyRecordUtil;
    private NormalVideoControl mVideoControl;

    int uid;
    private String myUid;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dubbing_lib);
        initOldView();
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.black, getTheme()));

        mRanking = getIntent().getParcelableExtra(RANKING);
        mVoa = getIntent().getParcelableExtra(VOA);
        uid = getIntent().getIntExtra(UID, 0);
        mContext =this;

        myUid = String.valueOf(UserInfoManager.getInstance().getUserId());
        int myId = UserInfoManager.getInstance().getUserId();
        mPresenter= new WatchDubbingPresenter(DataManager.getInstance(),myId);
        mPresenter.attachView(this);
        initVideo();
        initView();
        initFragment();

        if (DubDBManager.getInstance().isAgree(myUid,mRanking.getID())){
            mThumbIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_thumb_yellow));
        }
    }

    private void initOldView(){
        mVideoView = findViewById(R.id.video_view);
        mPhotoIv = findViewById(R.id.photo_iv);
        mThumbIv = findViewById(R.id.thumb_iv);
        mThumbNumTv = findViewById(R.id.thumb_num_tv);
        mUserNameTv = findViewById(R.id.user_name_tv);
        mDateTv = findViewById(R.id.date_tv);
        mLoadingView = findViewById(R.id.loading_view);
        mLoadingTv = findViewById(R.id.loading_tv);

        RelativeLayout thumbLayout = findViewById(R.id.thumb_layout);
        thumbLayout.setOnClickListener(v->{
            onThumbClick();
        });
    }

    private void initVideo() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);

        mVideoControl = new NormalVideoControl(this);
//        mVideoControl.setPlayPauseImages(R.drawable.play, R.drawable.pause);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_AUTO);
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                finish();
            }
        });
        mVideoControl.setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                if (mVideoView == null) {
                    return false;
                }

                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    stopVideoView("0");
                } else {
                    mVideoView.start();
                    //EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VOICE));
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
        });
        mVideoView.setControls(mVideoControl);
        mVideoControl.setOnTouchListener(listener);
        mVideoView.setVideoPath(Constant.getNewDubbingUrl(mRanking.videoUrl));
        mVideoView.setOnPreparedListener(mVideoPreparedListener);
        mVideoView.setOnCompletionListener(mVideoCompletionListener);

        //设置禁止投屏显示
        mVideoControl.showControlToTv(false);
    }


    OnPreparedListener mVideoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            mVideoView.start();
        }
    };

    OnCompletionListener mVideoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            mVideoView.restart();
            mVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    mVideoView.pause();
                    stopVideoView("1");
                }
            });
        }
    };

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

    private void stopVideoView(String flag) {
//        uploadStudyRecordUtil.stopStudyRecord(getApplicationContext(), mPresenter.checkLogin(),
//                flag, mPresenter.getUploadService());
    }

    private void initView() {
        if (mRanking != null) {
            if (mRanking.agreeNum == 0) {
                mRanking.agreeNum = mRanking.agreeCount;
            }
           // mPresenter.checkThumb(mRanking.id);

//            ImageLoader.getInstance().displayImage(mRanking.imgSrc, mPhotoIv);
            LibGlide3Util.loadImg(mContext,mRanking.imgSrc,0,mPhotoIv);

            mThumbNumTv.setText(String.valueOf(mRanking.agreeNum));
            mUserNameTv.setText(mRanking.userName);
            mDateTv.setText(mRanking.createDate);
        }
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container,
                CommentFragment.newInstance(mVoa, mRanking.id, mRanking.userName,mRanking.videoUrl));
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setVideoViewParams();
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

    public void onThumbClick() {
        if (DubDBManager.getInstance().isAgree(myUid,mRanking.getID())) {
            //点赞前本地数据库判断
            ToastUtil.showToast(mContext,"您已经点过赞了~");
        }else {
            mPresenter.doThumb(mRanking.id);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        stopVideoView("0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mDownloadPresenter.detachView();
        //EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    @Override
    public void onBackPressed() {
        if (mVideoControl.isFullScreen()) {
            mVideoControl.exitFullScreen();
        } else {
            finish();
        }
    }

    @Override
    public void updateThumbIv(int action) { //点赞后操作
        int resId = action == ThumbAction.THUMB ? R.drawable.ic_thumb_yellow : R.drawable.thumb_gray;
        mThumbIv.setImageResource(resId);
    }

    @Override
    public void updateThumbNumTv(String id) {//点赞后操作
        int thumbNum = Integer.parseInt(mThumbNumTv.getText().toString());
        mThumbNumTv.setText(String.valueOf(thumbNum + 1));
        //数据库操作
        DubDBManager.getInstance().setAgree(myUid,id);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(mContext,resId);
    }
}
