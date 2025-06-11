package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnSeekCompletionListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionPageCallback;
import com.hjq.permissions.XXPermissions;
import com.iyuba.conceptEnglish.databinding.FragmentTalkDetailBinding;
import com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail.TalkShowDetailVideoControl;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.download.DubbingDownloadPresenter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingPreview.DubbingPreviewActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank.DubbingRankActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FileManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.RecordManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.xxpermission.PermissionBackListener;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.sqlite.op.WordOp;
import com.iyuba.core.event.DownloadEvent;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.core.talkshow.lesson.videoView.MyOnTouchListener;
import com.iyuba.play.ExtendedPlayer;
import com.iyuba.sdk.other.NetworkUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * @title: 配音界面
 * @date: 2023/6/6 10:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingFragment extends BaseViewBindingFragment<FragmentTalkDetailBinding> implements DubbingView{

    //类型
    private String types;
    //voaId
    private String voaId;

    private DubbingAdapter dubbingAdapter;
    private DubbingPresenter presenter;
    private DubbingDownloadPresenter downloadPresenter;

    //章节数据
    private BookChapterBean chapterBean;

    //是否已经加载文件完成
    private boolean isFileLoadFinish = false;

    //背景音播放器
    private MediaPlayer bgAudioPlayer;
    //是否背景音可用
    private boolean isBgAudioUse = false;
    //单词播放器
    private MediaPlayer wordPlayer;
    //评测播放器
    private ExtendedPlayer evalPlayer;
    //视频控制器
    private TalkShowDetailVideoControl videoControl;
    //视频播放标识类
    private String videoPlayTag = "videoPlayTag";
    //录音器
    private RecordManager recordManager;
    //是否正在录音
    private boolean isRecording = false;
    //录音标识类
    private String recordTag = "recordTag";
    //单词收藏数据库
    private WordOp wordOp;
    //单词查询的弹窗
//    private SearchWordDialog searchWordDialog;

    //这里做一下数据标记，标记下评测开始时的总时间和进度时间
    private long curEvalTotalTime = 0;
    private long curEvalProgressTime = 0;

    public static DubbingFragment getInstance(String types,String voaId){
        DubbingFragment fragment = new DubbingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaid, voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        types = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaid);

        presenter = new DubbingPresenter();
        presenter.attachView(this);

        chapterBean = presenter.getChapterData(types,voaId);

        wordOp = new WordOp(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initDownload();
        initBottom();
        initPlayer();
        initClick();

        showMsgByStatus();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (binding!=null&&binding.videoViewDub.isPlaying()){
            binding.videoViewDub.pause();
        }
//        closeSearchWordDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        stopRecord(true);
        pauseVideoPlay();
        pauseRecordPlay();
        pauseBgAudioPlay();

        if (!isFileLoadFinish){
            downloadPresenter.cancelDownload();
        }
        presenter.detachView();
    }

    /********************回调数据*********************/
    @Override
    public void showSingleEval(EvalChapterBean bean) {
        isRecording = false;

        if (bean!=null){
            dubbingAdapter.refreshEvalLoading(false,true);
        }else {
            dubbingAdapter.refreshEvalLoading(false,false);
            ToastUtil.showToast(getActivity(),"提交评测失败，请重试～");
        }
    }

    @Override
    public void showSearchWord(Word_detail detail) {
        if (detail!=null){
//            showSearchWordDialog(detail);
            NewSearchActivity.start(getActivity(),detail.key);
        }else {
            ToastUtil.showToast(getActivity(),"查询单词失败，请重试~");
        }
    }

    @Override
    public void showWordMsg(boolean isSuccess, boolean isInsert, Word_detail detail) {
        binding.jiexiRoot.setVisibility(View.GONE);

        if (isSuccess){
            if (isInsert){
                ToastUtil.showToast(getActivity(),"收藏单词成功~");
            }else {
                ToastUtil.showToast(getActivity(),"取消收藏单词成功~");
            }
        }else {
            if (isInsert){
                ToastUtil.showToast(getActivity(),"收藏单词失败~");
            }else {
                ToastUtil.showToast(getActivity(),"取消收藏单词失败~");
            }
            return;
        }

        if (isInsert){
            Word newWord = new Word();
            newWord.audioUrl = detail.audio;
            newWord.def = detail.def;
            newWord.userid = String.valueOf(UserInfoManager.getInstance().getUserId());
            newWord.pron = detail.pron;
            newWord.key = detail.key;
            wordOp.saveData(newWord);
        }else {
            wordOp.deleteItemWord(String.valueOf(UserInfoManager.getInstance().getUserId()),detail.key);
        }
    }

    /***********************初始化数据*****************/
    private void initList(){
        dubbingAdapter = new DubbingAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(dubbingAdapter);
        dubbingAdapter.setOnEvalCallBackListener(new DubbingAdapter.OnEvalCallBackListener() {
            @Override
            public void switchItem(int nextPosition) {
                //判断是否录音
                if (isRecording){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }
                //暂停其他播放
                pause();
                //刷新位置
                dubbingAdapter.refreshIndex(nextPosition);
            }

            @Override
            public void onPlayRead(long startTime, long endTime) {
                //判断是否录音
                if (isRecording){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }


                //判断播放还是暂停
                if (binding!=null){
                    if (binding.videoViewDub.isPlaying()){
                        pauseVideoPlay();
                    }else {
                        startVideoPlay(startTime, endTime);
                    }
                }
            }

            @Override
            public void onRecord(long startTime, long time,String types, String voaId, String paraId, String idIndex, String sentence) {
                //暂停其他播放
                pauseVideoPlay();

                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                //登录判断
                if (!UserInfoManager.getInstance().isLogin()){
                    showAbilityDialog(true,"录音评测");
                    return;
                }

                //会员和限制判断
                if (!presenter.isEvalNext(types,voaId,paraId,idIndex)){
                    showAbilityDialog(false,"录音评测");
                    return;
                }

                //权限判断
                if (Build.VERSION.SDK_INT < 35){
                    PermissionUtil.requestRecordAudio(getActivity(), new PermissionBackListener() {
                        @Override
                        public void allGranted() {

                            if (isRecording){
                                stopRecord(false);
                                submitEval(types, voaId, paraId, idIndex, sentence);
                            }else {
                                startRecordPlay(startTime);
                                startRecord(time,types,voaId,paraId,idIndex,sentence);
                            }
                        }

                        @Override
                        public void allDenied() {
                            ToastUtil.showToast(getActivity(),"当前功能需要授权后使用～");
                        }

                        @Override
                        public void halfPart(List<String> grantedList, List<String> deniedList) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("授权禁止")
                                    .setMessage("当前功能所需的部分权限被禁止，请授权后使用")
                                    .setPositiveButton("确定",null)
                                    .show();
                        }

                        @Override
                        public void warnRequest() {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("授权禁止")
                                    .setMessage("当前功能所需的 存储权限 和 录音权限 权限被禁止，请手动授权后使用")
                                    .setPositiveButton("暂不使用",null)
                                    .setNegativeButton("前往授权", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            PermissionUtil.jumpToSetting(getActivity());
                                        }
                                    }).show();
                        }
                    });
                }else {
                    boolean isPermissionOk = PermissionFixUtil.isPermissionOk(getActivity(),PermissionFixUtil.junior_talkShow_recordAudio_code);
                    if (isPermissionOk){
                        if (isRecording){
                            stopRecord(false);
                            submitEval(types, voaId, paraId, idIndex, sentence);
                        }else {
                            startRecordPlay(startTime);
                            startRecord(time,types,voaId,paraId,idIndex,sentence);
                        }
                    }
                }
            }

            @Override
            public void onPlayEval(long startTime, String playUrl, String playPath) {
                //停止其他播放
                pauseVideoPlay();
                //判断是否录音
                if (isRecording){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }

                //判断是否关闭
                if (evalPlayer!=null){
                    if (evalPlayer.isPlaying()){
                        pauseEvalPlay();
                    }else {
                        startEvalPlay(startTime,playUrl, playPath);
                    }
                }else {
                    startEvalPlay(startTime,playUrl, playPath);
                }
            }

            @Override
            public void onWordSelect(String wordStr) {
                //停止其他播放
                pauseVideoPlay();
                //判断是否录音
                if (isRecording){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }

                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                //查询单词
                if (!TextUtils.isEmpty(wordStr)&&wordStr.matches("^[a-zA-Z]*")) {
//                    showSearchWordDialog(wordStr);
                    NewSearchActivity.start(getActivity(),wordStr);
                } else {
                    ToastUtil.showToast(getActivity(), "请取英文单词");
                }
            }
        });
    }

    private void initDownload(){
        //下载内容
        downloadPresenter = new DubbingDownloadPresenter();
        downloadPresenter.init(getActivity(),chapterBean);
        downloadPresenter.setUrlAndFolder(chapterBean.getBgAudioUrl(),chapterBean.getVideoUrl());
    }

    private void initPlayer(){
        //背景音
        bgAudioPlayer = new MediaPlayer();
        bgAudioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isBgAudioUse = true;
            }
        });
        bgAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.pause();
            }
        });
        //评测播放器
        evalPlayer = new ExtendedPlayer(getActivity());
        //视频
