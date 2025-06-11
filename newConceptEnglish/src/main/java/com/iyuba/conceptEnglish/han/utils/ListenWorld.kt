package com.iyuba.conceptEnglish.han.utils

import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail

/**
苏州爱语吧科技有限公司
 */
interface OnEvaluationListener{
    //请求单句评测接口
    fun showDialog(currentItem:VoaDetail,word:String)
}
