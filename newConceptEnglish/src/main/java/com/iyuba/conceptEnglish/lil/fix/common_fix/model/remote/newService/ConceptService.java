package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.newService;

import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceData;
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceResponse;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.UrlLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.NetHostManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.studyReport.StudyReportManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_voatext;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Collect_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_comment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_four_chapter_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_four_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_junior_chapter_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_junior_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_result_concept;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_chapter_collect;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Pdf_url;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Publish_eval;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Study_report;

import java.util.List;

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
 * @title: 服务-新概念
 * @date: 2023/7/4 16:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface ConceptService {
    //接口
    class Interface{
        //书籍
        private static final String concept_junior_book = "/iyuba/getTitleBySeries.jsp";
        //章节
        private static final String concept_four_chapter = "/concept/getConceptTitle.jsp";
        private static final String concept_junior_chapter = "/iyuba/getTitleBySeries.jsp";
        //章节详情
        private static final String concept_four_chapter_detail = "/concept/getConceptSentence.jsp";
        private static final String concept_junior_chapter_detail = "/iyuba/textExamApi.jsp";
        //单词
        private static final String concept_four_word = "/concept/getConceptWord.jsp";
        private static final String concept_junior_word = "/iyuba/getWordByUnit.jsp";
    }

    /******************************pdf************************************/
    //获取新概念的pdf下载链接-英文
    //http://apps.iyuba.cn/iyuba/getConceptPdfFile_eg.jsp?type=concept&voaid=1002
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+ NetHostManager.domain_short})
    @GET(UrlLibrary.concept_pdf_url_eg)
    Observable<Pdf_url> getConceptEnPdfDownloadUrl(@Query(StrLibrary.type) String type,
                                                   @Query(StrLibrary.voaid) String voaId);

    //获取新概念的pdf下载链接-双语
    //http://apps.iyuba.cn/iyuba/getConceptPdfFile.jsp?type=concept&voaid=1002
    @Headers({StrLibrary.urlPrefix+":"+ UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+ NetHostManager.domain_short})
    @GET(UrlLibrary.concept_pdf_url)
    Observable<Pdf_url> getConceptPdfDownloadUrl(@Query(StrLibrary.type) String type,
                                                 @Query(StrLibrary.voaid) String voaId);

    /****************************************收藏*****************************/
    //收藏/取消收藏文章
    //http://apps.iyuba.cn/iyuba/updateCollect.jsp?groupName=Iyuba&sentenceFlg=0&appId=260&userId=12071118&topic=primary&voaId=313026&sentenceId=0&type=insert
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.COLLECT_ARTICLE)
    Observable<Collect_chapter> collectArticle(@Query(StrLibrary.groupName) String groupName,
                                               @Query(StrLibrary.sentenceFlg) String sentenceFlg,
                                               @Query(StrLibrary.appId) int appId,
                                               @Query(StrLibrary.userId) String userId,
                                               @Query(StrLibrary.topic) String topic,
                                               @Query(StrLibrary.voaId) String voaId,
                                               @Query(StrLibrary.sentenceId) String sentenceId,
                                               @Query(StrLibrary.type) String type);

    //获取收藏的文章数据
    //http://cms.iyuba.cn/dataapi/jsp/getCollect.jsp?userId=12071118&sign=a9f0a998cf149fd187145a3abb176a30&topic=primary&appid=260&sentenceFlg=0
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_CMS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(UrlLibrary.COURSE_COLLECT)
    Observable<BaseBean_data<List<Junior_chapter_collect>>> getArticleCollect(@Query(StrLibrary.userId) int userId,
                                                                              @Query(StrLibrary.sign) String sign,
                                                                              @Query(StrLibrary.topic) String topic,
                                                                              @Query(StrLibrary.appid) int appId,
                                                                              @Query(StrLibrary.sentenceFlg) int flag);

    /*************************章节详情***************************/
    //获取全四册的章节详情数据
    //http://apps.iyuba.cn/concept/getConceptSentence.jsp?voaid=1001
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(Interface.concept_four_chapter_detail)
    Observable<BaseBean_data<List<Concept_four_chapter_detail>>> getConceptFourChapterDetailData(@Query(StrLibrary.voaid) String voaId);

    //获取青少版的章节详情数据
    //http://apps.iyuba.cn/iyuba/textExamApi.jsp?voaid=321001
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(Interface.concept_junior_chapter_detail)
    Observable<BaseBean_voatext<List<Concept_junior_chapter_detail>>> getConceptJuniorChapterDetailData(@Query(StrLibrary.voaid) String voaId);

    /***********************************************单词********************************/
    //获取全四册的单词数据
    //http://apps.iyuba.cn/concept/getConceptWord.jsp?book=1
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(Interface.concept_four_word)
    Observable<BaseBean_data<List<Concept_four_word>>> getConceptFourWordData(@Query(StrLibrary.book) String bookId);

    //获取青少版的单词数据
    //http://apps.iyuba.cn/iyuba/getWordByUnit.jsp?bookid=289
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_APPS,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET(Interface.concept_junior_word)
    Observable<BaseBean_data<List<Concept_junior_word>>> getConceptJuniorWordData(@Query(StrLibrary.bookid) String bookId);

    /***********************************************评论************************************/
    //获取新概念内容的评论数据
    //http://daxue.iyuba.cn/appApi/UnicomApi?protocol=60001&platform=android&format=xml&voaid=2001&pageNumber=1&pageCounts=15&appName=concept
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_DAXUE,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @POST("/appApi/UnicomApi")
    Observable<Concept_comment> getConceptCommentData(@Query(StrLibrary.protocol) int protocol,
                                                      @Query(StrLibrary.platform) String platform,
                                                      @Query(StrLibrary.format) String format,
                                                      @Query(StrLibrary.voaid) String voaid,
                                                      @Query(StrLibrary.pageNumber) int pageNumber,
                                                      @Query(StrLibrary.pageCounts) int pageCounts,
                                                      @Query(StrLibrary.appName) String appName);

    /**************************************************学习报告********************************/
    //提交听力的学习报告
    //http://daxue.iyuba.cn/ecollege/updateStudyRecordNew.jsp?format=json&platform=android&appName=concept&Lesson=concept&appId=222&BeginTime=2023-11-10+14%3A32%3A07&EndTime=2023-11-10+14%3A32%3A13&EndFlg=0&LessonId=30010&TestNumber=1&TestWords=0&TestMode=1&UserAnswer=&Score=0&DeviceId=&uid=14977580&sign=2efebc3d06007c3e38023af0779f9eb3&rewardVersion=1
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_DAXUE,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @POST("/ecollege/updateStudyRecordNew.jsp")
    Observable<Study_report> submitListenReport(@Query(StrLibrary.format) String format,
                                                @Query(StrLibrary.platform) String platform,
                                                @Query(StrLibrary.appName) String appName,
                                                @Query(StrLibrary.Lesson) String lesson,
                                                @Query(StrLibrary.appId) int appId,
                                                @Query(StrLibrary.BeginTime) String startTime,
                                                @Query(StrLibrary.EndTime) String endTime,
                                                @Query(StrLibrary.EndFlg) int flag,
                                                @Query(StrLibrary.LessonId) int voaId,
                                                @Query(StrLibrary.TestNumber) int testNum,
                                                @Query(StrLibrary.TestMode) int testMode,
                                                @Query(StrLibrary.TestWords) String testWord,
                                                @Query(StrLibrary.UserAnswer) String userAnswer,
                                                @Query(StrLibrary.Score) String score,
                                                @Query(StrLibrary.DeviceId) String deviceId,
                                                @Query(StrLibrary.uid) int userId,
                                                @Query(StrLibrary.sign) String sign,
                                                @Query(StrLibrary.rewardVersion) int version);

    /****************************************评测*************************************/
    //句子评测
    //http://iuserspeech.iyuba.cn:9001/test/concept/
    //sentence			What%20does%20a%20pen%20have%20to%20do%20to%20record%20on%20paper%20the%20vibrations%20generated%20by%20an%20earthquake%3F
    //paraId			1
    //newsId			4042
    //IdIndex			2
    //type			concept
    //userId			14977580
    //file	application/octet-stream	/storage/emulated/0/Android/data/com.iyuba.learnNewEnglish/files/audio/sound14977580/40422.mp4	5.37 KB (5,496 bytes)
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_IUSERSPEECH,StrLibrary.urlHost+":"+NetHostManager.domain_short,StrLibrary.urlSuffix + ":" + UrlLibrary.SUFFIX_9001})
    @POST("/test/concept/")
    Observable<EvaluationSentenceResponse> submitEval(@Body RequestBody body);

    /************************************************发布评测************************/
    //句子评测发布
    //http://voa.iyuba.cn/voa/UnicomApi?platform=android&format=json&protocol=60002&topic=concept&userid=14977580&username=newtest101&voaid=1002&idIndex=3&paraid=1&score=41&shuoshuotype=2&content=wav6/202311/concept/20231118/17002727290802218.mp3
    @Headers({StrLibrary.urlPrefix+":"+UrlLibrary.HTTP_VOA,StrLibrary.urlHost+":"+NetHostManager.domain_short})
    @GET("/voa/UnicomApi")
    Observable<Publish_eval> publishEval(@Query(StrLibrary.platform) String platform,
                                         @Query(StrLibrary.format) String json,
                                         @Query(StrLibrary.protocol) int protocol,
                                         @Query(StrLibrary.topic) String topic,
                                         @Query(StrLibrary.userid) int userid,
                                         @Query(StrLibrary.username) String username,
                                         @Query(StrLibrary.voaid) int voaid,
                                         @Query(StrLibrary.idIndex) String idIndex,
                                         @Query(StrLibrary.paraid) String paraid,
                                         @Query(StrLibrary.score) String score,
                                         @Query(StrLibrary.shuoshuotype) int shuoshuotype,
                                         @Query(StrLibrary.content) String evalAudioUrl);
}

