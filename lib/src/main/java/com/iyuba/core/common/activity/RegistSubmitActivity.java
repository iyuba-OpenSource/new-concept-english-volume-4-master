package com.iyuba.core.common.activity;

/**
 * 手机注册完善内容界面
 *
 * @author czf
 * @version 1.0
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestPhoneNumRegister;
import com.iyuba.core.common.protocol.message.ResponsePhoneNumRegister;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.core.lil.user.util.UserInfoErrorMsg;
import com.iyuba.lib.R;

public class RegistSubmitActivity extends BasisActivity {
    private static final String HANDLER_TOAST_STRING = "msg";
    private Context mContext;
    private EditText userNameEditText, passWordEditText;
    private Button submitButton, backButton;
    private String phonenumb, userName, passWord;
    private CustomDialog wettingDialog;
    private boolean boolIsSecVerify = false;
    private TextView mPasswordRemindTxt;
    private ImageView mIsCanSee;
    private boolean boolCanSee = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        CrashApplication.getInstance().addActivity(this);
        setContentView(R.layout.regist_layout_phone_regist);
        backButton = (Button) findViewById(R.id.button_back);
        userNameEditText = (EditText) findViewById(R.id.regist_phone_username);
        passWordEditText = (EditText) findViewById(R.id.regist_phone_paswd);
        mPasswordRemindTxt = findViewById(R.id.password_remind_Txt);
        submitButton = (Button) findViewById(R.id.regist_phone_submit);
        mIsCanSee = findViewById(R.id.is_can_see);

        Intent intent = getIntent();
        phonenumb = intent.getStringExtra("phoneNumb");
        boolIsSecVerify = intent.getBooleanExtra("isSecVerify", false);
        if (boolIsSecVerify) {
            userName = intent.getStringExtra("username");
            passWord = intent.getStringExtra("password");
            userNameEditText.setText(userName);
            passWordEditText.setText(passWord);
            mPasswordRemindTxt.setVisibility(View.VISIBLE);
        }
        wettingDialog = WaittingDialog.showDialog(mContext);
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (verification()) {// 验证通过
                    // 开始注册
                    handler.sendEmptyMessage(0);// 在handler中注册
                }
            }

        });
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIsCanSee.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                canSeePwd();
            }
        });
    }

    /**
     * 验证
     */
    public boolean verification() {
        userName = userNameEditText.getText().toString();
        passWord = passWordEditText.getText().toString();
        if (!checkUserId(userName)) {
            userNameEditText.setError(mContext
                    .getString(R.string.regist_check_username_1));
            return false;
        }
        if (!checkUserName(userName)) {
            userNameEditText.setError(mContext
                    .getString(R.string.regist_check_username_2));
            return false;
        }
        if (!checkUserPwd(passWord)) {
            passWordEditText.setError(mContext
                    .getString(R.string.regist_check_userpwd_1));
            return false;
        }
        return true;
    }

    /**
     * check the pwd
     */
    private void canSeePwd() {
        if (boolCanSee) {
            // can not  -> can
            passWordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mIsCanSee.setImageResource(R.drawable.icon_regist_cant_see);
            boolCanSee = false;
        } else {
            // can  -> can not
            passWordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            mIsCanSee.setImageResource(R.drawable.icon_regist_can_see);
            boolCanSee = true;
        }
    }

    /**
     * 匹配用户名1
     *
     * @param userId
     * @return
     */
    public boolean checkUserId(String userId) {
        if (userId.length() < 3 || userId.length() > 20)
            return false;
        return true;
    }

    /**
     * 匹配用户名2 验证非手机号 邮箱号
     *
     * @param userId
     * @return
     */
    public boolean checkUserName(String userId) {
        if (userId
                .matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
            return false;
        }
        if (userId.matches("^(1)\\d{10}$")) {
            return false;
        }

        return true;
    }

    /**
     * 匹配密码
     *
     * @param userPwd
     * @return
     */
    public boolean checkUserPwd(String userPwd) {
        if (userPwd.length() < 6 || userPwd.length() > 20)
            return false;
        return true;
    }

    private void regist() {
        ExeProtocol.exe(new RequestPhoneNumRegister(userName, passWord, phonenumb), new ProtocolResponse() {
            @Override
            public void finish(BaseHttpResponse bhr) {
                ResponsePhoneNumRegister rr = (ResponsePhoneNumRegister) bhr;
                if (rr.isRegSuccess) {
                    ConfigManager.Instance().setIsNewRegister(true);

                    handler.sendEmptyMessage(9);
                } else if (rr.resultCode.equals("112")) {
                    // 提示用户已存在
                    handler.sendEmptyMessage(3);
                } else {
                    Message message = new Message();
                    message.what = 11;
                    Bundle bundle = new Bundle();
                    bundle.putString(HANDLER_TOAST_STRING, rr.message + "");
                    message.setData(bundle);
                    handler.sendMessage(message);// 弹出错误提示
                }
            }

            @Override
            public void error() {
                handler.sendEmptyMessage(1);// 弹出错误提示
            }
        });
    }

    private void gotoLogin() {
        UserInfoManager.getInstance().postRemoteAccountLogin(userName, passWord, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                SettingConfig.Instance().setAutoLogin(true);
                startActivity(new Intent(mContext, InfoFulfillActivity.class));
                finish();
            }

            @Override
            public void onFail(String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(RegistSubmitActivity.this, UserInfoErrorMsg.showRegisterMsg(errorMsg));
                    }
                });
            }
        });
//        AccountManager.Instance(mContext).login(userName, passWord,
//                new OperateCallBack() {
//                    @Override
//                    public void success(String result) {
//                        // TODO Auto-generated method stub
//                        /* if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码 */
//                        if (true) {
//                            SettingConfig.Instance().setAutoLogin(true);
//                            AccountManager.Instance(mContext)
//                                    .saveUserNameAndPwd(userName, passWord);
//                        } else {
//                            AccountManager.Instance(mContext)
//                                    .saveUserNameAndPwd("", "");
//                        }
////                        Intent intent = new Intent(
////                                mContext, UpLoadImage.class);
////                        intent.putExtra("regist", true);
////                        startActivity(intent);
//                        startActivity(new Intent(mContext, InfoFulfillActivity.class));
//                        finish();
//                    }
//
//                    @Override
//                    public void fail(String message) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO 自动生成的方法存根
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    handler.sendEmptyMessage(5);
                    regist();
                    break;
                case 1:
                    handler.sendEmptyMessage(4);
                    CustomToast.showToast(mContext, R.string.network_error);
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.regist_success);
                    break;
                case 3:
                    handler.sendEmptyMessage(4);
                    CustomToast.showToast(mContext, R.string.regist_userid_exist);
                    break;
                case 4:
                    wettingDialog.dismiss();
                    break;
                case 5:
                    wettingDialog.show();
                    break;
                case 6:
                    gotoLogin();
                    break;
                case 9:
                    CustomToast.showToast(mContext, R.string.regist_success);
                    wettingDialog.dismiss();
                    gotoLogin();
                    break;
                case 11:
                    handler.sendEmptyMessage(4);
                    String toastMsg = msg.getData().getString(HANDLER_TOAST_STRING);
                    CustomToast.showToast(mContext, toastMsg);
                    break;
                default:
                    break;
            }
        }
    };
}
