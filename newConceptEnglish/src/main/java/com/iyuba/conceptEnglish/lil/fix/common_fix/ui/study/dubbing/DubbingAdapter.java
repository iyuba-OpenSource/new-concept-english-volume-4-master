package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.core.common.util.NumberUtil;
import com.iyuba.core.common.widget.DubbingProgressBar;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.textpage.TextPage;

import java.text.MessageFormat;
import java.util.List;

/**
 * @title:
 * @date: 2023/6/6 11:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingAdapter extends RecyclerView.Adapter<DubbingAdapter.DubbingHolder> {

    private static final int POSITION_FIRST = 65;
    private static final int POSITION_SECOND = 100;

    private Context context;
    private List<ChapterDetailBean> list;

    //视频总的时长
    private long videoTime = 0;

    //选中的位置
    private int selectIndex = 0;
    //选中的数据
    private ChapterDetailBean selectDetailBean;
    //选中的评测
    private EvalChapterBean selectEvalBean;
    //选中的样式
    private DubbingHolder selectHolder;

    public DubbingAdapter(Context context, List<ChapterDetailBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DubbingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.iyuba.lib.R.layout.item_record_talk, parent, false);
        return new DubbingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DubbingHolder holder, int position) {
        if (holder==null){
            return;
        }

        //句子数据
        ChapterDetailBean detailBean = list.get(position);
        holder.index.setText((position+1)+"/"+list.size());
        holder.sentence.setText(HelpUtil.transTitleStyle(detailBean.getSentence()));
        holder.sentenceCn.setText(detailBean.getSentenceCn());
        holder.score.setVisibility(View.INVISIBLE);

        //进度显示
        if (position < list.size()-1){
            float addTime = (float) (list.get(position+1).getTiming()-detailBean.getEndTiming());
            holder.progressBar.setAddingTime(MessageFormat.format("{0}s",NumberUtil.keepOneDecimal(addTime)));
        }else {
            float addTime = (float) (detailBean.getEndTiming()-detailBean.getTiming());
            if (videoTime>0){
                float videoTimeF = videoTime/1000.0f;
                addTime = (float) (videoTimeF - detailBean.getTiming());
            }
            holder.progressBar.setAddingTime(MessageFormat.format("{0}s", NumberUtil.keepOneDecimal(addTime/4)));
        }

        float perfectTime = (float) (detailBean.getEndTiming()-detailBean.getTiming());
        String perfectTimeStr = MessageFormat.format("{0}s",NumberUtil.keepOneDecimal(perfectTime));
        holder.progressBar.setPerfectTime(perfectTimeStr);
        holder.progressBar.setPosition(POSITION_FIRST);
        holder.time.setText(perfectTimeStr);

        //评测数据
        EvalChapterBean evalBean = DBTransUtil.transEvalSingleChapterData(CommonDataManager.getEvalChapterDataFromDB(detailBean.getTypes(),detailBean.getVoaId(),detailBean.getParaId(),detailBean.getIndexId(), String.valueOf(UserInfoManager.getInstance().getUserId())));
        if (evalBean!=null){
            holder.index.setBackgroundResource(R.drawable.index_green);
            holder.play.setVisibility(View.VISIBLE);

            holder.progressBar.setProgress(POSITION_FIRST);
            holder.progressBar.setSecondaryProgress(POSITION_SECOND);

            holder.score.setVisibility(View.VISIBLE);
            int score = (int) (evalBean.getTotalScore()*20);
            holder.score.setText(String.valueOf(score));
        }else {
            holder.index.setBackgroundResource(R.drawable.index_gray);
            holder.play.setVisibility(View.INVISIBLE);
            holder.progressBar.setProgress(0);
            holder.progressBar.setSecondaryProgress(0);

            holder.score.setVisibility(View.INVISIBLE);
        }

        //取消动画显示
        if (holder.animatorSet!=null){
            holder.animatorSet.cancel();
        }

        //设置选中的数据
        if (selectIndex == position){
            selectDetailBean = detailBean;
            selectEvalBean = evalBean;
            selectHolder = holder;

            holder.bottomLayout.setVisibility(View.VISIBLE);
        }else {
            holder.bottomLayout.setVisibility(View.GONE);
        }

        holder.topLayout.setOnClickListener(v->{
            if (selectIndex==position){
                return;
            }

            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.switchItem(position);
            }
        });
        holder.sentenceLayout.setOnClickListener(v->{
            long startTime = (long) (selectDetailBean.getTiming()*1000);
            long endTime = (long) (selectDetailBean.getEndTiming()*1000);

            Log.d("视频播放", "onBindViewHolder: --"+startTime+"----"+endTime);

            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.onPlayRead(startTime,endTime);
            }
        });
        holder.record.setOnClickListener(v->{
            float recordTime = Float.parseFloat(holder.progressBar.getPerfectTime().replace("s",""));
            float addTime = Float.parseFloat(holder.progressBar.getAddingTime().replace("s",""));
            long startTime = (long) (selectDetailBean.getTiming()*1000);
            long time = (long) ((recordTime+addTime)*1000);

            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.onRecord(startTime,time, selectDetailBean.getTypes(), selectDetailBean.getVoaId(), selectDetailBean.getParaId(), selectDetailBean.getIndexId(), selectDetailBean.getSentence());
            }
        });
        holder.play.setOnClickListener(v->{
            long startTime = (long) (selectDetailBean.getTiming()*1000);

            if (onEvalCallBackListener!=null){
                onEvalCallBackListener.onPlayEval(startTime, selectEvalBean.getUrl(), selectEvalBean.getFilepath());
            }
        });
        holder.sentence.setOnSelectListener(new TextPage.OnSelectListener() {
            @Override
            public void onSelect(String s) {
                if (onEvalCallBackListener!=null){
                    onEvalCallBackListener.onWordSelect(s);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class DubbingHolder extends RecyclerView.ViewHolder{

        private LinearLayout topLayout;
        private LinearLayout sentenceLayout;
        private TextView index;
        private TextView score;
        private TextPage sentence;
        private TextView sentenceCn;

        private LinearLayout bottomLayout;
        private DubbingProgressBar progressBar;
        private TextView time;
        private ImageView play;
        private ImageView record;

        private AnimatorSet animatorSet = new AnimatorSet();

        public DubbingHolder(View view){
            super(view);

            topLayout = view.findViewById(R.id.topLayout);
            sentenceLayout = view.findViewById(R.id.sentenceLayout);
            index = view.findViewById(R.id.tV_index);
            score = view.findViewById(R.id.voice_score);
            sentence = view.findViewById(R.id.tv_content_en);
            sentenceCn = view.findViewById(R.id.tv_content_ch);
            progressBar = view.findViewById(R.id.progress);
            time = view.findViewById(R.id.tv_time);

            bottomLayout = view.findViewById(R.id.bottom_bar);
            play = view.findViewById(R.id.iv_play);
            record = view.findViewById(R.id.iv_record);
        }
    }

    /******************回调数据************************/
    private OnEvalCallBackListener onEvalCallBackListener;

    public interface OnEvalCallBackListener{
        //切换item
        void switchItem(int nextPosition);
        //播放原音
        void onPlayRead(long startTime,long endTime);
        //录音
        void onRecord(long startTime,long time,String types,String voaId,String paraId,String idIndex,String sentence);
        //播放评测
        void onPlayEval(long startTime,String playUrl,String playPath);

        //单词选择
        void onWordSelect(String wordStr);
    }

    public void setOnEvalCallBackListener(OnEvalCallBackListener onEvalCallBackListener) {
        this.onEvalCallBackListener = onEvalCallBackListener;
    }

    /*******************刷新界面******************/
    //设置视频总时长
    public void refreshVideoTime(long videoTime){
        this.videoTime = videoTime;
        notifyDataSetChanged();
    }

    //刷新数据
    public void refreshData(List<ChapterDetailBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新选中的位置
    public void refreshIndex(int position){
        this.selectIndex = position;
        notifyDataSetChanged();
    }

    //刷新原文
    public void refreshReadPlay(boolean isPlay,long startTime,long endTime){

    }

    //刷新录音
    public void refreshRecord(boolean isRecord,long curTime,long totalTime){
//        if (isRecord){
//            //计算当前进度
//            int progress = (int) (curTime*100/totalTime);
//            if (curTime>=totalTime){
//                progress = 100;
//            }
//
//            if (progress<=POSITION_FIRST){
//                selectHolder.progressBar.setProgress(progress);
//            }else {
//                selectHolder.progressBar.setProgress(POSITION_FIRST);
//            }
//            selectHolder.progressBar.setSecondaryProgress(progress);
//        }else {
//            selectHolder.progressBar.setProgress(POSITION_FIRST);
//            selectHolder.progressBar.setSecondaryProgress(POSITION_SECOND);
//        }

        if (totalTime>0){
            int progress = (int) (curTime*100/totalTime);

            if (progress<=POSITION_FIRST){
                selectHolder.progressBar.setProgress(progress);
            }else {
                selectHolder.progressBar.setProgress(POSITION_FIRST);
            }
            selectHolder.progressBar.setSecondaryProgress(progress);
        }
    }

    //刷新评测加载
    public void refreshEvalLoading(boolean isShowLoading,boolean isSuccess){
        if (isShowLoading){
            selectHolder.score.setVisibility(View.VISIBLE);
            selectHolder.score.setBackgroundResource(R.drawable.ic_wait_64px);
            selectHolder.record.setVisibility(View.INVISIBLE);

            ValueAnimator rotateAnim = ObjectAnimator.ofFloat(selectHolder.score, "rotation", 0f, 45f);
            ValueAnimator fadeAnim = ObjectAnimator.ofFloat(selectHolder.score, "alpha", 0f, 1f, 0f);
            rotateAnim.setRepeatCount(3000);
            fadeAnim.setRepeatCount(300);
            selectHolder.animatorSet.playTogether(rotateAnim, fadeAnim);
            selectHolder.animatorSet.setDuration(1050);
            selectHolder.animatorSet.start();
        }else {
            selectHolder.record.setVisibility(View.VISIBLE);
            if (selectHolder.animatorSet!=null){
                selectHolder.animatorSet.cancel();
            }

            selectHolder.score.setRotation(0);
            selectHolder.score.setAlpha(1);
            selectHolder.score.setBackgroundResource(R.drawable.red_circle);

            if (isSuccess){
                selectHolder.score.setVisibility(View.VISIBLE);

                //这里暂时这么处理
//                notifyDataSetChanged();
                showEvalLayout();
            }else {
                selectHolder.score.setVisibility(View.INVISIBLE);
            }
        }
    }

    //刷新评测
    public void refreshEvalPlay(boolean isPlay){
        if (isPlay){
            selectHolder.play.setImageResource(R.drawable.ic_pause_talk);
        }else {
            selectHolder.play.setImageResource(R.drawable.ic_play);
        }
    }

    //针对单个样式进行处理
    private void showEvalLayout(){
        selectHolder.index.setBackgroundResource(R.drawable.index_green);
        selectHolder.play.setVisibility(View.VISIBLE);

        selectHolder.score.setVisibility(View.VISIBLE);
        selectEvalBean = DBTransUtil.transEvalSingleChapterData(CommonDataManager.getEvalChapterDataFromDB(selectDetailBean.getTypes(),selectDetailBean.getVoaId(),selectDetailBean.getParaId(),selectDetailBean.getIndexId(), String.valueOf(UserInfoManager.getInstance().getUserId())));
        int score = (int) (selectEvalBean.getTotalScore()*20);
        selectHolder.score.setText(String.valueOf(score));
    }
}
