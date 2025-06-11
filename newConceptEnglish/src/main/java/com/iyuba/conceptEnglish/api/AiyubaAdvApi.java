package com.iyuba.conceptEnglish.api;


import com.iyuba.conceptEnglish.api.data.AiyubaAdvResult;
import com.iyuba.conceptEnglish.sqlite.mode.MessageLiuResult;
import com.iyuba.configation.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/9/2.
 */

public interface AiyubaAdvApi {

    //    String BASEURL = "http://app." + Constant.IYUBA_CN + "dev/";
    // TODO: 2023/12/11 李涛在技术群里说明，这里去掉dev，更换广告链接
    /**
     * 广告的接口，新版本发布的话，变更下
     * 如果使用的是 http://app.iyuba.cn/dev/getAdxxxx的，修正为：http://dev.iyuba.cn/getAdxxxx 即可  变更了服务器了
     */
    String BASEURL = "http://dev." + Constant.IYUBA_CN;
    String FLAG = "4";
    String KPFLAG = "1";

    @GET("getAdEntryAll.jsp")
    Call<List<AiyubaAdvResult>> getAdvByaiyuba(@Query("uid") String uid, @Query("appId") String appid,
                                               @Query("flag") String flag);


    @GET("getAdEntryAll.jsp")
    Call<List<MessageLiuResult>> getMessageLiuType(
            @Query("appId") String appid,
            @Query("flag") String flag);


}
