package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.eval;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.FragmentFixEvalBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.UrlLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.NetHostManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Publish_eval;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.evalFix.EvalFixBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.evalFix.EvalFixDialog;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FileManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.RecordManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ShareUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.gson.GsonUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.xxpermission.PermissionBackListener;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.view.PermissionMsgDialog;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.sdk.other.NetworkUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: 评测界面
 * @date: 2023/5/23 23:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class EvalFragment extends BaseViewBindingFragment<FragmentFixEvalBinding> implements EvalView {

    //类型
    private String types;
    //voaId
    private String voaId;

    private BookChapterBean chapterBean;
    private EvalAdapter evalAdapter;
    private EvalPresenter presenter;

    //是否正在录音
    private boolean isRecord = false;
    //加载弹窗
    private LoadingDialog loadingDialog;

    //原文播放器
    private ExoPlayer readPlayer;
    //原文播放链接
    private String readPlayUrl = null;
    //原文播放标识位
    private String readPlayTag = "readPlayTag";
    //原文播放是否可用
    private boolean isCanPlay = false;

    //评测播放器
    private ExoPlayer evalPlayer;
    //评测播放标识位
    private String evalPlayTag = "evalPlayTag";

    //录音器
    private RecordManager recordManager;
    //录音的标识位
    private String recordTag = "recordTag";

    //合成播放器
    private ExoPlayer margePlayer;
    //合成播放链接
    private String margePlayUrl = null;
    //合成音频播放标识位
    private String margePlayTag = "margePlayTag";
    //合成音频是否可用
    private boolean isCanMargePlay = false;

    //权限弹窗
    private PermissionMsgDialog msgDialog = null;
    //纠音弹窗
    private EvalFixDialog evalFixDialog;

    public static EvalFragment getInstance(String types, String voaId) {
        EvalFragment fragment = new EvalFragment();
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

        presenter = new EvalPresenter();
        presenter.attachView(this);

        chapterBean = presenter.getChapterData(types, voaId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();
        initBottom();
        initClick();

        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();

        pauseReadPlay();
        pauseEvalPlay();
        pauseMargePlay();
        stopRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        if (evalFixDialog!=null){
            evalFixDialog.dismiss();
        }
        stopLoading();
        presenter.detachView();
    }

    /*********************初始化********************/
    private void initList() {
        evalAdapter = new EvalAdapter(getActivity(), new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(evalAdapter);
        evalAdapter.setOnEvalCallBackListener(new EvalAdapter.OnEvalCallBackListener() {
            @Override
            public void switchItem(int nextPosition) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseMargePlay();
                pauseReadPlay();
                pauseEvalPlay();

                evalAdapter.refreshIndex(nextPosition);
            }

            @Override
            public void onPlayRead(long startTime, long endTime) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseEvalPlay();
                pauseMargePlay();

                if (readPlayer != null) {
                    if (readPlayer.isPlaying()) {
                        pauseReadPlay();
                    } else {
                        startReadPlay(startTime, endTime);
                    }
                } else {
                    startReadPlay(startTime, endTime);
                }
            }

            @Override
            public void onRecord(long time, String types, String voaId, String paraId, String idIndex, String sentence) {
                checkRecord(time, types, voaId, paraId, idIndex, sentence);
            }

            @Override
            public void onPlayEval(String playUrl, String playPath) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseReadPlay();
                pauseMargePlay();

                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
                    return;
                }

                if (evalPlayer != null) {
                    if (evalPlayer.isPlaying()) {
                        pauseEvalPlay();
                    } else {
                        startEvalPlay(playUrl, playPath);
                    }
                } else {
                    startEvalPlay(playUrl, playPath);
                }
            }

            @Override
            public void onPublish(String types, String voaId, String paraId, String idIndex, int score, String evalUrl) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseReadPlay();
                pauseEvalPlay();
                pauseMargePlay();

                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastUtil.showToast(getActivity(), "请链接网络后重试~");
                    return;
                }

                startLoading("正在发布到排行榜～");
                presenter.publishSingleEval(types, voaId, idIndex, paraId, score, evalUrl);
            }

            @Override
            public void onShare(int totalScore, String audioUrl, String shareUrl) {
                if (isRecord) {
                    ToastUtil.showToast(getActivity(), "正在录音中～");
                    return;
                }

                pauseReadPlay();
                pauseEvalPlay();
                pauseMargePlay();

                shareSingleEval(chapterBean.getTitleCn(), totalScore, shareUrl, audioUrl);
            }

            @Override
            public void onEvalFix(String voaId, String paraId, String idIndex, String sentence, List<EvalChapterBean.WordBean> wordList) {
                //纠音功能(合成指定的数据，然后进行处理)
                List<EvalFixBean.WordEvalFixBean> wordFixList = new ArrayList<>();
                for (int i = 0; i < wordList.size(); i++) {
                    EvalChapterBean.WordBean tempWordData = wordList.get(i);
                    wordFixList.add(new EvalFixBean.WordEvalFixBean(
                            tempWordData.getContent(),
                            tempWordData.getPron2(),
                            Float.parseFloat(tempWordData.getScore()),
                            Integer.parseInt(tempWordData.getIndex()),
                            tempWordData.getUser_pron2()
                    ));
                }

                EvalFixBean fixBean = new EvalFixBean(
                        Integer.parseInt(voaId),
                        Integer.parseInt(paraId),
                        Integer.parseInt(idIndex),
                        sentence,
                        wordFixList
                );

                //转换数据并显示
                String showJsonData = GsonUtil.toJson(fixBean);
                evalFixDialog = EvalFixDialog.getInstance(showJsonData);
                evalFixDialog.show(getChildFragmentManager(),"EvalFixDialog");
            }
        });
    }

    private void initPlayer() {
        //获取当前的原文播放链接
        BookChapterBean chapterBean = presenter.getChapterData(types, voaId);
        if (chapterBean != null) {
            readPlayUrl = chapterBean.getAudioUrl();
        }

        //原文播放
        readPlayer = new ExoPlayer.Builder(getActivity()).build();
        MediaItem mediaItem = MediaItem.fromUri(readPlayUrl);
        readPlayer.setPlayWhenReady(false);
        readPlayer.setMediaItem(mediaItem);
        readPlayer.prepare();
        readPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //可用
                        isCanPlay = true;
                        break;
                    case Player.STATE_ENDED:
                        //完成
                        pauseReadPlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                if (!isResumed()){
                    return;
                }

                isCanPlay = false;
                ToastUtil.showToast(getActivity(), "播放原文音频出错～");
            }
        });

        //评测播放
        evalPlayer = new ExoPlayer.Builder(getActivity()).build();
        evalPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //准备
                        if (!getActivity().isDestroyed()) {
                            evalPlayer.play();
                        }
                        break;
                    case Player.STATE_ENDED:
                        //完成
                        pauseEvalPlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                pauseEvalPlay();
                ToastUtil.showToast(getActivity(), "播放评测音频出错～");
            }
        });

        //合成播放
        margePlayer = new ExoPlayer.Builder(getActivity()).build();
        margePlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //准备
                        isCanMargePlay = true;
                        binding.publish.setVisibility(View.VISIBLE);
                        binding.totalTime.setText(showTime(margePlayer.getDuration()));
                        break;
                    case Player.STATE_ENDED:
                        //完成
                        stopMargePlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(), "合成音频播放失败～");
                stopMargePlay();
            }
        });
    }

    private void initBottom() {
        binding.publish.setVisibility(View.INVISIBLE);
        binding.playTime.setText(showTime(0));
    }

    private void initClick() {
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseMargePlay();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isRecord) {
                    pauseMargePlay();
                    return;
                }

                startMargePlay(seekBar.getProgress());
            }
        });
        binding.marge.setOnClickListener(v -> {
            if (isRecord) {
                ToastUtil.showToast(getActivity(), "正在录音中～");
                return;
            }

            pauseReadPlay();
            pauseEvalPlay();
            pauseMargePlay();

            String showText = binding.marge.getText().toString();
            if (showText.equals("合成")) {
                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastUtil.showToast(getActivity(), "请链接网络后重试~");
                    return;
                }

                if (presenter.getCurChapterEvalCount(types,voaId)<2){
                    ToastUtil.showToast(getActivity(), "请至少评测两句后进行合成~");
                    return;
                }

                startLoading("正在合成音频");
                presenter.margeAudio(types, voaId);
            } else if (showText.equals("试听")) {
                startMargePlay(0);
            } else if (showText.equals("停止")) {
                stopMargePlay();
            }
        });
        binding.publish.setOnClickListener(v -> {
            if (isRecord) {
                ToastUtil.showToast(getActivity(), "正在录音中～");
                return;
            }

            pauseReadPlay();
            pauseEvalPlay();
            pauseMargePlay();

            if (TextUtils.isEmpty(margePlayUrl)) {
                ToastUtil.showToast(getActivity(), "请合成音频后发布～");
                return;
            }

            if (!NetworkUtil.isConnected(getActivity())) {
                ToastUtil.showToast(getActivity(), "请链接网络后重试~");
                return;
            }

            startLoading("正在发布到排行榜～");
            //取出模版数据
            String prefix = UrlLibrary.HTTP_USERSPEECH + NetHostManager.getInstance().getDomainShort() + "/voa/";
            String publishAudioUrl = margePlayUrl;
            if (publishAudioUrl.startsWith(prefix)) {
                publishAudioUrl = publishAudioUrl.replace(prefix, "");
            }
            presenter.publishMargeAudio(types, voaId, publishAudioUrl);
        });
    }

    /*******************刷新数据********************/
    private void refreshData() {
        List<ChapterDetailBean> list = presenter.getChapterDetail(types, voaId);
        if (list != null && list.size() > 0) {
            binding.noData.getRoot().setVisibility(View.GONE);
            evalAdapter.refreshData(list);
        } else {
            binding.noData.getRoot().setVisibility(View.VISIBLE);
            binding.noData.msgNoData.setText("暂无该章节数据");
        }
    }

    /********************回调数据******************/
    @Override
    public void showSingleEval(EvalChapterBean bean) {
        stopLoading();

        if (bean != null) {
            binding.marge.setText("合成");
            binding.totalTime.setText(showTime(0));
            margePlayUrl = null;
            isCanMargePlay = false;
            binding.score.setVisibility(View.INVISIBLE);
            binding.publish.setVisibility(View.INVISIBLE);

            evalAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToast(getActivity(), "录音评测失败，请重试～");
        }
    }

    @Override
    public void showMargeAudio(String margeAudioUrl) {
        stopLoading();

        if (!TextUtils.isEmpty(margeAudioUrl)) {
            ToastUtil.showToast(getActivity(), "合成音频成功～");
            margePlayUrl = fixMargeAudioUrl(margeAudioUrl);
            binding.score.setVisibility(View.VISIBLE);
            binding.score.setText(String.valueOf(presenter.getMargeAudioScore(types, voaId)));
            initMargePlay();
            pauseMargePlay();
        } else {
            ToastUtil.showToast(getActivity(), "合成音频失败～");
        }
    }

    @Override
    public void showPublishRank(boolean isSingle, Publish_eval bean) {
        stopLoading();

        if (bean != null) {
            ToastUtil.showToast(getActivity(), "发布到排行榜成功，请前往排行界面查看");

            if (isSingle) {
                evalAdapter.refreshShare(String.valueOf(bean.getShuoshuoId()));
            }
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.eval_rank));
        } else {
            ToastUtil.showToast(getActivity(), "发布到排行榜失败，请重试～");
        }
    }

    /******************原文*****************/
    private void startReadPlay(long startTime, long endTime) {
        if (TextUtils.isEmpty(readPlayUrl)) {
            ToastUtil.showToast(getActivity(), "未获取到该音频的文件～");
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
            return;
        }

        if (readPlayer == null) {
            readPlayer = new ExoPlayer.Builder(getActivity()).build();
            MediaItem mediaItem = MediaItem.fromUri(readPlayUrl);
            readPlayer.setMediaItem(mediaItem);
            readPlayer.prepare();
        }

        if (!isCanPlay) {
            ToastUtil.showToast(getActivity(), "原文音频正在加载中～");
            return;
        }

        readPlayer.seekTo(startTime);
        readPlayer.play();

        RxTimer.getInstance().multiTimerInMain(readPlayTag, 500L, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //刷新ui
                long totalPlayTime = endTime - startTime;
                long curPlayTime = readPlayer.getCurrentPosition();
                long playedTime = curPlayTime - startTime;
                Log.d("显示进度", playedTime+"----"+totalPlayTime+"---"+curPlayTime);
                evalAdapter.refreshReadPlay(true, playedTime, totalPlayTime);

                if (curPlayTime >= endTime) {
                    pauseReadPlay();
                }
            }
        });
    }

    private void pauseReadPlay() {
        if (readPlayer != null && readPlayer.isPlaying()) {
            readPlayer.pause();
        }

        evalAdapter.refreshReadPlay(false, 0, 0);
        RxTimer.getInstance().cancelTimer(readPlayTag);
    }

    /******************录音*****************/
    private void checkRecord(long time, String types, String voaId, String paraId, String idIndex, String sentence) {
        pauseReadPlay();
        pauseEvalPlay();
        pauseMargePlay();

        //登录判断
        if (!UserInfoManager.getInstance().isLogin()) {
            showAbilityDialog(true, "录音评测");
            return;
        }

        //会员和限制判断
        if (!presenter.isEvalNext(types, voaId, paraId, idIndex)) {
            showAbilityDialog(false, "录音评测");
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后重试～");
            return;
        }

        List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
        //权限判断
        if (Build.VERSION.SDK_INT < 35){
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));
        }
        msgDialog = new PermissionMsgDialog(getActivity());
        msgDialog.showDialog(null, pairList, true, new PermissionMsgDialog.OnPermissionApplyListener() {
            @Override
            public void onApplyResult(boolean isSuccess) {
                if (isSuccess){
                    if (isRecord) {
                        stopRecord();

                        startLoading("正在进行录音评测~");
                        String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types, voaId, paraId, idIndex, UserInfoManager.getInstance().getUserId());
                        presenter.submitSingleEval(types, true, voaId, paraId, idIndex, sentence, recordPath);
                    } else {
                        startRecord(time, types, voaId, paraId, idIndex,sentence);
                    }
                }
            }
        });
        /*else {
            boolean isPermissionOk = PermissionFixUtil.isPermissionOk(getActivity(),PermissionFixUtil.junior_eval_recordAudio_code);
            if (isPermissionOk){
                if (isRecord) {
                    stopRecord();

                    startLoading("正在进行录音评测~");
                    String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types, voaId, paraId, idIndex, UserInfoManager.getInstance().getUserId());
                    presenter.submitSingleEval(types, true, voaId, paraId, idIndex, sentence, recordPath);
                } else {
                    startRecord(time, types, voaId, paraId, idIndex,sentence);
                }
            }
        }*/
    }

    private void startRecord(long time, String types, String voaId, String paraId, String idIndex,String sentence) {
        try {
            String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types, voaId, paraId, idIndex, UserInfoManager.getInstance().getUserId());
            boolean isCreate = FileManager.getInstance().createEmptyFile(recordPath);
            if (!isCreate) {
                ToastUtil.showToast(getActivity(), "录音文件出现问题，请重试~");
                return;
            }

            isRecord = true;
            recordManager = new RecordManager(new File(recordPath));
            recordManager.startRecord();

            //时间延长3s
            long recordTime = time + 3000L;
            RxTimer.getInstance().multiTimerInMain(recordTag, 0, 100L, new RxTimer.RxActionListener() {
                @Override
                public void onAction(long number) {
                    long curTime = number * 100L;
                    evalAdapter.refreshRecord(true, (int) recordManager.getVolume());

                    Log.d("录音评测", "onAction: --" + recordTime + "--" + curTime);

                    if (curTime >= recordTime) {
                        stopRecord();
                        startLoading("正在进行录音评测~");
                        String recordPath = FileManager.getInstance().getCourseEvalAudioPath(types, voaId, paraId, idIndex, UserInfoManager.getInstance().getUserId());
                        presenter.submitSingleEval(types, true, voaId, paraId, idIndex, sentence, recordPath);
                    }
                }
            });
        } catch (Exception e) {
            ToastUtil.showToast(getActivity(), "录音出现问题，请重试~");
        }
    }

    private void stopRecord() {
        if (recordManager != null && isRecord) {
            recordManager.stopRecord();
        }

        Log.d("录音评测", "onAction: --完成");
        isRecord = false;
        RxTimer.getInstance().cancelTimer(recordTag);
        evalAdapter.refreshRecord(false, 0);
    }

    /******************评测播放**************/
    private void startEvalPlay(String audioUrl, String audioPath) {
        if (TextUtils.isEmpty(audioUrl)) {
            ToastUtil.showToast(getActivity(), "未获取到该评测的音频文件～");
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
            return;
        }

        if (evalPlayer == null) {
            evalPlayer = new ExoPlayer.Builder(getActivity()).build();
        }
        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
        evalPlayer.setMediaItem(mediaItem);
        evalPlayer.prepare();

        RxTimer.getInstance().multiTimerInMain(readPlayTag, 0, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //刷新ui
                long totalPlayTime = evalPlayer.getDuration();
                long curPlayTime = evalPlayer.getCurrentPosition();
                evalAdapter.refreshEvalPlay(true, curPlayTime, totalPlayTime);

                if (curPlayTime >= totalPlayTime) {
                    pauseEvalPlay();
                }
            }
        });
    }

    private void pauseEvalPlay() {
        if (evalPlayer != null && evalPlayer.isPlaying()) {
            evalPlayer.pause();
        }

        evalAdapter.refreshEvalPlay(false, 0, 0);
        RxTimer.getInstance().cancelTimer(evalPlayTag);
    }

    /*******************合成播放**************/
    private void initMargePlay() {
        if (TextUtils.isEmpty(margePlayUrl)) {
            ToastUtil.showToast(getActivity(), "合成的音频链接不可用");
            return;
        }

        MediaItem mediaItem = MediaItem.fromUri(margePlayUrl);
        margePlayer.setMediaItem(mediaItem);
        margePlayer.setPlayWhenReady(false);
        margePlayer.prepare();

        binding.marge.setText("试听");
    }

    private void startMargePlay(long progress) {
        if (TextUtils.isEmpty(margePlayUrl)) {
            ToastUtil.showToast(getActivity(), "请合成音频后播放~");
            return;
        }

        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
            return;
        }

        if (!isCanMargePlay) {
            ToastUtil.showToast(getActivity(), "合成音频未初始化");
            return;
        }

        if (margePlayer != null) {
            margePlayer.seekTo(progress);
            margePlayer.play();
        }

        RxTimer.getInstance().multiTimerInMain(margePlayTag, 0, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long max = margePlayer.getDuration();
                long progress = margePlayer.getCurrentPosition();

                binding.seekBar.setMax((int) max);
                binding.seekBar.setProgress((int) progress);
                binding.marge.setText("停止");

                binding.playTime.setText(showTime(progress));
                binding.totalTime.setText(showTime(max));
            }
        });
    }

    private void pauseMargePlay() {
        if (margePlayer != null && margePlayer.isPlaying()) {
            margePlayer.pause();
        }

        RxTimer.getInstance().cancelTimer(margePlayTag);
    }

    private void stopMargePlay() {
        pauseMargePlay();

        binding.playTime.setText(showTime(0));
        binding.seekBar.setProgress(0);
        binding.marge.setText("试听");
    }

    /*******************辅助功能*******************/
    //时间显示
    private String showTime(long time) {
        if (time == 0) {
            return "00:00";
        }

        long totalTime = time / 1000;

        long minTime = totalTime / 60;
        long secTime = totalTime % 60;

        String showMin = "";
        String showSec = "";
        if (minTime >= 10) {
            showMin = String.valueOf(minTime);
        } else {
            showMin = "0" + String.valueOf(minTime);
        }
        if (secTime >= 10) {
            showSec = String.valueOf(secTime);
        } else {
            showSec = "0" + String.valueOf(secTime);
        }

        return showMin + ":" + showSec;
    }

    //显示加载弹窗
    private void startLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void stopLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    //弹窗功能展示
    private void showAbilityDialog(boolean isLogin, String abName) {
        String msg = null;
        if (isLogin) {
            msg = "该功能需要登录后才可以使用，是否立即登录？";
        } else {
            msg = "继续使用" + abName + "需要VIP权限，是否立即开通解锁？";
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(abName)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (isLogin) {
//                            Login.start(getActivity());
                            LoginUtil.startToLogin(getActivity());
                        } else {
                            NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_APP);
                        }
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    //分享评测的数据
    private void shareSingleEval(String titleCn, int totalScore, String shuoshuoId, String evalAudioUrl) {
        String title = UserInfoManager.getInstance().getUserName() + "在" + getResources().getString(R.string.app_name) + "的评测中获得了" + totalScore + "分";
        String content = titleCn;
        String evalUrl = evalAudioUrl;

        ShareUtil.getInstance().shareEval(getActivity(), types, voaId, shuoshuoId, evalUrl, UserInfoManager.getInstance().getUserId(), title, content);
    }

    //获取合成音频的音频链接
    private String fixMargeAudioUrl(String suffix){
        String margeAudioUrl = "";

        if (TextUtils.isEmpty(suffix)){
            return margeAudioUrl;
        }

        switch (types){
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                margeAudioUrl = FixUtil.fixJuniorEvalAudioUrl(suffix);
                break;
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //小说
                margeAudioUrl = FixUtil.fixNovelEvalAudioUrl(suffix);
                break;
        }

        return margeAudioUrl;
    }

    /************************************回调**************************************************/
    //通用刷新事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        //刷新课程详情的内容
        if (event.getType().equals(TypeLibrary.RefreshDataType.study_detailRefresh)){
            refreshData();
        }
    }
}
