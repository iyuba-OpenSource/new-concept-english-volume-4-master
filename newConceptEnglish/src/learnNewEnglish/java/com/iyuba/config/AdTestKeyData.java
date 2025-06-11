package com.iyuba.config;

/**
 * 广告测试的key数据
 *
 * 自用数据
 */
public interface AdTestKeyData {

    /**
     * 广告位key配置如下
     *
     * 穿山甲
     * com.iyuba.learnNewEnglish 开屏 0071
     * com.iyuba.learnNewEnglish Banner 0072
     * com.iyuba.learnNewEnglish 插屏 0373
     * com.iyuba.learnNewEnglish 模版 0374
     * com.iyuba.learnNewEnglish DrawVideo 0375
     * com.iyuba.learnNewEnglish 激励视频 0376
     *
     * 优量汇
     * com.iyuba.learnNewEnglish 开屏 0377
     * com.iyuba.learnNewEnglish Banner 0378
     * com.iyuba.learnNewEnglish 插屏 0379
     * com.iyuba.learnNewEnglish 模版 0380
     * com.iyuba.learnNewEnglish DrawVideo 0381
     * com.iyuba.learnNewEnglish 激励视频 0382
     *
     * 百度
     * com.iyuba.learnNewEnglish 开屏 0383
     * com.iyuba.learnNewEnglish 插屏 0384
     * com.iyuba.learnNewEnglish 模版 0385
     * com.iyuba.learnNewEnglish 激励视频 0386
     *
     * 快手
     * com.iyuba.learnNewEnglish 开屏 0387
     * com.iyuba.learnNewEnglish 插屏 0388
     * com.iyuba.learnNewEnglish 模版 0389
     * com.iyuba.learnNewEnglish DrawVideo 0390
     * com.iyuba.learnNewEnglish 激励视频 0391
     *
     *
     * 接口数据请在浏览器中查看 http://ai.iyuba.cn/mediatom/server/adplace?placeid=0391
     */

    //key值信息
    interface  KeyData{
        class SpreadAdKey{
            /**
             * 穿山甲 开屏 0071
             * 优量汇 开屏 0377
             * 百度 开屏 0383
             * 快手 开屏 0387
             */
            public static final String spread_youdao = "9755487e03c2ff683be4e2d3218a2f2b";//有道
            public static final String spread_beizi = "0634";//倍孜[展姐说：倍孜的广告key是没有绑定包名的]
            public static final String spread_csj = "0071";//穿山甲
            public static final String spread_ylh = "0377";//优量汇
            public static final String spread_baidu = "0383";//百度
            public static final String spread_ks = "0387";//快手
        }

        class BannerAdKey{
            /**
             * 穿山甲 Banner 0072
             * 优量汇 Banner 0378
             */
            public static final String banner_youdao = "230d59b7c0a808d01b7041c2d127da95";//有道
            public static final String banner_csj = "0072";//穿山甲
            public static final String banner_ylh = "0378";//优量汇
        }

        class TemplateAdKey{
            /**
             * 穿山甲 模版 0374
             * 优量汇 模版 0380
             * 百度 模版 0385
             * 快手 模版 0389
             */
            public static final String template_youdao = "3438bae206978fec8995b280c49dae1e";//有道
            public static final String template_csj = "0374";//穿山甲
            public static final String template_ylh = "0380";//优量汇
            public static final String template_baidu = "0385";//百度
            public static final String template_ks = "0389";//快手
            public static final String template_vlion = "";//瑞狮
        }

        class InterstitialAdKey{
            /**
             * 穿山甲 插屏 0373
             * 优量汇 插屏 0379
             * 百度 插屏 0384
             * 快手 插屏 0388
             */
            public static final String interstitial_csj = "0373";//穿山甲
            public static final String interstitial_ylh = "0379";//优量汇
            public static final String interstitial_baidu = "0384";//百度
            public static final String interstitial_ks = "0388";//快手
        }

        class DrawVideoAdKey{
            /**
             * 穿山甲 DrawVideo 0375
             * 优量汇 DrawVideo 0381
             * 快手 DrawVideo 0390
             */
            public static final String drawVideo_csj = "0375";//穿山甲
            public static final String drawVideo_ylh = "0381";//优量汇
            public static final String drawVideo_ks = "0390";//快手
        }

        class IncentiveAdKey{
            /**
             * 穿山甲 激励视频 0376
             * 优量汇 激励视频 0382
             * 百度 激励视频 0386
             * 快手 激励视频 0391
             */
            public static final String incentive_csj = "0376";//穿山甲
            public static final String incentive_ylh = "0382";//优量汇
            public static final String incentive_baidu = "0386";//百度
            public static final String incentive_ks = "0391";//快手
        }
    }

    //相关的工具
    interface Util{
        //获取接口中的广告位置数据

        //获取接口中的广告类型数据
    }
}
