package com.iyuba.core.common.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseContactRequest;
import com.iyuba.core.common.protocol.BaseContactResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseResponse;
import com.iyuba.core.common.protocol.GetQQServiceRequest;
import com.iyuba.core.common.protocol.GetQQServiceResponse;

import java.util.List;

/**
 * BrandUtil
 * qq群号 ---管理
 *
 * @author wayne
 * @date 2017/11/18
 */
public class BrandUtil {

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

    private static String getBrandName() {
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

//    String[] vivo_qq = {"vivo","691047086","Ayo5DzH2g9KYSeZnnHec8LGaLM3X1HzP"};
//    String[] oppo_qq = {"oppo","805145562","krH1ukBZbCHEGsixIMvRa4w3NhO7_eWt"};
//    String[] meizu_qq = {"meizu","745011534","zS82Y-4zaPVChkpun-HLOnNpKcf_h2_3"};
//    String[] huawei_qq = {"huawei","264454431","Pr7ZFSmHnnbKXQ_JgSE-2PrC-kmlDvpS"};
//    String[] gionee_qq = {"gionee","621392974","FrBn9MMar2QxpP2svZDHq2LrbiWqvtNE"};
//    String[] xiaomi_qq = {"xiaomi","499939472","0G7KUtkpo1mtHXaWNRyolEJoWeeT1_B9"};
//    String[] iyu360_qq = {"360","625355797","aL0KeUgRFfl1TJ6INW_W0YLhGoPW5bD1"};
//    String[] samsung_qq = {"samsung","639727892","0FCy3TsMqO8FItsXsFR98-ePA8f6C4Nn"};
//    String[] android_qq = {"android","482516431","DEbZdKF9fjFpsxAzdcEQ5rhzHz9WWCFW"};
    private static String getQQGroupNumber(String brand) {
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


    public static String getQQGroupNumber(Context context) {
        return (String) SP.get(context, "sp_qq_group_number", getQQGroupNumber(getBrandName()));
    }

    public static String getQQGroupKey(Context context) {
        return (String) SP.get(context, "sp_qq_group_key", getQQGroupKey(getBrandName()));
    }

    public static String getServicQQEdeitor(Context context) {
        return (String) SP.get(context, "sp_qq_service_deitor", "445167605");
    }
    public static String getServicQQTechnician(Context context) {
        return (String) SP.get(context, "sp_qq_service_technician", "787532674");
    }
    public static String getServicQQManager(Context context) {
        return (String) SP.get(context, "sp_qq_service_manager", "572828703");
    }

    public static void requestQQGroupNumber(final Context context) {
        String url = "http://m." + Constant.IYUBA_CN + "m_login/getQQGroup.jsp?type=" + getBrandName();

        ExeProtocol.exe(
                new BaseContactRequest(url),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        BaseContactResponse response = (BaseContactResponse) bhr;

                        try {
                            QQGroupBean bean = new Gson().fromJson(response.jsonObjectRoot.toString(), QQGroupBean.class);
                            if ("true".equals(bean.message)) {
                                SP.put(context, "sp_qq_group_number", bean.QQ);
                                SP.put(context, "sp_qq_group_key", bean.key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void error() {

                    }
                });
    }


    public static void requestServiceQQNumber(final Context context) {
        String url = "http://"+Constant.userSpeech+"japanapi/getJpQQ.jsp?appid=222";

        ExeProtocol.exe(
                new GetQQServiceRequest(url),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        GetQQServiceResponse response = (GetQQServiceResponse) bhr;

                        try {
                            ServiceQQ bean = new Gson().fromJson(response.jsonObjectRoot.toString(), ServiceQQ.class);
                            if ("200".equals(bean.result)) {
                                SP.put(context, "sp_qq_service_deitor", bean.data.get(0).editor);
                                SP.put(context, "sp_qq_service_technician", bean.data.get(0).technician);
                                SP.put(context, "sp_qq_service_manager", bean.data.get(0).manager);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void error() {

                    }
                });
    }

    class QQGroupBean {

        /**
         * message : true
         * QQ : 433075910
         * key : lr0jfBh_9Ly0S3iUPUnCSNhAV8UkiQRI
         */

        public String message;
        public String QQ;
        public String key;
    }

    class ServiceQQ {
        public String result;
        public List<ServiceQQSun> data;
    }

    class ServiceQQSun {

        public String editor; //编辑qq
        public String technician;//开发qq
        public String manager;//投诉qq
    }
}
