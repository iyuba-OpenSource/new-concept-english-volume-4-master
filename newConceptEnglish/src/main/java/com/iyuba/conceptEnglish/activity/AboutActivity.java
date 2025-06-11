package com.iyuba.conceptEnglish.activity;

import com.iyuba.conceptEnglish.BuildConfig;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.evalFix.EvalFixBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.evalFix.EvalFixDialog;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.PractiseShowActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.gson.GsonUtil;
import com.iyuba.conceptEnglish.listener.AppUpdateCallBack;
import com.iyuba.conceptEnglish.listener.DownLoadFailCallBack;
import com.iyuba.conceptEnglish.manager.VersionManager;
import com.iyuba.conceptEnglish.util.FailOpera;
import com.iyuba.conceptEnglish.util.FileDownProcessBar;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.youdao.sdk.common.OAIDHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于界面
 */

public class AboutActivity extends BasisActivity implements AppUpdateCallBack {

    private int REQUEST_OK = 10010;
    private View background;
    private Button backBtn;
    private View url;
    private View appUpdateBtn;
    private View appNewImg;
    private TextView currVersionCode;
    private String version_code;
    private String appUpdateUrl;// 版本号
    private ProgressBar progressBar_downLoad; // 下载进度条
    private String mLocalPath;
    private int requestPermissionCode = 0;
    //备案号控件
    private TextView filingNumberView;

    //测试-渠道点击数量
    private int channelClickCount = 0;

