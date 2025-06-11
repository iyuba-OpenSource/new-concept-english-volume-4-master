package com.iyuba.config;

/**
 * 广告测试的key数据
 *
 * 自用数据
 */
public interface AdTestKeyData {

    //key值信息
    interface  KeyData{
        class SpreadAdKey{
            /**
             * 穿山甲 开屏 0071
             * 优量汇 开屏 0377
             * 百度 开屏 0383
             * 快手 开屏 0387
             */
            public static final String spread_youdao = "";//有道
            public static final String spread_beizi = "";//倍孜
            public static final String spread_csj = "0063";//穿山甲
            public static final String spread_ylh = "";//优量汇
            public static final String spread_baidu = "";//百度
            public static final String spread_ks = "";//快手
        }

        class BannerAdKey{
            /**
             * 穿山甲 Banner 0072
             * 优量汇 Banner 0378
             */
            public static final String banner_youdao = "";//有道
            public static final String banner_csj = "0064";//穿山甲
            public static final String banner_ylh = "";//优量汇
        }

        class TemplateAdKey{
            /**
             * 穿山甲 模版 0374
             * 优量汇 模版 0380
             * 百度 模版 0385
             * 快手 模版 0389
             */
            public static final String template_youdao = "";//有道
            public static final String template_csj = "";//穿山甲
            public static final String template_ylh = "";//优量汇
            public static final String template_baidu = "";//百度
            public static final String template_ks = "";//快手
        }

        class InterstitialAdKey{
            /**
             * 穿山甲 插屏 0373
             * 优量汇 插屏 0379
             * 百度 插屏 0384
             * 快手 插屏 0388
             */
            public static final String interstitial_csj = "";//穿山甲
            public static final String interstitial_ylh = "";//优量汇
            public static final String interstitial_baidu = "";//百度
            public static final String interstitial_ks = "";//快手
        }

        class DrawVideoAdKey{
            /**
             * 穿山甲 DrawVideo 0375
             * 优量汇 DrawVideo 0381
             * 快手 DrawVideo 0390
             */
            public static final String drawVideo_csj = "";//穿山甲
            public static final String drawVideo_ylh = "";//优量汇
            public static final String drawVideo_ks = "";//快手
        }

        class IncentiveAdKey{
            /**
             * 穿山甲 激励视频 0376
             * 优量汇 激励视频 0382
             * 百度 激励视频 0386
             * 快手 激励视频 0391
             */
            public static final String incentive_csj = "";//穿山甲
            public static final String incentive_ylh = "";//优量汇
            public static final String incentive_baidu = "";//百度
            public static final String incentive_ks = "";//快手
        }
    }

    //相关的工具
    interface Util{
        //获取接口中的广告位置数据

        //获取接口中的广告类型数据
    }
}
