package com.iyuba.conceptEnglish.api;

import com.iyuba.conceptEnglish.sqlite.mode.BaseBean;
import com.iyuba.conceptEnglish.sqlite.mode.SpeakCircleBean;
import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface SpeakCircleApi {


    String url = "http://voa." + Constant.IYUBA_CN + "voa/UnicomApi";
    String protocol = "60001";

//    http://voa."+Constant.IYUBA_CN+"voa/UnicomApi?
// protocol=60001&topic=concept&
// selflg=1&pageNumber=1&pageCounts=30&appid=222&userid=5492787

    //selflg: 0 全部 1 自己  2好友
    @GET
    Call<SpeakCircleBean> getSpeakList(
            @Url String url,
            @Query("protocol") String protocol,
            @Query("topic") String topic,
            @Query("selflg") String selflg,
            @Query("pageNumber") String pageNumber,
            @Query("pageCounts") String pageCounts,
            @Query("appid") String appid,
            @Query("userid") String userid
    );


    String protocol_give_five = "61001";

    //评论点赞
    @GET
    Call<BaseBean> giveFive(
            @Url String url,
            @Query("protocol") String protocol, @Query("id") String commentId, @Query("userid") int userId

    );
}
