package com.iyuba.core.lil;

public class WxLoginSession {

    private static WxLoginSession instance;

    public static WxLoginSession getInstance(){
        if (instance==null){
            synchronized (WxLoginSession.class){
                if (instance==null){
                    instance = new WxLoginSession();
                }
            }
        }
        return instance;
    }

    //微信小程序登陆的token
    private String wxSmallToken;

    public void setWxSmallToken(String token){
        this.wxSmallToken = token;
    }

    public String getWxSmallToken() {
        return wxSmallToken;
    }
}
