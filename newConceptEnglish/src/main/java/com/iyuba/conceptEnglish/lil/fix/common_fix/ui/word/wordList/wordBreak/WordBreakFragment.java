package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordBreak;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.pass.WordShareActivity;
import com.iyuba.conceptEnglish.databinding.FragmentWordBreakBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.studyReport.StudyReportManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line.PractiseLineEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.WordListActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.widget.OutsideClickDialog;
import com.iyuba.conceptEnglish.widget.cdialog.CustomDialog;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.sdk.other.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/6/12 14:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordBreakFragment extends BaseViewBindingFragment<FragmentWordBreakBinding> {

    private String bookType;
    private String bookId;
    private String id;

    private WordBreakPresenter presenter;
    private WordBreakAdapter breakAdapter;

    private CustomDialog mAlertDialog;

    //当前位置
    private int curIndex = 0;
    //需要展示的单词数据
    private List<Pair<WordBean, List<WordBean>>> showWordList;
    //暂存数据
    private Map<WordBean, WordBean> saveMap = new HashMap<>();
    //播放器
    private ExoPlayer exoPlayer;

    //进入界面的开始时间
    private long enterStartTime;

    public static WordBreakFragment getInstance(String bookType, String bookId, String id){
        WordBreakFragment fragment = new WordBreakFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types,bookType);
        bundle.putString(StrLibrary.bookId,bookId);
        bundle.putString(StrLibrary.id,id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        presenter = new WordBreakPresenter();
//        presenter.attachView(this);

        bookType = getArguments().getString(StrLibrary.types);
        bookId = getArguments().getString(StrLibrary.bookId);
        id = getArguments().getString(StrLibrary.id);
        showWordList = presenter.getRandomWordShowData(bookType,bookId,id);

        enterStartTime = System.currentTimeMillis();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initPlayer();
        initList();
        initClick();

        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();

        pausePlay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();
    }

    /***********************************初始化************************/
    private void initToolbar(){
        binding.toolbar.getRoot().setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button);
        binding.toolbar.btnBack.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });

        binding.toolbar.title.setText((curIndex+1)+"/"+showWordList.size());
    }

    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(getActivity()).build();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        break;
                    case Player.STATE_ENDED:
                        pausePlay();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(),"加载音频失败～");
            }
        });
    }

    private void initList(){
        mAlertDialog = new CustomDialog(getActivity(), R.style.dialog_style);
        mAlertDialog.setCanceledOnTouchOutside(false);

        breakAdapter = new WordBreakAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(breakAdapter);
        breakAdapter.setListener(new OnSimpleClickListener<Pair<Integer, WordBean>>() {
            @Override
            public void onClick(Pair<Integer, WordBean> pair) {
                //当前展示数据
                WordBean curBean = showWordList.get(curIndex).first;
                //保存结果数据
                saveMap.put(curBean,pair.second);
                //刷新显示
                breakAdapter.refreshAnswer(pair.first, curBean.getWord());
                //底部按钮刷新
                binding.next.setVisibility(View.VISIBLE);
                //控制结果显示
                if (curIndex == showWordList.size()-1){
                    binding.next.setText("看结果");
                }else {
                    binding.next.setText("下一个");
                }
            }
        });
    }

    private void initClick(){
        binding.next.setOnClickListener(v->{
            if (curIndex == showWordList.size()-1){
                showDialog();
            }else {
                curIndex++;
                refreshData();
            }
        });
    }

    /*******************刷新数据*******************/
    private void refreshData(){
        Pair<WordBean,List<WordBean>> refreshPair = showWordList.get(curIndex);
        breakAdapter.refreshData(refreshPair.second);

        binding.word.setText(refreshPair.first.getWord());
        binding.next.setVisibility(View.INVISIBLE);
        binding.toolbar.title.setText((curIndex+1)+"/"+showWordList.size());

        startPlay(refreshPair.first.getWordAudioUrl());
    }

    /********************音频播放******************/
    //播放音频
    private void startPlay(String wordAudioUrl){
        if (exoPlayer==null){
            ToastUtil.showToast(getActivity(),"音频播放器初始化失败～");
            return;
        }
        
        if (!NetworkUtil.isConnected(getActivity())){
            ToastUtil.showToast(getActivity(),"播放单词音频需要网络，请链接网络后重试～");
            return;
        }

        if (TextUtils.isEmpty(wordAudioUrl)){
            ToastUtil.showToast(getActivity(),"当前单词音频未找到");
            return;
        }

        MediaItem mediaItem = MediaItem.fromUri(wordAudioUrl);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    //暂停音频
    private void pausePlay(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
    }

    /******************其他功能*******************/
    //显示结果
    private void showDialog(){
        //获取数据展示
        int allCount = saveMap.keySet().size();
        int rightCount = 0;
        for (WordBean key:saveMap.keySet()){
            WordBean result = saveMap.get(key);
            if (key.getWord().equals(result.getWord())){
                rightCount++;
            }
        }
        float rightRate = rightCount*1.0F/allCount;
        int rightRateInt = (int) (rightRate*100);


        //这里根据要求进行单词闯关后单词正确数量的显示，无论是否通过，都需要增加
        presenter.saveWordBreakDataToDB(UserInfoManager.getInstance().getUserId(), saveMap);
        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.junior));

        //根据结果显示(按照要求，应该是80%为通过率)
        int passCount = Math.round(showWordList.size()*0.8f);
        if (rightCount>=passCount){
            showCompleteDialog(rightRateInt);
        }else {
            StringBuilder sb = new StringBuilder();
            sb.append("共做：").append(showWordList.size()).append("题，做对：").append(rightCount).append("题").
                    append("，正确比例：").append(rightRateInt).append("%");
            showFailAlertDialog(sb.toString(), "闯关失败", true);
        }
    }
    /**
     * 闯关失败时 的提示框，上传记录
     * @param contentStr
     * @param titleStr
     * @param isShowCancel
     */
    private void showFailAlertDialog(String contentStr, String titleStr, boolean isShowCancel) {
        View merge_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_custom_wrong, null);//要填充的layout
        mAlertDialog.setContentView(merge_view);
        TextView content = merge_view.findViewById(R.id.tv_dialog_content);//提示的内容
        TextView title = merge_view.findViewById(R.id.tv_dialog_title);//提示的内容
        View line = merge_view.findViewById(R.id.line);//提示的内容
        Button rechCancleBtn = merge_view.findViewById(R.id.btn_dialog_no);
        Button rechOkBtn = merge_view.findViewById(R.id.btn_dialog_yes);
        content.setText(contentStr);
        if (!TextUtils.isEmpty(titleStr)) {
            title.setText(titleStr);
        }
        if (isShowCancel) {
            rechCancleBtn.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            rechCancleBtn.setText("错误列表");
            rechCancleBtn.setVisibility(View.GONE);
        } else {
            rechCancleBtn.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        mAlertDialog.show();
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mAlertDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.8); //设置宽度
        mAlertDialog.getWindow().setAttributes(lp);

        //分享领红包 按钮
        rechOkBtn.setOnClickListener(v -> {
//            new Thread(new ExerciseWordActivity.UpdateStudyRecordThread()).start();
            StackUtil.getInstance().finishCur();
        });
    }

    /**
     * 闯关成功时候的提示框，上传记录
     */
    private void showCompleteDialog(int rateInt) {
//        new Thread(this::updatePassData).start();

        final Dialog dialog = new OutsideClickDialog(getActivity(), R.style.Dialog) {
            @Override
            protected void onTouchOutside() {
                ToastUtil.showToast(getActivity(), "恭喜您闯关成功，请开始下一关吧！");
                StackUtil.getInstance().finishCur();
                StackUtil.getInstance().finish(WordListActivity.class);
            }
        };
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View successView = inflater.inflate(R.layout.dialog_word_share, null);
        TextView txtStage = successView.findViewById(R.id.txt_stage_num);
        TextView txtWordNum = successView.findViewById(R.id.txt_word_num);
        ImageView ic_close = successView.findViewById(R.id.iv_close);
        Button button = successView.findViewById(R.id.btn_share);
        if (InfoHelper.getInstance().openShare()){
            button.setVisibility(View.VISIBLE);
        }else {
            button.setVisibility(View.GONE);
        }
        dialog.setContentView(successView);

        txtStage.setText("恭喜您成功过关");
        txtWordNum.setText("本单元共学习单词" + showWordList.size() + "个");
        dialog.show();

        //保存数据
        presenter.saveWordBreakDataToDB(UserInfoManager.getInstance().getUserId(), saveMap);
        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.junior));

        //设置点击
        ic_close.setOnClickListener(v -> {
            dialog.dismiss();
            ToastUtil.showToast(getActivity(), "恭喜您闯关成功，请开始下一关吧！");
            //提交学习报告
            StudyReportManager.getInstance().submitWordReportData(bookType,bookId,enterStartTime,saveMap);
            //显示界面
            StackUtil.getInstance().finishCur();
            StackUtil.getInstance().finish(WordListActivity.class);
        });

        button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WordShareActivity.class);
            intent.putExtra("courseId", id);
            intent.putExtra("wordNum", showWordList.size());
            intent.putExtra("rate",+rateInt+ "%");
            startActivity(intent);

            StackUtil.getInstance().finishCur();
            StackUtil.getInstance().finish(WordListActivity.class);
        });
    }

    //是否显示弹窗
    public boolean showBackDialog(){
        if (saveMap.keySet().size()<showWordList.size()){
            showFailAlertDialog("尚未闯关成功，即将退出？", "", false);
            return true;
        }

        return false;
    }
}
