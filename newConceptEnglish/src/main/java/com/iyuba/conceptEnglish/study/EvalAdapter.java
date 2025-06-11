//package com.iyuba.conceptEnglish.study;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Typeface;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.util.Pair;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.gson.Gson;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceData;
//import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceDataItem;
//import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceResponse;
//import com.iyuba.conceptEnglish.han.utils.CorrectEvalHelper;
//import com.iyuba.conceptEnglish.han.utils.ExpandKt;
//import com.iyuba.conceptEnglish.han.utils.OnEvaluationListener;
//import com.iyuba.conceptEnglish.han.utils.OnWordClickListener;
//import com.iyuba.conceptEnglish.han.utils.SelectableTextView;
//import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
//import com.iyuba.conceptEnglish.manager.VoaDataManager;
//import com.iyuba.conceptEnglish.sqlite.mode.EvaluateBean;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
//import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
//import com.iyuba.conceptEnglish.util.ConceptApplication;
//import com.iyuba.conceptEnglish.util.LoadIconUtil;
//import com.iyuba.conceptEnglish.util.MediaRecordHelper;
//import com.iyuba.conceptEnglish.util.NetWorkState;
//import com.iyuba.conceptEnglish.util.ResultParse;
//import com.iyuba.conceptEnglish.util.ShareUtils;
//import com.iyuba.conceptEnglish.widget.RoundProgressBar;
//import com.iyuba.conceptEnglish.widget.cdialog.CustomToast;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.InfoHelper;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.common.util.NetStateUtil;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.core.lil.view.PermissionMsgDialog;
//import com.iyuba.play.ExtendedPlayer;
//
//import net.protyposis.android.mediaplayer.MediaPlayer;
//
//import org.greenrobot.eventbus.EventBus;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import butterknife.ButterKnife;
//import me.drakeet.materialdialog.MaterialDialog;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import timber.log.Timber;
//
//public class EvalAdapter extends RecyclerView.Adapter<EvalAdapter.MyViewHolder> {
//    private static final int HANDLER_AUTO_STOP = 3;
//    private static final int HANDLER_FOLLOW_PLAYER = 4;
//    private static final int HANDLER_PLAYER = 0;
//    private static final int HANDLER_SEND = 5;
//    private static final int HANDLER_SOUND_DB = 1;
//    private static final int HANDLER_STOP_EVALUATE = 2;
//    private int clickPosition = 0;
//    private MyViewHolder clickViewHolder;
//    private VoaDetail clickVoaDetail;
//    public boolean isEvaluating = false;
//    private boolean isRecording = false;
//    private boolean isSending = false;
//    private List<VoaDetail> list;
//    private Context mContext;
//    private MediaRecordHelper mediaRecordHelper;
//    private MixSound mixSound;
//    //原文播放器
////    private IJKPlayer player;
//    //评测播放器
//    private ExtendedPlayer followPlayer;
//    //课文播放器
////    private IJKPlayer textPlayer;
////    private android.media.MediaPlayer textPlayer;
//
//    private VoaSoundOp voaSoundOp;
//    private CustomDialog waitingDialog;
//    private OnEvaluationListener onEvaluationListener;
//    private final int evalSuccess = 414;
//    private final int evalFail = 415;
//    private CorrectEvalHelper helper;
//    private String currentVoaId = "", userId = "", groupId = "";
//    public static final int maxEval = 3;
//
//    //权限弹窗
//    private PermissionMsgDialog msgDialog = null;
//
//    //界面
//    private EvalFragment evalFragment;
//
//    public void setOnEvaluationListener(OnEvaluationListener onEvaluationListener) {
//        this.onEvaluationListener = onEvaluationListener;
//    }
//
//    public void setEvalFragment(EvalFragment evalFragment) {
//        this.evalFragment = evalFragment;
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                //不会写回调的码农快给我414
//                /*case HANDLER_PLAYER:
//                    RoundProgressBar bar = EvalAdapter.this.clickViewHolder.senPlay;
//                    bar.setBackgroundResource(R.drawable.sen_stop);
//                    int totalTime;
//                    if (0 != list.get(clickPosition).endTime) {
//                        totalTime = (int) (list.get(clickPosition).endTime * 1000.0D);
//                    } else {
//                        if (clickPosition == list.size() - 1) {
//                            totalTime = player.getDuration();
//                        } else {
//                            totalTime = (int) ((list.get(clickPosition + 1)).startTime * 1000.0D);
//                        }
//                    }
//
//                    int starTime = (int) (clickVoaDetail.startTime * 1000.0D);
//                    int currTime = player.getCurrentPosition();
//                    bar.setMax(totalTime - starTime);
//                    Log.e("播放时间", currTime + "==" + starTime);
//                    bar.setProgress((currTime - starTime) > 0 ? (currTime - starTime) : 0);
//                    if (player.getCurrentPosition() < totalTime) {
//                        handler.sendEmptyMessageDelayed(0, 300L);
//                    } else {
//                        handler.removeMessages(0);
//                        EvalAdapter.this.player.pause();
//                        bar.setBackgroundResource(R.drawable.sen_play);
//                        bar.setProgress(0);
//                    }
//                    if (player.isCompleted()) {
//                        handler.removeMessages(0);
//                        bar.setBackgroundResource(R.drawable.sen_play);
//                        bar.setProgress(0);
//                    }
//                    break;*/
//                case HANDLER_SOUND_DB:
//                    try {
//                        clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_stop);
//                        clickViewHolder.senIRead.setCricleProgressColor(0xff87c973);
//                        clickViewHolder.senIRead.setMax(100);
//                        clickViewHolder.senIRead.setProgress(mediaRecordHelper.getDB());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    handler.sendEmptyMessageDelayed(1, 100L);
//                    break;
//                case HANDLER_STOP_EVALUATE:
//
//                    handler.removeMessages(1);
//                    clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read);
//                    clickViewHolder.senIRead.setProgress(0);
//                    break;
//                case HANDLER_AUTO_STOP:
//                    stopPC();
//                    break;
//                case HANDLER_FOLLOW_PLAYER:
//                    RoundProgressBar bar1 = clickViewHolder.senReadPlay;
//                    bar1.setBackgroundResource(R.drawable.sen_stop);
//                    bar1.setMax(followPlayer.getDuration());
//                    bar1.setProgress(followPlayer.getCurrentPosition());
//                    if (followPlayer.getCurrentPosition() < followPlayer.getDuration()) {
//                        handler.sendEmptyMessageDelayed(4, 300L);
//                    } else {
//                        handler.removeMessages(4);
//                        try {
//                            followPlayer.pause();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        bar1.setBackgroundResource(R.drawable.play_ok);
//                        bar1.setProgress(0);
//                    }
//                    break;
//                case HANDLER_SEND:
//                    String addscore = String.valueOf(msg.arg1);
//
//                    if (InfoHelper.getInstance().openShare()) {
//                        clickViewHolder.senReadShare.setVisibility(View.VISIBLE);
//                    } else {
//                        clickViewHolder.senReadShare.setVisibility(View.GONE);
//                    }
//
//                    //刷新排行榜数据
//                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.eval_rank));
//
//                    if (addscore.equals("5")) {
//                        String mg = "语音成功发送至排行榜，恭喜您获得了" + addscore + "分";
//                        CustomToast.showToast(mContext, mg, 3000);
//                    } else {
//                        String mg = "语音成功发送至排行榜";
//                        CustomToast.showToast(mContext, mg, 3000);
//                    }
//                    break;
//                case 13:
//                    isRecording = false;
//                    ToastUtil.showToast(mContext, "录音失败，请稍后再试");
//                    break;
//                case 14:
//                case evalFail:
//                    ToastUtil.showToast(mContext, "评测失败");
//                    isEvaluating = false;
//                    break;
//                case evalSuccess:
//                    isEvaluating = false;
//                    String result = (String) msg.obj;
//                    Timber.tag("评测返回").e(result);
//                    try {
//                        EvaluationSentenceData data = new Gson().fromJson(result, EvaluationSentenceResponse.class).getData();
//                        loadLocalData(data.getWords());
//                        saveLocal(data);
//                        Toast.makeText(mContext, "评测成功", Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(mContext, "评测失败" + e, Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//            }
//        }
//    };
//
//    public String getMP4FileaAbsluatePath() {
//        //外部存储的  /iyuba/concept2/audio/sound 路径下
////        switch (ConfigManager.Instance().getBookType()){
////        switch (VoaDataManager.getInstance().voaTemp.lessonType){
////            case TypeLibrary.BookType.conceptFourUS:
////                return Constant.getsimRecordAddr(mContext) + "/" + clickVoaDetail.voaId + clickVoaDetail.lineN + ".mp4";
////            case TypeLibrary.BookType.conceptFourUK:
////                return Constant.getsimRecordAddr(mContext) + "/" + (clickVoaDetail.voaId * 10) + clickVoaDetail.lineN + ".mp4";
////            case TypeLibrary.BookType.conceptJunior:
////                return Constant.getsimRecordAddr(mContext) + "/" + clickVoaDetail.voaId + clickVoaDetail.lineN + ".mp4";
////            default:
////                return Constant.getsimRecordAddr(mContext) + "/" + clickVoaDetail.voaId + clickVoaDetail.lineN + ".mp4";
////        }
//
//        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
//            case TypeLibrary.BookType.conceptFourUS:
//                return Constant.getsimRecordAddr(mContext) + "/" + clickVoaDetail.voaId + clickVoaDetail.lineN + ".mp3";
//            case TypeLibrary.BookType.conceptFourUK:
//                return Constant.getsimRecordAddr(mContext) + "/" + (clickVoaDetail.voaId * 10) + clickVoaDetail.lineN + ".mp3";
//            case TypeLibrary.BookType.conceptJunior:
//                return Constant.getsimRecordAddr(mContext) + "/" + clickVoaDetail.voaId + clickVoaDetail.lineN + ".mp3";
//            default:
//                return Constant.getsimRecordAddr(mContext) + "/" + clickVoaDetail.voaId + clickVoaDetail.lineN + ".mp3";
//        }
//    }
//
//    EvalAdapter(List<VoaDetail> paramList, Context paramContext) {
//        this.mContext = paramContext;
//        this.list = paramList;
//        voaSoundOp = new VoaSoundOp(paramContext);
////        textPlayer = BackgroundManager.Instace().bindService.getPlayer();
//        mediaRecordHelper = new MediaRecordHelper();
//        waitingDialog = WaittingDialog.showDialog(paramContext);
//        followPlayer = new ExtendedPlayer(paramContext);
//        followPlayer.setOnPreparedListener(mp -> handler.sendEmptyMessage(4));
//        followPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                handler.removeMessages(4);
//                clickViewHolder.senReadPlay.setBackgroundResource(R.drawable.play_ok);
//                clickViewHolder.senReadPlay.setProgress(0);
//            }
//        });
//        helper = new CorrectEvalHelper(mContext);
//    }
//
//    private boolean canEvaluate() {
//        ArrayList<VoaSound> voaSounds = voaSoundOp.findDataByvoaId(this.clickVoaDetail.voaId);
//        if (!NetWorkState.isConnectingToInternet()) {
//            CustomToast.showToast(this.mContext, R.string.alert_net_content, 1000);
//            return false;
//        }
////        if ("0".equals(ConfigManager.Instance().getUserId())) {
////            mContext.startActivity(new Intent(mContext, Login.class));
////            return false;
////        }
//        if ((!clickVoaDetail.isRead) && (voaSounds.size() >= 3) && !UserInfoManager.getInstance().isVip()) {
//            final MaterialDialog materialDialog = new MaterialDialog(mContext);
//            materialDialog.setTitle("提醒");
//            materialDialog.setMessage("本篇你已评测3句！成为VIP后可评测更多");
//            materialDialog.setPositiveButton("确定", view -> materialDialog.dismiss());
//            materialDialog.show();
//            return false;
//        }
//        return true;
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
//    private void send() {
//        if (!UserInfoManager.getInstance().isLogin()) {
//            CustomToast.showToast(this.mContext, "请登录后再执行此操作", 1000);
//            return;
//        }
//
//        if (isSending) {
//            CustomToast.showToast(this.mContext, "评测发送中，请不要重复提交", 1000);
//            return;
//        }
//        this.waitingDialog.show();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                String currVoaId;
////                switch (ConfigManager.Instance().getBookType()){
//                switch (VoaDataManager.getInstance().voaTemp.lessonType) {
//                    //这种多处存在的臃肿判断，kotlin一个扩展方法解决
//                    case TypeLibrary.BookType.conceptFourUS:
//                    case TypeLibrary.BookType.conceptJunior:
//                    default:
//                        currVoaId = String.valueOf(clickVoaDetail.voaId);
//                        break;
//                    case TypeLibrary.BookType.conceptFourUK:
//                        currVoaId = String.valueOf(clickVoaDetail.voaId * 10);
//                        break;
//                }
//                String actionUrl = "http://voa." + Constant.IYUBA_CN + "voa/UnicomApi?"
//                        + "platform=android&format=json&protocol=60002"
//                        + "&topic=" + Constant.EVAL_TYPE
//                        + "&userid=" + UserInfoManager.getInstance().getUserId()
//                        + "&username=" + TextAttr.encode(UserInfoManager.getInstance().getUserName())
//                        + "&voaid=" + currVoaId
//                        + "&idIndex=" + clickVoaDetail.lineN
//                        + "&paraid=" + clickVoaDetail.paraId
//                        + "&score=" + clickVoaDetail.getReadScore() + "&shuoshuotype=2"
//                        + "&content=" + clickVoaDetail.evaluateBean.getURL();
//
//                Timber.tag("jsonObjectRoot").e(actionUrl);
//                //POST参数构造MultipartBody.Builder，表单提交
//                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
//                        connectTimeout(25, TimeUnit.SECONDS).
//                        readTimeout(25, TimeUnit.SECONDS).
//                        writeTimeout(25, TimeUnit.SECONDS).build();
//                // 构造Request->call->执行
//                Request request = new Request.Builder().headers(new Headers.Builder().build())//extraHeaders 是用户添加头
//                        .url(actionUrl)
////                      .post(urlBuilder.build())//参数放在body体里
//                        .build();
//                isSending = true;
//                okHttpClient.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                waitingDialog.dismiss();
//                                Toast.makeText(mContext, "分享失败，服务器连接失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) {
//                        try {
//                            String data = response.body().string().toString();
//                            isSending = false;
//                            JSONObject jsonObjectRoot = new JSONObject(data);
//                            Log.e("jsonObjectRoot", data);
//                            String result = jsonObjectRoot.getString("ResultCode");
//                            String shuoshuoId = jsonObjectRoot.getInt("ShuoshuoId") + "";
//                            clickVoaDetail.setShuoshuoId(Integer.parseInt(shuoshuoId));
//                            String addscore = jsonObjectRoot.getInt("AddScore") + "";
//                            if (result.equals("501")) {
//                                waitingDialog.dismiss();
//                                Message msg = handler.obtainMessage();
//                                msg.what = HANDLER_SEND;
//                                msg.arg1 = Integer.parseInt(addscore);
//                                handler.sendMessage(msg);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            isSending = false;
//                            waitingDialog.dismiss();
//                        }
//                    }
//                });
//            }
//        }.start();
//    }
//
//    private void setReadScoreViewContent(int score, TextView textView) {
//        if (score < 50) {
//            textView.setText("");
//            textView.setBackgroundResource(R.drawable.sen_score_lower60);
//        } else if (score > 80) {
//            textView.setText(score + "");
//            textView.setBackgroundResource(R.drawable.sen_score_higher_80);
//        } else {
//            textView.setText(score + "");
//            textView.setBackgroundResource(R.drawable.sen_score_60_80);
//        }
//    }
//
//    /**
//     * 请求单句评测接口
//     */
//    private void setRequest() {
//        if (isEvaluating) {
//            CustomToast.showToast(this.mContext, "正在评测中，请不要重复提交", 1000);
//            return;
//        }
//        new Thread() {
//            public void run() {
//                super.run();
//                isEvaluating = true;
//                File file = new File(getMP4FileaAbsluatePath());
//                evaluationSentence(file, clickVoaDetail);
//            }
//        }.start();
//    }
//
//    private void evaluationSentence(File file, VoaDetail currentItem) {
//        RequestBody body = MultipartBody.create(MediaType.parse("application/octet-stream"), file);
//        currentVoaId = ExpandKt.realVoaId(currentItem);
//        userId = String.valueOf(UserInfoManager.getInstance().getUserId());
//        groupId = currentItem.lineN;
//        MultipartBody builder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("type", Constant.AppName)
//                .addFormDataPart("userId", userId)
//                .addFormDataPart("newsId", currentVoaId)
//                .addFormDataPart("paraId", currentItem.paraId)
//                .addFormDataPart("IdIndex", groupId)
//                .addFormDataPart("sentence", currentItem.sentence)
//                .addFormDataPart("file", file.getName(), body)
//                .addFormDataPart("wordId", "0")
//                .addFormDataPart("flg", "0")
//                .addFormDataPart("appId", Constant.APPID)
//                .build();
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url(Constant.evalUrl).post(builder).build();
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                handler.sendEmptyMessage(evalFail);
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
//                try {
//                    String result = response.body().string();
//                    Message msg = new Message();
//                    msg.what = evalSuccess;
//                    msg.obj = result;
//                    handler.sendMessage(msg);
//                } catch (Exception e) {
//                    handler.sendEmptyMessage(evalFail);
//                }
//            }
//        });
//    }
//
//    private void startPC() {
//        // 设置为正在录音状态
//        this.isRecording = true;
//        //time为结束时间 ， 数量表示在总时长中的位置
//        long time;
//
//        if (0 != (list.get(this.clickPosition)).endTime) {
//            time = (int) ((list.get(this.clickPosition)).endTime * 1000.0D);
//        } else {
//            if (this.clickPosition == this.list.size() - 1) {
//                time = (int) ((list.get(this.clickPosition)).endTime * 1000.0D);
//            } else {
//                time = (int) ((list.get(this.clickPosition + 1)).startTime * 1000.0D);
//            }
//        }
//
//        //播放总时间  = 结束时间-开始时间
//        long totalTime = time - (int) (this.clickVoaDetail.startTime * 1000.0D);
//        //生成根目录文件夹
//        makeRootDirectory(Constant.getsimRecordAddr(mContext));
//        //设置 文件路径
//        mediaRecordHelper.setFilePath(getMP4FileaAbsluatePath());
//        mediaRecordHelper.recorder_Media();
//        handler.sendEmptyMessage(1);
//
//        if (totalTime < 2000) {
//            handler.sendEmptyMessageDelayed(HANDLER_AUTO_STOP, (long) (totalTime * 2.5D));
//        } else {
//            handler.sendEmptyMessageDelayed(HANDLER_AUTO_STOP, (long) (totalTime * 1.5D));
//        }
//
//    }
//
//    private void saveLocal(EvaluationSentenceData data) {
//        clickVoaDetail.evaluateBean = data.convertEvaluateBean();
//        double totalScore = Double.parseDouble(clickVoaDetail.evaluateBean.getTotal_score());
//        clickVoaDetail.isRead = true;
//        clickVoaDetail.readResult = ResultParse.getSenResultEvaluate(clickVoaDetail.evaluateBean.getWords(), EvalAdapter.this.clickVoaDetail.evaluateBean.getSentence());
//        int score = (int) (totalScore * 20.0D);
//        clickVoaDetail.setReadScore(score);
//        notifyDataSetChanged();
//        String wordScore = "";
//        for (int i = 0; i < clickVoaDetail.evaluateBean.getWords().size(); i++) {
//            EvaluateBean.WordsBean word = clickVoaDetail.evaluateBean.getWords().get(i);
//            wordScore = wordScore + word.getScore() + ",";
//        }
//
//        int itemId = Integer.parseInt(clickVoaDetail.voaId + "" + clickVoaDetail.paraId + "" + clickVoaDetail.lineN);
//        voaSoundOp.updateWordScore(wordScore, score, clickVoaDetail.voaId,
//                getMP4FileaAbsluatePath(),
//                "", itemId, clickVoaDetail.evaluateBean.getURL());
//        mixSound.reStart();
//    }
//
//    private void stopPC() {
//        if (isRecording) {
//            isRecording = false;
//            mediaRecordHelper.stop_record();
//            handler.sendEmptyMessage(2);
//            if (NetStateUtil.isConnected(mContext)) {
//                setRequest();
//            } else {
//                ToastUtil.showToast(mContext, "请检查网络链接");
//            }
//
//        }
//    }
//
//    /**
//     * 提示dialog
//     */
//    private void tipDialog() {
//        String buildStr = "再次点击即可开始录音，完成评测"
////                + "\n" +
////                "评测完成后，点击单词可以进行纠音"
//                ;
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
//                .setTitle("温馨提示")
//                .setMessage(buildStr)
//                .setPositiveButton(R.string.alert_btn_ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ConfigManager.Instance().setShowTip(true);
//                    }
//                });
//        builder.create().show();
//    }
//
//    public int getItemCount() {
//        return list == null ? 0 : list.size();
//    }
//
//    @SuppressLint({"SetTextI18n"})
//    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
//        VoaDetail detail = list.get(position);
//        //动态修改文字大小
//        int showPosition = position + 1;
//        viewHolder.textIndex.setText(String.valueOf(showPosition));
//        if (showPosition >= 100) {
//            viewHolder.textIndex.setTextSize(12);
//        } else if (showPosition >= 10) {
//            viewHolder.textIndex.setTextSize(14);
//        } else {
//            viewHolder.textIndex.setTextSize(16);
//        }
//        viewHolder.senEn.setText(HelpUtil.transTitleStyle(detail.sentence));
//
//        int selectColor = ContextCompat.getColor(mContext, R.color.bookChooseUncheck);
//
//        if (TextUtils.isEmpty(detail.sentenceCn)) {
//            viewHolder.senZh.setVisibility(View.GONE);
//        } else {
//            viewHolder.senZh.setVisibility(View.VISIBLE);
//            viewHolder.senZh.setText(detail.sentenceCn);
//        }
//        viewHolder.bottomView.setVisibility(View.GONE);
//
//        viewHolder.sepLine.setVisibility(View.GONE);
//        viewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read);
//        viewHolder.senIRead.setProgress(0);
//        if (clickPosition == position) {
//            clickViewHolder = viewHolder;
//            clickVoaDetail = detail;
//            clickViewHolder.bottomView.setVisibility(View.VISIBLE);
//            clickViewHolder.sepLine.setVisibility(View.VISIBLE);
//        }
//
//        //这里暂时屏蔽掉选择单词的功能，换成textView
//        /*viewHolder.senEn.setSelectTextBackColor(selectColor);
//        viewHolder.senEn.setOnWordClickListener(new OnWordClickListener() {
//            @Override
//            protected void onNoDoubleClick(@NonNull String str) {
//                handler.postDelayed(() -> viewHolder.senEn.dismissSelected(),1000);
//                clickPosition=position;
//                clickViewHolder = viewHolder;
//                clickVoaDetail = detail;
//                notifyDataSetChanged();
//                userId = String.valueOf(UserInfoManager.getInstance().getUserId());
//                currentVoaId=ExpandKt.realVoaId(clickVoaDetail);
//                List<EvaluationSentenceDataItem> wordList = helper.findByContent(userId, currentVoaId, clickVoaDetail.lineN, ExpandKt.removeSymbol(str), true);
//                if (detail.isRead&&!wordList.isEmpty()){
//                    onEvaluationListener.showDialog(detail,str);
//                }else {
//                    ToastUtil.showToast(mContext,"请重新评测");
//                }
//            }
//        });*/
//
//        //查询评测数据
//        int itemId = Integer.parseInt(detail.voaId + "" + detail.paraId + "" + detail.lineN);
//        VoaSound voaSound = voaSoundOp.findDataById(itemId);
//
//        if ((voaSound != null) && (!TextUtils.isEmpty(voaSound.sound_url))) {
//            String[] floats = voaSound.wordScore.split(",");
//            detail.setReadScore(voaSound.totalScore);
//            detail.readResult = ResultParse.getSenResultLocal(floats, detail.sentence);
//            detail.isRead = true;
//            detail.setEvaluateBean(new EvaluateBean(voaSound.sound_url));
//            detail.evaluateBean.setURL(voaSound.sound_url);
//            detail.pathLocal = voaSound.filepath;
//        }
//
//        if (detail.isRead) {
//            Timber.e("evaled: %s", String.valueOf(detail.readResult));
//            viewHolder.senEn.setText(detail.readResult);
//            viewHolder.senEn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//            viewHolder.senReadResult.setVisibility(View.VISIBLE);
//            setReadScoreViewContent(detail.getReadScore(), viewHolder.senReadResult);
//            viewHolder.senReadPlay.setVisibility(View.VISIBLE);
//            viewHolder.senReadSend.setVisibility(View.VISIBLE);
//
//            if (InfoHelper.getInstance().openShare()) {
//                viewHolder.senReadShare.setVisibility(View.VISIBLE);
//            } else {
//                viewHolder.senReadShare.setVisibility(View.GONE);
//            }
//
//            if (detail.getShuoshuoId() != 0) {
//                if (InfoHelper.getInstance().openShare()) {
//                    viewHolder.senReadShare.setVisibility(View.VISIBLE);
//                } else {
//                    viewHolder.senReadShare.setVisibility(View.GONE);
//                }
//            } else {
//                viewHolder.senReadShare.setVisibility(View.GONE);
//            }
//        } else {
//            viewHolder.senEn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//            viewHolder.senReadPlay.setVisibility(View.GONE);
//            viewHolder.senReadSend.setVisibility(View.GONE);
//            viewHolder.senReadShare.setVisibility(View.GONE);
//            viewHolder.senReadResult.setVisibility(View.GONE);
//        }
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View paramAnonymousView) {
//                if (isRecording || isEvaluating) {
//                    ToastUtil.showToast(mContext,"正在录音评测中...");
//                    return;
//                }
//
//
//                if (EvalAdapter.this.clickPosition != position) {
//                    if (onEvalCallBackListener != null) {
//                        onEvalCallBackListener.clickItem(position);
//                    }
//
////                    stopAllVoice(null);
//                    //关闭合成评测和合成播放
//                    stopEvalPlayer();
//                    stopMixPlayer();
//
//                    clickPosition = position;
//                    clickViewHolder.bottomView.setVisibility(View.VISIBLE);
//                    clickViewHolder.sepLine.setVisibility(View.VISIBLE);
//                    notifyDataSetChanged();
//
//
//                    /*if ((player != null) && (player.isPlaying())) {
//                        player.pause();
//                    }
//                    handler.removeMessages(0);*/
//                }
//            }
//        });
//        if (clickViewHolder == null) {
//            return;
//        }
//
//        //评测结果
//        ArrayList<VoaSound> voaSounds = voaSoundOp.findDataByvoaId(this.clickVoaDetail.voaId);
//
//        //原音播放
//        clickViewHolder.senPlay.setOnClickListener(view -> {
//            if (isRecording || isEvaluating) {
//                ToastUtil.showToast(mContext,"正在录音评测中...");
//                return;
//            }
//
//            //关闭合成评测和合成播放
//            stopEvalPlayer();
//            stopMixPlayer();
//
//            if (onEvalCallBackListener != null) {
//                long startTime = (long) (clickVoaDetail.startTime * 1000L);
//                long endTime = (long) (clickVoaDetail.endTime * 1000L);
//                onEvalCallBackListener.audioPlay(startTime, endTime);
//            }
//        });
//        //录音
//        this.clickViewHolder.senIRead.setOnClickListener(paramAnonymousView -> {
//            //请求录音权限
//            List<Pair<String, Pair<String, String>>> pairList = new ArrayList<>();
//            pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO, new Pair<>("麦克风权限", "录制评测时朗读的音频，用于评测打分使用")));
//            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "保存评测的音频文件，用于评测打分使用")));
//
//            msgDialog = new PermissionMsgDialog(mContext);
//            msgDialog.showDialog(null, pairList, true, new PermissionMsgDialog.OnPermissionApplyListener() {
//                @Override
//                public void onApplyResult(boolean isSuccess) {
//                    if (isSuccess) {
//
//                        //关闭评测和合成播放
//                        stopEvalPlayer();
//                        stopMixPlayer();
//
//                        //关闭播放
//                        if (onEvalCallBackListener != null) {
//                            onEvalCallBackListener.recordAudio();
//                        }
//
//                        //进行登录判断
//                        if (!UserInfoManager.getInstance().isLogin()) {
//                            LoginUtil.startToLogin(mContext);
//                            return;
//                        }
//
//                        //是否有资格测评
//                        boolean isVip = Integer.parseInt(UserInfoManager.getInstance().getVipStatus()) >= 1;
//                        boolean isLogin = UserInfoManager.getInstance().isLogin();
//                        boolean isReadOut = voaSounds.size() >= maxEval;
//                        if ((!clickVoaDetail.isRead) && !voaSoundOp.temporaryUserFull()) {
//                            ExpandKt.goSomeAction(mContext, "");
//                            return;
//                        }
//                        boolean flag = isLogin && !isVip && isReadOut;
//                        if (flag) {
//                            ExpandKt.goSomeAction(mContext, "评测" + maxEval + "句以上");
//                            return;
//                        }
//
//                        //是否正在录音
//                        if (!isRecording) {
//                            if (!isEvaluating) {
//                                // 开始录音
//                                boolean showTip = ConfigManager.Instance().isShowTip();
//                                if (showTip) {
//                                    startPC();
//                                } else {
//                                    tipDialog();
//                                }
//                                return;
//                            }
//                            CustomToast.showToast(mContext, "正在评测中，请不要重复提交", 1000);
//                            return;
//                        }
//                        // 停止评测
//                        handler.removeMessages(3);
//                        stopPC();
//
//                    }
//                }
//            });
//        });
//        //评测播放
//        clickViewHolder.senReadPlay.setOnClickListener(view -> {
//            if (isRecording || isEvaluating) {
//                ToastUtil.showToast(mContext,"正在录音评测中...");
//                return;
//            }
////            stopAllVoice(clickViewHolder.senReadPlay);
//
//            //禁止播放其他音频
//            if (onEvalCallBackListener != null) {
//                onEvalCallBackListener.evalPlay();
//            }
//
//            //关闭评测和合成播放
//            stopEvalPlayer();
//            stopMixPlayer();
//
//            if (followPlayer == null) {
//                return;
//            }
//            if (followPlayer.isPlaying()) {
//                try {
//                    followPlayer.pause();
//                    handler.removeMessages(4);
//                    clickViewHolder.senReadPlay.setBackgroundResource(R.drawable.play_ok);
//                    clickViewHolder.senReadPlay.setProgress(0);
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//            } else {
//
//                if (TextUtils.isEmpty(clickVoaDetail.pathLocal)) {
//                    //没有存到本地
//                }
//
//                File file = new File(getMP4FileaAbsluatePath());
//                final String url;
//                if (file.exists() && file.isFile()) {
//                    if (TextUtils.isEmpty(clickVoaDetail.pathLocal)) {
//                        //没有存到本地
//                        url = Constant.EVAL_PREFIX + clickVoaDetail.evaluateBean.getURL();
//                        Log.e("播放网络连接", url);
//                    } else {
//                        url = getMP4FileaAbsluatePath();
//                        Log.e("播放网络连接", "=== 本地");
//                    }
//
//                } else {
//                    url = Constant.EVAL_PREFIX + clickVoaDetail.evaluateBean.getURL();
//                    Log.e("播放网络连接", url);
//                }
//                new Thread(() -> {
//                    followPlayer.initialize(url);
//                    followPlayer.prepareAndPlay();
//                }).start();
//
//            }
//        });
//
//        //发送到排行榜
//        this.clickViewHolder.senReadSend.setOnClickListener(view -> {
//            if (!UserInfoManager.getInstance().isLogin()) {
//                LoginUtil.startToLogin(mContext);
//                return;
//            }
//
//            if (isRecording || isEvaluating) {
//                ToastUtil.showToast(mContext,"正在录音评测中...");
//                return;
//            }
//
//            //关闭评测和合成播放
//            stopEvalPlayer();
//            stopMixPlayer();
//            //关闭其他
//            if (onEvalCallBackListener!=null){
//                onEvalCallBackListener.publishEval();
//            }
//
////            stopAllVoice(null);
//
//            send();
//        });
//
//        //分享
//        this.clickViewHolder.senReadShare.setOnClickListener(paramAnonymousView -> {
//            if (!UserInfoManager.getInstance().isLogin()) {
//                LoginUtil.startToLogin(mContext);
//                return;
//            }
//
//            if (isRecording || isEvaluating) {
//                ToastUtil.showToast(mContext,"正在录音评测中...");
//                return;
//            }
//
//            //关闭合成评测和合成播放
//            stopEvalPlayer();
//            stopMixPlayer();
//            //关闭其他
//            if (onEvalCallBackListener!=null){
//                onEvalCallBackListener.shareEval();
//            }
//
////            stopAllVoice(null);
//
//            String userName;
//            if (TextUtils.isEmpty(UserInfoManager.getInstance().getUserName())) {
//                userName = String.valueOf(UserInfoManager.getInstance().getUserId());
//            } else {
//                userName = UserInfoManager.getInstance().getUserName();
//            }
//            String content = "课程显示";
//            String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + clickVoaDetail.getShuoshuoId()
//                    + "&addr=" + clickVoaDetail.evaluateBean.getURL() + "&apptype=" + Constant.EVAL_TYPE;
//
//            //String imagePath = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
//            LoadIconUtil.loadCommonIcon(mContext);
//            String imagePath = Constant.iconAddr;
//            String title = userName + "在" + mContext.getResources().getString(R.string.app_name) + "第" + ConceptApplication.courseIndex + "课的评测：" + clickVoaDetail.sentence;
//            ShareUtils localShareUtils = new ShareUtils();
//            localShareUtils.setMContext(mContext);
//            localShareUtils.setVoaId(clickVoaDetail.voaId);
//            localShareUtils.showShare(mContext, imagePath, siteUrl, title, content, localShareUtils.platformActionListener, null);
//        });
//    }
//
//    private void loadLocalData(List<EvaluationSentenceDataItem> netResult) {
//        List<EvaluationSentenceDataItem> localData = helper.findByContent(userId, currentVoaId, groupId, "", false);
//        if (localData.isEmpty()) {
//            for (int i = 0; i < netResult.size(); i++) {
//                EvaluationSentenceDataItem item = netResult.get(i);
//                item.setGroupId(Integer.parseInt(groupId));
//                item.setUserId(Integer.parseInt(userId));
//                item.setVoaId(Integer.parseInt(currentVoaId));
//                item.setContent(ExpandKt.removeSymbol(item.getContent()));
//                helper.insertItem(item);
//            }
//        } else if (netResult.size() == localData.size()) {
//            for (int i = 0; i < netResult.size(); i++) {
//                EvaluationSentenceDataItem netItem = netResult.get(i);
//                EvaluationSentenceDataItem localItem = localData.get(i);
//                localItem.setPron(netItem.getPron());
//                localItem.setUser_pron(netItem.getUser_pron());
//                helper.updateItem(localItem);
//            }
//        }
//    }
//
//    @NonNull
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int paramInt) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_eval_fragment, viewGroup, false);
//        return new MyViewHolder(view);
//    }
//
//    void setMixSound(MixSound mixSound) {
//        this.mixSound = mixSound;
//    }
//
//    void stopAllVoice(RoundProgressBar roundProgressBar) {
////        if (clickViewHolder != null && (roundProgressBar != clickViewHolder.senPlay)) {
////            handler.removeMessages(0);
////            if (onEvalCallBackListener!=null){
////                onEvalCallBackListener.evalPlay();
////            }
////            clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_play);
////            clickViewHolder.senPlay.setProgress(0);
////        }
//
//        //评测播放
//        if (clickViewHolder != null && (roundProgressBar != clickViewHolder.senReadPlay) && (followPlayer != null) && (followPlayer.isPlaying())) {
//            followPlayer.stopPlay();
//            handler.removeMessages(4);
//            clickViewHolder.senReadPlay.setBackgroundResource(R.drawable.play_ok);
//            clickViewHolder.senReadPlay.setProgress(0);
//        }
//
//        //录音操作
//        if (clickViewHolder != null && (roundProgressBar != clickViewHolder.senIRead) && (mediaRecordHelper.isRecording)) {
//            mediaRecordHelper.stop_record();
//            handler.sendEmptyMessage(2);
//            handler.removeMessages(3);
//            isRecording = false;
//            isEvaluating = false;
//        }
//
//        //合成播放
//        if ((evalFragment != null) && (evalFragment.isAdded())) {
//            evalFragment.stopMixPlayer();
//        }
//
//        //关闭弹窗
//        if (msgDialog != null) {
//            msgDialog.dismiss();
//        }
//    }
//
//    /*******************************分类型操作***********************************/
//    public void stopEvalPlayer(){
//        if (followPlayer != null && clickViewHolder != null){
//            if (followPlayer.isPlaying()){
//                followPlayer.stopPlay();
//            }
//
//            handler.removeMessages(HANDLER_FOLLOW_PLAYER);
//            clickViewHolder.senReadPlay.setBackgroundResource(R.drawable.play_ok);
//            clickViewHolder.senReadPlay.setProgress(0);
//        }
//    }
//
//    //关闭当前的合成播放
//    private void stopMixPlayer() {
//        if ((evalFragment != null) && (evalFragment.isAdded())) {
//            evalFragment.stopMixPlayer();
//        }
//    }
//
//    //关闭当前的录音操作
//    private void stopRecord(){
//        if (mediaRecordHelper.isRecording && clickViewHolder != null){
//            mediaRecordHelper.stop_record();
//            handler.sendEmptyMessage(2);
//            handler.removeMessages(3);
//            isRecording = false;
//            isEvaluating = false;
//        }
//    }
//
//    public interface MixSound {
//        void reStart();
//    }
//
//    class MyViewHolder extends RecyclerView.ViewHolder {
//        LinearLayout bottomView;
//
//        TextView senEn;
//        RoundProgressBar senIRead;
//        RoundProgressBar senPlay;
//
//        RoundProgressBar senReadPlay;
//        TextView senReadResult;
//        ImageView senReadSend;
//        ImageView senReadShare;
//        TextView senZh;
//        ImageView sepLine;
//        TextView textIndex;
//
//        MyViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//
//            bottomView = itemView.findViewById(R.id.bottom_view);
//
//            senEn = itemView.findViewById(R.id.sen_en);
//            senIRead = itemView.findViewById(R.id.sen_i_read);
//            senPlay = itemView.findViewById(R.id.sen_play);
//
//            senReadPlay = itemView.findViewById(R.id.sen_read_playing);
//            senReadResult = itemView.findViewById(R.id.sen_read_result);
//            senReadSend = itemView.findViewById(R.id.sen_read_send);
//            senReadShare = itemView.findViewById(R.id.sen_read_collect);
//            senZh = itemView.findViewById(R.id.sen_zh);
//            sepLine = itemView.findViewById(R.id.sep_line);
//            textIndex = itemView.findViewById(R.id.sen_index);
//        }
//    }
//
//    /*********************************新的操作逻辑***************************/
//    //回调接口
//    public interface OnEvalCallBackListener {
//        //切换item
//        void clickItem(int position);
//
//        //原音播放
//        void audioPlay(long startTime, long endTime);
//
//        //音频录制
//        void recordAudio();
//
//        //评测播放
//        void evalPlay();
//
//        //发布评测
//        void publishEval();
//
//        //评测分享
//        void shareEval();
//    }
//
//    private OnEvalCallBackListener onEvalCallBackListener;
//
//    public void setOnEvalCallBackListener(OnEvalCallBackListener onEvalCallBackListener) {
//        this.onEvalCallBackListener = onEvalCallBackListener;
//    }
//
//    //刷新原文播放进度
//    public void refreshAudioPlay(int progress, int total, boolean isPlay) {
//        if (clickViewHolder == null) {
//            return;
//        }
//
//        if (isPlay) {
//            clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_stop);
//            clickViewHolder.senPlay.setMax(total);
//            if (progress <= 0) {
//                progress = 0;
//            }
//            clickViewHolder.senPlay.setProgress(progress);
//        } else {
//            clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_play);
//            clickViewHolder.senPlay.setProgress(0);
//        }
//    }
//}
