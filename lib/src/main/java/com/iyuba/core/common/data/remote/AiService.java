package com.iyuba.core.common.data.remote;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.SendEvaluateResponse;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiService {

    //String ENDPOINT = "https://"+Constant.userSpeech+Constant.IYUBA_CN+"";
    //String ENDPOINT = "https://"+Constant.userSpeech+"";
    String ENDPOINT = "http://"+Constant.userSpeech+"";
    //http://"+Constant.userSpeech+"test/concept/

    //@POST("test/concept/")
    @POST("test/eval/")
    Single<AiResponse.GetEvaluateResponse> uploadSentence(@Body RequestBody body);
    class Creator {

        public static AiService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(AiService.class);
        }
    }
}
