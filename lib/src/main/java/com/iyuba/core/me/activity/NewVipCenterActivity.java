package com.iyuba.core.me.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.util.BrandUtil;
import com.iyuba.core.common.util.ScreenUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.MyGridView;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.DateUtil;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.me.adapter.MyGridAdapter;
import com.iyuba.core.me.pay.PayOrderActivity;
import com.iyuba.lib.R;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * 会员界面
 */
public class NewVipCenterActivity extends BasisActivity {
    private Context mContext;
    private RelativeLayout rl_buyforevervip;
    private MyGridView gv_tequan;
    private TextView localVip, goldVip;
    private CustomDialog wettingDialog;
    private Button iv_back;
    private TabHost th;
    private ContextCompat contextCompat;
    private TextView tv_vip_html;

    private double price;

    private TextView tv_user_name, tv_aiyubi, tv_time;
    private CircleImageView circularImageView;
    private String orderInfo;

    ConstraintLayout all_app_re_1;
    ConstraintLayout all_app_re_2;
    ConstraintLayout all_app_re_3;
    ConstraintLayout all_app_re_4;
    ConstraintLayout all_app_re_5;

    ConstraintLayout my_app_re_1;
    ConstraintLayout my_app_re_2;
    ConstraintLayout my_app_re_3;
    ConstraintLayout my_app_re_4;

    ConstraintLayout gold_app_re_1;
    ConstraintLayout gold_app_re_2;
    ConstraintLayout gold_app_re_3;
    ConstraintLayout gold_app_re_4;

    CheckBox all_app_cb_1;
    CheckBox all_app_cb_2;
    CheckBox all_app_cb_3;
    CheckBox all_app_cb_4;
    CheckBox all_app_cb_5;

    CheckBox my_app_cb_1;
    CheckBox my_app_cb_2;
    CheckBox my_app_cb_3;
    CheckBox my_app_cb_4;

    CheckBox gold_app_cb_1;
    CheckBox gold_app_cb_2;
    CheckBox gold_app_cb_3;
    CheckBox gold_app_cb_4;

    TextView tv_submit;
    TextView tv_submit_my_app;
    TextView tv_submit_gold;

    TextView jumpToWechat1;
    TextView jumpToWechat2;
    TextView jumpToWechat3;

    ImageView mQQImage;

    private String []tabArray=new String[]{"tab1","tab2","tab3"};

    private int all_app_vip_month = -1, my_app_month = -1, gold_app_month = -1;

    private final int selectColor=Color.rgb(253,218,148);
    private final int unSelectColor=Color.WHITE;

    public static final int VIP_APP = 0;//本应用会员
    public static final int VIP_ALL = 1;//全站会员
    public static final int VIP_GOLD = 2;//黄金会员

    //本应用会员-0，全站会员-1，黄金会员-2
    public static void start(Context context,int vipType){
        Intent intent = new Intent(context, NewVipCenterActivity.class);
        intent.putExtra("show_gold_member",vipType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_vip);
        CrashApplication.getInstance().addActivity(this);
        mContext = this;

        findViewById();
        initView();
        setDefault();

        BrandUtil.requestQQGroupNumber(this);
        BrandUtil.requestServiceQQNumber(this);

        // TODO: 2025/4/8 这里关闭掉小程序的跳转(因为旧的小程序被注销了，新的小程序中也没有 爱语吧 这个内容，直接关掉算了)
//        if (mContext.getApplicationInfo().packageName.equals(Constant.package_concept2)) {
//            jumpToWechat1.setVisibility(View.VISIBLE);
//            jumpToWechat2.setVisibility(View.VISIBLE);
//            jumpToWechat3.setVisibility(View.VISIBLE);
//        } else {
            jumpToWechat1.setVisibility(View.GONE);
            jumpToWechat2.setVisibility(View.GONE);
            jumpToWechat3.setVisibility(View.GONE);
//        }

        //这里按照新标准进行处理
        int vipType = getIntent().getIntExtra("show_gold_member",VIP_APP);
        showSelectView(vipType);
        th.setCurrentTabByTag(tabArray[vipType]);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UserInfoManager.getInstance().isLogin()) {
            String headUrl="http://api."+Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + UserInfoManager.getInstance().getUserId()
                    + "&size=big";
            LibGlide3Util.loadCircleImg(this,headUrl,R.drawable.defaultavatar,circularImageView);

            tv_user_name.setText(UserInfoManager.getInstance().getUserName());
        } else {
            LibGlide3Util.loadCircleImg(this,R.drawable.defaultavatar,R.drawable.defaultavatar,circularImageView);

            tv_user_name.setText("未登录");
        }

