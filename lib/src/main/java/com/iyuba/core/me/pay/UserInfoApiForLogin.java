package com.iyuba.core.me.pay;

import com.iyuba.core.common.sqlite.mode.UserInfo;
import com.iyuba.core.networkbean.UserInfoForLogin;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 获取个人信息，刷新
 *
 * Created by wangwenyang
 */

public interface UserInfoApiForLogin {
    //http://api."+Constant.IYUBA_COM+"v2/api.iyuba?
    // platform=android
    // &format=json
    // &protocol=20001
    // &appid=240
    // &id=2561832
    // &myid=2561832
    // &sign=5219f39e23f0ae161cb60eee75afeb1a

    /**
     * 看自己,  id和myid都传登录的用户id;   看别人, 就是myid是自己的, id传被查看人的uid;  //sign = MD5(protocol + uid + iyubaV2)    appid是你的自己的appid
     * @param platform
     * @param format
     * @param protocol
     * @param appid
     * @param uid
     * @param myuid
     * @param sign
     * @return
     */

    @GET("/v2/api.iyuba")
    Call<UserInfoForLogin> userInfoApiForLogin(
            @Query("platform") String platform,
            @Query("format") String format,
            @Query("protocol") String protocol,
            @Query("appid") String appid,
            @Query("id") String uid,
            @Query("myid") String myuid,
            @Query("sign") String sign
    );
}
