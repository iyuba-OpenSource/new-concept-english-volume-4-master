package com.iyuba.core.lil;

import com.iyuba.core.common.retrofitapi.UidResponse;
import com.iyuba.core.networkbean.UserInfoForLogin;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface HelpApi {

    //根据token获取用户id
    @GET
    Observable<UidResponse> getUidByToken(@Url String url,
                                          @Query("platform") String platform,
                                          @Query("format") String format,
                                          @Query("appid") String appid,
                                          @Query("protocol") int protocol,
                                          @Query("token") String token,
                                          @Query("sign") String sign);

    //获取用户信息-2001
    @GET
    Observable<UserInfoForLogin> getUserInfo(@Url String url,
                                             @Query("platform") String platform,
                                             @Query("format") String format,
                                             @Query("protocol") String protocol,
                                             @Query("appid") String appid,
                                             @Query("id") String uid,
                                             @Query("myid") String myuid,
                                             @Query("sign") String sign);
}
