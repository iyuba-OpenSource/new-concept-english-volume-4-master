//package com.iyuba.conceptEnglish.adapter;
//
//import android.Manifest;
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.text.style.ForegroundColorSpan;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.activity.StudyActivity;
//import com.iyuba.conceptEnglish.listener.RequestCallBack;
//import com.iyuba.conceptEnglish.manager.VoaDataManager;
//import com.iyuba.conceptEnglish.protocol.AddCreditsRequest;
//import com.iyuba.conceptEnglish.protocol.DataCollectRequest;
//import com.iyuba.conceptEnglish.protocol.DataCollectResponse;
//import com.iyuba.conceptEnglish.sqlite.mode.EvaluateBean;
//import com.iyuba.conceptEnglish.sqlite.mode.ReadVoiceComment;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
//import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
//import com.iyuba.conceptEnglish.util.EvaluateRequset;
//import com.iyuba.conceptEnglish.util.GsonUtils;
//import com.iyuba.conceptEnglish.util.MediaRecordHelper;
//import com.iyuba.conceptEnglish.util.NetWorkState;
//import com.iyuba.conceptEnglish.util.PcmToWavUtil;
//import com.iyuba.conceptEnglish.util.Player;
//import com.iyuba.conceptEnglish.util.ResultParse;
//import com.iyuba.conceptEnglish.util.UtilPostFile;
//import com.iyuba.conceptEnglish.widget.RoundProgressBar;
//import com.iyuba.conceptEnglish.widget.cdialog.CustomToast;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.InfoHelper;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.INetStateReceiver;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.ErrorResponse;
//import com.iyuba.core.common.util.LogUtils;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.event.StopTextPlayerEvent;
//import com.iyuba.core.lil.user.UserInfoManager;
//
//import org.greenrobot.eventbus.EventBus;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.tencent.qzone.QZone;
//import cn.sharesdk.wechat.favorite.WechatFavorite;
//import cn.sharesdk.wechat.friends.Wechat;
//import cn.sharesdk.wechat.moments.WechatMoments;
//import me.drakeet.materialdialog.MaterialDialog;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.OkHttpClient;
//import okhttp3.Response;
//
//public class ValReadAdapter extends BaseAdapter implements PcmToWavUtil.PcmToWavUtilInteraction {
//
//    private static final String TAG = ValReadAdapter.class.getSimpleName();
//    private List<VoaDetail> mList;
//    private Context mContext;
//    private LayoutInflater mInflater;
//    static public int clickPosition = -1;
//    private int senIndex;
//    private VoaDetail clickDetail;
//    static private ViewHolder clickViewHolder;
//    private double time;
//    private double playTime;
//    public MediaPlayer videoView = null;
//    private int currParagraph = 1;
//    private int lastParegraph = 0;
//    private Voa voaTemp;
//    private ViewHolder curViewHolder;
//    private Player mPlayer;
//    private VoaDetail tempDetail = new VoaDetail();
//    private boolean isUploadVoice = false;
//    private CustomDialog waittingDialog;
//    private HashMap<Integer, ReadVoiceComment> mMap;
//    private LinkedHashMap<String, String> unknownWord;
//    private SpannableStringBuilder style[];
//    private SpannableStringBuilder tip;
//    private SpannableStringBuilder words;
//    private int mHeaderViewHeight;
//    private static String beginTime; // 听力开始学习时间(听力和语音测试都用的这两个变量记录时间）
//    private static String endTime;// 听力开始学习结束时间
//    private static Bundle bundle = new Bundle();
//
//    private VoaSoundOp voaSoundOp;
//
//
////    private RecordPCM rManager;
//    //录音
//    private MediaRecordHelper recordHelper;
//
//    private boolean isEvaluating = false, isStopEvaluate = false;
//    private long timeEvluate;
//    private String shuoshuoId;
//
//
//    private MediaRecordHelper mediaRecordHelper;
//
//
//    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {
//
//        @Override
//        public void onStartConnect(BaseHttpRequest request, int rspCookie) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onConnected(BaseHttpRequest request, int rspCookie) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onStartSend(BaseHttpRequest request, int rspCookie, int totalLen) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onSend(BaseHttpRequest request, int rspCookie, int len) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onSendFinish(BaseHttpRequest request, int rspCookie) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onStartRecv(BaseHttpRequest request, int rspCookie, int totalLen) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onNetError(BaseHttpRequest request, int rspCookie, ErrorResponse errorInfo) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onCancel(BaseHttpRequest request, int rspCookie) {
//            // TODO Auto-generated method stub
//
//        }
//    };
//
//    public ValReadAdapter(Context mContext, List<VoaDetail> mList, Voa voaTemp, MediaPlayer mp, ValReadAdapterInteraction valReadAdapterInteraction) {
//        this.mContext = mContext;
//        this.mList = mList;
//        this.voaTemp = voaTemp;
//
//        if (mList != null && mList.size() > 0) this.clickDetail = mList.get(0);
//        this.valReadAdapterInteraction = valReadAdapterInteraction;
//        waittingDialog = WaittingDialog.showDialog(mContext);
//        this.videoView = mp;
//        mMap = new HashMap<Integer, ReadVoiceComment>();
//        unknownWord = new LinkedHashMap<String, String>();
//        mInflater = LayoutInflater.from(mContext);
//
//        //初始化MP4工具类
////        mediaRecordHelper = new MediaRecordHelper();
//
//        voaSoundOp = new VoaSoundOp(mContext);
//        // url = Constant.sound + voaTemp.voaId / 1000 + "_" + voaTemp.voaId
//        // % 1000 + Constant.append;
//        // netType = NetWorkState.getAPNType();
//        try {
//            tempDetail = mList.get(currParagraph - 1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mPlayer = new Player(mContext, null);
//        style = new SpannableStringBuilder[mList.size()];
//        for (int i = 0; i < mList.size(); i++) {
//            // allEn[i] = mList.get(i).sentence;
//            style[i] = new SpannableStringBuilder(mList.get(i).sentence);
//        }
//        tip = new SpannableStringBuilder("请先听原文，然后选择未听懂的单词");
//        tip.setSpan(new ForegroundColorSpan(0xff639cfe), 0, tip.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//
//    }
//
//    public void setAdapter(List<VoaDetail> mList, Voa voaTemp) {
//        this.mList.clear();
//        this.mList.addAll(mList);
//
//        style = new SpannableStringBuilder[mList.size()];
//        for (int i = 0; i < mList.size(); i++) {
//            // allEn[i] = mList.get(i).sentence;
//            style[i] = new SpannableStringBuilder(mList.get(i).sentence);
//        }
////        this.mList = mList;
//        this.voaTemp = voaTemp;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getCount() {
//        // TODO Auto-generated method stub
//        return mList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        // TODO Auto-generated method stub
//        return mList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        // TODO Auto-generated method stub
//        return position;
//    }
//
//    /**
//     * 反转字符串
//     *
//     * @param s
//     * @return
//     */
//    public String reverseStr(String s) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = s.length() - 1; i >= 0; i--) {
//            sb.append(s.charAt(i));
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 字符串找词的index
//     */
//    public int findWord(String sub, SpannableStringBuilder ssb, int index, int start) {
//        int i = 0;
//        for (int j = start; j < ssb.length(); j++) {
//            i = ssb.toString().indexOf(sub, j);
//            if (i + sub.length() < index + start) j = i + sub.length();
//            else break;
//        }
//        return i;
//
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final VoaDetail curDetail = mList.get(position);
//        // String[] allEn = new String [mList.size()];
//        final int curPosition = position;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.listitem_read, null);
//
//            curViewHolder = new ViewHolder();
//            curViewHolder.senIndex = (TextView) convertView.findViewById(R.id.sen_index);
//            curViewHolder.senEn = (TextView) convertView.findViewById(R.id.sen_en);
//            curViewHolder.senZh = (TextView) convertView.findViewById(R.id.sen_zh);
//            curViewHolder.wordChosn = (TextView) convertView.findViewById(R.id.chosn_word);
//            curViewHolder.senPlay = (RoundProgressBar) convertView.findViewById(R.id.sen_play);
//            curViewHolder.senIRead = (RoundProgressBar) convertView.findViewById(R.id.sen_i_read);
//            curViewHolder.senReadPlayButton = convertView.findViewById(R.id.sen_read_button);
//            curViewHolder.senReadPlay = (ImageView) convertView.findViewById(R.id.sen_read_play);
//            curViewHolder.senReadPlaying = (RoundProgressBar) convertView.findViewById(R.id.sen_read_playing);
//            curViewHolder.senReadSend = (ImageView) convertView.findViewById(R.id.sen_read_send);
//            curViewHolder.senReadCollect = (ImageView) convertView.findViewById(R.id.sen_read_collect);
//            // curViewHolder.senReadCollect.setVisibility(View.GONE);
//            curViewHolder.senReadResult = (TextView) convertView.findViewById(R.id.sen_read_result);
//            curViewHolder.sepLine = (ImageView) convertView.findViewById(R.id.sep_line);
//            curViewHolder.bottomView = convertView.findViewById(R.id.bottom_view);
//            curViewHolder.frontView = convertView.findViewById(R.id.front_view);
//            curViewHolder.wordCommit = (Button) convertView.findViewById(R.id.word_commit);
//
//            convertView.setTag(curViewHolder);
//        } else {
//            curViewHolder = (ViewHolder) convertView.getTag();
//        }
//
//
//        final VoaSound voaSound = voaSoundOp.findDataById(Integer.parseInt(curDetail.voaId + "" + position));
//
//        if (voaSound != null && voaSound.sound_url != null && !"".equals(voaSound.sound_url)) {
//
//            LogUtils.e("单句===" + voaSound.sound_url);
////            String filepath = voaSound.filepath;
////            File file = new File(filepath);
////            if (file.isFile() && file.exists()) {
//            String[] floats = voaSound.wordScore.split(",");
//            curDetail.setReadScore(voaSound.totalScore);
//            curDetail.readResult = ResultParse.getSenResultLocal(floats, curDetail.sentence);
//            curDetail.isRead = true;
//
//            curDetail.setEvaluateBean(new EvaluateBean(voaSound.sound_url));
//            curDetail.evaluateBean.setURL(voaSound.sound_url);
//            valReadAdapterInteraction.getIndex(position, voaSound.totalScore, Long.parseLong(voaSound.time), voaSound.itemId, voaSound.sound_url);
////            }
//        }
//
//        curViewHolder.senIndex.setText((position + 1) + "");
//
//
//        if (curDetail.isListen && curDetail.isRead == false) {
//            words = new SpannableStringBuilder(curDetail.chosnWords);
//            words.setSpan(new ForegroundColorSpan(0xffff0000), 0, words.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        } else {
//            curViewHolder.senZh.setVisibility(View.VISIBLE);
//            curViewHolder.wordChosn.setVisibility(View.INVISIBLE);
//            curViewHolder.wordCommit.setVisibility(View.INVISIBLE);
//        }
//
//        curViewHolder.senZh.setText(curDetail.sentenceCn);
//        // curViewHolder.senEn.setText(curDetail.sentence);
//        curViewHolder.senEn.setText(style[position]);
//        curViewHolder.bottomView.getParent().requestDisallowInterceptTouchEvent(true);
//
//
//        if (curDetail.isRead) {
//            curViewHolder.senEn.setText(curDetail.readResult);
//            curViewHolder.senZh.setText(curDetail.sentenceCn);
//            setReadScoreViewContent(curDetail.getReadScore());
//        } else {
//            // curViewHolder.senEn.setText(curDetail.sentence);
//            if (curDetail.isListen) curViewHolder.senEn.setText(style[position]);
//
//            else curViewHolder.senEn.setText(style[position]);
//        }
//
//        if (curPosition == clickPosition) {
//            clickViewHolder = curViewHolder;
//            clickDetail = curDetail;
//        }
//
//        // curViewHolder.senReadCollect.setVisibility(View.GONE);
//        if (mMap.containsKey(curPosition)) {
//            curViewHolder.senReadCollect.setVisibility(View.VISIBLE);
//        } else {
//            curViewHolder.senReadCollect.setVisibility(View.GONE);
//        }
//        Log.d("mdg", "it is my dog,hahahaha");
//        if (curPosition != clickPosition) {
//            curViewHolder.sepLine.setVisibility(View.GONE);
//            curViewHolder.bottomView.setVisibility(View.GONE);
//        } else {
//            curViewHolder.sepLine.setVisibility(View.VISIBLE);
//            curViewHolder.bottomView.setVisibility(View.VISIBLE);
//            if (curDetail.isRead) {
//                curViewHolder.senReadPlayButton.setVisibility(View.VISIBLE);
//                curViewHolder.senReadSend.setVisibility(View.VISIBLE);
//                curViewHolder.senReadResult.setVisibility(View.VISIBLE);
//            } else {
//                curViewHolder.senReadPlayButton.setVisibility(View.INVISIBLE);
//                curViewHolder.senReadSend.setVisibility(View.INVISIBLE);
//                curViewHolder.senReadResult.setVisibility(View.INVISIBLE);
//            }
//        }
//        // 实现各种监听
//        curViewHolder.wordCommit.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//
//                VoaDetail en = mList.get(clickPosition);
//                int length;
//                String[] split = en.sentence.split(" ");
//                String userWord = en.chosnWords;
//                userWord = userWord.replace(" ", ",");
//                String[] splitt = en.chosnWords.split(" ");
//
//                if ("".equals(en.chosnWords)) length = 0;
//                else length = splitt.length;
//
//                int score = 100 - length * 100 / split.length;
//
//                Message msg = new Message();
//                msg.what = 1;
//                // Bundle bundle = new Bundle();
//                bundle.putString("endFlag", "1");
//                bundle.putString("lessonId", String.valueOf(voaTemp.voaId));
//                bundle.putString("lesson", voaTemp.title);
//                bundle.putString("testNumber", String.valueOf(currParagraph));
//                bundle.putString("testWords", String.valueOf(split.length));
//                bundle.putString("testMode", "1");
//                bundle.putString("userAnswer", userWord);
//                bundle.putString("score", String.valueOf(score));
//                msg.setData(bundle);
//                if (UserInfoManager.getInstance().isLogin()) {
//                    if (userWord == "")
//                        Toast.makeText(mContext, "您没有选择任何单词", Toast.LENGTH_SHORT).show();
//                    else {
//                        Toast.makeText(mContext, "正在上传...", Toast.LENGTH_SHORT).show();
//                        videoHandle.sendMessage(msg); // 向服务器发送数据
//                    }
//                } else {
//                    LoginUtil.startToLogin(mContext);
//                }
//            }
//        });
//        curViewHolder.senPlay.setOnClickListener(new OnClickListener() {
//
//            public void onClick(View view) {
//
//                valReadAdapterInteraction.setMixPlayerPause();
//                stopEval();
//
//                notifyDataSetChanged();
//                handler.sendEmptyMessage(7);
//                Log.e("开始时间", System.currentTimeMillis() + "");
//                int endtime;
//                int positon = videoView.getCurrentPosition();
//                if (currParagraph == mList.size()) endtime = videoView.getDuration();
//                else endtime = (int) mList.get(currParagraph).startTime * 1000;
//
//                Log.e("持续时间", positon + "--" + endtime + "--" + currParagraph);
//
//
//                if (mPlayer.isPlaying()) mPlayer.pause();
//                if (videoView.isPlaying()) {
//                    videoView.pause();
//                    handler.removeMessages(0);
//                    curViewHolder.senPlay.setBackgroundResource(R.drawable.sen_play);
//                } else if (positon > tempDetail.startTime * 1000.0 && positon < endtime) {
//                    curViewHolder.senPlay.setBackgroundResource(R.drawable.sen_stop);
//                    videoView.start();
//                    handler.sendEmptyMessage(0);
//                } else setReadTime();
//
//                if (currParagraph - lastParegraph == 2) {
//                    currParagraph--;
//                }
//
//            }
//        });
//
//        //测评按钮
//        curViewHolder.senIRead.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//
//                notifyDataSetChanged();
//                EventBus.getDefault().post(new StopTextPlayerEvent());
//                ((StudyActivity) mContext).checkStudyPermission();
//                if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})) {
//                    return;
//                }
//                ArrayList<VoaSound> voaSoundArrayList = voaSoundOp.findDataByvoaId(tempDetail.voaId);
//                if (!curDetail.isRead && voaSoundArrayList.size() >= 5) {
//                    if (ConfigManager.Instance().loadInt("isvip") < 1) {
//                        final MaterialDialog materialDialog = new MaterialDialog(mContext);
//                        materialDialog.setTitle("提醒");
//                        materialDialog.setMessage("本篇你已评测5句！成为vip后可评测更多");
//
//                        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                materialDialog.dismiss();
//                            }
//                        });
//                        materialDialog.show();
//                        return;
//                    }
//                }
//                if (!NetWorkState.isConnectingToInternet()) {
//                    CustomToast.showToast(mContext, R.string.alert_net_content, 1000);
//                    return;
//                }
//                valReadAdapterInteraction.setMixPlayerPause();
//
//                if (videoView.isPlaying()) {
//                    videoView.pause();
//                    handler.removeMessages(0);
//                    handler.sendEmptyMessage(1);
//                }
//                if (mPlayer.isPlaying()) {
//                    mPlayer.pause();
//                    handler.removeMessages(4);
//                    handler.sendEmptyMessage(5);
//                }
//
//                if (!UserInfoManager.getInstance().isLogin()) {
//                    LoginUtil.startToLogin(mContext);
//                } else {
//
//                    if (isStopEvaluate) {
//                        dismissDia();
//                    } else {
//                        startCP();
//                    }
//                }
//            }
//        });
//
//        curViewHolder.senReadPlayButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                valReadAdapterInteraction.setMixPlayerPause();
//                stopEval();
//
//                handler.sendEmptyMessage(7);
//                if (videoView.isPlaying()) {
//                    videoView.pause();
//                    handler.removeMessages(0);
//                    handler.sendEmptyMessage(1);
//                }
//
//
//                notifyDataSetChanged();
//                if (isStoppedAndCouldPlay()) {
//                    clickViewHolder.senReadPlay.setVisibility(View.GONE);
//                    clickViewHolder.senReadPlaying.setVisibility(View.VISIBLE);
//                    notifyDataSetChanged();
//                    playRecord2("http://voa." + Constant.IYUBA_CN + "voa/" + clickDetail.evaluateBean.getURL());
//                    Log.e("音频", mPlayer.getDuration() + "");
//                    handler.sendEmptyMessage(4);
//                } else if (mPlayer.isPlaying()) {
//                    clickViewHolder.senReadPlaying.setVisibility(View.GONE);
//                    clickViewHolder.senReadPlay.setVisibility(View.VISIBLE);
//                    notifyDataSetChanged();
//                    mPlayer.pause();
//                    handler.removeMessages(4);
//                } else if (mPlayer.isPausing()) {
//                    Log.e("play===", "play===");
//                    clickViewHolder.senReadPlay.setVisibility(View.GONE);
//                    clickViewHolder.senReadPlaying.setVisibility(View.VISIBLE);
//                    mPlayer.restart();
//                    handler.sendEmptyMessage(4);
//                }
//            }
//        });
//
//        curViewHolder.senReadCollect.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                stopEval();
//                showShare();
//            }
//        });
//
//        curViewHolder.senReadSend.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopEval();
//                if (UserInfoManager.getInstance().isLogin()) {
//
//                    if (isUploadVoice) {
//                        CustomToast.showToast(mContext, "评测发送中，请不要重复提交", 1000);
//                    } else {
//
//                        if (clickDetail.evaluateBean != null && clickDetail.evaluateBean.getURL() != null && !"".equals(clickDetail.evaluateBean.getURL())) {
//                            waittingDialog.show();
//                            new Thread() {
//                                @Override
//                                public void run() {
//                                    super.run();
//                                    String actionUrl = "http://voa." + Constant.IYUBA_CN + "voa/UnicomApi?topic=concept" + "&platform=android&format=json&protocol=60003" + "&userid=" + UserInfoManager.getInstance().getUserId() + "&voaid=" + voaTemp.voaId + "&idIndex=" + tempDetail.lineN + "&score=" + clickDetail.getReadScore() + "&shuoshuotype=2" + "&content=" + clickDetail.evaluateBean.getURL();
//                                    Log.e("jsonObjectRoot", actionUrl);
//                                    //POST参数构造MultipartBody.Builder，表单提交
//                                    OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
//                                            connectTimeout(15, TimeUnit.SECONDS).
//                                            readTimeout(15, TimeUnit.SECONDS).
//                                            writeTimeout(15, TimeUnit.SECONDS).build();
//                                    // 构造Request->call->执行
//                                    okhttp3.Request request = new okhttp3.Request.Builder().headers(new Headers.Builder().build())//extraHeaders 是用户添加头
//                                            .url(actionUrl)
////                                            .post(urlBuilder.build())//参数放在body体里
//                                            .build();
//                                    isUploadVoice = true;
//                                    okHttpClient.newCall(request).enqueue(new Callback() {
//                                        @Override
//                                        public void onFailure(Call call, IOException e) {
//                                        }
//
//                                        @Override
//                                        public void onResponse(Call call, Response response) {
//                                            try {
//                                                String data = response.body().string().toString();
//                                                isUploadVoice = false;
//                                                JSONObject jsonObjectRoot = new JSONObject(data);
//                                                Log.e("jsonObjectRoot", data);
//                                                String result = jsonObjectRoot.getString("ResultCode");
//                                                ReadVoiceComment rvc = new ReadVoiceComment(VoaDataManager.getInstance().voaTemp, mList.get(curPosition));
//                                                rvc.id = jsonObjectRoot.getInt("ShuoshuoId") + "";
//
//                                                shuoshuoId = jsonObjectRoot.getInt("ShuoshuoId") + "";
//                                                clickDetail.setShuoshuoId(Integer.parseInt(shuoshuoId));
////                                                rvc.shuoshuo = jsonObjectRoot.getString("FilePath");
//                                                String addscore = jsonObjectRoot.getInt("AddScore") + "";
//                                                mMap.put(curPosition, rvc);
//
//                                                // TODO
//                                                if (result.equals("501")) {
//                                                    waittingDialog.dismiss();
//                                                    Message msg = handler.obtainMessage();
//                                                    msg.what = 17;
//                                                    msg.arg1 = Integer.parseInt(addscore);
//                                                    handler.sendMessage(msg);
//                                                    StudyActivity.newInstance().rankHandler.sendEmptyMessage(0);
//                                                }
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                                isUploadVoice = false;
//                                                waittingDialog.dismiss();
//                                            }
//                                        }
//                                    });
//                                }
//                            }.start();
//                        } else {
//                            waittingDialog.show();
//                            Thread threadsend = new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Map<String, String> textParams = new HashMap<String, String>();
//                                    Map<String, File> fileParams = new HashMap<String, File>();
//                                    File file = new File(Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition + ".mp3");
//
//                                    textParams.put("topic", "concept");
//                                    textParams.put("IdIndex", tempDetail.lineN);
//                                    fileParams.put("content.acc", file);
//
//                                    if (file != null && file.exists()) {
//                                        try {
//                                            isUploadVoice = true;
//                                            String response = UtilPostFile.post("http://voa." + Constant.IYUBA_CN + "voa/UnicomApi?topic=concept" + "&platform=android&format=json&protocol=60003" + "&userid=" + UserInfoManager.getInstance().getUserId() + "&voaid=" + voaTemp.voaId + "&idIndex=" + tempDetail.lineN + "&score=" + clickDetail.getReadScore() + "&shuoshuotype=2", textParams, fileParams);
//                                            Log.e("sendRank", response);
//                                            isUploadVoice = false;
//
//                                            JSONObject jsonObjectRoot;
//                                            jsonObjectRoot = new JSONObject(response);
//                                            String result = jsonObjectRoot.getInt("ResultCode") + "";
//                                            ReadVoiceComment rvc = new ReadVoiceComment(VoaDataManager.getInstance().voaTemp, mList.get(curPosition));
//                                            rvc.id = jsonObjectRoot.getInt("ShuoShuoId") + "";
//                                            rvc.shuoshuo = jsonObjectRoot.getString("FilePath");
//                                            String addscore = jsonObjectRoot.getString("AddScore");
//                                            shuoshuoId = jsonObjectRoot.getInt("ShuoShuoId") + "";
//                                            clickDetail.setShuoshuoId(Integer.parseInt(shuoshuoId));
//                                            mMap.put(curPosition, rvc);
//
//                                            // TODO
//                                            if (result.equals("1")) {
//                                                waittingDialog.dismiss();
//                                                Message msg = handler.obtainMessage();
//
//                                                msg.what = 10;
//                                                msg.arg1 = Integer.parseInt(addscore);
//                                                StudyActivity.newInstance().commentHandler.sendMessage(msg);
//                                                StudyActivity.newInstance().commentHandler.sendEmptyMessage(4);
//                                                StudyActivity.newInstance().rankHandler.sendEmptyMessage(0);
//                                            }
//                                        } catch (Exception e) {
//                                            isUploadVoice = false;
//                                            waittingDialog.dismiss();
//                                            e.printStackTrace();
//                                        }
//                                        // catch (JSONException e) {
//                                        // isUploadVoice = false;
//                                        // e.printStackTrace();
//                                        // }
//                                    }
//                                }
//                            });
//                            threadsend.start();
//                        }
//                    }
//                } else {
////                    Intent intent = new Intent();
////                    intent.setClass(mContext, Login.class);
////                    StudyActivity.newInstance().startActivity(intent);
//                    LoginUtil.startToLogin(mContext);
//                }
//            }
//        });
//
////        curViewHolder.senEn.setOnTouchListener(new OnTouchListener() {
////
////            @SuppressLint("NewApi")
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                // TODO 自动生成的方法存根
////                switch (event.getAction()) {
////                    case MotionEvent.ACTION_DOWN:
////                        try {
////                            int[] location = new int[2];
////                            clickViewHolder.frontView.getLocationInWindow(location);
////                            // while(flag);
////                            // flag = true;
////                            float x = event.getX();
////                            float y = event.getY();
////                            float rawy = event.getRawY();
////                            mHeaderViewHeight = clickViewHolder.frontView.getHeight();
////                            if (rawy > location[1] + mHeaderViewHeight) break;
////                            if (rawy < location[1]) break;
////                            // 因为行设置行间距后，文本在顶部，空白在底部，加上一半的行间距，可以实现与文本垂直居中一样的效果
////                            y = y + clickViewHolder.senEn.getLineSpacingExtra() / 2 - clickViewHolder.senEn.getPaddingTop();
////                            // 在顶部padding空白处点击
////                            if (y < 0) return false;
////                            int singleLineHeight = clickViewHolder.senEn.getLineHeight();
////                            // StaticLayout layout=(StaticLayout)text.getLayout();
////
////                            int lineNumber = Math.round(y / singleLineHeight) - 1;
////                            if (lineNumber < 0) lineNumber = 0;
////                            if (lineNumber > clickViewHolder.senEn.getLineCount() - 1)
////                                lineNumber -= 1;
////                            int start = clickViewHolder.senEn.getLayout().getLineStart(lineNumber);
////                            int end = clickViewHolder.senEn.getLayout().getLineEnd(lineNumber);
////
////                            String str = clickViewHolder.senEn.getText().toString().substring(start, end);
////                            Paint paint = new Paint();
////                            paint.setTextSize(clickViewHolder.senEn.getTextSize());// 设置字符大小
////                            // 单字节占宽度
////                            int sigleByteWidth = (int) paint.measureText("1", 0, 1);
////
////                            // 减掉padding
////                            float realX = x - clickViewHolder.senEn.getPaddingLeft();
////                            // 在左侧padding空白处点击
////                            if (realX < 0) {
////                                return false;
////                            }
////                            // 字符串可能的最大长度
////                            // 这里应该有更好更快的算法
////                            int maxLength = (int) Math.floor(realX / sigleByteWidth);
////                            // 一般不会有字符显示超过两个1的宽度
////                            int miniLenth = maxLength / 2;
////                            int strLength = str.length();
////                            for (int i = strLength; i >= miniLenth; i--) {
////                                // 字符串显示的宽度
////                                float displayWidth = paint.measureText(str, 0, i);
////                                if (Math.abs(displayWidth - realX) < sigleByteWidth * 2) {
////                                    int beforeStrLen = 0;
////                                    int beginIndex = 0;
////                                    // //找到了，如果处理中文，这里用substring就可以了
////                                    int clickChar = str.charAt(i - 2);
////                                    // //不是英文字母，直接返回
////                                    if (clickChar < 65 || (clickChar > 90 && clickChar < 97) || clickChar > 122) {
////                                        return false;
////                                    }
////                                    // 取单词,这段只针对英文，中文需要海量词库，太复杂了，前后看是不是空格或符号
////                                    StringBuilder sb = new StringBuilder();
////                                    sb.append(str.substring(i - 2, i - 1));
////                                    // 查找前后的字符是否是同一个单词
////                                    String strBefore = str.substring(0, i - 2);
////                                    String strAfter = str.substring(i - 1, str.length());
////
////                                    String strBeforeReverse = reverseStr(strBefore);
////                                    Pattern p1 = Pattern.compile("(^[a-zA-Z]+)");
////                                    // 正则
////                                    Matcher m1 = p1.matcher(strBeforeReverse);
////                                    if (m1.find()) {
////                                        sb.insert(0, reverseStr(m1.group(1)));
////                                        beforeStrLen = m1.group(1).length();
////                                        beginIndex = i - 2 - beforeStrLen;
////                                    }
////
////                                    m1 = p1.matcher(strAfter);
////                                    if (m1.find()) {
////                                        sb.append(m1.group(1));
////                                    }
////                                    System.out.println("点击单词" + sb.toString());
////                                    int in = findWord(sb.toString(), style[clickPosition], i - 2, start);
////                                    String flag = String.valueOf(clickPosition) + "." + String.valueOf(in);
////                                    if (unknownWord.get(flag) == null) {
////                                        unknownWord.put(flag, sb.toString());
////                                        style[clickPosition].setSpan(new ForegroundColorSpan(0xff639cfe), in, in + sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                                    } else {
////                                        unknownWord.remove(flag);
////                                        style[clickPosition].setSpan(new ForegroundColorSpan(0xff7a665c), in, in + sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                                    }
////
////                                    Iterator iter = unknownWord.entrySet().iterator();
////                                    clickDetail.chosnWords = "";
////                                    while (iter.hasNext()) {
////                                        Map.Entry entry = (Map.Entry) iter.next();
////                                        String fl = (String) entry.getKey();
////                                        String dl = (String) entry.getValue();
////                                        int lc = fl.indexOf(".");
////                                        String flag2 = fl.substring(0, lc);
////                                        if (flag2.equals(String.valueOf(clickPosition))) {
////                                            if (clickDetail.chosnWords == "")
////                                                clickDetail.chosnWords = dl;
////                                            else
////                                                clickDetail.chosnWords = clickDetail.chosnWords + " " + dl;
////                                        }
////                                    }
////
////                                    notifyDataSetChanged();
////                                    break;
////                                }
////                            }
////
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                        }
////                        break;
////
////                }
////                return false;
////            }
////        });
//
//        return convertView;
//    }
//
//    public void setClickPosition(int clickPosition) {
//        stopAll();
//        this.clickPosition = clickPosition;
//        this.currParagraph = clickPosition + 1;
//        this.lastParegraph = clickPosition;
//        if (mList.size() == 0) tempDetail = new VoaDetail();
//        else tempDetail = mList.get(currParagraph - 1);
//        mPlayer.reset();
//
////        if (rManager != null) {
////            rManager.stopRecord();
////        }
//            if (recordHelper!=null&&recordHelper.isRecording){
//                recordHelper.stop_record();
//            }
//    }
//
//    public void stopAll() {
//        if (videoView.isPlaying()) {
//            videoView.pause();
//            // lisNotCmplMsg();
//        }
//
//        if (mPlayer.isPlaying()) {
//            mPlayer.pause();
//
//        }
//        if (clickViewHolder != null && clickDetail != null) {
//            handler.removeMessages(0);
//            handler.sendEmptyMessage(1);
//            handler.sendEmptyMessage(3);
//            handler.sendEmptyMessage(5);
//        }
//
//    }
//
//    private void setReadScoreViewContent(int score) {
//        if (score < 50) {
//            curViewHolder.senReadResult.setText("");
//            curViewHolder.senReadResult.setBackgroundResource(R.drawable.sen_score_lower60);
//        } else if (score > 80) {
//            curViewHolder.senReadResult.setText(score + "");
//            curViewHolder.senReadResult.setBackgroundResource(R.drawable.sen_score_higher_80);
//        } else {
//            curViewHolder.senReadResult.setText(score + "");
//            curViewHolder.senReadResult.setBackgroundResource(R.drawable.sen_score_60_80);
//        }
//    }
//
//    private void setReadTime() {
//        time = 0;
//        List<VoaDetail> textDetailTemp = mList;
//        videoView.start();
//        if (currParagraph != 0) {
//
//            videoView.seekTo((int) (tempDetail.startTime * 1000.0));
//
//            if (currParagraph == textDetailTemp.size()) {
//                playTime = videoView.getDuration() / 1000 - (tempDetail.startTime);
//            } else {
//                playTime = textDetailTemp.get(currParagraph).startTime - tempDetail.startTime - 0.5;
//            }
//        }
//
//
//        handler.removeMessages(0);
//        handler.sendEmptyMessage(0);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//        beginTime = df.format(new Date());// new Date()为获取当前系统时间
//    }
//
//    public boolean isStoppedAndCouldPlay() {
//        if (mPlayer != null) {
//            return mPlayer.isIdle() || mPlayer.isCompleted() || mPlayer.isInitialized();
//        }
//        return false;
//    }
//
//    public void playRecord2(String soundUrl) {
//        if (mPlayer != null) {
//            if (mPlayer.isIdle()) {
////                String filepath = Constant.getsimRecordAddr() + senIndex + ".mp3";
////                Log.e("filepath", filepath);
//                mPlayer.initialize(soundUrl);
//                mPlayer.prepareAndPlay();
//            } else if (mPlayer.isCompleted()) {
//                mPlayer.start();
//            } else if (mPlayer.isInitialized()) {
//                mPlayer.prepareAndPlay();
//            }
//        }
//    }
//
//    public String getLocalMacAddress() {
//        WifiManager wifi = (WifiManager) StudyActivity.newInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        return info.getMacAddress();
//    }
//
//    // 将要执行的操作写在线程对象的run方法当中
//    Runnable updateReadThread = new Runnable() {
//        public void run() {
//            // 播放音频时，计时
//            if (time <= playTime) {
//                time = time + 0.05;
//                handler.sendEmptyMessageDelayed(0, 50);
//            } else {
//
//                Log.e("结束时间", System.currentTimeMillis() + "");
//                if (videoView != null && videoView.isPlaying()) {
//                    videoView.pause();
//                    videoView.seekTo((int) (tempDetail.startTime * 1000.0));
//                }
//                clickDetail.isListen = true;
//                handler.sendEmptyMessage(1);
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//                endTime = df.format(new Date());// new Date()为获取当前系统时间
//
//            }
//        }
//    };
//
//    public Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            RoundProgressBar tempBar = null;
//            switch (msg.what) {
//                case 0:// to change the progress bar of play button with handler
//                    Log.e("play---", clickViewHolder.senPlay.getId() + "");
//
//                    tempBar = clickViewHolder.senPlay;
//                    tempBar.setBackgroundResource(R.drawable.sen_stop);
////                    if (clickPosition == 0) notifyDataSetChanged();
//                    tempBar.setCricleProgressColor(0xff66a6e8);
//                    tempBar.setMax((int) (playTime * 100));
//                    tempBar.setProgress((int) (time * 100));
//                    updateReadThread.run();
//
//                    break;
//                case 1:// reset the progress bar of play button when the playing is
//                    // over
//                    clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_play);
//                    tempBar = clickViewHolder.senPlay;
//                    tempBar.setCricleProgressColor(0xff66a6e8);
//                    tempBar.setMax(100);
//                    tempBar.setProgress(0);
//                    notifyDataSetChanged();
//                    break;
//                case 2:// set the read button progress bar with value of voice
//                    // volume
//                    try {
//                        tempBar = clickViewHolder.senIRead;
//                        int db = msg.arg1;
//                        // Log.e(TAG, "sound DB value: " + db);
//                        tempBar.setCricleProgressColor(0xff87c973);
//                        tempBar.setMax(100);
//                        tempBar.setProgress(db);
//                    } catch (Exception e) {
//                        Log.e("val", "handler.case2");
//                    }
//                    break;
//                case 3: // reset the read button progress bar
//                    try {
//                        clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read);
//                        tempBar = clickViewHolder.senIRead;
//                        tempBar.setCricleProgressColor(0xff87c973);
//                        tempBar.setMax(100);
//                        tempBar.setProgress(0);
//                    } catch (Exception e) {
//                        Log.e("val", "handler.case3");
//                    }
//                    break;
//                case 4:// 播放自己录音时的progressbar
//                    tempBar = clickViewHolder.senReadPlaying;
//                    tempBar.setCricleProgressColor(0xff66a6e8);
//                    tempBar.setMax(mPlayer.getDuration() - 250);
//                    tempBar.setProgress(mPlayer.getCurrentTime());
//                    if (mPlayer.isPlaying()) {
//                        handler.sendEmptyMessageDelayed(4, 100);
//                    } else {
//                        handler.sendEmptyMessage(5);
//                    }
//                    break;
//                case 5:// 重置播放录音的progressbar
//                    clickViewHolder.senReadPlay.setVisibility(View.VISIBLE);
//                    clickViewHolder.senReadPlaying.setVisibility(View.GONE);
//                    tempBar = clickViewHolder.senReadPlaying;
//                    tempBar.setCricleProgressColor(0xff66a6e8);
//                    tempBar.setMax(100);
//                    tempBar.setProgress(0);
//                    notifyDataSetChanged();
//                    break;
//                case 6:
//                    break;
//                case 7:
//                    EventBus.getDefault().post(new StopTextPlayerEvent());
//                    break;
//                case 8:
//                    clickDetail.isCommit = true;
//                    notifyDataSetChanged();
//                    break;
//                case 9:
//                    break;
//                case 10:
//                    break;
//                case 11:
//                    double score_double = Double.parseDouble(clickDetail.evaluateBean.getTotal_score());
//                    clickDetail.isRead = true;
//                    clickDetail.readResult = ResultParse.getSenResultEvaluate(clickDetail.evaluateBean.getWords(), clickDetail.evaluateBean.getSentence());
//                    clickDetail.setReadScore((int) (score_double * 20));
//                    CustomToast.showToast(mContext, "评测成功", 1000);
//                    notifyDataSetChanged();
//
//                    String wordScore = "";
//                    for (int i = 0; i < clickDetail.evaluateBean.getWords().size(); i++) {
//                        EvaluateBean.WordsBean word = clickDetail.evaluateBean.getWords().get(i);
//                        wordScore = wordScore + word.getScore() + ",";
//                    }
//                    valReadAdapterInteraction.getIndex(senIndex, (int) (score_double * 20), timeEvluate, Integer.parseInt(tempDetail.voaId + "" + clickPosition), clickDetail.evaluateBean.getURL());
//                    valReadAdapterInteraction.setDefault();
//                    voaSoundOp.updateWordScore(wordScore, (int) (score_double * 20), tempDetail.voaId, Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition + ".mp3", timeEvluate + "", Integer.parseInt(tempDetail.voaId + "" + clickPosition), clickDetail.evaluateBean.getURL());
//
//                    File filepcm = new File(Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition + ".pcm");
//                    if (filepcm.isFile() && filepcm.exists()) {
//                        filepcm.delete();
//                    }
//                    File filewav = new File(Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition + ".wav");
//                    if (filewav.isFile() && filewav.exists()) {
//                        filewav.delete();
//                    }
//                    break;
//                case 12:
//                    ToastUtil.showToast(mContext, "评测失败，请稍后再试");
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
//                    clickDetail.evaluateBean = GsonUtils.toObject(data, EvaluateBean.class);
//                    handler.sendEmptyMessage(11);
//                    break;
//                case 16:
//                    if (UserInfoManager.getInstance().isLogin()) {
//                        RequestCallBack rc = new RequestCallBack() {
//
//                            @Override
//                            public void requestResult(Request result) {
//                                AddCreditsRequest rq = (AddCreditsRequest) result;
//                                if (rq.isShareFirstlySuccess()) {
//                                    String msg = "分享成功，增加了" + rq.addCredit + "积分，共有" + rq.totalCredit + "积分";
//                                    CustomToast.showToast(mContext, msg, 3000);
//                                } else if (rq.isShareRepeatlySuccess()) {
//                                    CustomToast.showToast(mContext, "分享成功", 3000);
//                                }
//                            }
//                        };
//                        int uid = UserInfoManager.getInstance().getUserId();
//                        AddCreditsRequest rq = new AddCreditsRequest(uid, mMap.get(clickPosition).getVoaRef().voaId, msg.arg1, rc);
//                        RequestQueue queue = Volley.newRequestQueue(mContext);
//                        queue.add(rq);
//                    }
//                    break;
//                case 17:
//
//                    String addscore = String.valueOf(msg.arg1);
//                    if (addscore.equals("5")) {
//                        String mg = "语音成功发送至排行榜，恭喜您获得了" + addscore + "分";
//                        com.iyuba.core.common.widget.dialog.CustomToast.showToast(mContext, mg, 3000);
//                    } else {
//                        String mg = "语音成功发送至排行榜";
//                        com.iyuba.core.common.widget.dialog.CustomToast.showToast(mContext, mg, 3000);
//                    }
//                    break;
//
//                case 18:
//                    //获取录音分贝
//                    try {
//                        clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_stop);
//                        tempBar = clickViewHolder.senIRead;
//                        tempBar.setCricleProgressColor(0xff87c973);
//                        tempBar.setMax(100);
////                        clickViewHolder.senIRead.setProgress(rManager.getReadVioce());
//                        clickViewHolder.senIRead.setProgress(recordHelper.getDB());
//                    } catch (Exception e) {
//                        Log.e("val", "handler.case18");
//                    }
//                    handler.sendEmptyMessageDelayed(18, 100);
//                    break;
//                case 19:
//                    handler.removeMessages(18);
//                    clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read);
//                    clickViewHolder.senIRead.setProgress(0);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    Handler videoHandle = new Handler() {
//        public void handleMessage(Message msg) {
//            // Looper.prepare();
//            switch (msg.what) {
//                case 1: // 向服务器发送数据
//                    String endFlag = msg.getData().getString("endFlag");
//                    // String lesson = msg.getData().getString("lesson");
//                    String lesson = "concept"; // lesson应该上传用户使用的哪一款app的名称
//                    String lessonId = msg.getData().getString("lessonId");
//                    String testNumber = msg.getData().getString("testNumber");
//                    String testWords = msg.getData().getString("testWords");
//                    final String testMode = msg.getData().getString("testMode");
//                    String userAnswer = msg.getData().getString("userAnswer");
//                    String score = msg.getData().getString("score");
//                    String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
//                    SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
//                    String sign = uid + beginTime + dft.format(System.currentTimeMillis());
//                    String deviceId = getLocalMacAddress();
//                    Log.e("endtime", endTime);
//                    if (NetWorkState.isConnectingToInternet()) {
//                        try {
//                            ClientSession.Instace().asynGetResponse(new DataCollectRequest(uid, beginTime, endTime, lesson, lessonId, testNumber, testWords, testMode, userAnswer, score, endFlag, deviceId, sign,false), new IResponseReceiver() {
//                                @Override
//                                public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                                    DataCollectResponse tr = (DataCollectResponse) response;
//
//                                    if (tr != null && tr.result.equals("1") && testMode.equals("1")) {
//                                        Looper.prepare();
//                                        if (tr.score.equals("0"))
//                                            Toast.makeText(mContext, "数据提交成功!", Toast.LENGTH_SHORT).show();
//                                        else
//                                            Toast.makeText(mContext, "数据提交成功，恭喜您获得了" + tr.score + "分", Toast.LENGTH_SHORT).show();
//                                        handler.sendEmptyMessage(8);
//                                        Looper.loop();
//                                    } else if (tr.result.equals("0")) {
//                                        Looper.prepare();
//                                        Toast.makeText(mContext, "数据提交出错", Toast.LENGTH_SHORT).show();
//                                        Looper.loop();
//                                    } else {
//                                        Looper.prepare();
//                                        Looper.loop();
//                                    }
//                                }
//                            }, null, mNetStateReceiver);
//                        } catch (UnsupportedEncodingException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                case 12:
//                    ToastUtil.showToast(mContext, "评测失败，请稍后再试");
//                    waittingDialog.dismiss();
//                    break;
//                default:
//                    break;
//            }
//        }
//
//    };
//
//
//    public static class ViewHolder {
//        TextView senIndex;
//        TextView senEn;
//        TextView senZh;
//        TextView wordChosn;
//        RoundProgressBar senPlay;
//        View senReadPlayButton;
//        RoundProgressBar senIRead;
//        RoundProgressBar senReadPlaying;
//        ImageView senReadPlay;
//        ImageView senReadSend;
//        ImageView senReadCollect;
//        TextView senReadResult;
//        ImageView sepLine;
//        View bottomView;
//        View frontView;
//        Button wordCommit;
//    }
//
//    // 1 定义了所有activity必须实现的接口方法
//    public interface ValReadAdapterInteraction {
//        void getIndex(int position, int score, long time, int itemid, String filepath);
//
//        void setDefault();
//
//        void setMixPlayerPause();
//    }
//
//    private ValReadAdapterInteraction valReadAdapterInteraction;
//
//
//    public void setStopplay() {
//
//        if (videoView != null) {
//            if (videoView.isPlaying()) {
//                videoView.pause();
//            }
//            videoView.stop();
//            videoView.release();
//            videoView = null;
//        }
//    }
//
//
//    //暂停播放原文和播放原文
//    public void stopPlay() {
//        if (videoView != null & videoView.isPlaying()) {
//            videoView.pause();
//            handler.removeMessages(0);
//            handler.sendEmptyMessage(1);
//        }
//        if (mPlayer != null & mPlayer.isPlaying()) {
//            mPlayer.pause();
//            handler.removeMessages(4);
//            handler.sendEmptyMessage(5);
//        }
//    }
//
//    public void stopEval() {
//
//        if (isStopEvaluate) {
//
////            rManager.stopRecord();
////            timeEvluate = rManager.stopRecord();
//
//            recordHelper.stop_record();
//            recordHelper.getRecordTime();
//
//            handler.sendEmptyMessage(19);
//            isStopEvaluate = false;
//        }
//    }
//
//    public void dismissDia() {
//        if (isStopEvaluate) {
//
////            timeEvluate = rManager.stopRecord();
//            recordHelper.stop_record();
//            timeEvluate = recordHelper.getRecordTime();
//            setFile(true);
//
//            handler.sendEmptyMessage(19);
//
////            RecordUtil recordUtil = new RecordUtil();
////            recordUtil.pcm2mp3(Constant.getsimRecordAddr() + tempDetail.voaId + clickPosition + ".pcm", Constant.getsimRecordAddr() + tempDetail.voaId + clickPosition + ".mp3");
//            //MP4 文件录制停止
////            mediaRecordHelper.stop_record();
////            Log.e("开始录制时间2", System.currentTimeMillis() + "");
//            isStopEvaluate = false;
////            handler.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    SoundDialog.soundText.setText("正在评测");
////                    setRequest();
////                }
////            }, 500);
//
////            makeRootDirectory(Constant.getsimRecordAddr());
////            PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(RecordPCM.SAMPLE_RATE_INHZ, RecordPCM.CHANNEL_CONFIG, RecordPCM.AUDIO_FORMAT, mContext, this);
////            pcmToWavUtil.pcmToWavToMp3(Constant.getsimRecordAddr() + tempDetail.voaId + clickPosition + ".pcm", Constant.getsimRecordAddr() + tempDetail.voaId + clickPosition + ".wav");
//        }
//    }
//
//    @Override
//    public void setFile(boolean isSuccess) {
//        //判断文件是否转化MP3成功
//        try {
//            if (isSuccess) {
//                setRequest();
//            } else {
//                ToastUtil.showToast(mContext, "录音失败，请稍后再试");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    long currentTimess = 0;
//
//    private void startCP() {
//        isStopEvaluate = true;
//        if (currParagraph == 1) {
//            currentTimess = 5000;
//        } else if (currParagraph == mList.size()) {
//            currentTimess = (long) ((videoView.getDuration() - mList.get(currParagraph - 1).startTime * 1000) * 1.5);
//        } else {
//            currentTimess = (long) ((mList.get(currParagraph).startTime * 1000 - mList.get(currParagraph - 1).startTime * 1000) * 1.5);
//        }
//
//
//        makeRootDirectory(Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition);
//        String saveMp3Path = Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition + ".mp3";
//        recordHelper = new MediaRecordHelper();
//        recordHelper.setFilePath(saveMp3Path);
//
////        rManager = new RecordPCM(Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition + ".pcm", currentTimess, this);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                rManager.startRecord();
//                recordHelper.recorder_Media();
//                handler.sendEmptyMessageDelayed(18, 100);
//            }
//        }, 100);
//
//
//        //MP4 文件录制
////        mediaRecordHelper.setFilePath(Constant.getsimRecordAddr() + tempDetail.voaId + clickPosition + ".mp4");
////        mediaRecordHelper.recorder_Media();
////        Log.e("开始录制时间1", System.currentTimeMillis() + "");
//
//    }
//
//    private void setRequest() {
//        if (UserInfoManager.getInstance().isLogin()) {
//            Log.e("开始录制时间", System.currentTimeMillis() + "");
//            if (isEvaluating) {
//                CustomToast.showToast(mContext, "正在评测中，请不要重复提交", 1000);
//            } else {
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//                        Map<String, String> textParams = new HashMap<String, String>();
//
//                        File file = new File(Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition + ".mp3");
//                        textParams.put("type", "concept");
//                        textParams.put("userId", String.valueOf(UserInfoManager.getInstance().getUserId()));
//                        textParams.put("newsId", voaTemp.voaId + "");
//                        textParams.put("paraId", "1");
//                        textParams.put("IdIndex", tempDetail.lineN);
//
//                        String urlSentence = TextAttr.encode(tempDetail.sentence);
//                        urlSentence = urlSentence.replaceAll("\\+", "%20");
//                        textParams.put("sentence", urlSentence);
//
//                        if (file != null && file.exists()) {
//                            try {
//                                isEvaluating = true;
//                                EvaluateRequset.post(Constant.EVALUATE_URL_NEW, textParams, Constant.getsimRecordAddr(mContext) + tempDetail.voaId + clickPosition + ".mp3", handler);
//                            } catch (Exception e) {
//                                isEvaluating = false;
//                                e.printStackTrace();
//                                Log.e("Exception", e.toString());
//                            }
//                        } else {
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
////            StudyActivity.newInstance().startActivity(intent);
//            LoginUtil.startToLogin(mContext);
//        }
//    }
//
//
//    private void showShare() {
//        String username = null;
//        if ("".equals(UserInfoManager.getInstance().getUserName())) {
//            username = String.valueOf(UserInfoManager.getInstance().getUserId());
//        } else {
//            username = UserInfoManager.getInstance().getUserName();
//        }
//        String url;
//        if (clickDetail.evaluateBean != null && clickDetail.evaluateBean.getURL() != null) {
//            url = "&addr=" + clickDetail.evaluateBean.getURL();
//        } else {
//            url = "";
//        }
//
//        String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + clickDetail.getShuoshuoId() + url + "&apptype=concept";
//        String sitTitle = username + "在" + "爱语吧语音评测中获得了" + clickDetail.getReadScore() + "分";
//        String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
//        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
//        weibo.removeAccount(true);
//        ShareSDK.removeCookieOnAuthorize(true);
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
//        oks.setTitle(sitTitle);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(siteUrl);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(voaTemp.title);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        // oks.setImagePath("/sdcard/test.jpg");
//        // imageUrl是Web图片路径，sina需要开通权限
//        oks.setImageUrl(imageUrl);
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(siteUrl);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
////        oks.setComment("爱语吧的这款应用" + Constant.APPName + "真的很不错啊~推荐！");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(sitTitle);
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
//            }
//
//            @Override
//            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//                Log.e("okCallbackonComplete", "onComplete");
//                int srid = 46;
//                String name = arg0.getName();
//                if (name.equals(QQ.NAME) || name.equals(Wechat.NAME) || name.equals(WechatFavorite.NAME)) {
//                    srid = 45;
//                } else if (name.equals(QZone.NAME) || name.equals(WechatMoments.NAME)
//                        //微博飞雷神
//                        || name.equals(SinaWeibo.NAME)
//                ) {
//                    srid = 46;
//                }
//                Message message = new Message();
//                message.what = 17;
//                message.arg1 = srid;
//                handler.sendMessage(message);
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
//}
