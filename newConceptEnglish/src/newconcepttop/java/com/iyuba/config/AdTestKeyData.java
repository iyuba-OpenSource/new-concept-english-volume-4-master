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
     * com.iyuba.newconcepttop 开屏 0017
     * com.iyuba.newconcepttop Banner 0018
     * com.iyuba.newconcepttop 插屏 0756
     * com.iyuba.newconcepttop 模版 0757
     * com.iyuba.newconcepttop DrawVideo 0758
     * com.iyuba.newconcepttop 激励视频 0759
     *
     * 优量汇
     * com.iyuba.newconcepttop 开屏 0760
     * com.iyuba.newconcepttop Banner 0761
     * com.iyuba.newconcepttop 插屏 0762
     * com.iyuba.newconcepttop 模版 0763
     * com.iyuba.newconcepttop DrawVideo 0764
     * com.iyuba.newconcepttop 激励视频 0765
     *
     * 快手
     * com.iyuba.newconcepttop 开屏 0766
     * com.iyuba.newconcepttop 插屏 0767
     * com.iyuba.newconcepttop 模版 0768
     * com.iyuba.newconcepttop DrawVideo 0769
     * com.iyuba.newconcepttop 激励视频 0770
     *
     * 百度
     * com.iyuba.newconcepttop 开屏 0771
     * com.iyuba.newconcepttop 插屏 0772
     * com.iyuba.newconcepttop 模版 0773
     * com.iyuba.newconcepttop 激励视频 0774
     *
     *
     * 接口数据请在浏览器中查看 http://ai.iyuba.cn/mediatom/server/adplace?placeid=0391
     */

    //key值信息
    interface  KeyData{
        class SpreadAdKey{
            /**
             * 穿山甲 开屏 0017
             * 优量汇 开屏 0760
             * 百度 开屏 0771
             * 快手 开屏 0766
             */
            public static final String spread_youdao = "9755487e03c2ff683be4e2d3218a2f2b";//有道
            public static final String spread_beizi = "0634";//背孜
            public static final String spread_csj = "0017";//穿山甲
            public static final String spread_ylh = "0760";//优量汇
            public static final String spread_baidu = "0771";//百度
            public static final String spread_ks = "0766";//快手
        }

        class BannerAdKey{
            /**
             * 穿山甲 Banner 0018
             * 优量汇 Banner 0761
             */
            public static final String banner_youdao = "230d59b7c0a808d01b7041c2d127da95";//有道
            public static final String banner_csj = "0018";//穿山甲
            public static final String banner_ylh = "0761";//优量汇
        }

        class TemplateAdKey{
            /**
             * 穿山甲 模版 0757
             * 优量汇 模版 0763
             * 百度 模版 0773
             * 快手 模版 0768
             */
            public static final String template_youdao = "3438bae206978fec8995b280c49dae1e";//有道
            public static final String template_csj = "0757";//穿山甲
            public static final String template_ylh = "0763";//优量汇
            public static final String template_baidu = "0773";//百度
            public static final String template_ks = "0768";//快手
            public static final String template_vlion = "";//瑞狮
        }

        class InterstitialAdKey{
            /**
             * 穿山甲 插屏 0756
             * 优量汇 插屏 0762
             * 百度 插屏 0772
             * 快手 插屏 0767
             */
            public static final String interstitial_csj = "0756";//穿山甲
            public static final String interstitial_ylh = "0762";//优量汇
            public static final String interstitial_baidu = "0772";//百度
            public static final String interstitial_ks = "0767";//快手
        }

        class DrawVideoAdKey{
            /**
             * 穿山甲 DrawVideo 0758
             * 优量汇 DrawVideo 0764
             * 快手 DrawVideo 0769
             */
            public static final String drawVideo_csj = "0758";//穿山甲
            public static final String drawVideo_ylh = "0764";//优量汇
            public static final String drawVideo_ks = "0769";//快手
        }

        class IncentiveAdKey{
            /**
             * 穿山甲 激励视频 0759
             * 优量汇 激励视频 0765
             * 百度 激励视频 0774
             * 快手 激励视频 0770
             */
            public static final String incentive_csj = "0759";//穿山甲
            public static final String incentive_ylh = "0765";//优量汇
            public static final String incentive_baidu = "0774";//百度
            public static final String incentive_ks = "0770";//快手
        }
    }
}