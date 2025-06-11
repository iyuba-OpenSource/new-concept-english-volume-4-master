//package com.iyuba.conceptEnglish.activity;
//
//import android.app.ActivityGroup;
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Color;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnPreparedListener;
//import android.media.PlaybackParams;
//import android.net.Uri;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ViewFlipper;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager.widget.ViewPager;
//import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;
//import com.bumptech.glide.Glide;
//import com.google.android.exoplayer2.ExoPlayer;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.adapter.CommentListAdapterNew;
//import com.iyuba.conceptEnglish.adapter.RankListAdapterNew;
//import com.iyuba.conceptEnglish.adapter.ValReadAdapter;
//import com.iyuba.conceptEnglish.adapter.ViewPagerAdapter;
//import com.iyuba.conceptEnglish.api.AiyubaAdvApi;
//import com.iyuba.conceptEnglish.api.ApiRetrofit;
//import com.iyuba.conceptEnglish.api.AudioComposeApi;
//import com.iyuba.conceptEnglish.api.AudioSendApi;
//import com.iyuba.conceptEnglish.api.data.AiyubaAdvResult;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.util.BigDecimalUtil;
//import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
//import com.iyuba.conceptEnglish.listener.AppUpdateCallBack;
//import com.iyuba.conceptEnglish.listener.RequestCallBack;
//import com.iyuba.conceptEnglish.manager.BackgroundManager;
//import com.iyuba.conceptEnglish.manager.RecordManager;
//import com.iyuba.conceptEnglish.manager.VoaDataManager;
//import com.iyuba.conceptEnglish.protocol.AddCreditsRequest;
//import com.iyuba.conceptEnglish.protocol.CommentRequest;
//import com.iyuba.conceptEnglish.protocol.CommentResponse;
//import com.iyuba.conceptEnglish.protocol.DataCollectRequest;
//import com.iyuba.conceptEnglish.protocol.DataCollectResponse;
//import com.iyuba.conceptEnglish.protocol.ExpressionRequest;
//import com.iyuba.conceptEnglish.protocol.FavorUpdateRequest;
//import com.iyuba.conceptEnglish.protocol.FavorUpdateResponse;
//import com.iyuba.conceptEnglish.protocol.GetRankInfoRequest;
//import com.iyuba.conceptEnglish.protocol.GetRankInfoResponse;
//import com.iyuba.conceptEnglish.sqlite.mode.Comment;
//import com.iyuba.conceptEnglish.sqlite.mode.EvaMixBean;
//import com.iyuba.conceptEnglish.sqlite.mode.EvaSendBean;
//import com.iyuba.conceptEnglish.sqlite.mode.RankUser;
//import com.iyuba.conceptEnglish.sqlite.mode.ReadVoiceComment;
//import com.iyuba.conceptEnglish.sqlite.mode.StudyPcmFile;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
//import com.iyuba.conceptEnglish.util.AdTimeCheck;
//import com.iyuba.conceptEnglish.util.NetWorkState;
//import com.iyuba.conceptEnglish.util.NextVideo;
//import com.iyuba.conceptEnglish.util.OnPlayStateChangedListener;
//import com.iyuba.conceptEnglish.util.Player;
//import com.iyuba.conceptEnglish.util.UtilLightnessControl;
//import com.iyuba.conceptEnglish.util.UtilPostFile;
//import com.iyuba.conceptEnglish.widget.CircleImageView;
//import com.iyuba.conceptEnglish.widget.WordCard;
//import com.iyuba.conceptEnglish.widget.subtitle.SubtitleSum;
//import com.iyuba.conceptEnglish.widget.subtitle.SubtitleSynView;
//import com.iyuba.conceptEnglish.widget.subtitle.TextPageSelectTextCallBack;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.InfoHelper;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.listener.ResultIntCallBack;
//import com.iyuba.core.common.manager.SocialDataManager;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.INetStateReceiver;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.ErrorResponse;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.common.thread.GitHubImageLoader;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.util.LogUtils;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.ContextMenu;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.event.StopTextPlayerEvent;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.core.me.activity.PersonalHome;
//import com.iyuba.multithread.util.NetStatusUtil;
//import com.scwang.smartrefresh.layout.SmartRefreshLayout;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
//import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import okhttp3.OkHttpClient;
//import permissions.dispatcher.NeedsPermission;
//import permissions.dispatcher.OnPermissionDenied;
//import permissions.dispatcher.RuntimePermissions;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//@RuntimePermissions
//public class StudyActivity extends ActivityGroup implements AppUpdateCallBack, ValReadAdapter.ValReadAdapterInteraction, CommentListAdapterNew.OnItemClickListener {
//    public static StudyActivity instance;
//    private String curVoaId;
//    // 通用界面
//    private ContextMenu contextMenu;
//    private ViewPager container = null;// 这个组件，注意这个组件是用来显示左右滑动的界面的，
//    // 如果不加载xml布局文件，他是不会显示内容的。
//    private ViewPagerAdapter viewPagerAdapter;
//    private Context mContext;
//    private Button shareButton, backButton, favorButton;
//    private TextView Title, textButton, knowledgeButton, reReadButton, commentButton, today, week, month, note, userImageText, userName, myUsername, userInfo, title, cpReadWords, exerciseButton, rankButton, commentLoadMoreTextView, totalTime = null, curTime = null, textView_time;
//
//    private LinearLayout voa_words_ln, voa_annotations_ln, voa_important_sentences_ln, voa_diffculty_ln;
//
//    private TextView textPlaySpeed_05, textPlaySpeed_10, textPlaySpeed_125, textPlaySpeed_15, textPlaySpeed_20;
//    private View text, knowledge, read, exercise, comment, rank;
//    private CircleImageView userImage, myImage;
//    private ViewFlipper knowledgeContainer = null;
//    private ImageView voaWord, voaAnno, voaImport, voaDiffcult;
//    private int curSelectActivity = 0;
//    // 通用变量
//    private ArrayList<View> mList;
//    private boolean isvip;
//    private boolean isConnected;
//    private int currentPage = 0, lastPage = 0;
//    private int voaId;
//    private VoaOp voaOp;
//    private Voa voaTemp;
//    private List<VoaDetail> textDetailTemp;
//    private VoaDetailOp textDetailOp;
//    private SubtitleSum subtitleSum;
//    private LayoutInflater inflater;
////    public IJKPlayer videoView = null;  //播放文章用的调速播放器
////    public MediaPlayer videoView = null;  //播放文章用的调速播放器
//    public ExoPlayer exoPlayer;
//
//    // info
//    private int mode = 0, source = 0;// 循环模式，来源（最新\本地）
//    // 原文
//    private boolean syncho, isShowChinese;
//    private SubtitleSynView textCenter = null;
//    private int currParagraph = 1;
//    private int lastParegraph = 0;
//    private SeekBar lightBar;
//    private SeekBar seekBar = null;
//    private ImageButton pause = null;
//    private RelativeLayout functionButton, videoFormer, videoNext, re_CHN, abPlay, re_one_video;
//    private ImageView showChineseButton, modeButton;
//    private LinearLayout functionLayout, textControlLayout;
//    private int player_alltime;
//    // 生词
//    private WordCard card;
//    //评测
//    private Player mixPlayer; //测评合成播放器
//    public MediaPlayer mp = null;      //评测中使用的播放器
//    // 跟读
//    private boolean scorllable = true;
//    // 控制属性
//    private boolean isPaused = false;
//    // handler
//    private final static int PROGRESS_CHANGED = 0;
//    private final static int BUFFER_CHANGED = 2;
//    private final static int NEXT_VIDEO = 3;
//    // 练习
//    private CustomDialog waittingDialog;
//    private ImageView multipleChoice, voaStructure, voaDiffculty;
//    private LinearLayout ll_multipleChoice, ll_voaStructure, ll_voaDiffculty;
//    private TextView tv_multipleChoice, tv_voaStructure, tv_voaDiffculty;
//    private ViewFlipper exerciseContainer = null;
//    private int curExerciseActivity = 0;
//    // 评论
//    private ArrayList<Comment> comments = new ArrayList<Comment>();
//    private int curCommentPage = 1;
//    private boolean isDownloadAll;
//    private Button expressButton;
//    private EditText expressEditText;
//    private String expressWord;
//    private CommentListAdapterNew commentAdapter;
//    private View commentFooter;
//    private boolean commentAll = false;
//    private int aPositon, bPosition, abState = 0;// aPosition,bPosition分别存放
//    // a，b的位置，abState显示现在是a还是b
//    // A-B播放
//    // 语音评论新加
//    private RecyclerView listComment;
//    private SmartRefreshLayout swiperefresh_comment;
//    private ImageButton setModeButton;
//    private Button pressSpeakButton, testListenButton;
//    private View voiceView;
//    private int currMode = 1;// 0是文字评论，1是语音评论
//    private ImageView voiceValue;
//    private RecordManager rManager;
//    private MediaPlayer voiceMediaPlayer;
//    private boolean isUploadVoice = false;
//    private ValReadAdapter valReadAdapter;
//    private String uid = "";
//    private String beginTime, endTime;
//    // private String beginTimeStu;
//    private ListView senListView;
//    //
//    private String type;
//    private String total;
//    public String myName = "";
//    public String myImgSrc = "";
//    public String myScores = "";
//    public String myCount = "";
//    public String myRanking = "";
//    public String result = "";
//    public String message = "";
//    private RankUser champion;
//    private RankListAdapterNew rankListAdapter;
//    private Pattern p;
//    private Matcher m;
//    public List<RankUser> rankUsers = new ArrayList<RankUser>();
//
//
//    //评测语音合成
//    private TextView tv_read_mix, tv_read_share; //合成 分享
//    private TextView imv_current_time, imv_total_time, tv_read_sore;
//    private SeekBar imv_seekbar_player;
//    private int mp3changTime = 0, totalScore; //变化时长,总得分
//    private Boolean isSendSound = false, isMix = false;
//    private String shuoshuoId;
//    private long mp3TotalTime = 0;
//
//
//    private RelativeLayout fail_rela_rank, fail_rela_comment;
//    private Button btn_rank_refresh_rank, btn_rank_refresh_comment;
//
//
//    private SmartRefreshLayout swipeRefreshLayout;
//    private RecyclerView rankListView;
//    private String startRank = "0";
//    private boolean isLastPageRank = false;
//    public List<RankUser> rankUsersList = new ArrayList<RankUser>();
//
//    //录音文件地址
//    private List<File> pmc_file_list = new ArrayList<>();
//    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {
//        @Override
//        public void onStartSend(BaseHttpRequest request, int rspCookie, int totalLen) {
//            Log.e("onStartSend", "---");
//        }
//
//        @Override
//        public void onStartRecv(BaseHttpRequest request, int rspCookie, int totalLen) {
//            Log.e("onStartRecv", "---");
//        }
//
//        @Override
//        public void onStartConnect(BaseHttpRequest request, int rspCookie) {
//            Log.e("onStartConncet", "---");
//        }
//
//        @Override
//        public void onSendFinish(BaseHttpRequest request, int rspCookie) {
//            Log.e("onSendFinish", "---");
//        }
//
//        @Override
//        public void onSend(BaseHttpRequest request, int rspCookie, int len) {
//        }
//
//        @Override
//        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
//        }
//
//        @Override
//        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
//        }
//
//        @Override
//        public void onNetError(BaseHttpRequest request, int rspCookie, ErrorResponse errorInfo) {
//            handler.sendEmptyMessage(4);
//        }
//
//        @Override
//        public void onConnected(BaseHttpRequest request, int rspCookie) {
//        }
//
//        @Override
//        public void onCancel(BaseHttpRequest request, int rspCookie) {
//        }
//    };
//
//
//    private String mixUrl = "";
//
//    private List<StudyPcmFile> list_pcm_file_all = new ArrayList<>();
//
//    TextPageSelectTextCallBack tp = new TextPageSelectTextCallBack() {
//        @Override
//        public void selectTextEvent(String selectText) {
//            // card.setVisibility(View.GONE);
//            if (selectText.matches("^[a-zA-Z]*")) {
//                card.setVisibility(View.VISIBLE);
//                card.searchWord(selectText, mContext, wordHandler);
//            } else {
//                CustomToast.showToast(mContext, R.string.play_please_take_the_word, 1000);
//            }
//            Log.e("iyuba", "game over");
//        }
//
//        @Override
//        public void selectParagraph(int paragraph) {
//            exoPlayer.seekTo((int) (textDetailTemp.get(paragraph).startTime * 1000));
//            currParagraph = paragraph;
//        }
//    };
//    private RelativeLayout rl;
//    private View adView;
//    private ImageView photoImage;
//    AiyubaAdvApi advApi = null;
//
//
//    private void initaiyubaAd() {
//        if (CheckNetWork() && !isvip) {
//
//            if (!AdTimeCheck.setAd()) {
//                return;
//            }
//
//
//            uid = (uid == null && "".equals(uid) ? "0" : uid);
//            Log.e("tag--adurl", "http://app." + Constant.IYUBA_CN + "dev/getAdEntryAll.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&flag=" + AiyubaAdvApi.FLAG);
//            advApi.getAdvByaiyuba(uid, Constant.APPID, AiyubaAdvApi.FLAG).enqueue(new Callback<List<AiyubaAdvResult>>() {
//                @Override
//                public void onResponse(Call<List<AiyubaAdvResult>> call, Response<List<AiyubaAdvResult>> response) {
//                    if (response.isSuccessful()) {
//                        final AiyubaAdvResult result = response.body().get(0);
//                        if (result.getResult().equals("1")) {
//                            tagAd = result.getData().getType();
//                            Log.e("Tag-adtype", tagAd);
//
//                            if ("addam".equals(result.getData().getType())) {
//
//                                Log.e("TAG--Ad", "dameng_show");
//                            } else if ("web".equals(result.getData().getType())) {
//                                Log.e("Tag--myadv", result.getData().getStartuppic_Url());
//                                try {
//                                    if (photoImage != null && !isFinishing())
//                                        Glide.with(getApplicationContext()).load("http://static3." + Constant.IYUBA_CN + "dev/" + result.getData().getStartuppic()).into(photoImage);
//                                } catch (Exception e) {
//
//                                }
//
//                                adView.setVisibility(View.VISIBLE);
//
//
//                                if (rl.getVisibility() == View.VISIBLE) rl.setVisibility(View.GONE);
//
//                                adView.setOnClickListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        startActivity(new Intent(StudyActivity.this, Web.class).putExtra("url", result.getData().getStartuppic_Url()));
//                                    }
//                                });
//                            } else if ("ssp".equals(result.getData().getType())) {
//                                Log.e("ssp", "-----");
//
//                                showJesgoo();
//                            } else {
//
//                                showGugeAd();
//                                Log.e("Tag--ad", "guge");
//                            }
//                        } else {
//
//                            Log.e("Tag--ad", "guge");
//                            showGugeAd();
//                        }
//                    } else {
//
//                        Log.e("Tag--ad", "guge");
//                        showGugeAd();
//                    }
//                    handler.removeMessages(0x111);
//                    handler.sendEmptyMessageDelayed(0x111, 60000);
//                }
//
//                @Override
//                public void onFailure(Call<List<AiyubaAdvResult>> call, Throwable t) {
//                    Log.e("Tag--errot", "广告自己的" + t.toString());
//                    Log.e("Tag--ad", "guge");
//
//                    showGugeAd();
//                    handler.removeMessages(0x111);
//                    handler.sendEmptyMessageDelayed(0x111, 60000);
//                }
//
//            });
//        }
//        return;
//    }
//
//    private void addIcon(ViewGroup viewGroup) {
//        if (viewGroup != null) {
//            TextView ad_Image = new TextView(StudyActivity.this);
//            ad_Image.setText("广告");
//            ad_Image.setTextColor(Color.WHITE);
//            ad_Image.setTextSize(15.0f);
//            ad_Image.setBackgroundColor(Color.parseColor("#77000000"));
//            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            viewGroup.addView(ad_Image, layoutParams1);
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    private void showJesgoo() {
//        rl.setVisibility(View.VISIBLE);
//        Log.e("ren", "one weekend,one month,on day,I will back");
//    }
//
//    private boolean addicon = false;
//
//    public void initAdsetting() {
//        Retrofit retrofit;
//        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
//        retrofit = new Retrofit.Builder().
//                client(client).
//                addConverterFactory(GsonConverterFactory.create()).baseUrl(AiyubaAdvApi.BASEURL).build();
//        Log.e("Tag--myadv", "广告自己的");
//        advApi = retrofit.create(AiyubaAdvApi.class);
//
//
////        bannerAdView.CloseBannerCarousel();
////        Log.e(TAG, "to setListener");
//
//
//    }
//
//    private void showGugeAd() {
//
//    }
//
//    private boolean CheckNetWork() {
//        if (NetWorkState.isConnectingToInternet() && NetWorkState.getAPNType() != 1) {
//
//            return true;
//        }
//        return false;
//
//    }
//
//    private String tagAd = "";
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
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.study);
//        EventBus.getDefault().register(this);
//        instance = this;
//        Intent intent = getIntent();
//        curVoaId = intent.getStringExtra("curVoaId");
//        // beginTimeStu = intent.getStringExtra("beginTime");
//        mContext = this;
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);//可以设置该Activity中音量控制键控制的音频流
//        CrashApplication.getInstance().addActivity(this);
//        // 初始化原文信息
//        voaOp = new VoaOp(mContext);
//        textDetailOp = new VoaDetailOp(mContext);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//        beginTime = df.format(new Date());
//        subtitleSum = VoaDataManager.Instace().subtitleSum; // 文章mp3，文章中英文
//        voaTemp = VoaDataManager.Instace().voaTemp;// 句子操作
//        textDetailTemp = VoaDataManager.Instace().voaDetailsTemp;// 句子list
//        voaId = voaTemp.voaId;
//        getCounts(textDetailTemp);
//        waittingDialog = WaittingDialog.showDialog(mContext);
//        // 判断是否连接网络
//        isConnected = NetWorkState.isConnectingToInternet();
//        // 是否让屏幕保持不暗不关闭的
//        if (SettingConfig.Instance().isLight()) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
//        // 是否获取是否播放时同步
//        if (!SettingConfig.Instance().isSyncho()) {
//            syncho = false;
//        } else {
//            syncho = true;
//        }
//        isvip = UserInfoManager.getInstance().isVip();
//        // 是否显示中文
//        try {
//            isShowChinese = ConfigManager.Instance().loadBoolean("showChinese");
//        } catch (Exception e) {
//            isShowChinese = true;
//        }
//        //
//        try {
//            mode = ConfigManager.Instance().loadInt("mode");
//        } catch (Exception e) {
//            mode = 1;
//        }
//        source = this.getIntent().getIntExtra("source", 0);
//        // 初始化viewpager
//        container = (ViewPager) findViewById(R.id.mainBody);
//        inflater = getLayoutInflater();
//        text = inflater.inflate(R.layout.text, null);
//        knowledge = inflater.inflate(R.layout.voa_knowledge, null);
//        exercise = inflater.inflate(R.layout.voa_exercise, null);
//        read = inflater.inflate(R.layout.read, null);
//        rank = inflater.inflate(R.layout.rank, null);
//        comment = inflater.inflate(R.layout.comment, null);
//        commentFooter = inflater.inflate(R.layout.comment_footer, null);
//
//
//        mList = new ArrayList<View>();
//        mList.add(text);
//        mList.add(read);
//        mList.add(rank);
//        mList.add(knowledge);
//        mList.add(exercise);
//        mList.add(comment);
//
//        // 初始化头部
//        Title = (TextView) findViewById(R.id.study_text);
//        backButton = (Button) findViewById(R.id.button_back);
//        favorButton = (Button) findViewById(R.id.favor);
//        shareButton = (Button) findViewById(R.id.share);
//        textButton = (TextView) findViewById(R.id.voa_button_text);
//        reReadButton = (TextView) findViewById(R.id.voa_button_reRead);
//        rankButton = (TextView) findViewById(R.id.voa_rank);
//        exerciseButton = (TextView) findViewById(R.id.voa_button_exercise);
//        knowledgeButton = (TextView) findViewById(R.id.voa_button_knowledge);
//        commentButton = (TextView) findViewById(R.id.voa_button_remark);
//        adView = findViewById(R.id.my_ad);
//        photoImage = (ImageView) findViewById(R.id.photoImage);
//        rl = (RelativeLayout) this.findViewById(R.id.adViewParent);
//        adView.setVisibility(View.GONE);
//        //上方的原文按钮
//        textButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConfigManager.Instance().putString("cur_tab", "text");
//                container.setCurrentItem(0);
//
//                if (valReadAdapter.videoView != null && valReadAdapter.videoView.isPlaying()) {
//                    valReadAdapter.videoView.pause();
//                    valReadAdapter.handler.removeMessages(0);
//                }
//
//                if (mixPlayer != null && mixPlayer.isPlaying()) {
//                    tv_read_mix.setText("试听");
//                    handler.removeMessages(6);
//                    mixPlayer.pause();
//                }
//            }
//        });
//
//        //上方评测按钮
//        reReadButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConfigManager.Instance().putString("cur_tab", "reRead");
//                container.setCurrentItem(1);
//                if (valReadAdapter != null) valReadAdapter.setClickPosition(0);
//            }
//        });
//
//        //排行榜按钮
//        rankButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConfigManager.Instance().putString("cur_tab", "rank");
////                rankHandler.removeCallbacksAndMessages(null);
////                rankHandler.sendEmptyMessage(0);
//                container.setCurrentItem(2);
//                if (valReadAdapter.videoView != null && valReadAdapter.videoView.isPlaying()) {
//                    valReadAdapter.videoView.pause();
//                    valReadAdapter.handler.removeMessages(0);
//                }
//                if (mixPlayer != null && mixPlayer.isPlaying()) {
//                    tv_read_mix.setText("试听");
//                    handler.removeMessages(6);
//                    mixPlayer.pause();
//                }
//            }
//        });
//        //知识按钮
//        knowledgeButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConfigManager.Instance().putString("cur_tab", "knowledge");
//                container.setCurrentItem(3);
//                if (valReadAdapter.videoView != null && valReadAdapter.videoView.isPlaying()) {
//                    valReadAdapter.videoView.pause();
//                    valReadAdapter.handler.removeMessages(0);
//                }
//                if (mixPlayer != null && mixPlayer.isPlaying()) {
//                    tv_read_mix.setText("试听");
//                    handler.removeMessages(6);
//                    mixPlayer.pause();
//                }
//            }
//        });
//        //练习按钮
//        exerciseButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConfigManager.Instance().putString("cur_tab", "exercise");
//                container.setCurrentItem(4);
//                if (valReadAdapter.videoView != null && valReadAdapter.videoView.isPlaying()) {
//                    valReadAdapter.videoView.pause();
//                    valReadAdapter.handler.removeMessages(0);
//                }
//
//                if (MultipleChoiceActivity.instance != null) {
//                    // TODO
//                    switch (curExerciseActivity) {
//                        case 0:
//                            MultipleChoiceActivity.instance.setMultiplechoice();
//                            break;
//                        case 1:
//                        case 2:
//                    }
//                }
//                if (mixPlayer != null && mixPlayer.isPlaying()) {
//                    tv_read_mix.setText("试听");
//                    handler.removeMessages(6);
//                    mixPlayer.pause();
//                }
//            }
//        });
//
//        //评论按钮
//        commentButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConfigManager.Instance().putString("cur_tab", "comment");
//                container.setCurrentItem(5);
//                if (valReadAdapter.videoView != null && valReadAdapter.videoView.isPlaying()) {
//                    valReadAdapter.videoView.pause();
//                    valReadAdapter.handler.removeMessages(0);
//                }
//                if (mixPlayer != null && mixPlayer.isPlaying()) {
//                    tv_read_mix.setText("试听");
//                    handler.removeMessages(6);
//                    mixPlayer.pause();
//                }
//            }
//        });
//
//        //返回按钮
//        backButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean flag = false;
//                if ("knowledge".equals(ConfigManager.Instance().loadString("cur_tab")) && curSelectActivity == 0) {
//                    flag = VoaWordActivity.instance.changeWordState();
//                }
//                if (!flag) {
//                    tofinish();
//
//                }
//            }
//        });
//        if (voaTemp.isCollect.equals("0")) {
//            favorButton.setBackgroundResource(R.drawable.nfavor);
//        } else {
//            favorButton.setBackgroundResource(R.drawable.favor);
//        }
//        favorButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (UserInfoManager.getInstance().isLogin()) {
//                    if (voaTemp.isCollect.equals("0")) {
//                        favorButton.setBackgroundResource(R.drawable.favor);
//
//                        voaTemp.isCollect = "1";
//                        voaOp.insertDataToCollection(voaTemp.voaId);
//                        voaOp.updateSynchro(voaTemp.voaId, 0);
//
//                        handler.sendEmptyMessage(1);
//                    } else {
//                        favorButton.setBackgroundResource(R.drawable.nfavor);
//
//                        voaTemp.isCollect = "0";
//                        voaOp.deleteDataInCollection(voaId);
//                        voaOp.updateSynchro(voaTemp.voaId, 0);
//
//                        handler.sendEmptyMessage(1);
//                    }
//                } else {
////                    Intent intent = new Intent();
////                    intent.setClass(mContext, Login.class);
////                    startActivity(intent);
//                    LoginUtil.startToLogin(mContext);
//                }
//            }
//        });
//        shareButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                if (!isConnected)
//                    CustomToast.showToast(mContext, R.string.category_check_network, 1000);
//                else {
//                    try {
//                        showShare();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("异常", e.toString());
//                    }
//                }
//            }
//        });
//
//        card = (WordCard) findViewById(R.id.word);
//        card.setWordCard(mContext);
//        card.setVisibility(View.GONE);
//        viewPagerAdapter = new ViewPagerAdapter(mList);
//        ConfigManager.Instance().putString("cur_tab", "text");
//
//
//        uid = String.valueOf(UserInfoManager.getInstance().getUserId());
//        initRank();
//
//
//        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
//        // values/strings.xml.
////        if (ConfigManager.Instance().loadInt("isvip") == 0) {
////            // Create an ad request. Check your logcat output for the hashed device ID to
////            // get test ads on a physical device. e.g.
////            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
////            AdRequest adRequest = new AdRequest.Builder()
////                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
////                    .build();
////
////            // Start loading the ad in the background.
////            mAdView.loadAd(adRequest);
////            mAdView.setAdListener(new AdListener() {
////                @Override
////                public void onAdLoaded() {
////                    super.onAdLoaded();
////                    mAdView.setVisibility(View.VISIBLE);
////                }
////            });
////        }
//    }
//
//    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//    private long bgtime;
//    private String showadtime = "2018-02-13 12:00:00";
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        isvip = UserInfoManager.getInstance().isVip();
//
//        exercise = BackgroundManager.Instace().bindService.getPlayer();
//        try {
//            PlaybackParams params = videoView.getPlaybackParams();
//            params.setSpeed(ConfigManager.Instance().loadFloat("playSpeed", 1.0f));
//            videoView.setPlaybackParams(params);
//        }catch (Exception e){
//
//        }
//
//        if (mp == null) {
//            mp = new MediaPlayer();
//        }
//
//
//        initText();
//
//        initRead();
//
//        initKnowledge();
//        initExercise();
//
//        long time = System.currentTimeMillis();
//        Log.e("Tag", "--bgtime--" + bgtime);
//        Date date = null;
//        try {
//            date = sdf.parse(showadtime);
//            long thetime = date.getTime();
//            Log.e("show-time", date.getTime() + "");
//            if (time > thetime) {
//                initAdsetting();
//                initaiyubaAd();
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//            initAdsetting();
//            initaiyubaAd();
//        }
//
////        tagAd = "ssp";
////        showJesgoo();
//        // showjesgoo();
//        //initaiyubaAd();
//        initExpression();
//        initComment();
//        controlVideo();
//        viewPagerAdapter.notifyDataSetChanged();
//        container.setAdapter(viewPagerAdapter);
//        container.setOffscreenPageLimit(1);
//        container.setOnPageChangeListener(new OnPageChangeListener() {
//            @Override
//            public void onPageSelected(int arg0) {
//                currentPage = container.getCurrentItem();
//                setBackGround(currentPage);
//                card.setVisibility(View.GONE);
//                /*if (currentPage == 1) {
//                    if (isSpeed)
//                        videoView.seekTo(0);
//                    else
//                        videoViewBP.seekTo(0);
//                    valReadAdapter.setClickPosition(0);
//                    valReadAdapter.notifyDataSetChanged();
//                } */
//
//                if (currentPage != 1) {
//                    if (valReadAdapter.videoView != null && valReadAdapter.videoView.isPlaying()) {
//                        valReadAdapter.videoView.pause();
//                        valReadAdapter.handler.removeMessages(0);
//                    }
//                }
//                lastPage = currentPage;
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//                /*if (currentPage == 1) {
//                    if (isSpeed)
//                        videoView.seekTo(0);
//                    else
//                        videoViewBP.seekTo(0);
//                }*/
//                if (arg0 == 4) {
//                    voice_handler.removeMessages(0);
//                    voice_handler.sendEmptyMessage(1);
//                    if (rManager != null) {
//                        rManager.stopRecord();
//                    }
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//            }
//        });
//        container.setCurrentItem(currentPage);
//
//        Title.setText(voaTemp.title);
//        setBackGround(currentPage);
//        // if(isConnected)
//        new initExtendedPlayer().execute();
////        playVideo(false);
////        handler.sendEmptyMessage(5);
////        setSeekbar();
////        setModeBackground();
////        setShowChineseButton();
////        setLockButton();
////        videoHandler.sendEmptyMessage(PROGRESS_CHANGED);
//        registerReceiver(rpl, new IntentFilter("toreply"));
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 这你写你的返回处理
////            if (isSpeed)
////                videoView.pause();
////            else
////                videoViewBP.pause();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    void showShare() {
//
//
////        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
////        weibo.removeAccount(true);
//        ShareSDK.removeCookieOnAuthorize(true);
//        ReadVoiceComment rvc = new ReadVoiceComment(voaTemp, true);
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
//        oks.setTitle(rvc.getShareTitle());
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(rvc.getArticleShareUrl());
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(rvc.getShareShortText());
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        // oks.setImagePath("/sdcard/test.jpg");
//        // imageUrl是Web图片路径，sina需要开通权限
//        oks.setImageUrl(rvc.getShareImageUrl());
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(rvc.getArticleShareUrl());
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("这款应用" + Constant.APPName + "真的很不错啊~推荐！");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(Constant.APPName);
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(rvc.getArticleShareUrl());
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
//                if (UserInfoManager.getInstance().getUserId() > 0) {
//                    Message msg = new Message();
//                    msg.obj = arg0.getName();
//                    if (arg0.getName().equals("QQ") || arg0.getName().equals("Wechat") || arg0.getName().equals("WechatFavorite")) {
//                        msg.what = 49;
//                    } else if (arg0.getName().equals("QZone") || arg0.getName().equals("WechatMoments") || arg0.getName().equals("SinaWeibo") || arg0.getName().equals("TencentWeibo")) {
//                        msg.what = 19;
//                    }
//                    handler.sendMessage(msg);
//                } else {
//                    handler.sendEmptyMessage(13);
//                }
//            }
//
//            @Override
//            public void onCancel(Platform arg0, int arg1) {
//                Log.e("okCallbackonCancel", "onCancel");
//            }
//        });
//        // 启动分享GUI
//        oks.show(this);
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        boolean flag = false;
//
//        if (event.getAction() != KeyEvent.ACTION_UP) {
//            if ("exercise".equals(ConfigManager.Instance().loadString("cur_tab"))) {
//                switch (curExerciseActivity) {
//                    case 1:
//                        flag = VoaStructureExerciseActivity.instance.dispatchKeyEvent(event);
//                        break;
//                    case 2:
//                        flag = VoaDiffcultyExerciseActivity.instance.dispatchKeyEvent(event);
//                        break;
//                }
//            } else if ("knowledge".equals(ConfigManager.Instance().loadString("cur_tab")) && curSelectActivity == 0) {
//                flag = VoaWordActivity.instance.changeWordState();
//            }
//
//            if (!flag) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//                    if (functionLayout.getVisibility() == View.VISIBLE) {
//                        functionLayout.setVisibility(View.GONE);
//                    } else {
//                        tofinish();
//                    }
//                }
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }
//
//    @Override
//    protected void onDestroy() {
//
//        EventBus.getDefault().unregister(this);
//        if (voiceMediaPlayer != null) {
//            voiceMediaPlayer.stop();
//            voiceMediaPlayer.release();
//            voiceMediaPlayer = null;
//        }
//        card.setWordPlayerStop(); //释放卡片页面上的播放器
//
//        valReadAdapter.setStopplay();
//        if (mixPlayer != null) {
//            mixPlayer.stopAndRelease();
//        }
//
//        Log.e("Tag--", "onDestroy");
//        if (!isPaused) {
//            new Thread(new UpdateStudyRecordunfinishThread()).start();
//        }
//        try {
//            unregisterReceiver(rpl);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Glide.with(getApplicationContext()).onDestroy();
//        if (commentAdapter != null) commentAdapter.stopVoices();
//        super.onDestroy();
//        handler.removeCallbacksAndMessages(null);
//        commentHandler.removeCallbacksAndMessages(null);
//        rankHandler.removeCallbacksAndMessages(null);
//        wordHandler.removeCallbacksAndMessages(null);
//        videoHandler.removeCallbacksAndMessages(null);
//        handlerText.removeCallbacksAndMessages(null);
//        voice_handler.removeCallbacksAndMessages(null);
//
//
//        File file = new File(Constant.getsimRecordAddr(this) + "mix" + ".mp3");
//        if (file != null && file.exists() && file.isFile()) {
//            file.delete();
//        }
//    }
//
//    public static StudyActivity newInstance() {
//        return instance;
//    }
//
//    private void playVideo(boolean finish) {
//        String url = null;
//        if (isvip) {
//            url = Constant.sound_vip + voaTemp.voaId / 1000 + "_" + voaTemp.voaId % 1000 + Constant.append;
//        } else {
//            url = Constant.sound + voaTemp.voaId / 1000 + "_" + voaTemp.voaId % 1000 + Constant.append;
//        }
//
//        int netType = NetWorkState.getAPNType();
//
//        // 原文音频的存放路径
//        String pathString = Constant.videoAddr + voaTemp.voaId + Constant.append;
//        String pathString2 = ConfigManager.Instance().loadString("media_saving_path") + File.separator + voaTemp.voaId + Constant.append;
//
//        File fileTemp = new File(pathString2);
//        if (!fileTemp.exists()) {
//            fileTemp = new File(pathString);
//        } else {
//            pathString = pathString2;
//        }
//
//        if (BackgroundManager.Instace().bindService.getTag() == voaId) {
//            if (fileTemp.exists()) {
//                seekBar.setSecondaryProgress(seekBar.getMax());
//                try {
//                    mp.reset();
//                    mp.setDataSource(pathString);
//                    mp.prepare();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    resetMediaPlayer(pathString, true);
//                }
//                if (finish) {
//////                    videoView.setVideoPath(pathString);
////                    videoView.initialize(pathString);
////                    videoView.prepareAndPlay();
//
//                    try {
//                        videoView.setDataSource(pathString);
//                        videoView.prepareAsync();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                }
//            } else {
//                if (!isConnected) {
//                    Log.e("网络连接", isConnected + "");
//                    return;
//                }
//
//                try {
//                    mp.reset();
//                    mp.setDataSource(url);
//                    mp.prepareAsync();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    resetMediaPlayer(url, false);
//                }
//                if (finish || exoPlayer.getCurrentPosition() == 0) {
//////                      videoView.setVideoPath(url);
////                    videoView.initialize(url);
////                    videoView.prepareAndPlay();
//
//                    try {
//                        videoView.setDataSource(this, Uri.parse(url));
//                        videoView.prepareAsync();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        } else {
//            BackgroundManager.Instace().bindService.setTag(voaId);
//            if (fileTemp.exists()) {
//                seekBar.setSecondaryProgress(seekBar.getMax());
//////                videoView.setVideoPath(pathString);
////                videoView.initialize(pathString);
////                videoView.prepareAndPlay();
//                try {
//                    videoView.setDataSource(pathString);
//                    videoView.prepareAsync();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                try {
//                    mp.reset();
//                    mp.setDataSource(pathString);
//                    mp.prepare();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    resetMediaPlayer(pathString, true);
//                }
//
//            } else if (netType == 0) {
//                if (finish) {
//                    CustomToast.showToast(mContext, R.string.study_info4, 1000);
//                    source = 1;
//                    voaId = voaOp.findDataByBook(1).get(0).voaId;
//                    VoaDataManager.Instace().voaDetailsTemp = textDetailOp.findDataByVoaId(voaId);
//                    setVoaData();
//                } else {
//
//                    handler.sendEmptyMessage(5);
//                    try {
//                        videoView.setDataSource(this, Uri.parse(url));
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                    setVoaData();
//                }
//            } else if (netType == 1) {
//                CustomToast.showToast(mContext, R.string.study_info2, 2000);
////                videoView.initialize(url);
////                videoView.prepareAndPlay();
//                try {
//                    videoView.setDataSource(this, Uri.parse(url));
//                    videoView.prepareAsync();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//
//                seekBar.setSecondaryProgress(0);
//                try {
//                    mp.reset();
//                    mp.setDataSource(url);
//                    mp.prepareAsync();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    resetMediaPlayer(url, false);
//                }
//            } else if (netType == 2) {
////                videoView.initialize(url);
////                videoView.prepareAndPlay();
//                try {
//                    videoView.setDataSource(this, Uri.parse(url));
//                    videoView.prepareAsync();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                seekBar.setSecondaryProgress(0);
//
//                try {
//                    mp.reset();
//                    mp.setDataSource(url);
//                    mp.prepareAsync();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    resetMediaPlayer(url, false);
//                }
//            }
//        }
//    }
//
//    // 初始化原文
//    private void initText() {
//        // 初始化 控制信息
//        textControlLayout = (LinearLayout) text.findViewById(R.id.text_control);
//        totalTime = (TextView) text.findViewById(R.id.total_time);
//        curTime = (TextView) text.findViewById(R.id.cur_time);
//        textCenter = (SubtitleSynView) text.findViewById(R.id.text_center);
//        textCenter.setSubtitleSum(subtitleSum);
//        textCenter.setTpstcb(tp);
//
//        videoFormer = (RelativeLayout) text.findViewById(R.id.former_button);
//        videoNext = (RelativeLayout) text.findViewById(R.id.next_button);
//
//        re_CHN = (RelativeLayout) text.findViewById(R.id.re_CHN);
//
//        videoFormer.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currParagraph != 0 && currParagraph != 1) {
//                    // 将videoView移动到指定的时间
//
//                    videoView.seekTo((int) VoaDataManager.Instace().voaDetailsTemp.get(currParagraph - 2).startTime * 1000);
//                    textCenter.unsnyParagraph();
//                } else {
//                    CustomToast.showToast(mContext, R.string.study_first, 2000);
//                }
//            }
//        });
//
//        videoNext.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currParagraph < VoaDataManager.Instace().voaDetailsTemp.size()) {
//
//                    videoView.seekTo((int) (VoaDataManager.Instace().voaDetailsTemp.get(currParagraph).startTime * 1000));
//                    textCenter.unsnyParagraph();
//                } else {
//                    CustomToast.showToast(mContext, R.string.study_last, 2000);
//                }
//            }
//        });
//
//        lightBar = (SeekBar) text.findViewById(R.id.light_bar);
//        lightBar.setProgress(UtilLightnessControl.GetLightness(StudyActivity.this));
//
//
//        lightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                UtilLightnessControl.setActivityBrightness(progress, StudyActivity.this);
//            }
//        });
//
//        seekBar = (SeekBar) text.findViewById(R.id.seek_bar);
//        seekBar.getParent().requestDisallowInterceptTouchEvent(true);
//        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
//                if (fromUser) {
//
//                    videoView.seekTo(progress);
//                    currParagraph = subtitleSum.getParagraph(videoView.getCurrentPosition() / 1000.0);
//                    lastParegraph = currParagraph - 1;
//
//                    if (currParagraph != 0) {
//                        textCenter.snyParagraph(currParagraph);
//                    }
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar arg0) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//
//        abPlay = (RelativeLayout) text.findViewById(R.id.abplay);// A-B循环播放
//        abPlay.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // abState最开始是0
//                abState++;
//                if (abState % 3 == 1) {
//                    aPositon = videoView.getCurrentPosition();
//
//                    CustomToast.showToast(mContext, R.string.study_ab_a, 2000);
//                } else if (abState % 3 == 2) {
//                    bPosition = videoView.getCurrentPosition();
//
//                    handlerText.sendEmptyMessage(1);// 开始A-B循环
//                    CustomToast.showToast(mContext, R.string.study_ab_b, 1000);
//                } else if (abState % 3 == 0) {
//                    handlerText.sendEmptyMessage(0);// 手动停止
//                }
//            }
//        });
//
//        showChineseButton = (ImageView) text.findViewById(R.id.CHN);
//
//        setShowChineseButton();
//        re_CHN.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                isShowChinese = !isShowChinese;
//                ConfigManager.Instance().putBoolean("showChinese", isShowChinese);
//
//                setShowChineseButton();
//            }
//        });
//
//        pause = (ImageButton) text.findViewById(R.id.video_play);
//
//        if ((videoView != null && !videoView.isPlaying())) {
//            isPaused = true;
//
//        }
//
//
//        pause.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                isPaused = !isPaused;
//                if (isPaused) {
//                    //暂停播放
//                    new Thread(new UpdateStudyRecordunfinishThread()).start();
//                    pause.setBackgroundResource(R.drawable.image_play);
//                    try {
//                        videoView.pause();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    //开始播放重置beginTime
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//                    beginTime = df.format(new Date());
//                    pause.setBackgroundResource(R.drawable.image_pause);
//                    videoView.start();
//                }
//            }
//        });
//
//        re_one_video = (RelativeLayout) text.findViewById(R.id.re_one_video);
//        modeButton = (ImageView) text.findViewById(R.id.one_video);
//        re_one_video.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                mode = (mode + 1) % 3;
//                ConfigManager.Instance().putInt("mode", mode);
//                setModeBackground();
//
//                if (mode == 0) {
//                    CustomToast.showToast(mContext, R.string.study_repeatone, 1000);
//                } else if (mode == 1) {
//                    CustomToast.showToast(mContext, R.string.study_follow, 1000);
//                } else if (mode == 2) {
//                    CustomToast.showToast(mContext, R.string.study_random, 1000);
//                }
//            }
//        });
//
//        functionLayout = (LinearLayout) text.findViewById(R.id.more_function_layout);
//        functionButton = (RelativeLayout) text.findViewById(R.id.function_button);
//        functionButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                if (functionLayout.getVisibility() == View.VISIBLE) {
//                    functionLayout.setVisibility(View.GONE);
//                } else {
//                    functionLayout.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//
//        OnClickListener onClickListener = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                float speed = 0.0f;
//                switch (v.getId()) {
//                    case R.id.text_play_speed_05:
//                        speed = 0.5f;
//                        changeSpeed(speed, (TextView) v);
//                        break;
//                    case R.id.text_play_speed_10:
//                        speed = 1.0f;
//                        changeSpeed(speed, (TextView) v);
//                        break;
//                    case R.id.text_play_speed_125:
//                        speed = 1.25f;
//                        changeSpeed(speed, (TextView) v);
//                        break;
//                    case R.id.text_play_speed_15:
//                        speed = 1.5f;
//                        changeSpeed(speed, (TextView) v);
//
//                        break;
//                    case R.id.text_play_speed_20:
//                        speed = 2.0f;
//                        changeSpeed(speed, (TextView) v);
//                        break;
//                }
//
//
//            }
//        };
//        textPlaySpeed_05 = (TextView) text.findViewById(R.id.text_play_speed_05);
//        textPlaySpeed_10 = (TextView) text.findViewById(R.id.text_play_speed_10);
//        textPlaySpeed_125 = (TextView) text.findViewById(R.id.text_play_speed_125);
//        textPlaySpeed_15 = (TextView) text.findViewById(R.id.text_play_speed_15);
//        textPlaySpeed_20 = (TextView) text.findViewById(R.id.text_play_speed_20);
//
//
//        textPlaySpeed_05.setOnClickListener(onClickListener);
//        textPlaySpeed_10.setOnClickListener(onClickListener);
//        textPlaySpeed_125.setOnClickListener(onClickListener);
//        textPlaySpeed_15.setOnClickListener(onClickListener);
//        textPlaySpeed_20.setOnClickListener(onClickListener);
//
//
//        if (UserInfoManager.getInstance().isVip()) {
//
//            float currentSpeed = ConfigManager.Instance().loadFloat("playSpeed", 1.0f);
//            switch (String.valueOf(currentSpeed)) {
//
//                case "0.5":
//                    changeSpeed(currentSpeed, textPlaySpeed_05);
//                    break;
//                case "1.0":
//                    changeSpeed(currentSpeed, textPlaySpeed_10);
//                    break;
//                case "1.25":
//                    changeSpeed(currentSpeed, textPlaySpeed_125);
//                    break;
//                case "1.5":
//                    changeSpeed(currentSpeed, textPlaySpeed_15);
//                    break;
//                case "2.0":
//                    changeSpeed(currentSpeed, textPlaySpeed_20);
//                    break;
//
//
//            }
//        }
//
//        /*textPlaySpeed.setText(buildSpeedString(ConfigManager.Instance().loadFloat("playSpeed", 1.0f)));
//        textPlaySpeed.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ConfigManager.Instance().loadInt("isvip") >= 1) {
//                    if (isSpeed) {
//                        float currentSpeed = ConfigManager.Instance().loadFloat("playSpeed", 1.0f);
//                        currentSpeed = stepIncrease(currentSpeed);
//                        videoView.setPlaySpeed(currentSpeed);
//                        textPlaySpeed.setText(buildSpeedString(currentSpeed));
//                        ConfigManager.Instance().putFloat("playSpeed", currentSpeed);
//                    } else {
//                        Toast.makeText(mContext, "请在设置中切换为调速播放器！", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(mContext, "成为VIP用户即可调节播放速度！", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });*/
//    }
//
//    private void changeSpeed(float speed, TextView textView) {
//        if (UserInfoManager.getInstance().isVip()) {
//            setDefalt();
//            float currentSpeed = speed;
//            try {
//                PlaybackParams params = videoView.getPlaybackParams();
//                params.setSpeed(currentSpeed);
//                videoView.setPlaybackParams(params);
//            }catch (Exception e){
//
//            }
//            ConfigManager.Instance().putFloat("playSpeed", currentSpeed);
//            textView.setBackgroundResource(R.drawable.background_speed_on);
//            textView.setTextColor(Color.parseColor("#ffffff"));
//
//        } else {
//            Toast.makeText(mContext, "成为VIP用户即可调节播放速度！", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void setDefalt() {
//        textPlaySpeed_05.setBackgroundResource(R.drawable.bacground_speed_off);
//
//        textPlaySpeed_10.setBackgroundResource(R.drawable.bacground_speed_off);
//        textPlaySpeed_125.setBackgroundResource(R.drawable.bacground_speed_off);
//        textPlaySpeed_15.setBackgroundResource(R.drawable.bacground_speed_off);
//        textPlaySpeed_20.setBackgroundResource(R.drawable.bacground_speed_off);
//
//        textPlaySpeed_05.setTextColor(Color.parseColor("#F6B476"));
//        textPlaySpeed_10.setTextColor(Color.parseColor("#F6B476"));
//        textPlaySpeed_125.setTextColor(Color.parseColor("#F6B476"));
//        textPlaySpeed_15.setTextColor(Color.parseColor("#F6B476"));
//        textPlaySpeed_20.setTextColor(Color.parseColor("#F6B476"));
//
//    }
//
//
//    // 初始化知识
//    public void initKnowledge() {
//        voaWord = (ImageView) knowledge.findViewById(R.id.voa_words);
//        voaAnno = (ImageView) knowledge.findViewById(R.id.voa_annotations);
//        voaImport = (ImageView) knowledge.findViewById(R.id.voa_important_sentences);
//        voaDiffcult = (ImageView) knowledge.findViewById(R.id.voa_diffculty);
//
//        voa_words_ln = (LinearLayout) knowledge.findViewById(R.id.voa_words_ln);
//        voa_annotations_ln = (LinearLayout) knowledge.findViewById(R.id.voa_annotations_ln);
//        voa_important_sentences_ln = (LinearLayout) knowledge.findViewById(R.id.voa_important_sentences_ln);
//        voa_diffculty_ln = (LinearLayout) knowledge.findViewById(R.id.voa_diffculty_ln);
//
//
//        OnClickListener ocl = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v == voa_words_ln) {
//                    if (curSelectActivity != 0) {
//                        curSelectActivity = 0;
//                    } else {
//                        Intent intent = new Intent("presstorefresh");
//                        sendBroadcast(intent);
//                        return;
//                    }
//                } else if (v == voa_annotations_ln) {
//                    if (curSelectActivity != 1) {
//                        curSelectActivity = 1;
//                    } else {
//                        return;
//                    }
//                } else if (v == voa_important_sentences_ln) {
//                    if (curSelectActivity != 2) {
//                        curSelectActivity = 2;
//                    } else {
//                        return;
//                    }
//
//                } else if (v == voa_diffculty_ln) {
//                    if (curSelectActivity != 3) {
//                        curSelectActivity = 3;
//                    } else {
//                        return;
//                    }
//                }
//
//                clickTab();
//            }
//        };
//
//        voa_words_ln.setOnClickListener(ocl);
//        voa_annotations_ln.setOnClickListener(ocl);
//        voa_important_sentences_ln.setOnClickListener(ocl);
//        voa_diffculty_ln.setOnClickListener(ocl);
//        knowledgeContainer = (ViewFlipper) knowledge.findViewById(R.id.knowledgeBody);
//        knowledgeContainer.setAnimateFirstView(true);
//
//        clickTab();
//    }
//
//    private void clickTab() {
//        voaWord.setImageResource(R.drawable.voa_words_normal_new);
//        voaAnno.setImageResource(R.drawable.voa_annotations_normal_new);
//        voaImport.setImageResource(R.drawable.voa_improtant_sentences_normal_new);
//        voaDiffcult.setImageResource(R.drawable.voa_diffcult_normal_new);
//
//        switch (curSelectActivity) {
//            case 0:
//                setActivity(VoaWordActivity.class, curVoaId);
//                voaWord.setImageResource(R.drawable.voa_words_press_new);
//                break;
//            case 1:
//                setActivity(VoaAnnotationActivity.class, curVoaId);
//                voaAnno.setImageResource(R.drawable.voa_annotations_press_new);
//                break;
//            case 2:
//                setActivity(VoaStructureActivity.class, curVoaId);
//                voaImport.setImageResource(R.drawable.voa_improtent_sentences_press_new);
//                break;
//            case 3:
//                setActivity(VoaDiffcultyActivity.class, curVoaId);
//                voaDiffcult.setImageResource(R.drawable.voa_diffcult_press_new);
//                break;
//        }
//    }
//
//    public void setActivity(Class<?> cls, String voaId) {
//        Intent intent = new Intent();
//        intent.putExtra("curVoaId", curVoaId);
//        intent.setClass(mContext, cls);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        final Window window = getLocalActivityManager().startActivity(String.valueOf(0), intent);
//        final View view = window != null ? window.getDecorView() : null;
//        if (view != null) {
//            knowledgeContainer.removeAllViews();
//            knowledgeContainer.addView(view);
//            view.setFocusable(true);
//            knowledgeContainer.showNext();
//        }
//    }
//
//    private void initRank() {
//        today = (TextView) rank.findViewById(R.id.rank_today);
//        week = (TextView) rank.findViewById(R.id.rank_week);
//        month = (TextView) rank.findViewById(R.id.rank_month);
//        note = (TextView) rank.findViewById(R.id.rank_note);
//        userImageText = (TextView) rank.findViewById(R.id.rank_user_image_text);
//        userName = (TextView) rank.findViewById(R.id.rank_user_name);
//        myUsername = (TextView) rank.findViewById(R.id.username);
//        userImage = (CircleImageView) rank.findViewById(R.id.rank_user_image);
//        userInfo = (TextView) rank.findViewById(R.id.rank_info);
//
//        swipeRefreshLayout = (SmartRefreshLayout) rank.findViewById(R.id.swipe_refresh_widget);
//        myImage = (CircleImageView) rank.findViewById(R.id.my_image);
//
//        fail_rela_rank = (RelativeLayout) rank.findViewById(R.id.fail_rela_rank);
//        btn_rank_refresh_rank = (Button) rank.findViewById(R.id.btn_rank_refresh_rank);
//
//        rankListView = (RecyclerView) rank.findViewById(R.id.rank_list);
//        rankListView.setLayoutManager(new LinearLayoutManager(mContext));
//        rankListView.addItemDecoration(new DividerItemDecoration(mContext, 1));
//
//        //刷新
//        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//
//                if (!NetStatusUtil.isConnected(mContext)) {
//                    refreshlayout.finishRefresh();
//                    return;
//                }
//                startRank = "0";
//                isLastPageRank = false;
//                rankUsersList.clear();
//                rankHandler.sendEmptyMessage(0);
//                refreshlayout.finishRefresh();
//            }
//        });
//        //加载更多
//        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//
//                if (!NetStatusUtil.isConnected(mContext)) {
//                    refreshlayout.finishLoadMore();
//                    return;
//                }
//                if (isLastPageRank) {
//                    ToastUtil.showToast(mContext, "已加载全部数据");
//                } else {
//                    rankHandler.sendEmptyMessage(0);
//                }
//                refreshlayout.finishLoadMore();
//            }
//        });
//
//
//        btn_rank_refresh_rank.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                type = "D";
//                total = "20";
//                startRank = "0";
//                rankHandler.sendEmptyMessage(0);
//            }
//        });
//
//
//        cpReadWords = (TextView) rank.findViewById(R.id.champion_read_words);
//        today.setSelected(true);
//        today.setTextColor(0xffffffff);
//        type = "D";
//        total = "20";
//        startRank = "0";
//        rankHandler.sendEmptyMessage(0);
//        myImage.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent();
//                intent.putExtra("uid", String.valueOf(UserInfoManager.getInstance().getUserId()));
//                intent.putExtra("voaId", curVoaId);
//                intent.putExtra("userName", UserInfoManager.getInstance().getUserName());
//                intent.putExtra("userPic", String.valueOf(UserInfoManager.getInstance().getJiFen()));
//                intent.setClass(mContext, CommentActivity.class);
//                startActivity(intent);
//
//            }
//        });
//
//
//        today.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!today.isSelected()) {
//                    rankUsers.clear();
//                    today.setSelected(true);
//                    today.setTextColor(0xffffffff);
//                    week.setSelected(false);
//                    week.setTextColor(0xffFFB151);
//                    month.setSelected(false);
//                    month.setTextColor(0xffFFB151);
//                    type = "D";
//                    note.setText("今日数据本日24:00清零");
//                    rankHandler.sendEmptyMessage(0);
//                }
//            }
//        });
//
//        week.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!week.isSelected()) {
//                    rankUsers.clear();
//
//                    today.setSelected(false);
//                    today.setTextColor(0xffFFB151);
//
//                    week.setSelected(true);
//                    week.setTextColor(0xffffffff);
//
//                    month.setSelected(false);
//                    month.setTextColor(0xffFFB151);
//
//                    type = "W";
//
//                    note.setText("本周数据周日24:00清零");
//
//                    rankHandler.sendEmptyMessage(0);
//                }
//            }
//        });
//
//        month.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!month.isSelected()) {
//                    rankUsers.clear();
//
//                    today.setSelected(false);
//                    today.setTextColor(0xffFFB151);
//
//                    week.setSelected(false);
//                    week.setTextColor(0xffFFB151);
//
//                    month.setSelected(true);
//                    month.setTextColor(0xffffffff);
//
//                    type = "M";
//
//                    note.setText("本月数据月末24:00清零");
//                    rankHandler.sendEmptyMessage(0);
//                }
//            }
//        });
//
//
//    }
//
//    // 初始化跟读
//    private void initRead() {
//        // 初始化控制信息--评测
//        tv_read_sore = (TextView) read.findViewById(R.id.tv_read_sore);
//        tv_read_share = (TextView) read.findViewById(R.id.tv_read_share);
//        tv_read_mix = (TextView) read.findViewById(R.id.tv_read_mix);
//
//
//        imv_current_time = (TextView) read.findViewById(R.id.imv_current_time);
//
//        imv_seekbar_player = (SeekBar) read.findViewById(R.id.imv_seekbar_player);
////        imv_seekbar_player.setEnabled(false);
//
//        imv_total_time = (TextView) read.findViewById(R.id.imv_total_time);
//
//        imv_seekbar_player.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                if (fromUser) {
//                    mixPlayer.seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        //合成语音
//        tv_read_mix.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                valReadAdapter.stopEval();
//                valReadAdapter.stopPlay();
//
//                if (mixPlayer == null) {
//                    mixPlayer = new Player(mContext, new OnPlayStateChangedListener() {
//                        @Override
//                        public void playCompletion() {
//                            tv_read_mix.setText("试听");
//                        }
//
//                        @Override
//                        public void playFaild() {
//
//                        }
//
//                        @Override
//                        public void playSuccess() {
//
//                        }
//                    });
//
//                }
//                if (tv_read_mix.getText().toString().equals("合成")) {
//
//                    if (list_pcm_file_all.size() <= 1) {
//                        ToastUtil.showToast(mContext, "至少读两句方可合成！");
//                        return;
//                    }
//
//                    pmc_file_list.clear();
//                    totalScore = 0;
//                    mp3TotalTime = 0l;
//                    String soundUrls = "";
//
//                    for (int i = 0; i < list_pcm_file_all.size(); i++) {
//                        StudyPcmFile studyPcmFile = list_pcm_file_all.get(i);
//                        soundUrls = soundUrls + studyPcmFile.getFilePath() + ",";
//                        pmc_file_list.add(new File(studyPcmFile.getFilePath()));
//                        totalScore += studyPcmFile.getScore();
//                        mp3TotalTime += studyPcmFile.getTotalTime();
//                    }
//
//
//                    audioCompose(soundUrls);
//
//
//                    //合成录音
//              /*      File pcmFile = new File(Constant.getsimRecordAddr() + "mix" + ".mp3");
//                    try {
//                        MergeHelper.merge(pcmFile, pmc_file_list);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }*/
//
//
//                } else if (tv_read_mix.getText().toString().equals("试听")) {
//
//                    tv_read_mix.setText("暂停");
//                    EventBus.getDefault().post(new StopTextPlayerEvent());
//                    mp3changTime = 100;
//                    playRecord2();
//                    handler.sendEmptyMessageDelayed(6, 100);
//                } else if ("暂停".equals(tv_read_mix.getText().toString())) {
//                    tv_read_mix.setText("试听");
//                    handler.removeMessages(6);
//                    mixPlayer.pause();
//                }
//            }
//        });
//        tv_read_share.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (tv_read_share.getText().equals("发布")) {
//                    if (!isMix) {
//                        ToastUtil.showToast(mContext, "请先合成后再发布");
//                        return;
//                    }
////                    sendSound();
//                    sendsong();
//                    tv_read_share.setText("分享");
//                } else {
//                    showShareSound();
//                }
//            }
//        });
//        senListView = (ListView) read.findViewById(R.id.sen_list);
//        valReadAdapter = new ValReadAdapter(mContext, textDetailTemp, voaTemp, mp, this);
//        senListView.setAdapter(valReadAdapter);
//        try {
//            valReadAdapter.setClickPosition(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        senListView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                // flag = 1;
//                valReadAdapter.setClickPosition(position);
//                valReadAdapter.notifyDataSetChanged();
//                if (valReadAdapter.videoView.isPlaying()) {
//                    valReadAdapter.videoView.pause();
//                }
//            }
//        });
//    }
//
//
//    public void playRecord2() {
//        if (mixPlayer != null) {
//            if (mixPlayer.isIdle()) {
//                mixPlayer.initialize("http://voa."+Constant.IYUBA_CN+"voa/" + mixUrl);
//                mixPlayer.prepareAndPlay();
//            } else if (mixPlayer.isCompleted()) {
//                mixPlayer.start();
//            } else if (mixPlayer.isInitialized()) {
//                mixPlayer.prepareAndPlay();
//            } else if (mixPlayer.isPausing()) {
//                mixPlayer.start();
//            }
//        }
//    }
//
//    // 以下评论
//    private BroadcastReceiver rpl = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO 自动生成的方法存根
//            if (currMode == 1) {
//                switchToText();
//                expressEditText.findFocus();
//                expressEditText.requestFocus();
//                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(expressEditText, 0);
//                expressEditText.setText(getResources().getString(R.string.reply) + intent.getExtras().getString("username") + ":");
//                expressEditText.setSelection(expressEditText.length());
//            } else if (currMode == 0) {
//                switchToVoice();
//                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//
//            }
//        }
//    };
//
//
//    private void initComment() {
//        if (voiceMediaPlayer == null) {
//            voiceMediaPlayer = new MediaPlayer();
//        }
//
//        voiceMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        voiceMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                voiceMediaPlayer.start();
//            }
//        });
//
//        contextMenu = (ContextMenu) findViewById(R.id.context_menu);
//        setModeButton = (ImageButton) comment.findViewById(R.id.setmode);
//        pressSpeakButton = (Button) comment.findViewById(R.id.press_speak);
//        testListenButton = (Button) comment.findViewById(R.id.test_listen);
//        voiceView = comment.findViewById(R.id.voice_view);
//        pressSpeakButton.setOnTouchListener(voice_otl);
//        setModeButton.setOnClickListener(voice_ocl);
//        testListenButton.setOnClickListener(voice_ocl);
//        voiceValue = (ImageView) comment.findViewById(R.id.mic_value);
//        commentAdapter = new CommentListAdapterNew(mContext, /*comments,*/ 0);
//        comments.removeAll(comments);
//
//
//        swiperefresh_comment = (SmartRefreshLayout) comment.findViewById(R.id.swiperefresh_comment);
//        listComment = (RecyclerView) comment.findViewById(R.id.list_comment);
//        listComment.setLayoutManager(new LinearLayoutManager(mContext));
//        listComment.setAdapter(commentAdapter);
//
//        //刷新
//        swiperefresh_comment.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//
//                if (!NetStatusUtil.isConnected(mContext)) {
//                    refreshlayout.finishRefresh();
//                    return;
//                }
//                commentHandler.sendEmptyMessage(4);
//                refreshlayout.finishRefresh();
//            }
//        });
//        //加载更多
//        swiperefresh_comment.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//
//                if (!NetStatusUtil.isConnected(mContext)) {
//                    refreshlayout.finishLoadMore();
//                    return;
//                }
//                if (comments != null && comments.size() != 0 && !commentAll) {
//                    if (!isDownloadAll) {
//                        commentHandler.sendEmptyMessage(0);
//                    } else {
//                        ToastUtil.showToast(mContext, "已加载全部数据");
//                    }
//                }
//                refreshlayout.finishLoadMore();
//            }
//        });
//        listComment.setAdapter(commentAdapter);
//        commentLoadMoreTextView = (TextView) commentFooter.findViewById(R.id.comment_loadmore_text);
//        commentHandler.sendEmptyMessage(3);
//
//        fail_rela_comment = (RelativeLayout) comment.findViewById(R.id.fail_rela_comment);
//        btn_rank_refresh_comment = (Button) comment.findViewById(R.id.btn_rank_refresh_comment);
//        btn_rank_refresh_comment.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                commentHandler.sendEmptyMessage(3);
//            }
//        });
//    }
//
//    private OnClickListener voice_ocl = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.setmode:
//                    if (currMode == 1) {
//                        comment.findViewById(R.id.voicebutton).setVisibility(View.GONE);
//                        comment.findViewById(R.id.edittext).setVisibility(View.VISIBLE);
//                        currMode = 0;
//                        setModeButton.setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
//                    } else {
//                        comment.findViewById(R.id.voicebutton).setVisibility(View.VISIBLE);
//                        comment.findViewById(R.id.edittext).setVisibility(View.GONE);
//                        currMode = 1;
//                        setModeButton.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
//                    }
//                    break;
//                case R.id.test_listen:
//                    if (voiceMediaPlayer.isPlaying()) {
//                        voiceMediaPlayer.stop();
//                    }
//                    voiceMediaPlayer.reset();
//                    try {
//                        voiceMediaPlayer.setDataSource(Constant.voiceCommentAddr);
//                        voiceMediaPlayer.prepareAsync();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private OnTouchListener voice_otl = new OnTouchListener() {
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            if (v.getId() == R.id.press_speak) {
//
//                StudyActivityPermissionsDispatcher.requestEvaluateWithPermissionCheck(StudyActivity.this);
//                if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO})) {
//
//                    return true;
//                }
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    voice_handler.sendEmptyMessageDelayed(0, 300);
//                    // setTextPause(true);
//                    // 录音
//                    try {
//                        File file = new File(Constant.voiceCommentAddr);
//                        rManager = new RecordManager(file, voiceValue);
//                        rManager.startRecord();
//                    } catch (Exception e) {
//                        Log.e("onTouch", e.getMessage());
//                    }
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    // comment.findViewById(R.id.voice_view).setVisibility(
//                    // View.VISIBLE);
//                    // comment.findViewById(R.id.voice_view)
//                    // setTextPause(false);
//                    voice_handler.sendEmptyMessageDelayed(1, 300);
//                    rManager.stopRecord();
//                }
//            } else {
//                // comment.findViewById(R.id.voice_view)
//                voiceView.setVisibility(View.VISIBLE);
//                // comment.findViewById(R.id.voice_view)
//                voiceView.setVisibility(View.GONE);
//                testListenButton.setVisibility(View.VISIBLE);
//                rManager.stopRecord();
//            }
//
//            return true;
//        }
//    };
//
//    // 评论切换到文字输入
//    private void switchToText() {
//        findViewById(R.id.voicebutton).setVisibility(View.GONE);
//        findViewById(R.id.edittext).setVisibility(View.VISIBLE);
//        setModeButton.setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
//        currMode = 0;
//    }
//
//    // 评论切换到语音输入
//    private void switchToVoice() {
//        findViewById(R.id.voicebutton).setVisibility(View.VISIBLE);
//        findViewById(R.id.edittext).setVisibility(View.GONE);
//        setModeButton.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
//        currMode = 1;
//    }
//
//    @Override
//    public void setDefault() {
//        tv_read_mix.setText("合成");
//    }
//
//    @Override
//    public void getIndex(int position, int score, long adpterTime, int itemId, String filepath) {
//
//
//        StudyPcmFile studyPcmFile = new StudyPcmFile();
//        studyPcmFile.setCurrIndex(position);
//        studyPcmFile.setFilePath(filepath);
//        studyPcmFile.setScore(score);
//        studyPcmFile.setTotalTime(adpterTime);
//        studyPcmFile.setItemId(itemId);
//
//        boolean addFlag = true;
//
//        if (list_pcm_file_all.size() > 0) {
//            for (int i = 0; i < list_pcm_file_all.size(); i++) {
//                if (studyPcmFile.getItemId() == list_pcm_file_all.get(i).getItemId()) {
//                    list_pcm_file_all.remove(i);
//                    list_pcm_file_all.add(i, studyPcmFile);
//                    addFlag = false;
//                }
//            }
//            if (addFlag) {
//                list_pcm_file_all.add(studyPcmFile);
//            }
//        } else {
//            list_pcm_file_all.add(studyPcmFile);
//        }
//
//        Collections.sort(list_pcm_file_all, new Comparator<StudyPcmFile>() {
//            @Override
//            public int compare(StudyPcmFile o1, StudyPcmFile o2) {
//                int i = o1.getCurrIndex() - o2.getCurrIndex();
//                return i;
//            }
//        });
//
//
//    }
//
//    @Override
//    public void onItemClick(int index) {
//        //评论item点击事件
//        final boolean login = UserInfoManager.getInstance().isLogin();
//        if (login) {
//            final int position = index;
//            contextMenu.setText(mContext.getResources().getStringArray(R.array.context_menu_comment));
//            contextMenu.setCallback(new ResultIntCallBack() {
//                @Override
//                public void setResult(int result) {
//                    switch (result) {
//                        case 0:
//                            Intent intent = new Intent("toreply");
//                            if (comments != null && comments.size() > 0 && position < comments.size() && null != comments.get(position)) {
//                                intent.putExtra("username", ("null".equals(comments.get(position).username) ? comments.get(position).userId : comments.get(position).username));
//                                mContext.sendBroadcast(intent);
//                            }
//                            break;
//                        case 1:
//                            if (login) {
//                                if (comments != null && comments.size() > 0 && position < comments.size() && null != comments.get(position)) {
//                                    intent = new Intent();
//                                    SocialDataManager.Instance().userid = comments.get(position).userId;
//                                    intent.setClass(mContext, PersonalHome.class);
//                                    mContext.startActivity(intent);
//                                }
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });
//            contextMenu.show();
//        } else {
////            startActivity(new Intent(mContext, Login.class));
//            LoginUtil.startToLogin(mContext);
//        }
//
//    }
//
//    // 获取评论
//    class commentThread implements Runnable {
//        @Override
//        public void run() {
//            ClientSession.Instace().asynGetResponse(new CommentRequest(String.valueOf(voaId), String.valueOf(curCommentPage)), new IResponseReceiver() {
//                @Override
//                public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                    CommentResponse commentResponse = (CommentResponse) response;
//                    if (commentResponse.resultCode.equals("511")) {
//                        comments.addAll(commentResponse.Comments);
//                        commentHandler.sendEmptyMessage(5);
//                        if (comments.size() != 0) {
//                            // curCommentPage<lastPage表示还有评论
//                            if (curCommentPage < Integer.valueOf(commentResponse.lastPage)) {
//                                isDownloadAll = false;
//                                curCommentPage += 1;
//                                commentHandler.sendEmptyMessage(6);
//                            } else {// curCommentPage ==
//                                // lastPage表示已经加载全部评论
//                                isDownloadAll = true;
//                                commentHandler.sendEmptyMessage(7);
//                            }
//                        } else {// comments.size()==0表示还没有评论
//                            commentHandler.sendEmptyMessage(8);
//                        }
//                    } else if (commentResponse.resultCode.equals("510")) {
//                        commentHandler.sendEmptyMessage(8);
//                    } else {
//                        commentHandler.sendEmptyMessage(9);
//                    }
//                }
//            }, null, mNetStateReceiver);
//        }
//    }
//
//    public Handler commentHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    commentFooter.setVisibility(View.VISIBLE);
//                    commentLoadMoreTextView.setTextSize(Constant.textSize);
//                    commentLoadMoreTextView.setTextColor(Constant.normalColor);
//                    commentLoadMoreTextView.setText(getResources().getString(R.string.study_on_load));
//                    new Thread(new commentThread()).start();
//                    break;
//                case 1:// 发表评论
//                    expressEditText.setText("");
//                    String userId = String.valueOf(UserInfoManager.getInstance().getUserId());
//
//                    String username = UserInfoManager.getInstance().getUserName();
//                    ClientSession.Instace().asynGetResponse(new ExpressionRequest(userId, String.valueOf(voaId), expressWord, username), new IResponseReceiver() {
//                        @Override
//                        public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                            waittingDialog.dismiss();
//                            commentHandler.sendEmptyMessageDelayed(4, 1000);
//                        }
//                    }, null, mNetStateReceiver);
//                    break;
//                case 3:
//                    commentAll = false;
//                    commentHandler.sendEmptyMessage(0);
//                    break;
//                case 4:
//                    curCommentPage = 1;
//                    comments.clear();
//                    commentHandler.sendEmptyMessage(3);
//                    break;
//                case 5:
//                    fail_rela_comment.setVisibility(View.GONE);
//                    commentAdapter.notifyDataSetChanged();
//                    break;
//                case 6:
//                    commentLoadMoreTextView.setTextSize(Constant.textSize);
//                    commentLoadMoreTextView.setTextColor(Constant.normalColor);
//                    commentLoadMoreTextView.setText(getResources().getString(R.string.study_load_more));
//                    break;
//                case 7:
//                    commentLoadMoreTextView.setTextSize(Constant.textSize);
//                    commentLoadMoreTextView.setTextColor(Constant.normalColor);
//                    commentLoadMoreTextView.setText(getResources().getString(R.string.study_all_loaded));
//                    break;
//                case 8:
//                    commentLoadMoreTextView.setTextSize(Constant.textSize);
//                    commentLoadMoreTextView.setTextColor(Constant.normalColor);
//                    commentLoadMoreTextView.setText(getResources().getString(R.string.study_no_comment));
//                    break;
//                case 9:
//                    commentLoadMoreTextView.setTextSize(Constant.textSize);
//                    commentLoadMoreTextView.setTextColor(Constant.normalColor);
//                    commentLoadMoreTextView.setText(getResources().getString(R.string.study_checknet));
//                    break;
//                case 10:
//                    String addscore = String.valueOf(msg.arg1);
//                    if (addscore.equals("5")) {
//                        String mg = "语音成功发送至评论区，恭喜您获得了" + addscore + "分";
//                        CustomToast.showToast(mContext, mg, 3000);
//                    } else {
//                        String mg = "语音成功发送至评论区";
//                        CustomToast.showToast(mContext, mg, 3000);
//                    }
//                    valReadAdapter.notifyDataSetChanged();
//                    break;
//                case 11:
//                    CustomToast.showToast(mContext, "请录音后，再发送。", 3000);
//                    if (waittingDialog.isShowing()) waittingDialog.dismiss();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    //练习模块
//    private void initExercise() {
//
//        ll_multipleChoice = (LinearLayout) exercise.findViewById(R.id.ll_multiple_choice);
//        ll_voaStructure = (LinearLayout) exercise.findViewById(R.id.ll_voa_structure);
//        ll_voaDiffculty = (LinearLayout) exercise.findViewById(R.id.ll_voa_diffculty);
//
//
//        tv_multipleChoice = (TextView) exercise.findViewById(R.id.tv_multiple_choice);
//        tv_voaStructure = (TextView) exercise.findViewById(R.id.tv_voa_structure);
//        tv_voaDiffculty = (TextView) exercise.findViewById(R.id.tv_voa_diffculty);
//
//        multipleChoice = (ImageView) exercise.findViewById(R.id.multiple_choice);
//        voaStructure = (ImageView) exercise.findViewById(R.id.voa_structure);
//        voaDiffculty = (ImageView) exercise.findViewById(R.id.voa_diffculty);
//
//        OnClickListener exerciseListener = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v == ll_multipleChoice) {
//                    if (curExerciseActivity != 0) {
//                        curExerciseActivity = 0;
//                    } else {
//                        return;
//                    }
//                } else if (v == ll_voaStructure) {
//                    if (curExerciseActivity != 1) {
//                        curExerciseActivity = 1;
//                    } else {
//                        return;
//                    }
//                } else if (v == ll_voaDiffculty) {
//                    if (curExerciseActivity != 2) {
//                        curExerciseActivity = 2;
//                    } else {
//                        return;
//                    }
//                }
//
//                clickExerciseTab();
//            }
//        };
//
//        ll_multipleChoice.setOnClickListener(exerciseListener);
//        ll_voaStructure.setOnClickListener(exerciseListener);
//        ll_voaDiffculty.setOnClickListener(exerciseListener);
//        exerciseContainer = (ViewFlipper) exercise.findViewById(R.id.exerciseBody);
//        exerciseContainer.setAnimateFirstView(true);
//        clickExerciseTab();
//    }
//
//    private void clickExerciseTab() {
//        multipleChoice.setImageResource(R.drawable.multiple_choice_normal_new);
//        voaStructure.setImageResource(R.drawable.voa_improtant_sentences_normal_new);
//        voaDiffculty.setImageResource(R.drawable.voa_diffcult_normal_new);
//        tv_multipleChoice.setTextColor(Color.parseColor("#333333"));
//        tv_voaStructure.setTextColor(Color.parseColor("#333333"));
//        tv_voaDiffculty.setTextColor(Color.parseColor("#333333"));
//
//        switch (curExerciseActivity) {
//            case 0:
//                exerciseHandler.obtainMessage(1);
//                setExerciseActivity(MultipleChoiceActivity.class, curVoaId);
//                multipleChoice.setImageResource(R.drawable.multiple_choice_press_new);
//                tv_multipleChoice.setTextColor(Color.parseColor("#F6B476"));
//                exerciseHandler.obtainMessage(0);
//                break;
//            case 1:
//                exerciseHandler.obtainMessage(1);
//                setExerciseActivity(VoaStructureExerciseActivity.class, curVoaId);
//                voaStructure.setImageResource(R.drawable.voa_improtent_sentences_press_new);
//                tv_voaStructure.setTextColor(Color.parseColor("#F6B476"));
//                break;
//            case 2:
//                exerciseHandler.obtainMessage(1);
//                setExerciseActivity(VoaDiffcultyExerciseActivity.class, curVoaId);
//                voaDiffculty.setImageResource(R.drawable.voa_diffcult_press_new);
//                tv_voaDiffculty.setTextColor(Color.parseColor("#F6B476"));
//                break;
//        }
//    }
//
//    public Handler exerciseHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    waittingDialog.dismiss();
//                    break;
//                case 1:
//                    waittingDialog.show();
//                    break;
//            }
//        }
//    };
//
//    public void setExerciseActivity(Class<?> cls, String voaId) {
//        Intent intent = new Intent();
//        intent.putExtra("curVoaId", curVoaId);
//        intent.setClass(mContext, cls);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        final Window window = getLocalActivityManager().startActivity(String.valueOf(0), intent);
//        final View view = window != null ? window.getDecorView() : null;
//        if (view != null) {
//            exerciseContainer.removeAllViews();
//            exerciseContainer.addView(view);
//            view.setFocusable(true);
//            exerciseContainer.showNext();
//        }
//    }
//
//    // 评论
//    private void initExpression() {
//        expressButton = (Button) comment.findViewById(R.id.button_express);
//        expressEditText = (EditText) comment.findViewById(R.id.editText_express);
//
//        if (UserInfoManager.getInstance().isLogin()) {
//            expressEditText.setHint(getResources().getString(R.string.hint1));
//            expressButton.setText(getResources().getString(R.string.send));
//            expressEditText.setFocusableInTouchMode(true);
//
//            expressButton.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (CheckNetWork()) {
//                        waittingDialog.show();
//                        if (currMode == 0) {
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(expressEditText.getWindowToken(), 0);
//                            String expressionInput = expressEditText.getText().toString();
//                            if (expressionInput.toString().equals("")) {
//                                CustomToast.showToast(mContext, R.string.study_input_comment, 1000);
//                            } else {
//                                expressWord = expressionInput;
//                                commentHandler.sendEmptyMessage(1);
//                            }
//                        } else {
//                            if (isUploadVoice) {
//                                CustomToast.showToast(mContext, "评论发送中，请不要重复提交", 1000);
//                            } else {
//                                Thread thread = new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Map<String, String> textParams = new HashMap<String, String>();
//                                        Map<String, File> fileParams = new HashMap<String, File>();
//                                        File file = new File(Constant.voiceCommentAddr);
//                                        fileParams.put("content.acc", file);
//                                        if (file != null && file.exists()) {
//                                            try {
//                                                isUploadVoice = true;
//                                                String response = UtilPostFile.post("http://daxue." + Constant.IYUBA_CN + "appApi/UnicomApi?" + "&platform=android&format=json&protocol=60003" + "&userid=" + UserInfoManager.getInstance().getUserId() + "&voaid=" + voaTemp.voaId + "&shuoshuotype=1" + "&appName=concept", textParams, fileParams);
//                                                isUploadVoice = false;
//                                                JSONObject jsonObjectRoot;
//                                                jsonObjectRoot = new JSONObject(response);
//                                                String result = jsonObjectRoot.getInt("ResultCode") + "";
//                                                String addscore = jsonObjectRoot.getString("AddScore");
//                                                // TODO
//                                                if (result.equals("1")) {
//                                                    waittingDialog.dismiss();
//                                                    Looper.prepare();
//                                                    if (addscore.equals("5"))
//                                                        CustomToast.showToast(mContext, "评论成功，恭喜您获得了" + addscore + "分", 3000);
//                                                    commentHandler.sendEmptyMessage(4);
//                                                    file.delete();
//                                                }
//                                            } catch (IOException e) {
//                                                isUploadVoice = false;
//                                                e.printStackTrace();
//                                            } catch (JSONException e) {
//                                                isUploadVoice = false;
//                                                e.printStackTrace();
//                                            }
//                                        } else {
//                                            commentHandler.sendEmptyMessage(11);
//                                        }
//                                    }
//                                });
//                                thread.start();
//                            }
//                        }
//                    } else {
//                        Toast.makeText(mContext, "请在有网的状态下发送。", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
//        } else {
//            expressEditText.setHint(getResources().getString(R.string.hint3));
//            expressEditText.setFocusableInTouchMode(false);
//            expressButton.setText(getResources().getString(R.string.login));
//            expressButton.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
////                    Intent intent = new Intent();
////                    intent.setClass(mContext, Login.class);
////                    startActivity(intent);
//                    LoginUtil.startToLogin(mContext);
//                }
//            });
//        }
//    }
//
//    // 执行resume之后的设置
//    private void setBackGround(int item) {
////        textButton.setBackgroundResource(0);
//        textButton.setSelected(false);
//        textButton.setTextColor(0xff8C8C8C);
//        textButton.setTextSize(15);
////        knowledgeButton.setBackgroundResource(0);
//        rankButton.setSelected(false);
//        rankButton.setTextColor(0xff8C8C8C);
//        rankButton.setTextSize(15);
//        knowledgeButton.setSelected(false);
//        knowledgeButton.setTextColor(0xff8C8C8C);
//        knowledgeButton.setTextSize(15);
////        exerciseButton.setBackgroundResource(0);
//        exerciseButton.setSelected(false);
//        exerciseButton.setTextColor(0xff8C8C8C);
//        exerciseButton.setTextSize(15);
////        reReadButton.setBackgroundResource(0);
//        reReadButton.setSelected(false);
//        reReadButton.setTextColor(0xff8C8C8C);
//        reReadButton.setTextSize(15);
////        commentButton.setBackgroundResource(0);
//        commentButton.setSelected(false);
//        commentButton.setTextColor(0xff8C8C8C);
//        commentButton.setTextSize(15);
//
//        switch (item) {
//            case 0:
////                textButton.setBackgroundResource(R.drawable.tab_orange);
//                textButton.setSelected(true);
//                textButton.setTextColor(Constant.normalColor);
//                textButton.setTextSize(17);
//                break;
//            case 1:
////                reReadButton.setBackgroundResource(R.drawable.tab_orange);
//                reReadButton.setSelected(true);
//                reReadButton.setTextColor(Constant.normalColor);
//                reReadButton.setTextSize(17);
//                break;
//            case 2:
////                reReadButton.setBackgroundResource(R.drawable.tab_orange);
//                rankButton.setSelected(true);
//                rankButton.setTextColor(Constant.normalColor);
//                rankButton.setTextSize(17);
//                break;
//            case 3:
////                knowledgeButton.setBackgroundResource(R.drawable.tab_orange);
//                knowledgeButton.setSelected(true);
//                knowledgeButton.setTextColor(Constant.normalColor);
//                knowledgeButton.setTextSize(17);
//                break;
//            case 4:
////                exerciseButton.setBackgroundResource(R.drawable.tab_orange);
//                exerciseButton.setSelected(true);
//                exerciseButton.setTextColor(Constant.normalColor);
//                exerciseButton.setTextSize(17);
//                break;
//            case 5:
////                commentButton.setBackgroundResource(R.drawable.tab_orange);
//                commentButton.setSelected(true);
//                commentButton.setTextColor(Constant.normalColor);
//                commentButton.setTextSize(17);
//                break;
//        }
//    }
//
//    // 设置text的内容
//    private void setVoaData() {
//        voaTemp = voaOp.findDataById(voaId);
//        VoaDataManager.Instace().voaTemp = voaTemp;
//        VoaDataManager.Instace().setSubtitleSum(voaTemp, VoaDataManager.Instace().voaDetailsTemp);
//        videoHandler.sendEmptyMessage(NEXT_VIDEO);
//    }
//
//    private void RefreshData() {
//
//        subtitleSum = VoaDataManager.Instace().subtitleSum;
//        textDetailTemp = VoaDataManager.Instace().voaDetailsTemp;
//        getCounts(textDetailTemp);
//        currParagraph = 1;
//        VoaDataManager.Instace().changeLanguage(!isShowChinese);
//        textCenter.setSubtitleSum(subtitleSum);
//
//        if (valReadAdapter != null) {
//            Log.e("StudyActivity", "RefreshData");
//            valReadAdapter.setAdapter(textDetailTemp, voaTemp);
//        }
//
//        comments.removeAll(comments);
//        commentHandler.sendEmptyMessage(3);
//        commentAdapter.notifyDataSetChanged();
//        playVideo(true);
//        Title.setText(voaTemp.title);
//    }
//
//    private void setSeekbar() {
//        int i = 0;
//        i = videoView.getDuration();
//
//        seekBar.setMax(i);
//        i /= 1000;
//        int minute = i / 60;
//        int second = i % 60;
//        minute %= 60;
//        totalTime.setText(String.format("%02d:%02d", minute, second));
//
//        player_alltime = i;
//    }
//
//    private void setModeBackground() {
//        if (mode == 0) {
//            modeButton.setImageDrawable(getResources().getDrawable(R.drawable.play_this));
//        } else if (mode == 1) {
//            modeButton.setImageDrawable(getResources().getDrawable(R.drawable.play_next));
//        } else if (mode == 2) {
//            modeButton.setImageDrawable(getResources().getDrawable(R.drawable.play_random));
//        }
//    }
//
//    private void setShowChineseButton() {
//        if (isShowChinese) {
//            showChineseButton.setImageResource(R.drawable.show_chinese_selected);
//            VoaDataManager.Instace().changeLanguage(false);
//            textCenter.updateSubtitleView();
//        } else {
//            showChineseButton.setImageResource(R.drawable.show_chinese);
//            VoaDataManager.Instace().changeLanguage(true);
//            textCenter.updateSubtitleView();
//        }
//    }
//
//    private void setLockButton() {
//        Handler voice_handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what) {
//                    case 0:
//                        // comment.findViewById(R.id.voice_view)
//                        voiceView.setVisibility(View.VISIBLE);
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//        };
//        textCenter.syncho = syncho;
//    }
//
////    private void getIyubi(String to) {
////        String sigMd5 = AccountManager.Instace(mContext).userId
////                + Constant.APPID + "android" + to + String.valueOf(voaId) + 0
////                + "iyuba"; // 认证码Md5(userId+appId+from+to+titleId+type+’iyuba’)
////        sigMd5 = MD5.getMD5ofStr(sigMd5);
////        ClientSession.Instace().asynGetResponse(
////                new ShareRequest(AccountManager.Instace(mContext).userId,
////                        String.valueOf(voaTemp.voaId), to, sigMd5),
////                new IResponseReceiver() {
////
////                    @Override
////                    public void onResponse(BaseHttpResponse response,
////                                           BaseHttpRequest request, int rspCookie) {
////                    }
////                }, null, null);
////    }
//
//    private void controlVideo() {
//
//        videoView.setOnPreparedListener(new OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                setSeekbar();
//                if (currentPage == 0) {
//                    videoView.start();
//                }
//                videoHandler.sendEmptyMessage(PROGRESS_CHANGED);
//            }
//        });
//
//
//
//        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//            @Override
//            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                Message msg = Message.obtain();
//                msg.arg1 = percent;
//                msg.what = BUFFER_CHANGED;
//                videoHandler.sendMessage(msg);
//            }
//        });
//
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                new Thread(new UpdateStudyRecordThread()).start();
//
//                if (mode == 0) {
//                    playVideo(true);
//                    textCenter.setSubtitleSum(subtitleSum);
//                    // setReadText();
//                } else {
//                    if (mode == 1) {
//                        voaId = new NextVideo(voaId, source, mContext).following();
//                    } else if (mode == 2) {
//                        voaId = new NextVideo(voaId, source, mContext).nextVideo();
//                    }
//
//                    VoaDataManager.Instace().voaDetailsTemp = textDetailOp.findDataByVoaId(voaId);
//                    if (VoaDataManager.Instace().voaDetailsTemp != null && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
//                        setVoaData();
//                    }
//                }
//
//                setReadLesson(voaId);
//
//                Title.setText(voaTemp.title);
//                updateNotification();
//            }
//        });
//
//    }
//
//    public void setReadLesson(int voaId) {
//        voaOp.updateReadCount(voaId);
//        int curBook = ConceptBookChooseManager.getInstance().getBookId();
//        switch (curBook) {
//            case 1:
//                ConfigManager.Instance().putInt("lately_one", voaId);
//                break;
//            case 2:
//                ConfigManager.Instance().putInt("lately_two", voaId);
//                break;
//            case 3:
//                ConfigManager.Instance().putInt("lately_three", voaId);
//                break;
//            case 4:
//                ConfigManager.Instance().putInt("lately_four", voaId);
//                break;
//        }
//    }
//
//    private void showAlertAndCancel(String title, String msg) {
//        final AlertDialog alert = new AlertDialog.Builder(this).create();
//        alert.setTitle(title);
//        alert.setMessage(msg);
//        alert.setIcon(android.R.drawable.ic_dialog_alert);
//        alert.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.alert_btn_ok), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                alert.dismiss();
//            }
//        });
//        alert.show();
//    }
//
//    // 结构化时间
//    private void Reciprocal(int time) {
//        int i = time;
//        int minute = i / 60;
//        int second = i % 60;
//        textView_time.setText(String.format("%02d:%02d", minute, second));
//    }
//
//    private void updateNotification() {
//        currParagraph = 0;
//        lastParegraph = 1;
//    }
//
//    public String getLocalMacAddress() {
//        WifiManager wifi = (WifiManager) StudyActivity.newInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        return info.getMacAddress();
//    }
//
//    public void tofinish() {
//
//        this.finish();
//    }
//
//    private String getFirstChar(String name) {
//        String subString;
//        for (int i = 0; i < name.length(); i++) {
//            subString = name.substring(i, i + 1);
//
//            p = Pattern.compile("[0-9]*");
//            m = p.matcher(subString);
//            if (m.matches()) {
////                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
//                return subString;
//            }
//
//            p = Pattern.compile("[a-zA-Z]");
//            m = p.matcher(subString);
//            if (m.matches()) {
////                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
//                return subString;
//            }
//
//            p = Pattern.compile("[\u4e00-\u9fa5]");
//            m = p.matcher(subString);
//            if (m.matches()) {
////                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
//                return subString;
//            }
//        }
//
//        return "A";
//    }
//
//    @Override
//    protected void onPause() {
//        // TODO
//        // updateNotification();
//        handlerText.removeMessages(1);// 手动停止A-B播放
//
//        if (currParagraph != 0) {
//            textCenter.snyParagraph(currParagraph);
//        }
//
//        if (valReadAdapter.videoView != null && valReadAdapter.videoView.isPlaying()) {
//            valReadAdapter.videoView.pause();
//            valReadAdapter.handler.removeMessages(0);
//        }
//
//        commentHandler.removeMessages(0);
//        videoHandler.removeMessages(PROGRESS_CHANGED);
//        videoHandler.removeMessages(BUFFER_CHANGED);
//
//        super.onPause();
//        // MobclickAgent.onPause(this);
//    }
//
//    /**
//     * 提交学习记录
//     */
//
//    // todo 需要修改testNumber
//    class UpdateStudyRecordThread implements Runnable {
//        @Override
//        public void run() {
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//            endTime = df.format(new Date());
//
//            uid = String.valueOf(UserInfoManager.getInstance().getUserId());
//            String endFlag = "1";
//            // String lesson = msg.getData().getString("lesson");
//            String lesson = "concept"; // lesson应该上传用户使用的哪一款app的名称
//            String lessonId = String.valueOf(voaId);
//            String testNumber = String.valueOf(currParagraph);
//            String testWords = "0";
//            final String testMode = "1";
//            String userAnswer = "";
//            String score = "0";
//            SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
//            String sign = uid + beginTime + dft.format(System.currentTimeMillis());
//            String deviceId = getLocalMacAddress();
//
//            if (NetWorkState.isConnectingToInternet()) {
//                try {
//                    ClientSession.Instace().asynGetResponse(new DataCollectRequest(uid, beginTime, endTime, lesson, lessonId, testNumber, words + "", testMode, userAnswer, score, endFlag, deviceId, sign,false), new IResponseReceiver() {
//                        @Override
//                        public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                            DataCollectResponse tr = (DataCollectResponse) response;
//
//                            if (tr != null && tr.result.equals("1") && testMode.equals("1")) {
//                                Looper.prepare();
//                                if (tr.score.equals("0")) {
//                                    // Toast.makeText(mContext,
//                                    // "数据提交成功!", 1500).show();
//                                } else
//                                    Toast.makeText(mContext, "数据提交成功，恭喜您获得了" + tr.score + "分", Toast.LENGTH_SHORT).show();
//
//                                handler.sendEmptyMessage(8);
//                                Looper.loop();
//                            } else if (tr.result.equals("0")) {
//                                Looper.prepare();
////                                        Toast.makeText(mContext, "数据提交出错", Toast.LENGTH_SHORT)
////                                                .show();
//                                Looper.loop();
//                            } else {
//                                Looper.prepare();
//                                Toast.makeText(mContext, "数据提交异常", Toast.LENGTH_SHORT).show();
//                                Looper.loop();
//                            }
//                        }
//                    }, null, mNetStateReceiver);
//                } catch (UnsupportedEncodingException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//
//            //重置学习开始时间,不能和上一篇的末尾时间重叠，需要延时一秒
//            timeHandler.sendEmptyMessageDelayed(1, 1000);
//        }
//    }
//
//    @Override
//    public void appUpdateSave(String version_code, String newAppNetworkUrl) {
//
//    }
//
//    private Handler timeHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//            beginTime = df.format(new Date());
//        }
//    };
//
//    private float stepIncrease(float speed) {
//        int temp = (int) (speed * 10);
//        temp = (temp >= 20) ? 6 : temp + 2;
//        return (float) temp / 10.0f;
//    }
//
//    private String buildSpeedString(float speed) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.valueOf(speed)).append("x");
//        return sb.toString();
//    }
//
//    @Override
//    public void appUpdateFaild() {
//
//    }
//
//    @Override
//    public void appUpdateBegin(String newAppNetworkUrl) {
//
//    }
//
//    private class initExtendedPlayer extends AsyncTask<String, Integer, String> {
//        @Override
//        protected String doInBackground(String... params) {
//
//            playVideo(false);
//            return null;
//        }
//
////        @Override
////        protected void onPreExecute() {
////            super.onPreExecute();
////            waittingDialog.show();
////        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            setSeekbar();
//            setModeBackground();
//            setShowChineseButton();
//            setLockButton();
//            videoHandler.sendEmptyMessage(PROGRESS_CHANGED);
////            waittingDialog.dismiss();
//        }
//    }
//
//    Handler voice_handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    voiceView.setVisibility(View.VISIBLE);
//                    break;
//                case 1:
//                    voiceView.setVisibility(View.GONE);
//                    break;
//            }
//        }
//    };
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
//                    videoView.seekTo(aPositon);// A-B播放
//
//                    handlerText.sendEmptyMessageDelayed(1, bPosition - aPositon + 300);
//                    break;
//                default:
//                    break;
//
//            }
//        }
//    };
//
//
//    private Handler videoHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case PROGRESS_CHANGED:
//                    int i = 0;
//                    i = videoView.getCurrentPosition();
//
//                    seekBar.setProgress(i);
//                    i /= 1000;
//                    int minute = i / 60;
//                    int second = i % 60;
//                    minute %= 60;
//                    // playedTime = i;
//                    curTime.setText(String.format("%02d:%02d", minute, second));
//                    try {
//                        if (videoView.isPlaying()) {
//                            Log.d("控制按钮显示", "handleMessage: --切换为播放状态");
//
//                            Log.e("speedplayer", "" + videoView.getCurrentPosition() / 1000.0);
//                            currParagraph = subtitleSum.getParagraph(videoView.getCurrentPosition() / 1000.0);
//
//                            if (currParagraph != 0) {
//                                textCenter.snyParagraph(currParagraph);
//                            }
//                            isPaused = false;
//                            pause.setBackgroundResource(R.drawable.image_pause);
//                        } else if (textCenter != null) {
//                            Log.d("控制按钮显示", "handleMessage: --切换为暂停状态");
//
//                            pause.setBackgroundResource(R.drawable.image_play);
//                            textCenter.unsnyParagraph();
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
//                    }
//                    break;
//                case BUFFER_CHANGED:
//                    seekBar.setSecondaryProgress(msg.arg1 * seekBar.getMax() / 100);
//                    break;
//                case NEXT_VIDEO:
//                    RefreshData();
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//    public Handler wordHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    card.setVisibility(View.VISIBLE);
//                    break;
//            }
//        }
//    };
//
//    public Handler rankHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    try {
//                        ClientSession.Instace().asynGetResponse(new GetRankInfoRequest(uid, "concept", String.valueOf(voaId), startRank, total), new IResponseReceiver() {
//                            @Override
//                            public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                                GetRankInfoResponse tr = (GetRankInfoResponse) response;
//                                if (tr.myName != null && !"".equals(tr.myName) && !"null".equals(tr.myName) && !"none".equals(tr.myName))
//                                    myName = tr.myName;
//                                else {
//                                    myName = tr.uid;
//                                }
//                                myImgSrc = tr.myImgSrc;
//                                myScores = tr.myScores;
//                                myCount = tr.myCount;
//                                myRanking = tr.myRanking;
//                                rankUsers = tr.rankUsers;
//                                try {
//                                    champion = rankUsers.get(0);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    champion = new RankUser();
//                                }
//
//                                if (rankUsers.size() < 20) {
//                                    isLastPageRank = true;
//                                }
//                                rankHandler.sendEmptyMessage(1);
//                                if (champion.getRanking().equals("1"))
//                                    rankHandler.sendEmptyMessage(2);
//                            }
//                        }, null, mNetStateReceiver);
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    break;
//                case 1:
//                    fail_rela_rank.setVisibility(View.GONE);
//                    if (rankListAdapter == null) {
//                        rankUsersList.addAll(rankUsers);
//                        rankListAdapter = new RankListAdapterNew(mContext, rankUsersList);
//                        rankListView.setAdapter(rankListAdapter);
//
//                    } else {
//                        rankUsersList.addAll(rankUsers);
//                        rankListAdapter.notifyDataSetChanged();
//                    }
//                    rankListAdapter.setCurVoaId(curVoaId);
//                    startRank = String.valueOf(rankListAdapter.getItemCount());
//                    myUsername.setText(myName);
//                    userInfo.setText("句子:" + myCount + ",得分:" + myScores + ",排名:" + myRanking);
//                    GitHubImageLoader.Instace(mContext).setRawPic(myImgSrc, myImage, R.drawable.noavatar_small);
//                    break;
//                case 2:
//                    String firstChar = getFirstChar(champion.getName());
//                    if (champion.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
//                        userImage.setVisibility(View.INVISIBLE);
//                        userImageText.setVisibility(View.VISIBLE);
//                        p = Pattern.compile("[a-zA-Z]");
//                        m = p.matcher(firstChar);
//                        if (m.matches()) {
////                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
//                            userImageText.setBackgroundResource(R.drawable.rank_blue);
//                            userImageText.setText(firstChar);
//
//                            if (champion.getName() != null && !"".equals(champion.getName()) && !"null".equals(champion.getName())
//
//                                    && !"none".equals(champion.getName())) {
//                                userName.setText(champion.getName());
//                            } else {
//                                userName.setText(champion.getUid());
//                            }
//
//
//                            cpReadWords.setText(champion.getScores());
//                        } else {
//                            userImageText.setBackgroundResource(R.drawable.rank_green);
//                            userImageText.setText(firstChar);
//                            if (champion.getName() != null && !"".equals(champion.getName()) && !"null".equals(champion.getName())
//
//                                    && !"none".equals(champion.getName())) {
//                                userName.setText(champion.getName());
//                            } else {
//                                userName.setText(champion.getUid());
//                            }
//                            cpReadWords.setText(champion.getScores());
//                        }
//                    } else {
//                        userImageText.setVisibility(View.INVISIBLE);
//                        userImage.setVisibility(View.VISIBLE);
//                        GitHubImageLoader.Instace(mContext).setRawPic(champion.getImgSrc(), userImage, R.drawable.noavatar_small);
//
//                        if (champion.getName() != null && !"".equals(champion.getName()) && !"null".equals(champion.getName())
//
//                                && !"none".equals(champion.getName())) {
//                            userName.setText(champion.getName());
//                        } else {
//                            userName.setText(champion.getUid());
//                        }
//
//
//                        cpReadWords.setText(champion.getScores());
//                    }
//                    break;
//            }
//        }
//    };
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0x111:
//                    initaiyubaAd();
//                    break;
//                case 1:
//                    String type = (voaTemp.isCollect.equals("1")) ? "insert" : "del";
//                    ExeProtocol.exe(new FavorUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), voaTemp.voaId, type), new ProtocolResponse() {
//                        @Override
//                        public void finish(BaseHttpResponse bhr) {
//                            FavorUpdateResponse reponse = (FavorUpdateResponse) bhr;
//                            if (reponse.result == 1 || reponse.result == 2) {
//                                voaOp.updateSynchro(voaTemp.voaId, 1);
//
//                                handler.sendEmptyMessage(3);
//                            } else {
//                                handler.sendEmptyMessage(4);
//                            }
//                        }
//
//                        @Override
//                        public void error() {
//                        }
//                    });
//                    break;
//                case 3:
//                    String tip = (voaTemp.isCollect.equals("1")) ? "收藏成功" : "删除成功";
//                    CustomToast.showToast(mContext, tip, 2000);
//                    break;
//                case 4:
//                    CustomToast.showToast(mContext, R.string.play_check_network, 1000);
//
//                    if (rankUsers != null && rankUsers.size() == 0) {
//                        fail_rela_rank.setVisibility(View.VISIBLE);
//                    }
//                    if (curCommentPage == 1) {
//                        fail_rela_comment.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 5:
//                    showAlertAndCancel(getResources().getString(R.string.alert), getResources().getString(R.string.study_info1));
//                    break;
//                case 13:
//                    Toast.makeText(mContext, "分享成功！", Toast.LENGTH_SHORT).show();
//                    break;
//                case 19:
//                case 49:
//                    if (UserInfoManager.getInstance().isLogin()) {
//                        final ReadVoiceComment rvc = new ReadVoiceComment(voaTemp);
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
//                        AddCreditsRequest rq = new AddCreditsRequest(uid, rvc.getVoaRef().voaId, msg.what, rc);
//                        RequestQueue queue = Volley.newRequestQueue(mContext);
//                        queue.add(rq);
//                    }
//                    break;
//
//                case 6:
//                    if (mixPlayer.isPlaying()) {
//                        imv_total_time.setText(getDurationInFormat());
//                        imv_seekbar_player.setMax((int) mp3TotalTime);
//                        imv_seekbar_player.setProgress(mixPlayer.getCurrentTime());
//                        if (mixPlayer.getCurrentTime() < mp3TotalTime) {
//
//                            mp3changTime = mp3changTime + 100;
//                            imv_current_time.setText(mixPlayer.getCurrentTimeInFormat());
//                            handler.sendEmptyMessageDelayed(6, 100);
//                        } else {
//
//                            handler.sendEmptyMessage(7);
//                        }
//                    } else {
//                        handler.sendEmptyMessage(7);
//                    }
//                    break;
//                case 7:
//                    imv_seekbar_player.setMax(100);
//                    imv_seekbar_player.setProgress(100);
//                    imv_current_time.setText(getDurationInFormat());
//                    tv_read_mix.setEnabled(true);
//                    break;
//
//
//            }
//        }
//    };
//
//    //合成语音上发至服务器
//    private void sendSound() {
//
//        if (UserInfoManager.getInstance().isLogin()) {
//            if (isSendSound) {
//                com.iyuba.conceptEnglish.widget.cdialog.CustomToast.showToast(mContext, "评论发送中，请不要重复提交", 1000);
//            } else {
//                waittingDialog.show();
//                Thread threadsend = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Map<String, String> textParams = new HashMap<String, String>();
//                        Map<String, File> fileParams = new HashMap<String, File>();
//                        File file = new File(Constant.getsimRecordAddr(StudyActivity.this) + "mix" + ".mp3");
//                        textParams.put("topic", "concept");
//                        fileParams.put("content.acc", file);
//                        Log.e("voaid", voaTemp.voaId + "===");
//                        if (file != null && file.exists()) {
//                            try {
//                                isSendSound = true;
//                                String response = UtilPostFile.post("http://voa." + Constant.IYUBA_CN + "voa/UnicomApi?topic=concept" + "&platform=android&format=json&protocol=60003" + "&userid=" + UserInfoManager.getInstance().getUserId() + "&voaid=" + voaTemp.voaId + "&score=" + (totalScore / pmc_file_list.size()) + "&shuoshuotype=4", textParams, fileParams);
//                                Log.e("sendRank", response);
//                                isSendSound = false;
//
//                                JSONObject jsonObjectRoot;
//                                jsonObjectRoot = new JSONObject(response);
//                                String result = jsonObjectRoot.getInt("ResultCode") + "";
//
//
//                                shuoshuoId = jsonObjectRoot.getInt("ShuoShuoId") + "";
//                                String addscore = jsonObjectRoot.getString("AddScore");
//
//
//                                // TODO
//                                if (result.equals("1")) {
//                                    waittingDialog.dismiss();
//                                    Message msg = handler.obtainMessage();
//
//                                    msg.what = 10;
//                                    msg.arg1 = Integer.parseInt(addscore);
//                                    commentHandler.sendMessage(msg);
//                                    rankHandler.sendEmptyMessage(0);
//                                }
//                            } catch (Exception e) {
//                                isSendSound = false;
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                });
//                threadsend.start();
//            }
//        } else {
////            Intent intent = new Intent();
////            intent.setClass(mContext, Login.class);
////            StudyActivity.newInstance().startActivity(intent);
//            LoginUtil.startToLogin(mContext);
//        }
//    }
//
//    private void showShareSound() {
//        String url;
//        if (mixUrl != null) {
//            url = "&addr=" + mixUrl;
//        } else {
//            url = "";
//        }
//        String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + shuoshuoId + url + "&apptype=concept";
//
//        String text = "我在爱语吧语音评测中获得了" + (totalScore / pmc_file_list.size()) + "分";
//        String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
////        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
////        weibo.removeAccount(true);
//        //微博飞雷神
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
//        oks.setTitle(text);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(siteUrl);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(voaTemp.title);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
////         oks.setImagePath("/sdcard/test.jpg");
//        // imageUrl是Web图片路径，sina需要开通权限
//        oks.setImageUrl(imageUrl);
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(siteUrl);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("爱语吧的这款应用" + Constant.APPName + "真的很不错啊~推荐！");
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
//                if (UserInfoManager.getInstance().getUserId() > 0) {
//                    Message msg = new Message();
//                    msg.obj = arg0.getName();
//                    if (arg0.getName().equals("QQ") || arg0.getName().equals("Wechat") || arg0.getName().equals("WechatFavorite")) {
//                        msg.what = 49;
//                    } else if (arg0.getName().equals("QZone") || arg0.getName().equals("WechatMoments") || arg0.getName().equals("SinaWeibo") || arg0.getName().equals("TencentWeibo")) {
//                        msg.what = 19;
//                    }
//                    handler.sendMessage(msg);
//                } else {
//                    handler.sendEmptyMessage(13);
//                }
//            }
//
//            @Override
//            public void onCancel(Platform arg0, int arg1) {
//                Log.e("okCallbackonCancel", "onCancel");
//            }
//        });
//        // 启动分享GUI
//        oks.show(this);
//    }
//
//    /**
//     * 时间格式转化为00：00
//     *
//     * @return
//     */
//    private String getDurationInFormat() {
//        StringBuffer sb = new StringBuffer("");
//        int musicTime = (int) (mp3TotalTime / 1000);
//        String minu = String.valueOf(musicTime / 60);
//        if (minu.length() == 1) {
//            minu = "0" + minu;
//        }
//        String sec = String.valueOf(musicTime % 60);
//        if (sec.length() == 1) {
//            sec = "0" + sec;
//        }
//
//        sb.append(minu).append(":").append(sec);
//        return sb.toString();
//    }
//
//    /**
//     * list 去重 保留最新元素
//     *
//     * @param list
//     * @return
//     */
//    public static List removeDuplicate(List list) {
//        for (int i = 0; i < list.size() - 1; i++) {
//            for (int j = list.size() - 1; j > i; j--) {
//                if (list.get(j).equals(list.get(i))) {
//                    list.remove(j);
//                }
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 提交学习记录，原文点击暂停按钮，或者切换到评测，排行等页面时强制暂停，或者点击返回按钮时提交不完成阅读文章记录
//     */
//
//    private void commitStudyRecordUnfinish() {
//
//        isPaused = true;
//
//        if (videoView.isPlaying()) {
//            new Thread(new UpdateStudyRecordunfinishThread()).start();
//        }
//
//    }
//
//    /**
//     * 提交学习记录
//     */
//
//    class UpdateStudyRecordunfinishThread implements Runnable {
//        @Override
//        public void run() {
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//            endTime = df.format(new Date());
//
//            uid = String.valueOf(UserInfoManager.getInstance().getUserId());
//
//            String endFlag = "0";  //未完成播放
//            // String lesson = msg.getData().getString("lesson");
//            String lesson = "concept"; // lesson应该上传用户使用的哪一款app的名称
//            String lessonId = String.valueOf(voaId);
//            String testNumber = String.valueOf(currParagraph);
//            String testWords = "0";
//            final String testMode = "1";
//            String userAnswer = "";
//            String score = "0";
//            SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
//            String sign = uid + beginTime + dft.format(System.currentTimeMillis());
//            String deviceId = getLocalMacAddress();
//
//            if (NetWorkState.isConnectingToInternet()) {
//                try {
//                    ClientSession.Instace().asynGetResponse(new DataCollectRequest(uid, beginTime, endTime, lesson, lessonId, testNumber, wordsCount() + "", testMode, userAnswer, score, endFlag, deviceId, sign,false), new IResponseReceiver() {
//                        @Override
//                        public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                            DataCollectResponse tr = (DataCollectResponse) response;
//
//                            if (tr != null && tr.result.equals("1") && testMode.equals("1")) {
//                                Looper.prepare();
//                                if (tr.score.equals("0")) {
//                                    // Toast.makeText(mContext,
//                                    // "数据提交成功!", 1500).show();
//                                } else
//                                    Toast.makeText(mContext, "数据提交成功，恭喜您获得了" + tr.score + "分", Toast.LENGTH_SHORT).show();
//
//                                handler.sendEmptyMessage(8);
//                                Looper.loop();
//                            } else if (tr.result.equals("0")) {
//                                Looper.prepare();
//                                Looper.loop();
//                            } else {
//                                Looper.prepare();
//                                Toast.makeText(mContext, "数据提交异常", Toast.LENGTH_SHORT).show();
//                                Looper.loop();
//                            }
//                        }
//                    }, null, mNetStateReceiver);
//                } catch (UnsupportedEncodingException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//
//
//        }
//    }
//
//    /**
//     * 计算提交时间段的单词数
//     */
//    private int wordsCount() {
//
//        int wordNum = 0;
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//
//        try {
//
//            int timeAll = (int) ((df.parse(endTime).getTime() - df.parse(beginTime).getTime()) / 1000);
//
//
//            wordNum = timeAll * words / player_alltime;
//
//            Log.e("url", timeAll + "==" + player_alltime + "==" + words + "====" + wordNum);
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
//
//    @NeedsPermission({android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
//    public void requestEvaluate() {
//
//
//    }
//
//    @OnPermissionDenied({android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
//    public void requestEvaluateDenied() {
//        ToastUtil.showToast(StudyActivity.this, "申请权限失败,此功能无法正常使用!");
//        return;
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        StudyActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
//
//    public void checkStudyPermission() {
//        StudyActivityPermissionsDispatcher.requestEvaluateWithPermissionCheck(StudyActivity.this);
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(StopTextPlayerEvent event) {
//        if (videoView != null && videoView.isPlaying()) {
//            videoView.pause();
//            isPaused = !isPaused;
//        }
//    }
//
//    private void resetMediaPlayer(String url, boolean flag) {
//        try {
//            mp = null;
//            mp = new MediaPlayer();
//            mp.reset();
//            mp.setDataSource(url);
//            if (flag) {
//                mp.prepare();
//            } else {
//                mp.prepareAsync();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 合成语音
//     *
//     * @param audios
//     */
//    private void audioCompose(String audios) {
//        AudioComposeApi audioComposeApi = ApiRetrofit.getInstance().getAudioComposeApi();
//        audioComposeApi.audioComposeApi(AudioComposeApi.BASEURL, audios, "concept").enqueue(new Callback<EvaMixBean>() {
//
//            @Override
//            public void onResponse(Call<EvaMixBean> call, Response<EvaMixBean> response) {
//
//                if (response.isSuccessful()) {
//                    LogUtils.e("返回数据==" + response.body());
//                    EvaMixBean evaMixBean = response.body();
//                    String result = evaMixBean.getResult();
//
//                    if ("1".equals(result)) {
//                        //成功
//                        mixUrl = evaMixBean.getURL();
//                        tv_read_sore.setVisibility(View.VISIBLE);
//                        tv_read_sore.setText(totalScore / pmc_file_list.size() + "");
//                        tv_read_mix.setText("试听");
//                        isMix = true;
//                        Log.e("时间", mp3TotalTime + "");
//                        imv_seekbar_player.setProgress(0);
//
//                    } else {
//                        ToastUtil.showToast(mContext, "合成失败，请稍后再试");
//                    }
//                } else {
//                    ToastUtil.showToast(mContext, "合成失败，请稍后再试");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<EvaMixBean> call, Throwable t) {
//                ToastUtil.showToast(mContext, "合成失败，请稍后再试");
//            }
//        });
//    }
//
//    //合成语音上发至服务器
//    private void sendsong() {
//        waittingDialog.show();
//
//        AudioSendApi audioSendApi = ApiRetrofit.getInstance().getAudioSendApi();
//        audioSendApi.audioSendApi(AudioSendApi.BASEURL, "concept",
//                AudioSendApi.platform,
//                AudioSendApi.format, AudioSendApi.protocol,
//                String.valueOf(UserInfoManager.getInstance().getUserId()),
//                TextAttr.encode(UserInfoManager.getInstance().getUserName()),
//                voaTemp.voaId + "", totalScore / pmc_file_list.size() + "", "4", mixUrl,Constant.APP_ID,1).enqueue(new Callback<EvaSendBean>() {
//            @Override
//            public void onResponse(Call<EvaSendBean> call, Response<EvaSendBean> response) {
//
//
//                if (response.isSuccessful()) {
//
//
//                    EvaSendBean evaSendBean = response.body();
//                    shuoshuoId = evaSendBean.getShuoshuoId() + "";
//
//                    String resultNew = evaSendBean.getResultCode();
//                    // TODO
//                    if (resultNew.equals("501")) {
//                        waittingDialog.dismiss();
//                        Message msg = handler.obtainMessage();
//                        msg.what = 10;
//                        msg.arg1 = evaSendBean.getAddScore();
//                        commentHandler.sendMessage(msg);
//                        rankHandler.sendEmptyMessage(0);
//
//                        //获取奖励并显示
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
//    @Override
//    public void setMixPlayerPause() {
//        if (mixPlayer != null && mixPlayer.isPlaying()) {
//            mixPlayer.pause();
//            handler.removeMessages(6);
//            if (tv_read_mix.getText().equals("暂停")) {
//                tv_read_mix.setText("试听");
//            }
//        }
//    }
//}