package com.iyuba.core.common.protocol.base;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SecVerifyLoginRequest extends BaseJSONRequest {

    public SecVerifyLoginRequest(String opToken, String operator, String token) throws UnsupportedEncodingException {

        String opTokenEncode=URLEncoder.encode(URLEncoder.encode(opToken, "UTF-8"),"UTF-8");
        String tokenEncode=URLEncoder.encode(URLEncoder.encode(token, "UTF-8"),"UTF-8");
        String operatorEncode=URLEncoder.encode(URLEncoder.encode(operator, "UTF-8"));
//        String opTokenEncode=URLEncoder.encode(opToken, "UTF-8");
//        String tokenEncode=URLEncoder.encode(token, "UTF-8");
        setAbsoluteURI("http://api."+Constant.IYUBA_COM+"v2/api.iyuba?" +
                "protocol=10010" +
                "&appId=" + Constant.APPID +
                "&appkey=" + Constant.getMobKey() +
                "&opToken=" + opTokenEncode +
                "&operator=" + operatorEncode +
                "&token=" + tokenEncode);
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {
        // TODO Auto-generated method stub


    }

    @Override
    public BaseHttpResponse createResponse() {
        // TODO Auto-generated method stub
        return new SecVerifyLoginResponse();
    }

}
