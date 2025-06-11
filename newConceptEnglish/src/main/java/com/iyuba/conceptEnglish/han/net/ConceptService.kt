package com.iyuba.conceptEnglish.han.net

import com.iyuba.conceptEnglish.han.bean.CorrectSoundResponse
import com.iyuba.conceptEnglish.han.bean.DeleteResponse
import com.iyuba.conceptEnglish.han.bean.DeleteVideoResponse
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceResponse
import okhttp3.RequestBody
import retrofit2.http.*

/**
苏州爱语吧科技有限公司
 */
interface ConceptService {
    @POST
    suspend fun evaluationSentence(
        @Url url:String,
        @Body body: RequestBody
    ): EvaluationSentenceResponse

    //纠音&&单词评测
    @GET
    suspend fun correctSound(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ): CorrectSoundResponse

    //删除评论
    @GET
    suspend fun deleteComment(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ): DeleteResponse
    //删除评测
    @GET
    suspend fun deleteEval(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ): DeleteVideoResponse
}