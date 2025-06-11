package com.iyuba.core.common.activity.login;

import android.content.Context;
import android.content.Intent;

import com.iyuba.core.me.activity.NewVipCenterActivity;

/**
 * @title:
 * @date: 2023/8/25 18:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LoginUtil {

    //跳转类型
    public static void startToLogin(Context context){
        NewLoginActivity.start(context, LoginType.getInstance().getCurLoginType());
    }
}
