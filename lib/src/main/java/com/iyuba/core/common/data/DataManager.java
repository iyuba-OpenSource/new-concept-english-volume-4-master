package com.iyuba.core.common.data;

import android.util.Log;
import android.util.Pair;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.core.common.data.model.CheckIPResponse;
import com.iyuba.core.common.data.model.ChildWordResponse;
import com.iyuba.core.common.data.model.ChildWordUpData;
import com.iyuba.core.common.data.model.ClearUserResponse;
import com.iyuba.core.common.data.model.GetMyDubbingResponse;
import com.iyuba.core.common.data.model.GetRankingResponse;
import com.iyuba.core.common.data.model.IntegralBean;
import com.iyuba.core.common.data.model.PdfResponse;
import com.iyuba.core.common.data.model.SendDubbingResponse;
import com.iyuba.core.common.data.model.SendEvaluateResponse;
import com.iyuba.core.common.data.model.TalkClass;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.model.ThumbsResponse;
import com.iyuba.core.common.data.model.UploadUserInfoResponse;
import com.iyuba.core.common.data.model.UserDetailInfoResponse;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.common.data.model.VoaTextYouthByBook;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.data.model.Word;
import com.iyuba.core.common.data.remote.AiResponse;
import com.iyuba.core.common.data.remote.AiService;
import com.iyuba.core.common.data.remote.ApiComService;
import com.iyuba.core.common.data.remote.ApiService;
import com.iyuba.core.common.data.remote.AppsResponse;
import com.iyuba.core.common.data.remote.AppsService;
import com.iyuba.core.common.data.remote.CheckIPService;
import com.iyuba.core.common.data.remote.CmsApiService;
import com.iyuba.core.common.data.remote.CmsResponse;
import com.iyuba.core.common.data.remote.VoaApiService;
import com.iyuba.core.common.data.remote.VoaService;
import com.iyuba.core.common.data.remote.VoaTextBySeriesResponse;
import com.iyuba.core.common.data.remote.VoaTextResponse;
import com.iyuba.core.common.data.remote.WordCollectResponse;
import com.iyuba.core.common.data.remote.WordCollectService;
import com.iyuba.core.common.data.remote.WordResponse;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.module.toolbox.MD5;
import com.iyuba.module.toolbox.SingleParser;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import timber.log.Timber;

public class DataManager {
    private static DataManager sInstance = new DataManager();

    public static DataManager getInstance() {
        return sInstance;
    }

    private final CmsApiService mCmsApiService;
    private final AppsService mAppsService;
    private final WordCollectService mWordCollectService;
    private final VoaApiService mVoaApiService;
    private final AiService mAiService;
    private final VoaService mVoaService;
    private final ApiService mApiService;
    private final ApiComService mApiComService;
    private final CheckIPService mCheckIPService;

    private static final SimpleDateFormat CREDIT_TIME = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private final DubDBManager dubDBManager;

