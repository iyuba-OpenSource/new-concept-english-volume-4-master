package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.newService;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.UrlLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.NetHostManager;
import com.iyuba.core.lil.user.remote.User_info;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @title:
 * @date: 2023/11/3 09:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface UserInfoService {

    //获取用户信息
    //http://api.iyuba.com.cn/v2/api.iyuba?protocol=20001&myid=13865961&appid=260&format=json&sign=4603fde5e47c4c44628e30912817d8e5&id=13865961&platform=android
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_API,StrLibrary.urlHost+":"+ NetHostManager.domain_long})
    @GET("/v2/api.iyuba")
    Observable<User_info> getUserInfo(@Query(StrLibrary.protocol) int protocol,
                                         @Query(StrLibrary.appid) int appId,
                                         @Query(StrLibrary.myid) long uid,
                                         @Query(StrLibrary.id) long searchUserId,
                                         @Query(StrLibrary.format) String format,
                                         @Query(StrLibrary.sign) String sign,
                                         @Query(StrLibrary.platform) String platform);
}
