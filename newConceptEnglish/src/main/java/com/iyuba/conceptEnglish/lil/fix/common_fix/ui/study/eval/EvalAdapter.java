package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.eval;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemFixEvalBinding;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.util.ResultParse;
import com.iyuba.conceptEnglish.widget.RoundProgressBar;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/5/24 10:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class EvalAdapter extends RecyclerView.Adapter<EvalAdapter.EvalHolder> {

    private Context context;
    private List<ChapterDetailBean> list;

    //当前选中的位置
    private int selectIndex = 0;
    //当前选中的句子数据
    private ChapterDetailBean selectDetailBean;
    //当前选中的样式
    private EvalHolder selectHolder;
    //当前选中的评测数据
    private EvalChapterBean selectEvalBean;

    //临时保存分享的url
    private Map<Integer,String> shareMap = new HashMap<>();

    public EvalAdapter(Context context, List<ChapterDetailBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EvalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFixEvalBinding binding = ItemFixEvalBinding.inflate(LayoutInflater.from(context),parent,false);
        return new EvalHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EvalHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        //句子数据
        ChapterDetailBean detailBean = list.get(position);
        int showPosition = position+1;
        holder.index.setText(String.valueOf(position+1));
        if (showPosition>=100){
            holder.index.setTextSize(12);
        }else if (showPosition>=10){
            holder.index.setTextSize(14);
        }else {
            holder.index.setTextSize(16);
        }
        holder.sentenceCn.setText(detailBean.getSentenceCn());

        //评测数据
        EvalChapterBean evalBean = DBTransUtil.transEvalSingleChapterData(CommonDataManager.getEvalChapterDataFromDB(detailBean.getTypes(), detailBean.getVoaId(), detailBean.getParaId(), detailBean.getIndexId(), String.valueOf(UserInfoManager.getInstance().getUserId())));
        if (evalBean==null){
            holder.sentence.setText(HelpUtil.transTitleStyle(detailBean.getSentence()));
            holder.eval.setVisibility(View.INVISIBLE);
            holder.publish.setVisibility(View.INVISIBLE);
            holder.score.setVisibility(View.INVISIBLE);
            holder.evalFix.setVisibility(View.INVISIBLE);
        }else {
            SpannableStringBuilder span = ResultParse.getSenResultLocal(getWordScore(evalBean.getWordList()),HelpUtil.transTitleStyle(detailBean.getSentence()));
            holder.sentence.setText(span);
            holder.publish.setVisibility(View.VISIBLE);
            holder.eval.setVisibility(View.VISIBLE);
            holder.score.setVisibility(View.VISIBLE);

            int score = (int) (evalBean.getTotalScore()*20);
            if (score<50){
                holder.score.setText("");
                holder.score.setBackgroundResource(R.drawable.sen_score_lower60);
            }else if (score>80){
                holder.score.setText(String.valueOf(score));
                holder.score.setBackgroundResource(R.drawable.sen_score_higher_80);
            }else {
                holder.score.setText(String.valueOf(score));
                holder.score.setBackgroundResource(R.drawable.sen_score_60_80);
            }

            //判断是否存在需要纠音的数据
            boolean isShowEvalFix = false;
            for (EvalChapterBean.WordBean wordBean:evalBean.getWordList()){
                float wordScore = Float.parseFloat(wordBean.getScore());
                if (wordScore<=2.5 || wordScore>=4.0){
                    isShowEvalFix = true;
                    break;
                }
            }
            if (isShowEvalFix){
                holder.evalFix.setVisibility(View.VISIBLE);
            }else {
                holder.evalFix.setVisibility(View.INVISIBLE);
            }
        }

        //分享按钮
        if (!TextUtils.isEmpty(shareMap.get(position))&& InfoHelper.getInstance().openShare()){
            holder.share.setVisibility(View.VISIBLE);
        }else {
            holder.share.setVisibility(View.INVISIBLE);
        }

        //选中数据
        if (selectIndex == position){
            selectDetailBean = detailBean;
            selectEvalBean = evalBean;
            selectHolder = holder;

            holder.bottomLayout.setVisibility(View.VISIBLE);
        }else {
            holder.bottomLayout.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v->{
            if (selectIndex == position){
                return;
            }

            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.switchItem(position);
            }
        });
        holder.play.setOnClickListener(v->{
            //播放
            if (onEvalCallBackListener!=null){
                long startTime = (long) (detailBean.getTiming()*1000L);
                long endTime = (long) (detailBean.getEndTiming()*1000L);

                if (startTime<=0){
                    startTime = 0;
                }
                if (endTime<=0){
                    endTime = 0;
                }
                onEvalCallBackListener.onPlayRead(startTime,endTime);
            }
        });
        holder.record.setOnClickListener(v->{
            //录音
            if (onEvalCallBackListener!=null){
                long startTime = (long) (detailBean.getTiming()*1000L);
                long endTime = (long) (detailBean.getEndTiming()*1000L);

                onEvalCallBackListener.onRecord(endTime-startTime, detailBean.getTypes(), detailBean.getVoaId(), detailBean.getParaId(), detailBean.getIndexId(), detailBean.getSentence());
            }
        });
        holder.eval.setOnClickListener(v->{
            //评测播放
            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.onPlayEval(evalBean.getUrl(),evalBean.getFilepath());
            }
        });
        holder.publish.setOnClickListener(v->{
            //发布
            if (onEvalCallBackListener!=null){
                int score = (int) (evalBean.getTotalScore()*20);

                onEvalCallBackListener.onPublish(detailBean.getTypes(), detailBean.getVoaId(), detailBean.getParaId(), detailBean.getIndexId(),score,evalBean.getUrl());
            }
        });
        holder.share.setOnClickListener(v->{
            //分享
            if (onEvalCallBackListener!=null){
                int totalScore = (int) (evalBean.getTotalScore()*20);

                onEvalCallBackListener.onShare(totalScore,evalBean.getUrl(),shareMap.get(position));
            }
        });
        holder.evalFix.setOnClickListener(v->{
            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.onEvalFix(selectDetailBean.getVoaId(),selectDetailBean.getParaId(),selectDetailBean.getIndexId(),selectDetailBean.getSentence(),evalBean.getWordList());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class EvalHolder extends RecyclerView.ViewHolder{

        private TextView index;
        private TextView sentence;
        private TextView sentenceCn;

        private LinearLayout bottomLayout;
        private RoundProgressBar play;
        private RoundProgressBar record;
        private RoundProgressBar eval;
        private ImageView publish;
        private ImageView share;
        private TextView score;
        private ImageView evalFix;

        public EvalHolder(ItemFixEvalBinding binding){
            super(binding.getRoot());

            index = binding.index;
            sentence = binding.sentence;
            sentenceCn = binding.sentenceCn;

            bottomLayout = binding.bottomLayout;
            play = binding.play;
            record = binding.record;
            eval = binding.eval;
            publish = binding.publish;
            share = binding.share;
            score = binding.score;
            evalFix = binding.evalFix;
        }
    }

    /******************主要功能***********************/
    //刷新数据
    public void refreshData(List<ChapterDetailBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新位置
    public void refreshIndex(int index){
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    //刷新播放功能
    public void refreshReadPlay(boolean isPlay,long playedTime,long totalTime){
        if (selectHolder==null){
            return;
        }

        if (isPlay){
            selectHolder.play.setBackgroundResource(R.drawable.sen_stop);
            if (totalTime>=0&&playedTime>=0){
                selectHolder.play.setMax((int) totalTime);
                selectHolder.play.setProgress((int) playedTime);
            }
        }else {
            selectHolder.play.setBackgroundResource(R.drawable.sen_play);
            selectHolder.play.setProgress(0);
        }
    }

    //刷新评测功能
    public void refreshEvalPlay(boolean isPlay,long playTime,long totalTime){
        if (selectHolder==null){
            return;
        }

        if (isPlay){
            selectHolder.eval.setBackgroundResource(R.drawable.sen_stop);
            selectHolder.eval.setMax((int) totalTime);
            selectHolder.eval.setProgress((int) playTime);
        }else {
            selectHolder.eval.setBackgroundResource(R.drawable.play_ok);
            selectHolder.eval.setProgress(0);
        }
    }

    //刷新录音功能
    public void refreshRecord(boolean isPlay,int volumeDB){
        if (selectHolder==null){
            return;
        }

        if (isPlay){
            selectHolder.record.setBackgroundResource(R.drawable.sen_i_stop);
            selectHolder.record.setMax(100);
            selectHolder.record.setProgress(volumeDB);
        }else {
            selectHolder.record.setBackgroundResource(R.drawable.sen_i_read);
            selectHolder.record.setProgress(0);
        }
    }

    //刷新分享的数据
    public void refreshShare(String shareId){
        shareMap.put(selectIndex,shareId);
        notifyDataSetChanged();
    }

    /******************辅助功能***********************/
    //获取成绩的集合
    private String[] getWordScore(List<EvalChapterBean.WordBean> list){
        String[] scoreArray = null;
        if (list!=null&&list.size()>0){
            scoreArray = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                EvalChapterBean.WordBean wordsBean = list.get(i);
                scoreArray[i] = wordsBean.getScore();
            }
        }
        return scoreArray;
    }

    /******************回调数据************************/
    private OnEvalCallBackListener onEvalCallBackListener;

    public interface OnEvalCallBackListener{
        //切换item
        void switchItem(int nextPosition);
        //播放原音
        void onPlayRead(long startTime,long endTime);
        //录音
        void onRecord(long time,String types,String voaId,String paraId,String idIndex,String sentence);
        //播放评测
        void onPlayEval(String playUrl,String playPath);
        //发布评测
        void onPublish(String types,String voaId,String paraId,String idIndex,int score,String evalUrl);
        //分享
        void onShare(int totalScore,String audioUrl,String shareUrl);
        //纠音
        void onEvalFix(String voaId, String paraId, String idIndex, String sentence, List<EvalChapterBean.WordBean> wordList);
    }

    public void setOnEvalCallBackListener(OnEvalCallBackListener onEvalCallBackListener) {
        this.onEvalCallBackListener = onEvalCallBackListener;
    }
}
