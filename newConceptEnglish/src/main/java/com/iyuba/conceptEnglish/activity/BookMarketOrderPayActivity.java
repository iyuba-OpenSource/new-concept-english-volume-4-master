package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.ShopCartAdapter;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.sqlite.mode.BookDetail;
import com.iyuba.conceptEnglish.sqlite.op.ShopCartOp;
import com.iyuba.conceptEnglish.widget.MyListView;
import com.iyuba.conceptEnglish.widget.cdialog.CustomToast;
import com.iyuba.configation.Constant;
import com.iyuba.core.LibRequestFactory;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.sqlite.mode.PayInfo;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.pay.MD5;
import com.iyuba.core.me.pay.NoScrollListView;
import com.iyuba.core.me.pay.OrderGenerateRequestNew;
import com.iyuba.core.me.pay.OrderGenerateWeiXinRequest;
import com.iyuba.core.me.pay.PayMethodAdapter;
import com.iyuba.core.me.pay.PayResultMap;
import com.iyuba.core.me.pay.PaySuccessEvent;
import com.iyuba.core.me.pay.RequestCallBack;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by ivotsm on 2017/3/1.
 */

public class BookMarketOrderPayActivity extends BasisActivity {
    private ArrayList<BookDetail> bookDetails = new ArrayList<>();
    private static final String Seller = "iyuba@sina.com";
    private NoScrollListView methodList;
    private PayMethodAdapter methodAdapter;
    private Button payorder_submit_btn;
    private int selectPosition = 0;
    private boolean confirmMutex = true;
    private Context mContext;
    private int totalAmount = 0;
    private IWXAPI msgApi;
    private float price = 0;
    private String out_trade_no = "";
    private String subject = "";
    private String body = "";
    private String productId = "";
    private String amount = "";
    private ShopCartAdapter adapter;

    @BindView(R.id.list)
    MyListView list;
    @BindView(R.id.titlebar_back_button)
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_books);
        if (!AdvertisingKey.releasePackage.equals(ConstantNew.PACK_NAME)){
            selectPosition = 1;
        }
        mContext = this;
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        msgApi = WXAPIFactory.createWXAPI(mContext, null);
        out_trade_no = getOutTradeNo();

        Bundle bundle = getIntent().getBundleExtra("books");
        bookDetails = (ArrayList<BookDetail>) bundle.getSerializable("books");

        for (int i = 0; i < bookDetails.size(); i++) {
            price = price + bookDetails.get(i).num * Integer.parseInt(bookDetails.get(i).totalPrice);
            totalAmount = totalAmount + bookDetails.get(i).num;
//            price += Float.parseFloat(bookDetails.get(i).totalPrice) * bookDetails.get(i).num;
            if (amount.equals("")) {
                amount = amount.concat(bookDetails.get(i).id);
            } else {
                amount = amount.concat("," + bookDetails.get(i).id);
            }

        }

        subject = "全媒体图书" + String.valueOf(totalAmount) + "套";
        body = amount;
        productId = "100";

        adapter = new ShopCartAdapter(mContext, bookDetails, 1);
        list.setAdapter(adapter);

        methodList = (NoScrollListView) findViewById(com.iyuba.lib.R.id.payorder_methods_lv);
        payorder_submit_btn = (Button) findViewById(com.iyuba.lib.R.id.payorder_submit_btn);
        methodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (AdvertisingKey.releasePackage.equals(ConstantNew.PACK_NAME)){
                    selectPosition = position;
                    methodAdapter.changeSelectPosition(position);
                    methodAdapter.notifyDataSetChanged();
                }
            }
        });
        methodAdapter = new PayMethodAdapter(this);
        methodList.setAdapter(methodAdapter);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        payorder_submit_btn.setOnClickListener(view -> {
            if (confirmMutex) {
                confirmMutex = false;
                String newSubject;
                String newbody;
                try {
                    newSubject = URLEncoder.encode(subject, "UTF-8");
                    newbody = URLEncoder.encode(body, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    newSubject = "iyubi";
                    newbody = "iyubi";
                }
                switch (selectPosition) {
                    case PayMethodAdapter.PayMethod.ALIPAY:
                        payByAlipay(newbody, newSubject);
                        break;
                    case PayMethodAdapter.PayMethod.WEIXIN:
                        Log.e("PayOrderActivity", "weixin");
                        if (msgApi.isWXAppInstalled()) {
                            payByWeiXin();
                        } else {
                            ToastUtil.showToast(mContext, "您还未安装微信客户端");
                        }
                        break;
                    default:
                        payByAlipay(newbody, newSubject);
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

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
                            PayTask alipay = new PayTask(BookMarketOrderPayActivity.this);
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
                    alipayHandler.sendEmptyMessage(1);
                }
            }
        };


        OrderGenerateRequestNew orderRequest = new OrderGenerateRequestNew(productId, subject, String.valueOf(price), body, Constant.APPID, String.valueOf(UserInfoManager.getInstance().getUserId()), amount, 0,mOrderErrorListener, rc);
        CrashApplication.getInstance().getQueue().add(orderRequest);
    }

    private void payByWeiXin() {
        confirmMutex = true;
        RequestCallBack rc = result -> {
            OrderGenerateWeiXinRequest first = (OrderGenerateWeiXinRequest) result;
            if (first.isRequestSuccessful()) {
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
                alipayHandler.sendEmptyMessage(1);
            }
        };
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        OrderGenerateWeiXinRequest request = new OrderGenerateWeiXinRequest(productId, Constant.getWxKey(), Constant.APPID, uid, String.valueOf(price), String.valueOf(totalAmount), body, 0,mOrderErrorListener, rc);
        CrashApplication.getInstance().getQueue().add(request);
    }

    private String buildWeixinSign(PayReq payReq, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildWeixinStringA(payReq));
        sb.append("&key=").append(key);
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

    private Response.ErrorListener mOrderErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
//            ToastUtil.showToast(mContext, "订单异常!");
//            PayOrderActivity.this.finish();
            new AlertDialog.Builder(mContext)
                    .setTitle("订单提交出现问题!")
                    .setPositiveButton("确定", (dialog, which) -> {
                        confirmMutex = true;
                        dialog.dismiss();
                        finish();
                    })
                    .show();
        }
    };

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
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
                    for (int i = 0; i < bookDetails.size(); i++) {
                        ShopCartOp shopCartOp = new ShopCartOp(mContext);
                        shopCartOp.deleteBookDetail(bookDetails.get(i).id, String.valueOf(UserInfoManager.getInstance().getUserId()));
                    }
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        uploadPayInfo(payResult.toString());
                        new AlertDialog.Builder(mContext)
                                .setTitle("提示")
                                .setMessage("支付成功")
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
//                                        finish();
                                        Intent intent = new Intent();
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.setClass(mContext, MainFragmentActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .show();

                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            CustomToast.showToast(mContext, "支付结果确认中", 1500);
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            CustomToast.showToast(mContext, "您已取消支付", 1500);
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            CustomToast.showToast(mContext, "网络连接出错", 1500);
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            CustomToast.showToast(mContext, "支付失败", 1500);
                        }
                    }
                    break;
                }
                case 1:
                    ToastUtil.showToast(mContext, "服务器正忙,请稍后再试!");
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /** 目前支付宝专用*/
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
        finish();
    }
}
