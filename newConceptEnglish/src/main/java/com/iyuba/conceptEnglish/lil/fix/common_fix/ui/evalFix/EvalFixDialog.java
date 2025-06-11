package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.evalFix;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.DialogEvalFixBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_eval;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.StudyActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FileManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.RecordManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.gson.GsonUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.SelectWordTextView;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.conceptEnglish.util.GsonUtils;
import com.iyuba.conceptEnglish.util.NetWorkState;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.view.PermissionMsgDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class EvalFixDialog extends DialogFragment {

    //参数(2.5及以下显示红色，4.0及以上显示绿色)
    private static final float Score_low = 2.5f;
    private static final float Score_high = 4.0f;

    //布局
    private DialogEvalFixBinding binding;

    //原文播放器
    private ExoPlayer audioPlayer;
    //原文播放器-音频链接
    private String audioPlay_playUrl = "";

    //录音器
    private RecordManager recordManager;
    //录音器-是否正在录音
    private boolean recordManager_isRecording = false;
    //录音器-录音地址
    private String recordManager_recordPath = "";

    //评测播放器
    private ExoPlayer evalPlayer;

    //待纠音的数据
    private EvalFixBean fixBean;
    //当前单词的id
    private int curWordId = 0;
    //权限弹窗
    private PermissionMsgDialog permissionDialog;

    public static EvalFixDialog getInstance(String jsonData){
        EvalFixDialog fixDialog = new EvalFixDialog();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.data,jsonData);
        fixDialog.setArguments(bundle);
        return fixDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取数据并转换
        String jsonData = getArguments().getString(StrLibrary.data);
        if (!TextUtils.isEmpty(jsonData)){
            fixBean = GsonUtils.toObject(jsonData,EvalFixBean.class);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEvalFixBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initClick();

        initAudioPlayer();
        initEvalPlayer();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog!=null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopPage();
    }

    private void initData(){
        //获取数据并转换
        if (fixBean!=null){
            updateUI(true,"正在查询纠音信息");
            initSpan();
        }else {
            updateUI(false,"请设置需要显示的句子");
        }
    }

    private void initClick(){
        binding.close.setOnClickListener(v->{
            stopPage();
            dismiss();
        });
        binding.playAudio.setOnClickListener(v->{
            if (audioPlayer==null){
                ToastUtil.showToast(getContext(),"未初始化原声播放器");
                return;
            }

            if (recordManager_isRecording){
                ToastUtil.showToast(getContext(),"正在录音评测中～");
                return;
            }

            if (audioPlayer.isPlaying()){
                stopAudio();
            }
            playAudio(audioPlay_playUrl);
        });
        binding.recordAudio.setOnClickListener(v->{
            checkPermission();
        });
        binding.evalAudio.setOnClickListener(v->{
            if (evalPlayer==null){
                ToastUtil.showToast(getContext(),"未初始化评测播放器");
                return;
            }

            if (recordManager_isRecording){
                ToastUtil.showToast(getContext(),"正在录音评测中～");
                return;
            }

            if (evalPlayer.isPlaying()){
                stopEval();
            }
            playEval(recordManager_recordPath);
        });
    }

    private void initSpan(){
        //转换数据
        if (fixBean!=null && fixBean.getWordList()!=null && !fixBean.getWordList().isEmpty()){
            //合成数据
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < fixBean.getWordList().size(); i++) {
                EvalFixBean.WordEvalFixBean wordBean = fixBean.getWordList().get(i);
                stringBuilder.append(wordBean.getWord());
                if (i < fixBean.getWordList().size()-1){
                    stringBuilder.append(" ");
                }
            }

            SpannableStringBuilder builder = new SpannableStringBuilder(stringBuilder.toString());

            //循环处理数据
            int curShowPosition = 0;
            for (int i = 0; i < fixBean.getWordList().size(); i++) {
                EvalFixBean.WordEvalFixBean wordBean = fixBean.getWordList().get(i);
                //判断是否存在空格
                String showWord = wordBean.getWord();
                if (i<fixBean.getWordList().size()-1){
                    showWord+=" ";
                }
                //判断数据显示
                if (wordBean.getScore()>=Score_low && wordBean.getScore()<=Score_high){
                    builder.setSpan(new ForegroundColorSpan(Color.BLACK),curShowPosition,curShowPosition+showWord.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }else {
                    builder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            if (recordManager_isRecording){
                                ToastUtil.showToast(getContext(),"正在录音评测中～");
                                return;
                            }

                            //切换单词显示
                            switchSearchWord();
                            //查询单词
                            String needWord = filterWord(wordBean.getWord());
                            binding.word.setText(needWord);
                            queryWord(needWord);
                            //获取当前的用户音标
                            curWordId = wordBean.getSort();
                            EvalFixBean.WordEvalFixBean tempData = getWordData(curWordId);
                            binding.userPron.setText(TextUtils.isEmpty(tempData.getUserPron())?"":"["+tempData.getUserPron()+"]");
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            if (wordBean.getScore()<Score_low){
                                ds.setColor(Color.RED);
                            }else if (wordBean.getScore()>Score_high){
                                ds.setColor(0xff079500);
                            }
                        }
                    },curShowPosition,curShowPosition+showWord.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                    //设置第一个需要显示的数据
                    if (TextUtils.isEmpty(binding.word.getText().toString())){
                        switchSearchWord();
                        //重置数据
                        switchSearchWord();
                        //设置单词id
                        curWordId = wordBean.getSort();
                        //查询单词数据
                        String needWord = filterWord(wordBean.getWord());
                        binding.word.setText(needWord);
                        queryWord(needWord);
                    }
                }
                //设置位置
                curShowPosition+=showWord.length();
            }

            //设置数据显示
            binding.sentence.setText(builder);
            binding.sentence.setMovementMethod(LinkMovementMethod.getInstance());
            binding.sentence.setHighlightColor(Color.TRANSPARENT);
        }
    }

    /**********************************主要操作******************************/
    //查询单词数据
    private Disposable searchWordDis;
    private void queryWord(String word){
        if (!NetWorkState.isConnectingToInternet()){
            updateUI(false,"请链接网络后重试");
            return;
        }

        updateUI(true,"正在查询单词信息～");
        CommonDataManager.searchWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_detail>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        searchWordDis = d;
                    }

                    @Override
                    public void onNext(Word_detail wordDetail) {
                        if (wordDetail!=null && wordDetail.result.equals("1")){
                            updateUI(false,null);
                            //显示样式
                            showSearchWord(wordDetail);
                        }else {
                            updateUI(false,"查询单词失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        updateUI(false,"查询单词异常，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //提交评测数据
    private Disposable submitEvalDis;
    private void submitEval(){
        if (!NetWorkState.isConnectingToInternet()){
            updateUI(false,"请链接网络后重试");
            return;
        }

        //需要的数据
        EvalFixBean.WordEvalFixBean wordData = getWordData(curWordId);

        updateUI(true,"正在提交评测数据～");
        CommonDataManager.evalWord(fixBean.getVoaId(),fixBean.getParaId(),fixBean.getIdIndex(),UserInfoManager.getInstance().getUserId(), String.valueOf(wordData.getSort()),wordData.getWord(),recordManager_recordPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_eval>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitEvalDis = d;
                    }

                    @Override
                    public void onNext(Word_eval wordEval) {
                        if (wordEval!=null && wordEval.getResult().equals("1")){
                            updateUI(false,null);
                            //将数据放在树中
                            EvalFixBean.WordEvalFixBean curWordData = getWordData(curWordId);
                            curWordData.setUserPron(wordEval.getData().getWords().get(0).getUser_pron2());
                            fixBean.getWordList().set(getCurWordIndex(curWordId),curWordData);
                            //显示数据
                            String userPorn = getWordData(curWordId).getUserPron();
                            binding.userPron.setText(TextUtils.isEmpty(userPorn)?"":"["+userPorn+"]");
                            //显示评测
                            showEvalView(true);
                            binding.evalAudioText.setText(String.valueOf(wordEval.getData().getScores()));
                        }else {
                            updateUI(false,"单词评测失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        updateUI(false,"单词评测异常("+e.getMessage()+")");
                    }

                    @Override
                    public void onComplete() {
                        binding.recordAudioText.setText("点击录音");
                        recordManager_isRecording = false;
                    }
                });
    }

    //音频播放
    private void initAudioPlayer(){
        audioPlayer = new ExoPlayer.Builder(getContext()).build();
        audioPlayer.setPlayWhenReady(false);
        audioPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        playAudio(null);
                        break;
                    case Player.STATE_ENDED:
                        break;
                }
            }



            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getContext(),"原音播放器加载异常("+error.getMessage()+")");
            }
        });
    }

    private void playAudio(String playUrl){
        if (audioPlayer==null){
            ToastUtil.showToast(getContext(),"原音播放器未初始化");
            return;
        }

        if (TextUtils.isEmpty(playUrl)){
            audioPlayer.play();
        }else {
            MediaItem mediaItem = MediaItem.fromUri(playUrl);
            audioPlayer.setMediaItem(mediaItem);
            audioPlayer.prepare();
        }
    }

    private void stopAudio(){
        if (audioPlayer!=null){
            audioPlayer.stop();
        }
    }

    //录音评测
    private void checkPermission(){
        if (getActivity()==null){
            return;
        }

        //权限判断
        List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
        // TODO: 2025/4/11 区分android版本处理
        if (Build.VERSION.SDK_INT < 35){
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));
        }
        permissionDialog = new PermissionMsgDialog(getActivity());
        permissionDialog.showDialog(null, pairList, true, new PermissionMsgDialog.OnPermissionApplyListener() {
            @Override
            public void onApplyResult(boolean isSuccess) {
                if (isSuccess){
                    if (recordManager_isRecording){
                        stopRecord();
                        submitEval();
                    }else {
                        startRecord();
                    }
                }
            }
        });


