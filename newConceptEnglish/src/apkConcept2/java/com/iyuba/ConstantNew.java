package com.iyuba;


import com.iyuba.core.common.activity.login.LoginType;

public class ConstantNew  {

	public static final String PACK_NAME= "com.iyuba.concept2";

	//升级需要的类型
	public static final String PACKAGE_TYPE = "concept2";

	//引导界面版本，如果版本大于缓存中的数据，则显示
	public static final int guide_version = 0;

	/*****************************审核内容控制**********************************/
	/****微课显示控制******/
	//是否需要控制显示
	public static boolean mocVerifyCheck = true;

	//获取微课的控制渠道id
	public static int getMocLimitChannelId(String channel){
		switch (channel){
			case "huawei":
				return 8001;
			case "xiaomi":
				return 8002;
			case "oppo":
				return 8003;
			case "vivo":
				return 8004;
		}
		return 8000;
	}

	/********人教版内容控制*****/
	//是否需要控制显示
	public static boolean renVerifyCheck = false;

	//获取人教版的控制渠道id
	public static int getRenLimitChannelId(String channel){
		switch (channel){
			case "huawei":
				return 8011;
			case "xiaomi":
				return 8012;
			case "oppo":
				return 8013;
			case "vivo":
				return 8014;
		}
		return 8010;
	}

	/****视频显示控制******/
	//是否需要控制显示
	public static boolean videoVerifyCheck = false;

	//获取视频的控制渠道id
	public static int getVideoLimitChannelId(String channel){
		return 0;
	}

	/****小说显示控制******/
	//是否需要控制显示
	public static boolean novelVerifyCheck = false;

	//获取小说的控制渠道id
	public static int getNovelLimitChannelId(String channel){
		return 0;
	}

	/****中小学显示控制******/
	//是否需要控制显示
	public static boolean juniorVerifyCheck = false;

	//获取中小学的控制渠道id
	public static int getJuniorLimitChannelId(String channel){
		return 0;
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

	/**********分享功能*******/
	//是否开启分享功能
	public static final boolean openShare = true;
	//是否开启qq分享功能
	public static final boolean showQQShare = true;
	//是否开启微信分享功能
	public static final boolean showWeChatShare = true;
	//是否开启微博分享功能
	public static final boolean showWeiboShare = true;

	/****oaid升级****/
	//oaid的证书名称
	public static final String oaid_pem = "com.iyuba.concept2.cert.pem";

	/*****友盟的key****/
	public static final String umeng_key = "541b94aefd98c52eb3014729";
}
