package com.iyuba.core.me.pay;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.sqlite.mode.PayInfo;
import com.iyuba.core.common.sqlite.mode.UserInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuzhenli on 2017/4/14.
 */

public interface PayInfoApi {
    String url = "http://vip."+ Constant.IYUBA_CN+"";

    @GET("notifyAliNew.jsp")
    Call<PayInfo> payInfoApi(
            @Query("data") String data
    );
}
