package com.iyuba.conceptEnglish.api;

import com.iyuba.conceptEnglish.entity.CommonResponce;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.configation.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface RefreshMicroReadPercentageAPI {

    //    https://apps."+Constant.IYUBA_CN+"concept/getConceptTitle.jsp?book=1&language=UK&uid=8866880
    String url = "http://apps."+ Constant.IYUBA_CN+"concept/getConceptTitle.jsp";

    String TYPE_UK = "UK";
    String TYPE_US = "US";

    @GET
    Call<CommonResponce<List<Voa>>> getData(
            @Url String url,
            @Query("book") int book,
            @Query("language") String language,
            @Query("uid") String uid
    );

}

