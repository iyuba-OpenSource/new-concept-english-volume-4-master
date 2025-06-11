package com.iyuba.conceptEnglish.lil.fix.concept.study;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.WebActivity;
import com.iyuba.conceptEnglish.ad.AdInitManager;
import com.iyuba.conceptEnglish.databinding.FragmentContentBinding;
import com.iyuba.conceptEnglish.han.utils.ExpandKt;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.concept_other.util.ConceptHomeRefreshUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshUserInfoEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Study_report;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.banner.AdBannerShowManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.banner.AdBannerViewBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line.PractiseLineEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.BigDecimalUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.DateUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.CenterLinearLayoutManager;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.mode.ArticleRecordBean;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.op.ArticleRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailYouthOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.widget.dialog.DialogCallBack;
import com.iyuba.conceptEnglish.widget.dialog.ListenStudyReportDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.sdk.other.NetworkUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 新的原文界面
 * @date: 2023/10/27 14:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ContentFragment extends BaseViewBindingFragment<FragmentContentBinding> {

    /*********部分数据标志*************/
    //中英文切换
    private static final String showChinese = "showChinese";
    //倍速
    private static final String playSpeed = "playSpeed";
    //播放类型
    private static final String mode = "mode";

    //播放器
    private ExoPlayer exoPlayer;
    //是否可以播放
    private boolean isCanPlay = true;
    //适配器
    private ContentAdapter contentAdapter;
    //是否文本可以滚动
    private boolean isSyncText = true;

    //ab点播放处理
    private long aPositon, bPosition, abState = 0L;
    //收藏的单词
    private Map<String,VoaWord2> collectWordMap = new HashMap<>();
    //听力学习报告的开始时间
    // TODO: 2023/11/24 这里根据李涛所说，开始时间一样也会变成重复数据提交的 
    private long listenStartTime = 0;


    //当前的课程数据
    private Voa curVoa;
    //当前的内容数据
    private List<VoaDetail> curDetailList = new ArrayList<>();
    //是否来自练习题界面
    private boolean isFromPractise = false;

    public static ContentFragment getInstance(int position,boolean isFromPractise){
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(StrLibrary.position,position);
        bundle.putBoolean(StrLibrary.from,isFromPractise);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        isFromPractise = getArguments().getBoolean(StrLibrary.from,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //通用数据
        initList();
        initData();
        checkData();
        initClick();

        //开启广告计时器
        if (!UserInfoManager.getInstance().isVip()
                &&!AdBlocker.getInstance().shouldBlockAd()){
            startAdTimer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

//        closeSearchWordDialog();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);

        //关闭广告
        AdBannerShowManager.getInstance().stopBannerAd();
        stopTimer();
//        closeSearchWordDialog();

        if (ConceptBgPlaySession.getInstance().isTempData()){
            pauseTempAudio();
        }

        super.onDestroyView();
    }

    /******************************初始化************************/
    private void initData(){
        curVoa = VoaDataManager.getInstance().voaTemp;
        if (ConceptBgPlaySession.getInstance().isTempData()){
            initTempPlayer();
        }else {
            exoPlayer = ConceptBgPlayManager.getInstance().getPlayService().getPlayer();
        }
        //隐藏查词
        binding.word.setVisibility(View.GONE);
        //设置滚动(默认为可以滚动)
        binding.ivSync.setImageResource(R.drawable.icon_sync);
        //设置中英文
        boolean isChinese = ConfigManager.Instance().loadBoolean(showChinese);
        if (isChinese){
            binding.CHN.setImageResource(R.drawable.show_chinese_selected);
        }else {
            binding.CHN.setImageResource(R.drawable.show_chinese);
        }
        contentAdapter.refreshLanguage(isChinese?TypeLibrary.TextShowType.ALL:TypeLibrary.TextShowType.EN);
        //设置倍速
        float speed = ConfigManager.Instance().loadFloat(playSpeed, 1.0f);
        exoPlayer.setPlaybackSpeed(speed);
        binding.textPlaySpeed10.setText("倍速"+speed+"x");
        //设置播放模式
        switchPlayMode();
        //设置是否自动播放
        //设置更多功能界面
        binding.llMoreFunction.setVisibility(View.GONE);
        //设置文本字体大小
        int level = ConfigManager.Instance().getfontSizeLevel();
        contentAdapter.refreshTextSize(16+2*level);
    }

    private void initList(){
        contentAdapter = new ContentAdapter(getActivity(),curDetailList);
        binding.recyclerView.setLayoutManager(new CenterLinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(contentAdapter);
        contentAdapter.setOnWordSelectListener(new ContentAdapter.OnWordSelectListener() {
            @Override
            public void onSelect(String selectText) {
                if (isFastClick()){
                    return;
                }

                if (!TextUtils.isEmpty(selectText)&&selectText.matches("^[a-zA-Z]*")) {
//                    showSearchWordDialog(selectText);

                    pauseAudio(false);
                    NewSearchActivity.start(getActivity(),selectText);
                }else {
                    CustomToast.showToast(getActivity(), R.string.play_please_take_the_word, 1000);
                }
            }
        });
    }

    private void initClick(){
        //切换中英文
        binding.reCHN.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            boolean isChinese = ConfigManager.Instance().loadBoolean(showChinese);
            if (isChinese){
                ConfigManager.Instance().putBoolean(showChinese,false);
                binding.CHN.setImageResource(R.drawable.show_chinese);
                VoaDataManager.Instace().changeLanguage(true);
            }else {
                ConfigManager.Instance().putBoolean(showChinese,true);
                binding.CHN.setImageResource(R.drawable.show_chinese_selected);
                VoaDataManager.Instace().changeLanguage(false);
            }
            contentAdapter.refreshLanguage(isChinese?TypeLibrary.TextShowType.EN:TypeLibrary.TextShowType.ALL);
        });
        //切换ab点
        binding.abplay.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            abState++;
            if (abState % 3 == 1) {
                aPositon = exoPlayer.getCurrentPosition();
                CustomToast.showToast(getActivity(), R.string.study_ab_a, 2000);
            } else if (abState % 3 == 2) {
                bPosition = exoPlayer.getCurrentPosition();
                if (exoPlayer!=null){
                    exoPlayer.seekTo(aPositon);
                    if (!exoPlayer.isPlaying()){

                        if (ConceptBgPlaySession.getInstance().isTempData()){
                            playTempAudio(null);
                        }else {
                            playAudio(null);
                        }

                    }
                }
                CustomToast.showToast(getActivity(), R.string.study_ab_b, 1000);
            } else if (abState % 3 == 0) {
                aPositon = 0;
                bPosition = 0;
                CustomToast.showToast(getActivity(), "区间播放已取消", 1000);
            }
        });
        //切换上/下进度
        binding.formerButton.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            //获取当前的进度
            int selectIndex = contentAdapter.getSelectIndex();
            if (selectIndex<=0){
                ToastUtil.showToast(getActivity(),"当前已经是第一个了");
            }else {
                int preIndex = selectIndex-1;
                long preProgress = (long) (curDetailList.get(preIndex).startTime*1000L);
                exoPlayer.seekTo(preProgress);
            }
        });
        binding.nextButton.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            //获取当前的进度
            int selectIndex = contentAdapter.getSelectIndex();
            if (selectIndex >= curDetailList.size()-1){
                ToastUtil.showToast(getActivity(),"当前已经是最后一个了");
            }else {
                int nextIndex = selectIndex+1;
                long preProgress = (long) (curDetailList.get(nextIndex).startTime*1000L);
                exoPlayer.seekTo(preProgress);
            }
        });
        //切换倍速
        binding.textPlaySpeed10.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            if (!UserInfoManager.getInstance().isLogin()){
                if (ConceptBgPlaySession.getInstance().isTempData()){
                    pauseTempAudio();
                }else {
                    pauseAudio(false);
                }
                LoginUtil.startToLogin(getActivity());
                return;
            }

            if (!UserInfoManager.getInstance().isVip()){
                if (ConceptBgPlaySession.getInstance().isTempData()){
                    pauseTempAudio();
                }else {
                    pauseAudio(false);
                }
                ExpandKt.goSomeAction(getActivity(),"调速功能");
            }else {
                // TODO: 2024/7/11 李涛私聊：仅用于一个用户，发版的不能改动
                String[] speedArray = new String[]{"0.5x", "0.75x", "1.0x", "1.1x","1.2x","1.3x","1.4x","1.5x", "2.0x"};
//                String[] speedArray = new String[]{"0.5x", "0.75x", "1.0x", "1.5x", "2.0x"};

                new AlertDialog.Builder(getActivity())
                        .setTitle("调速功能")
                        .setItems(speedArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //设置调速
                                float speed = Float.parseFloat(speedArray[which].replace("x",""));
                                ConfigManager.Instance().putFloat(playSpeed, speed);
                                exoPlayer.setPlaybackSpeed(speed);
                                binding.textPlaySpeed10.setText("倍速 " + speed + "x");
                                if (speed == 1.99f){
                                    binding.textPlaySpeed10.setText("倍速 2.0x");
                                }
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
        //切换滚动
        binding.reSync.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            if (isSyncText){
                isSyncText = false;
                binding.ivSync.setImageResource(R.drawable.icon_unsync);
            }else {
                isSyncText = true;
                binding.ivSync.setImageResource(R.drawable.icon_sync);
            }
        });
        //切换设置界面
        binding.functionButton.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            if (binding.llMoreFunction.getVisibility() == View.VISIBLE){
                binding.llMoreFunction.setVisibility(View.GONE);
            }else {
                binding.llMoreFunction.setVisibility(View.VISIBLE);
            }
        });
        //切换播放/暂停
        binding.videoPlay.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            if (exoPlayer!=null){
                if (exoPlayer.isPlaying()){
                    if (ConceptBgPlaySession.getInstance().isTempData()){
                        pauseTempAudio();
                    }else {
                        pauseAudio(false);
                    }
                }else {
                    if (ConceptBgPlaySession.getInstance().isTempData()){
                        if (!isCanPlay){
                            ToastUtil.showToast(getActivity(),"正在加载音频内容，请稍后");
                            return;
                        }

                        playTempAudio(null);
                    }else {
                        if (!isCanPlay || ConceptBgPlayManager.getInstance().getPlayService().isPrepare()){
                            ToastUtil.showToast(getActivity(),"正在加载音频内容，请稍后");
                            return;
                        }

                        playAudio(null);
                    }
                }
            }
        });
        //切换播放模式
        binding.reOneVideo.setOnClickListener(v->{
            if (isFastClick()){
                return;
            }

            //获取并设置
            int playMode = ConfigManager.Instance().loadInt(mode,1);
            playMode = (playMode+1)%3;
            ConfigManager.Instance().putInt(mode,playMode);
            switchPlayMode();
            //根据类型显示
            if (playMode == 0) {
                CustomToast.showToast(getActivity(), R.string.study_repeatone, 1000);
            } else if (playMode == 1) {
                CustomToast.showToast(getActivity(), R.string.study_follow, 1000);
            } else if (playMode == 2) {
                CustomToast.showToast(getActivity(), R.string.study_random, 1000);
            }
        });
        //进度条
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser){
//                    //显示的进度
//                    long showProgress = progress*1000L;
//                    //设置播放器
//                    exoPlayer.seekTo(showProgress);
//                    // TODO: 2023/11/1  这里要求不再跳动
//
//                    //设置时间
//                    binding.curTime.setText(transPlayTime(showProgress/1000L));
//                    //设置文本
//                    binding.textCenter.snyParagraph(subtitleSum.getParagraph(showProgress/1000.0f));
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (ConceptBgPlaySession.getInstance().isTempData()){
                    pauseTempAudio();
                }else {
                    pauseAudio(false);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //显示的进度
                long showProgress = seekBar.getProgress();
                //设置播放器
                exoPlayer.seekTo(showProgress);
                //开始播放
//                playAudio(null);
            }
        });
    }

    /*****************************数据**************************/
    private void checkData(){
        //显示数据
        curVoa = VoaDataManager.Instace().voaTemp;
        if (curVoa.lessonType.equals(TypeLibrary.BookType.conceptJunior)){
            curDetailList = new VoaDetailYouthOp(getActivity()).getVoaDetailByVoaid(curVoa.voaId);
        }else {
            curDetailList = new VoaDetailOp(getActivity()).findDataByVoaId(curVoa.voaId);
        }
        contentAdapter.refreshData(curDetailList);
        listenStartTime = System.currentTimeMillis();

        //设置到本地数据中
        VoaDataManager.getInstance().voaDetailsTemp = curDetailList;

        if (ConceptBgPlaySession.getInstance().isTempData()){
            binding.reOneVideo.setVisibility(View.GONE);
        }else {
            binding.reOneVideo.setVisibility(View.VISIBLE);
        }

        //临时数据和非临时数据处理
        if (ConceptBgPlaySession.getInstance().isTempData()){
            //临时数据处理，统统直接播放即可
            if (TextUtils.isEmpty(getLocalSoundPath())){
                //获取网络链接
                String soundUrl = getRemoteSoundPath();
                playTempAudio(soundUrl);
            }else {
                //本地播放
                playTempAudio(getLocalSoundPath());
            }
        }else {
            //这里进行特殊处理：如果是当前的类型并且已经存在操作，则按照当前状态处理；如果不是，则重新加载
            if (ConceptBgPlaySession.getInstance().getCurData()==null
                    ||VoaDataManager.getInstance().voaTemp.position != ConceptBgPlaySession.getInstance().getPlayPosition()
                    ||VoaDataManager.getInstance().voaTemp.voaId != ConceptBgPlaySession.getInstance().getCurData().voaId
                    ||!VoaDataManager.getInstance().voaTemp.lessonType.equals(ConceptBgPlaySession.getInstance().getCurData().lessonType)
                    ||ConceptBgPlaySession.getInstance().isTempData()){
                //全新处理

                //保存在新的后台播放的会话中
                ConceptBgPlaySession.getInstance().setPlayPosition(VoaDataManager.getInstance().voaTemp.position);

                //先获取本地音频，然后判断文件是否存在
                String filePath = getLocalSoundPath();
                File audioFile = new File(filePath);
                //播放音频
                if (TextUtils.isEmpty(filePath) || !audioFile.exists()){
                    //获取网络链接
                    String soundUrl = getRemoteSoundPath();
                    playAudio(soundUrl);
                }else {
                    //本地播放
                    playAudio(filePath);
                }
                return;
            }


            if (ConceptBgPlaySession.getInstance().getPlayPosition() == VoaDataManager.getInstance().voaTemp.position){
                //获取当前状态进行处理

                //获取当前状态-播放/暂停（这里暂时处理播放状态，外面暂停的话这里也进行播放）
                if (exoPlayer!=null&&exoPlayer.isPlaying()){
                    playAudio(null);
                }else {
                    //这里增加进度数据显示
                    binding.curTime.setText(transPlayTime(exoPlayer.getCurrentPosition()));
                    binding.totalTime.setText(transPlayTime(exoPlayer.getDuration()));
                    binding.seekBar.setMax((int) exoPlayer.getDuration());
                    binding.seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    int curSelectIndex = getCurShowTextIndex();
                    contentAdapter.refreshIndex(curSelectIndex);
                    if (isSyncText){
                        ((CenterLinearLayoutManager)binding.recyclerView.getLayoutManager()).smoothScrollToPosition(binding.recyclerView,new RecyclerView.State(),curSelectIndex);
                    }

                    pauseAudio(false);
                }
            }
        }
    }

    public void refreshData(){
        checkData();
    }

    /*******************************播放器***********************/
    //开始播放
    private void playAudio(String urlOrPath){
        if (!TextUtils.isEmpty(urlOrPath)){

            MediaItem mediaItem = null;
            if (urlOrPath.startsWith("http")){
                mediaItem = MediaItem.fromUri(urlOrPath);
            }else {
                //本地加载
                Uri uri = Uri.fromFile(new File(urlOrPath));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    uri = FileProvider.getUriForFile(getActivity(),getResources().getString(R.string.file_provider_name_personal),new File(urlOrPath));
                }
                mediaItem = MediaItem.fromUri(uri);
            }
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }else {
            Log.d("播放音频hhhh", "playAudio: ");

            if (ConceptBgPlayManager.getInstance().getPlayService().isPrepare()){
                ToastUtil.showToast(getActivity(),"正在加载音频内容，请稍后～");
                return;
            }

            if (exoPlayer!=null&&!exoPlayer.isPlaying()){
                if (exoPlayer.getCurrentPosition()>=exoPlayer.getDuration()){
                    exoPlayer.seekTo(0);
                }

                exoPlayer.play();
            }
            //图标文本设置
            binding.videoPlay.setImageResource(R.drawable.image_pause);
            //倒计时
            startTimer();
            //外部控制
            if (!ConceptBgPlaySession.getInstance().isTempData()){
                Log.d("后台音频播放", "处理方式3");
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_play));
            }
            //通知栏控制
            Log.d("后台音频播放", "操作2");
            ConceptBgPlayManager.getInstance().getPlayService().showNotification(false,true);

            if (!isCanPlay){
                pauseAudio(false);
            }
        }
    }

    //暂停播放
    private void pauseAudio(boolean isEnd){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
        //图标
        if (binding!=null){
            binding.videoPlay.setImageResource(R.drawable.image_play);
        }
        //倒计时
        stopTimer();
        //外部控制
        if (!ConceptBgPlaySession.getInstance().isTempData()){
            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
            //刷新外面的显示数据
            ConceptHomeRefreshUtil.getInstance().setRefreshState(true);
        }
        //通知栏控制
        ConceptBgPlayManager.getInstance().getPlayService().showNotification(false,false);


        /***********************学习报告操作*******************/
        //临时数据
        if (ConceptBgPlaySession.getInstance().isTempData()){
            if (isEnd){
                exoPlayer.seekTo(0);
                ConceptBgPlayManager.getInstance().getPlayService().setPrepare(false);
            }
            return;
        }

        //非临时数据
        if (UserInfoManager.getInstance().isLogin()){
            if (isEnd){
                //是否展示学习报告
                boolean isShowReport = ConfigManager.Instance().getSendListenReport();
                Log.d("显示听力学习报告", "是否显示报告："+isShowReport);

                submitReport(true,isShowReport);
            }else {
                //不用展示学习报告，直接提交就行
                submitReport(false,false);
            }
        }else {
            if (isEnd){
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_switch));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(ConceptBgPlayEvent event){
        //播放
        if (event.getShowType().equals(ConceptBgPlayEvent.event_audio_play)){
            //直接显示进度
            if (exoPlayer!=null&&!ConceptBgPlayManager.getInstance().getPlayService().isPrepare()){
                binding.totalTime.setText(transPlayTime(exoPlayer.getDuration()));
                binding.seekBar.setMax((int) exoPlayer.getDuration());
            }

            //这里是自动播放按钮的处理
            if (!ConfigManager.Instance().loadAutoPlay()||!isCanPlay){
                pauseAudio(false);
                return;
            }

            playAudio(null);
        }

        //暂停
        if (event.getShowType().equals(ConceptBgPlayEvent.event_audio_pause)){
            pauseAudio(false);
        }

        //播放完成
        if (event.getShowType().equals(ConceptBgPlayEvent.event_audio_completeFinish)){
            //重置进度
            pauseAudio(true);
            //切换下一个
//            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_switch));
        }

        //数据异常
        if (event.getShowType().equals(ConceptBgPlayEvent.event_data_error)){
            //显示弹窗
            new AlertDialog.Builder(getActivity())
                    .setTitle("播放器异常")
                    .setMessage(event.getShowMsg())
                    .setPositiveButton("确定",null)
                    .create().show();
        }
    }

    /*******************************倒计时***********************/
    private final Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case time_bgPlay:
                    //播放
                    //当前进度
                    String curTimeStr = transPlayTime(exoPlayer.getCurrentPosition());
                    binding.curTime.setText(curTimeStr);
                    //总时间
                    String totalTimeStr = transPlayTime(exoPlayer.getDuration());
                    binding.totalTime.setText(totalTimeStr);
                    //进度显示
                    binding.seekBar.setMax((int) exoPlayer.getDuration());
                    binding.seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    //文章显示
                    int curSelectIndex = getCurShowTextIndex();
                    contentAdapter.refreshIndex(curSelectIndex);
                    if (isSyncText){
                        ((CenterLinearLayoutManager)binding.recyclerView.getLayoutManager()).smoothScrollToPosition(binding.recyclerView,new RecyclerView.State(),curSelectIndex);
                    }

                    //这里处理下ab点播放
                    if (aPositon!=0&&bPosition!=0){
                        if (exoPlayer.getCurrentPosition()>=bPosition){
                            exoPlayer.seekTo(aPositon);
                        }
                    }

                    Log.d("晋时期进度显示", binding.seekBar.getMax()+"---"+binding.seekBar.getProgress());

                    //循环加载
                    timeHandler.sendEmptyMessageDelayed(time_bgPlay,500L);
                    break;
            }
        }
    };

    private static final int time_bgPlay = 0;
    private static final String bgPlayTag = "bgPlayTag";

    private void startTimer(){
//        RxTimer.getInstance().cancelTimer(bgPlayTag);
//        RxTimer.getInstance().multiTimerInMain(bgPlayTag, 0, 500L, new RxTimer.RxActionListener() {
//            @Override
//            public void onAction(long number) {
//                //当前进度
//                String curTimeStr = transPlayTime(exoPlayer.getCurrentPosition());
//                binding.curTime.setText(curTimeStr);
//                //总时间
//                String totalTimeStr = transPlayTime(exoPlayer.getDuration());
//                binding.totalTime.setText(totalTimeStr);
//                //进度显示
//                binding.seekBar.setMax((int) exoPlayer.getDuration());
//                binding.seekBar.setProgress((int) exoPlayer.getCurrentPosition());
//                //文章显示
//                int curSelectIndex = getCurShowTextIndex();
//                contentAdapter.refreshIndex(curSelectIndex);
//                if (isSyncText){
//                    ((CenterLinearLayoutManager)binding.recyclerView.getLayoutManager()).smoothScrollToPosition(binding.recyclerView,new RecyclerView.State(),curSelectIndex);
//                }
//
//                //这里处理下ab点播放
//                if (aPositon!=0&&bPosition!=0){
//                    if (exoPlayer.getCurrentPosition()>=bPosition){
//                        exoPlayer.seekTo(aPositon);
//                    }
//                }
//
//                Log.d("晋时期进度显示", binding.seekBar.getMax()+"---"+binding.seekBar.getProgress());
//            }
//        });
        timeHandler.sendEmptyMessage(time_bgPlay);
    }

    private void stopTimer(){
//        RxTimer.getInstance().cancelTimer(bgPlayTag);
        timeHandler.removeMessages(time_bgPlay);
    }

    /******************************其他操作**********************/
    //将当前播放暂停
    public void setPlayStatus(boolean isPlay){
        this.isCanPlay = isPlay;
        //暂停音频播放
        if (!isPlay){
            if (ConceptBgPlaySession.getInstance().isTempData()){
                pauseTempAudio();
            }else {
                pauseAudio(false);
            }
        }
    }

    //设置文本标号
    public void setTextSize(int textSize){
        contentAdapter.refreshTextSize(textSize);
    }

    //转换时间显示
    private String transPlayTime(long playTime){
        //这里处理下时间
        playTime = playTime/1000L;

        long minute = playTime/60;
        long second = playTime%60;

        StringBuffer buffer = new StringBuffer();
        if (minute>=10){
            buffer.append(minute);
        }else {
            buffer.append("0").append(minute);
        }
        buffer.append(":");
        if (second>=10){
            buffer.append(second);
        }else {
            buffer.append("0").append(second);
        }
        return buffer.toString();
    }

    //获取当前章节的音频本地路径
    private String getLocalSoundPath() {
        String localPath = "";

        // TODO: 2025/4/17 在android 15上不再主动请求存储权限
        if (!PermissionFixUtil.isCanUseExternalPermission(getActivity())) {
            return localPath;
        }

        //这里不获取当前的数据，而是获取数据中的类型
        /*switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptJunior:
            default:
                // 美音原文音频的存放路径
                String pathString = Constant.videoAddr + VoaDataManager.getInstance().voaTemp.voaId + Constant.append;
                File fileTemp = new File(pathString);
                if (fileTemp.exists()) {
                    localPath =  pathString;
                }
                break;
            case TypeLibrary.BookType.conceptFourUK:
                // 英音原文音频的存放路径
                String pathStringEng = Constant.videoAddr + VoaDataManager.getInstance().voaTemp.voaId + "_B" + Constant.append;
                File fileTempEng = new File(pathStringEng);
                if (fileTempEng.exists()) {
                    localPath = pathStringEng;
                }
                break;
        }*/

        //更换路径获取方式
        String pathString = FilePathUtil.getHomeAudioPath(VoaDataManager.getInstance().voaTemp.voaId,VoaDataManager.getInstance().voaTemp.lessonType);
        File file = new File(pathString);
        if (file.exists()){
            localPath = pathString;
        }

        Log.d("当前播放路径", "本地路径：--"+localPath);

        return localPath;
    }

    //获取当前章节的音频网络路径
    private String getRemoteSoundPath(){
        String soundUrl = null;
        //这里针对会员和非会员不要修改，测试也不要修改
        if (UserInfoManager.getInstance().isVip()){
            soundUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
        }else {
            soundUrl=Constant.sound;
        }

//        switch (ConfigManager.Instance().getBookType()) {
        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            default:
                //美音
                soundUrl = soundUrl
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "_"
                        + VoaDataManager.getInstance().voaTemp.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptFourUK: //英音
                soundUrl = soundUrl
                        + "british/"
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "/"
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "_"
                        + VoaDataManager.getInstance().voaTemp.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptJunior:
                soundUrl = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
                        + VoaDataManager.getInstance().voaTemp.voaId
                        + "/"
                        + VoaDataManager.getInstance().voaTemp.voaId
                        + Constant.append;
                break;
        }

        Log.d("当前播放路径", "远程路径：--"+soundUrl);

        return soundUrl;
    }

    //切换播放模式
    private void switchPlayMode(){
        int playMode = ConfigManager.Instance().loadInt(mode, 1);
        if (playMode == 0) {
            binding.oneVideo.setImageDrawable(getResources().getDrawable(R.drawable.play_this));
        } else if (playMode == 1) {
            binding.oneVideo.setImageDrawable(getResources().getDrawable(R.drawable.play_next));
        } else if (playMode == 2) {
            binding.oneVideo.setImageDrawable(getResources().getDrawable(R.drawable.play_random));
        }
    }

    /*********************单词查询****************/
    //单词查询的弹窗
