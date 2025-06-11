package com.iyuba.conceptEnglish.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.multidex.MultiDex;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.guide.GuideActivity;
import com.iyuba.conceptEnglish.ad.AdInitManager;
import com.iyuba.conceptEnglish.databinding.WelcomeBinding;
import com.iyuba.conceptEnglish.event.WelcomeBackEvent;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.lil.concept_other.book_choose.ConceptBookChooseActivity;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AbilityControlManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshUserInfoEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.StudySettingManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.showManager.VerifyShowManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.AdLogUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.spread.AdSpreadShowManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.spread.AdSpreadShowSession;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.spread.AdSpreadViewBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.manager.sharedpreferences.InfoHelper;
import com.iyuba.conceptEnglish.sqlite.DatabaseUtil;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.conceptEnglish.util.ShareHelper;
import com.iyuba.conceptEnglish.widget.dialog.SeparatedPrivacyDialog;
import com.iyuba.config.AdTestKeyData;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.configation.util.AsyncConfigThreadPool;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.util.PrivacyUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.data.local.db.HLDBManager;
import com.iyuba.module.dl.BasicDLDBManager;
import com.iyuba.module.favor.data.local.BasicFavorDBManager;
import com.iyuba.module.privacy.IPrivacy;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.mse.BuildConfig;
import com.iyuba.trainingcamp.ITraining;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;


/**
 * 起始界面Activity
 */
public class WelcomeActivity extends BasisActivity implements OnRequestPermissionsResultCallback {
    private static final String TAG = "WelcomeActivity";

    private Context mContext;

    //布局界面
    private WelcomeBinding binding;

    //计时器-延迟显示
    private static final String timer_delayShow = "delayShow";
    //计时器-延迟跳转
    private static final String timer_delayJump = "delayJump";

    /**
     * WelcomeActivity的两种启动方式
     * 1.由桌面启动 onActivityStarted=false
     * 2.由应用内的其他activity启动 onActivityStarted=true
     */
    private boolean onActivityStarted = false;
    /**
     * 必须添加的标识
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        MultiDex.install(this);
        PrivacyInfoHelper.init(getApplicationContext());
        PrivacyInfoHelper.getInstance().putApproved(true);

        updateDatabase();
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Debug.startMethodTracing("browser.trace");
        }
        onActivityStarted = getIntent().getBooleanExtra("onActivityStarted", false);
        mContext = this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//指定应用窗体无标题
        setVolumeControlStream(AudioManager.STREAM_MUSIC);//多媒体音量控制  /音乐回放即媒体音量/
        RuntimeManager.setDisplayMetrics(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        //设置布局界面
        binding = WelcomeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());//设置页面布局
        //全局程序崩溃捕捉
        CrashApplication.getInstance().addActivity(this);
        /* 再也不用每次去调了 */
        Constant.APPName = mContext.getString(R.string.app_name);

        //设置底部的图片样式
        if (getPackageName().equals(AdvertisingKey.releasePackage)) {
            binding.base.setVisibility(View.VISIBLE);
            binding.linearFoot.setVisibility(View.GONE);
        } else {
            binding.base.setVisibility(View.GONE);
            binding.linearFoot.setVisibility(View.VISIBLE);
        }

        //初始化 isdowning 数据
        ConfigManager.Instance().putInt("isdowning", 0);

        if (!onActivityStarted) {
            //保存app启动次数，一定次数后弹出好评送书弹框
            int newInt = ConfigManager.Instance().loadInt("firstSendBookFlag") + 1;
            ConfigManager.Instance().putInt("firstSendBookFlag", newInt);
        }

