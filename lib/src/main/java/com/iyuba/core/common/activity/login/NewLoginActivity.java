package com.iyuba.core.common.activity.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.InfoFulfillActivity;
import com.iyuba.core.common.activity.RegistByPhoneActivity;
import com.iyuba.core.common.activity.RegistSubmitActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.retrofitapi.result.MobCheckBean;
import com.iyuba.core.common.retrofitapi.result.VXTokenResponse;
import com.iyuba.core.common.util.PrivacyUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.WxLoginSession;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.lib.R;
import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.mob.secverify.OAuthPageEventCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.VerifyResult;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @title: 登录界面
 * @date: 2023/8/25 09:42
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewLoginActivity extends AppCompatActivity {
    //登录类型
    private static final String tag_loginType = "loginType";
    private String loginType = "loginType";
    //控件
    private View wxLoginView,accountLoginView;
    private EditText etUserName,etPassword;
    private TextView tvTitle,tvForgetPassword,tvSmallLogin,tvWxLogin,tvAccount,tvPrivacy;
    private Button btnBack,btnRegister,btnLogin;
    private CheckBox cbPrivacy;
    private RadioButton rbCheck;
    //加载弹窗
    private LoadingDialog loadingDialog;
    //微信小程序的token
    private String wxMiniToken = null;

    public static void start(Context context,String loginType){
        Intent intent = new Intent();
        intent.setClass(context,NewLoginActivity.class);
        intent.putExtra(tag_loginType,loginType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        initView();
        initData();
        initClick();

        switchType();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //关闭用户信息操作
        UserInfoManager.getInstance().destroyUserInfo();
        UserInfoManager.getInstance().destroyAccountLogin();
    }

    /************************初始化********************************/
    private void initView(){
        //切换类型界面
        wxLoginView = findViewById(R.id.loginType);
        accountLoginView = findViewById(R.id.accountLoginLayout);

        //账号登录
        etUserName = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);
        tvTitle = findViewById(R.id.tv_title);
        tvForgetPassword = findViewById(R.id.tv_forgetPassword);
        tvSmallLogin = findViewById(R.id.tv_smallLogin);
        cbPrivacy = findViewById(R.id.cb_privacy);
        btnBack = findViewById(R.id.btn_back);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        //登录类型选择
        tvWxLogin = findViewById(R.id.vx_login);
        tvAccount = findViewById(R.id.account_login);
        rbCheck = findViewById(R.id.agree_other);
        tvPrivacy = findViewById(R.id.agree_tv);


        //设置账号和密码
        String userName = UserInfoManager.getInstance().getLoginAccount();
        if (!TextUtils.isEmpty(userName)){
            etUserName.setText(userName);
        }
        String password = UserInfoManager.getInstance().getLoginPassword();
        if (!TextUtils.isEmpty(password)){
            etPassword.setText(password);
        }
    }

    private void initData(){
        loginType = getIntent().getStringExtra(tag_loginType);
        if (TextUtils.isEmpty(loginType)){
            loginType = LoginType.loginByAccount;
        }

        //隐私政策显示
        tvPrivacy.setText(setPrivacySpan());
        tvPrivacy.setMovementMethod(new LinkMovementMethod());

        //账号登录界面的隐私政策显示
        cbPrivacy.setText(setPrivacySpan());
        cbPrivacy.setMovementMethod(new LinkMovementMethod());
    }

    private void initClick(){
        btnBack.setOnClickListener(v->{
            finish();
        });
        btnRegister.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(this, RegistByPhoneActivity.class);
            startActivity(intent);
            finish();
        });
        btnLogin.setOnClickListener(v->{
            if (!cbPrivacy.isChecked()){
                ToastUtil.showToast(this,"请先阅读并同意隐私政策和用户协议");
                hideKeyBoard();
                return;
            }

            if (verifyAccountAndPsd()){
                String userName = etUserName.getText().toString().trim();
                String userPwd = etPassword.getText().toString().trim();

                accountLogin(userName,userPwd);
            }
        });
        tvSmallLogin.setOnClickListener(v->{
            if (!cbPrivacy.isChecked()){
                ToastUtil.showToast(this,"请先阅读并同意隐私政策和用户协议");
                hideKeyBoard();
                return;
            }

            toWxLogin();
        });
        tvForgetPassword.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(this,Web.class);
            intent.putExtra("url","http://m." + Constant.IYUBA_CN + "m_login/inputPhonefp.jsp");
            intent.putExtra("title","重置密码");
            startActivity(intent);
        });
        tvWxLogin.setOnClickListener(v->{
            if (!rbCheck.isChecked()){
                ToastUtil.showToast(this,"请先阅读并同意隐私政策和用户协议");
                hideKeyBoard();
                return;
            }

            toWxLogin();
        });
        tvAccount.setOnClickListener(v->{
            wxLoginView.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.VISIBLE);
        });
    }

    private SpannableStringBuilder setPrivacySpan(){
        String privacyStr = "隐私政策";
        String termStr = "用户协议";
        String showMsg = "我已阅读并同意"+privacyStr+"和"+termStr;

        SpannableStringBuilder spanStr = new SpannableStringBuilder();
        spanStr.append(showMsg);
        //隐私政策
        int privacyIndex = showMsg.indexOf(privacyStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(NewLoginActivity.this, Web.class);
                String url = PrivacyUtil.getSeparatedSecretUrl();
                intent.putExtra("url", url);
                intent.putExtra("title", privacyStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        },privacyIndex,privacyIndex+privacyStr.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用户协议
        int termIndex = showMsg.indexOf(termStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(NewLoginActivity.this, Web.class);
                String url = PrivacyUtil.getSeparatedProtocolUrl();
                intent.putExtra("url", url);
                intent.putExtra("title", termStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        },termIndex,termIndex+termStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    /**************************样式显示*****************************/
    //切换样式
    private void switchType(){
        if (loginType.equals(LoginType.loginByWXSmall)){
            //微信登录
            wxLoginView.setVisibility(View.VISIBLE);
            accountLoginView.setVisibility(View.GONE);

            getWXSmallToken();
        }else if (loginType.equals(LoginType.loginByVerify)){
            //秒验登录
            wxLoginView.setVisibility(View.GONE);
            tvSmallLogin.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.GONE);

            showVerify();
        }else {
            //账号登录
            wxLoginView.setVisibility(View.GONE);
            tvSmallLogin.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.VISIBLE);
        }
    }

    /******************************登录方式************************/
    /*************微信登录***********/
    //获取微信小程序的token
    private void getWXSmallToken(){
        startLoading("正在加载登录信息～");
        LoginPresenter.getWXSmallToken(new Observer<VXTokenResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(VXTokenResponse bean) {
                closeLoading();

                if (bean.getResult().equals("200")){
                    wxMiniToken = bean.getToken();

                    //这里判断微信是否已经安装
                    IWXAPI wxapi = WXAPIFactory.createWXAPI(NewLoginActivity.this, Constant.getWxKey());
                    if (!wxapi.isWXAppInstalled()){
                        wxMiniToken = null;

                        loginType = LoginType.loginByAccount;
                        switchType();
                    }
                }else {
                    wxMiniToken = null;

                    loginType = LoginType.loginByAccount;
                    switchType();
                }
            }

            @Override
            public void onError(Throwable e) {
                closeLoading();
                wxMiniToken = null;

                loginType = LoginType.loginByAccount;
                switchType();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //跳转到微信登录
    private void toWxLogin(){
        IWXAPI wxapi = WXAPIFactory.createWXAPI(NewLoginActivity.this,Constant.getWxKey());
        if (!wxapi.isWXAppInstalled()){
            ToastUtil.showToast(NewLoginActivity.this,"您还未安装微信客户端");
            hideKeyBoard();
            return;
        }

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName="gh_a8c17ad593be";
        req.path="/subpackage/getphone/getphone?token="+wxMiniToken+"&appid="+Constant.APPID;
        req.miniprogramType=WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
        wxapi.sendReq(req);

        //放在临时框架中，后面要用
        WxLoginSession.getInstance().setWxSmallToken(wxMiniToken);

        finish();
    }

    /*************秒验登录***********/
    //展示秒验功能
    private void showVerify(){
        if (SecVerify.isVerifySupport()&&TempDataManager.getInstance().getMobVerify()){
            startLoading("正在获取登录信息~");

            SecVerify.setDebugMode(false);
            SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
                @Override
                public void initCallback(OAuthPageEventResultCallback callback) {
                    callback.pageOpenCallback(new PageOpenedCallback() {
                        @Override
                        public void handle() {
                            closeLoading();
                        }
                    });
                }
            });
            SecVerify.verify(new VerifyCallback() {
                @Override
                public void onOtherLogin() {
                    //点击其他登录方式
                    closeLoading();
                    loginType = LoginType.loginByAccount;
                    switchType();
                }

                @Override
                public void onUserCanceled() {
                    //用户取消
                    closeLoading();
                    SecVerify.finishOAuthPage();
                    NewLoginActivity.this.finish();
                }

                @Override
                public void onComplete(VerifyResult result) {
                    closeLoading();

                    //调用完成
                    if (result!=null){
                        //这里调用接口，从服务器获取数据展示
                        checkMobDataFromServer(result);
                    }else {
                        loginType = LoginType.loginByAccount;
                        switchType();
                    }
                }

                @Override
                public void onFailure(VerifyException e) {
                    //调用失败
                    closeLoading();
                    loginType = LoginType.loginByAccount;
                    switchType();
                }
            });
        }else {
            closeLoading();
            loginType = LoginType.loginByAccount;
            switchType();
        }
    }

    //秒验和服务器查询
    private void checkMobDataFromServer(VerifyResult result){
        startLoading("正在获取用户信息～");
        LoginPresenter.getMobDataFromServer(NewLoginActivity.this, result.getToken(), result.getOpToken(), result.getOperator(), new Observer<MobCheckBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MobCheckBean bean) {
                //存在数据
                if (bean!=null){
                    //存在账号数据
                    if (bean.getIsLogin().equals("1")&&bean.getUserinfo()!=null){
                        //根据20001重新获取数据
                        UserInfoManager.getInstance().getRemoteUserInfo(bean.getUserinfo().getUid(), new UserinfoCallbackListener() {
                            @Override
                            public void onSuccess() {
                                closeLoading();
                                SecVerify.finishOAuthPage();

                                NewLoginActivity.this.finish();
                            }

                            @Override
                            public void onFail(String errorMsg) {
                                closeLoading();
                                SecVerify.finishOAuthPage();

                                hideKeyBoard();
                                loginType = LoginType.loginByAccount;
                                switchType();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(NewLoginActivity.this,errorMsg);
                                    }
                                });
                            }
                        });
                        return;
                    }

                    //不存在账号数据，但是存在电话号
                    if (bean.getRes()!=null&&!TextUtils.isEmpty(bean.getRes().getPhone())){
                        closeLoading();
                        SecVerify.finishOAuthPage();

                        Intent intent = new Intent();
                        intent.setClass(NewLoginActivity.this, RegistSubmitActivity.class);
                        intent.putExtra("phoneNumb", bean.getRes().getPhone());
                        intent.putExtra("isSecVerify",true);
                        //合成随机数据显示
                        intent.putExtra("username",getRandomByPhone(bean.getRes().getPhone()));
                        intent.putExtra("password",bean.getRes().getPhone().substring(bean.getRes().getPhone().length()-6));
                        startActivity(intent);

                        NewLoginActivity.this.finish();
                        return;
                    }
                }

                closeLoading();
                SecVerify.finishOAuthPage();

                hideKeyBoard();
                loginType = LoginType.loginByAccount;
                switchType();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(NewLoginActivity.this,"获取登录信息失败，请手动登录或注册账号");
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                SecVerify.finishOAuthPage();
                hideKeyBoard();
                loginType = LoginType.loginByAccount;
                switchType();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(NewLoginActivity.this,"获取登录信息失败异常("+e.getMessage()+")");
                    }
                });
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /*************账号登录************/
    //验证账号和密码
    private boolean verifyAccountAndPsd(){
        String userName = etUserName.getText().toString().trim();
        String userPwd = etPassword.getText().toString().trim();

        if (userName.length() < 3) {
            ToastUtil.showToast(this,getResources().getString(R.string.login_check_effective_user_id));
            hideKeyBoard();
            return false;
        }

        if (userPwd.length() == 0) {
            ToastUtil.showToast(this,getResources().getString(R.string.login_check_user_pwd_null));
            hideKeyBoard();
            return false;
        }

        if (userPwd.length() < 6 || userPwd.length() > 20) {
            ToastUtil.showToast(this,getResources().getString(R.string.login_check_user_pwd_constraint));
            hideKeyBoard();
            return false;
        }

        return true;
    }

    //账号登录
    private Disposable accountLoginDis;

    private void accountLogin(String userName,String userPwd){
        startLoading("正在登录～");
        hideKeyBoard();

        Log.d("用户登录", "账号登录-准备登录");

        UserInfoManager.getInstance().postRemoteAccountLogin(userName, userPwd, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                closeLoading();
                isToFulfillInfo();
            }

            @Override
            public void onFail(String errorMsg) {
                hideKeyBoard();
                closeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(NewLoginActivity.this,errorMsg);
                    }
                });
            }
        });
