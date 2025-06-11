package com.iyuba.conceptEnglish.han.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary
import com.iyuba.conceptEnglish.manager.VoaDataManager
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail
import com.iyuba.conceptEnglish.util.ConceptApplication
import com.iyuba.configation.Constant
import com.iyuba.core.common.activity.Web
import com.iyuba.core.common.activity.login.LoginUtil
import com.iyuba.core.common.util.PrivacyUtil
import com.iyuba.core.me.activity.NewVipCenterActivity
import com.iyuba.lib.R
import java.net.URLEncoder
import java.security.MessageDigest

/**
苏州爱语吧科技有限公司
 */
fun String.changeEncode(): String = URLEncoder.encode(this, "utf-8")

fun VoaDetail.realVoaId()= when (VoaDataManager.getInstance().voaTemp.lessonType) {
    TypeLibrary.BookType.conceptFourUS -> voaId.toString()
    TypeLibrary.BookType.conceptFourUK -> (voaId * 10).toString()
    TypeLibrary.BookType.conceptJunior -> voaId.toString()
    else -> voaId.toString()
}
fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(ConceptApplication.getContext(), this, duration).show()
}
fun String.changeVideoUrl()="http://userspeech.${Constant.IYUBA_CN_IN}/voa/$this"

fun String.removeSymbol():String{
    //去除标点(非字母)
    return if (length<3){
        this
    }else{
        val builder=StringBuilder()
        toCharArray().forEach {
            if (it.isLetter()){
                builder.append(it.toString())
            }
        }
        return builder.toString()
    }
}
fun Char.isLetter()=(isLowerCase()||isUpperCase())

fun String.toMd5(): String {
    val hash = MessageDigest.getInstance("MD5").digest(toByteArray())
    return with(StringBuilder()) {
        hash.forEach {
            val i = it.toInt() and (0xFF)
            var temp = Integer.toHexString(i)
            if (temp.length == 1) {
                temp = "0$temp"
            }
            this.append(temp)
        }
        this.toString()
    }
}
fun Context.goSomeAction(option:String){
    val negative="去"+if (option.isEmpty()){"登录"}else{"开通"}

    val msg=if (option.isEmpty()){
        "该功能需要登录后才可以使用，是否立即登录？"
    }else{
        "${option}需要VIP权限，是否立即开通解锁？"
    }

    AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage(msg)
            .setPositiveButton("取消",null)
            .setNegativeButton(negative,object:DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (option.isEmpty()){
                        LoginUtil.startToLogin(this@goSomeAction)
                    }else{
                        NewVipCenterActivity.start(this@goSomeAction,NewVipCenterActivity.VIP_APP)
                    }
                }

            }).create().show()
}

fun Context.getProtocolText():SpannableStringBuilder{
    val privacy2 = "使用协议和隐私政策"

    val start = 0
    val end = start + 4
    val start2 = end + 1
    val end2 = start2 + 4

    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val intent = Intent(this@getProtocolText, Web::class.java).let {
                val url = PrivacyUtil.getSeparatedProtocolUrl()
                it.putExtra("url", url)
                it.putExtra("title", "使用协议")
                it
            }
            startActivity(intent)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.colorPrimary)
        }
    }
    val clickableSpan2: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val intent = Intent(this@getProtocolText, Web::class.java).let {
                val url = PrivacyUtil.getSeparatedSecretUrl()
                it.putExtra("url", url)
                it.putExtra("title", "隐私政策")
                it
            }
            startActivity(intent)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.colorPrimary)
        }
    }

    return with(SpannableStringBuilder()){
        append(privacy2)
        setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(clickableSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        this
    }
}