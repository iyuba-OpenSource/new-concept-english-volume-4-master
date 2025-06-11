package com.iyuba.conceptEnglish.lil.concept_other.talkshow_fix.publish.watch;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.iyuba.conceptEnglish.databinding.ActivityWatchDubbingFixBinding;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.ScreenUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.talkshow.lesson.NormalVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.BaseVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.MyOnTouchListener;
import com.iyuba.core.talkshow.lesson.watch.WatchDubbingMvpView;
import com.iyuba.core.talkshow.lesson.watch.WatchDubbingPresenter;

import personal.iyuba.personalhomelibrary.utils.StatusBarUtil;

/**
 * @title:  详情界面
 * @date: 2023/8/2 11:45
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Fix_WatchDubbingActivity extends BaseViewBindingActivity<ActivityWatchDubbingFixBinding> implements WatchDubbingMvpView {

    private static final String RANKING = "ranking";
    private static final String VOA = "voa";
    private static final String UID = "uid";

    private boolean isInterrupted = false;
    private Ranking mRanking;
    private TalkLesson mVoa;

    WatchDubbingPresenter mPresenter;
    private NormalVideoControl mVideoControl;

    int uid;
    private String myUid;
    private Context mContext;

    public static Intent buildIntent(Context context, Ranking ranking, TalkLesson voa, Integer uid) {
        Intent intent = new Intent();
        intent.setClass(context, Fix_WatchDubbingActivity.class);
        intent.putExtra(RANKING, ranking);
        intent.putExtra(VOA, voa);
        intent.putExtra(UID, uid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initOldView();
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), com.iyuba.lib.R.color.black, getTheme()));

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
            binding.layoutCenter.thumbIv.setImageDrawable(mContext.getResources().getDrawable(com.iyuba.lib.R.drawable.ic_thumb_yellow));
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.videoView.pause();
        stopVideoView("0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    /******************回调数据*****************/
    @Override
    public void updateThumbIv(int action) {
        int resId = action == ThumbAction.THUMB ? com.iyuba.lib.R.drawable.ic_thumb_yellow : com.iyuba.lib.R.drawable.thumb_gray;
        binding.layoutCenter.thumbIv.setImageResource(resId);
    }

    @Override
    public void updateThumbNumTv(String id) {
        int thumbNum = Integer.parseInt(binding.layoutCenter.thumbNumTv.getText().toString());
        binding.layoutCenter.thumbNumTv.setText(String.valueOf(thumbNum + 1));
        //数据库操作
        DubDBManager.getInstance().setAgree(myUid,id);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(mContext,resId);
    }

    /*******************初始化和其他************************/
    private void initOldView(){
        RelativeLayout thumbLayout = findViewById(com.iyuba.lib.R.id.thumb_layout);
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
                if (binding.videoView == null) {
                    return false;
                }

                if (binding.videoView.isPlaying()) {
                    binding.videoView.pause();
                    stopVideoView("0");
                } else {
                    binding.videoView.start();
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
        binding.videoView.setControls(mVideoControl);
        mVideoControl.setOnTouchListener(listener);
        binding.videoView.setVideoPath(Constant.getNewDubbingUrl(mRanking.videoUrl));
        binding.videoView.setOnPreparedListener(mVideoPreparedListener);
        binding.videoView.setOnCompletionListener(mVideoCompletionListener);

        //设置禁止投屏显示
        mVideoControl.showControlToTv(false);
    }

    OnPreparedListener mVideoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            binding.videoView.start();
        }
    };

    OnCompletionListener mVideoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            binding.videoView.restart();
            binding.videoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    binding.videoView.pause();
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
                    if (binding.videoView.isPlaying()) {
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

//            ImageLoader.getInstance().displayImage(mRanking.imgSrc, binding.layoutCenter.photoIv);
            LibGlide3Util.loadImg(mContext,mRanking.imgSrc,0,binding.layoutCenter.photoIv);

            binding.layoutCenter.thumbNumTv.setText(String.valueOf(mRanking.agreeNum));
            binding.layoutCenter.userNameTv.setText(mRanking.userName);
            binding.layoutCenter.dateTv.setText(mRanking.createDate);
        }
    }

    private void initFragment() {
        //屏蔽这里
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.replace(com.iyuba.lib.R.id.container,
//                CommentFragment.newInstance(mVoa, mRanking.id, mRanking.userName,mRanking.videoUrl));
//        transaction.commit();
    }

    private void setVideoViewParams() {
        ViewGroup.LayoutParams lp = binding.videoView.getLayoutParams();
        int[] screenSize = ScreenUtils.getScreenSize(this);
        lp.width = screenSize[0];
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = screenSize[1]; // 16 : 9
        } else {
            lp.height = (int) (lp.width * 0.5625);
        }
        binding.videoView.setLayoutParams(lp);
    }

    public void onThumbClick() {
        if (DubDBManager.getInstance().isAgree(myUid,mRanking.getID())) {
            //点赞前本地数据库判断
            ToastUtil.showToast(mContext,"您已经点过赞了~");
        }else {
            mPresenter.doThumb(mRanking.id);
        }
    }
}
