package com.iyuba.core.common.protocol.base;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SecVerifyLoginResponse extends BaseJSONResponse {
    // 是否已经注册了  1：已经注册 0：未注册
	public String isLogin;
    //
	public String valid;
    // 手机号
	public String phone;
    //
	public String isValid;
    //用户信息
//	public UserInfo userInfo = new UserInfo();
    public LoginResponse loginResponse=new LoginResponse();

//	public JSONObject jsonObjectUserinfo;


    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        JSONObject jsonObjectRoot;
        try {
            jsonObjectRoot = new JSONObject(bodyElement);
            isLogin = jsonObjectRoot.getString("isLogin");

            JSONObject jsonObjectRes = jsonObjectRoot.getJSONObject("res");
            valid = jsonObjectRes.getString("valid");
            phone = jsonObjectRes.getString("phone");
            isValid = jsonObjectRes.getString("isValid");
            if ("1".equals(isLogin)){
                JSONObject jsonObjectUserinfo = jsonObjectRoot.getJSONObject("userinfo");
                loginResponse.uid = jsonObjectUserinfo.getString("uid");
                loginResponse.result = jsonObjectUserinfo.getString("result");
                loginResponse.username = jsonObjectUserinfo.getString("username");
                loginResponse.imgsrc = jsonObjectUserinfo.getString("imgSrc");
                loginResponse.validity = jsonObjectUserinfo.getString("expireTime");
                loginResponse.amount = jsonObjectUserinfo.getString("Amount");
                loginResponse.isteacher = jsonObjectUserinfo.getString("isteacher");
                loginResponse.money = jsonObjectUserinfo.getString("money");
                loginResponse.vip = jsonObjectUserinfo.getString("vipStatus");
                loginResponse.nickName = jsonObjectUserinfo.getString("nickname");

                loginResponse.credits = jsonObjectUserinfo.getString("credits");
                loginResponse.message = jsonObjectUserinfo.getString("message");
                loginResponse.email = jsonObjectUserinfo.getString("email");
                loginResponse.jiFen = jsonObjectUserinfo.getString("jiFen");
                loginResponse.mobile = jsonObjectUserinfo.getString("mobile");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }


}
