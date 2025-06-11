package com.iyuba.core.discover.activity;

import static com.iyuba.share.SharePlatform.SinaWeibo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.LibRequestFactory;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.share.SharePlatform;

import java.lang.reflect.Field;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordPdfExport {

    private Context mContext;
    private CustomDialog dialog;




    public WordPdfExport(Context mContext) {
        this.mContext = mContext;
        dialog =  WaittingDialog.showDialog(mContext);
    }


    public void getPDFResult(String userId, int pageNumber, int pageCounts) {
        dialog.show();
        LibRequestFactory.getWordPdfAPI().getWordPdf(userId, pageNumber, pageCounts).enqueue(new Callback<WordPdfBean>() {
            @Override
            public void onResponse(Call<WordPdfBean> call, Response<WordPdfBean> response) {
                WordPdfBean bean = response.body();
                dialog.dismiss();
                if (1 == bean.getResult()) {
                    createDialog(bean.getFilePath());
                } else {
                    ToastUtil.showToast(mContext, "导出PDF失败");
                }
            }

            @Override
            public void onFailure(Call<WordPdfBean> call, Throwable t) {

                try {
                    dialog.dismiss();
                    ToastUtil.showToast(mContext, "导出PDF失败");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createDialog(final String path) {

        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(path);

        AlertDialog alertDialog = null;
        //针对临时的包名处理
        if (!InfoHelper.getInstance().openShare()){
            alertDialog = new AlertDialog.Builder(mContext).setTitle("PDF链接生成成功!").setMessage(path + "\t(链接已复制)").setNegativeButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(path);
                    intent.setData(uri);
                    mContext.startActivity(intent);
                }
            }).create();
        }else {
            alertDialog = new AlertDialog.Builder(mContext).setTitle("PDF链接生成成功!").setMessage(path + "\t(链接已复制)").setNegativeButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(path);
                    intent.setData(uri);
                    mContext.startActivity(intent);
                }
            }).setPositiveButton("发送", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sendPDF(path);

                }
            }).create();
        }
        alertDialog.show();
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);

            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextColor(Color.BLUE);

            //通过反射修改message字体大小和颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextSize(14);


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    void sendPDF(String pdfurl) {
        //ShareSDK.initSDK(this);
        //微博飞雷神
        Platform weibo = ShareSDK.getPlatform(SinaWeibo);
        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);

        String imageUrl = "http://static3."+Constant.IYUBA_CN+"android/images/New%20concept%20English/New%20concept%20English.png";
        String text = "-爱语吧-";
        String siteUrl = pdfurl;

        OnekeyShare oks = new OnekeyShare();
        if (!InfoHelper.showWeiboShare()){
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
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("单词本PDF");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        //根据包名进行判断(样式)
        if ("com.iyuba.conceptStory".equals(mContext.getPackageName())){
            text = "爱语言";
        }
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");
        // imageUrl是Web图片路径，sina需要开通权限
        oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("这款应用" + Constant.APPName + "真的很不错啊~推荐！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(Constant.APPName);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.e("okCallbackonError", "onError" + arg2.toString());
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {

            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(mContext);
    }

}
