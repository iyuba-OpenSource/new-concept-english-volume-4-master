package com.iyuba.core.common.activity.login;

import android.content.Context;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.base.LoginResponse;
import com.iyuba.core.common.retrofitapi.VXLoginService;
import com.iyuba.core.common.retrofitapi.result.MobCheckBean;
import com.iyuba.core.common.retrofitapi.result.MobVerifyResponse;
import com.iyuba.core.common.retrofitapi.result.VXTokenResponse;
import com.iyuba.core.common.util.MD5;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @title:
 * @date: 2023/8/25 11:20
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LoginPresenter {

    //获取小程序的token
    public static void getWXSmallToken(Observer<VXTokenResponse> observer){
        int protocol = 10011;
        String sign = MD5.getMD5ofStr(protocol + Constant.APPID + "iyubaV2");
        String url="http://api."+Constant.IYUBA_COM_IN+"/v2/api.iyuba";

        HashMap<String, String> map = new HashMap<>();
        map.put("platform","android");
        map.put("format","json");
        map.put("protocol",String.valueOf(protocol));
        map.put("appid",Constant.APPID);
        map.put("sign",sign);

        Retrofit retrofit = new Retrofit.Builder()
                //使用自定义的mGsonConverterFactory
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://apis.baidu.com/txapi/")
                .build();
        VXLoginService service = retrofit.create(VXLoginService.class);
        service.getWxSmallToken(url,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    //从服务器获取mob的数据信息
    public static void getMobDataFromServer(Context context, String token, String opToken, String operator, Observer<MobCheckBean> observer){
        String url = "http://api."+Constant.IYUBA_COM_IN+"/v2/api.iyuba";
        Map<String, String> map = new HashMap<>();
        map.put("protocol", "10010");
        try {
            map.put("token", URLEncoder.encode(token, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            map.put("token", token);
        }
        map.put("opToken", opToken);
        map.put("operator", operator);
        map.put("appId", String.valueOf(Constant.APP_ID));
        map.put("appkey",Constant.getMobKey());

        Retrofit retrofit = new Retrofit.Builder()
                //使用自定义的mGsonConverterFactory
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://apis.baidu.com/txapi/")
                .build();
        VXLoginService service = retrofit.create(VXLoginService.class);
        service.loginByMob(url,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
