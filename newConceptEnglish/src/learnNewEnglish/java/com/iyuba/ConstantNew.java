package com.iyuba;


import com.iyuba.core.common.activity.login.LoginType;

public class ConstantNew  {

	public static final String PACK_NAME= "com.iyuba.learnNewEnglish";

	//升级需要的类型
	public static final String PACKAGE_TYPE = "learnNewEnglish";

	//引导界面版本，如果版本大于缓存中的数据，则显示
	public static final int guide_version = 1;


	/****微课显示控制******/
	//是否需要控制显示
	public static boolean mocVerifyCheck = true;

	//获取微课的控制渠道id
	public static int getMocLimitChannelId(String channel){
		switch (channel){
			case "huawei":
				return 8601;
			case "xiaomi":
				return 8602;
			case "oppo":
				return 8603;
			case "vivo":
				return 8604;
		}
		return 8600;
	}

	/********人教版内容控制*****/
	//是否需要控制显示
	public static boolean renVerifyCheck = true;

	//获取人教版的控制渠道id
	public static int getRenLimitChannelId(String channel){
		switch (channel){
			case "huawei":
				return 8611;
			case "xiaomi":
				return 8612;
			case "oppo":
				return 8613;
			case "vivo":
				return 8614;
		}
		return 8610;
	}

	/****视频显示控制******/
	//是否需要控制显示
	public static boolean videoVerifyCheck = true;

	//获取视频的控制渠道id
	public static int getVideoLimitChannelId(String channel){
		switch (channel){
			case "huawei":
				return 8621;
			case "xiaomi":
				return 8622;
			case "oppo":
				return 8623;
			case "vivo":
				return 8624;
		}
		return 8620;
	}

	/****小说显示控制******/
	//是否需要控制显示
	public static boolean novelVerifyCheck = true;

	//获取小说的控制渠道id
	public static int getNovelLimitChannelId(String channel){
		switch (channel){
			case "huawei":
				return 8631;
			case "xiaomi":
				return 8632;
			case "oppo":
				return 8633;
			case "vivo":
				return 8634;
		}
		return 8630;
	}

	/****中小学显示控制******/
	//是否需要控制显示
	public static boolean juniorVerifyCheck = true;

	//获取中小学的控制渠道id
	public static int getJuniorLimitChannelId(String channel){
		switch (channel){
			case "huawei":
				return 8641;
			case "xiaomi":
				return 8642;
			case "oppo":
				return 8643;
			case "vivo":
				return 8644;
		}
		return 8640;
	}

	/****新概念显示控制******/
	//是否需要控制显示
	public static boolean conceptVerifyCheck = false;

	//获取新概念的控制渠道id
	public static int getConceptLimitChannelId(String channel){
		return 0;
	}

	/***************************************展示内容***********************************/
	//是否展示新概念内容
	public static final boolean isShowConcept = true;
	//是否展示中小学内容
	public static final boolean isShowJunior = false;
	//是否展示小说内容
	public static final boolean isShowNovel = true;

	/*****************************************登录功能*******************************/
	//登录功能的类型
	public static final String loginType = LoginType.loginByVerify;

	/*********广告*********/
	//com.iyuba.learnNewEnglish 开屏 0071
	//com.iyuba.learnNewEnglish banner 0072
	public static final String SDK_SPLASH_CODE = "0071";
	public static final String SDK_BANNER_CODE = "0072";

	/*********支付**********/
	//是否开启微信支付
	public static boolean openWxPay = false;
	//微信支付的key
	public static final String WX_KAY = "";//也是小程序的key

	/**********特殊操作*******/
	//是否处于应用宝的专版
	public static final boolean isTencentStore = false;

	/**********分享功能*******/
	//是否开启分享功能
	public static final boolean openShare = true;
	//是否开启qq分享功能
	public static final boolean showQQShare = true;
	//是否开启微信分享功能
	public static final boolean showWeChatShare = true;
	//是否开启微博分享功能
	public static final boolean showWeiboShare = false;


	/****oaid升级****/
	//oaid的证书名称
	public static final String oaid_pem = "com.iyuba.conceptStory.cert.pem";

	/*****友盟的key****/
	public static final String umeng_key = "541b94aefd98c52eb3014729";
}
