package com.iyuba.conceptEnglish.api;

import com.iyuba.conceptEnglish.sqlite.mode.UpdateTestDataBean;
import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface UpdateTestDataApi {

    String url = "http://daxue." + Constant.IYUBA_CN + "ecollege/getTestRecordDetail.jsp";
//    http://daxue."+Constant.IYUBA_CN+"ecollege/getTestRecordDetail.jsp?
//    sign = md5.format(uid + YYYY-MM-DD)

    @GET
    Call<UpdateTestDataBean> getData(
            @Url String url,
            @Query("appId") String appId,
            @Query("uid") String uid,
            @Query("TestMode") String testMode,
            @Query("sign") String sign,
            @Query("format") String format,
            @Query("Pageth") int Pageth,
            @Query("NumPerPage") int NumPerPage
    );
}
