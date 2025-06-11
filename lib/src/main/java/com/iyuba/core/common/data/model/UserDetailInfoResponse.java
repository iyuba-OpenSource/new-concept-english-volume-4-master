package com.iyuba.core.common.data.model;

import com.iyuba.module.toolbox.SingleParser;

import io.reactivex.Single;

public class UserDetailInfoResponse implements SingleParser<UserDetailInfoResponse> {
    public int result;// 返回代码
    public String message;// 返回信息

    public String gender;// 性别
    public String age;// 性别
    public String resideLocation;// 现住地
    public String education;// 学历
    public String occupation;// 职业

    @Override
    public Single<UserDetailInfoResponse> parse() {
        if (result == 211){
            return Single.just(this);
        }
        return Single.error(new Throwable(message));
    }
}