    private DataManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);

        OkHttpClient client = builder.build();
        SimpleXmlConverterFactory xmlFactory = SimpleXmlConverterFactory.create();
        GsonConverterFactory gsonFactory = GsonConverterFactory.create();
        RxJava2CallAdapterFactory rxJavaFactory = RxJava2CallAdapterFactory.create();


        mCmsApiService = CmsApiService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mAppsService = AppsService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mWordCollectService = WordCollectService.Creator.createService();
        mVoaApiService = VoaApiService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mAiService = AiService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mVoaService = VoaService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mApiService = ApiService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mApiComService = ApiComService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mCheckIPService = CheckIPService.Creator.createService(client, gsonFactory, rxJavaFactory);


        dubDBManager = DubDBManager.getInstance();
    }

    public Single<List<TalkClass>> getTalkClass(String type) {
        String sign = buildV2Sign("iyuba", getCurrentDate(), type);
        return mCmsApiService.getTalkClass(type, sign, "json")
                .compose(this.<CmsResponse.TalkClassList, List<TalkClass>>applyParser());
    }

    public Single<List<TalkClass>> getTalkClassLesson(String type) {
        String sign = buildV2Sign("iyuba", getCurrentDate(), "series");
        Timber.d("Sign: %s", sign);
        return mAppsService.getTalkClassLesson("category", sign, type, "json")
                .compose(this.<AppsResponse.TalkClassList, List<TalkClass>>applyParser());
    }

    public Single<List<TalkLesson>> getTalkLessonOld(String classId) {
        String type = "series";
        String total = "200";
        String sign = buildV2Sign("iyuba", getCurrentDate(), type);
        return mCmsApiService.getTalkLessonOld(type, classId, sign, total, "json")
                .compose(this.<CmsResponse.TalkLessonList, List<TalkLesson>>applyParser());
    }

    public Single<List<TalkLesson>> getTalkLesson(String classId) {
        String type = "title";
        String sign = buildV2Sign("iyuba", getCurrentDate(), "series");
        Timber.d("wangwenyang  getTalkLesson sign " + sign);
        return mAppsService.getTalkLesson(type, classId, sign)
                .compose(this.<AppsResponse.TalkLessonList, List<TalkLesson>>applyParser());
    }



    public Single<Boolean> deleteWords(int userId, List<String> words) {
        String wordsStr = buildUpdateWords(words);
        return mWordCollectService.updateWords(userId, "delete", "Iyuba", wordsStr)
                .compose(this.<WordCollectResponse.Update, Boolean>applyParser());
    }

    public Single<Boolean> insertWords(int userId, List<String> words) {
        String wordsStr = buildUpdateWords(words);
        return mWordCollectService.updateWords(userId, "insert", "Iyuba", wordsStr)
                .compose(this.<WordCollectResponse.Update, Boolean>applyParser());
    }


    public Single<Pair<List<Word>, Integer>> getNoteWords(int useId, final int pageNumber, int pageCounts) {
        return mWordCollectService.getNoteWords(useId, pageNumber, pageCounts)
                .flatMap(new Function<WordCollectResponse.GetNoteWords, SingleSource<? extends Pair<List<Word>, Integer>>>() {
                    @Override
                    public SingleSource<? extends Pair<List<Word>, Integer>> apply(WordCollectResponse.GetNoteWords response) throws Exception {
                        if (pageNumber <= response.lastPage && response.tempWords.size() > 0) {
                            List<Word> words = new ArrayList<>(response.tempWords.size());
                            for (WordCollectResponse.GetNoteWords.TempWord tempWord : response.tempWords) {
                                words.add(new Word(tempWord.word, tempWord.audioUrl,
                                        tempWord.pronunciation, tempWord.definition));
                            }
                            return Single.just(new Pair<>(words, response.counts));
                        } else {
                            List<Word> words = new ArrayList<>();
                            return Single.just(new Pair<>(words, 0));
                        }
                    }
                });
    }

    public Observable<WordResponse> getWordOnNet(String key) {
        return mWordCollectService.getNetWord(key);
    }

    private String buildUpdateWords(List<String> words) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i += 1) {
            if (i == 0) {
                sb.append(words.get(i));
            } else {
                sb.append(",").append(words.get(i));
            }
        }
        return sb.toString();
    }

    public Single<SendEvaluateResponse> uploadSentence(RequestBody body) {
        return mAiService.uploadSentence(body)
                .compose(this.<AiResponse.GetEvaluateResponse, SendEvaluateResponse>applyParser());
    }

    public Single<SendDubbingResponse> sendDubbingComment(Map<String, String> params, RequestBody body) {
        return mVoaService.sendDubbingComment(params, body)
                .compose(this.<SendDubbingResponse, SendDubbingResponse>applyParser());
    }

    public Single<GetRankingResponse> getThumbRanking(int voaId, int pageNum, int pageSize) {
        return mVoaService.getThumbRanking("android", "json", "60001", voaId,
                pageNum, pageSize, 2, "concept", "3")
                .compose(this.<GetRankingResponse, GetRankingResponse>applyParser());
    }

    public Single<ThumbsResponse> doAgree(int id) {
        return mVoaService.doThumbs(61001, id)//61002 是反对
                .compose(this.<ThumbsResponse, ThumbsResponse>applyParser());
    }

    public Single<GetMyDubbingResponse> getMyDubbing(int uId) {
        return mVoaService.getMyDubbing(uId) // 获取我的发布数据
                .compose(this.<GetMyDubbingResponse, GetMyDubbingResponse>applyParser());
    }
    public Single<GetMyDubbingResponse> getMyDubbing(int uId,String appName) {
        return mVoaService.getMyDubbing(uId,appName) // 获取我的发布数据
                .compose(this.<GetMyDubbingResponse, GetMyDubbingResponse>applyParser());
    }

    public Single<ThumbsResponse> deleteReleaseRecordList(String Id, int userID) {
        return mVoaService.deleteReleaseRecordList(Id, String.valueOf(userID)) // 获取我的发布数据
                .compose(this.<ThumbsResponse, ThumbsResponse>applyParser());
    }

    public Single<List<VoaTextYouthByBook>> getVoaTextsBySeries(int series, int userid, int appid) {
        Log.d("wangwenyang", "_series: "+series +"_userid: "+userid +"_appid: "+appid);
        return mVoaApiService.getVoaTextsBySeries("321", series, userid, appid)
                .compose(this.<VoaTextBySeriesResponse, List<VoaTextYouthByBook>>applyParser());
    }

    public Single<List<VoaText>> syncVoaTexts(int voaId) {
        return mVoaApiService.getVoaTexts("json", voaId)
                .compose(this.<VoaTextResponse, List<VoaText>>applyParser());
    }

    public Single<PdfResponse> getPdf(String type, int voaId, int language) {
        return mVoaApiService.getPdf(type, voaId, language)
                .compose(this.<PdfResponse, PdfResponse>applyParser());
    }

    //获取青少版代码
    public Single<List<VoaWord2>> getChildWords(String bookId) {
        return mVoaApiService.getChildWords(bookId)
                .compose(this.<ChildWordResponse, List<VoaWord2>>applyParser());
    }

    //获取青少版代码
    public Single<List<VoaWord2>> upDataDownload(String bookId, int version) {
        return mVoaApiService.upDataDownload(bookId, version)
                .compose(this.<ChildWordUpData, List<VoaWord2>>applyParser());
    }


    /**
     * 减积分
     *
     * @param appId voaId
     * @return -20
     */
    public Single<IntegralBean> deductIntegral(String flag, int uid, int appId, int idIndex) {
        return mApiService.deductIntegral(flag, uid, appId, idIndex)
                .compose(this.<IntegralBean, IntegralBean>applyParser());
    }

    //   return getVoaTexts(voaId);//重新拉取一遍
    //  //dubDBManager.setVoaTexts(voaTextResponse.voaTexts(), voaId);
    //                        //return getVoaTexts(voaId);

    /**
     * 注销账号
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回是否成功
     */
    public Single<ClearUserResponse> clearUser(String username, String password) {
        String protocol = "11005";
        String format = "json";
        String passwordMD5 = MD5.getMD5ofStr(password);
        String sign = buildV2Sign(protocol, username, passwordMD5, "iyubaV2");
        return mApiComService.clearUser(protocol, username, passwordMD5, sign, format)
                .compose(this.<ClearUserResponse, ClearUserResponse>applyParser());
    }

    private RequestBody fromString(String text) {
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }


    private MultipartBody.Part fromFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }


