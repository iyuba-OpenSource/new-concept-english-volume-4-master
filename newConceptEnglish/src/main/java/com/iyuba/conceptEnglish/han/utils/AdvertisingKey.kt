package com.iyuba.conceptEnglish.han.utils

/**
苏州爱语吧科技有限公司
@Date:  2022/10/20
@Author:  han rong cheng
 */
object AdvertisingKey {
    /**
     *
     * 新概念英语全四册  Z6638849421
    广告位ID：J9963622982
    广告位名称：开屏广告
    广告类型：开屏广告

    广告位ID：J6296263662
    广告位名称：插屏广告
    广告类型：插屏广告

    广告位ID：J5408462465
    广告位名称：信息流广告
    广告类型：信息流

    广告位ID：J6450306188
    广告位名称：banner
    广告类型：Banner
    -------------------------------------------------------------------
     */

    /**
     * 新概念英语全四册-englishfm  Z0210330697
    广告位ID：J7286547604
    广告位名称：开屏广告
    广告类型：开屏广告

    广告位ID：J7132226090
    广告位名称：插屏广告
    广告类型：插屏广告

    广告位ID：J7139107832
    广告位名称：banner
    广告类型：Banner

    广告位ID：J5595268686
    广告位名称：左图右文-信息流广告
    广告类型：信息流
    -------------------------------------------------------------------
     * */

    /**
     * 新概念微课  Z7034203855
    广告位ID：J7137566946
    广告位名称：开屏广告
    广告类型：开屏广告

    广告位ID：J8953900139
    广告位名称：插屏广告
    广告类型：插屏广告

    广告位ID：J9840675580
    广告位名称：banner
    广告类型：Banner

    广告位ID：J1730770422
    广告位名称：左图右文-信息流广告
    广告类型：信息流
    -------------------------------------------------------------------
     * */
    /**
     * 新概念英语-nce  Z6465857083
    广告位ID：J3520690989
    广告位名称：开屏广告
    广告类型：开屏广告

    广告位ID：J5496151483
    广告位名称：插屏广告
    广告类型：插屏广告

    广告位ID：J1367832482
    广告位名称：banner
    广告类型：Banner

    广告位ID：J8731060494
    广告位名称：左图右文-信息流广告
    广告类型：信息流
    -------------------------------------------------------------------
    小学英语  Z7494812928
    包名：com.iyuba.talkshow.childenglish

    广告位ID：J7463162090
    广告位名称：开屏广告
    广告类型：开屏广告

    广告位ID：J9054296540
    广告位名称：插屏广告
    广告类型：插屏广告

    广告位ID：J8857508007
    广告位名称：左图右文-信息流广告
    广告类型：信息流

    广告位ID：J7375987377
    广告位名称：banner
    广告类型：Banner
     * */
    val initKey by lazy {
        when(packageName){
            xiaomiPackage->"Z0210330697"
            releasePackage->"Z6638849421"
            smallClassPackage->"Z7034203855"
            vivoPackage->"Z6465857083"
            else->""
        }
    }

    val splashKey by lazy {
        when(packageName){
            xiaomiPackage->"J7286547604"
            releasePackage->"J9963622982"
            smallClassPackage->"J7137566946"
            vivoPackage->"J3520690989"
            else->""
        }
    }

    val insertSplashKey by lazy {
        when(packageName){
            xiaomiPackage->"J7132226090"
            releasePackage->"J6296263662"
            smallClassPackage->"J8953900139"
            vivoPackage->"J5496151483"
            else->""
        }
    }


    val bannerKey by lazy {
        when(packageName){
            xiaomiPackage->"J7139107832"
            releasePackage->"J6450306188"
            smallClassPackage->"J9840675580"
            vivoPackage->"J1367832482"
            else->""
        }
    }

    val infoFlowKey by lazy {
        when(packageName){
            xiaomiPackage->"J5595268686"
            releasePackage->"J5408462465"
            smallClassPackage->"J1730770422"
            vivoPackage->"J8731060494"
            else->""
        }
    }

    /**
     * 新概念英语全四册-安卓-APP正式ID：21020
     * 新概念英语全四册-安卓-广告位正式ID：开屏105098
     * */
    const val beiZiId="105098"
    const val beiZiInitId="21020"


    /**
     * 爱语吧广告
     */
    const val web = "web";
    /**
     * 有道广告
     * */
    const val youdao="youdao"
    /**
     * 倍孜广告
     * */
    const val ads1="ads1"
    /**
     * 创见广告
     * */
    const val ads2="ads2"
    /**
     * 倍孜广告
     * */
    const val ads3="ads3"
    /**
     * 头条穿山甲
     * */
    const val ads4="ads4"
    /**
     * 快手
     * */
    const val ads5="ads5"

    /**
     * 小米包名
     * */
    const val xiaomiPackage="com.iyuba.englishfm"
    /**
     * (正式&普通)包名
     * */
    const val releasePackage="com.iyuba.concept2"

    /**
     *  新概念英语极速版/新概念微课
     * */
    const val smallClassPackage="com.iyuba.newconcepttop"

    /**
     * 单vivo的
     * */
    const val vivoPackage="com.iyuba.nce"


    var packageName=""


}