        String iyuIcon = "0";
        if (UserInfoManager.getInstance().isLogin()){
            iyuIcon = String.valueOf(UserInfoManager.getInstance().getIyuIcon());
        }
        tv_aiyubi.setText("爱语币余额：" + iyuIcon);

        if (UserInfoManager.getInstance().isVip()) {
            tv_time.setVisibility(View.VISIBLE);
            //格式化会员时间
            tv_time.setText(DateUtil.toDateStr(UserInfoManager.getInstance().getVipTime(),DateUtil.YMD));
        } else {
            tv_time.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //对应xml布局关系
    private void findViewById(){
        all_app_re_1 = findViewById(R.id.all_app_re_1);
        all_app_re_2 = findViewById(R.id.all_app_re_2);
        all_app_re_3 = findViewById(R.id.all_app_re_3);
        all_app_re_4 = findViewById(R.id.all_app_re_4);
        all_app_re_5 = findViewById(R.id.all_app_re_5);

        my_app_re_1 = findViewById(R.id.my_app_re_1);
        my_app_re_2 = findViewById(R.id.my_app_re_2);
        my_app_re_3 = findViewById(R.id.my_app_re_3);
        my_app_re_4 = findViewById(R.id.my_app_re_4);

        gold_app_re_1 = findViewById(R.id.gold_app_re_1);
        gold_app_re_2 = findViewById(R.id.gold_app_re_2);
        gold_app_re_3 = findViewById(R.id.gold_app_re_3);
        gold_app_re_4 = findViewById(R.id.gold_app_re_4);

        all_app_cb_1 = findViewById(R.id.all_app_cb_1);
        all_app_cb_2 = findViewById(R.id.all_app_cb_2);
        all_app_cb_3 = findViewById(R.id.all_app_cb_3);
        all_app_cb_4 = findViewById(R.id.all_app_cb_4);
        all_app_cb_5 = findViewById(R.id.all_app_cb_5);

        my_app_cb_1 = findViewById(R.id.my_app_cb_1);
        my_app_cb_2 = findViewById(R.id.my_app_cb_2);
        my_app_cb_3 = findViewById(R.id.my_app_cb_3);
        my_app_cb_4 = findViewById(R.id.my_app_cb_4);

        gold_app_cb_1 = findViewById(R.id.gold_app_cb_1);
        gold_app_cb_2 = findViewById(R.id.gold_app_cb_2);
        gold_app_cb_3 = findViewById(R.id.gold_app_cb_3);
        gold_app_cb_4 = findViewById(R.id.gold_app_cb_4);

        tv_submit = findViewById(R.id.tv_submit);
        tv_submit_my_app = findViewById(R.id.tv_submit_my_app);
        tv_submit_gold = findViewById(R.id.tv_submit_gold);

        jumpToWechat1 = findViewById(R.id.tv_jump_to_wechat1);
        jumpToWechat2 = findViewById(R.id.tv_jump_to_wechat2);
        jumpToWechat3 = findViewById(R.id.tv_jump_to_wechat3);
    }

    private void initView() {
        wettingDialog = WaittingDialog.showDialog(mContext);
        iv_back = (Button) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NewVipCenterActivity.this.finish();
            }
        });

        mQQImage = findViewById(R.id.vip_qq_image);
        mQQImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showQQDialog(mContext, v);
            }
        });

        btn_buyiyuba = (Button) findViewById(R.id.btn_buyiyuba);
        tv_vip_html = (TextView) findViewById(R.id.tv_vip_html);


        tv_aiyubi = (TextView) findViewById(R.id.tv_aiyubi);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        circularImageView = findViewById(R.id.image_head);

