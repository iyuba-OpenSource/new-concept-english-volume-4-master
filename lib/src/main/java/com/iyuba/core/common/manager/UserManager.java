package com.iyuba.core.common.manager;

import android.content.SharedPreferences;

import com.yd.saas.config.utils.SPUtil;

/**
 * @title:
 * @date: 2023/9/7 13:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class UserManager {
    private static UserManager instance;

    public static UserManager getInstance(){
        if (instance==null){
            synchronized (UserManager.class){
                if (instance==null){
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    private SharedPreferences preferences;
    private SharedPreferences getPreference(){
        if (preferences==null){
            preferences = SPUtil.getInstance().getSharedPreferences();
        }
        return preferences;
    }

    //账号
    private String UserName = "userName";
    private String Password = "password";

    public String getUserName() {
        return getPreference().getString(UserName,"");
    }

    public void setUserName(String userName) {
        getPreference().edit().putString(UserName,userName).apply();
    }

    public String getPassword() {
        return getPreference().getString(Password,"");
    }

    public void setPassword(String password) {
        getPreference().edit().putString(Password,password).apply();
    }
}
