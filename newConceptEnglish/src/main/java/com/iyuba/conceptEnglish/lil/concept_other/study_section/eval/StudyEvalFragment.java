package com.iyuba.conceptEnglish.lil.concept_other.study_section.eval;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.hjq.permissions.XXPermissions;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.FragmentEvalBinding;
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceDataItem;
import com.iyuba.conceptEnglish.han.utils.CorrectEvalHelper;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_result;
import com.iyuba.conceptEnglish.lil.concept_other.util.PermissionDialogUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.evalFix.EvalFixBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.evalFix.EvalFixDialog;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line.PractiseLineEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.BigDecimalUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.RecordManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.LibRxTimer;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.EvaluateBean;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.conceptEnglish.util.GsonUtils;
import com.iyuba.conceptEnglish.util.LoadIconUtil;
import com.iyuba.conceptEnglish.util.ShareUtils;
import com.iyuba.conceptEnglish.widget.dialog.EvaluatingStudyReportDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.view.PermissionMsgDialog;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.multithread.util.NetStatusUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * 学习界面-评测界面
 */
public class StudyEvalFragment extends BaseViewBindingFragment<FragmentEvalBinding> implements StudyEvalView{

    //数据
    private StudyEvalPresenter presenter;
    //适配器
    private StudyEvalAdapter adapter;
    //课程数据
    private Voa voaData;
    //句子数据
    private List<VoaDetail> voaDetailList;
    //当前需要使用的评测数据
    private int curUseVoaId = 0;

    //原文播放器
    private ExoPlayer audioPlayer;
    //是否加载音频完成
    private boolean isAudioPlayPrepared = false;

    //录音器
    private RecordManager recordManager;
    //是否正在录音
    private boolean isRecordAndEval = false;

    //评测播放器
    private ExoPlayer evalPlayer;


    //合成播放器
    private ExoPlayer margePlayer;
    //是否合成已经还在完成
    private boolean isMargePlayPrepared = false;
    //合成的音频播放链接
    private String margeAudioUrl = null;

    //权限弹窗
    private PermissionMsgDialog permissionDialog = null;
    //vip会员购买弹窗
    private AlertDialog vipDialog = null;
    //合成说明弹窗
    private AlertDialog margeTimeOutDialog = null;

    //合成后发布获取的分享id
    private int margeShareId = 0;

    //纠音界面
    private EvalFixDialog evalFixDialog;

    public static StudyEvalFragment getInstance() {
        StudyEvalFragment evalFragment = new StudyEvalFragment();
        return evalFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voaData = VoaDataManager.Instace().voaTemp;
        voaDetailList = VoaDataManager.getInstance().voaDetailsTemp;

        presenter = new StudyEvalPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initBottom();
        initClick();
        showData();

        initPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();

        //关闭所有的音频和录音器
        pauseAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //关闭弹窗
        if (permissionDialog!=null){
            permissionDialog.dismiss();
        }
        if (vipDialog!=null){
            vipDialog.dismiss();
        }
        if (evalFixDialog!=null){
            evalFixDialog.dismiss();
        }
        //重置数据
        resetData();
        //解除绑定
        presenter.detachView();
    }

