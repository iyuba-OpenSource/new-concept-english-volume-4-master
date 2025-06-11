package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload;

import android.text.TextUtils;

import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;

/**
 * 广告上传的工具数据
 */
public interface AdUploadUtil {

    //参数
    class Param{
        //广告显示位置
        public interface AdShowPosition{
            String show_spread = "spread";//开屏
            String show_banner = "banner";//banner
            String show_template = "template";//信息流
            String show_rewardVideo = "rewardVideo";//激励视频
            String show_interstitial = "interstitial";//插屏
            String show_drawVideo = "drawVideo";//draw
        }

        //广告显示类型
        public interface AdShowType{
            String ad_youdao = "youdao";//有道广告
            String ad_baidu = "baidu";//百度广告
            String ad_ylh = "ylh";//优量汇广告
            String ad_csj = "csj";//穿山甲广告
            String ad_ks = "ks";//快手广告

            String ad_beizi = "beizi";//倍孜广告
            String ad_rs = "rs";//瑞狮广告
        }
    }

    //工具
    class Util{
        //获取接口中的显示位置数据
        //ad_space：广告位，banner，信息流，开屏，激励，插屏，draw，顺序1-6
        public static int getNetShowType(String showType){
            switch (showType){
                case Param.AdShowPosition.show_banner:
                    return 1;
                case Param.AdShowPosition.show_template:
                    return 2;
                case Param.AdShowPosition.show_spread:
                    return 3;
                case Param.AdShowPosition.show_rewardVideo:
                    return 4;
                case Param.AdShowPosition.show_interstitial:
                    return 5;
                case Param.AdShowPosition.show_drawVideo:
                    return 6;
                default:
                    return -1;
            }
        }

        //获取接口中的广告类型数据-测试广告
        //platform：平台，百度，流量汇，穿山甲，快手，顺序1-4，0是有道
        public static int getNetAdType(String adType){
            if (TextUtils.isEmpty(adType)){
                return -1;
            }

            switch (adType){
                case Param.AdShowType.ad_youdao:
                    return 0;
                case Param.AdShowType.ad_baidu:
                    return 1;
                case Param.AdShowType.ad_ylh:
                    return 2;
                case Param.AdShowType.ad_csj:
                    return 3;
                case Param.AdShowType.ad_ks:
                    return 4;
                default:
                    return -1;
            }
        }

        //将显示广告的类型转换为上传广告的类型
        public static String transShowAdTypeToNetAdType(String showAdType){
            if (TextUtils.isEmpty(showAdType)){
                return null;
            }

            /**
             * ads2：穿山甲的
             * ads3：百度的
             * ads4：优量汇的
             * ads5：快手的
             * ads6：瑞狮
             */
            switch (showAdType){
                case AdShowUtil.NetParam.AdType.show_youdao:
                    return Param.AdShowType.ad_youdao;
                case AdShowUtil.NetParam.AdType.show_ads2:
                    return Param.AdShowType.ad_csj;
                case AdShowUtil.NetParam.AdType.show_ads3:
                    return Param.AdShowType.ad_baidu;
                case AdShowUtil.NetParam.AdType.show_ads4:
                    return Param.AdShowType.ad_ylh;
                case AdShowUtil.NetParam.AdType.show_ads5:
                    return Param.AdShowType.ad_ks;
                case AdShowUtil.NetParam.AdType.show_ads6:
                    return Param.AdShowType.ad_rs;
                default:
                    return null;
            }
        }
    }
}
