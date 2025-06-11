package com.iyuba.conceptEnglish.util;

import android.content.Context;

import com.iyuba.share.ShareExecutor;
import com.iyuba.share.SharePlatform;
import com.iyuba.share.mob.MobShareExecutor;
import com.mob.MobSDK;

import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareHelper {

    /**
     * example
     *
     * @param appContext
     */
    public static void init(Context appContext) {
        MobSDK.init(appContext, "dbd2a6d8f0a4", "e49c934187152c3a5c2bee8186ea0fdc");

        MobSDK.submitPolicyGrantResult(true, null);

        initPlatforms();

        MobShareExecutor executor = new MobShareExecutor();
        ShareExecutor.getInstance().setRealExecutor(executor);
    }

    /**
     * not init platform
     *
     * @param appContext
     */
    public static void initWithOutPlatForm(Context appContext,String[] mob) {
        MobSDK.init(appContext, mob[0], mob[1]);

        MobSDK.submitPolicyGrantResult(true, null);

        MobShareExecutor executor = new MobShareExecutor();
        ShareExecutor.getInstance().setRealExecutor(executor);
    }

    public static void init(Context appContext, String[] mob, String[] wx, String[] qq, String[] sina) {
        MobSDK.init(appContext, mob[0], mob[1]);

        MobSDK.submitPolicyGrantResult(true, null);

        initPlatforms(wx, qq, sina);

        MobShareExecutor executor = new MobShareExecutor();
        executor.setPlatformHidden(new String[]{SharePlatform.QQ,SharePlatform.QZone});
        ShareExecutor.getInstance().setRealExecutor(executor);
    }

    /**
     * example
     */
    private static void initPlatforms() {
        String wechatAppId = "wxa3808403b18c6c92";
        String wechatAppSecret = "ca26f79b0281cdae0371e8726ba2b59e";
        String qqId = "100737826";
        String qqKey = "15c55278d0843272f848f4b6974f2b18";
        String sinaKey = "1758814933";
        String sinaSecret = "cef407c8d7f2502137fc3e9de7716a5f";
        setDevInfo(QQ.NAME, qqId, qqKey);
        setDevInfo(QZone.NAME, qqId, qqKey);
        setDevInfo(SinaWeibo.NAME, sinaKey, sinaSecret);
        //微博飞雷神
        setDevInfo(Wechat.NAME, wechatAppId, wechatAppSecret);
        setDevInfo(WechatMoments.NAME, wechatAppId, wechatAppSecret);
        setDevInfo(WechatFavorite.NAME, wechatAppId, wechatAppSecret);
    }

    private static void initPlatforms(String[] wx, String[] qq, String[] sina) {
        String wechatAppId = wx[0];
        String wechatAppSecret = wx[1];
        String qqId = qq[0];
        String qqKey = qq[1];
        String sinaKey = sina[0];
        String sinaSecret = sina[1];
        setDevInfo(QQ.NAME, qqId, qqKey);
        setDevInfo(QZone.NAME, qqId, qqKey);
        setDevInfo(SinaWeibo.NAME, sinaKey, sinaSecret);
        //微博飞雷神
        setDevInfo(Wechat.NAME, wechatAppId, wechatAppSecret);
        setDevInfo(WechatMoments.NAME, wechatAppId, wechatAppSecret);
        setDevInfo(WechatFavorite.NAME, wechatAppId, wechatAppSecret);
    }

    private static void setDevInfo(String platform, String str1, String str2) {
        HashMap<String, Object> devInfo = new HashMap<>();
        //微博飞雷神
        if (SinaWeibo.NAME.equals(platform)) {
            devInfo.put("Id", "1");
            devInfo.put("SortId", "1");
            devInfo.put("AppKey", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("RedirectUrl", "http://iyuba.cn");
            devInfo.put("ShareByAppClient", "true");
        } else
            if (QQ.NAME.equals(platform)) {
            devInfo.put("Id", "2");
            devInfo.put("SortId", "2");
            devInfo.put("AppId", str1);
            devInfo.put("AppKey", str2);
            devInfo.put("Enable", "true");
            devInfo.put("ShareByAppClient", "true");
        } else if (QZone.NAME.equals(platform)) {
            devInfo.put("Id", "3");
            devInfo.put("SortId", "3");
            devInfo.put("AppId", str1);
            devInfo.put("AppKey", str2);
            devInfo.put("Enable", "true");
            devInfo.put("ShareByAppClient", "true");
        } else if (Wechat.NAME.equals(platform)) {
            devInfo.put("Id", "4");
            devInfo.put("SortId", "4");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("BypassApproval", "false");
        } else if (WechatMoments.NAME.equals(platform)) {
            devInfo.put("Id", "5");
            devInfo.put("SortId", "5");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("BypassApproval", "false");
        } else if (WechatFavorite.NAME.equals(platform)) {
            devInfo.put("Id", "6");
            devInfo.put("SortId", "6");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
        }
        ShareSDK.setPlatformDevInfo(platform, devInfo);
    }
}
