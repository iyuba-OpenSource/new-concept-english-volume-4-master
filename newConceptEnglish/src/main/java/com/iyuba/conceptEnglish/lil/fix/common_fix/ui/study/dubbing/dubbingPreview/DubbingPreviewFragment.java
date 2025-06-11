package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingPreview;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.FragmentPreviewBinding;
import com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail.TalkShowDetailVideoControl;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.DubbingPreviewShowBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.DubbingPreviewSubmitBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.UrlLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.JuniorBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.NetHostManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ShareUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.talkshow.lesson.videoView.BaseVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.MyOnTouchListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: 配音的预览界面
 * @date: 2023/6/7 15:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingPreviewFragment extends BaseViewBindingFragment<FragmentPreviewBinding> implements DubbingPreviewView{

    //类型
    private String types;
    //voaId
    private String voaId;

    private DubbingPreviewPresenter presenter;
    //章节数据
    private BookChapterBean chapterBean;
    //章节详情数据
    private List<ChapterDetailBean> detailBeanList;
    //合并后的数据
    private DubbingPreviewShowBean showBean;

    //视频控制器
    private TalkShowDetailVideoControl videoControl;
    //背景音播放器
    private MediaPlayer bgAudioPlayer;
    //评测播放器
    private MediaPlayer evalPlayer;
    //当前评测播放的地址
    private String evalPlayPath = "";
    //计时器
    private String allTimerTag = "allTimerTag";

    //加载弹窗
    private LoadingDialog loadingDialog;

    public static DubbingPreviewFragment getInstance(String types, String voaId){
        DubbingPreviewFragment fragment = new DubbingPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaId, voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        types = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaId);

        presenter = new DubbingPreviewPresenter();
        presenter.attachView(this);

        chapterBean = presenter.getChapterData(types,voaId);
        detailBeanList = presenter.getChapterDetail(types,voaId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPlayer();
        initClick();

        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopPlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();
    }

    /****************************回调数据*************************/
    @Override
    public void showPublishData(String shareId) {
        closeLoading();

        if (!TextUtils.isEmpty(shareId)){
            ToastUtil.showToast(getActivity(),"提交配音数据成功");

            if (InfoHelper.getInstance().openShare()){
                //这里进行数据的分享
                sharePublish(types,chapterBean.getBookId(),shareId,chapterBean.getPicUrl());
            }
        }else {
            ToastUtil.showToast(getActivity(),"提交配音数据失败～");
        }
    }

    /**********************初始化数据********************/
    private void initPlayer(){
        //视频播放器
        MyOnTouchListener touchListener = new MyOnTouchListener(getActivity());
        touchListener.setSingleTapListener(singleTapListener);
        videoControl = new TalkShowDetailVideoControl(getActivity());
        videoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
        videoControl.setFullScreenBtnVisible(false);
        videoControl.setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                if (binding.videoView.isPlaying()) {
                    stopPlayer();
                } else {
                    startPlayer();
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
        videoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                try {
                    StackUtil.getInstance().finishCur();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        videoControl.setOnTouchListener(touchListener);
        binding.videoView.setControls(videoControl);
        binding.videoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                stopPlayer();
            }
        });
        //背景音播放器
        bgAudioPlayer = new MediaPlayer();
        //评测播放器
        evalPlayer = new MediaPlayer();
    }

    private void initClick(){
        if (InfoHelper.getInstance().openShare()){
            binding.tvPublishShare.setText("发布并分享");
        }else {
            binding.tvPublishShare.setText("发布该配音");
        }
        binding.tvPublishShare.setOnClickListener(v->{
            stopPlayer();

            DubbingPreviewSubmitBean submitBean = getSubmitData();
            if (submitBean==null){
                ToastUtil.showToast(getActivity(),"数据错误，请重试~");
                return;
            }

            //提交数据
            startLoading("正在提交配音数据~");
            presenter.submitDubbingPreview(submitBean);
        });
        binding.tvBackHome.setVisibility(View.INVISIBLE);
        binding.tvBackHome.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
        binding.backBtn.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
    }

    /*********************刷新和组合数据******************/
    //获取数据进行展示
    private void refreshData(){
        showBean = presenter.margeDubbingShowData(types,voaId);
        if (showBean == null){
            ToastUtil.showToast(getActivity(),"获取预览数据失败~");
            binding.tvPublishShare.setVisibility(View.INVISIBLE);
            return;
        }

        //准确度-5
        int rightScore = (int) (showBean.getRightScore()*10);
        binding.progressAccuracy.setMax(50);
        binding.progressAccuracy.setProgress(rightScore);
        binding.tvAccuracy.setText(String.valueOf(rightScore));
        //完整度-1
        int completeScore = (int) (showBean.getCompleteScore()*10);
        binding.progressCompleteness.setMax(10);
        binding.progressCompleteness.setProgress(completeScore);
        binding.tvCompleteness.setText(String.valueOf(completeScore));
        //流畅度-5
        int fluentScore = (int) (showBean.getFluentScore()*10);
        binding.progressFluence.setMax(50);
        binding.progressFluence.setProgress(fluentScore);
        binding.tvFluence.setText(String.valueOf(fluentScore));

        setPlayer();
    }

    //视频触摸回调
    private MyOnTouchListener.SingleTapListener singleTapListener = new MyOnTouchListener.SingleTapListener() {
        @Override
        public void onSingleTap() {
            if (videoControl != null) {
                if (videoControl.getControlVisibility() == View.GONE) {
                    videoControl.show();
                    if (binding.videoView.isPlaying()) {
                        videoControl.hideDelayed(VideoControls.DEFAULT_CONTROL_HIDE_DELAY);
                    }
                } else {
                    videoControl.hideDelayed(0);
                }
            }
        }
    };

    /*********************************音视频操作********************************/
    //设置数据
    private void setPlayer(){
        try {
            binding.videoView.setVideoURI(Uri.fromFile(new File(showBean.getVideoPath())));
            if (bgAudioPlayer!=null){
                bgAudioPlayer.setDataSource(showBean.getBgAudioPath());
                bgAudioPlayer.prepare();
            }
            startPlayer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //开启播放器
    private void startPlayer(){
        if (binding.videoView.getCurrentPosition()>=binding.videoView.getDuration()){
            binding.videoView.seekTo(0);
        }

        binding.videoView.setVolume(0f);
        binding.videoView.start();

        RxTimer.getInstance().multiTimerInMain(allTimerTag, 0, 100L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                checkPlayerPlay();
            }
        });
    }

    //关闭播放器
    private void stopPlayer(){
        if (binding!=null&&binding.videoView.isPlaying()){
            binding.videoView.pause();
        }
        if (bgAudioPlayer!=null&&bgAudioPlayer.isPlaying()){
            bgAudioPlayer.pause();
        }
        if (evalPlayer!=null&&evalPlayer.isPlaying()){
            evalPlayer.pause();
        }

        RxTimer.getInstance().cancelTimer(allTimerTag);
    }

    //判断当前播放器的显示
    private void checkPlayerPlay(){
        boolean isPlaying = false;

        try {
            if (showBean.getDubbingList()==null||showBean.getDubbingList().size()==0){
                return;
            }

            //当前视频进度
            long curTime = binding.videoView.getCurrentPosition();

            eval:for (int i = 0; i < showBean.getDubbingList().size(); i++) {
                DubbingPreviewShowBean.DubbingBean dubbingBean =showBean.getDubbingList().get(i);

                //当前的评测数据位置
                long startTime = (long) (dubbingBean.getStartTime()*1000);
                long endTime = (long) (dubbingBean.getEndTime()*1000);

                if (curTime>=startTime&&curTime<=endTime){
                    isPlaying = true;
                    bgAudioPlayer.pause();

                    if (evalPlayPath.equals(dubbingBean.getLocalPath())){
                        if (!evalPlayer.isPlaying()){
                            long progressTime = curTime-startTime;
                            evalPlayer.seekTo((int) progressTime);
                            evalPlayer.start();

                            Log.d("评测播放", "checkPlayerPlay: 1111--"+progressTime);
                        }
                    }else {
                        evalPlayPath = dubbingBean.getLocalPath();

                        evalPlayer.reset();
                        evalPlayer.setDataSource(dubbingBean.getLocalPath());
                        evalPlayer.prepare();
                        evalPlayer.start();

                        Log.d("评测播放", "checkPlayerPlay: 1111--"+dubbingBean.getLocalPath());
                    }

                    break eval;
                }
            }

            if (!isPlaying&&!bgAudioPlayer.isPlaying()){
                bgAudioPlayer.seekTo((int) curTime);
                bgAudioPlayer.start();
            }
        }catch (Exception e){
            stopPlayer();
            ToastUtil.showToast(getActivity(),"加载数据异常~");
        }
    }

    /*********************************发布预览数据*********************/
    private DubbingPreviewSubmitBean getSubmitData(){
        String appName = "primaryEnglish";
        if (types.equals(TypeLibrary.BookType.junior_middle)){
            appName = "juniorEnglish";
        }

        int category = Integer.parseInt(JuniorBookChooseManager.getInstance().getBookSmallTypeId());//类型id
        int paraId = 0;
        int idIndex = 0;
        int shuoshuoType = 3;
        String topic = FixUtil.getTopic(types);
        int flag = 1;
        String format = "json";
        String platform = "android";

        String userName = UserInfoManager.getInstance().getUserName();
        int score = (int) (showBean.getRightScore()*20);
        String audioUrl = getBgSubmitUrl(chapterBean.getBgAudioUrl());

        //合并评测数据
        List<DubbingPreviewShowBean.DubbingBean> evalList = showBean.getDubbingList();
        List<DubbingPreviewSubmitBean.WavListBean> wavList = new ArrayList<>();
        if (evalList!=null&&evalList.size()>0){
            for (int i = 0; i < evalList.size(); i++) {
                DubbingPreviewShowBean.DubbingBean evalBean = evalList.get(i);

                //获取开始时间、持续时间、结束时间
                double startTime = evalBean.getStartTime();
                double duration = presenter.trans1Data(getAudioPlayTime(evalBean.getRemoteUrl())/1000.0f);
                double endTime = presenter.trans1Data(startTime+duration);
                //

                wavList.add(new DubbingPreviewSubmitBean.WavListBean(
                        getEvalSubmitUrl(evalBean.getRemoteUrl()),
                        startTime,
                        duration,
                        endTime,
                        evalBean.getCurIndex()
                ));
            }
        }

        //合并数据
        return new DubbingPreviewSubmitBean(
                appName,
                category,
                flag,
                format,
                idIndex,
                paraId,
                platform,
                score,
                shuoshuoType,
                audioUrl,
                topic,
                userName,
                Integer.parseInt(voaId),
                wavList
        );
    }


    //获取背景音的url
    private String getBgSubmitUrl(String url){
        String prefix = "http://staticvip.iyuba.cn/sounds/voa";
        if (url.startsWith(prefix)){
            return url.replace(prefix,"");
        }
        return url;
    }

    //获取评测音频的url
    private String getEvalSubmitUrl(String url){
        String prefix = UrlLibrary.HTTP_USERSPEECH+ NetHostManager.getInstance().getDomainShort()+"/voa/";
        if (url.startsWith(prefix)){
            return url.replace(prefix,"");
        }
        return url;
    }

    //获取音频的时间
    private long getAudioPlayTime(String url){
        try {
            MediaPlayer audioPlayer = new MediaPlayer();
            audioPlayer.setDataSource(url);
            audioPlayer.prepare();

            return audioPlayer.getDuration();
        }catch (Exception e){
            return 0;
        }
    }

    //开启加载
    private void startLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    //关闭加载
    private void closeLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //分享发布的内容
    private void sharePublish(String types,String bookId,String shuoshuoId,String imageUrl){
        String title = "播音员：" + UserInfoManager.getInstance().getUserName() + " " + chapterBean.getTitleCn();
        String text = getResources().getString(R.string.app_name)+"\t"+chapterBean.getTitleCn();
        String shareUrl = "http://voa."+ NetHostManager.getInstance().getDomainShort()+"/voa/talkShowShare.jsp?shuoshuoId="+shuoshuoId+"&apptype="+FixUtil.getTopic(types);

        ShareUtil.getInstance().shareArticle(getActivity(),types,bookId,voaId,UserInfoManager.getInstance().getUserId(),title,text,imageUrl,shareUrl);
    }
}
