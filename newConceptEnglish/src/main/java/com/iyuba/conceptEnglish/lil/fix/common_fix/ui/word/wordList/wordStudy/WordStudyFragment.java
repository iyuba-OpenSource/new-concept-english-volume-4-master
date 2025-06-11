package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordStudy;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.FragmentWordStudyBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalShowBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FileManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ImageUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.RecordManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.MultiButtonDialog;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.SingleButtonDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.xxpermission.PermissionBackListener;
import com.iyuba.conceptEnglish.manager.sharedpreferences.InfoHelper;
import com.iyuba.conceptEnglish.protocol.WordUpdateRequest;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.sqlite.op.WordOp;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.io.File;
import java.util.List;

/**
 * @title:
 * @date: 2023/6/9 18:17
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordStudyFragment extends BaseViewBindingFragment<FragmentWordStudyBinding> implements WordStudyView{

    private static final String TIP1 = "多加练习，口语大咖在向你招手！";
    private static final String TIP2 = "勤能补拙，每天都来练一练！"; //60以下
    private static final String TIP3 = "再接再厉，一定会有提升！";
    private static final String TIP4 = "多读多练，你还可以读的更好！";//60-75
    private static final String TIP5 = "表现不错，继续努力冲高分！";
    private static final String TIP6 = "标准流利，为你点赞！";//75-90
    private static final String TIP7 = "十分优秀，口语大咖！"; //90以上

    private String bookType;
    private String bookId;
    private String id;

    //单词数据
    private List<WordBean> wordList;
    //当前位置
    private int selectIndex = 0;
    //当前的数据
    private WordBean selectWordBean;
    //当前的评测数据
    private EvalShowBean selectEvalBean;
    //音频
    private ExoPlayer audioPlayer;
    //播放的音频
    private String playAudioUrl = null;
    //音频准备状态-默认可以播放，加载过程中不可以播放
    private boolean isCanPlay = true;
    //视频
    private ExoPlayer videoPlayer;
    //录音
    private RecordManager recordManager;
    //是否正在录音
    private boolean isRecord = false;
    //录音计时计时器标识位
    private String recordTimeTimerTag = "recordTimeTimerTag";
    //录音文件保存路径
    private String recordFilePath = null;//这里可以考虑播放的时候直接使用文件路径播放（这里暂时没有加入）
    //加载弹窗
    private LoadingDialog loadingDialog;

    private WordStudyPresenter presenter;

    //动画样式
    private AnimationDrawable playAnim,recordAnim;

    public static WordStudyFragment getInstance(String types,String bookId,String id, int position) {
        WordStudyFragment fragment = new WordStudyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.bookId, bookId);
        bundle.putString(StrLibrary.id, id);
        bundle.putInt(StrLibrary.position, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookType = getArguments().getString(StrLibrary.types);
        bookId = getArguments().getString(StrLibrary.bookId);
        id = getArguments().getString(StrLibrary.id);
        selectIndex = getArguments().getInt(StrLibrary.position,0);

        presenter = new WordStudyPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initPlayer();
        initClick();

        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopRecord();
        pauseAudio();
        stopVideo(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();
    }

    /************************初始化数据************************/
    private void initToolbar() {
        binding.btnBack.setVisibility(View.VISIBLE);
        binding.btnBack.setImageResource(R.drawable.back_button);
        binding.btnBack.setOnClickListener(v -> {
            presenter.detachView();

            pauseAudio();
            stopVideo(true);
            stopRecord();

            StackUtil.getInstance().finishCur();
        });
        binding.tvTitle.setText("闯关单词");
    }

    private void initPlayer(){
        selectIndex = getArguments().getInt(StrLibrary.position, 0);
        String id = getArguments().getString(StrLibrary.id);
        wordList = presenter.getWordData(bookType,bookId,id);
        selectWordBean = wordList.get(selectIndex);
        binding.change.setSelected(true);

        //初始化音频
        audioPlayer = new ExoPlayer.Builder(getActivity()).build();
        audioPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                isCanPlay = true;
                ToastUtil.showToast(getActivity(), "播放音频失败～");
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //准备完成
                        isCanPlay = true;
                        audioPlayer.play();
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseAudio();
                        break;
                }
            }
        });

        //初始化视频
        videoPlayer = new ExoPlayer.Builder(getActivity()).build();
        binding.video.setPlayer(videoPlayer);
        binding.video.setUseController(false);
        videoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                showVideoLayout();
                ToastUtil.showToast(getActivity(), "播放视频失败～");
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_ENDED:
                        //播放完成
                        showVideoLayout();
                        break;
                    case Player.STATE_READY:
                        //准备完成
                        binding.videoProgress.setVisibility(View.GONE);
                        binding.videoPic.setVisibility(View.GONE);
                        binding.videoPlace.setVisibility(View.GONE);
                        binding.video.setVisibility(View.VISIBLE);

                        videoPlayer.play();
                        break;
                }
            }
        });
    }

    private void initClick(){
        //上一个
        binding.pagePre.setOnClickListener(v -> {
            int index = selectIndex - 1;
            if (index < 0) {
                ToastUtil.showToast(getActivity(), "当前已经是第一页～");
                return;
            }

            selectIndex = index;
            binding.change.setSelected(true);
            refreshData();
        });
        //下一个
        binding.pageNext.setOnClickListener(v -> {
            int index = selectIndex + 1;
            if (index > wordList.size() - 1) {
                ToastUtil.showToast(getActivity(), "当前已经是最后一页～");
                return;
            }

            selectIndex = index;
            binding.change.setSelected(true);
            refreshData();
        });
        //单词播放
        binding.audioWord.setOnClickListener(v -> {
            if (selectWordBean == null) {
                ToastUtil.showToast(getActivity(), "未查询到该单词音频");
                return;
            }

            setAudioPlay(selectWordBean.getWordAudioUrl());
        });
        //切换类型
        binding.change.setOnClickListener(v -> {
            binding.change.setSelected(!binding.change.isSelected());

            //句子显示
            refreshEvalData();
        });
        //视频播放
        binding.videoPlay.setOnClickListener(v -> {
            setVideo(selectWordBean.getVideoUrl());
        });
        //音频播放
        binding.play.setOnClickListener(v -> {
            if (selectWordBean == null) {
                ToastUtil.showToast(getActivity(), "未查询到该单词音频");
                return;
            }

            if (binding.change.isSelected()) {
                setAudioPlay(selectWordBean.getSentenceAudioUrl());
            } else {
                setAudioPlay(selectWordBean.getWordAudioUrl());
            }
        });
        //评测音频播放
        binding.eval.setOnClickListener(v -> {
            if (selectEvalBean == null) {
                ToastUtil.showToast(getActivity(), "未查询到该单词评测内容");
                return;
            }

            setAudioPlay(selectEvalBean.getEvalUrl());
        });
        //录音评测
        binding.record.setOnClickListener(v -> {
            setRecord();
        });
        //单词收藏
        binding.collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectWord();
            }
        });
        //自动播放
        binding.btnAuto.setOnClickListener(v->{
            boolean isAuto = InfoHelper.getInstance().getWordIsAuto();
            if (isAuto){
                InfoHelper.getInstance().putWordIsAuto(false);
                com.iyuba.core.common.util.ToastUtil.showToast(getActivity(), "关闭单词自动发音");
                binding.btnAuto.setImageResource(R.drawable.ic_auto_false);
            }else {
                InfoHelper.getInstance().putWordIsAuto(true);
                com.iyuba.core.common.util.ToastUtil.showToast(getActivity(), "开启单词自动发音");
                binding.btnAuto.setImageResource(R.drawable.ic_auto);
            }
        });
    }

    private void initTips(int score){
        String[] score60 = new String[]{TIP1, TIP2};
        String[] score75 = new String[]{TIP3, TIP4};
        String[] score90 = new String[]{TIP5, TIP6};
        int index = ((int) (Math.random() * 10)) % 2;

        if (score < 60) {
            binding.tvTip.setText(score60[index]);
        } else if (score < 75) {
            binding.tvTip.setText(score75[index]);
        } else if (score < 90) {
            binding.tvTip.setText(score90[index]);
        } else {
            binding.tvTip.setText(TIP7);
        }
    }

    /**************************刷新数据*****************************/
    private void refreshData(){
        //暂停播放
        pauseAudio();
        stopVideo(false);
        stopRecord();

        //刷新数据
        if (selectIndex <= 0) {
            binding.pagePre.setVisibility(View.INVISIBLE);
        } else {
            binding.pagePre.setVisibility(View.VISIBLE);
        }

        if (selectIndex >= wordList.size() - 1) {
            binding.pageNext.setVisibility(View.INVISIBLE);
        } else {
            binding.pageNext.setVisibility(View.VISIBLE);
        }

        selectWordBean = wordList.get(selectIndex);
        //刷新单词数据
        binding.pageIndex.setText((selectIndex + 1)+"/"+wordList.size());
        binding.word.setText(selectWordBean.getWord());
        String pron = selectWordBean.getPron();
        if (!TextUtils.isEmpty(selectWordBean.getPron())){
            pron = "["+selectWordBean.getPron()+"]";
        }
        binding.pron.setText(pron);
        binding.def.setText(selectWordBean.getDef());
        if (isCollect(selectWordBean.getWord())){
            binding.collect.setChecked(true);
        }else {
            binding.collect.setChecked(false);
        }

        //刷新视频数据
        showVideoLayout();

        //刷新评测的数据
        refreshEvalData();

        //刷新自动播放
        boolean isAutoPlay = InfoHelper.getInstance().getWordIsAuto();
        if (isAutoPlay){
            binding.btnAuto.setImageResource(R.drawable.ic_auto);
            setAudioPlay(selectWordBean.getWordAudioUrl());
        }else {
            binding.btnAuto.setImageResource(R.drawable.ic_auto_false);
        }
    }

    //刷新评测的数据
    private void refreshEvalData(){
        //刷新数据库的数据
        if (binding.change.isSelected()) {
            binding.sentence.setText(selectWordBean.getSentence().replace("\n", ""));
            binding.sentenceCn.setText(selectWordBean.getSentenceCn().replace("\n", ""));
            selectEvalBean = presenter.getEvalData(bookType, bookId, selectWordBean.getVoaId(), selectWordBean.getPosition(), selectWordBean.getSentence());
        } else {
            binding.sentence.setText(selectWordBean.getWord().replace("\n", ""));
            binding.sentenceCn.setText(selectWordBean.getDef().replace("\n", ""));
            selectEvalBean = presenter.getEvalData(bookType, bookId, selectWordBean.getVoaId(), selectWordBean.getPosition(), selectWordBean.getWord());
        }

        binding.llMiddle.setVisibility(View.INVISIBLE);
        binding.evalLayout.setVisibility(View.INVISIBLE);

        if (selectEvalBean != null) {
            binding.evalLayout.setVisibility(View.VISIBLE);
            binding.llMiddle.setVisibility(View.VISIBLE);
            int totalScore = (int) (Double.parseDouble(selectEvalBean.getTotalScore()) * 20);
            binding.score.setText(String.valueOf(totalScore));

            initTips(totalScore);
        }
    }

    /**************************回调数据****************************/
    @Override
    public void showEvalData(EvalShowBean evalBean) {
        closeLoading();

        if (evalBean != null) {
            refreshData();
        } else {
            ToastUtil.showToast(getActivity(), "评测失败，请重试～");
        }
    }

    /***************音频****************/
    //设置音频
    private void setAudioPlay(String audioUrl) {
        stopVideo(true);

        if (isRecord){
            ToastUtil.showToast(getActivity(), "正在录音中~");
            return;
        }

        if (!isCanPlay){
            ToastUtil.showToast(getActivity(),"正在加载音频文件，请稍等～");
            return;
        }

        if (audioPlayer == null) {
            startAudio(audioUrl);
            return;
        }

        if (!audioPlayer.isPlaying()) {
            startAudio(audioUrl);
            return;
        }


        if (audioPlayer.isPlaying()&&playAudioUrl.equals(audioUrl)) {
            pauseAudio();
        } else {
            startAudio(audioUrl);
        }
    }

    //播放音频
    private void startAudio(String audioUrl) {
        playAnim = (AnimationDrawable) binding.play.getDrawable();
        playAnim.start();

        playAudioUrl = audioUrl;

        if (audioPlayer == null) {
            audioPlayer = new ExoPlayer.Builder(getActivity()).build();
        }

        isCanPlay = false;
        pauseAudio();
        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
        audioPlayer.setMediaItem(mediaItem);
        audioPlayer.prepare();
    }

    //暂停音频
    private void pauseAudio() {
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.pause();
        }

        if (playAnim!=null){
            playAnim.selectDrawable(0);
            playAnim.stop();
        }
    }

    /****************视频**************/
    //设置视频
    private void setVideo(String videoUrl) {
        if (selectWordBean == null) {
            ToastUtil.showToast(getActivity(), "未查询到该单词的例句视频");
            return;
        }

        pauseAudio();

        if (isRecord){
            ToastUtil.showToast(getActivity(), "正在录音中~");
            return;
        }

        if (videoPlayer == null) {
            playVideo(videoUrl);
            return;
        }

        if (!videoPlayer.isPlaying()) {
            playVideo(videoUrl);
            return;
        }


        stopVideo(true);
    }

    //播放视频
    private void playVideo(String videoUrl) {
        binding.videoPlay.setVisibility(View.INVISIBLE);
        binding.videoProgress.setVisibility(View.VISIBLE);

        if (videoPlayer == null) {
            videoPlayer = new ExoPlayer.Builder(getActivity()).build();
            binding.video.setPlayer(videoPlayer);
        }

        stopVideo(false);
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        videoPlayer.setMediaItem(mediaItem);
        videoPlayer.prepare();
    }

    //停止视频
    private void stopVideo(boolean showLayout) {
        if (videoPlayer != null && videoPlayer.isPlaying()) {
            videoPlayer.stop();
        }

        if (showLayout) {
            showVideoLayout();
        }
    }

    //处理视频相关样式
    private void showVideoLayout() {
        binding.video.setVisibility(View.GONE);
        binding.videoPlace.setVisibility(View.GONE);
        binding.videoProgress.setVisibility(View.GONE);

        if (TextUtils.isEmpty(selectWordBean.getVideoUrl())) {
            binding.videoPlay.setVisibility(View.INVISIBLE);

            if (TextUtils.isEmpty(selectWordBean.getVideoPic())) {
                binding.videoPic.setVisibility(View.GONE);
                binding.videoPlace.setVisibility(View.VISIBLE);
                binding.videoPlace.setText(selectWordBean.getSentence());
            } else {
                binding.videoPlace.setVisibility(View.GONE);
                binding.videoPic.setVisibility(View.VISIBLE);
                ImageUtil.loadRoundImg(selectWordBean.getVideoPic(), 0, binding.videoPic);
            }

        } else {
            binding.videoPlay.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(selectWordBean.getVideoPic())) {
                binding.videoPic.setVisibility(View.GONE);
            } else {
                binding.videoPic.setVisibility(View.VISIBLE);
                ImageUtil.loadRoundImg(selectWordBean.getVideoPic(), 0, binding.videoPic);
            }
        }
    }

    /*****************录音***************/
    //设置录音
    private void setRecord() {
        pauseAudio();
        stopVideo(true);

        if (selectWordBean == null) {
            ToastUtil.showToast(getActivity(), "未查询到该单词的信息，无法录音评测~");
            return;
        }

        PermissionUtil.requestRecordAudio(getActivity(), new PermissionBackListener() {
            @Override
            public void allGranted() {
                if (isRecord) {
                    //停止录音
                    stopRecord();
                    //开启评测
                    sendEval();
                } else {
                    startRecord();
                }
            }

            @Override
            public void allDenied() {
                SingleButtonDialog dialog = new SingleButtonDialog(getActivity());
                dialog.create();
                dialog.setTitle("授权说明");
                dialog.setMsg("此功能需要该权限才能正常运行，请授权后使用～");
                dialog.setButton("确定", null);
                dialog.show();
            }

            @Override
            public void halfPart(List<String> grantedList, List<String> deniedList) {
                SingleButtonDialog dialog = new SingleButtonDialog(getActivity());
                dialog.create();
                dialog.setTitle("授权说明");
                dialog.setMsg("此功能需要该权限才能正常运行，请授权后使用～");
                dialog.setButton("确定", null);
                dialog.show();
            }

            @Override
            public void warnRequest() {
                MultiButtonDialog dialog = new MultiButtonDialog(getActivity());
                dialog.create();
                dialog.setTitle("授权说明");
                dialog.setMsg("此功能需要该权限才能正常运行，请手动授权后使用～");
                dialog.setButton("取消使用", "手动授权", new MultiButtonDialog.OnMultiClickListener() {
                    @Override
                    public void onAgree() {
                        PermissionUtil.jumpToSetting(getActivity());
                    }

                    @Override
                    public void onDisagree() {

                    }
                });
                dialog.show();
            }
        });
    }

    //开始录音
    private void startRecord() {
        if (binding.change.isSelected()) {
            recordFilePath = FileManager.getInstance().getWordSentenceEvalAudioPath(bookType, selectWordBean.getVoaId(), selectWordBean.getSentence(), UserInfoManager.getInstance().getUserId());
        } else {
            recordFilePath = FileManager.getInstance().getWordEvalAudioPath(bookType, selectWordBean.getVoaId(), selectWordBean.getWord(), UserInfoManager.getInstance().getUserId());
        }

        boolean isCreate = FileManager.getInstance().createEmptyFile(recordFilePath);
        if (!isCreate) {
            ToastUtil.showToast(getActivity(), "加载文件失败，请重试～");
            return;
        }

        isRecord = true;
        recordManager = new RecordManager(new File(recordFilePath));
        recordManager.startRecord();

        //动画
        recordAnim = (AnimationDrawable) binding.record.getDrawable();
        recordAnim.start();
        binding.recordText.setText("正在录音中~");

        //这里加载音频信息，获取音频时间
        long playTime = getAudioPlayTime() + 3000L;
        RxTimer.getInstance().multiTimerInMain(recordTimeTimerTag, 200, 200L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long recordTime = number*200L;

                if (recordTime>=playTime){
                    //停止录音
                    stopRecord();
                    //发送评测
                    sendEval();
                }
            }
        });
    }

    //结束录音
    private void stopRecord() {
        if (isRecord && recordManager != null) {
            recordManager.stopRecord();
        }

        if (recordAnim!=null){
            recordAnim.selectDrawable(0);
            recordAnim.stop();
        }

        isRecord = false;
        RxTimer.getInstance().cancelTimer(recordTimeTimerTag);
        binding.recordText.setText("轻点开始录音");
    }

    //获取音频的时长
    private long getAudioPlayTime() {
        String audio = null;
        if (binding.change.isSelected()) {
            audio = selectWordBean.getSentenceAudioUrl();
        } else {
            audio = selectWordBean.getWordAudioUrl();
        }

        try {
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(getActivity(), Uri.parse(audio));
            player.prepare();
            return player.getDuration();
        } catch (Exception e) {
            return 0;
        }
    }

    /******************评测*******************/
    //发送评测
    private void sendEval(){
        startLoading("正在提交评测～");
        String sentence = selectWordBean.getSentence();
        if (!binding.change.isSelected()){
            sentence = selectWordBean.getWord();
        }
        presenter.evalWordOrSentence(binding.change.isSelected(),bookType, selectWordBean.getBookId(),selectWordBean.getVoaId(),selectWordBean.getId(),selectWordBean.getPosition(),sentence,recordFilePath);
    }

    /**********************其他操作*********************/
    //开启加载
    private void startLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    //关闭加载
    private void closeLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /*******************************单词收藏***********************/
    private boolean isCollect(String wordStr) {
        wordStr = RemoteTransUtil.transWordToEntityData(wordStr);

        com.iyuba.core.common.sqlite.mode.Word word = new WordOp(getActivity()).findDataByName(wordStr, String.valueOf(UserInfoManager.getInstance().getUserId()));
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

    //收藏或者取消收藏
    private void collectWord(){
        if (isCollect(selectWordBean.getWord())) {
            ClientSession.Instace().asynGetResponse(
                    new com.iyuba.core.common.protocol.news.WordUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
                            com.iyuba.core.common.protocol.news.WordUpdateRequest.MODE_DELETE,
                            selectWordBean.getWord()), (response, request, rspCookie) -> {
                        getActivity().runOnUiThread(() -> com.iyuba.core.common.util.ToastUtil.showToast(getActivity(), "取消收藏成功"));

                        String wordStr = RemoteTransUtil.transWordToEntityData(selectWordBean.getWord());
                        new WordOp(getActivity()).deleteItemWord(String.valueOf(UserInfoManager.getInstance().getUserId()), wordStr);
                    }
            );
        } else {
            Word newWord = new Word();
            newWord.audioUrl = selectWordBean.getWordAudioUrl();
            newWord.def = selectWordBean.getDef();
            newWord.userid = String.valueOf(UserInfoManager.getInstance().getUserId());
            newWord.pron = selectWordBean.getPron();
            newWord.key = DBTransUtil.transWordToShowData(selectWordBean.getWord());
            saveNewWords(newWord);
        }
    }

    private void saveNewWords(Word wordTemp) {
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(getActivity());
        } else {
            try {
                new WordOp(getActivity()).saveData(wordTemp);
                CustomToast.showToast(getActivity(), R.string.play_ins_new_word_success, 1000);
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
}
