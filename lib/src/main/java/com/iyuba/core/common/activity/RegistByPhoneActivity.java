package com.iyuba.core.common.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.retrofitapi.YzPhoneNumber;
import com.iyuba.core.common.retrofitapi.result.ApiRequestFactory;
import com.iyuba.core.common.retrofitapi.result.YzPhoneResult;
import com.iyuba.core.common.util.PrivacyUtil;
import com.iyuba.core.common.util.SmsContent;
import com.iyuba.core.common.util.TelNumMatch;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.lib.R;
import com.mob.MobSDK;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * 手机注册界面
 *
 * @author czf
 * @version 1.0
 */
//@RuntimePermissions
public class RegistByPhoneActivity extends BasisActivity {
    private Context mContext;
    private EditText phoneNum, messageCode;
    private Button getCodeButton;
    private TextView toEmailButton, registWebButton;
    private Button backBtn;
    private String phoneNumString = "", messageCodeString = "";
    private Timer timer;
    private TextView protocol;
    private EventHandler eh;
    private TimerTask timerTask;
    private SmsContent smsContent;
    private CustomDialog waittingDialog;
    private EditTextWatch editTextWatch;
    private CheckBox check_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        editTextWatch = new EditTextWatch();
        setContentView(R.layout.regist_layout_phone);
        CrashApplication.getInstance().addActivity(this);
        waittingDialog =  WaittingDialog.showDialog(mContext);
        messageCode = (EditText) findViewById(R.id.regist_phone_code);
        messageCode.addTextChangedListener(editTextWatch);
        phoneNum = (EditText) findViewById(R.id.regist_phone_numb);
        phoneNum.addTextChangedListener(editTextWatch);
        getCodeButton = (Button) findViewById(R.id.regist_getcode);
        nextstep_unfocus = (Button) findViewById(R.id.nextstep_unfocus);
        nextstep_unfocus.setEnabled(false);
        nextstep_focus = (Button) findViewById(R.id.nextstep_focus);

        //网页注册
        registWebButton = findViewById(R.id.regist_web);
        //这里网页注册也没有必要的
        registWebButton.setVisibility(View.GONE);
//        registWebButton.setText(Html.fromHtml("<a href=\"http://m." + Constant.IYUBA_CN + "m_login/inputPhone.jsp" + "\">" + getString(R.string.regist_phone_web) + "</a>"));
//        registWebButton.setMovementMethod(LinkMovementMethod.getInstance());

        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Timber.d("SMSSDK event: %d", event);
                Timber.d("SMSSDK result: %d", result);
                Timber.d("SMSSDK data: %s", data.toString());

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handlerSms.sendMessage(msg);
            }
        };
        MobSDK.init(this, Constant.getMobKey(), Constant.getMobSecret());

        SMSSDK.registerEventHandler(eh);
        smsContent = new SmsContent(RegistByPhoneActivity.this, handler_verify);
//        protocol = (TextView) findViewById(R.id.protocol);
//        protocol.setText(Html.fromHtml("我已阅读并同意<a href=\"https://"+Constant.userSpeech + Constant.IYUBA_CN + "api/protocol.jsp?apptype=" + TextAttr.encode(getString(R.string.app_name)) + "\">使用条款和隐私政策</a>"));
//        protocol.setMovementMethod(LinkMovementMethod.getInstance());
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toEmailButton = (TextView) findViewById(R.id.regist_email);
        //这里没必要邮箱注册了
        toEmailButton.setVisibility(View.INVISIBLE);
