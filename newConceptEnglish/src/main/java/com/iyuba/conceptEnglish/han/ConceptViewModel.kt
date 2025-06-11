//package com.iyuba.conceptEnglish.han
//
//import androidx.lifecycle.ViewModel
//import com.iyuba.conceptEnglish.han.bean.CorrectSoundResponse
//import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceDataItem
//import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceResponse
//import com.iyuba.conceptEnglish.han.net.ConceptService
//import com.iyuba.conceptEnglish.han.net.ServiceCreator
//import com.iyuba.conceptEnglish.han.utils.changeEncode
//import com.iyuba.conceptEnglish.han.utils.realVoaId
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail
//import com.iyuba.configation.Constant
//import com.iyuba.core.lil.user.UserInfoManager
//import okhttp3.MediaType
//import okhttp3.MultipartBody
//import java.io.File
//
///**
//苏州爱语吧科技有限公司
// */
//class ConceptViewModel : ViewModel() {
//    private val conceptService = ServiceCreator.create<ConceptService>()
//    suspend fun evaluationSentence(item: VoaDetail, file: File, wordId: String = "0"): EvaluationSentenceResponse {
//        val body = MultipartBody.create(MediaType.parse("application/octet-stream"), file)
//        val flg = (if (wordId == "0") "0" else "2")
//        val builder = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("type", Constant.AppName)
//            .addFormDataPart("userId", UserInfoManager.getInstance().userId.toString())
//            .addFormDataPart("newsId", item.realVoaId())
//            .addFormDataPart("paraId", item.paraId)
//            .addFormDataPart("IdIndex", item.lineN)
//            .addFormDataPart("sentence", item.sentence)
//            .addFormDataPart("file", file.name, body)
//            .addFormDataPart("wordId", wordId)
//            .addFormDataPart("flg", flg)
//            .addFormDataPart("appId", Constant.APPID)
//            .build()
//        return conceptService.evaluationSentence(Constant.evalUrl, builder)
//    }
//    suspend fun correctSound(word:String,item: EvaluationSentenceDataItem): CorrectSoundResponse {
//        val map= mutableMapOf<String,String>().apply {
//            put("q",word)
//            put("user_pron",item.user_pron.changeEncode())
//            put("ori_pron",item.pron.changeEncode())
//        }
//        return conceptService.correctSound("http://word.${Constant.IYUBA_CN_IN}/words/apiWordAi.jsp", map)
//    }
//    /*fun deleteComment(id:String,uid:String,success:(response:DeleteResponse)->Unit,failed:(e:Exception)->Unit) {
//        val protocol="60004"
//        val code=(protocol+id+"Iyuba").toMd5()
//        val url="http://daxue.${Constant.IYUBA_CN}appApi/UnicomApi"
//        val map= mutableMapOf<String,String>().apply {
//            put("platform","android")
//            put("format","json")
//            put("protocol",protocol)
//            put("userid",uid)
//            put("id",id)
//            put("code",code)
//        }
//        viewModelScope.launch {
//            try {
//                success(conceptService.deleteComment(url,map))
//            }catch (e:Exception){
//                failed(e)
//            }
//        }
//    }*/
//
//    /*fun deleteEval(id:String,success:(response:DeleteVideoResponse)->Unit,failed:(e:Exception)->Unit) {
//        val url="http://voa.${Constant.IYUBA_CN}/voa/UnicomApi"
//        val map= mutableMapOf<String,String>().apply {
//            put("protocol","61003")
//            put("id",id)
//        }
//        viewModelScope.launch {
//            try {
//                success(conceptService.deleteEval(url,map))
//            }catch (e:Exception){
//                failed(e)
//            }
//        }
//    }*/
//}