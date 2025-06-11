//package com.iyuba.core.common.util;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.widget.Toast;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.core.common.activity.InfoFulfillActivity;
//import com.iyuba.core.common.activity.RegistSubmitActivity;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IErrorReceiver;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.ErrorResponse;
//import com.iyuba.core.common.protocol.base.SecVerifyLoginRequest;
//import com.iyuba.core.common.protocol.base.SecVerifyLoginResponse;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.lil.util.ResLibUtil;
//import com.iyuba.lib.R;
//import com.mob.secverify.GetTokenCallback;
//import com.mob.secverify.OAuthPageEventCallback;
//import com.mob.secverify.PageCallback;
//import com.mob.secverify.SecVerify;
//import com.mob.secverify.UiLocationHelper;
//import com.mob.secverify.common.exception.VerifyException;
//import com.mob.secverify.datatype.UiSettings;
//import com.mob.secverify.datatype.VerifyResult;
//import com.mob.secverify.ui.component.CommonProgressDialog;
//
//import java.io.UnsupportedEncodingException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class SecVerifyUtil {
//    private long starttime;
//    private String TAG = "SecVerifyUtil:";
//
//    private Context mContext;
//
//    private IVerifyCallback mCallback;
//
//    private VerifyResult verifyResult;
//
//    public SecVerifyUtil(IVerifyCallback callback) {
//        mContext = ResLibUtil.getInstance().getContext();
//        mCallback = callback;
//    }
//
//    public void gotoVerify() {
//        verify();
//    }
//
//    private boolean boolRequestSuccess = false;
//
//    /**
//     * 一键登录页面是否已经打开的标志
//     */
//    private boolean isOpen = false;
//
//    private Handler handler = new Handler();
//
//
//    Timer timer = new Timer();
//
//    TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//            if (!isOpen) {
//                //超过12s 还没有打开一键登录页面
//                //就跳转到普通登录页面
//                LogUtils.d(Thread.currentThread().getId() + "");
//                handler.post(cancelSecVerify);
//            }
//        }
//    };
//
//    Runnable cancelSecVerify = new Runnable() {
//        @Override
//        public void run() {
//            LogUtils.d(Thread.currentThread().getId() + "");
//            CommonProgressDialog.dismissProgressDialog();
////            Intent intent = new Intent(mContext, Login.class);
////            intent.putExtra("noSecVerify", true);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            mContext.startActivity(intent);
//            LoginUtil.startToLogin(mContext);
//        }
//    };
//
//    /**
//     * 免密登录
//     */
//    private void verify() {
//        //change UI
//        setUISetting();
//        CommonProgressDialog.showProgressDialog(mContext);
//        //需要在verify之前设置，（其他验证页面回调）
//        SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
//            @Override
//            public void initCallback(OAuthPageEventResultCallback cb) {
//                cb.pageOpenCallback(new PageOpenedCallback() {
//                    @Override
//                    public void handle() {
////                        mCallback.onCloseLoginActivity();
//                        LogUtils.i(TAG, System.currentTimeMillis() + " pageOpened");
//                        LogUtils.e(TAG, (System.currentTimeMillis() - starttime) + "ms is the time pageOpen take ");
//                        isOpen = true;
//                    }
//                });
//                cb.loginBtnClickedCallback(new LoginBtnClickedCallback() {
//                    @Override
//                    public void handle() {
//                        LogUtils.i(TAG, System.currentTimeMillis() + " loginBtnClicked");
//                    }
//                });
//                cb.agreementPageClosedCallback(new AgreementPageClosedCallback() {
//                    @Override
//                    public void handle() {
//                        LogUtils.i(TAG, System.currentTimeMillis() + " agreementPageClosed");
//                    }
//                });
//                cb.agreementPageOpenedCallback(new AgreementClickedCallback() {
//                    @Override
//                    public void handle() {
//                        LogUtils.i(TAG, System.currentTimeMillis() + " agreementPageOpened");
//                    }
//                });
//                cb.cusAgreement1ClickedCallback(new CusAgreement1ClickedCallback() {
//                    @Override
//                    public void handle() {
//                        LogUtils.i(TAG, System.currentTimeMillis() + " cusAgreement1ClickedCallback");
//                    }
//                });
//                cb.cusAgreement2ClickedCallback(new CusAgreement2ClickedCallback() {
//                    @Override
//                    public void handle() {
//                        LogUtils.i(TAG, System.currentTimeMillis() + " cusAgreement2ClickedCallback");
//                    }
//                });
//                cb.checkboxStatusChangedCallback(new CheckboxStatusChangedCallback() {
//                    @Override
//                    public void handle(boolean b) {
//                        LogUtils.i(TAG, System.currentTimeMillis() + " current status is " + b);
//                    }
//                });
//                cb.pageCloseCallback(new PageClosedCallback() {
//                    @Override
//                    public void handle() {
//                        LogUtils.i(TAG, System.currentTimeMillis() + " pageClosed");
//                        HashMap<String, List<Integer>> map = UiLocationHelper.getInstance().getViewLocations();
//                        if (map == null) {
//                            return;
//                        }
//                        for (String key : map.keySet()) {
//                            List<Integer> locats = map.get(key);
//                            if (locats != null && locats.size() > 0) {
//                                for (int i : locats) {
//                                    LogUtils.i(TAG, i + " xywh");
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        });
//        starttime = System.currentTimeMillis();
//        //启动一个计时器 ，超过8s后跳转到普通登录界面
//        timer.schedule(task, 8 * 1000);
//        SecVerify.verify(new PageCallback() {
//            @Override
//            public void pageCallback(int code, String desc) {
//                LogUtils.i(TAG, System.currentTimeMillis() + " 授权失败");
//                LogUtils.i(TAG, " code：" + code);
//                LogUtils.i(TAG, " desc：" + desc);
//                if (code == 6119152) {
//                    //other methods to login
////                    Intent intent = new Intent(mContext, Login.class);
////                    intent.putExtra("noSecVerify", true);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    mContext.startActivity(intent);
//                    LoginUtil.startToLogin(mContext);
//                }
//
//            }
//        }, new GetTokenCallback() {
//            @Override
//            public void onComplete(VerifyResult data) {
//                LogUtils.i(TAG, System.currentTimeMillis() + " 授权成功");
//                verifyResult = data;
//                CommonProgressDialog.showProgressDialog(mContext);
//                try {
//                    secVerifyTryLogin(data);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    CommonProgressDialog.dismissProgressDialog();
//                    //处理失败的逻辑
//                }
//            }
//
//            @Override
//            public void onFailure(VerifyException e) {
//                LogUtils.i(TAG, System.currentTimeMillis() + " 处理失败"+e.getCode());
//            }
//        });
//    }
//
//
//    /**
//     * 尝试一键登录
//     */
//    private void secVerifyTryLogin(VerifyResult data) throws UnsupportedEncodingException {
//        boolRequestSuccess = false;
//        //所有的操作结果都是在子线程中
//        ClientSession.Instace().asynGetResponse(
//                //请求地址和参数
//                new SecVerifyLoginRequest(data.getOpToken(), data.getOperator(), data.getToken()),
//                //返回正确时的流程
//                new IResponseReceiver() {
//                    @Override
//                    public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                        LogUtils.i(TAG, System.currentTimeMillis() + " 尝试登录成功");
//                        SecVerifyLoginResponse loginDetail = (SecVerifyLoginResponse) response;
//                        LogUtils.i(TAG, " 手机号：" + loginDetail.phone);
//                        //一键登录的使用者默认是自动登录
//                        //一键登录的注册流程的使用者，默认是自动登录
//                        SettingConfig.Instance().setAutoLogin(true);
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                CommonProgressDialog.dismissProgressDialog();
//                            }
//                        });
//                        if ("1".equals(loginDetail.isLogin)) {
//                            //already rejust
//                            //save user info
//                            AccountManager.Instance(mContext).saveUserNameAndPwd(
//                                    loginDetail.loginResponse.username, null);
//                            AccountManager.Instance(mContext).Refresh(loginDetail.loginResponse);
////                            InitPush.getInstance().registerToken(mContext, Integer.parseInt(
////                                    ConfigManager.Instance().getUserId()));
//                            isToFulfillInfo();
//                        } else {
//                            //not rejust
//                            //need to rejust
//
//                            Intent intent = new Intent();
//                            intent.setClass(mContext, RegistSubmitActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("isSecVerify", true);
//                            intent.putExtra("phoneNumb", loginDetail.phone);
//
//                            intent.putExtra("username", "iyuba"
//                                    + (int) (Math.random() * 9000 + 1000)
//                                    + loginDetail.phone.substring(loginDetail.phone.length() - 4));
//                            intent.putExtra("password", loginDetail.phone.substring(loginDetail.phone.length() - 6));
//
//                            mContext.startActivity(intent);
//                        }
//                        boolRequestSuccess = true;
//                    }
//                }
//                //返回错误时的流程
//                ,
//                new IErrorReceiver() {
//                    @Override
//                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
//                        // no matter success or failure
//                        //This way always be used
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                CommonProgressDialog.dismissProgressDialog();
//                                if (!boolRequestSuccess) {
////                                    Intent intent = new Intent(mContext, Login.class);
////                                    intent.putExtra("noSecVerify", true);
////                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                    mContext.startActivity(intent);
//                                    Toast.makeText(mContext, R.string.sec_Verify_fail_Notify, Toast.LENGTH_SHORT).show();
//                                    LogUtils.i(TAG, System.currentTimeMillis() + " 尝试登录失败");
//                                    LoginUtil.startToLogin(mContext);
//                                }
//                            }
//                        });
//
//                    }
//                }
//                , null);
//    }
//
//    /**
//     * whether jump to perfect personal info
//     */
//    private void isToFulfillInfo() {
//        String uid = ConfigManager.Instance().getUserId();
//        if (ConfigManager.Instance().getAccountIsShowFulfill(uid)) {
//            mContext.startActivity(new Intent(mContext, InfoFulfillActivity.class));
//        }
//    }
//
//    /**
//     * change UI
//     */
//    private void setUISetting() {
//        UiSettings.Builder builder = new UiSettings.Builder();
//        builder.setSwitchAccText(R.string.login_other_method);
//        builder.setLoginBtnTextSize(14);
//
//        SecVerify.setUiSettings(builder.build());
//    }
//
//    public interface IVerifyCallback {
//        void onSuccess();
//
//        void onFail(int failCode, String descirbeMessage);
//
//        void onOtherMethod(int failCode, String descirbeMessage);
//
//        void onControlDialog(boolean isShow);
//
//        void onCloseLoginActivity();
//
//    }
//}