//        toEmailButton.setText(Html.fromHtml("<a" + " href=\"http://"+Constant.userSpeech+"\">" + getString(R.string.regist_phone_toemail) + "</a>"));
//        toEmailButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent();
//                intent.setClass(mContext, RegistActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//            }
//        });

        getCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //这里不要获取权限，直接发送即可
                /*String key="申请权限";
                if (!preferences.getBoolean(key,false)){
                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle("提示")
                            .setMessage("获取发送以及接受短信权限")
                            .setPositiveButton("同意", (d, i) -> {
                                preferences.edit().putBoolean(key,true).apply();
                                if (ContextCompat.checkSelfPermission(RegistByPhoneActivity.this,Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
                                    String[] array = {Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS};
                                    ActivityCompat.requestPermissions(RegistByPhoneActivity.this,array,123);
                                }
                            })
                            .setNegativeButton("取消", (d, i) -> ToastUtil.showToast(mContext,"申请权限被拒绝"))
                            .create();
                    dialog.show();
                    return;
                }*/

                permissionRequst();
            }
        });
        nextstep_focus.setEnabled(true);
        nextstep_focus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verification()) {
                    SMSSDK.submitVerificationCode("86", phoneNumString, messageCode.getText().toString());
                } else {
                    ToastUtil.showToast(mContext, "验证码不能为空");
                    hideKeyBoard();
                }
            }
        });



        protocol = (TextView) findViewById(R.id.protocol);
        protocol.setText(setSpan());
        protocol.setMovementMethod(ScrollingMovementMethod.getInstance());
        protocol.setMovementMethod(LinkMovementMethod.getInstance());

        check_box = findViewById(R.id.check_box);

        //设置按钮状态
        setGetCodeBtnState(true);
    }

    private SpannableStringBuilder setSpan() {
        String privacy1 = "我已阅读并同意";
        String privacy2 = "使用协议和隐私政策";

        int start = privacy1.length();
        int end = start + 4;
        int start2 = end + 1;
        int end2 = start2 + 4;

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(mContext, Web.class);
                String url = PrivacyUtil.getSeparatedProtocolUrl();
                intent.putExtra("url", url);
                intent.putExtra("title", "使用协议");
                mContext.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(mContext, Web.class);
                String url = PrivacyUtil.getSeparatedSecretUrl();
                intent.putExtra("url", url);
                intent.putExtra("title", "隐私政策");
                mContext.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
        };

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(privacy1);
        strBuilder.append(privacy2);
        strBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        strBuilder.setSpan(clickableSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return strBuilder;
    }

    public class EditTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (messageCode.getText().toString().length() >= 4) {
                nextstep_focus.setVisibility(View.VISIBLE);
                nextstep_focus.setEnabled(true);
            } else {
                nextstep_focus.setVisibility(View.GONE);
                nextstep_focus.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    public boolean verification() {
        phoneNumString = phoneNum.getText().toString();
        messageCodeString = messageCode.getText().toString();
        if (phoneNumString.length() == 0) {
//            phoneNum.setError("手机号不能为空");
            ToastUtil.showToast(this,"手机号不能为空");
            return false;
        }
        if (!checkPhoneNum(phoneNumString)) {
//            phoneNum.setError("手机号输入错误");
            ToastUtil.showToast(this,"手机号输入错误");
            return false;
        }
        if (messageCodeString.length() == 0) {
//            messageCode.setError("验证码不能为空");
            ToastUtil.showToast(this,"验证码不能为空");
            return false;
        }
        return true;
    }
    private boolean checkBox(){
        if (check_box.isChecked()){
            return true;
        }else {
            ToastUtil.showToast(mContext,"请同意使用条款和隐私政策");
            hideKeyBoard();
            return false;
        }
    }

    /**
     * 验证
     */
    public boolean verificationNum() {
        if (!checkBox()){
            return false;
        }
        phoneNumString = phoneNum.getText().toString();
        messageCodeString = messageCode.getText().toString();
        if (phoneNumString.length() == 0) {
//            phoneNum.setError("手机号不能为空");
            ToastUtil.showToast(this,"手机号不能为空");
            return false;
        }
        if (!checkPhoneNum(phoneNumString)) {
//            phoneNum.setError("手机号输入错误");
            ToastUtil.showToast(this,"手机号输入错误");
            return false;
        }

        return true;
    }

    public boolean checkPhoneNum(String userId) {
        if (userId.length() < 2)
            return false;
        TelNumMatch match = new TelNumMatch(userId);
        int flag = match.matchNum();
        /*不check 号码的正确性，只check 号码的长度*/
		/*if (flag == 1 || flag == 2 || flag == 3) {
			return true;
		} else {
			return false;
		}*/
        if (flag == 5) {
            return false;
        } else {
            return true;
        }
    }

    Handler handlerSms = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    CustomToast.showToast(mContext, "验证成功");
                    hideKeyBoard();
                    Intent intent = new Intent();
                    intent.setClass(mContext, RegistSubmitActivity.class);
                    intent.putExtra("phoneNumb", phoneNumString);
                    startActivity(intent);
                    finish();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        boolean smart = (Boolean) msg.obj;
                        CustomToast.showToast(mContext, "验证码已经发送，请等待接收");
                        hideKeyBoard();
                        setGetCodeBtnState(false);
                    }
                }
            } else {
                hideKeyBoard();
                ToastUtil.showToast(mContext,"验证失败，请输入正确的验证码！");
                //CustomToast.showToast(mContext, "验证失败，请输入正确的验证码！");
                getCodeButton.setText("获取验证码");
                setGetCodeBtnState(true);
            }
        }
    };

    Handler handler_time = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Handler处理消息
            if (msg.what > 0) {
                getCodeButton.setText("重新发送(" + msg.what + "s)");
            } else {
                timer.cancel();
                setGetCodeBtnState(true);
                getCodeButton.setText("获取验证码");
            }
        }
    };

    Handler handler_waitting = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    waittingDialog.show();
                    break;
                case 2:
                    waittingDialog.dismiss();
                    break;
                case 3:
                    CustomToast.showToast(mContext, "手机号已注册，请换一个号码试试~", 2000);
                    break;
            }
        }
    };

    Handler handler_verify = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Handler处理消息
            if (msg.what == 0) {
                timer.cancel();
                /*
                 * getCodeButton.setText("下一步"); getCodeButton.setEnabled(true);
                 */
                String verifyCode = (String) msg.obj;
                messageCode.setText(verifyCode);
                nextstep_focus.setVisibility(View.VISIBLE);
                nextstep_focus.setEnabled(true);
            } else if (msg.what == 1) {
                SMSSDK.getVerificationCode("86", phoneNum.getText().toString());
                timer = new Timer();
                timerTask = new TimerTask() {
                    int i = 60;

                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = i--;
                        handler_time.sendMessage(msg);
                    }
                };
                timer.schedule(timerTask, 1000, 1000);
                getCodeButton.setTextColor(Color.WHITE);
                getCodeButton.setEnabled(false);
            }
        }
    };
    private Button nextstep_unfocus;
    private Button nextstep_focus;

    public void permissionRequst() {
        if (verificationNum()) {
            if (timer != null) {
                timer.cancel();
            }
            handler_waitting.sendEmptyMessage(1);
            //键盘收起
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            phoneNumString = phoneNum.getText().toString();
            ApiRequestFactory.getYzPhoneNumber().
                    getYzPhoneNumberState(YzPhoneNumber.FORMAT, phoneNumString)
                    .enqueue(new Callback<YzPhoneResult>() {
                        @Override
                        public void onResponse(Call<YzPhoneResult> call, Response<YzPhoneResult> response) {
                            if (response.isSuccessful()) {
                                if ("1".equals(response.body().getResult())) {
                                    handler_verify.sendEmptyMessage(1);
//                                    RegistByPhoneActivity.this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContent);
                                } else if ("-1".equals(response.body().getResult())) {
                                    handler_waitting.sendEmptyMessage(3);
                                }
                                handler_waitting.sendEmptyMessage(2);
                            }
                        }

                        @Override
                        public void onFailure(Call<YzPhoneResult> call, Throwable t) {
                            ToastFactory.showShort(mContext, "短信发送失败，请重试");
                            handler_waitting.sendEmptyMessage(2);
                        }
                    });
        }
    }

    /*@OnPermissionDenied({Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION})
    public void permissionDenied() {
        CustomToast.showToast(mContext, "申请权限失败,无法获取验证码", 1000);
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==123){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                permissionRequst();
            }else {
                ToastUtil.showToast(mContext,"申请权限被拒绝");
                hideKeyBoard();
            }
        }
        RegistByPhoneActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }*/

    //设置按钮点击状态
    private void setGetCodeBtnState(boolean setOpen){
        if (setOpen){
            getCodeButton.setEnabled(true);
            getCodeButton.setBackgroundResource(R.drawable.shape_btn_bg_theme);
        }else {
            getCodeButton.setEnabled(false);
            getCodeButton.setBackgroundResource(R.drawable.shape_btn_bg_gray);
        }
    }

    /*********************************键盘设置***************************/
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
