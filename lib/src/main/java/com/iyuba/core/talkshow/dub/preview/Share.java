package com.iyuba.core.talkshow.dub.preview;

import static com.iyuba.share.SharePlatform.SinaWeibo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.data.model.DownLoadJFResult;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.remote.IntegralService;
import com.iyuba.lib.R;
import com.iyuba.share.SharePlatform;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Share {

    public static String MY_DUBBING_PREFIX = "http://voa."+Constant.IYUBA_CN+"voa/talkShowShare.jsp?shuoshuoId=";

    public static void prepareDubbingMessage(Context context, TalkLesson voa, int backId, String user,
                                             IntegralService service, int uid) {
        String descUrl = getMyDubbingUrl(backId);

        shareMessage(context, voa.Pic, getText(context, voa), descUrl, "播音员：" + user + " " + voa.TitleCn, getListener(context, service, uid, backId));
    }

    public static void prepareVideoMessage(Context context, TalkLesson voa, IntegralService service, int uid) {
        //String url = VoaMediaUtil.getVideoUrl(voa.category(),voa.voaId());
        //String url = "http://"+Constant.staticStr+Constant.IYUBA_CN+"video/voa/321/" + voa.Id + ".mp4";;
        String url = "http://m."+Constant.IYUBA_CN+"voaS/playPY.jsp?id=" + voa.Id + "&apptype=newConceptTalk";

        shareMessageMiniProgram(context, voa.Pic, getText(context, voa), url, voa.TitleCn, getListener(context, service, uid, voa.voaId()), "" + voa.voaId(), voa.series);
    }

    public static void shareMessage(Context context, String imageUrl, String text, String url,
                                    String title, PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        if (!InfoHelper.showWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo);
        }

        if ("com.iyuba.conceptStory".equals(context.getPackageName())){
            oks.addHiddenPlatform(SinaWeibo);
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
            oks.addHiddenPlatform(SinaWeibo);
        }

        //微博飞雷神
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(text);
        oks.setImageUrl(imageUrl);
        oks.setUrl(url);
        oks.setComment(MessageFormat.format(
                context.getString(R.string.app_share_content), Constant.AppName));
        oks.setSite(Constant.AppName);
        oks.setSiteUrl(url);
        oks.setSilent(true);
        oks.setLatitude((float) 0);
        oks.setLongitude((float) 0);

        oks.setCallback(listener);
        oks.show(context);
    }

    public static void shareMessageMiniProgram(Context context, String imageUrl, String text, String url,
                                               String title, PlatformActionListener listener, final String voaId, final String series) {
        OnekeyShare oks = new OnekeyShare();
        if (!InfoHelper.showWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo);
        }

        if ("com.iyuba.conceptStory".equals(context.getPackageName())){
            oks.addHiddenPlatform(SinaWeibo);
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
            oks.addHiddenPlatform(SinaWeibo);
        }

        //微博飞雷神
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(text);
        oks.setImageUrl(imageUrl);
        oks.setUrl(url);
        oks.setComment(MessageFormat.format(
                context.getString(R.string.app_share_content), Constant.AppName));
        oks.setSite(Constant.AppName);
        oks.setSiteUrl(url);
        oks.setSilent(true);
        oks.setLatitude((float) 0);
        oks.setLongitude((float) 0);

        if ("com.iyuba.concept2".equals(context.getPackageName())){
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                @Override
                public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                    if ("Wechat".equals(platform.getName())/* || "WechatFavorite".equals(platform.getName()) || "WechatMoments".equals(platform.getName())*/) {//微信
                        paramsToShare.setShareType(Platform.SHARE_WXMINIPROGRAM);
                        paramsToShare.setWxUserName("gh_a8c17ad593be");
                        paramsToShare.setWxMiniProgramType(0);//0正式、1测试、2体验
                        Log.d("MiniProgramPath", "pages/detail/detail?voaid=" + voaId);
                        paramsToShare.setWxPath("pages/detail/detail?voaid=" + voaId + "&bookid=" + series);
                    }
                }
            });
        }

        oks.setCallback(listener);
        oks.show(context);
    }

    public static String getMyDubbingUrl(int backId) {
        return MY_DUBBING_PREFIX + backId+"&apptype=newConceptTalk";
    }

    public static PlatformActionListener getListener(final Context mContext, final IntegralService service, final int uid, final int id) {
        return new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.e("share", "onComplete");

                String srid = "";
                if (platform.getName().equals("QQ")
                        || platform.getName().equals("Wechat")
                        || platform.getName().equals("WechatFavorite")) {
                    srid = "7";
                } else if (platform.getName().equals("QZone")
                        || platform.getName().equals("WechatMoments")
                        || platform.getName().equals("SinaWeibo")
                        || platform.getName().equals("TencentWeibo")) {
                    srid = "19";
                }

                service.integral(srid, 1, getTime(), uid, Constant.APP_ID, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<DownLoadJFResult>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Log.e("onError", "分享积分失败");
                                if (mContext != null) {
                                    Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onNext(DownLoadJFResult result) {
                                Log.e("onNext", "分享积分成功---");
                                if (mContext != null) {
                                    if (result != null && "200" .equals(result.getResult())) {
                                        Toast.makeText(mContext, "分享成功" + result.getAddcredit() + "分，当前积分为："
                                                        + result.getTotalcredit() + "分",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("share", "onError");
                Toast.makeText(mContext, "分享失败",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("share", "onCancel");
                Toast.makeText(mContext, "分享已取消",
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        //设置日期格式
        return "1234567890" + df.format(new Date());// new Date()为获取当前系统时间
    }

    private static String getText(Context context, TalkLesson voa) {
        return MessageFormat.format(context.getString(R.string.video_share_content),
                voa.TitleCn, voa.Title, voa.DescCn);
    }

}
