package com.iyuba.core.common.data.remote;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.ChildWordResponse;
import com.iyuba.core.common.data.model.ChildWordUpData;
import com.iyuba.core.common.data.model.PdfResponse;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface VoaApiService {
    String ENDPOINT = "http://apps."+ Constant.IYUBA_CN+"iyuba/";


    //http://apps."+ Constant.IYUBA_CN+"iyuba/textExamApiBySeries.jsp?category=321&series=278&userid=639600&appid=222
    @GET("textExamApiBySeries.jsp")
    Single<VoaTextBySeriesResponse> getVoaTextsBySeries(@Query("category") String category,
                                                @Query("series") int series,
                                                @Query("userid") int userid,
                                                @Query("appid") int appid);

    //http://apps."+ Constant.IYUBA_CN+"iyuba/textExamApi.jsp?format=gson&voaid=321001
    @GET("textExamApi.jsp")
    Single<VoaTextResponse> getVoaTexts(@Query("format") String format,
                                        @Query("voaid") int voaId);

    @GET("getVoapdfFile_new.jsp")
    Single<PdfResponse> getPdf(@Query("type") String type,
                               @Query("voaid") int voaId,
                               @Query("isenglish") int isEnglish);
    //http://cms."+Constant.IYUBA_CN+"dataapi/jsp/getTitleBySeries.jsp?type=text&voaid=321001&sign=74c452dcc2055c02b3b291a5bd2bafce


    //http://apps."+ Constant.IYUBA_CN+"iyuba/getWordByUnit.jsp?bookid=278
    @GET("getWordByUnit.jsp")
    Single<ChildWordResponse> getChildWords(@Query("bookid") String bookId);

    @GET("updateWords.jsp")
    Single<ChildWordUpData> upDataDownload(@Query("bookid") String bookId,
                                           @Query("version") int version);

    class Creator {
        public static VoaApiService createService(OkHttpClient client, GsonConverterFactory gsonFactory,
                                                  RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(VoaApiService.class);
        }
    }
}
