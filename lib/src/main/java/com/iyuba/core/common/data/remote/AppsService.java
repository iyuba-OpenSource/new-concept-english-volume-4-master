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
public interface AppsService {

    String ENDPOINT = "http://apps."+ Constant.IYUBA_CN+"";
    String appid = Constant.APPID;

    //http://apps."+ Constant.IYUBA_CN+"iyuba/getTitleBySeries.jsp?type=category&category=314&sign=19d74bdebbc6413105ebc37bf2e551e0&format=json
    @GET("iyuba/getTitleBySeries.jsp")
    Single<AppsResponse.TalkClassList> getTalkClassLesson(@Query("type") String type,
                                                         @Query("sign") String sign,
                                                         @Query("category") String category,
                                                         @Query("format") String format);


    //http://apps."+ Constant.IYUBA_CN+"iyuba/getTitleBySeries.jsp?type=title&seriesid=283&sign=d420f556f3e7a24ff58042e5e8e6960c
    @GET("iyuba/getTitleBySeries.jsp")
    Single<AppsResponse.TalkLessonList> getTalkLesson(@Query("type") String level,
                                                     @Query("seriesid") String id,
                                                     @Query("sign") String sign);

    class Creator {
        public static AppsService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(AppsService.class);
        }
    }
}
