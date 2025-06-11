//package com.iyuba.conceptEnglish.util;
//
//import android.content.Context;
//
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.core.common.manager.AccountManager;
//
//import timber.log.Timber;
//
///**
// * 账号相关判断
// */
//public class AccountUtil {
//
//
//    /**
//     * 判断是否为临时账号
//     */
//    public static boolean isTemporary(Context context) {
//        return AccountManager.Instance(context).islinshi;
//    }
//
//
//    /**
//     * 判断是否登录
//     */
//    public static boolean isLogin() {
//        if (ConfigManager.Instance().getUserId() == null) {
//            Timber.d("uid is null");
//        } else {
//            String a = ConfigManager.Instance().getUserId();
//            Timber.d(a);
//        }
//        return !"".equals(ConfigManager.Instance().getUserId())
//                &&!"0".equals(ConfigManager.Instance().getUserId())
//                && ConfigManager.Instance().getUserId() != null;
//    }
//
//    /**
//     * 判断是否为VIP
//     */
//    public static boolean isVip() {
//        return ConfigManager.Instance().getIsVip() > 0;
//    }
//}
