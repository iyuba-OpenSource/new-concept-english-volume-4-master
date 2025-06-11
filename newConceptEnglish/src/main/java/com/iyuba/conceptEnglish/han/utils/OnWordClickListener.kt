package com.iyuba.conceptEnglish.han.utils

import java.util.*

/**
苏州爱语吧科技有限公司
 */
abstract class OnWordClickListener {
    private var lastClickTime = 0L
    fun onClick(word: String) {
        val current = Calendar.getInstance().timeInMillis
        if (current - lastClickTime > 1000L) {
            lastClickTime = current
            onNoDoubleClick(word)
        }
    }

    protected abstract fun onNoDoubleClick(str: String)
}