//        MyOnTouchListener touchListener = new MyOnTouchListener(getActivity());
//        touchListener.setSingleTapListener(singleTapListener);
//        videoControl = new TalkShowDetailVideoControl(getActivity());
//        videoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
//        videoControl.setFullScreenBtnVisible(false);
//        videoControl.setButtonListener(new VideoControlsButtonListener() {
//            @Override
//            public boolean onPlayPauseClicked() {
//                if (binding.videoViewDub.isPlaying()) {
//                    pause();
//                } else {
//                    binding.videoViewDub.start();
//                }
//                return true;
//            }
//
//            @Override
//            public boolean onPreviousClicked() {
//                return false;
//            }
//
//            @Override
//            public boolean onNextClicked() {
//                return false;
//            }
//
//            @Override
//            public boolean onRewindClicked() {
//                return false;
//            }
//
//            @Override
//            public boolean onFastForwardClicked() {
//                return false;
//            }
//        });
//        videoControl.setBackCallback(new BaseVideoControl.BackCallback() {
//            @Override
//            public void onBack() {
//                try {
//                    getActivity().finish();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        videoControl.setOnTouchListener(touchListener);
//        binding.videoViewDub.setControls(videoControl);
        binding.videoViewDub.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                pauseVideoPlay();
            }
        });
        binding.videoViewDub.setOnSeekCompletionListener(new OnSeekCompletionListener() {
            @Override
            public void onSeekComplete() {

            }
        });
        //评测播放器
        evalPlayer = new ExtendedPlayer(getActivity());
        evalPlayer.setOnCompletionListener(new net.protyposis.android.mediaplayer.MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(net.protyposis.android.mediaplayer.MediaPlayer mediaPlayer) {
                pauseEvalPlay();
            }
        });
    }

    private void initBottom(){
        binding.previewDubbing.setVisibility(View.VISIBLE);
        binding.moreDubbing.setVisibility(View.INVISIBLE);
    }

    private void initClick(){
        binding.showBtn.setOnClickListener(v->{
            String showText = binding.showBtn.getText().toString();
            if (showText.equals("登录账号")){
//                startActivity(new Intent(getActivity(), Login.class));
                LoginUtil.startToLogin(getActivity());
            }else if (showText.equals("申请权限")){
                // TODO: 2025/4/17 针对android 15进行处理
                if (Build.VERSION.SDK_INT<35){
                    XXPermissions.with(getActivity())
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                            .request(new OnPermissionCallback() {
                                @Override
                                public void onGranted(List<String> permissions, boolean all) {
                                    if (all){
                                        showMsgByStatus();
                                    }else {
                                        binding.showMsg.setText("当前功能需要开启 存储权限 和 录音权限 ,请全部授权后使用～");
                                    }
                                }

                                @Override
                                public void onDenied(List<String> permissions, boolean never) {
                                    if (never){
                                        binding.showMsg.setText("当前功能需要开启 存储权限 和 录音权限 ,您已经拒绝授权，请手动授权后使用～");
                                        binding.showBtn.setText("手动授权");
                                    }
                                }
                            });
                }else {
                    boolean isPermissionOK = PermissionFixUtil.isPermissionOk(getActivity(),PermissionFixUtil.junior_talkShow_recordAudio_code);
                    if (isPermissionOK){
                        showMsgByStatus();
                    }
                }
            }else if (showText.equals("手动授权")){
                XXPermissions.startPermissionActivity(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, new OnPermissionPageCallback() {
                    @Override
                    public void onGranted() {
                        showMsgByStatus();
                    }

                    @Override
                    public void onDenied() {

                    }
                });
            }else if (showText.equals("重新下载")){
                downloadPresenter.download();
                checkVideoAndMedia(false);
            }else if (showText.equals("下载文件")){
                showLoadingByProgress(true,true,"正在下载","");
                downloadPresenter.download();
            }
        });
        binding.previewDubbing.setOnClickListener(v->{
            if (isRecording){
                ToastUtil.showToast(getActivity(),"正在录音评测中～");
                return;
            }

            //登录判断
            if (!UserInfoManager.getInstance().isLogin()){
                showAbilityDialog(true,"录音评测");
                return;
            }

            //合并预览限制
            if (!presenter.isPreview(chapterBean.getTypes(), chapterBean.getVoaId())){
                ToastUtil.showToast(getActivity(),"请评测至少一句后再预览~");
                return;
            }

            DubbingPreviewActivity.start(getActivity(),types,voaId);
        });
        binding.rankDubbing.setOnClickListener(v->{
            DubbingRankActivity.start(getActivity(),types,voaId);
        });
        binding.moreDubbing.setOnClickListener(v->{

        });
    }

    /*********************刷新数据*****************/
    //章节详情数据显示
    private void refreshDetailData(){
        List<ChapterDetailBean> list = presenter.getChapterDetail(types,voaId);
        if (list!=null&&list.size()>0){
            dubbingAdapter.refreshData(list);
        }else {
            binding.showLayout.setVisibility(View.VISIBLE);
            binding.showLoading.setVisibility(View.GONE);
            binding.showMsg.setText("当前章节暂无详情数据～");
        }
    }

    //判断数据显示状态
    private void showMsgByStatus(){
        List<ChapterDetailBean> detailList = presenter.getChapterDetail(types,voaId);
        if (detailList==null||detailList.size()==0){
            binding.showLayout.setVisibility(View.VISIBLE);
            binding.showLoading.setVisibility(View.GONE);
            binding.showMsg.setText("当前章节暂无详情数据～");
            return;
        }

        if (!UserInfoManager.getInstance().isLogin()){
            binding.showLayout.setVisibility(View.VISIBLE);
            binding.showLoading.setVisibility(View.GONE);
            binding.showMsg.setText("当前功能需要登录后使用，请先登录~");
            binding.showBtn.setText("登录账号");
            return;
        }

        // TODO: 2025/4/17 针对android 15进行处理
        if (Build.VERSION.SDK_INT<35){
            if (!XXPermissions.isGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)){
                binding.showLayout.setVisibility(View.VISIBLE);
                binding.showLoading.setVisibility(View.GONE);
                binding.showMsg.setText("当前功能需要开启 存储权限 和 录音权限 ,请授权此权限后使用~");
                binding.showBtn.setText("申请权限");
            }else {
                checkVideoAndMedia(true);
            }
        }else {
            if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                binding.showLayout.setVisibility(View.VISIBLE);
                binding.showLoading.setVisibility(View.GONE);
                binding.showMsg.setText("当前功能需要开启 录音权限 ,请授权此权限后使用~");
                binding.showBtn.setText("申请权限");
            }else {
                checkVideoAndMedia(true);
            }
        }
    }

    //加载数据
    private void showLoadingByProgress(boolean isShow,boolean isLoading,String msg,String btn){
        if (isShow){
            binding.showLayout.setVisibility(View.VISIBLE);
            if (isLoading){
                binding.showLoading.setVisibility(View.VISIBLE);
                binding.showBtn.setVisibility(View.GONE);
                binding.showMsg.setText(msg);
            }else {
                binding.showLoading.setVisibility(View.GONE);
                binding.showBtn.setVisibility(View.VISIBLE);
                binding.showMsg.setText(msg);

                if (TextUtils.isEmpty(btn)){
                    binding.showBtn.setVisibility(View.GONE);
                }else {
                    binding.showBtn.setVisibility(View.VISIBLE);
                    binding.showBtn.setText(btn);
                }
            }
        }else {
            binding.showLayout.setVisibility(View.GONE);
            binding.showLoading.setVisibility(View.GONE);
            binding.showBtn.setVisibility(View.VISIBLE);
        }
    }

    //检查音频和视频文件是否下载
    private void checkVideoAndMedia(boolean isFirst){

        if (downloadPresenter.checkFileExist()){
            //展示数据进行操作
            showLoadingByProgress(false,false,"","");
            setPlayer();
        }else {
            if (isFirst){
                showLoadingByProgress(true,false,"首次使用需要下载音频和视频文件，时间较长，请确认是否下载所需文件~","下载文件");
            }else {
                showLoadingByProgress(true,true,"正在下载","");
                downloadPresenter.download();
            }
        }
    }

    //设置播放器
    private void setPlayer(){
        //顺便设置数据
        refreshDetailData();

        try {
            isFileLoadFinish = true;

            String videoPath = downloadPresenter.getVideoPath();
            binding.videoViewDub.setVideoURI(Uri.fromFile(new File(videoPath)));
            binding.videoViewDub.pause();
            String bgAudioPath = downloadPresenter.getAudioPath();
            Timber.e("背景音地址" + bgAudioPath);
            bgAudioPlayer.setDataSource(bgAudioPath);
            //mAccAudioPlayer.setDataSource(mTalkLesson.Sound);
            bgAudioPlayer.prepare();
        }catch (Exception e){
            isFileLoadFinish = false;
        }
    }

    //视频触摸回调
    private MyOnTouchListener.SingleTapListener singleTapListener = new MyOnTouchListener.SingleTapListener() {
        @Override
        public void onSingleTap() {
            if (videoControl != null) {
                if (videoControl.getControlVisibility() == View.GONE) {
                    videoControl.show();
                    if (binding.videoViewDub.isPlaying()) {
                        videoControl.hideDelayed(VideoControls.DEFAULT_CONTROL_HIDE_DELAY);
                    }
                } else {
                    videoControl.hideDelayed(0);
                }
            }
        }
    };

    /**************************事件************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                DubDBManager.getInstance().setDownload(chapterBean.getVoaId(),String.valueOf(UserInfoManager.getInstance().getUserId()), chapterBean.getTitleEn(),
                        chapterBean.getTitleCn(),chapterBean.getPicUrl(),chapterBean.getBookId());
                showLoadingByProgress(false,false,"","");
                setPlayer();
                break;
            case DownloadEvent.Status.DOWNLOADING:
                showLoadingByProgress(true,true,downloadEvent.msg,"");
                break;
            case DownloadEvent.Status.ERROR:
                showLoadingByProgress(true,false,"下载出错，文件内容缺失，请重新下载~","重新下载");
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VipChangeEvent event){
        showMsgByStatus();
    }

    //通用事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        //刷新课程详情的呢内容
        if (event.getType().equals(TypeLibrary.RefreshDataType.study_detailRefresh)){
            refreshDetailData();
        }
    }

    /**************************音视频**********************/
    //视频播放
    private void startVideoPlay(long startTime,long endTime){
        pauseVideoPlay();

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放～");
            return;
        }

        binding.videoViewDub.setVolume(1.0f);
        binding.videoViewDub.seekTo(startTime);
        binding.videoViewDub.start();

        RxTimer.getInstance().multiTimerInMain(videoPlayTag, 0, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long curTime = binding.videoViewDub.getCurrentPosition();
                if (curTime>=endTime){
                    pauseVideoPlay();
                }
            }
        });
    }

    //视频暂停
    private void pauseVideoPlay(){
        if (binding!=null&&binding.videoViewDub.isPlaying()){
            binding.videoViewDub.pause();
        }

        RxTimer.getInstance().cancelTimer(videoPlayTag);
    }

    //录音播放-视频播放+背景音播放
    private void startRecordPlay(long startTime){
        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放～");
            return;
        }

        //视频播放
        binding.videoViewDub.setVolume(0f);
        binding.videoViewDub.seekTo(startTime);
        binding.videoViewDub.start();
        //背景音播放
        bgAudioPlayer.seekTo((int) startTime);
        bgAudioPlayer.start();
    }

    //录音播放暂停
    private void pauseRecordPlay(){
        pauseVideoPlay();
        pauseBgAudioPlay();
    }

    //背景音暂停
    private void pauseBgAudioPlay(){
        if (bgAudioPlayer!=null&&bgAudioPlayer.isPlaying()){
            bgAudioPlayer.pause();
        }
    }

    //评测播放
    private void startEvalPlay(long startTime,String playUrl,String playPath){
        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放～");
            return;
        }

        if (evalPlayer==null){
            evalPlayer = new ExtendedPlayer(getActivity());
        }
        evalPlayer.reset();

        boolean isExist = FileManager.getInstance().isFileExist(playPath);
        if (isExist){
            evalPlayer.initialize(getActivity(),Uri.fromFile(new File(playPath)));
        }else {
            evalPlayer.initialize(getActivity(),Uri.parse(playUrl));
        }
        evalPlayer.prepareAndPlay();
        dubbingAdapter.refreshEvalPlay(true);

        //开启视频
        binding.videoViewDub.setVolume(0f);
        binding.videoViewDub.seekTo(startTime);
        binding.videoViewDub.start();
    }

    //评测暂停
    private void pauseEvalPlay(){
        if (evalPlayer!=null&&evalPlayer.isPlaying()){
            evalPlayer.pause();
        }
        dubbingAdapter.refreshEvalPlay(false);

        //关闭视频
        pauseVideoPlay();
    }

    //开始录音
    private void startRecord(long recordTime,String types,String voaId,String paraId,String idIndex,String sentence){
        try {
            String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types, voaId, paraId, idIndex,UserInfoManager.getInstance().getUserId());
            boolean isCreate = FileManager.getInstance().createEmptyFile(recordPath);
            if (!isCreate) {
                ToastUtil.showToast(getActivity(), "录音文件出现问题，请重试~");
                return;
            }

            if (!isBgAudioUse){
                ToastUtil.showToast(getActivity(), "背景音频不可用～");
                return;
            }

            isRecording = true;
            recordManager = new RecordManager(new File(recordPath));
            recordManager.startRecord();

            RxTimer.getInstance().multiTimerInMain(recordTag, 0, 100L, new RxTimer.RxActionListener() {
                @Override
                public void onAction(long number) {
                    long curTime = number * 100L;
                    Log.d("进度显示", curTime+"------"+recordTime);

                    //临时数据处理(非得要显示实际的进度)
                    curEvalProgressTime = curTime;
                    curEvalTotalTime = recordTime;

                    dubbingAdapter.refreshRecord(true, curTime,recordTime);

                    Log.d("录音评测", "onAction: --" + recordTime + "--" + curTime);

                    if (curTime >= recordTime) {
                        stopRecord(false);
                        submitEval(types,voaId,paraId,idIndex,sentence);
                    }
                }
            });

        }catch (Exception e){
            ToastUtil.showToast(getActivity(),"录音出现问题，请重试~");
        }
    }

    //停止录音
    private void stopRecord(boolean isStopRecord){
        if (isRecording){
            recordManager.stopRecord();
        }
        isRecording = false;
        RxTimer.getInstance().cancelTimer(recordTag);
//        dubbingAdapter.refreshRecord(false,0,0);
        dubbingAdapter.refreshRecord(false,curEvalProgressTime,curEvalTotalTime);
    }

    //暂停所有的操作
    private void pause(){
        pauseVideoPlay();
        pauseBgAudioPlay();
    }

    //提交评测
    private void submitEval(String types,String voaId,String paraId,String idIndex,String sentence){
        //停止播放
        pauseRecordPlay();
        //刷新显示加载
        dubbingAdapter.refreshEvalLoading(true,true);
        //提交评测
        String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types,voaId,paraId,idIndex,UserInfoManager.getInstance().getUserId());
        presenter.submitSingleEval(types,true,voaId,paraId,idIndex,sentence,recordPath);
    }

    /*********************单词查询****************/
    //显示查询弹窗
    /*private void showSearchWordDialog(String word){
        searchWordDialog = new SearchWordDialog(getActivity(),word);
        searchWordDialog.create();
        searchWordDialog.show();
    }*/

    //关闭查询弹窗
    /*private void closeSearchWordDialog(){
        if (searchWordDialog!=null&&searchWordDialog.isShowing()){
            searchWordDialog.dismiss();
        }
    }*/

    /**************************辅助功能***********************/
    //弹窗功能展示
    private void showAbilityDialog(boolean isLogin,String abName){
        String msg = null;
        if (isLogin){
            msg = "该功能需要登录后才可以使用，是否立即登录？";
        }else {
            msg = "继续使用"+abName+"需要VIP权限，是否立即开通解锁？";
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(abName)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (isLogin){
//                            Login.start(getActivity());
                            LoginUtil.startToLogin(getActivity());
                        }else {
                            NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_APP);
                        }
                    }
                }).setNegativeButton("取消",null)
                .show();
    }

    //展示单词查询界面
    /*private void showSearchWordDialog(Word_detail detail){
        binding.jiexiRoot.startAnimation(initAnimation());
        binding.jiexiRoot.setVisibility(View.VISIBLE);
        binding.word.setText(detail.key);
        binding.pron.setText(detail.pron);
        binding.def.setText(detail.def);

        //查询本地单词状态
        Word localWord = wordOp.findDataByName(detail.key,String.valueOf(UserInfoManager.getInstance().getUserId()));
        if (localWord!=null){
            binding.dialogBtnAddword.setText("删除");
        }else {
            binding.dialogBtnAddword.setText("添加");
        }

        binding.dialogBtnAddword.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()) {
                com.iyuba.core.common.util.ToastUtil.show(getActivity(), "请先登录");
                return;
            }


            //根据单词状态显示单词是否插入或者删除
            if (localWord!=null){
                presenter.insertOrDeleteWords(UserInfoManager.getInstance().getUserId(),detail,false);
            }else {
                presenter.insertOrDeleteWords(UserInfoManager.getInstance().getUserId(),detail,true);
            }
        });
        binding.close.setOnClickListener(v->{
            binding.jiexiRoot.setVisibility(View.GONE);
        });
        binding.ivAudio.setOnClickListener(v->{

        });
    }*/

    //动画样式
    /*public TranslateAnimation initAnimation() {
        TranslateAnimation animation = new TranslateAnimation(-300, 0, 0, 0);
        animation.setDuration(200);
        return animation;
    }*/
}
