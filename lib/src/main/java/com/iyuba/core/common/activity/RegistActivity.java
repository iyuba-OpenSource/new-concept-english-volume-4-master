//package com.iyuba.core.common.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//
//import androidx.annotation.NonNull;
//
//import android.text.SpannableStringBuilder;
//import android.text.Spanned;
//import android.text.TextPaint;
//import android.text.method.LinkMovementMethod;
//import android.text.method.ScrollingMovementMethod;
//import android.text.style.ClickableSpan;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.base.BasisActivity;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.listener.OperateCallBack;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.base.RegistRequest;
//import com.iyuba.core.common.protocol.base.RegistResponse;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.util.PrivacyUtil;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.me.activity.UpLoadImage;
//import com.iyuba.lib.R;
//
//import timber.log.Timber;
//
///**
// * 注册界面
// *
// * @author chentong
// * @version 1.1 修改内容 更新API
// */
//public class RegistActivity extends BasisActivity {
//    private Context mContext;
//    private Button backBtn;
//    private EditText userName, userPwd, reUserPwd, email;
//    private Button regBtn;
//    private String userNameString;
//    private String userPwdString;
//    private String reUserPwdString;
//    private String emailString;
//    private boolean send = false;
//    private CustomDialog wettingDialog;
//    private TextView protocol;
//    private CheckBox check_box;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mContext = this;
//        setContentView(R.layout.regist_layout);
//        CrashApplication.getInstance().addActivity(this);
//        wettingDialog = WaittingDialog.showDialog(mContext);
//        backBtn = (Button) findViewById(R.id.button_back);
//        backBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        userName = (EditText) findViewById(R.id.editText_userId);
//        userPwd = (EditText) findViewById(R.id.editText_userPwd);
//        reUserPwd = (EditText) findViewById(R.id.editText_reUserPwd);
//        email = (EditText) findViewById(R.id.editText_email);
//        regBtn = (Button) findViewById(R.id.button_regist);
//        regBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (verification()) { // 验证通过
//                    // 开始注册
//                    if (!send) {
//                        send = true;
//                        handler.sendEmptyMessage(5);
//                        regist();
//                    } else {
//                        handler.sendEmptyMessage(7);
//                    }
//                }
//            }
//        });
////		protocol = (TextView) findViewById(R.id.protocol);
////		protocol.setText(Html.fromHtml("我已阅读并同意<a href=\"http://app." + Constant.IYUBA_CN + "ios/protocol.html\">使用条款和隐私政策</a>"));
////		protocol.setMovementMethod(LinkMovementMethod.getInstance());
//
//
//        protocol = (TextView) findViewById(R.id.protocol);
//        protocol.setText(setSpan());
//        protocol.setMovementMethod(ScrollingMovementMethod.getInstance());
//        protocol.setMovementMethod(LinkMovementMethod.getInstance());
//        check_box = findViewById(R.id.check_box);
//
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
//    /**
//     * 验证
//     */
//    public boolean verification() {
//        userNameString = userName.getText().toString();
//        userPwdString = userPwd.getText().toString();
//        reUserPwdString = reUserPwd.getText().toString();
//        emailString = email.getText().toString();
//        if (!checkBox()) {
//            return false;
//        }
//        if (userNameString.length() == 0) {
//            userName.setError(mContext
//                    .getString(R.string.regist_check_username_1));
//            return false;
//        }
//        if (!checkUserId(userNameString)) {
//            userName.setError(mContext
//                    .getString(R.string.regist_check_username_1));
//            return false;
//        }
//        if (!checkUserName(userNameString)) {
//            userName.setError(mContext
//                    .getString(R.string.regist_check_username_2));
//            return false;
//        }
//        if (userPwdString.length() == 0) {
//            userPwd.setError(mContext
//                    .getString(R.string.regist_check_userpwd_1));
//            return false;
//        }
//        if (!checkUserPwd(userPwdString)) {
//            userPwd.setError(mContext
//                    .getString(R.string.regist_check_userpwd_1));
//            return false;
//        }
//        if (!reUserPwdString.equals(userPwdString)) {
//            reUserPwd.setError(mContext
//                    .getString(R.string.regist_check_reuserpwd));
//            return false;
//        }
//        if (emailString.length() == 0) {
//            email.setError(getResources().getString(
//                    R.string.regist_check_email_1));
//            return false;
//        }
//        if (!emailCheck(emailString)) {
//            email.setError(mContext.getString(R.string.regist_check_email_2));
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 匹配用户名
//     *
//     * @param userId
//     * @return
//     */
//    public boolean checkUserId(String userId) {
//        if (userId.length() < 3 || userId.length() > 20)
//            return false;
//        return true;
//    }
//
//    /**
//     * 匹配用户名2 验证非手机号 邮箱号
//     *
//     * @param userId
//     * @return
//     */
//    public boolean checkUserName(String userId) {
//        if (userId
//                .matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
//            return false;
//        }
//        if (userId.matches("^(1)\\d{10}$")) {
//            return false;
//        }
//
//        return true;
//    }
//
//    /**
//     * 匹配密码
//     *
//     * @param userPwd
//     * @return
//     */
//    public boolean checkUserPwd(String userPwd) {
//        if (userPwd.length() < 6 || userPwd.length() > 20)
//            return false;
//        return true;
//    }
//
//    /**
//     * email格式匹配
//     *
//     * @param email
//     * @return
//     */
//    public boolean emailCheck(String email) {
//        return email
//                .matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$");
//    }
//
//    private boolean checkBox() {
//        if (check_box.isChecked()) {
//            return true;
//        } else {
//            ToastUtil.showToast(mContext, "请同意使用条款和隐私政策");
//            return false;
//        }
//    }
//
//    Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    finish();
//                    break;
//                case 1: // 弹出错误信息
//                    CustomToast.showToast(mContext, R.string.regist_email_used);
//                    break;
//                case 2:
//                    CustomToast.showToast(mContext, R.string.check_network);
//                    break;
//                case 3:
//                    CustomToast.showToast(mContext, R.string.regist_userid_exist);
//                    break;
//                case 4:
//                    CustomToast.showToast(mContext, msg.obj.toString());
//                    break;
//                case 5:
//                    wettingDialog.show();
//                    break;
//                case 6:
//                    wettingDialog.dismiss();
//                    break;
//                case 7:
//                    CustomToast.showToast(mContext, R.string.regist_operating);
//                    break;
//            }
//        }
//    };
//
//    private void regist() {
//        ExeProtocol.exe(new RegistRequest(userName.getText().toString(),
//                        userPwd.getText().toString(), email.getText().toString()),
//                new ProtocolResponse() {
//
//                    @Override
//                    public void finish(BaseHttpResponse bhr) {
//                        // TODO Auto-generated method stub
//                        RegistResponse rr = (RegistResponse) bhr;
//                        send = false;
//                        handler.sendEmptyMessage(6);
//                        //新注册不提示同步
//                        if (rr.result.equals("111")) {
//                            ConfigManager.Instance().setIsNewRegister(true);
//                            Looper.prepare();
//                            AccountManager.Instance(mContext).login(
//                                    userName.getText().toString(),
//                                    userPwd.getText().toString(),
//                                    new OperateCallBack() {
//                                        @Override
//                                        public void success(String result) {
//                                            // TODO Auto-generated method stub
////                                            if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
//                                            if (true) {
//                                                SettingConfig.Instance().setAutoLogin(true);
//                                                AccountManager.Instance(mContext).saveUserNameAndPwd(
//                                                        userName.getText().toString(),
//                                                        userPwd.getText().toString());
//                                            } else {
//                                                AccountManager.Instance(mContext)
//                                                        .saveUserNameAndPwd("",
//                                                                "");
//                                            }
////											Intent intent = new Intent(
////													mContext, UpLoadImage.class);
////											intent.putExtra("regist", true);
////											startActivity(intent);
//                                            startActivity(new Intent(mContext, InfoFulfillActivity.class));
//                                            handler.sendEmptyMessage(0);
//                                        }
//
//                                        @Override
//                                        public void fail(String message) {
//                                            // TODO Auto-generated method stub
//
//                                        }
//                                    });
//                            Looper.loop();
//                        } else if (rr.result.equals("112")) {
//                            handler.sendEmptyMessage(3);
//                        } else if (rr.result.equals("114")) {
//                            handler.obtainMessage(4, rr.message).sendToTarget();
//                        } else {
//                            handler.sendEmptyMessage(1);
//                        }
//                    }
//
//                    @Override
//                    public void error() {
//                        // TODO Auto-generated method stub
//                        send = false;
//                        handler.sendEmptyMessage(2);
//                        handler.sendEmptyMessage(6);
//                    }
//                });
//    }
//
//    @Override
//    protected void onResume() {
//        // TODO 自动生成的方法存根
//        super.onResume();
//        findViewById(R.id.button_regist_phone).setOnClickListener(
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // TODO 自动生成的方法存根
//                        startActivity(new Intent(mContext,
//                                RegistByPhoneActivity.class));
//                        finish();
//                    }
//                });
//    }
//}
