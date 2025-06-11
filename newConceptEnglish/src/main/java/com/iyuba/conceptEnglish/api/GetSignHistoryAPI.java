package com.iyuba.conceptEnglish.api;

import com.iyuba.conceptEnglish.entity.VoicesResult;
import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface GetSignHistoryAPI {

    String url = "http://app."+ Constant.IYUBA_CN+"getShareInfoShow.jsp";

    @GET
    Call<VoicesResult> getCalendar(@Url String path,
                                   @Query("uid") String uid,
                                   @Query("appId") String appId,
                                   @Query("time") String time);



}

