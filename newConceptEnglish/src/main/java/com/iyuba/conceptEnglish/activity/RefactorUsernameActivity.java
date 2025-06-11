//package com.iyuba.conceptEnglish.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.text.Html;
//import android.text.InputType;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.event.ChangeUsernameEvent;
//import com.iyuba.conceptEnglish.protocol.RefactorUsernameRequest;
//import com.iyuba.conceptEnglish.protocol.RefectorUsernameResponse;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.listener.OperateCallBack;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IErrorReceiver;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.ErrorResponse;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
//
//import org.greenrobot.eventbus.EventBus;
//
///**
// * 修改用户名界面
// *
// * @author chentong
// * @version 1.2
// */
//public class RefactorUsernameActivity extends Activity {
//    private static final int VERIFY_PASSWORD = 0;
//    private static final int REFACTOR_USERNAME = 2;
//    private static final int WAITING = 3;
//    private int mMethodChoose = VERIFY_PASSWORD;
//    private TextView mCurrentUsername;
//
//    private Button backBtn;
//    private Button registBtn, loginBtn;
//    private String userName, userPwd;
//    private EditText userPwdET;
//    private CheckBox autoLogin;
//    private CustomDialog cd;
//    private Context mContext;
//    private TextView findPassword;
//    private boolean boolRequestSuccess = false;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        setContentView(R.layout.activity_refactor_username);
//
//        CrashApplication.getInstance().addActivity(this);
//        mContext = this;
//
//        cd = WaittingDialog.showDialog(mContext);
//        // 密码 edit
//        userPwdET = findViewById(R.id.editText_userPwd);
//        mCurrentUsername=findViewById(R.id.current_username);
//        mCurrentUsername.setText("当前用户名："+ UserInfoManager.getInstance().getUserName());
//        //返回btn
//        backBtn = (Button) findViewById(R.id.button_back);
//        backBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        //登录btn
//        loginBtn = (Button) findViewById(R.id.button_login);
//        loginBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (mMethodChoose) {
//                    case VERIFY_PASSWORD:
//                        //验证密码
//                        checkPassword(userPwdET.getText().toString());
//                        break;
//                    case REFACTOR_USERNAME:
//                        modifyUsernameHttp(userPwdET.getText().toString(),
//                                UserInfoManager.getInstance().getLoginPassword());
//                        break;
//                    case WAITING:
//                        break;
//                }
//            }
//        });
//
//        //忘记密码btn
//        findPassword = (TextView) findViewById(R.id.find_password);
//        findPassword.setText(Html.fromHtml("<a href=\"http://m." + Constant.IYUBA_CN + "m_login/inputPhonefp.jsp?\">"
//                + getResources().getString(
//                R.string.login_find_password) + "</a>"));
//        findPassword.setMovementMethod(LinkMovementMethod.getInstance());
//    }
//
//    private void checkPassword(String inputPwd) {
//        //验证密码
//        if (TextUtils.isEmpty(userPwdET.getText().toString())) {
//            ToastUtil.show(RefactorUsernameActivity.this, "密码不能为空");
//            return;
//        }
//        if (userPwdET.getText().toString().length() < 6) {
//            ToastUtil.show(RefactorUsernameActivity.this, "密码不能小于6位");
//            return;
//        }
//        if (userPwdET.getText().toString().length() > 20) {
//            ToastUtil.show(RefactorUsernameActivity.this, "密码不能高于20位");
//            return;
//        }
//
//        loginBtn.setBackgroundResource(R.drawable.regbtn);
//        loginBtn.setText("验证中...");
//        mMethodChoose = WAITING;
//
//        //账号登录操作
//        UserInfoManager.getInstance().postRemoteAccountLogin(userName,userPwd, new UserinfoCallbackListener() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(mContext, "验证成功", Toast.LENGTH_SHORT).show();
//                loginBtn.setBackgroundResource(R.drawable.loginbtn);
//                loginBtn.setText("修改用户名");
//                mMethodChoose = REFACTOR_USERNAME;
//                userPwdET.setHint("请输入新的用户名");
//                userPwdET.setText("");
//                userPwdET.setInputType(InputType.TYPE_CLASS_TEXT);
//            }
//
//            @Override
//            public void onFail(String errorMsg) {
//                Toast.makeText(mContext, "密码验证失败", Toast.LENGTH_SHORT).show();
//                loginBtn.setBackgroundResource(R.drawable.loginbtn);
//                loginBtn.setText("验证");
//                mMethodChoose = VERIFY_PASSWORD;
//            }
//        });
//
//        /*String[] nameAndPwd = AccountManager.Instance(mContext).getUserNameAndPwd();
//        String userName = nameAndPwd[0];
////                                        String userPwd = nameAndPwd[1];
//        String userPwd = inputPwd;
//
//        AccountManager.Instance(mContext).loginNoPrompt(userName, userPwd,
//                new OperateCallBack() {
//                    @Override
//                    public void success(String userName) {
//                        // save username and password to memory and hard disk
//                        AccountManager.Instance(mContext).saveUserNameAndPwd(userName, userPwd);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //modify UI
//                                Toast.makeText(mContext, "验证成功", Toast.LENGTH_SHORT).show();
//                                loginBtn.setBackgroundResource(R.drawable.loginbtn);
//                                loginBtn.setText("修改用户名");
//                                mMethodChoose = REFACTOR_USERNAME;
//                                userPwdET.setHint("请输入新的用户名");
//                                userPwdET.setText("");
//                                userPwdET.setInputType(InputType.TYPE_CLASS_TEXT);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void fail(String message) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(mContext, "密码验证失败", Toast.LENGTH_SHORT).show();
//                                loginBtn.setBackgroundResource(R.drawable.loginbtn);
//                                loginBtn.setText("验证");
//                                mMethodChoose = VERIFY_PASSWORD;
//                            }
//                        });
//                    }
//                });*/
//
//    }
//
//    /**
//     * 开始网络请求 ， 修改用户名
//     *
//     * @param newUsername
//     */
//    private void modifyUsernameHttp(String newUsername, String userPwd) {
//
//        //验证密码
//        if (TextUtils.isEmpty(userPwdET.getText().toString())) {
//            ToastUtil.show(RefactorUsernameActivity.this, "用户名不能为空");
//            return;
//        }
//        if (userPwdET.getText().toString().length() < 3) {
//            ToastUtil.show(RefactorUsernameActivity.this, "用户名不能小于3位");
//            return;
//        }
//        if (userPwdET.getText().toString().length() > 20) {
//            ToastUtil.show(RefactorUsernameActivity.this, "用户名不能高于20位");
//            return;
//        }
//
//        loginBtn.setBackgroundResource(R.drawable.regbtn);
//        loginBtn.setText("修改中...");
//        mMethodChoose = WAITING;
//
//
//        String sign = com.iyuba.module.toolbox.MD5.getMD5ofStr("10012" + UserInfoManager.getInstance().getUserId() + "iyubaV2");
//
//        boolRequestSuccess = false;
//        //所有的操作结果都是在子线程中
//        ClientSession.Instace().asynGetResponse(
//                //请求地址和参数
//                new RefactorUsernameRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), UserInfoManager.getInstance().getLoginAccount(), newUsername, sign),
//                //返回正确时的流程
//                new IResponseReceiver() {
//                    @Override
//                    public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                        boolRequestSuccess = true;
//                        RefectorUsernameResponse refectorUsernameResponse = (RefectorUsernameResponse) response;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if ("121".equals(refectorUsernameResponse.result)) {
//                                    Toast.makeText(RefactorUsernameActivity.this,
//                                            "修改成功", Toast.LENGTH_SHORT).show();
//                                    //保存账户密码 到内存和硬盘中
//                                    UserInfoManager.getInstance().saveAccountAndPwd(newUsername,userPwd);
//
//                                    ChangeUsernameEvent event = new ChangeUsernameEvent();
//                                    event.newUsername = newUsername;
//                                    EventBus.getDefault().post(event);
//                                    RefactorUsernameActivity.this.finish();
//                                } else {
//                                    Toast.makeText(RefactorUsernameActivity.this,
//                                            refectorUsernameResponse.message, Toast.LENGTH_SHORT).show();
//                                    loginBtn.setBackgroundResource(R.drawable.loginbtn);
//                                    loginBtn.setText("修改用户名");
//                                    mMethodChoose = REFACTOR_USERNAME;
//                                }
//                            }
//                        });
//                    }
//                }
//                //返回错误时的流程
//                ,
//                new IErrorReceiver() {
//                    @Override
//                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
//                        if (!boolRequestSuccess) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(RefactorUsernameActivity.this,
//                                            "修改失败", Toast.LENGTH_SHORT).show();
//                                    loginBtn.setBackgroundResource(R.drawable.loginbtn);
//                                    loginBtn.setText("修改用户名");
//                                    mMethodChoose = REFACTOR_USERNAME;
//                                }
//                            });
//                        }
//                    }
//                }
//                , null);
//    }
//
//}
