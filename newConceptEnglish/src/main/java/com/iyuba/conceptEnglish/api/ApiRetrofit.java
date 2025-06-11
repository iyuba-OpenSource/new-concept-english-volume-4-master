package com.iyuba.conceptEnglish.api;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.iyuba.conceptEnglish.PDF.PDFApi;
import com.iyuba.conceptEnglish.PDF.UpdateScoreApi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by iyuba on 2017/8/21.
 */

public class ApiRetrofit {

    private static ApiRetrofit apiRetrofit;
    private final Retrofit mRetrofit;
    private OkHttpClient mClient;
    private ApiService service;
    private YzPhoneNumber yzPhoneNumber;
    private AudioComposeApi audioComposeApi;
    private AudioSendApi audioSendApi;
    private SpeakCircleApi speakCircleApi;
    private QQGroupApi qqGroupApi;
    private AiyubaAdvApi aiyubaAdvApi;
    private UpdateEvalDataApi updateEvalDataApi;
    private UpdateTestDataApi updateTestDataApi;
    private PDFApi pdfApi;
    private UpdateScoreApi updateScoreApi;
    private UpdateTitleAPI updateTitleAPI;
    private GetDataForReadClick getDataForReadClick;
    private UpdateUnitTitleAPI updateUnitTitleAPI;
    private UpdateVoaDetailAPI updateVoaDetailAPI;
    private UpdateWordDetailAPI updateWordDetailAPI;
    private GetSignHistoryAPI getSignHistoryAPI;
    private GetOfficialAccountListAPI getOfficialAccountListAPI;
    private RefreshMicroReadPercentageAPI refreshMicroReadPercentageAPI;
    private UpdateImoocRecordAPI updateImoocRecordAPI;
    public UpdateTestAPI updateTestAPI;

    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {

            Log.e("okhttp", message);
        }
    });

    private ApiRetrofit() {
        mClient = new OkHttpClient.Builder()
                .addInterceptor(mHeaderInterceptor)//添加头部信息拦截器
                .addInterceptor(interceptor)//添加log拦截器
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//支持RxJava
                .client(mClient)
                .build();
        service = mRetrofit.create(ApiService.class);
        audioComposeApi = mRetrofit.create(AudioComposeApi.class);
        yzPhoneNumber = mRetrofit.create(YzPhoneNumber.class);
        audioSendApi = mRetrofit.create(AudioSendApi.class);
        speakCircleApi = mRetrofit.create(SpeakCircleApi.class);
        qqGroupApi = mRetrofit.create(QQGroupApi.class);
        aiyubaAdvApi = mRetrofit.create(AiyubaAdvApi.class);
        updateEvalDataApi = mRetrofit.create(UpdateEvalDataApi.class);
        updateTestDataApi = mRetrofit.create(UpdateTestDataApi.class);
        pdfApi = mRetrofit.create(PDFApi.class);
        updateScoreApi = mRetrofit.create(UpdateScoreApi.class);
        updateTitleAPI = mRetrofit.create(UpdateTitleAPI.class);
        getDataForReadClick = mRetrofit.create(GetDataForReadClick.class);
        updateUnitTitleAPI = mRetrofit.create(UpdateUnitTitleAPI.class);
        updateTestAPI = mRetrofit.create(UpdateTestAPI.class);
        updateVoaDetailAPI = mRetrofit.create(UpdateVoaDetailAPI.class);
        updateWordDetailAPI = mRetrofit.create(UpdateWordDetailAPI.class);
        getSignHistoryAPI = mRetrofit.create(GetSignHistoryAPI.class);
        getOfficialAccountListAPI=mRetrofit.create(GetOfficialAccountListAPI.class);
        refreshMicroReadPercentageAPI=mRetrofit.create(RefreshMicroReadPercentageAPI.class);
        updateImoocRecordAPI=mRetrofit.create(UpdateImoocRecordAPI.class);
    }

    /**
     * 请求访问quest和response拦截器
     */
    private Interceptor mLogInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            okhttp3.Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();

            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();

        }


    };

    /**
     * 增加头部信息的拦截器
     */
    private Interceptor mHeaderInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.108 Safari/537.36 2345Explorer/8.0.0.13547");
            builder.addHeader("Cache-Control", "max-age=0");
            builder.addHeader("Upgrade-Insecure-Requests", "1");
            builder.addHeader("X-Requested-With", "XMLHttpRequest");
            builder.addHeader("Cookie", "uuid=\"w:f2e0e469165542f8a3960f67cb354026\"; __tasessionId=4p6q77g6q1479458262778; csrftoken=7de2dd812d513441f85cf8272f015ce5; tt_webid=36385357187");
            return chain.proceed(builder.build());
        }
    };

    public YzPhoneNumber getYzPhoneNumber() {
        return yzPhoneNumber;
    }

    public static ApiRetrofit getInstance() {
        if (apiRetrofit == null) {
            synchronized (Object.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit();
                }
            }
        }
        return apiRetrofit;
    }

    public GetDataForReadClick getGetDataForReadClick() {
        return getDataForReadClick;
    }

    public void setGetDataForReadClick(GetDataForReadClick getDataForReadClick) {
        this.getDataForReadClick = getDataForReadClick;
    }

    public ApiService getApiService() {
        return service;
    }

    public AudioComposeApi getAudioComposeApi() {
        return audioComposeApi;
    }

    public AudioSendApi getAudioSendApi() {
        return audioSendApi;
    }

    public SpeakCircleApi getSpeakCircleApi() {
        return speakCircleApi;
    }

    public QQGroupApi getQqGroupApi() {
        return qqGroupApi;
    }

    public UpdateEvalDataApi getUpdateEvalDataApi() {
        return updateEvalDataApi;
    }

    public UpdateTestDataApi getUpdateTestDataApi() {
        return updateTestDataApi;
    }

    public AiyubaAdvApi getAiyubaAdvApi() {
        return aiyubaAdvApi;
    }

    public PDFApi getPdfApi() {
        return pdfApi;
    }

    public UpdateScoreApi getUpdateScoreApi() {
        return updateScoreApi;
    }

    public UpdateTitleAPI getUpdateTitleAPI() {
        return updateTitleAPI;
    }

    public UpdateUnitTitleAPI getUnitTitleAPI() {
        return updateUnitTitleAPI;
    }

    public GetSignHistoryAPI getGetSignHistoryAPI() {
        return getSignHistoryAPI;
    }

    public RefreshMicroReadPercentageAPI getRefreshMicroReadPercentageAPI() {
        return refreshMicroReadPercentageAPI;
    }

    public UpdateImoocRecordAPI getUpdateImoocRecordAPI() {
        return updateImoocRecordAPI;
    }

    public GetOfficialAccountListAPI getGetOfficialAccountListAPI() {
        return getOfficialAccountListAPI;
    }

    public UpdateVoaDetailAPI getUpdateVoaDetailAPI() {
        return updateVoaDetailAPI;
    }

    public void setUpdateVoaDetailAPI(UpdateVoaDetailAPI updateVoaDetailAPI) {
        this.updateVoaDetailAPI = updateVoaDetailAPI;
    }

    public UpdateWordDetailAPI getUpdateWordDetailAPI() {
        return updateWordDetailAPI;
    }
}
