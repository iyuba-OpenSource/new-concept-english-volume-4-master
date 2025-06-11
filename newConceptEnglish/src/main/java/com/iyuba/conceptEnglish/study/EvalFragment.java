//package com.iyuba.conceptEnglish.study;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.MediaItem;
//import com.google.android.exoplayer2.PlaybackException;
//import com.google.android.exoplayer2.Player;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.api.ApiRetrofit;
//import com.iyuba.conceptEnglish.api.AudioComposeApi;
//import com.iyuba.conceptEnglish.api.AudioSendApi;
//import com.iyuba.conceptEnglish.han.CorrectSoundDialog;
//import com.iyuba.conceptEnglish.han.utils.OnEvaluationListener;
//import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.util.BigDecimalUtil;
//import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
//import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
//import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
//import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
//import com.iyuba.conceptEnglish.manager.VoaDataManager;
//import com.iyuba.conceptEnglish.sqlite.mode.EvaMixBean;
//import com.iyuba.conceptEnglish.sqlite.mode.EvaSendBean;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
//import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
//import com.iyuba.conceptEnglish.util.ConceptApplication;
//import com.iyuba.conceptEnglish.util.ShareUtils;
//import com.iyuba.conceptEnglish.widget.dialog.EvaluatingStudyReportDialog;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.InfoHelper;
//import com.iyuba.core.common.util.LogUtils;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.play.ExtendedPlayer;
//import com.iyuba.play.IJKPlayer;
//
//import net.protyposis.android.mediaplayer.MediaPlayer;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import timber.log.Timber;
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//
//public class EvalFragment extends Fragment implements EvalAdapter.MixSound , OnEvaluationListener {
//
//    @BindView(R.id.tv_read_mix)
//    TextView tvMix;
//    @BindView(R.id.tv_read_sore)
//    TextView tvSore;
//    @BindView(R.id.tv_read_share)
//    TextView tvShare;
//    @BindView(R.id.imv_current_time)
//    TextView tvCurrTime;
//    @BindView(R.id.imv_seekbar_player)
//    SeekBar seekbar;
//    @BindView(R.id.imv_total_time)
//    TextView tvTotalTime;
//
//    @BindView(R.id.recyclerView)
//    RecyclerView recyclerView;
//
//
//    private View rootView;
//    private Context mContext;
//    private CustomDialog waitingDialog;
//
//    private int voaId;
//    private Voa voaTemp;
//    private List<VoaDetail> textDetailTemp;
//
//    public ExtendedPlayer mixPlayer = null;
//    private IJKPlayer player;
//
//    private String soundUrl;
//
//    private EvalAdapter evalAdapter;
//
//    private boolean isMix = false;
//    private boolean isPrepared = false;
//    private String mixUrl;
//
//    private boolean isRePlay = false;
//
//    private String shuoshuoId;
//
//    private int totalScore = 0;
//    private String StringUrls = "";
//    private int sentenceSize = 0;
//    private CorrectSoundDialog soundDialog;
//    private VoaSoundOp voaSoundOp;
//
//
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (((StudyNewActivity) mContext).isDestroyed() && !EvalFragment.this.isAdded()) {
//                return;
//            }
//            switch (msg.what) {
//                case 0:
//                    if ((mixPlayer != null) && (mixPlayer.isPlaying())) {
//                        tvCurrTime.setText(getTime(mixPlayer.getCurrentPosition()));
//                        tvTotalTime.setText(getTime(mixPlayer.getDuration()));
//                        seekbar.setMax(mixPlayer.getDuration());
//                        seekbar.setProgress(mixPlayer.getCurrentPosition());
//                        handler.sendEmptyMessageDelayed(0, 300L);
//                    } else {
//                        EvalFragment.this.handler.removeMessages(0);
//                        EvalFragment.this.tvMix.setText("试听");
//                    }
//                    break;
//                case 1:
//                    String addscore = String.valueOf(msg.arg1);
//                    if (addscore.equals("5")) {
//                        String mg = "语音成功发送至排行榜，恭喜您获得了" + addscore + "分";
//                        CustomToast.showToast(mContext, mg, 3000);
//                    } else {
//                        String mg = "语音成功发送至排行榜";
//                        CustomToast.showToast(mContext, mg, 3000);
//                    }
//
//                    //刷新排行榜数据
//                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.eval_rank));
//
//                    if (InfoHelper.getInstance().openShare()){
//                        tvShare.setText("分享");
//                    }else {
//                        tvShare.setText("发布");
//                    }
//                    break;
//            }
//        }
//    };
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        if (rootView == null) {
//            rootView = inflater.inflate(R.layout.fragment_eval, container, false);
//        }
//        ButterKnife.bind(this, rootView);
//        mContext = getActivity();
//        waitingDialog = WaittingDialog.showDialog(this.mContext);
//        getCurrentArticleInfo();
//        initData();
//        initNewPlayer();
//        initMixPlayer();
//        soundDialog= new CorrectSoundDialog();
//        voaSoundOp=new VoaSoundOp(mContext);
//        return rootView;
//    }
//
//    public void getCurrentArticleInfo() {
//        voaTemp = VoaDataManager.Instace().voaTemp;
//        textDetailTemp = VoaDataManager.Instace().voaDetailsTemp;
//        voaId = voaTemp.voaId;
//    }
//
//    private void initData() {
//        recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
//        evalAdapter = new EvalAdapter(this.textDetailTemp, this.mContext);
//        evalAdapter.setEvalFragment(this);
//        evalAdapter.setOnEvaluationListener(this);
//        recyclerView.setAdapter(evalAdapter);
//        evalAdapter.setMixSound(this);
//
//        //新的回调操作
//        evalAdapter.setOnEvalCallBackListener(new EvalAdapter.OnEvalCallBackListener() {
//            @Override
//            public void clickItem(int position) {
//                //停止播放
//                pauseNewAudio();
//            }
//
//            @Override
//            public void audioPlay(long startTime, long endTime) {
//                if (exoPlayer!=null){
//                    if (exoPlayer.isPlaying()){
//                        pauseNewAudio();
//                    }else {
//                        playNewAudio(startTime, endTime);
//                    }
//                }else {
//                    ToastUtil.showToast(getActivity(),"播放器未进行初始化");
//                }
//            }
//
//            @Override
//            public void recordAudio() {
//                //停止播放
//                pauseNewAudio();
//            }
//
//            @Override
//            public void evalPlay() {
//                //停止播放
//                pauseNewAudio();
//            }
//
//            @Override
//            public void publishEval() {
//                //停止播放
//                pauseNewAudio();
//            }
//
//            @Override
//            public void shareEval() {
//                //停止播放
//                pauseNewAudio();
//            }
//        });
//    }
//
//    private void initMixPlayer() {
//        if (mixPlayer == null) {
//            mixPlayer = new ExtendedPlayer(mContext);
//        }
//        mixPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            public void onPrepared(MediaPlayer mp) {
//                isPrepared = true;
//                tvMix.setText("暂停");
//                handler.sendEmptyMessage(0);
//            }
//        });
//        mixPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                try {
//                    seekbar.setProgress(mp.getDuration());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//
//        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
//                if ((fromUser) && (EvalFragment.this.mixPlayer != null) && (EvalFragment.this.mixPlayer.isPlaying())) {
//                    mixPlayer.seekTo(progress);
//                }
//            }
//
//            public void onStartTrackingTouch(SeekBar bar) {
//            }
//
//            public void onStopTrackingTouch(SeekBar bar) {
//
//            }
//        });
//    }
//
//
//    //本地音频
//    private String getLocalSoundPath() {
//        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            return "";
//        }
//        /*switch (VoaDataManager.getInstance().voaTemp.lessonType) {
//            case TypeLibrary.BookType.conceptFourUS:
//            case TypeLibrary.BookType.conceptJunior:
//            default:
//                // 美音原文音频的存放路径
//                String pathString = Constant.videoAddr + voaTemp.voaId + Constant.append;
//                File fileTemp = new File(pathString);
//                if (fileTemp.exists()) {
//                    return pathString;
//                }
//                break;
//            case TypeLibrary.BookType.conceptFourUK:
//                // 英音原文音频的存放路径
//                String pathStringEng = Constant.videoAddr + voaTemp.voaId + "_B" + Constant.append;
//                File fileTempEng = new File(pathStringEng);
//                if (fileTempEng.exists()) {
//                    return pathStringEng;
//                }
//                break;
//        }*/
//
//        //更换地址
//        String pathString = FilePathUtil.getHomeAudioPath(voaTemp.voaId,voaTemp.lessonType);
//        File file = new File(pathString);
//        if (file.exists()){
//            return pathString;
//        }
//
//        return "";
//    }
//
//    //获取当前章节的音频网络路径
//    private String getRemoteSoundPath(){
//        String soundUrl = null;
//        //这里针对会员和非会员不要修改，测试也不要修改
//        if (UserInfoManager.getInstance().isVip()){
//            soundUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
//        }else {
//            soundUrl=Constant.sound;
//        }
//
////        switch (ConfigManager.Instance().getBookType()) {
//        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
//            case TypeLibrary.BookType.conceptFourUS:
//            default:
//                //美音
//                soundUrl = soundUrl
//                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
//                        + "_"
//                        + VoaDataManager.getInstance().voaTemp.voaId % 1000
//                        + Constant.append;
//                break;
//            case TypeLibrary.BookType.conceptFourUK: //英音
//                soundUrl = soundUrl
//                        + "british/"
//                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
//                        + "/"
//                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
//                        + "_"
//                        + VoaDataManager.getInstance().voaTemp.voaId % 1000
//                        + Constant.append;
//                break;
//            case TypeLibrary.BookType.conceptJunior:
//                soundUrl = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
//                        + VoaDataManager.getInstance().voaTemp.voaId
//                        + "/"
//                        + VoaDataManager.getInstance().voaTemp.voaId
//                        + Constant.append;
//                break;
//        }
//
//        Log.d("当前播放路径", "远程路径：--"+soundUrl);
//
//        return soundUrl;
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        voaSoundOp.temporaryReplaceReal();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        try {
//            player.reset();
//            IJKPlayer.endNative();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void reStart() {
//        //评测后重置和好合成按钮
//        tvMix.setText("合成");
//        tvCurrTime.setText("00:00");
//        tvTotalTime.setText("00:00");
//        tvSore.setVisibility(View.INVISIBLE);
//        seekbar.setProgress(0);
//        isMix = false;
//    }
//
//    public void refreshData() {
//        getCurrentArticleInfo();
//        initData();
//        initNewPlayer();
//        reStart();
//    }
//
//    public void stopMixPlayer() {
//        if ((mixPlayer != null) && (mixPlayer.isPlaying())) {
//            try {
//                mixPlayer.pause();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        if (evalAdapter != null) {
//            evalAdapter.stopAllVoice(null);
//            stopMixPlayer();
//        }
//    }
//
//
//    private String getTime(int time) {
//        time /= 1000;
//        int minute = time / 60;
//        int second = time % 60;
//        minute %= 60;
//        return String.format("%02d:%02d", minute, second, Locale.CHINA);
//    }
//
//
//    //合成语音上发至服务器
//    private void sendToRank() {
//        if (!UserInfoManager.getInstance().isLogin()) {
//            ToastUtil.showToast(mContext, "请登录后再执行此操作");
//            return;
//        }
//        waitingDialog.show();
//        String currVoaId;
////        switch (ConfigManager.Instance().getBookType()) {
//        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
//            case TypeLibrary.BookType.conceptJunior:
//            case TypeLibrary.BookType.conceptFourUS:
//            default:
//                currVoaId = String.valueOf(voaTemp.voaId);
//                break;
//            case TypeLibrary.BookType.conceptFourUK:
//                currVoaId = String.valueOf(voaTemp.voaId * 10);
//                break;
//        }
//        AudioSendApi audioSendApi = ApiRetrofit.getInstance().getAudioSendApi();
//        audioSendApi.audioSendApi(AudioSendApi.BASEURL, Constant.EVAL_TYPE,
//                AudioSendApi.platform, AudioSendApi.format,
//                AudioSendApi.protocol, String.valueOf(UserInfoManager.getInstance().getUserId()),
//                TextAttr.encode(UserInfoManager.getInstance().getUserName()),
//                currVoaId, totalScore / sentenceSize + "", "4", mixUrl,Constant.APP_ID,1).enqueue(new Callback<EvaSendBean>() {
//            @Override
//            public void onResponse(Call<EvaSendBean> call, Response<EvaSendBean> response) {
//
//
//                if (response.isSuccessful()) {
//                    EvaSendBean evaSendBean = response.body();
//                    shuoshuoId = evaSendBean.getShuoshuoId() + "";
//                    String resultNew = evaSendBean.getResultCode();
//                    if (resultNew.equals("501")) {
//                        waitingDialog.dismiss();
//                        Message msg = handler.obtainMessage();
//                        msg.what = 1;
//                        msg.arg1 = evaSendBean.getAddScore();
//                        handler.sendMessage(msg);
//
//                        //显示奖励的信息
//                        double price = Integer.parseInt(evaSendBean.getReward())*0.01;
//                        if (price>0){
//                            price = BigDecimalUtil.trans2Double(price);
//                            String showMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show),price);
//                            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_dialog,showMsg));
//                        }
//                    }
//                } else {
//                    ToastUtil.showToast(mContext, "发布失败");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<EvaSendBean> call, Throwable t) {
//
//            }
//        });
//
//    }
//
//
//    private boolean addToList() {
//        totalScore = 0;
//        StringUrls = "";
//        sentenceSize = 0;
//        ArrayList localArrayList = new VoaSoundOp(mContext).findDataByvoaId(voaId);
//
//        for (int i = 0; i < localArrayList.size(); i++) {
//            VoaSound voaSound = (VoaSound) localArrayList.get(i);
//
//            if (!TextUtils.isEmpty(voaSound.sound_url)) {
//                totalScore += voaSound.totalScore;
//                StringUrls = String.format("%s%s,", new Object[]{this.StringUrls, voaSound.sound_url});
//                sentenceSize++;
//            }
//
//        }
//
//        if (sentenceSize <= 1) {
//            ToastUtil.showToast(this.mContext, "至少读两句方可合成");
//            return false;
//        }
//        return true;
//    }
//
//
//    @OnClick(R.id.tv_read_mix)
//    void mixOrPlayUrl() {
//        Log.e("试听", this.tvMix.getText().toString());
////        IJKPlayer textPlayer;
////        android.media.MediaPlayer textPlayer;
////        if (BackgroundManager.Instace().bindService != null) {
////            textPlayer = BackgroundManager.Instace().bindService.getPlayer();
////            if ((textPlayer != null) && (textPlayer.isPlaying())) {
////                textPlayer.pause();
////            }
////        }
//        //暂停音频播放
//        EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
//        EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
//
//        if (evalAdapter != null) {
//            evalAdapter.stopAllVoice(null);
//        }
//        if ("合成".equals(tvMix.getText().toString())) {
//            if (addToList()){
//                boolean isCurrentMonth=true;
//                int itemIndex=0;
//                for (int i = 0; i < textDetailTemp.size(); i++) {
//                    VoaDetail v = textDetailTemp.get(i);
//                    if (v.isRead){
//                        int start=v.evaluateBean.getURL().indexOf("/")+1;
//                        int end=v.evaluateBean.getURL().indexOf("/concept");
//                        String result=v.evaluateBean.getURL().substring(start,end);
//                        int year=Integer.parseInt(result.substring(0,4));
//                        int month=Integer.parseInt(result.substring(4));
//                        int currentYear=getCurrentDate("yyyy");
//                        int currentMonth=getCurrentDate("MM");
//                        isCurrentMonth=(year==currentYear&&month==currentMonth);
//                        if (!isCurrentMonth){
//                            itemIndex=i;
//                            break;
//                        }
//                    }
//                }
//                if (isCurrentMonth){
//                    audioCompose(StringUrls);
//                }else {
//                    ToastUtil.showToast(mContext,"当前合成录音中含非本月的录音数据, 第"+(itemIndex+1)+"句;请重新录制后再进行合成");
//                }
//            }
//        } else if ("试听".equals(tvMix.getText().toString())) {
//            if (mixPlayer == null) {
//                return;
//            }
//            if (isRePlay) {
//                isRePlay = false;
//                isPrepared = true;
//                new Thread(() -> {
//                    mixPlayer.initialize(Constant.EVAL_PREFIX + mixUrl);
//                    mixPlayer.prepareAndPlay();
//                }).start();
//                return;
//            }
//            if (mixPlayer.isPausing()) {
//                mixPlayer.start();
//                handler.sendEmptyMessage(0);
//                tvMix.setText("暂停");
//                return;
//            }
//
//            if (!isPrepared) {
//                isPrepared = true;
//                new Thread(() -> {
//                    mixPlayer.initialize(Constant.EVAL_PREFIX + mixUrl);
//                    mixPlayer.prepareAndPlay();
//                }).start();
//
//            } else {
//                try {
//                    mixPlayer.start();
//                    handler.sendEmptyMessage(0);
//                    tvMix.setText("暂停");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        } else if ("暂停".equals(this.tvMix.getText().toString())) {
//            if (mixPlayer == null) {
//                return;
//            }
//            tvMix.setText("试听");
//            mixPlayer.pause();
//        }
//    }
//
//    /**
//     * 合成语音
//     *
//     * @param audios
//     */
//    private void audioCompose(String audios) {
//        AudioComposeApi audioComposeApi = ApiRetrofit.getInstance().getAudioComposeApi();
//        Timber.d("%s %s %s", AudioComposeApi.BASEURL, audios, Constant.EVAL_TYPE);
//        audioComposeApi.audioComposeApi(AudioComposeApi.BASEURL, audios, Constant.EVAL_TYPE).enqueue(new Callback<EvaMixBean>() {
//            @Override
//            public void onResponse(Call<EvaMixBean> call, Response<EvaMixBean> response) {
//                if (response.isSuccessful()) {
//                    LogUtils.e("返回数据==" + response.body());
//                    EvaMixBean evaMixBean = response.body();
//                    String result = evaMixBean.getResult();
//
//                    if ("1".equals(result)) {
//                        //成功
//                        mixUrl = evaMixBean.getURL();
//                        LogUtils.e("返回数据==" + mixUrl);
//                        tvSore.setVisibility(View.VISIBLE);
//                        tvSore.setText(totalScore / sentenceSize + "");
//                        tvMix.setText("试听");
//                        isMix = true;
//                        isRePlay = true;
//                        seekbar.setProgress(0);
//
//                        if (ConfigManager.Instance().getsendEvaReport()
//                                && UserInfoManager.getInstance().isLogin()) {
//                            //弹出 评测详情框
//                            EvaluatingStudyReportDialog.getInstance()
//                                    .init(mContext)
//                                    .setData(textDetailTemp, totalScore / sentenceSize, sentenceSize)
//                                    .prepare()
//                                    .show();
//                        }
//                    } else {
//                        ToastUtil.showToast(mContext, "合成失败，请稍后再试 错误码：" + result);
//                    }
//                } else {
//                    ToastUtil.showToast(mContext, "合成网络连接失败，请稍后再试");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<EvaMixBean> call, Throwable t) {
//                ToastUtil.showToast(mContext, "合成网络请求失败，请稍后再试");
//            }
//        });
//    }
//
//
//    @OnClick(R.id.tv_read_share)
//    void sendOrShare() {
//        if ("发布".equals(this.tvShare.getText().toString())) {
//            //发布逻辑
//            if (!UserInfoManager.getInstance().isLogin()) {
//                ToastUtil.showToast(mContext, "请登录后再发布");
//                return;
//            }
//
//            if (!isMix) {
//                ToastUtil.showToast(mContext, "请先合成后再发布");
//                return;
//            }
//            sendToRank();
//        } else {
//            //分享逻辑
//            String userName;
//            if (TextUtils.isEmpty(UserInfoManager.getInstance().getUserName())) {
//                userName = String.valueOf(UserInfoManager.getInstance().getUserId());
//            } else {
//                userName = UserInfoManager.getInstance().getUserName();
//            }
//            String content = voaTemp.title;
//            String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + shuoshuoId + "&shuoshuo=" + mixUrl + "&apptype=" + Constant.EVAL_TYPE;
//            String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
//            String title = userName + "在评测中获得了" + (totalScore / sentenceSize) + "分";
//            ShareUtils localShareUtils = new ShareUtils();
//            localShareUtils.setMContext(mContext);
//            localShareUtils.setVoaId(voaId);
//            localShareUtils.showShare(mContext,
//                    imageUrl,
//                    siteUrl,
//                    title,
//                    content,
//                    localShareUtils.platformActionListener,
//                    new ShareContentCustomizeCallback() {
//                        @Override
//                        public void onShare(Platform platform, Platform.ShareParams shareParams) {
//                            shareParams.setShareType(Platform.SHARE_WEBPAGE);
//                        }
//                    });
//        }
//
//
//    }
//
//    private void showSound(VoaDetail currentItem,String currentWord){
//        if (!soundDialog.isAdded()){
//            ConceptApplication.currentEvalLength=currentItem.readResult.length();
//            soundDialog.showNow(getChildFragmentManager(),"");
//            soundDialog.changeContent(currentItem,currentWord);
//        }
//    }
//
//    private int getCurrentDate(String type){
//        SimpleDateFormat format = new SimpleDateFormat(type,Locale.CHINA);
//        String time=format.format(new Date(System.currentTimeMillis()));
//        return Integer.parseInt(time);
//    }
//
//
//    @Override
//    public void showDialog(@NonNull VoaDetail currentItem, @NonNull String word) {
//        showSound(currentItem,word);
//    }
//
//    /*******************************原文音频*****************************/
//    private ExoPlayer exoPlayer;
//    //是否加载完成
//    private boolean isPlayerPrepare = false;
//    //音频计时器标志
//    private static final String tag_audioTimer = "audioTimer";
//
//    private void initNewPlayer(){
//        exoPlayer = new ExoPlayer.Builder(getActivity()).build();
//        exoPlayer.setPlayWhenReady(false);
//        exoPlayer.addListener(new Player.Listener() {
//            @Override
//            public void onPlaybackStateChanged(int playbackState) {
//                switch (playbackState){
//                    case Player.STATE_READY:
//                        isPlayerPrepare = true;
//                        //加载完成
//                        break;
//                    case Player.STATE_ENDED:
//                        //播放完成
//                        break;
//                }
//            }
//
//            @Override
//            public void onPlayerError(PlaybackException error) {
//                ToastUtil.showToast(getActivity(),"播放器初始化异常("+error.errorCode+")");
//            }
//        });
//
//
//        //加载音频
//        String playUrl = null;
//        if (TextUtils.isEmpty(getLocalSoundPath())){
//            playUrl = getRemoteSoundPath();
//        }else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                playUrl = String.valueOf(FileProvider.getUriForFile(getActivity(),getResources().getString(R.string.file_provider_name_personal),new File(getLocalSoundPath())));
//            }else {
//                playUrl = String.valueOf(Uri.fromFile(new File(getLocalSoundPath())));
//            }
//        }
//        MediaItem mediaItem = MediaItem.fromUri(playUrl);
//        exoPlayer.setMediaItem(mediaItem);
//        exoPlayer.prepare();
//    }
//
//    //播放音频
//    private void playNewAudio(long startTime,long endTime){
//        if (!isPlayerPrepare){
//            ToastUtil.showToast(getActivity(),"正在初始化播放器");
//            return;
//        }
//
//        //计算并显示动画
//        exoPlayer.seekTo(startTime);
//        exoPlayer.play();
//
//        startAudioTimer(startTime,endTime);
//    }
//
//    //暂停播放
//    private void pauseNewAudio(){
//        stopAudioTimer();
//        if (exoPlayer!=null&&exoPlayer.isPlaying()){
//            exoPlayer.pause();
//        }
//        evalAdapter.refreshAudioPlay(0,0,false);
//    }
//
//    //开启原文计时
//    private void startAudioTimer(long startTime,long endTime){
//        stopAudioTimer();
//        RxTimer.getInstance().multiTimerInMain(tag_audioTimer, 0, 200L, new RxTimer.RxActionListener() {
//            @Override
//            public void onAction(long number) {
//                //刷新显示
//                long totalTime = endTime-startTime;
//                long progressTime = exoPlayer.getCurrentPosition()-startTime;
//
//                Log.d("原文播放进度显示", progressTime+"---"+totalTime);
//
//                evalAdapter.refreshAudioPlay((int) progressTime, (int) totalTime,true);
//
//                if (progressTime>=totalTime){
//                    pauseNewAudio();
//                }
//            }
//        });
//    }
//
//    //关闭原文计时
//    private void stopAudioTimer(){
//        RxTimer.getInstance().cancelTimer(tag_audioTimer);
//    }
//}
