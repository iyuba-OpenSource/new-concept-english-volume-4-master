//package com.iyuba.core.me.pay;
//
//import static com.iyuba.configation.Constant.getWxKey;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//
//import com.alipay.sdk.app.PayTask;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.LibRequestFactory;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.sqlite.mode.PayInfo;
//import com.iyuba.core.common.util.RxTimer;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.event.VipChangeEvent;
//import com.iyuba.core.lil.base.StackUtil;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
//import com.iyuba.imooclib.IMooc;
//import com.iyuba.lib.R;
//import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
//import com.tencent.mm.opensdk.modelpay.PayReq;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.Map;
//
//import retrofit2.Call;
//import timber.log.Timber;
//
///**
// * 爱语币的订单界面
// * Created by howard9891 on 2016/10/28.
// * 购买爱语币使用的  是正在使用的
// */
//
//public class IyubiPayOrderActivity extends Activity {
//    private static final int TEMP_USER = 50000000;
//    private TextView payorder_username;
//    private TextView payorder_rmb_amount;
//    private TextView payorder_orderinfo_de;
//    private NoScrollListView methodList;
//    private PayMethodAdapter methodAdapter;
//    private Button payorder_submit_btn;
//    private boolean confirmMutex = true;
//    private static final String TAG = IyubiPayOrderActivity.class.getSimpleName();
//    private Context mContext;
//    private static final String Seller = "iyuba@sina.com";
//    private String price;
//    private String subject;
//    private String body;
//    private String amount;
//    private int type;
////    private String out_trade_no;
//    private IWXAPI mWXAPI;
//
//    //选中的位置
//    private int selectPosition = 0;
//    //支付标志
//    private int payTag = 0;
//
//    private Button button;
//    private String productId;
//    private TextView tv_header;
//    private String orderInfo;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext = this;
//        setContentView(R.layout.activity_buyvip);
//        EventBus.getDefault().register(this);
//
//        if (!UserInfoManager.getInstance().isLogin()) {
//            Toast.makeText(mContext, "请登录后付款购买", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        Intent intent = getIntent();
//        price = intent.getStringExtra("price");
//        amount = intent.getStringExtra("amount");
//
////        type = intent.getIntExtra("type", -1);
//        subject = intent.getStringExtra("subject");
//        body = intent.getStringExtra("body");
////        out_trade_no = intent.getStringExtra("out_trade_no");
//        productId = intent.getStringExtra("productID");
//        orderInfo = intent.getStringExtra("orderinfo");
//        findView();
//        payorder_orderinfo_de.setText(orderInfo);
//        //mWXAPI = WXAPIFactory.createWXAPI(this, Constant.mWeiXinKey, true);
//        mWXAPI = WXAPIFactory.createWXAPI(mContext, null);
//        // 将该app注册到微信
//        mWXAPI.registerApp(Constant.getWxKey());
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        payorder_username.setText(UserInfoManager.getInstance().getUserName());
//    }
//
//    private void findView() {
//        button = (Button) findViewById(R.id.btn_back);
//        tv_header = (TextView) findViewById(R.id.tv_header);
//        tv_header.setText("购买爱语币");
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//        payorder_username = (TextView) findViewById(R.id.payorder_username_tv);
//        payorder_username.setText(UserInfoManager.getInstance().getUserName());
//
//        payorder_orderinfo_de = (TextView) findViewById(R.id.payorder_orderinfo_de);
//        payorder_rmb_amount = (TextView) findViewById(R.id.payorder_rmb_amount_tv);
//        payorder_rmb_amount.setText(price + "元");
//        methodList = (NoScrollListView) findViewById(R.id.payorder_methods_lv);
//        payorder_submit_btn = (Button) findViewById(R.id.payorder_submit_btn);
//        methodList.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectPosition = position;
//                methodAdapter.changeSelectPosition(position);
//                methodAdapter.notifyDataSetChanged();
//            }
//        });
//        methodAdapter = new PayMethodAdapter(this);
//        methodList.setAdapter(methodAdapter);
//        payorder_submit_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (confirmMutex) {
//                    confirmMutex = false;
//                    String newSubject;
//                    String newbody;
//                    try {
//                        newSubject = URLEncoder.encode(subject, "UTF-8");
//                        newbody = URLEncoder.encode(body, "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        newSubject = "iyubi";
//                        newbody = "iyubi";
//                    }
//                    if (methodAdapter.methods[selectPosition].equals("支付宝支付")) {
//                        payTag = PayMethodAdapter.PayMethod.ALIPAY;
//                    } else {
//                        payTag = PayMethodAdapter.PayMethod.WEIXIN;
//                    }
//                    switch (payTag) {
//                        case PayMethodAdapter.PayMethod.ALIPAY:
//                            if (!UserInfoManager.getInstance().isLogin()){
//                                showNormalDialog();
//                            } else{
//                                payByAlipay(newbody, newSubject);
//                            }
//                            break;
//                        case PayMethodAdapter.PayMethod.WEIXIN:
//                            Log.e("PayOrderActivity", "weixin");
//                            if (mWXAPI.isWXAppInstalled()) {
//                                payByWeiXin();
//                            } else {
//                                ToastUtil.showToast(mContext, "您还未安装微信客户端");
//                            }
//                            break;
//                            /*case PayMethodAdapter.PayMethod.BANKCARD:
//                            payByWeb();
//                            break;*/
//                        default:
//                            payByAlipay(newbody, newSubject);
//                            break;
//                    }
//                }
//            }
//        });
//    }
//
//    private void payByAlipay(String body, String subject) {
//        try {
//            body = URLEncoder.encode(body, "UTF-8");
//            subject = URLEncoder.encode(subject, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            body = "iyubi";
//            subject = "iyubi";
//        }
//
//        confirmMutex = true;
//        RequestCallBack rc = new RequestCallBack() {
//            @Override
//            public void requestResult(Request result) {
//                final OrderGenerateRequestNew response = (OrderGenerateRequestNew) result;
//                if (response.isRequestSuccessful()) {
//                    // 完整的符合支付宝参数规范的订单信息
//
//                    Runnable payRunnable = new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // 构造PayTask 对象
//                            PayTask alipay = new PayTask(IyubiPayOrderActivity.this);
//                            // 调用支付接口，获取支付结果
//                            Map<String, String> result = alipay.payV2(response.alipayTradeStr, true);
//
//                            Message msg = new Message();
//                            msg.what = 0;
//                            msg.obj = result;
//                            alipayHandler.sendMessage(msg);
//                        }
//                    };
//                    // 必须异步调用
//                    Thread payThread = new Thread(payRunnable);
//                    payThread.start();
//                } else {
//                    validateOrderFail();
//                }
//            }
//        };
//
//        OrderGenerateRequestNew orderRequest = new OrderGenerateRequestNew(productId, subject, price, body, Constant.APPID, String.valueOf(UserInfoManager.getInstance().getUserId()), amount, mOrderErrorListener, rc);
//        CrashApplication.getInstance().getQueue().add(orderRequest);
//    }
//
//    private void payByWeiXin() {
//        confirmMutex = true;
//        RequestCallBack rc = new RequestCallBack() {
//            @Override
//            public void requestResult(Request result) {
//                OrderGenerateWeiXinRequest first = (OrderGenerateWeiXinRequest) result;
//                if (first.isRequestSuccessful()) {
//                    Log.e(TAG, "OrderGenerateWeiXinRequest success!");
//                    PayReq req = new PayReq();
//                    req.appId = getWxKey();
//                    req.partnerId = first.partnerId;
//                    req.prepayId = first.prepayId;
//                    req.nonceStr = first.nonceStr;
//                    req.timeStamp = first.timeStamp;
//                    req.packageValue = "Sign=WXPay";
//                    req.sign = buildWeixinSign(req, first.mchkey);
//                    mWXAPI.sendReq(req);
//                } else {
//                    validateOrderFail();
//                }
//            }
//        };
//        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
//
//        try {
//            //两次加码，因为一次会出现乱码
//            body = URLEncoder.encode(body, "UTF-8");
//            body = URLEncoder.encode(body, "UTF-8");
//            subject = URLEncoder.encode(subject, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            body = "iyubi";
//            subject = "iyubi";
//        }
//
//        OrderGenerateWeiXinRequest request = new OrderGenerateWeiXinRequest(productId, getWxKey(),
//                Constant.APPID, uid, price, amount, body, mOrderErrorListener, rc);
//        CrashApplication.getInstance().getQueue().add(request);
//    }
//
//    private String buildWeixinSign(PayReq payReq, String key) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(buildWeixinStringA(payReq));
//        sb.append("&key=").append(key);
//        Log.i(TAG, sb.toString());
//        return MD5.getMD5ofStr(sb.toString()).toUpperCase();
//    }
//
//    private String buildWeixinStringA(PayReq payReq) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("appid=").append(payReq.appId);
//        sb.append("&noncestr=").append(payReq.nonceStr);
//        sb.append("&package=").append(payReq.packageValue);
//        sb.append("&partnerid=").append(payReq.partnerId);
//        sb.append("&prepayid=").append(payReq.prepayId);
//        sb.append("&timestamp=").append(payReq.timeStamp);
//        return sb.toString();
//    }
//
//    /*private void payByWeb() {
//        String url = "http://app." + Constant.IYUBA_CN + "wap/servlet/paychannellist?";
//        url += "out_user=" + AccountManager.Instace(mContext).userId;
//        url += "&appid=" + Constant.APPID;
//        url += "&amount=" + 0;
//        Intent intent = WebActivity.buildIntent(this, url, "订单支付");
//        startActivity(intent);
//        confirmMutex = true;
//        finish();
//    }*/
//
//    private void validateOrderFail() {
//        ToastUtil.showToast(mContext, "服务器正忙,请稍后再试!");
//        IyubiPayOrderActivity.this.finish();
//    }
//
//    private Response.ErrorListener mOrderErrorListener = new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
////            ToastUtil.showToast(mContext, "订单异常!");
////            PayOrderActivity.this.finish();
//            new AlertDialog.Builder(IyubiPayOrderActivity.this)
//                    .setTitle("订单提交出现问题!")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            confirmMutex = true;
//                            dialog.dismiss();
//                            IyubiPayOrderActivity.this.finish();
//                        }
//                    })
//                    .show();
//        }
//    };
//
//    private Handler alipayHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0: {
//                    confirmMutex = true;
//                    PayResultMap payResult = new PayResultMap((Map<String, String>) msg.obj);
//
//                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//                    String resultInfo = payResult.getResult();
//                    String resultStatus = payResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        uploadPayInfo(payResult.toString());
//                        //更新用户信息
//                        refreshUserInfo();
//                    } else {
//                        // 判断resultStatus 为非“9000”则代表可能支付失败
//                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
//                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
//                        if (TextUtils.equals(resultStatus, "8000")) {
//                            CustomToast.showToast(IyubiPayOrderActivity.this, "支付结果确认中", 1500);
//                        } else if (TextUtils.equals(resultStatus, "6001")) {
//                            CustomToast.showToast(IyubiPayOrderActivity.this, "您已取消支付", 1500);
//                        } else if (TextUtils.equals(resultStatus, "6002")) {
//                            CustomToast.showToast(IyubiPayOrderActivity.this, "网络连接出错", 1500);
//                        } else {
//                            // 其他值就可以判断为支付失败，或者系统返回的错误
//                            CustomToast.showToast(IyubiPayOrderActivity.this, "支付失败", 1500);
//                        }
//                    }
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
//    };
//
//    private void showNormalDialog() {
//        /* @setIcon 设置对话框图标
//         * @setTitle 设置对话框标题
//         * @setMessage 设置对话框消息提示
//         * setXXX方法返回Dialog对象，因此可以链式设置属性
//         */
//        final AlertDialog.Builder normalDialog =
//                new AlertDialog.Builder(mContext);
//        normalDialog.setIcon(R.drawable.iyubi_icon);
//        normalDialog.setTitle("提示");
//        normalDialog.setMessage("未登录用户无法购买vip和爱语币！");
//        normalDialog.setPositiveButton("确定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        LoginUtil.startToLogin(mContext);
//                    }
//                });
//        normalDialog.setNegativeButton("取消",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //...To-do
//
//                    }
//                });
//        // 显示
//        normalDialog.show();
//    }
//
//    /**
//     * 目前支付宝专用
//     */
//    private void uploadPayInfo(String data) {
//        Call<PayInfo> call = LibRequestFactory.getPayInfoApi().payInfoApi(data);
//        call.enqueue(new retrofit2.Callback<PayInfo>() {
//
//            @Override
//            public void onResponse(Call<PayInfo> call, retrofit2.Response<PayInfo> response) {
//                Timber.d("Upload pay info success! response: %s", response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<PayInfo> call, Throwable t) {
//                Timber.e("Upload pay info fail!");
//            }
//        });
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void event(PaySuccessEvent event) {
//
//        switch (event.getCode()) {
//            case 0:
//                //更新微课直购的状态
//                IMooc.notifyCoursePurchased();
//                //刷新用户信息
//                refreshUserInfo();
//                break;
//            case 1:
//                new AlertDialog.Builder(mContext)
//                        .setTitle("提示")
//                        .setMessage("支付错误")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        }).show();
//                break;
//            case -2:
//                new AlertDialog.Builder(mContext)
//                        .setTitle("提示")
//                        .setMessage("用户取消支付\n")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        }).show();
//                break;
//            default:
//                new AlertDialog.Builder(mContext)
//                        .setTitle("提示")
//                        .setMessage("支付失败")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        }).show();
//                break;
//        }
//    }
//
//    /*****************获取用户信息***********/
//    private void refreshUserInfo(){
//        openDialog();
//
//        RxTimer.getInstance().timerInMain("userInfo", 3000L, new RxTimer.RxActionListener() {
//            @Override
//            public void onAction(long number) {
//                RxTimer.getInstance().cancelTimer("userInfo");
//                getPayUserInfo();
//            }
//        });
//    }
//
//    private LoadingDialog loadingDialog;
//
//    private void openDialog(){
//        if (loadingDialog==null){
//            loadingDialog = new LoadingDialog(this);
//            loadingDialog.create();
//            loadingDialog.setMessage("正在获取您的账号信息~");
//        }
//
//        loadingDialog.show();
//    }
//
//    private void closeDialog(){
//        if (loadingDialog!=null&&loadingDialog.isShowing()){
//            loadingDialog.dismiss();
//        }
//    }
//
//    //退出当前界面
//    private void existPage(String msg){
//        closeDialog();
//        ToastUtil.showToast(this,msg);
//        StackUtil.getInstance().finishCur();
//    }
//
//    private void getPayUserInfo() {
//        UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), new UserinfoCallbackListener() {
//            @Override
//            public void onSuccess() {
//                EventBus.getDefault().post(new VipChangeEvent());
//
//                RxTimer.getInstance().timerInMain("refreshTag", 1000L, new RxTimer.RxActionListener() {
//                    @Override
//                    public void onAction(long number) {
//                        RxTimer.getInstance().cancelTimer("refreshTag");
//                        existPage("更新用户信息完成，会员状态未生效时，请退出重进");
//                    }
//                });
//            }
//
//            @Override
//            public void onFail(String errorMsg) {
//                existPage("更新用户信息完成，会员状态未生效时，请退出重进");
//            }
//        });
//    }
//}
