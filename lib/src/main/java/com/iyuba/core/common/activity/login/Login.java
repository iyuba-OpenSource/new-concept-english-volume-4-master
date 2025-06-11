//package com.iyuba.core.common.activity.login;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Html;
//import android.text.SpannableStringBuilder;
//import android.text.Spanned;
//import android.text.TextPaint;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.text.style.ClickableSpan;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.LibRequestFactory;
//import com.iyuba.core.common.activity.InfoFulfillActivity;
//import com.iyuba.core.common.activity.RegistByPhoneActivity;
//import com.iyuba.core.common.activity.Web;
//import com.iyuba.core.common.activity.WxLoginEvent;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.listener.OperateCallBack;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.protocol.base.LoginResponse;
//import com.iyuba.core.common.retrofitapi.UidResponse;
//import com.iyuba.core.common.retrofitapi.VXLoginService;
//import com.iyuba.core.common.retrofitapi.result.VXTokenResponse;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.common.util.CommonUtil;
//import com.iyuba.core.common.util.LogUtils;
//import com.iyuba.core.common.util.MD5;
//import com.iyuba.core.common.util.PrivacyUtil;
//import com.iyuba.core.common.util.SecVerifyUtil;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.event.VipChangeEvent;
//import com.iyuba.core.lil.WxLoginSession;
//import com.iyuba.core.networkbean.UserInfoForLogin;
//import com.iyuba.core.util.RxTimer;
//import com.iyuba.lib.R;
//import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
//import com.mob.secverify.SecVerify;
//import com.mob.secverify.ui.component.CommonProgressDialog;
//import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.HashMap;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
///**
// * 登录界面
// *
// * @author chentong
// * @version 1.2
// * 修改内容 API更新; userinfo引入; VIP更新方式变化
// * 主分支，不需要秒验操作！！！
// */
//public class Login extends Activity {
//    private Button backBtn;
//    private Button registBtn, loginBtn;
//    private String userName, userPwd;
//    private EditText userNameET, userPwdET;
//    private CheckBox autoLogin;
//    private CustomDialog cd;
//    private Context mContext;
//    private TextView findPassword;
//    private RelativeLayout mRelativeWrapper;
//    private VXLoginService service;
//
//    //用户信息
//    private LoadingDialog userDialog;
//    //登陆选项窗口
//    private Dialog selectDialog;
//
//    private SecVerifyUtil.IVerifyCallback callback=new SecVerifyUtil.IVerifyCallback() {
//        @Override
//        public void onSuccess() {
//            // if success,then close this activity
//            Login.this.finish();
//        }
//
//        @Override
//        public void onFail(int failCode, String descirbeMessage) {
//
//        }
//
//        @Override
//        public void onOtherMethod(int failCode, String descirbeMessage) {
//            //if the other method that user choose , then show the common method
//            mRelativeWrapper.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        public void onControlDialog(boolean isShow) {
//            if (isShow){
//                CommonProgressDialog.showProgressDialog(mContext);
//            }else {
//
//                CommonProgressDialog.dismissProgressDialog();
//            }
//        }
//
//        @Override
//        public void onCloseLoginActivity() {
//            Login.this.finish();
//        }
//
//    };
//    private String wxMiniToken;
//
//    public static void start(Context context){
//        Intent intent = new Intent();
//        intent.setClass(context,Login.class);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.login_layout);
//        EventBus.getDefault().register(this);
//
//        openDialog("正在加载登录信息～");
//
//        //先获取token，没有token则不显示微信登录
//        int protocol = 10011;
//        String sign = MD5.getMD5ofStr(protocol + Constant.APPID + "iyubaV2");
//        String url="http://api.iyuba.com.cn/v2/api.iyuba";
//        HashMap<String, String> map = new HashMap<>();
//        map.put("platform","android");
//        map.put("format","json");
//        map.put("protocol",String.valueOf(protocol));
//        map.put("appid",Constant.APPID);
//        map.put("sign",sign);
//        service=initService();
//        service.getToken(url,map).enqueue(new Callback<VXTokenResponse>() {
//            @Override
//            public void onResponse(Call<VXTokenResponse> call, Response<VXTokenResponse> response) {
//                closeDialog();
//                wxMiniToken=response.body().getToken();
//
//                //这里判断微信是否已经安装
//                IWXAPI wxapi = WXAPIFactory.createWXAPI(Login.this,Constant.getWxKey());
//                if (!wxapi.isWXAppInstalled()){
//                    wxMiniToken = null;
//                }
//
//                initView();
//            }
//
//            @Override
//            public void onFailure(Call<VXTokenResponse> call, Throwable t) {
//                closeDialog();
//                wxMiniToken = null;
//
//                initView();
//            }
//        });
//    }
//
//    private void goVxLogin(){
//        /*int protocol = 10011;
//        String sign = MD5.getMD5ofStr(protocol + Constant.APPID + "iyubaV2");
//        String url="http://api.iyuba.com.cn/v2/api.iyuba";
//        HashMap<String, String> map = new HashMap<>();
//        map.put("platform","android");
//        map.put("format","json");
//        map.put("protocol",String.valueOf(protocol));
//        map.put("appid",Constant.APPID);
//        map.put("sign",sign);
//        service=initService();
//        service.getToken(url,map).enqueue(new Callback<VXTokenResponse>() {
//            @Override
//            public void onResponse(Call<VXTokenResponse> call, Response<VXTokenResponse> response) {
//                IWXAPI wxapi = WXAPIFactory.createWXAPI(Login.this, "wx6ce5ac6bcb03a302");
//                if (!wxapi.isWXAppInstalled()){
//                    ToastUtil.showToast(Login.this,"您还未安装微信客户端");
//                    return;
//                }
//                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
//                req.userName="gh_a8c17ad593be";
//                wxMiniToken=response.body().getToken();
//                req.path="/subpackage/getphone/getphone?token="+response.body().getToken()+"&appid="+Constant.APPID;
//                req.miniprogramType=WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW;
//                wxapi.sendReq(req);
//            }
//
//            @Override
//            public void onFailure(Call<VXTokenResponse> call, Throwable t) {
//                ToastUtil.showToast(Login.this,"请求失败\n"+ t);
//            }
//        });*/
//
//        IWXAPI wxapi = WXAPIFactory.createWXAPI(Login.this,Constant.getWxKey());
//        if (!wxapi.isWXAppInstalled()){
//            ToastUtil.showToast(Login.this,"您还未安装微信客户端");
//            return;
//        }
//
//        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
//        req.userName="gh_a8c17ad593be";
//        req.path="/subpackage/getphone/getphone?token="+wxMiniToken+"&appid="+Constant.APPID;
//        req.miniprogramType=WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
//        wxapi.sendReq(req);
//
//        //放在临时框架中
//        WxLoginSession.getInstance().setWxSmallToken(wxMiniToken);
//
//        finish();
//    }
//
//    private void showSelectLoginType(){
//        if (selectDialog==null){
//            selectDialog = new Dialog(mContext,R.style.Dialog_Fullscreen);
//            View v = View.inflate(this,R.layout.select_login_type_layout,null);
//            TextView vxLogin=v.findViewById(R.id.vx_login);
//            TextView accountLogin=v.findViewById(R.id.account_login);
//            TextView agreeTv=v.findViewById(R.id.agree_tv);
//            RadioButton agreeOther=v.findViewById(R.id.agree_other);
//            vxLogin.setOnClickListener(view -> {
//                if (!agreeOther.isChecked()){
//                    ToastUtil.showToast(mContext, "请同意使用条款和隐私政策");
//                    return;
//                }
//                goVxLogin();
//            });
//            accountLogin.setOnClickListener(view -> {
//                if (!agreeOther.isChecked()){
//                    ToastUtil.showToast(mContext, "请同意使用条款和隐私政策");
//                    return;
//                }
//                selectDialog.dismiss();
//            });
//            agreeTv.setText(setSpan());
//            agreeTv.setMovementMethod(LinkMovementMethod.getInstance());
//            selectDialog.setOnCancelListener(d -> finish());
//            selectDialog.setContentView(v);
//        }
//
//        selectDialog.show();
//    }
//
//    private SpannableStringBuilder setSpan() {
//        String privacy1 = "我已阅读并同意";
//        String privacy2 = "使用协议和隐私政策";
//
//        int start = privacy1.length();
//        int end = start + 4;
//        int start2 = end + 1;
//        int end2 = start2 + 4;
//
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                Intent intent = new Intent(mContext, Web.class);
//                String url = PrivacyUtil.getSeparatedProtocolUrl();
//                intent.putExtra("url", url);
//                intent.putExtra("title", "使用协议");
//                mContext.startActivity(intent);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
//            }
//        };
//        ClickableSpan clickableSpan2 = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                Intent intent = new Intent(mContext, Web.class);
//                String url = PrivacyUtil.getSeparatedSecretUrl();
//                intent.putExtra("url", url);
//                intent.putExtra("title", "隐私政策");
//                mContext.startActivity(intent);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
//            }
//        };
//
//        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
//        strBuilder.append(privacy1);
//        strBuilder.append(privacy2);
//        strBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        strBuilder.setSpan(clickableSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        return strBuilder;
//    }
//
//    private VXLoginService initService(){
//        Retrofit retrofit = new Retrofit.Builder()
//                //使用自定义的mGsonConverterFactory
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl("http://apis.baidu.com/txapi/")
//                .build();
//        return retrofit.create(VXLoginService.class);
//    }
//
//
//    /**
//     * 根据是否满足一键登录的条件进行初始化
//     */
//    private void initAboutSecVerify() {
//        if (SecVerify.isVerifySupport()
//                && CommonUtil.getMobileDataState(mContext, null)
//                && CommonUtil.hasSimCard(this)) {
//            SecVerifyUtil secVerifyUtil = new SecVerifyUtil(callback);
//            secVerifyUtil.gotoVerify();
//            mRelativeWrapper.setVisibility(View.GONE);
//            Login.this.finish();
//        } else {
//            mRelativeWrapper.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//    }
//
//    /**
//     * 验证
//     */
//    public boolean verification() {
//        userName = userNameET.getText().toString();
//        userPwd = userPwdET.getText().toString();
//        if (userName.length() < 3) {
//            userNameET.setError(getResources().getString(
//                    R.string.login_check_effective_user_id));
//            return false;
//        }
//
//        if (userPwd.length() == 0) {
//            userPwdET.setError(getResources().getString(
//                    R.string.login_check_user_pwd_null));
//            return false;
//        }
//        if (!checkUserPwd(userPwd)) {
//            userPwdET.setError(getResources().getString(
//                    R.string.login_check_user_pwd_constraint));
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 匹配密码
//     *
//     * @param userPwd 密码
//     * @return
//     */
//    public boolean checkUserPwd(String userPwd) {
//        if (userPwd.length() < 6 || userPwd.length() > 20)
//            return false;
//        return true;
//    }
//
//    Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    userNameET.setText(userName);
//                    userPwdET.setText(userPwd);
//                    break;
//                case 1:
//                    cd.show();
//                    break;
//                case 2:
//                    cd.dismiss();
//                    break;
//                case 3:
//                    //判断是否跳转到个人信息完善界面
//                    isToFulfillInfo();
//                    break;
//            }
//        }
//    };
//
//    @Override
//    public void finish() {
//        super.finish();
//        if (AccountManager.Instance(mContext).userName == null) {
//            SettingConfig.Instance().setAutoLogin(false);
//        }
//    }
//
//    public void loginM() {
//        if (verification()) {
//            handler.sendEmptyMessage(1);
//            AccountManager.Instance(mContext).login(userName, userPwd,
//                    new OperateCallBack() {
//                        @Override
//                        public void success(String userName) {
//                            if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
//                                AccountManager.Instance(mContext).saveUserNameAndPwd(userName, userPwd);
//                            } else {
//                                AccountManager.Instance(mContext).saveUserNameAndPwd("", "");
//                            }
//                            handler.sendEmptyMessage(2);
//                            handler.sendEmptyMessage(3);
//                        }
//
//                        @Override
//                        public void fail(String message) {
//                            handler.sendEmptyMessage(2);
//                        }
//                    });
//        }
//    }
//
//    private void isToFulfillInfo() {
//
//        //针对临时的包名处理
//        if (!"com.iyuba.peiyin".equals(getPackageName())){
//            String uid = ConfigManager.Instance().getUserId();
//            if (ConfigManager.Instance().getAccountIsShowFulfill(uid)) {
//                mContext.startActivity(new Intent(mContext, InfoFulfillActivity.class));
//            }
//        }
//
//        Login.this.finish();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//
//        if (selectDialog!=null){
//            selectDialog.dismiss();
//        }
//    }
//
//    //初始化界面
//    private void initView(){
//        CrashApplication.getInstance().addActivity(this);
//        mContext = this;
//        mRelativeWrapper = findViewById(R.id.relativeWrapper);
//        //首先判断是不是满足一键登录的条件，如果满足就调用一键登录
//        // TODO: 2023/3/13 这里要求屏蔽秒验，使用小程序登录
////        boolean noSecVerify=getIntent().getBooleanExtra("noSecVerify",false);
////        if (!noSecVerify){
////            initAboutSecVerify();
////        }else {
////            mRelativeWrapper.setVisibility(View.VISIBLE);
////        }
//        mRelativeWrapper.setVisibility(View.VISIBLE);
//
//        cd = WaittingDialog.showDialog(mContext);
//        userNameET = findViewById(R.id.editText_userId);
//        userPwdET = findViewById(R.id.editText_userPwd);
//        if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
//            String[] nameAndPwd = AccountManager.Instance(mContext)
//                    .getUserNameAndPwd();
//            userName = nameAndPwd[0];
//            userPwd = nameAndPwd[1];
//            handler.sendEmptyMessage(0);
//        }
//
//        autoLogin = findViewById(R.id.checkBox_autoLogin);
//        autoLogin.setChecked(SettingConfig.Instance().isAutoLogin());
//        autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//                SettingConfig.Instance().setAutoLogin(isChecked);
//            }
//        });
//        if (!autoLogin.isChecked()) {
//            autoLogin.setChecked(true);
//            SettingConfig.Instance().setAutoLogin(true);
//        }
//
//        backBtn = (Button) findViewById(R.id.button_back);
//        backBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        loginBtn = (Button) findViewById(R.id.button_login);
//        loginBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //用户退出登录
//                loginM();
//            }
//        });
//
//        registBtn = (Button) findViewById(R.id.button_regist);
//        registBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //修改为手机注册优先
//                Intent intent = new Intent();
////			    intent.setClass(mContext, RegistActivity.class);
//                intent.setClass(mContext, RegistByPhoneActivity.class);
////                intent.setClass(mContext, RegisterNewActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        TextView smallAppLogin = findViewById(R.id.small_app_login);
//        if (TextUtils.isEmpty(wxMiniToken)){
//            smallAppLogin.setVisibility(View.GONE);
//        }else {
//            smallAppLogin.setVisibility(View.VISIBLE);
//        }
//        smallAppLogin.setOnClickListener(view -> goVxLogin());
//        findPassword = (TextView) findViewById(R.id.find_password);
//        findPassword.setText(Html.fromHtml("<a href=\"http://m." + Constant.IYUBA_CN + "m_login/inputPhonefp.jsp?\">"
//                + getResources().getString(
//                R.string.login_find_password) + "</a>"));
//        findPassword.setMovementMethod(LinkMovementMethod.getInstance());
//
//        //这边进行了限制处理
//        if ((getPackageName().equals(Constant.package_concept2)//concept2包名
//                ||getPackageName().equals(Constant.package_newconcepttop)//top包名
//                ||getPackageName().equals(Constant.package_englishfm)//englishfm包名
//                ||getPackageName().equals(Constant.package_nce))//nce的包名
//                &&!TextUtils.isEmpty(wxMiniToken)){
//            showSelectLoginType();
//            smallAppLogin.setVisibility(View.VISIBLE);
//        }else {
//            smallAppLogin.setVisibility(View.GONE);
//        }
//    }
//
//    //开启弹窗
//    private void openDialog(String msg){
//        if (userDialog == null){
//            userDialog = new LoadingDialog(this);
//        }
//
//        if (userDialog.isShowing()){
//            return;
//        }
//
//        userDialog.setMessage(msg);
//        userDialog.show();
//    }
//
//    //关闭弹窗
//    private void closeDialog(){
//        if (userDialog!=null&&userDialog.isShowing()){
//            userDialog.dismiss();
//        }
//    }
//
//    /**************这里根据要求，将小程序登陆的回调操作放在了mefragment中***************/
//    //微信登录回调
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(WxLoginEvent event){
//        if (wxMiniToken==null){
//            return;
//        }
//
//
//        if (event.getErrCode() == 0){
//            getUserInfo();
//        }else {
//            closeDialog();
//            ToastUtil.showToast(this,"微信一键登录失败，请重试或者更换登录方式");
//        }
//    }
//
//    //重试次数
//    private int userInfoRetryCount = 0;
//    //获取用户信息
//    private void getUserInfo(){
//        openDialog("正在获取用户信息~");
//
//        int protocol=10016;
//        String text=protocol + Constant.APPID + wxMiniToken + "iyubaV2";
//        String sign=MD5.getMD5ofStr(text);
//        String url="http://api.iyuba.com.cn/v2/api.iyuba";
//        HashMap<String, String> map = new HashMap<>();
//        map.put("platform","android");
//        map.put("format","json");
//        map.put("appid",Constant.APPID);
//        map.put("protocol",String.valueOf(protocol));
//        map.put("token",wxMiniToken);
//        map.put("sign",sign);
//        service.getUid(url,map).enqueue(new Callback<UidResponse>() {
//            @Override
//            public void onResponse(Call<UidResponse> call, Response<UidResponse> response) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getUserInfo(response.body().getUid());
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call<UidResponse> call, Throwable t) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeDialog();
//                        ToastUtil.showToast(Login.this,"获取用户信息失败，请重试");
//                    }
//                });
//            }
//        });
//    }
//
//    //重新获取用户信息操作
//    private void retryUserInfo(String uid){
//        if (userInfoRetryCount<3){
//            userInfoRetryCount++;
//
//            LogUtils.d("重试次数--"+userInfoRetryCount);
//
//            RxTimer.timer(500L, new RxTimer.RxAction() {
//                @Override
//                public void action(long number) {
//                    getUserInfo(uid);
//                }
//            });
//        }else {
//            userInfoRetryCount = 0;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    closeDialog();
//                    ToastUtil.showToast(Login.this,"获取用户信息失败，请重试");
//                }
//            });
//        }
//    }
//
//    private void getUserInfo(String userId) {
//        String protocal = "20001";
//        String sign = com.iyuba.core.me.pay.MD5.getMD5ofStr("20001" + userId + "iyubaV2");
//        Call<UserInfoForLogin> call = LibRequestFactory.getUserInfoApiForLogin().userInfoApiForLogin("android",
//                "json", protocal, Constant.APPID, userId, userId, sign);
//        call.enqueue(new retrofit2.Callback<UserInfoForLogin>() {
//            @Override
//            public void onResponse(Call<UserInfoForLogin> call, retrofit2.Response<UserInfoForLogin> response) {
//                if (response.isSuccessful()
//                        && response.body() != null) {
//                    UserInfoForLogin userInfo = response.body();
//                    LoginResponse loginResponse = new LoginResponse();
//
//                    if (TextUtils.isEmpty(userInfo.username)){
//                        retryUserInfo(userId);
//                        return;
//                    }
//
//                    closeDialog();
//
//                    loginResponse.uid = userId;
//                    loginResponse.username = userInfo.username;
//                    loginResponse.nickName = userInfo.nickname;
//                    loginResponse.imgsrc = userInfo.middle_url;
//                    loginResponse.validity = userInfo.expireTime;
//                    loginResponse.amount = userInfo.amount;
//                    loginResponse.isteacher = userInfo.isteacher;
//                    loginResponse.money = userInfo.money;
//                    loginResponse.vip = userInfo.vipStatus;
//                    AccountManager.Instance(mContext).Refresh(loginResponse);
//                    finish();
//                } else {
//                    if (userInfoRetryCount>=3){
//                        closeDialog();
//
//                        ConfigManager.Instance().setFulFillLoginStatus();
//                        AccountManager.Instance(mContext).setLoginState(1);
//                        EventBus.getDefault().post(new VipChangeEvent());
//                    }else {
//                        retryUserInfo(userId);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserInfoForLogin> call, Throwable t) {
//                if (userInfoRetryCount>=3){
//                    closeDialog();
//
//                    ConfigManager.Instance().setFulFillLoginStatus();
//                    AccountManager.Instance(mContext).setLoginState(1);
//                    EventBus.getDefault().post(new VipChangeEvent());
//                }else {
//                    retryUserInfo(userId);
//                }
//            }
//        });
//    }
//}