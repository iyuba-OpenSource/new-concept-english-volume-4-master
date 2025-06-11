package com.iyuba.core.common.protocol.base;

import android.text.TextUtils;
import android.util.Log;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;
import com.iyuba.module.user.User;

public class LoginResponse extends BaseXMLResponse {
    public String result, uid, username, imgsrc, vip, validity, amount, isteacher, money;
    //昵称
    public String nickName;
    //以下属性暂时没用到
    //0
    public String credits;
    //success
    public String message;
    //邮箱
    public String email;
    //积分
    public String jiFen;
    //手机号
    public String mobile;

    @Override
    protected boolean extractBody(kXMLElement headerEleemnt,
                                  kXMLElement bodyElement) {
        // TODO Auto-generated method stub
        try {
            result = Utility.getSubTagContent(bodyElement, "result");
            uid = Utility.getSubTagContent(bodyElement, "uid");
            username = Utility.getSubTagContent(bodyElement, "username");
            nickName = Utility.getSubTagContent(bodyElement, "nickname");
            imgsrc = Utility.getSubTagContent(bodyElement, "imgSrc");
            vip = Utility.getSubTagContent(bodyElement, "vipStatus");
            money = Utility.getSubTagContent(bodyElement, "money");
            validity = Utility.getSubTagContent(bodyElement, "expireTime");
            amount = Utility.getSubTagContent(bodyElement, "Amount");
            isteacher = Utility.getSubTagContent(bodyElement, "isteacher");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public User convertUser(){
        User user = new User();
        user.uid=Integer.parseInt(uid);
        user.name=username;
        user.nickname=nickName;
        user.email=email;
        user.imgUrl=imgsrc;
        user.mobile=mobile;
        if (credits!=null){
            user.credit=Integer.parseInt(credits);
        }
        user.vipStatus=vip;
        user.vipExpireTime=Long.parseLong(validity);
        try {
            user.isTemp=Boolean.getBoolean(isteacher);
        }catch (Exception e){
            e.printStackTrace();
        }
        user.iyubiAmount=Integer.parseInt(amount);
        user.money=Integer.parseInt(money);
        return user;
    }

}
