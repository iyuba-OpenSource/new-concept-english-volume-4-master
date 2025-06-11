package com.iyuba.conceptEnglish.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.event.MoneyChangeEvent;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.han.utils.CalendarRecordHelper;
import com.iyuba.conceptEnglish.protocol.AddScoreRequest;
import com.iyuba.conceptEnglish.protocol.AddScoreResponse;
import com.iyuba.conceptEnglish.protocol.SignRequest;
import com.iyuba.conceptEnglish.protocol.SignResponse;
import com.iyuba.conceptEnglish.sqlite.mode.SignBean;
import com.iyuba.conceptEnglish.sqlite.mode.StudyTimeBeanNew;
import com.iyuba.conceptEnglish.util.QRCodeEncoder;
import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.module.toolbox.DensityUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.moments.WechatMoments;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * 打卡页面
 */

public class SignActivity extends BaseStackActivity {

    private RelativeLayout sharePart;
    private ImageView imageView;
    private ImageView qrImage;
    private TextView tv1, tv2, tv3;
    private Context mContext;
    private TextView sign;
    private ImageView userIcon;
    private TextView tvShareMsg;
    private int signStudyTime = 3 * 60;
    private String loadFiledHint = "打卡加载失败";

    String shareTxt;
    String getTimeUrl;
    LinearLayout ll;
    CustomDialog mWaittingDialog;
    String addCredit = "";//Integer.parseInt(bean.getAddcredit());
    String days = "";//Integer.parseInt(bean.getDays());
    String totalCredit = "";//bean.getTotalcredit();
    String money = "";
//    private int QR_HEIGHT = 77;
//    private int QR_WIDTH = 77;

    private TextView tvUserName;
    private TextView tvAppName;
    private TextView tv_finish;

    private ImageView btn_close;
    private MaterialDialog dialog, dialog_share;
    private final String fileName="aaa.jpg";
    private CalendarRecordHelper recordHelper;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;

        mWaittingDialog = WaittingDialog.showDialog(SignActivity.this);
        mWaittingDialog.setTitle("请稍后");
        setContentView(R.layout.activity_sign);

