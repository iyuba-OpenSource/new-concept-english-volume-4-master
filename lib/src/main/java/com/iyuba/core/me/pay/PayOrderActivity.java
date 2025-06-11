package com.iyuba.core.me.pay;

import static com.iyuba.core.me.pay.PayMethodAdapter.PayMethod.ALIPAY;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.BuildConfig;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.LibRequestFactory;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.sqlite.mode.PayInfo;
import com.iyuba.core.common.util.PrivacyUtil;
import com.iyuba.core.common.util.RxTimer;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.event.ShowPageEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.core.lil.util.BigDecimalUtil;
import com.iyuba.imooclib.IMooc;
import com.iyuba.lib.R;
import com.iyuba.lib.databinding.ActivityBuyvipBinding;
import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;
import retrofit2.Call;
import timber.log.Timber;

/**
 * 订单界面
 * Created by howard9891 on 2016/10/28.
 */

public class PayOrderActivity extends BaseStackActivity {
    private static final String TAG = "PayOrderActivity";

    private boolean confirmMutex = true;
    private Context mContext;
    private int selectPosition = 0;

    //参数
    public static final String DESCRIPTION = "description";
    public static final String AMOUNT = "amount";
    public static final String PRICE = "price";
    public static final String SUBJECT = "subject";
    public static final String BODY = "body";
    public static final String PRODUCT_ID = "product_id";
    public static final String ORDER_TYPE = "order_type";

    //订单类型
    public static final String Order_vip = "vipOrder";
    public static final String Order_iyubi = "iyubiOrder";
    public static final String Order_moc = "mocOrder";

    //订单参数内容
    //详细描述
    private String mDescription;
    //价格
    private String mPrice;
    //类型
    private String mSubject;
    //简要描述
    private String mBody;
    //月数或爱语币数量
    private int mAmount;
    //爱语币或会员的类型
    private int mProductId;
    //抵扣的价格(分为单位)
    private long mDeduction = 0;
    //购买物品的类型
    private String mOrderType;

    //布局样式
    private ActivityBuyvipBinding binding;
    //支付适配器
    private PayMethodAdapter methodAdapter;
    //微信支付功能
    private IWXAPI msgApi;

