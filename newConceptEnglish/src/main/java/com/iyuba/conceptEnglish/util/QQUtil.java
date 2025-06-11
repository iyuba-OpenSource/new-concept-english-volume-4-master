package com.iyuba.conceptEnglish.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.iyuba.core.common.util.ToastUtil;


/**
 * BrandUtil
 * qq群号 ---管理
 *
 * @author wayne
 * @date 2017/11/18
 */
public class QQUtil {

    private static final String BRAND_HUAWEI = "huawei";
    private static final String BRAND_XIAOMI = "xiaomi";
    private static final String BRAND_MEIZU = "meizu";
    private static final String BRAND_VIVO = "vivo";
    private static final String BRAND_OPPO = "oppo";
    private static final String BRAND_SAMSUNG = "samsung";
    private static final String BRAND_GIONEE = "gionee";
    private static final String BRAND_360 = "360";
    private static final String BRAND_OTHER = "android";

    private static String brandName;

    public static String getBrandName() {
        if (TextUtils.isEmpty(brandName)) {
            brandName = setBrandName();
        }
        return brandName;
    }

    public static String getBrandChinese() {
        String brand = getBrandName();
        switch (brand) {
            case BRAND_HUAWEI:
                return "华为";
            case BRAND_VIVO:
                return "Vivo";
            case BRAND_OPPO:
                return "Oppo";
            case BRAND_XIAOMI:
                return "小米";
            case BRAND_SAMSUNG:
                return "三星";
            case BRAND_GIONEE:
                return "金立";
            case BRAND_MEIZU:
                return "魅族";
            case BRAND_360:
                return "360";
            default:
                return "安卓";
        }
    }

    private static String setBrandName() {
        String brand = Build.MANUFACTURER.trim().toLowerCase();
        if (brand.contains("huawei") || brand.contains("honor")
                || brand.contains("nova") || brand.contains("mate")) {
            return BRAND_HUAWEI;
        }
        if (brand.contains("xiaomi")) {
            return BRAND_XIAOMI;
        }
        if (brand.contains("vivo")) {
            return BRAND_VIVO;
        }
        if (brand.contains("oppo")) {
            return BRAND_OPPO;
        }
        if (brand.contains("samsung")) {
            return BRAND_SAMSUNG;
        }
        if (brand.contains("meizu")) {
            return BRAND_MEIZU;
        }
        // 金立
        if (brand.contains("gionee")) {
            return BRAND_GIONEE;
        }
        if (brand.contains("360") || brand.contains("qiku")
                || brand.contains("qiho") || brand.contains("qihu")) {
            return BRAND_360;
        }
        return brand;
    }


    public static String getQQGroupNumber(String brand) {
        switch (brand) {
            case BRAND_HUAWEI:
                return "264454431";
            case BRAND_VIVO:
                return "691047086";
            case BRAND_OPPO:
                return "805145562";
            case BRAND_XIAOMI:
                return "499939472";
            case BRAND_SAMSUNG:
                return "639727892";
            case BRAND_GIONEE:
                return "621392974";
            case BRAND_MEIZU:
                return "745011534";
            case BRAND_360:
                return "625355797";
            default:
                return "482516431";
        }
    }

    public static String getQQGroupKey(String brandName) {
        switch (brandName) {
            case BRAND_HUAWEI:
                return "Pr7ZFSmHnnbKXQ_JgSE-2PrC-kmlDvpS";
            case BRAND_VIVO:
                return "Ayo5DzH2g9KYSeZnnHec8LGaLM3X1HzP";
            case BRAND_OPPO:
                return "krH1ukBZbCHEGsixIMvRa4w3NhO7_eWt";
            case BRAND_XIAOMI:
                return "0G7KUtkpo1mtHXaWNRyolEJoWeeT1_B9";
            case BRAND_SAMSUNG:
                return "4LU-47yf_P510zgmdp98miJtDx366Ty5";
            case BRAND_GIONEE:
                return "FrBn9MMar2QxpP2svZDHq2LrbiWqvtNE";
            case BRAND_MEIZU:
                return "zS82Y-4zaPVChkpun-HLOnNpKcf_h2_3";
            case BRAND_360:
                return "0yHQOAWPGPOPacORm2BXdOblJZvlzeLw";
            default:
                return "DEbZdKF9fjFpsxAzdcEQ5rhzHz9WWCFW";
        }
    }


    public static void startQQGroup(Context context, String qqKey) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?" + "url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + qqKey));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            e.printStackTrace();
            ToastUtil.showLongToast(context, "您的设备尚未安装QQ客户端");
        }
    }
}
