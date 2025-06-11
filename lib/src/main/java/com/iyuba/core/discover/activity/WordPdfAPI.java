package com.iyuba.core.discover.activity;

import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WordPdfAPI {
    String BASEURL = "http://"+Constant.userSpeech + "management/";

    @GET("getWordToPDF.jsp")
    Call<WordPdfBean> getWordPdf(@Query("u") String userId,
                                 @Query("pageNumber") int pageNumber,
                                 @Query("pageCounts") int pageCounts);

}
