package com.iyuba.core.me.pay;

import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.iyuba.configation.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

/**
 * 支付宝请求订单操作
 */
public class OrderGenerateRequestNew extends BaseJsonObjectRequest {
    private static String newApi = "http://vip." + Constant.IYUBA_CN + "alipay.jsp?";
    //    public String productId;
    public String result;
    public String message;
    public String alipayTradeStr;

    /* , @Query("app_id") String app_id (done)
    , @Query("userId") String userId (done)
    , @Query("code") String code//加密sign (done)
    , @Query("WIDtotal_fee") String WIDtotal_fee//支付金额 (done)
    , @Query("amount") String amount//爱语币数量或者会员月数 (done)
    , @Query("product_id") String product_id//产品类型 (done)
    , @Query("W	IDbody") String WIDbody (done)
    , @Query("WIDsubject") String WIDsubject (done)
    */
    public OrderGenerateRequestNew(String productId, String subject, String total_fee, String body,
                                   String app_id, String userId, String amount,long deduction,
                                   ErrorListener el, final RequestCallBack rc) {

        super(Method.POST, buildUrl(newApi,productId,subject,total_fee,body,app_id,userId,amount,deduction), el);
        setResListener(new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObjectRoot) {
                try {
                    result = jsonObjectRoot.getString("result");
                    Log.e("支付result",jsonObjectRoot.toString());
                    message = jsonObjectRoot.getString("message");
                    alipayTradeStr = jsonObjectRoot.getString("alipayTradeStr");
                    /*if (isRequestSuccessful()) {
                        alipayTradeStr = URLDecoder.decode(jsonObjectRoot.getString("alipayTradeStr"),
                                "utf-8");
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rc.requestResult(OrderGenerateRequestNew.this);
            }
        });
    }

    private static String buildUrl(String url,String productId, String subject, String total_fee, String body,
                            String app_id, String userId, String amount,long deduction){
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        sb.append("app_id=").append(app_id);
        sb.append("&").append("userId=").append(userId);
        sb.append("&").append("code=").append(generateCode(userId));
        sb.append("&").append("WIDtotal_fee=").append(total_fee);
        sb.append("&").append("amount=").append(amount);
        sb.append("&").append("product_id=").append(productId);
        sb.append("&").append("WIDbody=").append(body);
        sb.append("&").append("WIDsubject=").append(subject);
        //增加抵扣数据
        sb.append("&").append("deduction=").append(deduction);
        return sb.toString();
    }

    @Override
    public boolean isRequestSuccessful() {
        return "200".equals(result);
    }

    private static String generateCode(String userId) {
        String code = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        code = MD5.getMD5ofStr(userId + "iyuba" + df.format(System.currentTimeMillis()));
        return code;
    }

}
