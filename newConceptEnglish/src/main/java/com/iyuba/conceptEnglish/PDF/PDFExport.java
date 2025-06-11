package com.iyuba.conceptEnglish.PDF;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.han.utils.ExpandKt;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.util.ShareUtils;
import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * PDF 导出
 */
public class PDFExport {

    private Context mContext;
    private PDFApi pdfApi;
    private String types,title, voaId;
    private boolean isEnglish = true;

    private UpdateScoreApi updateScoreApi;


    public PDFExport(Context mContext) {
        this.mContext = mContext;
        pdfApi = ApiRetrofit.getInstance().getPdfApi();
        updateScoreApi = ApiRetrofit.getInstance().getUpdateScoreApi();
    }

    /**
     * @param title
     * @param voaId
     */
    public void getPDFData(String bookType,final String title, final String voaId) {
        if (!UserInfoManager.getInstance().isLogin()){
            ExpandKt.goSomeAction(mContext,"");
            return;
        }
        this.title = title;
        this.voaId = voaId;
        String[] strings;
        strings = new String[]{"导出英文", "导出中英双语"};
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("请选择需要导出的PDF形式")
                .setItems(strings, (dialog, which) -> {
                    isEnglish = (which == 0);
                    dialog.dismiss();
                    if (UserInfoManager.getInstance().isVip()) {
                        getPDFInfo(bookType);
                    } else {
                        onCheckCreditThenDownloadPassed(bookType);
                    }
                }).create();
        alertDialog.show();
    }

    private void getPDFInfo(String bookType) {
        String url;
        if (isEnglish) {
            url = PDFApi.URL_EG;
        } else {
            url = PDFApi.URL;
        }
        pdfApi.getPDFInfo(url, PDFApi.PDF_TYPE, voaId).enqueue(new Callback<PDFApi.PDFBean>() {
            @Override
            public void onResponse(Call<PDFApi.PDFBean> call, Response<PDFApi.PDFBean> response) {
                PDFApi.PDFBean bean = response.body();

                if (bean != null && !TextUtils.isEmpty(bean.path)) {
                    setdownPDFDialog(bookType,Constant.PDF_PREFIX + bean.path);
                } else {
                    try {
                        ToastUtil.showToast(mContext, "生成PDF失败，请稍后再试");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PDFApi.PDFBean> call, Throwable t) {
                try {
                    ToastUtil.showToast(mContext, "生成PDF失败，请稍后再试");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setdownPDFDialog(String bookType,final String path) {

        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(path);

        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle("PDF链接生成成功!")
                .setMessage(path + "\t(链接已复制)").setNegativeButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(path);
                        intent.setData(uri);
                        mContext.startActivity(intent);
                    }
                });

        if (InfoHelper.getInstance().openShare()){
            builder.setPositiveButton("发送", (dialog, which) -> sendPDF(bookType,path));
        }
        alertDialog = builder.create();
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

    private void sendPDF(String bookType,String path) {

        String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
        String content = "即时导出中英课文PDF";
        String siteUrl = path;

        int id;
        if (bookType.equals(TypeLibrary.BookType.conceptFourUS))
            id = Integer.parseInt(voaId) % 1000;
        else
            id = Integer.parseInt(voaId) / 10 % 1000;

        String realTitle = "Lesson " + id + " " + title;

        PlatformActionListener platformActionListener = new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        };
        new ShareUtils().showShare(mContext, imageUrl, siteUrl, realTitle, content, platformActionListener,null);
    }


    //扣积分
    public void onCheckCreditThenDownloadPassed(String bookType) {

        new AlertDialog.Builder(mContext)
                .setTitle("提示:")
                .setMessage("生成PDF每篇文章将消耗20积分,是否生成? 开通VIP后即可免积分下载。")
                .setPositiveButton("生成", (dialog, which) -> {
                    ductScore(bookType);
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

    }


    private void ductScore(String bookType) {
        final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);//设置日期格式

        try {
            updateScoreApi.ductPointsForPDF(UpdateScoreApi.URL, UpdateScoreApi.SRID, UpdateScoreApi.MOBILE,
                    Base64.encodeToString(URLEncoder.encode(df.format(new Date(System.currentTimeMillis())), "UTF-8").getBytes(), Base64.DEFAULT),
                    String.valueOf(UserInfoManager.getInstance().getUserId()), Constant.APPID, voaId).enqueue(new Callback<UpdateScoreBean>() {
                @Override
                public void onResponse(Call<UpdateScoreBean> call, Response<UpdateScoreBean> response) {

                    UpdateScoreBean bean = response.body();

                    if (bean != null) {
                        if ("200".equals(bean.result)) {
                            //成功
                            getPDFInfo(bookType);
                        } else if ("205".equals(bean.result)) {
                            //积分不足
                            ToastUtil.showToast(mContext, "剩余积分不足");
                        } else if ("406".equals(bean.result)) {
                            //未登录
                            ExpandKt.goSomeAction(mContext,"导出PDF");

                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdateScoreBean> call, Throwable t) {
                    ToastUtil.showToast(mContext, "扣除积分失败，请稍后再试");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*******************中小学内容*******************/
    public void getJuniorPDFData(boolean isMiddle,String types,final String title, final String voaId) {
        if (!UserInfoManager.getInstance().isLogin()){
            ExpandKt.goSomeAction(mContext,"");
            return;
        }

        this.types = types;
        this.title = title;
        this.voaId = voaId;

        String[] strings = new String[]{"导出英文", "导出中英双语"};
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("请选择需要导出的PDF形式")
                .setItems(strings, (dialog, which) -> {
                    //设置是否为英文
                    isEnglish = (which == 0);
                    dialog.dismiss();
                    //操作
                    if (UserInfoManager.getInstance().isVip()) {
                        getJuniorPDFInfo(types,isMiddle);
                    } else {
                        onCheckCreditThenDownloadPassed(types);
                    }
                }).create();
        alertDialog.show();
    }

    private void getJuniorPDFInfo(String bookType,boolean isMiddle) {
        String url = "http://apps."+Constant.IYUBA_CN+"iyuba/getVoapdfFile_new.jsp";;
        String type = "";
        if (isMiddle){
            type = "juniorenglish";
        }else {
            type = "primaryenglish";
        }
        int pdfType = 0;
        if (isEnglish){
            pdfType = 1;
        }
        pdfApi.getJuniorPDFInfo(url,type,voaId,pdfType).enqueue(new Callback<PDFApi.PDFBean>() {
            @Override
            public void onResponse(Call<PDFApi.PDFBean> call, Response<PDFApi.PDFBean> response) {
                PDFApi.PDFBean bean = response.body();

                if (bean != null && !TextUtils.isEmpty(bean.path)) {
                    setdownPDFDialog(bookType,Constant.PDF_PREFIX + bean.path);
                } else {
                    try {
                        ToastUtil.showToast(mContext, "生成PDF失败，请稍后再试");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PDFApi.PDFBean> call, Throwable t) {
                try {
                    ToastUtil.showToast(mContext, "生成PDF失败，请稍后再试");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
