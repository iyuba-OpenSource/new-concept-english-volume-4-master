package com.iyuba.conceptEnglish.api;

import com.iyuba.conceptEnglish.sqlite.mode.UpdateEvalDataBean;
import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface UpdateEvalDataApi {


    String url = "http://"+Constant.userSpeech + "management/getVoaTestRecord.jsp";
//    http://"+Constant.userSpeech+Constant.IYUBA_CN+"management/getVoaTestRecord.jsp?userId=8866880&newstype=concept
    @GET
    Call<UpdateEvalDataBean> getData(
            @Url String url,
            @Query("userId") String userId,
            @Query("newstype") String newstype
    );
}