    //下载文件路径
    private String downloadFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);
        CrashApplication.getInstance().addActivity(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        background = findViewById(R.id.backlayout);
        filingNumberView = findViewById(R.id.filingNumber);
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        url = findViewById(R.id.imageView_url_btn);// 下载其他应用
        url.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AboutActivity.this, Web.class);
                intent.putExtra("url", "http://app." + Constant.IYUBA_CN + "android");
                intent.putExtra("title", "精品应用");
                startActivity(intent);
            }
        });
        appUpdateBtn = findViewById(R.id.relativeLayout1);
        appUpdateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAppUpdate();
            }
        });
        appNewImg = findViewById(R.id.imageView_new_app);
        currVersionCode = (TextView) findViewById(R.id.app_version);
        currVersionCode.setText(getResources()
                .getString(R.string.about_version) + VersionManager.VERSION_CODE);
        //如果是测试版本，则标记为测试版
        if (BuildConfig.DEBUG) {
            currVersionCode.setText(getResources().getString(R.string.about_version) + VersionManager.VERSION_CODE + "_测试版");
        }
        //如果是抖音版本，则在版本后增加标识
        String channelName = ChannelReaderUtil.getChannel(this);
        if (channelName.toLowerCase().equals("tiktok")){
            currVersionCode.setText(getResources().getString(R.string.about_version) + VersionManager.VERSION_CODE + "_tiktok");
        }
        progressBar_downLoad = (ProgressBar) findViewById(R.id.progressBar_update);
        checkAppUpdate();

        findViewById(R.id.logout_user).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        //点击显示
        TextView channel = findViewById(R.id.channel);
        findViewById(R.id.imageView1).setOnClickListener(v -> {
            //增加一个显示oaid的功能
            if (BuildConfig.DEBUG) {
                ToastUtil.showToast(AboutActivity.this, OAIDHelper.getInstance().getOAID());
            }


            //显示当前打包渠道
            channelClickCount++;

            if (channelClickCount>5){
                channel.setVisibility(View.VISIBLE);
                channel.setText(channelName);
            }

            /*//全四册-1，2，3，4
            //青少版-278,279,280,281,282,283,284,285,286,287,288,289
            VoaOp voaOp = new VoaOp(this);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .get()
//                    .url("http://apps.iyuba.cn/iyuba/getTitleBySeries.jsp?appid=222&uid=0&type=title&sign=15ae6c8652b29bf5bcb237855e722894&seriesid=289")
                    .url("http://apps.iyuba.cn/concept/getConceptTitle.jsp?language=US&book=4&flg=1")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    int a = 0;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        Log.d("添加完成","数据加入开始");

                        String data = response.body().string();

                        //青少版处理
//                        JuniorChapterTestData unitTitle = new Gson().fromJson(data, JuniorChapterTestData.class);
//
//                        for (int i = 0; i < unitTitle.getData().size(); i++) {
//                            JuniorChapterTestData.DataBean bean = unitTitle.getData().get(i);
//                            voaOp.updatePic(bean.getId(),bean.getPic());
//                        }

                        //全四册处理
                        FourChapterTestData unitTitle = new Gson().fromJson(data,FourChapterTestData.class);

                        for (int i = 0; i < unitTitle.getData().size(); i++) {
                            FourChapterTestData.DataBean bean = unitTitle.getData().get(i);
                            voaOp.updatePic(bean.getVoa_id(),bean.getPic());
                        }

                        Log.d("添加完成","数据加入完成");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });*/
        });

        //标题点击
        findViewById(R.id.play_title_info).setOnClickListener(v->{
            if (BuildConfig.DEBUG){
                List<EvalFixBean.WordEvalFixBean> wordList = new ArrayList<>();
                wordList.add(new EvalFixBean.WordEvalFixBean("Welcome","001",2.1f,0,""));
                wordList.add(new EvalFixBean.WordEvalFixBean("my","001",4.1f,2,""));
                wordList.add(new EvalFixBean.WordEvalFixBean("to","001",2.6f,1,""));
                wordList.add(new EvalFixBean.WordEvalFixBean("house!","001",2.1f,3,""));
                EvalFixBean fixBean = new EvalFixBean(313001,1,1,"Welcome to my house!",wordList);

                EvalFixDialog dialog = EvalFixDialog.getInstance(GsonUtil.toJson(fixBean));
                dialog.show(getSupportFragmentManager(),"ShowDialog");
            }
        });

        findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
        findViewById(R.id.textView2).setVisibility(View.GONE);
        TextView tv1 = findViewById(R.id.textView1);
        String msg = getResources().getString(R.string.app_name) + "是一款使用先进的语音识别智能学习技术，基于国外主流的英语学习理论指导，帮助广大新概念英语学习者轻松跨越学习障碍！\n" +
                "\n" +
                "产品特色：\n" +
                "1.新概念英语（New Concept English）作为全世界广为流传的英语学习的宝典，以其严密的体系性、严谨的科学性、精湛的实用性、浓郁的趣味性深受英语学习者的青睐。\n" +
                "2.海量优质潮流的经典英语视频，通过为电影、动漫等视频的配音让您在不知不觉中提高口语能力，掌握地道标准的发音。\n" +
                "3.智能配音评测，摆脱枯燥无味的传统学习方式，模仿配音、翻译生词、打榜排行，用寓教于乐的方式在不自觉中提升您的外语水平。";
        tv1.setText(msg);

        //根据包名处理
        if (getPackageName().equals(Constant.package_learnNewEnglish)) {
            findViewById(R.id.imageView1).setVisibility(View.INVISIBLE);
        }

        //显示备案号
        showFilingNumber();

        //根据登录状态判断注销操作显示
        if (UserInfoManager.getInstance().isLogin()) {
            findViewById(R.id.logout_user).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.logout_user).setVisibility(View.INVISIBLE);
        }

        //设置toolbar的标题的点击