//    public Observable<List<VoaText>> getVoaTexts(final int voaId) {
//        return dubDBManager.getVoaTexts(voaId);
//    }


    private String getCurrentDate() {
        long timeStamp = new Date().getTime() / 1000 + 3600 * 8; //东八区;
        long days = timeStamp / 86400;
        return Long.toString(days);
    }

    private String buildV2Sign(String... stuffs) {
        StringBuilder sb = new StringBuilder();
        for (String stuff : stuffs) {
            sb.append(stuff);
        }
        return MD5.getMD5ofStr(sb.toString());
    }

    public Single<UserDetailInfoResponse> getUserInfo() {
        String protocol = "20002";
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        String platform = "android";
        String format = "json";
        String sign = buildV2Sign(protocol, uid, "iyubaV2");
        return mApiComService.getUserInfo(protocol, uid, sign, platform, format)
                .compose(this.<UserDetailInfoResponse, UserDetailInfoResponse>applyParser());
    }

    public Single<UploadUserInfoResponse> uploadUserInfo(String gender, String province,
                                                         String age, String city, String occupation) {
        String format = "json";
        String protocol = "99010";
        String platform = "android";
        String userId = String.valueOf(UserInfoManager.getInstance().getUserId());
        String appId = Constant.APPID;

        String encodedProvince = "";
        String encodedCity = "";
        String encodedOccupation = "";
        try {
            encodedProvince = doubleEncoded(province);
            encodedCity = doubleEncoded(city);
            encodedOccupation = doubleEncoded(occupation);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //encodedProvince = URLEncoder.encode(province, "UTF-8");
        String sign = buildV2Sign("iyubaV2" + userId);

        return mApiComService.uploadUserInfo(format, protocol, platform, userId, gender, age,
                appId, encodedProvince, encodedCity, encodedOccupation, sign)
                .compose(this.<UploadUserInfoResponse, UploadUserInfoResponse>applyParser());
    }

    private String doubleEncoded(String word) throws UnsupportedEncodingException {
        String output = URLEncoder.encode(word, "UTF-8");
        output = URLEncoder.encode(output, "UTF-8");

        return output;
    }

    public Single<CheckIPResponse> checkIP(String uid, String appid) {
        return mCheckIPService.checkIP(uid, appid)
                .compose(this.<CheckIPResponse, CheckIPResponse>applyParser());
    }

    // ----------------------- divider ---------------------------

    @SuppressWarnings("unchecked")
    private <T, R> SingleTransformer<T, R> applyParser() {
        return (SingleTransformer<T, R>) SingleParser.parseTransformer;
    }

}
