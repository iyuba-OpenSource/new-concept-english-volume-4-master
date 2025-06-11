package com.iyuba.conceptEnglish.api;


import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.sqlite.mode.EvaSendBean;
import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 语音发布
 * Created by iyuba on
 */

public interface AudioSendApi {

    String BASEURL = "http://voa." + Constant.IYUBA_CN + "voa/UnicomApi";
    String platform = "android";
    String format = "json";
    String protocol = "60003";

    @FormUrlEncoded
    @POST
    Call<EvaSendBean> audioSendApi(@Url String url,
                                   @Field("topic") String topic,
                                   @Field("platform") String platform,
                                   @Field("format") String format,
                                   @Field("protocol") String protocol,
                                   @Field("userid") String userid,
                                   @Field("username") String username,
                                   @Field("voaid") String voaid,
                                   @Field("score") String score,
                                   @Field("shuoshuotype") String shuoshuotype,
                                   @Field("content") String content,
                                   @Field("appid") int appId,
                                   @Field(StrLibrary.rewardVersion) int rewardVersion);

}
