package com.iyuba.core.common.retrofitapi;

import com.iyuba.core.common.protocol.base.LoginResponse;
import com.iyuba.core.common.retrofitapi.result.MobCheckBean;
import com.iyuba.core.common.retrofitapi.result.MobVerifyResponse;
import com.iyuba.core.common.retrofitapi.result.VXTokenResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * 苏州爱语吧科技有限公司
 *
 * @Date: 2022/12/27
 * @Author: han rong cheng
 */
public interface VXLoginService {

    @GET
    Call<VXTokenResponse> getToken(@Url String url, @QueryMap Map<String,String> map);

    @GET
    Call<UidResponse> getUid(@Url String url, @QueryMap Map<String,String> map);

    //获取微信的token
    @GET
    Observable<VXTokenResponse> getWxSmallToken(@Url String url, @QueryMap Map<String,String> map);

    //获取用户id
    @GET
    Observable<UidResponse> getUserId(@Url String url, @QueryMap Map<String,String> map);

    //mob登录
    @GET
    Observable<MobCheckBean> loginByMob(@Url String url, @QueryMap Map<String,String> map);
}
