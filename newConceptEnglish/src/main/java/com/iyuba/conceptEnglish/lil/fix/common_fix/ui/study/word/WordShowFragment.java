package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.FragmentFixWordBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.wordTrain.WordTrainActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.event.WordSearchEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 单词展示界面
 * @date: 2023/8/15 11:40
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordShowFragment extends BaseViewBindingFragment<FragmentFixWordBinding> implements WordShowView{

    private WordShowPresenter presenter;
    private WordShowAdapter showAdapter;

    private String types;
    private String bookId;
    private String id;//中小学为unitId，新概念内容为voaId
    private int position;//当前章节的位置

    //播放器
    private ExoPlayer exoPlayer;
    //重复查询次数
    private int retryCount = 0;

    //这里必须传id，因为中小学类型和新概念类型不同的界面处理，中小学的是unitId,新概念的是voaId，需要自行处理
    public static WordShowFragment getInstance(String types,String bookId,String id,int position){
        WordShowFragment fragment = new WordShowFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types,types);
        bundle.putString(StrLibrary.bookId,bookId);
        bundle.putString(StrLibrary.id,id);
        bundle.putInt(StrLibrary.position,position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        types = getArguments().getString(StrLibrary.types);
        bookId = getArguments().getString(StrLibrary.bookId);
        id = getArguments().getString(StrLibrary.id);
        position = getArguments().getInt(StrLibrary.position,0);

        presenter = new WordShowPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();

        //这里不用自己的处理了，直接从数据库获取数据
        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();
    }

    /**********************初始化***************************/
    private void initList(){
        binding.toolbar.getRoot().setVisibility(View.GONE);
        binding.bottomView.setVisibility(View.INVISIBLE);

        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);

        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                presenter.getWordData(types,bookId,id);
            }
        });

        showAdapter = new WordShowAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(showAdapter);
        showAdapter.setOnWordItemClickListener(new WordShowAdapter.OnWordItemClickListener() {
            @Override
            public void onPlay(WordBean bean) {
                Log.d("音频播放", bean.toString());

                if (exoPlayer!=null){
                    if (exoPlayer.isPlaying()){
                        Log.d("音频播放", "准备暂停");
                        pausePlay();
                    }

                    Log.d("音频播放", "开始播放--"+bean.getWordAudioUrl());
                    startPlay(bean.getWordAudioUrl());
                }
            }

            @Override
            public void onItem(WordBean bean) {
                EventBus.getDefault().post(new WordSearchEvent(bean.getWord()));
            }
        });

        List<Pair<String,Pair<Integer,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_enToCn,new Pair<>(R.drawable.vector_en2cn,"英汉训练")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_cnToEn,new Pair<>(R.drawable.vector_cn2en,"汉英训练")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Word_spell,new Pair<>(R.drawable.vector_spelling,"单词拼写")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_listen,new Pair<>(R.drawable.vector_listen,"听力训练")));
        WordShowBottomAdapter bottomAdapter = new WordShowBottomAdapter(getActivity(),pairList);
        GridLayoutManager bottomManager = new GridLayoutManager(getActivity(),pairList.size());
        binding.bottomView.setLayoutManager(bottomManager);
        binding.bottomView.setAdapter(bottomAdapter);
        bottomAdapter.setListener(new OnSimpleClickListener<String>() {
            @Override
            public void onClick(String showType) {
                pausePlay();
                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(getActivity());
                    return;
                }

                //这里需要判断当前章节的位置，前三个章节免费，后面的需要开通会员收费
                if (position<3){
                    WordTrainActivity.start(getActivity(),showType,types,bookId,id);
                }else {
                    /*if (!AccountManager.getInstance().checkUserLogin()){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("使用说明")
                                .setMessage("课程前三章节为免费章节，后续章节需要登录您的账号，是否继续使用？")
                                .setPositiveButton("继续使用", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        LoginUtil.startToLogin(getActivity());
                                    }
                                }).setNegativeButton("暂不使用",null)
                                .setCancelable(false)
                                .show();
                        return;
                    }*/

                    if (!UserInfoManager.getInstance().isVip()){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("使用说明")
                                .setMessage("课程前三章节为免费章节，后续章节需要开通会员后使用，是否继续使用？")
                                .setPositiveButton("继续使用", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_APP);
                                    }
                                }).setNegativeButton("暂不使用",null)
                                .setCancelable(false)
                                .show();
                        return;
                    }

                    WordTrainActivity.start(getActivity(),showType,types,bookId,id);
                }
            }
        });
    }

    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(getActivity()).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        Log.d("音频播放", "准备播放");

                        exoPlayer.play();
                        break;
                    case Player.STATE_ENDED:
                        //播放结束
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.d("音频播放", "音频有毛病");
                ToastUtil.showToast(getActivity(),"音频初始化失败~");
            }
        });
    }

    /*********************回调********************/
    @Override
    public void showWord(List<WordBean> list) {
        if (list==null){
            binding.refreshLayout.finishRefresh(false);
            ToastUtil.showToast(getActivity(),"获取当前章节单词异常，请下拉重试");
            binding.bottomView.setVisibility(View.GONE);
            binding.noDataLayout.setVisibility(View.GONE);
            return;
        }

        binding.refreshLayout.finishRefresh(true);
        if (list.size()>0){
            binding.noDataLayout.setVisibility(View.GONE);
            showAdapter.refreshData(list);
            binding.bottomView.setVisibility(View.VISIBLE);
        }else {
            //这里进行三次查询，三次都没有则显示暂无数据
            retryCount++;
            if (retryCount>=3){
                retryCount = 0;
                binding.noDataLayout.setVisibility(View.VISIBLE);
                binding.noDataText.setText("当前章节暂无单词数据");
                binding.bottomView.setVisibility(View.GONE);
            }else {
                //延迟1000L查询
                RxTimer.getInstance().timerInMain("delay", 1000L, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer("delay");
                        presenter.getWordData(types,bookId,id);
                    }
                });
            }
        }
    }

    /************************辅助功能************************/
    //播放音频
    private void startPlay(String url){
        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"请链接网络后播放");
            return;
        }

        MediaItem mediaItem = MediaItem.fromUri(url);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    //暂停音频
    private void pausePlay(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
    }
}
