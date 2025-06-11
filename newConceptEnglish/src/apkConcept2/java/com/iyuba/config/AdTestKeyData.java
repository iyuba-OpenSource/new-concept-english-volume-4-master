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
     * 倍孜
     * com.iyuba.concept2 开屏 0634
     *
     * 穿山甲
     * com.iyuba.concept2 开屏 0013
     * com.iyuba.concept2 Banner 0014
     * com.iyuba.concept2 插屏 0595
     * com.iyuba.concept2 模版 0596
     * com.iyuba.concept2 DrawVideo 0597
     * com.iyuba.concept2 激励视频 0598
     *
     * 优量汇
     * com.iyuba.concept2 开屏 0599
     * com.iyuba.concept2 Banner 0600
     * com.iyuba.concept2 插屏 0601
     * com.iyuba.concept2 模版 0602
     * com.iyuba.concept2 DrawVideo 0603
     * com.iyuba.concept2 激励视频 0604
     *
     * 快手
     * com.iyuba.concept2 开屏 0605
     * com.iyuba.concept2 插屏 0606
     * com.iyuba.concept2 模版 0607
     * com.iyuba.concept2 DrawVideo 0608
     * com.iyuba.concept2 激励视频 0609
     *
     * 百度
     * com.iyuba.concept2 开屏 0610
     * com.iyuba.concept2 插屏 0611
     * com.iyuba.concept2 模版 0612
     * com.iyuba.concept2 激励视频 0613
     *
     * 接口数据请在浏览器中查看 http://ai.iyuba.cn/mediatom/server/adplace?placeid=0613
     */

    //key值信息
    interface  KeyData{
        class SpreadAdKey{
            /**
             * 倍孜 开屏 0634
             * 穿山甲 开屏 0013
             * 优量汇 开屏 0599
             * 快手 开屏 0605
             * 百度 开屏 0610
             */
            public static final String spread_youdao = "9755487e03c2ff683be4e2d3218a2f2b";//有道
            public static final String spread_beizi = "0634";//倍孜
            public static final String spread_csj = "0013";//穿山甲
            public static final String spread_ylh = "0599";//优量汇
            public static final String spread_ks = "0605";//快手
            public static final String spread_baidu = "0610";//百度
        }

        class BannerAdKey{
            /**
             * 穿山甲 Banner 0014
             * 优量汇 Banner 0600
             */
            public static final String banner_youdao = "230d59b7c0a808d01b7041c2d127da95";//有道
            public static final String banner_csj = "0014";//穿山甲
            public static final String banner_ylh = "0600";//优量汇
        }

        class TemplateAdKey{
            /**
             * 穿山甲 模版 0596
             * 优量汇 模版 0602
             * 快手 模版 0607
             * 百度 模版 0612
             */
            // TODO: 2024/5/28 因为 5542d99e63893312d28d7e49e2b43559 这个key在后台设置的分辨率太低，显示模糊，经过新概念群里说明，修改为34这个
            public static final String template_youdao = "3438bae206978fec8995b280c49dae1e";//有道
            public static final String template_csj = "0596";//穿山甲
            public static final String template_ylh = "0602";//优量汇
            public static final String template_ks = "0607";//快手
            public static final String template_baidu = "0612";//百度
            public static final String template_vlion = "";//瑞狮
        }

        class InterstitialAdKey{
            /**
             * 穿山甲 插屏 0595
             * 优量汇 插屏 0601
             * 快手 插屏 0606
             * 百度 插屏 0611
             */
            public static final String interstitial_csj = "0595";//穿山甲
            public static final String interstitial_ylh = "0601";//优量汇
            public static final String interstitial_ks = "0606";//快手
            public static final String interstitial_baidu = "0611";//百度
        }

        class DrawVideoAdKey{
            /**
             * 穿山甲 DrawVideo 0597
             * 优量汇 DrawVideo 0603
             * 快手 DrawVideo 0608
             */
            public static final String drawVideo_csj = "0597";//穿山甲
            public static final String drawVideo_ylh = "0603";//优量汇
            public static final String drawVideo_ks = "0608";//快手
        }

        class IncentiveAdKey{
            /**
             * 穿山甲 激励视频 0598
             * 优量汇 激励视频 0604
             * 快手 激励视频 0609
             * 百度 激励视频 0613
             */
            public static final String incentive_csj = "0598";//穿山甲
            public static final String incentive_ylh = "0604";//优量汇
            public static final String incentive_ks = "0609";//快手
            public static final String incentive_baidu = "0613";//百度
        }
    }
}