//        tv_vip_html.setOnClickListener(ocl);
        btn_buyiyuba.setOnClickListener(ocl);
        th = (TabHost) findViewById(R.id.tabhost);

        gv_tequan = (MyGridView) findViewById(R.id.gridview);
        localVip = (TextView) findViewById(R.id.view4);
        goldVip = (TextView) findViewById(R.id.gold_tv);

        my_app_re_1.setOnClickListener(ocl);
        my_app_re_2.setOnClickListener(ocl);
        my_app_re_3.setOnClickListener(ocl);
        my_app_re_4.setOnClickListener(ocl);

        all_app_re_1.setOnClickListener(ocl);
        all_app_re_2.setOnClickListener(ocl);
        all_app_re_3.setOnClickListener(ocl);
        all_app_re_4.setOnClickListener(ocl);
        all_app_re_5.setOnClickListener(ocl);

        gold_app_re_4.setOnClickListener(ocl);
        gold_app_re_3.setOnClickListener(ocl);
        gold_app_re_2.setOnClickListener(ocl);
        gold_app_re_1.setOnClickListener(ocl);

        tv_submit.setOnClickListener(ocl);
        tv_submit_gold.setOnClickListener(ocl);
        tv_submit_my_app.setOnClickListener(ocl);

        RelativeLayout re1 = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.vip_tab_item, null);

        TextView tv_1 = re1.findViewById(R.id.tv_item);
        ImageView iv_1 = re1.findViewById(R.id.iv_item);
        TextView tv_1_course = re1.findViewById(R.id.tv_course);

        tv_1.setText("全站VIP");
        tv_1_course.setText("（不含微课）");
        iv_1.setImageResource(R.drawable.all_vip);

        RelativeLayout re2 = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.vip_tab_item, null);
        TextView tv_2 = re2.findViewById(R.id.tv_item);
        TextView tv_2_course = re2.findViewById(R.id.tv_course);
        ImageView iv_2 = re2.findViewById(R.id.iv_item);
        tv_2.setText("本应用VIP");
        tv_2_course.setText("（不含微课）");
        iv_2.setImageResource(R.drawable.forever_vip);

        RelativeLayout re3 = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.vip_tab_item, null);
        TextView tv_3 = re3.findViewById(R.id.tv_item);
        TextView tv_3_course = re3.findViewById(R.id.tv_course);
        ImageView iv_3 = re3.findViewById(R.id.iv_item);
        tv_3.setText("黄金VIP");
        tv_3_course.setText("（全部微课）");
        iv_3.setImageResource(R.drawable.gold_vip);


        //设置选项显示
        th.setup();
        th.addTab(th.newTabSpec(tabArray[0]).setIndicator(re2).setContent(R.id.appVipLayout));
        th.addTab(th.newTabSpec(tabArray[1]).setIndicator(re1).setContent(R.id.allVipLayout));
        th.addTab(th.newTabSpec(tabArray[2]).setIndicator(re3).setContent(R.id.goldVipLayout));

        final MyGridAdapter mga = new MyGridAdapter(mContext);
        gv_tequan.setAdapter(mga);
        localVip.setText(getResources().getString(R.string.vip_app));
        goldVip.setText(
                "1. 微课模块：新概念英语全四册Diana老师、Ace老师、Jimmy老师、Emma老师等；\n" +
                        "VOA英语Lisa博士、Rachel美语发音；四六级名师陈苏灵、李尚龙、石磊鹏、尹延、章敏等；\n" +
                        "托福、雅思、考研一、走遍美国和中职英语等名师讲解所有课程全部免费学习；\n" +
                        "2.尊享旗下全站会员特权。"
        );

        View view = th.getTabWidget().getChildAt(0);
        view.setBackgroundColor(selectColor);
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "tab1":
                        showSelectView(VIP_APP);
                        break;
                    case "tab2":
                        showSelectView(VIP_ALL);
                        break;
                    case "tab3":
                        showSelectView(VIP_GOLD);
                        break;
                }
            }
        });

        setJumpToWechat();

        //根据包名进行判断(样式)
        if (getPackageName().equals(Constant.package_conceptStory)
                ||getPackageName().equals(Constant.package_nce)){
            all_app_cb_1.setButtonDrawable(R.drawable.vip_checkbox_new);
            all_app_cb_2.setButtonDrawable(R.drawable.vip_checkbox_new);
            all_app_cb_3.setButtonDrawable(R.drawable.vip_checkbox_new);
            all_app_cb_4.setButtonDrawable(R.drawable.vip_checkbox_new);
            all_app_cb_5.setButtonDrawable(R.drawable.vip_checkbox_new);

            my_app_cb_1.setButtonDrawable(R.drawable.vip_checkbox_new);
            my_app_cb_2.setButtonDrawable(R.drawable.vip_checkbox_new);
            my_app_cb_3.setButtonDrawable(R.drawable.vip_checkbox_new);
            my_app_cb_4.setButtonDrawable(R.drawable.vip_checkbox_new);

            gold_app_cb_1.setButtonDrawable(R.drawable.vip_checkbox_new);
            gold_app_cb_2.setButtonDrawable(R.drawable.vip_checkbox_new);
            gold_app_cb_3.setButtonDrawable(R.drawable.vip_checkbox_new);
            gold_app_cb_4.setButtonDrawable(R.drawable.vip_checkbox_new);
        }

        if (getPackageName().equals(Constant.package_learnNewEnglish)){
            all_app_cb_1.setButtonDrawable(R.drawable.vip_checkbox_new2);
            all_app_cb_2.setButtonDrawable(R.drawable.vip_checkbox_new2);
            all_app_cb_3.setButtonDrawable(R.drawable.vip_checkbox_new2);
            all_app_cb_4.setButtonDrawable(R.drawable.vip_checkbox_new2);
            all_app_cb_5.setButtonDrawable(R.drawable.vip_checkbox_new2);

            my_app_cb_1.setButtonDrawable(R.drawable.vip_checkbox_new2);
            my_app_cb_2.setButtonDrawable(R.drawable.vip_checkbox_new2);
            my_app_cb_3.setButtonDrawable(R.drawable.vip_checkbox_new2);
            my_app_cb_4.setButtonDrawable(R.drawable.vip_checkbox_new2);

            gold_app_cb_1.setButtonDrawable(R.drawable.vip_checkbox_new2);
            gold_app_cb_2.setButtonDrawable(R.drawable.vip_checkbox_new2);
            gold_app_cb_3.setButtonDrawable(R.drawable.vip_checkbox_new2);
            gold_app_cb_4.setButtonDrawable(R.drawable.vip_checkbox_new2);
        }
    }


    public void showQQDialog(final Context mContext, View v) {


        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv1 = new TextView(mContext);
        TextView tv2 = new TextView(mContext);
        TextView tv3 = new TextView(mContext);

        tv1.setTextSize(16);
        tv2.setTextSize(16);
        tv3.setTextSize(16);

        tv1.setPadding(10, ScreenUtils.dp2px(mContext, 10), 10, 0);
        tv2.setPadding(10, ScreenUtils.dp2px(mContext, 20), 10, ScreenUtils.dp2px(mContext, 20));
        tv3.setPadding(10, 0, 10, ScreenUtils.dp2px(mContext, 10));

        tv1.setTextColor(Color.BLACK);
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);


        tv1.setText(String.format("%s用户群: %s", BrandUtil.getBrandChinese(), BrandUtil.getQQGroupNumber(mContext)));
        tv2.setText("编辑qq:" + BrandUtil.getServicQQEdeitor(mContext));
        tv3.setText("技术qq:" +BrandUtil.getServicQQTechnician(mContext));

        linearLayout.addView(tv1);
        linearLayout.addView(tv2);
        linearLayout.addView(tv3);

        tv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startQQGroup(mContext, BrandUtil.getQQGroupKey(mContext));
            }
        });

        tv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startQQ(mContext, BrandUtil.getServicQQEdeitor(mContext));
            }
        });

        tv3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startQQ(mContext, BrandUtil.getServicQQTechnician(mContext));
            }
        });


        BubbleLayout bl = new BubbleLayout(this);
        new BubbleDialog(mContext)
                .addContentView(linearLayout)
                .setClickedView(v)
                .setPosition(BubbleDialog.Position.BOTTOM)
                .calBar(true)
                .setBubbleLayout(bl)
                .show();


        bl.setLook(BubbleLayout.Look.TOP);
    }

    public void startQQ(Context context, String qq) {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq + "&version=1";
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(context, "您的设备尚未安装QQ客户端", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void startQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?" +
                "url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(context, "您的设备尚未安装QQ客户端", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setJumpToWechat() {
        String str1 = "打开微信小程序，";
        String str2 = "立即领取";
        String str3 = "3天全站会员试用";

        ClickableSpan span = setJumpClickableSpan();

        int start = str1.length();
        int end = str1.length() + str2.length();

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(str1);
        strBuilder.append(str2);
        strBuilder.append(str3);
        strBuilder.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        jumpToWechat1.setText(strBuilder);
        jumpToWechat2.setText(strBuilder);
        jumpToWechat3.setText(strBuilder);

        jumpToWechat1.setOnClickListener(ocl);
        jumpToWechat2.setOnClickListener(ocl);
        jumpToWechat3.setOnClickListener(ocl);

    }

    private ClickableSpan setJumpClickableSpan() {
        return new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Log.d("ClickableSpan", "OnClick");
                //jumpToWechatAction();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
        };
    }

    private void jumpToWechatAction() {
        if (Constant.package_concept2.equals(mContext.getPackageName())) {
            String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
            ToastFactory.showShort(mContext, "正在跳转到小程序");
            //572828703@qq.com 账号下的主要 appid，但因为跟小程序不在同一个账号下，
            //不能用于跳转小程序
//            String appId = "wx284c59e5e08db5d7";
            //iyubaios@sina.com  账号下，占用了CET4听力的坑位，因为CET4听力很长时间没更新了，只用于跳转小程序
            //相当于新概念全四册占用了 两个微信开放平台的坑位
            String appPayId = "wx6ce5ac6bcb03a302";
            IWXAPI api = WXAPIFactory.createWXAPI(mContext, appPayId);

            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
            // TODO: 2025/4/8 原来的小程序被注销了，如果需要的话可以换成下面那个，不需要的话直接隐藏掉
//            req.userName = "gh_a8c17ad593be";
            req.userName = "gh_16b544c61525";
            // TODO: 2024/2/21 这里根据孟裴瑞私聊，应该修改为/subpackage/guide/guide 这个
            req.path = "/subpackage/guide/guide?uid=" + uid + "&appid=" + Constant.APPID;////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
//            req.path = "/pages/index/index?uid=" + uid + "&appid=" + Constant.APPID;////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
            api.sendReq(req);
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            Dialog dialog;
            switch (msg.what) {
                case 0:
                    if (UserInfoManager.getInstance().isLogin()) {
                        final int month = msg.arg1;
                        buyAllVip(month);
                    } else {
                        LoginUtil.startToLogin(mContext);
                    }
                    break;
                case 1:
                    wettingDialog.dismiss();
                    dialog = new AlertDialog.Builder(mContext).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.alert_title).setMessage(R.string.alert_recharge_content).setPositiveButton(R.string.alert_btn_recharge, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            buyIyubi();
                        }
                    }).setNeutralButton(R.string.alert_btn_cancel, null).create();
                    dialog.show();// 如果要显示对话框，一定要加上这句
                    break;
                case 2:
                    wettingDialog.dismiss();
                    CustomToast.showToast(mContext, R.string.buy_success_update);
                    break;
                case 3:
                    wettingDialog.show();
                    break;
                case 4:
                    wettingDialog.dismiss();
                    dialog = new AlertDialog.Builder(mContext).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.alert_title).setMessage(R.string.alert_all_life_vip).setPositiveButton(R.string.alert_btn_recharge, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            buyIyubi();
                        }
                    }).setNeutralButton(R.string.alert_btn_cancel, null).create();
                    dialog.show();// 如果要显示对话框，一定要加上这句
                    break;
                default:
                    break;
            }
        }
    };

    private void setDefault() {
        all_app_vip_month = 1;
        setAllAppCheckBox(all_app_vip_month, 2);

        my_app_month = 1;
        setAllAppCheckBox(my_app_month, 1);

        gold_app_month = 1;
        setAllAppCheckBox(gold_app_month, 3);

    }

    private OnClickListener ocl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == all_app_re_1) {
                all_app_vip_month = 1;
                setAllAppCheckBox(all_app_vip_month, 2);
            } else if (v == all_app_re_2) {
                all_app_vip_month = 3;
                setAllAppCheckBox(all_app_vip_month, 2);
            } else if (v == all_app_re_3) {
                all_app_vip_month = 6;
                setAllAppCheckBox(all_app_vip_month, 2);
            } else if (v == all_app_re_4) {
                all_app_vip_month = 12;
                setAllAppCheckBox(all_app_vip_month, 2);
            } else if (v == all_app_re_5) {
                all_app_vip_month = 36;
                setAllAppCheckBox(all_app_vip_month, 2);

            } else if (v == my_app_re_1) {
                my_app_month = 1;
                setAllAppCheckBox(my_app_month, 1);

            } else if (v == my_app_re_2) {
                my_app_month = 60;
                setAllAppCheckBox(my_app_month, 1);

            } else if (v == my_app_re_3) {
                my_app_month = 12;
                setAllAppCheckBox(my_app_month, 1);

            } else if (v == my_app_re_4) {
                my_app_month = 36;
                setAllAppCheckBox(my_app_month, 1);

            } else if (v == btn_buyiyuba) {
                buyIyubi();
            } else if (v == gold_app_re_1) {
                gold_app_month = 1;
                setAllAppCheckBox(gold_app_month, 3);

            } else if (v == gold_app_re_2) {
                gold_app_month = 3;
                setAllAppCheckBox(gold_app_month, 3);

            } else if (v == gold_app_re_3) {
                gold_app_month = 6;
                setAllAppCheckBox(gold_app_month, 3);

            } else if (v == gold_app_re_4) {
                gold_app_month = 12;
                setAllAppCheckBox(gold_app_month, 3);

            } else if (v == tv_vip_html) {
                Intent intent = new Intent(NewVipCenterActivity.this, Web.class);
                intent.putExtra("url", "http://vip." + Constant.IYUBA_CN + "vip/vip.html");
                intent.putExtra("title", "全站VIP");
                startActivity(intent);
            } else if (v == tv_submit) {
                if (all_app_vip_month == -1) {
                    ToastUtil.showToast(mContext, "请选择需要开通的VIP!");
                } else {
                    handler.obtainMessage(0, all_app_vip_month, 0).sendToTarget();
                }

            } else if (v == tv_submit_my_app) {
                if (my_app_month == -1) {
                    ToastUtil.showToast(mContext, "请选择需要开通的VIP!");
                } else {
                    buyCurrVip(my_app_month);
                }

            } else if (v == tv_submit_gold) {

                if (gold_app_month == -1) {
                    ToastUtil.showToast(mContext, "请选择需要开通的VIP!");
                } else {
                    buyGoldVip(gold_app_month);
                }

            } else if (v == jumpToWechat1 || v == jumpToWechat2 || v == jumpToWechat3) {
                //跳转到小程序
                jumpToWechatAction();
            }
        }
    };
    private Button btn_buyiyuba;

    private void buyIyubi() {
        Intent intent = new Intent();
        intent.setClass(mContext, BuyIyubiActivity.class);
        intent.putExtra("title", "爱语币充值");
        startActivity(intent);
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }

    public View composeLayout(String s, int i) {
        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView iv = new ImageView(this);
        iv.setImageResource(i);
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 15, 0, 0);

        layout.addView(iv, lp);
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setText(s);
        tv.setTextColor(0xFF598aad);
        tv.setTextSize(14);
        LinearLayout.LayoutParams lpo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpo.setMargins(0, 0, 0, 15);
        layout.addView(tv, lpo);
        return layout;
    }


    //购买本应用会员
    private void buyCurrVip(int monthCount) {
        if (UserInfoManager.getInstance().isLogin()) {
            String vipPrice = "0";

            switch (monthCount) {
                case 1:
                    vipPrice = "30";
                    break;
                case 6:
                    vipPrice = "69";
                    break;
                case 12:
                    vipPrice = "99";
                    break;
                case 36:
                    vipPrice = "199";
                    break;
                case 60:
                    vipPrice = "299";
                    break;
            }

            String vipSubject = "本应用会员";
            String vipDesc = "购买"+ monthCount+"个月的"+vipSubject;
            String vipBody = "花费" + vipPrice + "元购买" + monthCount + "个月的"+vipSubject;

            Intent intent = PayOrderActivity.buildIntent(this,vipPrice,monthCount,10,vipSubject,vipBody,vipDesc,PayOrderActivity.Order_vip);
            startActivity(intent);
        } else {
            LoginUtil.startToLogin(mContext);
        }
    }

    //购买全站会员
    private void buyAllVip(int monthCount) {
        if (UserInfoManager.getInstance().isLogin()) {
            String vipPrice = "0";

            switch (monthCount) {
                case 1:
                    vipPrice = "50";
                    break;
                case 3:
                    vipPrice = "88";
                    break;
                case 6:
                    vipPrice = "198";
                    break;
                case 12:
                    vipPrice = "298";
                    break;
                case 36:
                    vipPrice = "588";
                    break;
            }

            String vipSubject = "全站会员";
            String vipDesc = "购买"+ monthCount+"个月的"+vipSubject;
            String vipBody = "花费" + vipPrice + "元购买" + monthCount + "个月的"+vipSubject;

            Intent intent = PayOrderActivity.buildIntent(this,vipPrice,monthCount,0,vipSubject,vipBody,vipDesc,PayOrderActivity.Order_vip);
            startActivity(intent);
        } else {
            LoginUtil.startToLogin(mContext);
        }
    }

    //购买黄金会员
    private void buyGoldVip(int monthCount) {
        if (UserInfoManager.getInstance().isLogin()) {
            String vipPrice = "0";

            switch (monthCount) {
                case 1:
                    vipPrice = "98";
                    break;
                case 3:
                    vipPrice = "288";
                    break;
                case 6:
                    vipPrice = "518";
                    break;
                case 12:
                    vipPrice = "998";
                    break;
            }

            String vipSubject = "黄金会员";
            String vipDesc = "购买"+monthCount+"个月的"+vipSubject;
            String vipBody = "花费" + vipPrice + "元购买" + monthCount + "个月的"+vipSubject;

            Intent intent = PayOrderActivity.buildIntent(this,vipPrice,monthCount,21,vipSubject,vipBody,vipDesc,PayOrderActivity.Order_vip);
            startActivity(intent);
        } else {
            LoginUtil.startToLogin(mContext);
        }

    }

    //显示选中的样式
    private void showSelectView(int vipType){
        for (int i = 0; i < 3; i++) {
            View tabView = th.getTabWidget().getChildAt(i);
            tabView.setBackgroundColor(unSelectColor);
        }

        View showView = th.getTabWidget().getChildAt(vipType);
        showView.setBackgroundColor(selectColor);
    }

    //设置选中按钮
    private void setCheckBoxFalse(int flag) {
        if (flag == 1) {  //本应用vip
            my_app_cb_1.setChecked(false);
            my_app_cb_2.setChecked(false);
            my_app_cb_3.setChecked(false);
            my_app_cb_4.setChecked(false);

        } else if (flag == 2) { // 全站vip
            all_app_cb_1.setChecked(false);
            all_app_cb_2.setChecked(false);
            all_app_cb_3.setChecked(false);
            all_app_cb_4.setChecked(false);
            all_app_cb_5.setChecked(false);

        } else if (flag == 3) { //黄金会员
            gold_app_cb_1.setChecked(false);
            gold_app_cb_2.setChecked(false);
            gold_app_cb_3.setChecked(false);
            gold_app_cb_4.setChecked(false);
        }
    }

    //设置所有的选中操作
    private void setAllAppCheckBox(int month, int flag) {
        setCheckBoxFalse(flag);

        if (flag == 1) {
            if (month == 1) {
                my_app_cb_1.setChecked(true);
            } else if (month == 6) {
                my_app_cb_2.setChecked(true);
            } else if (month == 12) {
                my_app_cb_3.setChecked(true);
            } else if (month == 36) {
                my_app_cb_4.setChecked(true);
            } else if (month == 60) {
                my_app_cb_2.setChecked(true);
            }
        } else if (flag == 2) {
            if (month == 1) {
                all_app_cb_1.setChecked(true);
            } else if (month == 3) {
                all_app_cb_2.setChecked(true);
            } else if (month == 6) {
                all_app_cb_3.setChecked(true);
            } else if (month == 12) {
                all_app_cb_4.setChecked(true);
            } else if (month == 36) {
                all_app_cb_5.setChecked(true);
            } else if (month == 60) {
                all_app_cb_3.setChecked(true);
            }
        } else if (flag == 3) {
            if (month == 1) {
                gold_app_cb_1.setChecked(true);
            } else if (month == 3) {
                gold_app_cb_2.setChecked(true);
            } else if (month == 6) {
                gold_app_cb_3.setChecked(true);
            } else if (month == 12) {
                gold_app_cb_4.setChecked(true);
            }
        }
    }
}