//        AccountManager.Instance(this).login(userName, userPwd, new OperateCallBack() {
//            @Override
//            public void success(String message) {
////                if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
////                    AccountManager.Instance(NewLoginActivity.this).saveUserNameAndPwd(userName, userPwd);
////                } else {
////                    AccountManager.Instance(NewLoginActivity.this).saveUserNameAndPwd("", "");
////                }
//
//                //保存账号密码
////                AccountManager.Instance(NewLoginActivity.this).saveUserNameAndPwd(userName, userPwd);
//                UserManager.getInstance().setUserName(userName);
//                UserManager.getInstance().setPassword(userPwd);
//
//                //再次强制使用20001接口处理，真sb啊，这个接口返回不了vip的信息
//                getUserInfo(String.valueOf(AccountManager.getInstance().getUserId()));
//            }
//
//            @Override
//            public void fail(String message) {
//                closeLoading();
//                ToastUtil.showToast(NewLoginActivity.this,ToastMsg.showLoginMsg(message,null));
//            }
//        });
    }

    //判断是否跳转到用户信息完善界面
    private void isToFulfillInfo() {
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        if (ConfigManager.Instance().getAccountIsShowFulfill(uid)) {
            startActivity(new Intent(this, InfoFulfillActivity.class));
        }

        NewLoginActivity.this.finish();
    }

    /*******************************辅助功能***********************/
    //开启加载弹窗
    private void startLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
        }

        if (loadingDialog.isShowing()){
            return;
        }

        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void closeLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //获取用户信息
