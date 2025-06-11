package com.iyuba.core.me.pay;

import com.iyuba.core.common.sqlite.mode.UserInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuzhenli on 2017/4/14.
 */

public interface UserInfoApi {
    //http://api."+Constant.IYUBA_COM+"v2/api.iyuba?
    // platform=android
    // &format=json
    // &protocol=20001
    // &appid=240
    // &id=2561832
    // &myid=2561832
    // &sign=5219f39e23f0ae161cb60eee75afeb1a
    @GET("/v2/api.iyuba")
    Call<UserInfo> userInfoApi(
            @Query("platform") String platform,
            @Query("format") String format,
            @Query("protocol") String protocol,
            @Query("appid") String appid,
            @Query("id") String uid,
            @Query("myid") String myuid,
            @Query("sign") String sign
    );
}
