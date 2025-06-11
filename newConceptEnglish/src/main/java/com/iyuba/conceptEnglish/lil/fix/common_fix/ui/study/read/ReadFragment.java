package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.read;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.WebActivity;
import com.iyuba.conceptEnglish.databinding.FragmentFixReadBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshUserInfoEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.studyReport.StudyReportManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.banner.AdBannerShowManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.banner.AdBannerViewBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.CenterLinearLayoutManager;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlayManager;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlaySession;
import com.iyuba.conceptEnglish.lil.fix.novel.bgService.NovelBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.novel.bgService.NovelBgPlayManager;
import com.iyuba.conceptEnglish.lil.fix.novel.bgService.NovelBgPlaySession;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.widget.dialog.DialogCallBack;
import com.iyuba.conceptEnglish.widget.dialog.NewListenStudyReportDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.util.NetStateUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.sdk.other.NetworkUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title: 原文界面
 * @date: 2023/5/22 18:24
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ReadFragment extends BaseViewBindingFragment<FragmentFixReadBinding> implements ReadView {

    //播放类型
    private static final String mode = "mode";
    //中英文切换
    private static final String showChinese = "showChinese";
    //倍速
    private static final String playSpeed = "playSpeed";

    //上下文
    private Context mContext;

    private String bookType;
    private String voaId;
    private int position = -1;
    private String bookId;
    private String unitId;

    private ReadPresenter presenter;
    private ReadAdapter readAdapter;

    //章节数据
    private BookChapterBean chapterBean;
    //详情数据
    private List<ChapterDetailBean> list;
    //播放器
    private ExoPlayer exoPlayer;
    //音频播放地址
    private String playAudioUrl = null;
    //是否可以播放
    private boolean isCanPlay = true;

    //单词查询弹窗
//    private SearchWordDialog searchWordDialog;
    //是否展示界面
    private boolean isShowView = false;
    //是否文字滚动
    private boolean isSyncText = true;

    //ab点次数
    private long abState = 0;
    //ab开始点
    private long abStartPosition = 0;
    //ab结束点
    private long abEndPosition = 0;

    //是否是第一次提交学习报告
    private boolean isFirstReport = true;
    //是否已经加载
    private boolean isLazyLoad = false;
    //学习报告的开始时间
    private long reportStartTime = 0;
    //是否最终学习报告提交了
    private boolean isSubmitReport = false;

    public static ReadFragment getInstance(String types, String voaId,int position,String bookId,String unitId) {
        ReadFragment fragment = new ReadFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaId, voaId);
        bundle.putInt(StrLibrary.position,position);
        bundle.putString(StrLibrary.bookId,bookId);
        bundle.putString(StrLibrary.id,unitId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setContext(Context context){
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        bookType = getArguments().getString(StrLibrary.types);
        Log.d("显示类型", bookType);

        voaId = getArguments().getString(StrLibrary.voaId);
        position = getArguments().getInt(StrLibrary.position,-1);
        bookId = getArguments().getString(StrLibrary.bookId);
        unitId = getArguments().getString(StrLibrary.id);

        presenter = new ReadPresenter();
        presenter.attachView(this);

        reportStartTime = System.currentTimeMillis();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();
        initClick();
        initUi();

        refreshData();

        //开启广告计时器
        if (!UserInfoManager.getInstance().isVip()
                &&!AdBlocker.getInstance().shouldBlockAd()){
            startAdTimer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isShowView = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isShowView = false;

//        pausePlay();
//        closeSearchWordDialog();
    }

    /**************初始化***************/
    private void initList() {
        binding.wordCardLayout.setVisibility(View.GONE);
        binding.audioPlay.setBackgroundResource(R.drawable.image_play);
        binding.llMoreFunction.setVisibility(View.GONE);

        readAdapter = new ReadAdapter(getActivity(), new ArrayList<>());
        CenterLinearLayoutManager manager = new CenterLinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(readAdapter);
        readAdapter.setOnWordSearchListener(new ReadAdapter.onWordSearchListener() {
            @Override
            public void onWordSearch(String selectText) {
                pauseAudio(false);
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                if (!TextUtils.isEmpty(selectText)) {
                    //先处理下数据
                    selectText = filterWord(selectText);

                    if (selectText.matches("^[a-zA-Z]*")){
                        //更换成界面显示
                        NewSearchActivity.start(getActivity(),selectText);
                    }else {
                        CustomToast.showToast(getActivity(), R.string.play_please_take_the_word, 1000);
                    }
                } else {
                    CustomToast.showToast(getActivity(), R.string.play_please_take_the_word, 1000);
                }
            }
        });

        //设置文本字体大小
        int level = ConfigManager.Instance().getfontSizeLevel();
        readAdapter.refreshShowTextSize(16+2*level);
    }

    private void initClick() {
        //播放进度条
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    exoPlayer.seekTo(progress);
                    isSubmitReport = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress()>abEndPosition){
                    abState = 0;
                    abStartPosition = 0;
                    abEndPosition = 0;
                }

                playAudio(null);
            }
        });
        //AB点播放
        binding.abPlay.setOnClickListener(v -> {
            if (exoPlayer == null||!isCanPlay) {
                ToastUtil.showToast(getActivity(),"播放器未初始化～");
                return;
            }

            abState++;
            if (abState%3==1){
                //a点记录
                abStartPosition = exoPlayer.getCurrentPosition();
                CustomToast.showToast(getActivity(), R.string.study_ab_a, 2000);
            }else if (abState%3==2){
                //b点播放
                abEndPosition = exoPlayer.getCurrentPosition();
                CustomToast.showToast(getActivity(), R.string.study_ab_b, 1000);
            }else if (abState%3==0){
                //停止循环
                abStartPosition = 0;
                abEndPosition = 0;
                CustomToast.showToast(getActivity(), "区间播放已取消", 1000);
            }
        });
        //文本滚动
        binding.textRoll.setOnClickListener(v -> {
            if (isSyncText){
                binding.textRollPic.setImageResource(R.drawable.icon_unsync);
                ToastUtil.showToast(getActivity(),"文本自动滚动关闭");
            }else {
                binding.textRollPic.setImageResource(R.drawable.icon_sync);
                ToastUtil.showToast(getActivity(),"文本自动滚动开启");
            }
            isSyncText = !isSyncText;
        });
        //播放倍速
        binding.playSpeed.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()){
                showAbilityDialog(true,"调速功能");
                return;
            }

            if (!UserInfoManager.getInstance().isLogin()){
                pauseAudio(false);
                LoginUtil.startToLogin(getActivity());
                return;
            }

            if (!UserInfoManager.getInstance().isVip()) {
                pauseAudio(false);
                showAbilityDialog(false,"调速功能");
                return;
            }

            //弹出倍速
            showSpeedDialog();
        });
        //播放类型
        binding.playMode.setOnClickListener(v -> {
            //获取并设置
            int playMode = ConfigManager.Instance().loadInt(mode,1);
            playMode = (playMode+1)%3;
            ConfigManager.Instance().putInt(mode,playMode);
            //根据类型显示
            if (playMode == 0) {
                binding.playModePic.setImageResource(R.drawable.play_this);
                CustomToast.showToast(getActivity(), R.string.study_repeatone, 1000);
            } else if (playMode == 1) {
                binding.playModePic.setImageResource(R.drawable.play_next);
                CustomToast.showToast(getActivity(), R.string.study_follow, 1000);
            } else if (playMode == 2) {
                binding.playModePic.setImageResource(R.drawable.play_random);
                CustomToast.showToast(getActivity(), R.string.study_random, 1000);
            }
        });
        //切换中英文
        binding.textSwitch.setOnClickListener(v -> {
            boolean isChinese = ConfigManager.Instance().loadBoolean(showChinese);
            if (isChinese){
                ConfigManager.Instance().putBoolean(showChinese,false);
                binding.textSwitchPic.setImageResource(R.drawable.show_chinese);
                VoaDataManager.Instace().changeLanguage(true);
            }else {
                ConfigManager.Instance().putBoolean(showChinese,true);
                binding.textSwitchPic.setImageResource(R.drawable.show_chinese_selected);
                VoaDataManager.Instace().changeLanguage(false);
            }
            readAdapter.refreshShowTextType(isChinese?TypeLibrary.TextShowType.EN:TypeLibrary.TextShowType.ALL);
        });
        //课程音频播放和暂停
        binding.audioPlay.setOnClickListener(v -> {
            if (!NetworkUtil.isConnected(getActivity())){
                ToastUtil.showToast(getActivity(),"请链接网络后播放音频～");
                return;
            }

            if (!isCanPlay) {
                ToastUtil.showToast(getActivity(), "正在加载音频文件～");
                return;
            }

            if (exoPlayer != null) {
                if (exoPlayer.isPlaying()) {
                    pauseAudio(false);
                } else {
                    playAudio(null);
                }
            }
        });
        //上一个句子
        binding.prefixSong.setOnClickListener(v -> {
            int selectIndex = readAdapter.getSelectIndex();
            if (selectIndex==0){
                ToastUtil.showToast(getActivity(),"当前已经是第一个了");
            }else {
                int preIndex = selectIndex-1;
                long preProgress = (long) (list.get(preIndex).getTiming()*1000L);
                exoPlayer.seekTo(preProgress);

                isSubmitReport = false;
            }
        });
        //下一个句子
        binding.nextSong.setOnClickListener(v -> {
            int selectIndex = readAdapter.getSelectIndex();
            if (selectIndex==list.size()-1){
                ToastUtil.showToast(getActivity(),"当前已经是最后一个了");
            }else {
                int nextIndex = selectIndex+1;
                long nextProgress = (long) (list.get(nextIndex).getTiming()*1000L);
                exoPlayer.seekTo(nextProgress);

                isSubmitReport = false;
            }
        });
        //设置按钮
        binding.settingBtn.setOnClickListener(v -> {
            if (binding.llMoreFunction.getVisibility() == View.VISIBLE){
                binding.llMoreFunction.setVisibility(View.GONE);
            }else {
                binding.llMoreFunction.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initUi(){
        //文本显示类型
        boolean isChinese = ConfigManager.Instance().loadBoolean(showChinese);
        if (isChinese){
            binding.textSwitchPic.setImageResource(R.drawable.show_chinese_selected);
        }else {
            binding.textSwitchPic.setImageResource(R.drawable.show_chinese);
        }
        readAdapter.refreshShowTextType(isChinese?TypeLibrary.TextShowType.ALL:TypeLibrary.TextShowType.EN);

        //播放类型
        int playMode = ConfigManager.Instance().loadInt(mode, 1);
        if (playMode == 0) {
            binding.playModePic.setImageResource(R.drawable.play_this);
        } else if (playMode == 1) {
            binding.playModePic.setImageResource(R.drawable.play_next);
        } else if (playMode == 2) {
            binding.playModePic.setImageResource(R.drawable.play_random);
        }

        //滚动类型(这里默认为滚动，临时变量)
        if (isSyncText){
            binding.textRollPic.setImageResource(R.drawable.icon_sync);
        }else {
            binding.textRollPic.setImageResource(R.drawable.icon_unsync);
        }

        //倍速数据
        float speed = ConfigManager.Instance().loadFloat(playSpeed, 1.0f);
        binding.playSpeed.setText("倍速 " + speed + "x");
        exoPlayer.setPlaybackSpeed(speed);
    }

    /*********************刷新数据显示***************/
    private void refreshData() {
        chapterBean = presenter.getChapterData(bookType,voaId);

        list = presenter.getChapterDetail(bookType, voaId);
        readAdapter.refreshData(list);

        //保存学习报告内容
        StudyReportManager.getInstance().saveListenReportData(System.currentTimeMillis(),list);
    }

    /*********************单词查询****************/
    //显示查询弹窗
    /*private void showSearchWordDialog(String word){
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

        searchWordDialog.setOnCancelListener(dialog->{
            readAdapter.notifyDataSetChanged();
        });
    }*/

    //关闭查询弹窗
    /*private void closeSearchWordDialog(){
        if (searchWordDialog!=null&&searchWordDialog.isShowing()){
            searchWordDialog.dismiss();
        }
    }*/

    /*******************辅助功能*******************/
    //将当前播放暂停
    public void setPlayStatus(boolean isPlay){
        this.isCanPlay = isPlay;
        //暂停音频播放
        if (!isPlay){
            pauseAudio(false);
        }
    }

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

    //文章滚动操作
    private int getCurShowTextIndex(){
        if (exoPlayer==null){
            return 0;
        }

        long curProgress = exoPlayer.getCurrentPosition();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterDetailBean detail = list.get(i);

                if (i == list.size()-1){
                    return i;
                }

                if (curProgress <= detail.getEndTiming()*1000L){
                    return i;
                }
            }
        }
        return 0;
    }

    //显示倍速弹窗
    public void showSpeedDialog() {
        // TODO: 2024/7/11 李涛私聊：仅用于一个用户，发版的不能改动
        String[] items = new String[]{"0.5x","0.75x","1.0x","1.1x","1.2x","1.3x","1.4x","1.5x","2.0x"};

//        String[] items = new String[]{"0.5x","0.75x","1.0x","1.5x","2.0x"};
        new AlertDialog.Builder(getActivity())
                .setItems(items, (dialog, which) -> {
                    dialog.dismiss();

                    String speed = items[which];
                    float speedDub = Float.parseFloat(speed.replace("x",""));
                    binding.playSpeed.setText("倍速 " + speedDub + "x");
                    if (exoPlayer!=null){
                        exoPlayer.setPlaybackSpeed(speedDub);
                    }
                    ConfigManager.Instance().putFloat(playSpeed,speedDub);
                })
                .create()
                .show();
    }

    //弹窗功能展示
    public void showAbilityDialog(boolean isLogin,String abName){
        String msg = null;
        if (isLogin){
            msg = "该功能需要登录后才可以使用，是否立即登录？";
        }else {
            msg = abName+"需要VIP权限，是否立即开通解锁？";
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(abName)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        pauseAudio(false);

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

    //刷新列表文字大小
    public void refreshTextSize(int size){
        readAdapter.refreshShowTextSize(size);
    }

    //处理单词数据
    public String filterWord(String selectText){
        selectText = selectText.replace(".","");
        selectText = selectText.replace(",","");
        selectText = selectText.replace("!","");
        selectText = selectText.replace("?","");
        selectText = selectText.replace("'","");

        return selectText;
    }

    //处理句子数据
    public String filterSentence(String selectText){
        selectText = selectText.replace("."," ");
        selectText = selectText.replace(","," ");
        selectText = selectText.replace("!"," ");
        selectText = selectText.replace("?"," ");
        selectText = selectText.replace("'"," ");

        return selectText;
    }

    /***************************加载广告******************************/
    //加载广告接口
    private void loadADType(){
        Log.d("广告显示", "banner: --加载广告");
        if (AdBlocker.getInstance().shouldBlockAd()){
            binding.adLayout.getRoot().setVisibility(View.GONE);
            stopAdTimer();
            return;
        }

        if (UserInfoManager.getInstance().isVip()){
            binding.adLayout.getRoot().setVisibility(View.GONE);
            stopAdTimer();
            return;
        }

        if (!NetStateUtil.isConnected(getActivity())){
            binding.adLayout.getRoot().setVisibility(View.GONE);
            stopAdTimer();
            return;
        }

        //显示界面
        binding.adLayout.getRoot().setVisibility(View.VISIBLE);
        //显示新的广告操作
        showBannerAd();
    }

    private static final String adUpdate = "adUpdate";
    private long updateAdTime = 20*1000L;
    //开启广告定时器
    private void startAdTimer(){
        stopAdTimer();
        RxTimer.getInstance().multiTimerInMain(adUpdate, 0, updateAdTime, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //切换广告
                if (getActivity()==null||getActivity().isFinishing()||getActivity().isDestroyed()){
                    return;
                }

                Log.d("刷新广告显示", "onAction: ");
                loadADType();
            }
        });
    }

    //关闭广告定时器
    private void stopAdTimer(){
        RxTimer.getInstance().cancelTimer(adUpdate);
    }

    /********************************新的后台播放逻辑******************************/
    //是否是中小学（不是中小学就是小说）
    private boolean isJunior(){
        if (bookType.equals(TypeLibrary.BookType.junior_primary)
                ||bookType.equals(TypeLibrary.BookType.junior_middle)){
            return true;
        }
        return false;
    }

    //初始化音频
    private void initPlayer(){
        chapterBean = presenter.getChapterData(bookType, voaId);
        if (chapterBean!=null){
            playAudioUrl = chapterBean.getAudioUrl();
        }

        //设置图标样式
        binding.seekBar.setProgress(0);
        binding.curTime.setText(showTime(0));
        binding.audioPlay.setBackgroundResource(R.drawable.image_play);

        //是否和外面的播放数据一致
        boolean isSameInOut = false;
        if (isJunior()){
            exoPlayer = JuniorBgPlayManager.getInstance().getPlayService().getPlayer();

            if (JuniorBgPlaySession.getInstance().getCurData()!=null
                    &&JuniorBgPlaySession.getInstance().getCurData().getVoaId().equals(voaId)){
                isSameInOut = true;
                JuniorBgPlayManager.getInstance().getPlayService().setPrepare(false);
            }else {
                isSameInOut = false;
                JuniorBgPlayManager.getInstance().getPlayService().setPrepare(true);
            }
            JuniorBgPlaySession.getInstance().setPlayPosition(position);
        }else {
            exoPlayer = NovelBgPlayManager.getInstance().getPlayService().getPlayer();

            if (NovelBgPlaySession.getInstance().getCurData()!=null
                    &&NovelBgPlaySession.getInstance().getCurData().getVoaId().equals(voaId)){
                isSameInOut = true;
                NovelBgPlayManager.getInstance().getPlayService().setPrepare(false);
            }else {
                isSameInOut = false;
                NovelBgPlayManager.getInstance().getPlayService().setPrepare(true);
            }
            NovelBgPlaySession.getInstance().setPlayPosition(position);
        }

        if (isSameInOut){
            if (exoPlayer!=null){
                if (exoPlayer.isPlaying()){
                    //直接播放即可
                    playAudio(null);
                }else {
                    //显示进度数据并且暂停
                    binding.curTime.setText(showTime(exoPlayer.getCurrentPosition()));
                    binding.totalTime.setText(showTime(exoPlayer.getDuration()));
                    binding.seekBar.setMax((int) exoPlayer.getDuration());
                    binding.seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    int curScrolledIndex = getCurShowTextIndex();
                    readAdapter.refreshIndex(curScrolledIndex);
                    if (isSyncText){
                        ((CenterLinearLayoutManager)binding.recyclerView.getLayoutManager()).smoothScrollToPosition(binding.recyclerView,new RecyclerView.State(),curScrolledIndex);
                    }
                    pauseAudio(false);
                }
            }else {
                ToastUtil.showToast(getActivity(),"播放器未初始化");
            }
        }else {
            //不一致的话则直接播放
            playAudio(playAudioUrl);
        }
    }

    //播放音频
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
            boolean isPrepare = true;
            if (isJunior()){
                isPrepare = JuniorBgPlayManager.getInstance().getPlayService().isPrepare();
            }else {
                isPrepare = NovelBgPlayManager.getInstance().getPlayService().isPrepare();
            }

            if (isPrepare){
                ToastUtil.showToast(getActivity(),"正在加载音频内容，请稍后～");
                return;
            }

            if (exoPlayer!=null&&!exoPlayer.isPlaying()){
                exoPlayer.play();
            }
            //图标文本设置
            if (binding!=null){
                binding.audioPlay.setImageResource(R.drawable.image_pause);
            }
            //倒计时
            startTimer();
            //外部控制
            boolean isTempData = false;
            if (isJunior()){
                isTempData = JuniorBgPlaySession.getInstance().isTempData();
            }else {
                isTempData = NovelBgPlaySession.getInstance().isTempData();
            }
            if (!isTempData){
                if (isJunior()){
                    EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_play));
                }else {
                    EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_control_play));
                }
            }
            //通知栏控制
            if (isJunior()){
                JuniorBgPlayManager.getInstance().getPlayService().showNotification(false,true,chapterBean.getTitleEn());
            }else {
                NovelBgPlayManager.getInstance().getPlayService().showNotification(false,true,chapterBean.getTitleEn());
            }

            if (!isCanPlay){
                pauseAudio(false);
            }
        }
    }

    //暂停音频
    private void pauseAudio(boolean isEnd){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
        //图标
        if (binding!=null){
            binding.audioPlay.setImageResource(R.drawable.image_play);
        }
        //倒计时
        stopTimer();
        //外部控制
        boolean isTempData = false;
        if (isJunior()){
            isTempData = JuniorBgPlaySession.getInstance().isTempData();
        }else {
            isTempData = NovelBgPlaySession.getInstance().isTempData();
        }
        //通知栏控制
        if (isJunior()){
            JuniorBgPlayManager.getInstance().getPlayService().showNotification(false,false,chapterBean.getTitleEn());
        }else {
            NovelBgPlayManager.getInstance().getPlayService().showNotification(false,false,chapterBean.getTitleEn());
        }
        if (!isTempData){
            if (isJunior()){
                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_pause));
            }else {
                EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_control_pause));
            }
        }


        //临时数据处理
        if (isTempData){
            //直接暂停
            if (isEnd){
                exoPlayer.seekTo(0);
                if (isJunior()){
                    JuniorBgPlayManager.getInstance().getPlayService().setPrepare(false);
                }else {
                    NovelBgPlayManager.getInstance().getPlayService().setPrepare(false);
                }
            }
            return;
        }

        //非临时数据
        if (UserInfoManager.getInstance().isLogin()){
            if (isEnd){
                //是否展示学习报告
                boolean isShowReport = ConfigManager.Instance().getSendListenReport();
                submitListenReport(true,isShowReport);
                Log.d("学习报告显示", "pauseAudio: ");
            }else {
                //不用展示学习报告，直接提交就行
                submitListenReport(false,false);
            }
        }else {
            if (isEnd){
                if (isJunior()){
                    EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_switch));
                }else {
                    EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_audio_switch));
                }
                //注销数据
                destroyReadFragment();
            }
        }
    }

    //中小学-接收音频操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJuniorPlayEvent(JuniorBgPlayEvent event){
        //播放完成
        if (event.getShowType().equals(JuniorBgPlayEvent.event_audio_completeFinish)){
            //暂停
            if (!isSubmitReport){
                isSubmitReport = true;
                pauseAudio(true);
                Log.d("学习报告显示", "onJuniorPlayEvent: --播放完成");
            }
        }

        //播放
        if (event.getShowType().equals(JuniorBgPlayEvent.event_audio_play)){
            //停止播放
            EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_control_pause));

            boolean isPrepare = JuniorBgPlayManager.getInstance().getPlayService().isPrepare();

            if (exoPlayer!=null&&!isPrepare){
                if (binding!=null){
                    binding.totalTime.setText(showTime(exoPlayer.getDuration()));
                    binding.seekBar.setMax((int) exoPlayer.getDuration());
                }
            }

            //这里是自动播放按钮的处理
            if (!ConfigManager.Instance().loadAutoPlay()||!isCanPlay){
                pauseAudio(false);
                return;
            }

            playAudio(null);
        }

        //暂停
        if (event.getShowType().equals(JuniorBgPlayEvent.event_audio_pause)){
            pauseAudio(false);
            Log.d("学习报告显示", "onJuniorPlayEvent: --暂停");
        }
    }

    //小说-接收音频操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNovelPlayEvent(NovelBgPlayEvent event){
        //播放完成
        if (event.getShowType().equals(NovelBgPlayEvent.event_audio_completeFinish)){
            //暂停
            if (!isSubmitReport){
                isSubmitReport = true;
                pauseAudio(true);
                Log.d("学习报告显示", "onJuniorPlayEvent: --播放完成");
            }
        }

        //播放
        if (event.getShowType().equals(NovelBgPlayEvent.event_audio_play)){
            //停止播放
            EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_pause));

            boolean isPrepare = NovelBgPlayManager.getInstance().getPlayService().isPrepare();

            if (exoPlayer!=null&&!isPrepare){
                binding.totalTime.setText(showTime(exoPlayer.getDuration()));
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
        if (event.getShowType().equals(NovelBgPlayEvent.event_audio_pause)){
            pauseAudio(false);
        }
    }

    //通用刷新事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){

        //刷新课程详情数据
        if (event.getType().equals(TypeLibrary.RefreshDataType.study_detailRefresh)){
            refreshData();
        }
    }

    private static final String playTagTimer = "playTagTimer";
    //播放倒计时
    private void startTimer(){
        if (binding!=null){
            if (exoPlayer!=null){
                //设置时间
                binding.curTime.setText(showTime(exoPlayer.getCurrentPosition()));
                binding.totalTime.setText(showTime(exoPlayer.getDuration()));
                //设置进度
                binding.seekBar.setMax((int) exoPlayer.getDuration());
                binding.seekBar.setProgress((int) exoPlayer.getCurrentPosition());
            }
        }

        RxTimer.getInstance().multiTimerInMain(playTagTimer, 0, 500L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {

                if (binding!=null){
                    //设置时间
                    binding.curTime.setText(showTime(exoPlayer.getCurrentPosition()));
                    binding.totalTime.setText(showTime(exoPlayer.getDuration()));
                    //设置进度
                    binding.seekBar.setMax((int) exoPlayer.getDuration());
                    binding.seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    //设置文本
                    int curShowIndex = getCurShowTextIndex();
                    readAdapter.refreshIndex(curShowIndex);
                    if (isSyncText){
                        ((CenterLinearLayoutManager)binding.recyclerView.getLayoutManager()).smoothScrollToPosition(binding.recyclerView,new RecyclerView.State(),curShowIndex);
                    }
                    //处理a、b点播放
                    if (abStartPosition!=0&&abEndPosition!=0){
                        if (exoPlayer.getCurrentPosition()>abStartPosition){
                            exoPlayer.seekTo(abStartPosition);
                        }
                    }

                    Log.d("晋时期进度显示", binding.seekBar.getMax()+"---"+binding.seekBar.getProgress());
                }else {
                    Log.d("晋时期进度显示", "不显示1");
                }
            }
        });
    }

    //播放倒计时停止
    private void stopTimer(){
        RxTimer.getInstance().cancelTimer(playTagTimer);
    }

    //显示学习报告
    private Map<String,VoaWord2> collectWordMap = new HashMap<>();
    private NewListenStudyReportDialog reportDialog = null;
    private String reportTime = "reportTime";
    private void showReadReportDialog(String reward){
        //当前单词
        List<VoaWord2> wordList = new ArrayList<>();
        for (String key:collectWordMap.keySet()){
            wordList.add(collectWordMap.get(key));
        }

        //这里获取当前的单词数据
        List<VoaWord2> showWordList = new ArrayList<>();
        if (isJunior()
                &&!TextUtils.isEmpty(bookId)
                &&!TextUtils.isEmpty(unitId)){
            List<WordEntity_junior> juniorList = JuniorDataManager.searchWordByUnitIdFromDB(bookId,unitId);
            if (juniorList!=null&&juniorList.size()>0){
                for (int i = 0; i < juniorList.size(); i++) {
                    WordEntity_junior curWord = juniorList.get(i);
                    VoaWord2 word2 = new VoaWord2();
                    word2.word = curWord.word;
                    word2.def = curWord.def;
                    word2.pron = curWord.pron;
                    word2.audio = curWord.audio;

                    showWordList.add(word2);
                }
            }
        }

        Log.d("学习报告显示", "showReadReportDialog: ");
        reportDialog = new NewListenStudyReportDialog(mContext);
        reportDialog.setData(reward, showWordList, wordList, new DialogCallBack() {
            @Override
            public void callback() {
                boolean isTempData = false;
                if (isJunior()){
                    isTempData = JuniorBgPlaySession.getInstance().isTempData();
                }else {
                    isTempData = NovelBgPlaySession.getInstance().isTempData();
                }

                if (isTempData){
                    if (isJunior()){
                        JuniorBgPlayManager.getInstance().getPlayService().setPrepare(false);
                    }else {
                        NovelBgPlayManager.getInstance().getPlayService().setPrepare(false);
                    }
                    exoPlayer.seekTo(0);
                }else {
                    if (isJunior()){
                        EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_switch));
                    }else {
                        EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_audio_switch));
                    }
                }

                //注销数据
                destroyReadFragment();
            }
        });
        reportDialog.setOnDialogTouchListener(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                RxTimer.getInstance().cancelTimer(reportTime);
            }
        });
        reportDialog.create();
        reportDialog.show();
        RxTimer.getInstance().timerInMain(reportTime, 5000L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                if (reportDialog!=null){
                    reportDialog.dismiss();
                }
                RxTimer.getInstance().cancelTimer(reportTime);
            }
        });

        /*reportDialog = ListenStudyReportDialog.getInstance()
                .init(mContext)
                .setData(reward, showWordList, wordList, new DialogCallBack() {
                    @Override
                    public void callback() {
                        boolean isTempData = false;
                        if (isJunior()){
                            isTempData = JuniorBgPlaySession.getInstance().isTempData();
                        }else {
                            isTempData = NovelBgPlaySession.getInstance().isTempData();
                        }

                        if (isTempData){
                            if (isJunior()){
                                JuniorBgPlayManager.getInstance().getPlayService().setPrepare(false);
                            }else {
                                NovelBgPlayManager.getInstance().getPlayService().setPrepare(false);
                            }
                            exoPlayer.seekTo(0);
                        }else {
                            if (isJunior()){
                                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_switch));
                            }else {
                                EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_audio_switch));
                            }
                        }

                        //注销数据
                        destroyReadFragment();
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
                reportDialog.closeSelf();
                RxTimer.getInstance().cancelTimer(reportTime);
            }
        });*/
    }

    //提交学习报告
    private void submitListenReport(boolean isEnd,boolean isShowReport){
        if (isEnd&&isShowReport){
            startLoading("正在提交学习报告...");
        }

        StudyReportManager.getInstance().submitListenReportData(bookType, System.currentTimeMillis(), isEnd, voaId, new StudyReportManager.OnListenReportCallBack() {
            @Override
            public void onShowReward(String price) {
                stopLoading();

                if (isEnd){
                    if (isShowReport){
                        Log.d("学习报告显示", "onShowReward: ");

                        showReadReportDialog(price);
                    }else {
                        if (isJunior()){
                            EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_switch));
                        }else {
                            EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_audio_switch));
                        }
                        //注销数据
                        destroyReadFragment();
                    }
                }

                if (!TextUtils.isEmpty(price)){
                    double readPrice = Double.parseDouble(price);
                    if (readPrice>0){
                        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
                    }
                }
            }
        });
    }

    //外部处理
    public void destroyReadFragment(){
        RxTimer.getInstance().cancelTimer(reportTime);
        AdBannerShowManager.getInstance().stopBannerAd();
        stopTimer();
        presenter.detachView();
    }

    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(mContext);
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }

    /****************************开屏广告点击**************************/
    //点击开屏广告结果
    public void showClickAdResultData(boolean isSuccess, String showMsg) {
        //直接显示信息即可
        ToastUtil.showToast(getActivity(),showMsg);

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


                        String fixShowType = AdUploadUtil.Param.AdShowPosition.show_banner;
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
                    //关闭界面
                    binding.adLayout.getRoot().setVisibility(View.GONE);
                    //关闭计时器
                    stopAdTimer();
                    //关闭广告
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
