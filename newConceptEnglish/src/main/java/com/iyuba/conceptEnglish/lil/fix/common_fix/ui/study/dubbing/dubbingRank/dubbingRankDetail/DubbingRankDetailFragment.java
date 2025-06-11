package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank.dubbingRankDetail;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.iyuba.conceptEnglish.databinding.ActivityWatchDubbingBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.NetHostManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.AgreeEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Dubbing_rank;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.DubbingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ImageUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.core.common.util.ScreenUtils;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.talkshow.lesson.NormalVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.BaseVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.MyOnTouchListener;

import org.greenrobot.eventbus.EventBus;

/**
 * @title: 配音排行详情界面
 * @date: 2023/6/13 18:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingRankDetailFragment extends BaseViewBindingFragment<ActivityWatchDubbingBinding> implements DubbingRankDetailView{

    private String types;
    private String voaId;
    private Dubbing_rank rank;

    private DubbingRankDetailPresenter presenter;

    //视频控制器
    private NormalVideoControl mVideoControl;

    public static DubbingRankDetailFragment getInstance(String types, String voaId, Dubbing_rank rank){
        DubbingRankDetailFragment fragment = new DubbingRankDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types,types);
        bundle.putString(StrLibrary.voaId,voaId);
        bundle.putSerializable(StrLibrary.data,rank);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        types = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaId);
        rank = (Dubbing_rank) getArguments().getSerializable(StrLibrary.data);

        presenter = new DubbingRankDetailPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initVideo();
        initClick();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (binding!=null){
            binding.videoView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();
    }

    /*******************************初始化********************************/
    private void initView(){
        binding.layoutCenter.thumbNumTv.setText(rank.getAgreeCount());
        if (isAgree()){
            binding.layoutCenter.thumbIv.setImageResource(com.iyuba.lib.R.drawable.ic_thumb_yellow);
        }else {
            binding.layoutCenter.thumbIv.setImageResource(com.iyuba.lib.R.drawable.thumb_gray);
        }

        ImageUtil.loadCircleImg(rank.getImgSrc(),0,binding.layoutCenter.photoIv);
        binding.layoutCenter.userNameTv.setText(rank.getUserName());
        binding.layoutCenter.dateTv.setText(rank.getCreateDate());
    }

    private void initVideo(){
        MyOnTouchListener listener = new MyOnTouchListener(getActivity());
        listener.setSingleTapListener(mSingleTapListener);

        mVideoControl = new NormalVideoControl(getActivity());
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_AUTO);
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                StackUtil.getInstance().finishCur();
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
                } else {
                    binding.videoView.start();
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
        binding.videoView.setVideoPath(getVideoUrl(rank.getVideoUrl()));
        binding.videoView.setOnPreparedListener(onPreparedListener);
        binding.videoView.setOnCompletionListener(onCompletionListener);
        //设置禁止投屏显示
        mVideoControl.showControlToTv(false);
    }

    private void initClick(){
        binding.layoutCenter.thumbLayout.setOnClickListener(v->{
            //判断是否已经点赞
            if (isAgree()){
                ToastUtil.showToast(getActivity(),"您已经点赞过了");
            }else {
                presenter.dubbingRankDetailAgree(rank.getId());
            }
        });
        binding.dubbingTv.setOnClickListener(v->{
            DubbingActivity.start(getActivity(),types,voaId);
        });
    }
    /********************************音视频*****************************/
    //设置界面样式
    private void setVideoViewParams() {
        ViewGroup.LayoutParams lp = binding.videoView.getLayoutParams();
        int[] screenSize = ScreenUtils.getScreenSize(getActivity());
        lp.width = screenSize[0];
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = screenSize[1]; // 16 : 9
        } else {
            lp.height = (int) (lp.width * 0.5625);
        }
        binding.videoView.setLayoutParams(lp);
    }

    //视频回调
    private OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            binding.videoView.start();
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            binding.videoView.restart();
            binding.videoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    binding.videoView.pause();
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

    /*********************************其他功能***********************/
    //获取视频的链接
    private String getVideoUrl(String videoUrl){
        String prefix = "http://userspeech."+ NetHostManager.getInstance().getDomainShort() + "/";
        return prefix+videoUrl;
    }

    //获取是否点赞
    private boolean isAgree(){
        AgreeEntity entity = CommonDataManager.getAgreeDataFromDB(String.valueOf(UserInfoManager.getInstance().getUserId()),rank.getUserid(),types,voaId,rank.getId());
        return entity!=null;
    }

    /********************************回调****************************/
    @Override
    public void showAgree(boolean isSuccess) {
        if (isSuccess){
            //刷新界面显示
            binding.layoutCenter.thumbIv.setImageResource(com.iyuba.lib.R.drawable.ic_thumb_yellow);
            int agreeCount = Integer.parseInt(rank.getAgreeCount());
            binding.layoutCenter.thumbNumTv.setText(String.valueOf(agreeCount+1));
            //保存数据
            AgreeEntity entity = new AgreeEntity(
                    String.valueOf(UserInfoManager.getInstance().getUserId()),
                    rank.getUserid(),
                    types,
                    voaId,
                    rank.getId());
            CommonDataManager.saveAgreeDataToDB(entity);
            //刷新数据
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.dubbing_rank));
        }else {
            ToastUtil.showToast(getActivity(),"点赞失败，请重试~");
        }
    }

    /********************************界面横竖屏切换********************/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setVideoViewParams();
    }
}
