package com.iyuba.conceptEnglish.api;


import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 获取群号
 */

public interface QQGroupApi {

    public String URL = "http://m." + Constant.IYUBA_CN + "m_login/getQQGroup.jsp";


    @GET
    Call<QQGroupBean> getQQGroup(
            @Url String url,
            @Query("type") String type);


    class QQGroupBean {
        /**
         * message : true
         * QQ : 433075910
         * key : lr0jfBh_9Ly0S3iUPUnCSNhAV8UkiQRI
         */

        public String message;
        public String QQ;
        public String key;
    }
}
