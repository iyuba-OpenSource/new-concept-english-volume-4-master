package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemSearchSentenceBinding;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.bean.SearchSentenceBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.AudioHelpUtil;
import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
import com.iyuba.conceptEnglish.util.ResultParse;
import com.iyuba.conceptEnglish.widget.RoundProgressBar;
import com.iyuba.configation.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/11/16 16:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SearchSentenceAdapter extends RecyclerView.Adapter<SearchSentenceAdapter.SentenceHolder> {

    private Context context;
    private List<SearchSentenceBean> list;

    //选中的位置
    private int selectIndex = 0;
    //选中的样式
    private SentenceHolder selectHolder;
    //选中的句子数据
    private SearchSentenceBean selectSentenceData;
    //选中的评测数据
    private VoaSound selectEvalData;

    //需要分享的id
    private Map<Integer,String> shareMap = new HashMap<>();

    public SearchSentenceAdapter(Context context, List<SearchSentenceBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SentenceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchSentenceBinding binding = ItemSearchSentenceBinding.inflate(LayoutInflater.from(context),parent,false);
        return new SentenceHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        SearchSentenceBean bean = list.get(position);
        holder.indexView.setText(String.valueOf(position+1));
        holder.title.setText(HelpUtil.transTitleStyle(bean.getTitle()));
        holder.titleCn.setText(bean.getTitleCn());

        //查询当前的评测数据进行显示
        int itemId = Integer.parseInt(bean.getVoaId() + "" + bean.getParaId()+""+bean.getIdIndex());
        VoaSound voaSound = new VoaSoundOp(context).findDataByItemIdAndType(bean.getLessonType(),itemId);
        if (voaSound!=null){
            holder.evalView.setVisibility(View.VISIBLE);
            holder.publishView.setVisibility(View.VISIBLE);

            if (shareMap.get(itemId)==null){
                holder.shareView.setVisibility(View.INVISIBLE);
            }else {
                holder.shareView.setVisibility(View.VISIBLE);
            }

            //设置文本显示
            String[] floats = voaSound.wordScore.split(",");
            holder.title.setText(ResultParse.getSenResultLocal(floats, bean.getTitle()));
            setReadScoreViewContent(holder, voaSound.totalScore);
        }else {
            holder.evalView.setVisibility(View.INVISIBLE);
            holder.publishView.setVisibility(View.INVISIBLE);
            holder.shareView.setVisibility(View.INVISIBLE);
        }

        if (position == selectIndex){
            holder.lineView.setVisibility(View.VISIBLE);
            holder.bottomView.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(bean.getAudioUrl())){
                holder.playView.setVisibility(View.INVISIBLE);
            }else {
                holder.playView.setVisibility(View.VISIBLE);
            }

            //设置选中
            selectSentenceData = bean;
            selectEvalData = voaSound;
            selectHolder = holder;
            selectIndex = position;
        }else {
            holder.lineView.setVisibility(View.GONE);
            holder.bottomView.setVisibility(View.GONE);
        }

        holder.frontView.setOnClickListener(v->{
            if (selectIndex==position){
                return;
            }

            if (onSentenceItemListener!=null){
                onSentenceItemListener.onSwitchItem(position);
            }
        });
        holder.playView.setOnClickListener(v->{
            //这里处理下，结束时间增加300L,发现时间不太对

            if (onSentenceItemListener!=null){
                onSentenceItemListener.onAudioPlay(bean.getAudioUrl(),bean.getStartTime(),bean.getEndTime()+300L);
            }
        });
        holder.recordView.setOnClickListener(v->{
            if (onSentenceItemListener!=null){
                onSentenceItemListener.onRecordEval(position,getEvalRecordPath(bean),bean.getEndTime()-bean.getStartTime());
            }
        });
        holder.evalView.setOnClickListener(v->{
            //这里处理下远程和本地的处理。本地存在则用本地，本地不存在则用远程
            String playUrl = null;
            if (selectEvalData!=null&&!TextUtils.isEmpty(selectEvalData.filepath)){
                playUrl = selectEvalData.filepath;
            }else {
                playUrl = AudioHelpUtil.getConceptAudioEvalUrl(context,bean.getLessonType(),bean.getVoaId(),bean.getIdIndex());
            }

            if (onSentenceItemListener!=null){
                onSentenceItemListener.onEvalPlay(playUrl);
            }
        });
        holder.publishView.setOnClickListener(v->{
            int score = 0;
            String evalAudioUrl = null;
            if (voaSound!=null){
                score = voaSound.totalScore;
                evalAudioUrl = voaSound.sound_url;
            }

            if (onSentenceItemListener!=null){
                onSentenceItemListener.onPublish(bean.getVoaId(),bean.getParaId(), bean.getIdIndex(), score,evalAudioUrl);
            }
        });
        holder.shareView.setOnClickListener(v->{
            if (onSentenceItemListener!=null){
                onSentenceItemListener.onShare(bean.getVoaId(),bean.getTitle(),voaSound.sound_url,shareMap.get(itemId));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class SentenceHolder extends RecyclerView.ViewHolder{

        private LinearLayout frontView;
        private TextView indexView;
        private TextView title;
        private TextView titleCn;

        private ImageView lineView;

        private LinearLayout bottomView;
        private RoundProgressBar playView;
        private RoundProgressBar recordView;
        private RoundProgressBar evalView;
        private ImageView publishView;
        private ImageView shareView;
        private TextView scoreView;

        public SentenceHolder(ItemSearchSentenceBinding binding){
            super(binding.getRoot());

            frontView = binding.frontView;
            indexView = binding.textIndex;
            title = binding.senEn;
            titleCn = binding.senZh;

            lineView = binding.sepLine;

            bottomView = binding.bottomView;
            playView = binding.senPlay;
            recordView = binding.senIRead;
            evalView = binding.senReadPlay;
            publishView = binding.senReadSend;
            shareView = binding.senReadShare;
            scoreView = binding.senReadResult;
        }
    }

    //相关的接口回调
    private OnSentenceItemListener onSentenceItemListener;

    public interface OnSentenceItemListener{
        //切换类型
        void onSwitchItem(int position);
        //原音播放
        void onAudioPlay(String audioUrl,long startTime,long endTime);
        //录音评测
        void onRecordEval(int selectPosition,String savePath,long totalTime);
        //评测播放
        void onEvalPlay(String audioUrl);
        //发布音频
        void onPublish(int voaId,String paraId,String idIndex,int score,String evalAudioUrl);
        //分享链接
        void onShare(int voaId,String sentence,String evalAudioUrl,String shareId);
    }

    public void setOnSentenceItemListener(OnSentenceItemListener onSentenceItemListener) {
        this.onSentenceItemListener = onSentenceItemListener;
    }

    //刷新数据
    public void refreshData(List<SearchSentenceBean> refreshList){
        this.list = refreshList;
        this.selectIndex = 0;
        this.selectHolder = null;
        this.selectSentenceData = null;
        this.selectEvalData = null;
        this.shareMap.clear();

        notifyDataSetChanged();
    }

    //设置分享的id
    public void setShareData(String shareId){
        int itemId = Integer.parseInt(selectSentenceData.getVoaId()+""+selectSentenceData.getIdIndex());
        shareMap.put(itemId,shareId);
        //刷新显示
        notifyDataSetChanged();
    }

    /**************************相关方法**************************/
    //刷新选中的数据
    public void refreshIndex(int showPosition){
        this.selectIndex = showPosition;
        notifyDataSetChanged();
    }

    //刷新原文播放
    public void refreshPlay(ExoPlayer exoPlayer){
        if (selectHolder!=null&&selectSentenceData!=null){
            //获取当前的时间段
            long totalTime = selectSentenceData.getEndTime() - selectSentenceData.getStartTime();
            if (totalTime<=0){
                totalTime = 0;
            }
            //获取播放的时间段
            long progressTime = exoPlayer.getCurrentPosition() - selectSentenceData.getStartTime();
            if (progressTime<=0||totalTime<=0){
                progressTime = 0;
            }
            //计算并且刷新显示
            this.selectHolder.playView.setBackgroundResource(R.drawable.sen_stop);
            this.selectHolder.playView.setMax((int) totalTime);
            this.selectHolder.playView.setProgress((int) progressTime);

            Log.d("播放进度", totalTime+"---"+progressTime);
        }
    }

    //停止原文播放
    public void stopPlay(){
        if (selectHolder!=null){
            this.selectHolder.playView.setProgress(0);
            this.selectHolder.playView.setBackgroundResource(R.drawable.sen_play);
        }
    }

    //刷新录音评测
    public void refreshRecordEval(int audioDb){
        if (selectHolder!=null){
            selectHolder.recordView.setBackgroundResource(R.drawable.sen_i_stop);
            selectHolder.recordView.setMax(100);
            selectHolder.recordView.setProgress(audioDb);
        }
    }

    //停止录音评测
    public void stopRecord(){
        if (selectHolder!=null){
            selectHolder.recordView.setProgress(0);
            selectHolder.recordView.setBackgroundResource(R.drawable.sen_i_read);
        }
    }

    //开启评测播放
    public void startEvalPlay(long audioMaxLength,long curProgress){
        if (selectHolder!=null){
            selectHolder.evalView.setMax((int) audioMaxLength);
            selectHolder.evalView.setProgress((int) curProgress);
            selectHolder.evalView.setBackgroundResource(R.drawable.sen_stop);
        }
    }

    //关闭评测播放
    public void stopEvalPlay(){
        if (selectHolder!=null){
            selectHolder.evalView.setProgress(0);
            selectHolder.evalView.setBackgroundResource(R.drawable.play_ok);
        }
    }

    /***********************辅助方法****************************/
    private void setReadScoreViewContent(SentenceHolder viewHolder, int score) {
        if (score < 50) {
            viewHolder.scoreView.setText("");
            viewHolder.scoreView.setBackgroundResource(R.drawable.sen_score_lower60);
        } else if (score > 80) {
            viewHolder.scoreView.setText(score + "");
            viewHolder.scoreView.setBackgroundResource(R.drawable.sen_score_higher_80);
        } else {
            viewHolder.scoreView.setText(score + "");
            viewHolder.scoreView.setBackgroundResource(R.drawable.sen_score_60_80);
        }
    }

    public String getEvalRecordPath(SearchSentenceBean bean) {
        switch (bean.getLessonType()){
            case TypeLibrary.BookType.conceptFourUS:
                return Constant.getsimRecordAddr(context) + "/" + bean.getVoaId() + bean.getIdIndex() + ".mp3";
            case TypeLibrary.BookType.conceptFourUK:
                return Constant.getsimRecordAddr(context) + "/" + (bean.getVoaId() * 10) + bean.getIdIndex() + ".mp3";
            case TypeLibrary.BookType.conceptJunior:
                return Constant.getsimRecordAddr(context) + "/" + bean.getVoaId() + bean.getIdIndex() + ".mp3";
            default:
                return Constant.getsimRecordAddr(context) + "/" + bean.getVoaId() + bean.getIdIndex() + ".mp3";
        }
    }

    //获取选中的数据
    public SearchSentenceBean getSelectData(){
        return selectSentenceData;
    }

    //获取选中的位置
    public int  getSelectIndex(){
        return selectIndex;
    }
}
