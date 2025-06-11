//package com.iyuba.conceptEnglish.study;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.media.MediaPlayer;
//import android.media.PlaybackParams;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.bumptech.glide.Glide;
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.MediaItem;
//import com.google.android.exoplayer2.PlaybackException;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.extractor.ExtractorsFactory;
//import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.iyuba.ConstantNew;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.activity.MainFragmentActivity;
//import com.iyuba.conceptEnglish.activity.WebActivity;
//import com.iyuba.conceptEnglish.ad.AdInitManager;
//import com.iyuba.conceptEnglish.api.AiyubaAdvApi;
//import com.iyuba.conceptEnglish.api.data.AiyubaAdvResult;
//import com.iyuba.conceptEnglish.event.PlayControlEvent;
//import com.iyuba.conceptEnglish.han.UpdateStudyRecordNewThread;
//import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
//import com.iyuba.conceptEnglish.han.utils.ExpandKt;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.searchWord.SearchWordDialog;
//import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
//import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
//import com.iyuba.conceptEnglish.manager.BackgroundManager;
//import com.iyuba.conceptEnglish.manager.VoaDataManager;
//import com.iyuba.conceptEnglish.sqlite.mode.ArticleRecordBean;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.op.ArticleRecordOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaDetailYouthOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
//import com.iyuba.conceptEnglish.util.AccountUtil;
//import com.iyuba.conceptEnglish.util.AdBannerUtil;
//import com.iyuba.conceptEnglish.util.NextVideoNew;
//import com.iyuba.conceptEnglish.util.ScreenUtils;
//import com.iyuba.conceptEnglish.widget.WordCard;
//import com.iyuba.conceptEnglish.widget.dialog.ListenStudyReportDialog;
//import com.iyuba.conceptEnglish.widget.subtitle.SubtitleSum;
//import com.iyuba.conceptEnglish.widget.subtitle.SubtitleSynView;
//import com.iyuba.conceptEnglish.widget.subtitle.TextPageSelectTextCallBack;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.data.DataManager;
//import com.iyuba.core.common.data.model.VoaText;
//import com.iyuba.core.common.data.model.VoaWord2;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.util.NetStateUtil;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.module.toolbox.RxUtil;
//import com.iyuba.play.IJKPlayer;
//import com.iyuba.sdk.other.NetworkUtil;
//import com.yd.saas.base.interfaces.AdViewBannerListener;
//import com.yd.saas.config.exception.YdError;
//import com.yd.saas.ydsdk.YdBanner;
//import com.youdao.sdk.nativeads.YouDaoNative;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//import okhttp3.OkHttpClient;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import timber.log.Timber;
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//
//public class TextFragment extends Fragment {
//    @BindView(R.id.text_center)
//    SubtitleSynView textCenter;
//    @BindView(R.id.seek_bar)
//    SeekBar seekBar;
//    @BindView(R.id.cur_time)
//    TextView curTime;
//    @BindView(R.id.total_time)
//    TextView totalTime;
//    @BindView(R.id.abplay)
//    RelativeLayout abplay;
//    @BindView(R.id.text_play_speed_05)
//    TextView textPlaySpeed05;
//    @BindView(R.id.text_play_speed_10)
//    TextView textPlaySpeed10;
//    @BindView(R.id.text_play_speed_125)
//    TextView textPlaySpeed125;
//    @BindView(R.id.text_play_speed_15)
//    TextView textPlaySpeed15;
//    @BindView(R.id.text_play_speed_20)
//    TextView textPlaySpeed20;
//    @BindView(R.id.one_video)
//    ImageView oneVideo;
//    @BindView(R.id.re_one_video)
//    RelativeLayout reOneVideo;
//    @BindView(R.id.light_left)
//    ImageView lightLeft;
//    @BindView(R.id.light_bar)
//    SeekBar lightBar;
//    @BindView(R.id.light_right)
//    ImageView lightRight;
//    @BindView(R.id.ll_more_function)
//    LinearLayout llMoreFunction;
//    @BindView(R.id.CHN)
//    ImageView CHN;
//    @BindView(R.id.re_CHN)
//    RelativeLayout reCHN;
//    @BindView(R.id.former_button)
//    RelativeLayout formerButton;
//    @BindView(R.id.video_play)
//    ImageButton videoPlay;
//    @BindView(R.id.next_button)
//    RelativeLayout nextButton;
//    @BindView(R.id.function_button)
//    RelativeLayout functionButton;
//
//    @BindView(R.id.iv_sync)
//    ImageView ivSync;
//
//    @BindView(R.id.seekbar_speed)
//    SeekBar seekbarSpeed;
//
//    @BindView(R.id.word)
//    WordCard card;
//    private static final int closeHint=123;
//
//    private View rootView;
//    private Context mContext;
//    private CustomDialog mWaittingDialog;
//    private ListenStudyReportDialog show;
//
//    private List<VoaDetail> textDetailTemp;
//    private VoaDetailOp textDetailOp;
//    private VoaDetailYouthOp mVoaDetailYouthOp;
//    private SubtitleSum subtitleSum;
//    private String soundUrl;
//    private Disposable mDispose_SyncVoaSub;
//    private DataManager mDataManager;
//    private List<VoaWord2> mTimpWordList;
//
//    private AtomicBoolean atomicBoolean;
//
//    //是否可以进行播放操作
//    private boolean isCanStartPlay = true;
//
//    //广告显示
//    @BindView(R.id.re_ad)
//    RelativeLayout frAd;
//
//    @BindView(R.id.tv_close_ad)
//    TextView tvCloseAd;
//
//    @BindView(R.id.img_ad)
//    ImageView imgAd;
//
//    private YdBanner ydBanner;
//    private AdBannerUtil adBannerUtil;
//
//    public Handler wordHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    //展示单词卡
//                    card.setVisibility(View.VISIBLE);
//                    //添加到临时数据中
//                    boolean isAdd = true;
//                    VoaWord2 word2 = new VoaWord2();
//                    word2.word = card.getKeyStirng();
//                    word2.def = card.getDefStirng();
//                    if (TextUtils.isEmpty(word2.word)) {
//                        //空的不添加
//                        break;
//                    }
//                    for (VoaWord2 w : mTimpWordList) {
//                        if (w.word.equals(word2.word)) {
//                            isAdd = false;
//                        }
//                    }
//                    if (isAdd) {
//                        mTimpWordList.add(word2);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    TextPageSelectTextCallBack tp = new TextPageSelectTextCallBack() {
//        @Override
//        public void selectTextEvent(String selectText) {
//            Log.d("单词查询", "开始----");
//
//            if (!TextUtils.isEmpty(selectText)&&selectText.matches("^[a-zA-Z]*")) {
//                //更换单词查询方式
////                card.setVisibility(View.VISIBLE);
////                card.searchWord(selectText, mContext, wordHandler);
//                Log.e("单词查询", "开始----0000");
//                showSearchWordDialog(selectText);
//            } else {
//                Log.e("单词查询", "开始----1111");
//                CustomToast.showToast(mContext, R.string.play_please_take_the_word, 1000);
//            }
//            Log.e("iyuba", "game over");
//        }
//
//        @Override
//        public void selectParagraph(int paragraph) {
//        }
//    };
//
//    //暂时换成exoPlayer
////    private IJKPlayer player;
//    private MediaPlayer mediaPlayer;
//
//    // info
//    private int mode = 0, source = 0;// 循环模式，来源（最新\本地）
//    private int voaId;
//    private Voa voaTemp;
//    private int currParagraph;
//    private VoaOp voaOp;
//
//    private boolean hasChinese;
//
//    private ArticleRecordOp articleRecordOp;
//    private int listenPercent;
//
//    public Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (((StudyNewActivity) mContext).isDestroyed()) {
//                return;
//            }
//            switch (msg.what) {
//
//                case 0:
//                    card.setVisibility(View.VISIBLE);
//                    break;
//                case 1:
//                    if (mediaPlayer.isPlaying()) {
//                        seekBar.setMax(mediaPlayer.getDuration());
//                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                        curTime.setText(getTime(mediaPlayer.getCurrentPosition()));
//                        totalTime.setText(getTime(mediaPlayer.getDuration()));
//
//                        currParagraph = subtitleSum.getParagraph(mediaPlayer.getCurrentPosition() * 1.00f / 1000);
//                        if (currParagraph != 0) {
//                            textCenter.snyParagraph(currParagraph);
//                        }
//                        videoPlay.setImageResource(R.drawable.image_pause);
//
//
//                    } else if (!mediaPlayer.isPlaying()) {
//                        seekBar.setMax(mediaPlayer.getDuration());
//                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                        curTime.setText(getTime(mediaPlayer.getCurrentPosition()));
//                        totalTime.setText(getTime(mediaPlayer.getDuration()));
//
//                        videoPlay.setImageResource(R.drawable.image_play);
//                        textCenter.unsnyParagraph();
//                    }
//                    handler.sendEmptyMessageDelayed(1, 300L);
//                    break;
//                case closeHint:
//                    show.closeSelf();
//                    break;
//            }
//
//        }
//    };
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        if (rootView == null) {
//            rootView = inflater.inflate(R.layout.fragment_text, container, false);
//        }
//        ButterKnife.bind(this, rootView);
//        mContext = getActivity();
//        EventBus.getDefault().register(this);
//
//
//        articleRecordOp = new ArticleRecordOp(mContext);
//        textDetailOp = new VoaDetailOp(this.mContext);
//        mVoaDetailYouthOp = new VoaDetailYouthOp(mContext);
//        mTimpWordList = new ArrayList<>();
//        mDataManager = DataManager.getInstance();
//        mWaittingDialog = WaittingDialog.showDialog(mContext);
//        atomicBoolean = new AtomicBoolean(false);
//
//        card.setWordCard(mContext);
//        card.setVisibility(View.GONE);
//        getCurrentArticleInfo();
//        initText();
//        initPlayer();
//        playArticle(isCanStartPlay);//播放地址
//        llMoreFunction.setVisibility(View.GONE);
//        source = getActivity().getIntent().getIntExtra("source", 0);
//
//        textPlaySpeed10.setOnClickListener(view -> {
//            if (!AccountUtil.isVip()) {
//                ExpandKt.goSomeAction(mContext,"调速功能");
//            } else {
//                //弹出倍速
//                showSpeed();
//            }
//        });
//        return rootView;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        initAD();
//        requestAd();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (AccountUtil.isVip()) {
//            float speed = ConfigManager.Instance().loadFloat("playSpeed", 1.0f);
//            seekbarSpeed.setProgress((int) (speed * 10) - 5);
//            textPlaySpeed10.setText("倍速 " + speed + "x");
//            if (speed == 1.99f) {
//                textPlaySpeed10.setText(String.format("%s %s", "倍速", "2.0x"));
//            }
//        }
//
//        atomicBoolean.set(true);
//        mode = ConfigManager.Instance().loadInt("mode", 1);
//        setModeBackground();
//
//        if (!TextUtils.isEmpty(isToNext)){
//            refreshAudioUrl();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        atomicBoolean.set(false);
//
//        isToNext = "true";
//
//        closeSearchWordDialog();
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//        isCanStartPlay = isVisibleToUser;
//    }
//
//    //刷新音频的url
//    private void refreshAudioUrl(){
//        //这里把音频修改过来
//        VoaDataManager.Instace().voaTemp = voaTemp;
//        playArticle(false);
//        seekBar.setProgress(0);
//        curTime.setText(getTime(0));
//    }
//
//    //标识位
//    private String isToNext = "";
//
//    public void initText() {
//
//        synchronized (MainFragmentActivity.class){
//            if (VoaDataManager.Instace().voaDetailsTemp != null) {
//                textDetailTemp = VoaDataManager.Instace().voaDetailsTemp;
//            }
//        }
//
//        if (textDetailTemp == null || textDetailTemp.size() == 0) {
//            ToastUtil.show(mContext, "暂无数据，请重新进入");
//            getActivity().finish();
//            return;
//        }
//        synchronized (MainFragmentActivity.class){
//            for (VoaDetail ddd : textDetailTemp) {
//                Log.e("句子", ddd.sentence + "--" + ddd.endTime);
//            }
//        }
//        subtitleSum = VoaDataManager.Instace().subtitleSum;
//        textCenter.setSubtitleSum(subtitleSum);
//        textCenter.setTpstcb(tp);
//        textCenter.syncho = true;
//        ivSync.setImageResource(R.drawable.icon_sync);
//        card.setVisibility(View.GONE);
//    }
//
//    private void playNext() {
//        if (ConfigManager.Instance().isYouth()) {
//            youthPlayNext();
//        } else {
//            fourVolumesPlayNext();
//        }
//    }
//
//    /**
//     * 青少版 播放下一个
//     */
//    private void youthPlayNext() {
//
//        List<VoaDetail> list = mVoaDetailYouthOp.getVoaDetailByVoaid(voaId);
//        if (list == null || list.size() == 0) {
//            getYouthDataFromNet();
//            return;
//        }
//        //转换数据
//        youthUnitDataTranslate(list);
//    }
//
//    private void getYouthDataFromNet() {
//
//        if (getActivity() != null && !getActivity().isDestroyed()) {
//            mWaittingDialog.show();
//        }
//        RxUtil.dispose(mDispose_SyncVoaSub);
//        mDispose_SyncVoaSub = mDataManager.syncVoaTexts(voaId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<VoaText>>() {
//                    @Override
//                    public void accept(List<VoaText> voaTexts) throws Exception {
//                        if (mWaittingDialog.isShowing()
//                                && getActivity() != null
//                                && !getActivity().isDestroyed()) {
//                            mWaittingDialog.dismiss();
//                        }
//                        if (voaTexts != null && voaTexts.size() != 0) {
//                            //转换数据
//                            youthUnitDataTranslate(mVoaDetailYouthOp.voaTextTranslateToVoaDetail(voaId, voaTexts));
//                            //存储数据
//                            mVoaDetailYouthOp.insertOrReplaceData(voaId, voaTexts);
//                        } else {
//                            ToastUtil.showToast(mContext, "进入失败，无法获取课文详情");
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        if (mWaittingDialog.isShowing()
//                                && getActivity() != null
//                                && !getActivity().isDestroyed()) {
//                            mWaittingDialog.dismiss();
//                        }
//                    }
//                });
//    }
//
//
//    private void youthUnitDataTranslate(List<VoaDetail> timpList) {
//        //进入的逻辑
//        if (VoaDataManager.Instace().voaDetailsTemp == null) {
//            VoaDataManager.Instace().voaDetailsTemp = new ArrayList<>();
//        }
//        VoaDataManager.Instace().voaDetailsTemp.clear();
//        VoaDataManager.Instace().voaDetailsTemp.addAll(timpList);
//
//
//        voaOp = new VoaOp(mContext);
//        voaTemp = voaOp.findDataById(voaId);
//        VoaDataManager.Instace().voaTemp = voaTemp;
//        VoaDataManager.Instace().setSubtitleSum(voaTemp, VoaDataManager.Instace().voaDetailsTemp);
//        textCenter.selectParagraph(0);
//        textCenter.snyParagraph(0);
//
//        //播放
//        playTextFragment();
//    }
//
//    /**
//     * 全四册播放下一个
//     */
//    private void fourVolumesPlayNext() {
//        reFreshData();
//
//        //播放
//        playTextFragment();
//    }
//
//    private void playTextFragment() {
//        playArticle(true);//播放
//
//        if (!((StudyNewActivity) mContext).isDestroyed()) {
//            if (!isAdded()) {
//                return;
//            }
//            initText();
//            ((StudyNewActivity) mContext).refreshVoaData();
//        }
//    }
//
//
//    private int finish_voaId = 1001;
//
//
//    private void initPlayer() {
//        /*player = BackgroundManager.Instace().bindService.getPlayer();
//        player.setPlaySpeed(ConfigManager.Instance().loadFloat("playSpeed", 1.0F));
//        player.setOnCompletionListener((IMediaPlayer mp) -> {
//            //播放完成后的操作
//            ArticleRecordBean bean = new ArticleRecordBean();
//            bean.voa_id = voaId;
//            bean.is_finish = 1;
//            new ArticleRecordOp(mContext).updateData(bean);
//            finish_voaId = voaId;
//
//            //显示弹窗信息
//            mode = ConfigManager.Instance().loadInt("mode");
//            boolean autoPlay = ConfigManager.Instance().loadAutoPlay();
//            if (mContext != null
//                    && getActivity() != null
//                    && !getActivity().isDestroyed()
//                    && ConfigManager.Instance().getSendListenReport()
//                    && AccountUtil.isLogin()
//                    && atomicBoolean.get()) {
//                //显示弹窗
//                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.read_refresh_tips));
//                //这里调用提交学习记录接口，然后显示加载弹窗，等回调数据之后显示学习报告弹窗
//                startSubmit(true,true);
//            } else if (autoPlay){
//                continuePlay();
//                //提交学习记录
//                startSubmit(true,false);
//            }
//        });
//        player.setOnPreparedListener(paramAnonymousMediaPlayer -> {
//            //加载完成后的操作
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//            BackgroundManager.Instace().bindService.startTime = df.format(new Date());
//            voaId=VoaDataManager.getInstance().voaTemp.voaId;
//            BackgroundManager.Instace().bindService.setTag(voaId);
//            EventBus.getDefault().post(new PlayControlEvent());
//            voaTemp=VoaDataManager.getInstance().voaTemp;
//            BackgroundManager.Instace().bindService.notifyNotification(voaTemp,true);
//
//            switch (ConfigManager.Instance().loadInt("curBook")) {
//                default:
//                    break;
//                case 4:
//                    ConfigManager.Instance().putInt("lately_four", voaId);
//                    break;
//                case 3:
//                    ConfigManager.Instance().putInt("lately_three", voaId);
//                    break;
//                case 2:
//                    ConfigManager.Instance().putInt("lately_two", voaId);
//                    break;
//                case 1:
//                    ConfigManager.Instance().putInt("lately_one", voaId);
//            }
//            handler.sendEmptyMessageDelayed(1, 1000L);
//            if (!((StudyNewActivity) mContext).isDestroyed()) {
//                if (!isAdded()) {
//                    return;
//                }
//                videoPlay.setImageResource(R.drawable.image_pause);
//            }
//        });
//        player.setOnErrorListener((IMediaPlayer iMediaPlayer, int i, int i1) -> {
//            int a = 0;
//            ToastUtil.showToast(getActivity(),"播放器加载异常，请重试~");
//            return true;
//        });*/
//
//        //重新初始化新的播放器
//        mediaPlayer = BackgroundManager.Instace().bindService.getPlayer();
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                //加载完成后的操作
//                try {
//                    PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
//                    playbackParams.setSpeed(ConfigManager.Instance().loadFloat("playSpeed", 1.0F));
//                    mediaPlayer.setPlaybackParams(playbackParams);
//                }catch (Exception e){
//
//                }
//
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//                BackgroundManager.Instace().bindService.startTime = df.format(new Date());
//                voaId=VoaDataManager.getInstance().voaTemp.voaId;
//                BackgroundManager.Instace().bindService.setTag(voaId);
//                EventBus.getDefault().post(new PlayControlEvent());
//                voaTemp=VoaDataManager.getInstance().voaTemp;
//                BackgroundManager.Instace().bindService.notifyNotification(voaTemp,true);
//
//                switch (ConfigManager.Instance().loadInt("curBook")) {
//                    default:
//                        break;
//                    case 4:
//                        ConfigManager.Instance().putInt("lately_four", voaId);
//                        break;
//                    case 3:
//                        ConfigManager.Instance().putInt("lately_three", voaId);
//                        break;
//                    case 2:
//                        ConfigManager.Instance().putInt("lately_two", voaId);
//                        break;
//                    case 1:
//                        ConfigManager.Instance().putInt("lately_one", voaId);
//                }
//                handler.sendEmptyMessageDelayed(1, 1000L);
//                if (!((StudyNewActivity) mContext).isDestroyed()) {
//                    if (!isAdded()) {
//                        return;
//                    }
//                    videoPlay.setImageResource(R.drawable.image_pause);
//                }
//            }
//        });
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                //播放完成后的操作
//                mediaPlayer.pause();
//                mediaPlayer.seekTo(0);
//
//                ArticleRecordBean bean = new ArticleRecordBean();
//                bean.voa_id = voaId;
//                bean.is_finish = 1;
//                new ArticleRecordOp(mContext).updateData(bean);
//                finish_voaId = voaId;
//
//                //显示弹窗信息
//                mode = ConfigManager.Instance().loadInt("mode");
//                boolean autoPlay = ConfigManager.Instance().loadAutoPlay();
//                if (mContext != null
//                        && getActivity() != null
//                        && !getActivity().isDestroyed()
//                        && ConfigManager.Instance().getSendListenReport()
//                        && AccountUtil.isLogin()
//                        && atomicBoolean.get()) {
//                    //显示弹窗
//                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.read_refresh_tips));
//                    //这里调用提交学习记录接口，然后显示加载弹窗，等回调数据之后显示学习报告弹窗
//                    startSubmit(true,true);
//                } else if (autoPlay){
//                    continuePlay();
//                    //提交学习记录
//                    startSubmit(true,false);
//                }
//            }
//        });
//        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
////                ToastUtil.showToast(getActivity(),"播放器加载异常，请重试~("+what+")");
//                return true;
//            }
//        });
//
//        seekBar.getParent().requestDisallowInterceptTouchEvent(true);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
//                if (fromUser) {
//
//                    mediaPlayer.seekTo(progress);
//                    currParagraph = subtitleSum.getParagraph(mediaPlayer.getCurrentPosition() / 1000.0);
//
//                    if (currParagraph != 0) {
//                        textCenter.snyParagraph(currParagraph);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar arg0) {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                }
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (!mediaPlayer.isPlaying()) {
//                    mediaPlayer.start();
//                }
//            }
//        });
//
//        seekbarSpeed.setProgress((int) (ConfigManager.Instance().loadFloat("playSpeed") * 10));
//        seekbarSpeed.getParent().requestDisallowInterceptTouchEvent(true);
//        seekbarSpeed.setOnSeekBarChangeListener(
//                new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                        if (AccountUtil.isVip()) {
//                            float speed = (float) ((progress + 5) / 10.0);
//                            ConfigManager.Instance().putFloat("playSpeed", speed);
//                            try {
//                                PlaybackParams params = mediaPlayer.getPlaybackParams();
//                                params.setSpeed(speed);
//                                mediaPlayer.setPlaybackParams(params);
//                            }catch (Exception e){
//
//                            }
//                            textPlaySpeed10.setText(String.format("%sx", speed));
//                        } else {
//                            ExpandKt.goSomeAction(mContext,"调速功能");
//                        }
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//
//                    }
//                }
//        );
//    }
//
//    /**
//     * 播放完成后继续播放
//     */
//    private void continuePlay() {
//        Timber.e("播放mode" + mode);
//        if (mode == 0) {//单曲循环
//            mediaPlayer.seekTo(0);
//            mediaPlayer.start();
//        } else if (mode == 1) {//循环播放 下一曲
//            //好像有卡住的情况，也不是错误，就是暂停无法播放 过一段时间后操作可恢复
//            voaId = new NextVideoNew(voaId).following();
//            playNext();
//        } else if (mode == 2) {
//            //随机播放
//            voaId = new NextVideoNew(voaId).nextVideo();
//            playNext();
//        }
//        //清除临时数据
//        mTimpWordList.clear();
//    }
//
//    public void showSpeed() {
//        String[] items = new String[]{"0.5x", "0.6x", "0.7x", "0.8x", "0.9x", "1.0x", "1.1x",
//                "1.2x", "1.3x", "1.4x", "1.5x", "1.6x", "1.7x", "1.8x", "1.9x", "2.0x"};
//        new AlertDialog.Builder(mContext)
//                .setItems(items, (dialog, which) -> {
//                    switch (which) {
//                        case 0:
//                            changeSpeed(0.5f);
//                            break;
//                        case 1:
//                            changeSpeed(0.6f);
//                            break;
//                        case 2:
//                            changeSpeed(0.7f);
//                            break;
//                        case 3:
//                            changeSpeed(0.8f);
//                            break;
//                        case 4:
//                            changeSpeed(0.9f);
//                            break;
//                        case 5:
//                            changeSpeed(1.0f);
//                            break;
//                        case 6:
//                            changeSpeed(1.1f);
//                            break;
//                        case 7:
//                            changeSpeed(1.2f);
//                            break;
//                        case 8:
//                            changeSpeed(1.3f);
//                            break;
//                        case 9:
//                            changeSpeed(1.4f);
//                            break;
//                        case 10:
//                            changeSpeed(1.5f);
//                            break;
//                        case 11:
//                            changeSpeed(1.6f);
//                            break;
//                        case 12:
//                            changeSpeed(1.7f);
//                            break;
//                        case 13:
//                            changeSpeed(1.8f);
//                            break;
//                        case 14:
//                            changeSpeed(1.9f);
//                            break;
//                        case 15:
//                            changeSpeed(1.99f);
//                            break;
//                    }
//                    dialog.dismiss();
//                })
//                .create()
//                .show();
//    }
//
//    private void changeSpeed(float speed) {
//        if (ConfigManager.Instance().loadInt("isvip") >= 1) {
//            try {
//                PlaybackParams params = mediaPlayer.getPlaybackParams();
//                params.setSpeed(speed);
//                mediaPlayer.setPlaybackParams(params);
//            }catch (Exception e){
//
//            }
//            ConfigManager.Instance().putFloat("playSpeed", speed);
//            textPlaySpeed10.setText("倍速 " + speed + "x");
//            if (speed == 1.99f) {
//                textPlaySpeed10.setText("倍速 2.0x");
//            }
//        } else {
//            Toast.makeText(mContext, "成为VIP用户即可调节播放速度！", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 提交学习记录
//     */
//    private void startSubmit(boolean isEnd,boolean isShowReport){
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE);// 设置日期格式
//        String endTime = df.format(new Date());
//        new Thread(new UpdateStudyRecordNewThread(voaId,
//                currParagraph,
//                endTime,wordsCount(endTime),isEnd,true,isShowReport)).start();
//    }
//
//    /**
//     * 计算提交时间段的单词数
//     */
//    private int wordsCount(String endTime) {
//        int wordNum = 0;
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 设置日期格式
//
//        try {
//            if (TextUtils.isEmpty(BackgroundManager.Instace().bindService.startTime)) {
//                wordNum = 0;
//                return wordNum;
//            }
//            int timeAll = (int) ((df.parse(endTime).getTime() - df.parse(BackgroundManager.Instace().bindService.startTime).getTime()));
//            wordNum = timeAll * words / mediaPlayer.getDuration();
//            Log.e("url", timeAll + "==" + mediaPlayer.getDuration() + "==" + words + "====" + wordNum);
//            if (wordNum > words) {
//                wordNum = words;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            wordNum = 0;
//        }
//
//        return wordNum;
//    }
//
//
//    public void reFreshData() {
//        voaOp = new VoaOp(mContext);
//        voaTemp = voaOp.findDataById(voaId);
//        VoaDataManager.Instace().voaDetailsTemp = textDetailOp.findDataByVoaId(voaId);
//        VoaDataManager.Instace().voaTemp = voaTemp;
//        VoaDataManager.Instace().setSubtitleSum(voaTemp, VoaDataManager.Instace().voaDetailsTemp);
//        textCenter.selectParagraph(0);
//        textCenter.snyParagraph(0);
//    }
//
//    public void getCurrentArticleInfo() {
//        voaTemp = VoaDataManager.Instace().voaTemp;
//        synchronized (MainFragmentActivity.class){
//            textDetailTemp = VoaDataManager.Instace().voaDetailsTemp;
//        }
//        subtitleSum = VoaDataManager.Instace().subtitleSum;
//        if (voaTemp == null) {
//            voaId = 0;
//        } else {
//            voaId = voaTemp.voaId;
//        }
//    }
//
//    private void playArticle(boolean isPlay) {
//        if (voaTemp==null){
//            return;
//        }
//
//        voaTemp=VoaDataManager.Instace().voaTemp;
//        if (BackgroundManager.Instace().bindService.getTag() == voaTemp.voaId) {
//            BackgroundManager.Instace().bindService.startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
//            handler.sendEmptyMessage(1);
//            return;
//        }
//
//        try {
//            int formerVoaId = BackgroundManager.Instace().bindService.getTag();
//            ArticleRecordBean bean = articleRecordOp.getData(formerVoaId);
//
//            if (bean == null) {
//                bean = new ArticleRecordBean();
//                bean.voa_id = formerVoaId;
//                bean.curr_time = (int) (mediaPlayer.getCurrentPosition() / 1000);
//                bean.total_time = (int) (mediaPlayer.getDuration() / 1000);
//                bean.is_finish = 0;
//                articleRecordOp.updateData(bean);
//            } else if (bean.is_finish == 0 && bean.curr_time < mediaPlayer.getCurrentPosition() / 1000) {
//                bean.curr_time = (int) (mediaPlayer.getCurrentPosition() / 1000);
//                articleRecordOp.updateData(bean);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //此处开线程意义不太
//        /*new Thread(() -> {
//            try {
//                if (TextUtils.isEmpty(getLocalSoundPath())) {
//                    String headUrl;
//
//                    //这里针对会员和非会员不要修改，测试也不要修改
//                    if (AccountManager.getInstance().isVip()){
//                        headUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
//                    }else {
//                        headUrl=Constant.sound;
//                    }
//
//                    switch (ConfigManager.Instance().getBookType()) {
//                        case AMERICA:
//                        default:
//                            //美音
//                            soundUrl = headUrl
//                                    + voaTemp.voaId / 1000
//                                    + "_"
//                                    + voaTemp.voaId % 1000
//                                    + Constant.append;
//                            break;
//                        case ENGLISH: //英音
//                            soundUrl = headUrl
//                                    + "british/"
//                                    + voaTemp.voaId / 1000
//                                    + "/"
//                                    + voaTemp.voaId / 1000
//                                    + "_"
//                                    + voaTemp.voaId % 1000
//                                    + Constant.append;
//                            break;
//                        case YOUTH:
//                            soundUrl = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
//                                    + voaTemp.voaId
//                                    + "/"
//                                    + voaTemp.voaId
//                                    + Constant.append;
//                            break;
//                    }
//                    Log.d("123459678913", "playArticle: _____________________"+soundUrl);
//                    if (NetStateUtil.isConnected(requireContext())) {
//                        //如果有网络才播放
//                        player.reset();
//                        player.initialize(soundUrl);
//
//                        if (isPlay){
//                            player.prepareAndPlay();
//                        }
//                    } else {
//                        //线程中
//                        ToastUtil.show(requireContext(), getResources().getString(R.string.network_error));
//                        handlerText.sendEmptyMessage(2);
//                    }
//                } else {
//                    soundUrl = getLocalSoundPath();
//                    player.reset();
//                    player.initialize(soundUrl);
//                    if (isPlay){
//                        player.prepareAndPlay();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();*/
//
//        try {
//            if (TextUtils.isEmpty(getLocalSoundPath())) {
//                String headUrl;
//
//                //这里针对会员和非会员不要修改，测试也不要修改
//                if (AccountManager.getInstance().isVip()){
//                    headUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
//                }else {
//                    headUrl=Constant.sound;
//                }
//
//                /*switch (ConfigManager.Instance().getBookType()) {*/
//                switch (VoaDataManager.getInstance().voaTemp.lessonType) {
//                    case TypeLibrary.BookType.conceptFourUS:
//                    default:
//                        //美音
//                        soundUrl = headUrl
//                                + voaTemp.voaId / 1000
//                                + "_"
//                                + voaTemp.voaId % 1000
//                                + Constant.append;
//                        break;
//                    case TypeLibrary.BookType.conceptFourUK: //英音
//                        soundUrl = headUrl
//                                + "british/"
//                                + voaTemp.voaId / 1000
//                                + "/"
//                                + voaTemp.voaId / 1000
//                                + "_"
//                                + voaTemp.voaId % 1000
//                                + Constant.append;
//                        break;
//                    case TypeLibrary.BookType.conceptJunior:
//                        soundUrl = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
//                                + voaTemp.voaId
//                                + "/"
//                                + voaTemp.voaId
//                                + Constant.append;
//                        break;
//                }
//                Log.d("123459678913", "playArticle: _____________________"+soundUrl);
//                if (NetStateUtil.isConnected(requireContext())) {
//                    //如果有网络才播放
//                    mediaPlayer.reset();
//                    mediaPlayer.setDataSource(getActivity(), Uri.parse(soundUrl));
//
//                    if (isPlay){
//                        mediaPlayer.prepareAsync();
//                    }
//                } else {
//                    //线程中
//                    ToastUtil.show(requireContext(), getResources().getString(R.string.network_error));
//                    handlerText.sendEmptyMessage(2);
//                }
//            } else {
//                soundUrl = getLocalSoundPath();
//                mediaPlayer.reset();
//                mediaPlayer.setDataSource(soundUrl);
//                if (isPlay){
//                    mediaPlayer.prepareAsync();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String getLocalSoundPath() {
//        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            return "";
//        }
//
////        switch (ConfigManager.Instance().getBookType()) {
//        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
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
//        }
//
//        return "";
//    }
//
//
//    private String getTime(int progress) {
//        progress /= 1000;
//        return String.format("%02d:%02d", new Object[]{Integer.valueOf(progress / 60 % 60), Integer.valueOf(progress % 60)});
//    }
//
//
//    private int words;
//
//    public void getCounts(List<VoaDetail> details) {
//        if (details != null) {
//            words = 0;
//            for (VoaDetail detail : details) {
//                words += getWordCounts(detail.sentence);
//                Log.e("Tag-content:", detail.sentence + "count:" + getWordCounts(detail.sentence));
//            }
//        }
//
//    }
//
//    public int getWordCounts(String content) {
//        return content.split(" ").length;
//    }
//
//    @OnClick(R.id.re_sync)
//    void syncText() {
//        if (textCenter.syncho) {
//            textCenter.syncho = false;
//            ToastUtil.showToast(mContext, "文本自动滚动关闭");
//            ivSync.setImageResource(R.drawable.icon_unsync);
//        } else {
//            textCenter.syncho = true;
//            ToastUtil.showToast(mContext, "文本自动滚动开启");
//            ivSync.setImageResource(R.drawable.icon_sync);
//        }
//    }
//
//
//    @OnClick({R.id.re_CHN})
//    void setLanguage() {
//        hasChinese = !hasChinese;
//        ConfigManager.Instance().putBoolean("showChinese", hasChinese);
//        setShowChineseButton();
//    }
//
//    private void setShowChineseButton() {
//        if (hasChinese) {
//            CHN.setImageResource(R.drawable.show_chinese_selected);
//            VoaDataManager.Instace().changeLanguage(false);
//        } else {
//            CHN.setImageResource(R.drawable.show_chinese);
//            VoaDataManager.Instace().changeLanguage(true);
//        }
//        textCenter.updateSubtitleView();
//    }
//
//    /* 原本没有public限定符 */
//    @OnClick({R.id.video_play})
//    public void playOrPause() {
//        if (!isCanStartPlay){
//            BackgroundManager.Instace().bindService.notifyNotification(voaTemp,false);
//            videoPlay.setImageResource(R.drawable.image_play);
//            mediaPlayer.pause();
//            return;
//        }
//
//        if (mediaPlayer.isPlaying()) {
//            BackgroundManager.Instace().bindService.notifyNotification(voaTemp,false);
//            videoPlay.setImageResource(R.drawable.image_play);
//            mediaPlayer.pause();
//            ArticleRecordBean bean = articleRecordOp.getData(voaId);
//            if (bean == null) {
//                bean = new ArticleRecordBean();
//                bean.voa_id = voaId;
//                bean.curr_time = mediaPlayer.getCurrentPosition() / 1000;
//                bean.total_time = mediaPlayer.getDuration() / 1000;
//                bean.percent = currParagraph;
//                bean.is_finish = 0;
//                articleRecordOp.updateData(bean);
//
//            } else if (bean.is_finish == 0 && bean.curr_time < mediaPlayer.getCurrentPosition() / 1000) {
//                bean.curr_time = mediaPlayer.getCurrentPosition() / 1000;
//                bean.total_time = mediaPlayer.getDuration() / 1000;
//                bean.percent = currParagraph;
//                articleRecordOp.updateData(bean);
//            }
//            if (mediaPlayer.getDuration() != 0)
//                listenPercent = 100 * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
//            startSubmit(false,false);
////            new Thread(new UpdateStudyRecordunfinishThread()).start();
//        } else if (!mediaPlayer.isPlaying()) {
//            BackgroundManager.Instace().bindService.notifyNotification(voaTemp,true);
//            BackgroundManager.Instace().bindService.startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
//            videoPlay.setImageResource(R.drawable.image_play);
//            mediaPlayer.start();
//        }else {
//            playArticle(true);
//        }
//    }
//
//    @OnClick({R.id.re_one_video})
//    void setPlayMode() {
//        mode = ((mode + 1) % 3);
//        ConfigManager.Instance().putInt("mode", mode);
//        setModeBackground();
//        if (mode == 0) {
//            CustomToast.showToast(mContext, R.string.study_repeatone, 1000);
//        } else if (mode == 1) {
//            CustomToast.showToast(mContext, R.string.study_follow, 1000);
//        } else if (mode == 2) {
//            CustomToast.showToast(mContext, R.string.study_random, 1000);
//        }
//    }
//
//    private void setModeBackground() {
//        if (mode == 0) {
//            oneVideo.setImageDrawable(getResources().getDrawable(R.drawable.play_this));
//        } else if (mode == 1) {
//            oneVideo.setImageDrawable(getResources().getDrawable(R.drawable.play_next));
//        } else if (mode == 2) {
//            oneVideo.setImageDrawable(getResources().getDrawable(R.drawable.play_random));
//        }
//    }
//
//    @OnClick({R.id.next_button})
//    void next() {
//        if (currParagraph < VoaDataManager.Instace().voaDetailsTemp.size()) {
//            mediaPlayer.seekTo((int) (VoaDataManager.Instace().voaDetailsTemp.get(currParagraph).startTime * 1000));
//            //这里可能导致切换时回弹的问题
//            //textCenter.unsnyParagraph();
//        } else {
//            CustomToast.showToast(mContext, R.string.study_last, 2000);
//        }
//    }
//
//    @OnClick({R.id.former_button})
//    void former() {
//        if (currParagraph != 0 && currParagraph != 1) {
//            // 将videoView移动到指定的时间
//            mediaPlayer.seekTo((int) VoaDataManager.Instace().voaDetailsTemp.get(currParagraph - 2).startTime * 1000);
//            //这里可能导致切换时回弹的问题
//            //textCenter.unsnyParagraph();
//        } else {
//            CustomToast.showToast(mContext, R.string.study_first, 2000);
//        }
//    }
//
//    @OnClick({R.id.function_button})
//    void moreFunction() {
//        if (llMoreFunction.getVisibility() == View.VISIBLE) {
//            llMoreFunction.setVisibility(View.GONE);
//        } else {
//            llMoreFunction.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private int aPositon, bPosition, abState = 0;// aPosition,bPosition分别存放
//    // a，b的位置，abState显示现在是a还是b
//    // A-B播放
//
//    @OnClick({R.id.abplay})
//    void abPlay() {
//        // abState最开始是0
//        abState++;
//        if (abState % 3 == 1) {
//            aPositon = mediaPlayer.getCurrentPosition();
//            CustomToast.showToast(mContext, R.string.study_ab_a, 2000);
//        } else if (abState % 3 == 2) {
//            bPosition = mediaPlayer.getCurrentPosition();
//            handlerText.sendEmptyMessage(1);// 开始A-B循环
//            CustomToast.showToast(mContext, R.string.study_ab_b, 1000);
//        } else if (abState % 3 == 0) {
//            handlerText.sendEmptyMessage(0);// 手动停止
//        }
//    }
//
//    private Handler handlerText = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    CustomToast.showToast(mContext, "区间播放已取消", 1000);
//                    handlerText.removeMessages(1);// 手动停止A-B播放
//                    break;
//                case 1:
//                    mediaPlayer.seekTo(aPositon);// A-B播放
//                    handlerText.sendEmptyMessageDelayed(1, bPosition - aPositon + 300);
//                    break;
//                case 2:
//                    ToastUtil.showToast(getContext(), "无网络连接,无法播放");
//                    break;
//                default:
//                    break;
//
//            }
//        }
//    };
//
//    public void refreshSelf() {
//        initText();
//    }
//
//
//    public void setTextSize(int textSize) {
//        textCenter.setTextSize(textSize);
//    }
//
//    public boolean isPlayerPlaying() {
//        if (mediaPlayer == null) {
//            return false;
//        } else {
//            return mediaPlayer.isPlaying();
//        }
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//        handlerText.removeCallbacksAndMessages(null);
//        handler.removeCallbacksAndMessages(null);
//        wordHandler.removeCallbacksAndMessages(null);
//
//        if (adBannerUtil!=null){
//            adBannerUtil.destroyAd();
//        }
//        if (ydBanner!=null){
//            ydBanner.destroy();
//        }
//    }
//
//    /*********************单词查询****************/
//    //单词查询的弹窗
//    private SearchWordDialog searchWordDialog;
//    //显示查询弹窗
//    private void showSearchWordDialog(String word){
//        searchWordDialog = new SearchWordDialog(getActivity(),word);
//        searchWordDialog.create();
//        searchWordDialog.show();
//    }
//
//    //关闭查询弹窗
//    private void closeSearchWordDialog(){
//        if (searchWordDialog!=null&&searchWordDialog.isShowing()){
//            searchWordDialog.dismiss();
//        }
//    }
//
//    /*****************************广告显示****************************/
//    private AiyubaAdvApi advApi = null;
//
//    //初始化广告
//    public void initAD() {
//        Retrofit retrofit;
//        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
//        retrofit = new Retrofit.Builder().
//                client(client).
//                addConverterFactory(GsonConverterFactory.create()).baseUrl(AiyubaAdvApi.BASEURL).build();
//        Log.e("Tag--myadv", "广告自己的");
//        advApi = retrofit.create(AiyubaAdvApi.class);
//    }
//
//    //请求广告
//    private void requestAd() {
//        //先隐藏广告界面
//        frAd.setVisibility(View.GONE);
//
//        //设置统一的广告高度
//        boolean isVip = ConfigManager.Instance().getIsVip() > 0;
//        if (NetworkUtil.isConnected(getActivity()) && !isVip && AdInitManager.isShowAd()) {
//            String uid = ConfigManager.Instance().getUserId();
//            advApi.getAdvByaiyuba(uid, Constant.APPID, AiyubaAdvApi.FLAG).enqueue(new Callback<List<AiyubaAdvResult>>() {
//                @Override
//                public void onResponse(Call<List<AiyubaAdvResult>> call, Response<List<AiyubaAdvResult>> response) {
//                    if (response.isSuccessful()) {
//
//                        final AiyubaAdvResult result = response.body().get(0);
//                        if (result.getResult().equals("1")) {
//                            Log.d("广告显示", "onResponse: banner--"+result.getData().getType()+"--"+result.getData().getStartuppic()+"--"+result.getData().getStartuppic_Url());
//
//                            // TODO: 2023/9/14 展姐在[中小学英语书虫讨论组]中明确说明ads2使用共通广告模块显示
//                            switch (result.getData().getType()) {
//                                case AdvertisingKey.web:
//                                    loadIyubaAD(result.getData().getStartuppic(),result.getData().getStartuppic_Url());
//                                    break;
//                                case AdvertisingKey.youdao:
//                                    loadYoudaoAD(result);
//                                    break;
//                                case AdvertisingKey.ads1:
//                                case AdvertisingKey.ads2:
//                                case AdvertisingKey.ads3:
//                                case AdvertisingKey.ads4:
//                                case AdvertisingKey.ads5:
//                                    loadIyubaSdkAD(result);
//                                    break;
//                                default:
//                                    loadIyubaAD(result.getData().getStartuppic(),result.getData().getStartuppic_Url());
//                                    break;
//                            }
////                            ConfigManager.Instance().setBannerImg("http://app."+Constant.IYUBA_CN+"dev/"+result.getData().getStartuppic());
//                        } else {
//                            loadIyubaAD(null,null);
//                        }
//                    } else {
//                        loadIyubaAD(null,null);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<AiyubaAdvResult>> call, Throwable t) {
//                    loadIyubaAD(null,null);
//                }
//            });
//        }else {
//            frAd.setVisibility(View.GONE);
//        }
//    }
//
//    //有道banner广告
//    private void loadYoudaoAD(AiyubaAdvResult result){
//        Log.d("广告显示", "loadYoudaoAD: --有道");
//
//        adBannerUtil.loadYouDaoAD();
//        adBannerUtil.setOnCallBackListener(new AdBannerUtil.OnCallBackListener() {
//            @Override
//            public void onSuccess() {
//                frAd.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFail() {
//                Log.d("广告显示", "loadYoudaoAD: --有道错误，显示默认");
//                loadIyubaAD(result.getData().getStartuppic(),result.getData().getStartuppic_Url());
//            }
//        });
//    }
//
//    //爱语吧通用广告
//    private void loadIyubaSdkAD(AiyubaAdvResult result){
//        Log.d("广告显示", "sdk广告");
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //获取宽高
////                int width = ScreenUtils.getScreenWidth(StudyNewActivity.this)-10;
//                int width = ScreenUtils.getScreenWidth(getActivity());
//                int height = ScreenUtils.getScreenWidth(getActivity())*4/20;
//                //设置宽高
//                ViewGroup.LayoutParams params = frAd.getLayoutParams();
//                params.width = width;
//                params.height = height;
//                frAd.setLayoutParams(params);
//
//                ydBanner = new YdBanner.Builder(getActivity())
//                        .setKey(ConstantNew.SDK_BANNER_CODE)
//                        .setMaxTimeoutSeconds(5)
//                        .setWidth(width)
//                        .setHeight(height)
//                        .setBannerListener(new AdViewBannerListener() {
//                            @Override
//                            public void onReceived(View view) {
//                                if (getActivity().isFinishing()||getActivity().isDestroyed()){
//                                    destroyAd();
//                                    return;
//                                }
//
//                                frAd.setVisibility(View.VISIBLE);
//                                frAd.addView(view);
//                            }
//
//                            @Override
//                            public void onAdExposure() {
//
//                            }
//
//                            @Override
//                            public void onAdClick(String s) {
//                                pausePlayer();
//                            }
//
//                            @Override
//                            public void onClosed() {
//                                destroyAd();
//                                frAd.setVisibility(View.GONE);
//                            }
//
//                            @Override
//                            public void onAdFailed(YdError ydError) {
//                                Log.d("广告显示", "onAdFailed: --"+ydError.getMsg()+"--"+ydError.getCode()+"--"+ydError.getErrorType());
//                                destroyAd();
//                                loadIyubaAD(result.getData().getStartuppic(),result.getData().getStartuppic_Url());
//                            }
//                        }).build();
//                ydBanner.requestBanner();
//            }
//        });
//    }
//
//    //网页广告
//    private void loadIyubaAD(String picUrl,String jumpUrl){
//        Log.d("广告显示", "loadIyubaAD: --网页广告，作为默认显示--"+picUrl+"--"+jumpUrl);
//
//        if (!TextUtils.isEmpty(picUrl)){
//            picUrl = "http://static3."+Constant.IYUBA_CN+"dev/"+picUrl;
//
//            if (!TextUtils.isEmpty(jumpUrl)){
//                //配合数据展示
//            }else {
//                jumpUrl = null;
//            }
//        }else {
//            picUrl = ConfigManager.Instance().getBannerImg();
//            jumpUrl = "http://app."+Constant.IYUBA_CN;
//        }
//
//        loadErrorAd(picUrl, jumpUrl);
//    }
//
//    //错误样式广告
//    private void loadErrorAd(String picUrl,String jumpUrl){
//        frAd.setVisibility(View.VISIBLE);
//        tvCloseAd.setVisibility(View.VISIBLE);
//
//        //设置宽高
//        ViewGroup.LayoutParams params = frAd.getLayoutParams();
//        params.width = ScreenUtils.getScreenWidth(getActivity());
//        params.height = ScreenUtils.dp2px(getActivity(),50);
//        frAd.setLayoutParams(params);
//
//        Glide.clear(imgAd);
//        Glide.with(getActivity()).load(picUrl).asBitmap().error(R.drawable.ic_ad_banner_error).into(imgAd);
//        imgAd.setOnClickListener(v -> {
//            pausePlayer();
//
//            if (TextUtils.isEmpty(jumpUrl)){
//                ToastUtil.showToast(getActivity(),"暂无内容");
//                return;
//            }
//
//            Intent intent = new Intent();
//            intent.setClass(mContext, WebActivity.class);
//            intent.putExtra("url", jumpUrl);
//            startActivity(intent);
//        });
//    }
//
//    //销毁广告
//    private void destroyAd(){
//        if (adBannerUtil!=null){
//            adBannerUtil.destroyAd();
//        }
//        if (ydBanner!=null){
//            ydBanner.destroy();
//        }
//    }
//
//    //暂停音频
//    private void pausePlayer(){
//        BackgroundManager.Instace().bindService.notifyNotification(voaTemp,false);
//        videoPlay.setImageResource(R.drawable.image_play);
//        mediaPlayer.pause();
//    }
//
//    /**************************学习报告显示************************/
//    //显示学习报告
//    private void showReadReportDialog(String reward){
//        VoaWordOp voaWordOp = new VoaWordOp(mContext);
//        show = ListenStudyReportDialog.getInstance()
//                .init(mContext)
//                .setData(reward,voaWordOp.findDataByVoaId(voaId), mTimpWordList, this::continuePlay)
//                .setOnDialogTouchListener(i -> handler.removeMessages(closeHint))
//                .prepare()
//                .show();
//        handler.sendEmptyMessageDelayed(closeHint,5000);
//    }
//
//    /*************************数据回调*****************************/
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(RefreshDataEvent event){
//        if (event.getType().equals(TypeLibrary.RefreshDataType.reward_refresh_toast)){
//            //显示toast弹窗
//
//            //关闭加载
//            closeLoading();
//
//            if (!TextUtils.isEmpty(event.getMsg())){
//                //显示toast
//                ToastUtil.showToast(getActivity(),event.getMsg());
//            }else {
//                //显示弹窗
//                showReadReportDialog(event.getTips());
//            }
//
//            //刷新用户信息并填充
//            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
//        }
//
//        if (event.getType().equals(TypeLibrary.RefreshDataType.read_refresh_tips)){
//            //原文加载弹窗
//            showLoading();
//        }
//
//        if (event.getType().equals(TypeLibrary.RefreshDataType.study_next)){
//            //下一个
//            continuePlay();
//        }
//    }
//
//    /***************************加载弹窗***************************/
//    private LoadingDialog loadingDialog;
//
//    private void showLoading(){
//        if (loadingDialog==null){
//            loadingDialog = new LoadingDialog(getActivity());
//            loadingDialog.create();
//        }
//        loadingDialog.setMsg("正在加载学习报告～");
//        loadingDialog.show();
//    }
//
//    private void closeLoading(){
//        if (loadingDialog!=null&&loadingDialog.isShowing()){
//            loadingDialog.dismiss();
//        }
//    }
//}
