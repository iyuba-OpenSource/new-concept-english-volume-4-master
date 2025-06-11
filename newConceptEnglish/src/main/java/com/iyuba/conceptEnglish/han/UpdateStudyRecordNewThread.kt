//package com.iyuba.conceptEnglish.han
//
//import android.util.Log
//import com.iyuba.conceptEnglish.R
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary
//import com.iyuba.conceptEnglish.lil.fix.common_fix.util.BigDecimalUtil
//import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil
//import com.iyuba.conceptEnglish.manager.BackgroundManager
//import com.iyuba.conceptEnglish.manager.VoaDataManager
//import com.iyuba.conceptEnglish.protocol.DataCollectRequest
//import com.iyuba.conceptEnglish.protocol.DataCollectResponse
//import com.iyuba.conceptEnglish.util.NetWorkState
//import com.iyuba.configation.Constant
//import com.iyuba.core.common.network.ClientSession
//import com.iyuba.core.lil.user.UserInfoManager
//import org.greenrobot.eventbus.EventBus
//import timber.log.Timber
//import java.text.SimpleDateFormat
//import java.util.*
//
///**
//此线程调用的时机有待**
// */
//class UpdateStudyRecordNewThread(val voaId:Int, private val currParagraph:Int,val endTime:String, val testWords:Int, val end:Boolean,val isListen:Boolean,val showReport: Boolean) :Runnable {
//
//    override fun run() {
//        val startTime = if (BackgroundManager.Instace().bindService != null) {
//            if (BackgroundManager.Instace().bindService.startTime == null) {
//                "0"
//            } else {
//                BackgroundManager.Instace().bindService.startTime
//            }
//        } else "0"
//        val notNet=!NetWorkState.isConnectingToInternet()
//        val login=!UserInfoManager.getInstance().isLogin
//        if (startTime.isEmpty()||notNet||login||startTime==null){
//            return
//        }
//        kotlin.runCatching {
//            startSubmit(startTime,showReport)
//        }.onFailure {
//            Timber.tag(javaClass.simpleName).d(it)
//        }
//    }
//
//    private fun startSubmit(startTime:String,showReport:Boolean){
//        val uid= UserInfoManager.getInstance().userId.toString()
//        val endFlag=if (end)"1" else "0"
//        val lessonId = (voaId * if (!VoaDataManager.getInstance().voaTemp.lessonType.equals(TypeLibrary.BookType.conceptFourUS)) 10 else 1).toString()
//        val testMode="1"
//        val userAnswer=""
//        val score="0"
//        val sdf=SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
//        val sign=uid+startTime+sdf.format(System.currentTimeMillis())
//        val deviceId=""
//
//        Log.d("奖励接口-口语学习报告-新概念", "endFlag--$endFlag")
//
//        ClientSession.Instace().asynGetResponse(DataCollectRequest(uid,startTime,endTime,Constant.AppName,lessonId,currParagraph.toString(),
//            testWords.toString(),testMode,userAnswer,score,endFlag,deviceId,sign,isListen)) { response, _, _ ->
//            response?.let {
//                val data= it as DataCollectResponse
//
//                Log.d("奖励接口-口语学习报告-新概念", "回调数据--" + data.toString())
//
//                val result=if (data.result=="1"){
//                    ("数据提交成功"+if (data.score=="0") "" else "，恭喜您获得了${data.score}分")
//                }else{
//                    if (data.result=="0") data.message else "数据提交异常"
//                }
//                Timber.tag(javaClass.simpleName).d(result)
//
//                Log.d("奖励显示", "显示信息---------$isListen----${data.result}-----$endFlag")
//
//                if (isListen && endFlag == "1"){
//                    //价格显示
//                    var price:Double = data.reward.toInt()*0.01
//                    price = BigDecimalUtil.trans2Double(price)
//
//                    Log.d("奖励显示", "显示奖励信息---------$price")
//
//                    if (data.result=="1"){
//                        if (price>0){
//                            val showMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show),price)
//
//                            if (showReport){
//                                EventBus.getDefault().post(RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast, price.toString(),""))
//                            }else{
//                                EventBus.getDefault().post(RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast, "",showMsg))
//                            }
//                        }else{
//                            //直接刷新下一个
//                            if (showReport){
//                                EventBus.getDefault().post(RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,"",""))
//                            }else{
//                                EventBus.getDefault().post(RefreshDataEvent(TypeLibrary.RefreshDataType.study_next))
//                            }
//                        }
//                    }else{
//                        //直接刷新下一个
//                        if (showReport){
//                            EventBus.getDefault().post(RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_toast,"",""))
//                        }else{
//                            EventBus.getDefault().post(RefreshDataEvent(TypeLibrary.RefreshDataType.study_next))
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}