    public static Intent buildIntent(Context context, String price,int amount,int productId,String subject,String body,String desc,String orderType) {
        Intent intent = new Intent();
        intent.setClass(context, PayOrderActivity.class);
        intent.putExtra(PRICE, price);
        intent.putExtra(AMOUNT, amount);
        intent.putExtra(PRODUCT_ID, productId);
        intent.putExtra(SUBJECT, subject);
        intent.putExtra(BODY, body);
        intent.putExtra(DESCRIPTION, desc);
        intent.putExtra(ORDER_TYPE, orderType);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContext = this;

        //布局
        binding = ActivityBuyvipBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        //相关参数内容
        mPrice = getIntent().getStringExtra(PRICE);
        mAmount = getIntent().getIntExtra(AMOUNT, -1);
        mProductId = getIntent().getIntExtra(PRODUCT_ID, -1);
        mSubject = getIntent().getStringExtra(SUBJECT);
        mBody = getIntent().getStringExtra(BODY);
        mDescription = getIntent().getStringExtra(DESCRIPTION);
        mOrderType = getIntent().getStringExtra(ORDER_TYPE);

        //这面这个仅用于测试使用，禁止在发布环境中使用
//        mPrice = "0.01";

        msgApi = WXAPIFactory.createWXAPI(mContext, null);
        // 将该app注册到微信
        msgApi.registerApp(Constant.getWxKey());

        //根据包名进行判断(样式)
        if (getPackageName().equals(Constant.package_conceptStory)
                || getPackageName().equals(Constant.package_nce)) {
            RelativeLayout toolbar = findViewById(R.id.rl_bar);
            toolbar.setBackgroundColor(Color.parseColor("#5468FF"));
        }

        //显示订单信息
        showOrderInfo();

        //显示抵扣金额
        showDeduction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void showOrderInfo() {
        //设置会员协议
        binding.vipAgreement.setText(setVipAgreement());
        binding.vipAgreement.setMovementMethod(new LinkMovementMethod());

        //返回按钮
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //用户名
        binding.payorderUsernameTv.setText(UserInfoManager.getInstance().getUserName());
        //价格
        binding.payorderRmbAmountTv.setText(mPrice + "元");
        //订单信息
        binding.payorderOrderinfoDe.setText(mDescription);
        //支付方式
        methodAdapter = new PayMethodAdapter(this);
        binding.payorderMethodsLv.setAdapter(methodAdapter);
        if (methodAdapter.getMethodsLength() == 1) {
            selectPosition = ALIPAY;
        }
        binding.payorderMethodsLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (methodAdapter.getMethodsLength() == 1) {
                    selectPosition = ALIPAY;
                } else {
                    selectPosition = position;
                }
                methodAdapter.changeSelectPosition(position);
                methodAdapter.notifyDataSetChanged();
            }
        });

        //支付按钮
        binding.payorderSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmMutex) {
                    confirmMutex = false;
                    String newSubject;
                    String newbody;
                    try {
                        newSubject = URLEncoder.encode(mSubject, "UTF-8");
                        newbody = URLEncoder.encode(mBody, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        newSubject = "iyubi";
                        newbody = "iyubi";
                    }
                    switch (selectPosition) {
                        case ALIPAY:
                            if (!UserInfoManager.getInstance().isLogin()) {
                                showNormalDialog();
                            } else {
                                payByAlipay(newbody, newSubject);
                            }
                            break;
                        case PayMethodAdapter.PayMethod.WEIXIN:
                            Log.e("PayOrderActivity", "weixin");
                            if (msgApi.isWXAppInstalled()) {
                                payByWeiXin();
                            } else {
                                ToastUtil.showToast(mContext, "您还未安装微信客户端");
                            }
                            break;
                      /*  case PayMethodAdapter.PayMethod.BANKCARD:
                            payByWeb();
                            break;*/
                        default:
                            payByAlipay(newbody, newSubject);
                            break;
                    }
                }
            }
        });
    }

    //设置会员服务协议的样式
    private SpannableStringBuilder setVipAgreement() {
        String vipStr = "《会员服务协议》";
        String showMsg = "点击支付即代表您已充分阅读并同意" + vipStr;

        SpannableStringBuilder spanStr = new SpannableStringBuilder();
        spanStr.append(showMsg);
        //会员服务协议
        int termIndex = showMsg.indexOf(vipStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(PayOrderActivity.this, Web.class);
                String url = PrivacyUtil.getVipAgreementUrl();
                intent.putExtra("url", url);
                intent.putExtra("title", "会员服务协议");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }, termIndex, termIndex + vipStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    //支付宝支付
    private void payByAlipay(String body, String subject) {
        try {
            body = URLEncoder.encode(body, "UTF-8");
            subject = URLEncoder.encode(subject, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            body = "iyubi";
            subject = "iyubi";
        }

        confirmMutex = true;
        RequestCallBack rc = new RequestCallBack() {
            @Override
            public void requestResult(Request result) {
                final OrderGenerateRequestNew response = (OrderGenerateRequestNew) result;
                if (response.isRequestSuccessful()) {
                    // 完整的符合支付宝参数规范的订单信息

                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(PayOrderActivity.this);
                            // 调用支付接口，获取支付结果
                            Map<String, String> result = alipay.payV2(response.alipayTradeStr, true);

                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = result;
                            alipayHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {
                    validateOrderFail();
                }
            }
        };

        OrderGenerateRequestNew orderRequest = new OrderGenerateRequestNew(String.valueOf(mProductId), subject, mPrice, body, Constant.APPID, String.valueOf(UserInfoManager.getInstance().getUserId()), String.valueOf(mAmount), mDeduction,mOrderErrorListener, rc);
        CrashApplication.getInstance().getQueue().add(orderRequest);
    }

    //微信支付
    private void payByWeiXin() {
        confirmMutex = true;
        RequestCallBack rc = new RequestCallBack() {
            @Override
            public void requestResult(Request result) {
                OrderGenerateWeiXinRequest first = (OrderGenerateWeiXinRequest) result;
                if (first.isRequestSuccessful()) {
                    Log.e(TAG, "OrderGenerateWeiXinRequest success!");
                    Log.e("first", first.toString());
                    PayReq req = new PayReq();
                    req.appId = Constant.getWxKey();
                    req.partnerId = first.partnerId;
                    req.prepayId = first.prepayId;
                    req.nonceStr = first.nonceStr;
                    req.timeStamp = first.timeStamp;
                    req.packageValue = "Sign=WXPay";
                    req.sign = buildWeixinSign(req, first.mchkey);
                    msgApi.sendReq(req);
                } else {
                    validateOrderFail();
                }
            }
        };
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        try {
            //两次加码，因为一次会出现乱码
            mBody = URLEncoder.encode(URLEncoder.encode(mBody, "UTF-8"), "UTF-8");
            mSubject = URLEncoder.encode(mSubject, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            mBody = "iyubi";
            mSubject = "iyubi";
        }

        OrderGenerateWeiXinRequest request = new OrderGenerateWeiXinRequest(String.valueOf(mProductId),
                Constant.getWxKey(), Constant.APPID, uid, mPrice,
                String.valueOf(mAmount), mBody, mDeduction,mOrderErrorListener, rc);
        CrashApplication.getInstance().getQueue().add(request);
    }

    private String buildWeixinSign(PayReq payReq, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildWeixinStringA(payReq));
        sb.append("&key=").append(key);
        Log.i(TAG, sb.toString());
        return MD5.getMD5ofStr(sb.toString()).toUpperCase();
    }

    private String buildWeixinStringA(PayReq payReq) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(payReq.appId);
        sb.append("&noncestr=").append(payReq.nonceStr);
        sb.append("&package=").append(payReq.packageValue);
        sb.append("&partnerid=").append(payReq.partnerId);
        sb.append("&prepayid=").append(payReq.prepayId);
        sb.append("&timestamp=").append(payReq.timeStamp);
        return sb.toString();
    }

    private void validateOrderFail() {
        ToastUtil.showToast(mContext, "服务器正忙,请稍后再试!");
        PayOrderActivity.this.finish();
    }

    private Response.ErrorListener mOrderErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            new AlertDialog.Builder(PayOrderActivity.this).setTitle("订单提交出现问题!").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmMutex = true;
                    dialog.dismiss();
                    PayOrderActivity.this.finish();
                }
            }).show();
        }
    };

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(mContext);
        normalDialog.setIcon(R.drawable.iyubi_icon);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("未登录无法购买vip和爱语币！");
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, Login.class);
//                startActivity(intent);
                LoginUtil.startToLogin(mContext);
            }
        });
        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do

            }
        });
        // 显示
        normalDialog.show();
    }

    private Handler alipayHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    confirmMutex = true;
                    PayResultMap payResult = new PayResultMap((Map<String, String>) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    Timber.tag("resultstatus").e(resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ConfigManager.Instance().putInt("isvip", 1);
                        uploadPayInfo(payResult.toString());
                        //更新用户信息
                        refreshUserInfo();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            CustomToast.showToast(PayOrderActivity.this, "支付结果确认中", 1500);
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            CustomToast.showToast(PayOrderActivity.this, "您已取消支付", 1500);
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            CustomToast.showToast(PayOrderActivity.this, "网络连接出错", 1500);
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            CustomToast.showToast(PayOrderActivity.this, "支付失败", 1500);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 目前支付宝专用
     */
    private void uploadPayInfo(String data) {
        Call<PayInfo> call = LibRequestFactory.getPayInfoApi().payInfoApi(data);
        call.enqueue(new retrofit2.Callback<PayInfo>() {

            @Override
            public void onResponse(Call<PayInfo> call, retrofit2.Response<PayInfo> response) {
                Timber.d("Upload pay info success! response: %s", response.toString());
            }

            @Override
            public void onFailure(Call<PayInfo> call, Throwable t) {
                Timber.e("Upload pay info fail!");
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(PaySuccessEvent event) {

        switch (event.getCode()) {
            case 0:
                //更新微课直购的状态
                if (mOrderType.equals(Order_moc)){
                    IMooc.notifyCoursePurchased();
                }
                //刷新用户信息
                refreshUserInfo();
                break;
            case 1:
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("支付错误")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                break;
            case -2:
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("用户取消支付\n")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                break;
            default:
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("支付失败")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                break;
        }
    }

    /*****************获取用户信息***********/
    private void refreshUserInfo() {
        openDialog();

        RxTimer.getInstance().timerInMain("userInfo", 3000L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                RxTimer.getInstance().cancelTimer("userInfo");
                getPayUserInfo();
            }
        });
    }

    private LoadingDialog loadingDialog;

    private void openDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
            loadingDialog.setMessage("正在获取您的账号信息~");
        }

        loadingDialog.show();
    }

    private void closeDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    //退出当前界面
    private void existPage(String msg) {
        closeDialog();
        ToastUtil.showToast(this, msg);
        StackUtil.getInstance().finishCur();
    }

    private void getPayUserInfo() {
        UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post(new VipChangeEvent());

                RxTimer.getInstance().timerInMain("refreshTag", 1000L, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer("refreshTag");
                        existPage("更新用户信息完成，会员状态未生效时，请退出重进");
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                existPage("更新用户信息完成，会员状态未生效时，请退出重进");
            }
        });
    }

    /*****************************抵扣功能操作**************************/
    //显示抵扣内容
    private void showDeduction() {
        //如果当前为调试模式，则直接不使用抵扣操作
        double showPrice = Double.parseDouble(mPrice);
        if (BuildConfig.DEBUG && showPrice < 2) {
            //因为价格低于2块钱的话，抵扣钱数为0，无法抵扣
            binding.deductionLayout.setVisibility(View.GONE);
            return;
        }

        //根据要求，爱语币暂时不增加抵扣功能，微课直购也不支持，仅支持会员
        if (!mOrderType.equals(Order_vip)) {
            binding.deductionLayout.setVisibility(View.GONE);
            return;
        }

        //最大不超过价格的一半，数据要为整数
        long walletMoney = (long) (BigDecimalUtil.trans2Double(0, UserInfoManager.getInstance().getMoney()) * 100L);
        //价格的一半(取整)
        long halfPrice = (long) (BigDecimalUtil.trans2Double(0, Double.parseDouble(mPrice)) * 100L / 2);
        //判断这两个，然后选择其中一个抵扣
        long deductionPrice = 0;
        if (walletMoney > 0 && halfPrice > 0) {
            if (walletMoney > halfPrice) {
                deductionPrice = halfPrice;
            } else {
                deductionPrice = walletMoney;
            }
        }
        //显示可用钱包
        int showWalletMoney = (int) (deductionPrice / 100L);
        binding.userMoneyTv.setText("(可用金额:" + showWalletMoney + "元)");
        binding.userMoneyTv.setOnClickListener(v -> {
//            WalletListActivity.start(this);
            EventBus.getDefault().post(new ShowPageEvent(ShowPageEvent.Page_WalletList));
        });
        binding.deductionTv.setText("0元");
        binding.clickDeductionLayout.setOnClickListener(v -> {
            if (showWalletMoney <= 0) {
                ToastUtil.showToast(this, "您当前可用金额为：" + showWalletMoney + "元，不足以进行抵扣");
                return;
            }

            showDeductionDialog(showWalletMoney);
        });
    }

    private void showDeductionDialog(int deductionMoney) {
        //价格数据
        deductionMoney += 1;
        //设置显示数据
        String[] showMoneyArray = new String[deductionMoney];
        for (int i = 0; i < deductionMoney; i++) {
            showMoneyArray[i] = String.valueOf(i);
        }

        OptionPicker optionPicker = new OptionPicker(this, showMoneyArray);
        //设置标题
        optionPicker.setTitleText("请选择抵扣金额(元)");
        optionPicker.setTitleTextColor(getResources().getColor(R.color.black));
        optionPicker.setTitleTextSize(16);
        optionPicker.setTopLineColor(getResources().getColor(R.color.gray));
        //设置按钮
        optionPicker.setSubmitTextSize(16);
        optionPicker.setSubmitTextColor(getResources().getColor(R.color.colorPrimary));
        optionPicker.setCancelTextSize(16);
        optionPicker.setCancelTextColor(getResources().getColor(R.color.colorPrimary));
        //设置item
        optionPicker.setTextSize(20);
        optionPicker.setTextColor(getResources().getColor(R.color.black));
        optionPicker.setDividerColor(getResources().getColor(R.color.gray));
        //设置回调
        optionPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int which, String s) {
                long showDeduction = Long.parseLong(showMoneyArray[which]);
                binding.deductionTv.setText(showDeduction + "元");
                //设置抵扣数据
                mDeduction = showDeduction * 100L;
            }
        });
        optionPicker.show();
    }
}
