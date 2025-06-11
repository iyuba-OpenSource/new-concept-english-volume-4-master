package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.AtySearchNewListBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.bean.SearchSentenceBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.data.NewSearchPresenter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.data.NewSearchView;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.RecordManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.util.LoadIconUtil;
import com.iyuba.conceptEnglish.util.ShareUtils;
import com.iyuba.configation.Constant;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.view.PermissionMsgDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: 新搜索-句子界面
 * @date: 2023/11/18 11:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewSearchSentenceListActivity extends BaseViewBindingActivity<AtySearchNewListBinding> implements NewSearchView {

    private NewSearchPresenter searchPresenter;
    private SearchSentenceAdapter sentenceAdapter;
    //播放器
    private ExoPlayer exoPlayer;
    //是否可以播放
    private boolean isCanPlay = true;
    //当前播放的链接
    private String curPlayUrl = "";
    //录音器
    private RecordManager recordManager;
    //是否正在录音
    private boolean isRecord = false;

    //原文播放tag
    private static final String playTag = "playTag";
    //录音时间
    private static final String recordTimeTag = "recordTimeTag";
    //录音动画
    private static final String recordAnimTag = "recordAnimTag";
    //评测播放
    private static final String evalTag = "evalTag";


    public static void start(Context context, String wordStr){
        Intent intent = new Intent();
        intent.setClass(context,NewSearchSentenceListActivity.class);
        intent.putExtra(StrLibrary.word,wordStr);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchPresenter = new NewSearchPresenter();
        searchPresenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();
        initPlayer();

        checkData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isRecord){
            stopRecord();
        }
        stopAudioToNext();
        searchPresenter.detachView();

        if (msgDialog!=null){
            msgDialog.dismiss();
        }
    }

    /******************************初始化*********************/
    private void initToolbar(){
        binding.title.setText("精彩句子");
        binding.back.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
    }

    private void initList(){
        sentenceAdapter = new SearchSentenceAdapter(this,new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(sentenceAdapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        sentenceAdapter.setOnSentenceItemListener(new SearchSentenceAdapter.OnSentenceItemListener() {
            @Override
            public void onSwitchItem(int position) {
                //停止
                stopAudioToNext();

                if (isRecord){
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"正在录音评测中...");
                    return;
                }

                //刷新选中
                sentenceAdapter.refreshIndex(position);
            }

            @Override
            public void onAudioPlay(String audioUrl, long startTime, long endTime) {
                //先判断是否在录音
                if (isRecord){
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"正在录音评测中...");
                    return;
                }

                if (!curPlayUrl.equals(audioUrl)){
                    stopAudioToNext();
                }

                //判断是否正在
                if (exoPlayer!=null){
                    if (exoPlayer.isPlaying()){
                        pauseAudio();

                        //停止刷新
                        RxTimer.getInstance().cancelTimer(playTag);
                        sentenceAdapter.stopPlay();
                    }else {
                        playAudio(audioUrl);
                        exoPlayer.seekTo(startTime);

                        //开始刷新动画
                        RxTimer.getInstance().multiTimerInMain(playTag, 0, 200L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                sentenceAdapter.refreshPlay(exoPlayer);

                                if (exoPlayer.getCurrentPosition()>=endTime){
                                    RxTimer.getInstance().cancelTimer(playTag);
                                    pauseAudio();
                                    sentenceAdapter.stopPlay();
                                }
                            }
                        });
                    }
                }else {
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"音频播放器未初始化");
                }
            }

            @Override
            public void onRecordEval(int selectPosition,String savePath,long totalTime) {
                //停止播放
                stopAudioToNext();
                //判断是否在录音
                if (selectPosition!=sentenceAdapter.getSelectIndex()&&isRecord){
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"正在录音评测中...");
                    return;
                }

                if (isRecord){
                    stopRecord();
                    submitEval(sentenceAdapter.getSelectData(),savePath);
                }else {
                    showPermissionDialog(savePath, totalTime);
                }
            }

            @Override
            public void onEvalPlay(String audioUrl) {
                //判断是否在录音
                if (isRecord){
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"正在录音评测中...");
                    return;
                }

                //是否播放
                if (!curPlayUrl.equals(audioUrl)){
                    stopAudioToNext();
                }

                if (exoPlayer!=null){
                    if (exoPlayer.isPlaying()){
                        pauseAudio();
                        sentenceAdapter.stopEvalPlay();
                        RxTimer.getInstance().cancelTimer(evalTag);
                    }else {
                        playAudio(audioUrl);
                        RxTimer.getInstance().multiTimerInMain(evalTag, 0, 200L, new RxTimer.RxActionListener() {
                            @Override
                            public void onAction(long number) {
                                sentenceAdapter.startEvalPlay(exoPlayer.getDuration(),exoPlayer.getCurrentPosition());
                            }
                        });
                    }
                }else {
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"音频播放器未初始化");
                }
            }

            @Override
            public void onPublish(int voaId, String paraId, String idIndex, int score, String evalAudioUrl) {
                //判断是否在录音
                if (isRecord){
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"正在录音评测中...");
                    return;
                }

                stopAudioToNext();

                //提交发布
                if (TextUtils.isEmpty(evalAudioUrl)){
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"未获取到评测数据");
                    return;
                }

                showLoading("正在发布到排行榜～");
                searchPresenter.publishEvalSentence(voaId,paraId,idIndex,String.valueOf(score),evalAudioUrl);
            }

            @Override
            public void onShare(int voaId, String sentence, String evalAudioUrl, String shareId) {
                //判断是否在录音
                if (isRecord){
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"正在录音评测中...");
                    return;
                }

                stopAudioToNext();

                if (TextUtils.isEmpty(shareId)){
                    ToastUtil.showToast(NewSearchSentenceListActivity.this,"未找到分享id");
                }else {
                    shareApp(voaId, sentence, evalAudioUrl, shareId);
                }
            }
        });
    }

    /*****************************刷新数据*******************/
    private void checkData(){
        String wordStr = getIntent().getStringExtra(StrLibrary.word);
        if (TextUtils.isEmpty(wordStr)){
            updateUi(false,"未获取相关的单词数据");
            return;
        }

        //从数据库查询数据并展示
        List<SearchSentenceBean> sentenceList = new VoaDetailOp(this).findAllDataByKey(wordStr);
        if (sentenceList!=null&&sentenceList.size()>0){
            updateUi(false,null);
            sentenceAdapter.refreshData(sentenceList);
        }else {
            updateUi(false,"未查询到相关句子数据");
        }
    }

    /*********************************回调数据***************************/
    @Override
    public void showWord(String failMsg, Word_detail detail) {
        //这里无用
    }

    @Override
    public void showCollectResult(boolean isCollect, boolean isSuccess) {
        //这里无用
    }

    @Override
    public void showSentenceEvalResult(String errMsg, boolean isEvalSuccess) {
        stopLoading();

        if (isEvalSuccess){
            //刷新显示
            sentenceAdapter.notifyDataSetChanged();
        }else {
            //错误显示
            ToastUtil.showToast(this,errMsg);
        }
    }

    @Override
    public void showSentencePublishResult(String errorMsg, int shuoshuoId) {
        stopLoading();
        if (TextUtils.isEmpty(errorMsg)){
            //设置数据
            sentenceAdapter.setShareData(String.valueOf(shuoshuoId));
            ToastUtil.showToast(NewSearchSentenceListActivity.this,"发布成功");
        }else {
            ToastUtil.showToast(this,errorMsg);
        }
    }

    /****************************播放器**************************/
    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //准备播放
                        if (isCanPlay){
                            playAudio(null);
                        }
                        break;
                    case Player.STATE_ENDED:
                        //播放结束
                        stopAudioToNext();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                //设置为不可用
                ToastUtil.showToast(NewSearchSentenceListActivity.this,"加载音频出错("+error.errorCode+")");
            }
        });
    }

    private void playAudio(String urlOrPath){
        if (!TextUtils.isEmpty(urlOrPath)){
            curPlayUrl = urlOrPath;

            MediaItem mediaItem = null;
            if (urlOrPath.startsWith("http")){
                mediaItem = MediaItem.fromUri(urlOrPath);
            }else {
                //本地加载
                Uri uri = Uri.fromFile(new File(urlOrPath));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    uri = FileProvider.getUriForFile(this,getResources().getString(R.string.file_provider_name_personal),new File(urlOrPath));
                }
                mediaItem = MediaItem.fromUri(uri);
            }
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }else {
            if (exoPlayer!=null&&isCanPlay){
                exoPlayer.play();
            }
        }
    }

    private void pauseAudio(){
        if (exoPlayer!=null){
            exoPlayer.pause();
        }
    }

    /********************************录音器**********************/
    private void startRecord(String savePath,long totalTime){
        try {
            File saveFile = new File(savePath);
            if (saveFile.exists()){
                saveFile.delete();
            }
            if (!saveFile.getParentFile().exists()){
                saveFile.getParentFile().mkdirs();
            }
            boolean createFile = saveFile.createNewFile();
            if (createFile){
                isRecord = true;
                //开始录音
                recordManager = new RecordManager(saveFile);
                recordManager.startRecord();
                //刷新显示
                RxTimer.getInstance().multiTimerInMain(recordAnimTag, 0, 200L, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        int audioDb = (int) recordManager.getVolume();
                        sentenceAdapter.refreshRecordEval(audioDb);
                    }
                });
                //计时器开启
                RxTimer.getInstance().timerInMain(recordTimeTag, totalTime, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(recordTimeTag);
                        RxTimer.getInstance().cancelTimer(recordAnimTag);
                        stopRecord();
                        //发起评测
                        submitEval(sentenceAdapter.getSelectData(),savePath);
                    }
                });
            }else {
                ToastUtil.showToast(this,"创建文件异常，请重试~");
            }
        }catch (Exception e){
            ToastUtil.showToast(this,"录音异常，请重试");
        }
    }

    private void stopRecord(){
        isRecord = false;
        if (recordManager!=null){
            recordManager.stopRecord();
        }
        sentenceAdapter.stopRecord();
        RxTimer.getInstance().cancelTimer(recordTimeTag);
        RxTimer.getInstance().cancelTimer(recordAnimTag);
    }

    /******************************评测****************************/
    private void submitEval(SearchSentenceBean bean,String savePath){
        if (bean==null){
            ToastUtil.showToast(this,"获取数据错误");
            return;
        }

        File evalFile = new File(savePath);
        if (!evalFile.exists()){
            ToastUtil.showToast(this,"评测文件不存在");
            return;
        }

        showLoading("正在评测中。。。");
        searchPresenter.submitSentenceEval(bean.getLessonType(),bean.getVoaId(),bean.getParaId(),bean.getIdIndex(),bean.getTitle(),savePath);
    }

    /****************************辅助功能**************************/
    //加载弹窗
    private LoadingDialog loadingDialog;

    private void showLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
    //显示加载进度
    private void updateUi(boolean isLoading,String showMsg){
        if (isLoading){
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.loadingProgress.setVisibility(View.VISIBLE);
            binding.loadingImg.setVisibility(View.GONE);
            binding.loadingMsg.setText("正在查询相关内容～");
            binding.loadingLayout.setOnClickListener(null);
        }else {
            if (!TextUtils.isEmpty(showMsg)){
                binding.loadingLayout.setVisibility(View.VISIBLE);
                binding.loadingImg.setVisibility(View.VISIBLE);
                binding.loadingProgress.setVisibility(View.GONE);
                binding.loadingMsg.setText(showMsg);
            }else {
                binding.loadingLayout.setVisibility(View.GONE);
            }
        }
    }

    //跳转操作
    private void stopAudioToNext(){
        //暂停播放
        pauseAudio();
        sentenceAdapter.stopPlay();
        RxTimer.getInstance().cancelTimer(playTag);
        sentenceAdapter.stopEvalPlay();
        RxTimer.getInstance().cancelTimer(evalTag);
    }

    //分享到其他app中
    private void shareApp(int voaId,String sentence,String evalAudioUrl,String shareId){
        //反向查询下相关的内容标题
        String content = null;
        Voa tempVoa = new VoaOp(this).findDataById(voaId);
        if (tempVoa!=null&&!TextUtils.isEmpty(tempVoa.title)){
            content = tempVoa.title;
        }
        String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + shareId
                + "&addr=" + evalAudioUrl + "&apptype=" + Constant.EVAL_TYPE;

        LoadIconUtil.loadCommonIcon(this);
        String imagePath = Constant.iconAddr;
        String title = UserInfoManager.getInstance().getUserName() + "在"+getResources().getString(R.string.app_name)+"的评测：" + sentence;
        ShareUtils localShareUtils = new ShareUtils();
        localShareUtils.setMContext(this);
        localShareUtils.setVoaId(voaId);
        localShareUtils.showShare(this, imagePath, siteUrl, title, content, localShareUtils.platformActionListener,null);
    }

    //权限弹窗
    private PermissionMsgDialog msgDialog = null;

    private void showPermissionDialog(String savePath,long totalTime){
        List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
        // TODO: 2025/4/11 根据android版本处理
        if (Build.VERSION.SDK_INT<35){
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));
        }

        msgDialog = new PermissionMsgDialog(this);
        msgDialog.showDialog(null, pairList, true, new PermissionMsgDialog.OnPermissionApplyListener() {
            @Override
            public void onApplyResult(boolean isSuccess) {
                startRecord(savePath, totalTime);
            }
        });
    }
}
