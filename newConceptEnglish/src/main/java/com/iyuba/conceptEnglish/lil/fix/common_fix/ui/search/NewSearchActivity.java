package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.AtySearchNewBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.StudyDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.bean.SearchSentenceBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.data.NewSearchPresenter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.data.NewSearchView;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.RecordManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.recyclerview.FlowLayoutManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.conceptEnglish.util.LoadIconUtil;
import com.iyuba.conceptEnglish.util.ShareUtils;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.sqlite.op.WordOp;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.view.PermissionMsgDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * @title:  新的搜索界面
 * @date: 2023/11/16 15:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 *
 * 之前搜索界面查询单词的接口
 */
public class NewSearchActivity extends BaseViewBindingActivity<AtySearchNewBinding> implements NewSearchView {

    //文章显示
    private List<Voa> articleList;
    private SearchArticleAdapter articleAdapter;
    //句子显示
    private List<VoaDetail> sentenceList;
    private SearchSentenceAdapter sentenceAdapter;
    //历史记录显示
    private SearchHistoryListAdapter historyListAdapter;

    //操作内容
    private NewSearchPresenter searchPresenter;
    //当前的单词数据
    private Word_detail curWordDetail;

    //播放器
    private ExoPlayer exoPlayer;
    //是否可以播放
    private boolean isCanPlay = false;
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

    //是否删除/收藏操作完成
    private boolean isRefreshWordList = false;

    public static void start(Context context,String wordStr){
        Intent intent = new Intent();
        intent.setClass(context,NewSearchActivity.class);
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

        //初始化
        initList();
        initClick();
        initPlayer();

        //先判断是否传输了单词数据
        String wordKey = getIntent().getStringExtra(StrLibrary.word);
        if (!TextUtils.isEmpty(wordKey)){
            binding.input.setText(wordKey);
            checkWordData();
        }else {
            updateUi(false,"还没有任何搜索哟~",true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCanPlay = true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        isCanPlay = false;
        pauseAudio();
    }

    @Override
    protected void onDestroy() {
        stopAudioToJump();
        searchPresenter.detachView();

        if (msgDialog!=null){
            msgDialog.dismiss();
        }

        if (isRefreshWordList){
            //刷新收藏列表显示
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.word_note));
        }

        super.onDestroy();
    }