    /**********************************初始化**********************************/
    private void initList() {
        //这里处理下voaId数据
        curUseVoaId = transVoaId(voaData.voaId);

        adapter = new StudyEvalAdapter(getActivity(), new ArrayList<>());
        adapter.setHelpData(curUseVoaId,voaData.lessonType);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnEvalItemClickListener(new StudyEvalAdapter.OnEvalItemClickListener() {
            @Override
            public void onItem(int position) {
                if (isRecordAndEval) {
                    ToastUtil.showToast(getActivity(), "正在录音评测中～");
                    return;
                }

                //关闭原音、评测、合成
                pauseAudio();
                pauseEval();
                pauseMarge();

                //刷新样式
                adapter.refreshItem(position);
            }

            @Override
            public void onPlayAudio(long startTime, long endTime) {
                if (isRecordAndEval) {
                    ToastUtil.showToast(getActivity(), "正在录音评测中～");
                    return;
                }

                if (audioPlayer == null) {
                    ToastUtil.showToast(getActivity(), "原文播放器初始化失败");
                    return;
                }

                //关闭评测和合成
                pauseEval();
                pauseMarge();

                if (audioPlayer.isPlaying()) {
                    pauseAudio();
                } else {
                    playAudio(startTime, endTime);
                }
            }

            @Override
            public void onRecordAudio(int voaId,String paraId,String lineN,String sentence,long recordTime) {
                //权限信息弹窗
                List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
                pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
                if (Build.VERSION.SDK_INT < 35){
                    pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));
                }
//                else {
//                    boolean isPermissionOk = PermissionFixUtil.isPermissionOk(getActivity(),PermissionFixUtil.concept_eval_recordAudio_code);
//                    if (isPermissionOk){
//                        checkRecordAudio(voaId, paraId, lineN, sentence, recordTime);
//                    }
//                }
                PermissionDialogUtil.getInstance().showMsgDialog(getActivity(), pairList, new PermissionDialogUtil.OnPermissionResultListener() {
                    @Override
                    public void onGranted(boolean isSuccess) {
                        if (isSuccess){
                            checkRecordAudio(voaId, paraId, lineN, sentence, recordTime);
                        }
                    }
                });
            }

            @Override
            public void onEvalAudio(String playUrl, int voaId, String lineN) {
                if (isRecordAndEval) {
                    ToastUtil.showToast(getActivity(), "正在录音评测中～");
                    return;
                }

                if (evalPlayer == null) {
                    ToastUtil.showToast(getActivity(), "评测播放器初始化失败");
                    return;
                }

                //暂停其他播放
                pauseAudio();
                pauseMarge();

                if (evalPlayer.isPlaying()) {
                    pauseEval();
                } else {
                    //判断文件是否存在
                    String localEvalPath = getLocalEvalPath(lineN);
                    File localEvalFile = new File(localEvalPath);

                    //播放地址
                    String evalPlayUrl = null;
                    if (localEvalFile.exists()) {
                        evalPlayUrl = localEvalPath;
                    } else {
                        evalPlayUrl = Constant.EVAL_PREFIX + playUrl;
                    }

                    playEval(evalPlayUrl);
                }
            }

            @Override
            public void onPublishEval(int voaId,String paraId,String lineN,String sentence,String evalAudioUrl) {
                if (isRecordAndEval) {
                    ToastUtil.showToast(getActivity(), "正在录音评测中～");
                    return;
                }

                //判断登录
                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(getActivity());
                    return;
                }

                //暂停播放
                pauseAudio();
                pauseEval();
                pauseMarge();

                //发布评测
                publishEval(voaId, paraId, lineN, sentence, evalAudioUrl);
            }

            @Override
            public void onShareSentence(int voaId,int shuoshuoId,String evalAudioUrl,int lessonIndex,String sentence) {
                if (isRecordAndEval) {
                    ToastUtil.showToast(getActivity(), "正在录音评测中～");
                    return;
                }

                //判断登录
                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(getActivity());
                    return;
                }

                //暂停播放
                pauseAudio();
                pauseEval();
                pauseMarge();

