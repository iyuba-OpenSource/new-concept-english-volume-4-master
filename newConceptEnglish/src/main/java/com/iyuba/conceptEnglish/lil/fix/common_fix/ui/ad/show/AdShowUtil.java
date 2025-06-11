package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show;

import android.app.Activity;
import android.text.TextUtils;

import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.configation.Constant;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import java.util.Locale;

/**
 * 广告展示的工具数据
 */
public interface AdShowUtil {

    //参数
    class NetParam{
        //新的接口版本使用的appId
        public static int getAdId(){
            String channel = ChannelReaderUtil.getChannel(ResUtil.getInstance().getContext());
            if (channel.toLowerCase(Locale.CHINA).equals("huawei")){
                return Constant.APP_ID*10+2;
            }

            return Constant.APP_ID*10+1;
        };

        //信息流广告-起始位置
        public static final int SteamAd_startIndex = 3;
        //信息流广告-间隔位置
        public static final int SteamAd_intervalIndex = 5;

        /**
         * 广告接口的flag参数
         */
        public interface Flag{
            int net_spreadFlag = 1;//开屏广告、插屏、激励
            int net_templateFlag = 2;//信息流广告
            int net_bannerFlag = 4;//banner广告
        }

        /**
         * 广告的显示类型参数
         * youdao：youdao
         * web：就是我们的连接，和之前的逻辑一样，不过现在没返回了
         * ads2：穿山甲的
         * ads3：百度的
         * ads4：优量汇的
         * ads5：快手的
         * ads6：瑞狮
         */
        public interface AdType{
            String show_web = "web";//web类型
            String show_youdao = "youdao";//有道类型
            String show_ads1 = "ads1";//倍孜类型
            String show_ads2 = "ads2";//穿山甲类型
            String show_ads3 = "ads3";//百度类型
            String show_ads4 = "ads4";//优量汇类型
            String show_ads5 = "ads5";//快手类型
            String show_ads6 = "ads6";//瑞狮类型
            String show_other = "other";//其他类型
            String show_exception = "exception";//异常类型
        }
    }

    //方法
    class Util{
        //判断界面是否存在
        public static boolean isPageExist(Activity context) {
            if (context == null || context.isFinishing() || context.isDestroyed()) {
                return false;
            }

            return true;
        }

        //广告名称显示
        public static String showAdName(String showType){
            if (TextUtils.isEmpty(showType)){
                return showType;
            }

            switch (showType){
                case NetParam.AdType.show_youdao:
                    return "有道广告";
                case NetParam.AdType.show_web:
                    return "web广告";
                case NetParam.AdType.show_ads1:
                    return "倍孜广告";
                case NetParam.AdType.show_ads2:
                    return "穿山甲广告";
                case NetParam.AdType.show_ads3:
                    return "百度广告";
                case NetParam.AdType.show_ads4:
                    return "优量汇广告";
                case NetParam.AdType.show_ads5:
                    return "快手广告";
                case NetParam.AdType.show_ads6:
                    return "瑞狮广告";
                case NetParam.AdType.show_exception:
                    return "异常类型广告";
                default:
                    return showType;
            }
        }
    }
}
