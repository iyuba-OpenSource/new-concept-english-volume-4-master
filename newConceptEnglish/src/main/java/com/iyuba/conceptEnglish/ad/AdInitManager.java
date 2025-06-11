package com.iyuba.conceptEnglish.ad;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.iyuba.ConstantNew;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.OAIDNewHelper;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.conceptEnglish.util.TimeUtil;
import com.iyuba.configation.Constant;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.youdao.sdk.common.OAIDHelper;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.common.YoudaoSDK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class AdInitManager {

    //设定需要显示的时间
    private static final String showTime = "2024-04-30 00:00:00";

    public void init(Context context, Application application) {
        //关闭有道广告的获取应用列表的功能
        YouDaoAd.getNativeDownloadOptions().setConfirmDialogEnabled(true);
        YouDaoAd.getYouDaoOptions().setAppListEnabled(false);
        YouDaoAd.getYouDaoOptions().setPositionEnabled(false);
        YouDaoAd.getYouDaoOptions().setSdkDownloadApkEnabled(true);
        YouDaoAd.getYouDaoOptions().setDeviceParamsEnabled(false);
        YouDaoAd.getYouDaoOptions().setWifiEnabled(false);
        YouDaoAd.getYouDaoOptions().setCanObtainAndroidId(false);
        YoudaoSDK.init(context);
        //OAID 信息安全 初始化
        initOaid(context);

        //模块广告初始化
        try {
            Date compileDate = TimeUtil.GLOBAL_SDF.parse(showTime);
            AdBlocker.getInstance().setBlockStartDate(compileDate);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 是否 展示广告
     *
     * @return 1:允许展示广告
     * 0：不允许展示广告
     */
    public static boolean isShowAd() {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long nowDate = new Date().getTime();
        long showAdDate = 0;
        try {
            showAdDate = sf.parse(showTime).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //判断显示
        if (nowDate > showAdDate) {
            return true;
        }

        return false;
    }

    private void initOaid(Context context) {
        try {
            //初始化oaid处理
            OAIDHelper.getInstance().init(context);
            //使用新的oaid版本
            OAIDNewHelper oaidNewHelper = new OAIDNewHelper(new OAIDNewHelper.AppIdsUpdater() {
                @Override
                public void onIdData(boolean isSupported, boolean isLimited, String oaid, String vaid, String aaid) {
                    if (isSupported && !isLimited){
                        OAIDHelper.getInstance().setOAID(oaid);
                    }
                }
            },"msaoaidsec", ConstantNew.oaid_pem);
            oaidNewHelper.getDeviceIds(ConceptApplication.getContext(),true,false,false);
        }catch (Exception e){
            //oaid初始化错误
        }
    }

    /**
     * 创建 隐私信息控制 实例，媒体可以自主控制是否提供权限给 广告 sdk使用
     * 强烈建议：为了保证广告的填充率，除oaid项，其他采用默认配置(不实现相关方法)
     *
     * @return
     */
    /*private JadCustomController createCustomController() {
        return new JadCustomController() {
            */

    /**
     * 是否允许SDK主动使用地理位置信息
     *
     * @return true可以获取，false禁止获取。默认为true
     *//*
            public boolean isCanUseLocation() {
                return false;
            }

            *//**
     * 当isCanUseLocation=false时，可传入地理位置信息，sdk使用您传入的地理位置信息
     *
     * @return 地理位置参数
     *//*
            public @Nullable
            JadLocation getJadLocation() {
                return null;
            }

            *//**
     * 是否允许SDK主动使用手机硬件参数，如：imei
     *
     * @return true可以使用，false禁止使用。默认为true
     *//*
            public boolean isCanUsePhoneState() {
                return true;
            }

            *//**
     * 当 isCanUsePhoneState=false 时，
     * 可传入 imei 信息，sdk使用您传入的 imei 信息
     *
     * @return imei信息
     *//*
            public String getDevImei() {
                return null;
            }

            *//**
     * 开发者可以传入oaid
     *
     * @return oaid
     *//*
            @Override
            public String getOaid() {
//                String oaid = mIdSupplier != null ? mIdSupplier.getOAID() : "";
//                Timber.d("%s OAID %s", IAdManager.TAG, oaid);
                return "oaid";
            }
        };
    }*/


    /**
     * 从asset文件读取证书内容
     *
     * @param context
     * @param assetFileName
     * @return 证书字符串
     */
    public static String loadPemFromAssetFile(Context context, String assetFileName) {
        try {
            InputStream is = context.getAssets().open(assetFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            Timber.e("%s loadPemFromAssetFile failed", IAdManager.TAG);
            return "";
        }
    }

    /**
     * Android10.0以上才加载
     */
    private Boolean loadYouDaoFlag() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
