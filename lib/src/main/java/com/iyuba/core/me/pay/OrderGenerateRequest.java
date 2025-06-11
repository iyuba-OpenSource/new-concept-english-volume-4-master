package com.iyuba.core.me.pay;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.iyuba.configation.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

public class OrderGenerateRequest extends BaseJsonObjectRequest {
    private static final String TAG = OrderGenerateRequest.class.getSimpleName();
    private static final String oldApi = "http://vip." + Constant.IYUBA_CN + "chargeapi.jsp?";
    private static final String newApi = "http://vip." + Constant.IYUBA_CN + "chargeapinew.jsp?";
    //    public String productId;
    public String result;
    public String message;
    public String orderInfo;
    public String orderSign;

    public OrderGenerateRequest(String productId, String seller_email, String out_trade_no, String subject,
                                String total_fee, String body, String defaultbank, String app_id, String userId, String amount,
                                ErrorListener el, final RequestCallBack rc) {
//        this.productId = productId;
        super(Request.Method.POST, newApi + "WIDseller_email=" + seller_email + "&WIDout_trade_no="
                + out_trade_no + "&WIDsubject=" + subject + "&WIDtotal_fee=" + total_fee
                + "&WIDbody=" + body + "&WIDdefaultbank=" + defaultbank + "&WIDshow_url=" + ""
                + "&app_id=" + app_id + "&userId=" + userId + "&amount=" + amount + "&product_id=" + productId
                + "&code=" + generateCode(userId), el);
        Log.e("ali", newApi + "WIDseller_email=" + seller_email + "&WIDout_trade_no="
                + out_trade_no + "&WIDsubject=" + subject + "&WIDtotal_fee=" + total_fee
                + "&WIDbody=" + body + "&WIDdefaultbank=" + defaultbank + "&WIDshow_url=" + ""
                + "&app_id=" + app_id + "&userId=" + userId + "&amount=" + amount + "&product_id=" + productId
                + "&code=" + generateCode(userId));
        setResListener(new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObjectRoot) {
                try {
                    result = jsonObjectRoot.getString("result");
                    Log.e("支付result",jsonObjectRoot.toString());
                    message = jsonObjectRoot.getString("message");
                    if (isRequestSuccessful()) {
                        orderInfo = URLDecoder.decode(jsonObjectRoot.getString("orderInfo"),
                                "utf-8");
                        orderSign = jsonObjectRoot.getString("sign");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                rc.requestResult(OrderGenerateRequest.this);
            }
        });
    }

    @Override
    public boolean isRequestSuccessful() {
        return "1".equals(result);
    }

    private static String generateCode(String userId) {
        String code = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        code = MD5.getMD5ofStr(userId + "iyuba" + df.format(System.currentTimeMillis()));
        return code;
    }

}