        //状态栏处理
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {

            //android手机小于5.0的直接全屏显示，防止截图留白边
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        initView();

        initData();

        initBackGround();
        recordHelper=new CalendarRecordHelper(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initData() {

        mWaittingDialog.show();

        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());


        String url = String.format(Locale.CHINA, "http://daxue." + Constant.IYUBA_CN + "ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uid, getDays());
        getTimeUrl = url;

        ExeProtocol.exe(new SignRequest(String.valueOf(UserInfoManager.getInstance().getUserId())), new ProtocolResponse() {

            @Override
            public void finish(final BaseHttpResponse bhr) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        SignResponse response = (SignResponse) bhr;
                        try {
                            if (null != mWaittingDialog) {
                                if (mWaittingDialog.isShowing()) {
                                    mWaittingDialog.dismiss();
                                }
                            }

                            final StudyTimeBeanNew bean = new Gson().fromJson(response.jsonObjectRoot.toString(), StudyTimeBeanNew.class);
                            Log.d("dddd", response.jsonObjectRoot.toString());
                            if ("1".equals(bean.getResult())) {
                                final int time = Integer.parseInt(bean.getTotalTime());
                                int totaltime = Integer.parseInt(bean.getTotalDaysTime());
                                tv1.setText(bean.getTotalDays() + ""); //学习天数
                                tv2.setText(bean.getTotalWord() + "");//今日单词

                                //二维码确认与生成
                                String qrIconUrl = "http://app."+Constant.IYUBA_CN+"share.jsp?uid=" + UserInfoManager.getInstance().getUserId()
                                        + "&appId=" + Constant.APPID + "&shareId=" + bean.getShareId();
                                Bitmap qr_bitmap = QRCodeEncoder.syncEncodeQRCode(qrIconUrl, DensityUtil.dp2px(mContext, 65),
                                        Color.BLACK, Color.WHITE, null);
                                qrImage.setImageBitmap(qr_bitmap);

                                int nowRank = Integer.parseInt(bean.getRanking());
                                double allPerson = Double.parseDouble(bean.getTotalUser());
                                double carry;
                                String over = null;
                                if (allPerson != 0) {
                                    carry = 1 - nowRank / allPerson;
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    Log.e("百分比", df.format(carry) + "--" + nowRank + "--" + allPerson);

                                    over = df.format(carry).substring(2, 4);
                                }

                                tv3.setText(over + "%同学"); //超越了
                                shareTxt = bean.getSentence() + "我在爱语吧坚持学习了" + bean.getTotalDays() + "天,积累了" + bean.getTotalWords() + "单词如下";

                                if (time < signStudyTime) {
                                    sign.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            toast(String.format(Locale.CHINA, "打卡失败，当前已学习%d秒，\n满%d分钟可打卡", time, signStudyTime / 60));
                                        }
                                    });
                                } else {
                                    sign.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                            qrImage.setVisibility(View.VISIBLE);
//                                            sign.setVisibility(View.GONE);
                                            tvShareMsg.setVisibility(View.VISIBLE);
                                            tv_finish.setVisibility(View.VISIBLE);
                                            tvShareMsg.setText("长按图片识别二维码");
                                            tvShareMsg.setBackground(getResources().getDrawable(R.drawable.sign_bg_yellow));


                                            writeBitmapToFile();

                                            if (InfoHelper.getInstance().openShare()){
                                                showShareOnMoment(mContext, String.valueOf(UserInfoManager.getInstance().getUserId()), Constant.APPID);
                                            }
                                        }
                                    });
//                            startShareInterface();
                                }
                            } else {
                                toast(loadFiledHint + bean.getResult());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            toast(loadFiledHint + "！！");
                        }

                    }
                });
            }

            @Override
            public void error() {

            }
        });


    }

    private void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private long getDays() {
        //东八区;
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance(Locale.CHINA);
        long intervalMilli = now.getTimeInMillis() - cal.getTimeInMillis();
        long xcts = intervalMilli / (24 * 60 * 60 * 1000);
        return xcts;
    }

    private void initView() {
        sharePart = (RelativeLayout) findViewById(R.id.rl_share_part);
        imageView = (ImageView) findViewById(R.id.iv);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);

        sign = (TextView) findViewById(R.id.tv_sign);
        ll = (LinearLayout) findViewById(R.id.ll);
        qrImage = (ImageView) findViewById(R.id.tv_qrcode);
        userIcon = (ImageView) findViewById(R.id.iv_userimg);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvAppName = (TextView) findViewById(R.id.tv_appname);
        tvShareMsg = (TextView) findViewById(R.id.tv_sharemsg);

        btn_close = (ImageView) findViewById(R.id.btn_close);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
        String type;
        switch (getPackageName()){
            case AdvertisingKey.releasePackage:
            case AdvertisingKey.xiaomiPackage:
                type="新概念英语全四册";
                break;
            case "com.iyuba.talkshow.childenglish":
            case "com.iyuba.talkshow.childenglishnew":
                type="小学英语";
                break;
            case "com.iyuba.talkshow.juniortalkshow":
                type="新概念初中英语";
                break;
            case "com.iyuba.youth":
                type="新概念英语青少版";
                break;
            default:
                type="新概念英语";
                break;
        }
        tv_finish.setText(" 刚刚在『" +type+ "』上完成了打卡");
        

        tv_finish.setVisibility(View.INVISIBLE);

        //关闭打卡页面弹出提示
        dialog = new MaterialDialog(SignActivity.this);
        dialog.setTitle("温馨提示");
        //根据包名处理
        if (InfoHelper.getInstance().openShare()){
            dialog.setMessage("点击下边的打卡按钮，成功分享至微信朋友圈才算成功打卡，才能领取红包哦！确定退出么？");
        }else {
            dialog.setMessage("点击下边的打卡按钮，才能领取红包哦！确定退出么？");
        }
        dialog.setPositiveButton("继续打卡", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("去意已决", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                Log.d("当前退出的界面0015", getClass().getName());
            }
        });


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

            }
        });


        //当天再次打卡成功后显示
        dialog_share = new MaterialDialog(SignActivity.this);
        dialog_share.setTitle("提醒");