//        findViewById(R.id.play_title_info).setOnClickListener(v->{
//            PractiseShowActivity.start(this, PractiseShowActivity.showType_line);
//        });
    }

    /**
     * user logout
     */
    private void logoutUser() {
        if (UserInfoManager.getInstance().isLogin()) {
            Intent intent = new Intent(AboutActivity.this, LogoutUserActivity.class);
            startActivity(intent);
        } else {
            LoginUtil.startToLogin(this);
        }
    }

    /**
     * 检查新版本
     */
    public void checkAppUpdate() {
        if (getPackageName().equals(AdvertisingKey.releasePackage)
                || getPackageName().equals(AdvertisingKey.xiaomiPackage)
                || getPackageName().equals(AdvertisingKey.smallClassPackage)
                || getPackageName().equals("com.iyuba.learnNewEnglish")) {
            VersionManager.Instace(this).checkNewVersion(VersionManager.version, ConstantNew.PACKAGE_TYPE, this);
        }
    }

    @Override
    public void appUpdateSave(String version_code, String newAppNetworkUrl) {
        this.version_code = version_code;
        this.appUpdateUrl = newAppNetworkUrl;
        handler.sendEmptyMessage(0);
    }

    @Override
    public void appUpdateFaild() {
        handler.sendEmptyMessage(1);
    }

    @Override
    public void appUpdateBegin(String newAppNetworkUrl) {
    }

    private void startDownload() {
        // 下载更新
        progressBar_downLoad.setMax(100); // 设置progressBar最大值
        FileDownProcessBar fdpb = new FileDownProcessBar(AboutActivity.this, progressBar_downLoad);

        fdpb.downLoadApkFile(appUpdateUrl, Constant.appfile + "_" + version_code, new DownLoadFailCallBack() {
            @Override
            public void downLoadSuccess(String localFilPath) {
                mLocalPath = localFilPath;
                handler.sendEmptyMessage(2);
            }

            //权限请求
            @Override
            public void downLoadFaild(String errorInfo) {
                CustomToast.showToast(getBaseContext(), R.string.about_error, 1000);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (appNewImg != null) {
                        appNewImg.setVisibility(View.VISIBLE);
                        appUpdateBtn.setEnabled(false);
                        progressBar_downLoad.setVisibility(View.VISIBLE);
                    }
                    String content = getResources().getString(R.string.about_update_alert_1) + version_code + getResources().getString(R.string.about_update_alert_2);
                    showAlertAndCancel(content, (dialog, which) -> {
                        handler.sendEmptyMessage(3);
                    });
                    break;
                case 1:
                    if (appNewImg != null) {
                        appNewImg.setVisibility(View.INVISIBLE);
                        progressBar_downLoad.setVisibility(View.INVISIBLE);
                    }

                    CustomToast.showToast(getBaseContext(),
                            getResources().getString(R.string.app_name) + "已经是最新版本", 1000);
                    break;
                case 2:
                    appUpdateBtn.setEnabled(true);
                    progressBar_downLoad.setVisibility(View.INVISIBLE);
                    boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                    if (!haveInstallPermission) {
                        //权限没有打开则提示用户去手动打开
                        ToastUtil.showToast(AboutActivity.this, "请开启安装应用设置");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            // 开启当前应用的权限管理页
                            Uri packageUri = Uri.parse("package:" + AboutActivity.this.getPackageName());
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageUri);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, REQUEST_OK);

                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
                            } else {
                                FailOpera.Instace(AboutActivity.this).openFile(mLocalPath);
                            }
                        }
                    } else {
                        FailOpera.Instace(AboutActivity.this).openFile(mLocalPath);
                    }
                    break;
                case 3:
                    String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (checkSelfPermission(permission[0]) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(permission, requestPermissionCode);
                    } else {
                        startDownload();
                    }
                    break;
            }
        }
    };

    private void showAlertAndCancel(String msg,
                                    DialogInterface.OnClickListener ocl) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(R.string.alert_title);
        alert.setMessage(msg);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setCancelable(false);
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.alert_btn_ok), ocl);
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appUpdateBtn.setEnabled(true);
                        progressBar_downLoad.setVisibility(View.INVISIBLE);
                    }
                });
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!TextUtils.isEmpty(mLocalPath)) {
                    FailOpera.Instace(AboutActivity.this).openFile(mLocalPath);
                } else {
                    ToastUtil.showToast(AboutActivity.this, "下载路径为空");
                }
            } else {
                ToastUtil.showToast(AboutActivity.this, "请授权安装更新！");
            }
        } else if (requestCode == requestPermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload();
            } else {
                ToastUtil.showToast(AboutActivity.this, "已拒绝申请权限");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && REQUEST_OK == requestCode) {
            if (!TextUtils.isEmpty(mLocalPath)) {
                FailOpera.Instace(AboutActivity.this).openFile(mLocalPath);
            }
        }
    }


    //显示备案号
    private void showFilingNumber() {
        if (getPackageName().equals(Constant.package_learnNewEnglish)) {
            filingNumberView.setText("鲁ICP备2022029024号-2A");
        } else if (getPackageName().equals(Constant.package_concept2)) {
            filingNumberView.setText("京ICP备14035507号-25A");
        } else if (getPackageName().equals(Constant.package_englishfm)) {
            filingNumberView.setText("京ICP备14035507号-25A");
        } else if (getPackageName().equals(Constant.package_newconcepttop)) {
            filingNumberView.setText("京ICP备18027903号-11A");
        } else if (getPackageName().equals(Constant.package_nce)) {
            filingNumberView.setText("京ICP备18027903号-24A");
        } else {
            filingNumberView.setText("教APP备110213号");
        }
    }
}
