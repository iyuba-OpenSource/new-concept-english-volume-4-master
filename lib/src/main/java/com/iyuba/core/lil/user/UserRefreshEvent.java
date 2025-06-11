package com.iyuba.core.lil.user;

/**
 * @title: 用户信息刷新
 * @date: 2023/11/3 13:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class UserRefreshEvent {
    public static final String userInfo_success = "userInfo_success";//用户信息加载成功
    public static final String userInfo_fail = "userInfo_fail";//用户信息加载失败

    private String showType;

    public UserRefreshEvent(String showType) {
        this.showType = showType;
    }

    public static String getUserInfo_success() {
        return userInfo_success;
    }

    public static String getUserInfo_fail() {
        return userInfo_fail;
    }

    public String getShowType() {
        return showType;
    }
}