        //隐私权限和政策的加载
        if (!InfoHelper.getInstance().isHidePrivacy()) {
            android.app.AlertDialog dialog = new SeparatedPrivacyDialog().showDialog(mContext, new SeparatedPrivacyDialog.OnAgreeListener() {
                @Override
                public void onAgree() {
                    initCommonModule();
                    skipFive(true);
                }

                @Override
                public void onNoAgree() {
                    StackUtil.getInstance().finishCur();
                }
            });
            dialog.show();
            binding.adSkip.setClickable(false);
            binding.adSkip.setVisibility(View.VISIBLE);
        } else {
            initCommonModule();

            //设置开屏广告
//            if (AdInitManager.isShowAd() && AdSpreadShowSession.getInstance().isCanShowSplashAd()) {
            // TODO: 2024/8/7 李涛在新概念群租中说：先将当前的三次逻辑去掉展示看下
            if (AdInitManager.isShowAd()) {
                showKaiPingAdShow();
            } else {
                // TODO: 2023/10/25 穆老师在新概念群组中确定前三次不展示广告
                AdSpreadShowSession.getInstance().setCurSplashAdShowCount();

                //设置自有的计时处理
                binding.adSkip.setVisibility(View.VISIBLE);
                binding.adSkip.setOnClickListener(v->{
                    RxTimer.getInstance().cancelTimer(timer_delayJump);
                    skipFive(true);
                });
                RxTimer.getInstance().multiTimerInMain(timer_delayJump, 0, 1000L, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        int downTime = (int) (5 - number);
                        binding.adSkip.setText("跳过(" + downTime + "s)");

                        if (downTime <= 0) {
                            RxTimer.getInstance().cancelTimer(timer_delayJump);
                            skipFive(true);
                        }
                    }
                });
            }
        }

        if (BuildConfig.DEBUG) {
            Debug.stopMethodTracing();
        }

        Constant.IYUBA_CN = InfoHelper.getInstance().getDomain();
        Constant.IYUBA_COM = InfoHelper.getInstance().getShort();

        /****************************************内容审核处理****************************************/
        //判断微课是否显示
        if (ConstantNew.mocVerifyCheck) {
            VerifyShowManager.getInstance().checkMocVerify(this);
        } else {
            AbilityControlManager.getInstance().setLimitMoc(false);
        }

        //判断人教版是否显示-中小学中的内容
        if (ConstantNew.renVerifyCheck) {
            VerifyShowManager.getInstance().checkPepVerify(this);
        } else {
            AbilityControlManager.getInstance().setLimitPep(false);
        }

        //判断视频界面是否显示
        if (ConstantNew.videoVerifyCheck) {
            VerifyShowManager.getInstance().checkVideoVerify(this);
        } else {
            AbilityControlManager.getInstance().setLimitVideo(false);
        }

        //判断小说界面是否显示
        if (ConstantNew.novelVerifyCheck) {
            VerifyShowManager.getInstance().checkNovelVerify(this);
        } else {
            AbilityControlManager.getInstance().setLimitNovel(false);
        }

        //判断加载中小学数据
        if (ConstantNew.juniorVerifyCheck) {
            VerifyShowManager.getInstance().checkJuniorVerify(this);
        } else {
            AbilityControlManager.getInstance().setLimitJunior(false);
        }

        //判断加载新概念数据
        if (ConstantNew.conceptVerifyCheck) {
            VerifyShowManager.getInstance().checkConceptVerify(this);
        } else {
            AbilityControlManager.getInstance().setLimitConcept(false);
        }
    }

    private void jumpWhenCanClick() {
        if (canJumpImmediately && InfoHelper.getInstance().isHidePrivacy()) {
            skipFive(true);
        } else {
            canJumpImmediately = true;
        }
    }

    private void asyncLoad() {
        //初始化部分数据
        ConceptApplication.initSdk();

        //分享初始化
        ShareHelper.initWithOutPlatForm(getApplicationContext(), new String[]{Constant.getMobKey(), Constant.getMobSecret()});

        /*******************隐私模块****************/
        String appId = Constant.APPID;
        String appName = Constant.AppName;

        String usage = PrivacyUtil.getSeparatedProtocolUrl();
        String privacy = PrivacyUtil.getSeparatedSecretUrl();
        IPrivacy.init(getApplicationContext(), usage, privacy);

        /****************视频模块******************/
        IHeadline.setDebug(BuildConfig.DEBUG);
        IHeadline.init(getApplicationContext(), appId, appName, true);
        //设置广告id
        IHeadline.setAdAppId(String.valueOf(AdShowUtil.NetParam.getAdId()));
        //设置广告数据
        IHeadline.setYoudaoStreamId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IHeadline.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,"");
        //开启分享功能
        IHeadline.setEnableShare(true);
        //开启口语圈
        IHeadline.setEnableGoStore(true);
        //开启视频配音
        IHeadline.setEnableSmallVideoTalk(true);

        /*******************其他模块*********************/
        BasicDLDBManager.init(this);
        BasicFavorDBManager.init(this);
        HLDBManager.init(this);

        /*********************训练营********************/
        ITraining.setDebug(BuildConfig.DEBUG);
        ITraining.init(getApplicationContext());
        ITraining.setAppInfo(appId, appName);
        ITraining.setEnableShare(true);
    }

    @SuppressLint("RestrictedApi")
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //这里比较奇怪，微信小程序登录后总是跳转到欢迎界面，这里监测到顶部是欢迎界面时直接finish掉
        //这里是个临时的处理方式，以后需要查找下问题的根源
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (manager.getAppTasks().size() > 0) {
                String topClassName = manager.getAppTasks().get(0).getTaskInfo().topActivity.getClassName();
                String baseClassName = manager.getAppTasks().get(0).getTaskInfo().baseActivity.getClassName();
                if (topClassName.equals(WelcomeActivity.class.getName())
                        && baseClassName.equals(MainFragmentActivity.class.getName())) {
                    Log.d("当前退出的界面1002", "这里有问题");
                    StackUtil.getInstance().finishCur();
                    return;
                }
            }
        }

        if (isClickAd){
            isClickAd = false;
            skipFive(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        RxTimer.getInstance().cancelTimer(timer_delayShow);
        RxTimer.getInstance().cancelTimer(timer_delayJump);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mContext = null;
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

        //关闭广告
        AdSpreadShowManager.getInstance().stopSpreadAd();
    }

    //初始化一些数据(共同模块、mob、友盟等)
    private void initCommonModule() {
        init();
        //升级数据库
        updateDatabase();
    }

    private void init() {
        //异步加载 一些配置
        AsyncConfigThreadPool.run(this::asyncLoad);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //检查堆栈中是否存在这个，不存在则不处理
            if (!StackUtil.getInstance().isExistActivity(WelcomeActivity.class)) {
                Log.d("当前退出的界面1006", "这里进行处理了");
                return;
            }

            switch (msg.what) {
                case 0:
                    try {
                        String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
                        Runtime runtime = Runtime.getRuntime();
                        Process proc = runtime.exec(keyCommand);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    //根据包名处理
                    if (getPackageName().equals("com.iyuba.learnNewEnglish")
                            || getPackageName().equals("com.iyuba.conceptStory")
                            || getPackageName().equals("com.iyuba.concept2")
                            || getPackageName().equals("com.iyuba.englishfm")) {
                        if (ConceptBookChooseManager.getInstance().getBookId() == 0) {
                            ConceptBookChooseManager.getInstance().setBookType(TypeLibrary.BookType.conceptFourUS);
                            ConceptBookChooseManager.getInstance().setBookId(1);
                            ConceptBookChooseManager.getInstance().setBookName("新概念英语一（美音）");
                        }
                    }

                    if (!getPackageName().equals(Constant.package_learnNewEnglish)) {
                        //判断是否展示引导页
                        int guideVersion = StudySettingManager.getInstance().getGuideVersion();
                        if (guideVersion < ConstantNew.guide_version) {
                            //设置跳转
                            StudySettingManager.getInstance().setGuideVersion(ConstantNew.guide_version);
                            //跳转数据
                            Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
                            startActivity(intent);

                            Log.d("当前退出的界面1003", "这里有问题");
                            StackUtil.getInstance().finishCur();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            return;
                        }
                    }

                    if (ConceptBookChooseManager.getInstance().getBookId() == 0) {
                        if (!onActivityStarted) {

//                            Intent intent3 = new Intent(WelcomeActivity.this, BookChooseActivity.class);
//                            intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent3);
                            ConceptBookChooseActivity.start(WelcomeActivity.this, 0);

                            Log.d("当前退出的界面1005", "这里有问题");
                            StackUtil.getInstance().finishCur();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    } else {
                        if (!onActivityStarted) {
                            Intent intent3 = new Intent(WelcomeActivity.this, MainFragmentActivity.class);
                            intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent3);

                            Log.d("当前退出的界面1004", "这里有问题");
                            StackUtil.getInstance().finishCur();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    }
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private void skipFive(boolean flag) {
        //关闭计时器
        RxTimer.getInstance().cancelTimer(timer_delayShow);
        RxTimer.getInstance().cancelTimer(timer_delayJump);

        handler.removeCallbacksAndMessages(null);
        if (InfoHelper.getInstance().isHidePrivacy() && flag) {
            handler.sendEmptyMessage(1);
        }
        binding.adSkip.setClickable(true);
    }

    //显示开屏广告操作
    private void showKaiPingAdShow() {
        binding.adSkip.setClickable(true);
        //延迟1s后显示(这里避免太快导致一些需要判断的接口没来得及加载就无法显示了)
        binding.adSkip.setVisibility(View.INVISIBLE);

        RxTimer.getInstance().timerInMain(timer_delayShow, 1000L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                RxTimer.getInstance().cancelTimer(timer_delayShow);

                //显示开屏广告
                showSplashAd();
            }
        });
    }

    private void updateDatabase() {
        DatabaseUtil.getInstance().updateDatabase(WelcomeActivity.this, new DatabaseUtil.Callback() {
            @Override
            public void scheduleDo() {
//                Intent intent2 = new Intent(WelcomeActivity.this, HelpUseActivity.class);
//                intent2.putExtra("isFirstInfo", 0);
//                startActivity(intent2);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WelcomeBackEvent event) {
        jumpWhenCanClick();
    }

    /****************************开屏广告点击**************************/
    //点击开屏广告结果(暂时保存)
    public void showClickAdResultData(boolean isSuccess, String showMsg) {
        //直接显示信息即可
        ToastUtil.showToast(this, showMsg);

        if (isSuccess) {
            EventBus.getDefault().post(new RefreshUserInfoEvent());
        }
    }

    /*******************************开屏广告显示****************************/
    //开屏广告接口是否完成
    private boolean isSplashAdLoaded = false;
    //是否已经点击了广告
    private boolean isClickAd = false;
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //广告倒计时时间
    private static final int AdDownTime = 5;
    //操作倒计时时间
    private static final int OperateTime = 5;
    //界面数据
    private AdSpreadViewBean spreadViewBean = null;

    //显示开屏广告
    private void showSplashAd() {
        if (spreadViewBean==null){
            spreadViewBean = new AdSpreadViewBean(binding.adImage, binding.adSkip, binding.adTips, binding.adLayout, new AdSpreadShowManager.OnAdSpreadShowListener() {
                @Override
                public void onLoadFinishAd() {
                    isSplashAdLoaded = true;
                    AdSpreadShowManager.getInstance().stopOperateTimer();
                }

                @Override
                public void onAdShow(String adType) {

                }

                @Override
                public void onAdClick(String adType,boolean isJumpByUserClick,String jumpUrl) {
                    if (isJumpByUserClick){
                        if (TextUtils.isEmpty(jumpUrl)){
                            ToastUtil.showToast(WelcomeActivity.this, "暂无内容");
                            return;
                        }

                        //设置点击
                        isClickAd = true;
                        //关闭计时器
                        AdSpreadShowManager.getInstance().stopAdTimer();
                        //跳转显示
                        Intent intent = new Intent();
                        intent.setClass(mContext, WebActivity.class);
                        intent.putExtra("url", jumpUrl);
                        startActivity(intent);
                    }

                    //点击广告获取奖励
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;


                        String fixShowType = AdUploadUtil.Param.AdShowPosition.show_spread;
                        String fixAdType = AdUploadUtil.Util.transShowAdTypeToNetAdType(adType);
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                showClickAdResultData(isSuccess, showMsg);
                            }
                        });
                    }
                }

                @Override
                public void onAdClose(String adType) {
                    //关闭广告
                    AdSpreadShowManager.getInstance().stopSpreadAd();
                    //跳出
                    skipFive(true);
                }

                @Override
                public void onAdError(String adType) {

                }

                @Override
                public void onAdShowTime(boolean isEnd, int lastTime) {
                    if (isEnd){
                        //跳转
                        skipFive(true);
                    }else {
                        //开启广告计时器
                        binding.adSkip.setText("跳过("+lastTime+"s)");
                    }
                }

                @Override
                public void onOperateTime(boolean isEnd, int lastTime) {

                    if (isEnd){
                        //跳转
                        skipFive(true);
                        return;
                    }

                    if (isSplashAdLoaded){
                        AdSpreadShowManager.getInstance().stopOperateTimer();
                        return;
                    }

                    AdLogUtil.showDebug(AdSpreadShowManager.TAG,"操作定时器时间--"+lastTime);
                }
            },AdDownTime,OperateTime);
            AdSpreadShowManager.getInstance().setShowData(this, spreadViewBean);
        }
        AdSpreadShowManager.getInstance().showSpreadAd();
        //重置奖励
        isGetRewardByClickAd = false;
    }
}
