//package com.iyuba.conceptEnglish.adapter;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Typeface;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Html;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.balysv.materialripple.MaterialRippleLayout;
//import com.flyco.roundview.RoundTextView;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.activity.ArticleSearchActivity;
//import com.iyuba.conceptEnglish.activity.SearchActivity;
//import com.iyuba.conceptEnglish.activity.SentenceSearchActivity;
//import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
//import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
//import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
//import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
//import com.iyuba.conceptEnglish.manager.VoaDataManager;
//import com.iyuba.conceptEnglish.protocol.WordUpdateRequest;
//import com.iyuba.conceptEnglish.sqlite.mode.EvaluateBean;
//import com.iyuba.conceptEnglish.sqlite.mode.RecycleViewItemData;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
//import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
//import com.iyuba.conceptEnglish.study.StudyNewActivity;
//import com.iyuba.conceptEnglish.util.CommonUtils;
//import com.iyuba.conceptEnglish.util.EvaluateRequset;
//import com.iyuba.conceptEnglish.util.ExeProtocol;
//import com.iyuba.conceptEnglish.util.GlideUtil;
//import com.iyuba.conceptEnglish.util.GsonUtils;
//import com.iyuba.conceptEnglish.util.MediaRecordHelper;
//import com.iyuba.conceptEnglish.util.ResultParse;
//import com.iyuba.conceptEnglish.widget.Player;
//import com.iyuba.conceptEnglish.widget.RoundProgressBar;
//import com.iyuba.conceptEnglish.widget.cdialog.CustomToast;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.InfoHelper;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.common.sqlite.mode.Word;
//import com.iyuba.core.common.sqlite.op.WordOp;
//import com.iyuba.core.common.util.LogUtils;
//import com.iyuba.core.common.util.OnPlayStateChangedListener;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.discover.activity.WordContent;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.headlinelibrary.event.HeadlinePlayEvent;
//
//import org.greenrobot.eventbus.EventBus;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.OkHttpClient;
//import okhttp3.Response;
//
///**
// * Created by iyuba on 2018/12/4.
// */
//
//public class SearchWordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    public final static int ITEM_TYPE_WORD = 0;
//    public final static int ITEM_TYPE_SENTENCE = 1;
//    public final static int ITEM_TYPE_ARTICLE = 2;
//    public final static int ITEM_TYPE_MORE = 3;
//
//    private final static int SENTENCEPLAY_FALG = 0;
//    private final static int MYRECORDPLAY_FLAG = 1;
//
//    public final static int article = 0;
//    public final static int articleMore = 1; //精彩文章 + 更多
//    public final static int sentence = 2;
//    public final static int sentencMore = 3; // 精彩句子 + 更多
//
//    @SuppressLint("SimpleDateFormat")
//    private static final SimpleDateFormat before = new SimpleDateFormat("yyyy.MM.dd");
//    @SuppressLint("SimpleDateFormat")
//    private static final SimpleDateFormat today = new SimpleDateFormat("HH:mm");
//
//    private String keyWord;
//    private Context mContext;
//    private List<RecycleViewItemData> searchBeanList = new ArrayList<>();
//
//
//    //sentenceViewholder
//    private Player wordPlayer, sentencePlayer, myRecordPlayer;
//    private SentenceViewHolder sentenceViewHolder;
//    public int clilcPosition = 0;
//    private int currentPlay_sen = 0, currentPlay_record = 0;
//
//    private int SentenceId;
//    private CustomDialog mWaittingDialog;
//    private boolean isUploadVoice = false;
//    private VoaDetail bean;
//    private VoaDetail clicBean;
//    private boolean isEvaluating = false;
//    public MediaRecordHelper mediaRecordHelper;
//    private boolean isStopEvaluate = false;
//    private int sentenceAllTime;
//
//    private VoaSoundOp voaSoundOp;
//
//
//    //文章
//    private Voa voa;
//    private VoaOp voaOp;
//    private Handler articleHandler;
//    private Voa clicVoa;
//    private VoaDetailOp voaDetailOp;
//
//    Handler handlerWaitting = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what) {
//                case 0:
//                    if (mWaittingDialog.isShowing()) mWaittingDialog.dismiss();
//                    break;
//                case 1:
//                    if (!mWaittingDialog.isShowing()) mWaittingDialog.show();
//                    break;
//            }
//        }
//    };
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case SENTENCEPLAY_FALG:
//
//                    currentPlay_sen = sentencePlayer.getCurrDuration();
//                    int startTime = (int) (clicBean.startTime * 1000);
//                    int progress = (currentPlay_sen - startTime) * 100 / sentenceAllTime;
//                    if ((currentPlay_sen - startTime) >= sentenceAllTime) {
//                        handler.removeMessages(SENTENCEPLAY_FALG);
//                        sentencePlayer.pause();
//                        sentenceViewHolder.sen_play.setProgress(0);
//                        sentenceViewHolder.sen_play.setBackgroundResource(R.drawable.sen_play);
//
//                    } else {
//                        Log.e("time", currentPlay_sen + "总时间" + sentencePlayer.getDuration());
//                        sentenceViewHolder.sen_play.setBackgroundResource(R.drawable.sen_stop);
//                        sentenceViewHolder.sen_play.setProgress(progress);
//                        handler.sendEmptyMessageDelayed(SENTENCEPLAY_FALG, 300);
//                    }
//
//                    break;
//
//                case MYRECORDPLAY_FLAG:
//                    currentPlay_record = myRecordPlayer.getCurrDuration();
//                    Log.e("time", currentPlay_record + "总时间" + myRecordPlayer.getDuration());
//                    sentenceViewHolder.sen_read_play.setBackgroundResource(R.drawable.sen_stop);
//                    sentenceViewHolder.sen_read_play.setProgress(currentPlay_record * 100 / myRecordPlayer.getDuration());
//                    handler.sendEmptyMessageDelayed(MYRECORDPLAY_FLAG, 300);
//                    break;
//                case 2:
//
//                    break;
//                case 3:
//                    if (mWaittingDialog != null && !mWaittingDialog.isShowing()) {
//                        mWaittingDialog.show();
//                    }
//                    break;
//                case 4:
//                    if (mWaittingDialog != null && mWaittingDialog.isShowing()) {
//                        mWaittingDialog.dismiss();
//                    }
//                    break;
//                case 6:
//                    //测评完成
//                    break;
//                case 7:
//
//                    break;
//                case 8:
//
//                    break;
//                case 10:
//                    String addscore = String.valueOf(msg.arg1);
//                    sentenceViewHolder.sen_read_share.setVisibility(View.VISIBLE);
//                    if (addscore.equals("5")) {
//                        String mg = "语音成功发送至排行榜，恭喜您获得了" + addscore + "分";
//                        CustomToast.showToast(mContext, mg, 3000);
//
//                    } else {
//                        String mg = "语音成功发送至排行榜";
//                        CustomToast.showToast(mContext, mg, 3000);
//                    }
//                    break;
//                case 11:
//                    double score_double = Double.parseDouble(clicBean.getEvaluateBean().getTotal_score());
//
//                    clicBean.isRead = true;
//                    clicBean.readResult = ResultParse.getSenResultEvaluate(clicBean.evaluateBean.getWords(), clicBean.evaluateBean.getSentence());
//                    clicBean.setReadScore((int) (score_double * 20));
//                    String wordScore = "";
//                    for (int i = 0; i < clicBean.evaluateBean.getWords().size(); i++) {
//                        EvaluateBean.WordsBean word = clicBean.evaluateBean.getWords().get(i);
//                        wordScore = wordScore + word.getScore() + ",";
//                    }
//                    voaSoundOp.updateWordScore(wordScore, (int) (score_double * 20), clicBean.voaId, Constant.getsimRecordAddr(mContext) + clicBean.voaId + clicBean + ".mp3", mediaRecordHelper.getRecordTime() + "", Integer.parseInt(clicBean.voaId + "" + (Integer.parseInt(clicBean.lineN) - 1)), clicBean.evaluateBean.getURL());
//
//                    CustomToast.showToast(mContext, "评测成功", 1000);
//                    notifyDataSetChanged();
//                    break;
//                case 13:
//                    ToastUtil.showToast(mContext, "录音失败，请稍后再试");
//                    break;
//                case 14:
//                    isEvaluating = false;
//                    ToastUtil.showToast(mContext, "评测失败，请稍后再试");
//                    break;
//                case 15:
//                    String data = (String) msg.obj;
//                    Log.e("sendRank", data);
//                    isEvaluating = false;
//                    clicBean.setEvaluateBean(GsonUtils.toObject(data, EvaluateBean.class));
//                    handler.sendEmptyMessage(11);
//                    break;
//                case 16:
//                    //获取录音分贝值
//                    sentenceViewHolder.sen_i_read.setProgress(mediaRecordHelper.getDB());
//                    handler.sendEmptyMessageDelayed(16, 300);
//                    break;
//            }
//        }
//    };
//
//
//    public SearchWordAdapter(Context mContext, List<RecycleViewItemData> searchBeanList) {
//        this.mContext = mContext;
//        this.searchBeanList = searchBeanList;
//        mWaittingDialog = WaittingDialog.showDialog(mContext);
//        mediaRecordHelper = new MediaRecordHelper();
//        voaDetailOp = new VoaDetailOp(mContext);
//        voaOp = new VoaOp(mContext);
//        voaSoundOp = new VoaSoundOp(mContext);
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        switch (i) {
//            case ITEM_TYPE_WORD:
//                return new WordViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_word_search, viewGroup, false));
//            case ITEM_TYPE_SENTENCE:
//                return new SentenceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_sentence, viewGroup, false));
//            case ITEM_TYPE_ARTICLE:
//                return new ArticleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_voa, viewGroup, false));
//            case ITEM_TYPE_MORE:
//                return new MoreViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_more_search, viewGroup, false));
//        }
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int i) {
//
//        //单词
//        if (holder instanceof WordViewHolder) {
//            if (isCollect()) {
//                ((WordViewHolder) holder).collect.setImageResource(R.drawable.headline_collect);
//            } else {
//                ((WordViewHolder) holder).collect.setImageResource(R.drawable.headline_collect_not);
//            }
//            final Word word = (Word) searchBeanList.get(i).getT();
//            ((WordViewHolder) holder).ll_am_speaker_right.setVisibility(View.GONE);
//            ((WordViewHolder) holder).ll_am_speaker.setVisibility(View.GONE);
//
//            if (TextUtils.isEmpty(word.pron)) {
//                ((WordViewHolder) holder).ll_en_speaker.setVisibility(View.GONE);
//            } else {
//                ((WordViewHolder) holder).ll_en_speaker.setVisibility(View.VISIBLE);
//                Log.e("单词音标", word.pron);
//
//                Typeface mFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/segoeui.ttf");
//                ((WordViewHolder) holder).en_pron.setTypeface(mFace);
//
//                ((WordViewHolder) holder).en_pron.setText(Html.fromHtml("[" + word.pron + "]"));
//
//            }
//            ((WordViewHolder) holder).word_key.setText(word.key);
//            ((WordViewHolder) holder).word_def.setText(word.def.trim() + "");
//            ((WordViewHolder) holder).rootView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    pauseAllPlayer(0);
//                    Intent intent = new Intent(mContext, WordContent.class);
//                    intent.putExtra("word", word.key);
//                    mContext.startActivity(intent);
//                }
//            });
//
//            ((WordViewHolder) holder).ll_en_speaker.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (wordPlayer == null) {
//                        wordPlayer = new Player(mContext, null);
//                    }
//                    wordPlayer.playUrl(word.audioUrl);
//                }
//            });
//
//            ((WordViewHolder) holder).collect.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //登录判断
//                    if (!UserInfoManager.getInstance().isLogin()){
//                        LoginUtil.startToLogin(mContext);
//                        return;
//                    }
//
//                    if (isCollect()) {
//                        //删除收藏
//                        new WordOp(mContext).tryToDeleteItemWord("\'" + keyWord + "\'", String.valueOf(UserInfoManager.getInstance().getUserId()));
//                        CustomToast.showToast(mContext, "单词已从生词本中删除！", 1000);
//                        ((WordViewHolder) holder).collect.setImageResource(R.drawable.headline_collect_not);
//                        deleteNetwordWord(keyWord);
//                    } else {
//                        //添加收藏
//                        Word word1 = new Word();
//                        word1.key = keyWord;
//                        word1.userid = String.valueOf(UserInfoManager.getInstance().getUserId());
//                        word1.delete = "0";
//                        word1.audioUrl = word.audioUrl;
//                        word1.pron = word.pron;
//                        word1.def = word.def;
//
//                        new WordOp(mContext).saveData(word1);
//                        CustomToast.showToast(mContext, "成功加入生词本！", 1000);
//                        ((WordViewHolder) holder).collect.setImageResource(R.drawable.headline_collect);
//                        addNetwordWord(word1.key);
//                    }
//                }
//            });
//        }
//
//        //文章
//        if (holder instanceof ArticleViewHolder) {
//            final Voa curVoa = (Voa) searchBeanList.get(i).getT();
//            ((ArticleViewHolder) holder).deleteBox.setVisibility(View.GONE);
//
//            int index = curVoa.voaId % 1000;
//
//            //原版数据
////            ((ArticleViewHolder) holder).voa.setText("Lesson");
////            ((ArticleViewHolder) holder).voaN.setText(index + "");
//            //新版数据
//            if (TextUtils.isEmpty(curVoa.pic)){
//                ((ArticleViewHolder) holder).voaPic.setVisibility(View.GONE);
//                ((ArticleViewHolder) holder).voaIndex.setVisibility(View.VISIBLE);
//
//                if (curVoa.category>10) {
//                    ((ArticleViewHolder) holder).voaIndex.setText(CommonUtils.getUnitFromTitle(curVoa.title) + "");
//                } else {
//                    ((ArticleViewHolder) holder).voaIndex.setText(index + "");
//                }
//            }else {
//                ((ArticleViewHolder) holder).voaPic.setVisibility(View.VISIBLE);
//                ((ArticleViewHolder) holder).voaIndex.setVisibility(View.GONE);
//
//                GlideUtil.setImage(curVoa.pic,mContext,R.drawable.shape_btn_bg, ((ArticleViewHolder) holder).voaPic);
//            }
//
//            ((ArticleViewHolder) holder).title.setText(HelpUtil.transTitleStyle(curVoa.title));
//            ((ArticleViewHolder) holder).titleCn.setText(curVoa.titleCn);
//
//            ((ArticleViewHolder) holder).mCircleProgressBar.setVisibility(View.GONE);
//
//            //文章点击
//            ((ArticleViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //隐藏掉后台播放
//                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_hide));
//
//                    clicVoa = (Voa) searchBeanList.get(i).getT();
//                    getVoaDetail();
//                }
//            });
//
//        }
//
//        //查看更多
//        if (holder instanceof MoreViewHolder) {
//
//            final int currentType = (int) searchBeanList.get(i).getT();
//
//            switch (currentType) {
//                case article:
//                    ((MoreViewHolder) holder).type_text.setText("精彩文章");
//                    ((MoreViewHolder) holder).image_more.setVisibility(View.GONE);
//                    break;
//                case articleMore:
//                    ((MoreViewHolder) holder).type_text.setText("精彩文章");
//                    ((MoreViewHolder) holder).image_more.setVisibility(View.VISIBLE);
//                    break;
//                case sentence:
//                    ((MoreViewHolder) holder).type_text.setText("精彩句子");
//                    ((MoreViewHolder) holder).image_more.setVisibility(View.GONE);
//                    break;
//                case sentencMore:
//                    ((MoreViewHolder) holder).type_text.setText("精彩句子");
//                    ((MoreViewHolder) holder).image_more.setVisibility(View.VISIBLE);
//                    break;
//            }
//
//
//            ((MoreViewHolder) holder).rootView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    pauseAllPlayer(0);
//                    if (currentType == articleMore) {
//                        Intent intent = new Intent(mContext, ArticleSearchActivity.class);
//                        intent.putExtra("keyword", keyWord);
//                        mContext.startActivity(intent);
//                    } else if (currentType == sentencMore) {
//                        Intent intent = new Intent(mContext, SentenceSearchActivity.class);
//                        intent.putExtra("keyword", keyWord);
//                        mContext.startActivity(intent);
//                    }
//                }
//            });
//        }
//
//        //句子
//        if (holder instanceof SentenceViewHolder) {
//
//            bean = (VoaDetail) searchBeanList.get(i).getT();
//            if (bean.getRealIndex() != 0) {
//                ((SentenceViewHolder) holder).text_index.setText(bean.getRealIndex() + "");
//            } else {
//                ((SentenceViewHolder) holder).text_index.setText(i + 1 + "");
//            }
//
//            ((SentenceViewHolder) holder).sen_en.setText(bean.sentence.trim());
//            ((SentenceViewHolder) holder).sen_zh.setText(bean.sentenceCn);
//            ((SentenceViewHolder) holder).sen_read_share.setVisibility(View.GONE);
//
//            if (clilcPosition == i) {
//                Log.e("clic====", i + "----" + clilcPosition + "---" + bean.getRealIndex());
//                sentenceViewHolder = (SentenceViewHolder) holder;
//                clicBean = bean;
//                ((SentenceViewHolder) holder).sen_play.setProgress(0);
//                ((SentenceViewHolder) holder).bottom_view.setVisibility(View.VISIBLE);
//                ((SentenceViewHolder) holder).sep_line.setVisibility(View.VISIBLE);
//            } else {
//                ((SentenceViewHolder) holder).bottom_view.setVisibility(View.GONE);
//                ((SentenceViewHolder) holder).sep_line.setVisibility(View.GONE);
//                ((SentenceViewHolder) holder).sen_play.setBackgroundResource(R.drawable.sen_play);
//                ((SentenceViewHolder) holder).sen_play.setProgress(0);
//                ((SentenceViewHolder) holder).sen_read_play.setBackgroundResource(R.drawable.play_ok);
//                ((SentenceViewHolder) holder).sen_read_play.setProgress(0);
//            }
//            final VoaSound voaSound = voaSoundOp.findDataById(Integer.parseInt(bean.voaId + "" + (Integer.parseInt(bean.lineN) - 1)));
//
//            if (voaSound != null && voaSound.sound_url != null && !"".equals(voaSound.sound_url)) {
//                LogUtils.e("单句===" + voaSound.sound_url);
//                bean.isRead = true;
//                String[] floats = voaSound.wordScore.split(",");
//                bean.setReadScore(voaSound.totalScore);
//                bean.readResult = ResultParse.getSenResultLocal(floats, bean.sentence);
//                bean.setEvaluateBean(new EvaluateBean(voaSound.sound_url));
//                bean.evaluateBean.setURL(voaSound.sound_url);
//            }
//
//            if (bean.isRead) {
//                ((SentenceViewHolder) holder).sen_read_play.setVisibility(View.VISIBLE);
//                ((SentenceViewHolder) holder).sen_read_result.setVisibility(View.VISIBLE);
//                ((SentenceViewHolder) holder).sen_en.setText(bean.readResult);
//                ((SentenceViewHolder) holder).sen_read_send.setVisibility(View.VISIBLE);
//                setReadScoreViewContent((SentenceViewHolder) holder, bean.getReadScore());
//
//                if (bean.getShuoshuoId() != 0) {
//                    ((SentenceViewHolder) holder).sen_read_share.setVisibility(View.VISIBLE);
//                }
//            } else {
//                ((SentenceViewHolder) holder).sen_read_play.setVisibility(View.GONE);
//                ((SentenceViewHolder) holder).sen_read_send.setVisibility(View.GONE);
//                ((SentenceViewHolder) holder).sen_read_result.setVisibility(View.GONE);
//            }
//
//
//            ((SentenceViewHolder) holder).rootView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (clilcPosition == holder.getAdapterPosition()) {
//                        return;
//                    }
//                    currentPlay_sen = 0;
//                    currentPlay_record = 0;
//                    clilcPosition = holder.getAdapterPosition();
//                    Log.e("tag", holder.getAdapterPosition() + "");
//                    if (sentencePlayer != null && sentencePlayer.isPlaying()) {
//                        sentencePlayer.pause();
//                        handler.removeMessages(SENTENCEPLAY_FALG);
//                    }
//                    if (myRecordPlayer != null && myRecordPlayer.isPlaying()) {
//                        myRecordPlayer.pause();
//                        handler.removeMessages(MYRECORDPLAY_FLAG);
//                    }
//                    if (mediaRecordHelper != null && mediaRecordHelper.isRecording) {
//                        mediaRecordHelper.stop_record();
//                        handler.removeMessages(16);
//                        sentenceViewHolder.sen_i_read.setProgress(0);
//                        sentenceViewHolder.sen_i_read.setBackgroundResource(R.drawable.sen_i_read);
//
//                        isStopEvaluate = false;
//                    }
//                    notifyDataSetChanged();
//                }
//            });
//
//            //句子暂时去掉评测功能
//            //播放原文
//            ((SentenceViewHolder) holder).sen_play.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    pauseAllPlayer(1);
////                    if (bean.getSoundText() == null || "".equals(bean.getSoundText())) {
////                        ToastUtil.showToast(mContext, "暂无本句音频");
////                        return;
////                    }
//                    if (sentencePlayer == null) {
//                        sentencePlayer = new Player(mContext, new OnPlayStateChangedListener() {
//                            @Override
//                            public void playCompletion() {
//                                resetStateSenPlay();
//                            }
//
//                            @Override
//                            public void playFaild() {
//                                int a = 0;
//                            }
//
//                            @Override
//                            public void playSuccess() {
//                                sentencePlayer.seekTo((int) (clicBean.startTime * 1000));
//                                handler.sendEmptyMessage(SENTENCEPLAY_FALG);
//                            }
//
//                            @Override
//                            public void setPlayTime(String currTime, String allTime) {
//
//                            }
//                        });
//                    }
//
//                    VoaDetail voaDetailnext = voaDetailOp.findDataByLineId(clicBean.voaId, Integer.parseInt(clicBean.lineN) + 1);
//                    if (sentencePlayer.isPlaying()) {
//                        sentencePlayer.pause();
//                        resetStateSenPlay();
//                    } else if (currentPlay_sen > clicBean.startTime * 1000 && currentPlay_sen < voaDetailnext.startTime * 1000) {
//                        sentenceViewHolder.sen_play.setBackgroundResource(R.drawable.sen_stop);
//                        sentencePlayer.start();
//                        handler.sendEmptyMessage(SENTENCEPLAY_FALG);
//                    } else {
//                        String url = null;
//                        boolean isAmerican = ConfigManager.Instance().isAmercan();
//                        if (isAmerican) {
//                            //美音
//                            url = Constant.sound + clicBean.voaId / 1000 + "_" + clicBean.voaId % 1000 + Constant.append;
//                        } else {
//                            //英音
//                            url = Constant.sound + "british/" + clicBean.voaId / 1000 + "/" + clicBean.voaId / 1000 + "_" + clicBean.voaId % 1000 + Constant.append;
//                        }
//                        //下面是之前的操作，现在不用本地的了，用接口的
////                        if (TextUtils.isEmpty(getLocalSoundPath(clicBean.voaId))) {
////
////                            boolean isAmerican = ConfigManager.Instance().isAmercan();
////                            if (isAmerican) {
////                                //美音
////                                url = Constant.sound + clicBean.voaId / 1000 + "_" + clicBean.voaId % 1000 + Constant.append;
////                            } else {
////                                //英音
////                                url = Constant.sound + "british/" + clicBean.voaId / 1000 + "/" + clicBean.voaId / 1000 + "_" + clicBean.voaId % 1000 + Constant.append;
////                            }
////
////                        } else {
////                            url = getLocalSoundPath(clicBean.voaId);
////                        }
//                        sentencePlayer.playUrl(url);
//                        if (voaDetailnext.startTime == 0) {
//                            voaDetailnext.startTime = sentencePlayer.getDuration();
//                            sentenceAllTime = (int) (voaDetailnext.startTime - clicBean.startTime * 1000);
//                        } else {
//                            sentenceAllTime = (int) ((voaDetailnext.startTime - clicBean.startTime) * 1000);
//                        }
//                    }
//                }
//            });
//
//            //播放录音
//            ((SentenceViewHolder) holder).sen_read_play.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    pauseAllPlayer(2);
//
//                    if (myRecordPlayer == null) {
//                        myRecordPlayer = new Player(mContext, new OnPlayStateChangedListener() {
//                            @Override
//                            public void playCompletion() {
//                                resetStateSenReadPlay();
//                            }
//
//                            @Override
//                            public void playFaild() {
//                            }
//
//                            @Override
//                            public void playSuccess() {
//                                handler.sendEmptyMessage(MYRECORDPLAY_FLAG);
//                            }
//
//                            @Override
//                            public void setPlayTime(String currTime, String allTime) {
//
//                            }
//                        });
//                    }
//
//
//                    if (myRecordPlayer.isPlaying()) {
//                        myRecordPlayer.pause();
//                        resetStateSenReadPlay();
//                    } else if (currentPlay_record > 0 && currentPlay_record < myRecordPlayer.getDuration()) {
//                        sentenceViewHolder.sen_read_play.setBackgroundResource(R.drawable.sen_stop);
//                        myRecordPlayer.start();
//                        handler.sendEmptyMessage(MYRECORDPLAY_FLAG);
//                    } else {
//                        myRecordPlayer.playUrl("http://voa." + Constant.IYUBA_CN + "voa/" + clicBean.getEvaluateBean().getURL());
//
//
//                    }
//                }
//            });
//
//            //语音测评
//            ((SentenceViewHolder) holder).sen_i_read.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    //使用前必须开启-存储权限--
//                    if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
//                        if (mContext instanceof SearchActivity) {
//                            ((SearchActivity) mContext).requstPerssiion();
//                        } else if (mContext instanceof SentenceSearchActivity) {
//                            ((SentenceSearchActivity) mContext).requstPerssiion();
//                        }
//                        if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})) {
//                            return;
//                        }
//                    }
//
//                    showTipDialog();
//                    pauseAllPlayer(3);
//                    if (isEvaluating) {
//                        ToastUtil.showToast(mContext, "提交数据中，请稍后");
//                        return;
//                    }
//
//                    if (!UserInfoManager.getInstance().isLogin()) {
//                        LoginUtil.startToLogin(mContext);
//                        return;
//                    }
//                    if (!isStopEvaluate) {
//                        isStopEvaluate = true;
//                        ((SentenceViewHolder) holder).sen_i_read.setBackgroundResource(R.drawable.sen_i_stop);
//                        handler.sendEmptyMessage(16);
//                        makeRootDirectory(Constant.getsimRecordAddr(mContext) + UserInfoManager.getInstance().getUserId() + "/");
//                        //MP4 文件录制
//                        mediaRecordHelper.setFilePath(Constant.getsimRecordAddr(mContext) + UserInfoManager.getInstance().getUserId() + "/" + clicBean.voaId + clicBean.lineN + ".mp4");
//                        mediaRecordHelper.recorder_Media();
//                    } else {
//                        dismissDia();
//                    }
//                }
//            });
//
//            //发送评论
//            ((SentenceViewHolder) holder).sen_read_send.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (UserInfoManager.getInstance().isLogin()) {
//                        submitEvalute();
//                    } else {
//                        ToastUtil.showToast(mContext, "请登录后在发送");
//                    }
//                }
//            });
//            //分享
//            ((SentenceViewHolder) holder).sen_read_share.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    shareRecord();
//                }
//            });
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return searchBeanList.get(position).getDataType();
//    }
//
//    @Override
//    public int getItemCount() {
//        return searchBeanList.size();
//    }
//
//    class WordViewHolder extends RecyclerView.ViewHolder {
//        TextView word_key, word_def, en_pron, am_pron, am_pron_right;
//
//        RoundTextView pron_type;
//        LinearLayout ll_en_speaker, ll_am_speaker, ll_am_speaker_right;
//        RelativeLayout rootView;
//        ImageView collect;
//
//        public WordViewHolder(View itemView) {
//            super(itemView);
//            word_key = itemView.findViewById(R.id.word_key);
//            word_def = itemView.findViewById(R.id.word_def);
//
//            pron_type = itemView.findViewById(R.id.pron_type);
//            ll_en_speaker = itemView.findViewById(R.id.ll_en_speaker);
//            ll_am_speaker = itemView.findViewById(R.id.ll_am_speaker);
//            en_pron = itemView.findViewById(R.id.en_pron);
//            am_pron = itemView.findViewById(R.id.am_pron);
//            rootView = itemView.findViewById(R.id.re_root);
//            ll_am_speaker_right = itemView.findViewById(R.id.ll_am_speaker_right);
//            am_pron_right = itemView.findViewById(R.id.am_pron_right);
//
//            collect = itemView.findViewById(R.id.collect);
//
//        }
//    }
//
//    class SentenceViewHolder extends RecyclerView.ViewHolder {
//        TextView text_index, sen_en, sen_zh, sen_read_result;
//        LinearLayout bottom_view;
//        LinearLayout rootView;
//        RoundProgressBar sen_play, sen_read_play, sen_i_read;
//        ImageView sep_line, sen_read_send, sen_read_share;
//
//        public SentenceViewHolder(View itemView) {
//            super(itemView);
//            rootView = itemView.findViewById(R.id.ll_root);
//            text_index = itemView.findViewById(R.id.text_index);
//            sen_en = itemView.findViewById(R.id.sen_en);
//            sen_zh = itemView.findViewById(R.id.sen_zh);
//            sen_play = itemView.findViewById(R.id.sen_play);
//            bottom_view = itemView.findViewById(R.id.bottom_view);
//            sen_read_play = itemView.findViewById(R.id.sen_read_play);
//            sep_line = itemView.findViewById(R.id.sep_line);
//            sen_i_read = itemView.findViewById(R.id.sen_i_read);
//            sen_read_result = itemView.findViewById(R.id.sen_read_result);
//            sen_read_send = itemView.findViewById(R.id.sen_read_send);
//            sen_read_share = itemView.findViewById(R.id.sen_read_share);
//        }
//    }
//
//    class ArticleViewHolder extends RecyclerView.ViewHolder {
////        TextView voa, voaN;
//        TextView voaIndex;
//        ImageView voaPic;
//
//        TextView title, titleCn;
//        ImageView deleteBox;
//        com.iyuba.core.common.widget.RoundProgressBar downloadedImage;
//        MaterialRippleLayout rootView;
//        com.iyuba.core.common.widget.RoundProgressBar mCircleProgressBar;
//        View downloadLayout;
//
//        public ArticleViewHolder(View itemView) {
//            super(itemView);
//            deleteBox = itemView.findViewById(R.id.checkBox_isDelete);
//
//            //原版数据
////            voa = itemView.findViewById(R.id.voa);
////            voaN = itemView.findViewById(R.id.voaN);
//            //新版数据
//            voaPic = itemView.findViewById(R.id.voa_pic);
//            voaIndex = itemView.findViewById(R.id.voa_index);
//
//            title = itemView.findViewById(R.id.title);
//            titleCn = itemView.findViewById(R.id.titleCn);
//            downloadLayout = itemView.findViewById(R.id.download_layout);
//            downloadLayout.setVisibility(View.INVISIBLE);
//            downloadedImage = itemView.findViewById(R.id.image_downloaded);
//            downloadedImage.setVisibility(View.INVISIBLE);
//            // 下载的滚动条，实际是不动的
//            mCircleProgressBar = itemView.findViewById(R.id.roundBar1);
//        }
//    }
//
//    class MoreViewHolder extends RecyclerView.ViewHolder {
//        MaterialRippleLayout rootView;
//        TextView type_text;
//        ImageView image_more;
//
//        public MoreViewHolder(View itemView) {
//            super(itemView);
//            rootView = itemView.findViewById(R.id.rl_more);
//            type_text = itemView.findViewById(R.id.type_text);
//            image_more = itemView.findViewById(R.id.image_more);
//        }
//
//    }
//
//    public String getKeyWord() {
//        return keyWord;
//    }
//
//    public void setKeyWord(String keyWord) {
//        this.keyWord = keyWord;
//    }
//
//
//    public void stopAllPlayer() {
//        if (wordPlayer != null) {
//            wordPlayer.stop();
//            wordPlayer = null;
//        }
//        if (sentencePlayer != null) {
//            sentencePlayer.stop();
//            handler.removeMessages(SENTENCEPLAY_FALG);
//            sentencePlayer = null;
//        }
//        if (myRecordPlayer != null) {
//            myRecordPlayer.stop();
//            handler.removeMessages(MYRECORDPLAY_FLAG);
//            myRecordPlayer = null;
//        }
//
//    }
//
//    public void pauseAllPlayer(int playerFlag) {
//
//        EventBus.getDefault().post(new HeadlinePlayEvent());
//        if (wordPlayer != null && wordPlayer.isPlaying()) {
//            wordPlayer.pause();
//        }
//
//        if (sentencePlayer != null && sentencePlayer.isPlaying()) {
//            if (playerFlag != 1) {
//                sentencePlayer.pause();
//                resetStateSenPlay();
//            }
//
//        }
//        if (myRecordPlayer != null && myRecordPlayer.isPlaying()) {
//            if (playerFlag != 2) {
//                myRecordPlayer.pause();
//                resetStateSenReadPlay();
//            }
//        }
//        if (mediaRecordHelper != null && mediaRecordHelper.isRecording) {
//            if (playerFlag != 3) {
//                mediaRecordHelper.stop_record();
//                handler.removeMessages(16);
//                sentenceViewHolder.sen_i_read.setProgress(0);
//                sentenceViewHolder.sen_i_read.setBackgroundResource(R.drawable.sen_i_read);
//                isStopEvaluate = false;
//            }
//
//        }
//
//
//    }
//
//    private void resetStateSenReadPlay() {
//        handler.removeMessages(MYRECORDPLAY_FLAG);
//        sentenceViewHolder.sen_read_play.setBackgroundResource(R.drawable.play_ok);
//        sentenceViewHolder.sen_read_play.setProgress(0);
//    }
//
//    private void resetStateSenPlay() {
//        handler.removeMessages(SENTENCEPLAY_FALG);
//        sentenceViewHolder.sen_play.setBackgroundResource(R.drawable.sen_play);
//        sentenceViewHolder.sen_play.setProgress(0);
//    }
//
//    private void setReadScoreViewContent(SentenceViewHolder viewHolder, int score) {
//        if (score < 50) {
//            viewHolder.sen_read_result.setText("");
//            viewHolder.sen_read_result.setBackgroundResource(R.drawable.sen_score_lower60);
//        } else if (score > 80) {
//            viewHolder.sen_read_result.setText(score + "");
//            viewHolder.sen_read_result.setBackgroundResource(R.drawable.sen_score_higher_80);
//        } else {
//            viewHolder.sen_read_result.setText(score + "");
//            viewHolder.sen_read_result.setBackgroundResource(R.drawable.sen_score_60_80);
//        }
//    }
//
//    private void shareRecord() {
//        String url;
//        if (clicBean.getEvaluateBean() != null && clicBean.getEvaluateBean().getURL() != null) {
//            url = "&addr=" + clicBean.getEvaluateBean().getURL();
//        } else {
//            url = "";
//        }
//        String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + clicBean.getShuoshuoId() + "&apptype=" + Constant.EVAL_TYPE + url;
//        String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
//        String title;
//        if ("".equals(UserInfoManager.getInstance().getUserName())) {
//            title = "[" + UserInfoManager.getInstance().getUserId() + "]" + "在" + "语音评测中获得了" + clicBean.getReadScore() + "分";
//        } else {
//            title = "[" + UserInfoManager.getInstance().getUserName() + "]" + "在" + "语音评测中获得了" + clicBean.getReadScore() + "分";
//        }
//
//        OnekeyShare oks = new OnekeyShare();
//        if (!InfoHelper.showWeiboShare()){
//            oks.addHiddenPlatform(SinaWeibo.NAME);
//        }
//        //微博飞雷神
//        // 关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        // 分享时Notification的图标和文字
//        // oks.setNotification(R.drawable.ic_launcher,
//        // getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(title);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(siteUrl);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(clicBean.sentence);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        // oks.setImagePath("/sdcard/test.jpg");
//        // imageUrl是Web图片路径，sina需要开通权限
//        oks.setImageUrl(imageUrl);
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(siteUrl);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("这款应用" + Constant.APPName + "真的很不错啊~推荐！");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(Constant.APPName);
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(siteUrl);
//        // oks.setDialogMode();
//        // oks.setSilent(false);
//        oks.setCallback(new PlatformActionListener() {
//
//            @Override
//            public void onError(Platform arg0, int arg1, Throwable arg2) {
//                Log.e("okCallbackonError", "onError");
//                Log.e("--分享失败===", arg2.toString());
//
//            }
//
//            @Override
//            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//                Log.e("okCallbackonComplete", "onComplete");
//
//            }
//
//            @Override
//            public void onCancel(Platform arg0, int arg1) {
//                Log.e("okCallbackonCancel", "onCancel");
//            }
//        });
//        // 启动分享GUI
//        oks.show(mContext);
//    }
//
//    private int getSententceNum() {
//        String[] strings = clicBean.sentence.split(" ");
//        Log.e("string.length", clicBean.sentence);
//        Log.e("string.length", strings.length + strings[0] + strings[1] + strings[2]);
//        return strings.length;
//    }
//
//    private boolean isCollect() {
//        com.iyuba.core.common.sqlite.mode.Word word = new WordOp(mContext).findDataByName(keyWord, String.valueOf(UserInfoManager.getInstance().getUserId()));
//        if (word == null) {
//            return false;
//        } else {
//            if (word.delete.equals("1")) {
//                return false;
//            } else {
//                return true;
//            }
//
//        }
//    }
//
//    private void addNetwordWord(String wordTemp) {
//        ExeProtocol.exe(new WordUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), WordUpdateRequest.MODE_INSERT, wordTemp), null);
//    }
//
//    private void deleteNetwordWord(String wordTemp) {
//        ExeProtocol.exe(new WordUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), WordUpdateRequest.MODE_DELETE, wordTemp), null);
//    }
//
//    //评测请求
//    private void setRequest() {
//        if (UserInfoManager.getInstance().isLogin()) {
//            if (isEvaluating) {
//                CustomToast.showToast(mContext, "正在评测中，请不要重复提交", 1000);
//            } else {
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//
//                        boolean isAmerican = ConfigManager.Instance().isAmercan();
//                        String currVoaId;
//                        if (isAmerican) {
//                            currVoaId = String.valueOf(clicBean.voaId);
//                        } else {
//                            currVoaId = String.valueOf(clicBean.voaId * 10);
//                        }
//
//                        Map<String, String> textParams = new HashMap<String, String>();
//
//                        File file = new File(Constant.getsimRecordAddr(mContext) + UserInfoManager.getInstance().getUserId() + "/" + clicBean.voaId + clicBean.lineN + ".mp4");
//                        textParams.put("type", Constant.EVAL_TYPE);
//                        textParams.put("userId", String.valueOf(UserInfoManager.getInstance().getUserId()));
//                        textParams.put("newsId", currVoaId);
//                        textParams.put("IdIndex", clicBean.lineN + "");
//                        textParams.put("paraId", "1");
//
//                        String urlSentence = TextAttr.encode(clicBean.sentence);
//                        urlSentence = urlSentence.replaceAll("\\+", "%20");
//                        textParams.put("sentence", urlSentence);
//                        Log.e("sentence", clicBean.sentence);
//                        if (file != null && file.exists()) {
//                            try {
//                                isEvaluating = true;
//                                EvaluateRequset.post(Constant.EVALUATE_URL_NEW, textParams, Constant.getsimRecordAddr(mContext) + UserInfoManager.getInstance().getUserId() + "/" + clicBean.voaId + clicBean.lineN + ".mp4", handler);
//                            } catch (Exception e) {
//                                isEvaluating = false;
//                                e.printStackTrace();
//                                Log.e("Exception", e.toString());
//                            }
//                        } else {
//                            isEvaluating = false;
//                            handler.sendEmptyMessage(13);
//                        }
//
//                    }
//                };
//                thread.start();
//            }
//        } else {
////            Intent intent = new Intent();
////            intent.setClass(mContext, Login.class);
////            mContext.startActivity(intent);
//            LoginUtil.startToLogin(mContext);
//        }
//    }
//
//
//    public void dismissDia() {
//        if (isStopEvaluate) {
//            //MP4 文件录制停止
//            mediaRecordHelper.stop_record();
//            isStopEvaluate = false;
//            handler.removeMessages(16);
//            sentenceViewHolder.sen_i_read.setProgress(0);
//            setRequest();
//            sentenceViewHolder.sen_i_read.setBackgroundResource(R.drawable.sen_i_read);
//        }
//    }
//
//    private void submitEvalute() {
//
//        if (isUploadVoice) {
//            CustomToast.showToast(mContext, "评测发送中，请不要重复提交", 1000);
//            return;
//        }
//        if (clicBean.getEvaluateBean() != null && clicBean.getEvaluateBean().getURL() != null && !"".equals(clicBean.getEvaluateBean().getURL())) {
//            handler.sendEmptyMessage(3);
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    boolean isAmerican = ConfigManager.Instance().isAmercan();
//                    String currVoaId;
//                    if (isAmerican) {
//                        currVoaId = String.valueOf(clicBean.voaId);
//                    } else {
//                        currVoaId = String.valueOf(clicBean.voaId * 10);
//                    }
//
//
//                    String actionUrl = "http://voa." + Constant.IYUBA_CN + "voa/UnicomApi?"
//                            + "platform=android&format=json&protocol=60003"
//                            + "&topic=" + Constant.EVAL_TYPE
//                            + "&userid=" + UserInfoManager.getInstance().getUserId()
//                            + "&username=" + TextAttr.encode(UserInfoManager.getInstance().getUserName())
//                            + "&voaid=" + currVoaId + "&idIndex=" + clicBean.lineN + "&score=" + clicBean.getReadScore() + "&shuoshuotype=2" + "&content=" + clicBean.getEvaluateBean().getURL();
//                    Log.e("jsonObjectRoot", actionUrl);
//                    //POST参数构造MultipartBody.Builder，表单提交
//                    OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
//                            connectTimeout(5, TimeUnit.SECONDS).
//                            readTimeout(5, TimeUnit.SECONDS).
//                            writeTimeout(5, TimeUnit.SECONDS).build();
//                    // 构造Request->call->执行
//                    okhttp3.Request request = new okhttp3.Request.Builder().headers(new Headers.Builder().build())//extraHeaders 是用户添加头
//                            .url(actionUrl).build();
//                    isUploadVoice = true;
//                    okHttpClient.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) {
//                            try {
//                                String data = response.body().string().toString();
//                                isUploadVoice = false;
//                                JSONObject jsonObjectRoot = new JSONObject(data);
//                                Log.e("jsonObjectRoot", data);
//                                String result = jsonObjectRoot.getString("ResultCode");
//                                clicBean.setShuoshuoId(jsonObjectRoot.getInt("ShuoshuoId"));
//                                String addscore = jsonObjectRoot.getInt("AddScore") + "";
//                                // TODO
//                                if (result.equals("501")) {
//                                    handler.sendEmptyMessage(4);
//                                    Message msg = handler.obtainMessage();
//                                    msg.what = 10;
//                                    msg.arg1 = Integer.parseInt(addscore);
//                                    handler.sendMessage(msg);
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                isUploadVoice = false;
//                                handler.sendEmptyMessage(4);
//
//                            }
//                        }
//                    });
//
//                }
//            }.start();
//        }
//    }
//
//    private void showTipDialog() {
//
//        boolean showTip = ConfigManager.Instance().isShowTip();
//        if (showTip) {
//            return;
//        }
//        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
//        builder.setTitle("温馨提示");
//        builder.setMessage("再次点击即可停止录音，完成评测");
//        builder.setPositiveButton(R.string.alert_btn_ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                ConfigManager.Instance().setShowTip(true);
//            }
//        });
//        builder.create().show();
//    }
//
//    //进入学习页面
//    public void getVoaDetail() {
//        handlerWaitting.sendEmptyMessage(1);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // 从本地数据库中查找
//                VoaDataManager.Instace().voaTemp = clicVoa;
//                VoaDataManager.Instace().voaDetailsTemp = voaDetailOp.findDataByVoaId(clicVoa.voaId);
//                if (VoaDataManager.Instace().voaDetailsTemp != null && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
//                    //这里同步处理下当前数据内容
//                    //默认为美音，特殊情况为青少版
//                    clicVoa.position = 0;
//                    if (clicVoa.voaId>300000){
//                        clicVoa.lessonType = TypeLibrary.BookType.conceptJunior;
//                    }else {
//                        clicVoa.lessonType = TypeLibrary.BookType.conceptFourUS;
//                    }
//                    VoaDataManager.getInstance().voaTemp = clicVoa;
//                    //设置为临时数据
//                    ConceptBgPlaySession.getInstance().setTempData(true);
//
//                    VoaDataManager.Instace().setSubtitleSum(clicVoa, VoaDataManager.Instace().voaDetailsTemp);
//                    //这里把一些数据直接指定处理（这里好像没必要啊，有点问题啊）
////                    if (clicVoa.voaId>300000){
////                        ConfigManager.Instance().setYouth(true);
////                        ConfigManager.Instance().setAmerican(false);
////                    }else if (clicVoa.voaId>10000){
////                        ConfigManager.Instance().setYouth(false);
////                        ConfigManager.Instance().setAmerican(false);
////                    }else {
////                        ConfigManager.Instance().setYouth(false);
////                        ConfigManager.Instance().setAmerican(true);
////                    }
//
//                    //跳转数据
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, StudyNewActivity.class);
//                    intent.putExtra("curVoaId", clicVoa.voaId + "");
//                    mContext.startActivity(intent);
//                    handlerWaitting.sendEmptyMessage(0);
//                }
//            }
//        }).start();
//    }
//
//    // 生成文件夹
//    public static void makeRootDirectory(String filePath) {
//        File file = null;
//        try {
//            file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//        } catch (Exception e) {
//            Log.i("error:", e + "");
//        }
//    }
//
//    private String getLocalSoundPath(int voaId) {
//
//        boolean isAmerican = ConfigManager.Instance().isAmercan();
//        if (isAmerican) {
//            // 美音原文音频的存放路径
////            String pathString = Constant.videoAddr + voaId + Constant.append;
//            String pathString = FilePathUtil.getHomeAudioPath(voaId,TypeLibrary.BookType.conceptFourUS);
//            File fileTemp = new File(pathString);
//            if (fileTemp.exists()) {
//                return pathString;
//            }
//        } else {
//            // 英音原文音频的存放路径
////            String pathString = Constant.videoAddr + voaId + "_B" + Constant.append;
//            String pathString = FilePathUtil.getHomeAudioPath(voaId,TypeLibrary.BookType.conceptFourUK);
//            File fileTemp = new File(pathString);
//            if (fileTemp.exists()) {
//                return pathString;
//            }
//
//            /*if (voaId < 2000 && voaId % 2 == 0) {
//                // 美音原文音频的存放路径
//                String pathStringM = Constant.videoAddr + voaId + Constant.append;
//                File fileTempM = new File(pathStringM);
//                if (fileTempM.exists()) {
//                    return pathStringM;
//                }
//            }*/
//
//        }
//
//        return "";
//    }
//}
