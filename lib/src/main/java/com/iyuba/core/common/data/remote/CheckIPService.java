package com.iyuba.core.common.data.remote;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.CheckIPResponse;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/** 目前只有确定ip地址的接口符合 */
public interface CheckIPService {

    String ENDPOINT = "http://apps."+ Constant.IYUBA_CN+"minutes/";
    String appid = Constant.APPID;

    @GET("doCheckIP.jsp")
    Single<CheckIPResponse> checkIP(@Query("uid") String uid,
                                    @Query("appid") String appid);



    class Creator {
        public static CheckIPService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(CheckIPService.class);
        }
    }
}
