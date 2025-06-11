package com.iyuba.conceptEnglish.lil.fix.junior.main;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.ad.AdInitManager;
import com.iyuba.conceptEnglish.databinding.FragmentListRefreshBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.JuniorBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.template.AdTemplateShowManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.template.AdTemplateViewBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.template.OnAdTemplateShowListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.lesson.LessonPresenter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.lesson.LessonView;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.StudyActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlayManager;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlaySession;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 中小学内容
 * @date: 2023/5/19 09:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorListFragment extends BaseViewBindingFragment<FragmentListRefreshBinding> implements LessonView {

    private LessonPresenter presenter;
    private JuniorListAdapter adapter;

    public static JuniorListFragment getInstance(){
        JuniorListFragment fragment = new JuniorListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new LessonPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        isToOtherPage = false;

        Log.d("界面显示", "是否为其他界面--"+isToOtherPage);
    }

    @Override
    public void onPause() {
        super.onPause();
        isToOtherPage = true;

        Log.d("界面显示", "是否为其他界面--"+isToOtherPage);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initClick();

        //延迟1s加载
        RxTimer.getInstance().timerInMain("juniorDelayTime", 1500L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                RxTimer.getInstance().cancelTimer("juniorDelayTime");
                refreshData(JuniorBookChooseManager.getInstance().getBookType(), JuniorBookChooseManager.getInstance().getBookId());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        //关闭广告
        AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);

        presenter.detachView();
    }

    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    binding.refreshLayout.finishRefresh(false);
                    ToastUtil.showLongToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                presenter.loadNetChapterData(JuniorBookChooseManager.getInstance().getBookType(),"", JuniorBookChooseManager.getInstance().getBookId());
            }
        });

        adapter = new JuniorListAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);
        adapter.setListener(new OnSimpleClickListener<Pair<Integer, BookChapterBean>>() {
            @Override
            public void onClick(Pair<Integer, BookChapterBean> pair) {
                StudyActivity.start(getActivity(),pair.second.getTypes(), pair.second.getBookId(), pair.second.getVoaId(),pair.first);
            }
        });
    }

    private void initClick(){
        binding.reBottom.setOnClickListener(v->{
            //跳转
            BookChapterBean chapterBean = JuniorBgPlaySession.getInstance().getCurData();
            StudyActivity.start(getActivity(),chapterBean.getTypes(),chapterBean.getBookId(),chapterBean.getVoaId(),JuniorBgPlaySession.getInstance().getPlayPosition());
        });
        binding.imgPlay.setOnClickListener(v->{
            ExoPlayer exoPlayer = JuniorBgPlayManager.getInstance().getPlayService().getPlayer();
            if (exoPlayer!=null){
                if (exoPlayer.isPlaying()){
                    EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_pause));
                }else {
                    EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_play));
                }
            }else {
                ToastUtil.showToast(getActivity(),"未初始化音频");
            }
        });
    }

    //刷新数据
    private void refreshData(String types,String bookId){
        presenter.loadLocalChapterData(types, "",bookId);
    }

    @Override
    public void showData(List<BookChapterBean> list) {
        if (list!=null){
            binding.refreshLayout.finishRefresh(true);
            adapter.refreshData(list);

            //保存在后台数据中
            JuniorBgPlaySession.getInstance().setVoaList(list);

            if (list.size()==0){
                ToastUtil.showToast(getActivity(),"暂无该章节的数据~");
            }else {
                //刷新广告
                refreshTemplateAd();
            }
        }else {
            binding.refreshLayout.finishRefresh(false);
            ToastUtil.showToast(getActivity(),"获取章节数据失败，请重试～");
        }
    }

    @Override
    public void loadNetData() {
        binding.refreshLayout.autoRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.junior)){
            refreshData(JuniorBookChooseManager.getInstance().getBookType(), JuniorBookChooseManager.getInstance().getBookId());
        }
    }

    //用户登录后的操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VipChangeEvent event){
        refreshData(JuniorBookChooseManager.getInstance().getBookType(), JuniorBookChooseManager.getInstance().getBookId());
    }

    /**********************************后台播放相关****************************/
    //是否去了其他界面
    private boolean isToOtherPage = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(JuniorBgPlayEvent event){
        if (!isToOtherPage){
            //加载完成
            if (event.getShowType().equals(JuniorBgPlayEvent.event_audio_prepareFinish)){
                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_play));
            }

            //播放完成
            if (event.getShowType().equals(JuniorBgPlayEvent.event_audio_completeFinish)){
                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_switch));
            }
        }

        //播放
        if (event.getShowType().equals(JuniorBgPlayEvent.event_control_play)){
            ExoPlayer exoPlayer = JuniorBgPlayManager.getInstance().getPlayService().getPlayer();
            if (exoPlayer!=null&&!exoPlayer.isPlaying()){
                exoPlayer.play();
            }
            BookChapterBean chapterBean = JuniorBgPlaySession.getInstance().getCurData();
            binding.reBottom.setVisibility(View.VISIBLE);
            binding.imgPlay.setImageResource(R.drawable.image_pause);
            if (chapterBean!=null){
                binding.tvTitle.setText(chapterBean.getTitleEn());
                binding.tvTitleCn.setText(chapterBean.getTitleCn());
                //通知栏处理
                JuniorBgPlayManager.getInstance().getPlayService().showNotification(false,true,chapterBean.getTitleEn());
            }
        }

        //暂停
        if (event.getShowType().equals(JuniorBgPlayEvent.event_control_pause)){
            //暂停播放
            ExoPlayer exoPlayer = JuniorBgPlayManager.getInstance().getPlayService().getPlayer();
            if (exoPlayer!=null&&exoPlayer.isPlaying()){
                exoPlayer.pause();
            }
            //显示图标
            BookChapterBean chapterBean = JuniorBgPlaySession.getInstance().getCurData();
            if (binding.reBottom.getVisibility() == View.VISIBLE){
                binding.reBottom.setVisibility(View.VISIBLE);
            }
            binding.imgPlay.setImageResource(R.drawable.image_play);
            if (chapterBean!=null){
                binding.tvTitle.setText(chapterBean.getTitleEn());
                binding.tvTitleCn.setText(chapterBean.getTitleCn());
                //通知栏处理
                JuniorBgPlayManager.getInstance().getPlayService().showNotification(false,false,chapterBean.getTitleEn());
            }
        }

        //隐藏控制栏
        if (event.getShowType().equals(JuniorBgPlayEvent.event_control_hide)){
            //停止播放
            ExoPlayer exoPlayer = JuniorBgPlayManager.getInstance().getPlayService().getPlayer();
            exoPlayer.pause();
            //通知栏处理
            BookChapterBean chapterBean = JuniorBgPlaySession.getInstance().getCurData();
            JuniorBgPlayManager.getInstance().getPlayService().showNotification(false,false,chapterBean.getTitleEn());
            //隐藏控制栏
            binding.reBottom.setVisibility(View.GONE);
        }

        //切换播放
        if (event.getShowType().equals(JuniorBgPlayEvent.event_audio_switch)){
            //根据当前类型选择下一个音频的位置
            ExoPlayer exoPlayer = JuniorBgPlayManager.getInstance().getPlayService().getPlayer();

            int playMode = ConfigManager.Instance().loadInt("mode",1);
            if (playMode==0){
                //单曲循环
                exoPlayer.seekTo(0);
                //这里不需要重新加载
                JuniorBgPlayManager.getInstance().getPlayService().setPrepare(false);

                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_play));
                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_play));
            }else if (playMode==1){
                //顺序播放
                if (JuniorBgPlaySession.getInstance().getPlayPosition() <= JuniorBgPlaySession.getInstance().getVoaList().size()-1){
                    //刷新数据显示
                    int nextPosition = JuniorBgPlaySession.getInstance().getPlayPosition()+1;

                    if (isToOtherPage){
                        EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_data_refresh,nextPosition));
                    }else {
                        //获取音频并播放
                        JuniorBgPlaySession.getInstance().setPlayPosition(nextPosition);
                        initPlayerAndPlayAudio(JuniorBgPlaySession.getInstance().getCurData());
                    }
                }
            }else if (playMode==2){
                //随机播放
                //获取随机数
                int randomIndex = (int) (JuniorBgPlaySession.getInstance().getVoaList().size()*Math.random());
                if (randomIndex == JuniorBgPlaySession.getInstance().getPlayPosition()){
                    if (randomIndex == JuniorBgPlaySession.getInstance().getVoaList().size()-1){
                        randomIndex--;
                    }
                    randomIndex++;
                }

                if (isToOtherPage){
                    EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_data_refresh,randomIndex));
                }else {
                    //获取音频并播放
                    JuniorBgPlaySession.getInstance().setPlayPosition(randomIndex);
                    initPlayerAndPlayAudio(JuniorBgPlaySession.getInstance().getCurData());
                }
            }
        }
    }

    //播放音频
    private void initPlayerAndPlayAudio(BookChapterBean chapterBean){
        MediaItem mediaItem = MediaItem.fromUri(chapterBean.getAudioUrl());
        JuniorBgPlayManager.getInstance().getPlayService().getPlayer().setMediaItem(mediaItem);
        JuniorBgPlayManager.getInstance().getPlayService().getPlayer().prepare();
    }

    /***************************************信息流广告****************************************/
    //当前信息流广告的key
    private String adTemplateKey = JuniorListFragment.class.getName();
    //模版广告数据
    private AdTemplateViewBean templateViewBean = null;
    //显示广告
    private void showTemplateAd() {
        if (templateViewBean == null) {
            templateViewBean = new AdTemplateViewBean(R.layout.item_ad_mix, R.id.template_container, R.id.ad_whole_body, R.id.native_main_image, R.id.native_title, binding.recyclerView, adapter, new OnAdTemplateShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String showAdMsg) {

                }

                @Override
                public void onAdClick() {

                }
            });
            AdTemplateShowManager.getInstance().setShowData(adTemplateKey, templateViewBean);
        }
        AdTemplateShowManager.getInstance().showTemplateAd(adTemplateKey,getActivity());
    }

    //刷新广告操作[根据类型判断刷新还是隐藏]
    private void refreshTemplateAd(){
//        if (NetworkUtil.isConnected(getActivity()) && AdInitManager.isShowAd() && !UserInfoManager.getInstance().isVip()) {
//            showTemplateAd();
//        } else {
//            AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);
//        }

        if (NetworkUtil.isConnected(getActivity()) && AdInitManager.isShowAd()){
            showTemplateAd();
        }
    }
}
