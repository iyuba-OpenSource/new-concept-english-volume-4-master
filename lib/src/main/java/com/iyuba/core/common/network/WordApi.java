package com.iyuba.core.common.network;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.sqlite.mode.UserInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuzhenli on 2017/4/14.
 */

public interface WordApi {
    public static final String MODE_INSERT = "insert";
    public static final String MODE_DELETE = "delete";
    String groupname = "Iyuba";

    String url = "http://word." + Constant.IYUBA_CN + "words/";

    @GET("updateWord.jsp")
    Call<updateWordInfo> updateWord(
            @Query("userId") String userId,
            @Query("mod") String mod,
            @Query("groupName") String groupName,
            @Query("word") String word
    );


    class updateWordInfo {
        String result;
        String word;
    }

}


