package com.iyuba.conceptEnglish.api;

import com.iyuba.conceptEnglish.api.data.ImoocRecordBean;
import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface UpdateImoocRecordAPI {

    //    http://daxue."+Constant.IYUBA_CN+"ecollege/getMicroStudyRecord.jsp?uid=8866880&Lesson=class.jichu&Pageth=1&NumPerPage=1000&sign=41f5a76442a659d9fd25dcb102fb5e4d
    String url = "http://daxue."+ Constant.IYUBA_CN+"ecollege/getMicroStudyRecord.jsp";
    String lesson = "class.jichu";
    int Pageth = 1;
    int NumPerPage = 1000;

    @GET
    Call<ImoocRecordBean> getData(
            @Url String url,
            @Query("uid") String uid,
            @Query("Lesson") String Lesson,
            @Query("Pageth") int Pageth,
            @Query("NumPerPage") int NumPerPage,
            @Query("sign") String sign
    );


}

