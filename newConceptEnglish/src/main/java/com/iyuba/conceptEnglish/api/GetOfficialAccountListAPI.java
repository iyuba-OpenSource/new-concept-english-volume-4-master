package com.iyuba.conceptEnglish.api;

import com.iyuba.conceptEnglish.entity.OfficialAccountListResponse;
import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface GetOfficialAccountListAPI {

    String url = "http://apps."+ Constant.IYUBA_CN+"iyuba/getOfficialAccount.jsp";

    @GET
    Call<OfficialAccountListResponse> getList(@Url String path,
                                                  @Query("pageNumber") int pageNumber,
                                                  @Query("pageCount") int pageCount,
                                                  @Query("newsfrom") String newsfrom);



}

