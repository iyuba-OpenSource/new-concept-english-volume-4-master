package com.iyuba.conceptEnglish.PDF;


import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 下载pdf扣积分
 */
public interface UpdateScoreApi {

    String SRID = "40";
    String MOBILE = "1";
    String URL = "http://api." + Constant.IYUBA_CN + "credits/updateScore.jsp";

    // "updateScore.jsp?srid=40&mobile=1&flag=MjAxNzExMzAxMDU0NTA=&uid=4586981&appid=148&idindex=200560";
    @GET
    Call<UpdateScoreBean> ductPointsForPDF(
            @Url String url,
            @Query("srid") String srid,
            @Query("mobile") String mobile,
            @Query("flag") String flag,
            @Query("uid") String uid,
            @Query("appid") String appid,
            @Query("idindex") String idindex);
}

class UpdateScoreBean {

    public String result;
    public String addcredit;
    public String totalcredit;
}

