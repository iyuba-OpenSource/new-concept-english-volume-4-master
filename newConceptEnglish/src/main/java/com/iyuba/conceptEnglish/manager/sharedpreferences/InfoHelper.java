package com.iyuba.conceptEnglish.manager.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * sharedpreferences 的辅助类
 */
public class InfoHelper {
    private static final String PREF_NAME = "voa_series_app_info";
    private static final String short1 = "short1";
    private static final String short2 = "short2";
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

    public boolean has(String key) {
        return mPref.contains(key);
    }


    public String getDeviceId() {
        return mPref.getString(Infos.Keys.DEVICE_ID, Infos.DefaultValue.DEVICE_ID);
    }

    public void putDeviceId(String deviceId) {
        mPref.edit().putString(Infos.Keys.DEVICE_ID, deviceId).apply();
    }

    public String getDomain() {
        return mPref.getString(short1, "iyuba.cn/");
    }

    public InfoHelper putDomain(String domain) {
        mPref.edit().putString(short1, domain).apply();
        return sInstance;
    }
    public String getShort(){
        return mPref.getString(short2, "iyuba.com.cn/");
    }

    public InfoHelper putShort(String s) {
        mPref.edit().putString(short2, s).apply();
        return sInstance;
    }



    public boolean isHidePrivacy() {
        return mPref.getBoolean(Infos.Keys.PRIVACY, Infos.DefaultValue.PRIVACY);
    }

    /**
     * 设置隐藏隐私权限和政策
     *
     * @param Privacy
     */
    public void setHidePrivacy(boolean Privacy) {
        mPref.edit().putBoolean(Infos.Keys.PRIVACY, Privacy).apply();
    }

    /**
     * 单词学习是否自动读音
     *
     * @return
     */
    public boolean getWordIsAuto() {
        return mPref.getBoolean(Infos.Keys.IS_AUTIO_READ, Infos.DefaultValue.IS_AUTIO_READ);
    }

    public void putWordIsAuto(boolean isAuto) {
        mPref.edit().putBoolean(Infos.Keys.IS_AUTIO_READ, isAuto).apply();
    }

    /**
     * 个性化广告展示设置
     * */
    public boolean agreePersonal() {
        return mPref.getBoolean(Infos.Keys.PERSONAL, Infos.DefaultValue.PERSONAL);
    }

    public void changeAgreePersonal(boolean isAuto) {
        mPref.edit().putBoolean(Infos.Keys.PERSONAL, isAuto).apply();
    }
}
