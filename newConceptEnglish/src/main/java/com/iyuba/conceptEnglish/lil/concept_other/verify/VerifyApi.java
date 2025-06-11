package com.iyuba.conceptEnglish.lil.concept_other.verify;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @desction: 用于审核接口的处理
 * @date: 2023/3/23 17:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface VerifyApi {

    //http://api.qomolama.cn/getRegisterAll.jsp
    String BASEURL_1 = "http://api.qomolama.cn/getRegisterAll.jsp";

    //微课审核接口处理
    @GET
    Observable<AppCheckResponse> verifyMoc(@Url String url, @Query("appId") int appId, @Query("appVersion") String version);
}
