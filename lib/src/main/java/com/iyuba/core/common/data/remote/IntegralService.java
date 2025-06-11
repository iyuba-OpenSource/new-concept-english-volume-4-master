package com.iyuba.core.common.data.remote;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.DownLoadJFResult;
import com.iyuba.core.common.data.model.HttpUtil;
import com.iyuba.core.common.data.model.IntegralBean;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

/**
 * IntegralService 旧的网络请求方式！！！
 *
 * @author wayne
 * @date 2018/2/8
 */
public interface IntegralService {
    String ENDPOINT = "http://api."+ Constant.IYUBA_CN+"";

      /*
    http://api."+com.iyuba.talkshow.Constant.Web.WEB_SUFFIX+"credits/updateScore.jsp?srid=40&mobile=1" +
            "&flag=%s=&uid=%s&appid=%s&idindex=%s", flag, AccountManager.Instace(mContext).getId(), Constant.APPID, idIndex
    */


    @GET("credits/updateScore.jsp")
    Observable<DownLoadJFResult> integral(@Query("srid") String srid,
                                          @Query("mobile") int mobile,
                                          @Query("flag") String flag,
                                          @Query("uid") int uid,
                                          @Query("appid") int appid,
                                          @Query("idindex") int idindex);

    class Creator {

        public static IntegralService newIntegralService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(IntegralService.class);
        }
    }
}
