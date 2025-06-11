package com.iyuba.conceptEnglish.activity.pass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.MainFragmentActivity;
import com.iyuba.conceptEnglish.event.MoneyChangeEvent;
import com.iyuba.conceptEnglish.lil.concept_other.util.PermissionDialogUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.protocol.AddScoreRequest;
import com.iyuba.conceptEnglish.protocol.AddScoreResponse;
import com.iyuba.conceptEnglish.sqlite.mode.SignBean;
import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * 单词闯关成功后的分享界面
 *
 *
 * 这个的每个课程每个账号只能领取一次
 */
public class WordShareActivity extends Activity {

    @BindView(R.id.txt_level)
    TextView txt_level;
    @BindView(R.id.txt_wordNum)
    TextView txt_wordNum;
    @BindView(R.id.txt_ratio)
    TextView txt_ratio;
    @BindView(R.id.btn_share)
    Button btn_share;
    @BindView(R.id.txt_cancel)
    TextView txt_cancel;
    @BindView(R.id.txt_username)
    TextView txt_username;
    @BindView(R.id.iv_elvator)
    ImageView iv_elvator;


    @BindView(R.id.txt_hint)
    TextView txt_hint;

    @BindView(R.id.re_rootView)
    RelativeLayout re_rootView;

    private static final String imagePath="share.png";

    /**
     * 是否已经调用过增加积分接口
     */
    private volatile AtomicBoolean isAdd=new AtomicBoolean(false);


    private Context mContext;

    private MaterialDialog dialog_share;
    private int courseId;

    private int[] pictures = {R.drawable.ic_bg_share1, R.drawable.ic_bg_share3, R.drawable.ic_bg_share4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decoView = getWindow().getDecorView();
            decoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_word_share);
        ButterKnife.bind(this);
        mContext = this;
        int picIndex = (int) (Math.random() * 3);

        re_rootView.setBackgroundResource(pictures[picIndex]);
        re_rootView.setOnClickListener(v -> {
            int picIndex2 = (int) (Math.random() * 3);
            re_rootView.setBackgroundResource(pictures[picIndex2]);
        });


        txt_username.setText(UserInfoManager.getInstance().getUserName());

        courseId = getIntent().getIntExtra("courseId", 1);
        int wordNum = getIntent().getIntExtra("wordNum", 1);
        String rate = getIntent().getStringExtra("rate");
        try {
            rate = rate.substring(0, rate.indexOf(".") + 2) + "%";
        } catch (Exception e) {
            rate = "0.0%";
        }
        txt_level.setText(courseId + "");
        txt_wordNum.setText(wordNum + "");
        txt_ratio.setText(rate);
        GitHubImageLoader.getInstance().setCirclePic(String.valueOf(UserInfoManager.getInstance().getUserId()), iv_elvator);


        ((TextView) findViewById(R.id.tv_desc)).setText("刚刚在 「" + getString(R.string.app_name) + "」上完成了闯关");
        txt_hint.setText("「" + getString(R.string.app_name) + "」\n专业口语评测\n学习英语必备");


