package com.iyuba.conceptEnglish.api;

import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface UpdateUnitTitleAPI {

    //    http://apps."+ Constant.IYUBA_CN+"concept/getConceptTitle.jsp?book=1&language=US&flg=1
    String TYPE_UK = "UK";
    String TYPE_US = "US";
    String url = "http://apps."+ Constant.IYUBA_CN+"concept/getConceptTitle.jsp";
    int flgContainNew = 1;
    int flgExcludeNew = 0;

    @GET
    Call<UnitTitle> getData(
            @Url String url,
            @Query("book") int bookId,
            @Query("language") String type,
            @Query("flg") int flg
    );



}

