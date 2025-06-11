package com.iyuba.conceptEnglish.api;


import com.iyuba.conceptEnglish.sqlite.mode.EvaMixBean;
import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 语音合成
 * Created by iyuba on
 */

public interface AudioComposeApi {

    String BASEURL = "http://"+Constant.userSpeech+"test/merge/";


    @FormUrlEncoded
    @POST
    Call<EvaMixBean> audioComposeApi(@Url String url,
                                     @Field(value = "audios",encoded = true) String audios,
                                     @Field("type") String type);

}