        //当天再次打卡成功后显示
        dialog_share = new MaterialDialog(this);
        dialog_share.setTitle("提醒");
        dialog_share.setPositiveButton("好的", v -> {
            dialog_share.dismiss();
            startActivity(new Intent(mContext, MainFragmentActivity.class));
            finish();
            Log.d("退出显示26", this.getClass().getName());
        });
    }

    private void setOperateBtnStatus(Boolean flag){
        int status=View.VISIBLE;
        if (!flag){
            status=View.INVISIBLE;
        }
        txt_cancel.setVisibility(status);
        btn_share.setVisibility(status);
    }

    @OnClick(R.id.btn_share)
    void share() {
        //这里增加权限说明
        List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.READ_EXTERNAL_STORAGE,new Pair<>("存储权限","用于截图后分享至社交平台使用")));
        PermissionDialogUtil.getInstance().showMsgDialog(this, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
            @Override
            public void onGranted(boolean isSuccess) {
                if (isSuccess){
                    //分享领红包传82，打卡传81
                    setOperateBtnStatus(false);

                    boolean isSave = writeBitmapToFile();
                    if (isSave){
                        showShareOnMoment(WordShareActivity.this, String.valueOf(UserInfoManager.getInstance().getUserId()), Constant.APPID);
                    }else {
                        setOperateBtnStatus(true);
                        ToastUtil.showToast(WordShareActivity.this,"图片加载失败，请重试");
                    }
                }else {
                    setOperateBtnStatus(true);
                }
            }
        });
    }

    @OnClick(R.id.txt_cancel)
    void cancel() {
        startActivity(new Intent(mContext, MainFragmentActivity.class));
        finish();
        Log.d("退出显示27", this.getClass().getName());
    }

    public void showShareOnMoment(Context context, final String userID, final String AppId) {
//        changeBypassApproval(true);
        OnekeyShare oks = new OnekeyShare();
        if (!InfoHelper.showWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        //微博飞雷神
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setSilent(true);
        oks.addHiddenPlatform(WechatFavorite.NAME);
//        oks.addHiddenPlatform(SinaWeibo.NAME);
        oks.addHiddenPlatform(QQ.NAME);
        oks.addHiddenPlatform(Wechat.NAME);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setImagePath(getSharePicPath(this));
        oks.setSilent(true);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                /**
                 * 防止 mob 二次回调这个接口，造成显示问题
                 */
                if (isAdd.get()){
                    return;
                }
                isAdd.set(true);

                startInterfaceADDScore(userID, AppId);
                setOperateBtnStatus(true);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("--分享失败===", throwable.toString());
                setOperateBtnStatus(true);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                setOperateBtnStatus(true);
                Log.e("--分享取消===", "....");
            }
        });
        // 启动分享GUI
        oks.show(context);
    }

    private void changeBypassApproval(boolean thatSet){
        HashMap<String, Object> devInfo = new HashMap<>();
        devInfo.put("Id", ShareSDK.getDevinfo(WechatMoments.NAME,"Id"));
        devInfo.put("SortId",ShareSDK.getDevinfo(WechatMoments.NAME,"SortId"));
        devInfo.put("AppId",ShareSDK.getDevinfo(WechatMoments.NAME,"AppId"));
        devInfo.put("AppSecret",ShareSDK.getDevinfo(WechatMoments.NAME,"AppSecret"));
        devInfo.put("Enable",ShareSDK.getDevinfo(WechatMoments.NAME,"Enable"));
        devInfo.put("BypassApproval",thatSet+"");
        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, devInfo);

    }

    private void startInterfaceADDScore(String userID, String appid) {

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        String time = Base64.encodeToString(dateString.getBytes(), Base64.NO_WRAP);
        ExeProtocol.exe(new AddScoreRequest(userID, appid, time, "82", courseId), new ProtocolResponse() {
            @Override
            public void finish(BaseHttpResponse bhr) {
                AddScoreResponse response = (AddScoreResponse) bhr;
                final SignBean bean = new Gson().fromJson(response.jsonObjectRoot.toString(), SignBean.class);
                if (bean.getResult().equals("200")) {
                    //打卡成功,您已连续打卡xx天,获得xx元红包,关注[爱语课吧]微信公众号即可提现!
                    runOnUiThread(() -> {
                        StringBuilder builder = new StringBuilder();
                        float moneyThisTime = Float.parseFloat(bean.getMoney());
                        float allMoney = Float.parseFloat(bean.getTotalcredit());

                        if (moneyThisTime > 0) {
                            builder.append("分享成功, 恭喜您完成了单词闯关,获得了").append(floatToString(moneyThisTime)).append("元奖励");
                            //刷新用户信息
                            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
                        } else {
                            builder.append("分享成功,总计: ").append(floatToString(allMoney)+"元");
                        }
                        dialog_share.setMessage(builder.toString());
                        dialog_share.show();
                        EventBus.getDefault().post(new MoneyChangeEvent());
                    });
                } else if ("203".equals(bean.getResult())) {
                    runOnUiThread(() -> {
                        dialog_share.setMessage("分享成功，每日最多可领取3次红包");
                        dialog_share.show();
                    });
                } else if ("201".equals(bean.getResult())) {
                    runOnUiThread(() -> {
                        //这里要求去掉红包提示
//                        dialog_share.setMessage("分享成功，重复分享不能获得红包");
                        dialog_share.setMessage("分享成功");
                        dialog_share.show();
                    });
                }
            }

            @Override
            public void error() {

            }
        });


    }


    public boolean writeBitmapToFile() {
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        if (bitmap == null) {
            return false;
        }
        bitmap.setHasAlpha(false);
        bitmap.prepareToDraw();
        File newpngfile = new File(getSharePicPath(this));
        if (newpngfile.exists()) {
            newpngfile.delete();
        }
        try {
            //兄弟，这里有点sb了，删除图片后没有创建图片，你这怎么存储啊
            if (!newpngfile.getParentFile().exists()){
                newpngfile.getParentFile().mkdirs();
            }
            newpngfile.createNewFile();
            //然后将图片放进去
            FileOutputStream out = new FileOutputStream(newpngfile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String floatToString(float fNumber) {

        fNumber = (float) (fNumber * 0.01);

        DecimalFormat myformat = new java.text.DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }

    //设置分享图片的路径
    public static String getSharePicPath(Context context){
        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            path = context.getExternalFilesDir(null).getPath()+"/iyuba/"+imagePath;
        }else {
            path = Environment.getExternalStorageDirectory().getPath()+"/iyuba/"+imagePath;
        }
        return path;
    }
}
