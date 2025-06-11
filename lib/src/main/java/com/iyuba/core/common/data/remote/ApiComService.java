package com.iyuba.core.common.data.remote;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.ClearUserResponse;
import com.iyuba.core.common.data.model.IntegralBean;
import com.iyuba.core.common.data.model.UploadUserInfoResponse;
import com.iyuba.core.common.data.model.UserDetailInfoResponse;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiComService {

    String ENDPOINT = "http://api."+ Constant.IYUBA_COM+"";

    @GET("v2/api.iyuba")
    Single<ClearUserResponse> clearUser(@Query("protocol") String protocol ,
                                        @Query("username") String username,
                                        @Query("password") String password,
                                        @Query("sign") String sign,
                                        @Query("format") String format);

    @GET("v2/api.iyuba")
    Single<UploadUserInfoResponse> uploadUserInfo(@Query("format") String format,
                                                  @Query("protocol") String protocol,
                                                  @Query("platform") String platform,
                                                  @Query("userid") String userid,
                                                  @Query("gender") String gender,
                                                  @Query("age") String age,
                                                  @Query("appid") String appid,
                                                  @Query("resideprovince") String resideprovince,
                                                  @Query("residecity") String residecity,
                                                  @Query("occupation") String occupation,
                                                  @Query("sign") String sign);

    @GET("v2/api.iyuba")
    Single<UserDetailInfoResponse> getUserInfo(@Query("protocol") String protocol,
                                               @Query("id") String uid,
                                               @Query("sign") String sign,
                                               @Query("platform") String platform,
                                               @Query("format") String format);




    class Creator {
        public static ApiComService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(ApiComService.class);
        }
    }
}
