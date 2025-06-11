package com.iyuba.conceptEnglish.api;

import com.iyuba.conceptEnglish.api.data.TemporaryUserJson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/8/21.
 */

public interface ApiService {

    @GET("v2/api.iyuba")
    public Call<TemporaryUserJson> getTemporaryAccount(@Query("protocol") int protocol,
                                                       @Query("deviceId") String deviceId,
                                                       @Query("platform") String platform,
                                                       @Query("appid") int appid,
                                                       @Query("format")String format,
                                                       @Query("sign") String sign);
}
