package com.iyuba.conceptEnglish.lil.concept_other.word.wordNote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_note;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.discover.activity.WordPdfExport;
import com.iyuba.core.discover.activity.WordSetActivity;
import com.iyuba.core.event.WordSearchEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.lib.databinding.WordNoteNewBinding;
import com.iyuba.multithread.util.NetStatusUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 新的生词本界面
 */
public class WordNoteActivity extends BaseViewBindingActivity<WordNoteNewBinding> implements WordNoteView{

    //参数
    private int showIndex = 1;
    private static final int pageCount = 30;

    //数据
    private WordNotePresenter presenter;
    //适配器
    private WordNoteAdapter adapter;
    //播放器
    private ExoPlayer audioPlayer;
    //当前播放音频的url
    private String curPlayWordUrl = "";


    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context, WordNoteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new WordNotePresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();
        initClick();
        initPlayer();

        binding.refreshLayout.autoRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        pauseAudio();
        presenter.detachView();
    }

    /******************************初始化****************************/
    private void initToolbar(){
        binding.buttonBack.setOnClickListener(v->{
            finish();
        });
        /*binding.tvWordEdit.setText("编辑");
        binding.tvWordEdit.setOnClickListener(v->{
            String showText = binding.tvWordStatistic.getText().toString();
            if (showText.equals("编辑")){

            }else if (showText.equals("取消")){

            }else if (showText.equals("删除")){

            }
        });*/

        //设置功能显示
        showAbilityVisible(false);
    }

    private void initList(){
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetStatusUtil.isConnected(WordNoteActivity.this)){
                    binding.refreshLayout.finishLoadMore(false);
                    ToastUtil.showToast(WordNoteActivity.this,"请链接网络后获取数据");
                    return;
                }

                refreshData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetStatusUtil.isConnected(WordNoteActivity.this)){
                    binding.refreshLayout.finishRefresh(false);
                    ToastUtil.showToast(WordNoteActivity.this,"请链接网络后获取数据");
                    return;
                }

                showIndex = 1;
                refreshData();
            }
        });

        adapter = new WordNoteAdapter(this,new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnWordNoteClickListener(new WordNoteAdapter.OnWordNoteClickListener() {
            @Override
            public void onItemClick(Word_note.TempWord word) {
                EventBus.getDefault().post(new WordSearchEvent(word.word));
            }

            @Override
            public void onPlayAudio(String playUrl) {
                if (TextUtils.isEmpty(playUrl)){
                    ToastUtil.showToast(WordNoteActivity.this,"暂无当前单词音频");
                    return;
                }

                if (audioPlayer==null){
                    ToastUtil.showToast(WordNoteActivity.this,"播放器未初始化完成");
                    return;
                }

                if (curPlayWordUrl.equals(playUrl)){
                    if (audioPlayer.isPlaying()){
                        pauseAudio();
                    }else {
                        playAudio(playUrl);
                    }
                }else {
                    playAudio(playUrl);
                }
            }

            @Override
            public void onLongDeleteClick(Word_note.TempWord word) {
                new AlertDialog.Builder(WordNoteActivity.this)
                        .setTitle("提示")
                        .setMessage("确认删除\"" + word.word + "\"吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                //删除单词
                                deleteData(word.word);
                            }
                        })
                        .create().show();
            }
        });
    }

    private void initClick(){
        binding.tvWordPdf.setOnClickListener(v->{
            //pdf
            if (adapter.getShowList().size()==0){
                ToastUtil.showToast(this,"暂无生词数据，无法导出PDF文件");
                return;
            }

            new WordPdfExport(this).getPDFResult(String.valueOf(UserInfoManager.getInstance().getUserId()), 1, adapter.getShowList().size());
        });
        binding.tvWordSet.setOnClickListener(v->{
            //设置
            startActivityForResult(new Intent(this, WordSetActivity.class),1);
        });

        //根据包名进行判断(样式)
        if (getPackageName().equals(Constant.package_conceptStory)
                ||getPackageName().equals(Constant.package_nce)){
            binding.tvWordPdf.setTextColor(Color.parseColor("#5468FF"));
            binding.tvWordSet.setTextColor(Color.parseColor("#5468FF"));
        }
    }

    /******************************播放器***************************/
    private void initPlayer(){
        audioPlayer = new ExoPlayer.Builder(this).build();
        audioPlayer.setPlayWhenReady(false);
        audioPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        playAudio(null);
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseAudio();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(WordNoteActivity.this,"加载播放音频失败");
            }
        });
    }

    private void playAudio(String playUrl){
        if (!NetStatusUtil.isConnected(this)){
            ToastUtil.showToast(this,"请链接网络后重试");
            return;
        }

        if (TextUtils.isEmpty(playUrl)){
            audioPlayer.play();
        }else {
            MediaItem mediaItem = MediaItem.fromUri(playUrl);
            audioPlayer.setMediaItem(mediaItem);
            audioPlayer.prepare();

            //设置当前播放的
            curPlayWordUrl = playUrl;
        }
    }

    private void pauseAudio(){
        if (audioPlayer!=null && audioPlayer.isPlaying()){
            audioPlayer.pause();
        }
    }

    /******************************刷新数据*************************/
    //刷新数据
    private void refreshData(){
        presenter.getWordNoteData(showIndex,pageCount);
    }

    //删除数据
    private void deleteData(String word){
        startLoading("正在删除单词～");

        presenter.deleteWordData(word,false);
    }

    /****************************回调******************************/
    @Override
    public void onWordShow(List<Word_note.TempWord> list, String showMsg) {
        if (list==null){
            binding.refreshLayout.finishRefresh(false);
            binding.refreshLayout.finishLoadMore(false);
            ToastUtil.showToast(this,showMsg);

            //判断数据显示
            if (showIndex==1){
                showAbilityVisible(false);
            }
            return;
        }

        //关闭刷新
        binding.refreshLayout.finishRefresh(true);
        binding.refreshLayout.finishLoadMore(true);
        //将数据合并或者处理
        if (showIndex>1){
            adapter.addData(list);
            //显示功能
            showAbilityVisible(true);
            //显示编辑按钮
            binding.tvWordStatistic.setVisibility(View.VISIBLE);
        }else {
            adapter.refreshData(list);

            //如果第一个，则不显示功能
            if (list.size()==0){
                showAbilityVisible(false);
            }else {
                showAbilityVisible(true);
            }
        }
        //判断数据操作
        showIndex++;
        //显示单词数量
        binding.tvWordStatistic.setText("共"+adapter.getShowList().size()+"个单词");

        //根据当前的状态显示排序类型
        int sortFlag = ConfigManager.Instance().getWordSort();
        switch (sortFlag){
            case 0:
                //首字母
                reSortByLetter();
                break;
            case 1:
                //日期
                reSortByDate();
                break;
        }
    }

    @Override
    public void onCollectWord(boolean isSuccess, String showMsg) {
        stopLoading();

        if (!isSuccess){
            ToastUtil.showToast(this,showMsg);
            return;
        }

        //刷新单词显示
        binding.refreshLayout.autoRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            //根据当前的状态显示排序类型
            int sortFlag = ConfigManager.Instance().getWordSort();
            switch (sortFlag){
                case 0:
                    //首字母
                    reSortByLetter();
                    break;
                case 1:
                    //日期
                    reSortByDate();
                    break;
            }
            //刷新显示
            boolean isShowDef = ConfigManager.Instance().isShowDef();
            adapter.setShowDef(isShowDef);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.word_note)){
            //刷新生词列表
            binding.refreshLayout.autoRefresh();
        }
    }

    /*********************************其他方法*********************************/
    //显示操作功能
    private void showAbilityVisible(boolean isShow){
        if (isShow){
            binding.tvWordPdf.setVisibility(View.VISIBLE);
            binding.tvWordSet.setVisibility(View.VISIBLE);
        }else {
            binding.tvWordPdf.setVisibility(View.GONE);
            binding.tvWordSet.setVisibility(View.GONE);
        }
    }

    //重排数据-将数据按照字母顺序排列
    private void reSortByLetter(){
        //获取当前的数据
        List<Word_note.TempWord> showList = adapter.getShowList();
        //重新排列
        Collator collator = Collator.getInstance(Locale.ENGLISH);
        Collections.sort(showList, new Comparator<Word_note.TempWord>() {
            @Override
            public int compare(Word_note.TempWord o1, Word_note.TempWord o2) {
                return collator.compare(o1.word,o2.word);
            }
        });
        //刷新数据显示
        adapter.refreshData(showList);
    }

    //重排数据-将数据按照日期排列
    private void reSortByDate(){
        //获取当前的数据
        List<Word_note.TempWord> showList = adapter.getShowList();
        //重新排列
        Collator collator = Collator.getInstance();
        Collections.sort(showList, new Comparator<Word_note.TempWord>() {
            @Override
            public int compare(Word_note.TempWord o1, Word_note.TempWord o2) {
                return collator.compare(o2.createDate,o1.createDate);
            }
        });
        //刷新数据显示
        adapter.refreshData(showList);
    }

    //弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