//    private Disposable userInfoSub;
//    private void getUserInfo(String userId){
//        RxUtil.unsubscribe(userInfoSub);
//        DataHelpManager.getInstance().getUserInfo(userId)
//                .subscribe(new Observer<UserInfoForLogin>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        userInfoSub = d;
//                    }
//
//                    @Override
//                    public void onNext(UserInfoForLogin userInfo) {
//                        if (userInfo!=null&&!TextUtils.isEmpty(userInfo.username)){
//                            LoginResponse loginResponse = new LoginResponse();
//                            loginResponse.uid = userId;
//                            loginResponse.username = userInfo.username;
//                            loginResponse.nickName = userInfo.nickname;
//                            loginResponse.imgsrc = userInfo.middle_url;
//                            loginResponse.validity = userInfo.expireTime;
//                            loginResponse.amount = userInfo.amount;
//                            loginResponse.isteacher = userInfo.isteacher;
//                            loginResponse.money = userInfo.money;
//                            loginResponse.vip = userInfo.vipStatus;
//                            AccountManager.Instance(NewLoginActivity.this).Refresh(loginResponse);
//
//                            //保存用户的账号和密码
//                            EventBus.getDefault().post(new VipChangeEvent());
//
//                            //关闭弹窗
//                            closeLoading();
//                            isToFulfillInfo();
//                        }else {
//                            closeLoading();
//                            ToastUtil.showToast(NewLoginActivity.this,"获取用户信息失败，请重试～");
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        closeLoading();
//                        ToastUtil.showToast(NewLoginActivity.this,"获取用户信息失败，请重试～");
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }


    //根据手机号随机生成用户名称
    private String getRandomByPhone(String phone){
        StringBuilder builder = new StringBuilder();
        builder.append("iyuba");

        //随机数
        for (int i = 0; i < 4; i++) {
            int randomInt = (int) (Math.random()*10);
            builder.append(randomInt);
        }

        String lastPhone = null;
        if (phone.length()>4){
            lastPhone = phone.substring(phone.length()-4);
        }else {
            String time = String.valueOf(System.currentTimeMillis());
            lastPhone = time.substring(time.length()-4);
        }
        builder.append(lastPhone);
        return builder.toString();
    }

    //隐藏键盘(用于处理鸿蒙手机上的显示问题)
    private void hideKeyBoard(){
        if (isKeyBoardOpen()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //判断键盘是否弹窗
    private boolean isKeyBoardOpen(){
        int height = getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return height*2/3 > rect.bottom;
    }
}
