package com.iyuba.core;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.WordApi;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.discover.activity.WordPdfAPI;
import com.iyuba.core.me.pay.PayInfoApi;
import com.iyuba.core.me.pay.UserInfoApi;
import com.iyuba.core.me.pay.UserInfoApiForLogin;
import com.youdao.sdk.nativeads.RequestParameters;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


/**
 * 网络请求
 * Created by liuzhenli on 2017/4/12.
 */

public class LibRequestFactory {
    private static final String TAG = "LibRequestFactory";
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Converter.Factory gsonConvertFactory = GsonConverterFactory.create();
    private static SimpleXmlConverterFactory xmlConverterFactory = SimpleXmlConverterFactory.create();
    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
            new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LogUtils.e(TAG, message.trim());
                }
            });


    private static void initHttpClient() {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }


    private static UserInfoApi userInfoApi;

    public static UserInfoApi getUserInfoApi() {
        if (userInfoApi == null) {
            initHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://api."+Constant.IYUBA_COM_IN)
                    .addConverterFactory(gsonConvertFactory)
                    .build();
            userInfoApi = retrofit.create(UserInfoApi.class);
        }
        return userInfoApi;
    }


    private static UserInfoApiForLogin userInfoApiForLogin;

    public static UserInfoApiForLogin getUserInfoApiForLogin() {
        if (userInfoApiForLogin == null) {
            initHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://api."+Constant.IYUBA_COM_IN)
                    .addConverterFactory(gsonConvertFactory)
                    .build();
            userInfoApiForLogin = retrofit.create(UserInfoApiForLogin.class);
        }
        return userInfoApiForLogin;
    }

    private static PayInfoApi payInfoApi;

    public static PayInfoApi getPayInfoApi() {
        if (payInfoApi == null) {
            initHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(PayInfoApi.url)
                    .addConverterFactory(gsonConvertFactory)
                    .build();
            payInfoApi = retrofit.create(PayInfoApi.class);
        }
        return payInfoApi;
    }

    private static WordApi wordApi;

    public static WordApi getWordApi() {
        if (wordApi == null) {
            initHttpClient();
            Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                    .baseUrl(WordApi.url)
                    .addConverterFactory(xmlConverterFactory)
                    .build();
            wordApi = retrofit.create(WordApi.class);
        }
        return wordApi;
    }

    private static WordPdfAPI wordPdfAPI;

    public static WordPdfAPI getWordPdfAPI() {
        if (wordPdfAPI == null) {
            initHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(WordPdfAPI.BASEURL)
                    .addConverterFactory(gsonConvertFactory)
                    .build();
            wordPdfAPI = retrofit.create(WordPdfAPI.class);
        }
        return wordPdfAPI;
    }

}
