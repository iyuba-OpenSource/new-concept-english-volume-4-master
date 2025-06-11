package com.iyuba.conceptEnglish.lil.concept_other.remote.service;

import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceData;
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceResponse;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_marge;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_publish;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_publish_marge;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_result;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.UrlLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.NetHostManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 评测的服务
 */
public interface EvalService {

    //提交评测
    //http://iuserspeech.iyuba.cn:9001/test/concept/
    //type			concept
    //userId			12071118
    //newsId			1001
    //paraId			1
    //IdIndex			1
    //sentence			lesson 1 Excuse me!
    //file	application/octet-stream	10011.mp3	4.99 KB (5,112 bytes)
    //wordId			0
    //flg			0
    //appId			222
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_IUSERSPEECH, StrLibrary.urlHost + ":" + NetHostManager.domain_short, StrLibrary.urlSuffix + ":" + UrlLibrary.SUFFIX_9001})
    @POST("/test/concept/")
    Observable<BaseBean_data<Concept_eval_result>> submitEval(@Body RequestBody body);

    //发布评测
    //get
    //http://voa.iyuba.cn/voa/UnicomApi?platform=android&format=json&protocol=60002&topic=concept&userid=15399731&username=test9653&voaid=1003&idIndex=3&paraid=1&score=40&shuoshuotype=2&content=wav6/202405/concept/20240529/17169799007151254.mp3
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_VOA, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET("/voa/UnicomApi")
    Observable<Concept_eval_publish> publishEval(@Query(StrLibrary.platform) String platform,
                                                 @Query(StrLibrary.format) String format,
                                                 @Query(StrLibrary.protocol) int protocol,
                                                 @Query(StrLibrary.topic) String topic,
                                                 @Query(StrLibrary.userid) int userId,
                                                 @Query(StrLibrary.username) String userName,
                                                 @Query(StrLibrary.voaid) int voaId,
                                                 @Query(StrLibrary.idIndex) String idIndex,
                                                 @Query(StrLibrary.paraid) String paraId,
                                                 @Query(StrLibrary.score) String score,
                                                 @Query(StrLibrary.shuoshuotype) int shuoType,
                                                 @Query(StrLibrary.content) String evalAudioUrl);

    //合成音频
    //http://iuserspeech.iyuba.cn:9001/test/merge/
    //audios	wav6/202405/concept/20240529/17169814963668210.mp3,wav6/202405/concept/20240529/17169804976673786.mp3,
    //type	concept
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_IUSERSPEECH,StrLibrary.urlHost+":"+NetHostManager.domain_short,StrLibrary.urlSuffix+":"+UrlLibrary.SUFFIX_9001})
    @FormUrlEncoded
    @POST("/test/merge/")
    Observable<Concept_eval_marge> margeAudio(@Field(StrLibrary.audios) String audioStr,
                                              @Field(StrLibrary.type) String type);

    //发布合成音频
    //http://voa.iyuba.cn/voa/UnicomApi
    //topic	concept
    //platform	android
    //format	json
    //protocol	60003
    //userid	15399731
    //username	test9653
    //voaid	1003
    //score	38
    //shuoshuotype	4
    //content	wav6/202405/concept/20240529/17169814998537090.mp3
    //appid	222
    //rewardVersion	1
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_VOA,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @FormUrlEncoded
    @POST("/voa/UnicomApi")
    Observable<Concept_eval_publish_marge> publishMarge(@Field(StrLibrary.topic) String topic,
                                                        @Field(StrLibrary.platform) String platform,
                                                        @Field(StrLibrary.format) String format,
                                                        @Field(StrLibrary.protocol) int protocol,
                                                        @Field(StrLibrary.userid) int userId,
                                                        @Field(StrLibrary.username) String userName,
                                                        @Field(StrLibrary.voaid) int voaId,
                                                        @Field(StrLibrary.score) String score,
                                                        @Field(StrLibrary.shuoshuotype) int shuoType,
                                                        @Field(StrLibrary.content) String margeAudioUrl,
                                                        @Field(StrLibrary.appid) int appId,
                                                        @Field(StrLibrary.rewardVersion) int rewardVersion);
}