    /**********************初始化数据***************************/
    private void initList(){
        historyListAdapter = new SearchHistoryListAdapter(this,new String[]{});
        binding.historyView.setLayoutManager(new FlowLayoutManager());
        binding.historyView.setAdapter(historyListAdapter);
        historyListAdapter.setOnHistoryItemClickListener(new SearchHistoryListAdapter.OnHistoryItemClickListener() {
            @Override
            public void onClick(String wordStr) {
                binding.input.setText(wordStr);
                checkWordData();
            }

            @Override
            public void onDelete(String wordStr) {
                new AlertDialog.Builder(NewSearchActivity.this)
                        .setMessage("是否删除 "+wordStr+" 的搜索记录？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StudyDataManager.getInstance().deleteHistoryData(wordStr);
                                String[] searchData = StudyDataManager.getInstance().getHistoryData();
                                historyListAdapter.refreshData(searchData);
                                if (searchData.length==0){
                                    updateUi(false,"还没有任何搜索哟~",true);
                                }
                            }
                        }).setNegativeButton("取消",null)
                        .create().show();
            }
        });

        articleAdapter = new SearchArticleAdapter(this,new ArrayList<>());
        binding.articleView.setLayoutManager(new NoScrollLinearLayoutManager(this,false));
        binding.articleView.setAdapter(articleAdapter);
        articleAdapter.setOnSearchArticleItemListener(voa -> {
            stopAudioToJump();

            //设置数据
            VoaDataManager.Instace().voaTemp = voa;
            VoaDataManager.Instace().voaDetailsTemp = new VoaDetailOp(this).findDataByVoaId(voa.voaId);

            //设置为临时数据
            ConceptBgPlaySession.getInstance().setTempData(true);

            //跳转界面
            Intent intent = new Intent();
            intent.setClass(this, StudyNewActivity.class);
            intent.putExtra("curVoaId",String.valueOf(voa.voaId));
            startActivity(intent);
        });

        sentenceAdapter = new SearchSentenceAdapter(this,new ArrayList<>());
        binding.sentenceView.setLayoutManager(new NoScrollLinearLayoutManager(this,false));
        binding.sentenceView.setAdapter(sentenceAdapter);
        binding.sentenceView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        sentenceAdapter.setOnSentenceItemListener(new SearchSentenceAdapter.OnSentenceItemListener() {
            @Override
            public void onSwitchItem(int position) {
                //停止
                stopAudioToNext();

                if (isRecord){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中...");
                    return;
                }

                //刷新选中
                sentenceAdapter.refreshIndex(position);
            }

            @Override
            public void onAudioPlay(String audioUrl, long startTime, long endTime) {
                //先判断是否在录音
                if (isRecord){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中...");
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
                    ToastUtil.showToast(NewSearchActivity.this,"音频播放器未初始化");
                }
            }

            @Override
            public void onRecordEval(int selectPosition,String savePath,long totalTime) {
                //停止播放
                stopAudioToNext();
                //判断是否在录音
                if (selectPosition!=sentenceAdapter.getSelectIndex()&&isRecord){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中...");
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
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中...");
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
                    ToastUtil.showToast(NewSearchActivity.this,"音频播放器未初始化");
                }
            }

            @Override
            public void onPublish(int voaId, String paraId, String idIndex, int score, String evalAudioUrl) {
                //判断是否在录音
                if (isRecord){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中...");
                    return;
                }

                stopAudioToNext();

                //提交发布
                if (TextUtils.isEmpty(evalAudioUrl)){
                    ToastUtil.showToast(NewSearchActivity.this,"未获取到评测数据");
                    return;
                }

                showLoading("正在发布到排行榜～");
                searchPresenter.publishEvalSentence(voaId,paraId,idIndex,String.valueOf(score),evalAudioUrl);
            }

            @Override
            public void onShare(int voaId, String sentence, String evalAudioUrl, String shareId) {
                //判断是否在录音
                if (isRecord){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中...");
                    return;
                }

                stopAudioToNext();

                if (TextUtils.isEmpty(shareId)){
                    ToastUtil.showToast(NewSearchActivity.this,"未找到分享id");
                }else {
                    shareApp(voaId, sentence, evalAudioUrl, shareId);
                }
            }
        });
    }

    private void initClick(){
        binding.back.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
        binding.audioView.setOnClickListener(v->{
            if (curWordDetail!=null&&!TextUtils.isEmpty(curWordDetail.audio)){
                stopAudioToNext();
                playAudio(curWordDetail.audio);
            }else {
                ToastUtil.showToast(this,"未找到当前音频");
            }
        });
        binding.collectView.setOnClickListener(v->{
            //先查询当前的收藏状态
            if (curWordDetail==null||TextUtils.isEmpty(curWordDetail.key)){
                ToastUtil.showToast(this,"未查询到单词信息");
            }else {
                showLoading("正在操作中～");

                Word collectWord = new WordOp(this).findDataByName(curWordDetail.key,String.valueOf(UserInfoManager.getInstance().getUserId()));
                if (collectWord==null){
                    //准备收藏
                    searchPresenter.collectWord(true,curWordDetail.key);
                }else {
                    //准备取消收藏
                    searchPresenter.collectWord(false,curWordDetail.key);
                }
            }
        });
        binding.articleMore.setOnClickListener(v->{
            stopAudioToJump();

            NewSearchArticleListActivity.start(this,curWordDetail.key);
        });
        binding.sentenceMore.setOnClickListener(v->{
            stopAudioToJump();

            NewSearchSentenceListActivity.start(this,curWordDetail.key);
        });
        binding.clear.setOnClickListener(v->{
            binding.input.setText("");
            updateUi(false,"还没有任何搜索哟~",true);
            curWordDetail = null;
        });
        binding.input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    //查询
                    String keyWord = binding.input.getText().toString().trim();
                    if (TextUtils.isEmpty(keyWord)){
                        updateUi(false,"查询内容不能为空",false);
                    }else {
                        checkWordData();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /***********************查询数据****************************/
    private void checkWordData(){
        updateUi(true,null,false);

        String wordStr = binding.input.getText().toString().trim();
        if (!TextUtils.isEmpty(wordStr)){
            binding.input.setEnabled(false);
            searchPresenter.searchWord(wordStr);
            //保存在缓存中作为历史记录
            StudyDataManager.getInstance().addHistoryData(wordStr);

            checkArticleData(wordStr);
            checkSentenceData(wordStr);
        }else {
            updateUi(false,"请输入正确的单词",false);
        }
    }

    private void checkArticleData(String wordStr){
        //从数据库中查询数据并展示
        articleList = new VoaOp(this).findDataByKeyLimit10(wordStr);
        //这里查询出的数据进行处理
        for (int i = 0; i < articleList.size(); i++) {
            //判断类型
            Voa tempVoa = articleList.get(i);
            if (tempVoa.voaId > 300000){
                tempVoa.lessonType = TypeLibrary.BookType.conceptJunior;
            }else {
                //这里根据当前数据类型来判断显示哪个
                if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptJunior)){
                    tempVoa.lessonType = TypeLibrary.BookType.conceptFourUS;
                }else {
                    tempVoa.lessonType = ConceptBookChooseManager.getInstance().getBookType();
                }
            }
            //临时数据，默认不显示单词操作
            tempVoa.position = -1;
            articleList.set(i,tempVoa);
        }

        if (articleList!=null&&articleList.size()>0){
            binding.articleLayout.setVisibility(View.VISIBLE);

            //只显示前三个，多于三个不显示
            List<Voa> showList = new ArrayList<>();
            for (int i = 0; i < articleList.size(); i++) {
                if (i<3){
                    showList.add(articleList.get(i));
                }
            }
            articleAdapter.refreshList(showList);

            if (articleList.size()>3){
                binding.articleMore.setVisibility(View.VISIBLE);
            }else {
                binding.articleMore.setVisibility(View.INVISIBLE);
            }
        }else {
            binding.articleLayout.setVisibility(View.GONE);
        }
    }

    private void checkSentenceData(String wordStr){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从数据库查询数据并展示
                List<SearchSentenceBean> sentenceList = new VoaDetailOp(NewSearchActivity.this).findAllDataByKeyLimit10(wordStr);
                if (sentenceList!=null&&sentenceList.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.sentenceLayout.setVisibility(View.VISIBLE);
                        }
                    });

                    //只显示三个
                    List<SearchSentenceBean> showList = new ArrayList<>();
                    for (int i = 0; i < sentenceList.size(); i++) {
                        if (i<3){
                            showList.add(sentenceList.get(i));
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sentenceAdapter.refreshData(showList);

                            if (sentenceList.size()>3){
                                binding.sentenceMore.setVisibility(View.VISIBLE);
                            }else {
                                binding.sentenceMore.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.sentenceLayout.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
    }

    /************************界面显示****************************/
    //显示加载进度
    private void updateUi(boolean isLoading,String showMsg,boolean isClear){
        //分状态显示
        if (isLoading){
            //加载中
            binding.noDataLayout.setVisibility(View.VISIBLE);
            binding.historyLayout.setVisibility(View.GONE);
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.loadingProgress.setVisibility(View.VISIBLE);
            binding.loadingImg.setVisibility(View.GONE);
            binding.loadingMsg.setText("正在查询相关内容～");
            binding.loadingLayout.setOnClickListener(null);
            return;
        }

        if (isClear){
            //清空了
            binding.noDataLayout.setVisibility(View.VISIBLE);
            String[] historyData = StudyDataManager.getInstance().getHistoryData();
            if (historyData!=null&&historyData.length>0){
                binding.historyLayout.setVisibility(View.VISIBLE);
                binding.loadingLayout.setVisibility(View.GONE);

                historyListAdapter.refreshData(historyData);
            }else {
                binding.historyLayout.setVisibility(View.GONE);
                binding.loadingLayout.setVisibility(View.VISIBLE);
                binding.loadingImg.setVisibility(View.VISIBLE);
                binding.loadingProgress.setVisibility(View.GONE);
                binding.loadingMsg.setText(showMsg);
                binding.loadingLayout.setOnClickListener(null);
            }
            return;
        }

        if (!TextUtils.isEmpty(showMsg)){
            //失败和异常显示
            binding.noDataLayout.setVisibility(View.VISIBLE);
            binding.historyLayout.setVisibility(View.GONE);
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.loadingImg.setVisibility(View.VISIBLE);
            binding.loadingProgress.setVisibility(View.GONE);
            binding.loadingMsg.setText(showMsg);
            binding.loadingLayout.setOnClickListener(v->{
                checkWordData();
            });
            return;
        }

        //正确显示
        binding.noDataLayout.setVisibility(View.GONE);
    }

    /**************************回调显示***************************/
    @Override
    public void showWord(String failMsg,Word_detail detail) {
        binding.input.setEnabled(true);

        if (detail!=null){
            updateUi(false,null,false);

            //设置为当前的单词
            curWordDetail = detail;

            //显示单词内容
            binding.wordView.setText(detail.key);
            if (TextUtils.isEmpty(detail.pron)){
                binding.pron.setText("");
            }else {
                binding.pron.setText("["+detail.pron+"]");
            }
            if (TextUtils.isEmpty(detail.def)){
                binding.def.setText("暂无释义");
            }else {
                binding.def.setText(detail.def);
            }

            //本地查询当前单词的收藏信息
            showWordCollectView(detail.key);
        }else {
            updateUi(false,failMsg,false);
        }
    }

    @Override
    public void showCollectResult(boolean isCollect, boolean isSuccess) {
        stopLoading();

        //显示的消息
        String showMsg = "";

        if (isSuccess){
            showMsg = "取消收藏单词成功";
            if (isCollect){
                showMsg = "收藏单词成功";

                //放在数据库中
                Word newWord = new Word();
                newWord.audioUrl = curWordDetail.audio;
                newWord.def = curWordDetail.def;
                newWord.userid = String.valueOf(UserInfoManager.getInstance().getUserId());
                newWord.pron = curWordDetail.pron;
                newWord.key = curWordDetail.key;
                new WordOp(this).saveData(newWord);

                //设置标志
                isRefreshWordList = false;
            }else {
                //从数据库中删除
                new WordOp(this).deleteItemWord(String.valueOf(UserInfoManager.getInstance().getUserId()),curWordDetail.key);

                //设置标志
                isRefreshWordList = true;
            }

            //显示收藏信息
            showWordCollectView(curWordDetail.key);
        }else {
            showMsg = "取消收藏单词失败";
            if (isCollect){
                showMsg = "收藏单词失败";
            }
        }
        ToastUtil.showToast(this,showMsg);
    }

    @Override
    public void showSentenceEvalResult(String errMsg,boolean isEvalSuccess) {
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
    public void showSentencePublishResult(String errorMsg,int shuoshuoId) {
        stopLoading();
        if (TextUtils.isEmpty(errorMsg)){
            //设置数据
            sentenceAdapter.setShareData(String.valueOf(shuoshuoId));
            ToastUtil.showToast(NewSearchActivity.this,"发布成功");
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
                ToastUtil.showToast(NewSearchActivity.this,"加载音频出错("+error.errorCode+")");
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

    //显示单词收藏信息
    private void showWordCollectView(String word){
        Word collectWord = new WordOp(this).findDataByName(word,String.valueOf(UserInfoManager.getInstance().getUserId()));
        if (collectWord!=null){
            binding.collectView.setImageResource(R.drawable.headline_collect);
        }else {
            binding.collectView.setImageResource(R.drawable.headline_collect_not);
        }
    }

    //跳转操作
    private void stopAudioToJump(){
        //暂停播放
        pauseAudio();

        if (sentenceAdapter!=null){
            sentenceAdapter.stopPlay();
            RxTimer.getInstance().cancelTimer(playTag);
            sentenceAdapter.stopEvalPlay();
            RxTimer.getInstance().cancelTimer(evalTag);
        }
        //停止录音
        if (isRecord){
            stopRecord();
        }
        RxTimer.getInstance().cancelTimer(recordTimeTag);
        RxTimer.getInstance().cancelTimer(recordAnimTag);
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
        List<Pair<String,Pair<String,String>>> pairList = new ArrayList<>();
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
