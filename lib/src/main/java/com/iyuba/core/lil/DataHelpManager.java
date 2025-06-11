package com.iyuba.core.lil;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.retrofitapi.UidResponse;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.networkbean.UserInfoForLogin;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataHelpManager {

    private static DataHelpManager instance;

    public static DataHelpManager getInstance(){
        if (instance==null){
            synchronized (DataHelpManager.class){
                if (instance==null){
                    instance = new DataHelpManager();
                }
            }
        }
        return instance;
    }

    private <T> T createService(Class<T> clz){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://www.baidu.com/")
                .build();

        return retrofit.create(clz);
    }

    //根据token获取用户id
    public Observable<UidResponse> getUserIdByToken(String token){
        String url = "http://api."+ Constant.IYUBA_COM_IN +"/v2/api.iyuba";

        String platform = "android";;
        String format = "json";
        String appid = Constant.APPID;
        int protocol = 10016;
        String sign = MD5.getMD5ofStr(protocol + appid + token + "iyubaV2");

        HelpApi helpApi = createService(HelpApi.class);
        return helpApi.getUidByToken(url,platform,format,appid,protocol,token,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //获取用户信息
    public Observable<UserInfoForLogin> getUserInfo(String userId){
        String url = "http://api."+Constant.IYUBA_COM_IN+"/v2/api.iyuba";

        String platform = "android";
        String format = "json";
        int protocol = 20001;
        String appid = Constant.APPID;
        String sign = MD5.getMD5ofStr(protocol+userId + "iyubaV2");

        HelpApi helpApi = createService(HelpApi.class);
        return helpApi.getUserInfo(url,platform,format,String.valueOf(protocol),appid,userId,userId,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    
}