                //分享内容
                shareEval(voaId, shuoshuoId, evalAudioUrl, lessonIndex, sentence);
            }

            @Override
            public void onEvalFix(int voaId,String paraId,String lineN,String sentence,String wordScores) {
                //纠音(先将数据转换成必要的样式，然后传递)

                //这里有些奇怪，可能需要直接获取成绩，然后自行组装成需要的数据操作
                //直接从数据库获取数据会存在错误

                //1.转换成需要的单词数据
                String[] scoreArray = wordScores.split(",");
                String[] wordArray = sentence.split(" ");
                List<EvalFixBean.WordEvalFixBean> wordFixList = new ArrayList<>();
                for (int i = 0; i < scoreArray.length; i++) {
                    wordFixList.add(new EvalFixBean.WordEvalFixBean(
                            wordArray[i],
                            "",
                            Float.parseFloat(scoreArray[i]),
                            i,
                            ""
                    ));
                }

                //2.转换成需要的数据
                EvalFixBean fixBean = new EvalFixBean(
                        curUseVoaId,
                        Integer.parseInt(paraId),
                        Integer.parseInt(lineN),
                        sentence,
                        wordFixList
                );

                //开启纠音显示
                String showJsonData = GsonUtils.toJson(fixBean,EvalFixBean.class);
                evalFixDialog = EvalFixDialog.getInstance(showJsonData);
                evalFixDialog.show(getChildFragmentManager(),"EvalFixDialog");
            }
        });
    }

    private void initBottom() {
        //设置底部样式
        binding.imvCurrentTime.setText(transShowTime(0));
        binding.imvSeekbarPlayer.setMax(0);
        binding.imvSeekbarPlayer.setProgress(0);
        binding.imvTotalTime.setText(transShowTime(0));

        binding.tvReadMix.setVisibility(View.VISIBLE);
        binding.tvReadMix.setText("合成");
        binding.tvReadSore.setVisibility(View.INVISIBLE);
        binding.tvReadShare.setVisibility(View.INVISIBLE);
    }

    private void initClick(){
        binding.tvReadMix.setOnClickListener(v->{
            if (isRecordAndEval){
                ToastUtil.showToast(getActivity(),"正在录音评测中～");
                return;
            }

            //关闭播放
            pauseAudio();
            pauseEval();

            String showText = binding.tvReadMix.getText().toString();

            if (showText.equals("合成")){
                margeAudio();
            }else if (showText.equals("试听")){
                playMarge(0);
                binding.tvReadMix.setText("停止");
            }else if (showText.equals("停止")){
                pauseMarge();
                binding.tvReadMix.setText("试听");
            }
        });
        binding.tvReadShare.setOnClickListener(v->{
            if (isRecordAndEval){
                ToastUtil.showToast(getActivity(),"正在录音评测中～");
                return;
            }

            //关闭播放
            pauseAudio();
            pauseEval();
            pauseMarge();

            String shareText = binding.tvReadShare.getText().toString();
            if (shareText.equals("发布")){
                publishMarge(curUseVoaId,binding.tvReadSore.getText().toString(),margeAudioUrl);
            }else if (shareText.equals("分享")){
                //获取分数进行处理
                List<VoaSound> evalList = new VoaSoundOp(getActivity()).findDataByvoaId(curUseVoaId);
                int totalScore = 0;
                int sentenceSize = evalList.size();
                for (int i = 0; i < evalList.size(); i++) {
                    VoaSound voaSound = evalList.get(i);
                    totalScore+=voaSound.totalScore;
                }

                //处理评测链接
                String showEvalUrl = margeAudioUrl.replace(Constant.EVAL_PREFIX,"");

                String lessonName = voaData.title;
                String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + margeShareId + "&shuoshuo=" + showEvalUrl + "&apptype=" + Constant.EVAL_TYPE;
                String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
                String title = UserInfoManager.getInstance().getUserName() + "在评测中获得了" + (totalScore / sentenceSize) + "分";
                ShareUtils localShareUtils = new ShareUtils();
                localShareUtils.setMContext(getActivity());
                localShareUtils.setVoaId(curUseVoaId);
                localShareUtils.showShare(getActivity(), imageUrl, siteUrl, title, lessonName, localShareUtils.platformActionListener, new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform, Platform.ShareParams shareParams) {
                        shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    }
                });
            }
        });
        binding.imvSeekbarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }

                pauseMarge();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }

                playMarge(seekBar.getProgress());
            }
        });
    }

    //重置数据
    private void resetData(){
        //初始化所有的数据
        isAudioPlayPrepared = false;
        isRecordAndEval = false;
        isMargePlayPrepared = false;

        //关闭音频
        pauseAudio();
        pauseEval();
        pauseMarge();
        //关闭录音
        stopRecord();
    }

    private void showData() {
        //刷新数据
        if (adapter != null) {
            adapter.refreshData(voaDetailList);
        }
    }

    /************************************音频**********************************/
    private void initPlayer() {
        /***********原音**************/
        //初始化播放
        audioPlayer = new ExoPlayer.Builder(getActivity()).build();
        audioPlayer.setPlayWhenReady(false);
        audioPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //加载完成
                        isAudioPlayPrepared = true;
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseAudio();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(ConceptApplication.getContext(), "加载原文音频异常");
            }
        });

        //初始化需要播放的音频
        String playUrl = null;
        if (TextUtils.isEmpty(getLocalSoundPath())) {
            playUrl = getRemoteSoundPath();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                playUrl = String.valueOf(FileProvider.getUriForFile(getActivity(), getResources().getString(R.string.file_provider_name_personal), new File(getLocalSoundPath())));
            } else {
                playUrl = String.valueOf(Uri.fromFile(new File(getLocalSoundPath())));
            }
        }
        MediaItem mediaItem = MediaItem.fromUri(playUrl);
        audioPlayer.setMediaItem(mediaItem);
        audioPlayer.prepare();

        /***********评测**************/
        evalPlayer = new ExoPlayer.Builder(getActivity()).build();
        evalPlayer.setPlayWhenReady(false);
        evalPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //加载完成
                        playEval(null);
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseEval();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(), "加载评测音频异常");
            }
        });

        /***********合成**************/
        margePlayer = new ExoPlayer.Builder(getActivity()).build();
        margePlayer.setPlayWhenReady(false);
        margePlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        //加载完成
                        isMargePlayPrepared = true;
                        //设置进度
                        binding.imvTotalTime.setText(transShowTime(margePlayer.getDuration()/1000L));
                        binding.imvSeekbarPlayer.setMax((int) margePlayer.getDuration());
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseMarge();
                        binding.tvReadMix.setText("试听");
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(), "加载合成音频异常");
            }
        });
    }

    /********原文音频**********/
    private void playAudio(long startTime, long endTime) {
        if (!isAudioPlayPrepared) {
            ToastUtil.showToast(getActivity(), "原文音频初始化中～");
            return;
        }

        audioPlayer.seekTo(startTime);
        audioPlayer.play();

        startAudioPlayTimer(startTime, endTime);
    }

    private void pauseAudio() {
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.pause();
        }
        //关闭计时器
        stopAudioPlayTimer();
        //设置样式
        if (adapter != null) {
            adapter.refreshAudioPlay(0, 0, false);
        }
    }

    private static final String timer_audioPlay = "audioPlayTimer";

    private void startAudioPlayTimer(long startTime, long endTime) {
        LibRxTimer.getInstance().multiTimerInMain(timer_audioPlay, 0, 200L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long curPlayTime = audioPlayer.getCurrentPosition();
                long progressTime = curPlayTime - startTime;
                long totalTime = endTime - startTime;

                //刷新适配器中的显示
                adapter.refreshAudioPlay(progressTime, totalTime, true);

                if (curPlayTime >= endTime) {
                    //设置停止
                    pauseAudio();
                }
            }
        });
    }

    private void stopAudioPlayTimer() {
        LibRxTimer.getInstance().cancelTimer(timer_audioPlay);
    }

    /********录音操作**********/
    private void startRecord(int voaId,String paraId,String lineN,String sentence,long recordTime) {
        //获取文件
        String evalSavePath = getLocalEvalPath(lineN);
        File evalFile = new File(evalSavePath);
        try {
            if (evalFile.exists()){
                evalFile.delete();
            }else {
                if (!evalFile.getParentFile().exists()){
                    evalFile.getParentFile().mkdirs();
                }
                evalFile.createNewFile();
            }
        }catch (Exception e){
            ToastUtil.showToast(getActivity(),"创建音频文件失败，请重试～");
        }

        //设置标志
        isRecordAndEval = true;

        recordManager = new RecordManager(evalFile);
        recordManager.startRecord();
        //开启定时器
        startRecordTimer(voaId,paraId,lineN,sentence,evalSavePath,recordTime);
    }

    private void stopRecord() {
        if (recordManager != null) {
            recordManager.stopRecord();
        }
        //关闭计时器
        stopRecordTimer();
        //设置样式初始化
        adapter.refreshRecordVolume(0,false);
    }

    private static final String timer_record = "recordTimer";
    private void startRecordTimer(int voaId,String paraId,String lineN,String sentence,String evalPath,long recordTime){
        LibRxTimer.getInstance().multiTimerInMain(timer_record, 0, 200L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                adapter.refreshRecordVolume((int)recordManager.getVolume(),true);

                long progressTime = number*200L;
                if (progressTime>=recordTime){
                    stopRecord();
                    //发送评测
                    submitEval(voaId,paraId,lineN,sentence,evalPath);
                }
            }
        });
    }

    private void stopRecordTimer(){
        LibRxTimer.getInstance().cancelTimer(timer_record);
    }

    //整体录音判断
    private void checkRecordAudio(int voaId,String paraId,String lineN,String sentence,long recordTime){
        //判断登录
        if (!UserInfoManager.getInstance().isLogin()){
            LoginUtil.startToLogin(getActivity());
            return;
        }

        //判断是否超过
        if (isEvalLimit(voaId, paraId, lineN)){
            vipDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("会员购买")
                    .setMessage("非会员只能评测三句，会员无限制。是否开通会员使用?")
                    .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_APP);
                        }
                    }).setNegativeButton("暂不使用",null)
                    .setCancelable(false)
                    .create();
            vipDialog.show();
            return;
        }


        //关闭播放
        pauseAudio();
        pauseEval();
        pauseMarge();

        //判断播放和暂停
        if (isRecordAndEval) {
            stopRecord();
            //提交评测
            submitEval(voaId,paraId,lineN,sentence,getLocalEvalPath(lineN));
        } else {
            //判断评测
            if (isRecordAndEval) {
                ToastUtil.showToast(getActivity(), "正在录音评测中～");
                return;
            }

            //这里针对录音时间比较短的问题，增加3s的录音时长
            long evalRecordTime=recordTime+3000L;
            startRecord(voaId, paraId, lineN, sentence, evalRecordTime);
        }
    }

    /********评测音频**********/
    private void playEval(String urlOrPath) {
        if (!TextUtils.isEmpty(urlOrPath)) {
            Uri playUrl = null;

            if (urlOrPath.startsWith("http://") || urlOrPath.startsWith("https://")) {
                playUrl = Uri.parse(urlOrPath);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    playUrl = FileProvider.getUriForFile(getActivity(), getResources().getString(R.string.file_provider_name_personal), new File(urlOrPath));
                } else {
                    playUrl = Uri.fromFile(new File(urlOrPath));
                }
            }

            MediaItem mediaItem = MediaItem.fromUri(playUrl);
            evalPlayer.setMediaItem(mediaItem);
            evalPlayer.prepare();
        } else {
            evalPlayer.play();
            //开启定时器
            startEvalPlayTimer();
        }
    }

    private void pauseEval() {
        if (evalPlayer != null && evalPlayer.isPlaying()) {
            evalPlayer.pause();
        }
        //关闭定时器
        stopEvalPlayTimer();
        //初始化样式
        adapter.refreshEvalPlay(0, 0, false);
    }

    private static final String timer_evalPlay = "evalPlayTimer";

    private void startEvalPlayTimer() {
        LibRxTimer.getInstance().multiTimerInMain(timer_evalPlay, 0, 200L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long progressTime = evalPlayer.getCurrentPosition();
                long totalTime = evalPlayer.getDuration();
                //刷新样式
                adapter.refreshEvalPlay(progressTime, totalTime, true);

                if (progressTime >= totalTime) {
                    //关闭评测播放
                    pauseEval();
                }
            }
        });
    }

    private void stopEvalPlayTimer() {
        LibRxTimer.getInstance().cancelTimer(timer_evalPlay);
    }

    /************合成音频***********/
    private void prepareMargePlayer(String audioUrl){
        //合成正常的额音频链接
        margeAudioUrl = Constant.EVAL_PREFIX+audioUrl;

        MediaItem mediaItem = MediaItem.fromUri(margeAudioUrl);
        margePlayer.setMediaItem(mediaItem);
        margePlayer.prepare();
    }

    private void playMarge(int progressTime) {
        if (!isMargePlayPrepared){
            ToastUtil.showToast(getActivity(),"合成音频正在初始化～");
            return;
        }

        if (!NetStatusUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后重试");
            return;
        }

        margePlayer.seekTo(progressTime);
        margePlayer.play();
        //开启计时器
        startMargeAudioTimer();
    }

    private void pauseMarge() {
        if (margePlayer != null && margePlayer.isPlaying()) {
            margePlayer.pause();
        }
        stopMargeAudioTimer();

        //判断当前状态并显示
        String showText = binding.tvReadMix.getText().toString().trim();
        if (showText.equals("停止")){
            binding.tvReadMix.setText("试听");
        }
    }

    private static final String timer_margeAudio = "margeAudioTimer";
    private void startMargeAudioTimer() {
        LibRxTimer.getInstance().multiTimerInMain(timer_margeAudio, 0, 500L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long playTime = margePlayer.getCurrentPosition();
                binding.imvSeekbarPlayer.setProgress((int) playTime);
                binding.imvCurrentTime.setText(transShowTime(playTime/1000L));

                if (playTime>=margePlayer.getDuration()){
                    pauseMarge();
                }
            }
        });
    }

    private void stopMargeAudioTimer() {
        LibRxTimer.getInstance().cancelTimer(timer_margeAudio);
        //重置显示
        if (binding!=null){
            binding.imvCurrentTime.setText(transShowTime(0));
            binding.imvSeekbarPlayer.setProgress(0);
        }
    }

    /******************************************评测相关**************************************/
    public void submitEval(int voaId,String paraId,String lineN,String sentence,String evalPath){
        startLoading("正在提交评测数据～");

        presenter.submitEval(voaId,paraId,lineN,sentence,evalPath);
    }

    private void publishEval(int voaId,String paraId,String lineN,String sentence,String evalAudioUrl) {
        startLoading("正在提交评测数据到排行榜～");

        presenter.publishEval(voaId, paraId, lineN, sentence, evalAudioUrl);
    }

    private void shareEval(int voaId,int shuoshuoId,String evalAudioUrl,int lessonIndex,String sentence) {
        String shareUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + shuoshuoId + "&addr=" + evalAudioUrl + "&apptype=" + Constant.EVAL_TYPE;
        String userName = UserInfoManager.getInstance().getUserName();
        String title = userName+"在"+getResources().getString(R.string.app_name)+"中的第"+lessonIndex+"课的评测："+sentence;
        LoadIconUtil.loadCommonIcon(getActivity());
        String imagePath = Constant.iconAddr;
        ShareUtils localShareUtils = new ShareUtils();
        localShareUtils.setMContext(getActivity());
        localShareUtils.setVoaId(voaId);
        String lessonName = voaData.title;
        localShareUtils.showShare(getActivity(), imagePath, shareUrl, title, lessonName, localShareUtils.platformActionListener, null);
    }

    public void margeAudio(){
        //登录处理
        if (!UserInfoManager.getInstance().isLogin()){
            LoginUtil.startToLogin(getActivity());
            return;
        }

        //获取已经评测的数据进行处理
        List<VoaSound> evalList = new VoaSoundOp(getActivity()).findDataByvoaId(curUseVoaId);
        if (evalList==null||evalList.size()<=1){
            ToastUtil.showToast(getActivity(),"请至少评测两句后合成");
            return;
        }

        //获取需要的数据
        StringBuffer timeOutBuffer = new StringBuffer();

        int allScore = 0;
        List<String> audioList = new ArrayList<>();
        for (int i = 0; i < evalList.size(); i++) {
            VoaSound voaSound = evalList.get(i);

            audioList.add(voaSound.sound_url);
            allScore+=voaSound.totalScore;

            //获取超时数据
            int start=voaSound.sound_url.indexOf("/")+1;
            int end=voaSound.sound_url.indexOf("/concept");
            String result=voaSound.sound_url.substring(start,end);
            int year=Integer.parseInt(result.substring(0,4));
            int month=Integer.parseInt(result.substring(4));
            int currentYear=getCurrentDate("yyyy");
            int currentMonth=getCurrentDate("MM");
            boolean isCurMonth=(year==currentYear&&month==currentMonth);
            if (!isCurMonth){
                timeOutBuffer.append("\n第"+(i+1)+"句");
            }
        }

        if (!TextUtils.isEmpty(timeOutBuffer.toString())){
            String showMsg = "当前已经评测的句子中："+timeOutBuffer.toString()+"\n为非当前月份的内容，需要重新评测后才能合成";
            margeTimeOutDialog = new AlertDialog.Builder(getActivity())
                    .setMessage(showMsg)
                    .setPositiveButton("确定",null)
                    .create();
            margeTimeOutDialog.show();
            return;
        }

        int averageScore = allScore/evalList.size();

        startLoading("正在合成音频～");
        presenter.margeAudio(audioList,String.valueOf(averageScore));
    }

    public void publishMarge(int voaId,String averageScore,String margeAudioUrl){
        startLoading("正在发布合成音频～");

        String showEvalUrl = margeAudioUrl.replace(Constant.EVAL_PREFIX,"");

        presenter.publishMarge(voaId, averageScore, showEvalUrl);
    }

    /***********************************************其他方法****************************************/
    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String showMsg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    private void stopLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    //本地原文音频
    private String getLocalSoundPath() {
        // TODO: 2025/4/17 在android 15上不再主动请求存储权限
        if (!PermissionFixUtil.isCanUseExternalPermission(getActivity())) {
            return null;
        }

        //更换地址
        String pathString = FilePathUtil.getHomeAudioPath(curUseVoaId, voaData.lessonType);
        File file = new File(pathString);
        if (file.exists()) {
            return pathString;
        }

        return "";
    }

    //网络原文音频
    private String getRemoteSoundPath() {
        String soundUrl = null;
        //这里针对会员和非会员不要修改，测试也不要修改
        if (UserInfoManager.getInstance().isVip()) {
            soundUrl = "http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
        } else {
            soundUrl = Constant.sound;
        }

        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            default:
                //美音
                soundUrl = soundUrl
                        + voaData.voaId / 1000
                        + "_"
                        + voaData.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptFourUK: //英音
                soundUrl = soundUrl
                        + "british/"
                        + voaData.voaId / 1000
                        + "/"
                        + voaData.voaId / 1000
                        + "_"
                        + voaData.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptJunior:
                soundUrl = "http://" + Constant.staticStr + Constant.IYUBA_CN + "sounds/voa/sentence/202005/"
                        + voaData.voaId
                        + "/"
                        + voaData.voaId
                        + Constant.append;
                break;
        }

        return soundUrl;
    }

    //本地评测音频
    public String getLocalEvalPath(String lineN) {
        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
                return Constant.getsimRecordAddr(getActivity()) + "/" + voaData.voaId + lineN + ".mp3";
            case TypeLibrary.BookType.conceptFourUK:
                return Constant.getsimRecordAddr(getActivity()) + "/" + (voaData.voaId * 10) + lineN + ".mp3";
            case TypeLibrary.BookType.conceptJunior:
                return Constant.getsimRecordAddr(getActivity()) + "/" + voaData.voaId + lineN + ".mp3";
            default:
                return Constant.getsimRecordAddr(getActivity()) + "/" + voaData.voaId + lineN + ".mp3";
        }
    }

    //刷新数据
    public void refreshData() {
        showData();
        initBottom();

        resetData();
    }

    //将voaId数据转换为标准的数据
    private int transVoaId(int voaId){
        switch (VoaDataManager.getInstance().voaTemp.lessonType){
            case TypeLibrary.BookType.conceptFourUS:
                return voaId;
            case TypeLibrary.BookType.conceptFourUK:
                return voaId*10;
            case TypeLibrary.BookType.conceptJunior:
                return voaId;
            default:
                return voaId;
        }
    }

    //判断是否评测限制
    private boolean isEvalLimit(int voaId,String paraId,String lineN){
        boolean isVip = UserInfoManager.getInstance().isVip();
        List<VoaSound> evalList = new VoaSoundOp(getActivity()).findDataByvoaId(voaId);
        boolean isThan3 = evalList.size()>=3;
        boolean isHasEval = false;

        int itemId = Integer.parseInt(voaId+""+paraId+""+lineN);
        for (int i = 0; i < evalList.size(); i++) {
            VoaSound tempdata = evalList.get(i);
            if (tempdata.itemId == itemId){
                isHasEval = true;
            }
        }

        //判断是否可以评测
        return !isVip && isThan3 && !isHasEval;
    }

    //格式化时间
    private String transShowTime(long showTime){
        long showSecTime = showTime%60;
        long showMinuteTime = showTime/60;

        StringBuffer showBuffer = new StringBuffer();
        if (showMinuteTime>=10){
            showBuffer.append(String.valueOf(showMinuteTime));
        }else {
            showBuffer.append("0"+showMinuteTime);
        }

        showBuffer.append(":");

        if (showSecTime>=10){
            showBuffer.append(String.valueOf(showSecTime));
        }else {
            showBuffer.append("0"+showSecTime);
        }

        return showBuffer.toString();
    }

    //获取当前日期
    private int getCurrentDate(String type){
        SimpleDateFormat format = new SimpleDateFormat(type, Locale.CHINA);
        String time=format.format(new Date(System.currentTimeMillis()));
        return Integer.parseInt(time);
    }

    /*******************************接口回调****************************/
    @Override
    public void showEvalResult(Concept_eval_result sentenceData, String evalPath,String showMsg) {
        stopLoading();
        //设置标志
        isRecordAndEval = false;

        if (sentenceData == null){
            ToastUtil.showToast(getActivity(),showMsg);
            return;
        }

        //刷新数据并保存在本地
        adapter.refreshEvalResult(sentenceData,evalPath);
        //重置合成
        binding.tvReadMix.setText("合成");
        binding.tvReadSore.setVisibility(View.INVISIBLE);
        binding.tvReadShare.setVisibility(View.INVISIBLE);
        binding.imvSeekbarPlayer.setProgress(0);
        binding.imvCurrentTime.setText(transShowTime(0));
        isMargePlayPrepared = false;

        // TODO: 2025/4/2 刷新练习题界面的评测数据
        EventBus.getDefault().post(new PractiseLineEvent(PractiseLineEvent.event_eval));
    }

    @Override
    public void showPublishEvalResult(boolean isSuccess, String showMsg,int shuoshuoId) {
        stopLoading();

        if (!isSuccess){
            ToastUtil.showToast(getActivity(),showMsg);
            return;
        }

        ToastUtil.showToast(getActivity(),"发布评测成功，请到排行榜界面查看");
        //刷新界面显示
        adapter.refreshPublishShareData(shuoshuoId);
        //刷新排行榜
        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.eval_rank));
    }

    @Override
    public void showMargeResult(boolean isSuccess, String showMsg, String score,String margeAudioUrl) {
        stopLoading();

        if (!isSuccess){
            ToastUtil.showToast(getActivity(),showMsg);
            return;
        }

        //初始化音频
        prepareMargePlayer(margeAudioUrl);
        //设置按钮
        binding.tvReadMix.setText("试听");
        binding.tvReadSore.setText(score);
        binding.tvReadSore.setVisibility(View.VISIBLE);
        binding.tvReadShare.setVisibility(View.VISIBLE);
        binding.tvReadShare.setText("发布");

        //显示弹窗
        if (ConfigManager.Instance().getsendEvaReport() && UserInfoManager.getInstance().isLogin()) {
            //获取评测数据
            List<VoaSound> evalList = new VoaSoundOp(getActivity()).findDataByvoaId(curUseVoaId);
            if (evalList!=null && evalList.size()>0){

                //将要使用的评测数据
                Map<Integer,VoaSound> evalMap = new HashMap<>();
                //需要使用的数据
                int sentenceSize = evalList.size();
                int totalScore = 0;
                for (int i = 0; i < evalList.size(); i++) {
                    VoaSound voaSound = evalList.get(i);
                    totalScore+=voaSound.totalScore;

                    evalMap.put(voaSound.itemId,voaSound);
                }


                //将单词和评测数据合并到详情中(感觉没啥用啊，暂时先这样吧)
                for (int i = 0; i < voaDetailList.size(); i++) {
                    VoaDetail voaDetail = voaDetailList.get(i);
                    int curItemId = Integer.parseInt(voaDetail.voaId+""+voaDetail.paraId+""+voaDetail.lineN);

                    //当前的评测内容
                    VoaSound curVoaSound = evalMap.get(curItemId);
                    if (curVoaSound!=null){
                        //需要合并的数据
                        List<EvaluateBean.WordsBean> submitWordList = new ArrayList<>();

                        //单词数据
                        CorrectEvalHelper evalHelper = new CorrectEvalHelper(getActivity());
                        List<EvaluationSentenceDataItem> wordItemList = evalHelper.findByVoaAndLineN(String.valueOf(UserInfoManager.getInstance().getUserId()),String.valueOf(voaDetail.voaId),voaDetail.lineN);
                        if (wordItemList!=null&&wordItemList.size()>0){
                            for (int j = 0; j < wordItemList.size(); j++) {
                                EvaluationSentenceDataItem dataItem = wordItemList.get(j);

                                EvaluateBean.WordsBean wordsBean = new EvaluateBean.WordsBean();
                                wordsBean.setContent(dataItem.getContent());
                                wordsBean.setIndex(dataItem.getIndex());
                                wordsBean.setScore(dataItem.getScore());

                                submitWordList.add(wordsBean);
                            }
                        }

                        //合并数据
                        EvaluateBean evaluateBean = new EvaluateBean(voaDetail.sentence,String.valueOf(curVoaSound.totalScore),submitWordList.size(),curVoaSound.sound_url,submitWordList);
                        voaDetailList.get(i).setEvaluateBean(evaluateBean);
                        voaDetailList.get(i).setReadScore(curVoaSound.totalScore);
                    }
                }
                //显示弹窗
                EvaluatingStudyReportDialog.getInstance()
                        .init(getActivity())
                        .setData(voaDetailList, totalScore / sentenceSize, sentenceSize)
                        .prepare()
                        .show();
            }
        }
    }

    @Override
    public void showPublishMargeResult(boolean isSuccess, String showMsg, int shuoshuoId,String rewardPrice) {
        stopLoading();

        if (!isSuccess){
            ToastUtil.showToast(getActivity(),showMsg);
            return;
        }


        ToastUtil.showToast(getActivity(),"发布合成音频成功，请到排行榜界面查看");
        //设置分享id
        margeShareId = shuoshuoId;
        //刷新数据
        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.eval_rank));
        //显示奖励
        double price = Integer.parseInt(rewardPrice)*0.01;
        if (price>0){
            price = BigDecimalUtil.trans2Double(price);
            String rewardMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show),price);
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_dialog,rewardMsg));
        }
        //更换显示
        binding.tvReadShare.setText("分享");
    }
}