//        else {
//            int permissionCode = PermissionFixUtil.concept_fix_recordAudio_code;
//            if (getActivity() instanceof StudyNewActivity){
//                permissionCode = PermissionFixUtil.concept_fix_recordAudio_code;
//            }else if (getActivity() instanceof StudyActivity){
//                permissionCode = PermissionFixUtil.junior_fix_recordAudio_code;
//            }
//
//            boolean isPermissionOk = PermissionFixUtil.isPermissionOk(getActivity(),permissionCode);
//            if (isPermissionOk){
//                if (recordManager_isRecording){
//                    stopRecord();
//                    submitEval();
//                }else {
//                    startRecord();
//                }
//            }
//        }
    }

    private void startRecord(){
        //当前单词
        EvalFixBean.WordEvalFixBean wordData = getWordData(curWordId);
        //设置存储地址
        recordManager_recordPath = FileManager.getInstance().getFixWordEvalAudioPath("fixAudio",String.valueOf(fixBean.getVoaId()),String.valueOf(fixBean.getParaId()),String.valueOf(fixBean.getIdIndex()),String.valueOf(wordData.getSort()), UserInfoManager.getInstance().getUserId());
        //创建文件
        File saveFile = new File(recordManager_recordPath);
        try {
            if (saveFile.exists()){
                saveFile.delete();
            }
            if (!saveFile.getParentFile().exists()){
                saveFile.getParentFile().mkdirs();
            }
            saveFile.createNewFile();
        }catch (Exception e){

        }

        //判断是否存在
        if (saveFile.exists()){
            binding.recordAudioText.setText("点击停止");
            recordManager_isRecording = true;
            startTimer();
            recordManager = new RecordManager(saveFile);
            recordManager.startRecord();
        }else {
            ToastUtil.showToast(getContext(),"创建录音文件失败");
        }
    }

    private void stopRecord(){
        if (recordManager!=null){
            recordManager.stopRecord();
        }
        stopTimer();
    }

    private static final String timer_record = "recordTimer";
    private void startTimer(){
        RxTimer.getInstance().timerInMain(timer_record, 3000L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                stopRecord();
                submitEval();
            }
        });
    }

    private void stopTimer(){
        RxTimer.getInstance().cancelTimer(timer_record);
    }

    //评测播放
    private void initEvalPlayer(){
        evalPlayer = new ExoPlayer.Builder(getContext()).build();
        evalPlayer.setPlayWhenReady(false);
        evalPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        playEval(null);
                        break;
                    case Player.STATE_ENDED:
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getContext(),"评测播放器加载失败");
            }
        });
    }

    private void playEval(String playPath){
        if (TextUtils.isEmpty(playPath)){
            if (!evalPlayer.isPlaying()){
                evalPlayer.play();
            }
        }else {
            //本地加载
            Uri uri = Uri.fromFile(new File(playPath));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                uri = FileProvider.getUriForFile(getActivity(),getResources().getString(R.string.file_provider_name_personal),new File(playPath));
            }
            MediaItem mediaItem = MediaItem.fromUri(uri);
            evalPlayer.setMediaItem(mediaItem);
            evalPlayer.prepare();
        }
    }

    private void stopEval(){
        if (evalPlayer!=null){
            evalPlayer.stop();
        }
    }

    /****************************其他功能******************************/
    //筛选单词数据
    private String filterWord(String showWord){
        if (TextUtils.isEmpty(showWord)){
            return showWord;
        }

        showWord = showWord.trim();
        showWord = showWord.replace("!","");
        showWord = showWord.replace(",","");
        showWord = showWord.replace(".","");
        showWord = showWord.replace("?","");
        return showWord;
    }

    //关闭界面
    private void stopPage(){
        RxUtil.unDisposable(searchWordDis);
        RxUtil.unDisposable(submitEvalDis);
        stopAudio();
        stopRecord();
        stopEval();
    }

    //展示ui
    private void updateUI(boolean isLoading,String showMsg){
        if (isLoading){
            binding.stateLayout.setVisibility(View.VISIBLE);
            binding.progressLoading.setVisibility(View.VISIBLE);
            binding.progressMsg.setText(showMsg);
        }else {
            binding.stateLayout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(showMsg)){
                ToastUtil.showToast(getContext(),showMsg);
            }
        }
    }

    //根据id获取单词数据
    private EvalFixBean.WordEvalFixBean getWordData(int wordId){
        for (int i = 0; i < fixBean.getWordList().size(); i++) {
            EvalFixBean.WordEvalFixBean tempData = fixBean.getWordList().get(i);
            if (tempData.getSort() == wordId){
                return tempData;
            }
        }

        return null;
    }

    //开启/关闭评测显示
    private void showEvalView(boolean isOpen){
        if (isOpen){
            binding.evalAudio.setVisibility(View.VISIBLE);
            binding.evalAudioText.setVisibility(View.VISIBLE);
        }else {
            binding.evalAudio.setVisibility(View.INVISIBLE);
            binding.evalAudioText.setVisibility(View.INVISIBLE);
        }
    }

    //开启/关闭原音显示
    private void showAudioView(boolean isOpen){
        if (isOpen){
            binding.playAudio.setVisibility(View.VISIBLE);
            binding.playAudioText.setVisibility(View.VISIBLE);
        }else {
            binding.playAudio.setVisibility(View.INVISIBLE);
            binding.playAudioText.setVisibility(View.INVISIBLE);
        }
    }

    //开启/关闭录音显示
    private void showRecordView(boolean isOpen){
        if (isOpen){
            binding.recordAudio.setVisibility(View.VISIBLE);
            binding.recordAudioText.setVisibility(View.VISIBLE);
        }else {
            binding.recordAudio.setVisibility(View.INVISIBLE);
            binding.recordAudioText.setVisibility(View.INVISIBLE);
        }
    }

    //切换查询单词
    private void switchSearchWord(){
        audioPlay_playUrl = "";
        //关闭评测显示
        showEvalView(false);
    }

    //展示单词查询结果
    private void showSearchWord(Word_detail wordDetail){
        binding.word.setText(wordDetail.key);
        binding.wordPron.setText(TextUtils.isEmpty(wordDetail.pron)?"":"["+wordDetail.pron+"]");
        binding.wordDef.setText(TextUtils.isEmpty(wordDetail.def)?"":wordDetail.def.replace("\n",""));

        if (TextUtils.isEmpty(wordDetail.audio)){
            showAudioView(false);
        }else {
            showAudioView(true);
            //配置音频链接
            audioPlay_playUrl = wordDetail.audio;
        }
        showRecordView(true);
    }

    //获取当前数据为第几个数据
    private int getCurWordIndex(int curWordId){
        for (int i = 0; i < fixBean.getWordList().size(); i++) {
            EvalFixBean.WordEvalFixBean wordData = fixBean.getWordList().get(i);
            if (wordData.getSort() == curWordId){
                return i;
            }
        }
        return 0;
    }
}
