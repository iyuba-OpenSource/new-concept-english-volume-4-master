package com.iyuba.conceptEnglish.util;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.activity.WelcomeActivity;
import com.iyuba.conceptEnglish.ad.AdInitManager;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.OAIDNewHelper;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.manager.sharedpreferences.InfoHelper;
import com.iyuba.conceptEnglish.sqlite.db.TalkShowDBManager;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginType;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.lil.util.ResLibUtil;
import com.iyuba.headlinelibrary.data.local.HeadlineInfoHelper;
import com.iyuba.headlinelibrary.data.local.db.HLDBManager;
import com.jn.yyz.practise.PractiseInit;
import com.mob.MobSDK;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.yd.saas.ydsdk.manager.YdConfig;

import java.util.Date;
//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

/**
 *
 */
public class ConceptApplication extends CrashApplication {
    private long startTime, endTime;
    private int mFinalCount;
    public static int courseIndex=0;
    public static int currentEvalLength=0;

    @Override
    public void onCreate() {
        super.onCreate();
        //设置oaid前置操作
        OAIDNewHelper.loadLibrary();

        //预初始化友盟
        String channel = ChannelReaderUtil.getChannel(this);
        UMConfigure.preInit(ConceptApplication.getInstance(), ConstantNew.umeng_key, channel);

        //分支-初始化数据
        ResUtil.getInstance().setApplication(this);
        ResLibUtil.getInstance().setApplication(this);
        //数据库链接测试用
//        SQLiteStudioService.instance().start(ResUtil.getInstance().getApplication());

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());
        // 单词数据库 管理辅助工具 初始化
        WordChildDBManager.init(this);
        //口语秀数据库初始化
        TalkShowDBManager.init(this);
        //享学初始化
//        XUtil.init(this);
        // sharedpreferences辅助类初始化
        InfoHelper.init(getApplicationContext());

        // TODO: 2025/3/14 这个好像暂时没用，关闭掉
//        LitePal.initialize(this);

        HeadlineInfoHelper.init(this);
        listenActivityLifecycle();
        HLDBManager.init(this);

        //设置包名
        Constant.inflatePackageName(getPackageName());
        //设置登录类型
        LoginType.getInstance().setCurLoginType(ConstantNew.loginType);

//        redirectCompany();
        AdvertisingKey.INSTANCE.setPackageName(getPackageName());

        /*****************根据配置预先设置分享功能********************/
        com.iyuba.core.InfoHelper.getInstance().setShare(ConstantNew.openShare);
        com.iyuba.core.InfoHelper.getInstance().setQQShare(ConstantNew.showQQShare);
        com.iyuba.core.InfoHelper.getInstance().setWeChatShare(ConstantNew.showWeChatShare);
        com.iyuba.core.InfoHelper.getInstance().setWeiBoShare(ConstantNew.showWeiboShare);
    }

    /**
     * 注册应用生命周期的回调方法
     */
    private void listenActivityLifecycle(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                //当应用启动时候的逻辑

                mFinalCount++;
                //如果mFinalCount ==1，说明是从后台到前台
                Log.e("onActivityStarted", mFinalCount + "");
                if (mFinalCount == 1) {
                    //说明从后台回到了前台
                    endTime = new Date().getTime();
                    Log.e("onActivityStarted", new Date().toString() + "");

                    long timeload = endTime - startTime;
                    Log.e("onActivityStarted", timeload + "--" + startTime);
                    /**
                     * 在这里判断是否跳转到 WelcomeActivity，
                     * startTime != 0 再次的启动
                     * timeload / 1000 >= 180 两次启动的间隔大于三分钟
                     * 满足以上两条的时候加载 WelcomeActivity 页面
                     */
                    if (startTime != 0 && timeload / 1000 >= 180&&AdInitManager.isShowAd()) {
                        Log.e("onActivityStarted", "===============" + activity.toString());

                        Intent intent = new Intent(activity, WelcomeActivity.class);
                        intent.putExtra("onActivityStarted", true);
                        activity.startActivity(intent);
                    }

                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                //当应用结束时候的逻辑，记录 startTime

                mFinalCount--;
                //如果mFinalCount ==0，说明是前台到后台
                Log.e("onActivityStopped", mFinalCount + "");
                if (mFinalCount == 0) {
                    //说明从前台回到了后台

                    startTime = new Date().getTime();
                    Log.e("onActivityStopped", new Date().toString() + "");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }



    public static void initSdk(){
        //-----------------------------//
        MobSDK.submitPolicyGrantResult(true);
        MobSDK.init(ConceptApplication.getInstance(), Constant.getMobKey(), Constant.getMobSecret());

        //初始化薛达的新版练习题sdk
        PractiseInit.init(Constant.APP_ID);

        //广告初始化
        AdInitManager adInitManager=new AdInitManager();
        adInitManager.init(ConceptApplication.getInstance(),ConceptApplication.getInstance());

        //友盟统计加载(暂时关闭)
        /*if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
            UMConfigure.setEncryptEnabled(false);
        } else {
            //友盟是否打印log
            UMConfigure.setLogEnabled(false);
            //友盟加密
            UMConfigure.setEncryptEnabled(true);
        }*/

        //firstSendBookFlag： 启动次数

        //更新友盟初始化逻辑
        /*String channel = ChannelReaderUtil.getChannel(ConceptApplication.getInstance());
        if (ConfigManager.Instance().loadInt("firstSendBookFlag") < 1) {
            UMConfigure.preInit(ConceptApplication.getInstance(), ConstantNew.umeng_key, channel);
        } else {
            //友盟初始化
            UMConfigure.init(ConceptApplication.getInstance(), ConstantNew.umeng_key,channel,UMConfigure.DEVICE_TYPE_PHONE, null);
            //mob 收集模式的配置
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        }*/
        String channel = ChannelReaderUtil.getChannel(ConceptApplication.getInstance());
        //友盟初始化
        UMConfigure.init(ConceptApplication.getInstance(), ConstantNew.umeng_key,channel,UMConfigure.DEVICE_TYPE_PHONE, null);
        //mob 收集模式的配置
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        //设置广告初始化
        YdConfig.getInstance().init(ConceptApplication.getInstance(),Constant.APPID);
    }
}