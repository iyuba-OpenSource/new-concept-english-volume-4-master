package com.iyuba.conceptEnglish.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.listener.RequestCallBack;
import com.iyuba.conceptEnglish.protocol.AddCreditsRequest;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareUtils {

    private static final String TAG = "ShareUtils";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (mContext == null) {
                return;
            }
            if (i != 19 && i != 49) {
                return;
            }
            if (UserInfoManager.getInstance().isLogin()) {
                RequestCallBack local1 = new RequestCallBack() {
                    public void requestResult(Request request) {
                        AddCreditsRequest addCredit = (AddCreditsRequest) request;
                        if (addCredit.isShareFirstlySuccess()) {
                            String msg = "分享成功，增加了" + addCredit.addCredit + "积分，共有" + addCredit.totalCredit + "积分";
                            CustomToast.showToast(mContext, msg, 3000);
                        } else if (addCredit.isShareRepeatlySuccess()) {
                            CustomToast.showToast(mContext, "分享成功", 3000);
                        }
                    }
                };

                int uid = UserInfoManager.getInstance().getUserId();
                AddCreditsRequest rq = new AddCreditsRequest(uid, voaId, msg.what, local1);
                RequestQueue queue = Volley.newRequestQueue(mContext);
                queue.add(rq);


            }
        }
    };
    private Context mContext;
    public PlatformActionListener platformActionListener = new PlatformActionListener() {
        public void onCancel(Platform paramAnonymousPlatform, int paramAnonymousInt) {
        }

        public void onComplete(Platform platform, int paramAnonymousInt, HashMap<String, Object> paramAnonymousHashMap) {
            Message message = new Message();
            message.obj = platform.getName();
            if ((!platform.getName().equals("QQ")) && (!platform.getName().equals("Wechat")) && (!platform.getName().equals("WechatFavorite"))) {
                if ((platform.getName().equals("QZone")) || (platform.getName().equals("WechatMoments")) || (platform.getName().equals("SinaWeibo")) || (platform.getName().equals("TencentWeibo"))) {
                    message.what = 19;
                }
            } else {
                message.what = 49;
            }
            handler.sendMessage(message);
        }

        public void onError(Platform paramAnonymousPlatform, int paramAnonymousInt, Throwable paramAnonymousThrowable) {
        }
    };

    public PlatformActionListener defaultPlatformActionListener = new PlatformActionListener() {
        public void onCancel(Platform paramAnonymousPlatform, int paramAnonymousInt) {
        }

        public void onComplete(Platform platform, int paramAnonymousInt, HashMap<String, Object> paramAnonymousHashMap) {
        }

        public void onError(Platform paramAnonymousPlatform, int paramAnonymousInt, Throwable paramAnonymousThrowable) {
        }
    };

    private int voaId;

    public void setMContext(Context paramContext) {
        this.mContext = paramContext;
    }

    public void setVoaId(int paramInt) {
        this.voaId = paramInt;
    }

    public void showShare(Context mContext, String imagePath, String siteUrl, String title,
                          String content, PlatformActionListener paramPlatformActionListener,
                          ShareContentCustomizeCallback callback) {
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.removeAccount(true);
        //微博飞雷神
        ShareSDK.removeCookieOnAuthorize(true);

        OnekeyShare oks = new OnekeyShare();
        //这里直接关闭微博分享
        if (!InfoHelper.showWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }

        //设置分享是否显示
        if (!InfoHelper.getInstance().openQQShare()){
            oks.addHiddenPlatform(QQ.NAME);
            oks.addHiddenPlatform(QZone.NAME);
        }
        if (!InfoHelper.getInstance().openWeChatShare()){
            oks.addHiddenPlatform(Wechat.NAME);
            oks.addHiddenPlatform(WechatMoments.NAME);
            oks.addHiddenPlatform(WechatFavorite.NAME);
        }
        if (!InfoHelper.getInstance().openWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }

        //微博飞雷神

        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);

        LoadIconUtil.loadCommonIcon(mContext);
//        String path = Constant.iconAddr;
        String path = LoadIconUtil.getAppLogoPath(mContext);
        oks.setImagePath(path);

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mContext.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        oks.setCallback(platformActionListener);
        oks.disableSSOWhenAuthorize();
        if (callback != null) {
            oks.setShareContentCustomizeCallback(callback);
        }
        // 启动分享GUI
        oks.show(mContext);
    }

    public void showShareMiniProgram(Context mContext, String voaId, String picUrl,String siteUrl, String title, String content,
                                     PlatformActionListener paramPlatformActionListener) {

        OnekeyShare oks = new OnekeyShare();
        if (!InfoHelper.showWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }

        //设置分享是否显示
        if (!InfoHelper.getInstance().openQQShare()){
            oks.addHiddenPlatform(QQ.NAME);
            oks.addHiddenPlatform(QZone.NAME);
        }
        if (!InfoHelper.getInstance().openWeChatShare()){
            oks.addHiddenPlatform(Wechat.NAME);
            oks.addHiddenPlatform(WechatMoments.NAME);
            oks.addHiddenPlatform(WechatFavorite.NAME);
        }
        if (!InfoHelper.getInstance().openWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }

        //微博飞雷神
        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);

        //将app的icon保存到Constant.iconAddr 这个路径下
//        LoadIconUtil.loadIcon(mContext);
//        String path = Constant.iconAddr;
//        oks.setImagePath(path);
        oks.setImageUrl(picUrl);

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mContext.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);

//        if ("com.iyuba.concept2".equals(mContext.getPackageName())) {
//
//            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//                @Override
//                public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
//                    if (Wechat.NAME.equals(platform.getName())/* || "WechatFavorite".equals(platform.getName()) || "WechatMoments".equals(platform.getName())*/) {//微信
//                        paramsToShare.setShareType(Platform.SHARE_WXMINIPROGRAM);
//                        paramsToShare.setImagePath(Constant.iconWxMiniProgramAddr);
//                        paramsToShare.setWxUserName("gh_a8c17ad593be");
//                        paramsToShare.setWxMiniProgramType(0);//0正式、1测试、2体验
//                        Log.d("MiniProgramPath", "pages/detail/detail?voaid=" + voaId);
//                        String bookId;
//                        if ("321".equals(voaId.substring(0, 3))){
//                            //青少版
//                            VoaWordOp voaWordOp=new VoaWordOp(mContext);
//                            bookId=voaWordOp.getBookidByVoaid(voaId)+"";
//                        }else {
//                            //全四册
//                            bookId=voaId.substring(0, 1);
//                        }
////                        Log.d(TAG, "bookId: ______"+bookId);
////                        Log.d(TAG, "setWxPath: ______"+"pages/detail/detail?voaid=" + voaId + "&bookid=" + voaId.substring(0, 1));
//                        paramsToShare.setWxPath("pages/detail/detail?voaid=" + voaId + "&bookid=" + voaId.substring(0, 1));
////                        paramsToShare.setWxPath("pages/detail/detail?voaid=10010&bookid=1");
////                        paramsToShare.setWxPath("pages/detail/detail?voaid=1001&bookid=1");
//                    }
//                }
//            });
//        }

        oks.setCallback(platformActionListener);
//        oks.disableSSOWhenAuthorize();
        // 启动分享GUI
        oks.show(mContext);
    }
}
