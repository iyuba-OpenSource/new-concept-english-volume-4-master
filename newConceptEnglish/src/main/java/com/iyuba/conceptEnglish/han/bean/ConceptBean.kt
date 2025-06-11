package com.iyuba.conceptEnglish.han.bean

import com.iyuba.conceptEnglish.sqlite.mode.EvaluateBean
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
苏州爱语吧科技有限公司
 */
data class EvaluationSentenceResponse(
    val result:Int,
    val message:String,
    val data: EvaluationSentenceData
)
data class EvaluationSentenceData(
    val sentence:String,
    val total_score:Float,
    val scores:Int,
    val URL:String,
    val filepath:String,
    val words:List<EvaluationSentenceDataItem>
){
    fun convertEvaluateBean(): EvaluateBean {
        return EvaluateBean(URL).apply {
            this.sentence=this@EvaluationSentenceData.sentence
            this.total_score=this@EvaluationSentenceData.total_score.toString()
            this.words=this@EvaluationSentenceData.words.map {
                EvaluateBean.WordsBean(it.content,it.index,it.score.toDouble())
            }
        }
    }
    val realScopes get()="${scores}分"
}
data class EvaluationSentenceDataItem (
    var content:String="",
    var index:Int=0,
    var score:Float=0f,
    var delete: String="",
    var insert: String="",
    var pron: String="",
    var pron2: String="",
    var substitute_orgi: String="",
    var substitute_user: String="",
    var user_pron: String="",
    var user_pron2: String="",
    //以下数据不是json数据，是本地逻辑需要
    var userId:Int=0,
    var voaId:Int=0,
    var groupId:Int=0
)

data class CorrectSoundResponse(
    val audio: String="",
    val def: String="",
    val delete_id: List<List<Int>>,
    val insert_id: List<List<Int>>,
    val key: String="",
    val match_idx: List<List<Int>>,
    val ori_pron: String="",
    val pron: String="",
    val proncode: String="",
    val result: Int=0,
    val sent: List<PickWordItem>,
    val substitute_id: List<List<Int>>,
    val user_pron: String=""
){
    val realOri get() = "[$ori_pron]"
    val realUserPron get() = "[$user_pron]"
}
@Root(name = "sent" )
data class PickWordItem @JvmOverloads constructor(
    @field:Element(name = "number")
    var number:Int=0,
    @field:Element(name = "orig")
    var orig:String="",
    @field:Element(name = "trans")
    var trans:String=""
){
    fun getRealOrig()=orig.replace("<em>","'").replace("</em>","'")
}

data class DeleteResponse(val message: String, val result: Int)

data class DeleteVideoResponse(val Message: String, val ResultCode: Int)


//本地打卡记录，上次感觉原生sqlite似乎比LitePal快
data class LocalCalendarRecord(
    var uid:Int=0,
    var createTime:String="",
    var scan:Int=0
    )