//        dialog_share.setMessage("今日已打卡，不能再次获取红包或积分哦！");
        dialog_share.setPositiveButton("好的", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_share.dismiss();
                finish();
                Log.d("当前退出的界面0016", getClass().getName());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initBackGround() {

        int day = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH);
        String url = "http://"+Constant.staticStr + Constant.IYUBA_CN + "images/mobile/" + day + ".jpg";

//        bitmap = returnBitMap(url);
        Glide.with(mContext).load(url).placeholder(R.drawable.sign_background).error(R.drawable.sign_background).into(imageView);
        String userIconUrl = "http://api."+Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + String.valueOf(UserInfoManager.getInstance().getUserId()) + "&size=middle";


//        Glide.with(mContext).load(userIconUrl).into(userIcon);

        /*Glide.with(mContext).load(userIconUrl).asBitmap()  //这句不能少，否则下面的方法会报错
                .placeholder(R.drawable.defaultavatar).error(R.drawable.defaultavatar).centerCrop().into(new BitmapImageViewTarget(userIcon) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                userIcon.setImageDrawable(circularBitmapDrawable);
            }
        });*/
        LibGlide3Util.loadCircleImg(mContext,userIconUrl,R.drawable.defaultavatar,userIcon);


        if (TextUtils.isEmpty(UserInfoManager.getInstance().getUserName())) {
            tvUserName.setText(String.valueOf(UserInfoManager.getInstance().getUserId()));
        } else {
            tvUserName.setText(UserInfoManager.getInstance().getUserName());
        }

        if (getPackageName().equals(AdvertisingKey.releasePackage)) {
            tvAppName.setText("新概念英语全四册" + "--英语学习必备软件");
        }
    }


    public void writeBitmapToFile() {

        btn_close.setVisibility(View.GONE);
        sign.setVisibility(View.GONE);
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap == null) {
            return;
        }

        bitmap.setHasAlpha(false);
        bitmap.prepareToDraw();

        File newPNGFile = new File(this.getExternalFilesDir(null) + "/"+fileName);
        if (newPNGFile.exists()) {
            newPNGFile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(newPNGFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        marginParams.setMargins(55, 0, 0, 0);
//        userIcon.setLayoutParams(marginParams);

        tv_finish.setVisibility(View.GONE);
        btn_close.setVisibility(View.VISIBLE);
        sign.setVisibility(View.VISIBLE);
    }

    public Bitmap fromView(@NonNull View v) {
        Bitmap screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(screenshot);
        v.draw(canvas);
        return screenshot;
    }

    private void startInterfaceADDScore(String userID, String appid) {

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        String dateString = formatter.format(currentTime);
//        String time = Base64Coder.encode(dateString);
        String time = Base64.encodeToString(dateString.getBytes(), Base64.NO_WRAP);
        ExeProtocol.exe(new AddScoreRequest(userID, appid, time, "81", 0), new ProtocolResponse() {
            @Override
            public void finish(BaseHttpResponse bhr) {
                recordHelper.insertSingle();
                AddScoreResponse response = (AddScoreResponse) bhr;
                final SignBean bean = new Gson().fromJson(response.jsonObjectRoot.toString(), SignBean.class);
                if (bean.getResult().equals("200")) {
                    money = bean.getMoney();
                    addCredit = bean.getAddcredit();
                    days = bean.getDays();
                    totalCredit = bean.getTotalcredit();

                    //打卡成功,您已连续打卡xx天,获得xx元红包,关注[爱语课吧]微信公众号即可提现!
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float moneyThisTime = Float.parseFloat(money);

                            if (moneyThisTime > 0) {
                                float allmoney = Float.parseFloat(totalCredit);
                                //获取新的数据
                                UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
                                dialog_share.setMessage("打卡成功," + "您已连续打卡" + days + "天,获得" + floatToString(moneyThisTime) + "元,总计: " + floatToString(allmoney) + "元");
                            } else {
                                dialog_share.setMessage("打卡成功，连续打卡" + days + "天,获得" + addCredit + "积分，总积分: " + totalCredit);
                            }

                            dialog_share.show();
                            EventBus.getDefault().post(new MoneyChangeEvent());
                        }
                    });


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog_share.setMessage("今日已打卡，切勿重复打卡！");
                            dialog_share.show();
                        }
                    });

                }


            }

            @Override
            public void error() {

            }
        });


    }


    public void showShareOnMoment(Context context, final String userID, final String AppId) {
        OnekeyShare oks = new OnekeyShare();
        if (!InfoHelper.showWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        //微博飞雷神
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        oks.setPlatform(WechatMoments.NAME);
        String titleText="我在"+getResources().getString(R.string.app_name)+"完成了打卡";
        oks.setText(titleText);
        oks.setTitle(titleText);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setImagePath(this.getExternalFilesDir(null) + "/"+fileName);

        oks.setSilent(true);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                startInterfaceADDScore(userID, AppId);
                tv_finish.setVisibility(View.GONE);
                changeBypassApproval(false);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                tv_finish.setVisibility(View.GONE);
                changeBypassApproval(false);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                tv_finish.setVisibility(View.GONE);
                changeBypassApproval(false);
            }
        });
        // 启动分享GUI
        oks.show(context);
    }

    private void changeBypassApproval(boolean thatSet){
        HashMap<String, Object> devInfo = new HashMap<>();
        devInfo.put("Id",ShareSDK.getDevinfo(WechatMoments.NAME,"Id"));
        devInfo.put("SortId",ShareSDK.getDevinfo(WechatMoments.NAME,"SortId"));
        devInfo.put("AppId",ShareSDK.getDevinfo(WechatMoments.NAME,"AppId"));
        devInfo.put("AppSecret",ShareSDK.getDevinfo(WechatMoments.NAME,"AppSecret"));
        devInfo.put("Enable",ShareSDK.getDevinfo(WechatMoments.NAME,"Enable"));
        devInfo.put("BypassApproval",thatSet+"");
        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, devInfo);

    }

//    private String buildImagePath(Bitmap bitmap) {
//        String dirPath = PathUtils.getSharePath(mContext);
//        File dir = new File(dirPath);
//        if (!dir.exists()) dir.mkdirs();
//        String filePath = dir + "check_in_info.jpg";
//        File oldFile = new File(filePath);
//        if (oldFile.exists()) oldFile.delete();
//        SaveImage.save(filePath, bitmap);
//        return filePath;
//    }


    private String floatToString(float fNumber) {

        fNumber = (float) (fNumber * 0.01);

        DecimalFormat myformat = new java.text.DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }

}



