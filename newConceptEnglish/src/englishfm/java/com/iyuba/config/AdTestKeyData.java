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
     * com.iyuba.englishfm 开屏 0015
     * com.iyuba.englishfm Banner 0016
     * com.iyuba.englishfm 插屏 0505
     * com.iyuba.englishfm 模版 0506
     * com.iyuba.englishfm DrawVideo 0507
     * com.iyuba.englishfm 激励视频 0508
     *
     * 优量汇
     * com.iyuba.englishfm 开屏 0509
     * com.iyuba.englishfm Banner 0510
     * com.iyuba.englishfm 插屏 0511
     * com.iyuba.englishfm 模版 0512
     * com.iyuba.englishfm DrawVideo 0513
     * com.iyuba.englishfm 激励视频 0514
     *
     * 百度
     * com.iyuba.englishfm 开屏 0515
     * com.iyuba.englishfm 插屏 0516
     * com.iyuba.englishfm 模版 0517
     * com.iyuba.englishfm 激励视频 0518
     *
     * 快手
     * com.iyuba.englishfm 开屏 0519
     * com.iyuba.englishfm 插屏 0520
     * com.iyuba.englishfm 模版 0521
     * com.iyuba.englishfm DrawVideo 0522
     * com.iyuba.englishfm 激励视频 0523
     *
     * 接口数据请在浏览器中查看 http://ai.iyuba.cn/mediatom/server/adplace?placeid=0523
     */

    //key值信息
    interface  KeyData{
        class SpreadAdKey{
            /**
             * 穿山甲：0015
             * 优量汇：0509
             * 百度：0515
             * 快手：0519
             */
            public static final String spread_youdao = "9755487e03c2ff683be4e2d3218a2f2b";//有道
            public static final String spread_beizi = "0634";//倍孜
            public static final String spread_csj = "0015";//穿山甲
            public static final String spread_ylh = "0509";//优量汇
            public static final String spread_baidu = "0515";//百度
            public static final String spread_ks = "0519";//快手
        }

        class BannerAdKey{
            /**
             * 穿山甲：0016
             * 优量汇：0510
             */
            public static final String banner_youdao = "230d59b7c0a808d01b7041c2d127da95";//有道
            public static final String banner_csj = "0016";//穿山甲
            public static final String banner_ylh = "0510";//优量汇
        }

        class TemplateAdKey{
            /**
             * 穿山甲：0506
             * 优量汇：0512
             * 百度：0517
             * 快手：0521
             */
            public static final String template_youdao = "3438bae206978fec8995b280c49dae1e";//有道-5542d99e63893312d28d7e49e2b43559(原来的，不清晰)
            public static final String template_csj = "0506";//穿山甲
            public static final String template_ylh = "0512";//优量汇
            public static final String template_baidu = "0517";//百度
            public static final String template_ks = "0521";//快手
            public static final String template_vlion = "";//瑞狮
        }

        class InterstitialAdKey{
            /**
             * 穿山甲：0505
             * 优量汇：0511
             * 百度：0516
             * 快手：0520
             */
            public static final String interstitial_csj = "0505";//穿山甲
            public static final String interstitial_ylh = "0511";//优量汇
            public static final String interstitial_baidu = "0516";//百度
            public static final String interstitial_ks = "0520";//快手
        }

        class DrawVideoAdKey{
            /**
             * 穿山甲：0507
             * 优量汇：0513
             * 快手：0522
             */
            public static final String drawVideo_csj = "0507";//穿山甲
            public static final String drawVideo_ylh = "0513";//优量汇
            public static final String drawVideo_ks = "0522";//快手
        }

        class IncentiveAdKey{
            /**
             * 穿山甲：0508
             * 优量汇：0514
             * 百度：0518
             * 快手：0523
             */
            public static final String incentive_csj = "0508";//穿山甲
            public static final String incentive_ylh = "0514";//优量汇
            public static final String incentive_baidu = "0518";//百度
            public static final String incentive_ks = "0523";//快手
        }
    }
}