//    private SearchWordDialog searchWordDialog;
    //显示查询弹窗
    /*private void showSearchWordDialog(String word){
        pauseAudio(false);

        //更换成界面
        searchWordDialog = new SearchWordDialog(getActivity(), word, new SearchWordDialog.OnWordCollectCallBack() {
            @Override
            public void onCollect(String word, VoaWord2 word2) {
                if (word2==null){
                    if (collectWordMap.get(word)!=null){
                        collectWordMap.remove(word2);
                    }
                }else {
                    collectWordMap.put(word,word2);
                }
            }
        });
        searchWordDialog.create();
        searchWordDialog.show();
    }*/

    //关闭查询弹窗
    /*private void closeSearchWordDialog(){
        if (searchWordDialog!=null&&searchWordDialog.isShowing()){
            searchWordDialog.dismiss();
        }
    }*/

    /******************************************学习记录*********************************/
    //提交学习记录
    /*private void startSubmit(boolean isEnd,boolean isShowReport){
        Voa curVoa = VoaDataManager.getInstance().voaTemp;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);// 设置日期格式
        String endData = df.format(new Date());
        String startData = df.format(listenStartTime);
        new Thread(new ListenStudyReportThread(curVoa.voaId,
                getCurShowTextIndex(),
                startData,endData,wordsCount(endData),isEnd,true,isShowReport)).start();
    }*/

    private int words;

    public void getCounts(List<VoaDetail> details) {
        if (details != null) {
            words = 0;
            for (VoaDetail detail : details) {
                words += getWordCounts(detail.sentence);
                Log.e("Tag-content:", detail.sentence + "count:" + getWordCounts(detail.sentence));
            }
        }
    }

    public int getWordCounts(String content) {
        return content.split(" ").length;
    }

    //计算提交时间段的单词数
    private int wordsCount(String endTime) {
        int wordNum = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 设置日期格式

        try {
            int timeAll = (int) ((df.parse(endTime).getTime() - listenStartTime));
            wordNum = (int) (timeAll * words / exoPlayer.getDuration());
            if (wordNum > words) {
                wordNum = words;
            }
        } catch (Exception e) {
            e.printStackTrace();
            wordNum = 0;
        }

        return wordNum;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.reward_refresh_toast)){
            //显示toast弹窗

            //关闭加载
            closeLoading();

            //显示弹窗
            showReadReportDialog(event.getTips());

            //刷新用户信息并填充
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
        }

        if (event.getType().equals(TypeLibrary.RefreshDataType.read_refresh_tips)){
            //原文加载弹窗
            showLoading();
        }
    }

    /***************************加载弹窗***************************/
    private LoadingDialog loadingDialog;

    private void showLoading(){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg("正在加载学习报告～");
        loadingDialog.show();
    }

    private void closeLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //显示学习报告
    private ListenStudyReportDialog reportDialog = null;
    private String reportTime = "reportTime";
    //显示学习报告弹窗
    private void showReadReportDialog(String reward){
        if (getActivity()==null||getActivity().isFinishing()||getActivity().isDestroyed()){
            return;
        }

        //当前课程
        Voa curVoa = VoaDataManager.Instace().voaTemp;
        //当前收藏的单词
        List<VoaWord2> collectWordList = new ArrayList<>();
        for (String key:collectWordMap.keySet()){
            collectWordList.add(collectWordMap.get(key));
        }
        //当前课程的单词
        List<VoaWord2> lessonWordList = new ArrayList<>();
        if (curVoa.lessonType.equals(TypeLibrary.BookType.conceptJunior)){
            lessonWordList = WordChildDBManager.getInstance().findDataByBookIdAndVoaId(String.valueOf(curVoa.category),String.valueOf(curVoa.voaId));
        }else {
            lessonWordList = new VoaWordOp(getActivity()).findDataByVoaId(curVoa.voaId);
        }

        reportDialog = ListenStudyReportDialog.getInstance()
                .init(getActivity())
                .setData(reward, lessonWordList, collectWordList, new DialogCallBack() {
                    @Override
                    public void callback() {
                        if (!ConceptBgPlaySession.getInstance().isTempData()){
                            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_switch));
                        }else {
                            ConceptBgPlayManager.getInstance().getPlayService().setPrepare(false);
                            exoPlayer.seekTo(0);
                        }
                    }
                })
                .setOnDialogTouchListener(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        RxTimer.getInstance().cancelTimer(reportTime);
                    }
                })
                .prepare()
                .show();
        RxTimer.getInstance().timerInMain(reportTime, 5000L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                RxTimer.getInstance().cancelTimer(reportTime);
                closeReadReportDialog();
            }
        });
    }

    //关闭学习报告弹窗
    private void closeReadReportDialog(){
        if (reportDialog!=null){
            reportDialog.closeSelf();
        }
    }

    /****************************************广告***************************************/

    //请求广告
    private void requestAd() {
        //设置统一的广告高度
        boolean isVip = UserInfoManager.getInstance().isVip();
        if (NetworkUtil.isConnected(getActivity()) && !isVip && AdInitManager.isShowAd()) {
            //显示界面
            binding.adLayout.getRoot().setVisibility(View.VISIBLE);
            //设置新的操作
            showBannerAd();
        }else {
            binding.adLayout.getRoot().setVisibility(View.GONE);
            stopAdTimer();
        }
    }

    private static final String adUpdate = "adUpdate";
    private long updateAdTime = 20*1000L;
    //开启广告定时器
    private void startAdTimer(){
        //先隐藏广告界面
        binding.adLayout.getRoot().setVisibility(View.GONE);
        //关闭定时器
        stopAdTimer();
        RxTimer.getInstance().multiTimerInMain(adUpdate, 0, updateAdTime, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //切换广告
                if (!AdShowUtil.Util.isPageExist(getActivity())){
                    return;
                }

                Log.d("刷新广告显示", "onAction: ");
                requestAd();
            }
        });
    }

    //关闭广告定时器
    private void stopAdTimer(){
        RxTimer.getInstance().cancelTimer(adUpdate);
    }

    //快速点击操作
    private long fastClickTime = 0;

    private boolean isFastClick(){
        if (System.currentTimeMillis() - fastClickTime <= 500L){
            return true;
        }

        fastClickTime = System.currentTimeMillis();
        return false;
    }

    //文章滚动操作
    private int getCurShowTextIndex(){
        if (exoPlayer==null){
            return 0;
        }

        long curProgress = exoPlayer.getCurrentPosition();

        if (curDetailList!=null&&curDetailList.size()>0){
            for (int i = 0; i < curDetailList.size(); i++) {
                VoaDetail detail = curDetailList.get(i);

                if (i == curDetailList.size()-1){
                    return i;
                }

                if (curProgress <= detail.endTime*1000L){
                    return i;
                }
            }
        }
        return 0;
    }

    //提交学习报告
    private Disposable listenReportDis;
    private void submitReport(boolean isEnd,boolean isShowReport){
        String endDate = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        int voaId = VoaDataManager.getInstance().voaTemp.voaId;
        if (VoaDataManager.getInstance().voaTemp.lessonType.equals(TypeLibrary.BookType.conceptFourUK)){
            voaId = voaId*10;
        }
        ConceptDataManager.submitConceptListenReport(listenStartTime,System.currentTimeMillis(),voaId,UserInfoManager.getInstance().getUserId(),isEnd,getCurShowTextIndex(),wordsCount(endDate))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Study_report>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        listenReportDis = d;
                    }

                    @Override
                    public void onNext(Study_report bean) {
                        if (bean!=null){
                            //重置开始时间
                            listenStartTime = System.currentTimeMillis();
                            //获取价格显示
                            String reward = bean.getReward();
                            double price = 0;
                            if (!TextUtils.isEmpty(reward)){
                                price = Integer.parseInt(reward);
                                price = BigDecimalUtil.trans2Double(price*0.01f);
                            }

                            if (isEnd){
                                Log.d("显示听力学习报告2", "是否显示报告："+isShowReport);

                                if (isShowReport){
                                    showReadReportDialog(String.valueOf(price));
                                }else {
                                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_switch));
                                }
                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
                            }
                        }else {
                            if (isEnd){
                                if (isShowReport){
                                    showReadReportDialog("");
                                }else {
                                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_switch));
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //提交失败，确认下一步
                        if (isEnd){
                            if (isShowReport){
                                showReadReportDialog("");
                            }else {
                                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_switch));
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        RxUtil.unDisposable(listenReportDis);
                    }
                });
    }

    /**************************************临时数据处理******************************/
    //初始化播放器
    private void initTempPlayer(){
        if (ConceptBgPlaySession.getInstance().isTempData()){
            exoPlayer = new ExoPlayer.Builder(getActivity()).build();
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    switch (playbackState){
                        case Player.STATE_READY:
                            //加载完成
                            playTempAudio(null);
                            break;
                        case Player.STATE_ENDED:
                            //播放完成
                            exoPlayer.seekTo(0);
                            pauseTempAudio();

                            // TODO: 2025/4/3 判断如果是练习题界面，则更新原文的数据库内容
                            if (UserInfoManager.getInstance().isLogin()){
                                ArticleRecordBean bean = new ArticleRecordBean();
                                bean.voa_id = curVoa.voaId;
                                bean.uid = UserInfoManager.getInstance().getUserId();
                                bean.is_finish = 1;
                                if (exoPlayer != null && exoPlayer.getDuration() > 0) {
                                    bean.total_time = (int) (exoPlayer.getDuration() / 1000L);
                                    bean.curr_time = (int) (exoPlayer.getDuration() / 1000L);
                                }
                                new ArticleRecordOp(getActivity()).updateData(bean);
                                //刷新数据
                                EventBus.getDefault().post(new PractiseLineEvent(PractiseLineEvent.event_listen));
                                //刷新原文进度
                                EventBus.getDefault().post(new RefreshDataEvent(StrLibrary.list));
                            }
                            break;
                    }
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    ToastUtil.showToast(getActivity(),"音频播放异常--"+error.getMessage());
                }
            });

            //关闭通知显示
            ConceptBgPlayManager.getInstance().getPlayService().showNotification(true,false);
        }
    }

    //播放
    private void playTempAudio(String urlOrPath){
        if (ConceptBgPlaySession.getInstance().isTempData()){
            if (!TextUtils.isEmpty(urlOrPath)){

                MediaItem mediaItem = null;
                if (urlOrPath.startsWith("http")){
                    mediaItem = MediaItem.fromUri(urlOrPath);
                }else {
                    //本地加载
                    Uri uri = Uri.fromFile(new File(urlOrPath));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        uri = FileProvider.getUriForFile(getActivity(),getResources().getString(R.string.file_provider_name_personal),new File(urlOrPath));
                    }
                    mediaItem = MediaItem.fromUri(uri);
                }
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
            }else {
                if (exoPlayer!=null&&!exoPlayer.isPlaying()){
                    if (exoPlayer.getCurrentPosition()>=exoPlayer.getDuration()){
                        exoPlayer.seekTo(0);
                    }

                    exoPlayer.play();
                }
                //图标文本设置
                if (binding!=null){
                    binding.videoPlay.setImageResource(R.drawable.image_pause);
                }
                //倒计时
                startTimer();

                if (!isCanPlay){
                    pauseTempAudio();
                }
            }
        }
    }

    //暂停
    private void pauseTempAudio(){
        if (ConceptBgPlaySession.getInstance().isTempData()){
            if (exoPlayer!=null&&exoPlayer.isPlaying()){
                exoPlayer.pause();
            }
            //显示按钮
            if (binding!=null){
                binding.videoPlay.setImageResource(R.drawable.image_play);
            }
            //停止计时器
            stopTimer();

            // TODO: 2025/4/3 如果是练习题界面，则刷新数据库数据
            if (UserInfoManager.getInstance().isLogin()){
                ArticleRecordBean recordBean = new ArticleRecordOp(getActivity()).getData(curVoa.voaId);
                if (recordBean!=null){
                    if (recordBean.is_finish==0){
                        ArticleRecordBean bean = new ArticleRecordBean();
                        bean.voa_id = curVoa.voaId;
                        bean.uid = UserInfoManager.getInstance().getUserId();
                        bean.is_finish = 0;
                        if (exoPlayer != null && exoPlayer.getDuration() > 0) {
                            bean.total_time = (int) (exoPlayer.getDuration() / 1000L);
                            bean.curr_time = (int) (exoPlayer.getCurrentPosition() / 1000L);
                        }
                        new ArticleRecordOp(getActivity()).updateData(bean);
                        //刷新数据
                        EventBus.getDefault().post(new PractiseLineEvent(PractiseLineEvent.event_listen));
                        //刷新原文进度
                        EventBus.getDefault().post(new RefreshDataEvent(StrLibrary.list));
                    }
                }else {
                    ArticleRecordBean bean = new ArticleRecordBean();
                    bean.voa_id = curVoa.voaId;
                    bean.uid = UserInfoManager.getInstance().getUserId();
                    bean.is_finish = 0;
                    if (exoPlayer != null && exoPlayer.getDuration() > 0) {
                        bean.total_time = (int) (exoPlayer.getDuration() / 1000L);
                        bean.curr_time = (int) (exoPlayer.getCurrentPosition() / 1000L);
                    }
                    new ArticleRecordOp(getActivity()).updateData(bean);
                    //刷新数据
                    EventBus.getDefault().post(new PractiseLineEvent(PractiseLineEvent.event_listen));
                    //刷新原文进度
                    EventBus.getDefault().post(new RefreshDataEvent(StrLibrary.list));
                }
            }
        }
    }

    /****************************banner广告点击**************************/
    //点击banner广告结果
    public void showClickAdResultData(boolean isSuccess, String showMsg) {
        //直接显示信息即可
        com.iyuba.core.common.util.ToastUtil.showToast(getActivity(),showMsg);

        if (isSuccess){
            EventBus.getDefault().post(new RefreshUserInfoEvent());
        }
    }

    /****************************banner广告展示*****************************/
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //显示的界面
    private AdBannerViewBean bannerViewBean = null;
    private void showBannerAd(){
        //请求广告
        if (bannerViewBean==null){
            bannerViewBean = new AdBannerViewBean(binding.adLayout.iyubaSdkAdLayout, binding.adLayout.webAdLayout, binding.adLayout.webAdImage, binding.adLayout.webAdClose,binding.adLayout.webAdTips, new AdBannerShowManager.OnAdBannerShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String adType) {

                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    pauseAudio(false);

                    if (isJumpByUserClick){
                        if (TextUtils.isEmpty(jumpUrl)){
                            com.iyuba.core.common.util.ToastUtil.showToast(getActivity(),"暂无内容");
                            return;
                        }

                        Intent intent = new Intent();
                        intent.setClass(getActivity(), WebActivity.class);
                        intent.putExtra("url", jumpUrl);
                        startActivity(intent);
                    }

                    //点击广告获取奖励
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;


                        String fixShowType = AdUploadUtil.Param.AdShowPosition.show_spread;
                        String fixAdType = AdUploadUtil.Util.transShowAdTypeToNetAdType(adType);
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                showClickAdResultData(isSuccess, showMsg);
                            }
                        });
                    }
                }

                @Override
                public void onAdClose(String adType) {
                    binding.adLayout.getRoot().setVisibility(View.GONE);
                    stopAdTimer();
                    AdBannerShowManager.getInstance().stopBannerAd();
                }

                @Override
                public void onAdError(String adType) {

                }
            });
            AdBannerShowManager.getInstance().setShowData(getActivity(),bannerViewBean);
        }
        AdBannerShowManager.getInstance().showBannerAd();
        //重置数据
        isGetRewardByClickAd = false;
    }
}
