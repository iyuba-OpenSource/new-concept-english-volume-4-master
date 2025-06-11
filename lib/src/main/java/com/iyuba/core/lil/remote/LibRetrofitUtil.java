package com.iyuba.core.lil.remote;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.lil.remote.util.LibEncodeUtil;
import com.iyuba.core.lil.user.remote.Login_account;
import com.iyuba.core.lil.user.remote.User_info;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class LibRetrofitUtil {

    private static LibRetrofitUtil instance;

    public static LibRetrofitUtil getInstance(){
        if (instance==null){
            synchronized (LibRetrofitUtil.class){
                if (instance==null){
                    instance = new LibRetrofitUtil();
                }
            }
        }
        return instance;
    }

    private <T>T createJson(Class<T> clz){
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl("http://www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(clz);
    }

    private <T>T createXml(Class<T> clz){
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl("http://www.baidu.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(clz);
    }

    private OkHttpClient getClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();
        return client;
    }

    /**************************************用户信息*****************************/
    //获取用户信息-20001
    public Observable<User_info> getUserInfo(long userId){
        String url = "http://api."+Constant.IYUBA_COM_IN+"/v2/api.iyuba";

        Log.d("用户登录", "获取用户信息-数据组合中");

        int protocol = 20001;
        int appId = Constant.APP_ID;
        String format = "json";
        String platform = "android";

        String sign = LibEncodeUtil.md5(protocol+""+userId+"iyubaV2");

        LibUserInfoService infoService = createJson(LibUserInfoService.class);
        return infoService.getUserInfo(url,protocol,appId,userId,userId,format,sign,platform);
    }

    //接口-账号登录
    public Observable<Login_account> loginByAccount(String userName, String password){
        //http://api.iyuba.com.cn/v2/api.iyuba
        String url = "http://api."+Constant.IYUBA_COM_IN+"/v2/api.iyuba";

        Log.d("用户登录", "账号登录-数据组合中");

        int protocol = 11001;
        String longitude = "";
        String latitude = "";
        int appId = Constant.APP_ID;
        String format = "json";
        String sign = LibEncodeUtil.md5(protocol+userName+ LibEncodeUtil.md5(password)+"iyubaV2");

        userName = LibEncodeUtil.encode(userName);
        password = LibEncodeUtil.md5(password);

        LibUserInfoService userService = createJson(LibUserInfoService.class);
        return userService.loginByAccount(url,protocol,appId,longitude,latitude,format,userName,password,sign);
    }
}
