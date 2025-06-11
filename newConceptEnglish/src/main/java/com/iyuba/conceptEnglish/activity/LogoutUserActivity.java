package com.iyuba.conceptEnglish.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.conceptEnglish.R;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.ClearUserResponse;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.ResLibUtil;
import com.iyuba.core.lil.util.SPUtil;
import com.iyuba.module.toolbox.RxUtil;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 修改用户名界面
 *
 * @author chentong
 * @version 1.2
 */
public class LogoutUserActivity extends BaseStackActivity {
    private TextView mCurrentUsername;

    private Button backBtn;
    private Button registBtn, loginBtn;
    private String userName, userPwd;
    private EditText userPwdET;
    private CheckBox autoLogin;
    private CustomDialog cd;
    private Context mContext;
    private TextView findPassword;
    private boolean boolRequestSuccess = false;

    private DataManager mDataManager;

    private Disposable mDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_logout_user);

        CrashApplication.getInstance().addActivity(this);
        mContext = this;
        mDataManager=DataManager.getInstance();

        cd = WaittingDialog.showDialog(mContext);
        // 密码 edit
        userPwdET = findViewById(R.id.editText_userPwd);
        mCurrentUsername=findViewById(R.id.current_username);
        mCurrentUsername.setText("当前用户名："+ UserInfoManager.getInstance().getUserName());
        //返回btn
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //登录btn
        loginBtn = (Button) findViewById(R.id.button_login);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userPwdET.getText().toString())){
                    Toast.makeText(LogoutUserActivity.this,"密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                clearUser(UserInfoManager.getInstance().getUserName(), userPwdET.getText().toString());
            }
        });

        //忘记密码btn
        findPassword = (TextView) findViewById(R.id.find_password);
        findPassword.setText(Html.fromHtml("<a href=\"http://m." + Constant.IYUBA_CN + "m_login/inputPhonefp.jsp?\">"
                + getResources().getString(
                R.string.login_find_password) + "</a>"));
        findPassword.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void clearUser(String username, String password) {
        RxUtil.dispose(mDisposable);
        mDisposable = mDataManager.clearUser(username, password)
                .compose(RxUtil.<ClearUserResponse>applySingleIoScheduler())
                .subscribe(new Consumer<ClearUserResponse>() {
                    @Override
                    public void accept(ClearUserResponse list) throws Exception {
                        //success
                        clearSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        // failure
                        Toast.makeText(LogoutUserActivity.this,"注销失败!请确认密码及网络正确。",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void clearSuccess() {
        //注销账号后退出登录
//        InitPush.getInstance().unRegisterToken(mContext, Integer.parseInt(ConfigManager.Instance().getUserId()));
        UserInfoManager.getInstance().clearUserInfo();
        //清空账号和密码信息
        UserInfoManager.getInstance().saveAccountAndPwd("","");

//        SettingConfig.Instance().setHighSpeed(false);
        CustomToast.showToast(mContext, R.string.account_cancellation_success);
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("账户注销成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //刷新用户信息
                        EventBus.getDefault().post(new VipChangeEvent());

                        //这里直接回退到我的界面
                        StackUtil.getInstance().finish(LogoutUserActivity.class);
                        StackUtil.getInstance().finish(AboutActivity.class);
                        StackUtil.getInstance().finish(SetActivity.class);
                    }
                })
                .show();
    }
}
