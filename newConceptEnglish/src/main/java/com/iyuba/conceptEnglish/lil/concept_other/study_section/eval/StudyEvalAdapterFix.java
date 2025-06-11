//package com.iyuba.conceptEnglish.lil.concept_other.study_section.eval;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.text.SpannableStringBuilder;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.gson.Gson;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.databinding.ItemEvalFragmentBinding;
//import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_result;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.EvalEntity_concept;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
//import com.iyuba.conceptEnglish.util.ResultParse;
//import com.iyuba.conceptEnglish.widget.RoundProgressBar;
//import com.iyuba.core.lil.user.UserInfoManager;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class StudyEvalAdapterFix extends RecyclerView.Adapter<StudyEvalAdapterFix.EvalHolder> {
//
//
//    private Context context;
//    private List<VoaDetail> list;
//
//    //通用的数据
//    private int curUseVoaId;
//    private String curBookType;
//
//
//    //选中的数据
//    private int selectIndex = 0;
//    private EvalHolder selectHolder;
//    private VoaDetail selectShowData;
//    private EvalEntity_concept selectEvalData;
//
//    //其他内容
//    private VoaSoundOp soundDB;
//    //分享的id(用于分享操作)
//    private Map<Integer,Integer> shareMap = new HashMap<>();
//
//    public StudyEvalAdapterFix(Context context, List<VoaDetail> list) {
//        this.context = context;
//        this.list = list;
//
//        soundDB = new VoaSoundOp(context);
//    }
//
//    public void setHelpData(int voaId,String bookType){
//        this.curUseVoaId = voaId;
//        this.curBookType = bookType;
//    }
//
//    @NonNull
//    @Override
//    public EvalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ItemEvalFragmentBinding binding = ItemEvalFragmentBinding.inflate(LayoutInflater.from(context),parent,false);
//        return new EvalHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull EvalHolder holder, @SuppressLint("RecyclerView") int position) {
//        if (holder==null){
//            return;
//        }
//
//        //展示数据
//        VoaDetail showData = list.get(position);
//        holder.indexView.setText(String.valueOf(position+1));
//        holder.sentenceView.setText(showData.sentence);
//        holder.sentenceCnView.setText(showData.sentenceCn);
//
//        //分享需要有数据才行
//        holder.shareView.setVisibility(View.INVISIBLE);
//
//        //展示评测数据
//        EvalEntity_concept evalData = CommonDataManager.getConceptEvalSingleData(curUseVoaId,showData.paraId,showData.lineN,UserInfoManager.getInstance().getUserId(), curBookType);
//        if (evalData==null){
//            holder.evalView.setVisibility(View.INVISIBLE);
//            holder.publishView.setVisibility(View.INVISIBLE);
//            holder.scoreView.setVisibility(View.INVISIBLE);
//        }else {
//            holder.evalView.setVisibility(View.VISIBLE);
//            holder.publishView.setVisibility(View.VISIBLE);
//            holder.scoreView.setVisibility(View.VISIBLE);
//
//            //设置分数显示
//            double totalScore = TextUtils.isEmpty(evalData.totalScore)?0:Double.parseDouble(evalData.totalScore);
//            int showScore = (int) (totalScore*20);
//            if (showScore < 50){
//                holder.scoreView.setBackgroundResource(R.drawable.sen_score_lower60);
//                holder.scoreView.setText("");
//            }else if (showScore > 80){
//                holder.scoreView.setBackgroundResource(R.drawable.sen_score_higher_80);
//                holder.scoreView.setText(String.valueOf(evalData.totalScore));
//            }else {
//                holder.scoreView.setBackgroundResource(R.drawable.sen_score_60_80);
//                holder.scoreView.setText(String.valueOf(showScore));
//            }
//            //文本显示
//            String[] scoreArray = evalData.wordScore.split(",");
//            SpannableStringBuilder showTextStyle = ResultParse.getSenResultLocal(scoreArray, showData.sentence);
//            holder.sentenceView.setText(showTextStyle);
//        }
//
//        //展示分享按钮
//        String itemId = curUseVoaId+"-"+showData.paraId+"-"+showData.lineN+"-"+UserInfoManager.getInstance().getUserId();
//        if (shareMap.get(itemId)==null){
//            holder.shareView.setVisibility(View.INVISIBLE);
//        }else {
//            holder.shareView.setVisibility(View.VISIBLE);
//        }
//
//        //设置选中
//        if (selectIndex == position){
//            selectIndex = position;
//            selectHolder = holder;
//            selectShowData = showData;
//            selectEvalData = evalData;
//
//            holder.bottomView.setVisibility(View.VISIBLE);
//        }else {
//            holder.bottomView.setVisibility(View.GONE);
//        }
//
//        //设置点击事件
//        holder.itemView.setOnClickListener(v->{
//            if (selectIndex == position){
//                return;
//            }
//
//            if (onEvalItemClickListener!=null){
//                onEvalItemClickListener.onItem(position);
//            }
//        });
//        holder.playView.setOnClickListener(v->{
//            long startTime = (long) (selectShowData.startTime*1000L);
//            long endTime = (long) (selectShowData.endTime*1000L);
//
//            if (onEvalItemClickListener!=null){
//                onEvalItemClickListener.onPlayAudio(startTime,endTime);
//            }
//        });
//        holder.recordView.setOnClickListener(v->{
//            long startTime = (long) (selectShowData.startTime*1000L);
//            long endTime = (long) (selectShowData.endTime*1000L);
//            long recordTime = endTime - startTime;
//
//            if (onEvalItemClickListener!=null){
//                onEvalItemClickListener.onRecordAudio(curUseVoaId,selectShowData.paraId,selectShowData.lineN,selectShowData.sentence,recordTime);
//            }
//        });
//        holder.evalView.setOnClickListener(v->{
//            if (onEvalItemClickListener!=null){
//                onEvalItemClickListener.onEvalAudio(selectEvalData.evalAudioUrl,curUseVoaId,selectShowData.lineN);
//            }
//        });
//        holder.publishView.setOnClickListener(v->{
//            if (onEvalItemClickListener!=null){
//                onEvalItemClickListener.onPublishEval(curUseVoaId,selectShowData.paraId,selectShowData.lineN,String.valueOf(selectEvalData.totalScore),selectEvalData.evalAudioUrl);
//            }
//        });
//        holder.shareView.setOnClickListener(v->{
//            int shareItemId = Integer.parseInt(curUseVoaId+""+selectShowData.paraId+""+selectShowData.lineN);
//
//            if (onEvalItemClickListener!=null){
//                onEvalItemClickListener.onShareSentence(curUseVoaId,shareMap.get(shareItemId),selectEvalData.evalAudioUrl,selectIndex,selectShowData.sentence);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return list==null?0:list.size();
//    }
//
//    class EvalHolder extends RecyclerView.ViewHolder{
//
//        private TextView indexView;
//        private TextView sentenceView;
//        private TextView sentenceCnView;
//
//        private ViewGroup bottomView;
//        private RoundProgressBar playView;
//        private RoundProgressBar recordView;
//        private RoundProgressBar evalView;
//
//        private ImageView publishView;
//        private ImageView shareView;
//        private TextView scoreView;
//
//        public EvalHolder(ItemEvalFragmentBinding binding){
//            super(binding.getRoot());
//
//            indexView = binding.senIndex;
//            sentenceView = binding.senEn;
//            sentenceCnView = binding.senZh;
//
//            bottomView = binding.bottomView;
//            playView = binding.senPlay;
//            recordView = binding.senIRead;
//            evalView = binding.senReadPlaying;
//            publishView = binding.senReadSend;
//            shareView = binding.senReadCollect;
//            scoreView = binding.senReadResult;
//        }
//    }
//
//    /*******************************刷新数据**********************************/
//    //刷新数据
//    public void refreshData(List<VoaDetail> refreshList){
//        this.list = refreshList;
//        notifyDataSetChanged();
//    }
//
//    //刷新item样式
//    public void refreshItem(int position){
//        this.selectIndex = position;
//        notifyDataSetChanged();
//    }
//
//    //刷新播放进度显示
//    public void refreshAudioPlay(long progressTime,long totalTime,boolean isPlay){
//        if (selectHolder==null){
//            return;
//        }
//
//        if (isPlay){
//            selectHolder.playView.setBackgroundResource(R.drawable.sen_stop);
//            selectHolder.playView.setMax((int) totalTime);
//            if (progressTime<=0){
//                progressTime = 0;
//            }
//            selectHolder.playView.setProgress((int) progressTime);
//        }else {
//            selectHolder.playView.setBackgroundResource(R.drawable.sen_play);
//            selectHolder.playView.setProgress(0);
//        }
//    }
//
//    //刷新评测进度显示
//    public void refreshEvalPlay(long progressTime,long totalTime,boolean isPlay){
//        if (selectHolder==null){
//            return;
//        }
//
//        if (isPlay){
//            selectHolder.evalView.setBackgroundResource(R.drawable.sen_stop);
//            selectHolder.evalView.setMax((int) totalTime);
//            if (progressTime<=0){
//                progressTime = 0;
//            }
//            selectHolder.evalView.setProgress((int) progressTime);
//        }else {
//            selectHolder.evalView.setBackgroundResource(R.drawable.play_ok);
//            selectHolder.evalView.setProgress(0);
//        }
//    }
//
//    //刷新录音音量
//    public void refreshRecordVolume(long recordVolume,boolean isRecord){
//        if (selectHolder==null){
//            return;
//        }
//
//        if (isRecord){
//            selectHolder.recordView.setBackgroundResource(R.drawable.sen_i_stop);
//            selectHolder.recordView.setMax(100);
//            selectHolder.recordView.setProgress((int) recordVolume);
//        }else {
//            selectHolder.recordView.setBackgroundResource(R.drawable.sen_i_read);
//            selectHolder.recordView.setProgress(0);
//        }
//    }
//
//    //刷新评测结果
//    public void refreshEvalResult(Concept_eval_result sentenceData,String evalPath){
//        List<Concept_eval_result.WordsDTO> wordList = sentenceData.getWords();
//        //合并单词数据
//        StringBuffer wordArrayBuffer = new StringBuffer();
//        for (int i = 0; i < wordList.size(); i++) {
//            Concept_eval_result.WordsDTO wordsDTO = wordList.get(i);
//            wordArrayBuffer.append(wordsDTO.getScore());
//            if (i < wordList.size()){
//                wordArrayBuffer.append(",");
//            }
//        }
//        //保存数据到数据库
//        EvalEntity_concept entity = new EvalEntity_concept(
//                curUseVoaId,
//                selectShowData.paraId,
//                selectShowData.lineN,
//                UserInfoManager.getInstance().getUserId(),
//                curBookType,
//                wordArrayBuffer.toString(),
//                sentenceData.getSentence(),
//                String.valueOf(sentenceData.getTotal_score()),
//                String.valueOf(sentenceData.getWord_count()),
//                sentenceData.getURL(),
//                new Gson().toJson(wordList),
//                evalPath
//        );
//        CommonDataManager.saveConceptEvalSingleData(entity);
//
//        //刷新数据
//        notifyDataSetChanged();
//    }
//
//    //刷新评测后发布的分享数据
//    public void refreshPublishShareData(int shuoshuoId){
//        //保存在当前的数据中
//        int itemId = Integer.parseInt(curUseVoaId+""+selectShowData.paraId+""+selectShowData.lineN);
//        shareMap.put(itemId,shuoshuoId);
//        //刷新界面
//        notifyDataSetChanged();
//    }
//
//    /*******************************接口回调**********************************/
//    private OnEvalItemClickListener onEvalItemClickListener;
//
//    public interface OnEvalItemClickListener{
//        //点击item
//        void onItem(int position);
//
//        //播放原音
//        void onPlayAudio(long startTime,long endTime);
//
//        //录音
//        void onRecordAudio(int voaId,String paraId,String lineN,String sentence,long recordTime);
//
//        //播放评测
//        void onEvalAudio(String playUrl,int voaId,String lineN);
//
//        //发布评测
//        void onPublishEval(int voaId,String paraId,String lineN,String score,String evalAudioUrl);
//
//        //分享
//        void onShareSentence(int voaId,int shuoshuoId,String evalAudioUrl,int lessonIndex,String sentence);
//    }
//
//    public void setOnEvalItemClickListener(OnEvalItemClickListener onEvalItemClickListener) {
//        this.onEvalItemClickListener = onEvalItemClickListener;
//    }
//}
