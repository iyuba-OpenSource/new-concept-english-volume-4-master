//package com.iyuba.core.common.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.text.Html;
//import android.text.SpannableStringBuilder;
//import android.text.Spanned;
//import android.text.TextPaint;
//import android.text.method.LinkMovementMethod;
//import android.text.method.ScrollingMovementMethod;
//import android.text.style.ClickableSpan;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.listener.OperateCallBack;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.base.RegistResponse;
//import com.iyuba.core.common.protocol.base.RegisterRequest;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.util.TelNumMatch;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.me.activity.UpLoadImage;
//import com.iyuba.lib.R;
//
//import timber.log.Timber;
//
//public class RegisterNewActivity extends AppCompatActivity {
//
//    private Context mContext;
//    private Button backBtn;
//    private EditText userName, userPwd, reUserPwd, email, phoneNum;
//    private Button regBtn;
//    private String userNameString;
//    private String userPwdString;
//    private String reUserPwdString;
//    private String emailString;
//    private String phoneNumString;
//    private boolean send = false;
//    private CustomDialog wettingDialog;
//    private TextView protocol;
//    private CheckBox check_box;
//    private TextView toEmailButton, registerWebButton;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registe_new);
//
//        mContext = this;
//        CrashApplication.getInstance().addActivity(this);
//        wettingDialog = WaittingDialog.showDialog(mContext);
//        backBtn = (Button) findViewById(R.id.button_back);
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        userName = (EditText) findViewById(R.id.editText_userId);
//        userPwd = (EditText) findViewById(R.id.editText_userPwd);
//        reUserPwd = (EditText) findViewById(R.id.editText_reUserPwd);
//        email = (EditText) findViewById(R.id.editText_email);
//        phoneNum = (EditText) findViewById(R.id.editText_phone);
//        regBtn = (Button) findViewById(R.id.button_regist);
//        regBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (verification()) { // 验证通过
//                    // 开始注册
//                    if (!send) {
//                        send = true;
//                        handler.sendEmptyMessage(5);
//                        register();
//                    } else {
//                        handler.sendEmptyMessage(7);
//                    }
//                }
//            }
//        });
//
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                Intent intent = new Intent(mContext, Web.class);
//                if ("北京爱语吧".equals(Constant.COMPANY_NAME)) {
//                    intent.putExtra("url","http://"+Constant.userSpeech+ "api/protocol.jsp?apptype=" + Constant.APPName);
//                } else if ("爱语言".equals(Constant.COMPANY_NAME)) {
//                    intent.putExtra("url","http://"+Constant.userSpeech+ "api/ailanguageprotocol666.jsp?apptype="+Constant.APPName);
//                } else {
//                    Timber.e("ERROR: Unexpected company name! %s", Constant.APPName);
//                    intent.putExtra("url","http://"+Constant.userSpeech+ "api/protocol.jsp?apptype=" + Constant.APPName);
//                }
//                intent.putExtra("title", "用户协议及隐私政策");
//                mContext.startActivity(intent);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
//            }
//        };
//        String privacy1 = "我已阅读并同意";
//        String privacy2 = "使用条款和隐私政策";
//
//        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
//        strBuilder.append(privacy1);
//        strBuilder.append(privacy2);
//        strBuilder.setSpan(clickableSpan, privacy1.length(), privacy1.length() + privacy2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        protocol = findViewById(R.id.protocol);
//        protocol.setText(strBuilder);
//        protocol.setMovementMethod(ScrollingMovementMethod.getInstance());
//        protocol.setMovementMethod(LinkMovementMethod.getInstance());
//        check_box = findViewById(R.id.check_box);
//
//        toEmailButton = findViewById(R.id.regist_email);
//        toEmailButton.setText(Html.fromHtml("<a" + " href=\"http://"+Constant.userSpeech+"\">" + getString(R.string.regist_phone_toemail) + "</a>"));
//        toEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, RegistActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });
//
//
//        registerWebButton = findViewById(R.id.regist_web);
//        registerWebButton.setText(Html.fromHtml("<a href=\"http://m." + Constant.IYUBA_CN + "m_login/inputPhone.jsp" + "\">" + getString(R.string.regist_phone_web) + "</a>"));
//        registerWebButton.setMovementMethod(LinkMovementMethod.getInstance());
//
//    }
//
//    /**
//     * 验证
//     */
//    public boolean verification() {
//        userNameString = userName.getText().toString();
//        userPwdString = userPwd.getText().toString();
//        reUserPwdString = reUserPwd.getText().toString();
//        emailString = email.getText().toString();
//        phoneNumString = phoneNum.getText().toString();
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
//        if (phoneNumString.length() == 0) {
//            phoneNum.setError("手机号不能为空");
//            return false;
//        }
//        if (!checkPhoneNum(phoneNumString)) {
//            phoneNum.setError("手机号输入错误");
//            return false;
//        }
//
//        return true;
//    }
//
//    public boolean checkPhoneNum(String phNum) {
//        if (phNum.length() < 11)
//            return false;
//        TelNumMatch match = new TelNumMatch(phNum);
//        int flag = match.matchNum();
//        /*不check 号码的正确性，只check 号码的长度*/
//		/*if (flag == 1 || flag == 2 || flag == 3) {
//			return true;
//		} else {
//			return false;
//		}*/
//        if (flag == 5) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    /**
//     * 匹配用户名
//     *
//     * @param userId 用户名
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
//     * @param email 邮箱
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
//                case 8:
//                    CustomToast.showToast(mContext, "手机号已注册");
//                    break;
//                case 9:
//                    CustomToast.showToast(mContext, "出现未知错误");
//                    break;
//            }
//        }
//    };
//
//    private void register() {
//        ExeProtocol.exe(new RegisterRequest(userName.getText().toString(),
//                        userPwd.getText().toString(), email.getText().toString(), phoneNum.getText().toString()),
//                new ProtocolResponse() {
//
//                    @Override
//                    public void finish(BaseHttpResponse bhr) {
//                        RegistResponse rr = (RegistResponse) bhr;
//                        send = false;
//                        handler.sendEmptyMessage(6);
//                        if (rr.result.equals("111")) {
//                            Looper.prepare();
//                            AccountManager.Instance(mContext).login(
//                                    userName.getText().toString(),
//                                    userPwd.getText().toString(),
//                                    new OperateCallBack() {
//                                        @Override
//                                        public void success(String result) {
//                                            if (SettingConfig.Instance()
//                                                    .isAutoLogin()) {// 保存账户密码
//                                                AccountManager
//                                                        .Instance(mContext)
//                                                        .saveUserNameAndPwd(
//                                                                userName.getText().toString(),
//                                                                userPwd.getText().toString());
//                                            } else {
//                                                AccountManager.Instance(mContext)
//                                                        .saveUserNameAndPwd("",
//                                                                "");
//                                            }
//                                            Intent intent = new Intent(
//                                                    mContext, UpLoadImage.class);
//                                            intent.putExtra("regist", true);
//                                            startActivity(intent);
//                                            handler.sendEmptyMessage(0);
//                                        }
//
//                                        @Override
//                                        public void fail(String message) {
//
//                                        }
//                                    });
//                            Looper.loop();
//                        } else if (rr.result.equals("112")) {
//                            handler.sendEmptyMessage(3);//用户名已存在
//                        } else if (rr.result.equals("113")) {
//                            handler.sendEmptyMessage(1);//邮箱已注册
//                        } else if (rr.result.equals("114")) {
//                            handler.obtainMessage(4, rr.message).sendToTarget();//用户名长度错误
//                        } else if (rr.result.equals("115")) {
//                            handler.sendEmptyMessage(8);//手机号已注册
//                        }else {
//                            handler.sendEmptyMessage(9);//未知错误
//                        }
//                    }
//
//                    @Override
//                    public void error() {
//                        send = false;
//                        handler.sendEmptyMessage(2);
//                        handler.sendEmptyMessage(6);
//                    }
//                });
//    }
//}
