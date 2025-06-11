package com.iyuba.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.iyuba.module.movies.data.local.Infos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * sharedpreferences 的辅助类
 */
public class InfoHelper {
    private static final String PREF_NAME = "voa_series_app_info";
    private static InfoHelper sInstance;

    public static void init(Context appContext) {
        if (sInstance == null) {
            sInstance = new InfoHelper(appContext);
        }
    }

    public static InfoHelper getInstance() {
        if (sInstance == null) throw new NullPointerException("not init");
        return sInstance;
    }

    private final SharedPreferences mPref;

    private InfoHelper(Context appContext) {
        mPref = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }



    public boolean isHidePrivacy() {
        return mPref.getBoolean("Privacy", false);
    }

    /**
     * 设置隐藏隐私权限和政策
     *
     * @param Privacy
     */
    public void setHidePrivacy(boolean Privacy) {
        mPref.edit().putBoolean("Privacy", Privacy).apply();
    }

    /**
     * 因为分享微博拉不起客户端总是跳转网页，所以暂时选择性隐藏
     * */
    public static boolean showWeiboShare(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long nowDate = new Date().getTime();
        long separatorDate = 0;
        try {
            separatorDate = sf.parse("2020-8-30 10:00:00").getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowDate > separatorDate;
    }


    /**************************设置是否隐藏部分渠道分享功能************************/
    private static final String showSharePlatform = "sharePlatform";
    private static final String showWeiboPlatform = "weiboPlatform";
    private static final String showQQPlatform = "qqPlatform";
    private static final String showWeChatPlatform = "weChatPlatform";

    public boolean openShare(){
        return mPref.getBoolean(showSharePlatform,true);
    }

    public void setShare(boolean setting){
        mPref.edit().putBoolean(showSharePlatform,setting).apply();
    }

    public boolean openWeiboShare(){
        return mPref.getBoolean(showWeiboPlatform,true);
    }

    public void setWeiBoShare(boolean setting){
        mPref.edit().putBoolean(showWeiboPlatform,setting).apply();
    }

    public boolean openQQShare(){
        return mPref.getBoolean(showQQPlatform,true);
    }

    public void setQQShare(boolean setting){
        mPref.edit().putBoolean(showQQPlatform,setting).apply();
    }

    public boolean openWeChatShare(){
        return mPref.getBoolean(showWeChatPlatform,true);
    }

    public void setWeChatShare(boolean setting){
        mPref.edit().putBoolean(showWeChatPlatform,setting).apply();
    }
}
